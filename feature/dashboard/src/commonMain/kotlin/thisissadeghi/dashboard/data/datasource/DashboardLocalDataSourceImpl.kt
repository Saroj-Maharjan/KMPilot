package thisissadeghi.dashboard.data.datasource

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

class DashboardLocalDataSourceImpl : DashboardLocalDataSource {
    override suspend fun getDashboard(): DashboardData =
        DashboardData(
            accountBalance =
                AccountBalance(
                    totalBalance = 24850.50,
                    currency = "USD",
                    changePercent = 2.4,
                    changeAmount = 580.00,
                ),
            monthlySummary =
                MonthlySummary(
                    monthName = "February 2026",
                    income = 5200.00,
                    expenses = 3450.00,
                    currency = "USD",
                ),
            recentTransactions =
                listOf(
                    Transaction("t1", "Netflix", "Streaming", 15.99, false, "Feb 18", "USD"),
                    Transaction("t2", "Salary Deposit", "Income", 5200.00, true, "Feb 15", "USD"),
                    Transaction("t3", "Whole Foods", "Food", 84.50, false, "Feb 14", "USD"),
                    Transaction("t4", "Uber Eats", "Food", 32.00, false, "Feb 13", "USD"),
                    Transaction("t5", "AWS Services", "Tech", 120.00, false, "Feb 12", "USD"),
                ),
            budgetCategories =
                listOf(
                    BudgetCategory("Food & Dining", 340.00, 500.00, "USD"),
                    BudgetCategory("Transportation", 120.00, 200.00, "USD"),
                    BudgetCategory("Entertainment", 85.00, 150.00, "USD"),
                    BudgetCategory("Shopping", 420.00, 400.00, "USD"),
                ),
            savingsGoals =
                listOf(
                    SavingsGoal("Emergency Fund", 8500.00, 10000.00, "USD", "Dec 2026"),
                    SavingsGoal("Vacation Fund", 1200.00, 3000.00, "USD", "Jul 2026"),
                ),
            quickActions =
                listOf(
                    QuickAction("send", "Send", "send"),
                    QuickAction("receive", "Receive", "receive"),
                    QuickAction("pay", "Pay", "pay"),
                    QuickAction("topup", "Top Up", "topup"),
                ),
            upcomingBills =
                listOf(
                    UpcomingBill("b1", "Rent", 1200.00, "Mar 1", "USD", false),
                    UpcomingBill("b2", "Electricity", 95.00, "Mar 5", "USD", false),
                    UpcomingBill("b3", "Internet", 59.99, "Feb 28", "USD", true),
                ),
            spendingInsight =
                SpendingInsight(
                    message = "You spent 20% more than last month on Food & Dining",
                    percentageChange = 20.0,
                    isPositive = false,
                ),
            portfolioAssets =
                listOf(
                    PortfolioAsset("p1", "Bitcoin", "BTC", 0.15, 6420.00, 3.2, "USD"),
                    PortfolioAsset("p2", "Ethereum", "ETH", 2.0, 4800.00, -1.5, "USD"),
                    PortfolioAsset("p3", "USD Coin", "USDC", 1000.0, 1000.00, 0.0, "USD"),
                ),
        )
}
