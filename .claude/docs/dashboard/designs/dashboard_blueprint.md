# Compose Implementation Blueprint: Dashboard (Finance Dashboard)

> **Decomposed blueprint** — 185 HTML elements, 9 sections. Shared scaffold described once; per-section components annotated with target files.

---

## Design Tokens

| Hex | M3 Role / Custom | Usage |
|-----|-----------------|-------|
| #0F0D09 | background | Screen + header background |
| #1C1910 | surface | All card backgrounds |
| #302B1C | surfaceVariant | Progress bar tracks, quick action button bg, expense/neutral icon circles |
| #F5D76E | primary | Balance amount, gold top strip, action icons, on-track progress fill |
| #2C1900 | onPrimary | Text on gold (retry button) |
| #4A3200 | primaryContainer | Action button circle (@ 30%), insight icon bg (@10%), BTC/SOL circles (@10%) |
| #FFF0C0 | onPrimaryContainer | Text on primary container chips |
| #EDE8D5 | onSurface | Primary text everywhere — titles, amounts, bill names, transaction names |
| #C4BA94 | onSurfaceVariant | Muted text, subtitle labels, due dates, quick action labels |
| #726A48 | outline | Card 1dp borders |
| #3F3822 | outlineVariant | Bill section dividers (@30%), budget/savings card borders |
| #FFB4AB | error | Failed state icon + glow |
| #4ADE80 | XTheme.Colors.Success | Income amounts, positive %, savings progress, income icon circles |
| #FF6B6B | XTheme.Colors.Danger | Expenses, over-budget, OVERDUE badge, negative % |

---

## Typography Scale

| Usage | Size (sp) | Weight | Letter Spacing | Text Transform | Color Role |
|-------|-----------|--------|----------------|----------------|------------|
| "Good morning," subtitle | 14 | Medium (500) | 0 | none | onSurfaceVariant |
| "Dashboard" header title | 20 | Bold (700) | -0.025em × 20sp | none | onSurface |
| "TOTAL NET WORTH" label | 12 | Bold (700) | 0.1em × 12sp | uppercase | onSurfaceVariant |
| Balance amount | 36 | ExtraBold (800) | -0.025em × 36sp | none | primary |
| Trend pill text | 12 | Bold (700) | 0 | none | XTheme.Colors.Success |
| "vs last month" | 12 | Normal | 0 | none | onSurfaceVariant |
| Section headers (h3) | 18 | Bold (700) | 0 | none | onSurface |
| Monthly Income/Expenses labels | 14 | Bold (700) | 0.05em × 14sp | uppercase | onSurfaceVariant |
| Monthly amounts | 24 | Bold (700) | 0 | none | Success / Danger |
| Budget category names | 12 | Bold (700) | 0 | none | onSurfaceVariant (normal) / Danger (over) |
| Budget spent amounts | 12 | Bold (700) | 0 | none | onSurface (normal) / Danger (over) |
| Insight title | 14 (body default) | Bold (700) | 0 | none | onSurface |
| Insight description | 14 | Normal | 0 | none | onSurfaceVariant |
| Savings goal name | 14 (body default) | Bold (700) | 0 | none | onSurface |
| Savings goal % | 14 | Bold (700) | 0 | none | XTheme.Colors.Success |
| Bill name | 14 (body default) | Bold (700) | 0 | none | onSurface |
| Bill due date | 12 | Normal | 0 | none | onSurfaceVariant |
| Bill amount | 14 (body default) | Bold (700) | 0 | none | onSurface / Danger (overdue) |
| OVERDUE badge | 10 | Bold (700) | 0 | uppercase | Danger |
| Portfolio symbol | 14 (body default) | Bold (700) | 0 | none | onSurface |
| Portfolio change % | 12 | Bold (700) | 0 | none | Success / Danger |
| Transaction name | 14 (body default) | Bold (700) | 0 | none | onSurface |
| Transaction subtitle | 12 | Normal | 0 | none | onSurfaceVariant |
| Transaction amount | 14 (body default) | Bold (700) | 0 | none | Success (income) / onSurface (expense) |
| Quick action label | 12 | Medium (500) | 0 | none | onSurfaceVariant |

---

## Spacing Grid

