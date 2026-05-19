package thisissadeghi.dashboard.presentation.ui

import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.hasScrollToIndexAction
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollToIndex
import androidx.compose.ui.test.v2.runComposeUiTest
import thisissadeghi.dashboard.fixtures.DashboardFixtures
import thisissadeghi.dashboard.fixtures.DashboardUiFixtures
import thisissadeghi.designsystem.XTheme
import kotlin.test.Test
import kotlin.test.assertTrue

@OptIn(ExperimentalTestApi::class)
class DashboardScreenTest {
    // === LOADING STATE ===

    @Test
    fun `shows loading indicator when state is Loading`() =
        runComposeUiTest {
            setContent {
                XTheme {
                    DashboardScreenRoot(
                        uiState = DashboardUiFixtures.createLoadingState(),
                        onActionClick = {},
                        onRetry = {},
                        onBackToDashboard = {},
                    )
                }
            }

            // Loading renders LoadingContent — no error text or dashboard header visible
            onNodeWithText("Something went wrong").assertDoesNotExist()
            onNodeWithText("Good morning,").assertDoesNotExist()
            onNodeWithText("Retry").assertDoesNotExist()
        }

    @Test
    fun `shows loading indicator when state is Uninitialized`() =
        runComposeUiTest {
            setContent {
                XTheme {
                    DashboardScreenRoot(
                        uiState = DashboardUiFixtures.createUninitializedState(),
                        onActionClick = {},
                        onRetry = {},
                        onBackToDashboard = {},
                    )
                }
            }

            // Uninitialized and Loading both render LoadingContent — no error or content text visible
            onNodeWithText("Something went wrong").assertDoesNotExist()
            onNodeWithText("Good morning,").assertDoesNotExist()
            onNodeWithText("Retry").assertDoesNotExist()
        }

    // === SUCCESS STATE ===

    @Test
    fun `shows dashboard header when state is Success`() =
        runComposeUiTest {
            setContent {
                XTheme {
                    DashboardScreenRoot(
                        uiState = DashboardUiFixtures.createSuccessState(),
                        onActionClick = {},
                        onRetry = {},
                        onBackToDashboard = {},
                    )
                }
            }

            onNodeWithText("Good morning,").assertIsDisplayed()
            onNodeWithText("Dashboard").assertIsDisplayed()
        }

    @Test
    fun `shows total net worth label when state is Success`() =
        runComposeUiTest {
            setContent {
                XTheme {
                    DashboardScreenRoot(
                        uiState = DashboardUiFixtures.createSuccessState(),
                        onActionClick = {},
                        onRetry = {},
                        onBackToDashboard = {},
                    )
                }
            }

            onNodeWithText("TOTAL NET WORTH").assertIsDisplayed()
        }

    @Test
    fun `shows account balance value when state is Success`() =
        runComposeUiTest {
            val data =
                DashboardFixtures.createDashboardData(
                    accountBalance = DashboardFixtures.createAccountBalance(totalBalance = 12_450.75),
                )

            setContent {
                XTheme {
                    DashboardScreenRoot(
                        uiState = DashboardUiFixtures.createSuccessState(data),
                        onActionClick = {},
                        onRetry = {},
                        onBackToDashboard = {},
                    )
                }
            }

            onNodeWithText("\$12,450.75").assertIsDisplayed()
        }

    @Test
    fun `shows recent transactions section header when state is Success`() =
        runComposeUiTest {
            setContent {
                XTheme {
                    DashboardScreenRoot(
                        uiState = DashboardUiFixtures.createSuccessState(),
                        onActionClick = {},
                        onRetry = {},
                        onBackToDashboard = {},
                    )
                }
            }

            // RecentTransactionsSection is item index 8 in the LazyColumn — scroll to it first
            onNode(hasScrollToIndexAction()).performScrollToIndex(8)
            onNodeWithText("Recent Transactions").assertIsDisplayed()
        }

    @Test
    fun `shows transaction titles when state is Success`() =
        runComposeUiTest {
            val data =
                DashboardFixtures.createDashboardData(
                    recentTransactions = DashboardFixtures.createTransactionList(3),
                )

            setContent {
                XTheme {
                    DashboardScreenRoot(
                        uiState = DashboardUiFixtures.createSuccessState(data),
                        onActionClick = {},
                        onRetry = {},
                        onBackToDashboard = {},
                    )
                }
            }

            // RecentTransactionsSection is item index 8 in the LazyColumn — scroll to it first
            onNode(hasScrollToIndexAction()).performScrollToIndex(8)
            onNodeWithText("Transaction 1").assertIsDisplayed()
        }

    @Test
    fun `shows quick action labels when state is Success`() =
        runComposeUiTest {
            val data =
                DashboardFixtures.createDashboardData(
                    quickActions = DashboardFixtures.createQuickActionList(4),
                )

            setContent {
                XTheme {
                    DashboardScreenRoot(
                        uiState = DashboardUiFixtures.createSuccessState(data),
                        onActionClick = {},
                        onRetry = {},
                        onBackToDashboard = {},
                    )
                }
            }

            onNodeWithText("Send").assertIsDisplayed()
            onNodeWithText("Receive").assertIsDisplayed()
        }

