package me.xhsun.gw2leo.storage.ui.model

import io.github.serpro69.kfaker.Faker
import me.xhsun.gw2leo.DataFaker
import me.xhsun.gw2leo.storage.Item
import org.assertj.core.api.Assertions
import org.junit.Before
import org.junit.Test

class ItemDialogViewModelTest {
    private val faker = Faker()
    private val dataFaker = DataFaker()

    private lateinit var target: ItemDialogViewModel

    @Before
    fun setUp() {
        target = ItemDialogViewModel()
    }

    @Test
    fun setItemSetAll() {
        val description = "${faker.random.randomString()}<br>${faker.random.randomString()}"
        val input = dataFaker.storageItemFaker(description = description)
        target.item = input
        Assertions.assertThat(target.item).isEqualTo(input)
        Assertions.assertThat(target.itemDescription.toString())
            .isEqualTo(description.replace("<br>", "\n"))
        Assertions.assertThat(target.bindingVisible).isTrue
        Assertions.assertThat(target.binding).isEqualTo(input.bindTo)
        Assertions.assertThat(target.totalBuy)
            .isEqualTo(Item.parseCoins(input.count * input.detail.buy))
        Assertions.assertThat(target.totalSell)
            .isEqualTo(Item.parseCoins(input.count * input.detail.sell))
    }

    @Test
    fun setItemNoDescription() {
        val input = dataFaker.storageItemFaker(description = "")
        target.item = input
        Assertions.assertThat(target.item).isEqualTo(input)
        Assertions.assertThat(target.itemDescription).isNullOrEmpty()
        Assertions.assertThat(target.bindingVisible).isTrue
        Assertions.assertThat(target.binding).isEqualTo(input.bindTo)
        Assertions.assertThat(target.totalBuy)
            .isEqualTo(Item.parseCoins(input.count * input.detail.buy))
        Assertions.assertThat(target.totalSell)
            .isEqualTo(Item.parseCoins(input.count * input.detail.sell))
    }

    @Test
    fun setItemNoBindTo() {
        val description = "${faker.random.randomString()}<br>${faker.random.randomString()}"
        val input = dataFaker.storageItemFaker(description = description, bindTo = "")
        target.item = input
        Assertions.assertThat(target.item).isEqualTo(input)
        Assertions.assertThat(target.itemDescription.toString())
            .isEqualTo(description.replace("<br>", "\n"))
        Assertions.assertThat(target.bindingVisible).isTrue
        Assertions.assertThat(target.binding).isEqualTo(input.binding)
        Assertions.assertThat(target.totalBuy)
            .isEqualTo(Item.parseCoins(input.count * input.detail.buy))
        Assertions.assertThat(target.totalSell)
            .isEqualTo(Item.parseCoins(input.count * input.detail.sell))
    }

    @Test
    fun setItemNoBinding() {
        val description = "${faker.random.randomString()}<br>${faker.random.randomString()}"
        val input = dataFaker.storageItemFaker(description = description, binding = "")
        target.item = input
        Assertions.assertThat(target.item).isEqualTo(input)
        Assertions.assertThat(target.itemDescription.toString())
            .isEqualTo(description.replace("<br>", "\n"))
        Assertions.assertThat(target.bindingVisible).isFalse
        Assertions.assertThat(target.binding).isNullOrEmpty()
        Assertions.assertThat(target.totalBuy)
            .isEqualTo(Item.parseCoins(input.count * input.detail.buy))
        Assertions.assertThat(target.totalSell)
            .isEqualTo(Item.parseCoins(input.count * input.detail.sell))
    }

    @Test
    fun setItemNotSellable() {
        val description = "${faker.random.randomString()}<br>${faker.random.randomString()}"
        val input = dataFaker.storageItemFaker(description = description, sellable = false)
        target.item = input
        Assertions.assertThat(target.item).isEqualTo(input)
        Assertions.assertThat(target.itemDescription.toString())
            .isEqualTo(description.replace("<br>", "\n"))
        Assertions.assertThat(target.bindingVisible).isTrue
        Assertions.assertThat(target.binding).isEqualTo(input.bindTo)
        Assertions.assertThat(target.totalBuy)
            .isEqualTo(Triple(0, 0, 0))
        Assertions.assertThat(target.totalSell).isEqualTo(Triple(0, 0, 0))
    }
}