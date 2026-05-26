package thisissadeghi.dashboard.presentation.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kmpilot.feature.dashboard.generated.resources.Res
import kmpilot.feature.dashboard.generated.resources.currency_bitcoin
import kmpilot.feature.dashboard.generated.resources.currency_exchange
import kmpilot.feature.dashboard.generated.resources.token
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.painterResource
import thisissadeghi.dashboard.data.model.PortfolioAsset
import thisissadeghi.designsystem.XIcon
import thisissadeghi.designsystem.XText
import thisissadeghi.designsystem.XTheme

@Composable
internal fun PortfolioItem(
    asset: PortfolioAsset,
    modifier: Modifier = Modifier,
) {
    val isPositive = asset.changePercent >= 0
    val iconColor =
        if (asset.symbol == "ETH") {
            MaterialTheme.colorScheme.onSurfaceVariant
        } else {
            MaterialTheme.colorScheme.primary
        }

    Box(
        modifier =
            modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.surface, RoundedCornerShape(24.dp))
                .border(1.dp, MaterialTheme.colorScheme.outlineVariant, RoundedCornerShape(24.dp))
                .padding(16.dp),
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Box(
                modifier =
                    Modifier
                        .size(32.dp)
                        .background(iconColor.copy(alpha = 0.1f), CircleShape)
                        .padding(bottom = 4.dp),
                contentAlignment = Alignment.Center,
            ) {
                XIcon(
                    painter = painterResource(portfolioIcon(asset.symbol)),
                    tint = iconColor,
                    modifier = Modifier.size(20.dp),
                )
            }
            XText(
                asset.symbol,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface,
            )
            XText(
                if (isPositive) "+${asset.changePercent}%" else "${asset.changePercent}%",
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = if (isPositive) XTheme.Colors.Success else XTheme.Colors.Danger,
            )
        }
    }
}

private fun portfolioIcon(symbol: String): DrawableResource =
    when (symbol.uppercase()) {
        "BTC" -> Res.drawable.currency_bitcoin
        "ETH" -> Res.drawable.currency_exchange
        else -> Res.drawable.token
    }

@Preview
@Composable
private fun PortfolioItemPreview() {
    XTheme {
        PortfolioItem(
            asset =
                PortfolioAsset(
                    id = "btc",
                    name = "Bitcoin",
                    symbol = "BTC",
                    balance = 0.42,
                    value = 28_400.0,
                    changePercent = 3.4,
                    currency = "$",
                ),
        )
    }
}
