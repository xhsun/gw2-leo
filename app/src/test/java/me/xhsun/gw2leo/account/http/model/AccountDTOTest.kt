package me.xhsun.gw2leo.account.http.model

import me.xhsun.gw2leo.DataFaker
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

internal class AccountDTOTest {
    private val faker = DataFaker()

    @Test
    fun `toDomain() should map`() {
        val expected = faker.accountFaker()
        val target = faker.accountDTOFaker(expected.id, expected.name)
        val actual = target.toDomain(expected.api)
        assertThat(actual).isEqualTo(expected)
    }
}