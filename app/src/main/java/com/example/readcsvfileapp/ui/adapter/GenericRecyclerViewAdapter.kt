package com.example.readcsvfileapp.ui.adapter

import android.util.SparseIntArray
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.readcsvfileapp.ui.StackViewDataSource
import com.example.readcsvfileapp.ui.StackViewDelegate


class GenericRecyclerViewAdapter :
    RecyclerView.Adapter<GenericRecyclerViewAdapter.ItemViewHolder>() {

    private var dataSource: StackViewDataSource? = null
    private var delegate: StackViewDelegate? = null

    private var totalItemsCount = 0
    private var items = SparseIntArray()


    private fun setupAccordingToDataSource() {
        totalItemsCount = 0
        items.clear()
        this.dataSource?.let {
            val itemsCount = it.getNumberOfItems()
            totalItemsCount += itemsCount
            for ((itemPosition, item) in (0 until itemsCount).withIndex()) {
                items.put(itemPosition, item)
            }
        }
    }

    fun updateAccordingToDelegateAndNotifyDataSetChanged() {
        this.delegate?.let {
            updateDelegateAndNotifyDataSetChanged(it)
        }
    }

    fun setDataSource(stackViewDelegate: StackViewDelegate) {
        this.delegate = stackViewDelegate
        this.dataSource = stackViewDelegate.getDataSource()
        setupAccordingToDataSource()
    }

    private fun updateDelegateAndNotifyDataSetChanged(stackViewDelegate: StackViewDelegate) {
        this.delegate = stackViewDelegate
        this.dataSource = stackViewDelegate.getDataSource()
        setupAccordingToDataSource()
        notifyDataSetChanged()
    }

    override fun getItemViewType(position: Int): Int {
        return 0
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        return this.delegate?.let {
            it.onCreateItemView(parent)?.let { it1 -> ItemViewHolder(it1) }
                ?: run {
                    super.createViewHolder(parent, viewType)
                }
        } ?: run {
            super.createViewHolder(parent, viewType)
        }
    }


    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        if (items.indexOfKey(position) >= 0) {
            this.delegate?.onItemViewCreated(holder.itemView, items.get(position))
        }
    }

    override fun getItemCount(): Int {
        return totalItemsCount
    }

    inner class ItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)
}
