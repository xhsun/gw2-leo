package me.xhsun.gw2leo.storage.datastore.entity

import io.github.serpro69.kfaker.Faker
import me.xhsun.gw2leo.DataFaker
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

internal class LastModifiedTest {
    private val faker = Faker()
    private val dataFaker = DataFaker()

    @Test
    fun elapsedTime() {
        val expected = faker.random.nextInt(1, 5).toLong()
        val target = dataFaker.lastModifiedFaker(timeOffset = expected)

        val actual = target.elapsedTime()
        assertThat(actual).isEqualTo(expected)
    }
}