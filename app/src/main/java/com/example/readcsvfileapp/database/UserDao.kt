package com.example.readcsvfileapp.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy.REPLACE
import androidx.room.Query
import com.example.readcsvfileapp.engine.User


@Dao
interface UserDao {

    @Query("SELECT * FROM user")
    fun allUsers(): List<User>?

    @Insert(onConflict = REPLACE)
    fun insertAll(users: List<User>)

    @Query("DELETE FROM user")
    fun delete()
}