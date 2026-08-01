package thisissadeghi.assetdetail.presentation.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.exclude
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.ime
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import kmpilot.feature.assetdetail.generated.resources.Res
import kmpilot.feature.assetdetail.generated.resources.action_buy
import kmpilot.feature.assetdetail.generated.resources.action_sell
import org.jetbrains.compose.resources.stringResource
import thisissadeghi.designsystem.XButton
import thisissadeghi.designsystem.XOutlinedButton
import thisissadeghi.designsystem.XText

@Composable
fun StickyTradeBar(
    onSellClick: () -> Unit,
    onBuyClick: () -> Unit,
) {
    val outlineVariant = MaterialTheme.colorScheme.outlineVariant
    Row(
        modifier =
            Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.surface)
                .drawBehind {
                    drawLine(
                        color = outlineVariant,
                        start = Offset(0f, 0f),
                        end = Offset(size.width, 0f),
                        strokeWidth = 1.dp.toPx(),
                    )
                }.windowInsetsPadding(WindowInsets.navigationBars.exclude(WindowInsets.ime))
                .padding(horizontal = 24.dp, vertical = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        XOutlinedButton(
            onClick = onSellClick,
            modifier =
                Modifier
                    .weight(1f)
                    .height(56.dp),
            shape = RoundedCornerShape(24.dp),
            border =
                BorderStroke(
                    2.dp,
                    MaterialTheme.colorScheme.primary,
                ),
            colors =
                ButtonDefaults.outlinedButtonColors(
                    contentColor = MaterialTheme.colorScheme.primary,
                ),
        ) {
            XText(
                text = stringResource(Res.string.action_sell),
                style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.primary,
            )
        }

        XButton(
            onClick = onBuyClick,
            modifier =
                Modifier
                    .weight(1f)
                    .height(56.dp),
            shape = RoundedCornerShape(24.dp),
            colors =
                ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary,
                ),
        ) {
            XText(
                text = stringResource(Res.string.action_buy),
                style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.onPrimary,
            )
        }
    }
}
