package thisissadeghi.dashboard.presentation.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import thisissadeghi.dashboard.data.model.BudgetCategory
import thisissadeghi.designsystem.XText
import thisissadeghi.designsystem.XTheme

@Composable
internal fun MonthlyBudgets(categories: List<BudgetCategory>) {
    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        XText(
            "Monthly Budgets",
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface,
        )
        Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
            categories.chunked(2).forEach { row ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                ) {
                    row.forEach { budget -> BudgetCard(budget, Modifier.weight(1f)) }
                    if (row.size < 2) Spacer(Modifier.weight(1f))
                }
            }
        }
    }
}

@Preview
@Composable
private fun MonthlyBudgetsPreview() {
    XTheme {
        MonthlyBudgets(
            categories =
                listOf(
                    BudgetCategory(name = "Food", spent = 320.0, total = 500.0, currency = "$"),
                    BudgetCategory(name = "Transport", spent = 145.0, total = 200.0, currency = "$"),
                    BudgetCategory(name = "Entertainment", spent = 220.0, total = 150.0, currency = "$"),
                    BudgetCategory(name = "Shopping", spent = 80.0, total = 300.0, currency = "$"),
                ),
        )
    }
}
