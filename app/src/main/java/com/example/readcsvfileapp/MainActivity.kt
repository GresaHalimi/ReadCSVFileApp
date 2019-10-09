package com.example.readcsvfileapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.ProgressBar
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.example.readcsvfile.ui.adapter.GenericRecyclerViewAdapter
import com.example.readcsvfileapp.about.AboutFragment
import com.example.readcsvfileapp.about.BaseFragment
import com.example.readcsvfileapp.engine.UsersEngineImpl
import com.example.readcsvfileapp.ui.UserItemView
import com.example.readcsvfileapp.ui.UsersView
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.content_main.*

class MainActivity : AppCompatActivity(), UsersView, FragmentManager.OnBackStackChangedListener{

    companion object {
        private val FRAGMENT_TAG = "FRAGMENT_TAG"
    }

    private lateinit var vRecyclerView: RecyclerView
    private lateinit var vProgressbar: ProgressBar
    private lateinit var vSwipeRefreshLayout: SwipeRefreshLayout

    private var mAdapter: GenericRecyclerViewAdapter? = null
    private var mUsersPresenter: UsersPresenter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        initializeViews()

        supportFragmentManager.addOnBackStackChangedListener(this)
        mUsersPresenter = UsersPresenter(UsersEngineImpl(applicationContext))
        initializeAdapter()
        mUsersPresenter?.attachedView(this)
    }

    private fun initializeViews() {
        vRecyclerView = recyclerview
        vProgressbar = progressbar
        vSwipeRefreshLayout = swiperefreshlayout
        vSwipeRefreshLayout.setOnRefreshListener(object : SwipeRefreshLayout.OnRefreshListener{
            override fun onRefresh() {
                mUsersPresenter?.onRefresh()
            }
        })
    }

    private fun initializeAdapter() {
        val mLayoutManager = LinearLayoutManager(this)
        vRecyclerView.setLayoutManager(mLayoutManager)
        mAdapter = GenericRecyclerViewAdapter()
        mUsersPresenter?.let { mAdapter?.setDataSource(it) }
        vRecyclerView.setAdapter(mAdapter)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)
        updateActionBar()

        return super.onCreateOptionsMenu(menu)
    }

    fun setFragment(fragment: BaseFragment) {
        supportFragmentManager
            .beginTransaction()
            .replace(R.id.fragment_container, fragment)
            .addToBackStack(FRAGMENT_TAG)
            .commit()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId

        if (id == R.id.action_about) {
            setFragment(AboutFragment())
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
        vProgressbar.setVisibility(View.GONE)
        vSwipeRefreshLayout.setEnabled(true)
        vRecyclerView.setVisibility(View.VISIBLE)
        mAdapter?.updateAccordingToDelegateAndNotifyDataSetChanged()
    }

    override fun getUserItemView(): UserItemView {
        return UserItemView(applicationContext)
    }

    override fun showProgressBar() {
        vSwipeRefreshLayout.setEnabled(false)
        vProgressbar.setVisibility(View.VISIBLE)
    }

    override fun dismissProgressBar() {
        vProgressbar.setVisibility(View.GONE)
    }

    override fun dismissSwipeRefresh() {
        vSwipeRefreshLayout.setRefreshing(false)
    }

    override fun showSwipeRefresh() {
        vSwipeRefreshLayout.setRefreshing(true)
    }


    override fun showSnackbar(message: String) {
        Snackbar.make(main_container, message,
            Snackbar.LENGTH_SHORT)
            .show()
    }

    override fun showErrorView(message: String) {
        //`TODO: implement
    }

    public override fun onDestroy() {
        mUsersPresenter?.detachView()
        super.onDestroy()
    }

}
