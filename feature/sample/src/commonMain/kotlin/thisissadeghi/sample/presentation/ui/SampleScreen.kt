package thisissadeghi.sample.presentation.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.automirrored.filled.TrendingUp
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.CallReceived
import androidx.compose.material.icons.filled.ElectricBolt
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.LocalCafe
import androidx.compose.material.icons.filled.Payments
import androidx.compose.material.icons.filled.Receipt
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material.icons.filled.Wifi
import androidx.compose.material.icons.filled.Work
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import thisissadeghi.common.UiState
import thisissadeghi.common.asString
import thisissadeghi.designsystem.XButton
import thisissadeghi.designsystem.XCard
import thisissadeghi.designsystem.XCircularProgressIndicator
import thisissadeghi.designsystem.XHorizontalDivider
import thisissadeghi.designsystem.XIcon
import thisissadeghi.designsystem.XText
import thisissadeghi.designsystem.XTheme
import thisissadeghi.sample.data.model.BudgetCategory
import thisissadeghi.sample.data.model.DashboardData
import thisissadeghi.sample.data.model.PortfolioAsset
import thisissadeghi.sample.data.model.QuickAction
import thisissadeghi.sample.data.model.SavingsGoal
import thisissadeghi.sample.data.model.Transaction
import thisissadeghi.sample.data.model.UpcomingBill
import thisissadeghi.sample.presentation.SampleUiModel
import thisissadeghi.sample.presentation.SampleViewModel

private fun Double.formatMoney(): String {
    val abs = if (this < 0) -this else this
    val whole = abs.toLong()
    val cents = ((abs - whole) * 100 + 0.5).toInt().coerceIn(0, 99)
    val wholeFormatted =
        whole
            .toString()
            .reversed()
            .chunked(3)
            .joinToString(",")
            .reversed()
    return "$wholeFormatted.${cents.toString().padStart(2, '0')}"
}

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

// ─── Header ──────────────────────────────────────────────────────────────────

