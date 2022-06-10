package me.xhsun.gw2leo.storage.service

import io.github.serpro69.kfaker.Faker
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import me.xhsun.gw2leo.DataFaker
import me.xhsun.gw2leo.account.service.IAccountService
import me.xhsun.gw2leo.core.config.ID_SEPARATOR
import me.xhsun.gw2leo.core.http.IGW2Repository
import me.xhsun.gw2leo.core.http.IGW2RepositoryFactory
import me.xhsun.gw2leo.storage.http.model.BagsDTO
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.ResponseBody.Companion.toResponseBody
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import retrofit2.HttpException
import retrofit2.Response

internal class StorageRetrievalServiceTest {
    private val faker = Faker()
    private val dataFaker = DataFaker()
    private lateinit var accountServiceMock: IAccountService
    private lateinit var gw2RepositoryFactoryMock: IGW2RepositoryFactory
    private lateinit var gw2RepositoryMock: IGW2Repository
    private lateinit var target: StorageRetrievalService

    @BeforeEach
    fun setUp() {
        accountServiceMock = mockk()
        gw2RepositoryFactoryMock = mockk()
        gw2RepositoryMock = mockk()
        every { gw2RepositoryFactoryMock.gw2Repository() } returns gw2RepositoryMock
        target = StorageRetrievalService(gw2RepositoryFactoryMock, accountServiceMock)
    }

    @Test
    fun `bankItems() should retrieve all valid`(): Unit = runBlocking {
        val inputAccountID = faker.random.randomString()
        val inputItemID = listOf(faker.random.nextInt(5000, 6000), faker.random.nextInt(5000, 6000))
        val inputIds = inputItemID.joinToString(separator = ID_SEPARATOR)
        val inputStorage =
            listOf(
                dataFaker.inventoryDTOFaker(id = inputItemID[0]),
                dataFaker.inventoryDTOFaker(id = inputItemID[1]),
                dataFaker.inventoryDTOFaker(count = 0),
                null
            )
        val inputItem = listOf(
            dataFaker.itemDTOFaker(id = inputItemID[0]),
            dataFaker.itemDTOFaker(id = inputItemID[1])
        )
        val inputPrice = listOf(dataFaker.priceDTOFaker(id = inputItemID[0]))

        every { accountServiceMock.accountID() } returns inputAccountID
        coEvery { gw2RepositoryMock.getBank() } returns inputStorage
        coEvery { gw2RepositoryMock.getItems(inputIds) } returns inputItem
        coEvery { gw2RepositoryMock.getPrices(inputIds) } returns inputPrice

        val actual = target.bankItems()
        assertThat(actual).hasSize(2)
        val actualSellable = actual.find { it.detail.id == inputItemID[0] }
        val actualNotSellable = actual.find { it.detail.id == inputItemID[1] }
        assertThat(actualSellable!!.detail.sellable).isTrue
        assertThat(actualSellable.detail.buy).isEqualTo(inputPrice[0].buys.unitPrice)
        assertThat(actualSellable.detail.sell).isEqualTo(inputPrice[0].sells.unitPrice)
        assertThat(actualNotSellable!!.detail.sellable).isFalse
        assertThat(actualNotSellable.detail.buy).isEqualTo(0)
        assertThat(actualNotSellable.detail.sell).isEqualTo(0)
    }

    @Test
    fun `bankItems() should return empty due to values invalid`(): Unit = runBlocking {
        val inputAccountID = faker.random.randomString()
        val inputStorage =
            listOf(
                dataFaker.inventoryDTOFaker(count = 0),
                null
            )

        every { accountServiceMock.accountID() } returns inputAccountID
        coEvery { gw2RepositoryMock.getBank() } returns inputStorage

        val actual = target.bankItems()
        assertThat(actual).isEmpty()
    }

    @Test
    fun `bankItems() should return empty due no value`(): Unit = runBlocking {
        val inputAccountID = faker.random.randomString()

        every { accountServiceMock.accountID() } returns inputAccountID
        coEvery { gw2RepositoryMock.getBank() } returns emptyList()

        val actual = target.bankItems()
        assertThat(actual).isEmpty()
    }

