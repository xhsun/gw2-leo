package me.xhsun.gw2leo.storage.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import me.xhsun.gw2leo.R
import me.xhsun.gw2leo.storage.StorageType

private const val STORAGE_TYPE_KEY = "STORAGE_TYPE"

/**
 * A simple [Fragment] subclass.
 * Use the [StorageFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class StorageFragment : Fragment() {
    private var storageType: StorageType = StorageType.Bank

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            storageType = enumValueOf(it.getString(STORAGE_TYPE_KEY) ?: StorageType.Bank.toString())
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_storage, container, false)
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param storageType Type of storage to display
         * @return A new instance of fragment StorageFragment.
         */
        @JvmStatic
        fun newInstance(storageType: StorageType) =
            StorageFragment().apply {
                arguments = Bundle().apply {
                    putString(STORAGE_TYPE_KEY, storageType.toString())
                }
            }
    }
}