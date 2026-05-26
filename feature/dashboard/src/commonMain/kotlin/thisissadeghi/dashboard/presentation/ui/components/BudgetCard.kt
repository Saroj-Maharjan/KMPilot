package thisissadeghi.dashboard.presentation.ui.components

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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import thisissadeghi.dashboard.data.model.BudgetCategory
import thisissadeghi.designsystem.XText
import thisissadeghi.designsystem.XTheme

@Composable
internal fun BudgetCard(
    budget: BudgetCategory,
    modifier: Modifier = Modifier,
) {
    val isOverBudget = budget.isOverBudget
    Box(
        modifier =
            modifier
                .background(MaterialTheme.colorScheme.surface, RoundedCornerShape(24.dp))
                .border(
                    1.dp,
                    if (isOverBudget) {
                        XTheme.Colors.Danger.copy(alpha = 0.3f)
                    } else {
                        MaterialTheme.colorScheme.outlineVariant
                    },
                    RoundedCornerShape(24.dp),
                ).padding(16.dp),
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                XText(
                    budget.name,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color =
                        if (isOverBudget) {
                            XTheme.Colors.Danger
                        } else {
                            MaterialTheme.colorScheme.onSurfaceVariant
                        },
                )
                XText(
                    "$${budget.spent.toInt()}/${budget.total.toInt()}",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color =
                        if (isOverBudget) {
                            XTheme.Colors.Danger
                        } else {
                            MaterialTheme.colorScheme.onSurface
                        },
                )
            }
            Box(
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .height(6.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.surfaceVariant),
            ) {
                Box(
                    modifier =
                        Modifier
                            .fillMaxHeight()
                            .fillMaxWidth(minOf(budget.progress, 1f))
                            .clip(CircleShape)
                            .background(
                                if (isOverBudget) {
                                    XTheme.Colors.Danger
                                } else {
                                    MaterialTheme.colorScheme.primary
                                },
                            ),
                )
            }
        }
    }
}

@Preview
@Composable
private fun BudgetCardPreview() {
    XTheme {
        BudgetCard(
            budget = BudgetCategory(name = "Entertainment", spent = 220.0, total = 150.0, currency = "$"),
        )
    }
}
