package thisissadeghi.dashboard.presentation

import app.cash.turbine.test
import dev.mokkery.answering.returns
import dev.mokkery.answering.sequentiallyReturns
import dev.mokkery.everySuspend
import dev.mokkery.mock
import dev.mokkery.resetAnswers
import dev.mokkery.verifySuspend
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import thisissadeghi.common.UiState
import thisissadeghi.dashboard.data.repository.DashboardRepository
import thisissadeghi.dashboard.fixtures.DashboardFixtures
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

@OptIn(ExperimentalCoroutinesApi::class)
class DashboardViewModelTest {
    private val testDispatcher = StandardTestDispatcher()
    private val repository = mock<DashboardRepository>()
    private lateinit var viewModel: DashboardViewModel

    @BeforeTest
    fun setup() {
        Dispatchers.setMain(testDispatcher)
    }

    @AfterTest
    fun teardown() {
        Dispatchers.resetMain()
        resetAnswers(repository)
    }

    private fun createViewModel() {
        viewModel = DashboardViewModel(repository)
    }

    // === INITIAL STATE ===

    @Test
    fun `initial state is Uninitialized before init coroutine completes`() =
        runTest {
            everySuspend { repository.getDashboard() } returns DashboardFixtures.createSuccessDashboardData()

            createViewModel()

            viewModel.uiModelState.test {
                val first = awaitItem()
                // First emission is either Uninitialized or Loading (init immediately sets Loading)
                assertTrue(
                    first.dashboardState is UiState.Uninitialized || first.dashboardState is UiState.Loading,
                )
                cancelAndIgnoreRemainingEvents()
            }
        }

    @Test
    fun `init block triggers loadDashboard automatically`() =
        runTest {
            everySuspend { repository.getDashboard() } returns DashboardFixtures.createSuccessDashboardData()

            createViewModel()

            viewModel.uiModelState.test {
                // Consume initial states
                var current = awaitItem()
                if (current.dashboardState is UiState.Uninitialized) {
                    current = awaitItem()
                }
                // After consuming Uninitialized, Loading should be present
                assertTrue(current.dashboardState is UiState.Loading)

                advanceUntilIdle()

                current = awaitItem()
                assertTrue(current.dashboardState is UiState.Success)

                cancelAndIgnoreRemainingEvents()
            }
        }

    // === HAPPY PATH: Loading → Success ===

    @Test
    fun `loadDashboard emits Loading then Success on success`() =
        runTest {
            everySuspend { repository.getDashboard() } returns DashboardFixtures.createSuccessDashboardData()

            createViewModel()

            viewModel.uiModelState.test {
                var current = awaitItem()
                if (current.dashboardState is UiState.Uninitialized) {
                    current = awaitItem()
                }
                assertTrue(current.dashboardState is UiState.Loading)

                advanceUntilIdle()

                current = awaitItem()
                assertTrue(current.dashboardState is UiState.Success)

                cancelAndIgnoreRemainingEvents()
            }
        }

    @Test
    fun `loadDashboard Success state contains correct dashboard data`() =
        runTest {
            val expectedData = DashboardFixtures.createDashboardData()
            everySuspend { repository.getDashboard() } returns DashboardFixtures.createSuccessDashboardData(expectedData)

            createViewModel()

            viewModel.uiModelState.test {
                var current = awaitItem()
                if (current.dashboardState is UiState.Uninitialized) {
                    current = awaitItem()
                }

                advanceUntilIdle()

                current = awaitItem()
                val successState = current.dashboardState as UiState.Success
                assertEquals(expectedData, successState.value)

                cancelAndIgnoreRemainingEvents()
            }
        }

    @Test
    fun `loadDashboard Success state preserves account balance fields`() =
        runTest {
            val data = DashboardFixtures.createDashboardData()
            everySuspend { repository.getDashboard() } returns DashboardFixtures.createSuccessDashboardData(data)

            createViewModel()

            viewModel.uiModelState.test {
                var current = awaitItem()
                if (current.dashboardState is UiState.Uninitialized) {
                    current = awaitItem()
                }

                advanceUntilIdle()

                current = awaitItem()
                val successState = current.dashboardState as UiState.Success
                assertEquals(data.accountBalance, successState.value.accountBalance)
                assertEquals(data.monthlySummary, successState.value.monthlySummary)

                cancelAndIgnoreRemainingEvents()
            }
        }

