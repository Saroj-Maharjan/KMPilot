# Compose Implementation Blueprint: Sample (KMPilot Dashboard)

> Source: Stitch HTML export — 3 state screens (success, loading, failed)
> Style: **Decomposed** (HTML >150 lines, >9 visual sections)

---

## Design Tokens

| Hex | M3 Role / Custom | Usage |
|-----|-----------------|-------|
| #0D0919 | `background` | Screen background |
| #181228 | `surface` | All card backgrounds |
| #9D70FF | `primary` | Accent: amounts, icons, buttons |
| #1A0054 | `onPrimary` | Text on primary button |
| #E9E0FF | `onBackground` / `onSurface` | Primary text |
| #C5BCE0 | `onSurfaceVariant` | Muted/subtitle text |
| #1E1A2E | `outlineVariant` | Progress track, dividers |
| #FFB4AB | `error` | Error accent bar + icon |
| #4ADE80 | `XTheme.Colors.Success` | Income, savings, on-track |
| #FF6B6B | `XTheme.Colors.Danger` | Over-budget, overdue, expenses |

---

## Typography Scale

| Usage | Size (sp) | Weight | Notes |
|-------|-----------|--------|-------|
| "Good morning" subtitle | 14 | Medium (500) | `onSurfaceVariant` |
| "Dashboard" screen title | 24 | ExtraBold (800) | `onBackground` |
| Section titles ("Budgets", etc.) | 18 | Bold (700) | `onSurface` |
| Balance amount | 38 | ExtraBold (800) | `primary` |
| Balance label ("Total Net Worth") | 10 | ExtraBold (800) | uppercase, wide letter-spacing, `onSurfaceVariant` |
| Card item title | 14 | Bold (700) | `onSurface` |
| Card item subtitle | 12 | Regular (400) | `onSurfaceVariant` |
| Trend/badge text | 12 | Bold (700) | `Success` |
| OVER/OVERDUE badge | 10 | Black (900) | uppercase |
| Change percentage | 10 | Bold (700) | `Success` or `Danger` |
| Quick action label | 12 | Medium (500) | `onSurfaceVariant` |
| "View All" | 12 | Bold (700) | `primary` |
| "Loading dashboard..." | 14 | Medium (500) | wide letter-spacing, `onSurfaceVariant` |
| Error title | 20 | Bold (700) | `onSurface`, textAlign=Center |
| Error body | 14 | Regular | `onSurfaceVariant`, textAlign=Center |
| Button label | 14 | Bold (700) | `onPrimary` |

---

## Spacing Grid

| Context | Value (dp) |
|---------|------------|
| Screen horizontal padding | 16 |
| Header top padding | 32 |
| Header bottom padding | 8 |
| Section gap (between LazyColumn items) | 24 |
| Card internal padding | 16 |
| Balance card internal padding | 24 |
| Row item gap (icon + text) | 12 |
| Progress bar height (summary) | 6 |
| Progress bar height (savings, budget track) | 8 |
| Card top accent bar height | 3 |
| Section header bottom gap | 16 |
| Quick action icon size | 56 × 56 |
| Quick action icon inner size | 24 |
| Category icon container size | 40 × 40 |
| Portfolio/tx icon container | 40 × 40 |
| Error icon size | 24 |
| Loading spinner size | 48 × 48 |
| Loading spinner stroke width | 4 |

---

## Border Radius (from tailwind config)

```
"DEFAULT": 0.5rem = 8dp
"lg": 1rem = 16dp
"xl": 1.5rem = 24dp
"full": CircleShape
"rounded-2xl" (not in config): standard Tailwind = 16dp
```

**Failed screen config**: `"card": 12px = 12dp`  — error card uses 12dp radius

---

## Component Tree

### Shared Scaffold (all states)

No `XTopAppBar`. Entire screen is a `Box` background + `Column`:

```kotlin
Box(Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background)) {
    Column(Modifier.fillMaxSize()) {
        DashboardHeader()         // "Good morning" + "Dashboard" inline
        [state-specific content]  // fills remaining space
    }
}
```

#### `DashboardHeader` (shared across all states)

```kotlin
Column(
    modifier = Modifier
        .fillMaxWidth()
        .padding(start = 16.dp, end = 16.dp, top = 32.dp, bottom = 8.dp)
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
```

---

### Success State

`LazyColumn` filling remaining space after the header:

