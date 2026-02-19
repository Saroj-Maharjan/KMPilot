package thisissadeghi.sample.presentation.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.automirrored.filled.TrendingDown
import androidx.compose.material.icons.automirrored.filled.TrendingUp
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.Computer
import androidx.compose.material.icons.filled.CreditCard
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.Receipt
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material.icons.filled.Tv
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
import thisissadeghi.designsystem.XIcon
import thisissadeghi.designsystem.XScaffold
import thisissadeghi.designsystem.XText
import thisissadeghi.designsystem.toolbar.XTopAppBar
import thisissadeghi.sample.data.model.AccountBalance
import thisissadeghi.sample.data.model.BudgetCategory
import thisissadeghi.sample.data.model.DashboardData
import thisissadeghi.sample.data.model.MonthlySummary
import thisissadeghi.sample.data.model.PortfolioAsset
import thisissadeghi.sample.data.model.QuickAction
import thisissadeghi.sample.data.model.SavingsGoal
import thisissadeghi.sample.data.model.SpendingInsight
import thisissadeghi.sample.data.model.Transaction
import thisissadeghi.sample.data.model.UpcomingBill
import thisissadeghi.sample.presentation.SampleUiModel
import thisissadeghi.sample.presentation.SampleViewModel

private val IncomeGreen = Color(0xFF4CAF82)
private val ExpenseRed = Color(0xFFFF6B6B)

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
    XScaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            XTopAppBar(
                title = {
                    XText(
                        text = "Dashboard",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onBackground,
                    )
                },
            )
        },
    ) { paddingValues ->
        when (val state = uiState.dashboardState) {
            UiState.Uninitialized ->
                LoadingContent(modifier = Modifier.fillMaxSize().padding(paddingValues))

            UiState.Loading ->
                LoadingContent(modifier = Modifier.fillMaxSize().padding(paddingValues))

            is UiState.Success ->
                DashboardContent(
                    data = state.value,
                    onActionClick = onActionClick,
                    modifier = Modifier.padding(paddingValues),
                )

            is UiState.Failed ->
                ErrorContent(
                    error = state.error.asString(),
                    onRetry = onRetry,
                    modifier = Modifier.fillMaxSize().padding(paddingValues),
                )
        }
    }
}

@Composable
private fun DashboardContent(
    data: DashboardData,
    onActionClick: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(bottom = 32.dp),
        verticalArrangement = Arrangement.spacedBy(0.dp),
    ) {
        item { Spacer(Modifier.height(8.dp)) }
        item { BalanceSection(balance = data.accountBalance) }
        item { Spacer(Modifier.height(16.dp)) }
        item { MonthlySummarySection(summary = data.monthlySummary) }
        item { Spacer(Modifier.height(16.dp)) }
        item { QuickActionsSection(actions = data.quickActions, onActionClick = onActionClick) }
        item { Spacer(Modifier.height(24.dp)) }
        item { SectionHeader(title = "RECENT TRANSACTIONS") }
        items(data.recentTransactions) { tx ->
            TransactionRow(transaction = tx)
        }
        item { Spacer(Modifier.height(24.dp)) }
        item { SectionHeader(title = "BUDGET") }
        items(data.budgetCategories) { budget ->
            BudgetRow(category = budget)
        }
        item { Spacer(Modifier.height(24.dp)) }
        item { SectionHeader(title = "SAVINGS GOALS") }
        items(data.savingsGoals) { goal ->
            SavingsGoalCard(goal = goal)
        }
        item { Spacer(Modifier.height(24.dp)) }
        item { SectionHeader(title = "UPCOMING BILLS") }
        items(data.upcomingBills) { bill ->
            BillRow(bill = bill)
        }
        item { Spacer(Modifier.height(16.dp)) }
        item { InsightBanner(insight = data.spendingInsight) }
        item { Spacer(Modifier.height(24.dp)) }
        item { SectionHeader(title = "PORTFOLIO") }
        items(data.portfolioAssets) { asset ->
            PortfolioAssetRow(asset = asset)
        }
    }
}

// ─── Balance Section ──────────────────────────────────────────────────────────

