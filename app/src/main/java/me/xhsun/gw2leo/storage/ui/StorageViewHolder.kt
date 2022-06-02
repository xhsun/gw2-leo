package me.xhsun.gw2leo.storage.ui

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import me.xhsun.gw2leo.R
import me.xhsun.gw2leo.config.COIN_COPPER
import me.xhsun.gw2leo.config.COIN_GOLD
import me.xhsun.gw2leo.config.COIN_SILVER
import me.xhsun.gw2leo.storage.StorageItem

class StorageViewHolder(view: View) :
    RecyclerView.ViewHolder(view) {
    private val icon: ImageView = view.findViewById(R.id.storage_item_img)
    private val goldImage: ImageView = view.findViewById(R.id.storage_item_gold_img)
    private val silverImage: ImageView = view.findViewById(R.id.storage_item_silver_img)
    private val copperImage: ImageView = view.findViewById(R.id.storage_item_copper_img)
    private val gold: TextView = view.findViewById(R.id.storage_item_gold_amount)
    private val silver: TextView = view.findViewById(R.id.storage_item_silver_amount)
    private val copper: TextView = view.findViewById(R.id.storage_item_copper_amount)
    private var item: StorageItem? = null

    init {
        view.setOnClickListener {
            TODO("Open popup to show item details")
        }
    }

    fun bind(item: StorageItem?, isBuy: Boolean) {
        this.item = item
        if (isBuy) {
            gold.text = this.item?.detail?.buyGold.toString()
            silver.text = this.item?.detail?.buySilver.toString()
            copper.text = this.item?.detail?.buyCopper.toString()
        } else {
            gold.text = this.item?.detail?.sellGold.toString()
            silver.text = this.item?.detail?.sellSilver.toString()
            copper.text = this.item?.detail?.sellCopper.toString()
        }

        Glide.with(this.itemView).load(COIN_GOLD)
            .placeholder(R.drawable.ic_image_placeholder)
            .into(goldImage)
        Glide.with(this.itemView).load(COIN_SILVER)
            .placeholder(R.drawable.ic_image_placeholder)
            .into(silverImage)
        Glide.with(this.itemView).load(COIN_COPPER)
            .placeholder(R.drawable.ic_image_placeholder)
            .into(copperImage)

        if (item?.detail?.icon?.startsWith("http") == true) {
            Glide.with(this.itemView).load(item.detail.icon)
                .placeholder(R.drawable.ic_image_placeholder)
                .into(icon)
        } else {
            icon.visibility = View.GONE
            Glide.with(this.itemView).clear(icon)
        }
    }

    companion object {
        fun create(parent: ViewGroup): StorageViewHolder {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_storage, parent, false)
            return StorageViewHolder(view)
        }
    }

    fun update(item: StorageItem?, isBuy: Boolean) {
        this.item = item ?: this.item
        if (isBuy) {
            gold.text = this.item?.detail?.buyGold.toString()
            silver.text = this.item?.detail?.buySilver.toString()
            copper.text = this.item?.detail?.buyCopper.toString()
        } else {
            gold.text = this.item?.detail?.sellGold.toString()
            silver.text = this.item?.detail?.sellSilver.toString()
            copper.text = this.item?.detail?.sellCopper.toString()
        }
    }
}