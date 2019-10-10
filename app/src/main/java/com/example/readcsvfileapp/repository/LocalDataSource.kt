package com.example.readcsvfileapp.repository

import android.content.Context
import com.example.readcsvfileapp.R
import com.opencsv.CSVReaderBuilder
import java.io.IOException
import java.io.InputStreamReader
import java.lang.Exception
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

interface LocalDataSource {

    @Throws(IOException::class, LocalDataSourceImpl.FileStructureException::class)
    fun readData(): List<User>?
}

class LocalDataSourceImpl(private val applicationContext: Context) : LocalDataSource {

    companion object {
        private const val DATE_FORMAT = "yyyy-MM-dd"
    }

    override fun readData(): List<User>? {
        val inputStream = applicationContext.resources.openRawResource(R.raw.issues)
        try {
            val csvReader = CSVReaderBuilder(InputStreamReader(inputStream, "UTF-8"))
                .withSkipLines(1)
                .build()
            val users = arrayListOf<User>()

            var nextRecord = csvReader.readNext()
            if (nextRecord == null) {
                throw FileStructureException("File is empty!")
            }
            while (nextRecord != null) {
                if (nextRecord.size == 4) {
                    val firstName = nextRecord[0]
                    val surname = nextRecord[1]
                    var issueCount = 0
                    nextRecord[2].let {
                        issueCount = it.toInt()
                    }
                    val birthDate = nextRecord[3]

                    var date: Date? = null
                    try {
                        date =
                            SimpleDateFormat(DATE_FORMAT, Locale.getDefault()).parse(birthDate)
                    } catch (e: ParseException) {
                        throw FileStructureException("Wrong format")
                    }
                    val dateOfBirth = date

                    if (!(firstName.isNullOrEmpty() && surname.isNullOrEmpty() && issueCount == 0 && birthDate.isNullOrEmpty())) {
                        val user = User(
                            firstname = firstName,
                            surname = surname,
                            issueCount = issueCount,
                            dateOfBirth = dateOfBirth,
                            id = 0
                        )
                        users.add(user)
                    }
                } else {
                    throw FileStructureException("Wrong format. Please check the file!")
                }
                nextRecord = csvReader.readNext()
            }
            return users
        } finally {
            try {
                inputStream.close()
            } catch (e: IOException) {
            }
        }
    }

    class FileStructureException(error: String) : Exception(error) {

        override fun hashCode(): Int {
            return javaClass.hashCode()
        }

        override fun equals(other: Any?): Boolean {
            return (other is FileStructureException && other.message == this.message)
        }
    }

}

