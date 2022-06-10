package me.xhsun.gw2leo.storage.http.model

import me.xhsun.gw2leo.DataFaker
import me.xhsun.gw2leo.storage.Item
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

internal class ItemDTOTest {
    private val dataFaker = DataFaker()

    @Test
    fun `toDomain() with null details`() {
        val target = dataFaker.itemDTOFaker()
        val expected = Item(
            id = target.id,
            chatLink = target.chatLink,
            name = target.name,
            icon = target.icon,
            type = target.type,
            description = target.description!!,
            rarity = target.rarity,
            level = target.level,
            sellable = true,
            buy = 0,
            buyGold = 0,
            buySilver = 0,
            buyCopper = 0,
            sell = 0,
            sellGold = 0,
            sellSilver = 0,
            sellCopper = 0
        )
        val actual = target.toDomain()
        assertThat(actual).isEqualTo(expected)
    }

    @Test
    fun `toDomain() with details`() {
        val target = dataFaker.itemDTOFaker(detailEmpty = false)
        val expected = Item(
            id = target.id,
            chatLink = target.chatLink,
            name = target.name,
            icon = target.icon,
            type = target.type,
            description = "${target.details?.asString()}<br>${target.description}",
            rarity = target.rarity,
            level = target.level,
            sellable = true,
            buy = 0,
            buyGold = 0,
            buySilver = 0,
            buyCopper = 0,
            sell = 0,
            sellGold = 0,
            sellSilver = 0,
            sellCopper = 0
        )
        val actual = target.toDomain()
        assertThat(actual).isEqualTo(expected)
    }

    @Test
    fun `toDomain() without description`() {
        val target = dataFaker.itemDTOFaker(detailEmpty = false, description = null)
        val expected = Item(
            id = target.id,
            chatLink = target.chatLink,
            name = target.name,
            icon = target.icon,
            type = target.type,
            description = target.details?.asString()!!,
            rarity = target.rarity,
            level = target.level,
            sellable = true,
            buy = 0,
            buyGold = 0,
            buySilver = 0,
            buyCopper = 0,
            sell = 0,
            sellGold = 0,
            sellSilver = 0,
            sellCopper = 0
        )
        val actual = target.toDomain()
        assertThat(actual).isEqualTo(expected)
    }
}