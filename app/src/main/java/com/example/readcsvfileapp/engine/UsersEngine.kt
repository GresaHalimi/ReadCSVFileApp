package com.example.readcsvfileapp.engine


interface UsersEngine{

    //----------------------------------------------------------------------------------------------
    //region UsersEngine

    fun register(callback: UsersEngineCallback)

    fun unRegister(callback: UsersEngineCallback)

    fun getUsers(): List<User>

    fun fetchUsers()

    //endregion


    //----------------------------------------------------------------------------------------------
    //region Callbacks

    interface UsersEngineCallback

    interface FetchUsersCallback : UsersEngineCallback {
        fun onFetchUsersSuccess(users: List<User>)
        fun onFetchUsersFailure(error: Throwable?)
    }

    //endregion
}