package me.xhsun.gw2leo.storage.ui.adapter

import android.view.ViewGroup
import androidx.fragment.app.FragmentManager
import androidx.paging.PagingDataAdapter
import me.xhsun.gw2leo.storage.StorageItem
import me.xhsun.gw2leo.storage.service.comparator.StorageItemComparator

class StorageAdapter(private val fragmentManager: FragmentManager) :
    PagingDataAdapter<StorageItem, StorageViewHolder>(StorageItemComparator()) {

    override fun onBindViewHolder(holder: StorageViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    override fun onBindViewHolder(
        holder: StorageViewHolder,
        position: Int,
        payloads: MutableList<Any>
    ) {
        if (payloads.isNotEmpty()) {
            val item = getItem(position)
            holder.update(item)
        } else {
            onBindViewHolder(holder, position)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StorageViewHolder {
        return StorageViewHolder.create(parent, fragmentManager)
    }
}
