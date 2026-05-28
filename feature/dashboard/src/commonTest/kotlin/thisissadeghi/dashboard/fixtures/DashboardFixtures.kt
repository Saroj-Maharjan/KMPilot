package thisissadeghi.dashboard.fixtures

import thisissadeghi.common.Either
import thisissadeghi.common.ErrorModel
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
import thisissadeghi.data.ErrorConst

object DashboardFixtures {
    // === ACCOUNT BALANCE FACTORIES ===

    fun createAccountBalance(
        totalBalance: Double = 12_450.75,
        currency: String = "USD",
        changePercent: Double = 2.35,
        changeAmount: Double = 285.50,
    ) = AccountBalance(
        totalBalance = totalBalance,
        currency = currency,
        changePercent = changePercent,
        changeAmount = changeAmount,
    )

    fun createAccountBalanceZero() =
        createAccountBalance(
            totalBalance = 0.0,
            changePercent = 0.0,
            changeAmount = 0.0,
        )

    fun createAccountBalanceNegativeChange() =
        createAccountBalance(
            changePercent = -3.14,
            changeAmount = -391.27,
        )

    fun createAccountBalanceWithMaxValues() =
        createAccountBalance(
            totalBalance = Double.MAX_VALUE,
            changePercent = Double.MAX_VALUE,
            changeAmount = Double.MAX_VALUE,
        )

    fun createAccountBalanceWithMinValues() =
        createAccountBalance(
            totalBalance = 0.0,
            changePercent = Double.MIN_VALUE,
            changeAmount = Double.MIN_VALUE,
        )

    // === MONTHLY SUMMARY FACTORIES ===

    fun createMonthlySummary(
        monthName: String = "May 2026",
        income: Double = 5_200.00,
        expenses: Double = 3_740.50,
        currency: String = "USD",
    ) = MonthlySummary(
        monthName = monthName,
        income = income,
        expenses = expenses,
        currency = currency,
    )

    fun createMonthlySummaryWithZeroValues() =
        createMonthlySummary(
            income = 0.0,
            expenses = 0.0,
        )

    fun createMonthlySummaryExpensesExceedIncome() =
        createMonthlySummary(
            income = 1_000.0,
            expenses = 4_500.0,
        )

    fun createMonthlySummaryWithEmptyMonthName() = createMonthlySummary(monthName = "")

    fun createMonthlySummaryWithLongMonthName() = createMonthlySummary(monthName = "a".repeat(500))

    // === TRANSACTION FACTORIES ===

    fun createTransaction(
        id: String = "txn-001",
        title: String = "Grocery Store",
        category: String = "Food",
        amount: Double = 54.30,
        isIncome: Boolean = false,
        date: String = "2026-05-19",
        currency: String = "USD",
    ) = Transaction(
        id = id,
        title = title,
        category = category,
        amount = amount,
        isIncome = isIncome,
        date = date,
        currency = currency,
    )

    fun createIncomeTransaction(
        id: String = "txn-income-001",
        title: String = "Salary",
        amount: Double = 5_200.00,
    ) = createTransaction(id = id, title = title, amount = amount, isIncome = true, category = "Income")

    fun createTransactionList(count: Int = 3) =
        (1..count).map { i ->
            createTransaction(
                id = "txn-$i",
                title = "Transaction $i",
                amount = (i * 10).toDouble(),
                isIncome = i % 2 == 0,
            )
        }

    fun createEmptyTransactionList() = emptyList<Transaction>()

    fun createSingleTransactionList() = listOf(createTransaction())

    fun createLargeTransactionList(count: Int = 100) = createTransactionList(count)

    fun createTransactionWithEmptyStrings() =
        createTransaction(
            id = "",
            title = "",
            category = "",
            date = "",
            currency = "",
        )

    fun createTransactionWithSpecialCharacters() =
        createTransaction(
            title = "Test's \"Transaction\" with <special> & chars",
            category = "Food & Drink",
        )

    fun createTransactionWithUnicode() =
        createTransaction(
            title = "日本語 émoji 🎉 中文",
            category = "Ümlauts & açcénts",
        )

    fun createTransactionWithLongStrings() =
        createTransaction(
            title = "a".repeat(500),
            category = "b".repeat(500),
        )

    fun createTransactionWithMaxValues() = createTransaction(amount = Double.MAX_VALUE)

    fun createTransactionWithMinValues() = createTransaction(amount = 0.0)

