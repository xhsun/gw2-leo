package me.xhsun.gw2leo.storage.service.comparator

import io.github.serpro69.kfaker.Faker
import me.xhsun.gw2leo.DataFaker
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

internal class StorageItemComparatorTest {
    private val faker = Faker()
    private val dataFaker = DataFaker()
    private lateinit var target: StorageItemComparator

    @BeforeEach
    fun setUp() {
        target = StorageItemComparator()
    }

    @Test
    fun `areItemsTheSame() should return true when id are equal`() {
        val id = faker.random.nextInt(5000, 6000)
        val inputOld = dataFaker.storageItemFaker(id = id)
        val inputNew = dataFaker.storageItemFaker(id = id)
        val actual = target.areItemsTheSame(inputOld, inputNew)
        assertThat(actual).isTrue
    }

    @Test
    fun `areItemsTheSame() should return false when id not equal`() {
        val inputOld = dataFaker.storageItemFaker(id = faker.random.nextInt(5000, 6000))
        val inputNew = dataFaker.storageItemFaker()
        val actual = target.areItemsTheSame(inputOld, inputNew)
        assertThat(actual).isFalse
    }

    @Test
    fun `areContentsTheSame() should return true when item is the same`() {
        val input = dataFaker.storageItemFaker()
        val actual = target.areContentsTheSame(input, input)
        assertThat(actual).isTrue
    }

    @Test
    fun `areContentsTheSame() should return false when item not the same`() {
        val actual =
            target.areContentsTheSame(dataFaker.storageItemFaker(), dataFaker.storageItemFaker())
        assertThat(actual).isFalse
    }

    @Test
    fun `getChangePayload() should return object when item is different`() {
        val actual =
            target.getChangePayload(dataFaker.storageItemFaker(), dataFaker.storageItemFaker())
        assertThat(actual).isNull()
    }

    @Test
    fun `getChangePayload() should return object when item is the same`() {
        val input = dataFaker.storageItemFaker()
        val actual = target.getChangePayload(input, input)
        assertThat(actual).isNotNull
    }

    @Test
    fun `getChangePayload() should return object when details are the different`() {
        val input = dataFaker.storageItemFaker()
        val inputItem = dataFaker.itemFaker()

        val actual = target.getChangePayload(input, input.copy(detail = inputItem))
        assertThat(actual).isNotNull
    }

    @Test
    fun `getChangePayload() should return object when price are the different`() {
        val input = dataFaker.storageItemFaker()

        val actual =
            target.getChangePayload(input, input.copy(price = faker.random.nextInt(5000, 6000)))
        assertThat(actual).isNotNull
    }

    @Test
    fun `getChangePayload() should return object when count are the different`() {
        val input = dataFaker.storageItemFaker()

        val actual =
            target.getChangePayload(input, input.copy(count = faker.random.nextInt(5000, 6000)))
        assertThat(actual).isNotNull
    }
}