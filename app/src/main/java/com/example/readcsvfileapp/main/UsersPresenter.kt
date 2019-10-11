package com.example.readcsvfileapp.main

import android.view.View
import android.view.ViewGroup
import com.example.readcsvfileapp.repository.User
import com.example.readcsvfileapp.repository.UsersRepository
import com.example.readcsvfileapp.ui.StackViewDataSource
import com.example.readcsvfileapp.ui.StackViewDelegate


open class UsersPresenter(private val usersRepository: UsersRepository) : Presenter<UsersView>,
    StackViewDataSource, StackViewDelegate,
    UsersRepository.FetchUsersCallback, UsersRepository.FetchCachedUsersCallback {

    private var mView: UsersView? = null
    var users: List<User> = arrayListOf()

    override fun attachedView(view: UsersView) {
        mView = view
        usersRepository.register(this)
        usersRepository.getCachedData()
        showLoading()
    }

    override fun detachView() {
        usersRepository.onCancelWorkerScope()
        usersRepository.unRegister(this)
        mView = null
    }

    override fun onRefresh() {
        showLoading()
        requestData()
    }

    fun requestData() {
        usersRepository.fetchUsers()
    }

    fun showLoading() {
        if (users.isNotEmpty()) {
            mView?.showSwipeRefresh()
        } else {
            mView?.showProgressBar()
        }
    }

    fun dismissLoading() {
        mView?.dismissSwipeRefresh()
        if (users.isEmpty()) {
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
        usersRepository.let {
            if (it.getUsers().isNotEmpty()) {
                mView?.showSnackbar(error?.localizedMessage.toString())
            } else {
                mView?.showErrorView(error?.localizedMessage.toString())
            }
        }
    }

    override fun onFetchCachedUsersSuccess(users: List<User>) {
        this.users = users
        if (users.isEmpty()) {
            requestData()
        } else {
            dismissLoading()
            mView?.reloadContentView()
        }
    }

}