    fun createTransactionWithZeroAmount() = createTransaction(amount = 0.0)

    // === BUDGET CATEGORY FACTORIES ===

    fun createBudgetCategory(
        name: String = "Food",
        spent: Double = 320.0,
        total: Double = 500.0,
        currency: String = "USD",
    ) = BudgetCategory(
        name = name,
        spent = spent,
        total = total,
        currency = currency,
    )

    fun createOverBudgetCategory() =
        createBudgetCategory(
            name = "Entertainment",
            spent = 750.0,
            total = 400.0,
        )

    fun createExactlyAtBudgetCategory() =
        createBudgetCategory(
            name = "Utilities",
            spent = 200.0,
            total = 200.0,
        )

    fun createZeroSpentCategory() = createBudgetCategory(spent = 0.0)

    fun createBudgetCategoryList(count: Int = 3) =
        (1..count).map { i ->
            createBudgetCategory(
                name = "Category $i",
                spent = (i * 50).toDouble(),
                total = (i * 100).toDouble(),
            )
        }

    fun createEmptyBudgetCategoryList() = emptyList<BudgetCategory>()

    fun createBudgetCategoryWithEmptyStrings() = createBudgetCategory(name = "", currency = "")

    fun createBudgetCategoryWithSpecialCharacters() =
        createBudgetCategory(
            name = "Food & Dining \"Restaurants\" <Local>",
        )

    fun createBudgetCategoryWithUnicode() = createBudgetCategory(name = "食費 🍜 émoji")

    fun createBudgetCategoryWithMaxValues() =
        createBudgetCategory(
            spent = Double.MAX_VALUE,
            total = Double.MAX_VALUE,
        )

    // === SAVINGS GOAL FACTORIES ===

    fun createSavingsGoal(
        name: String = "Emergency Fund",
        current: Double = 3_500.0,
        target: Double = 10_000.0,
        currency: String = "USD",
        dueDate: String = "2026-12-31",
    ) = SavingsGoal(
        name = name,
        current = current,
        target = target,
        currency = currency,
        dueDate = dueDate,
    )

    fun createCompletedSavingsGoal() =
        createSavingsGoal(
            name = "Vacation Fund",
            current = 5_000.0,
            target = 5_000.0,
        )

    fun createOverFundedSavingsGoal() =
        createSavingsGoal(
            name = "Laptop Fund",
            current = 1_800.0,
            target = 1_500.0,
        )

    fun createZeroProgressSavingsGoal() = createSavingsGoal(current = 0.0)

    fun createSavingsGoalList(count: Int = 3) =
        (1..count).map { i ->
            createSavingsGoal(
                name = "Goal $i",
                current = (i * 1_000).toDouble(),
                target = (i * 2_000).toDouble(),
                dueDate = "2026-0$i-01",
            )
        }

    fun createEmptySavingsGoalList() = emptyList<SavingsGoal>()

    fun createSavingsGoalWithEmptyStrings() = createSavingsGoal(name = "", currency = "", dueDate = "")

    fun createSavingsGoalWithSpecialCharacters() =
        createSavingsGoal(
            name = "Holiday's \"Savings\" <Goal> & More",
        )

    fun createSavingsGoalWithUnicode() = createSavingsGoal(name = "貯金 🎯 ültra")

    fun createSavingsGoalWithMaxValues() =
        createSavingsGoal(
            current = Double.MAX_VALUE,
            target = Double.MAX_VALUE,
        )

    // === QUICK ACTION FACTORIES ===

    fun createQuickAction(
        id: String = "action-001",
        label: String = "Send Money",
        iconName: String = "send",
    ) = QuickAction(id = id, label = label, iconName = iconName)

    fun createQuickActionList(count: Int = 4) =
        listOf(
            createQuickAction(id = "action-1", label = "Send", iconName = "send"),
            createQuickAction(id = "action-2", label = "Receive", iconName = "receive"),
            createQuickAction(id = "action-3", label = "Pay Bills", iconName = "bill"),
            createQuickAction(id = "action-4", label = "Top Up", iconName = "topup"),
        ).take(count)

    fun createEmptyQuickActionList() = emptyList<QuickAction>()

    fun createQuickActionWithEmptyStrings() = createQuickAction(id = "", label = "", iconName = "")

    fun createQuickActionWithSpecialCharacters() =
        createQuickAction(
            label = "Pay & Transfer \"Now\" <Fast>",
        )

    fun createQuickActionWithUnicode() = createQuickAction(label = "送金 💸 transfert")