| Context | Property | Value (dp) |
|---------|----------|------------|
| Screen | horizontal padding (px-6) | 24 |
| Screen | bottom padding (pb-12) | 48 |
| Sections | vertical gap (space-y-6) | 24 |
| Balance card | padding (p-6) | 24 |
| Balance card top strip | height | 3 |
| Balance amount row | gap (gap-2) | 8 |
| Balance label → amount | margin-bottom (mb-2) | 8 |
| Quick actions grid | gap (gap-4) | 16 |
| Quick action item | vertical gap (gap-2) | 8 |
| Quick action circle | size (w-14 h-14) | 56 × 56 |
| Insight banner | padding (p-5) | 20 |
| Insight banner | row gap (gap-4) | 16 |
| Insight icon circle | size (w-10 h-10) | 40 × 40 |
| Insight icon corner radius | (rounded-xl) | 20 |
| Monthly summary card | padding (p-6) | 24 |
| Monthly summary header | margin-bottom (mb-6) | 24 |
| Monthly summary bar | height (h-3) | 12 |
| Budget grid | gap (gap-4) | 16 |
| Budget card | padding (p-4) | 16 |
| Budget card inner | spacing (space-y-3) | 12 |
| Budget progress bar | height (h-1.5) | 6 |
| Savings goals inner | spacing (space-y-3) | 12 |
| Savings goal card | padding (p-5) | 20 |
| Savings goal header | margin-bottom (mb-3) | 12 |
| Savings goal header | gap (gap-3) | 12 |
| Savings goal bar | height (h-2) | 8 |
| Bills card row | padding (p-4) | 16 |
| Bills row | gap (gap-4) | 16 |
| Bill icon circle | size (w-10 h-10) | 40 × 40 |
| Bill icon circle | corner radius (rounded-xl) | 20 |
| Portfolio grid | gap (gap-3) | 12 |
| Portfolio card | padding (p-4) | 16 |
| Portfolio card inner | gap (gap-2) | 8 |
| Portfolio symbol circle | size (w-8 h-8) | 32 × 32 |
| Portfolio symbol margin | (mb-1) | 4 |
| Transactions inner | spacing (space-y-3) | 12 |
| Transaction card | padding (p-4) | 16 |
| Transaction row | gap (gap-4) | 16 |
| Transaction icon circle | size (w-10 h-10) | 40 × 40 |
| OVERDUE badge | horizontal padding (px-1.5) | 6 |
| OVERDUE badge | vertical padding (py-0.5) | 2 |
| OVERDUE badge | corner radius (rounded) | 8 |

---

## Component Tree

### Shared Scaffold (all states)

```
// → DashboardScreen.kt
XScaffold(containerColor = MaterialTheme.colorScheme.background)
  content: paddingValues →
    when (uiState.dashboardState) {
      Uninitialized, Loading → LoadingContent()     // → DashboardScreen.kt
      Failed              → ErrorContent(onRetry)   // → DashboardScreen.kt
      Success             → DashboardContent(data, onActionClick)  // → DashboardScreen.kt
    }
```

> **CRITICAL**: `XScaffold` default `containerColor` references `XTheme.Colors.PaleLavender` which is undefined — always pass `containerColor = MaterialTheme.colorScheme.background` explicitly.

---

### Success State

```
// → DashboardScreen.kt
fun DashboardContent(data: DashboardData, onActionClick: (String) -> Unit)
  Column(modifier = Modifier.fillMaxSize()) {
    // Sticky header — outside LazyColumn so it stays fixed while list scrolls
    DashboardHeader()                                       // inline in DashboardScreen.kt

    LazyColumn(
      modifier = Modifier.fillMaxSize(),
      contentPadding = PaddingValues(horizontal = 24.dp, bottom = 48.dp),
      verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
      item { BalanceCard(data.accountBalance) }                     // → components/BalanceCard.kt
      item { QuickActionsSection(data.quickActions, onActionClick) }// → components/QuickActionsSection.kt
      item { InsightBanner(data.spendingInsight) }                   // → components/InsightBanner.kt
      item { MonthlySummaryCard(data.monthlySummary) }               // → components/MonthlySummaryCard.kt
      item { BudgetsSection(data.budgetCategories) }                 // → components/BudgetsSection.kt
      item { SavingsGoalsSection(data.savingsGoals) }                // → components/SavingsGoalsSection.kt
      item { UpcomingBillsCard(data.upcomingBills) }                 // → components/UpcomingBillsCard.kt
      item { PortfolioSection(data.portfolioAssets) }                // → components/PortfolioSection.kt
      item { RecentTransactionsSection(data.recentTransactions) }    // → components/RecentTransactionsSection.kt
    }
  }
```

---

#### DashboardHeader (inline in DashboardScreen.kt)

