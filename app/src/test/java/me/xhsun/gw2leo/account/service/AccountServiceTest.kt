package me.xhsun.gw2leo.account.service

import io.github.serpro69.kfaker.Faker
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import me.xhsun.gw2leo.DataFaker
import me.xhsun.gw2leo.account.datastore.IAccountIDRepository
import me.xhsun.gw2leo.account.error.NotLoggedInError
import me.xhsun.gw2leo.core.datastore.IDatastoreRepository
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

internal class AccountServiceTest {
    private val faker = Faker()
    private val dataFaker = DataFaker()
    private lateinit var accountID: String
    private lateinit var datastoreMock: IDatastoreRepository
    private lateinit var accountIDRepositoryMock: IAccountIDRepository

    @BeforeEach
    fun setUp() {
        accountID = faker.random.nextUUID()
        datastoreMock = mockk()
        accountIDRepositoryMock = mockk()

    }

    @Test
    fun `api() should return existing`(): Unit = runBlocking {
        val expected = faker.random.randomString()
        val input = dataFaker.accountDAOFaker(id = accountID, api = expected)

        every { accountIDRepositoryMock.getCurrent() } returns accountID
        every { datastoreMock.accountDAO.getByID(accountID) } returns input

        val target = AccountService(datastoreMock, accountIDRepositoryMock)
        target.sync()
        val actual = target.api()
        assertThat(actual).isEqualTo(expected)
    }

    @Test
    fun `api() should retrieve new`() {
        val expected = faker.random.randomString()
        val input = dataFaker.accountDAOFaker(id = accountID, api = expected)

        every { accountIDRepositoryMock.getCurrent() } returns accountID
        every { datastoreMock.accountDAO.getByID(accountID) } returns input

        val target = AccountService(datastoreMock, accountIDRepositoryMock)
        val actual = target.api()
        assertThat(actual).isEqualTo(expected)
    }

    @Test
    fun `api() should return error due to accountID not found`() {
        every { accountIDRepositoryMock.getCurrent() } returns ""

        val target = AccountService(datastoreMock, accountIDRepositoryMock)
        assertThrows<NotLoggedInError> {
            target.api()
        }
    }

    @Test
    fun `api() should return error due to account not found`() {
        every { accountIDRepositoryMock.getCurrent() } returns accountID
        every { datastoreMock.accountDAO.getByID(accountID) } returns null

        val target = AccountService(datastoreMock, accountIDRepositoryMock)
        assertThrows<NotLoggedInError> {
            target.api()
        }
    }

    @Test
    fun `accountID() should return existing`() {
        every { accountIDRepositoryMock.getCurrent() } returns accountID

        val target = AccountService(datastoreMock, accountIDRepositoryMock)
        val actual = target.accountID()
        assertThat(actual).isEqualTo(accountID)
    }

    @Test
    fun `accountID() should return error`() {
        every { accountIDRepositoryMock.getCurrent() } returns ""

        val target = AccountService(datastoreMock, accountIDRepositoryMock)
        assertThrows<NotLoggedInError> {
            target.accountID()
        }
    }

    @Test
    fun `sync() should set account info`(): Unit = runBlocking {
        val expected = dataFaker.accountDAOFaker(id = accountID)

        every { accountIDRepositoryMock.getCurrent() } returns accountID
        every { datastoreMock.accountDAO.getByID(accountID) } returns expected

        val target = AccountService(datastoreMock, accountIDRepositoryMock)

        target.sync()
        assertThat(target.api()).isEqualTo(expected.API)
        assertThat(target.accountID()).isEqualTo(accountID)
    }

    @Test
    fun `sync() should return error due to accountID not found`(): Unit = runBlocking {
        every { accountIDRepositoryMock.getCurrent() } returns ""

        val target = AccountService(datastoreMock, accountIDRepositoryMock)

        assertThrows<NotLoggedInError> {
            target.sync()
        }
    }

    @Test
    fun `sync() should return error due to account not found`(): Unit = runBlocking {
        every { accountIDRepositoryMock.getCurrent() } returns accountID
        every { datastoreMock.accountDAO.getByID(accountID) } returns null

        val target = AccountService(datastoreMock, accountIDRepositoryMock)

        assertThrows<NotLoggedInError> {
            target.sync()
        }
    }
}