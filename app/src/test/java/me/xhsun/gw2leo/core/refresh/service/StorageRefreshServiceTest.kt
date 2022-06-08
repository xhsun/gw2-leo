package me.xhsun.gw2leo.core.refresh.service

import androidx.room.withTransaction
import io.github.serpro69.kfaker.Faker
import io.mockk.*
import kotlinx.coroutines.runBlocking
import me.xhsun.gw2leo.DataFaker
import me.xhsun.gw2leo.account.service.IAccountService
import me.xhsun.gw2leo.account.service.ICharacterService
import me.xhsun.gw2leo.core.config.BANK_STORAGE_PREFIX
import me.xhsun.gw2leo.core.config.MIN_REFRESH_RATE_HR
import me.xhsun.gw2leo.core.datastore.IDatastoreRepository
import me.xhsun.gw2leo.storage.datastore.entity.Item
import me.xhsun.gw2leo.storage.datastore.entity.LastModified
import me.xhsun.gw2leo.storage.datastore.entity.MaterialStorageBase
import me.xhsun.gw2leo.storage.datastore.entity.StorageBase
import me.xhsun.gw2leo.storage.error.NoItemFoundError
import me.xhsun.gw2leo.storage.service.IStorageRetrievalService
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

internal class StorageRefreshServiceTest {
    private val faker = Faker()
    private val dataFaker = DataFaker()
    private lateinit var accountServiceMock: IAccountService
    private lateinit var datastoreMock: IDatastoreRepository
    private lateinit var characterServiceMock: ICharacterService
    private lateinit var storageRetrievalServiceMock: IStorageRetrievalService
    private lateinit var target: StorageRefreshService

    @BeforeEach
    fun setUp() {
        MockKAnnotations.init(this)
        mockkStatic(
            "androidx.room.RoomDatabaseKt"
        )
        datastoreMock = mockk()
        val transactionLambda = slot<suspend () -> Unit>()
        coEvery { datastoreMock.withTransaction(capture(transactionLambda)) } coAnswers {
            transactionLambda.captured.invoke()
        }
        accountServiceMock = mockk()
        characterServiceMock = mockk()
        storageRetrievalServiceMock = mockk()
        target = StorageRefreshService(
            accountServiceMock,
            datastoreMock,
            characterServiceMock,
            storageRetrievalServiceMock
        )
    }

    @Test
    fun `updateAll() should update all items and storages`(): Unit = runBlocking {
        val inputID = faker.random.randomString()
        val inputCharacters = listOf(faker.random.randomString())
        val inputInventory = listOf(dataFaker.storageItemFaker())
        val inputBank = listOf(dataFaker.storageItemFaker())
        val inputMaterial = listOf(dataFaker.storageItemFaker())
        val inputItemIds = listOf(faker.random.nextInt(5000, 6000), inputBank[0].detail.id)
        val inputItem = dataFaker.itemFaker(id = inputItemIds[0])

        val actualItems = slot<List<Item>>()
        val actualTypes = slot<List<String>>()
        val actualStorage = slot<List<StorageBase>>()
        val actualMaterials = slot<List<MaterialStorageBase>>()
        val actualLastModified = slot<List<LastModified>>()

        every { accountServiceMock.accountID() } returns inputID
        coEvery { characterServiceMock.characters() } returns inputCharacters
        coEvery { storageRetrievalServiceMock.inventoryItems(inputCharacters[0]) } returns inputInventory
        coEvery { storageRetrievalServiceMock.bankItems() } returns inputBank
        coEvery { storageRetrievalServiceMock.materialItems() } returns inputMaterial
        coEvery { datastoreMock.itemDAO.getAllSellableIds() } returns inputItemIds
        coEvery { storageRetrievalServiceMock.fullItemDetails(setOf(inputItemIds[0])) } returns mapOf(
            Pair(inputItem.id, inputItem)
        )
        coEvery { datastoreMock.itemDAO.insertAll(capture(actualItems)) } returns Unit
        coEvery { datastoreMock.storageDAO.bulkDelete(capture(actualTypes)) } returns Unit
        coEvery { datastoreMock.storageDAO.insertAll(capture(actualStorage)) } returns Unit
        coEvery { datastoreMock.materialStorageDAO.bulkDelete(inputID) } returns Unit
        coEvery { datastoreMock.materialTypeDAO.insertAll(any()) } returns Unit
        coEvery { datastoreMock.materialStorageDAO.insertAll(capture(actualMaterials)) } returns Unit
        coEvery { datastoreMock.lastModifiedDAO.insertAll(capture(actualLastModified)) } returns Unit

        target.updateAll()
        assertThat(actualItems.captured).isNotNull.hasSize(4)
        assertThat(actualTypes.captured).isNotNull.hasSize(2)
        assertThat(actualStorage.captured).isNotNull.hasSize(2)
        assertThat(actualMaterials.captured).isNotNull.hasSize(1)
        assertThat(actualLastModified.captured).isNotNull.hasSize(3)
    }

