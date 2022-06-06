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
                characters =
                    it.getStringArray(CHARACTER_LIST_KEY)?.toList() ?: throw NotLoggedInError()
                accountID = it.getString(ACCOUNT_ID_KEY) ?: throw NotLoggedInError()
            }
            Timber.d("Displaying inventory holder for $accountID::$characters")
        } catch (e: NotLoggedInError) {
            Timber.d("Not logged in, start login process")
            val intent = Intent(activity, LoginActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
            startActivity(intent)
        }

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
        binding.inventoryViewPager.offscreenPageLimit = 1

        TabLayoutMediator(binding.inventoryTabs, binding.inventoryViewPager) { tab, position ->
            tab.text = characters[position]
        }.attach()
        return binding.root
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param characters List of characters for the currently logged in account
         * @param accountID Account ID of currently logged in account
         * @return A new instance of fragment InventoryFragment.
         */
        @JvmStatic
        fun newInstance(characters: Array<String>, accountID: String) =
            InventoryFragment().apply {
                arguments = Bundle().apply {
                    putStringArray(CHARACTER_LIST_KEY, characters)
                    putString(ACCOUNT_ID_KEY, accountID)
                }
            }
    }
}