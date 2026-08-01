package thisissadeghi.send.presentation.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.VerifiedUser
import androidx.compose.material3.LocalContentColor
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kmpilot.feature.send.generated.resources.Res
import kmpilot.feature.send.generated.resources.secured_by_vault
import org.jetbrains.compose.resources.stringResource
import thisissadeghi.designsystem.XIcon
import thisissadeghi.designsystem.XText
import thisissadeghi.send.data.model.SendData

@Composable
fun SuccessContent(
    data: SendData,
    onAddressChange: (String) -> Unit,
    onPasteClick: () -> Unit,
    onQrClick: () -> Unit,
    onQuickAmountClick: (String) -> Unit,
    onCoinSelectClick: () -> Unit,
    onNetworkSelectClick: () -> Unit,
) {
    Column(
        modifier =
            Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(start = 24.dp, top = 32.dp, end = 24.dp, bottom = 144.dp),
    ) {
        HeroAmountSection(
            amount = data.amount,
            coinSymbol = data.selectedCoin.symbol,
            balanceBtc = data.balanceBtc,
            balanceUsd = data.balanceUsd,
            onQuickAmountClick = onQuickAmountClick,
        )
        Spacer(modifier = Modifier.height(40.dp))
        RecipientCard(
            address = data.recipientAddress,
            onAddressChange = onAddressChange,
            onPasteClick = onPasteClick,
            onQrClick = onQrClick,
        )
        Spacer(modifier = Modifier.height(20.dp))
        AssetNetworkGrid(
            coinName = data.selectedCoin.name,
            coinSymbol = data.selectedCoin.symbol,
            networkName = data.selectedNetwork.name,
            networkSubtitle = data.selectedNetwork.description,
            onAssetClick = onCoinSelectClick,
            onNetworkClick = onNetworkSelectClick,
        )
        Spacer(modifier = Modifier.height(32.dp))
        TransactionSummaryCard(
            networkFee = data.networkFee,
            totalDeduct = data.totalDeduct,
            estimatedArrival = data.estimatedArrival,
        )
        Spacer(modifier = Modifier.height(32.dp))
        Row(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .alpha(0.5f),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            XIcon(
                imageVector = Icons.Default.VerifiedUser,
                contentDescription = null,
                modifier = Modifier.size(12.dp),
                tint = LocalContentColor.current,
            )
            Spacer(modifier = Modifier.width(8.dp))
            XText(
                text = stringResource(Res.string.secured_by_vault),
                style =
                    TextStyle(
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = (2.0).sp,
                    ),
            )
        }
    }
}
