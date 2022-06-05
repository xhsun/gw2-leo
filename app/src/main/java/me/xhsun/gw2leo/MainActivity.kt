package me.xhsun.gw2leo

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.os.bundleOf
import androidx.fragment.app.add
import androidx.fragment.app.commit
import dagger.hilt.android.AndroidEntryPoint
import me.xhsun.gw2leo.account.service.IAccountService
import me.xhsun.gw2leo.account.ui.LoginActivity
import me.xhsun.gw2leo.config.ACCOUNT_ID_KEY
import me.xhsun.gw2leo.config.DB_BANK_KEY_FORMAT
import me.xhsun.gw2leo.config.STORAGE_TYPE_KEY
import me.xhsun.gw2leo.storage.ui.StorageFragment
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
            if (savedInstanceState == null) {
                val bundle = bundleOf(
                    STORAGE_TYPE_KEY to DB_BANK_KEY_FORMAT.format(accountID),
                    ACCOUNT_ID_KEY to accountID
                )
                Timber.d("Attempt to show storage::$accountID")
                supportFragmentManager.commit {
                    setReorderingAllowed(true)
                    add<StorageFragment>(R.id.main_fragment_container, args = bundle)
                }
            }
        } catch (e: Exception) {
            Timber.d("Start login process")
            val intent = Intent(this, LoginActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
            startActivity(intent)
        }
    }
}