> HTML: `<header>` with `sticky top-0 bg-background px-6 py-4 z-50`. In Compose: place header as sibling before `LazyColumn` in a wrapping `Column`.

```
Box(
  modifier = Modifier
    .fillMaxWidth()
    .background(MaterialTheme.colorScheme.background)
    .padding(horizontal = 24.dp, vertical = 16.dp)
) {
  Column {
    XText("Good morning,",
          color = MaterialTheme.colorScheme.onSurfaceVariant,
          fontSize = 14.sp, fontWeight = FontWeight.Medium)
    XText("Dashboard",
          color = MaterialTheme.colorScheme.onSurface,
          fontSize = 20.sp, fontWeight = FontWeight.Bold,
          letterSpacing = (-0.5).sp)
  }
}
```

---

#### BalanceCard — `components/BalanceCard.kt`

```
Box(
  modifier = Modifier
    .fillMaxWidth()
    .background(MaterialTheme.colorScheme.surface, RoundedCornerShape(24.dp))
    .border(1.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(24.dp))
    .clip(RoundedCornerShape(24.dp))
) {
  // 3dp gold top accent strip
  Box(
    modifier = Modifier
      .fillMaxWidth()
      .height(3.dp)
      .background(MaterialTheme.colorScheme.primary)
      .align(Alignment.TopStart)
  )

  Column(modifier = Modifier.padding(24.dp)) {
    XText("TOTAL NET WORTH",
          color = MaterialTheme.colorScheme.onSurfaceVariant,
          fontSize = 12.sp, fontWeight = FontWeight.Bold,
          letterSpacing = 1.2.sp)
    Spacer(Modifier.height(8.dp))

    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
      XText(
        balance.formattedAmount,
        color = MaterialTheme.colorScheme.primary,
        fontSize = 36.sp,
        fontWeight = FontWeight.ExtraBold,
        letterSpacing = (-0.9).sp
      )
      Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
      ) {
        // Trend pill — bg-success/10 rounded-full
        Row(
          modifier = Modifier
            .background(XTheme.Colors.Success.copy(alpha = 0.1f), CircleShape)
            .padding(horizontal = 10.dp, vertical = 4.dp),
          verticalAlignment = Alignment.CenterVertically
        ) {
          XIcon(Icons.Default.TrendingUp,
                tint = XTheme.Colors.Success,
                modifier = Modifier.size(14.dp))
          Spacer(Modifier.width(4.dp))
          XText("+${balance.changePercent}% ($${balance.changeAmount})",
                color = XTheme.Colors.Success,
                fontSize = 12.sp, fontWeight = FontWeight.Bold)
        }
        XText("vs last month",
              color = MaterialTheme.colorScheme.onSurfaceVariant,
              fontSize = 12.sp)
      }
    }
  }
}
```

---

#### QuickActionsSection — `components/QuickActionsSection.kt`

> HTML: `grid grid-cols-4 gap-4`. 4 items with identical structure — repeated pattern.

```
Row(
  modifier = Modifier.fillMaxWidth(),
  horizontalArrangement = Arrangement.spacedBy(16.dp)
) {
  actions.forEach { action ->
    Column(
      modifier = Modifier.weight(1f),
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
          .clickable { onActionClick(action.id) },
        contentAlignment = Alignment.Center
      ) {
        XIcon(icon = action.icon,
              tint = MaterialTheme.colorScheme.primary,
              modifier = Modifier.size(24.dp))
      }
      XText(action.label,
            fontSize = 12.sp, fontWeight = FontWeight.Medium,
            color = MaterialTheme.colorScheme.onSurfaceVariant)
    }
  }
}
```

Icon mapping: Send → `Icons.Default.Send`, Receive → `Icons.Default.Download`, Pay → `Icons.Default.Payments`, Top Up → `Icons.Default.AddCircle`

---

#### InsightBanner — `components/InsightBanner.kt`

```
Row(
  modifier = Modifier
    .fillMaxWidth()
    .background(MaterialTheme.colorScheme.surface, RoundedCornerShape(24.dp))
    .border(1.dp, MaterialTheme.colorScheme.outlineVariant, RoundedCornerShape(24.dp))
    .padding(20.dp),
  verticalAlignment = Alignment.Top,
  horizontalArrangement = Arrangement.spacedBy(16.dp)
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
    XIcon(Icons.Default.Lightbulb, tint = MaterialTheme.colorScheme.primary)
  }
  Column {
    XText("Smart Insight", fontWeight = FontWeight.Bold,
          color = MaterialTheme.colorScheme.onSurface)
    XText(insight.message, fontSize = 14.sp,
          color = MaterialTheme.colorScheme.onSurfaceVariant,
          lineHeight = 22.75.sp)  // 1.625 × 14sp
  }
}
```

