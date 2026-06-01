package thisissadeghi.dashboard.presentation.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kmpilot.feature.dashboard.generated.resources.Res
import kmpilot.feature.dashboard.generated.resources.dashboard_title
import kmpilot.feature.dashboard.generated.resources.greeting
import org.jetbrains.compose.resources.stringResource
import thisissadeghi.dashboard.data.model.DashboardData
import thisissadeghi.designsystem.XText
import thisissadeghi.designsystem.XTheme

@Composable
internal fun DashboardContent(
    data: DashboardData,
    onActionClick: (String) -> Unit,
    paddingValues: PaddingValues,
    modifier: Modifier = Modifier,
) {
    LazyColumn(
        modifier = modifier.fillMaxSize().padding(paddingValues),
        contentPadding = PaddingValues(bottom = 48.dp),
    ) {
        item { DashboardHeader() }
        item {
            Column(
                modifier = Modifier.padding(horizontal = 24.dp),
                verticalArrangement = Arrangement.spacedBy(24.dp),
            ) {
                BalanceCard(data.accountBalance)
                QuickActions(
                    onSendClick = { onActionClick("send") },
                    onReceiveClick = { onActionClick("receive") },
                    onPayClick = { onActionClick("pay") },
                    onTopUpClick = { onActionClick("topup") },
                )
                InsightBanner(data.spendingInsight)
                MonthlySummary(data.monthlySummary)
                MonthlyBudgets(data.budgetCategories)
                SavingsGoals(data.savingsGoals)
                UpcomingBills(data.upcomingBills)
                Portfolio(data.portfolioAssets)
                RecentTransactions(data.recentTransactions)
            }
        }
    }
}

@Composable
private fun DashboardHeader() {
    Row(
        modifier =
            Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.background)
                .padding(horizontal = 24.dp, vertical = 16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Column {
            XText(
                stringResource(Res.string.greeting),
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
            )
            XText(
                stringResource(Res.string.dashboard_title),
                color = MaterialTheme.colorScheme.onSurface,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = (-0.5).sp,
            )
        }
    }
}

@Preview
@Composable
private fun DashboardHeaderPreview() {
    XTheme { DashboardHeader() }
}
