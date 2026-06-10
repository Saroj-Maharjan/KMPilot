package thisissadeghi.swap.presentation.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kmpilot.feature.swap.generated.resources.Res
import kmpilot.feature.swap.generated.resources.swap_amount_placeholder
import kmpilot.feature.swap.generated.resources.swap_balance_template
import kmpilot.feature.swap.generated.resources.swap_max_label
import org.jetbrains.compose.resources.stringResource
import thisissadeghi.designsystem.XText
import thisissadeghi.designsystem.XTheme

@Composable
fun FromAssetSection(
    avatarUrl: String,
    coinName: String,
    coinTicker: String,
    balanceLabel: String,
    amount: String,
    onAmountChange: (String) -> Unit,
    onMaxClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    AssetCard(modifier = modifier) {
        AssetCardHeader(
            avatarUrl = avatarUrl,
            avatarContentDescription = coinTicker,
            iconCircleColor = MaterialTheme.colorScheme.primaryContainer,
            coinName = coinName,
            coinTicker = coinTicker,
            trailing = {
                Box(
                    modifier =
                        Modifier
                            .background(MaterialTheme.colorScheme.surfaceVariant, RoundedCornerShape(16.dp))
                            .clickable(onClick = onMaxClick)
                            .padding(horizontal = 12.dp, vertical = 6.dp),
                ) {
                    XText(
                        text = stringResource(Res.string.swap_max_label),
                        style =
                            MaterialTheme.typography.labelSmall.copy(
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                            ),
                        color = MaterialTheme.colorScheme.primary,
                    )
                }
            },
        )
        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
            XText(
                text = stringResource(Res.string.swap_balance_template, balanceLabel),
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            BasicTextField(
                value = amount,
                onValueChange = onAmountChange,
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp),
                textStyle =
                    MaterialTheme.typography.displaySmall.copy(
                        fontWeight = FontWeight.ExtraBold,
                        color = MaterialTheme.colorScheme.onSurface,
                    ),
                singleLine = true,
                decorationBox = { innerTextField ->
                    if (amount.isEmpty()) {
                        XText(
                            text = stringResource(Res.string.swap_amount_placeholder),
                            style =
                                MaterialTheme.typography.displaySmall.copy(
                                    fontWeight = FontWeight.ExtraBold,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.3f),
                                ),
                        )
                    }
                    innerTextField()
                },
            )
        }
    }
}

@Preview
@Composable
private fun FromAssetSectionPreview() {
    XTheme {
        FromAssetSection(
            avatarUrl = "",
            coinName = "Bitcoin",
            coinTicker = "BTC",
            balanceLabel = "0.4821 BTC",
            amount = "",
            onAmountChange = {},
            onMaxClick = {},
        )
    }
}
