package me.xhsun.gw2leo.storage.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.navArgs
import androidx.paging.LoadState
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.distinctUntilChangedBy
import kotlinx.coroutines.flow.filter
import me.xhsun.gw2leo.R
import me.xhsun.gw2leo.config.ORDER_BY_BUY
import me.xhsun.gw2leo.config.STORAGE_LIST_COLUMN_WIDTH
import me.xhsun.gw2leo.databinding.FragmentStorageBinding
import me.xhsun.gw2leo.storage.ui.adapter.PostsLoadStateAdapter
import me.xhsun.gw2leo.storage.ui.adapter.StorageAdapter
import me.xhsun.gw2leo.storage.ui.model.StorageDisplay
import me.xhsun.gw2leo.storage.ui.model.StorageViewModel
import timber.log.Timber

@AndroidEntryPoint
class StorageFragment : Fragment() {
    private val args: StorageFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding: FragmentStorageBinding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_storage, container, false
        )
        binding.lifecycleOwner = viewLifecycleOwner
        val accountID = args.accountID
        if (accountID.isEmpty()) {
            Timber.d("Not logged in")
        } else {
            val storageType = args.storageType
            val viewModel = ViewModelProvider(this)[StorageViewModel::class.java]
            val adapter = StorageAdapter()
            this.addLayoutManager(binding.storageList)

            binding.storageList.adapter = adapter
            binding.swipeRefresh.setOnRefreshListener {
                adapter.refresh()
                binding.swipeRefresh.isRefreshing = false
            }

            viewModel.updateItem(
                StorageDisplay(
                    storageType,
                    ORDER_BY_BUY,
                    true
                )
            )

            binding.storageList.adapter = adapter.withLoadStateHeaderAndFooter(
                header = PostsLoadStateAdapter(adapter),
                footer = PostsLoadStateAdapter(adapter)
            )

            lifecycleScope.launchWhenCreated {
                viewModel.items.collectLatest {
                    adapter.submitData(it)
                }
            }

            lifecycleScope.launchWhenCreated {
                adapter.loadStateFlow
                    .asMergedLoadStates()
                    .distinctUntilChangedBy { it.refresh }
                    .filter { it.refresh is LoadState.NotLoading }
                    .collect {
                        binding.storageList.scrollToPosition(0)
                    }
            }
        }

        return binding.root
    }

    private fun addLayoutManager(recyclerView: RecyclerView) {
        val displayMetrics = recyclerView.context.resources.displayMetrics
        val noOfColumns =
            ((displayMetrics.widthPixels / displayMetrics.density) / STORAGE_LIST_COLUMN_WIDTH).toInt()
        recyclerView.layoutManager = GridLayoutManager(this.context, noOfColumns)
    }
}