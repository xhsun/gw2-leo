package me.xhsun.gw2leo

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import dagger.hilt.android.AndroidEntryPoint
import me.xhsun.gw2leo.account.service.IAccountService
import me.xhsun.gw2leo.account.ui.LoginActivity
import timber.log.Timber
import javax.inject.Inject


@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    @Inject
    lateinit var accountService: IAccountService
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        try {
            val accountID = accountService.accountID()
            Timber.d("Currently logged in for $accountID")
        } catch (e: Exception) {
            Timber.d("Start login process")
            val intent = Intent(this, LoginActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
            startActivity(intent)
        }

    }
}