package thisissadeghi.assetdetail.presentation.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import kmpilot.feature.assetdetail.generated.resources.Res
import kmpilot.feature.assetdetail.generated.resources.chip_25
import kmpilot.feature.assetdetail.generated.resources.chip_50
import kmpilot.feature.assetdetail.generated.resources.chip_75
import kmpilot.feature.assetdetail.generated.resources.chip_max
import org.jetbrains.compose.resources.stringResource
import thisissadeghi.designsystem.XText
import thisissadeghi.designsystem.XTheme

private data class QuickAmount(
    val labelKey: @Composable () -> String,
    val percent: Float,
)

@Composable
fun QuickAmountChips(
    selectedPercent: Float,
    onSelect: (Float) -> Unit,
    modifier: Modifier = Modifier,
) {
    val chips =
        listOf(
            Pair(stringResource(Res.string.chip_25), 0.25f),
            Pair(stringResource(Res.string.chip_50), 0.50f),
            Pair(stringResource(Res.string.chip_75), 0.75f),
            Pair(stringResource(Res.string.chip_max), 1.0f),
        )
    val shape = RoundedCornerShape(16.dp)

    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        chips.forEach { (label, percent) ->
            val isSelected = selectedPercent == percent
            Box(
                modifier =
                    Modifier
                        .weight(1f)
                        .clip(shape)
                        .background(
                            if (isSelected) {
                                MaterialTheme.colorScheme.primary
                            } else {
                                MaterialTheme.colorScheme.surfaceVariant
                            },
                            shape,
                        ).clickable { onSelect(percent) }
                        .padding(vertical = 8.dp),
                contentAlignment = Alignment.Center,
            ) {
                XText(
                    text = label,
                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                    color =
                        if (isSelected) {
                            MaterialTheme.colorScheme.onPrimary
                        } else {
                            MaterialTheme.colorScheme.onSurfaceVariant
                        },
                )
            }
        }
    }
}

@Preview
@Composable
private fun QuickAmountChipsPreview() {
    XTheme {
        QuickAmountChips(
            selectedPercent = 0.5f,
            onSelect = {},
        )
    }
}
