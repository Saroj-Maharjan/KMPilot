package thisissadeghi.send.presentation.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import thisissadeghi.designsystem.XButton
import thisissadeghi.designsystem.XText

@Composable
fun AmountInput(
    amount: String,
    coinSymbol: String,
    balance: String,
    onPercentClick: (Int) -> Unit,
    onMaxClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        XText(
            text = "Amount",
            style =
                TextStyle(
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                ),
            modifier =
                Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp),
            textAlign = TextAlign.Center,
        )
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            XText(
                text = amount,
                style =
                    TextStyle(
                        fontSize = 40.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface,
                        lineHeight = 40.sp,
                    ),
            )
            Spacer(modifier = Modifier.width(8.dp))
            XText(
                text = coinSymbol,
                style =
                    TextStyle(
                        fontSize = 20.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.primary,
                    ),
            )
        }
        XText(
            text = balance,
            style =
                TextStyle(
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                ),
            modifier = Modifier.padding(top = 8.dp),
        )
        Row(
            modifier = Modifier.padding(top = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            OutlinedButton(
                onClick = { onPercentClick(25) },
                shape = CircleShape,
                colors =
                    ButtonDefaults.outlinedButtonColors(
                        containerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f),
                        contentColor = MaterialTheme.colorScheme.primary,
                    ),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.2f)),
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 6.dp),
            ) {
                XText(
                    text = "25%",
                    style = TextStyle(fontSize = 12.sp, fontWeight = FontWeight.Bold),
                )
            }
            OutlinedButton(
                onClick = { onPercentClick(50) },
                shape = CircleShape,
                colors =
                    ButtonDefaults.outlinedButtonColors(
                        containerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f),
                        contentColor = MaterialTheme.colorScheme.primary,
                    ),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.2f)),
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 6.dp),
            ) {
                XText(
                    text = "50%",
                    style = TextStyle(fontSize = 12.sp, fontWeight = FontWeight.Bold),
                )
            }
            XButton(
                onClick = onMaxClick,
                shape = CircleShape,
                colors =
                    ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                        contentColor = MaterialTheme.colorScheme.onPrimary,
                    ),
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 6.dp),
            ) {
                XText(
                    text = "MAX",
                    style = TextStyle(fontSize = 12.sp, fontWeight = FontWeight.Bold),
                )
            }
        }
    }
}