@Composable
private fun DashboardHeader() {
    Column(
        modifier =
            Modifier
                .fillMaxWidth()
                .padding(start = 16.dp, end = 16.dp, top = 8.dp, bottom = 8.dp),
    ) {
        XText(
            text = "Good morning",
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        XText(
            text = "Dashboard",
            fontSize = 24.sp,
            fontWeight = FontWeight.ExtraBold,
            color = MaterialTheme.colorScheme.onBackground,
        )
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
        contentPadding = PaddingValues(start = 16.dp, end = 16.dp, bottom = 32.dp),
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

// ─── Net Worth Card ───────────────────────────────────────────────────────────

@Composable
private fun NetWorthCard(
    totalBalance: Double,
    changePercent: Double,
) {
    XCard(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(0.dp),
        shape = RoundedCornerShape(24.dp),
    ) {
        Box {
            // Wallet icon watermark — top-right at 10% opacity
            XIcon(
                imageVector = Icons.Filled.AccountBalanceWallet,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.10f),
                modifier =
                    Modifier
                        .align(Alignment.TopEnd)
                        .padding(16.dp)
                        .size(64.dp),
            )
            Column {
                // 3dp primary accent bar at top
                Box(
                    modifier =
                        Modifier
                            .fillMaxWidth()
                            .height(3.dp)
                            .background(MaterialTheme.colorScheme.primary),
                )
                Column(modifier = Modifier.padding(24.dp)) {
                    XText(
                        text = "TOTAL NET WORTH",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        letterSpacing = 2.sp,
                    )
                    Spacer(Modifier.height(4.dp))
                    XText(
                        text = "$${ totalBalance.formatMoney() }",
                        fontSize = 38.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = MaterialTheme.colorScheme.primary,
                    )
                    Spacer(Modifier.height(16.dp))
                    // Trend chip
                    Row(
                        modifier =
                            Modifier
                                .background(
                                    XTheme.Colors.Success.copy(alpha = 0.10f),
                                    CircleShape,
                                ).border(
                                    1.dp,
                                    XTheme.Colors.Success.copy(alpha = 0.20f),
                                    CircleShape,
                                ).clip(CircleShape)
                                .padding(horizontal = 12.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                    ) {
                        XIcon(
                            imageVector = Icons.AutoMirrored.Filled.TrendingUp,
                            contentDescription = null,
                            tint = XTheme.Colors.Success,
                            modifier = Modifier.size(14.dp),
                        )
                        XText(
                            text = "+$changePercent% trend",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = XTheme.Colors.Success,
                        )
                    }
                }
            }
        }
    }
}

// ─── Quick Actions ────────────────────────────────────────────────────────────

@Composable
private fun QuickActionsRow(
    actions: List<QuickAction>,
    onActionClick: (String) -> Unit,
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceEvenly,
    ) {
        actions.forEach { action ->
            QuickActionItem(action, onClick = { onActionClick(action.id) })
        }
    }
}

@Composable
private fun QuickActionItem(
    action: QuickAction,
    onClick: () -> Unit,
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Box(
            modifier =
                Modifier
                    .size(56.dp)
                    .background(
                        MaterialTheme.colorScheme.primary.copy(alpha = 0.15f),
                        RoundedCornerShape(16.dp),
                    ).border(
                        1.dp,
                        MaterialTheme.colorScheme.primary.copy(alpha = 0.20f),
                        RoundedCornerShape(16.dp),
                    ).clip(RoundedCornerShape(16.dp))
                    .clickable(onClick = onClick),
            contentAlignment = Alignment.Center,
        ) {
            XIcon(
                imageVector = quickActionIcon(action.iconName),
                contentDescription = action.label,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(24.dp),
            )
        }
        XText(
            text = action.label,
            fontSize = 12.sp,
            fontWeight = FontWeight.Medium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
}

private fun quickActionIcon(iconName: String): ImageVector =
    when (iconName) {
        "send" -> Icons.AutoMirrored.Filled.Send
        "receive" -> Icons.Filled.CallReceived
        "pay" -> Icons.Filled.Payments
        "topup" -> Icons.Filled.AddCircle
        else -> Icons.Filled.Receipt
    }

// ─── Smart Insight Banner ─────────────────────────────────────────────────────

@Composable
private fun SmartInsightBanner(message: String) {
    XCard(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = XTheme.Colors.Success.copy(alpha = 0.05f)),
        shape = RoundedCornerShape(24.dp),
        border = BorderStroke(1.dp, XTheme.Colors.Success.copy(alpha = 0.20f)),
        elevation = CardDefaults.cardElevation(0.dp),
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Box(
                modifier =
                    Modifier
                        .size(40.dp)
                        .background(XTheme.Colors.Success.copy(alpha = 0.20f), RoundedCornerShape(16.dp)),
                contentAlignment = Alignment.Center,
            ) {
                XIcon(
                    imageVector = Icons.Filled.Lightbulb,
                    contentDescription = null,
                    tint = XTheme.Colors.Success,
                    modifier = Modifier.size(24.dp),
                )
            }
            Column {
                XText(
                    text = "Smart Insight",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface,
                )
                XText(
                    text = message,
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }
    }
}

// ─── Monthly Summary ──────────────────────────────────────────────────────────

@Composable
private fun MonthlySummarySection(
    income: Double,
    expenses: Double,
) {
    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        XText(
            text = "Monthly Summary",
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface,
        )
        XCard(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            shape = RoundedCornerShape(24.dp),
            elevation = CardDefaults.cardElevation(0.dp),
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(20.dp),
            ) {
                SummaryProgressRow(
                    label = "Income Performance",
                    amount = income,
                    fraction = (income / (income + expenses)).coerceIn(0.0, 1.0).toFloat(),
                    color = XTheme.Colors.Success,
                )
                SummaryProgressRow(
                    label = "Expenses Used",
                    amount = expenses,
                    fraction = (expenses / income).coerceIn(0.0, 1.0).toFloat(),
                    color = XTheme.Colors.Danger,
                )
            }
        }
    }
}

@Composable
private fun SummaryProgressRow(
    label: String,
    amount: Double,
    fraction: Float,
    color: Color,
) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            XText(label, fontSize = 12.sp, fontWeight = FontWeight.Medium, color = MaterialTheme.colorScheme.onSurfaceVariant)
            XText("$${ amount.formatMoney() }", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = color)
        }
        Box(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .height(6.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.outlineVariant),
        ) {
            Box(
                modifier =
                    Modifier
                        .fillMaxWidth(fraction)
                        .fillMaxHeight()
                        .background(color),
            )
        }
    }
}

// ─── Budgets ──────────────────────────────────────────────────────────────────

@Composable
private fun BudgetsSection(categories: List<BudgetCategory>) {
    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            XText("Budgets", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
            XText("View All", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
        }
        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            categories.forEach { BudgetItem(it) }
        }
    }
}

