package me.xhsun.gw2leo.storage

import io.github.serpro69.kfaker.Faker
import me.xhsun.gw2leo.DataFaker
import me.xhsun.gw2leo.storage.datastore.entity.MaterialStorageBase
import me.xhsun.gw2leo.storage.datastore.entity.StorageBase
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

internal class StorageItemTest {
    private val faker = Faker()
    private val dataFaker = DataFaker()

    @Test
    fun toMaterialDAO() {
        val target = dataFaker.storageItemFaker()
        val expected = MaterialStorageBase(
            id = target.id,
            itemID = target.detail.id,
            accountID = target.accountID,
            categoryID = target.category!!.id,
            binding = target.binding,
            count = target.count
        )
        val actual = target.toMaterialDAO()
        assertThat(actual).isEqualTo(expected)
    }

    @Test
    fun toStorageDAO() {
        val target = dataFaker.storageItemFaker()
        val expected = StorageBase(
            id = target.id,
            itemID = target.detail.id,
            storageType = target.storageType,
            charges = target.charges,
            binding = target.binding,
            bindTo = target.bindTo,
            count = target.count
        )
        val actual = target.toStorageDAO()
        assertThat(actual).isEqualTo(expected)
    }

    @Test
    fun update() {
        val target = dataFaker.storageItemFaker()
        val inputItem = dataFaker.itemFaker()
        val inputCategory = dataFaker.materialCategoryFaker()
        val expected = StorageItem(
            id = target.id,
            accountID = target.accountID,
            detail = inputItem,
            category = inputCategory,
            storageType = target.storageType,
            charges = target.charges,
            binding = target.binding,
            bindTo = target.bindTo,
            count = target.count,
            price = target.price,
            gold = target.gold,
            silver = target.silver,
            copper = target.copper
        )
        val actual = target.update(inputItem, inputCategory)
        assertThat(actual).isEqualTo(expected)
    }

    @Test
    fun `shouldShowIcon() should return true when icon is URL`() {
        val target = dataFaker.storageItemFaker(icon = "http://${faker.internet.domain()}")
        val actual = StorageItem.shouldShowIcon(target)
        assertThat(actual).isTrue
    }

    @Test
    fun `shouldShowIcon() should return true when icon is not URL`() {
        val target = dataFaker.storageItemFaker(icon = faker.random.randomString())
        val actual = StorageItem.shouldShowIcon(target)
        assertThat(actual).isFalse
    }

    @Test
    fun `shouldShowIcon() should return false when null`() {
        val actual = StorageItem.shouldShowIcon(null)
        assertThat(actual).isFalse
    }

    @Test
    fun `isSellable() should return true when price exist`() {
        val target = dataFaker.storageItemFaker(sellable = false)
        val actual = StorageItem.isSellable(target)
        assertThat(actual).isTrue
    }

    @Test
    fun `isSellable() should return true when sellable is true`() {
        val target = dataFaker.storageItemFaker(price = 0)
        val actual = StorageItem.isSellable(target)
        assertThat(actual).isTrue
    }

    @Test
    fun `isSellable() should return true when not sellable and no price`() {
        val target = dataFaker.storageItemFaker(price = 0, sellable = false)
        val actual = StorageItem.isSellable(target)
        assertThat(actual).isFalse
    }

    @Test
    fun `isSellable() should return false when null`() {
        val actual = StorageItem.isSellable(null)
        assertThat(actual).isFalse
    }

    @Test
    fun `emptyStorageItem() should return empty`() {
        val expected = StorageItem(
            id = 0,
            accountID = "",
            detail = Item(
                id = 0,
                chatLink = "",
                name = "",
                icon = "",
                description = "",
                rarity = "",
                level = 0,
                sellable = false,
                buy = 0,
                buyGold = 0,
                buySilver = 0,
                buyCopper = 0,
                sell = 0,
                sellGold = 0,
                sellSilver = 0,
                sellCopper = 0,
                type = ""
            ),
            category = null,
            storageType = "",
            charges = 0,
            binding = "",
            bindTo = "",
            count = 0,
            price = 0,
            gold = 0,
            silver = 0,
            copper = 0
        )
        val actual = StorageItem.emptyStorageItem()
        assertThat(actual).isEqualTo(expected)
    }
}