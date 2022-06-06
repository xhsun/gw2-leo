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
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.distinctUntilChangedBy
import me.xhsun.gw2leo.R
import me.xhsun.gw2leo.account.error.NotLoggedInError
import me.xhsun.gw2leo.account.ui.LoginActivity
import me.xhsun.gw2leo.config.*
import me.xhsun.gw2leo.databinding.FragmentStorageBinding
import me.xhsun.gw2leo.storage.ui.adapter.StorageAdapter
import me.xhsun.gw2leo.storage.ui.model.StorageDisplay
import me.xhsun.gw2leo.storage.ui.model.StorageViewModel
import timber.log.Timber

@AndroidEntryPoint
class StorageFragment : Fragment() {
    private lateinit var storageType: String
    private lateinit var accountID: String
    private lateinit var viewModel: StorageViewModel
    private lateinit var storageAdapter: StorageAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        try {
            arguments?.let {
                storageType = it.getString(STORAGE_TYPE_KEY) ?: throw IllegalArgumentException()
                accountID = it.getString(ACCOUNT_ID_KEY) ?: throw NotLoggedInError()
            }
            storageType =
                if (storageType.contains(BANK_STORAGE_PREFIX)) DB_BANK_KEY_FORMAT.format(accountID) else storageType
        } catch (e: NotLoggedInError) {
            Timber.d("Not logged in, start login process")
            val intent = Intent(activity, LoginActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
            startActivity(intent)
        }
        Timber.d("Displaying storage list for $accountID::$storageType")

        storageAdapter = StorageAdapter()
        viewModel = ViewModelProvider(this)[StorageViewModel::class.java]
        viewModel.updateItem(
            StorageDisplay(
                storageType,
                ORDER_BY_BUY,
                true
            )
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding: FragmentStorageBinding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_storage, container, false
        )
        binding.lifecycleOwner = viewLifecycleOwner
        binding.viewmodel = viewModel

        binding.storageList.apply {
            adapter = storageAdapter
            layoutManager = layoutManager(this)
        }

        binding.swipeRefresh.setOnRefreshListener {
            storageAdapter.refresh()
            binding.swipeRefresh.isRefreshing = false
        }

        lifecycleScope.launchWhenCreated {
            viewModel.items.collectLatest {
                storageAdapter.submitData(it)
            }
        }

        lifecycleScope.launchWhenCreated {
            storageAdapter.loadStateFlow
                .distinctUntilChangedBy { it.refresh }
                .collect {
                    viewModel.changeState(binding.storageList, it)
                }
        }
        return binding.root
    }

    private fun layoutManager(recyclerView: RecyclerView): GridLayoutManager {
        val displayMetrics = recyclerView.context.resources.displayMetrics
        val noOfColumns =
            ((displayMetrics.widthPixels / displayMetrics.density) / STORAGE_LIST_COLUMN_WIDTH).toInt()
        return GridLayoutManager(this.context, noOfColumns)
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param storageType Storage type of this fragment
         * @param accountID Account ID of currently logged in account
         * @return A new instance of fragment StorageFragment.
         */
        @JvmStatic
        fun newInstance(storageType: String, accountID: String) =
            StorageFragment().apply {
                arguments = Bundle().apply {
                    putString(STORAGE_TYPE_KEY, storageType)
                    putString(ACCOUNT_ID_KEY, accountID)
                }
            }
    }
}