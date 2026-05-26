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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kmpilot.feature.dashboard.generated.resources.Res
import kmpilot.feature.dashboard.generated.resources.coffee
import kmpilot.feature.dashboard.generated.resources.directions_car
import kmpilot.feature.dashboard.generated.resources.laptop_mac
import kmpilot.feature.dashboard.generated.resources.payments
import kmpilot.feature.dashboard.generated.resources.shopping_bag
import kmpilot.feature.dashboard.generated.resources.work
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.painterResource
import thisissadeghi.dashboard.data.model.Transaction
import thisissadeghi.dashboard.presentation.ui.formatMoney
import thisissadeghi.designsystem.XIcon
import thisissadeghi.designsystem.XText
import thisissadeghi.designsystem.XTheme

@Composable
internal fun TransactionItem(transaction: Transaction) {
    val iconBgColor: Color
    val iconTint: Color
    if (transaction.isIncome) {
        iconBgColor = XTheme.Colors.Success.copy(alpha = 0.1f)
        iconTint = XTheme.Colors.Success
    } else {
        iconBgColor = MaterialTheme.colorScheme.surfaceVariant
        iconTint = MaterialTheme.colorScheme.onSurfaceVariant
    }

    Row(
        modifier =
            Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.surface, RoundedCornerShape(24.dp))
                .border(1.dp, MaterialTheme.colorScheme.outlineVariant, RoundedCornerShape(24.dp))
                .padding(16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Row(
            modifier = Modifier.weight(1f),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Box(
                modifier = Modifier.size(40.dp).background(iconBgColor, CircleShape),
                contentAlignment = Alignment.Center,
            ) {
                XIcon(
                    painter = painterResource(transactionIcon(transaction.category)),
                    tint = iconTint,
                    modifier = Modifier.size(24.dp),
                )
            }
            Column {
                XText(
                    transaction.title,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
                XText(
                    transaction.category,
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }
        XText(
            if (transaction.isIncome) "+$${transaction.amount.formatMoney()}" else "$${transaction.amount.formatMoney()}",
            fontWeight = FontWeight.Bold,
            color = if (transaction.isIncome) XTheme.Colors.Success else MaterialTheme.colorScheme.onSurface,
        )
    }
}

private fun transactionIcon(category: String): DrawableResource =
    when (category.lowercase()) {
        "income" -> Res.drawable.work
        "food" -> Res.drawable.coffee
        "shopping" -> Res.drawable.shopping_bag
        "freelance" -> Res.drawable.laptop_mac
        "transport" -> Res.drawable.directions_car
        else -> Res.drawable.payments
    }

@Preview
@Composable
private fun TransactionItemPreview() {
    XTheme {
        TransactionItem(
            transaction =
                Transaction(
                    id = "1",
                    title = "Salary",
                    category = "Income",
                    amount = 4_200.0,
                    isIncome = true,
                    date = "May 21",
                    currency = "$",
                ),
        )
    }
}
