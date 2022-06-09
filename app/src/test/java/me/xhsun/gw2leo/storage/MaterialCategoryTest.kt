package me.xhsun.gw2leo.storage

import me.xhsun.gw2leo.DataFaker
import me.xhsun.gw2leo.storage.datastore.entity.MaterialType
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

internal class MaterialCategoryTest {
    private val dataFaker = DataFaker()

    @Test
    fun toDAO() {
        val target = dataFaker.materialCategoryFaker()
        val expected = MaterialType(
            id = target.id,
            name = target.name
        )
        val actual = target.toDAO()
        assertThat(actual).isEqualTo(expected)
    }
}