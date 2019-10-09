package com.example.readcsvfileapp.engine

import android.content.Context
import com.example.readcsvfileapp.R
import com.example.readcsvfileapp.database.AppDatabase
import com.example.readcsvfileapp.database.DatabaseInitializer
import java.util.*


open class UsersEngineImpl(val applicationContext: Context) : UsersEngine,
    ReadDataAsyncTask.OnLoadListener {

    private val observers = arrayListOf<UsersEngine.UsersEngineCallback>()
    private val mUsers = arrayListOf<User>()
    protected var readDataAsyncTask: ReadDataAsyncTask? = null

    init {
        checkCashedUserList()
    }

    private fun checkCashedUserList() {
        val cashedUsers =
            AppDatabase.getInstance(applicationContext)?.let { DatabaseInitializer.getAllUsers(it) }
        mUsers.clear()
        cashedUsers?.let {
            mUsers.addAll(it)
        }
    }

    private fun updateCashedUserList() {
        AppDatabase.getInstance(applicationContext)
            ?.let { DatabaseInitializer.addUsers(it, mUsers) }
    }

    override fun onLoadComplete(users: List<User>) {
        mUsers.clear()
        mUsers.addAll(users)
        notifyFetchUsersSuccess(users)
        updateCashedUserList()
    }

    override fun onError(error: String) {
        notifyFetchUsersFailure(Throwable(error))
    }

    fun onCancelAsyncTask() {
        readDataAsyncTask?.cancel(true)
    }

    override fun getUsers(): List<User> {
        return Collections.unmodifiableList(mUsers)
    }

    override fun fetchUsers() {
        val inputStream = applicationContext.resources.openRawResource(R.raw.issues)
        readDataAsyncTask = ReadDataAsyncTask(this).execute(inputStream) as ReadDataAsyncTask
    }

    //----------------------------------------------------------------------------------------------
    //region Publishers

    private fun notifyFetchUsersSuccess(users: List<User>) {
        observers.forEach { o -> (o as? UsersEngine.FetchUsersCallback)?.onFetchUsersSuccess(users) }
    }

    private fun notifyFetchUsersFailure(error: Throwable?) {
        observers.forEach { o -> (o as? UsersEngine.FetchUsersCallback)?.onFetchUsersFailure(error) }
    }

    override fun register(callback: UsersEngine.UsersEngineCallback) {
        if (!observers.contains(callback)) {
            observers.add(callback)
        }
    }

    override fun unRegister(callback: UsersEngine.UsersEngineCallback) {
        if (observers.contains(callback)) {
            observers.remove(callback)
        }
    }

    //endregion
}