    @Test
    fun `inventoryItems() should retrieve all valid`(): Unit = runBlocking {
        val input = faker.random.randomString()
        val inputItemID = listOf(faker.random.nextInt(5000, 6000), faker.random.nextInt(5000, 6000))
        val inputIds = inputItemID.joinToString(separator = ID_SEPARATOR)
        val inputStorage = BagsDTO(
            listOf(
                dataFaker.bagDTOFaker(
                    listOf(
                        dataFaker.inventoryDTOFaker(id = inputItemID[0]),
                        dataFaker.inventoryDTOFaker(id = inputItemID[1]),
                        dataFaker.inventoryDTOFaker(count = 0),
                        null
                    )
                ), null
            )
        )

        val inputItem = listOf(
            dataFaker.itemDTOFaker(id = inputItemID[0]),
            dataFaker.itemDTOFaker(id = inputItemID[1])
        )
        val inputPrice = listOf(dataFaker.priceDTOFaker(id = inputItemID[0]))

        coEvery { gw2RepositoryMock.getCharacterInventory(input) } returns inputStorage
        coEvery { gw2RepositoryMock.getItems(inputIds) } returns inputItem
        coEvery { gw2RepositoryMock.getPrices(inputIds) } returns inputPrice

        val actual = target.inventoryItems(input)
        assertThat(actual).hasSize(2)
        val actualSellable = actual.find { it.detail.id == inputItemID[0] }
        val actualNotSellable = actual.find { it.detail.id == inputItemID[1] }
        assertThat(actualSellable!!.detail.sellable).isTrue
        assertThat(actualSellable.detail.buy).isEqualTo(inputPrice[0].buys.unitPrice)
        assertThat(actualSellable.detail.sell).isEqualTo(inputPrice[0].sells.unitPrice)
        assertThat(actualNotSellable!!.detail.sellable).isFalse
        assertThat(actualNotSellable.detail.buy).isEqualTo(0)
        assertThat(actualNotSellable.detail.sell).isEqualTo(0)
    }

    @Test
    fun `inventoryItems() return empty due to values invalid`(): Unit = runBlocking {
        val input = faker.random.randomString()
        val inputStorage = BagsDTO(
            listOf(
                dataFaker.bagDTOFaker(
                    listOf(
                        dataFaker.inventoryDTOFaker(count = 0),
                        null
                    )
                ), null
            )
        )

        coEvery { gw2RepositoryMock.getCharacterInventory(input) } returns inputStorage

        val actual = target.inventoryItems(input)
        assertThat(actual).isEmpty()
    }

    @Test
    fun `inventoryItems() should return empty due no value`(): Unit = runBlocking {
        val input = faker.random.randomString()

        coEvery { gw2RepositoryMock.getCharacterInventory(input) } returns BagsDTO(emptyList())

        val actual = target.inventoryItems(input)
        assertThat(actual).isEmpty()
    }

    @Test
    fun `materialItems() should retrieve all valid`(): Unit = runBlocking {
        val inputAccountID = faker.random.randomString()
        val inputItemID = listOf(faker.random.nextInt(5000, 6000), faker.random.nextInt(5000, 6000))
        val inputCategory =
            listOf(faker.random.nextInt(5000, 6000))
        val inputIds = inputItemID.joinToString(separator = ID_SEPARATOR)
        val inputCategoryIds = inputCategory.joinToString(separator = ID_SEPARATOR)
        val inputStorage =
            listOf(
                dataFaker.materialDTOFaker(id = inputItemID[0], categoryID = inputCategory[0]),
                dataFaker.materialDTOFaker(id = inputItemID[1], categoryID = inputCategory[0]),
                dataFaker.materialDTOFaker(count = 0)
            )
        val inputCategories = listOf(dataFaker.materialTypeDTOFaker(id = inputCategory[0]))
        val inputItem = listOf(
            dataFaker.itemDTOFaker(id = inputItemID[0]),
            dataFaker.itemDTOFaker(id = inputItemID[1])
        )
        val inputPrice = listOf(dataFaker.priceDTOFaker(id = inputItemID[0]))

        every { accountServiceMock.accountID() } returns inputAccountID
        coEvery { gw2RepositoryMock.getMaterialBank() } returns inputStorage
        coEvery { gw2RepositoryMock.getMaterialBankInfo(inputCategoryIds) } returns inputCategories
        coEvery { gw2RepositoryMock.getItems(inputIds) } returns inputItem
        coEvery { gw2RepositoryMock.getPrices(inputIds) } returns inputPrice

        val actual = target.materialItems()
        assertThat(actual).hasSize(2)
        val actualSellable = actual.find { it.detail.id == inputItemID[0] }
        val actualNotSellable = actual.find { it.detail.id == inputItemID[1] }
        assertThat(actualSellable!!.detail.sellable).isTrue
        assertThat(actualSellable.category!!.name).isEqualTo(inputCategories[0].name)
        assertThat(actualSellable.detail.buy).isEqualTo(inputPrice[0].buys.unitPrice)
        assertThat(actualSellable.detail.sell).isEqualTo(inputPrice[0].sells.unitPrice)
        assertThat(actualNotSellable!!.detail.sellable).isFalse
        assertThat(actualNotSellable.category!!.name).isEqualTo(inputCategories[0].name)
        assertThat(actualNotSellable.detail.buy).isEqualTo(0)
        assertThat(actualNotSellable.detail.sell).isEqualTo(0)
    }

