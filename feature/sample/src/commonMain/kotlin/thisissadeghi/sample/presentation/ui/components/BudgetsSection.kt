package thisissadeghi.sample.presentation.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import thisissadeghi.designsystem.XText
import thisissadeghi.designsystem.XTheme
import thisissadeghi.sample.data.model.BudgetCategory

@Composable
internal fun BudgetsSection(categories: List<BudgetCategory>) {
    Column(modifier = Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(16.dp)) {
        XText(
            "Monthly Budgets",
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface,
        )
        val rows = categories.chunked(2)
        Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
            rows.forEach { rowItems ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                ) {
                    rowItems.forEach { budget ->
                        BudgetCard(budget, modifier = Modifier.weight(1f))
                    }
                    if (rowItems.size == 1) {
                        androidx.compose.foundation.layout
                            .Spacer(modifier = Modifier.weight(1f))
                    }
                }
            }
        }
    }
}

@Composable
private fun BudgetCard(
    budget: BudgetCategory,
    modifier: Modifier = Modifier,
) {
    val isOver = budget.isOverBudget
    val accentColor = if (isOver) XTheme.Colors.Danger else MaterialTheme.colorScheme.primary
    val borderColor =
        if (isOver) {
            XTheme.Colors.Danger.copy(alpha = 0.3f)
        } else {
            MaterialTheme.colorScheme.outlineVariant
        }

    Column(
        modifier =
            modifier
                .background(MaterialTheme.colorScheme.surface, RoundedCornerShape(24.dp))
                .border(1.dp, borderColor, RoundedCornerShape(24.dp))
                .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            XText(
                budget.name,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = if (isOver) XTheme.Colors.Danger else MaterialTheme.colorScheme.onSurfaceVariant,
            )
            XText(
                "$${budget.spent.toInt()}/${budget.total.toInt()}",
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = if (isOver) XTheme.Colors.Danger else MaterialTheme.colorScheme.onSurface,
            )
        }
        Box(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .height(6.dp)
                    .background(MaterialTheme.colorScheme.surfaceVariant, CircleShape)
                    .clip(CircleShape),
        ) {
            Box(
                modifier =
                    Modifier
                        .fillMaxHeight()
                        .fillMaxWidth(budget.progress.coerceAtMost(1f))
                        .background(accentColor, CircleShape),
            )
        }
    }
}