    @Test
    fun `updateAll() should skip storage`(): Unit = runBlocking {
        val inputID = faker.random.randomString()
        val inputCharacters = listOf(faker.random.randomString())
        val inputMaterial = listOf(dataFaker.storageItemFaker())
        val inputItemIds = listOf(faker.random.nextInt(5000, 6000))
        val inputItem = dataFaker.itemFaker(id = inputItemIds[0])

        val actualItems = slot<List<Item>>()
        val actualMaterials = slot<List<MaterialStorageBase>>()
        val actualLastModified = slot<List<LastModified>>()

        every { accountServiceMock.accountID() } returns inputID
        coEvery { characterServiceMock.characters() } returns inputCharacters
        coEvery { storageRetrievalServiceMock.inventoryItems(inputCharacters[0]) } returns emptyList()
        coEvery { storageRetrievalServiceMock.bankItems() } returns emptyList()
        coEvery { storageRetrievalServiceMock.materialItems() } returns inputMaterial
        coEvery { datastoreMock.itemDAO.getAllSellableIds() } returns inputItemIds
        coEvery { storageRetrievalServiceMock.fullItemDetails(setOf(inputItemIds[0])) } returns mapOf(
            Pair(inputItem.id, inputItem)
        )
        coEvery { datastoreMock.itemDAO.insertAll(capture(actualItems)) } returns Unit
        coEvery { datastoreMock.materialStorageDAO.bulkDelete(inputID) } returns Unit
        coEvery { datastoreMock.materialTypeDAO.insertAll(any()) } returns Unit
        coEvery { datastoreMock.materialStorageDAO.insertAll(capture(actualMaterials)) } returns Unit
        coEvery { datastoreMock.lastModifiedDAO.insertAll(capture(actualLastModified)) } returns Unit

        target.updateAll()
        assertThat(actualItems.captured).isNotNull.hasSize(2)
        assertThat(actualMaterials.captured).isNotNull.hasSize(1)
        assertThat(actualLastModified.captured).isNotNull.hasSize(1)
    }

