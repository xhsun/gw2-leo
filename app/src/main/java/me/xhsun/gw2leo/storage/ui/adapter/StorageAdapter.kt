package me.xhsun.gw2leo.storage.ui.adapter

import android.view.ViewGroup
import androidx.fragment.app.FragmentManager
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import me.xhsun.gw2leo.storage.StorageItem


class StorageAdapter(private val fragmentManager: FragmentManager) :
    PagingDataAdapter<StorageItem, StorageViewHolder>(STORAGE_COMPARATOR) {

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

    companion object {
        private val PAYLOAD_SCORE = Any()
        val STORAGE_COMPARATOR = object : DiffUtil.ItemCallback<StorageItem>() {
            override fun areContentsTheSame(
                oldItem: StorageItem,
                newItem: StorageItem
            ): Boolean =
                oldItem == newItem

            override fun areItemsTheSame(
                oldItem: StorageItem,
                newItem: StorageItem
            ): Boolean =
                oldItem.id == newItem.id

            override fun getChangePayload(
                oldItem: StorageItem,
                newItem: StorageItem
            ): Any? {
                return if (sameExceptDetails(oldItem, newItem) || sameExceptCount(
                        oldItem,
                        newItem
                    ) || sameExceptPrice(oldItem, newItem)
                ) {
                    PAYLOAD_SCORE
                } else {
                    null
                }
            }
        }

        private fun sameExceptDetails(oldItem: StorageItem, newItem: StorageItem): Boolean {
            return oldItem.copy(detail = newItem.detail) == newItem
        }

        private fun sameExceptPrice(oldItem: StorageItem, newItem: StorageItem): Boolean {
            return oldItem.copy(price = newItem.price) == newItem
        }

        private fun sameExceptCount(oldItem: StorageItem, newItem: StorageItem): Boolean {
            return oldItem.copy(count = newItem.count) == newItem
        }
    }
}
