package com.example.readcsvfileapp.repository

import kotlinx.coroutines.Dispatchers

class TestCoroutineContentProvider : CoroutineContextProvider {
    override fun main() =  Dispatchers.Unconfined
    override fun io() =  Dispatchers.Unconfined
}