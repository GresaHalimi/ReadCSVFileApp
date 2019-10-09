package com.example.readcsvfileapp.ui

import java.util.*

interface ItemView {

    fun setName(name: String)

    fun setSurname(surname: String)

    fun setIssueCount(issue: Int)

    fun setBirthDate(date: Date?)
}
