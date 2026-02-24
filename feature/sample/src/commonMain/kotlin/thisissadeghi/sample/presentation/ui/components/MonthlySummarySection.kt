package thisissadeghi.sample.presentation.ui.components

import androidx.compose.foundation.background
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
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import thisissadeghi.designsystem.XCard
import thisissadeghi.designsystem.XText
import thisissadeghi.designsystem.XTheme
import thisissadeghi.sample.presentation.ui.formatMoney

@Composable
internal fun MonthlySummarySection(
    income: Double,
    expenses: Double,
) {
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
                    amount = income,
                    fraction = (income / (income + expenses)).coerceIn(0.0, 1.0).toFloat(),
                    color = XTheme.Colors.Success,
                )
                SummaryProgressRow(
                    label = "Expenses Used",
                    amount = expenses,
                    fraction = (expenses / income).coerceIn(0.0, 1.0).toFloat(),
                    color = XTheme.Colors.Danger,
                )
            }
        }
    }
}

@Composable
private fun SummaryProgressRow(
    label: String,
    amount: Double,
    fraction: Float,
    color: Color,
) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            XText(label, fontSize = 12.sp, fontWeight = FontWeight.Medium, color = MaterialTheme.colorScheme.onSurfaceVariant)
            XText("$${ amount.formatMoney() }", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = color)
        }
        Box(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .height(6.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.outlineVariant),
        ) {
            Box(
                modifier =
                    Modifier
                        .fillMaxWidth(fraction)
                        .fillMaxHeight()
                        .background(color),
            )
        }
    }
}
