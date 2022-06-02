package me.xhsun.gw2leo.storage.ui

import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import me.xhsun.gw2leo.storage.StorageItem

class StorageAdapter() :
    PagingDataAdapter<StorageItem, StorageViewHolder>(STORAGE_COMPARATOR) {
    override fun onBindViewHolder(holder: StorageViewHolder, position: Int) {
        holder.bind(getItem(position), true)
    }

    override fun onBindViewHolder(
        holder: StorageViewHolder,
        position: Int,
        payloads: MutableList<Any>
    ) {
        if (payloads.isNotEmpty()) {
            val item = getItem(position)
            holder.update(item, true)
        } else {
            onBindViewHolder(holder, position)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StorageViewHolder {
        return StorageViewHolder.create(parent)
    }

    companion object {
        private val PAYLOAD_SCORE = Any()
        val STORAGE_COMPARATOR = object : DiffUtil.ItemCallback<StorageItem>() {
            override fun areContentsTheSame(oldItem: StorageItem, newItem: StorageItem): Boolean =
                oldItem == newItem

            override fun areItemsTheSame(oldItem: StorageItem, newItem: StorageItem): Boolean =
                oldItem.id == newItem.id

            override fun getChangePayload(oldItem: StorageItem, newItem: StorageItem): Any? {
                return if (sameExceptPrice(oldItem, newItem) || sameExceptCount(oldItem, newItem)) {
                    PAYLOAD_SCORE
                } else {
                    null
                }
            }
        }

        private fun sameExceptPrice(oldItem: StorageItem, newItem: StorageItem): Boolean {
            return oldItem.copy(detail = newItem.detail) == newItem
        }

        private fun sameExceptCount(oldItem: StorageItem, newItem: StorageItem): Boolean {
            return oldItem.copy(count = newItem.count) == newItem
        }
    }
}