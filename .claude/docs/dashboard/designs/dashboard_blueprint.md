# Compose Implementation Blueprint: Dashboard

> **Decomposed blueprint** — 185 HTML elements, 9 sections. Shared scaffold described once; per-section components annotated with target files.

---

## Design Tokens

| Hex | M3 Role | Usage |
|-----|---------|-------|
| #0F0D09 | background | Screen background, header background, loading/failed screen |
| #1C1910 | surface | All card backgrounds |
| #302B1C | surfaceVariant | Progress bar tracks, neutral icon containers (transactions, bills) |
| #EDE8D5 | onSurface | Primary text — titles, amounts, names |
| #C4BA94 | onSurfaceVariant | Muted text — greeting, secondary labels, failed screen heading |
| #F5D76E | primary | Balance amount, 3dp accent bar, action icon tints, on-track progress fills |
| #4A3200 | primaryContainer | Quick action button circles (@ 30% opacity), icon halos (@ 10%) |
| #2C1900 | onPrimary | Text on primary-colored Retry button (failed state) |
| #726A48 | outline | Balance Card border (1dp) |
| #3F3822 | outlineVariant | Most card borders, bill dividers (@ 30%), loading ring |
| #FFB4AB | error | Failed state warning icon, glow |
| #4ADE80 | XTheme.Colors.Success | Income amounts, positive %, savings progress, income icon circles |
| #FF6B6B | XTheme.Colors.Danger | Expense amounts, over-budget items, OVERDUE badge |

## Typography Scale

| Usage | Size (sp) | Weight | Letter Spacing | Case | Color Role |
|-------|-----------|--------|----------------|------|------------|
| Balance amount | 36 | ExtraBold (800) | -0.9sp (-0.025em) | none | primary |
| "Good morning," greeting | 14 | Medium (500) | 0 | none | onSurfaceVariant |
| Screen title ("Dashboard") | 20 | Bold (700) | -0.5sp (-0.025em) | none | onSurface |
| "TOTAL NET WORTH" label | 12 | Bold (700) | 1.2sp (0.1em) | UPPERCASE | onSurfaceVariant |
| "INCOME" / "EXPENSES" labels | 14 | Bold (700) | 0.7sp (0.05em) | UPPERCASE | onSurfaceVariant |
| Income / Expense values | 24 | Bold (700) | 0 | none | Success / Danger |
| Section headers | 18 | Bold (700) | 0 | none | onSurface |
| Insight title ("Smart Insight") | 14 | Bold (700) | 0 | none | onSurface |
| Insight body text | 14 | Normal (400) | 0 | none | onSurfaceVariant |
| Quick action label | 12 | Medium (500) | 0 | none | onSurfaceVariant |
| Trend chip text (+2.4%) | 12 | Bold (700) | 0 | none | Success |
| "vs last month" | 12 | Normal (400) | 0 | none | onSurfaceVariant |
| Savings % completion | 14 | Bold (700) | 0 | none | Success |
| Budget label (category) | 12 | Bold (700) | 0 | none | onSurfaceVariant / Danger (over-budget) |
| Budget amount | 12 | Bold (700) | 0 | none | onSurface / Danger (over-budget) |
| Bill name | 14 | Bold (700) | 0 | none | onSurface |
| Bill date | 12 | Normal (400) | 0 | none | onSurfaceVariant |
| Bill amount | 14 | Bold (700) | 0 | none | onSurface / Danger (overdue) |
| OVERDUE badge | 10 | Bold (700) | 0 | UPPERCASE | Danger |
| Portfolio ticker | 14 | Bold (700) | 0 | none | onSurface |
| Portfolio % change | 12 | Bold (700) | 0 | none | Success / Danger |
| Transaction name | 14 | Bold (700) | 0 | none | onSurface |
| Transaction category | 12 | Normal (400) | 0 | none | onSurfaceVariant |
| Transaction amount | 14 | Bold (700) | 0 | none | onSurface (expense) / Success (income) |
| Failed screen heading | 20 | SemiBold (600) | -0.5sp (-0.025em) | none | onSurfaceVariant |
| Failed screen body | 14 | Normal (400) | 0 | none | outline |
| Retry button label | 16 | Bold (700) | 0 | none | onPrimary |
| "Return to Dashboard" | 14 | Medium (500) | 0 | none | onSurfaceVariant |

## Spacing Grid

