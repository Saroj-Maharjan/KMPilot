package thisissadeghi.dashboard.data

import dev.mokkery.answering.returns
import dev.mokkery.everySuspend
import dev.mokkery.mock
import dev.mokkery.resetAnswers
import dev.mokkery.verify.VerifyMode
import dev.mokkery.verifySuspend
import kotlinx.coroutines.test.runTest
import thisissadeghi.common.Either
import thisissadeghi.dashboard.data.datasource.DashboardRemoteDataSource
import thisissadeghi.dashboard.data.repository.DashboardRepositoryImpl
import thisissadeghi.dashboard.fixtures.DashboardFixtures
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class DashboardRepositoryImplTest {
    private val remoteDataSource = mock<DashboardRemoteDataSource>()
    private lateinit var repository: DashboardRepositoryImpl

    @BeforeTest
    fun setup() {
        repository = DashboardRepositoryImpl(remoteDataSource = remoteDataSource)
    }

    @AfterTest
    fun teardown() {
        resetAnswers(remoteDataSource)
    }

    // === SUCCESS CASES ===

    @Test
    fun `getDashboard returns dashboard data on success`() =
        runTest {
            val data = DashboardFixtures.createDashboardData()
            everySuspend { remoteDataSource.getDashboard() } returns Either.Success(data)

            val result = repository.getDashboard()

            assertTrue(result is Either.Success)
            assertEquals(data, result.data)
        }

    @Test
    fun `getDashboard returns all fields unchanged from remote data source`() =
        runTest {
            val data = DashboardFixtures.createDashboardData()
            everySuspend { remoteDataSource.getDashboard() } returns Either.Success(data)

            val result = repository.getDashboard()

            assertTrue(result is Either.Success)
            assertEquals(data.accountBalance, result.data.accountBalance)
            assertEquals(data.monthlySummary, result.data.monthlySummary)
            assertEquals(data.recentTransactions, result.data.recentTransactions)
            assertEquals(data.budgetCategories, result.data.budgetCategories)
            assertEquals(data.savingsGoals, result.data.savingsGoals)
            assertEquals(data.quickActions, result.data.quickActions)
            assertEquals(data.upcomingBills, result.data.upcomingBills)
            assertEquals(data.spendingInsight, result.data.spendingInsight)
            assertEquals(data.portfolioAssets, result.data.portfolioAssets)
        }

    @Test
    fun `getDashboard returns data with empty lists when response contains empty collections`() =
        runTest {
            val data = DashboardFixtures.createDashboardDataWithEmptyLists()
            everySuspend { remoteDataSource.getDashboard() } returns Either.Success(data)

            val result = repository.getDashboard()

            assertTrue(result is Either.Success)
            assertTrue(result.data.recentTransactions.isEmpty())
            assertTrue(result.data.budgetCategories.isEmpty())
            assertTrue(result.data.savingsGoals.isEmpty())
            assertTrue(result.data.quickActions.isEmpty())
            assertTrue(result.data.upcomingBills.isEmpty())
            assertTrue(result.data.portfolioAssets.isEmpty())
        }

    @Test
    fun `getDashboard returns data with single items in all collections`() =
        runTest {
            val data = DashboardFixtures.createDashboardDataWithSingleItems()
            everySuspend { remoteDataSource.getDashboard() } returns Either.Success(data)

            val result = repository.getDashboard()

            assertTrue(result is Either.Success)
            assertEquals(1, result.data.recentTransactions.size)
            assertEquals(1, result.data.budgetCategories.size)
            assertEquals(1, result.data.savingsGoals.size)
            assertEquals(1, result.data.quickActions.size)
            assertEquals(1, result.data.upcomingBills.size)
            assertEquals(1, result.data.portfolioAssets.size)
        }

    @Test
    fun `getDashboard returns data with large collections`() =
        runTest {
            val data = DashboardFixtures.createDashboardDataWithLargeLists(100)
            everySuspend { remoteDataSource.getDashboard() } returns Either.Success(data)

            val result = repository.getDashboard()

            assertTrue(result is Either.Success)
            assertEquals(100, result.data.recentTransactions.size)
            assertEquals(100, result.data.budgetCategories.size)
            assertEquals(100, result.data.savingsGoals.size)
            assertEquals(100, result.data.upcomingBills.size)
            assertEquals(100, result.data.portfolioAssets.size)
        }

    @Test
    fun `getDashboard returns data with negative account balance change`() =
        runTest {
            val data = DashboardFixtures.createDashboardDataWithNegativeAccountChange()
            everySuspend { remoteDataSource.getDashboard() } returns Either.Success(data)

            val result = repository.getDashboard()

            assertTrue(result is Either.Success)
            assertTrue(result.data.accountBalance.changePercent < 0)
            assertTrue(result.data.accountBalance.changeAmount < 0)
        }

    @Test
    fun `getDashboard returns data with overdue bills`() =
        runTest {
            val data = DashboardFixtures.createDashboardDataWithAllOverdueBills()
            everySuspend { remoteDataSource.getDashboard() } returns Either.Success(data)

            val result = repository.getDashboard()

            assertTrue(result is Either.Success)
            assertTrue(result.data.upcomingBills.all { it.isOverdue })
        }

    @Test
    fun `getDashboard returns data with over-budget categories`() =
        runTest {
            val data = DashboardFixtures.createDashboardDataWithAllOverBudget()
            everySuspend { remoteDataSource.getDashboard() } returns Either.Success(data)

            val result = repository.getDashboard()

            assertTrue(result is Either.Success)
            assertTrue(result.data.budgetCategories.all { it.spent > it.total })
        }

    // === ERROR PROPAGATION ===

    @Test
    fun `getDashboard propagates network failure`() =
        runTest {
            everySuspend { remoteDataSource.getDashboard() } returns Either.Failure(DashboardFixtures.networkError)

            val result = repository.getDashboard()

            assertTrue(result is Either.Failure)
            assertEquals(DashboardFixtures.networkError, result.error)
        }

    @Test
    fun `getDashboard propagates unauthorized error`() =
        runTest {
            everySuspend { remoteDataSource.getDashboard() } returns Either.Failure(DashboardFixtures.unauthorizedError)

            val result = repository.getDashboard()

            assertTrue(result is Either.Failure)
            assertEquals(DashboardFixtures.unauthorizedError, result.error)
        }

    @Test
    fun `getDashboard propagates server error`() =
        runTest {
            everySuspend { remoteDataSource.getDashboard() } returns Either.Failure(DashboardFixtures.serverError)

            val result = repository.getDashboard()

            assertTrue(result is Either.Failure)
            assertEquals(DashboardFixtures.serverError, result.error)
        }

    @Test
    fun `getDashboard propagates not found error`() =
        runTest {
            everySuspend { remoteDataSource.getDashboard() } returns Either.Failure(DashboardFixtures.notFoundError)

            val result = repository.getDashboard()

            assertTrue(result is Either.Failure)
            assertEquals(DashboardFixtures.notFoundError, result.error)
        }

    @Test
    fun `getDashboard propagates bad request error`() =
        runTest {
            everySuspend { remoteDataSource.getDashboard() } returns Either.Failure(DashboardFixtures.badRequestError)

            val result = repository.getDashboard()

            assertTrue(result is Either.Failure)
            assertEquals(DashboardFixtures.badRequestError, result.error)
        }

    @Test
    fun `getDashboard propagates timeout error`() =
        runTest {
            everySuspend { remoteDataSource.getDashboard() } returns Either.Failure(DashboardFixtures.timeoutError)

            val result = repository.getDashboard()

            assertTrue(result is Either.Failure)
            assertEquals(DashboardFixtures.timeoutError, result.error)
        }

    @Test
    fun `getDashboard propagates service unavailable error`() =
        runTest {
            everySuspend { remoteDataSource.getDashboard() } returns Either.Failure(DashboardFixtures.serviceUnavailableError)

            val result = repository.getDashboard()

            assertTrue(result is Either.Failure)
            assertEquals(DashboardFixtures.serviceUnavailableError, result.error)
        }

    @Test
    fun `getDashboard propagates serialization error`() =
        runTest {
            everySuspend { remoteDataSource.getDashboard() } returns Either.Failure(DashboardFixtures.serializationError)

            val result = repository.getDashboard()

            assertTrue(result is Either.Failure)
            assertEquals(DashboardFixtures.serializationError, result.error)
        }

    // === DELEGATION VERIFICATION ===

    @Test
    fun `getDashboard delegates to remote data source`() =
        runTest {
            val data = DashboardFixtures.createDashboardData()
            everySuspend { remoteDataSource.getDashboard() } returns Either.Success(data)

            repository.getDashboard()

            verifySuspend { remoteDataSource.getDashboard() }
        }

    @Test
    fun `getDashboard calls remote data source exactly once`() =
        runTest {
            val data = DashboardFixtures.createDashboardData()
            everySuspend { remoteDataSource.getDashboard() } returns Either.Success(data)

            repository.getDashboard()

            verifySuspend(VerifyMode.exactly(1)) { remoteDataSource.getDashboard() }
        }

    @Test
    fun `getDashboard delegates to remote data source on failure`() =
        runTest {
            everySuspend { remoteDataSource.getDashboard() } returns Either.Failure(DashboardFixtures.networkError)

            repository.getDashboard()

            verifySuspend { remoteDataSource.getDashboard() }
        }
}