---

#### MonthlySummaryCard — `components/MonthlySummaryCard.kt`

> **Key note**: HTML uses a SINGLE combined progress bar (`h-3` = 12dp, `rounded-full`) with two fills side by side (income on left, expense on right). Not two separate bars. Income fill: 61.7%, expense fill: 38.3% (inline styles from HTML).

```
Column(
  modifier = Modifier
    .fillMaxWidth()
    .background(MaterialTheme.colorScheme.surface, RoundedCornerShape(24.dp))
    .border(1.dp, MaterialTheme.colorScheme.outlineVariant, RoundedCornerShape(24.dp))
    .padding(24.dp)
) {
  Row(
    modifier = Modifier.fillMaxWidth().padding(bottom = 24.dp),
    horizontalArrangement = Arrangement.SpaceBetween,
    verticalAlignment = Alignment.Bottom
  ) {
    Column {
      XText("INCOME", fontSize = 14.sp, fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            letterSpacing = 0.7.sp)
      XText(summary.formattedIncome, fontSize = 24.sp, fontWeight = FontWeight.Bold,
            color = XTheme.Colors.Success)
    }
    Column(horizontalAlignment = Alignment.End) {
      XText("EXPENSES", fontSize = 14.sp, fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            letterSpacing = 0.7.sp)
      XText(summary.formattedExpenses, fontSize = 24.sp, fontWeight = FontWeight.Bold,
            color = XTheme.Colors.Danger)
    }
  }

  // Combined split progress bar
  val total = summary.income + summary.expenses
  val incomeRatio = if (total > 0) (summary.income / total).toFloat() else 0f
  Box(
    modifier = Modifier
      .fillMaxWidth()
      .height(12.dp)
      .background(MaterialTheme.colorScheme.surfaceVariant, CircleShape)
      .clip(CircleShape)
  ) {
    Row(modifier = Modifier.fillMaxSize()) {
      Box(modifier = Modifier.fillMaxHeight().weight(incomeRatio)
            .background(XTheme.Colors.Success))
      Box(modifier = Modifier.fillMaxHeight().weight(1f - incomeRatio)
            .background(XTheme.Colors.Danger))
    }
  }
}
```

---

#### BudgetsSection — `components/BudgetsSection.kt`

> HTML: `grid grid-cols-2 gap-4`. Use chunked Row approach (nested lazy not allowed inside LazyColumn item).

```
Column(modifier = Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(16.dp)) {
  XText("Monthly Budgets", fontSize = 18.sp, fontWeight = FontWeight.Bold,
        color = MaterialTheme.colorScheme.onSurface)

  val rows = budgets.chunked(2)
  Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
    rows.forEach { rowItems ->
      Row(modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.spacedBy(16.dp)) {
        rowItems.forEach { budget ->
          BudgetCard(budget, modifier = Modifier.weight(1f))
        }
        if (rowItems.size == 1) Spacer(modifier = Modifier.weight(1f))
      }
    }
  }
}

// BudgetCard (private helper)
@Composable
private fun BudgetCard(budget: BudgetCategory, modifier: Modifier = Modifier) {
  val isOver = budget.spent > budget.total
  val accentColor = if (isOver) XTheme.Colors.Danger else MaterialTheme.colorScheme.primary
  val borderColor = if (isOver)
    XTheme.Colors.Danger.copy(alpha = 0.3f)
    else MaterialTheme.colorScheme.outlineVariant

  Column(
    modifier = modifier
      .background(MaterialTheme.colorScheme.surface, RoundedCornerShape(24.dp))
      .border(1.dp, borderColor, RoundedCornerShape(24.dp))
      .padding(16.dp),
    verticalArrangement = Arrangement.spacedBy(12.dp)
  ) {
    Row(modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween) {
      XText(budget.name, fontSize = 12.sp, fontWeight = FontWeight.Bold,
            color = if (isOver) XTheme.Colors.Danger
                    else MaterialTheme.colorScheme.onSurfaceVariant)
      XText("$${budget.spent.toInt()}/${budget.total.toInt()}", fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            color = if (isOver) XTheme.Colors.Danger
                    else MaterialTheme.colorScheme.onSurface)
    }
    // Progress bar (h-1.5 = 6dp)
    Box(
      modifier = Modifier.fillMaxWidth().height(6.dp)
        .background(MaterialTheme.colorScheme.surfaceVariant, CircleShape)
        .clip(CircleShape)
    ) {
      Box(
        modifier = Modifier.fillMaxHeight()
          .fillMaxWidth((budget.spent / budget.total).toFloat().coerceAtMost(1f))
          .background(accentColor, CircleShape)
      )
    }
  }
}
```