```kotlin
LazyColumn(
    modifier = Modifier.fillMaxSize(),
    contentPadding = PaddingValues(horizontal = 16.dp, bottom = 48.dp),
    verticalArrangement = Arrangement.spacedBy(24.dp),
) {
    item { NetWorthCard(data.accountBalance) }
    item { QuickActionsRow(data.quickActions, onActionClick) }
    item { SmartInsightBanner(data.spendingInsight) }
    item { MonthlySummarySection(data.monthlySummary) }
    item { BudgetsSection(data.budgetCategories, onViewAllClick) }
    item { SavingsGoalsSection(data.savingsGoals) }
    item { UpcomingBillsSection(data.upcomingBills) }
    item { PortfolioSection(data.portfolioAssets) }
    item { RecentTransactionsSection(data.recentTransactions) }
}
```

---

#### `NetWorthCard`

Design: XCard with 3dp primary top accent bar, wallet icon watermark at 10% opacity top-right, "Total Net Worth" label, balance amount in primary, trend chip.

```kotlin
XCard(
    modifier = Modifier.fillMaxWidth(),
    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
    elevation = CardDefaults.cardElevation(0.dp),
    shape = RoundedCornerShape(24.dp),  // rounded-xl = 1.5rem = 24dp
) {
    Box {
        // Wallet icon watermark — absolute top-right, 10% opacity
        XIcon(
            imageVector = Icons.Default.AccountBalanceWallet,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.10f),
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(16.dp)
                .size(64.dp),
        )
        Column {
            // 3dp primary top accent bar
            Box(
                Modifier
                    .fillMaxWidth()
                    .height(3.dp)
                    .background(MaterialTheme.colorScheme.primary)
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
                    text = "$${balance.totalBalance.formatMoney()}",
                    fontSize = 38.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = MaterialTheme.colorScheme.primary,
                )
                Spacer(Modifier.height(16.dp))
                // Trend chip
                Row(
                    modifier = Modifier
                        .background(
                            XTheme.Colors.Success.copy(alpha = 0.10f),
                            RoundedCornerShape(50),
                        )
                        .border(
                            1.dp,
                            XTheme.Colors.Success.copy(alpha = 0.20f),
                            RoundedCornerShape(50),
                        )
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
                        text = "+${balance.changePercent}% trend",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = XTheme.Colors.Success,
                    )
                }
            }
        }
    }
}
```

---

#### `QuickActionsRow`

Design: Row of 4 actions. Each action = Column(icon box + label). Icon container is 56×56dp, rounded-2xl (16dp, standard Tailwind not in config), background=primary@15%, border=primary@20%.

```kotlin
@Composable
fun QuickActionsRow(actions: List<QuickAction>, onActionClick: (String) -> Unit) {
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
private fun QuickActionItem(action: QuickAction, onClick: () -> Unit) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Box(
            modifier = Modifier
                .size(56.dp)
                .background(
                    MaterialTheme.colorScheme.primary.copy(alpha = 0.15f),
                    RoundedCornerShape(16.dp),  // rounded-2xl standard = 16dp
                )
                .border(
                    1.dp,
                    MaterialTheme.colorScheme.primary.copy(alpha = 0.20f),
                    RoundedCornerShape(16.dp),
                )
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
```

---

#### `SmartInsightBanner`

Design: XCard with success@5% background, success@20% border, lightbulb icon in green box, title + message.

```kotlin
XCard(
    modifier = Modifier.fillMaxWidth(),
    colors = CardDefaults.cardColors(
        containerColor = XTheme.Colors.Success.copy(alpha = 0.05f)
    ),
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
            modifier = Modifier
                .size(40.dp)
                .background(XTheme.Colors.Success.copy(alpha = 0.20f), RoundedCornerShape(16.dp)),
            contentAlignment = Alignment.Center,
        ) {
            XIcon(
                imageVector = Icons.Default.Lightbulb,
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
                text = insight.message,
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}
```

---

#### `MonthlySummarySection`

Design: "Monthly Summary" heading + XCard with 2 progress rows.

```kotlin
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
                amount = summary.income,
                fraction = 0.85f,  // from data
                color = XTheme.Colors.Success,
            )
            SummaryProgressRow(
                label = "Expenses Used",
                amount = summary.expenses,
                fraction = summary.expenses / summary.income,
                color = XTheme.Colors.Danger,
            )
        }
    }
}
```

`SummaryProgressRow`:
```kotlin
Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
        XText(label, 12.sp, Medium, color = onSurfaceVariant)
        XText("$${amount.formatMoney()}", 12.sp, Bold, color = color)
    }
    // Progress track
    Box(Modifier.fillMaxWidth().height(6.dp).clip(CircleShape)
            .background(MaterialTheme.colorScheme.outlineVariant)) {
        Box(Modifier.fillMaxWidth(fraction).fillMaxHeight().background(color))
    }
}
```

