package thisissadeghi.sample.presentation.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocalCafe
import androidx.compose.material.icons.filled.Receipt
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material.icons.filled.Work
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import thisissadeghi.designsystem.XCard
import thisissadeghi.designsystem.XHorizontalDivider
import thisissadeghi.designsystem.XIcon
import thisissadeghi.designsystem.XText
import thisissadeghi.designsystem.XTheme
import thisissadeghi.sample.data.model.Transaction
import thisissadeghi.sample.presentation.ui.formatMoney

@Composable
internal fun RecentTransactionsSection(transactions: List<Transaction>) {
    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            XText("Recent Transactions", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
            XIcon(
                imageVector = Icons.Filled.Tune,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.size(24.dp),
            )
        }
        XCard(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            shape = RoundedCornerShape(24.dp),
            elevation = CardDefaults.cardElevation(0.dp),
        ) {
            Column {
                transactions.forEachIndexed { index, tx ->
                    TransactionItem(tx)
                    if (index < transactions.lastIndex) {
                        XHorizontalDivider()
                    }
                }
            }
        }
    }
}

@Composable
private fun TransactionItem(tx: Transaction) {
    val amountColor = if (tx.isIncome) XTheme.Colors.Success else MaterialTheme.colorScheme.onSurface
    val sign = if (tx.isIncome) "+" else "-"
    Row(
        modifier =
            Modifier
                .fillMaxWidth()
                .padding(16.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        TransactionIcon(tx.category)
        Column(modifier = Modifier.weight(1f)) {
            XText(
                tx.title,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
            XText(
                "${tx.category} • ${tx.date}",
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
        XText("$sign$${ tx.amount.formatMoney() }", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = amountColor)
    }
}

@Composable
private fun TransactionIcon(category: String) {
    val bgColor: Color
    val content: @Composable () -> Unit
    when (category.lowercase()) {
        "streaming" -> {
            bgColor = Color(0xFFDC2626).copy(alpha = 0.20f)
            content = {
                XText(
                    text = "N",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Black,
                    color = Color(0xFFDC2626),
                    fontStyle = FontStyle.Italic,
                )
            }
        }
        "income" -> {
            bgColor = XTheme.Colors.Success.copy(alpha = 0.20f)
            content =
                {
                    XIcon(
                        imageVector = Icons.Filled.Work,
                        contentDescription = null,
                        tint = XTheme.Colors.Success,
                        modifier = Modifier.size(24.dp),
                    )
                }
        }
        "food" -> {
            bgColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.30f)
            content =
                {
                    XIcon(
                        imageVector = Icons.Filled.LocalCafe,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.size(24.dp),
                    )
                }
        }
        else -> {
            bgColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.30f)
            content =
                {
                    XIcon(
                        imageVector = Icons.Filled.Receipt,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(24.dp),
                    )
                }
        }
    }
    Box(
        modifier =
            Modifier
                .size(40.dp)
                .background(bgColor, RoundedCornerShape(16.dp)),
        contentAlignment = Alignment.Center,
        content = { content() },
    )
}
