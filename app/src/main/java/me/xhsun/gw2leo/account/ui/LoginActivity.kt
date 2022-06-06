package me.xhsun.gw2leo.account.ui

import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.TextInputLayout
import dagger.hilt.android.AndroidEntryPoint
import io.github.g00fy2.quickie.ScanCustomCode
import io.github.g00fy2.quickie.config.BarcodeFormat
import io.github.g00fy2.quickie.config.ScannerConfig
import me.xhsun.gw2leo.MainActivity
import me.xhsun.gw2leo.R
import me.xhsun.gw2leo.account.ui.model.LoginViewModel
import me.xhsun.gw2leo.account.ui.model.UIState
import me.xhsun.gw2leo.databinding.ActivityLoginBinding
import timber.log.Timber


@AndroidEntryPoint
class LoginActivity : AppCompatActivity() {
    private val viewModel by viewModels<LoginViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding: ActivityLoginBinding =
            DataBindingUtil.setContentView(this, R.layout.activity_login)
        binding.viewmodel = viewModel
        binding.lifecycleOwner = this

        val scanQrCode = registerForActivityResult(ScanCustomCode(), viewModel::handleQR)
        val inputLayout = findViewById<TextInputLayout>(R.id.login_input_layout)
        inputLayout.setEndIconOnClickListener {
            scanQrCode.launch(
                ScannerConfig.build {
                    setBarcodeFormats(listOf(BarcodeFormat.FORMAT_QR_CODE))
                    setOverlayStringRes(R.string.login_title)
                    setHapticSuccessFeedback(false)
                    setShowTorchToggle(true)
                    setHorizontalFrameRatio(2.2f)
                    setUseFrontCamera(false)
                }
            )
        }

        val stateObserver = Observer<UIState> {
            if (it != null) {
                if (it.snackbarText.isNotEmpty()) {
                    Snackbar.make(binding.root, it.snackbarText, Snackbar.LENGTH_SHORT).show()
                }
                if (it.shouldTransfer) {
                    Timber.d("Acquired account information, complete login process")
                    val intent = Intent(this, MainActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
                    startActivity(intent)
                    finishAndRemoveTask()
                }
            }
        }

        viewModel.states.observe(this, stateObserver)
    }
}