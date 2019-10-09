package com.example.readcsvfileapp.database

import com.example.readcsvfileapp.engine.User


object DatabaseInitializer {

    fun addUsers(db: AppDatabase, users: List<User>) {
        insertUsers(db, users)
    }

    fun getAllUsers(db: AppDatabase): List<User>?{
        return db.userDao().allUsers()
    }

    fun insertUsers(db: AppDatabase, users: List<User>) {
        db.userDao().delete()
        db.userDao().insertAll(users)
    }
}