    @Test
    fun `updateAll() should skip material storage`(): Unit = runBlocking {
        val inputID = faker.random.randomString()
        val inputCharacters = listOf(faker.random.randomString())
        val inputInventory = listOf(dataFaker.storageItemFaker())
        val inputBank = listOf(dataFaker.storageItemFaker())
        val inputItemIds = listOf(faker.random.nextInt(5000, 6000), inputBank[0].detail.id)
        val inputItem = dataFaker.itemFaker(id = inputItemIds[0])

        val actualItems = slot<List<Item>>()
        val actualTypes = slot<List<String>>()
        val actualStorage = slot<List<StorageBase>>()
        val actualLastModified = slot<List<LastModified>>()

        every { accountServiceMock.accountID() } returns inputID
        coEvery { characterServiceMock.characters() } returns inputCharacters
        coEvery { storageRetrievalServiceMock.inventoryItems(inputCharacters[0]) } returns inputInventory
        coEvery { storageRetrievalServiceMock.bankItems() } returns inputBank
        coEvery { storageRetrievalServiceMock.materialItems() } returns emptyList()
        coEvery { datastoreMock.itemDAO.getAllSellableIds() } returns inputItemIds
        coEvery { storageRetrievalServiceMock.fullItemDetails(setOf(inputItemIds[0])) } returns mapOf(
            Pair(inputItem.id, inputItem)
        )
        coEvery { datastoreMock.itemDAO.insertAll(capture(actualItems)) } returns Unit
        coEvery { datastoreMock.storageDAO.bulkDelete(capture(actualTypes)) } returns Unit
        coEvery { datastoreMock.storageDAO.insertAll(capture(actualStorage)) } returns Unit
        coEvery { datastoreMock.lastModifiedDAO.insertAll(capture(actualLastModified)) } returns Unit

        target.updateAll()
        assertThat(actualItems.captured).isNotNull.hasSize(3)
        assertThat(actualTypes.captured).isNotNull.hasSize(2)
        assertThat(actualStorage.captured).isNotNull.hasSize(2)
        assertThat(actualLastModified.captured).isNotNull.hasSize(2)
    }

    @Test
    fun `updateAll() should skip all`(): Unit = runBlocking {
        val inputID = faker.random.randomString()
        val inputCharacters = listOf(faker.random.randomString())
        val inputItemIds = listOf(faker.random.nextInt(5000, 6000))
        val inputItem = dataFaker.itemFaker(id = inputItemIds[0])

        val actualItems = slot<List<Item>>()

        every { accountServiceMock.accountID() } returns inputID
        coEvery { characterServiceMock.characters() } returns inputCharacters
        coEvery { storageRetrievalServiceMock.inventoryItems(inputCharacters[0]) } returns emptyList()
        coEvery { storageRetrievalServiceMock.bankItems() } returns emptyList()
        coEvery { storageRetrievalServiceMock.materialItems() } returns emptyList()
        coEvery { datastoreMock.itemDAO.getAllSellableIds() } returns inputItemIds
        coEvery { storageRetrievalServiceMock.fullItemDetails(setOf(inputItemIds[0])) } returns mapOf(
            Pair(inputItem.id, inputItem)
        )
        coEvery { datastoreMock.itemDAO.insertAll(capture(actualItems)) } returns Unit

        target.updateAll()
        assertThat(actualItems.captured).isNotNull.hasSize(1)
    }

    @Test
    fun `updateStorage() should update inventory`(): Unit = runBlocking {
        val inputType = faker.random.randomString()
        val inputStorage = listOf(dataFaker.storageItemFaker())

        val actualItems = slot<List<Item>>()
        val actualStorage = slot<List<StorageBase>>()
        val actualLastModified = slot<LastModified>()

        coEvery { storageRetrievalServiceMock.inventoryItems(inputType) } returns inputStorage
        coEvery { datastoreMock.itemDAO.insertAll(capture(actualItems)) } returns Unit
        coEvery { datastoreMock.storageDAO.bulkDelete(inputType) } returns Unit
        coEvery { datastoreMock.storageDAO.insertAll(capture(actualStorage)) } returns Unit
        coEvery { datastoreMock.lastModifiedDAO.insert(capture(actualLastModified)) } returns Unit

        target.updateStorage(inputType)
        assertThat(actualItems.captured).isNotNull.hasSize(1)
        assertThat(actualStorage.captured).isNotNull.hasSize(1)
        assertThat(actualLastModified.captured).isNotNull.extracting { it.type }
            .isEqualTo(inputType)
    }