---

#### SavingsGoalsSection — `components/SavingsGoalsSection.kt`

```
Column(modifier = Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(16.dp)) {
  XText("Savings Goals", fontSize = 18.sp, fontWeight = FontWeight.Bold,
        color = MaterialTheme.colorScheme.onSurface)

  Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
    goals.forEach { goal -> SavingsGoalCard(goal) }
  }
}

// SavingsGoalCard (private)
@Composable
private fun SavingsGoalCard(goal: SavingsGoal) {
  val progress = (goal.current / goal.target).toFloat().coerceIn(0f, 1f)
  Column(
    modifier = Modifier
      .fillMaxWidth()
      .background(MaterialTheme.colorScheme.surface, RoundedCornerShape(24.dp))
      .border(1.dp, MaterialTheme.colorScheme.outlineVariant, RoundedCornerShape(24.dp))
      .padding(20.dp)
  ) {
    Row(
      modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp),
      horizontalArrangement = Arrangement.SpaceBetween,
      verticalAlignment = Alignment.CenterVertically
    ) {
      Row(horizontalArrangement = Arrangement.spacedBy(12.dp),
          verticalAlignment = Alignment.CenterVertically) {
        XIcon(icon = goal.icon, tint = MaterialTheme.colorScheme.primary)
        XText(goal.name, fontWeight = FontWeight.Bold,
              color = MaterialTheme.colorScheme.onSurface)
      }
      XText("${(progress * 100).roundToInt()}%",
            fontSize = 14.sp, fontWeight = FontWeight.Bold,
            color = XTheme.Colors.Success)
    }
    // Progress bar (h-2 = 8dp)
    Box(
      modifier = Modifier.fillMaxWidth().height(8.dp)
        .background(MaterialTheme.colorScheme.surfaceVariant, CircleShape)
        .clip(CircleShape)
    ) {
      Box(modifier = Modifier.fillMaxHeight().fillMaxWidth(progress)
            .background(XTheme.Colors.Success, CircleShape))
    }
  }
}
```

Goal icon mapping: Emergency Fund → `Icons.Default.Savings`, Vacation → `Icons.Default.Flight`

---

#### UpcomingBillsCard — `components/UpcomingBillsCard.kt`

> HTML: single card with `divide-y divide-outline-variant/30`. Implement as Column with `XHorizontalDivider(color = outlineVariant.copy(0.3f))` between rows.

```
Column(modifier = Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(16.dp)) {
  XText("Upcoming Bills", fontSize = 18.sp, fontWeight = FontWeight.Bold,
        color = MaterialTheme.colorScheme.onSurface)

  Column(
    modifier = Modifier
      .fillMaxWidth()
      .background(MaterialTheme.colorScheme.surface, RoundedCornerShape(24.dp))
      .border(1.dp, MaterialTheme.colorScheme.outlineVariant, RoundedCornerShape(24.dp))
      .clip(RoundedCornerShape(24.dp))
  ) {
    bills.forEachIndexed { index, bill ->
      BillRow(bill)
      if (index < bills.lastIndex) {
        XHorizontalDivider(
          color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f)
        )
      }
    }
  }
}

// BillRow (private)
@Composable
private fun BillRow(bill: UpcomingBill) {
  Row(
    modifier = Modifier.fillMaxWidth().padding(16.dp),
    horizontalArrangement = Arrangement.SpaceBetween,
    verticalAlignment = Alignment.CenterVertically
  ) {
    Row(horizontalArrangement = Arrangement.spacedBy(16.dp),
        verticalAlignment = Alignment.CenterVertically) {
      Box(
        modifier = Modifier
          .size(40.dp)
          .background(MaterialTheme.colorScheme.surfaceVariant, RoundedCornerShape(20.dp)),
        contentAlignment = Alignment.Center
      ) {
        XIcon(icon = bill.icon,
              tint = if (bill.isOverdue) XTheme.Colors.Danger
                     else MaterialTheme.colorScheme.onSurfaceVariant)
      }
      Column {
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically) {
          XText(bill.name, fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface)
          if (bill.isOverdue) {
            Box(
              modifier = Modifier
                .background(XTheme.Colors.Danger.copy(alpha = 0.2f), RoundedCornerShape(8.dp))
                .padding(horizontal = 6.dp, vertical = 2.dp)
            ) {
              XText("OVERDUE", fontSize = 10.sp, fontWeight = FontWeight.Bold,
                    color = XTheme.Colors.Danger)
            }
          }
        }
        if (!bill.isOverdue) {
          XText(bill.dueDate, fontSize = 12.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
      }
    }
    XText(bill.formattedAmount, fontWeight = FontWeight.Bold,
          color = if (bill.isOverdue) XTheme.Colors.Danger
                  else MaterialTheme.colorScheme.onSurface)
  }
}
```