| Context | Property | Value (dp) |
|---------|----------|------------|
| Screen | horizontal padding (content) | 24 |
| Screen | bottom padding | 48 |
| Header | horizontal padding | 24 |
| Header | vertical padding | 16 |
| Sections | vertical gap between all sections | 24 |
| Balance Card | internal padding | 24 |
| Balance Card | top accent bar height | 3 |
| Balance Card | inner column gap | 8 |
| Balance Card trend chip | horizontal padding | 10 |
| Balance Card trend chip | vertical padding | 4 |
| Quick Actions | grid gap | 16 |
| Quick Actions | button size | 56 × 56 |
| Quick Actions | button-to-label gap | 8 |
| Insight Banner | internal padding | 20 |
| Insight Banner | icon-to-text gap | 16 |
| Insight Banner icon | size | 40 × 40 |
| Insight Banner icon | corner radius | 20 |
| Monthly Summary | internal padding | 24 |
| Monthly Summary | header row bottom margin | 24 |
| Monthly Summary | income/expense bar height | 12 |
| Budget section | grid gap | 16 |
| Budget card | internal padding | 16 |
| Budget card | inner spacing | 12 |
| Budget progress bar | height | 6 |
| Savings section | item gap | 12 |
| Savings goal card | internal padding | 20 |
| Savings goal | icon-to-label gap | 12 |
| Savings goal | header bottom margin | 12 |
| Savings progress bar | height | 8 |
| Bills container | item padding | 16 |
| Bill icon container | size | 40 × 40 |
| Bill icon container | corner radius | 20 |
| Bill icon / name | horizontal gap | 16 |
| Portfolio grid | gap | 12 |
| Portfolio item | internal padding | 16 |
| Portfolio item | inner gap | 8 |
| Portfolio icon circle | size | 32 × 32 |
| Portfolio icon circle | bottom margin | 4 |
| Transactions section | item gap | 12 |
| Transaction item | internal padding | 16 |
| Transaction icon / name | horizontal gap | 16 |
| Transaction icon | size | 40 × 40 |
| Failed screen | horizontal padding | 32 |
| Failed hero section | bottom margin | 32 |
| Failed icon glow box | size | 120 × 120 |
| Failed icon glow box | bottom margin | 24 |
| Failed icon | size | 80 × 80 |
| Failed body text | top margin | 8 |
| Failed actions column | top margin | 24 |
| Failed actions | gap between buttons | 16 |
| Failed Retry button | height | 56 |
| Failed Retry button | max-width | 200 |

## Component Tree

### Shared Scaffold (all states)

```kotlin
// → DashboardScreen.kt
XScaffold(containerColor = MaterialTheme.colorScheme.background) { paddingValues ->
    // State-routed content in content slot — see per-state sections below
}
```

> **Note**: Dashboard does NOT use `XTopAppBar`. The custom sticky header lives as the first item inside the scrollable `LazyColumn`. `XScaffold` provides only the container and system-bar inset handling.

---

### Success State

```kotlin
// → components/DashboardContent.kt
LazyColumn(
    modifier = Modifier.fillMaxSize().padding(paddingValues),
    contentPadding = PaddingValues(bottom = 48.dp)
) {
    item { DashboardHeader() }
    item {
        Column(
            modifier = Modifier.padding(horizontal = 24.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            BalanceCard(...)           // → components/BalanceCard.kt
            QuickActions(...)          // → components/QuickActions.kt
            InsightBanner(...)         // → components/InsightBanner.kt
            MonthlySummary(...)        // → components/MonthlySummary.kt
            MonthlyBudgets(...)        // → components/MonthlyBudgets.kt
            SavingsGoals(...)          // → components/SavingsGoals.kt
            UpcomingBills(...)         // → components/UpcomingBills.kt
            Portfolio(...)             // → components/Portfolio.kt
            RecentTransactions(...)    // → components/RecentTransactions.kt
        }
    }
}
```

#### DashboardHeader

```kotlin
// → DashboardScreen.kt (private composable, used as first LazyColumn item)
Row(
    modifier = Modifier
        .fillMaxWidth()
        .background(MaterialTheme.colorScheme.background)
        .padding(horizontal = 24.dp, vertical = 16.dp),
    horizontalArrangement = Arrangement.SpaceBetween,
    verticalAlignment = Alignment.CenterVertically
) {
    Column {
        XText(
            text = "Good morning,",
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium
        )
        XText(
            text = "Dashboard",
            color = MaterialTheme.colorScheme.onSurface,
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = (-0.5).sp
        )
    }
    // [no trailing icons in current design]
}
```

