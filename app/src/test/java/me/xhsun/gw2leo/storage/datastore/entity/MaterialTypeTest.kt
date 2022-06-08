package me.xhsun.gw2leo.storage.datastore.entity

import io.github.serpro69.kfaker.Faker
import me.xhsun.gw2leo.storage.MaterialCategory
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

internal class MaterialTypeTest {
    private val faker = Faker()

    @Test
    fun toDomain() {
        val target = MaterialType(
            faker.random.nextInt(),
            faker.random.randomString()
        )
        val expected = MaterialCategory(
            target.id, target.name
        )
        val actual = target.toDomain()
        assertThat(actual).isEqualTo(expected)
    }
}