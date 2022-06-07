package me.xhsun.gw2leo.storage.datastore.dao

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import me.xhsun.gw2leo.storage.datastore.entity.MaterialStorage
import me.xhsun.gw2leo.storage.datastore.entity.MaterialStorageBase

@Dao
interface MaterialStorageDAO {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(items: List<MaterialStorageBase>)

    @Query("DELETE FROM materialstorage WHERE accountID = :accountID")
    fun bulkDelete(accountID: String)

    @Query("SELECT *, i.id as itemItemID, t.id as categoryCategoryID, t.name as categoryName FROM materialstorage as storage INNER JOIN item as i INNER JOIN materialtype as t ON storage.itemID = i.id AND storage.categoryID = t.id ORDER BY i.buy ASC")
    fun getAllOderByBuyAscending(): PagingSource<Int, MaterialStorage>

    @Query("SELECT *, i.id as itemItemID, t.id as categoryCategoryID, t.name as categoryName FROM materialstorage as storage INNER JOIN item as i INNER JOIN materialtype as t ON storage.itemID = i.id AND storage.categoryID = t.id ORDER BY i.buy DESC")
    fun getAllOderByBuyDescending(): PagingSource<Int, MaterialStorage>

    @Query("SELECT *, i.id as itemItemID, t.id as categoryCategoryID, t.name as categoryName FROM materialstorage as storage INNER JOIN item as i INNER JOIN materialtype as t ON storage.itemID = i.id AND storage.categoryID = t.id ORDER BY i.sell ASC")
    fun getAllOderBySellAscending(): PagingSource<Int, MaterialStorage>

    @Query("SELECT *, i.id as itemItemID, t.id as categoryCategoryID, t.name as categoryName FROM materialstorage as storage INNER JOIN item as i INNER JOIN materialtype as t ON storage.itemID = i.id AND storage.categoryID = t.id ORDER BY i.sell DESC")
    fun getAllOderBySellDescending(): PagingSource<Int, MaterialStorage>

    @Query("SELECT *, i.id as itemItemID, t.id as categoryCategoryID, t.name as categoryName FROM materialstorage as storage INNER JOIN item as i INNER JOIN materialtype as t ON storage.itemID = i.id AND i.sellable = 1 AND storage.categoryID = t.id ORDER BY i.buy ASC")
    fun getAllOderByBuyAscendingSellable(): PagingSource<Int, MaterialStorage>

    @Query("SELECT *, i.id as itemItemID, t.id as categoryCategoryID, t.name as categoryName FROM materialstorage as storage INNER JOIN item as i INNER JOIN materialtype as t ON storage.itemID = i.id AND i.sellable = 1 AND storage.categoryID = t.id ORDER BY i.buy DESC")
    fun getAllOderByBuyDescendingSellable(): PagingSource<Int, MaterialStorage>

    @Query("SELECT *, i.id as itemItemID, t.id as categoryCategoryID, t.name as categoryName FROM materialstorage as storage INNER JOIN item as i INNER JOIN materialtype as t ON storage.itemID = i.id AND i.sellable = 1 AND storage.categoryID = t.id ORDER BY i.sell ASC")
    fun getAllOderBySellAscendingSellable(): PagingSource<Int, MaterialStorage>

    @Query("SELECT *, i.id as itemItemID, t.id as categoryCategoryID, t.name as categoryName FROM materialstorage as storage INNER JOIN item as i INNER JOIN materialtype as t ON storage.itemID = i.id AND i.sellable = 1 AND storage.categoryID = t.id ORDER BY i.sell DESC")
    fun getAllOderBySellDescendingSellable(): PagingSource<Int, MaterialStorage>
}