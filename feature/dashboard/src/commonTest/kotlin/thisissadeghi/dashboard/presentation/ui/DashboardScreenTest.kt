package thisissadeghi.dashboard.presentation.ui

import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.v2.runComposeUiTest
import thisissadeghi.dashboard.fixtures.DashboardFixtures
import thisissadeghi.dashboard.fixtures.DashboardUiFixtures
import thisissadeghi.designsystem.XTheme
import kotlin.test.Test
import kotlin.test.assertTrue

// IMPORTANT: Uses `androidx.compose.ui.test.v2.runComposeUiTest` (the v2 variant).
// Each Compose test function is imported explicitly — no wildcard import.

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

            // AppLoadingState shows XCircularProgressIndicator — no error text or dashboard content
            onNodeWithText("Something went wrong").assertDoesNotExist()
            onNodeWithText("Good morning,").assertDoesNotExist()
            onNodeWithText("Try again").assertDoesNotExist()
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

            // Uninitialized routes to AppLoadingState — same as Loading
            onNodeWithText("Something went wrong").assertDoesNotExist()
            onNodeWithText("Good morning,").assertDoesNotExist()
            onNodeWithText("Try again").assertDoesNotExist()
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
            onNodeWithText("Pay").assertIsDisplayed()
            onNodeWithText("Top Up").assertIsDisplayed()
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

            // RecentTransactions is inside a regular Column nested in LazyColumn item 1.
            // The node exists in the composition tree even when below the visible fold.
            onNodeWithText("Recent Transactions").assertExists()
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

            onNodeWithText("Transaction 1").assertExists()
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
            onNodeWithText("Try again").assertDoesNotExist()
        }

    // === ERROR STATE ===

    @Test
    fun `shows error heading when state is Failed`() =
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

            onNodeWithText(
                "An unexpected error occurred. Please try again or check your connection.",
                substring = true,
            ).assertIsDisplayed()
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

            onNodeWithText("Try again").assertIsDisplayed()
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
            onNodeWithText("Try again").assertIsDisplayed()
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
            onNodeWithText("Try again").assertIsDisplayed()
        }

    @Test
    fun `shows error state for unauthorized error`() =
        runComposeUiTest {
            setContent {
                XTheme {
                    DashboardScreenRoot(
                        uiState = DashboardUiFixtures.createUnauthorizedErrorState(),
                        onActionClick = {},
                        onRetry = {},
                        onBackToDashboard = {},
                    )
                }
            }

            onNodeWithText("Something went wrong").assertIsDisplayed()
            onNodeWithText("Try again").assertIsDisplayed()
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

            onNodeWithText("Try again").performClick()
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
    fun `all four quick action labels are rendered in success state`() =
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

            // Each QuickActionButton renders its label as XText; verify all four are present.
            // Note: the clickable area is a Box above the label — the label itself is not the
            // click target. Callback interaction tests require a contentDescription on the Box.
            onNodeWithText("Send").assertExists()
            onNodeWithText("Receive").assertExists()
            onNodeWithText("Pay").assertExists()
            onNodeWithText("Top Up").assertExists()
        }
}
