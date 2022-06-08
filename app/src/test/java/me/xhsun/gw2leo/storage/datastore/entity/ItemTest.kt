package me.xhsun.gw2leo.storage.datastore.entity

import me.xhsun.gw2leo.DataFaker
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

internal class ItemTest {
    private val dataFaker = DataFaker()

    @Test
    fun toDomain() {
        val target = dataFaker.itemDAOFaker()
        val expected = dataFaker.itemFaker(
            target.id,
            target.chatLink,
            target.name,
            target.icon,
            target.description,
            target.rarity,
            target.level,
            target.sellable,
            target.buy,
            target.buyGold,
            target.buySilver,
            target.buyCopper,
            target.sell,
            target.sellGold,
            target.sellSilver,
            target.sellCopper,
            target.type
        )
        val actual = target.toDomain()
        assertThat(actual).isEqualTo(expected)
    }
}