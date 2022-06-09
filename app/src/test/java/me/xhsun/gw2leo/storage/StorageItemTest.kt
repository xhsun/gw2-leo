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
}