    @Test
    fun `loadDashboard Success state preserves all list fields`() =
        runTest {
            val data = DashboardFixtures.createDashboardData()
            everySuspend { repository.getDashboard() } returns DashboardFixtures.createSuccessDashboardData(data)

            createViewModel()

            viewModel.uiModelState.test {
                var current = awaitItem()
                if (current.dashboardState is UiState.Uninitialized) {
                    current = awaitItem()
                }

                advanceUntilIdle()

                current = awaitItem()
                val successData = (current.dashboardState as UiState.Success).value
                assertEquals(data.recentTransactions, successData.recentTransactions)
                assertEquals(data.budgetCategories, successData.budgetCategories)
                assertEquals(data.savingsGoals, successData.savingsGoals)
                assertEquals(data.quickActions, successData.quickActions)
                assertEquals(data.upcomingBills, successData.upcomingBills)
                assertEquals(data.portfolioAssets, successData.portfolioAssets)

                cancelAndIgnoreRemainingEvents()
            }
        }

    @Test
    fun `loadDashboard Success state contains default transaction list of 3 items`() =
        runTest {
            everySuspend { repository.getDashboard() } returns DashboardFixtures.createSuccessDashboardData()

            createViewModel()

            viewModel.uiModelState.test {
                var current = awaitItem()
                if (current.dashboardState is UiState.Uninitialized) {
                    current = awaitItem()
                }

                advanceUntilIdle()

                current = awaitItem()
                val successData = (current.dashboardState as UiState.Success).value
                assertEquals(3, successData.recentTransactions.size)

                cancelAndIgnoreRemainingEvents()
            }
        }

    // === ERROR PATH: Loading → Failed ===

    @Test
    fun `loadDashboard emits Loading then Failed on network error`() =
        runTest {
            everySuspend { repository.getDashboard() } returns
                DashboardFixtures.createFailureDashboardData(DashboardFixtures.networkError)

            createViewModel()

            viewModel.uiModelState.test {
                skipItems(1) // Loading
                advanceUntilIdle()

                val failed = awaitItem()
                assertTrue(failed.dashboardState is UiState.Failed)

                cancelAndIgnoreRemainingEvents()
            }
        }

    @Test
    fun `loadDashboard emits Failed on server error`() =
        runTest {
            everySuspend { repository.getDashboard() } returns
                DashboardFixtures.createFailureDashboardData(DashboardFixtures.serverError)

            createViewModel()

            viewModel.uiModelState.test {
                skipItems(1) // Loading
                advanceUntilIdle()

                val failed = awaitItem()
                assertTrue(failed.dashboardState is UiState.Failed)

                cancelAndIgnoreRemainingEvents()
            }
        }

    @Test
    fun `loadDashboard emits Failed on unauthorized error`() =
        runTest {
            everySuspend { repository.getDashboard() } returns
                DashboardFixtures.createFailureDashboardData(DashboardFixtures.unauthorizedError)

            createViewModel()

            viewModel.uiModelState.test {
                skipItems(1) // Loading
                advanceUntilIdle()

                val failed = awaitItem()
                assertTrue(failed.dashboardState is UiState.Failed)

                cancelAndIgnoreRemainingEvents()
            }
        }

    @Test
    fun `loadDashboard emits Failed on timeout error`() =
        runTest {
            everySuspend { repository.getDashboard() } returns
                DashboardFixtures.createFailureDashboardData(DashboardFixtures.timeoutError)

            createViewModel()

            viewModel.uiModelState.test {
                skipItems(1) // Loading
                advanceUntilIdle()

                val failed = awaitItem()
                assertTrue(failed.dashboardState is UiState.Failed)

                cancelAndIgnoreRemainingEvents()
            }
        }

