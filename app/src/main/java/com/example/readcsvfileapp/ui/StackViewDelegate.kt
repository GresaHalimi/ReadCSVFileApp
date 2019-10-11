package com.example.readcsvfileapp.ui

import android.view.View
import android.view.ViewGroup


interface StackViewDelegate {

    fun getDataSource(): StackViewDataSource

    fun onCreateItemView(parent: ViewGroup): View?

    fun onItemViewCreated(view: View, item: Int)
}