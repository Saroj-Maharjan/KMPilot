package thisissadeghi.sample.presentation.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Error
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import thisissadeghi.common.UiState
import thisissadeghi.common.asString
import thisissadeghi.designsystem.XButton
import thisissadeghi.designsystem.XCard
import thisissadeghi.designsystem.XCircularProgressIndicator
import thisissadeghi.designsystem.XIcon
import thisissadeghi.designsystem.XText
import thisissadeghi.sample.data.model.DashboardData
import thisissadeghi.sample.presentation.SampleUiModel
import thisissadeghi.sample.presentation.SampleViewModel
import thisissadeghi.sample.presentation.ui.components.BudgetsSection
import thisissadeghi.sample.presentation.ui.components.DashboardHeader
import thisissadeghi.sample.presentation.ui.components.MonthlySummarySection
import thisissadeghi.sample.presentation.ui.components.NetWorthCard
import thisissadeghi.sample.presentation.ui.components.PortfolioSection
import thisissadeghi.sample.presentation.ui.components.QuickActionsRow
import thisissadeghi.sample.presentation.ui.components.RecentTransactionsSection
import thisissadeghi.sample.presentation.ui.components.SavingsGoalsSection
import thisissadeghi.sample.presentation.ui.components.SmartInsightBanner
import thisissadeghi.sample.presentation.ui.components.UpcomingBillsSection

@Composable
fun SampleScreen(
    viewModel: SampleViewModel,
    onActionClick: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    val uiState by viewModel.uiModelState.collectAsState()
    SampleScreenRoot(
        uiState = uiState,
        onActionClick = onActionClick,
        onRetry = viewModel::retry,
        modifier = modifier,
    )
}

@Composable
fun SampleScreenRoot(
    uiState: SampleUiModel,
    onActionClick: (String) -> Unit,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier =
            modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .statusBarsPadding(),
    ) {
        DashboardHeader()
        when (val state = uiState.dashboardState) {
            UiState.Uninitialized, UiState.Loading ->
                LoadingContent(modifier = Modifier.fillMaxSize())

            is UiState.Success ->
                DashboardContent(
                    data = state.value,
                    onActionClick = onActionClick,
                    modifier = Modifier.fillMaxSize(),
                )

            is UiState.Failed ->
                ErrorContent(
                    error = state.error.asString(),
                    onRetry = onRetry,
                    modifier = Modifier.fillMaxSize(),
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
    LazyColumn(
        modifier = modifier.navigationBarsPadding(),
        contentPadding = PaddingValues(start = 16.dp, end = 16.dp, bottom = 48.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp),
    ) {
        item { NetWorthCard(data.accountBalance.totalBalance, data.accountBalance.changePercent) }
        item { QuickActionsRow(data.quickActions, onActionClick) }
        item { SmartInsightBanner(data.spendingInsight.message) }
        item { MonthlySummarySection(data.monthlySummary.income, data.monthlySummary.expenses) }
        item { BudgetsSection(data.budgetCategories) }
        item { SavingsGoalsSection(data.savingsGoals) }
        item { UpcomingBillsSection(data.upcomingBills) }
        item { PortfolioSection(data.portfolioAssets) }
        item { RecentTransactionsSection(data.recentTransactions) }
    }
}

// ─── State Screens ────────────────────────────────────────────────────────────

@Composable
private fun LoadingContent(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier.navigationBarsPadding(),
        contentAlignment = Alignment.Center,
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(24.dp),
        ) {
            XCircularProgressIndicator(
                modifier = Modifier.size(48.dp),
                strokeWidth = 4.dp,
                color = MaterialTheme.colorScheme.primary,
                trackColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.20f),
            )
            XText(
                text = "Loading dashboard...",
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                letterSpacing = 0.5.sp,
            )
        }
    }
}

@Composable
private fun ErrorContent(
    error: String,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier =
            modifier
                .navigationBarsPadding()
                .padding(horizontal = 24.dp),
        contentAlignment = Alignment.Center,
    ) {
        XCard(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            shape = RoundedCornerShape(12.dp),
            elevation = CardDefaults.cardElevation(0.dp),
        ) {
            Column {
                Box(
                    modifier =
                        Modifier
                            .fillMaxWidth()
                            .height(3.dp)
                            .background(MaterialTheme.colorScheme.error),
                )
                Column(
                    modifier = Modifier.padding(32.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    XIcon(
                        imageVector = Icons.Filled.Error,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.error,
                        modifier = Modifier.size(24.dp),
                    )
                    Spacer(Modifier.height(16.dp))
                    XText(
                        text = "Something went wrong",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth(),
                    )
                    Spacer(Modifier.height(8.dp))
                    XText(
                        text = error,
                        fontSize = 14.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth(),
                    )
                    Spacer(Modifier.height(32.dp))
                    XButton(
                        onClick = onRetry,
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors =
                            ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.primary,
                                contentColor = MaterialTheme.colorScheme.onPrimary,
                            ),
                    ) {
                        XText(
                            text = "Try Again",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                        )
                    }
                }
            }
        }
    }
}
