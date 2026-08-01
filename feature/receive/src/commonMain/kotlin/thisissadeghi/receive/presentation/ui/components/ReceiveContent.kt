package thisissadeghi.receive.presentation.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import thisissadeghi.receive.data.model.ReceiveData

@Composable
fun ReceiveSuccessContent(
    data: ReceiveData,
    onAssetSelectorClick: () -> Unit,
    onCopyClick: () -> Unit,
) {
    Column(
        modifier =
            Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp)
                .padding(top = 24.dp),
    ) {
        AssetSelectorCard(
            coinName = data.coinName,
            networkName = data.networkName,
            onClick = onAssetSelectorClick,
        )
        Spacer(modifier = Modifier.height(24.dp))
        AddressCard(
            walletAddress = data.walletAddress,
            onCopyClick = onCopyClick,
        )
        Spacer(modifier = Modifier.height(24.dp))
        NetworkWarningBanner()
    }
}
