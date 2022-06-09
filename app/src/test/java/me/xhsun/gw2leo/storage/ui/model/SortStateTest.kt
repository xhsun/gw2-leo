package me.xhsun.gw2leo.storage.ui.model

import io.github.serpro69.kfaker.Faker
import me.xhsun.gw2leo.storage.StorageState
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

internal class SortStateTest {
    private val faker = Faker()

    @Test
    fun `toStorageDisplay() should set BUY_ASC_SELLABLE`() {
        val input = faker.random.randomString()
        val target = SortState(isBuy = true, isAsc = true, sellable = true)
        val expected = StorageDisplay(input, StorageState.BUY_ASC_SELLABLE)
        val actual = target.toStorageDisplay(input)
        assertThat(actual).isEqualTo(expected)
    }

    @Test
    fun `toStorageDisplay() should set BUY_ASC`() {
        val input = faker.random.randomString()
        val target = SortState(isBuy = true, isAsc = true, sellable = false)
        val expected = StorageDisplay(input, StorageState.BUY_ASC)
        val actual = target.toStorageDisplay(input)
        assertThat(actual).isEqualTo(expected)
    }

    @Test
    fun `toStorageDisplay() should set BUY_DESC_SELLABLE`() {
        val input = faker.random.randomString()
        val target = SortState(isBuy = true, isAsc = false, sellable = true)
        val expected = StorageDisplay(input, StorageState.BUY_DESC_SELLABLE)
        val actual = target.toStorageDisplay(input)
        assertThat(actual).isEqualTo(expected)
    }

    @Test
    fun `toStorageDisplay() should set BUY_DESC`() {
        val input = faker.random.randomString()
        val target = SortState(isBuy = true, isAsc = false, sellable = false)
        val expected = StorageDisplay(input, StorageState.BUY_DESC)
        val actual = target.toStorageDisplay(input)
        assertThat(actual).isEqualTo(expected)
    }

    @Test
    fun `toStorageDisplay() should set SELL_ASC_SELLABLE`() {
        val input = faker.random.randomString()
        val target = SortState(isBuy = false, isAsc = true, sellable = true)
        val expected = StorageDisplay(input, StorageState.SELL_ASC_SELLABLE)
        val actual = target.toStorageDisplay(input)
        assertThat(actual).isEqualTo(expected)
    }

    @Test
    fun `toStorageDisplay() should set SELL_ASC`() {
        val input = faker.random.randomString()
        val target = SortState(isBuy = false, isAsc = true, sellable = false)
        val expected = StorageDisplay(input, StorageState.SELL_ASC)
        val actual = target.toStorageDisplay(input)
        assertThat(actual).isEqualTo(expected)
    }

    @Test
    fun `toStorageDisplay() should set SELL_DESC_SELLABLE`() {
        val input = faker.random.randomString()
        val target = SortState(isBuy = false, isAsc = false, sellable = true)
        val expected = StorageDisplay(input, StorageState.SELL_DESC_SELLABLE)
        val actual = target.toStorageDisplay(input)
        assertThat(actual).isEqualTo(expected)
    }

    @Test
    fun `toStorageDisplay() should set SELL_DESC`() {
        val input = faker.random.randomString()
        val target = SortState(isBuy = false, isAsc = false, sellable = false)
        val expected = StorageDisplay(input, StorageState.SELL_DESC)
        val actual = target.toStorageDisplay(input)
        assertThat(actual).isEqualTo(expected)
    }
}