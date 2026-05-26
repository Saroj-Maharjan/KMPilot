package thisissadeghi.dashboard.presentation.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kmpilot.feature.dashboard.generated.resources.Res
import kmpilot.feature.dashboard.generated.resources.bolt
import kmpilot.feature.dashboard.generated.resources.payments
import kmpilot.feature.dashboard.generated.resources.subscriptions
import kmpilot.feature.dashboard.generated.resources.wifi
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.painterResource
import thisissadeghi.dashboard.data.model.UpcomingBill
import thisissadeghi.dashboard.presentation.ui.formatMoney
import thisissadeghi.designsystem.XIcon
import thisissadeghi.designsystem.XText
import thisissadeghi.designsystem.XTheme

@Composable
internal fun BillItem(bill: UpcomingBill) {
    val iconTint = if (bill.isOverdue) XTheme.Colors.Danger else MaterialTheme.colorScheme.onSurfaceVariant

    Row(
        modifier = Modifier.fillMaxWidth().padding(16.dp),
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
                    painter = painterResource(billIcon(bill.name)),
                    tint = iconTint,
                    modifier = Modifier.size(24.dp),
                )
            }
            Column {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
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

private fun billIcon(name: String): DrawableResource =
    when {
        name.contains("netflix", ignoreCase = true) -> Res.drawable.subscriptions
        name.contains("internet", ignoreCase = true) ||
            name.contains("wifi", ignoreCase = true) -> Res.drawable.wifi
        name.contains("electric", ignoreCase = true) ||
            name.contains("electricity", ignoreCase = true) -> Res.drawable.bolt
        else -> Res.drawable.payments
    }

@Preview
@Composable
private fun BillItemPreview() {
    XTheme {
        BillItem(
            bill =
                UpcomingBill(
                    id = "2",
                    name = "Internet",
                    amount = 59.99,
                    dueDate = "May 15",
                    currency = "$",
                    isOverdue = true,
                ),
        )
    }
}
