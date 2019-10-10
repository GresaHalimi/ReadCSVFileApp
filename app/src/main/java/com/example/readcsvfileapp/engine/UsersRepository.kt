package com.example.readcsvfileapp.engine


interface UsersRepository{

    //----------------------------------------------------------------------------------------------
    //region UsersRepository

    fun register(callback: UsersEngineCallback)

    fun unRegister(callback: UsersEngineCallback)

    fun getUsers(): List<User>

    fun fetchUsers()

    fun onCancelWorkerScope()
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
        fun onFetchCachedUsersFailure(error: Throwable?)
    }

    //endregion
}