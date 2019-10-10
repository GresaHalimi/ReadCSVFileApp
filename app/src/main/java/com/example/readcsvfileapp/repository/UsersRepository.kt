package com.example.readcsvfileapp.repository


interface UsersRepository{

    //----------------------------------------------------------------------------------------------
    //region UsersRepository

    fun register(callback: UsersEngineCallback)

    fun unRegister(callback: UsersEngineCallback)

    fun getUsers(): List<User>

    fun fetchUsers()

    fun onCancelWorkerScope()

    fun getCachedData()
    //endregion


    //----------------------------------------------------------------------------------------------
    //region Callbacks

    interface UsersEngineCallback

    interface FetchUsersCallback : UsersEngineCallback {
        fun onFetchUsersSuccess(users: List<User>)
        fun onFetchUsersFailure(error: Throwable?)
    }

    interface FetchCachedUsersCallback : UsersEngineCallback {
        fun onFetchCachedUsersSuccess(users: List<User>)
    }

    //endregion
}