---

#### `BudgetsSection`

Design: Row header "Budgets" + "View All", then vertical list of BudgetItem cards (gap=12dp).

```kotlin
Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically) {
        XText("Budgets", 18.sp, Bold, color = onSurface)
        XText("View All", 12.sp, Bold, color = primary)
    }
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        budgets.forEach { BudgetItem(it) }
    }
}
```

`BudgetItem` — Card with 4dp left accent border. Use `Modifier.drawBehind` or a left-bordered Box:

```kotlin
// IMPORTANT: XCard does not support border-l-4 natively.
// Implement as Row: Box(4dp wide, bg=accentColor) + Column(content, weight=1f)
// Wrap in a Box with surface background and 24dp corner radius.
Box(
    modifier = Modifier
        .fillMaxWidth()
        .background(MaterialTheme.colorScheme.surface, RoundedCornerShape(24.dp))
        .clip(RoundedCornerShape(24.dp)),
) {
    Row {
        // Left accent bar
        Box(
            Modifier
                .width(4.dp)
                .height(72.dp)   // match card height
                .background(if (category.isOverBudget) XTheme.Colors.Danger else MaterialTheme.colorScheme.primary)
        )
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically) {
                val accentColor = if (category.isOverBudget) XTheme.Colors.Danger else MaterialTheme.colorScheme.primary
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .background(accentColor.copy(alpha = 0.15f), RoundedCornerShape(16.dp)),
                    contentAlignment = Alignment.Center,
                ) {
                    XIcon(budgetIcon(category.name), 24.dp, tint = accentColor)
                }
                Column {
                    XText(category.name, 14.sp, Bold, color = onSurface)
                    XText(
                        "$${category.spent.formatMoney()} of $${category.total.formatMoney()}",
                        12.sp, color = onSurfaceVariant,
                    )
                }
            }
            // Status badge
            if (category.isOverBudget) {
                Box(
                    modifier = Modifier
                        .background(XTheme.Colors.Danger, RoundedCornerShape(8.dp))
                        .padding(horizontal = 8.dp, vertical = 4.dp),
                ) {
                    XText("OVER", 10.sp, FontWeight.Black, color = Color.White)
                }
            } else {
                XText("On track", 10.sp, Bold, color = XTheme.Colors.Success)
            }
        }
    }
}
```

---

#### `SavingsGoalsSection`

Design: "Savings Goals" heading, vertical list of SavingsGoalItem cards (gap=12dp). Each card: XCard with outlineVariant border, goal name + % on same row, amount below, 8dp progress bar.

```kotlin
Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
    XText("Savings Goals", 18.sp, Bold, color = onSurface)
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        goals.forEach { SavingsGoalItem(it) }
    }
}
```

`SavingsGoalItem`:
```kotlin
XCard(
    modifier = Modifier.fillMaxWidth(),
    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
    shape = RoundedCornerShape(24.dp),
    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
    elevation = CardDefaults.cardElevation(0.dp),
) {
    Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Row(Modifier.fillMaxWidth(), Arrangement.SpaceBetween, Alignment.Top) {
            Column {
                XText(goal.name, 14.sp, Bold, color = onSurface)
                XText(
                    "$${goal.current.formatMoney()} of $${goal.target.formatMoney()}",
                    10.sp, color = onSurfaceVariant,
                )
            }
            XText(
                "${(goal.progress * 100).toInt()}%",
                14.sp, ExtraBold, color = XTheme.Colors.Success,
            )
        }
        // Progress bar (8dp)
        Box(Modifier.fillMaxWidth().height(8.dp).clip(CircleShape)
                .background(MaterialTheme.colorScheme.outlineVariant)) {
            Box(Modifier.fillMaxWidth(goal.progress).fillMaxHeight()
                    .background(XTheme.Colors.Success))
        }
    }
}
```

---

#### `UpcomingBillsSection`

Design: "Upcoming Bills" heading, vertical list of BillItem (gap=12dp). Each item: card with 4dp left accent (Danger for overdue, outlineVariant color for upcoming).

```kotlin
Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
    XText("Upcoming Bills", 18.sp, Bold, color = onSurface)
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        bills.forEach { BillItem(it) }
    }
}
```

