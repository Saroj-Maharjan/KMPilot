package thisissadeghi.swap.presentation.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import kmpilot.feature.swap.generated.resources.Res
import kmpilot.feature.swap.generated.resources.swap_rate_template
import org.jetbrains.compose.resources.stringResource
import thisissadeghi.common.ext.formatDecimals
import thisissadeghi.designsystem.XTheme
import thisissadeghi.swap.data.model.SwapAsset
import thisissadeghi.swap.data.model.SwapQuoteResponse

@Composable
fun SwapContent(
    quote: SwapQuoteResponse,
    fromAmount: String,
    toAmount: String,
    onFromAmountChange: (String) -> Unit,
    onMaxClick: () -> Unit,
    onSwapDirectionClick: () -> Unit,
    onSlippageClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier =
            modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp)
                .padding(top = 8.dp, bottom = 160.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        FromAssetSection(
            avatarUrl = quote.fromAsset.avatarUrl,
            coinName = quote.fromAsset.name,
            coinTicker = quote.fromAsset.symbol,
            balanceLabel = "${quote.fromAsset.balance.formatDecimals(4)} ${quote.fromAsset.symbol}",
            amount = fromAmount,
            onAmountChange = onFromAmountChange,
            onMaxClick = onMaxClick,
        )
        SwapDirectionToggle(onClick = onSwapDirectionClick)
        ToAssetSection(
            avatarUrl = quote.toAsset.avatarUrl,
            coinName = quote.toAsset.name,
            coinTicker = quote.toAsset.symbol,
            receiveAmount = toAmount,
        )
        RateRow(rateText = stringResource(Res.string.swap_rate_template, quote.rateDisplay))
        SwapDetailsCard(
            networkFee = quote.networkFee,
            slippage = quote.slippageTolerance,
            estimatedTotal = quote.estimatedTotal,
            onSlippageClick = onSlippageClick,
        )
    }
}

@Preview
@Composable
private fun SwapContentPreview() {
    XTheme {
        SwapContent(
            quote =
                SwapQuoteResponse(
                    fromAsset =
                        SwapAsset(
                            id = "btc",
                            name = "Bitcoin",
                            symbol = "BTC",
                            avatarUrl = "",
                            balance = 0.4821,
                        ),
                    toAsset =
                        SwapAsset(
                            id = "eth",
                            name = "Ethereum",
                            symbol = "ETH",
                            avatarUrl = "",
                            balance = 8.5994,
                        ),
                    exchangeRate = 17.84,
                    rateDisplay = "1 BTC ≈ 17.84 ETH",
                    networkFee = "$7.20",
                    slippageTolerance = "0.5%",
                    estimatedTotal = "0.4821 BTC",
                ),
            fromAmount = "",
            toAmount = "8.5994",
            onFromAmountChange = {},
            onMaxClick = {},
            onSwapDirectionClick = {},
            onSlippageClick = {},
        )
    }
}