#### BalanceCard

```kotlin
// → components/BalanceCard.kt
Box(
    modifier = Modifier
        .fillMaxWidth()
        .background(MaterialTheme.colorScheme.surface, RoundedCornerShape(24.dp))
        .border(1.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(24.dp))
        .clip(RoundedCornerShape(24.dp))
) {
    // 3dp gold accent bar pinned to top
    Box(
        modifier = Modifier
            .align(Alignment.TopStart)
            .fillMaxWidth()
            .height(3.dp)
            .background(MaterialTheme.colorScheme.primary)
    )
    Column(modifier = Modifier.padding(24.dp)) {
        XText(
            text = "TOTAL NET WORTH",
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 1.2.sp,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            XText(
                text = netWorthText,       // e.g. "$48,520.00"
                color = MaterialTheme.colorScheme.primary,
                fontSize = 36.sp,
                fontWeight = FontWeight.ExtraBold,
                letterSpacing = (-0.9).sp
            )
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Trend chip: success@10% bg, CircleShape
                Row(
                    modifier = Modifier
                        .background(XTheme.Colors.Success.copy(alpha = 0.1f), CircleShape)
                        .padding(horizontal = 10.dp, vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    XIcon(
                        painter = painterResource(Res.drawable.trending_up),
                        tint = XTheme.Colors.Success,
                        modifier = Modifier.size(14.dp).padding(end = 4.dp)
                    )
                    XText(
                        text = trendText,  // e.g. "+2.4% ($1,140)"
                        color = XTheme.Colors.Success,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
                XText(
                    text = "vs last month",
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontSize = 12.sp
                )
            }
        }
    }
}
```

#### QuickActions

```kotlin
// → components/QuickActions.kt
@Composable
fun QuickActions(
    onSendClick: () -> Unit,
    onReceiveClick: () -> Unit,
    onPayClick: () -> Unit,
    onTopUpClick: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        QuickActionButton(Res.drawable.send,       "Send",    onSendClick,    Modifier.weight(1f))
        QuickActionButton(Res.drawable.download,   "Receive", onReceiveClick, Modifier.weight(1f))
        QuickActionButton(Res.drawable.payments,   "Pay",     onPayClick,     Modifier.weight(1f))
        QuickActionButton(Res.drawable.add_circle, "Top Up",  onTopUpClick,   Modifier.weight(1f))
    }
}

// Private helper — stays in components/QuickActions.kt
@Composable
private fun QuickActionButton(
    icon: DrawableResource,
    label: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Box(
            modifier = Modifier
                .size(56.dp)
                .background(
                    MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f),
                    CircleShape
                )
                .border(1.dp, MaterialTheme.colorScheme.outlineVariant, CircleShape)
                .clickable(onClick = onClick),
            contentAlignment = Alignment.Center
        ) {
            XIcon(
                painter = painterResource(icon),
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(24.dp)
            )
        }
        XText(
            text = label,
            fontSize = 12.sp,
            fontWeight = FontWeight.Medium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}
```

> Use `Box + .clickable`, NOT `XIconButton` — `XIconButton` renders a surface-colored circle by default.

#### InsightBanner

```kotlin
// → components/InsightBanner.kt
Row(
    modifier = Modifier
        .fillMaxWidth()
        .background(MaterialTheme.colorScheme.surface, RoundedCornerShape(24.dp))
        .border(1.dp, MaterialTheme.colorScheme.outlineVariant, RoundedCornerShape(24.dp))
        .padding(20.dp),
    horizontalArrangement = Arrangement.spacedBy(16.dp),
    verticalAlignment = Alignment.Top
) {
    Box(
        modifier = Modifier
            .size(40.dp)
            .background(
                MaterialTheme.colorScheme.primary.copy(alpha = 0.1f),
                RoundedCornerShape(20.dp)
            ),
        contentAlignment = Alignment.Center
    ) {
        XIcon(
            painter = painterResource(Res.drawable.lightbulb),
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(24.dp)
        )
    }
    Column {
        XText(
            text = "Smart Insight",
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface
        )
        XText(
            text = insightMessage,
            fontSize = 14.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            lineHeight = (14 * 1.625f).sp  // leading-relaxed
        )
    }
}
```

