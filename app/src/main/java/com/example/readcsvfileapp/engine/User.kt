package com.example.readcsvfileapp.engine

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.*

@Entity(tableName = "user")
data class User(@field:PrimaryKey(autoGenerate = true) var id: Int,
                var firstname: String?,
                var surname: String?,
                var issueCount: Int?,
                var dateOfBirth: Date?)
