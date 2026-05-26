package thisissadeghi.dashboard.presentation.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import thisissadeghi.dashboard.data.model.UpcomingBill
import thisissadeghi.designsystem.XHorizontalDivider
import thisissadeghi.designsystem.XText
import thisissadeghi.designsystem.XTheme

@Composable
internal fun UpcomingBills(
    bills: List<UpcomingBill>,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier, verticalArrangement = Arrangement.spacedBy(16.dp)) {
        XText(
            "Upcoming Bills",
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface,
        )
        Column(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.surface, RoundedCornerShape(24.dp))
                    .border(1.dp, MaterialTheme.colorScheme.outlineVariant, RoundedCornerShape(24.dp))
                    .clip(RoundedCornerShape(24.dp)),
        ) {
            bills.forEachIndexed { index, bill ->
                BillItem(bill)
                if (index < bills.lastIndex) {
                    XHorizontalDivider(
                        color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f),
                    )
                }
            }
        }
    }
}

@Preview
@Composable
private fun UpcomingBillsPreview() {
    XTheme {
        UpcomingBills(
            bills =
                listOf(
                    UpcomingBill(id = "1", name = "Netflix", amount = 15.49, dueDate = "May 10", currency = "$", isOverdue = false),
                    UpcomingBill(id = "2", name = "Internet", amount = 59.99, dueDate = "May 15", currency = "$", isOverdue = true),
                    UpcomingBill(id = "3", name = "Electricity", amount = 124.30, dueDate = "May 18", currency = "$", isOverdue = false),
                ),
        )
    }
}