#### MonthlySummary

```kotlin
// → components/MonthlySummary.kt
Box(
    modifier = Modifier
        .fillMaxWidth()
        .background(MaterialTheme.colorScheme.surface, RoundedCornerShape(24.dp))
        .border(1.dp, MaterialTheme.colorScheme.outlineVariant, RoundedCornerShape(24.dp))
        .padding(24.dp)
) {
    Column {
        Row(
            modifier = Modifier.fillMaxWidth().padding(bottom = 24.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Bottom
        ) {
            Column {
                XText("INCOME", fontSize = 14.sp, fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurfaceVariant, letterSpacing = 0.7.sp)
                XText(incomeText, fontSize = 24.sp, fontWeight = FontWeight.Bold,
                    color = XTheme.Colors.Success)
            }
            Column(horizontalAlignment = Alignment.End) {
                XText("EXPENSES", fontSize = 14.sp, fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurfaceVariant, letterSpacing = 0.7.sp)
                XText(expensesText, fontSize = 24.sp, fontWeight = FontWeight.Bold,
                    color = XTheme.Colors.Danger)
            }
        }
        // Split progress bar — income fills left, expenses fill right
        Row(
            modifier = Modifier
                .fillMaxWidth().height(12.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.surfaceVariant)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxHeight().weight(incomeRatio)          // e.g. 61.7f
                    .clip(RoundedCornerShape(topStart = 50.dp, bottomStart = 50.dp))
                    .background(XTheme.Colors.Success)
            )
            Box(
                modifier = Modifier
                    .fillMaxHeight().weight(expenseRatio)         // e.g. 38.3f
                    .background(XTheme.Colors.Danger)
            )
        }
    }
}
```

#### MonthlyBudgets

```kotlin
// → components/MonthlyBudgets.kt
Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
    XText("Monthly Budgets", fontSize = 18.sp, fontWeight = FontWeight.Bold,
        color = MaterialTheme.colorScheme.onSurface)
    // 2-column grid via Row pairs (fixed count — avoid lazy for inlined content)
    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        budgets.chunked(2).forEach { row ->
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                row.forEach { budget -> BudgetCard(budget, Modifier.weight(1f)) }
                if (row.size < 2) Spacer(Modifier.weight(1f))
            }
        }
    }
}
```

#### BudgetCard (4× repeated)

```kotlin
// → components/BudgetCard.kt
// Over-budget variant (Entertainment): border=Danger@30%, label/amount=Danger, bar fill=Danger
@Composable
fun BudgetCard(budget: BudgetData, modifier: Modifier = Modifier) {
    val isOverBudget = budget.isOverBudget
    Box(
        modifier = modifier
            .background(MaterialTheme.colorScheme.surface, RoundedCornerShape(24.dp))
            .border(
                1.dp,
                if (isOverBudget) XTheme.Colors.Danger.copy(alpha = 0.3f)
                else MaterialTheme.colorScheme.outlineVariant,
                RoundedCornerShape(24.dp)
            )
            .padding(16.dp)
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Row(
                Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                XText(budget.label, fontSize = 12.sp, fontWeight = FontWeight.Bold,
                    color = if (isOverBudget) XTheme.Colors.Danger
                            else MaterialTheme.colorScheme.onSurfaceVariant)
                XText(budget.amountText, fontSize = 12.sp, fontWeight = FontWeight.Bold,
                    color = if (isOverBudget) XTheme.Colors.Danger
                            else MaterialTheme.colorScheme.onSurface)
            }
            Box(
                Modifier.fillMaxWidth().height(6.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.surfaceVariant)
            ) {
                Box(
                    Modifier.fillMaxHeight()
                        .fillMaxWidth(minOf(budget.progress, 1f))
                        .clip(CircleShape)
                        .background(
                            if (isOverBudget) XTheme.Colors.Danger
                            else MaterialTheme.colorScheme.primary
                        )
                )
            }
        }
    }
}
```

#### SavingsGoals

```kotlin
// → components/SavingsGoals.kt
Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
    XText("Savings Goals", fontSize = 18.sp, fontWeight = FontWeight.Bold,
        color = MaterialTheme.colorScheme.onSurface)
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        savingsGoals.forEach { goal -> SavingsGoalItem(goal) }
    }
}
```

#### SavingsGoalItem (2× repeated)

