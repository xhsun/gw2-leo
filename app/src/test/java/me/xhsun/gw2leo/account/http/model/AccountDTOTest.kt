package me.xhsun.gw2leo.account.http.model

import io.github.serpro69.kfaker.Faker
import me.xhsun.gw2leo.account.Account
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

internal class AccountDTOTest {
    private val faker = Faker()

    @Test
    fun `toDomain() should map`() {
        val expected = Account(
            id = faker.random.randomString(),
            name = faker.random.randomString(),
            API = faker.random.nextUUID()
        )

        val target = AccountDTO(
            id = expected.id,
            name = expected.name
        )
        val actual = target.toDomain(expected.API)
        assertThat(actual).isEqualTo(expected)
    }
}