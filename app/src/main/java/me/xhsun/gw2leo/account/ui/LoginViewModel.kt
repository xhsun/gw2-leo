package me.xhsun.gw2leo.account.ui

import androidx.databinding.Bindable
import androidx.databinding.Observable
import androidx.databinding.ObservableField
import androidx.databinding.PropertyChangeRegistry
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import me.xhsun.gw2leo.BR
import me.xhsun.gw2leo.account.service.IRefreshService
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val refreshService: IRefreshService,
) : ViewModel(), Observable {
    private val callbacks: PropertyChangeRegistry by lazy { PropertyChangeRegistry() }

    @get:Bindable
    var loading: Boolean = false
    val api: ObservableField<String> = ObservableField<String>()

    val shouldTransfer: MutableLiveData<Boolean> by lazy {
        MutableLiveData<Boolean>()
    }

    fun onEnter() {
        val value = api.get()
        if (value != null && value.isNotEmpty()) {
            this.toggleLoading(true)
            this.initializeAccount(value)
        } else {
            TODO("Show error message for enter api key")
        }
    }

    private fun toggleLoading(state: Boolean) {
        loading = state
        notifyPropertyChanged(BR.loading)
    }

    private fun initializeAccount(api: String) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val result = refreshService.refreshAccount(api)
                if (result) {
                    shouldTransfer.value = true
                } else {
                    toggleLoading(false)
                    Timber.d("Unexpected issue happened when initializing account::${api}")
                    TODO("Show error message for try again")
                }
            } catch (e: Exception) {
                toggleLoading(false)
                Timber.d("Unable to authenticate with endpoint::${api}::${e.message}")
                TODO("Show error message for re-enter api key")
            }

        }
    }


    /**
     * Notifies listeners that a specific property has changed. The getter for the property
     * that changes should be marked with [Bindable] to generate a field in
     * `BR` to be used as `fieldId`.
     *
     * @param fieldId The generated BR id for the Bindable field.
     */
    fun notifyPropertyChanged(fieldId: Int) {
        callbacks.notifyCallbacks(this, fieldId, null)
    }

    override fun addOnPropertyChangedCallback(callback: Observable.OnPropertyChangedCallback?) {
        callbacks.add(callback)
    }

    override fun removeOnPropertyChangedCallback(callback: Observable.OnPropertyChangedCallback?) {
        callbacks.remove(callback)
    }
}