package thisissadeghi.dashboard.presentation.ui.components

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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CurrencyBitcoin
import androidx.compose.material.icons.filled.CurrencyExchange
import androidx.compose.material.icons.filled.MonetizationOn
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import thisissadeghi.dashboard.data.model.PortfolioAsset
import thisissadeghi.designsystem.XIcon
import thisissadeghi.designsystem.XText
import thisissadeghi.designsystem.XTheme

@Composable
internal fun PortfolioSection(assets: List<PortfolioAsset>) {
    Column(modifier = Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(16.dp)) {
        XText(
            "Portfolio",
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface,
        )
        val rows = assets.chunked(3)
        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            rows.forEach { rowItems ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    rowItems.forEach { asset ->
                        PortfolioAssetCard(asset, modifier = Modifier.weight(1f))
                    }
                    repeat(3 - rowItems.size) {
                        Spacer(modifier = Modifier.weight(1f))
                    }
                }
            }
        }
    }
}

@Composable
private fun PortfolioAssetCard(
    asset: PortfolioAsset,
    modifier: Modifier = Modifier,
) {
    val isPositive = asset.changePercent >= 0
    val circleBackground =
        if (asset.symbol == "ETH") {
            MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.1f)
        } else {
            MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.1f)
        }
    val circleIconTint =
        if (asset.symbol == "ETH") {
            MaterialTheme.colorScheme.onSurfaceVariant
        } else {
            MaterialTheme.colorScheme.primary
        }

    Column(
        modifier =
            modifier
                .background(MaterialTheme.colorScheme.surface, RoundedCornerShape(24.dp))
                .border(1.dp, MaterialTheme.colorScheme.outlineVariant, RoundedCornerShape(24.dp))
                .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Box(
            modifier =
                Modifier
                    .size(32.dp)
                    .background(circleBackground, CircleShape),
            contentAlignment = Alignment.Center,
        ) {
            XIcon(
                imageVector = portfolioIcon(asset.symbol),
                contentDescription = null,
                tint = circleIconTint,
                modifier = Modifier.size(18.dp),
            )
        }
        Spacer(Modifier.height(4.dp))
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

private fun portfolioIcon(symbol: String): ImageVector =
    when (symbol.uppercase()) {
        "BTC" -> Icons.Filled.CurrencyBitcoin
        "ETH" -> Icons.Filled.CurrencyExchange
        else -> Icons.Filled.MonetizationOn
    }
