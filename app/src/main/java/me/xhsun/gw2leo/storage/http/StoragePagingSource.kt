package me.xhsun.gw2leo.storage.http

import androidx.paging.PagingSource
import androidx.paging.PagingState
import me.xhsun.gw2leo.config.DB_BANK_KEY_FORMAT
import me.xhsun.gw2leo.config.MAX_RESPONSE_SIZE
import me.xhsun.gw2leo.config.STARTING_PAGE_INDEX
import me.xhsun.gw2leo.storage.StorageItem
import me.xhsun.gw2leo.storage.error.NoItemFoundError
import me.xhsun.gw2leo.storage.service.IStorageRetrievalService
import timber.log.Timber

class StoragePagingSource(
    private val storageType: String,
    private val storageRetrievalService: IStorageRetrievalService
) : PagingSource<Int, StorageItem>() {
    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, StorageItem> {
        val position = params.key ?: STARTING_PAGE_INDEX
        val pageSize =
            if (params.loadSize > MAX_RESPONSE_SIZE) MAX_RESPONSE_SIZE else params.loadSize

        return try {
            val response = if (storageType.contains(DB_BANK_KEY_FORMAT)) {
                storageRetrievalService.bankItems(position, pageSize)
            } else {
                storageRetrievalService.inventoryItems(storageType, position, pageSize)
            }
            LoadResult.Page(
                data = response.second,
                prevKey = if (position == STARTING_PAGE_INDEX) null else position - 1,
                nextKey = if (response.first) null else position + 1
            )
        } catch (e: Exception) {
            Timber.d("Encountered an error while trying to load more from endpoint::${e.message}")
            when (e) {
                is NoItemFoundError -> {
                    return LoadResult.Page(
                        data = emptyList(),
                        prevKey = if (position == STARTING_PAGE_INDEX) null else position - 1,
                        nextKey = null
                    )
                }
            }
            return LoadResult.Error(e)
        }
    }

    override fun getRefreshKey(state: PagingState<Int, StorageItem>): Int? {
        // We need to get the previous key (or next key if previous is null) of the page
        // that was closest to the most recently accessed index.
        // Anchor position is the most recently accessed index
        return state.anchorPosition?.let { anchorPosition ->
            state.closestPageToPosition(anchorPosition)?.prevKey?.plus(1)
                ?: state.closestPageToPosition(anchorPosition)?.nextKey?.minus(1)
        }
    }
}