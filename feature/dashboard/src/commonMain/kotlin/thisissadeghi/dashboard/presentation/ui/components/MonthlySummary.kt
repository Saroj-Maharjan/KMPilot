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
import kmpilot.feature.dashboard.generated.resources.Res
import kmpilot.feature.dashboard.generated.resources.expenses_label
import kmpilot.feature.dashboard.generated.resources.income_label
import org.jetbrains.compose.resources.stringResource
import thisissadeghi.dashboard.presentation.ui.formatMoney
import thisissadeghi.designsystem.XText
import thisissadeghi.designsystem.XTheme
import thisissadeghi.dashboard.data.model.MonthlySummary as MonthlySummaryData

@Composable
internal fun MonthlySummary(summary: MonthlySummaryData) {
    val total = summary.income + summary.expenses
    val incomeRatio = if (total > 0) (summary.income / total).toFloat().coerceIn(0.001f, 0.999f) else 0.5f
    val expenseRatio = 1f - incomeRatio

    Box(
        modifier =
            Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.surface, RoundedCornerShape(24.dp))
                .border(1.dp, MaterialTheme.colorScheme.outlineVariant, RoundedCornerShape(24.dp))
                .padding(24.dp),
    ) {
        Column {
            Row(
                modifier = Modifier.fillMaxWidth().padding(bottom = 24.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Bottom,
            ) {
                Column {
                    XText(
                        stringResource(Res.string.income_label),
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        letterSpacing = 0.7.sp,
                    )
                    XText(
                        "${summary.currency}${summary.income.formatMoney()}",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = XTheme.Colors.Success,
                    )
                }
                Column(horizontalAlignment = Alignment.End) {
                    XText(
                        stringResource(Res.string.expenses_label),
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        letterSpacing = 0.7.sp,
                    )
                    XText(
                        "${summary.currency}${summary.expenses.formatMoney()}",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = XTheme.Colors.Danger,
                    )
                }
            }
            Row(
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .height(12.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.surfaceVariant),
            ) {
                Box(
                    modifier =
                        Modifier
                            .fillMaxHeight()
                            .weight(incomeRatio)
                            .clip(RoundedCornerShape(topStart = 50.dp, bottomStart = 50.dp))
                            .background(XTheme.Colors.Success),
                )
                Box(
                    modifier =
                        Modifier
                            .fillMaxHeight()
                            .weight(expenseRatio)
                            .background(XTheme.Colors.Danger),
                )
            }
        }
    }
}

@Preview
@Composable
private fun MonthlySummaryPreview() {
    XTheme {
        MonthlySummary(
            summary =
                MonthlySummaryData(
                    monthName = "May",
                    income = 6_200.0,
                    expenses = 3_840.0,
                    currency = "$",
                ),
        )
    }
}