    fun createQuickActionWithLongStrings() =
        createQuickAction(
            label = "a".repeat(500),
            iconName = "b".repeat(500),
        )

    // === UPCOMING BILL FACTORIES ===

    fun createUpcomingBill(
        id: String = "bill-001",
        name: String = "Electricity",
        amount: Double = 95.00,
        dueDate: String = "2026-05-25",
        currency: String = "USD",
        isOverdue: Boolean = false,
    ) = UpcomingBill(
        id = id,
        name = name,
        amount = amount,
        dueDate = dueDate,
        currency = currency,
        isOverdue = isOverdue,
    )

    fun createOverdueBill() =
        createUpcomingBill(
            id = "bill-overdue",
            name = "Internet",
            dueDate = "2026-04-01",
            isOverdue = true,
        )

    fun createUpcomingBillList(count: Int = 3) =
        (1..count).map { i ->
            createUpcomingBill(
                id = "bill-$i",
                name = "Bill $i",
                amount = (i * 30).toDouble(),
                dueDate = "2026-05-${20 + i}",
            )
        }

    fun createEmptyUpcomingBillList() = emptyList<UpcomingBill>()

    fun createSingleUpcomingBillList() = listOf(createUpcomingBill())

    fun createUpcomingBillWithEmptyStrings() = createUpcomingBill(id = "", name = "", dueDate = "", currency = "")

    fun createUpcomingBillWithSpecialCharacters() =
        createUpcomingBill(
            name = "Water & Sewer \"Authority\" <City>",
        )

    fun createUpcomingBillWithUnicode() = createUpcomingBill(name = "電気代 ⚡ électricité")

    fun createUpcomingBillWithMaxValues() = createUpcomingBill(amount = Double.MAX_VALUE)

    fun createUpcomingBillWithMinValues() = createUpcomingBill(amount = 0.0)

    // === SPENDING INSIGHT FACTORIES ===

    fun createSpendingInsight(
        message: String = "You spent 12% less on dining this month.",
        percentageChange: Double = -12.0,
        isPositive: Boolean = true,
    ) = SpendingInsight(
        message = message,
        percentageChange = percentageChange,
        isPositive = isPositive,
    )

    fun createNegativeSpendingInsight() =
        createSpendingInsight(
            message = "Your spending increased by 18% compared to last month.",
            percentageChange = 18.0,
            isPositive = false,
        )

    fun createZeroChangeSpendingInsight() =
        createSpendingInsight(
            message = "Your spending is unchanged from last month.",
            percentageChange = 0.0,
            isPositive = true,
        )

    fun createSpendingInsightWithEmptyMessage() = createSpendingInsight(message = "")

    fun createSpendingInsightWithBlankMessage() = createSpendingInsight(message = "   ")

    fun createSpendingInsightWithSpecialCharacters() =
        createSpendingInsight(
            message = "Spent \"less\" on <Food> & Drink's this month!",
        )

    fun createSpendingInsightWithUnicode() =
        createSpendingInsight(
            message = "支出 🎉 dépenses — less than before",
        )

    fun createSpendingInsightWithLongMessage() = createSpendingInsight(message = "b".repeat(5000))

    fun createSpendingInsightWithMaxValues() =
        createSpendingInsight(
            percentageChange = Double.MAX_VALUE,
        )

    fun createSpendingInsightWithMinValues() =
        createSpendingInsight(
            percentageChange = Double.MIN_VALUE,
        )

    // === PORTFOLIO ASSET FACTORIES ===

    fun createPortfolioAsset(
        id: String = "asset-001",
        name: String = "Apple Inc.",
        symbol: String = "AAPL",
        balance: Double = 10.5,
        value: Double = 1_890.75,
        changePercent: Double = 1.25,
        currency: String = "USD",
    ) = PortfolioAsset(
        id = id,
        name = name,
        symbol = symbol,
        balance = balance,
        value = value,
        changePercent = changePercent,
        currency = currency,
    )

    fun createPortfolioAssetNegativeChange() =
        createPortfolioAsset(
            id = "asset-down",
            name = "Tesla Inc.",
            symbol = "TSLA",
            changePercent = -4.72,
        )

    fun createPortfolioAssetList(count: Int = 3) =
        (1..count).map { i ->
            createPortfolioAsset(
                id = "asset-$i",
                name = "Asset $i",
                symbol = "SYM$i",
                balance = (i * 5).toDouble(),
                value = (i * 500).toDouble(),
                changePercent = (i - 2) * 1.5,
            )
        }

