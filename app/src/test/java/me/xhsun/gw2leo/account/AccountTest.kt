package me.xhsun.gw2leo.account

import me.xhsun.gw2leo.DataFaker
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.Test

internal class AccountTest {
    private val dataFaker = DataFaker()

    @Test
    fun `toDAO() should map`() {
        val expected = dataFaker.accountDAOFaker()
        val target = dataFaker.accountFaker(expected.id, expected.name, expected.api)
        val actual = target.toDAO()
        Assertions.assertThat(actual).isEqualTo(expected)
    }
}