```kotlin
// → components/SavingsGoalItem.kt
// Icons: Res.drawable.savings (Emergency Fund, 64%), Res.drawable.flight (Vacation, 60%)
@Composable
fun SavingsGoalItem(goal: SavingsGoalData) {
    Box(
        Modifier.fillMaxWidth()
            .background(MaterialTheme.colorScheme.surface, RoundedCornerShape(24.dp))
            .border(1.dp, MaterialTheme.colorScheme.outlineVariant, RoundedCornerShape(24.dp))
            .padding(20.dp)
    ) {
        Column {
            Row(
                Modifier.fillMaxWidth().padding(bottom = 12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    XIcon(painter = painterResource(goal.icon),
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(24.dp))
                    XText(goal.name, fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface)
                }
                XText("${(goal.progress * 100).toInt()}%",
                    fontSize = 14.sp, fontWeight = FontWeight.Bold,
                    color = XTheme.Colors.Success)
            }
            Box(
                Modifier.fillMaxWidth().height(8.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.surfaceVariant)
            ) {
                Box(
                    Modifier.fillMaxHeight().fillMaxWidth(goal.progress)
                        .clip(CircleShape).background(XTheme.Colors.Success)
                )
            }
        }
    }
}
```

#### UpcomingBills

```kotlin
// → components/UpcomingBills.kt
Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
    XText("Upcoming Bills", fontSize = 18.sp, fontWeight = FontWeight.Bold,
        color = MaterialTheme.colorScheme.onSurface)
    Column(
        Modifier.fillMaxWidth()
            .background(MaterialTheme.colorScheme.surface, RoundedCornerShape(24.dp))
            .border(1.dp, MaterialTheme.colorScheme.outlineVariant, RoundedCornerShape(24.dp))
    ) {
        bills.forEachIndexed { index, bill ->
            BillItem(bill)
            if (index < bills.lastIndex) {
                XHorizontalDivider(
                    color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f)
                )
            }
        }
    }
}
```

#### BillItem (3× repeated)

```kotlin
// → components/BillItem.kt
// Netflix:     icon=subscriptions, iconTint=onSurfaceVariant, date="May 10",  amount=onSurface
// Internet:    icon=wifi,          iconTint=Danger,           isOverdue=true,  amount=Danger
// Electricity: icon=bolt,          iconTint=onSurfaceVariant, date="May 18",  amount=onSurface
@Composable
fun BillItem(bill: BillData) {
    Row(
        Modifier.fillMaxWidth().padding(16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalAlignment = Alignment.CenterVertically) {
            Box(
                Modifier.size(40.dp)
                    .background(MaterialTheme.colorScheme.surfaceVariant, RoundedCornerShape(20.dp)),
                contentAlignment = Alignment.Center
            ) {
                XIcon(painter = painterResource(bill.icon), tint = bill.iconTint,
                    modifier = Modifier.size(24.dp))
            }
            Column {
                Row(verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    XText(bill.name, fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface)
                    if (bill.isOverdue) {
                        Box(
                            Modifier.background(XTheme.Colors.Danger.copy(alpha = 0.2f),
                                RoundedCornerShape(8.dp))
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            XText("OVERDUE", fontSize = 10.sp, fontWeight = FontWeight.Bold,
                                color = XTheme.Colors.Danger)
                        }
                    }
                }
                if (bill.date != null) {
                    XText(bill.date, fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
        }
        XText(bill.amount, fontWeight = FontWeight.Bold,
            color = if (bill.isOverdue) XTheme.Colors.Danger
                    else MaterialTheme.colorScheme.onSurface)
    }
}
```

#### Portfolio

```kotlin
// → components/Portfolio.kt
Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
    XText("Portfolio", fontSize = 18.sp, fontWeight = FontWeight.Bold,
        color = MaterialTheme.colorScheme.onSurface)
    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
        portfolioItems.forEach { item -> PortfolioItem(item, Modifier.weight(1f)) }
    }
}
```

#### PortfolioItem (3× repeated)