    fun createEmptyPortfolioAssetList() = emptyList<PortfolioAsset>()

    fun createSinglePortfolioAssetList() = listOf(createPortfolioAsset())

    fun createPortfolioAssetWithEmptyStrings() = createPortfolioAsset(id = "", name = "", symbol = "", currency = "")

    fun createPortfolioAssetWithSpecialCharacters() =
        createPortfolioAsset(
            name = "Company's \"Name\" & <Ticker>",
            symbol = "SYM",
        )

    fun createPortfolioAssetWithUnicode() =
        createPortfolioAsset(
            name = "日本語 株式 🏦",
            symbol = "JPN",
        )

    fun createPortfolioAssetWithMaxValues() =
        createPortfolioAsset(
            balance = Double.MAX_VALUE,
            value = Double.MAX_VALUE,
            changePercent = Double.MAX_VALUE,
        )

    fun createPortfolioAssetWithMinValues() =
        createPortfolioAsset(
            balance = 0.0,
            value = 0.0,
            changePercent = Double.MIN_VALUE,
        )

    // === DASHBOARD DATA FACTORIES ===

    fun createDashboardData(
        accountBalance: AccountBalance = createAccountBalance(),
        monthlySummary: MonthlySummary = createMonthlySummary(),
        recentTransactions: List<Transaction> = createTransactionList(),
        budgetCategories: List<BudgetCategory> = createBudgetCategoryList(),
        savingsGoals: List<SavingsGoal> = createSavingsGoalList(),
        quickActions: List<QuickAction> = createQuickActionList(),
        upcomingBills: List<UpcomingBill> = createUpcomingBillList(),
        spendingInsight: SpendingInsight = createSpendingInsight(),
        portfolioAssets: List<PortfolioAsset> = createPortfolioAssetList(),
    ) = DashboardData(
        accountBalance = accountBalance,
        monthlySummary = monthlySummary,
        recentTransactions = recentTransactions,
        budgetCategories = budgetCategories,
        savingsGoals = savingsGoals,
        quickActions = quickActions,
        upcomingBills = upcomingBills,
        spendingInsight = spendingInsight,
        portfolioAssets = portfolioAssets,
    )

    fun createDashboardDataWithEmptyLists() =
        createDashboardData(
            recentTransactions = emptyList(),
            budgetCategories = emptyList(),
            savingsGoals = emptyList(),
            quickActions = emptyList(),
            upcomingBills = emptyList(),
            portfolioAssets = emptyList(),
        )

    fun createDashboardDataWithSingleItems() =
        createDashboardData(
            recentTransactions = createSingleTransactionList(),
            budgetCategories = listOf(createBudgetCategory()),
            savingsGoals = listOf(createSavingsGoal()),
            quickActions = listOf(createQuickAction()),
            upcomingBills = createSingleUpcomingBillList(),
            portfolioAssets = createSinglePortfolioAssetList(),
        )

    fun createDashboardDataWithLargeLists(count: Int = 100) =
        createDashboardData(
            recentTransactions = createLargeTransactionList(count),
            budgetCategories = createBudgetCategoryList(count),
            savingsGoals = createSavingsGoalList(count),
            quickActions = (1..count).map { createQuickAction(id = "action-$it", label = "Action $it") },
            upcomingBills = createUpcomingBillList(count),
            portfolioAssets = createPortfolioAssetList(count),
        )

    fun createDashboardDataWithAllOverBudget() =
        createDashboardData(
            budgetCategories =
                listOf(
                    createOverBudgetCategory(),
                    createOverBudgetCategory().copy(name = "Travel"),
                ),
        )

    fun createDashboardDataWithAllOverdueBills() =
        createDashboardData(
            upcomingBills =
                listOf(
                    createOverdueBill(),
                    createOverdueBill().copy(id = "bill-overdue-2", name = "Phone"),
                ),
        )

    fun createDashboardDataWithNegativePortfolioChange() =
        createDashboardData(
            portfolioAssets = listOf(createPortfolioAssetNegativeChange()),
        )

    fun createDashboardDataWithNegativeInsight() =
        createDashboardData(
            spendingInsight = createNegativeSpendingInsight(),
        )

    fun createDashboardDataWithNegativeAccountChange() =
        createDashboardData(
            accountBalance = createAccountBalanceNegativeChange(),
        )

