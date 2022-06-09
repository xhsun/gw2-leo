package me.xhsun.gw2leo.storage.ui.model

import android.widget.CheckBox
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import io.github.serpro69.kfaker.Faker
import io.mockk.every
import io.mockk.mockk
import me.xhsun.gw2leo.DataFaker
import me.xhsun.gw2leo.InstantExecutorExtension
import org.assertj.core.api.Assertions.assertThat
import org.junit.Rule
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith

@ExtendWith(InstantExecutorExtension::class)
internal class SortViewModelTest {
    private val faker = Faker()
    private val dataFaker = DataFaker()

    private lateinit var target: SortViewModel

    @Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    @BeforeEach
    fun setUp() {
        target = SortViewModel()
    }

    @Test
    fun setSellable() {
        target.sortState.observeForever {
            assertThat(it.sellable).isTrue
            assertThat(it.isAsc).isFalse
            assertThat(it.isBuy).isTrue
        }
        target.sellable = true
    }

    @Test
    fun onSortDirectionChange() {
        val input = mockk<CheckBox>(relaxed = true)

        every { input.isChecked } returns true

        target.sortState.observeForever {
            assertThat(it.sellable).isFalse
            assertThat(it.isAsc).isTrue
            assertThat(it.isBuy).isTrue
        }

        target.onSortDirectionChange(input)
    }

    @Test
    fun onSortFieldChange() {
        target.sortState.observeForever {
            assertThat(it.sellable).isFalse
            assertThat(it.isAsc).isFalse
            assertThat(it.isBuy).isFalse
        }

        target.onSortFieldChange(0)
    }
}