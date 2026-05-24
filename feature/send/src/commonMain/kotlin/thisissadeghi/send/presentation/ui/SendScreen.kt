package thisissadeghi.send.presentation.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.VerifiedUser
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
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
import thisissadeghi.designsystem.toolbar.XTopAppBar
import thisissadeghi.designsystem.toolbar.XTopAppBarAlignment
import thisissadeghi.send.presentation.SendUiModel
import thisissadeghi.send.presentation.SendUiState
import thisissadeghi.send.presentation.SendViewModel
import thisissadeghi.send.presentation.ui.components.AssetNetworkGrid
import thisissadeghi.send.presentation.ui.components.HeroAmountSection
import thisissadeghi.send.presentation.ui.components.RecipientCard
import thisissadeghi.send.presentation.ui.components.TransactionSummaryCard

@Composable
fun SendScreen(
    viewModel: SendViewModel,
    onBackClick: () -> Unit,
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    SendScreenRoot(
        uiState = uiState,
        onBackClick = onBackClick,
        onRetry = viewModel::retry,
        onSendClick = viewModel::onSendClick,
        onQrScanClick = viewModel::onQrScanClick,
        onAddressChange = viewModel::onAddressChange,
        onPasteClick = viewModel::onPasteClick,
        onQuickAmountClick = { label ->
            when {
                label == "MAX" -> viewModel.onMaxClick()
                else -> viewModel.onPercentClick(label.dropLast(1).toIntOrNull() ?: 0)
            }
        },
        onCoinSelectClick = viewModel::onCoinSelectClick,
        onNetworkSelectClick = viewModel::onNetworkSelectClick,
    )
}

@Composable
fun SendScreenRoot(
    uiState: SendUiState,
    onBackClick: () -> Unit,
    onRetry: () -> Unit,
    onSendClick: () -> Unit = {},
    onQrScanClick: () -> Unit = {},
    onAddressChange: (String) -> Unit = {},
    onPasteClick: () -> Unit = {},
    onQuickAmountClick: (String) -> Unit = {},
    onCoinSelectClick: () -> Unit = {},
    onNetworkSelectClick: () -> Unit = {},
) {
    XScaffold(
        topBar = {
            XTopAppBar(
                title = {
                    XText(
                        text = "Send",
                    )
                },
                navigationIcon = {
                    XIconButton(
                        onClick = onBackClick,
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
                },
                backgroundColor = Color.Transparent,
                alignment = XTopAppBarAlignment.Start,
            )
        },
        bottomBar = {
            when (uiState.state) {
                is UiState.Success -> SendBottomBar(onSendClick = onSendClick)
                else -> {}
            }
        },
        containerColor = MaterialTheme.colorScheme.background,
    ) { paddingValues ->
        when (val state = uiState.state) {
            UiState.Uninitialized -> Box(modifier = Modifier.fillMaxSize())

            UiState.Loading -> LoadingContent(paddingValues = paddingValues)

            is UiState.Success ->
                SuccessContent(
                    uiModel = state.value,
                    paddingValues = paddingValues,
                    onAddressChange = onAddressChange,
                    onPasteClick = onPasteClick,
                    onQrClick = onQrScanClick,
                    onQuickAmountClick = onQuickAmountClick,
                    onCoinSelectClick = onCoinSelectClick,
                    onNetworkSelectClick = onNetworkSelectClick,
                )

            is UiState.Failed ->
                FailedContent(
                    paddingValues = paddingValues,
                    onRetry = onRetry,
                    onReturnToDashboard = onBackClick,
                )
        }
    }
}

@Composable
private fun LoadingContent(paddingValues: PaddingValues) {
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
        )
    }
}

@Composable
private fun SuccessContent(
    uiModel: SendUiModel,
    paddingValues: PaddingValues,
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
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .padding(start = 24.dp, top = 32.dp, end = 24.dp, bottom = 144.dp),
    ) {
        HeroAmountSection(
            amount = uiModel.amount,
            coinSymbol = uiModel.coinSymbol,
            balanceBtc = uiModel.balanceBtc,
            balanceUsd = uiModel.balanceUsd,
            onQuickAmountClick = onQuickAmountClick,
        )
        Spacer(modifier = Modifier.height(40.dp))
        RecipientCard(
            address = uiModel.recipientAddress,
            onAddressChange = onAddressChange,
            onPasteClick = onPasteClick,
            onQrClick = onQrClick,
        )
        Spacer(modifier = Modifier.height(20.dp))
        AssetNetworkGrid(
            coinName = uiModel.coinName,
            coinSymbol = uiModel.coinSymbol,
            networkName = uiModel.networkName,
            networkSubtitle = uiModel.networkDescription,
            onAssetClick = onCoinSelectClick,
            onNetworkClick = onNetworkSelectClick,
        )
        Spacer(modifier = Modifier.height(32.dp))
        TransactionSummaryCard(
            networkFee = uiModel.networkFee,
            totalDeduct = uiModel.totalDeduct,
            estimatedArrival = uiModel.estimatedArrival,
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
                text = "SECURED BY KMPILOT VAULT",
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

@Composable
private fun FailedContent(
    paddingValues: PaddingValues,
    onRetry: () -> Unit,
    onReturnToDashboard: () -> Unit,
) {
    Column(
        modifier =
            Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        XIcon(
            imageVector = Icons.Default.Warning,
            contentDescription = "Error",
            modifier = Modifier.size(80.dp),
            tint = MaterialTheme.colorScheme.error,
        )
        Spacer(modifier = Modifier.height(32.dp))
        XText(
            text = "Something went wrong",
            style =
                TextStyle(
                    fontSize = 20.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    letterSpacing = (-0.5).sp,
                    lineHeight = 32.5.sp,
                ),
            modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.Center,
        )
        Spacer(modifier = Modifier.height(8.dp))
        XText(
            text = "An unexpected error occurred. Please try again.",
            style =
                TextStyle(
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.outline,
                ),
            modifier = Modifier.widthIn(max = 240.dp),
            textAlign = TextAlign.Center,
        )
        Spacer(modifier = Modifier.height(32.dp))
        XButton(
            onClick = onRetry,
            modifier =
                Modifier
                    .fillMaxWidth()
                    .widthIn(max = 200.dp)
                    .height(56.dp),
            shape = RoundedCornerShape(12.dp),
            colors =
                ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary,
                ),
        ) {
            XText(
                text = "Retry",
                style =
                    TextStyle(
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                    ),
            )
        }
        Spacer(modifier = Modifier.height(8.dp))
        XText(
            text = "Return to Dashboard",
            style =
                TextStyle(
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                ),
            modifier =
                Modifier
                    .padding(vertical = 8.dp)
                    .clickable(onClick = onReturnToDashboard),
        )
    }
}

@Composable
private fun SendBottomBar(onSendClick: () -> Unit) {
    val background = MaterialTheme.colorScheme.background
    Box(
        modifier =
            Modifier
                .fillMaxWidth()
                .background(
                    brush =
                        Brush.verticalGradient(
                            colors = listOf(Color.Transparent, background.copy(alpha = 0.8f)),
                        ),
                ).padding(24.dp),
    ) {
        XButton(
            onClick = onSendClick,
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
            contentPadding = PaddingValues(horizontal = 24.dp),
        ) {
            XText(
                text = "Send Bitcoin",
                style =
                    TextStyle(
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                    ),
            )
            Spacer(modifier = Modifier.width(8.dp))
            XIcon(
                imageVector = Icons.AutoMirrored.Filled.Send,
                contentDescription = null,
                modifier = Modifier.size(20.dp),
            )
        }
    }
}
