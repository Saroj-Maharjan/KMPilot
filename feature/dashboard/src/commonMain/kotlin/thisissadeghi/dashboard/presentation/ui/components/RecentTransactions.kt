package thisissadeghi.dashboard.presentation.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kmpilot.feature.dashboard.generated.resources.Res
import kmpilot.feature.dashboard.generated.resources.section_recent_transactions
import org.jetbrains.compose.resources.stringResource
import thisissadeghi.dashboard.data.model.Transaction
import thisissadeghi.designsystem.XText
import thisissadeghi.designsystem.XTheme

@Composable
internal fun RecentTransactions(
    transactions: List<Transaction>,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier, verticalArrangement = Arrangement.spacedBy(16.dp)) {
        XText(
            stringResource(Res.string.section_recent_transactions),
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface,
        )
        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            transactions.forEach { tx -> TransactionItem(tx) }
        }
    }
}

@Preview
@Composable
private fun RecentTransactionsPreview() {
    XTheme {
        RecentTransactions(
            transactions =
                listOf(
                    Transaction(
                        id = "1",
                        title = "Salary",
                        category = "Income",
                        amount = 4_200.0,
                        isIncome = true,
                        date = "May 21",
                        currency = "$",
                    ),
                    Transaction(
                        id = "2",
                        title = "Starbucks",
                        category = "Food",
                        amount = 6.50,
                        isIncome = false,
                        date = "May 20",
                        currency = "$",
                    ),
                    Transaction(
                        id = "3",
                        title = "Amazon",
                        category = "Shopping",
                        amount = 45.99,
                        isIncome = false,
                        date = "May 19",
                        currency = "$",
                    ),
                ),
        )
    }
}
