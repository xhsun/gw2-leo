package me.xhsun.gw2leo.account.service

import io.github.serpro69.kfaker.Faker
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import me.xhsun.gw2leo.DataFaker
import me.xhsun.gw2leo.account.error.NotLoggedInError
import me.xhsun.gw2leo.core.datastore.IDatastoreRepository
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

internal class CharacterServiceTest {
    private val faker = Faker()
    private val dataFaker = DataFaker()
    private lateinit var accountID: String
    private lateinit var datastoreMock: IDatastoreRepository
    private lateinit var accountServiceMock: IAccountService

    @BeforeEach
    fun setUp() {
        accountID = faker.random.nextUUID()
        datastoreMock = mockk()
        accountServiceMock = mockk()
    }

    @Test
    fun `characters() should retrieve new`(): Unit = runBlocking {
        val expected = faker.random.randomString()
        val input = listOf(
            dataFaker.characterDAOFaker(expected, accountID)
        )

        every { accountServiceMock.accountID() } returns accountID
        coEvery { datastoreMock.characterDAO.getAll(accountID) } returns input

        val target = CharacterService(datastoreMock, accountServiceMock)
        val actual = target.characters()
        assertThat(actual).singleElement().isEqualTo(expected)
    }

    @Test
    fun `sync() should update and characters() should return existing`(): Unit = runBlocking {
        val expected = faker.random.randomString()
        val input = listOf(
            dataFaker.characterDAOFaker(expected, accountID)
        )

        every { accountServiceMock.accountID() } returns accountID

        val target = CharacterService(datastoreMock, accountServiceMock)
        target.sync(input)
        val actual = target.characters()
        assertThat(actual).singleElement().isEqualTo(expected)
    }

    @Test
    fun `characters() should throw error due to accountID not found`(): Unit = runBlocking {
        every { accountServiceMock.accountID() } throws NotLoggedInError()

        val target = CharacterService(datastoreMock, accountServiceMock)
        assertThrows<NotLoggedInError> {
            target.characters()
        }
    }

    @Test
    fun `characters() should throw error due to characters not found`(): Unit = runBlocking {
        every { accountServiceMock.accountID() } returns accountID
        coEvery { datastoreMock.characterDAO.getAll(accountID) } returns emptyList()

        val target = CharacterService(datastoreMock, accountServiceMock)
        assertThrows<NotLoggedInError> {
            target.characters()
        }
    }

    @Test
    fun `sync() should return error due to accountID not found`(): Unit = runBlocking {
        val expected = faker.random.randomString()
        val input = listOf(
            dataFaker.characterDAOFaker(expected, accountID)
        )

        every { accountServiceMock.accountID() } throws NotLoggedInError()

        val target = CharacterService(datastoreMock, accountServiceMock)
        assertThrows<NotLoggedInError> {
            target.sync(input)
        }
    }
}