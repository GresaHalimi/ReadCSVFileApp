package com.example.readcsvfileapp

import android.view.View
import android.view.ViewGroup
import com.example.readcsvfile.ui.adapter.StackViewDataSource
import com.example.readcsvfile.ui.adapter.StackViewDelegate
import com.example.readcsvfileapp.engine.User
import com.example.readcsvfileapp.engine.UsersEngine
import com.example.readcsvfileapp.engine.UsersEngineImpl
import com.example.readcsvfileapp.ui.ItemView
import com.example.readcsvfileapp.ui.UsersView


open class UsersPresenter(val usersEngine: UsersEngineImpl) : Presenter<UsersView>,
    StackViewDataSource, StackViewDelegate,
    UsersEngine.FetchUsersCallback {

    private var mView: UsersView? = null
    var users: List<User> = arrayListOf()

    override fun attachedView(view: UsersView) {
        mView = view
        usersEngine.register(this)
        users = usersEngine.getUsers()
        if (users.isEmpty()) {
            showLoading()
            requestData()
        }
        updateView()
    }

    override fun detachView() {
        usersEngine.onCancelAsyncTask()
        usersEngine.unRegister(this)
        mView = null
    }

    override fun onRefresh() {
        showLoading()
        requestData()
    }

    private fun updateView() {
        mView?.reloadContentView()
    }

    fun requestData() {
        usersEngine.fetchUsers()
    }

    fun showLoading() {
        if (users.size > 0) {
            mView?.showSwipeRefresh()
        } else {
            mView?.showProgressBar()
        }
    }

    fun dismissLoading() {
        mView?.dismissSwipeRefresh()
        if (users.size <= 0) {
            mView?.dismissProgressBar()
        }
    }


    override fun getNumberOfItems(): Int {
        return users.size
    }

    override fun getDataSource(): StackViewDataSource {
        return this
    }

    override fun onCreateItemView(parent: ViewGroup): View? {
        return mView?.getUserItemView()
    }

    override fun onItemViewCreated(view: View, item: Int) {
        val user = users[item]
        if (view is ItemView) {
            user.let { u ->
                view.setName(u.firstname)
                view.setSurname(u.surname)
                view.setIssueCount(u.issueCount)
                view.setBirthDate(u.dateOfBirth)
            }
        }
    }

    override fun onFetchUsersSuccess(users: List<User>) {
        this.users = users
        dismissLoading()
        mView?.reloadContentView()
    }

    override fun onFetchUsersFailure(error: Throwable?) {
        dismissLoading()
        usersEngine.let {
            if (it.getUsers().isNotEmpty()) {
                mView?.showSnackbar(error?.localizedMessage.toString())
            } else {
                mView?.showErrorView(error?.localizedMessage.toString())
            }
        }
    }

}