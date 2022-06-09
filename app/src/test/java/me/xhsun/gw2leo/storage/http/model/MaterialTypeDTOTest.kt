package me.xhsun.gw2leo.storage.http.model

import me.xhsun.gw2leo.DataFaker
import me.xhsun.gw2leo.storage.MaterialCategory
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

internal class MaterialTypeDTOTest {
    private val dataFaker = DataFaker()

    @Test
    fun toDomain() {
        val target = dataFaker.materialTypeDTOFaker()
        val expected = MaterialCategory(
            id = target.id,
            name = target.name
        )
        val actual = target.toDomain()
        assertThat(actual).isEqualTo(expected)
    }
}