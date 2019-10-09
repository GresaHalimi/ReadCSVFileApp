package com.example.readcsvfileapp

interface Presenter<V> {

    fun attachedView(view: V)

    fun detachView()

    fun onRefresh()
}