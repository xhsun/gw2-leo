package me.xhsun.gw2leo

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.os.bundleOf
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.lifecycleScope
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import me.xhsun.gw2leo.account.service.IAccountService
import me.xhsun.gw2leo.account.service.ICharacterService
import me.xhsun.gw2leo.account.ui.LoginActivity
import me.xhsun.gw2leo.config.ACCOUNT_ID_KEY
import me.xhsun.gw2leo.config.CHARACTER_LIST_KEY
import me.xhsun.gw2leo.databinding.ActivityMainBinding
import timber.log.Timber
import javax.inject.Inject


@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    @Inject
    lateinit var accountService: IAccountService

    @Inject
    lateinit var characterService: ICharacterService

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding: ActivityMainBinding =
            DataBindingUtil.setContentView(this, R.layout.activity_main)
        binding.lifecycleOwner = this

        try {
            lifecycleScope.launch(Dispatchers.IO) {
                val accountID = accountService.accountID()
                Timber.d("Currently logged in for $accountID")
                val characters = characterService.characters().toTypedArray()

                val args = bundleOf(
                    CHARACTER_LIST_KEY to characters,
                    ACCOUNT_ID_KEY to accountID
                )
                binding.navigationRail.setOnItemSelectedListener { item ->
                    navController.navigate(item.itemId, args)
                    true
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