@Composable
private fun BudgetItem(category: BudgetCategory) {
    val accentColor = if (category.isOverBudget) XTheme.Colors.Danger else MaterialTheme.colorScheme.primary
    Row(
        modifier =
            Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.surface, RoundedCornerShape(24.dp))
                .clip(RoundedCornerShape(24.dp))
                .height(IntrinsicSize.Min),
    ) {
        Box(
            modifier =
                Modifier
                    .width(4.dp)
                    .fillMaxHeight()
                    .background(accentColor),
        )
        Row(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Box(
                    modifier =
                        Modifier
                            .size(40.dp)
                            .background(accentColor.copy(alpha = 0.15f), RoundedCornerShape(16.dp)),
                    contentAlignment = Alignment.Center,
                ) {
                    XIcon(
                        imageVector = budgetIcon(category.name),
                        contentDescription = null,
                        tint = accentColor,
                        modifier = Modifier.size(24.dp),
                    )
                }
                Column {
                    XText(category.name, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
                    XText(
                        "$${ category.spent.formatMoney() } of $${ category.total.formatMoney() }",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            }
            if (category.isOverBudget) {
                Box(
                    modifier =
                        Modifier
                            .background(XTheme.Colors.Danger, RoundedCornerShape(8.dp))
                            .padding(horizontal = 8.dp, vertical = 4.dp),
                ) {
                    XText("OVER", fontSize = 10.sp, fontWeight = FontWeight.Black, color = Color.White)
                }
            } else {
                XText("On track", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = XTheme.Colors.Success)
            }
        }
    }
}

private fun budgetIcon(name: String): ImageVector =
    when (name.lowercase()) {
        "shopping" -> Icons.Filled.ShoppingBag
        "dining" -> Icons.Filled.Restaurant
        else -> Icons.Filled.Receipt
    }

// ─── Savings Goals ────────────────────────────────────────────────────────────

@Composable
private fun SavingsGoalsSection(goals: List<SavingsGoal>) {
    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        XText("Savings Goals", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            goals.forEach { SavingsGoalItem(it) }
        }
    }
}

@Composable
private fun SavingsGoalItem(goal: SavingsGoal) {
    XCard(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        shape = RoundedCornerShape(24.dp),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
        elevation = CardDefaults.cardElevation(0.dp),
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top,
            ) {
                Column {
                    XText(goal.name, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
                    XText(
                        "$${ goal.current.formatMoney() } of $${ goal.target.formatMoney() }",
                        fontSize = 10.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
                XText(
                    "${(goal.progress * 100).toInt()}%",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = XTheme.Colors.Success,
                )
            }
            Box(
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .height(8.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.outlineVariant),
            ) {
                Box(
                    modifier =
                        Modifier
                            .fillMaxWidth(goal.progress)
                            .fillMaxHeight()
                            .background(XTheme.Colors.Success),
                )
            }
        }
    }
}

// ─── Upcoming Bills ───────────────────────────────────────────────────────────

@Composable
private fun UpcomingBillsSection(bills: List<UpcomingBill>) {
    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        XText("Upcoming Bills", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            bills.forEach { BillItem(it) }
        }
    }
}

