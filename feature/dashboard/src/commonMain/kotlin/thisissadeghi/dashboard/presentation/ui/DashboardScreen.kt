package thisissadeghi.dashboard.presentation.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
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
import thisissadeghi.dashboard.presentation.ui.components.BalanceCard
import thisissadeghi.dashboard.presentation.ui.components.BudgetsSection
import thisissadeghi.dashboard.presentation.ui.components.DashboardHeader
import thisissadeghi.dashboard.presentation.ui.components.InsightBanner
import thisissadeghi.dashboard.presentation.ui.components.MonthlySummaryCard
import thisissadeghi.dashboard.presentation.ui.components.PortfolioSection
import thisissadeghi.dashboard.presentation.ui.components.QuickActionsSection
import thisissadeghi.dashboard.presentation.ui.components.RecentTransactionsSection
import thisissadeghi.dashboard.presentation.ui.components.SavingsGoalsSection
import thisissadeghi.dashboard.presentation.ui.components.UpcomingBillsCard
import thisissadeghi.designsystem.XButton
import thisissadeghi.designsystem.XCircularProgressIndicator
import thisissadeghi.designsystem.XIcon
import thisissadeghi.designsystem.XScaffold
import thisissadeghi.designsystem.XText
import thisissadeghi.designsystem.XTextButton
import thisissadeghi.designsystem.XTheme

@Composable
fun DashboardScreen(
    viewModel: DashboardViewModel,
    onActionClick: (String) -> Unit,
    onBackToDashboard: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val uiState by viewModel.uiModelState.collectAsStateWithLifecycle()
    DashboardScreenRoot(
        uiState = uiState,
        onActionClick = onActionClick,
        onRetry = viewModel::retry,
        onBackToDashboard = onBackToDashboard,
        modifier = modifier,
    )
}

@Composable
fun DashboardScreenRoot(
    uiState: DashboardUiModel,
    onActionClick: (String) -> Unit,
    onRetry: () -> Unit,
    onBackToDashboard: () -> Unit,
    modifier: Modifier = Modifier,
) {
    XScaffold(
        modifier = modifier,
        containerColor = MaterialTheme.colorScheme.background,
    ) { paddingValues ->
        when (val state = uiState.dashboardState) {
            UiState.Uninitialized, UiState.Loading ->
                LoadingContent(modifier = Modifier.padding(paddingValues).fillMaxSize())

            is UiState.Failed ->
                ErrorContent(
                    onRetry = onRetry,
                    onBackToDashboard = onBackToDashboard,
                    modifier = Modifier.padding(paddingValues).fillMaxSize(),
                )

            is UiState.Success ->
                DashboardContent(
                    data = state.value,
                    onActionClick = onActionClick,
                    modifier = Modifier.padding(paddingValues).fillMaxSize(),
                )
        }
    }
}

// ─── Dashboard Content ────────────────────────────────────────────────────────

@Composable
private fun DashboardContent(
    data: DashboardData,
    onActionClick: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier) {
        DashboardHeader()
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(start = 24.dp, end = 24.dp, bottom = 48.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp),
        ) {
            item { BalanceCard(data.accountBalance) }
            item { QuickActionsSection(data.quickActions, onActionClick) }
            item { InsightBanner(data.spendingInsight) }
            item { MonthlySummaryCard(data.monthlySummary) }
            item { BudgetsSection(data.budgetCategories) }
            item { SavingsGoalsSection(data.savingsGoals) }
            item { UpcomingBillsCard(data.upcomingBills) }
            item { PortfolioSection(data.portfolioAssets) }
            item { RecentTransactionsSection(data.recentTransactions) }
        }
    }
}

// ─── State Screens ────────────────────────────────────────────────────────────

@Composable
private fun LoadingContent(modifier: Modifier = Modifier) {
    Box(modifier = modifier, contentAlignment = Alignment.Center) {
        Box(
            modifier =
                Modifier
                    .size(96.dp)
                    .border(
                        width = 1.dp,
                        color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f),
                        shape = CircleShape,
                    ),
            contentAlignment = Alignment.Center,
        ) {
            Box(
                modifier =
                    Modifier
                        .size(64.dp)
                        .border(
                            width = 4.dp,
                            color = MaterialTheme.colorScheme.surfaceVariant,
                            shape = CircleShape,
                        ),
                contentAlignment = Alignment.Center,
            ) {
                XCircularProgressIndicator(
                    modifier = Modifier.size(64.dp),
                    color = MaterialTheme.colorScheme.primary.copy(alpha = 0.4f),
                    strokeWidth = 4.dp,
                    trackColor = Color.Transparent,
                )
                Box(
                    modifier =
                        Modifier
                            .size(8.dp)
                            .background(MaterialTheme.colorScheme.primary, CircleShape),
                )
            }
        }
    }
}

@Composable
private fun ErrorContent(
    onRetry: () -> Unit,
    onBackToDashboard: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier.padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Box(contentAlignment = Alignment.Center) {
            Box(
                modifier =
                    Modifier
                        .size(80.dp)
                        .background(
                            color = MaterialTheme.colorScheme.error.copy(alpha = 0.1f),
                            shape = CircleShape,
                        ),
            )
            XIcon(
                imageVector = Icons.Filled.Warning,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.error,
                modifier = Modifier.size(80.dp),
            )
        }
        Spacer(Modifier.height(32.dp))
        XText(
            "Something went wrong",
            fontSize = 20.sp,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            letterSpacing = (-0.5).sp,
            lineHeight = 32.5.sp,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth(),
        )
        Spacer(Modifier.height(8.dp))
        XText(
            "An unexpected error occurred. Please try again.",
            fontSize = 14.sp,
            color = MaterialTheme.colorScheme.outline,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth().widthIn(max = 240.dp),
        )
        XButton(
            onClick = onRetry,
            modifier = Modifier.widthIn(max = 200.dp).height(56.dp),
            shape = RoundedCornerShape(12.dp),
        ) {
            XText("Retry", fontWeight = FontWeight.Bold, fontSize = 16.sp)
        }
        Spacer(Modifier.height(16.dp))
        XTextButton(onClick = onBackToDashboard) {
            XText(
                "Return to Dashboard",
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
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
        )
    }
}
