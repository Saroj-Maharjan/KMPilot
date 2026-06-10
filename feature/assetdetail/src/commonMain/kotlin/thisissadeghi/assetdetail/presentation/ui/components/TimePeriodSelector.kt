package thisissadeghi.assetdetail.presentation.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import kmpilot.feature.assetdetail.generated.resources.Res
import kmpilot.feature.assetdetail.generated.resources.chip_1d
import kmpilot.feature.assetdetail.generated.resources.chip_1m
import kmpilot.feature.assetdetail.generated.resources.chip_1w
import kmpilot.feature.assetdetail.generated.resources.chip_1y
import kmpilot.feature.assetdetail.generated.resources.chip_all
import org.jetbrains.compose.resources.stringResource
import thisissadeghi.designsystem.XText
import thisissadeghi.designsystem.XTheme

private val periods = listOf("1D", "1W", "1M", "1Y", "All")

@Composable
fun TimePeriodSelector(
    selectedPeriod: String,
    onPeriodSelected: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier =
            modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp)
                .padding(bottom = 40.dp)
                .horizontalScroll(rememberScrollState()),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        periods.forEach { period ->
            val isSelected = period == selectedPeriod
            val label =
                when (period) {
                    "1D" -> stringResource(Res.string.chip_1d)
                    "1W" -> stringResource(Res.string.chip_1w)
                    "1M" -> stringResource(Res.string.chip_1m)
                    "1Y" -> stringResource(Res.string.chip_1y)
                    else -> stringResource(Res.string.chip_all)
                }
            Box(
                modifier =
                    Modifier
                        .clip(CircleShape)
                        .then(
                            if (isSelected) {
                                Modifier.background(MaterialTheme.colorScheme.primary, CircleShape)
                            } else {
                                Modifier.border(1.dp, MaterialTheme.colorScheme.outlineVariant, CircleShape)
                            },
                        ).clickable { onPeriodSelected(period) }
                        .padding(horizontal = 20.dp, vertical = 8.dp),
            ) {
                XText(
                    text = label,
                    style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold),
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
private fun TimePeriodSelectorPreview() {
    XTheme {
        TimePeriodSelector(
            selectedPeriod = "1D",
            onPeriodSelected = {},
        )
    }
}
