package me.xhsun.gw2leo.storage.ui.model

import androidx.databinding.Bindable
import androidx.databinding.Observable
import androidx.databinding.PropertyChangeRegistry
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asFlow
import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flatMapLatest
import me.xhsun.gw2leo.config.MATERIAL_STORAGE_PREFIX
import me.xhsun.gw2leo.config.STORAGE_DISPLAY_KEY
import me.xhsun.gw2leo.storage.service.IStorageService
import javax.inject.Inject

@HiltViewModel
class StorageViewModel @Inject constructor(
    private val storageService: IStorageService,
    private val savedStateHandle: SavedStateHandle
) : ViewModel(), Observable {
    private val callbacks: PropertyChangeRegistry by lazy { PropertyChangeRegistry() }

    @OptIn(ExperimentalCoroutinesApi::class)
    val items = savedStateHandle.getLiveData<StorageDisplay>(STORAGE_DISPLAY_KEY)
        .asFlow()
        .flatMapLatest {
            val isMaterial = it.storageType!!.contains(MATERIAL_STORAGE_PREFIX)
            if (isMaterial) {
                storageService.materialStorageData(it.orderBy!!, it.isAsc, viewModelScope)
            } else {
                storageService.storageStream(
                    it.storageType,
                    it.orderBy!!,
                    it.isAsc,
                    viewModelScope
                )
            }
        }
        .cachedIn(viewModelScope)

    fun updateItem(storageDisplay: StorageDisplay) {
        savedStateHandle[STORAGE_DISPLAY_KEY] = storageDisplay
    }

    /**
     * Notifies listeners that a specific property has changed. The getter for the property
     * that changes should be marked with [Bindable] to generate a field in
     * `BR` to be used as `fieldId`.
     *
     * @param fieldId The generated BR id for the Bindable field.
     */
    private fun notifyPropertyChanged(fieldId: Int) {
        callbacks.notifyCallbacks(this, fieldId, null)
    }

    override fun addOnPropertyChangedCallback(callback: Observable.OnPropertyChangedCallback?) {
        callbacks.add(callback)
    }

    override fun removeOnPropertyChangedCallback(callback: Observable.OnPropertyChangedCallback?) {
        callbacks.remove(callback)
    }
}