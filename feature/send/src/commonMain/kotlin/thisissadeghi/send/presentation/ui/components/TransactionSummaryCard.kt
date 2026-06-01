package thisissadeghi.send.presentation.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kmpilot.feature.send.generated.resources.Res
import kmpilot.feature.send.generated.resources.estimated_arrival_label
import kmpilot.feature.send.generated.resources.network_fee_label
import kmpilot.feature.send.generated.resources.total_deduct_label
import org.jetbrains.compose.resources.stringResource
import thisissadeghi.designsystem.XIcon
import thisissadeghi.designsystem.XText
import thisissadeghi.designsystem.XTheme

@Composable
fun TransactionSummaryCard(
    networkFee: String,
    totalDeduct: String,
    estimatedArrival: String,
    modifier: Modifier = Modifier,
) {
    val dividerColor = Color(0xFF3F3822).copy(alpha = 0.3f)

    Column(
        modifier =
            modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.surfaceVariant, RoundedCornerShape(24.dp))
                .border(
                    1.dp,
                    MaterialTheme.colorScheme.primary.copy(alpha = 0.3f),
                    RoundedCornerShape(24.dp),
                ).padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        SummaryRow(
            label = stringResource(Res.string.network_fee_label),
            value = networkFee,
            valueStyle =
                TextStyle(
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.onSurface,
                ),
        )
        SummaryRow(
            label = stringResource(Res.string.total_deduct_label),
            value = totalDeduct,
            valueStyle =
                TextStyle(
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface,
                ),
        )
        Column(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .drawBehind {
                        drawLine(
                            color = dividerColor,
                            start = Offset(0f, 0f),
                            end = Offset(size.width, 0f),
                            strokeWidth = 1.dp.toPx(),
                        )
                    }.padding(top = 12.dp),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                XText(
                    text = stringResource(Res.string.estimated_arrival_label),
                    style =
                        TextStyle(
                            fontSize = 14.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        ),
                )
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                ) {
                    XIcon(
                        imageVector = Icons.Default.Bolt,
                        contentDescription = null,
                        tint = XTheme.Colors.Success,
                        modifier = Modifier.size(14.dp),
                    )
                    XText(
                        text = estimatedArrival,
                        style =
                            TextStyle(
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = XTheme.Colors.Success,
                            ),
                    )
                }
            }
        }
    }
}

@Composable
private fun SummaryRow(
    label: String,
    value: String,
    valueStyle: TextStyle,
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        XText(
            text = label,
            style =
                TextStyle(
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                ),
        )
        XText(text = value, style = valueStyle)
    }
}
