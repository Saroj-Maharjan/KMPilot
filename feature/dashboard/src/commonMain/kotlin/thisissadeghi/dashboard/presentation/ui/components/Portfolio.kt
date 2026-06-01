package thisissadeghi.dashboard.presentation.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kmpilot.feature.dashboard.generated.resources.Res
import kmpilot.feature.dashboard.generated.resources.section_portfolio
import org.jetbrains.compose.resources.stringResource
import thisissadeghi.dashboard.data.model.PortfolioAsset
import thisissadeghi.designsystem.XText
import thisissadeghi.designsystem.XTheme

@Composable
internal fun Portfolio(
    assets: List<PortfolioAsset>,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier, verticalArrangement = Arrangement.spacedBy(16.dp)) {
        XText(
            stringResource(Res.string.section_portfolio),
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface,
        )
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            assets.forEach { asset -> PortfolioItem(asset, Modifier.weight(1f)) }
        }
    }
}

@Preview
@Composable
private fun PortfolioPreview() {
    XTheme {
        Portfolio(
            assets =
                listOf(
                    PortfolioAsset(
                        id = "btc",
                        name = "Bitcoin",
                        symbol = "BTC",
                        balance = 0.42,
                        value = 28_400.0,
                        changePercent = 3.4,
                        currency = "$",
                    ),
                    PortfolioAsset(
                        id = "eth",
                        name = "Ethereum",
                        symbol = "ETH",
                        balance = 1.85,
                        value = 5_920.0,
                        changePercent = -1.2,
                        currency = "$",
                    ),
                    PortfolioAsset(
                        id = "sol",
                        name = "Solana",
                        symbol = "SOL",
                        balance = 24.0,
                        value = 1_440.0,
                        changePercent = 7.8,
                        currency = "$",
                    ),
                ),
        )
    }
}
