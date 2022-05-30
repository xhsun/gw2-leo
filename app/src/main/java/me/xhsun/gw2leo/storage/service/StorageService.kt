package me.xhsun.gw2leo.storage.service

import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import me.xhsun.gw2leo.datastore.SQLDatabase
import me.xhsun.gw2leo.http.IHttpClient
import me.xhsun.gw2leo.storage.MaterialItem
import me.xhsun.gw2leo.storage.StorageItem

class StorageService(private val datastore: SQLDatabase, private val client: IHttpClient) {
    fun storageData(storageType: String): LiveData<List<StorageItem>> {
        return Transformations.map(datastore.storageDAO.getAll(storageType)) { list ->
            list.map {
                it.toDomain()
            }
        }
    }

    fun materialStorageData(accountID: String): LiveData<List<MaterialItem>> {
        return Transformations.map(datastore.materialStorageDAO.getAll(accountID)) { list ->
            list.map {
                it.toDomain()
            }
        }
    }

    fun updateStorage(accountID: String, storageType: String) {
    }
}