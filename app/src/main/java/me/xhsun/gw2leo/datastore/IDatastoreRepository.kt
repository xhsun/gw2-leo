package me.xhsun.gw2leo.datastore

import androidx.room.Database
import androidx.room.RoomDatabase
import me.xhsun.gw2leo.account.datastore.dao.AccountDAO
import me.xhsun.gw2leo.account.datastore.dao.CharacterDAO
import me.xhsun.gw2leo.account.datastore.entity.Account
import me.xhsun.gw2leo.account.datastore.entity.Character
import me.xhsun.gw2leo.storage.datastore.dao.ItemDAO
import me.xhsun.gw2leo.storage.datastore.dao.MaterialStorageDAO
import me.xhsun.gw2leo.storage.datastore.dao.MaterialTypeDAO
import me.xhsun.gw2leo.storage.datastore.dao.StorageDAO
import me.xhsun.gw2leo.storage.datastore.entity.Item
import me.xhsun.gw2leo.storage.datastore.entity.MaterialStorageBase
import me.xhsun.gw2leo.storage.datastore.entity.MaterialType
import me.xhsun.gw2leo.storage.datastore.entity.StorageBase

@Database(
    entities = [Account::class, Character::class, Item::class, MaterialStorageBase::class, MaterialType::class, StorageBase::class],
    version = 1
)
abstract class IDatastoreRepository : RoomDatabase() {
    abstract val accountDAO: AccountDAO
    abstract val characterDAO: CharacterDAO
    abstract val itemDAO: ItemDAO
    abstract val materialStorageDAO: MaterialStorageDAO
    abstract val materialTypeDAO: MaterialTypeDAO
    abstract val storageDAO: StorageDAO
}