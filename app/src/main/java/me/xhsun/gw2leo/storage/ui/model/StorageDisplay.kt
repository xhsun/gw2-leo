package me.xhsun.gw2leo.storage.ui.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class StorageDisplay(
    val storageType: String?,
    val orderBy: String?,
    val isAsc: Boolean
) : Parcelable