`BillItem` — Same left-accent pattern as BudgetItem:
```kotlin
Box(
    modifier = Modifier
        .fillMaxWidth()
        .background(MaterialTheme.colorScheme.surface, RoundedCornerShape(24.dp))
        .clip(RoundedCornerShape(24.dp)),
) {
    Row {
        val accentColor = if (bill.isOverdue) XTheme.Colors.Danger else MaterialTheme.colorScheme.outlineVariant
        Box(Modifier.width(4.dp).matchParentSize().background(accentColor))
        Row(
            modifier = Modifier.fillMaxWidth().padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Row(Arrangement.spacedBy(12.dp), Alignment.CenterVertically) {
                val iconTint = if (bill.isOverdue) XTheme.Colors.Danger else MaterialTheme.colorScheme.onSurfaceVariant
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .background(iconTint.copy(alpha = 0.10f), RoundedCornerShape(16.dp)),
                    contentAlignment = Alignment.Center,
                ) {
                    XIcon(billIcon(bill.name), 24.dp, tint = iconTint)
                }
                Column {
                    XText(bill.name, 14.sp, Bold, color = onSurface)
                    if (bill.isOverdue) {
                        XText("OVERDUE", 12.sp, Bold, color = XTheme.Colors.Danger)
                    } else {
                        XText("Due ${bill.dueDate}", 12.sp, color = onSurfaceVariant)
                    }
                }
            }
            XText("$${bill.amount.formatMoney()}", 16.sp, Bold, color = onSurface)
        }
    }
}
```

---

#### `PortfolioSection`

Design: "Portfolio Assets" heading + single XCard containing all items separated by XHorizontalDividers.

```kotlin
Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
    XText("Portfolio Assets", 18.sp, Bold, color = onSurface)
    XCard(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        shape = RoundedCornerShape(24.dp),
        elevation = CardDefaults.cardElevation(0.dp),
    ) {
        Column {
            assets.forEachIndexed { index, asset ->
                PortfolioAssetItem(asset)
                if (index < assets.lastIndex) {
                    XHorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
                }
            }
        }
    }
}
```

`PortfolioAssetItem`:
```kotlin
Row(
    modifier = Modifier.fillMaxWidth().padding(16.dp),
    horizontalArrangement = Arrangement.spacedBy(12.dp),
    verticalAlignment = Alignment.CenterVertically,
) {
    // Circle avatar with symbol — opacity varies per rank: BTC=1.0, ETH=0.8, SOL=0.6
    val opacity = when (index) { 0 -> 1.0f; 1 -> 0.8f; else -> 0.6f }
    Box(
        modifier = Modifier
            .size(40.dp)
            .background(
                MaterialTheme.colorScheme.primary.copy(alpha = opacity),
                CircleShape,
            ),
        contentAlignment = Alignment.Center,
    ) {
        XText(asset.symbol.take(3), 12.sp, Bold, color = Color.White)
    }
    Column(modifier = Modifier.weight(1f)) {
        XText(asset.name, 14.sp, Bold, color = onSurface)
        XText("${asset.balance} ${asset.symbol}", 12.sp, color = onSurfaceVariant)
    }
    Column(horizontalAlignment = Alignment.End) {
        XText("$${asset.value.formatMoney()}", 14.sp, Bold, color = onSurface)
        val changeColor = if (asset.changePercent >= 0) XTheme.Colors.Success else XTheme.Colors.Danger
        val sign = if (asset.changePercent >= 0) "+" else ""
        XText("$sign${asset.changePercent}%", 10.sp, Bold, color = changeColor)
    }
}
```

---

#### `RecentTransactionsSection`

Design: Row header "Recent Transactions" + filter icon, then single XCard with items and dividers.

```kotlin
Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
    Row(Modifier.fillMaxWidth(), Arrangement.SpaceBetween, Alignment.CenterVertically) {
        XText("Recent Transactions", 18.sp, Bold, color = onSurface)
        XIcon(Icons.Default.Tune, 24.dp, tint = onSurfaceVariant)
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
                    XHorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
                }
            }
        }
    }
}
```

`TransactionItem`:
```kotlin
Row(
    modifier = Modifier.fillMaxWidth().padding(16.dp),
    horizontalArrangement = Arrangement.spacedBy(12.dp),
    verticalAlignment = Alignment.CenterVertically,
) {
    // Icon box — unique background per category
    Box(
        modifier = Modifier
            .size(40.dp)
            .background(categoryBackground(tx.category), RoundedCornerShape(16.dp)),
        contentAlignment = Alignment.Center,
    ) {
        // For Netflix: "N" text in red italic; for others: category icon
        if (tx.category == "streaming") {
            XText("N", 14.sp, Black, color = Color.Red, fontStyle = FontStyle.Italic)
        } else {
            XIcon(categoryIcon(tx.category), 24.dp, tint = categoryTint(tx.category))
        }
    }
    Column(modifier = Modifier.weight(1f)) {
        XText(tx.title, 14.sp, Bold, color = onSurface, maxLines = 1, overflow = Ellipsis)
        XText("${tx.category} • ${tx.date}", 12.sp, color = onSurfaceVariant)
    }
    val amountColor = if (tx.isIncome) XTheme.Colors.Success else MaterialTheme.colorScheme.onSurface
    val sign = if (tx.isIncome) "+" else "-"
    XText("$sign$${tx.amount.formatMoney()}", 14.sp, Bold, color = amountColor)
}
```