@Composable
private fun BalanceSection(balance: AccountBalance) {
    XCard(
        modifier =
            Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
    ) {
        Column {
            Box(
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .height(2.dp)
                        .background(MaterialTheme.colorScheme.primary),
            )
            Column(modifier = Modifier.padding(horizontal = 20.dp, vertical = 16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    XText(
                        text = "Total Balance",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        letterSpacing = 1.sp,
                    )
                    Box(
                        modifier =
                            Modifier
                                .background(
                                    MaterialTheme.colorScheme.primary.copy(alpha = 0.15f),
                                    RoundedCornerShape(4.dp),
                                ).padding(horizontal = 8.dp, vertical = 3.dp),
                    ) {
                        XText(
                            text = balance.currency,
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.primary,
                            fontWeight = FontWeight.Bold,
                        )
                    }
                }
                Spacer(Modifier.height(12.dp))
                XText(
                    text = "$${balance.totalBalance.formatMoney()}",
                    fontSize = 38.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary,
                )
                Spacer(Modifier.height(8.dp))
                val isPositive = balance.changePercent >= 0
                val changeColor = if (isPositive) IncomeGreen else ExpenseRed
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                ) {
                    XIcon(
                        imageVector = if (isPositive) Icons.AutoMirrored.Filled.TrendingUp else Icons.AutoMirrored.Filled.TrendingDown,
                        contentDescription = null,
                        tint = changeColor,
                        modifier = Modifier.size(16.dp),
                    )
                    val sign = if (isPositive) "+" else ""
                    XText(
                        text = "$sign${balance.changePercent}%  ($sign$${balance.changeAmount.formatMoney()}) today",
                        style = MaterialTheme.typography.bodySmall,
                        color = changeColor,
                    )
                }
            }
        }
    }
}

// ─── Monthly Summary Section ──────────────────────────────────────────────────

