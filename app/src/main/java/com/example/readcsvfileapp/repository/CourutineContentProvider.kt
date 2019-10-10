package com.example.readcsvfileapp.repository

import kotlinx.coroutines.Dispatchers
import kotlin.coroutines.CoroutineContext

interface CoroutineContextProvider {
    fun main(): CoroutineContext
    fun io(): CoroutineContext

}

class AppCoroutineContextProvider : CoroutineContextProvider {
    override fun main() =  Dispatchers.Main
    override fun io() =  Dispatchers.IO
}