package thisissadeghi.receive.presentation.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import thisissadeghi.common.UiState
import thisissadeghi.designsystem.XButton
import thisissadeghi.designsystem.XCircularProgressIndicator
import thisissadeghi.designsystem.XIcon
import thisissadeghi.designsystem.XIconButton
import thisissadeghi.designsystem.XOutlinedButton
import thisissadeghi.designsystem.XScaffold
import thisissadeghi.designsystem.XText
import thisissadeghi.designsystem.toolbar.XTopAppBar
import thisissadeghi.receive.presentation.ReceiveUiModel
import thisissadeghi.receive.presentation.ReceiveUiState
import thisissadeghi.receive.presentation.ReceiveViewModel
import thisissadeghi.receive.presentation.ui.components.AddressPill
import thisissadeghi.receive.presentation.ui.components.AssetSelectorCard
import thisissadeghi.receive.presentation.ui.components.NetworkWarningBanner

@Composable
fun ReceiveScreen(
    viewModel: ReceiveViewModel,
    onBackClick: () -> Unit,
) {
    val uiState by viewModel.uiState.collectAsState()
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
            XTopAppBar(
                title = {
                    XText(
                        text = "Receive",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = (-0.5).sp,
                        color = MaterialTheme.colorScheme.onSurface,
                    )
                },
                backgroundColor = MaterialTheme.colorScheme.background,
                navigationIcon = {
                    XIconButton(
                        onClick = onBackClick,
                        colors =
                            ButtonDefaults.buttonColors(
                                containerColor = Color.Transparent,
                                contentColor = MaterialTheme.colorScheme.onSurface,
                            ),
                    ) {
                        XIcon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                        )
                    }
                },
            )
        },
        bottomBar = {
            when (uiState.state) {
                is UiState.Success ->
                    ReceiveSuccessBottomBar(
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

            is UiState.Failed -> ReceiveFailedContent(paddingValues = paddingValues)
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
            modifier = Modifier.size(48.dp),
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
                .padding(horizontal = 16.dp, vertical = 24.dp)
                .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(24.dp),
    ) {
        AssetSelectorCard(
            coinName = uiModel.coinName,
            networkName = uiModel.networkName,
            onClick = onAssetSelectorClick,
        )
        AddressPill(
            address = uiModel.walletAddress,
            onCopyClick = onCopyClick,
        )
        NetworkWarningBanner(
            heading = "Bitcoin Network only",
            body = "Sending coins or tokens via any other network will result in permanent loss.",
        )
    }
}

@Composable
private fun ReceiveSuccessBottomBar(
    onShareClick: () -> Unit,
    onCopyClick: () -> Unit,
) {
    Box {
        Box(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .height(96.dp)
                    .align(Alignment.TopCenter)
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(Color.Transparent, MaterialTheme.colorScheme.background),
                        ),
                    ),
        )
        Row(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.background.copy(alpha = 0.8f))
                    .padding(start = 24.dp, end = 24.dp, top = 16.dp, bottom = 40.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            XOutlinedButton(
                onClick = onShareClick,
                modifier =
                    Modifier
                        .weight(1f)
                        .height(56.dp),
                shape = CircleShape,
                colors =
                    ButtonDefaults.outlinedButtonColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant,
                        contentColor = MaterialTheme.colorScheme.onSurface,
                    ),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
            ) {
                XIcon(
                    imageVector = Icons.Default.Share,
                    modifier = Modifier.size(20.dp),
                )
                Spacer(modifier = Modifier.size(8.dp))
                XText(
                    text = "Share",
                    fontWeight = FontWeight.SemiBold,
                )
            }
            XButton(
                onClick = onCopyClick,
                modifier =
                    Modifier
                        .weight(1.5f)
                        .height(56.dp),
                shape = CircleShape,
                colors =
                    ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                        contentColor = MaterialTheme.colorScheme.onPrimary,
                    ),
            ) {
                XIcon(
                    imageVector = Icons.Default.ContentCopy,
                    modifier = Modifier.size(20.dp),
                )
                Spacer(modifier = Modifier.size(8.dp))
                XText(
                    text = "Copy Address",
                    fontWeight = FontWeight.Bold,
                )
            }
        }
    }
}

@Composable
private fun ReceiveFailedContent(paddingValues: PaddingValues) {
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
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface,
                textAlign = TextAlign.Center,
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .padding(bottom = 12.dp),
            )
            XText(
                text = "Unable to retrieve your wallet address. Please try again.",
                fontSize = 16.sp,
                fontWeight = FontWeight.Normal,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                lineHeight = (16 * 1.625).sp,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth(),
            )
        }
    }
}
