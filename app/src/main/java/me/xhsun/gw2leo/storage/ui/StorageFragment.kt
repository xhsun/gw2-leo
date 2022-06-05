package me.xhsun.gw2leo.storage.ui

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.paging.LoadState
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.distinctUntilChangedBy
import kotlinx.coroutines.flow.filter
import me.xhsun.gw2leo.R
import me.xhsun.gw2leo.account.error.NotLoggedInError
import me.xhsun.gw2leo.account.ui.LoginActivity
import me.xhsun.gw2leo.config.ACCOUNT_ID_KEY
import me.xhsun.gw2leo.config.ORDER_BY_BUY
import me.xhsun.gw2leo.config.STORAGE_LIST_COLUMN_WIDTH
import me.xhsun.gw2leo.config.STORAGE_TYPE_KEY
import me.xhsun.gw2leo.databinding.FragmentStorageBinding
import me.xhsun.gw2leo.storage.ui.adapter.PostsLoadStateAdapter
import me.xhsun.gw2leo.storage.ui.adapter.StorageAdapter
import me.xhsun.gw2leo.storage.ui.model.StorageDisplay
import me.xhsun.gw2leo.storage.ui.model.StorageViewModel
import timber.log.Timber

@AndroidEntryPoint
class StorageFragment : Fragment() {
    private lateinit var storageType: String
    private lateinit var accountID: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        try {
            arguments?.let {
                storageType = it.getString(STORAGE_TYPE_KEY) ?: throw IllegalArgumentException()
                accountID = it.getString(ACCOUNT_ID_KEY) ?: throw NotLoggedInError()
            }
        } catch (e: NotLoggedInError) {
            Timber.d("Not logged in, start login process")
            val intent = Intent(activity, LoginActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
            startActivity(intent)
        }
        Timber.d("Displaying storage list for $accountID::$storageType")
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding: FragmentStorageBinding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_storage, container, false
        )
        val viewModel = ViewModelProvider(this)[StorageViewModel::class.java]
        binding.lifecycleOwner = viewLifecycleOwner

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

        return binding.root
    }

    private fun addLayoutManager(recyclerView: RecyclerView) {
        val displayMetrics = recyclerView.context.resources.displayMetrics
        val noOfColumns =
            ((displayMetrics.widthPixels / displayMetrics.density) / STORAGE_LIST_COLUMN_WIDTH).toInt()
        recyclerView.layoutManager = GridLayoutManager(this.context, noOfColumns)
    }
}