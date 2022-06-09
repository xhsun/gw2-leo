package me.xhsun.gw2leo.storage.ui.adapter

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import me.xhsun.gw2leo.storage.ui.StorageFragment

class InventoryCollectionAdapter(
    fragment: Fragment,
    private val characters: List<String>,
    private val accountID: String,
) : FragmentStateAdapter(fragment) {
    
    override fun getItemCount(): Int {
        return characters.size
    }

    override fun createFragment(position: Int): Fragment {
        return StorageFragment.newInstance(characters[position], accountID)
    }
}