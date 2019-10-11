package com.example.readcsvfileapp.database

import androidx.room.TypeConverter
import java.util.*



class ConverterHelper {

    @TypeConverter
    fun toDate(dateLong: Long?): Date? {
        return dateLong?.let { Date(it) }
    }

    @TypeConverter
    fun fromDate(date: Date?): Long? {
        return date?.time
    }
}