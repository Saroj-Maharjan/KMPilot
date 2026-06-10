package thisissadeghi.assetdetail.presentation.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.em
import androidx.compose.ui.unit.sp
import kmpilot.feature.assetdetail.generated.resources.Res
import kmpilot.feature.assetdetail.generated.resources.arrow_upward
import kmpilot.feature.assetdetail.generated.resources.call_made
import kmpilot.feature.assetdetail.generated.resources.cd_coin_icon
import kmpilot.feature.assetdetail.generated.resources.currency_bitcoin_fill
import kmpilot.feature.assetdetail.generated.resources.status_change_negative
import kmpilot.feature.assetdetail.generated.resources.status_change_positive
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import thisissadeghi.assetdetail.data.model.AssetDetailResponse
import thisissadeghi.assetdetail.presentation.ui.motion.animatedBadgeColor
import thisissadeghi.assetdetail.presentation.ui.motion.animatedPrice
import thisissadeghi.common.ext.formatDecimals
import thisissadeghi.designsystem.XIcon
import thisissadeghi.designsystem.XText
import thisissadeghi.designsystem.XTheme
import thisissadeghi.designsystem.motion.rememberReducedMotion

@Composable
fun HeroSection(
    detail: AssetDetailResponse,
    modifier: Modifier = Modifier,
) {
    val isPositive = detail.changePercent24h >= 0
    val badgeColor by animatedBadgeColor(isPositive = isPositive)
    val reducedMotion = rememberReducedMotion()

    val rawPrice = detail.price.toFloat()
    val animatedPriceValue by animatedPrice(targetPrice = rawPrice)
    val displayPrice = if (reducedMotion) rawPrice else animatedPriceValue

    Box(
        modifier = modifier.fillMaxWidth(),
    ) {
        // Hero gradient backdrop
        Box(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .height(240.dp)
                    .background(
                        brush =
                            Brush.verticalGradient(
                                listOf(
                                    MaterialTheme.colorScheme.primaryContainer,
                                    MaterialTheme.colorScheme.background,
                                ),
                            ),
                    ).alpha(0.6f),
        )

        // Content column on top
        Column(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp)
                    .padding(top = 8.dp, bottom = 32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            // Coin circle
            Box(
                modifier =
                    Modifier
                        .padding(bottom = 16.dp)
                        .size(64.dp)
                        .shadow(8.dp, CircleShape)
                        .background(MaterialTheme.colorScheme.primary, CircleShape),
                contentAlignment = Alignment.Center,
            ) {
                XIcon(
                    painter = painterResource(Res.drawable.currency_bitcoin_fill),
                    contentDescription = stringResource(Res.string.cd_coin_icon),
                    tint = MaterialTheme.colorScheme.onPrimary,
                    modifier = Modifier.size(36.dp),
                )
            }

            // Name + ticker
            XText(
                text = "${detail.name} (${detail.symbol})",
                style =
                    MaterialTheme.typography.titleMedium.copy(
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                    ),
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )

            Spacer(Modifier.height(4.dp))

            // Price (animated)
            XText(
                text = "$${displayPrice.formatDecimals(2)}",
                style =
                    MaterialTheme.typography.displaySmall.copy(
                        fontWeight = FontWeight.ExtraBold,
                        letterSpacing = (-0.025).em,
                    ),
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.padding(bottom = 12.dp),
            )

            // % change badge
            val badgeBg =
                Color(
                    red = badgeColor.red,
                    green = badgeColor.green,
                    blue = badgeColor.blue,
                    alpha = 0.1f,
                )
            val badgeBorder =
                Color(
                    red = badgeColor.red,
                    green = badgeColor.green,
                    blue = badgeColor.blue,
                    alpha = 0.2f,
                )
            Row(
                modifier =
                    Modifier
                        .background(badgeBg, CircleShape)
                        .border(1.dp, badgeBorder, CircleShape)
                        .padding(horizontal = 12.dp, vertical = 4.dp),
                horizontalArrangement = Arrangement.spacedBy(4.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                XIcon(
                    painter =
                        painterResource(
                            if (isPositive) Res.drawable.arrow_upward else Res.drawable.call_made,
                        ),
                    contentDescription = null,
                    tint = badgeColor,
                    modifier = Modifier.size(16.dp),
                )
                val changeFormatted = kotlin.math.abs(detail.changePercent24h).formatDecimals(2)
                XText(
                    text =
                        if (isPositive) {
                            stringResource(Res.string.status_change_positive, changeFormatted)
                        } else {
                            stringResource(Res.string.status_change_negative, changeFormatted)
                        },
                    style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold),
                    color = badgeColor,
                )
            }
        }
    }
}

@Preview
@Composable
private fun HeroSectionPreview() {
    XTheme {
        HeroSection(
            detail =
                AssetDetailResponse(
                    id = "bitcoin",
                    name = "Bitcoin",
                    symbol = "BTC",
                    price = 67420.50,
                    changePercent24h = 2.34,
                    marketCap = 1_300_000_000_000.0,
                    volume24h = 28_000_000_000.0,
                    circulatingSupply = 19_700_000.0,
                    holdingAmount = 0.085,
                    holdingFiatValue = 5_730.0,
                    currency = "USD",
                ),
        )
    }
}