    @Test
    fun `updateStorage() should update bank`(): Unit = runBlocking {
        val inputType = BANK_STORAGE_PREFIX
        val inputStorage = listOf(dataFaker.storageItemFaker())

        val actualItems = slot<List<Item>>()
        val actualStorage = slot<List<StorageBase>>()
        val actualLastModified = slot<LastModified>()

        coEvery { storageRetrievalServiceMock.bankItems() } returns inputStorage
        coEvery { datastoreMock.itemDAO.insertAll(capture(actualItems)) } returns Unit
        coEvery { datastoreMock.storageDAO.bulkDelete(inputType) } returns Unit
        coEvery { datastoreMock.storageDAO.insertAll(capture(actualStorage)) } returns Unit
        coEvery { datastoreMock.lastModifiedDAO.insert(capture(actualLastModified)) } returns Unit

        target.updateStorage(inputType)
        assertThat(actualItems.captured).isNotNull.hasSize(1)
        assertThat(actualStorage.captured).isNotNull.hasSize(1)
        assertThat(actualLastModified.captured).isNotNull.extracting { it.type }
            .isEqualTo(inputType)
    }

    @Test
    fun `updateStorage() should return error if no item found`(): Unit = runBlocking {
        val inputType = faker.random.randomString()

        coEvery { storageRetrievalServiceMock.inventoryItems(inputType) } returns emptyList()

        assertThrows<NoItemFoundError> {
            target.updateStorage(inputType)
        }
    }

    @Test
    fun `updateMaterial() should update material storage`(): Unit = runBlocking {
        val inputID = faker.random.randomString()
        val inputStorage = listOf(dataFaker.storageItemFaker())

        val actualItems = slot<List<Item>>()
        val actualStorage = slot<List<MaterialStorageBase>>()
        val actualLastModified = slot<LastModified>()

        every { accountServiceMock.accountID() } returns inputID
        coEvery { storageRetrievalServiceMock.materialItems() } returns inputStorage
        coEvery { datastoreMock.itemDAO.insertAll(capture(actualItems)) } returns Unit
        coEvery { datastoreMock.materialStorageDAO.bulkDelete(inputID) } returns Unit
        coEvery { datastoreMock.materialTypeDAO.insertAll(any()) } returns Unit
        coEvery { datastoreMock.materialStorageDAO.insertAll(capture(actualStorage)) } returns Unit
        coEvery { datastoreMock.lastModifiedDAO.insert(capture(actualLastModified)) } returns Unit

        target.updateMaterial()
        assertThat(actualItems.captured).isNotNull.hasSize(1)
        assertThat(actualStorage.captured).isNotNull.hasSize(1)
        assertThat(actualLastModified.captured).isNotNull
    }

    @Test
    fun `updateMaterial() should return error if no item found`(): Unit = runBlocking {
        coEvery { storageRetrievalServiceMock.materialItems() } returns emptyList()

        assertThrows<NoItemFoundError> {
            target.updateMaterial()
        }
    }

    @Test
    fun `shouldUpdate() should return true`(): Unit = runBlocking {
        val input = faker.random.randomString()
        val inputLastModified =
            dataFaker.lastModifiedFaker(input, (MIN_REFRESH_RATE_HR + 1).toLong())

        coEvery { datastoreMock.lastModifiedDAO.get(input) } returns inputLastModified

        val actual = target.shouldUpdate(input)
        assertThat(actual).isTrue()
    }

    @Test
    fun `shouldUpdate() should return true when no record found`(): Unit = runBlocking {
        val input = faker.random.randomString()

        coEvery { datastoreMock.lastModifiedDAO.get(input) } returns null

        val actual = target.shouldUpdate(input)
        assertThat(actual).isTrue()
    }

    @Test
    fun `shouldUpdate() should return false`(): Unit = runBlocking {
        val input = faker.random.randomString()
        val inputLastModified =
            dataFaker.lastModifiedFaker(input, (MIN_REFRESH_RATE_HR - 1).toLong())

        coEvery { datastoreMock.lastModifiedDAO.get(input) } returns inputLastModified

        val actual = target.shouldUpdate(input)
        assertThat(actual).isFalse()
    }
}