package me.xhsun.gw2leo.storage.service

import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import androidx.room.withTransaction
import me.xhsun.gw2leo.account.error.NotLoggedInError
import me.xhsun.gw2leo.account.service.IAccountService
import me.xhsun.gw2leo.config.DB_BANK_KEY_FORMAT
import me.xhsun.gw2leo.config.STARTING_PAGE_INDEX
import me.xhsun.gw2leo.datastore.IDatastoreRepository
import me.xhsun.gw2leo.storage.datastore.entity.Storage
import me.xhsun.gw2leo.storage.datastore.entity.StorageRemoteKey
import me.xhsun.gw2leo.storage.error.NoItemFoundError
import timber.log.Timber

@OptIn(ExperimentalPagingApi::class)
class StorageRemoteMediator(
    private val storageType: String,
    private val accountService: IAccountService,
    private val storageRetrievalService: IStorageRetrievalService,
    private val datastore: IDatastoreRepository
) : RemoteMediator<Int, Storage>() {

    override suspend fun load(
        loadType: LoadType,
        state: PagingState<Int, Storage>
    ): MediatorResult {
        val isBank = storageType.contains(DB_BANK_KEY_FORMAT)
        val page = when (loadType) {
            LoadType.REFRESH -> {
                val remoteKeys = getRemoteKeyClosestToCurrentPosition(state)
                remoteKeys?.nextKey?.minus(1) ?: STARTING_PAGE_INDEX
            }
            LoadType.PREPEND -> {
                val remoteKeys = getRemoteKeyForFirstItem(state)
                remoteKeys?.prevKey
                    ?: return MediatorResult.Success(endOfPaginationReached = remoteKeys != null)
            }
            LoadType.APPEND -> {
                val remoteKeys = getRemoteKeyForLastItem(state)
                remoteKeys?.nextKey
                    ?: return MediatorResult.Success(endOfPaginationReached = remoteKeys != null)
            }
        }

        try {
            val accountID = accountService.accountID()
            val response = if (isBank) {
                storageRetrievalService.bankItems(page, state.config.pageSize)
            } else {
                storageRetrievalService.inventoryItems(storageType, page, state.config.pageSize)
            }

            val items = response.second
            datastore.withTransaction {
                // clear all tables in the database
                if (loadType == LoadType.REFRESH) {
                    datastore.remoteKeysDAO.bulkDelete(accountID, storageType)
                    datastore.storageDAO.bulkDelete(storageType)
                }
                val prevKey = if (page == STARTING_PAGE_INDEX) null else page - 1
                val nextKey = if (response.first) null else page + 1
                val keys = items.map {
                    StorageRemoteKey(
                        id = it.id, prevKey = prevKey, nextKey = nextKey,
                        accountID = accountID, storageType = storageType
                    )
                }
                datastore.remoteKeysDAO.insertAll(keys)
                val dao = items.map {
                    it.toDAO()
                }.toTypedArray()
                datastore.storageDAO.insertAll(*dao)
            }
            return MediatorResult.Success(endOfPaginationReached = response.first)
        } catch (e: Exception) {
            Timber.d("Encountered an error while loading storage data::${e.message}")
            when (e) {// Could also be IOException, HttpException
                is NotLoggedInError -> {
                    return MediatorResult.Error(e)
                }
                is NoItemFoundError -> return MediatorResult.Success(endOfPaginationReached = true)
            }
            return MediatorResult.Error(e)
        }
    }

    private suspend fun getRemoteKeyClosestToCurrentPosition(
        state: PagingState<Int, Storage>
    ): StorageRemoteKey? {
        // The paging library is trying to load data after the anchor position
        // Get the item closest to the anchor position
        return state.anchorPosition?.let { position ->
            state.closestItemToPosition(position)?.id?.let { repoId ->
                datastore.remoteKeysDAO.getByID(repoId)
            }
        }
    }

    private suspend fun getRemoteKeyForFirstItem(state: PagingState<Int, Storage>): StorageRemoteKey? {
        // Get the first page that was retrieved, that contained items.
        // From that first page, get the first item
        return state.pages.firstOrNull { it.data.isNotEmpty() }?.data?.firstOrNull()
            ?.let { item ->
                // Get the remote keys of the first items retrieved
                datastore.remoteKeysDAO.getByID(item.id)
            }
    }

    private suspend fun getRemoteKeyForLastItem(state: PagingState<Int, Storage>): StorageRemoteKey? {
        // Get the last page that was retrieved, that contained items.
        // From that last page, get the last item
        return state.pages.lastOrNull() {
            it.data.isNotEmpty()
        }?.data?.lastOrNull()
            ?.let { item ->
                // Get the remote keys of the last item retrieved
                datastore.remoteKeysDAO.getByID(item.id)
            }
    }

}