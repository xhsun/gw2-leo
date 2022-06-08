package me.xhsun.gw2leo.account

import io.github.serpro69.kfaker.Faker
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.Test

internal class AccountTest {
    private val faker = Faker()

    @Test
    fun `toDAO() should map`() {
        val expected = me.xhsun.gw2leo.account.datastore.entity.Account(
            id = faker.random.randomString(),
            name = faker.random.randomString(),
            API = faker.random.nextUUID()
        )

        val target = Account(
            id = expected.id,
            name = expected.name,
            API = expected.API
        )

        val actual = target.toDAO()
        Assertions.assertThat(actual).isEqualTo(expected)
    }
}