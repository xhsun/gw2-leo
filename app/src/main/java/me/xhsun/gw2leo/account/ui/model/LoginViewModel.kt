package me.xhsun.gw2leo.account.ui.model

import androidx.databinding.Bindable
import androidx.databinding.BindingAdapter
import androidx.databinding.ObservableField
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.google.android.material.textfield.TextInputLayout
import dagger.hilt.android.lifecycle.HiltViewModel
import io.github.g00fy2.quickie.QRResult
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import me.xhsun.gw2leo.BR
import me.xhsun.gw2leo.account.error.NotLoggedInError
import me.xhsun.gw2leo.core.model.ObservableViewModel
import me.xhsun.gw2leo.core.refresh.service.IAccountRefreshService
import timber.log.Timber
import javax.inject.Inject


@HiltViewModel
class LoginViewModel @Inject constructor(
    private val refreshService: IAccountRefreshService,
) : ObservableViewModel() {

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
            is QRResult.QRUserCanceled -> "Operation Canceled"
            is QRResult.QRMissingPermission -> "Permission Denied"
            is QRResult.QRError -> {
                Timber.d("QR error::${result.exception.message}")
                result.exception.localizedMessage
            }
            else -> "Unexpected error"
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
                refreshService.initialize(api)
                states.postValue(UIState(shouldTransfer = true))
            } catch (e: Exception) {
                loading = false
                Timber.d("Unable to initialize account information::${api}::${e.message}")
                errMsg = when (e) {
                    is NotLoggedInError -> {
                        "Unable to authenticate, please try a different API key"
                    }
                    else -> {
                        "Unexpected error, please try again"
                    }
                }
            }
        }
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