    @Test
    fun `loadDashboard Failed state contains correct error model`() =
        runTest {
            everySuspend { repository.getDashboard() } returns
                DashboardFixtures.createFailureDashboardData(DashboardFixtures.networkError)

            createViewModel()

            viewModel.uiModelState.test {
                skipItems(1) // Loading
                advanceUntilIdle()

                val failed = awaitItem()
                val failedState = failed.dashboardState
                assertTrue(failedState is UiState.Failed)
                assertNotNull(failedState.error)
                assertEquals(DashboardFixtures.networkError, failedState.error)

                cancelAndIgnoreRemainingEvents()
            }
        }

    // === RETRY FLOW ===

    @Test
    fun `retry after failure transitions to Loading then Success`() =
        runTest {
            everySuspend { repository.getDashboard() } sequentiallyReturns
                listOf(
                    DashboardFixtures.createFailureDashboardData(DashboardFixtures.networkError),
                    DashboardFixtures.createSuccessDashboardData(),
                )

            createViewModel()

            viewModel.uiModelState.test {
                var current = awaitItem()
                while (current.dashboardState !is UiState.Failed) {
                    advanceUntilIdle()
                    current = awaitItem()
                }

                viewModel.retry()
                advanceUntilIdle()

                current = expectMostRecentItem()
                assertTrue(current.dashboardState is UiState.Success)

                cancelAndIgnoreRemainingEvents()
            }
        }

    @Test
    fun `retry after failure goes through Loading before Success`() =
        runTest {
            everySuspend { repository.getDashboard() } sequentiallyReturns
                listOf(
                    DashboardFixtures.createFailureDashboardData(DashboardFixtures.networkError),
                    DashboardFixtures.createSuccessDashboardData(),
                )

            createViewModel()

            viewModel.uiModelState.test {
                var current = awaitItem()
                while (current.dashboardState !is UiState.Failed) {
                    advanceUntilIdle()
                    current = awaitItem()
                }

                viewModel.retry()
                current = awaitItem()
                assertTrue(current.dashboardState is UiState.Loading)

                advanceUntilIdle()

                current = awaitItem()
                assertTrue(current.dashboardState is UiState.Success)

                cancelAndIgnoreRemainingEvents()
            }
        }

    @Test
    fun `retry after failure can fail again`() =
        runTest {
            everySuspend { repository.getDashboard() } sequentiallyReturns
                listOf(
                    DashboardFixtures.createFailureDashboardData(DashboardFixtures.networkError),
                    DashboardFixtures.createFailureDashboardData(DashboardFixtures.serverError),
                )

            createViewModel()

            viewModel.uiModelState.test {
                var current = awaitItem()
                while (current.dashboardState !is UiState.Failed) {
                    advanceUntilIdle()
                    current = awaitItem()
                }

                viewModel.retry()
                advanceUntilIdle()

                current = expectMostRecentItem()
                assertTrue(current.dashboardState is UiState.Failed)

                cancelAndIgnoreRemainingEvents()
            }
        }

    // === REPOSITORY DELEGATION ===

    @Test
    fun `loadDashboard delegates to repository getDashboard`() =
        runTest {
            everySuspend { repository.getDashboard() } returns DashboardFixtures.createSuccessDashboardData()

            createViewModel()

            viewModel.uiModelState.test {
                skipItems(1)
                advanceUntilIdle()

                verifySuspend { repository.getDashboard() }

                cancelAndIgnoreRemainingEvents()
            }
        }

    @Test
    fun `retry delegates to repository getDashboard again`() =
        runTest {
            everySuspend { repository.getDashboard() } sequentiallyReturns
                listOf(
                    DashboardFixtures.createFailureDashboardData(DashboardFixtures.networkError),
                    DashboardFixtures.createSuccessDashboardData(),
                )

            createViewModel()

            viewModel.uiModelState.test {
                var current = awaitItem()
                while (current.dashboardState !is UiState.Failed) {
                    advanceUntilIdle()
                    current = awaitItem()
                }

                viewModel.retry()
                advanceUntilIdle()

                verifySuspend(
                    mode =
                        dev.mokkery.verify.VerifyMode
                            .exactly(2),
                ) {
                    repository.getDashboard()
                }

                cancelAndIgnoreRemainingEvents()
            }
        }

    // === EMPTY STATE ===

