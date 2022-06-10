package me.xhsun.gw2leo.core.refresh.service

import androidx.room.withTransaction
import io.github.serpro69.kfaker.Faker
import io.mockk.*
import kotlinx.coroutines.runBlocking
import me.xhsun.gw2leo.DataFaker
import me.xhsun.gw2leo.account.datastore.IAccountIDRepository
import me.xhsun.gw2leo.account.datastore.entity.Character
import me.xhsun.gw2leo.account.service.IAccountService
import me.xhsun.gw2leo.account.service.ICharacterService
import me.xhsun.gw2leo.core.config.AUTH_BODY_FORMAT
import me.xhsun.gw2leo.core.config.BANK_STORAGE_KEY_FORMAT
import me.xhsun.gw2leo.core.datastore.IDatastoreRepository
import me.xhsun.gw2leo.core.http.IGW2Repository
import me.xhsun.gw2leo.core.http.IGW2RepositoryFactory
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

internal class AccountRefreshServiceTest {
    private val faker = Faker()
    private val dataFaker = DataFaker()
    private lateinit var datastoreMock: IDatastoreRepository
    private lateinit var accountServiceMock: IAccountService
    private lateinit var characterServiceMock: ICharacterService
    private lateinit var accountIDRepositoryMock: IAccountIDRepository
    private lateinit var gw2RepositoryFactoryMock: IGW2RepositoryFactory
    private lateinit var gw2RepositoryMock: IGW2Repository
    private lateinit var target: AccountRefreshService

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
        accountIDRepositoryMock = mockk()
        gw2RepositoryFactoryMock = mockk()
        gw2RepositoryMock = mockk()
        every { gw2RepositoryFactoryMock.gw2Repository() } returns gw2RepositoryMock
        target = AccountRefreshService(
            gw2RepositoryFactoryMock,
            datastoreMock,
            accountIDRepositoryMock,
            accountServiceMock,
            characterServiceMock
        )
    }

    @Test
    fun `initialize() should initialize new account`(): Unit = runBlocking {
        val inputAPI = faker.random.randomString()
        val inputDTO = dataFaker.accountDTOFaker()
        val inputCharacters = listOf(faker.random.randomString())
        val actualCharacters = slot<List<Character>>()

        coEvery { gw2RepositoryMock.getAccount(AUTH_BODY_FORMAT.format(inputAPI)) } returns inputDTO
        coEvery { gw2RepositoryMock.getAllCharacterName(AUTH_BODY_FORMAT.format(inputAPI)) } returns inputCharacters
        coEvery {
            datastoreMock.accountDAO.insertAll(*varargAll { it.api == inputAPI })
        } returns Unit
        coEvery { datastoreMock.characterDAO.insertAll(any()) } returns Unit
        every { accountIDRepositoryMock.updateCurrent(inputDTO.id) } returns true
        coEvery { accountServiceMock.sync() } returns Unit
        every { characterServiceMock.sync(capture(actualCharacters)) } returns Unit

        target.initialize(inputAPI)
        assertThat(actualCharacters.captured).hasSize(2).anyMatch { it.name == inputCharacters[0] }
    }

    @Test
    fun `update() should update all existing`(): Unit = runBlocking {
        val inputAccountDAO = listOf(dataFaker.accountDAOFaker(), dataFaker.accountDAOFaker())
        val nc1 = listOf(faker.random.randomString(), faker.random.randomString())
        val nc2 = listOf(faker.random.randomString())
        val inputOld = listOf(
            dataFaker.characterDAOFaker(accountID = inputAccountDAO[0].id),
            dataFaker.characterDAOFaker(
                accountID = inputAccountDAO[0].id,
                name = nc1[0]
            ),
            dataFaker.characterDAOFaker(
                accountID = inputAccountDAO[0].id,
                name = BANK_STORAGE_KEY_FORMAT.format(inputAccountDAO[0].id)
            )
        )

        val actualUpdate = slot<List<Character>>()
        val actualDelete = slot<List<Character>>()
        val actualInsert = slot<List<Character>>()

        every { accountServiceMock.accountID() } returns inputAccountDAO[0].id
        coEvery { datastoreMock.accountDAO.getAll() } returns inputAccountDAO
        coEvery { datastoreMock.characterDAO.getAll() } returns inputOld
        coEvery { gw2RepositoryMock.getAllCharacterName(AUTH_BODY_FORMAT.format(inputAccountDAO[0].api)) } returns nc1
        coEvery { gw2RepositoryMock.getAllCharacterName(AUTH_BODY_FORMAT.format(inputAccountDAO[1].api)) } returns nc2
        coEvery { datastoreMock.characterDAO.bulkDelete(capture(actualDelete)) } returns Unit
        coEvery { datastoreMock.characterDAO.bulkUpdate(capture(actualUpdate)) } returns Unit
        coEvery { datastoreMock.characterDAO.insertAll(capture(actualInsert)) } returns Unit
        coEvery { characterServiceMock.sync(any()) } returns Unit

        target.update()
        coVerify(exactly = 1) { characterServiceMock.sync(any()) }
        assertThat(actualDelete.captured).isNotNull.hasSize(1)
        assertThat(actualInsert.captured).isNotNull.hasSize(3)
        assertThat(actualUpdate.captured).isNotNull.hasSize(2)
    }

    @Test
    fun `update() should update without delete`(): Unit = runBlocking {
        val inputAccountDAO = listOf(dataFaker.accountDAOFaker())
        val nc = listOf(faker.random.randomString(), faker.random.randomString())
        val inputOld = listOf(
            dataFaker.characterDAOFaker(
                accountID = inputAccountDAO[0].id,
                name = nc[0]
            ),
            dataFaker.characterDAOFaker(
                accountID = inputAccountDAO[0].id,
                name = BANK_STORAGE_KEY_FORMAT.format(inputAccountDAO[0].id)
            )
        )

        val actualUpdate = slot<List<Character>>()
        val actualInsert = slot<List<Character>>()

        every { accountServiceMock.accountID() } returns inputAccountDAO[0].id
        coEvery { datastoreMock.accountDAO.getAll() } returns inputAccountDAO
        coEvery { datastoreMock.characterDAO.getAll() } returns inputOld
        coEvery { gw2RepositoryMock.getAllCharacterName(AUTH_BODY_FORMAT.format(inputAccountDAO[0].api)) } returns nc
        coEvery { datastoreMock.characterDAO.bulkUpdate(capture(actualUpdate)) } returns Unit
        coEvery { datastoreMock.characterDAO.insertAll(capture(actualInsert)) } returns Unit
        coEvery { characterServiceMock.sync(any()) } returns Unit

        target.update()
        coVerify(exactly = 1) { characterServiceMock.sync(any()) }
        assertThat(actualInsert.captured).isNotNull.hasSize(1)
        assertThat(actualUpdate.captured).isNotNull.hasSize(2)
    }

    @Test
    fun `update() should update without update`(): Unit = runBlocking {
        val inputAccountDAO = listOf(dataFaker.accountDAOFaker())
        val nc = listOf(faker.random.randomString(), faker.random.randomString())
        val inputOld = listOf(
            dataFaker.characterDAOFaker(accountID = inputAccountDAO[0].id)
        )

        val actualDelete = slot<List<Character>>()
        val actualInsert = slot<List<Character>>()

        every { accountServiceMock.accountID() } returns inputAccountDAO[0].id
        coEvery { datastoreMock.accountDAO.getAll() } returns inputAccountDAO
        coEvery { datastoreMock.characterDAO.getAll() } returns inputOld
        coEvery { gw2RepositoryMock.getAllCharacterName(AUTH_BODY_FORMAT.format(inputAccountDAO[0].api)) } returns nc
        coEvery { datastoreMock.characterDAO.bulkDelete(capture(actualDelete)) } returns Unit
        coEvery { datastoreMock.characterDAO.insertAll(capture(actualInsert)) } returns Unit
        coEvery { characterServiceMock.sync(any()) } returns Unit

        target.update()
        coVerify(exactly = 1) { characterServiceMock.sync(any()) }
        assertThat(actualInsert.captured).isNotNull.hasSize(3)
        assertThat(actualDelete.captured).isNotNull.hasSize(1)
    }

    @Test
    fun `update() should update without insert`(): Unit = runBlocking {
        val inputAccountDAO = listOf(dataFaker.accountDAOFaker())
        val nc = listOf(faker.random.randomString())
        val inputOld = listOf(
            dataFaker.characterDAOFaker(accountID = inputAccountDAO[0].id),
            dataFaker.characterDAOFaker(
                accountID = inputAccountDAO[0].id,
                name = nc[0]
            ),
            dataFaker.characterDAOFaker(
                accountID = inputAccountDAO[0].id,
                name = BANK_STORAGE_KEY_FORMAT.format(inputAccountDAO[0].id)
            )
        )

        val actualDelete = slot<List<Character>>()
        val actualUpdate = slot<List<Character>>()

        every { accountServiceMock.accountID() } returns inputAccountDAO[0].id
        coEvery { datastoreMock.accountDAO.getAll() } returns inputAccountDAO
        coEvery { datastoreMock.characterDAO.getAll() } returns inputOld
        coEvery { gw2RepositoryMock.getAllCharacterName(AUTH_BODY_FORMAT.format(inputAccountDAO[0].api)) } returns nc
        coEvery { datastoreMock.characterDAO.bulkDelete(capture(actualDelete)) } returns Unit
        coEvery { datastoreMock.characterDAO.bulkUpdate(capture(actualUpdate)) } returns Unit
        coEvery { characterServiceMock.sync(any()) } returns Unit

        target.update()
        coVerify(exactly = 1) { characterServiceMock.sync(any()) }
        assertThat(actualUpdate.captured).isNotNull.hasSize(2)
        assertThat(actualDelete.captured).isNotNull.hasSize(1)
    }
}