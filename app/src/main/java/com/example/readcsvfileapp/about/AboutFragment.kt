package com.example.readcsvfileapp.about

import android.os.Bundle
import android.view.Menu
import android.view.View
import com.example.readcsvfileapp.R
import kotlinx.android.synthetic.main.about_view.*

class AboutFragment : BaseFragment() {

    companion object{
        private const val FILE_PATH = "file:///android_asset/about.html"
    }

    override val layout: Int
        get() = R.layout.about_view


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onPrepareOptionsMenu(menu: Menu) {
        super.onPrepareOptionsMenu(menu)
        menu.clear()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val vWebView = webview
        vWebView.loadUrl(FILE_PATH)
    }
}