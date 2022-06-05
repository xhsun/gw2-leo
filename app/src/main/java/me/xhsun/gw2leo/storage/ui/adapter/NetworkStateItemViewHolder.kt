package me.xhsun.gw2leo.storage.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.paging.LoadState
import androidx.recyclerview.widget.RecyclerView
import me.xhsun.gw2leo.R
import me.xhsun.gw2leo.databinding.ItemNetworkstateBinding

class NetworkStateItemViewHolder(
    parent: ViewGroup,
    private val retryCallback: () -> Unit
) : RecyclerView.ViewHolder(
    LayoutInflater.from(parent.context).inflate(R.layout.item_networkstate, parent, false)
) {
    private val binding = ItemNetworkstateBinding.bind(itemView)
    private val progressBar = binding.storageLoadProgressBar
    private val errorMsg = binding.storageLoadError
    private val retry = binding.storageLoadRetryButton
        .also {
            it.setOnClickListener { retryCallback() }
        }

    fun bindTo(loadState: LoadState) {
        progressBar.isVisible = loadState is LoadState.Loading
        retry.isVisible = loadState is LoadState.Error
        errorMsg.isVisible = (loadState as LoadState.Error).error.message?.isNotEmpty() ?: false
        errorMsg.text = loadState.error.message
    }
}