package com.example.readcsvfileapp.database

import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.example.readcsvfileapp.engine.User
import junit.framework.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.util.*

@RunWith(AndroidJUnit4::class)
open class DatabaseInitializerTest {

    private lateinit var usersDatabase: AppDatabase

    @Before
    fun initDb() {
        AppDatabase.TEST_MODE = true
        usersDatabase = AppDatabase.getInstance(InstrumentationRegistry.getInstrumentation().getTargetContext())!!
    }

    @Test
    fun itShouldInsertOneUserToDB_When_DateOfBirthNull_On_AddUsers() {
        val user = User(1, "User1", "User1Surname", 1, null)
        val users = arrayListOf(user)
        DatabaseInitializer.addUsers(usersDatabase, arrayListOf(user))

        assertEquals(users, DatabaseInitializer.getAllUsers(usersDatabase))
    }

    @Test
    fun itShouldInsertOneUserToDB_When_DateOfBirthNotNull_On_AddUsers() {
        val user = User(1, "User1", "User1Surname", 1, Date())
        val users = arrayListOf(user)
        DatabaseInitializer.addUsers(usersDatabase, arrayListOf(user))

        assertEquals(users, DatabaseInitializer.getAllUsers(usersDatabase))
    }

    @Test
    fun itShouldInsertMoreUsersToDB_On_AddUsers(){
        val user1 = User(1, "User1", "User1Surname", 1, null)
        val user2 = User(2, "User2", "User2Surname", 3, Date())
        val users = arrayListOf<User>()
        users.add(user1)
        users.add(user2)

        DatabaseInitializer.addUsers(usersDatabase, users)

        assertEquals(users, DatabaseInitializer.getAllUsers(usersDatabase))
    }
}