package com.example.readcsvfileapp.main

import java.util.*

interface ItemView {

    fun setName(name: String)

    fun setSurname(surname: String)

    fun setIssueCount(issue: Int)

    fun setBirthDate(date: Date?)
}
