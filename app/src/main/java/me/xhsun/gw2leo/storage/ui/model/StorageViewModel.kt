package me.xhsun.gw2leo.storage.ui.model

import androidx.databinding.Bindable
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.asFlow
import androidx.lifecycle.viewModelScope
import androidx.paging.CombinedLoadStates
import androidx.paging.LoadState
import androidx.paging.cachedIn
import androidx.recyclerview.widget.RecyclerView
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flatMapLatest
import me.xhsun.gw2leo.BR
import me.xhsun.gw2leo.R
import me.xhsun.gw2leo.core.config.MATERIAL_STORAGE_PREFIX
import me.xhsun.gw2leo.core.config.STORAGE_DISPLAY_KEY
import me.xhsun.gw2leo.core.model.ObservableViewModel
import me.xhsun.gw2leo.storage.service.IStorageService
import me.xhsun.gw2leo.storage.ui.adapter.StorageAdapter
import javax.inject.Inject

@HiltViewModel
class StorageViewModel @Inject constructor(
    private val storageService: IStorageService,
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
    var storageErrMsg: String = "NO ITEM FOUND"
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
            val isMaterial = it.storageType.contains(MATERIAL_STORAGE_PREFIX)
            if (isMaterial) {
                storageService.materialStorageData(
                    it.storageState,
                    viewModelScope
                )
            } else {
                storageService.storageStream(
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

    private fun shouldUpdate(storageDisplay: StorageDisplay): Boolean {
        return savedStateHandle.get<StorageDisplay>(STORAGE_DISPLAY_KEY) != storageDisplay
    }

    fun onRetry() {
        storageLoading = true
        storageErrMsg = ""
        adapter.refresh()
    }

    fun changeState(list: RecyclerView, state: CombinedLoadStates) {
        when (state.refresh) {
            is LoadState.NotLoading -> {
                list.scrollToPosition(0)
                if (state.append.endOfPaginationReached || state.prepend.endOfPaginationReached) {
                    storageLoading = false
                    if (list.adapter == null || list.adapter!!.itemCount < 1) {
                        storageErrMsg = list.context.getString(R.string.err_items_not_found)
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
}