    fun createDashboardDataWithSpecialCharacters() =
        createDashboardData(
            monthlySummary = createMonthlySummary(monthName = "May's \"Month\" & <Year>"),
            spendingInsight = createSpendingInsightWithSpecialCharacters(),
        )

    fun createDashboardDataWithUnicode() =
        createDashboardData(
            monthlySummary = createMonthlySummary(monthName = "5月 🗓️ Май"),
            spendingInsight = createSpendingInsightWithUnicode(),
        )

    fun createDashboardDataWithBlankStrings() =
        createDashboardData(
            monthlySummary = createMonthlySummary(monthName = "   "),
            spendingInsight = createSpendingInsightWithBlankMessage(),
        )

    fun createDashboardDataWithLongStrings() =
        createDashboardData(
            monthlySummary = createMonthlySummaryWithLongMonthName(),
            spendingInsight = createSpendingInsightWithLongMessage(),
            recentTransactions = listOf(createTransactionWithLongStrings()),
            budgetCategories = listOf(createBudgetCategoryWithSpecialCharacters()),
            savingsGoals = listOf(createSavingsGoalWithSpecialCharacters()),
            quickActions = listOf(createQuickActionWithLongStrings()),
            upcomingBills = listOf(createUpcomingBillWithUnicode()),
            portfolioAssets = listOf(createPortfolioAssetWithUnicode()),
        )

    fun createDashboardDataWithMaxValues() =
        createDashboardData(
            accountBalance = createAccountBalanceWithMaxValues(),
            monthlySummary = createMonthlySummary(income = Double.MAX_VALUE, expenses = Double.MAX_VALUE),
            recentTransactions = listOf(createTransactionWithMaxValues()),
            budgetCategories = listOf(createBudgetCategoryWithMaxValues()),
            savingsGoals = listOf(createSavingsGoalWithMaxValues()),
            upcomingBills = listOf(createUpcomingBillWithMaxValues()),
            portfolioAssets = listOf(createPortfolioAssetWithMaxValues()),
            spendingInsight = createSpendingInsightWithMaxValues(),
        )

    fun createDashboardDataWithMinValues() =
        createDashboardData(
            accountBalance = createAccountBalanceWithMinValues(),
            monthlySummary = createMonthlySummaryWithZeroValues(),
            recentTransactions = listOf(createTransactionWithMinValues()),
            budgetCategories = listOf(createZeroSpentCategory()),
            savingsGoals = listOf(createZeroProgressSavingsGoal()),
            upcomingBills = listOf(createUpcomingBillWithMinValues()),
            portfolioAssets = listOf(createPortfolioAssetWithMinValues()),
            spendingInsight = createSpendingInsightWithMinValues(),
        )

    // === ERROR HELPERS ===

    val networkError = ErrorConst.NoNetwork
    val unauthorizedError = ErrorConst.Unauthorized
    val notFoundError = ErrorModel.MessageCode("Dashboard not found", 404)
    val badRequestError = ErrorModel.MessageCode("Invalid request parameters", 4001)
    val timeoutError = ErrorConst.ServerUnknownError(408)
    val serverError = ErrorConst.ServerUnknownError(500)
    val serviceUnavailableError = ErrorConst.ServerUnknownError(503)
    val serializationError = ErrorConst.SerializationError

    // === EITHER HELPERS ===

    fun createSuccessDashboardData(data: DashboardData = createDashboardData()) = Either.Success(data)

    fun createFailureDashboardData(error: ErrorModel = networkError) = Either.Failure(error)

    // === JSON RESPONSES (for MockEngine) ===

    val validTransactionJson = """{"id": "txn-001", "title": "Grocery Store", "category": "Food", "amount": 54.30, "isIncome": false, "date": "2026-05-19", "currency": "USD"}"""

