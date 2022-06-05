package me.xhsun.gw2leo.storage.ui.model

import android.os.Parcel
import android.os.Parcelable

data class StorageDisplay(
    val storageType: String?,
    val orderBy: String?,
    val isAsc: Boolean
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString(),
        parcel.readString(),
        parcel.readByte() != 0.toByte()
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(storageType)
        parcel.writeString(orderBy)
        parcel.writeByte(if (isAsc) 1 else 0)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<StorageDisplay> {
        override fun createFromParcel(parcel: Parcel): StorageDisplay {
            return StorageDisplay(parcel)
        }

        override fun newArray(size: Int): Array<StorageDisplay?> {
            return arrayOfNulls(size)
        }
    }
}
