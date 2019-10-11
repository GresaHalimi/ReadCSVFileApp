package com.example.readcsvfileapp.main

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.ProgressBar
import android.widget.TextView
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.example.readcsvfileapp.R
import com.example.readcsvfileapp.about.AboutFragment
import com.example.readcsvfileapp.about.BaseFragment
import com.example.readcsvfileapp.database.AppDatabase
import com.example.readcsvfileapp.repository.AppCoroutineContextProvider
import com.example.readcsvfileapp.repository.LocalDataSourceImpl
import com.example.readcsvfileapp.repository.UsersRepositoryImpl
import com.example.readcsvfileapp.ui.adapter.GenericRecyclerViewAdapter
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.content_main.*

class MainActivity : AppCompatActivity(), UsersView, FragmentManager.OnBackStackChangedListener {

    companion object {
        private const val FRAGMENT_TAG = "FRAGMENT_TAG"
    }

    private lateinit var vRecyclerView: RecyclerView
    private lateinit var vProgressbar: ProgressBar
    private lateinit var vSwipeRefreshLayout: SwipeRefreshLayout
    private lateinit var vErrorTextView: TextView

    private var mAdapter: GenericRecyclerViewAdapter? = null
    private var mUsersPresenter: UsersPresenter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        initializeViews()

        supportFragmentManager.addOnBackStackChangedListener(this)
        mUsersPresenter =
            UsersPresenter(
                UsersRepositoryImpl(
                    AppDatabase.getInstance(applicationContext),
                    AppCoroutineContextProvider(),
                    LocalDataSourceImpl(applicationContext)
                )
            )
        initializeAdapter()
        mUsersPresenter?.attachedView(this)
    }

    private fun initializeViews() {
        vRecyclerView = recyclerview
        vProgressbar = progressbar
        vErrorTextView = error_text
        vSwipeRefreshLayout = swiperefreshlayout
        vSwipeRefreshLayout.setOnRefreshListener { mUsersPresenter?.onRefresh() }
    }

    private fun initializeAdapter() {
        val mLayoutManager = LinearLayoutManager(this)
        vRecyclerView.layoutManager = mLayoutManager
        mAdapter = GenericRecyclerViewAdapter()
        mUsersPresenter?.let { mAdapter?.setDataSource(it) }
        vRecyclerView.adapter = mAdapter
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)
        updateActionBar()

        return super.onCreateOptionsMenu(menu)
    }

    private fun openFragment(fragment: BaseFragment) {
        supportFragmentManager
            .beginTransaction()
            .replace(R.id.fragment_container, fragment)
            .addToBackStack(FRAGMENT_TAG)
            .commit()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId

        if (id == R.id.action_about) {
            openFragment(AboutFragment())
            return true
        }

        if (id == android.R.id.home) {
            supportActionBar?.setDisplayHomeAsUpEnabled(false)
            supportFragmentManager.popBackStack()
            return true
        }

        return super.onOptionsItemSelected(item)
    }

    override fun onBackStackChanged() {
        updateActionBar()
    }

    private fun updateActionBar() {
        if (supportFragmentManager.backStackEntryCount >= 1) {
            supportActionBar?.setDisplayHomeAsUpEnabled(true)
        } else {
            supportActionBar?.setDisplayHomeAsUpEnabled(false)
        }
    }

    override fun reloadContentView() {
        vProgressbar.visibility = View.GONE
        vSwipeRefreshLayout.isEnabled = true
        vRecyclerView.visibility = View.VISIBLE
        mAdapter?.updateAccordingToDelegateAndNotifyDataSetChanged()
    }

    override fun getUserItemView(): UserItemView {
        return UserItemView(applicationContext)
    }

    override fun showProgressBar() {
        vSwipeRefreshLayout.isEnabled = false
        vErrorTextView.visibility = View.GONE
        vProgressbar.visibility = View.VISIBLE
    }

    override fun dismissProgressBar() {
        vProgressbar.visibility = View.GONE
    }

    override fun dismissSwipeRefresh() {
        vSwipeRefreshLayout.isRefreshing = false
    }

    override fun showSwipeRefresh() {
        vSwipeRefreshLayout.isRefreshing = true
    }

    override fun showSnackbar(message: String) {
        Snackbar.make(main_container, message, Snackbar.LENGTH_SHORT).show()
    }

    override fun showErrorView(message: String) {
        vProgressbar.visibility = View.GONE
        vRecyclerView.visibility = View.GONE
        vSwipeRefreshLayout.visibility = View.VISIBLE
        vSwipeRefreshLayout.isEnabled = true
        vErrorTextView.visibility = View.VISIBLE
        vErrorTextView.text = message
    }

    public override fun onDestroy() {
        mUsersPresenter?.detachView()
        super.onDestroy()
    }

}
