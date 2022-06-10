package me.xhsun.gw2leo.storage.ui.model

import me.xhsun.gw2leo.storage.StorageState

data class SortState(
    val isBuy: Boolean = true,
    val isAsc: Boolean = false,
    val sellable: Boolean = false
) {
    fun toStorageDisplay(storageType: String): StorageDisplay {
        val state = if (isBuy) {
            checkBuy()
        } else {
            checkSell()
        }
        return StorageDisplay(storageType, state)
    }

    /**
     * @return storage state for ordering by buy
     */
    private fun checkBuy(): StorageState {
        return if (isAsc) {
            if (sellable) {
                StorageState.BUY_ASC_SELLABLE
            } else {
                StorageState.BUY_ASC
            }
        } else {
            if (sellable) {
                StorageState.BUY_DESC_SELLABLE
            } else {
                StorageState.BUY_DESC
            }
        }
    }

    /**
     * @return storage state for ordering by sell
     */
    private fun checkSell(): StorageState {
        return if (isAsc) {
            if (sellable) {
                StorageState.SELL_ASC_SELLABLE
            } else {
                StorageState.SELL_ASC
            }
        } else {
            if (sellable) {
                StorageState.SELL_DESC_SELLABLE
            } else {
                StorageState.SELL_DESC
            }
        }
    }
}
