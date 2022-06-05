package me.xhsun.gw2leo.account.ui

import androidx.databinding.*
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.material.textfield.TextInputLayout
import dagger.hilt.android.lifecycle.HiltViewModel
import io.github.g00fy2.quickie.QRResult
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import me.xhsun.gw2leo.BR
import me.xhsun.gw2leo.account.error.NotLoggedInError
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
        set(value) {
            if (value != field) {
                field = value
                notifyPropertyChanged(BR.loading)
            }
        }

    @get:Bindable
    var errMsg: String = ""
        set(value) {
            if (value != field) {
                field = value
                notifyPropertyChanged(BR.errMsg)
            }
        }

    val api: ObservableField<String> = ObservableField()

    val states: MutableLiveData<UIState> by lazy {
        MutableLiveData<UIState>()
    }

    fun onTextChange() {
        errMsg = ""
    }

    fun onEnter() {
        val value = api.get()
        Timber.d("Enter button clicked::${value}")
        if (value != null && value.isNotEmpty()) {
            errMsg = ""
            loading = true
            this.initializeAccount(value)
        } else {
            errMsg = "API key is required"
        }

    }

    fun handleQR(result: QRResult) {
        val text = when (result) {
            is QRResult.QRSuccess -> result.content.rawValue
            QRResult.QRUserCanceled -> "Operation Canceled"
            QRResult.QRMissingPermission -> "Permission Denied"
            is QRResult.QRError -> {
                Timber.d("QR error::${result.exception.message}")
                result.exception.localizedMessage
            }
        }
        if (result is QRResult.QRSuccess) {
            errMsg = ""
            api.set(text)
        } else {
            states.value = UIState(snackbarText = text)
        }
    }

    private fun initializeAccount(api: String) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val result = refreshService.initializeAccount(api)
                if (result) {
                    states.postValue(UIState(shouldTransfer = true))
                } else {
                    loading = false
                    Timber.d("Unexpected issue happened when initializing account::${api}")
                    errMsg = "Unexpected error, please try again"
                }
            } catch (e: Exception) {
                loading = false
                Timber.d("Unable to initialize account information::${api}::${e.message}")
                errMsg = when (e) {
                    is NotLoggedInError -> {
                        "Unable to authenticate, please try a different API key"
                    }
                    else -> {
                        "Network issue, please try again"
                    }
                }
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
    private fun notifyPropertyChanged(fieldId: Int) {
        callbacks.notifyCallbacks(this, fieldId, null)
    }

    override fun addOnPropertyChangedCallback(callback: Observable.OnPropertyChangedCallback?) {
        callbacks.add(callback)
    }

    override fun removeOnPropertyChangedCallback(callback: Observable.OnPropertyChangedCallback?) {
        callbacks.remove(callback)
    }

    companion object {
        @JvmStatic
        @BindingAdapter("errorText")
        fun displayErrorMessage(view: TextInputLayout, errorMessage: String?) {
            view.errorIconDrawable = null
            view.error = errorMessage
        }
    }
}

