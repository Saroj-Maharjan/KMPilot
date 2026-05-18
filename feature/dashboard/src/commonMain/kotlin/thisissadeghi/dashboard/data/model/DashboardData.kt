package thisissadeghi.dashboard.data.model

import kotlinx.serialization.Serializable

@Serializable
data class DashboardData(
    val accountBalance: AccountBalance,
    val monthlySummary: MonthlySummary,
    val recentTransactions: List<Transaction>,
    val budgetCategories: List<BudgetCategory>,
    val savingsGoals: List<SavingsGoal>,
    val quickActions: List<QuickAction>,
    val upcomingBills: List<UpcomingBill>,
    val spendingInsight: SpendingInsight,
    val portfolioAssets: List<PortfolioAsset>,
)

@Serializable
data class AccountBalance(
    val totalBalance: Double,
    val currency: String,
    val changePercent: Double,
    val changeAmount: Double,
)

@Serializable
data class MonthlySummary(
    val monthName: String,
    val income: Double,
    val expenses: Double,
    val currency: String,
)

@Serializable
data class Transaction(
    val id: String,
    val title: String,
    val category: String,
    val amount: Double,
    val isIncome: Boolean,
    val date: String,
    val currency: String,
)

@Serializable
data class BudgetCategory(
    val name: String,
    val spent: Double,
    val total: Double,
    val currency: String,
) {
    val progress: Float get() = (spent / total).coerceIn(0.0, 1.0).toFloat()
    val isOverBudget: Boolean get() = spent > total
}

@Serializable
data class SavingsGoal(
    val name: String,
    val current: Double,
    val target: Double,
    val currency: String,
    val dueDate: String,
) {
    val progress: Float get() = (current / target).coerceIn(0.0, 1.0).toFloat()
}

@Serializable
data class QuickAction(
    val id: String,
    val label: String,
    val iconName: String,
)

@Serializable
data class UpcomingBill(
    val id: String,
    val name: String,
    val amount: Double,
    val dueDate: String,
    val currency: String,
    val isOverdue: Boolean,
)

@Serializable
data class SpendingInsight(
    val message: String,
    val percentageChange: Double,
    val isPositive: Boolean,
)

@Serializable
data class PortfolioAsset(
    val id: String,
    val name: String,
    val symbol: String,
    val balance: Double,
    val value: Double,
    val changePercent: Double,
    val currency: String,
)