    @Test
    fun `loadDashboard Success state with empty lists is handled correctly`() =
        runTest {
            val emptyData = DashboardFixtures.createDashboardDataWithEmptyLists()
            everySuspend { repository.getDashboard() } returns DashboardFixtures.createSuccessDashboardData(emptyData)

            createViewModel()

            viewModel.uiModelState.test {
                var current = awaitItem()
                if (current.dashboardState is UiState.Uninitialized) {
                    current = awaitItem()
                }

                advanceUntilIdle()

                current = awaitItem()
                val successData = (current.dashboardState as UiState.Success).value
                assertTrue(successData.recentTransactions.isEmpty())
                assertTrue(successData.budgetCategories.isEmpty())
                assertTrue(successData.savingsGoals.isEmpty())
                assertTrue(successData.quickActions.isEmpty())
                assertTrue(successData.upcomingBills.isEmpty())
                assertTrue(successData.portfolioAssets.isEmpty())

                cancelAndIgnoreRemainingEvents()
            }
        }

    // === DATA INTEGRITY EDGE CASES ===

    @Test
    fun `loadDashboard handles data with negative account balance change`() =
        runTest {
            val data = DashboardFixtures.createDashboardDataWithNegativeAccountChange()
            everySuspend { repository.getDashboard() } returns DashboardFixtures.createSuccessDashboardData(data)

            createViewModel()

            viewModel.uiModelState.test {
                var current = awaitItem()
                if (current.dashboardState is UiState.Uninitialized) {
                    current = awaitItem()
                }

                advanceUntilIdle()

                current = awaitItem()
                val successData = (current.dashboardState as UiState.Success).value
                assertTrue(successData.accountBalance.changePercent < 0)
                assertTrue(successData.accountBalance.changeAmount < 0)

                cancelAndIgnoreRemainingEvents()
            }
        }

    @Test
    fun `loadDashboard handles data with overdue bills`() =
        runTest {
            val data = DashboardFixtures.createDashboardDataWithAllOverdueBills()
            everySuspend { repository.getDashboard() } returns DashboardFixtures.createSuccessDashboardData(data)

            createViewModel()

            viewModel.uiModelState.test {
                var current = awaitItem()
                if (current.dashboardState is UiState.Uninitialized) {
                    current = awaitItem()
                }

                advanceUntilIdle()

                current = awaitItem()
                val successData = (current.dashboardState as UiState.Success).value
                assertTrue(successData.upcomingBills.all { it.isOverdue })

                cancelAndIgnoreRemainingEvents()
            }
        }

    @Test
    fun `loadDashboard handles data with over-budget categories`() =
        runTest {
            val data = DashboardFixtures.createDashboardDataWithAllOverBudget()
            everySuspend { repository.getDashboard() } returns DashboardFixtures.createSuccessDashboardData(data)

            createViewModel()

            viewModel.uiModelState.test {
                var current = awaitItem()
                if (current.dashboardState is UiState.Uninitialized) {
                    current = awaitItem()
                }

                advanceUntilIdle()

                current = awaitItem()
                val successData = (current.dashboardState as UiState.Success).value
                assertTrue(successData.budgetCategories.all { it.spent > it.total })

                cancelAndIgnoreRemainingEvents()
            }
        }

    @Test
    fun `loadDashboard handles large collections`() =
        runTest {
            val data = DashboardFixtures.createDashboardDataWithLargeLists(100)
            everySuspend { repository.getDashboard() } returns DashboardFixtures.createSuccessDashboardData(data)

            createViewModel()

            viewModel.uiModelState.test {
                var current = awaitItem()
                if (current.dashboardState is UiState.Uninitialized) {
                    current = awaitItem()
                }

                advanceUntilIdle()

                current = awaitItem()
                val successData = (current.dashboardState as UiState.Success).value
                assertEquals(100, successData.recentTransactions.size)
                assertEquals(100, successData.budgetCategories.size)
                assertEquals(100, successData.savingsGoals.size)
                assertEquals(100, successData.upcomingBills.size)
                assertEquals(100, successData.portfolioAssets.size)

                cancelAndIgnoreRemainingEvents()
            }
        }
}
