package me.xhsun.gw2leo.storage.ui.adapter

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import me.xhsun.gw2leo.core.config.ACCOUNT_ID_KEY
import me.xhsun.gw2leo.core.config.STORAGE_TYPE_KEY
import me.xhsun.gw2leo.storage.ui.StorageFragment

class InventoryCollectionAdapter(
    fragment: Fragment,
    private val characters: List<String>,
    private val accountID: String,
) :
    FragmentStateAdapter(fragment) {
    override fun getItemCount(): Int {
        return characters.size
    }

    override fun createFragment(position: Int): Fragment {
        val fragment = StorageFragment()
        fragment.arguments = Bundle().apply {
            putString(STORAGE_TYPE_KEY, characters[position])
            putString(ACCOUNT_ID_KEY, accountID)
        }
        return fragment
    }

}