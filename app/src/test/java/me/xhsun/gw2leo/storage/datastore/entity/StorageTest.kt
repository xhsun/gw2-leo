package me.xhsun.gw2leo.storage.datastore.entity

import io.github.serpro69.kfaker.Faker
import me.xhsun.gw2leo.DataFaker
import me.xhsun.gw2leo.storage.Item
import me.xhsun.gw2leo.storage.StorageItem
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

internal class StorageTest {
    private val faker = Faker()
    private val dataFaker = DataFaker()

    @Test
    fun `toDomain() without binding`() {
        val input = faker.random.nextBoolean()
        val target = dataFaker.storageDAOFaker(binding = "")
        val expected = StorageItem(
            id = target.id,
            detail = Item(
                id = target.itemID,
                chatLink = target.chatLink,
                name = target.name,
                icon = target.icon,
                description = target.description,
                rarity = target.rarity,
                level = target.level,
                sellable = target.sellable,
                buy = target.buy,
                buyGold = target.buyGold,
                buySilver = target.buySilver,
                buyCopper = target.buyCopper,
                sell = target.sell,
                sellGold = target.sellGold,
                sellSilver = target.sellSilver,
                sellCopper = target.sellCopper,
                type = target.type
            ),
            storageType = target.storageType,
            count = target.count,
            charges = target.charges,
            binding = target.binding,
            bindTo = target.bindTo,
            accountID = "",
            category = null,
            price = if (input) target.buy else target.sell,
            gold = if (input) target.buyGold else target.sellGold,
            silver = if (input) target.buySilver else target.sellSilver,
            copper = if (input) target.buyCopper else target.sellCopper
        )
        val actual = target.toDomain(input)
        assertThat(actual).isEqualTo(expected)
    }

    @Test
    fun `toDomain() with binding`() {
        val input = faker.random.nextBoolean()
        val target = dataFaker.storageDAOFaker()
        val expected = StorageItem(
            id = target.id,
            detail = Item(
                id = target.itemID,
                chatLink = target.chatLink,
                name = target.name,
                icon = target.icon,
                description = target.description,
                rarity = target.rarity,
                level = target.level,
                sellable = false,
                buy = target.buy,
                buyGold = target.buyGold,
                buySilver = target.buySilver,
                buyCopper = target.buyCopper,
                sell = target.sell,
                sellGold = target.sellGold,
                sellSilver = target.sellSilver,
                sellCopper = target.sellCopper,
                type = target.type
            ),
            storageType = target.storageType,
            count = target.count,
            charges = target.charges,
            binding = target.binding,
            bindTo = target.bindTo,
            accountID = "",
            category = null,
            price = if (input) target.buy else target.sell,
            gold = if (input) target.buyGold else target.sellGold,
            silver = if (input) target.buySilver else target.sellSilver,
            copper = if (input) target.buyCopper else target.sellCopper
        )
        val actual = target.toDomain(input)
        assertThat(actual).isEqualTo(expected)
    }
}