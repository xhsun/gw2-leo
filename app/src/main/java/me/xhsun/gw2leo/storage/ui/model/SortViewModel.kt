package me.xhsun.gw2leo.storage.ui.model

import android.view.View
import android.widget.CheckBox
import androidx.annotation.IdRes
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import me.xhsun.gw2leo.R
import javax.inject.Inject

@HiltViewModel
class SortViewModel @Inject constructor() : ViewModel() {
    private val mutableSort = MutableLiveData<SortState>()
    val sortState: LiveData<SortState> get() = mutableSort
    private var isAsc = false
    private var isBuy = true

    fun onSortDirectionChange(view: View) {
        if (view is CheckBox && view.isChecked != isAsc) {
            isAsc = view.isChecked
            mutableSort.postValue(SortState(isAsc, isBuy))
        }
    }

    fun onSortFieldChange(@IdRes checkedId: Int) {
        val isBuy = checkedId == R.id.sort_field_buy
        if (this.isBuy != isBuy) {
            this.isBuy = isBuy
            mutableSort.postValue(SortState(isAsc, this.isBuy))
        }
    }
}