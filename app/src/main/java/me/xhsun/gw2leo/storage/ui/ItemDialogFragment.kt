package me.xhsun.gw2leo.storage.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.bumptech.glide.Glide
import dagger.hilt.android.AndroidEntryPoint
import me.xhsun.gw2leo.R
import me.xhsun.gw2leo.core.config.COIN_COPPER
import me.xhsun.gw2leo.core.config.COIN_GOLD
import me.xhsun.gw2leo.core.config.COIN_SILVER
import me.xhsun.gw2leo.core.config.STORAGE_ITEM_KEY
import me.xhsun.gw2leo.databinding.FragmentItemDialogBinding
import me.xhsun.gw2leo.storage.Item
import me.xhsun.gw2leo.storage.StorageItem
import me.xhsun.gw2leo.storage.ui.model.ItemDialogViewModel

/**
 * A simple [Fragment] subclass.
 * Use the [ItemDialogFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
@AndroidEntryPoint
class ItemDialogFragment : DialogFragment() {
    private val viewModel by viewModels<ItemDialogViewModel>()

    private lateinit var storageItem: StorageItem

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            storageItem = it.getParcelable(STORAGE_ITEM_KEY) ?: throw IllegalArgumentException()
        }
        viewModel.item = storageItem
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding: FragmentItemDialogBinding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_item_dialog, container, false
        )
        binding.lifecycleOwner = viewLifecycleOwner
        binding.viewmodel = viewModel

        val color = Item.getColorCode(viewModel.item.detail.rarity)
        binding.itemRarity.setBackgroundColor(color)
        binding.itemTitle.setTextColor(color)

        Glide.with(this).load(viewModel.item.detail.icon)
            .placeholder(R.drawable.ic_image_placeholder)
            .into(binding.itemImg)
        Glide.with(this).load(COIN_GOLD)
            .placeholder(R.drawable.ic_image_placeholder)
            .into(binding.itemBuyGoldImg)
        Glide.with(this).load(COIN_GOLD)
            .placeholder(R.drawable.ic_image_placeholder)
            .into(binding.itemSellGoldImg)
        Glide.with(this).load(COIN_GOLD)
            .placeholder(R.drawable.ic_image_placeholder)
            .into(binding.itemTotalBuyGoldImg)
        Glide.with(this).load(COIN_GOLD)
            .placeholder(R.drawable.ic_image_placeholder)
            .into(binding.itemTotalSellGoldImg)
        Glide.with(this).load(COIN_SILVER)
            .placeholder(R.drawable.ic_image_placeholder)
            .into(binding.itemBuySilverImg)
        Glide.with(this).load(COIN_SILVER)
            .placeholder(R.drawable.ic_image_placeholder)
            .into(binding.itemSellSilverImg)
        Glide.with(this).load(COIN_SILVER)
            .placeholder(R.drawable.ic_image_placeholder)
            .into(binding.itemTotalBuySilverImg)
        Glide.with(this).load(COIN_SILVER)
            .placeholder(R.drawable.ic_image_placeholder)
            .into(binding.itemTotalSellSilverImg)
        Glide.with(this).load(COIN_COPPER)
            .placeholder(R.drawable.ic_image_placeholder)
            .into(binding.itemBuyCopperImg)
        Glide.with(this).load(COIN_COPPER)
            .placeholder(R.drawable.ic_image_placeholder)
            .into(binding.itemSellCopperImg)
        Glide.with(this).load(COIN_COPPER)
            .placeholder(R.drawable.ic_image_placeholder)
            .into(binding.itemTotalBuyCopperImg)
        Glide.with(this).load(COIN_COPPER)
            .placeholder(R.drawable.ic_image_placeholder)
            .into(binding.itemTotalSellCopperImg)

        return binding.root
    }


    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param storageItem Storage item to display
         * @return A new instance of fragment ItemDialogFragment.
         */
        @JvmStatic
        fun newInstance(storageItem: StorageItem) =
            ItemDialogFragment().apply {
                arguments = Bundle().apply {
                    putParcelable(STORAGE_ITEM_KEY, storageItem)
                }
            }
    }
}