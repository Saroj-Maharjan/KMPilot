package thisissadeghi.sample.presentation.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ElectricBolt
import androidx.compose.material.icons.filled.Receipt
import androidx.compose.material.icons.filled.Wifi
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import thisissadeghi.designsystem.XIcon
import thisissadeghi.designsystem.XText
import thisissadeghi.designsystem.XTheme
import thisissadeghi.sample.data.model.UpcomingBill
import thisissadeghi.sample.presentation.ui.formatMoney

@Composable
internal fun UpcomingBillsSection(bills: List<UpcomingBill>) {
    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        XText("Upcoming Bills", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            bills.forEach { BillItem(it) }
        }
    }
}

@Composable
private fun BillItem(bill: UpcomingBill) {
    val accentColor = if (bill.isOverdue) XTheme.Colors.Danger else MaterialTheme.colorScheme.outlineVariant
    val iconTint = if (bill.isOverdue) XTheme.Colors.Danger else MaterialTheme.colorScheme.onSurfaceVariant
    Row(
        modifier =
            Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.surface, RoundedCornerShape(24.dp))
                .clip(RoundedCornerShape(24.dp))
                .height(IntrinsicSize.Min),
    ) {
        Box(
            modifier =
                Modifier
                    .width(4.dp)
                    .fillMaxHeight()
                    .background(accentColor),
        )
        Row(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Box(
                    modifier =
                        Modifier
                            .size(40.dp)
                            .background(iconTint.copy(alpha = 0.10f), RoundedCornerShape(16.dp)),
                    contentAlignment = Alignment.Center,
                ) {
                    XIcon(imageVector = billIcon(bill.name), contentDescription = null, tint = iconTint, modifier = Modifier.size(24.dp))
                }
                Column {
                    XText(bill.name, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
                    if (bill.isOverdue) {
                        XText("OVERDUE", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = XTheme.Colors.Danger)
                    } else {
                        XText("Due ${bill.dueDate}", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
            }
            XText(
                "$${ bill.amount.formatMoney() }",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface,
            )
        }
    }
}

private fun billIcon(name: String): ImageVector =
    when {
        name.contains("internet", ignoreCase = true) -> Icons.Filled.Wifi
        name.contains("electric", ignoreCase = true) -> Icons.Filled.ElectricBolt
        else -> Icons.Filled.Receipt
    }
