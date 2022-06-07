package me.xhsun.gw2leo.storage.ui.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import me.xhsun.gw2leo.storage.StorageState

@Parcelize
data class StorageDisplay(
    val storageType: String,
    val storageState: StorageState
) : Parcelable
