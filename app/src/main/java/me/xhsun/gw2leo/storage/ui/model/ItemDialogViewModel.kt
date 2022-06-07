package me.xhsun.gw2leo.storage.ui.model

import android.text.Html
import android.text.Spanned
import androidx.databinding.Bindable
import dagger.hilt.android.lifecycle.HiltViewModel
import me.xhsun.gw2leo.BR
import me.xhsun.gw2leo.core.model.ObservableViewModel
import me.xhsun.gw2leo.storage.Item
import me.xhsun.gw2leo.storage.StorageItem
import javax.inject.Inject

@HiltViewModel
class ItemDialogViewModel @Inject constructor() : ObservableViewModel() {

    @get:Bindable
    var item: StorageItem = StorageItem.emptyStorageItem()
        set(value) {
            if (value != field) {
                field = value
                notifyPropertyChanged(BR.item)
                levelVisible = value.detail.level > 1

                itemDescription = if (value.detail.description.isNotEmpty()) Html.fromHtml(
                    value.detail.description,
                    Html.FROM_HTML_MODE_COMPACT
                ) else null


                if (value.binding.isNotEmpty()) {
                    bindingVisible = true
                    binding =
                        if (value.bindTo != null && value.bindTo.isNotEmpty()) value.bindTo else value.binding
                } else {
                    bindingVisible = false
                    binding = ""
                }

                if (value.detail.sellable) {
                    totalBuy = Item.parseCoins(value.count * value.detail.buy)
                    totalSell = Item.parseCoins(value.count * value.detail.sell)
                }
            }
        }

    @get:Bindable
    var itemDescription: Spanned? = null
        set(value) {
            if (value != field) {
                field = value
                notifyPropertyChanged(BR.itemDescription)
            }
        }

    @get:Bindable
    var totalSell: Triple<Int, Int, Int> = Triple(0, 0, 0)
        set(value) {
            if (value != field) {
                field = value
                notifyPropertyChanged(BR.totalSell)
            }
        }

    @get:Bindable
    var totalBuy: Triple<Int, Int, Int> = Triple(0, 0, 0)
        set(value) {
            if (value != field) {
                field = value
                notifyPropertyChanged(BR.totalBuy)
            }
        }

    @get:Bindable
    var levelVisible: Boolean = false
        set(value) {
            if (value != field) {
                field = value
                notifyPropertyChanged(BR.levelVisible)
            }
        }

    @get:Bindable
    var binding: String = ""
        set(value) {
            if (value != field) {
                field = value
                notifyPropertyChanged(BR.binding)
            }
        }

    @get:Bindable
    var bindingVisible: Boolean = false
        set(value) {
            if (value != field) {
                field = value
                notifyPropertyChanged(BR.bindingVisible)
            }
        }
}