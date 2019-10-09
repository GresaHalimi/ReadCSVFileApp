package com.example.readcsvfileapp.ui

import android.content.Context
import android.os.Build
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.RelativeLayout
import androidx.annotation.RequiresApi
import com.example.readcsvfileapp.R
import kotlinx.android.synthetic.main.user_item_view.view.*
import java.text.SimpleDateFormat
import java.util.*

open class UserItemView : RelativeLayout, ItemView {

    constructor(context: Context) : super(context) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        init()
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int, defStyleRes: Int) : super(context, attrs, defStyleAttr, defStyleRes) {
        init()
    }

    private fun init() {
        LayoutInflater.from(context).inflate(R.layout.user_item_view, this, true)
    }


    override fun setName(n: String) {
        name.text = n
    }

    override fun setSurname(sname: String){
        surname.text = sname
    }

    override fun setIssueCount(issue: Int){
        issue_count.text = issue.toString()
    }

    override fun setBirthDate(date: Date?){
        date?.let {
            val formattedDate = SimpleDateFormat("dd MMMM yyyy", Locale.getDefault()).format(it)
            date_of_birth.text = formattedDate
        }
    }

}