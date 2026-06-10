package thisissadeghi.assetdetail.presentation.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SliderDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kmpilot.feature.assetdetail.generated.resources.Res
import kmpilot.feature.assetdetail.generated.resources.account_balance_wallet
import kmpilot.feature.assetdetail.generated.resources.action_confirm_purchase
import kmpilot.feature.assetdetail.generated.resources.buy_currency_prefix
import kmpilot.feature.assetdetail.generated.resources.buy_sheet_subtitle
import kmpilot.feature.assetdetail.generated.resources.buy_sheet_title
import kmpilot.feature.assetdetail.generated.resources.expand_more
import kmpilot.feature.assetdetail.generated.resources.label_approx_btc
import kmpilot.feature.assetdetail.generated.resources.label_pay_with
import kmpilot.feature.assetdetail.generated.resources.label_wallet
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import thisissadeghi.assetdetail.data.model.AssetDetailResponse
import thisissadeghi.assetdetail.presentation.AssetDetailUiModel
import thisissadeghi.designsystem.XButton
import thisissadeghi.designsystem.XIcon
import thisissadeghi.designsystem.XModalBottomSheet
import thisissadeghi.designsystem.XSlider
import thisissadeghi.designsystem.XText
import thisissadeghi.designsystem.XTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BuyBottomSheet(
    uiModel: AssetDetailUiModel,
    assetDetail: AssetDetailResponse,
    onDismiss: () -> Unit,
    onAmountChange: (String) -> Unit,
    onSliderChange: (Float) -> Unit,
    onQuickAmountSelect: (Float) -> Unit,
    onConfirm: () -> Unit,
    modifier: Modifier = Modifier,
) {
    XModalBottomSheet(
        onDismissRequest = onDismiss,
        modifier = modifier,
        containerColor = MaterialTheme.colorScheme.surface,
        shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
        scrimColor = MaterialTheme.colorScheme.background.copy(alpha = 0.6f),
        dragHandle = {
            Box(
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .padding(top = 12.dp, bottom = 16.dp),
                contentAlignment = Alignment.Center,
            ) {
                Box(
                    modifier =
                        Modifier
                            .size(width = 40.dp, height = 4.dp)
                            .background(MaterialTheme.colorScheme.outlineVariant, CircleShape),
                )
            }
        },
    ) {
        Column(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp)
                    .padding(bottom = 32.dp),
        ) {
            // Header
            Column(modifier = Modifier.padding(bottom = 24.dp)) {
                XText(
                    text = stringResource(Res.string.buy_sheet_title, assetDetail.name),
                    style =
                        MaterialTheme.typography.titleMedium.copy(
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                        ),
                    color = MaterialTheme.colorScheme.onSurface,
                )
                XText(
                    text = stringResource(Res.string.buy_sheet_subtitle),
                    style = MaterialTheme.typography.bodySmall.copy(fontSize = 13.sp),
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }

            // Amount input display row
            Row(
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .height(56.dp)
                        .background(
                            MaterialTheme.colorScheme.surfaceVariant,
                            RoundedCornerShape(24.dp),
                        ).border(
                            1.5.dp,
                            MaterialTheme.colorScheme.primary,
                            RoundedCornerShape(24.dp),
                        ).padding(horizontal = 16.dp)
                        .padding(bottom = 24.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                // Currency prefix + editable input
                Row(
                    modifier = Modifier.weight(1f),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    XText(
                        text = stringResource(Res.string.buy_currency_prefix),
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                    Spacer(Modifier.width(8.dp))
                    BasicTextField(
                        value = uiModel.buyAmountInput,
                        onValueChange = onAmountChange,
                        textStyle =
                            MaterialTheme.typography.titleMedium.copy(
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface,
                            ),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                        singleLine = true,
                        modifier = Modifier.weight(1f),
                    )
                }
                // Approx BTC
                val approxBtc =
                    (uiModel.buyAmountInput.toDoubleOrNull() ?: 0.0) /
                        assetDetail.price
                XText(
                    text = stringResource(Res.string.label_approx_btc, "%.4f".format(approxBtc)),
                    style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Normal),
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }

            Spacer(Modifier.height(24.dp + 24.dp)) // compensate for row padding(bottom=24dp)

            // Slider section
            Column(modifier = Modifier.padding(bottom = 32.dp)) {
                XSlider(
                    value = uiModel.buySliderValue,
                    onValueChange = onSliderChange,
                    modifier = Modifier.fillMaxWidth(),
                    colors =
                        SliderDefaults.colors(
                            activeTrackColor = MaterialTheme.colorScheme.primary,
                            inactiveTrackColor = MaterialTheme.colorScheme.outlineVariant,
                            thumbColor = MaterialTheme.colorScheme.primary,
                        ),
                )
                Spacer(Modifier.height(12.dp))
                QuickAmountChips(
                    selectedPercent = uiModel.buySliderValue,
                    onSelect = onQuickAmountSelect,
                )
            }

            // Pay-with section
            Column(modifier = Modifier.padding(bottom = 32.dp)) {
                XText(
                    text = stringResource(Res.string.label_pay_with),
                    style = MaterialTheme.typography.bodySmall.copy(fontSize = 13.sp),
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(bottom = 8.dp),
                )
                Row(
                    modifier =
                        Modifier
                            .fillMaxWidth()
                            .height(52.dp)
                            .background(
                                MaterialTheme.colorScheme.surfaceVariant,
                                RoundedCornerShape(24.dp),
                            ).border(
                                1.dp,
                                MaterialTheme.colorScheme.outlineVariant,
                                RoundedCornerShape(24.dp),
                            ).padding(horizontal = 16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Row(
                        modifier = Modifier.weight(1f),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        XIcon(
                            painter = painterResource(Res.drawable.account_balance_wallet),
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(20.dp),
                        )
                        Spacer(Modifier.width(12.dp))
                        XText(
                            text = stringResource(Res.string.label_wallet),
                            style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Normal),
                            color = MaterialTheme.colorScheme.onSurface,
                        )
                    }
                    XIcon(
                        painter = painterResource(Res.drawable.expand_more),
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(20.dp),
                    )
                }
            }

            // Confirm button
            XButton(
                onClick = onConfirm,
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                shape = RoundedCornerShape(24.dp),
                colors =
                    ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                        contentColor = MaterialTheme.colorScheme.onPrimary,
                    ),
            ) {
                XText(
                    text = stringResource(Res.string.action_confirm_purchase),
                    style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.onPrimary,
                )
            }
        }
    }
}

@Preview
@Composable
private fun BuyBottomSheetPreview() {
    XTheme {
        BuyBottomSheet(
            uiModel =
                AssetDetailUiModel(
                    buyAmountInput = "500.00",
                    buySliderValue = 0.5f,
                    isBuySheetVisible = true,
                ),
            assetDetail =
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
            onDismiss = {},
            onAmountChange = {},
            onSliderChange = {},
            onQuickAmountSelect = {},
            onConfirm = {},
        )
    }
}
