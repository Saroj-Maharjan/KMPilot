package thisissadeghi.assetdetail.presentation.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import thisissadeghi.designsystem.XText
import thisissadeghi.designsystem.XTheme

@Composable
fun StatsCard(
    label: String,
    value: String,
    modifier: Modifier = Modifier,
    isHighlighted: Boolean = false,
    subValue: String? = null,
) {
    val shape = RoundedCornerShape(24.dp)
    val borderColor =
        if (isHighlighted) {
            MaterialTheme.colorScheme.primary.copy(alpha = 0.2f)
        } else {
            MaterialTheme.colorScheme.outlineVariant
        }
    val elevation = if (isHighlighted) 4.dp else 1.dp

    Column(
        modifier =
            modifier
                .shadow(elevation, shape)
                .background(MaterialTheme.colorScheme.surface, shape)
                .border(BorderStroke(1.dp, borderColor), shape)
                .padding(20.dp),
    ) {
        if (isHighlighted) {
            XText(
                text = label,
                style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.primary,
            )
        } else {
            XText(
                text = label,
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
        Spacer(Modifier.height(4.dp))
        XText(
            text = value,
            style =
                MaterialTheme.typography.titleMedium.copy(
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                ),
            color = MaterialTheme.colorScheme.onSurface,
        )
        if (subValue != null) {
            XText(
                text = subValue,
                style =
                    MaterialTheme.typography.labelSmall.copy(
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Normal,
                    ),
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}

@Preview
@Composable
private fun StatsCardPreview() {
    XTheme {
        StatsCard(
            label = "Market Cap",
            value = "$1.3T",
        )
    }
}

@Preview
@Composable
private fun StatsCardHighlightedPreview() {
    XTheme {
        StatsCard(
            label = "Your Holdings",
            value = "0.085 BTC",
            isHighlighted = true,
            subValue = "~$5,730.00",
        )
    }
}