    @Test
    fun `materialItems() should return empty due invalid value`(): Unit = runBlocking {
        val inputAccountID = faker.random.randomString()
        val inputStorage =
            listOf(
                dataFaker.materialDTOFaker(count = 0)
            )

        every { accountServiceMock.accountID() } returns inputAccountID
        coEvery { gw2RepositoryMock.getMaterialBank() } returns inputStorage

        val actual = target.materialItems()
        assertThat(actual).isEmpty()
    }

    @Test
    fun `materialItems() should return empty due no value`(): Unit = runBlocking {
        val inputAccountID = faker.random.randomString()

        every { accountServiceMock.accountID() } returns inputAccountID
        coEvery { gw2RepositoryMock.getMaterialBank() } returns emptyList()

        val actual = target.materialItems()
        assertThat(actual).isEmpty()
    }

    @Test
    fun `fullItemDetails() should retrieve all item details`(): Unit = runBlocking {
        val inputItemID = listOf(faker.random.nextInt(5000, 6000), faker.random.nextInt(5000, 6000))
        val inputIds = inputItemID.joinToString(separator = ID_SEPARATOR)
        val inputItem = listOf(
            dataFaker.itemDTOFaker(id = inputItemID[0]),
            dataFaker.itemDTOFaker(id = inputItemID[1])
        )
        val inputPrice = listOf(dataFaker.priceDTOFaker(id = inputItemID[0]))

        coEvery { gw2RepositoryMock.getItems(inputIds) } returns inputItem
        coEvery { gw2RepositoryMock.getPrices(inputIds) } returns inputPrice

        val actual = target.fullItemDetails(inputItemID.toSet())
        assertThat(actual).hasSize(2)
        val actualSellable = actual[inputItemID[0]]!!
        val actualNotSellable = actual[inputItemID[1]]!!
        assertThat(actualSellable.sellable).isTrue
        assertThat(actualSellable.buy).isEqualTo(inputPrice[0].buys.unitPrice)
        assertThat(actualSellable.sell).isEqualTo(inputPrice[0].sells.unitPrice)
        assertThat(actualNotSellable.sellable).isFalse
        assertThat(actualNotSellable.buy).isEqualTo(0)
        assertThat(actualNotSellable.sell).isEqualTo(0)
    }

    @Test
    fun `fullItemDetails() should return empty due to 400`(): Unit = runBlocking {
        val inputItemID = listOf(faker.random.nextInt(5000, 6000), faker.random.nextInt(5000, 6000))
        val inputIds = inputItemID.joinToString(separator = ID_SEPARATOR)


        coEvery { gw2RepositoryMock.getItems(inputIds) } throws HttpException(
            Response.error<String>(
                400,
                "".toResponseBody("application/json".toMediaTypeOrNull())
            )
        )

        val actual = target.fullItemDetails(inputItemID.toSet())
        assertThat(actual).isEmpty()
    }

    @Test
    fun `fullItemDetails() should return empty due to item not found`(): Unit = runBlocking {
        val inputItemID = listOf(faker.random.nextInt(5000, 6000), faker.random.nextInt(5000, 6000))
        val inputIds = inputItemID.joinToString(separator = ID_SEPARATOR)


        coEvery { gw2RepositoryMock.getItems(inputIds) } returns emptyList()

        val actual = target.fullItemDetails(inputItemID.toSet())
        assertThat(actual).isEmpty()
    }

