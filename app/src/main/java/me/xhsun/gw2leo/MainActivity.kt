package me.xhsun.gw2leo

import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.commit
import androidx.lifecycle.lifecycleScope
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import me.xhsun.gw2leo.account.service.IAccountService
import me.xhsun.gw2leo.account.service.ICharacterService
import me.xhsun.gw2leo.account.ui.LoginActivity
import me.xhsun.gw2leo.core.config.BANK_STORAGE_PREFIX
import me.xhsun.gw2leo.core.config.INVENTORY_STORAGE_PREFIX
import me.xhsun.gw2leo.core.config.MATERIAL_STORAGE_PREFIX
import me.xhsun.gw2leo.databinding.ActivityMainBinding
import me.xhsun.gw2leo.storage.ui.InventoryFragment
import me.xhsun.gw2leo.storage.ui.StorageFragment
import me.xhsun.gw2leo.storage.ui.model.SortViewModel
import timber.log.Timber
import javax.inject.Inject


@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    private var fragments: MutableMap<String, Fragment> =
        emptyMap<String, Fragment>().toMutableMap()
    private val viewModel by viewModels<SortViewModel>()

    @Inject
    lateinit var accountService: IAccountService

    @Inject
    lateinit var characterService: ICharacterService

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding: ActivityMainBinding =
            DataBindingUtil.setContentView(this, R.layout.activity_main)
        binding.lifecycleOwner = this
        binding.viewmodel = viewModel

        try {
            val accountID = accountService.accountID()
            Timber.d("Currently logged in for $accountID")
            lifecycleScope.launch(Dispatchers.IO) {
                initializeFragments(savedInstanceState, accountID)
            }
        } catch (e: Exception) {
            Timber.d("Account or character information not found, start login process::${e.message}")
            val intent = Intent(this, LoginActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
            startActivity(intent)
        }

        binding.sortFieldToggle.addOnButtonCheckedListener { group, _, _ ->
            viewModel.onSortFieldChange(group.checkedButtonId)
        }

        binding.navigationRail.setOnItemSelectedListener { this.navigation(it) }
    }

    private suspend fun initializeFragments(
        savedInstanceState: Bundle?,
        accountID: String
    ) {
        if (savedInstanceState == null) {
            fragments[BANK_STORAGE_PREFIX] =
                StorageFragment.newInstance(BANK_STORAGE_PREFIX, accountID)
            fragments[MATERIAL_STORAGE_PREFIX] = StorageFragment.newInstance(
                MATERIAL_STORAGE_PREFIX, accountID
            )
            fragments[INVENTORY_STORAGE_PREFIX] =
                InventoryFragment.newInstance(
                    characterService.characters().toTypedArray(),
                    accountID
                )
        }
        displayFragment(BANK_STORAGE_PREFIX)
    }

    private fun navigation(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.nav_bank -> {
                displayFragment(BANK_STORAGE_PREFIX)
                true
            }
            R.id.nav_materials -> {
                displayFragment(MATERIAL_STORAGE_PREFIX)
                true
            }
            R.id.nav_inventory -> {
                displayFragment(INVENTORY_STORAGE_PREFIX)
                true
            }
            else -> false
        }
    }

    private fun displayFragment(tag: String) {
        val current = fragments[tag] ?: throw IllegalArgumentException()

        supportFragmentManager.commit {
            setReorderingAllowed(true)
            if (current.isAdded) {
                show(current)
            } else {
                add(R.id.fragment_container, current, tag)
            }
            fragments.forEach {
                if (it.key != tag && it.value.isAdded) {
                    hide(it.value)
                }
            }
        }
    }
}