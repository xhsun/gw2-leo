package me.xhsun.gw2leo.storage.ui.model

import android.content.Context
import androidx.databinding.Bindable
import androidx.lifecycle.LiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.asFlow
import androidx.lifecycle.viewModelScope
import androidx.paging.CombinedLoadStates
import androidx.paging.LoadState
import androidx.paging.cachedIn
import androidx.recyclerview.widget.RecyclerView
import androidx.work.Data
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkInfo
import androidx.work.WorkManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flatMapLatest
import me.xhsun.gw2leo.BR
import me.xhsun.gw2leo.R
import me.xhsun.gw2leo.core.config.MATERIAL_STORAGE_PREFIX
import me.xhsun.gw2leo.core.config.STORAGE_DISPLAY_KEY
import me.xhsun.gw2leo.core.config.STORAGE_TYPE_KEY
import me.xhsun.gw2leo.core.model.ObservableViewModel
import me.xhsun.gw2leo.core.refresh.work.StorageRefreshWorker
import me.xhsun.gw2leo.storage.service.IStorageRepository
import me.xhsun.gw2leo.storage.ui.adapter.StorageAdapter
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class StorageViewModel @Inject constructor(
    private val storageRepository: IStorageRepository,
    private val savedStateHandle: SavedStateHandle
) : ObservableViewModel() {
    private lateinit var adapter: StorageAdapter

    @get:Bindable
    var storageLoading: Boolean = false
        set(value) {
            if (value != field) {
                field = value
                notifyPropertyChanged(BR.storageLoading)
            }
        }

    @get:Bindable
    var storageErrMsg: String = ""
        set(value) {
            if (value != field) {
                field = value
                notifyPropertyChanged(BR.storageErrMsg)
            }
        }

    @OptIn(ExperimentalCoroutinesApi::class)
    val items = savedStateHandle.getLiveData<StorageDisplay>(STORAGE_DISPLAY_KEY)
        .asFlow()
        .flatMapLatest {
            if (it.storageType.contains(MATERIAL_STORAGE_PREFIX)) {
                storageRepository.materialStorageData(
                    it.storageState,
                    viewModelScope
                )
            } else {
                storageRepository.storageStream(
                    it.storageType,
                    it.storageState,
                    viewModelScope
                )
            }
        }
        .cachedIn(viewModelScope)

    fun update(storageDisplay: StorageDisplay, adapter: StorageAdapter) {
        if (shouldUpdate(storageDisplay)) {
            storageLoading = true
            storageErrMsg = ""
            this.adapter = adapter
            savedStateHandle[STORAGE_DISPLAY_KEY] = storageDisplay
        }
    }

    fun hardRefresh(ctx: Context, storageType: String): LiveData<WorkInfo?> {
        Timber.d("Start hard refresh process")
        val builder = Data.Builder()
        builder.putString(STORAGE_TYPE_KEY, storageType)
        val input = builder.build()

        val workManager = WorkManager.getInstance(ctx)
        val worker =
            OneTimeWorkRequestBuilder<StorageRefreshWorker>()
                .setInputData(input)
                .build()
        workManager.enqueue(worker)
        return workManager.getWorkInfoByIdLiveData(worker.id)
    }

    fun changeState(list: RecyclerView, state: CombinedLoadStates) {
        when (state.refresh) {
            is LoadState.NotLoading -> {
                list.scrollToPosition(0)
                if (state.append.endOfPaginationReached || state.prepend.endOfPaginationReached) {
                    storageLoading = false
                    storageErrMsg = if (list.adapter == null || list.adapter!!.itemCount < 1) {
                        list.context.getString(R.string.err_items_not_found)
                    } else {
                        ""
                    }
                }
            }
            is LoadState.Error -> {
                storageLoading = false
                storageErrMsg = (state.refresh as LoadState.Error).error.message.toString()
            }
            else -> {
                storageErrMsg = ""
            }
        }
    }

    fun onRetry() {
        storageLoading = true
        storageErrMsg = ""
        adapter.refresh()
    }

    private fun shouldUpdate(storageDisplay: StorageDisplay): Boolean {
        return savedStateHandle.get<StorageDisplay>(STORAGE_DISPLAY_KEY) != storageDisplay
    }
}