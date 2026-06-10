package thisissadeghi.assetdetail.presentation.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import kmpilot.feature.assetdetail.generated.resources.Res
import kmpilot.feature.assetdetail.generated.resources.label_approx_btc
import kmpilot.feature.assetdetail.generated.resources.label_circ_supply
import kmpilot.feature.assetdetail.generated.resources.label_market_cap
import kmpilot.feature.assetdetail.generated.resources.label_volume_24h
import kmpilot.feature.assetdetail.generated.resources.label_your_holdings
import org.jetbrains.compose.resources.stringResource
import thisissadeghi.assetdetail.data.model.AssetDetailResponse
import thisissadeghi.common.ext.formatDecimals
import thisissadeghi.designsystem.XTheme

@Composable
fun StatsGrid(
    detail: AssetDetailResponse,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier =
            modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp)
                .padding(bottom = 40.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            StatsCard(
                label = stringResource(Res.string.label_market_cap),
                value = formatLargeNumber(detail.marketCap),
                modifier = Modifier.weight(1f),
            )
            StatsCard(
                label = stringResource(Res.string.label_volume_24h),
                value = formatLargeNumber(detail.volume24h),
                modifier = Modifier.weight(1f),
            )
        }
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            StatsCard(
                label = stringResource(Res.string.label_circ_supply),
                value = formatLargeNumber(detail.circulatingSupply),
                modifier = Modifier.weight(1f),
            )
            StatsCard(
                label = stringResource(Res.string.label_your_holdings),
                value = "${detail.holdingAmount.formatDecimals(4)} ${detail.symbol}",
                isHighlighted = true,
                subValue = stringResource(Res.string.label_approx_btc, "$${detail.holdingFiatValue.formatDecimals(2)}"),
                modifier = Modifier.weight(1f),
            )
        }
    }
}

private fun formatLargeNumber(value: Double): String =
    when {
        value >= 1_000_000_000_000 -> "$${(value / 1_000_000_000_000).formatDecimals(2)}T"
        value >= 1_000_000_000 -> "$${(value / 1_000_000_000).formatDecimals(2)}B"
        value >= 1_000_000 -> "$${(value / 1_000_000).formatDecimals(2)}M"
        value >= 1_000 -> "$${(value / 1_000).formatDecimals(2)}K"
        else -> "$${value.formatDecimals(2)}"
    }

@Preview
@Composable
private fun StatsGridPreview() {
    XTheme {
        StatsGrid(
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
