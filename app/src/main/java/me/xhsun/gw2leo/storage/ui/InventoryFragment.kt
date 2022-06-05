package me.xhsun.gw2leo.storage.ui

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.google.android.material.tabs.TabLayoutMediator
import me.xhsun.gw2leo.R
import me.xhsun.gw2leo.account.error.NotLoggedInError
import me.xhsun.gw2leo.account.ui.LoginActivity
import me.xhsun.gw2leo.config.ACCOUNT_ID_KEY
import me.xhsun.gw2leo.config.CHARACTER_LIST_KEY
import me.xhsun.gw2leo.databinding.FragmentInventoryBinding
import me.xhsun.gw2leo.storage.ui.adapter.InventoryCollectionAdapter
import timber.log.Timber


class InventoryFragment : Fragment() {
    private lateinit var characters: List<String>
    private lateinit var accountID: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        try {
            arguments?.let {
                characters = it.getStringArrayList(CHARACTER_LIST_KEY) ?: throw NotLoggedInError()
                accountID = it.getString(ACCOUNT_ID_KEY) ?: throw NotLoggedInError()
            }
        } catch (e: NotLoggedInError) {
            Timber.d("Not logged in, start login process")
            val intent = Intent(activity, LoginActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
            startActivity(intent)
        }
        Timber.d("Displaying inventory holder for $accountID::$characters")
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding: FragmentInventoryBinding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_inventory, container, false
        )

        val adapter = InventoryCollectionAdapter(this, characters, accountID)
        binding.inventoryViewPager.adapter = adapter

        TabLayoutMediator(binding.inventoryTabs, binding.inventoryViewPager) { tab, position ->
            tab.text = characters[position]
        }.attach()
        return binding.root
    }
}