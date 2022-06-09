package me.xhsun.gw2leo.storage.http.model

import io.github.serpro69.kfaker.Faker
import me.xhsun.gw2leo.DataFaker
import me.xhsun.gw2leo.storage.Item
import me.xhsun.gw2leo.storage.StorageItem
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

internal class InventoryDTOTest {
    private val faker = Faker()
    private val dataFaker = DataFaker()

    @Test
    fun toDomain() {
        val input = faker.random.randomString()
        val target = dataFaker.inventoryDTOFaker()
        val expected = StorageItem(
            id = 0,
            detail = Item(
                id = target.id,
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
            storageType = input,
            binding = target.binding!!,
            bindTo = target.boundTo,
            charges = target.charges,
            count = target.count,
            accountID = "",
            category = null,
            price = 0,
            gold = 0,
            silver = 0,
            copper = 0
        )
        val actual = target.toDomain(input)
        assertThat(actual).isEqualTo(expected)
    }
}