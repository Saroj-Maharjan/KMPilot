package thisissadeghi.sample.presentation.ui

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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import thisissadeghi.common.UiState
import thisissadeghi.designsystem.XButton
import thisissadeghi.designsystem.XCircularProgressIndicator
import thisissadeghi.designsystem.XIcon
import thisissadeghi.designsystem.XScaffold
import thisissadeghi.designsystem.XText
import thisissadeghi.sample.data.model.DashboardData
import thisissadeghi.sample.presentation.SampleUiModel
import thisissadeghi.sample.presentation.SampleViewModel
import thisissadeghi.sample.presentation.ui.components.BalanceCard
import thisissadeghi.sample.presentation.ui.components.BudgetsSection
import thisissadeghi.sample.presentation.ui.components.DashboardHeader
import thisissadeghi.sample.presentation.ui.components.InsightBanner
import thisissadeghi.sample.presentation.ui.components.MonthlySummaryCard
import thisissadeghi.sample.presentation.ui.components.PortfolioSection
import thisissadeghi.sample.presentation.ui.components.QuickActionsSection
import thisissadeghi.sample.presentation.ui.components.RecentTransactionsSection
import thisissadeghi.sample.presentation.ui.components.SavingsGoalsSection
import thisissadeghi.sample.presentation.ui.components.UpcomingBillsCard

@Composable
fun SampleScreen(
    viewModel: SampleViewModel,
    onActionClick: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    val uiState by viewModel.uiModelState.collectAsStateWithLifecycle()
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
        XCircularProgressIndicator()
    }
}

@Composable
private fun ErrorContent(
    onRetry: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier.padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        XIcon(
            imageVector = Icons.Filled.Warning,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.error,
            modifier = Modifier.size(80.dp),
        )
        Spacer(Modifier.height(32.dp))
        XText(
            "Something went wrong",
            fontSize = 20.sp,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
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
        Spacer(Modifier.height(32.dp))
        XButton(
            onClick = onRetry,
            modifier = Modifier.widthIn(max = 200.dp).height(56.dp),
            shape = RoundedCornerShape(12.dp),
        ) {
            XText("Retry", fontWeight = FontWeight.Bold, fontSize = 16.sp)
        }
    }
}
