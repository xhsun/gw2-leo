package me.xhsun.gw2leo.storage

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import me.xhsun.gw2leo.storage.datastore.entity.MaterialStorageBase
import me.xhsun.gw2leo.storage.datastore.entity.StorageBase

@Parcelize
data class StorageItem(
    val id: Int,
    val accountID: String,
    val category: MaterialCategory?,
    val detail: Item,
    val storageType: String,
    val count: Int,
    val charges: Int?,
    val binding: String,
    val bindTo: String?,
    val price: Int,
    val gold: Int,
    val silver: Int,
    val copper: Int,
) : Parcelable {
    fun toMaterialDAO(): MaterialStorageBase {
        return MaterialStorageBase(
            id = id,
            itemID = detail.id,
            accountID = accountID,
            categoryID = category!!.id,
            binding = binding,
            count = count
        )
    }

    fun toStorageDAO(): StorageBase {
        return StorageBase(
            id = id,
            itemID = detail.id,
            storageType = storageType,
            charges = charges,
            binding = binding,
            bindTo = bindTo,
            count = count
        )
    }

    fun update(item: Item, category: MaterialCategory?): StorageItem {
        return StorageItem(
            id = id,
            accountID = accountID,
            detail = item,
            category = category,
            storageType = storageType,
            charges = charges,
            binding = binding,
            bindTo = bindTo,
            count = count,
            price = price,
            gold = gold,
            silver = silver,
            copper = copper
        )
    }

    companion object {
        /**
         * Create an empty story item
         */
        @JvmStatic
        fun emptyStorageItem(): StorageItem {
            return StorageItem(
                id = 0,
                accountID = "",
                detail = Item(
                    id = 0,
                    chatLink = "",
                    name = "",
                    icon = "",
                    description = "",
                    rarity = "",
                    level = 0,
                    sellable = false,
                    buy = 0,
                    buyGold = 0,
                    buySilver = 0,
                    buyCopper = 0,
                    sell = 0,
                    sellGold = 0,
                    sellSilver = 0,
                    sellCopper = 0
                ),
                category = null,
                storageType = "",
                charges = 0,
                binding = "",
                bindTo = "",
                count = 0,
                price = 0,
                gold = 0,
                silver = 0,
                copper = 0
            )
        }
    }
}
