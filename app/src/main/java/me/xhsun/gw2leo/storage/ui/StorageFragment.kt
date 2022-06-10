package me.xhsun.gw2leo.storage.ui

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.distinctUntilChangedBy
import me.xhsun.gw2leo.R
import me.xhsun.gw2leo.account.error.NotLoggedInError
import me.xhsun.gw2leo.account.ui.LoginActivity
import me.xhsun.gw2leo.core.config.*
import me.xhsun.gw2leo.databinding.FragmentStorageBinding
import me.xhsun.gw2leo.storage.ui.adapter.StorageAdapter
import me.xhsun.gw2leo.storage.ui.model.SortState
import me.xhsun.gw2leo.storage.ui.model.SortViewModel
import me.xhsun.gw2leo.storage.ui.model.StorageViewModel
import timber.log.Timber

@AndroidEntryPoint
class StorageFragment : Fragment() {
    private lateinit var storageType: String
    private lateinit var accountID: String
    private val viewModel by viewModels<StorageViewModel>()
    private val sortViewModel by activityViewModels<SortViewModel>()

    private lateinit var storageAdapter: StorageAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        try {
            arguments?.let {
                storageType = it.getString(STORAGE_TYPE_KEY) ?: throw IllegalArgumentException()
                accountID = it.getString(ACCOUNT_ID_KEY) ?: throw NotLoggedInError()
            }
            storageType =
                if (storageType.contains(BANK_STORAGE_PREFIX)) BANK_STORAGE_KEY_FORMAT.format(
                    accountID
                ) else storageType
        } catch (e: NotLoggedInError) {
            Timber.d("Not logged in, start login process")
            val intent = Intent(activity, LoginActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
            startActivity(intent)
        }
        Timber.d("Displaying storage list for $accountID::$storageType")
        storageAdapter = StorageAdapter(childFragmentManager)
        this.registerSortStateObserver()
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

        this.setupRecyclerView(binding)
        return binding.root
    }

    private fun setupRecyclerView(binding: FragmentStorageBinding) {
        binding.storageList.apply {
            adapter = storageAdapter
            layoutManager = layoutManager(this)
        }

        binding.swipeRefresh.setOnRefreshListener {
            viewModel.hardRefresh(requireContext(), storageType)
                .observe(viewLifecycleOwner) { workInfo ->
                    if (workInfo != null) {
                        Timber.d("Hard refresh completed::${workInfo.state}")
                        binding.swipeRefresh.isRefreshing = false
                    }
                }
        }

        lifecycleScope.launchWhenCreated {
            viewModel.items.collectLatest {
                storageAdapter.submitData(it)
                viewModel.checkEmpty()
            }
        }

        lifecycleScope.launchWhenCreated {
            storageAdapter.loadStateFlow
                .distinctUntilChangedBy { it.refresh }
                .collect {
                    viewModel.changeState(binding.storageList, it)
                }
        }
    }

    private fun registerSortStateObserver() {
        sortViewModel.sortState.observe(this) {
            this.update(it)
        }
        this.update(SortState())
    }

    private fun update(state: SortState?) {
        if (state != null) {
            Timber.d("Update storage sorting::${state}")
            viewModel.update(
                state.toStorageDisplay(storageType),
                storageAdapter
            )
        }
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