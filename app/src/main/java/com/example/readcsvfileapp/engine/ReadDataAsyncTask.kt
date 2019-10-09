package com.example.readcsvfileapp.engine


import android.os.AsyncTask
import com.opencsv.CSVReaderBuilder
import java.io.IOException
import java.io.InputStream
import java.io.InputStreamReader
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*


class ReadDataAsyncTask(private val mListener: OnLoadListener) :
    AsyncTask<InputStream, Void, List<User>>() {

    private var error = "Error"

    override fun doInBackground(vararg params: InputStream): List<User>? {
        val inputStream = params[0]
        try {
            val csvReader = CSVReaderBuilder(InputStreamReader(inputStream, "UTF-8"))
                .withSkipLines(1)
                .build()
            val users = arrayListOf<User>()

            var nextRecord = csvReader.readNext()
            while (nextRecord != null) {
                val firstName = nextRecord[0]
                val surname = nextRecord[1]
                val issueCount = nextRecord[2].toInt()
                var date: Date? = null
                try {
                    date = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).parse(nextRecord[3])
                } catch (e: ParseException) {
                    e.printStackTrace()
                }
                val dateOfBirth = date

                val user = User(
                    firstname = firstName,
                    surname = surname,
                    issueCount = issueCount,
                    dateOfBirth = dateOfBirth,
                    id = 0
                )
                users.add(user)
                nextRecord = csvReader.readNext()
            }
            return users
        } catch (e: IOException) {
            error = e.message.toString()
        } finally {
            try {
                inputStream.close()
            } catch (e: IOException) {
                error = e.message.toString()
            }
        }

        return null
    }

    override fun onPostExecute(users: List<User>?) {
        super.onPostExecute(users)
        if (users != null) {
            mListener.onLoadComplete(users)
        } else {

            mListener.onError(error)
        }
    }

    interface OnLoadListener {
        fun onLoadComplete(users: List<User>)

        fun onError(error: String)
    }
}
