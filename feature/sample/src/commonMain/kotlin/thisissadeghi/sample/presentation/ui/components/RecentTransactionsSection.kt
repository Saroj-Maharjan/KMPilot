package thisissadeghi.sample.presentation.ui.components

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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material.icons.filled.LaptopMac
import androidx.compose.material.icons.filled.LocalCafe
import androidx.compose.material.icons.filled.Receipt
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material.icons.filled.Work
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import thisissadeghi.designsystem.XIcon
import thisissadeghi.designsystem.XText
import thisissadeghi.designsystem.XTheme
import thisissadeghi.sample.data.model.Transaction
import thisissadeghi.sample.presentation.ui.formatMoney

@Composable
internal fun RecentTransactionsSection(transactions: List<Transaction>) {
    Column(modifier = Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(16.dp)) {
        XText(
            "Recent Transactions",
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface,
        )
        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            transactions.forEach { TransactionCard(it) }
        }
    }
}

@Composable
private fun TransactionCard(tx: Transaction) {
    val iconBackground =
        if (tx.isIncome) {
            XTheme.Colors.Success.copy(alpha = 0.1f)
        } else {
            MaterialTheme.colorScheme.surfaceVariant
        }
    val iconTint =
        if (tx.isIncome) {
            XTheme.Colors.Success
        } else {
            MaterialTheme.colorScheme.onSurfaceVariant
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
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.weight(1f),
        ) {
            Box(
                modifier = Modifier.size(40.dp).background(iconBackground, CircleShape),
                contentAlignment = Alignment.Center,
            ) {
                XIcon(
                    imageVector = transactionIcon(tx.category),
                    contentDescription = null,
                    tint = iconTint,
                    modifier = Modifier.size(24.dp),
                )
            }
            Column {
                XText(
                    tx.title,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
                XText(
                    tx.category,
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }
        XText(
            if (tx.isIncome) "+$${tx.amount.formatMoney()}" else "$${tx.amount.formatMoney()}",
            fontWeight = FontWeight.Bold,
            color = if (tx.isIncome) XTheme.Colors.Success else MaterialTheme.colorScheme.onSurface,
        )
    }
}

private fun transactionIcon(category: String): ImageVector =
    when (category.lowercase()) {
        "income" -> Icons.Filled.Work
        "food" -> Icons.Filled.LocalCafe
        "shopping" -> Icons.Filled.ShoppingBag
        "freelance" -> Icons.Filled.LaptopMac
        "transport" -> Icons.Filled.DirectionsCar
        else -> Icons.Filled.Receipt
    }
