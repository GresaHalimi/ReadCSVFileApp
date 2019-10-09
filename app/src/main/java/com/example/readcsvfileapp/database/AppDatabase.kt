package com.example.readcsvfileapp.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.readcsvfileapp.engine.User


@Database(entities = arrayOf(User::class), version = 1)
@TypeConverters(ConverterHelper::class)
abstract class AppDatabase : RoomDatabase() {

    abstract fun userDao(): UserDao

    companion object {

        var TEST_MODE = false
        private val databaseName = "usersData_DB"

        private var appDatabase: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase? {
            if (appDatabase == null) {
                if (TEST_MODE) {
                    appDatabase =
                        Room.inMemoryDatabaseBuilder(context, AppDatabase::class.java).build()
                } else {
                    appDatabase =
                        Room.databaseBuilder(context, AppDatabase::class.java, databaseName)
                            .allowMainThreadQueries()
                            .build()
                }
            }
            return appDatabase
        }
    }
}