Bill icon mapping: Netflix → `Icons.Default.Subscriptions`, Internet → `Icons.Default.Wifi`, Electricity → `Icons.Default.Bolt`

---

#### PortfolioSection — `components/PortfolioSection.kt`

> HTML: `grid grid-cols-3 gap-3`. Each asset card is a small Column with centered icon circle, symbol, and change %. BTC/SOL use `primary/10` circle; ETH uses `on-surface-variant/10` (intentional — negative performer gets muted circle).

```
Column(modifier = Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(16.dp)) {
  XText("Portfolio", fontSize = 18.sp, fontWeight = FontWeight.Bold,
        color = MaterialTheme.colorScheme.onSurface)

  val rows = assets.chunked(3)
  Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
    rows.forEach { rowItems ->
      Row(modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.spacedBy(12.dp)) {
        rowItems.forEach { asset ->
          PortfolioAssetCard(asset, modifier = Modifier.weight(1f))
        }
        repeat(3 - rowItems.size) { Spacer(modifier = Modifier.weight(1f)) }
      }
    }
  }
}

// PortfolioAssetCard (private)
@Composable
private fun PortfolioAssetCard(asset: PortfolioAsset, modifier: Modifier = Modifier) {
  val isPositive = asset.changePercent >= 0
  // ETH (negative) gets muted circle; others get gold circle
  val circleBackground = if (asset.symbol == "ETH")
    MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.1f)
    else MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.1f)
  val circleIconTint = if (asset.symbol == "ETH")
    MaterialTheme.colorScheme.onSurfaceVariant
    else MaterialTheme.colorScheme.primary

  Column(
    modifier = modifier
      .background(MaterialTheme.colorScheme.surface, RoundedCornerShape(24.dp))
      .border(1.dp, MaterialTheme.colorScheme.outlineVariant, RoundedCornerShape(24.dp))
      .padding(16.dp),
    horizontalAlignment = Alignment.CenterHorizontally,
    verticalArrangement = Arrangement.spacedBy(8.dp)
  ) {
    Box(
      modifier = Modifier
        .size(32.dp)
        .padding(bottom = 4.dp)
        .background(circleBackground, CircleShape),
      contentAlignment = Alignment.Center
    ) {
      XIcon(icon = asset.icon, tint = circleIconTint)
    }
    XText(asset.symbol, fontWeight = FontWeight.Bold,
          color = MaterialTheme.colorScheme.onSurface)
    XText(
      if (isPositive) "+${asset.changePercent}%" else "${asset.changePercent}%",
      fontSize = 12.sp, fontWeight = FontWeight.Bold,
      color = if (isPositive) XTheme.Colors.Success else XTheme.Colors.Danger
    )
  }
}
```

Asset icon mapping: BTC → `Icons.Default.CurrencyBitcoin`, ETH → `Icons.Default.CurrencyExchange`, SOL → `Icons.Default.Token`

---

#### RecentTransactionsSection — `components/RecentTransactionsSection.kt`

> **Key note**: Each transaction is its OWN card (`bg-surface rounded-2xl border`), NOT rows in a single card. Space between them is 12dp (`space-y-3`). No `XHorizontalDivider`.

