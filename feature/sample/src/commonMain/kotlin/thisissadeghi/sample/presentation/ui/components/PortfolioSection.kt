package thisissadeghi.sample.presentation.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import thisissadeghi.designsystem.XCard
import thisissadeghi.designsystem.XHorizontalDivider
import thisissadeghi.designsystem.XText
import thisissadeghi.designsystem.XTheme
import thisissadeghi.sample.data.model.PortfolioAsset
import thisissadeghi.sample.presentation.ui.formatMoney

@Composable
internal fun PortfolioSection(assets: List<PortfolioAsset>) {
    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        XText("Portfolio Assets", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
        XCard(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            shape = RoundedCornerShape(24.dp),
            elevation = CardDefaults.cardElevation(0.dp),
        ) {
            Column {
                assets.forEachIndexed { index, asset ->
                    PortfolioAssetItem(asset, index)
                    if (index < assets.lastIndex) {
                        XHorizontalDivider()
                    }
                }
            }
        }
    }
}

@Composable
private fun PortfolioAssetItem(
    asset: PortfolioAsset,
    index: Int,
) {
    val opacity =
        when (index) {
            0 -> 1.0f
            1 -> 0.8f
            else -> 0.6f
        }
    val changeColor = if (asset.changePercent >= 0) XTheme.Colors.Success else XTheme.Colors.Danger
    val sign = if (asset.changePercent >= 0) "+" else ""
    Row(
        modifier =
            Modifier
                .fillMaxWidth()
                .padding(16.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(
            modifier =
                Modifier
                    .size(40.dp)
                    .background(MaterialTheme.colorScheme.primary.copy(alpha = opacity), CircleShape),
            contentAlignment = Alignment.Center,
        ) {
            XText(
                asset.symbol.take(3),
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White,
            )
        }
        Column(modifier = Modifier.weight(1f)) {
            XText(asset.name, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
            XText("${asset.balance} ${asset.symbol}", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
        Column(horizontalAlignment = Alignment.End) {
            XText(
                "$${ asset.value.formatMoney() }",
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface,
            )
            XText("$sign${asset.changePercent}%", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = changeColor)
        }
    }
}
