package thisissadeghi.assetdetail.presentation.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kmpilot.feature.assetdetail.generated.resources.Res
import kmpilot.feature.assetdetail.generated.resources.action_see_all
import kmpilot.feature.assetdetail.generated.resources.activity_unavailable
import kmpilot.feature.assetdetail.generated.resources.call_made
import kmpilot.feature.assetdetail.generated.resources.call_received
import kmpilot.feature.assetdetail.generated.resources.section_recent_activity
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import thisissadeghi.assetdetail.data.model.ActivityResponse
import thisissadeghi.assetdetail.data.model.AssetTransaction
import thisissadeghi.common.UiState
import thisissadeghi.designsystem.DesignSystemResources
import thisissadeghi.designsystem.XText
import thisissadeghi.designsystem.XTextButton
import thisissadeghi.designsystem.XTheme
import thisissadeghi.designsystem.motion.rememberReducedMotion
import thisissadeghi.designsystem.motion.shimmer

@Composable
fun ActivitySection(
    activityState: UiState<ActivityResponse>,
    modifier: Modifier = Modifier,
) {
    val reducedMotion = rememberReducedMotion()
    val surfaceVariant = MaterialTheme.colorScheme.surfaceVariant

    Column(
        modifier =
            modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp)
                .padding(bottom = 40.dp),
    ) {
        // Section header
        Row(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            XText(
                text = stringResource(Res.string.section_recent_activity),
                style =
                    MaterialTheme.typography.titleLarge.copy(
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                    ),
                color = MaterialTheme.colorScheme.onSurface,
            )
            XTextButton(onClick = {}) {
                XText(
                    text = stringResource(Res.string.action_see_all),
                    style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.primary,
                )
            }
        }

        when (activityState) {
            is UiState.Loading, UiState.Uninitialized -> {
                val shimmerHighlight =
                    Color(
                        red = surfaceVariant.red,
                        green = surfaceVariant.green,
                        blue = surfaceVariant.blue,
                        alpha = 0.5f,
                    )
                Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                    repeat(3) {
                        Box(
                            modifier =
                                Modifier
                                    .fillMaxWidth()
                                    .height(72.dp)
                                    .clip(RoundedCornerShape(24.dp))
                                    .then(
                                        if (reducedMotion) {
                                            Modifier.background(surfaceVariant)
                                        } else {
                                            Modifier.shimmer(
                                                baseColor = surfaceVariant,
                                                highlightColor = shimmerHighlight,
                                            )
                                        },
                                    ),
                        )
                    }
                }
            }

            is UiState.Failed -> {
                Box(
                    modifier =
                        Modifier
                            .fillMaxWidth()
                            .height(72.dp)
                            .background(surfaceVariant, RoundedCornerShape(24.dp)),
                    contentAlignment = Alignment.Center,
                ) {
                    XText(
                        text = stringResource(Res.string.activity_unavailable),
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            }

            is UiState.Success -> {
                Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                    activityState.value.transactions.take(3).forEach { transaction ->
                        val isPositive = transaction.amount >= 0
                        val iconPainter =
                            when (transaction.type.lowercase()) {
                                "received" -> painterResource(Res.drawable.call_received)
                                "sent" -> painterResource(Res.drawable.call_made)
                                else -> painterResource(DesignSystemResources.drawable.bolt)
                            }
                        val iconTint =
                            when (transaction.type.lowercase()) {
                                "sent" -> XTheme.Colors.Danger
                                else -> XTheme.Colors.Success
                            }
                        val iconBgColor =
                            Color(
                                red = iconTint.red,
                                green = iconTint.green,
                                blue = iconTint.blue,
                                alpha = 0.1f,
                            )
                        val amountColor = if (isPositive) XTheme.Colors.Success else XTheme.Colors.Danger
                        val amountPrefix = if (isPositive) "+" else ""

                        ActivityRow(
                            iconPainter = iconPainter,
                            iconTint = iconTint,
                            iconBgColor = iconBgColor,
                            title = transaction.title,
                            timestamp = transaction.timestamp,
                            amount = "$amountPrefix%.4f ${transaction.currency}".format(transaction.amount),
                            amountColor = amountColor,
                            fiatEquiv = "$%.2f".format(transaction.fiatValue),
                        )
                    }
                }
            }
        }
    }
}

@Preview
@Composable
private fun ActivitySectionPreview() {
    XTheme {
        ActivitySection(
            activityState =
                UiState.Success(
                    ActivityResponse(
                        assetId = "bitcoin",
                        transactions =
                            listOf(
                                AssetTransaction(
                                    id = "1",
                                    type = "received",
                                    title = "Received",
                                    timestamp = "Today, 2:30 PM",
                                    amount = 0.012,
                                    fiatValue = 810.0,
                                    currency = "BTC",
                                ),
                                AssetTransaction(
                                    id = "2",
                                    type = "sent",
                                    title = "Sent",
                                    timestamp = "Yesterday, 11:15 AM",
                                    amount = -0.005,
                                    fiatValue = 337.0,
                                    currency = "BTC",
                                ),
                            ),
                    ),
                ),
        )
    }
}
