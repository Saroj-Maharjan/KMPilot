package thisissadeghi.dashboard.presentation.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ElectricBolt
import androidx.compose.material.icons.filled.Receipt
import androidx.compose.material.icons.filled.Subscriptions
import androidx.compose.material.icons.filled.Wifi
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import thisissadeghi.dashboard.data.model.UpcomingBill
import thisissadeghi.dashboard.presentation.ui.formatMoney
import thisissadeghi.designsystem.XHorizontalDivider
import thisissadeghi.designsystem.XIcon
import thisissadeghi.designsystem.XText
import thisissadeghi.designsystem.XTheme

@Composable
internal fun UpcomingBillsCard(bills: List<UpcomingBill>) {
    Column(modifier = Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(16.dp)) {
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
                BillRow(bill)
                if (index < bills.lastIndex) {
                    XHorizontalDivider(
                        color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f),
                    )
                }
            }
        }
    }
}

@Composable
private fun BillRow(bill: UpcomingBill) {
    Row(
        modifier =
            Modifier
                .fillMaxWidth()
                .padding(16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Box(
                modifier =
                    Modifier
                        .size(40.dp)
                        .background(MaterialTheme.colorScheme.surfaceVariant, RoundedCornerShape(20.dp)),
                contentAlignment = Alignment.Center,
            ) {
                XIcon(
                    imageVector = billIcon(bill.name),
                    contentDescription = null,
                    tint =
                        if (bill.isOverdue) {
                            XTheme.Colors.Danger
                        } else {
                            MaterialTheme.colorScheme.onSurfaceVariant
                        },
                )
            }
            Column {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    XText(
                        bill.name,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface,
                    )
                    if (bill.isOverdue) {
                        Box(
                            modifier =
                                Modifier
                                    .background(
                                        XTheme.Colors.Danger.copy(alpha = 0.2f),
                                        RoundedCornerShape(8.dp),
                                    ).padding(horizontal = 6.dp, vertical = 2.dp),
                        ) {
                            XText(
                                "OVERDUE",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = XTheme.Colors.Danger,
                            )
                        }
                    }
                }
                if (!bill.isOverdue) {
                    XText(
                        bill.dueDate,
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            }
        }
        XText(
            "$${bill.amount.formatMoney()}",
            fontWeight = FontWeight.Bold,
            color = if (bill.isOverdue) XTheme.Colors.Danger else MaterialTheme.colorScheme.onSurface,
        )
    }
}

private fun billIcon(name: String): ImageVector =
    when {
        name.contains("netflix", ignoreCase = true) -> Icons.Filled.Subscriptions
        name.contains("internet", ignoreCase = true) -> Icons.Filled.Wifi
        name.contains("electric", ignoreCase = true) -> Icons.Filled.ElectricBolt
        else -> Icons.Filled.Receipt
    }

@Preview
@Composable
private fun UpcomingBillsCardPreview() {
    XTheme {
        UpcomingBillsCard(
            bills =
                listOf(
                    UpcomingBill(
                        id = "1",
                        name = "Netflix",
                        amount = 15.49,
                        dueDate = "May 24",
                        currency = "$",
                        isOverdue = false,
                    ),
                    UpcomingBill(
                        id = "2",
                        name = "Internet",
                        amount = 59.99,
                        dueDate = "May 26",
                        currency = "$",
                        isOverdue = false,
                    ),
                    UpcomingBill(
                        id = "3",
                        name = "Electric Bill",
                        amount = 124.30,
                        dueDate = "May 19",
                        currency = "$",
                        isOverdue = true,
                    ),
                ),
        )
    }
}
