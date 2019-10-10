package com.example.readcsvfileapp.main

interface Presenter<V> {

    fun attachedView(view: V)

    fun detachView()

    fun onRefresh()
}