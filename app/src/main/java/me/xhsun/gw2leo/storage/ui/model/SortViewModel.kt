package me.xhsun.gw2leo.storage.ui.model

import android.view.View
import android.widget.CheckBox
import androidx.databinding.Bindable
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import dagger.hilt.android.lifecycle.HiltViewModel
import me.xhsun.gw2leo.BR
import me.xhsun.gw2leo.R
import me.xhsun.gw2leo.core.model.ObservableViewModel
import javax.inject.Inject

@HiltViewModel
class SortViewModel @Inject constructor() : ObservableViewModel() {
    private val mutableSort = MutableLiveData<SortState>()
    val sortState: LiveData<SortState> get() = mutableSort
    private var isAsc = false
    private var isBuy = true

    @Bindable
    var sellable: Boolean = false
        set(value) {
            if (value != field) {
                field = value
                notifyPropertyChanged(BR.sellable)
                mutableSort.postValue(SortState(isBuy, isAsc, value))
            }
        }

    fun onSortDirectionChange(view: View) {
        if (view is CheckBox && view.isChecked != isAsc) {
            isAsc = view.isChecked
            mutableSort.postValue(SortState(isBuy, isAsc, sellable))
        }
    }

    fun onSortFieldChange(checkedId: Int) {
        val isBuy = checkedId == R.id.sort_field_buy
        if (this.isBuy != isBuy) {
            this.isBuy = isBuy
            mutableSort.postValue(SortState(this.isBuy, isAsc, sellable))
        }
    }
}