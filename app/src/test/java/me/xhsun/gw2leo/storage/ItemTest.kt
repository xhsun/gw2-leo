package me.xhsun.gw2leo.storage

import io.github.serpro69.kfaker.Faker
import me.xhsun.gw2leo.DataFaker
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

internal class ItemTest {
    private val faker = Faker()
    private val dataFaker = DataFaker()

    @Test
    fun toDAO() {
        val target = dataFaker.itemFaker()
        val expected = dataFaker.itemDAOFaker(
            id = target.id,
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
        )
        val actual = target.toDAO()
        assertThat(actual).isEqualTo(expected)
    }

    @Test
    fun updatePrice() {
        val inputBuy = faker.random.nextInt(1, 100)
        val inputSell = faker.random.nextInt(1, 100)
        val buyPrice = Item.parseCoins(inputBuy)
        val sellPrice = Item.parseCoins(inputSell)
        val inputSellable = faker.random.nextBoolean()

        val target = dataFaker.itemFaker()
        val expected = dataFaker.itemFaker(
            id = target.id,
            chatLink = target.chatLink,
            name = target.name,
            icon = target.icon,
            description = target.description,
            rarity = target.rarity,
            level = target.level,
            sellable = inputSellable,
            buy = inputBuy,
            buyGold = buyPrice.first,
            buySilver = buyPrice.second,
            buyCopper = buyPrice.third,
            sell = inputSell,
            sellGold = sellPrice.first,
            sellSilver = sellPrice.second,
            sellCopper = sellPrice.third,
            type = target.type
        )
        val actual = target.updatePrice(inputBuy, inputSell, inputSellable)
        assertThat(actual).isEqualTo(expected)
    }

    @Test
    fun testParseCoinsValidInput() {
        assertThat(Item.parseCoins(199)).isEqualTo(Triple(0, 1, 99))
    }

    @Test
    fun testParseCoinsInvalidInput() {
        assertThat(Item.parseCoins(-1)).isEqualTo(Triple(0, 0, 0))
    }

    @Test
    fun testParseCoinsCombinedValue() {
        assertThat(Item.parseCoins(99999)).isEqualTo(Triple(9, 99, 99))
    }
}