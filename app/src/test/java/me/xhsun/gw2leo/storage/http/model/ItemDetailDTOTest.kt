package me.xhsun.gw2leo.storage.http.model

import io.github.serpro69.kfaker.Faker
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

internal class ItemDetailDTOTest {
    private val faker = Faker()

    @Test
    fun `asString() convert all values`() {
        val target = ItemDetailDTO(
            faker.random.randomString(),
            faker.random.randomString(),
            faker.random.randomString(),
        )
        val expected = "${target.type}\n${target.weightClass}\n${target.description}\n"
        val actual = target.asString()
        assertThat(actual).isEqualTo(expected)
    }

    @Test
    fun `asString() without type`() {
        val target = ItemDetailDTO(
            null,
            faker.random.randomString(),
            faker.random.randomString(),
        )
        val expected = "${target.weightClass}\n${target.description}\n"
        val actual = target.asString()
        assertThat(actual).isEqualTo(expected)
    }

    @Test
    fun `asString() without weightClass`() {
        val target = ItemDetailDTO(
            faker.random.randomString(),
            null,
            faker.random.randomString(),
        )
        val expected = "${target.type}\n${target.description}\n"
        val actual = target.asString()
        assertThat(actual).isEqualTo(expected)
    }

    @Test
    fun `asString() without description`() {
        val target = ItemDetailDTO(
            faker.random.randomString(),
            faker.random.randomString(),
            null,
        )
        val expected = "${target.type}\n${target.weightClass}\n"
        val actual = target.asString()
        assertThat(actual).isEqualTo(expected)
    }
}