    @Test
    fun `fullItemDetails() should throw error due to unexpected error`(): Unit = runBlocking {
        val inputItemID = listOf(faker.random.nextInt(5000, 6000), faker.random.nextInt(5000, 6000))
        val inputIds = inputItemID.joinToString(separator = ID_SEPARATOR)


        coEvery { gw2RepositoryMock.getItems(inputIds) } throws Exception()

        assertThrows<Exception> {
            target.fullItemDetails(inputItemID.toSet())
        }
    }

    @Test
    fun `fullItemDetails() should throw error due to empty list`(): Unit = runBlocking {
        assertThrows<IllegalArgumentException> {
            target.fullItemDetails(emptySet())
        }
    }

    @Test
    fun `fullItemDetails() should return not sellable due to 400`(): Unit =
        runBlocking {
            val inputItemID =
                listOf(faker.random.nextInt(5000, 6000), faker.random.nextInt(5000, 6000))
            val inputIds = inputItemID.joinToString(separator = ID_SEPARATOR)
            val inputItem = listOf(
                dataFaker.itemDTOFaker(id = inputItemID[0]),
                dataFaker.itemDTOFaker(id = inputItemID[1])
            )

            coEvery { gw2RepositoryMock.getItems(inputIds) } returns inputItem
            coEvery { gw2RepositoryMock.getPrices(inputIds) } throws HttpException(
                Response.error<String>(
                    400,
                    "".toResponseBody("application/json".toMediaTypeOrNull())
                )
            )

            val actual = target.fullItemDetails(inputItemID.toSet())
            assertThat(actual).hasSize(2)
            val actualSellable = actual[inputItemID[0]]!!
            val actualNotSellable = actual[inputItemID[1]]!!
            assertThat(actualSellable.sellable).isFalse
            assertThat(actualNotSellable.buy).isEqualTo(0)
            assertThat(actualNotSellable.sell).isEqualTo(0)
            assertThat(actualNotSellable.sellable).isFalse
            assertThat(actualNotSellable.buy).isEqualTo(0)
            assertThat(actualNotSellable.sell).isEqualTo(0)
        }

    @Test
    fun `fullItemDetails() should return not sellable due to price not found`(): Unit =
        runBlocking {
            val inputItemID =
                listOf(faker.random.nextInt(5000, 6000), faker.random.nextInt(5000, 6000))
            val inputIds = inputItemID.joinToString(separator = ID_SEPARATOR)
            val inputItem = listOf(
                dataFaker.itemDTOFaker(id = inputItemID[0]),
                dataFaker.itemDTOFaker(id = inputItemID[1])
            )

            coEvery { gw2RepositoryMock.getItems(inputIds) } returns inputItem
            coEvery { gw2RepositoryMock.getPrices(inputIds) } returns emptyList()

            val actual = target.fullItemDetails(inputItemID.toSet())
            assertThat(actual).hasSize(2)
            val actualSellable = actual[inputItemID[0]]!!
            val actualNotSellable = actual[inputItemID[1]]!!
            assertThat(actualSellable.sellable).isFalse
            assertThat(actualNotSellable.buy).isEqualTo(0)
            assertThat(actualNotSellable.sell).isEqualTo(0)
            assertThat(actualNotSellable.sellable).isFalse
            assertThat(actualNotSellable.buy).isEqualTo(0)
            assertThat(actualNotSellable.sell).isEqualTo(0)
        }

    @Test
    fun `fullItemDetails() should throw error due to unexpected price error`(): Unit = runBlocking {
        val inputItemID =
            listOf(faker.random.nextInt(5000, 6000), faker.random.nextInt(5000, 6000))
        val inputIds = inputItemID.joinToString(separator = ID_SEPARATOR)
        val inputItem = listOf(
            dataFaker.itemDTOFaker(id = inputItemID[0]),
            dataFaker.itemDTOFaker(id = inputItemID[1])
        )

        coEvery { gw2RepositoryMock.getItems(inputIds) } returns inputItem
        coEvery { gw2RepositoryMock.getPrices(inputIds) } throws Exception()

        assertThrows<Exception> {
            target.fullItemDetails(inputItemID.toSet())
        }
    }
}