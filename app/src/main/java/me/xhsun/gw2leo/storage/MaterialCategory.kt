package me.xhsun.gw2leo.storage

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import me.xhsun.gw2leo.storage.datastore.entity.MaterialType

@Parcelize
data class MaterialCategory(
    val id: Int,
    val name: String
) : Parcelable {
    fun toDAO(): MaterialType {
        return MaterialType(
            id = id,
            name = name
        )
    }
}