```kotlin
// → components/PortfolioItem.kt
// BTC: icon=currency_bitcoin,  iconBgColor=primary,          iconTint=primary,          changeColor=Success
// ETH: icon=currency_exchange, iconBgColor=onSurfaceVariant, iconTint=onSurfaceVariant, changeColor=Danger
// SOL: icon=token,             iconBgColor=primary,          iconTint=primary,          changeColor=Success
@Composable
fun PortfolioItem(item: PortfolioData, modifier: Modifier = Modifier) {
    Box(
        modifier.fillMaxWidth()
            .background(MaterialTheme.colorScheme.surface, RoundedCornerShape(24.dp))
            .border(1.dp, MaterialTheme.colorScheme.outlineVariant, RoundedCornerShape(24.dp))
            .padding(16.dp)
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Box(
                Modifier.size(32.dp)
                    .background(item.iconBgColor.copy(alpha = 0.1f), CircleShape)
                    .padding(bottom = 4.dp),
                contentAlignment = Alignment.Center
            ) {
                XIcon(painter = painterResource(item.icon), tint = item.iconTint,
                    modifier = Modifier.size(20.dp))
            }
            XText(item.ticker, fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface)
            XText(item.changeText, fontSize = 12.sp, fontWeight = FontWeight.Bold,
                color = item.changeColor)
        }
    }
}
```

#### RecentTransactions

```kotlin
// → components/RecentTransactions.kt
Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
    XText("Recent Transactions", fontSize = 18.sp, fontWeight = FontWeight.Bold,
        color = MaterialTheme.colorScheme.onSurface)
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        transactions.forEach { tx -> TransactionItem(tx) }
    }
}
```

#### TransactionItem (5× repeated)

```kotlin
// → components/TransactionItem.kt
// Salary:    icon=work,           iconBgColor=Success@10%, iconTint=Success,          amountColor=Success
// Starbucks: icon=coffee,         iconBgColor=surfaceVariant, iconTint=onSurfaceVariant, amountColor=onSurface
// Amazon:    icon=shopping_bag,   iconBgColor=surfaceVariant, iconTint=onSurfaceVariant, amountColor=onSurface
// Freelance: icon=laptop_mac,     iconBgColor=Success@10%, iconTint=Success,          amountColor=Success
// Uber:      icon=directions_car, iconBgColor=surfaceVariant, iconTint=onSurfaceVariant, amountColor=onSurface
@Composable
fun TransactionItem(transaction: TransactionData) {
    Row(
        Modifier.fillMaxWidth()
            .background(MaterialTheme.colorScheme.surface, RoundedCornerShape(24.dp))
            .border(1.dp, MaterialTheme.colorScheme.outlineVariant, RoundedCornerShape(24.dp))
            .padding(16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalAlignment = Alignment.CenterVertically) {
            Box(
                Modifier.size(40.dp).background(transaction.iconBgColor, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                XIcon(painter = painterResource(transaction.icon), tint = transaction.iconTint,
                    modifier = Modifier.size(24.dp))
            }
            Column {
                XText(transaction.name, fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface)
                XText(transaction.category, fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
        XText(transaction.amount, fontWeight = FontWeight.Bold, color = transaction.amountColor)
    }
}
```

---

### Loading State

Shared screen — see: `.claude/docs/_shared/designs/loading.png`
Token inventory: `.claude/docs/_shared/designs/extracted/tokens_loading.md`

Full-screen centered spinner, chrome suppressed (no top app bar, no bottom nav).

```kotlin
// → DashboardScreen.kt (in DashboardScreenRoot state routing)
Box(
    Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background),
    contentAlignment = Alignment.Center
) {
    XCircularProgressIndicator()
    // [omit: decorative blur ring — no Compose blur equivalent]
    // [omit: bottom gradient branding line — opacity-10, decorative only]
    // [omit: radial-gradient background layer — decorative only]
}
```

---

### Failed State

Shared screen — see: `.claude/docs/_shared/designs/failed.png`
Token inventory: `.claude/docs/_shared/designs/extracted/tokens_failed.md`

Full-screen centered error content, chrome suppressed. Decorative background image anchored to the bottom at 20% opacity. Warning icon 80dp with error-tinted glow halo. Two action buttons: primary Retry + ghost "Return to Dashboard".

