package me.xhsun.gw2leo.account.ui

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.TextInputLayout
import dagger.hilt.android.AndroidEntryPoint
import io.github.g00fy2.quickie.ScanCustomCode
import io.github.g00fy2.quickie.config.BarcodeFormat
import io.github.g00fy2.quickie.config.ScannerConfig
import me.xhsun.gw2leo.MainActivity
import me.xhsun.gw2leo.R
import me.xhsun.gw2leo.databinding.ActivityLoginBinding
import timber.log.Timber


@AndroidEntryPoint
class LoginActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding: ActivityLoginBinding =
            DataBindingUtil.setContentView(this, R.layout.activity_login)
        val viewModel = ViewModelProvider(this)[LoginViewModel::class.java]
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

        val transferObserver = Observer<Boolean> {
            if (it) {
                Timber.d("Acquired account information, complete login process")
                val intent = Intent(this, MainActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
                startActivity(intent)
                finishAndRemoveTask()
            }
        }

        val snackbarObserver = Observer<String> {
            if (it != null) {
                Snackbar.make(binding.root, it, Snackbar.LENGTH_SHORT).show()
            }
        }

        viewModel.shouldTransfer.observe(this, transferObserver)
        viewModel.snackbarText.observe(this, snackbarObserver)
    }
}