@Composable
private fun BillItem(bill: UpcomingBill) {
    val accentColor = if (bill.isOverdue) XTheme.Colors.Danger else MaterialTheme.colorScheme.outlineVariant
    val iconTint = if (bill.isOverdue) XTheme.Colors.Danger else MaterialTheme.colorScheme.onSurfaceVariant
    Row(
        modifier =
            Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.surface, RoundedCornerShape(24.dp))
                .clip(RoundedCornerShape(24.dp))
                .height(IntrinsicSize.Min),
    ) {
        Box(
            modifier =
                Modifier
                    .width(4.dp)
                    .fillMaxHeight()
                    .background(accentColor),
        )
        Row(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Box(
                    modifier =
                        Modifier
                            .size(40.dp)
                            .background(iconTint.copy(alpha = 0.10f), RoundedCornerShape(16.dp)),
                    contentAlignment = Alignment.Center,
                ) {
                    XIcon(imageVector = billIcon(bill.name), contentDescription = null, tint = iconTint, modifier = Modifier.size(24.dp))
                }
                Column {
                    XText(bill.name, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
                    if (bill.isOverdue) {
                        XText("OVERDUE", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = XTheme.Colors.Danger)
                    } else {
                        XText("Due ${bill.dueDate}", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
            }
            XText(
                "$${ bill.amount.formatMoney() }",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface,
            )
        }
    }
}

private fun billIcon(name: String): ImageVector =
    when {
        name.contains("internet", ignoreCase = true) -> Icons.Filled.Wifi
        name.contains("electric", ignoreCase = true) -> Icons.Filled.ElectricBolt
        else -> Icons.Filled.Receipt
    }

// ─── Portfolio ────────────────────────────────────────────────────────────────

@Composable
private fun PortfolioSection(assets: List<PortfolioAsset>) {
    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        XText("Portfolio Assets", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
        XCard(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            shape = RoundedCornerShape(24.dp),
            elevation = CardDefaults.cardElevation(0.dp),
        ) {
            Column {
                assets.forEachIndexed { index, asset ->
                    PortfolioAssetItem(asset, index)
                    if (index < assets.lastIndex) {
                        XHorizontalDivider()
                    }
                }
            }
        }
    }
}

@Composable
private fun PortfolioAssetItem(
    asset: PortfolioAsset,
    index: Int,
) {
    val opacity =
        when (index) {
            0 -> 1.0f
            1 -> 0.8f
            else -> 0.6f
        }
    val changeColor = if (asset.changePercent >= 0) XTheme.Colors.Success else XTheme.Colors.Danger
    val sign = if (asset.changePercent >= 0) "+" else ""
    Row(
        modifier =
            Modifier
                .fillMaxWidth()
                .padding(16.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(
            modifier =
                Modifier
                    .size(40.dp)
                    .background(MaterialTheme.colorScheme.primary.copy(alpha = opacity), CircleShape),
            contentAlignment = Alignment.Center,
        ) {
            XText(
                asset.symbol.take(3),
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White,
            )
        }
        Column(modifier = Modifier.weight(1f)) {
            XText(asset.name, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
            XText("${asset.balance} ${asset.symbol}", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
        Column(horizontalAlignment = Alignment.End) {
            XText(
                "$${ asset.value.formatMoney() }",
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface,
            )
            XText("$sign${asset.changePercent}%", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = changeColor)
        }
    }
}

// ─── Recent Transactions ──────────────────────────────────────────────────────

@Composable
private fun RecentTransactionsSection(transactions: List<Transaction>) {
    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            XText("Recent Transactions", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
            XIcon(
                imageVector = Icons.Filled.Tune,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.size(24.dp),
            )
        }
        XCard(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            shape = RoundedCornerShape(24.dp),
            elevation = CardDefaults.cardElevation(0.dp),
        ) {
            Column {
                transactions.forEachIndexed { index, tx ->
                    TransactionItem(tx)
                    if (index < transactions.lastIndex) {
                        XHorizontalDivider()
                    }
                }
            }
        }
    }
}

@Composable
private fun TransactionItem(tx: Transaction) {
    val amountColor = if (tx.isIncome) XTheme.Colors.Success else MaterialTheme.colorScheme.onSurface
    val sign = if (tx.isIncome) "+" else "-"
    Row(
        modifier =
            Modifier
                .fillMaxWidth()
                .padding(16.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        TransactionIcon(tx.category)
        Column(modifier = Modifier.weight(1f)) {
            XText(
                tx.title,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
            XText(
                "${tx.category} • ${tx.date}",
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
        XText("$sign$${ tx.amount.formatMoney() }", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = amountColor)
    }
}

@Composable
private fun TransactionIcon(category: String) {
    val bgColor: Color
    val content: @Composable () -> Unit
    when (category.lowercase()) {
        "streaming" -> {
            bgColor = Color(0xFFDC2626).copy(alpha = 0.20f)
            content = {
                XText(
                    text = "N",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Black,
                    color = Color(0xFFDC2626),
                    fontStyle = FontStyle.Italic,
                )
            }
        }
        "income" -> {
            bgColor = XTheme.Colors.Success.copy(alpha = 0.20f)
            content =
                {
                    XIcon(
                        imageVector = Icons.Filled.Work,
                        contentDescription = null,
                        tint = XTheme.Colors.Success,
                        modifier = Modifier.size(24.dp),
                    )
                }
        }
        "food" -> {
            bgColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.30f)
            content =
                {
                    XIcon(
                        imageVector = Icons.Filled.LocalCafe,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.size(24.dp),
                    )
                }
        }
        else -> {
            bgColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.30f)
            content =
                {
                    XIcon(
                        imageVector = Icons.Filled.Receipt,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(24.dp),
                    )
                }
        }
    }
    Box(
        modifier =
            Modifier
                .size(40.dp)
                .background(bgColor, RoundedCornerShape(16.dp)),
        contentAlignment = Alignment.Center,
        content = { content() },
    )
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
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    XIcon(
                        imageVector = Icons.Filled.Error,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.error,
                        modifier = Modifier.size(24.dp),
                    )
                    XText(
                        text = "Something went wrong",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth(),
                    )
                    XText(
                        text = error,
                        fontSize = 14.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth(),
                    )
                    Spacer(Modifier.height(16.dp))
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
