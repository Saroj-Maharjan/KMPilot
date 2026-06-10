package thisissadeghi.dashboard.presentation.ui

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import kmpilot.feature.dashboard.generated.resources.Res
import kmpilot.feature.dashboard.generated.resources.error_message
import kmpilot.feature.dashboard.generated.resources.error_title
import kmpilot.feature.dashboard.generated.resources.return_to_dashboard
import org.jetbrains.compose.resources.stringResource
import thisissadeghi.common.ErrorModel
import thisissadeghi.common.UiState
import thisissadeghi.dashboard.data.model.AccountBalance
import thisissadeghi.dashboard.data.model.BudgetCategory
import thisissadeghi.dashboard.data.model.DashboardData
import thisissadeghi.dashboard.data.model.MonthlySummary
import thisissadeghi.dashboard.data.model.PortfolioAsset
import thisissadeghi.dashboard.data.model.QuickAction
import thisissadeghi.dashboard.data.model.SavingsGoal
import thisissadeghi.dashboard.data.model.SpendingInsight
import thisissadeghi.dashboard.data.model.Transaction
import thisissadeghi.dashboard.data.model.UpcomingBill
import thisissadeghi.dashboard.presentation.DashboardUiModel
import thisissadeghi.dashboard.presentation.DashboardViewModel
import thisissadeghi.dashboard.presentation.ui.components.DashboardContent
import thisissadeghi.designsystem.XScreen
import thisissadeghi.designsystem.XText
import thisissadeghi.designsystem.XTextButton
import thisissadeghi.designsystem.XTheme
import thisissadeghi.designsystem.app.AppErrorState
import thisissadeghi.designsystem.app.AppLoadingState

@Composable
fun DashboardScreen(
    viewModel: DashboardViewModel,
    onActionClick: (String) -> Unit,
    onBackToDashboard: () -> Unit,
    onAssetClick: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    val uiState by viewModel.uiModelState.collectAsStateWithLifecycle()
    DashboardScreenRoot(
        uiState = uiState,
        onActionClick = onActionClick,
        onRetry = viewModel::retry,
        onBackToDashboard = onBackToDashboard,
        onAssetClick = onAssetClick,
        modifier = modifier,
    )
}

@Composable
fun DashboardScreenRoot(
    uiState: DashboardUiModel,
    onActionClick: (String) -> Unit,
    onRetry: () -> Unit,
    onBackToDashboard: () -> Unit,
    onAssetClick: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    XScreen(
        modifier = modifier,
        containerColor = MaterialTheme.colorScheme.background,
    ) {
        when (val state = uiState.dashboardState) {
            UiState.Uninitialized, UiState.Loading ->
                AppLoadingState()

            is UiState.Failed ->
                AppErrorState(
                    title = stringResource(Res.string.error_title),
                    message = stringResource(Res.string.error_message),
                    onRetry = onRetry,
                    secondaryAction = {
                        XTextButton(onClick = onBackToDashboard) {
                            XText(
                                text = stringResource(Res.string.return_to_dashboard),
                                fontWeight = FontWeight.Medium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                            )
                        }
                    },
                )

            is UiState.Success ->
                DashboardContent(
                    data = state.value,
                    onActionClick = onActionClick,
                    onAssetClick = onAssetClick,
                )
        }
    }
}

// ─── Previews ─────────────────────────────────────────────────────────────────

private val sampleDashboardData =
    DashboardData(
        accountBalance =
            AccountBalance(
                totalBalance = 12_847.32,
                currency = "$",
                changePercent = 4.2,
                changeAmount = 516.18,
            ),
        monthlySummary =
            MonthlySummary(
                monthName = "May",
                income = 6_200.0,
                expenses = 3_840.0,
                currency = "$",
            ),
        recentTransactions =
            listOf(
                Transaction("1", "Salary", "Income", 4_200.0, true, "May 21", "$"),
                Transaction("2", "Whole Foods Market", "Food", 87.42, false, "May 20", "$"),
                Transaction("3", "Uber", "Transport", 18.5, false, "May 20", "$"),
            ),
        budgetCategories =
            listOf(
                BudgetCategory("Food", 320.0, 500.0, "$"),
                BudgetCategory("Transport", 145.0, 200.0, "$"),
                BudgetCategory("Entertainment", 220.0, 150.0, "$"),
                BudgetCategory("Shopping", 80.0, 300.0, "$"),
            ),
        savingsGoals =
            listOf(
                SavingsGoal("Vacation Fund", 1_400.0, 2_500.0, "$", "Aug 2026"),
                SavingsGoal("Emergency Fund", 3_800.0, 5_000.0, "$", "Dec 2026"),
            ),
        quickActions =
            listOf(
                QuickAction("send", "Send", "send"),
                QuickAction("receive", "Receive", "receive"),
                QuickAction("pay", "Pay", "pay"),
                QuickAction("topup", "Top up", "topup"),
                QuickAction("swap", "Swap", "swap"),
            ),
        upcomingBills =
            listOf(
                UpcomingBill("1", "Netflix", 15.49, "May 24", "$", false),
                UpcomingBill("2", "Internet", 59.99, "May 26", "$", false),
                UpcomingBill("3", "Electric Bill", 124.30, "May 19", "$", true),
            ),
        spendingInsight =
            SpendingInsight(
                message = "You're spending 12% less on food this month. Keep it up!",
                percentageChange = -12.0,
                isPositive = true,
            ),
        portfolioAssets =
            listOf(
                PortfolioAsset("btc", "Bitcoin", "BTC", 0.42, 28_400.0, 3.4, "$"),
                PortfolioAsset("eth", "Ethereum", "ETH", 1.85, 5_920.0, -1.2, "$"),
                PortfolioAsset("sol", "Solana", "SOL", 24.0, 1_440.0, 7.8, "$"),
            ),
    )

@Preview
@Composable
private fun DashboardScreenRootPreviewSuccess() {
    XTheme {
        DashboardScreenRoot(
            uiState = DashboardUiModel(dashboardState = UiState.Success(sampleDashboardData)),
            onActionClick = {},
            onRetry = {},
            onBackToDashboard = {},
            onAssetClick = {},
        )
    }
}

@Preview
@Composable
private fun DashboardScreenRootPreviewLoading() {
    XTheme {
        DashboardScreenRoot(
            uiState = DashboardUiModel(dashboardState = UiState.Loading),
            onActionClick = {},
            onRetry = {},
            onBackToDashboard = {},
            onAssetClick = {},
        )
    }
}

@Preview
@Composable
private fun DashboardScreenRootPreviewFailed() {
    XTheme {
        DashboardScreenRoot(
            uiState =
                DashboardUiModel(
                    dashboardState = UiState.Failed(ErrorModel.Message("Network unavailable")),
                ),
            onActionClick = {},
            onRetry = {},
            onBackToDashboard = {},
            onAssetClick = {},
        )
    }
}
