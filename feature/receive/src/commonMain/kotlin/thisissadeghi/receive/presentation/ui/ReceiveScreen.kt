package thisissadeghi.receive.presentation.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import thisissadeghi.common.UiState
import thisissadeghi.designsystem.XButton
import thisissadeghi.designsystem.XCircularProgressIndicator
import thisissadeghi.designsystem.XIcon
import thisissadeghi.designsystem.XIconButton
import thisissadeghi.designsystem.XScaffold
import thisissadeghi.designsystem.XText
import thisissadeghi.receive.presentation.ReceiveUiModel
import thisissadeghi.receive.presentation.ReceiveUiState
import thisissadeghi.receive.presentation.ReceiveViewModel
import thisissadeghi.receive.presentation.ui.components.AddressCard
import thisissadeghi.receive.presentation.ui.components.AssetSelectorCard
import thisissadeghi.receive.presentation.ui.components.NetworkWarningBanner

@Composable
fun ReceiveScreen(
    viewModel: ReceiveViewModel,
    onBackClick: () -> Unit,
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    ReceiveScreenRoot(
        uiState = uiState,
        onBackClick = onBackClick,
        onRetry = viewModel::retry,
    )
}

@Composable
fun ReceiveScreenRoot(
    uiState: ReceiveUiState,
    onBackClick: () -> Unit,
    onRetry: () -> Unit,
    onAssetSelectorClick: () -> Unit = {},
    onCopyClick: () -> Unit = {},
    onShareClick: () -> Unit = {},
) {
    XScaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            Row(
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .height(64.dp)
                        .background(Color.Transparent)
                        .padding(horizontal = 16.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                XIconButton(
                    onClick = onBackClick,
                    modifier = Modifier.size(40.dp),
                    colors =
                        ButtonDefaults.buttonColors(
                            containerColor = Color.Transparent,
                            contentColor = MaterialTheme.colorScheme.primary,
                        ),
                ) {
                    XIcon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                    )
                }
                XText(
                    text = "Receive",
                    modifier = Modifier.padding(start = 8.dp),
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = (-0.5).sp,
                    color = MaterialTheme.colorScheme.onSurface,
                )
                Spacer(modifier = Modifier.weight(1f))
            }
        },
        bottomBar = {
            when (uiState.state) {
                is UiState.Success ->
                    ReceiveBottomBar(
                        onShareClick = onShareClick,
                        onCopyClick = onCopyClick,
                    )
                else -> {}
            }
        },
    ) { paddingValues ->
        when (val state = uiState.state) {
            UiState.Uninitialized,
            UiState.Loading,
            -> ReceiveLoadingContent(paddingValues = paddingValues)

            is UiState.Success ->
                ReceiveSuccessContent(
                    uiModel = state.value,
                    paddingValues = paddingValues,
                    onAssetSelectorClick = onAssetSelectorClick,
                    onCopyClick = onCopyClick,
                )

            is UiState.Failed -> ReceiveFailedContent(paddingValues = paddingValues, onRetry = onRetry)
        }
    }
}

@Composable
private fun ReceiveLoadingContent(paddingValues: PaddingValues) {
    Box(
        modifier =
            Modifier
                .fillMaxSize()
                .padding(paddingValues),
        contentAlignment = Alignment.Center,
    ) {
        XCircularProgressIndicator(
            modifier = Modifier.size(64.dp),
            color = MaterialTheme.colorScheme.primary,
            strokeWidth = 4.dp,
        )
    }
}

@Composable
private fun ReceiveSuccessContent(
    uiModel: ReceiveUiModel,
    paddingValues: PaddingValues,
    onAssetSelectorClick: () -> Unit,
    onCopyClick: () -> Unit,
) {
    Column(
        modifier =
            Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp)
                .padding(top = 24.dp),
    ) {
        AssetSelectorCard(
            coinName = uiModel.coinName,
            networkName = uiModel.networkName,
            onClick = onAssetSelectorClick,
        )
        Spacer(modifier = Modifier.height(24.dp))
        AddressCard(
            walletAddress = uiModel.walletAddress,
            onCopyClick = onCopyClick,
        )
        Spacer(modifier = Modifier.height(24.dp))
        NetworkWarningBanner()
    }
}

@Composable
private fun ReceiveBottomBar(
    onShareClick: () -> Unit,
    onCopyClick: () -> Unit,
) {
    Row(
        modifier =
            Modifier
                .fillMaxWidth()
                .shadow(8.dp, RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp))
                .background(MaterialTheme.colorScheme.surface, RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp))
                .border(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.3f), RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp))
                .padding(horizontal = 16.dp)
                .padding(top = 16.dp)
                .padding(bottom = 32.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        XButton(
            onClick = onShareClick,
            modifier =
                Modifier
                    .weight(1f)
                    .height(56.dp),
            shape = RoundedCornerShape(24.dp),
            colors =
                ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant,
                    contentColor = MaterialTheme.colorScheme.onSurface,
                ),
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
        ) {
            XIcon(imageVector = Icons.Default.Share, modifier = Modifier.size(20.dp))
            Spacer(modifier = Modifier.size(8.dp))
            XText(text = "Share", fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
        }
        XButton(
            onClick = onCopyClick,
            modifier =
                Modifier
                    .weight(1f)
                    .height(56.dp)
                    .shadow(
                        elevation = 4.dp,
                        shape = RoundedCornerShape(24.dp),
                        ambientColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.2f),
                        spotColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.2f),
                    ),
            shape = RoundedCornerShape(24.dp),
            colors =
                ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary,
                ),
        ) {
            XIcon(imageVector = Icons.Default.ContentCopy, modifier = Modifier.size(20.dp))
            Spacer(modifier = Modifier.size(8.dp))
            XText(text = "Copy Address", fontSize = 14.sp, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
private fun ReceiveFailedContent(
    paddingValues: PaddingValues,
    onRetry: () -> Unit,
) {
    Box(
        modifier =
            Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp),
        contentAlignment = Alignment.Center,
    ) {
        Column(
            modifier = Modifier.widthIn(max = 280.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
        ) {
            Box(
                modifier =
                    Modifier
                        .size(96.dp)
                        .clip(RoundedCornerShape(24.dp))
                        .background(MaterialTheme.colorScheme.error.copy(alpha = 0.1f)),
                contentAlignment = Alignment.Center,
            ) {
                XIcon(
                    imageVector = Icons.Default.Error,
                    modifier = Modifier.size(80.dp),
                    tint = MaterialTheme.colorScheme.error,
                )
            }
            Spacer(modifier = Modifier.height(24.dp))
            XText(
                text = "Failed to Load Address",
                fontSize = 20.sp,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center,
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .padding(bottom = 12.dp),
            )
            XText(
                text = "Unable to retrieve your wallet address. Please try again.",
                fontSize = 14.sp,
                fontWeight = FontWeight.Normal,
                color = MaterialTheme.colorScheme.outline,
                lineHeight = (14 * 1.625).sp,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth(),
            )
            Spacer(modifier = Modifier.height(24.dp))
            XButton(
                onClick = onRetry,
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .height(56.dp)
                        .widthIn(max = 200.dp),
                shape = RoundedCornerShape(12.dp),
                colors =
                    ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                        contentColor = MaterialTheme.colorScheme.onPrimary,
                    ),
            ) {
                XText(text = "Retry", fontSize = 16.sp, fontWeight = FontWeight.Bold)
            }
        }
    }
}