```kotlin
// → DashboardScreen.kt (in DashboardScreenRoot state routing)
Box(
    Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background),
    contentAlignment = Alignment.Center
) {
    // Decorative bottom image — 265dp height, alpha 0.2
    Image(
        painter = painterResource(Res.drawable.failed_background),
        contentDescription = null,
        contentScale = ContentScale.Crop,
        modifier = Modifier
            .align(Alignment.BottomStart)
            .fillMaxWidth()
            .height(265.dp)
            .alpha(0.2f)
    )
    Column(
        Modifier.fillMaxWidth().padding(horizontal = 32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // Hero icon with glow halo
        Column(
            Modifier.padding(bottom = 32.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                Modifier.padding(bottom = 24.dp),
                contentAlignment = Alignment.Center
            ) {
                // Glow halo (blur-3xl → alpha-only approximation; no Compose blur)
                Box(
                    Modifier.size(120.dp)
                        .background(MaterialTheme.colorScheme.error.copy(alpha = 0.1f), CircleShape)
                    // [omit: blur-3xl — no Compose equivalent]
                )
                XIcon(
                    painter = painterResource(Res.drawable.warning),
                    tint = MaterialTheme.colorScheme.error,
                    modifier = Modifier.size(80.dp)
                )
            }
        }
        XText(
            text = "Something went wrong",
            fontSize = 20.sp, fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center,
            letterSpacing = (-0.5).sp,
            modifier = Modifier.fillMaxWidth()
        )
        XText(
            text = "An unexpected error occurred. Please try again or check your connection.",
            fontSize = 14.sp,
            color = MaterialTheme.colorScheme.outline,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp)
                .widthIn(max = 240.dp)
        )
        Column(
            Modifier.fillMaxWidth().padding(top = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            XButton(
                onClick = onRetry,
                modifier = Modifier.fillMaxWidth().widthIn(max = 200.dp).height(56.dp),
                shape = RoundedCornerShape(12.dp),   // rounded-md = 12dp (custom Tailwind config)
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary
                )
            ) {
                XText("Retry", fontSize = 16.sp, fontWeight = FontWeight.Bold)
            }
            XTextButton(onClick = onNavigateToDashboard) {
                XText(
                    text = "Return to Dashboard",
                    fontSize = 14.sp, fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}
```

Icon resources used in failed state:
- `Res.drawable.warning` — domain icon (scope: domain, state: failed, icons.json)
- `Res.drawable.failed_background` — domain image (scope: domain, state: failed, images.json)

---

## Pre-Implementation Contract

> Architecture rules, color rules, and X-component defaults are project-wide and live in their canonical sources — do not restate them here:
> - Architecture rules → [`_shared/patterns.md`](../../_shared/patterns.md)
> - Color rules → [`m3-colors.md`](m3-colors.md) (sections "Color Rules (Strict)" and "Complete M3 Role Catalog")
> - X-component default-render behavior → [`_shared/X_COMPONENTS_CATALOG.md`](../../_shared/X_COMPONENTS_CATALOG.md)
>
> This contract captures only feature-specific data the implementer cannot derive from those references.

### XTheme Updates Required

None — all M3 roles and custom color extensions used in this design are already defined in both `XLightColors` and `XDarkColors`.

### Color Audit

#### Defined Roles

| Role | Hex | Usage |
|------|-----|-------|
| background | #0F0D09 | Screen bg, header bg, loading/failed screen bg |
| surface | #1C1910 | All card backgrounds |
| surfaceVariant | #302B1C | Progress bar tracks, neutral transaction/bill icon circles |
| onSurface | #EDE8D5 | Primary text — titles, amounts, names |
| onSurfaceVariant | #C4BA94 | Muted text — greeting, labels, secondary info, failed heading |
| primary | #F5D76E | Balance amount, accent bar, action icons, on-track progress fills |
| primaryContainer | #4A3200 | Quick action button circles (@ 30%), icon halos (@ 10%) |
| onPrimary | #2C1900 | Retry button text (failed state) |
| outline | #726A48 | Balance Card border, failed screen body text |
| outlineVariant | #3F3822 | Most card borders, bill dividers (@ 30%), loading ring |
| error | #FFB4AB | Failed state warning icon, glow |

#### Missing Roles (must add before implementation)

None.

#### Custom Colors (justified exceptions only)

| Name | Hex | Justification |
|------|-----|---------------|
| XTheme.Colors.Success | #4ADE80 | Income amounts, savings progress, positive %, income icon circles — M3 has no semantic success role |
| XTheme.Colors.Danger | #FF6B6B | Expense amounts, over-budget items, OVERDUE badge — distinct financial negative from M3 error (#FFB4AB) |

### Component Overrides (divergences from X-component defaults)

> **Audit-aware**: `/verify-ui` reads this table directly. Every row is a CRITICAL check at audit time — missing overrides in code are flagged.

