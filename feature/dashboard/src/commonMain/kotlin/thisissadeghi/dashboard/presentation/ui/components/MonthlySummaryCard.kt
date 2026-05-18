package thisissadeghi.dashboard.presentation.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import thisissadeghi.designsystem.XText
import thisissadeghi.designsystem.XTheme
import thisissadeghi.dashboard.data.model.MonthlySummary
import thisissadeghi.dashboard.presentation.ui.formatMoney

@Composable
internal fun MonthlySummaryCard(summary: MonthlySummary) {
    Column(
        modifier =
            Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.surface, RoundedCornerShape(24.dp))
                .border(1.dp, MaterialTheme.colorScheme.outlineVariant, RoundedCornerShape(24.dp))
                .padding(24.dp),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(bottom = 24.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Bottom,
        ) {
            Column {
                XText(
                    "INCOME",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    letterSpacing = 0.7.sp,
                )
                XText(
                    "$${summary.income.formatMoney()}",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = XTheme.Colors.Success,
                )
            }
            Column(horizontalAlignment = Alignment.End) {
                XText(
                    "EXPENSES",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    letterSpacing = 0.7.sp,
                )
                XText(
                    "$${summary.expenses.formatMoney()}",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = XTheme.Colors.Danger,
                )
            }
        }
        val total = summary.income + summary.expenses
        val incomeRatio = if (total > 0) (summary.income / total).toFloat().coerceIn(0.001f, 0.999f) else 0.5f
        Box(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .height(12.dp)
                    .background(MaterialTheme.colorScheme.surfaceVariant, CircleShape)
                    .clip(CircleShape),
        ) {
            Row(modifier = Modifier.fillMaxSize()) {
                Box(
                    modifier =
                        Modifier
                            .fillMaxHeight()
                            .weight(incomeRatio)
                            .background(XTheme.Colors.Success),
                )
                Box(
                    modifier =
                        Modifier
                            .fillMaxHeight()
                            .weight(1f - incomeRatio)
                            .background(XTheme.Colors.Danger),
                )
            }
        }
    }
}
