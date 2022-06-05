package me.xhsun.gw2leo.storage.ui

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.navArgs
import com.google.android.material.tabs.TabLayoutMediator
import me.xhsun.gw2leo.R
import me.xhsun.gw2leo.account.ui.LoginActivity
import me.xhsun.gw2leo.databinding.FragmentInventoryBinding
import me.xhsun.gw2leo.storage.ui.adapter.InventoryCollectionAdapter
import timber.log.Timber

class InventoryFragment : Fragment() {
    private val args: InventoryFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val accountID = args.accountID
        val characters = args.characters.toList()
        if (accountID.isEmpty() || characters.isEmpty()) {
            Timber.d("Not logged in, start login process")
            val intent = Intent(activity, LoginActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
            startActivity(intent)
        }

        val binding: FragmentInventoryBinding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_inventory, container, false
        )

        val adapter = InventoryCollectionAdapter(this, characters, accountID)
        binding.inventoryViewPager.adapter = adapter
        binding.inventoryViewPager.offscreenPageLimit = 1

        TabLayoutMediator(binding.inventoryTabs, binding.inventoryViewPager) { tab, position ->
            tab.text = characters[position]
        }.attach()
        return binding.root
    }
}