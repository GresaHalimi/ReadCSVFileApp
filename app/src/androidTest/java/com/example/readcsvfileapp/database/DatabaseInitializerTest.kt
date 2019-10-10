package com.example.readcsvfileapp.database

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.readcsvfileapp.repository.User
import junit.framework.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.util.*

@RunWith(AndroidJUnit4::class)
open class AppDatabaseTest {

    private lateinit var usersDatabase: AppDatabase

    @Before
    fun initDb() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        usersDatabase = Room.inMemoryDatabaseBuilder(context, AppDatabase::class.java).build()
    }

    @Test
    fun itShouldInsertOneUserToDB_When_DateOfBirthNull_On_InsertAll() {
        val user = User(1, "User1", "User1Surname", 1, null)
        val users = arrayListOf(user)
        usersDatabase.userDao().insertAll(arrayListOf(user))

        assertEquals(users, usersDatabase.userDao().allUsers())
    }

    @Test
    fun itShouldInsertOneUserToDB_When_DateOfBirthNotNull_On_InsertAll() {
        val user = User(1, "User1", "User1Surname", 1, Date())
        val users = arrayListOf(user)
        usersDatabase.userDao().insertAll(arrayListOf(user))

        assertEquals(users, usersDatabase.userDao().allUsers())
    }

    @Test
    fun itShouldInsertMoreUsersToDB_On_InsertAll(){
        val user1 = User(1, "User1", "User1Surname", 1, null)
        val user2 = User(2, "User2", "User2Surname", 3, Date())
        val users = arrayListOf<User>()
        users.add(user1)
        users.add(user2)

        usersDatabase.userDao().insertAll(users)

        assertEquals(users, usersDatabase.userDao().allUsers())
    }

    @Test
    fun itShouldRemoveAllUsersFromDB_On_AddUsers(){
        val user1 = User(1, "User1", "User1Surname", 1, null)
        val users = arrayListOf<User>()
        users.add(user1)

        usersDatabase.userDao().insertAll(users)
        usersDatabase.userDao().delete()

        assertEquals(0, usersDatabase.userDao().allUsers()?.size)
    }
}