    val validDashboardDataJson =
        """
        {
          "accountBalance": {
            "totalBalance": 12450.75,
            "currency": "USD",
            "changePercent": 2.35,
            "changeAmount": 285.50
          },
          "monthlySummary": {
            "monthName": "May 2026",
            "income": 5200.00,
            "expenses": 3740.50,
            "currency": "USD"
          },
          "recentTransactions": [
            {"id": "txn-1", "title": "Transaction 1", "category": "Food", "amount": 10.0, "isIncome": false, "date": "2026-05-19", "currency": "USD"},
            {"id": "txn-2", "title": "Transaction 2", "category": "Food", "amount": 20.0, "isIncome": true, "date": "2026-05-18", "currency": "USD"},
            {"id": "txn-3", "title": "Transaction 3", "category": "Food", "amount": 30.0, "isIncome": false, "date": "2026-05-17", "currency": "USD"}
          ],
          "budgetCategories": [
            {"name": "Category 1", "spent": 50.0, "total": 100.0, "currency": "USD"},
            {"name": "Category 2", "spent": 100.0, "total": 200.0, "currency": "USD"},
            {"name": "Category 3", "spent": 150.0, "total": 300.0, "currency": "USD"}
          ],
          "savingsGoals": [
            {"name": "Goal 1", "current": 1000.0, "target": 2000.0, "currency": "USD", "dueDate": "2026-01-01"},
            {"name": "Goal 2", "current": 2000.0, "target": 4000.0, "currency": "USD", "dueDate": "2026-02-01"},
            {"name": "Goal 3", "current": 3000.0, "target": 6000.0, "currency": "USD", "dueDate": "2026-03-01"}
          ],
          "quickActions": [
            {"id": "action-1", "label": "Send", "iconName": "send"},
            {"id": "action-2", "label": "Receive", "iconName": "receive"},
            {"id": "action-3", "label": "Pay Bills", "iconName": "bill"},
            {"id": "action-4", "label": "Top Up", "iconName": "topup"}
          ],
          "upcomingBills": [
            {"id": "bill-1", "name": "Bill 1", "amount": 30.0, "dueDate": "2026-05-21", "currency": "USD", "isOverdue": false},
            {"id": "bill-2", "name": "Bill 2", "amount": 60.0, "dueDate": "2026-05-22", "currency": "USD", "isOverdue": false},
            {"id": "bill-3", "name": "Bill 3", "amount": 90.0, "dueDate": "2026-05-23", "currency": "USD", "isOverdue": false}
          ],
          "spendingInsight": {
            "message": "You spent 12% less on dining this month.",
            "percentageChange": -12.0,
            "isPositive": true
          },
          "portfolioAssets": [
            {"id": "asset-1", "name": "Asset 1", "symbol": "SYM1", "balance": 5.0, "value": 500.0, "changePercent": -1.5, "currency": "USD"},
            {"id": "asset-2", "name": "Asset 2", "symbol": "SYM2", "balance": 10.0, "value": 1000.0, "changePercent": 0.0, "currency": "USD"},
            {"id": "asset-3", "name": "Asset 3", "symbol": "SYM3", "balance": 15.0, "value": 1500.0, "changePercent": 1.5, "currency": "USD"}
          ]
        }
        """.trimIndent()

    val emptyListsDashboardDataJson =
        """
        {
          "accountBalance": {"totalBalance": 0.0, "currency": "USD", "changePercent": 0.0, "changeAmount": 0.0},
          "monthlySummary": {"monthName": "May 2026", "income": 0.0, "expenses": 0.0, "currency": "USD"},
          "recentTransactions": [],
          "budgetCategories": [],
          "savingsGoals": [],
          "quickActions": [],
          "upcomingBills": [],
          "spendingInsight": {"message": "No insights yet.", "percentageChange": 0.0, "isPositive": true},
          "portfolioAssets": []
        }
        """.trimIndent()

    // Error JSONs (NetworkErrorModel format)
    val error400Json = """{"detail": "Invalid request parameters", "code": 4001}"""
    val error401Json = """{"detail": "Unauthorized", "code": null}"""
    val error403Json = """{"detail": "Access denied", "code": 403}"""
    val error404Json = """{"detail": "Dashboard not found", "code": 404}"""
    val error500Json = """{"detail": "Internal Server Error", "code": 5001}"""
    val error503Json = """{"detail": null, "code": null}"""

    // Edge case JSONs
    val malformedJson = "{ invalid json"
    val incompleteJson = """{"accountBalance": {"totalBalance": 100.0, "currency": "USD", "changePercent": 0.0, "changeAmount": 0.0}}"""
    val nullFieldsJson = """{"id": "test-id", "name": "Test", "description": null}"""
    val extraFieldsJson = """{"id": "test-id", "unknownField": "ignored", "anotherUnknown": 42}"""
    val emptyStringFieldsJson = """{"id": "", "name": ""}"""
    val specialCharsJson = """{"id": "test-id", "name": "Test's \"Dashboard\" & <more>"}"""
    val unicodeJson = """{"id": "test-id", "name": "日本語 🎉"}"""
}