    @Test
    fun `does not show error content when state is Success`() =
        runComposeUiTest {
            setContent {
                XTheme {
                    DashboardScreenRoot(
                        uiState = DashboardUiFixtures.createSuccessState(),
                        onActionClick = {},
                        onRetry = {},
                        onBackToDashboard = {},
                    )
                }
            }

            onNodeWithText("Something went wrong").assertDoesNotExist()
            onNodeWithText("Retry").assertDoesNotExist()
        }

    // === ERROR STATE ===

    @Test
    fun `shows error message when state is Failed`() =
        runComposeUiTest {
            setContent {
                XTheme {
                    DashboardScreenRoot(
                        uiState = DashboardUiFixtures.createErrorState(),
                        onActionClick = {},
                        onRetry = {},
                        onBackToDashboard = {},
                    )
                }
            }

            onNodeWithText("Something went wrong").assertIsDisplayed()
        }

    @Test
    fun `shows error description when state is Failed`() =
        runComposeUiTest {
            setContent {
                XTheme {
                    DashboardScreenRoot(
                        uiState = DashboardUiFixtures.createErrorState(),
                        onActionClick = {},
                        onRetry = {},
                        onBackToDashboard = {},
                    )
                }
            }

            onNodeWithText("An unexpected error occurred. Please try again.").assertIsDisplayed()
        }

    @Test
    fun `shows Retry button when state is Failed`() =
        runComposeUiTest {
            setContent {
                XTheme {
                    DashboardScreenRoot(
                        uiState = DashboardUiFixtures.createErrorState(),
                        onActionClick = {},
                        onRetry = {},
                        onBackToDashboard = {},
                    )
                }
            }

            onNodeWithText("Retry").assertIsDisplayed()
        }

    @Test
    fun `shows Return to Dashboard button when state is Failed`() =
        runComposeUiTest {
            setContent {
                XTheme {
                    DashboardScreenRoot(
                        uiState = DashboardUiFixtures.createErrorState(),
                        onActionClick = {},
                        onRetry = {},
                        onBackToDashboard = {},
                    )
                }
            }

            onNodeWithText("Return to Dashboard").assertIsDisplayed()
        }

    @Test
    fun `shows error state for network error`() =
        runComposeUiTest {
            setContent {
                XTheme {
                    DashboardScreenRoot(
                        uiState = DashboardUiFixtures.createNetworkErrorState(),
                        onActionClick = {},
                        onRetry = {},
                        onBackToDashboard = {},
                    )
                }
            }

            onNodeWithText("Something went wrong").assertIsDisplayed()
            onNodeWithText("Retry").assertIsDisplayed()
        }

    @Test
    fun `shows error state for server error`() =
        runComposeUiTest {
            setContent {
                XTheme {
                    DashboardScreenRoot(
                        uiState = DashboardUiFixtures.createServerErrorState(),
                        onActionClick = {},
                        onRetry = {},
                        onBackToDashboard = {},
                    )
                }
            }

            onNodeWithText("Something went wrong").assertIsDisplayed()
            onNodeWithText("Retry").assertIsDisplayed()
        }

    @Test
    fun `does not show dashboard content when state is Failed`() =
        runComposeUiTest {
            setContent {
                XTheme {
                    DashboardScreenRoot(
                        uiState = DashboardUiFixtures.createErrorState(),
                        onActionClick = {},
                        onRetry = {},
                        onBackToDashboard = {},
                    )
                }
            }

            onNodeWithText("Good morning,").assertDoesNotExist()
            onNodeWithText("TOTAL NET WORTH").assertDoesNotExist()
        }

    // === USER INTERACTIONS ===

    @Test
    fun `retry button invokes onRetry callback`() =
        runComposeUiTest {
            var retryCalled = false

            setContent {
                XTheme {
                    DashboardScreenRoot(
                        uiState = DashboardUiFixtures.createErrorState(),
                        onActionClick = {},
                        onRetry = { retryCalled = true },
                        onBackToDashboard = {},
                    )
                }
            }

            onNodeWithText("Retry").performClick()
            assertTrue(retryCalled)
        }

    @Test
    fun `return to dashboard button invokes onBackToDashboard callback`() =
        runComposeUiTest {
            var backCalled = false

            setContent {
                XTheme {
                    DashboardScreenRoot(
                        uiState = DashboardUiFixtures.createErrorState(),
                        onActionClick = {},
                        onRetry = {},
                        onBackToDashboard = { backCalled = true },
                    )
                }
            }

            onNodeWithText("Return to Dashboard").performClick()
            assertTrue(backCalled)
        }

    @Test
    fun `quick action click invokes onActionClick with correct action id`() =
        runComposeUiTest {
            val actions = DashboardFixtures.createQuickActionList(4)
            var clickedActionId: String? = null

            setContent {
                XTheme {
                    DashboardScreenRoot(
                        uiState =
                            DashboardUiFixtures.createSuccessState(
                                DashboardFixtures.createDashboardData(quickActions = actions),
                            ),
                        onActionClick = { id -> clickedActionId = id },
                        onRetry = {},
                        onBackToDashboard = {},
                    )
                }
            }

            // The icon box is clickable and carries the action label as contentDescription
            onNodeWithContentDescription(actions[0].label).performClick()
            assertTrue(clickedActionId == actions[0].id)
        }
}
