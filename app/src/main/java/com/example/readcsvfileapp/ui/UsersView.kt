package com.example.readcsvfileapp.ui

interface UsersView{
    fun reloadContentView()

    fun getUserItemView(): UserItemView?
}