| Component | Property | HTML Value | X-component Default | Override Required |
|-----------|----------|-----------|-------------------|------------------|
| `XScaffold` | containerColor | `background` (#0F0D09) | `XTheme.Colors.PaleLavender` (undefined — runtime error without override) | `containerColor = MaterialTheme.colorScheme.background` **MANDATORY** |
| Section cards | corner radius | 24dp (`rounded-2xl`) | `XCard`: 12dp (`shapes.medium`) | Use `Box + RoundedCornerShape(24.dp)` — do NOT use `XCard` |
| Section cards | bg color | `surface` (#1C1910) | `XCard`: `surfaceVariant` | `Modifier.background(colorScheme.surface, RoundedCornerShape(24.dp))` |
| Balance Card | border | 1dp `outline` (#726A48) | `XCard`: no border | `Modifier.border(1.dp, colorScheme.outline, RoundedCornerShape(24.dp))` |
| Other section cards | border | 1dp `outlineVariant` (#3F3822) | `XCard`: no border | `Modifier.border(1.dp, colorScheme.outlineVariant, RoundedCornerShape(24.dp))` |
| Quick action button | implementation | 56×56dp CircleShape, `primaryContainer@30%`, 1dp `outlineVariant` border | `XIconButton`: `surface`-colored circle bg | `Box(56.dp, CircleShape) + .background(primaryContainer@30%) + .border(1.dp, outlineVariant) + .clickable` — NOT `XIconButton` |
| Dashboard header | implementation | Custom sticky `Row` with greeting column | `XTopAppBar`: center-aligned title, forced headlineSmall typography | Custom `Row` composable — NOT `XTopAppBar` |
| `XButton` (Retry) | shape | `rounded-md` = 12dp (custom Tailwind config) | `XButton`: `CircleShape` | `shape = RoundedCornerShape(12.dp)` |
| `XButton` (Retry) | width | `fillMaxWidth(max = 200.dp)` | Default: wrap content | `Modifier.fillMaxWidth().widthIn(max = 200.dp)` |
| `XButton` (Retry) | height | 56dp | Default: wrap content | `Modifier.height(56.dp)` |
| `XIcon` (warning, failed state) | size | 80dp | No default — caller must pass | `Modifier.size(80.dp)` |
| `XIcon` (all others) | size | 24dp (standard) | No default — caller must pass | `Modifier.size(24.dp)` |

---

## Post-Implementation Checklist

- [ ] All XTheme missing roles added to BOTH XLightColors and XDarkColors *(none required — skip)*
- [ ] Every component in the Component Tree exists in implementation
- [ ] Every `Modifier` in blueprint (border, alpha, padding, size, background, clip) is present in code
- [ ] All colors use `MaterialTheme.colorScheme.{role}` or `XTheme.Colors.{name}` — no raw `Color()` hex
- [ ] `XScaffold(containerColor = MaterialTheme.colorScheme.background)` explicitly set
- [ ] Section cards use `Box + RoundedCornerShape(24.dp)` — NOT `XCard` (wrong radius + wrong bg color by default)
- [ ] Quick action buttons: `Box(56.dp, CircleShape, primaryContainer@30%, border=outlineVariant) + .clickable` — NOT `XIconButton`
- [ ] Dashboard header: custom `Row` composable — NOT `XTopAppBar`
- [ ] `XButton` Retry: `shape = RoundedCornerShape(12.dp)`, `fillMaxWidth(max=200.dp)`, `height(56.dp)`
- [ ] `XIcon(Res.drawable.warning)` in failed state: `Modifier.size(80.dp)`
- [ ] Loading state: `Box(fillMaxSize, Center) → XCircularProgressIndicator()` with `background = colorScheme.background`
- [ ] Failed state: bottom image (alpha=0.2f, 265dp) + glow halo + warning icon + heading + body + Retry + ghost "Return to Dashboard"
- [ ] 20 domain icon XMLs declared in `icons.json` — materialized by implementation skill under `feature/dashboard/src/commonMain/composeResources/drawable/`
- [ ] `Res.drawable.failed_background` declared in `images.json` — materialized by implementation skill
- [ ] Build passes: `./gradlew :feature:dashboard:assembleAndroidMain`
- [ ] Code formatted: `./gradlew :feature:dashboard:ktlintFormat`
