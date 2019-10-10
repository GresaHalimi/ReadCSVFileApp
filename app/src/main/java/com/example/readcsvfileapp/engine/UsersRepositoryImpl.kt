package com.example.readcsvfileapp.engine

import android.content.Context
import com.example.readcsvfileapp.R
import com.example.readcsvfileapp.database.AppDatabase
import com.example.readcsvfileapp.database.DatabaseInitializer
import com.opencsv.CSVReaderBuilder
import kotlinx.coroutines.*
import java.io.IOException
import java.io.InputStreamReader
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*


open class UsersRepositoryImpl(val applicationContext: Context) : UsersRepository {

    private val observers = arrayListOf<UsersRepository.UsersEngineCallback>()
    private val mUsers = arrayListOf<User>()
    private var error: Throwable? = null

    private val job = Job()
    private val workerScope = CoroutineScope(Dispatchers.Main + job)

    companion object {
        private const val DATE_FORMAT = "yyyy-MM-dd"
    }

    init {
        getCachedData()
    }

    private fun getCachedData() {
        workerScope.launch(Dispatchers.Main) {
            readCachedData()
        }
    }

    private suspend fun readCachedData() {
        val deferredUsers = workerScope.async(Dispatchers.IO) {
            return@async AppDatabase.getInstance(applicationContext)
                ?.let { DatabaseInitializer.getAllUsers(it) }
        }
        try {
            val users = deferredUsers.await()
            mUsers.clear()
            users?.let { mUsers.addAll(it) }
            notifyFetchCashedUsersSuccess(mUsers)
        } catch (exception: Exception) {
            notifyFetchCachedUsersFailure(Throwable(exception.localizedMessage))
        }
    }

    private fun updateCashedUserList() {
        workerScope.launch(Dispatchers.IO) {
            AppDatabase.getInstance(applicationContext)
                ?.let { DatabaseInitializer.addUsers(it, mUsers) }
        }
    }

    override fun onCancelWorkerScope() {
        job.cancel()
    }

    override fun getUsers(): List<User> {
        return Collections.unmodifiableList(mUsers)
    }

    override fun fetchUsers() {
        workerScope.launch(Dispatchers.Main) {
            readUsers()
        }
    }

    private suspend fun readUsers() {
        val deferredUsers = workerScope.async(Dispatchers.IO) {
            return@async readData()
        }
        try {
            val users = deferredUsers.await()
            mUsers.clear()
            users?.let { mUsers.addAll(it) }
            if (mUsers.size == 0 && error != null) {
                notifyFetchUsersFailure(error)
            } else {
                notifyFetchUsersSuccess(mUsers)
            }
            updateCashedUserList()
        } catch (exception: Exception) {
            notifyFetchUsersFailure(exception)
        }
    }

    private fun readData(): List<User>? {
        val inputStream = applicationContext.resources.openRawResource(R.raw.issues)
        try {
            val csvReader = CSVReaderBuilder(InputStreamReader(inputStream, "UTF-8"))
                .withSkipLines(1)
                .build()
            val users = arrayListOf<User>()

            var nextRecord = csvReader.readNext()
            if (nextRecord == null) {
                error = FileStructureError("File is empty!")
            }
            while (nextRecord != null) {
                if (nextRecord.size == 4) {
                    val firstName = nextRecord[0]
                    val surname = nextRecord[1]
                    var issueCount = 0
                    nextRecord[2].let {
                        try {
                            issueCount = it.toInt()
                        } catch (ex: java.lang.Exception) {

                        }
                    }
                    val birthDate = nextRecord[3]

                    var date: Date? = null
                    try {
                        date =
                            SimpleDateFormat(DATE_FORMAT, Locale.getDefault()).parse(birthDate)
                    } catch (e: ParseException) {
                        error = FileStructureError("Wrong format")
                    }
                    val dateOfBirth = date

                    if (!(firstName.isNullOrEmpty() && surname.isNullOrEmpty() && issueCount == 0 && birthDate.isNullOrEmpty())) {
                        val user = User(
                            firstname = firstName,
                            surname = surname,
                            issueCount = issueCount,
                            dateOfBirth = dateOfBirth,
                            id = 0
                        )
                        users.add(user)
                    }
                } else {
                    error = FileStructureError("Wrong format. Please check the file!")
                }
                nextRecord = csvReader.readNext()
            }
            return users
        } catch (e: IOException) {
            error = e
        } finally {
            try {
                inputStream.close()
            } catch (e: IOException) {
                error = e
            }
        }
        return null
    }

    //----------------------------------------------------------------------------------------------
    //region Publishers

    private fun notifyFetchUsersSuccess(users: List<User>) {
        observers.forEach { o ->
            (o as? UsersRepository.FetchUsersCallback)?.onFetchUsersSuccess(
                users
            )
        }
    }

    private fun notifyFetchCashedUsersSuccess(users: List<User>) {
        observers.forEach { o ->
            (o as? UsersRepository.FetchCachedUsersCallback)?.onFetchCachedUsersSuccess(
                users
            )
        }
    }

    private fun notifyFetchCachedUsersFailure(error: Throwable?) {
        observers.forEach { o ->
            (o as? UsersRepository.FetchCachedUsersCallback)?.onFetchCachedUsersFailure(
                error
            )
        }
    }

    private fun notifyFetchUsersFailure(error: Throwable?) {
        observers.forEach { o ->
            (o as? UsersRepository.FetchUsersCallback)?.onFetchUsersFailure(
                error
            )
        }
    }

    override fun register(callback: UsersRepository.UsersEngineCallback) {
        if (!observers.contains(callback)) {
            observers.add(callback)
        }
    }

    override fun unRegister(callback: UsersRepository.UsersEngineCallback) {
        if (observers.contains(callback)) {
            observers.remove(callback)
        }
    }

    //endregion
}

class FileStructureError(error: String) : Throwable(error) {

}
