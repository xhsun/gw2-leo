package me.xhsun.gw2leo.storage.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import me.xhsun.gw2leo.R
import me.xhsun.gw2leo.config.COIN_COPPER
import me.xhsun.gw2leo.config.COIN_GOLD
import me.xhsun.gw2leo.config.COIN_SILVER
import me.xhsun.gw2leo.config.ITEM_DIALOG_TAG
import me.xhsun.gw2leo.databinding.ItemStorageBinding
import me.xhsun.gw2leo.storage.Item
import me.xhsun.gw2leo.storage.StorageItem
import me.xhsun.gw2leo.storage.ui.ItemDialogFragment

class StorageViewHolder(view: View, private val fragmentManager: FragmentManager) :
    RecyclerView.ViewHolder(view) {
    private val binding = ItemStorageBinding.bind(itemView)

    private val icon: ImageView = binding.storageItemImg
    private val rarity: ConstraintLayout = binding.storageItemRarity
    private val amount: TextView = binding.storageItemAmount
    private val priceContainer: ConstraintLayout = binding.storageItemPriceContainer
    private val notSellable: TextView = binding.storageItemNotSellable
    private val goldImage: ImageView = binding.storageItemGoldImg
    private val silverImage: ImageView = binding.storageItemSilverImg
    private val copperImage: ImageView = binding.storageItemCopperImg
    private val gold: TextView = binding.storageItemGoldAmount
    private val silver: TextView = binding.storageItemSilverAmount
    private val copper: TextView = binding.storageItemCopperAmount
    private var item: StorageItem? = null

    init {
        view.setOnClickListener {
            val i = item
            if (i != null) {
                val dialog = ItemDialogFragment.newInstance(i)
                dialog.isCancelable = true
                dialog.show(fragmentManager, ITEM_DIALOG_TAG)
            }
        }
    }

    fun bind(item: StorageItem?) {
        this.item = item
        rarity.setBackgroundColor(Item.getColorCode(item?.detail?.rarity ?: ""))
        if (item?.detail?.icon?.startsWith("http") == true) {
            Glide.with(this.itemView).load(item.detail.icon)
                .placeholder(R.drawable.ic_image_placeholder)
                .into(icon)
        } else {
            icon.visibility = GONE
            Glide.with(this.itemView).clear(icon)
        }

        amount.text = this.item?.count.toString()

        val isSellable = this.item?.price!! > 0 || this.item?.detail?.sellable ?: false
        if (isSellable) {
            notSellable.visibility = GONE
            priceContainer.visibility = VISIBLE
            gold.text = this.item?.gold.toString()
            silver.text = this.item?.silver.toString()
            copper.text = this.item?.copper.toString()

            Glide.with(this.itemView).load(COIN_GOLD)
                .placeholder(R.drawable.ic_image_placeholder)
                .into(goldImage)
            Glide.with(this.itemView).load(COIN_SILVER)
                .placeholder(R.drawable.ic_image_placeholder)
                .into(silverImage)
            Glide.with(this.itemView).load(COIN_COPPER)
                .placeholder(R.drawable.ic_image_placeholder)
                .into(copperImage)
        } else {
            notSellable.visibility = VISIBLE
            priceContainer.visibility = GONE
        }
    }

    fun update(item: StorageItem?) {
        this.item = item ?: this.item
        amount.text = this.item?.count.toString()
        gold.text = this.item?.gold.toString()
        silver.text = this.item?.silver.toString()
        copper.text = this.item?.copper.toString()
    }

    companion object {
        fun create(parent: ViewGroup, fragmentManager: FragmentManager): StorageViewHolder {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_storage, parent, false)
            return StorageViewHolder(view, fragmentManager)
        }
    }
}