Category backgrounds from design:
- "streaming" / Netflix: `Color(0xFFDC2626).copy(alpha=0.20f)` (red-600/20)
- "income" / Salary: `XTheme.Colors.Success.copy(alpha=0.20f)`
- "food" / Coffee: `MaterialTheme.colorScheme.surfaceVariant.copy(alpha=0.30f)`

---

### Loading State

```kotlin
// After DashboardHeader in the same Box > Column
Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(24.dp),
    ) {
        // Manual spinner: 48dp box, 4dp stroke, primary color, primary@20 track
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
```

---

### Failed State

```kotlin
// After DashboardHeader in the same Box > Column
Box(
    modifier = Modifier
        .fillMaxSize()
        .padding(horizontal = 24.dp),
    contentAlignment = Alignment.Center,
) {
    // Error card — rounded-card = 12dp (failed screen custom config)
    XCard(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        shape = RoundedCornerShape(12.dp),  // rounded-card from failed screen config
        elevation = CardDefaults.cardElevation(0.dp),
    ) {
        Column {
            // Error accent bar (3dp, error color)
            Box(
                Modifier
                    .fillMaxWidth()
                    .height(3.dp)
                    .background(MaterialTheme.colorScheme.error)
            )
            Column(
                modifier = Modifier.padding(32.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                XIcon(
                    imageVector = Icons.Default.Error,
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
                    text = "Unable to load your dashboard. Please try again.",
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth(),
                )
                Spacer(Modifier.height(16.dp))
                XButton(
                    onClick = onRetry,
                    modifier = Modifier.fillMaxWidth(),
                    // Primary background, onPrimary text
                    // rounded-xl standard = 12dp (not in failed config, use standard)
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
```

---

## Icon Mappings

| Context | Icon |
|---------|------|
| Quick Action: Send | `Icons.AutoMirrored.Filled.Send` |
| Quick Action: Receive | `Icons.Filled.CallReceived` |
| Quick Action: Pay | `Icons.Filled.Payments` |
| Quick Action: Top Up | `Icons.Filled.AddCircle` |
| Balance watermark | `Icons.Filled.AccountBalanceWallet` |
| Smart Insight | `Icons.Filled.Lightbulb` |
| Budget: Shopping | `Icons.Filled.ShoppingBag` |
| Budget: Dining | `Icons.Filled.Restaurant` |
| Bill: Internet | `Icons.Filled.Wifi` |
| Bill: Electricity | `Icons.Filled.ElectricBolt` |
| Error | `Icons.Filled.Error` |
| Trend up | `Icons.AutoMirrored.Filled.TrendingUp` |
| Transaction: Income | `Icons.Filled.Work` |
| Transaction: Food | `Icons.Filled.LocalCafe` |
| Filter | `Icons.Filled.Tune` |

---

## Key Changes From Current Implementation

1. **Remove `XScaffold` + `XTopAppBar`** — Replace with plain `Box(background)` + `Column` + inline `DashboardHeader`
2. **Balance card**: Change label to "Total Net Worth", show trend chip (not change line), add wallet icon watermark
3. **Quick actions**: Increase size to 56×56dp squares with 16dp radius; adjust spacing to `SpaceEvenly`
4. **Section order**: NetWorth → QuickActions → SmartInsight → MonthlySummary → Budgets → SavingsGoals → UpcomingBills → Portfolio → Transactions
5. **Replace `IncomeGreen`/`ExpenseRed` with `XTheme.Colors.Success`/`XTheme.Colors.Danger`**
6. **Budgets**: Add card with left-accent border pattern (4dp colored bar) + icon
7. **Bills**: Add card with left-accent border + icon
8. **Portfolio**: Wrap all items in single XCard with dividers
9. **Transactions**: Wrap all items in single XCard with dividers
10. **All section cards**: Use `RoundedCornerShape(24.dp)` (not medium 12dp)
11. **Section headers**: Remove divider line; use plain `XText(18sp, Bold)` + optional "View All" right label
12. **Loading state**: Keep `XCircularProgressIndicator` with primary color; keep "Loading dashboard..." text
13. **Error state**: Use `RoundedCornerShape(12.dp)` for error card; show error icon above title; use `XButton` full-width
