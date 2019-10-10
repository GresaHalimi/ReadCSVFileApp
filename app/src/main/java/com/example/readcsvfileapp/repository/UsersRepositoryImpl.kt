package com.example.readcsvfileapp.repository

import com.example.readcsvfileapp.database.AppDatabase
import kotlinx.coroutines.*
import java.util.*


open class UsersRepositoryImpl(
    private val database: AppDatabase,
    private val coroutineContextProvider: CoroutineContextProvider,
    private val localDataSource: LocalDataSource
) : UsersRepository {

    val observers = arrayListOf<UsersRepository.UsersEngineCallback>()
    private val mUsers = arrayListOf<User>()

    private val job = Job()
    val handler = CoroutineExceptionHandler { _, exception -> }
    val workerScope = CoroutineScope(coroutineContextProvider.main() + job + handler)


    override fun getCachedData() {
        workerScope.launch(coroutineContextProvider.main()) {
            readCachedData()
        }
    }

    private suspend fun readCachedData() {
        val deferredUsers = workerScope.async(coroutineContextProvider.io()) {
            return@async database.userDao().allUsers()
        }
        try {
            val users = deferredUsers.await()
            mUsers.clear()
            users?.let { mUsers.addAll(it) }
            notifyFetchCashedUsersSuccess(mUsers)
        } catch (ex: Exception) {
        }
    }

    private fun updateCashedUserList() {
        workerScope.launch(coroutineContextProvider.io()) {
            database.userDao().delete()
            database.userDao().insertAll(mUsers)
        }
    }

    override fun onCancelWorkerScope() {
        job.cancel()
    }

    override fun getUsers(): List<User> {
        return Collections.unmodifiableList(mUsers)
    }

    override fun fetchUsers() {
        workerScope.launch(coroutineContextProvider.main()) {
            readUsers()
        }
    }

    private suspend fun readUsers() {
        val deferredUsers = workerScope.async(coroutineContextProvider.io()) {
            return@async localDataSource.readData()
        }
        try {
            val users = deferredUsers.await()
            mUsers.clear()
            users?.let { mUsers.addAll(it) }
            notifyFetchUsersSuccess(mUsers)
            updateCashedUserList()
        } catch (exception: LocalDataSourceImpl.FileStructureException) {
            notifyFetchUsersFailure(exception)
        }
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


