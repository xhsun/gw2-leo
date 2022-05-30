package me.xhsun.gw2leo.storage.repository

import me.xhsun.gw2leo.datastore.SQLDatabase
import me.xhsun.gw2leo.http.IHttpClient

class StorageRepository(private val datastore: SQLDatabase, private val client: IHttpClient) {
}