```
Column(modifier = Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(16.dp)) {
  XText("Recent Transactions", fontSize = 18.sp, fontWeight = FontWeight.Bold,
        color = MaterialTheme.colorScheme.onSurface)

  Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
    transactions.forEach { tx -> TransactionCard(tx) }
  }
}

// TransactionCard (private)
@Composable
private fun TransactionCard(tx: Transaction) {
  val iconBackground = if (tx.isIncome)
    XTheme.Colors.Success.copy(alpha = 0.1f)
    else MaterialTheme.colorScheme.surfaceVariant
  val iconTint = if (tx.isIncome)
    XTheme.Colors.Success
    else MaterialTheme.colorScheme.onSurfaceVariant

  Row(
    modifier = Modifier
      .fillMaxWidth()
      .background(MaterialTheme.colorScheme.surface, RoundedCornerShape(24.dp))
      .border(1.dp, MaterialTheme.colorScheme.outlineVariant, RoundedCornerShape(24.dp))
      .padding(16.dp),
    horizontalArrangement = Arrangement.SpaceBetween,
    verticalAlignment = Alignment.CenterVertically
  ) {
    Row(horizontalArrangement = Arrangement.spacedBy(16.dp),
        verticalAlignment = Alignment.CenterVertically) {
      Box(
        modifier = Modifier.size(40.dp).background(iconBackground, CircleShape),
        contentAlignment = Alignment.Center
      ) {
        XIcon(icon = tx.icon, tint = iconTint)
      }
      Column {
        XText(tx.title, fontWeight = FontWeight.Bold,
              color = MaterialTheme.colorScheme.onSurface)
        XText(tx.subtitle, fontSize = 12.sp,
              color = MaterialTheme.colorScheme.onSurfaceVariant)
      }
    }
    XText(
      if (tx.isIncome) "+${tx.formattedAmount}" else tx.formattedAmount,
      fontWeight = FontWeight.Bold,
      color = if (tx.isIncome) XTheme.Colors.Success
              else MaterialTheme.colorScheme.onSurface
    )
  }
}
```

Transaction icon mapping: Salary → `Icons.Default.Work`, Starbucks → `Icons.Default.Coffee`, Amazon → `Icons.Default.ShoppingBag`, Freelance → `Icons.Default.LaptopMac`, Uber → `Icons.Default.DirectionsCar`

---

### Loading State

Shared screen — see: `.claude/docs/_shared/designs/loading.png`
Token inventory: `.claude/docs/_shared/designs/extracted/tokens_loading.md`

```
// → DashboardScreen.kt
Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
  XCircularProgressIndicator()
}
```

---

### Failed State

Shared screen — see: `.claude/docs/_shared/designs/failed.png`
Token inventory: `.claude/docs/_shared/designs/extracted/tokens_failed.md`

```
// → DashboardScreen.kt
Column(
  modifier = Modifier.fillMaxSize().padding(32.dp),
  horizontalAlignment = Alignment.CenterHorizontally,
  verticalArrangement = Arrangement.Center
) {
  XIcon(Icons.Default.Warning,
        tint = MaterialTheme.colorScheme.error,
        modifier = Modifier.size(80.dp))
  Spacer(Modifier.height(32.dp))
  XText("Something went wrong",
        fontSize = 20.sp, fontWeight = FontWeight.SemiBold,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        textAlign = TextAlign.Center,
        modifier = Modifier.fillMaxWidth())
  Spacer(Modifier.height(8.dp))
  XText("An unexpected error occurred. Please try again.",
        fontSize = 14.sp,
        color = MaterialTheme.colorScheme.outline,
        textAlign = TextAlign.Center,
        modifier = Modifier.fillMaxWidth().widthIn(max = 240.dp))
  Spacer(Modifier.height(32.dp))
  XButton(
    onClick = onRetry,
    modifier = Modifier.widthIn(max = 200.dp).height(56.dp),
    shape = RoundedCornerShape(12.dp)
  ) {
    XText("Retry", fontWeight = FontWeight.Bold, fontSize = 16.sp)
  }
}
```

---

## Pre-Implementation Contract

> Architecture rules, color rules, and X-component defaults are project-wide and live in their canonical sources — do not restate them here:
> - Architecture rules → [`_shared/patterns.md`](../../_shared/patterns.md)
> - Color rules → [`m3-colors.md`](m3-colors.md) (sections "Color Rules (Strict)" and "Complete M3 Role Catalog")
> - X-component default-render behavior → [`_shared/X_COMPONENTS_CATALOG.md`](../../_shared/X_COMPONENTS_CATALOG.md)
>
> This contract captures only feature-specific data the implementer cannot derive from those references.

### XTheme Updates Required

None — all design colors are already defined in `XDarkColors` / `XLightColors` or as `XTheme.Colors` custom extensions. No changes to `XTheme.kt` needed before implementation.

### Color Audit

