package thisissadeghi.swap.presentation.ui.components

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import kmpilot.feature.swap.generated.resources.Res
import kmpilot.feature.swap.generated.resources.swap_you_receive
import org.jetbrains.compose.resources.stringResource
import thisissadeghi.designsystem.XText
import thisissadeghi.designsystem.XTheme
import thisissadeghi.swap.presentation.ui.motion.shimmerBrush

@Composable
fun ToAssetSection(
    avatarUrl: String,
    coinName: String,
    coinTicker: String,
    receiveAmount: String,
    modifier: Modifier = Modifier,
) {
    AssetCard(modifier = modifier) {
        AssetCardHeader(
            avatarUrl = avatarUrl,
            avatarContentDescription = coinTicker,
            iconCircleColor = MaterialTheme.colorScheme.surfaceVariant,
            coinName = coinName,
            coinTicker = coinTicker,
            trailing = {
                XText(
                    text = stringResource(Res.string.swap_you_receive),
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            },
        )
        // Shimmering receive amount — see motion/SwapMotion.kt for the gradient brush animation
        XText(
            text = receiveAmount,
            style =
                MaterialTheme.typography.displaySmall.copy(
                    fontWeight = FontWeight.ExtraBold,
                    brush = shimmerBrush(),
                ),
        )
    }
}

@Preview
@Composable
private fun ToAssetSectionPreview() {
    XTheme {
        ToAssetSection(
            avatarUrl = "",
            coinName = "Ethereum",
            coinTicker = "ETH",
            receiveAmount = "8.5994",
        )
    }
}
