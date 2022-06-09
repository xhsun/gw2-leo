package me.xhsun.gw2leo.storage.service.comparator

import androidx.recyclerview.widget.DiffUtil
import me.xhsun.gw2leo.storage.StorageItem

class StorageItemComparator : DiffUtil.ItemCallback<StorageItem>() {
    private val payload = Any()

    override fun areItemsTheSame(oldItem: StorageItem, newItem: StorageItem): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: StorageItem, newItem: StorageItem): Boolean {
        return oldItem == newItem
    }

    override fun getChangePayload(
        oldItem: StorageItem,
        newItem: StorageItem
    ): Any? {
        return if (sameExceptDetails(oldItem, newItem) ||
            sameExceptCount(oldItem, newItem) || sameExceptPrice(oldItem, newItem)
        ) {
            payload
        } else {
            null
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