@Composable
private fun MonthlySummarySection(summary: MonthlySummary) {
    XCard(
        modifier =
            Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
    ) {
        Column(modifier = Modifier.padding(horizontal = 20.dp, vertical = 16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                XText(
                    text = summary.monthName,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface,
                )
                XText(
                    text = "Monthly Summary",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
            Spacer(Modifier.height(16.dp))
            val maxValue = maxOf(summary.income, summary.expenses)
            SummaryBar(
                label = "Income",
                amount = summary.income,
                maxAmount = maxValue,
                color = IncomeGreen,
            )
            Spacer(Modifier.height(10.dp))
            SummaryBar(
                label = "Expenses",
                amount = summary.expenses,
                maxAmount = maxValue,
                color = ExpenseRed,
            )
        }
    }
}

@Composable
private fun SummaryBar(
    label: String,
    amount: Double,
    maxAmount: Double,
    color: Color,
) {
    val progress = (amount / maxAmount).coerceIn(0.0, 1.0).toFloat()
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        XText(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.width(60.dp),
        )
        Box(
            modifier =
                Modifier
                    .weight(1f)
                    .height(8.dp)
                    .clip(RoundedCornerShape(4.dp))
                    .background(MaterialTheme.colorScheme.outlineVariant),
        ) {
            Box(
                modifier =
                    Modifier
                        .fillMaxWidth(progress)
                        .fillMaxHeight()
                        .background(color),
            )
        }
        XText(
            text = "$${amount.formatMoney()}",
            style = MaterialTheme.typography.labelMedium,
            color = color,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.width(80.dp),
            textAlign = TextAlign.End,
        )
    }
}

// ─── Quick Actions Section ────────────────────────────────────────────────────

@Composable
private fun QuickActionsSection(
    actions: List<QuickAction>,
    onActionClick: (String) -> Unit,
) {
    Row(
        modifier =
            Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        actions.forEach { action ->
            QuickActionButton(
                action = action,
                onClick = { onActionClick(action.id) },
                modifier = Modifier.weight(1f),
            )
        }
    }
}

@Composable
private fun QuickActionButton(
    action: QuickAction,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    XCard(
        onClick = onClick,
        modifier = modifier,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
    ) {
        Column(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .padding(vertical = 14.dp, horizontal = 8.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Box(
                modifier =
                    Modifier
                        .size(36.dp)
                        .background(
                            MaterialTheme.colorScheme.primary.copy(alpha = 0.12f),
                            CircleShape,
                        ),
                contentAlignment = Alignment.Center,
            ) {
                XIcon(
                    imageVector = quickActionIcon(action.iconName),
                    contentDescription = action.label,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(18.dp),
                )
            }
            XText(
                text = action.label,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurface,
                textAlign = TextAlign.Center,
            )
        }
    }
}

private fun quickActionIcon(iconName: String): ImageVector =
    when (iconName) {
        "send" -> Icons.AutoMirrored.Filled.Send
        "receive" -> Icons.AutoMirrored.Filled.TrendingDown
        "pay" -> Icons.Default.CreditCard
        "topup" -> Icons.Default.AddCircle
        else -> Icons.Default.Receipt
    }

// ─── Section Header ───────────────────────────────────────────────────────────

@Composable
private fun SectionHeader(title: String) {
    Column(
        modifier =
            Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
    ) {
        XText(
            text = title,
            fontSize = 10.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            letterSpacing = 2.sp,
        )
        Spacer(Modifier.height(4.dp))
        Box(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .height(1.dp)
                    .background(MaterialTheme.colorScheme.outlineVariant),
        )
        Spacer(Modifier.height(8.dp))
    }
}

// ─── Transaction Row ──────────────────────────────────────────────────────────

@Composable
private fun TransactionRow(transaction: Transaction) {
    Row(
        modifier =
            Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Box(
            modifier =
                Modifier
                    .size(40.dp)
                    .background(
                        MaterialTheme.colorScheme.surface,
                        CircleShape,
                    ),
            contentAlignment = Alignment.Center,
        ) {
            XIcon(
                imageVector = categoryIcon(transaction.category),
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.size(20.dp),
            )
        }
        Column(modifier = Modifier.weight(1f)) {
            XText(
                text = transaction.title,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.onSurface,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
            XText(
                text = transaction.date,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
        val amountColor = if (transaction.isIncome) IncomeGreen else MaterialTheme.colorScheme.onSurface
        val sign = if (transaction.isIncome) "+" else "-"
        XText(
            text = "$sign$${transaction.amount.formatMoney()}",
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.SemiBold,
            color = amountColor,
        )
    }
}

private fun categoryIcon(category: String): ImageVector =
    when (category.lowercase()) {
        "streaming" -> Icons.Default.Tv
        "income" -> Icons.Default.AccountBalance
        "food" -> Icons.Default.Restaurant
        "tech" -> Icons.Default.Computer
        "shopping" -> Icons.Default.ShoppingBag
        else -> Icons.Default.Receipt
    }

// ─── Budget Row ───────────────────────────────────────────────────────────────

@Composable
private fun BudgetRow(category: BudgetCategory) {
    Column(
        modifier =
            Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(6.dp),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                XText(
                    text = category.name,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurface,
                )
                if (category.isOverBudget) {
                    Box(
                        modifier =
                            Modifier
                                .background(ExpenseRed.copy(alpha = 0.15f), RoundedCornerShape(3.dp))
                                .padding(horizontal = 5.dp, vertical = 1.dp),
                    ) {
                        XText(
                            text = "OVER",
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold,
                            color = ExpenseRed,
                            letterSpacing = 1.sp,
                        )
                    }
                }
            }
            XText(
                text = "$${category.spent.formatMoney()} / $${category.total.formatMoney()}",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
        Box(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .height(6.dp)
                    .clip(RoundedCornerShape(3.dp))
                    .background(MaterialTheme.colorScheme.outlineVariant),
        ) {
            Box(
                modifier =
                    Modifier
                        .fillMaxWidth(category.progress)
                        .fillMaxHeight()
                        .background(if (category.isOverBudget) ExpenseRed else MaterialTheme.colorScheme.primary),
            )
        }
    }
}

// ─── Savings Goal Card ────────────────────────────────────────────────────────

@Composable
private fun SavingsGoalCard(goal: SavingsGoal) {
    XCard(
        modifier =
            Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 6.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 14.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                XText(
                    text = goal.name,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface,
                )
                XText(
                    text = goal.dueDate,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
            Box(
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .height(6.dp)
                        .clip(RoundedCornerShape(3.dp))
                        .background(MaterialTheme.colorScheme.outlineVariant),
            ) {
                Box(
                    modifier =
                        Modifier
                            .fillMaxWidth(goal.progress)
                            .fillMaxHeight()
                            .background(IncomeGreen),
                )
            }
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                XText(
                    text = "$${goal.current.formatMoney()} of $${goal.target.formatMoney()}",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
                XText(
                    text = "${(goal.progress * 100).toInt()}%",
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Bold,
                    color = IncomeGreen,
                )
            }
        }
    }
}

// ─── Upcoming Bill Row ────────────────────────────────────────────────────────

@Composable
private fun BillRow(bill: UpcomingBill) {
    Row(
        modifier =
            Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Column(modifier = Modifier.weight(1f)) {
            XText(
                text = bill.name,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Medium,
                color = if (bill.isOverdue) ExpenseRed else MaterialTheme.colorScheme.onSurface,
            )
            if (bill.isOverdue) {
                XText(
                    text = "OVERDUE",
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    color = ExpenseRed,
                    letterSpacing = 1.sp,
                )
            } else {
                XText(
                    text = "Due ${bill.dueDate}",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }
        XText(
            text = "$${bill.amount.formatMoney()}",
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.SemiBold,
            color = if (bill.isOverdue) ExpenseRed else MaterialTheme.colorScheme.onSurface,
        )
    }
}

// ─── Insight Banner ───────────────────────────────────────────────────────────

@Composable
private fun InsightBanner(insight: SpendingInsight) {
    XCard(
        modifier =
            Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
        colors =
            CardDefaults.cardColors(
                containerColor =
                    if (insight.isPositive) {
                        IncomeGreen.copy(alpha = 0.08f)
                    } else {
                        ExpenseRed.copy(alpha = 0.08f)
                    },
            ),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
        border =
            BorderStroke(
                1.dp,
                if (insight.isPositive) IncomeGreen.copy(alpha = 0.3f) else ExpenseRed.copy(alpha = 0.3f),
            ),
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.Top,
        ) {
            XIcon(
                imageVector = Icons.Default.Lightbulb,
                contentDescription = null,
                tint = if (insight.isPositive) IncomeGreen else ExpenseRed,
                modifier = Modifier.size(18.dp),
            )
            XText(
                text = insight.message,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurface,
            )
        }
    }
}

// ─── Portfolio Asset Row ──────────────────────────────────────────────────────

@Composable
private fun PortfolioAssetRow(asset: PortfolioAsset) {
    Row(
        modifier =
            Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Box(
            modifier =
                Modifier
                    .size(40.dp)
                    .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.15f), CircleShape),
            contentAlignment = Alignment.Center,
        ) {
            XText(
                text = asset.symbol.take(2),
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary,
            )
        }
        Column(modifier = Modifier.weight(1f)) {
            XText(
                text = asset.name,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.onSurface,
            )
            XText(
                text = "${asset.balance} ${asset.symbol}",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
        Column(horizontalAlignment = Alignment.End) {
            XText(
                text = "$${asset.value.formatMoney()}",
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurface,
            )
            val changeColor =
                when {
                    asset.changePercent > 0 -> IncomeGreen
                    asset.changePercent < 0 -> ExpenseRed
                    else -> MaterialTheme.colorScheme.onSurfaceVariant
                }
            val sign = if (asset.changePercent > 0) "+" else ""
            XText(
                text = "$sign${asset.changePercent}%",
                style = MaterialTheme.typography.labelSmall,
                color = changeColor,
            )
        }
    }
}

// ─── State Screens ────────────────────────────────────────────────────────────

@Composable
private fun LoadingContent(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center,
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            XCircularProgressIndicator()
            XText(
                text = "Loading dashboard",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                letterSpacing = 1.sp,
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
        modifier = modifier.padding(24.dp),
        contentAlignment = Alignment.Center,
    ) {
        XCard(
            modifier = Modifier.fillMaxWidth(),
            elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
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
                    modifier = Modifier.padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    XText(
                        text = "Something went wrong",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface,
                        textAlign = TextAlign.Center,
                    )
                    XText(
                        text = error,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = TextAlign.Center,
                    )
                    Spacer(Modifier.height(4.dp))
                    XButton(onClick = onRetry) {
                        XText(
                            text = "Try Again",
                            style = MaterialTheme.typography.labelLarge,
                            fontWeight = FontWeight.SemiBold,
                        )
                    }
                }
            }
        }
    }
}
