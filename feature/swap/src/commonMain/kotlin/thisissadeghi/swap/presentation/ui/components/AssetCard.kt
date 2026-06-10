package thisissadeghi.swap.presentation.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import thisissadeghi.designsystem.AsyncImage
import thisissadeghi.designsystem.DesignSystemResources
import thisissadeghi.designsystem.XText
import thisissadeghi.designsystem.XTheme

/**
 * Shared card shell for both From/To asset sections — surface bg, outlineVariant border, 20dp corners.
 * Custom `Column` (not `XCard`) per the blueprint's Component Overrides.
 */
@Composable
fun AssetCard(
    modifier: Modifier = Modifier,
    content: @Composable ColumnScope.() -> Unit,
) {
    Column(
        modifier =
            modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.surface, RoundedCornerShape(20.dp))
                .border(1.dp, MaterialTheme.colorScheme.outlineVariant, RoundedCornerShape(20.dp))
                .padding(24.dp),
        content = content,
    )
}

/**
 * Shared header row (icon circle + name/ticker + trailing slot) used by `FromAssetSection`/`ToAssetSection`.
 */
@Composable
fun AssetCardHeader(
    avatarUrl: String,
    avatarContentDescription: String,
    iconCircleColor: Color,
    coinName: String,
    coinTicker: String,
    trailing: @Composable () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier =
            modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Box(
                modifier =
                    Modifier
                        .size(40.dp)
                        .background(iconCircleColor, CircleShape)
                        .border(1.dp, MaterialTheme.colorScheme.outlineVariant, CircleShape),
                contentAlignment = Alignment.Center,
            ) {
                AsyncImage(
                    url = avatarUrl,
                    loadingResId = DesignSystemResources.drawable.ds_image_placeholder,
                    contentDescription = avatarContentDescription,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.size(24.dp).clip(CircleShape),
                )
            }
            Column {
                XText(
                    text = coinName,
                    style =
                        MaterialTheme.typography.titleMedium.copy(
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                        ),
                )
                XText(
                    text = coinTicker,
                    style =
                        MaterialTheme.typography.labelSmall.copy(
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Normal,
                            letterSpacing = (0.05 * 12).sp,
                        ),
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }
        trailing()
    }
}

@Preview
@Composable
private fun AssetCardPreview() {
    XTheme {
        AssetCard {
            AssetCardHeader(
                avatarUrl = "",
                avatarContentDescription = "BTC",
                iconCircleColor = MaterialTheme.colorScheme.primaryContainer,
                coinName = "Bitcoin",
                coinTicker = "BTC",
                trailing = {},
            )
        }
    }
}
