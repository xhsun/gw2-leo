package me.xhsun.gw2leo.storage.datastore.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.concurrent.TimeUnit

@Entity
data class LastModified(
    @PrimaryKey val type: String,
    @ColumnInfo(defaultValue = "CURRENT_TIMESTAMP")
    val lastModified: Long
) {

    /**
     * @return elapsed time(in hour) from [lastModified] to now
     */
    fun elapsedTime(): Long {
        return TimeUnit.MILLISECONDS.toHours(System.currentTimeMillis()) - TimeUnit.MILLISECONDS.toHours(
            lastModified
        )
    }
}