#### Defined Roles
| Role | Hex | Usage |
|------|-----|-------|
| background | #0F0D09 | Screen + header background |
| surface | #1C1910 | All card backgrounds |
| surfaceVariant | #302B1C | Progress bar tracks, quick action buttons, expense icon circles |
| primary | #F5D76E | Balance amount, top strip, action icons, on-track progress fill |
| onPrimary | #2C1900 | Text on gold |
| primaryContainer | #4A3200 | Action button bg (@30%), insight icon bg (@10%), BTC/SOL circles (@10%) |
| onPrimaryContainer | #FFF0C0 | Available for chips/labels on container bg |
| onSurface | #EDE8D5 | Primary text throughout |
| onSurfaceVariant | #C4BA94 | Muted text, labels, due dates, ETH circle |
| outline | #726A48 | Card 1dp borders |
| outlineVariant | #3F3822 | Dividers (@30%), weaker card borders |
| error | #FFB4AB | Failed state icon |

#### Missing Roles
None.

#### Custom Colors (justified exceptions only)
| Name | Hex | Justification |
|------|-----|---------------|
| XTheme.Colors.Success | #4ADE80 | Income amounts, savings bars, positive %, income icon bg — no M3 "success" role |
| XTheme.Colors.Danger | #FF6B6B | Expense amounts, over-budget, OVERDUE badge, negative % — distinct from error (#FFB4AB) |

### Component Overrides (divergences from X-component defaults)

| Component | Property | HTML Value | X-component Default | Override Required |
|-----------|----------|------------|---------------------|-------------------|
| All cards | shape | RoundedCornerShape(24.dp) | XCard: 12.dp (medium) | Use custom Box/Column — do NOT use XCard |
| All cards | containerColor | surface (#1C1910) | XCard: surfaceVariant | Modifier.background(surface) explicitly |
| XScaffold | containerColor | background | XTheme.Colors.PaleLavender (undefined) | Pass containerColor = MaterialTheme.colorScheme.background |
| Quick action buttons | background | primaryContainer @ 30% | XIconButton: surface | Use raw Box, not XIconButton |
| XHorizontalDivider (bills) | color | outlineVariant @ 30% | outlineVariant (100%) | Pass color = outlineVariant.copy(alpha = 0.3f) |
| XButton (retry) | shape | RoundedCornerShape(12.dp) | CircleShape | Pass shape = RoundedCornerShape(12.dp) |
| Monthly summary bar | height | 12.dp (h-3) | N/A (custom) | Single split Box with two weight() fills |
| Budget progress bars | height | 6.dp (h-1.5) | N/A (custom) | Custom Box, not XLinearProgressIndicator |
| Savings progress bars | height | 8.dp (h-2) | N/A (custom) | Custom Box, not XLinearProgressIndicator |
| ETH portfolio circle | background | onSurfaceVariant @ 10% | N/A | Intentional — ETH is negative performer; muted circle |
| Insight icon circle | corner radius | 20dp (rounded-xl) | N/A | RoundedCornerShape(20.dp), not CircleShape |

---

## Post-Implementation Checklist

- [ ] `XScaffold` called with `containerColor = MaterialTheme.colorScheme.background` (not default)
- [ ] No `XCard` used — all cards are Box/Column with `Modifier.background(surface, RoundedCornerShape(24.dp)) + border(1.dp, outline, ...) + clip(...)`
- [ ] Header is a sticky Box OUTSIDE the `LazyColumn`, not `XTopAppBar`
- [ ] Monthly summary uses a SINGLE split progress bar (Row with two weight() fills, 12dp height), not two separate bars
- [ ] Budget section uses 2-column chunked Row layout, not LazyVerticalGrid (nesting lazy not allowed)
- [ ] Portfolio section uses 3-column chunked Row layout, not LazyVerticalGrid
- [ ] Each transaction is its OWN card (no XHorizontalDivider between transactions)
- [ ] Bills section uses single card + `XHorizontalDivider(color = outlineVariant.copy(0.3f))` between rows
- [ ] ETH portfolio circle uses `onSurfaceVariant.copy(0.1f)` bg (not primaryContainer/10)
- [ ] Expense transaction icon circles use `surfaceVariant` bg (not success/10)
- [ ] Income transaction icon circles use `XTheme.Colors.Success.copy(0.1f)` bg
- [ ] Quick action circles use `primaryContainer.copy(0.3f)` + `border(outlineVariant)` — not XIconButton
- [ ] XButton (retry) has `shape = RoundedCornerShape(12.dp)` override
- [ ] All `XTheme.Colors.*` usages: `Success` and `Danger` only — no raw Color() hex
- [ ] All M3 colors use `MaterialTheme.colorScheme.{role}` — no hardcoded Color()
- [ ] Build passes: `./gradlew :feature:dashboard:assembleAndroidMain`
- [ ] Code formatted: `./gradlew :feature:dashboard:ktlintFormat`
