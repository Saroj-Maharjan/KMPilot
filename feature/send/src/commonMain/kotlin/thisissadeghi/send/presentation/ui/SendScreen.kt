package thisissadeghi.send.presentation.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
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
import androidx.compose.material.icons.filled.CurrencyBitcoin
import androidx.compose.material.icons.filled.ErrorOutline
import androidx.compose.material.icons.filled.Lan
import androidx.compose.material.icons.filled.QrCodeScanner
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import thisissadeghi.common.UiState
import thisissadeghi.designsystem.XButton
import thisissadeghi.designsystem.XCircularProgressIndicator
import thisissadeghi.designsystem.XIcon
import thisissadeghi.designsystem.XIconButton
import thisissadeghi.designsystem.XScaffold
import thisissadeghi.designsystem.XText
import thisissadeghi.designsystem.toolbar.XTopAppBar
import thisissadeghi.send.presentation.SendUiModel
import thisissadeghi.send.presentation.SendUiState
import thisissadeghi.send.presentation.SendViewModel
import thisissadeghi.send.presentation.ui.components.AmountInput
import thisissadeghi.send.presentation.ui.components.AssetSelectorRow
import thisissadeghi.send.presentation.ui.components.RecipientAddressInput
import thisissadeghi.send.presentation.ui.components.TransactionSummaryCard

@Composable
fun SendScreen(
    viewModel: SendViewModel,
    onBackClick: () -> Unit,
) {
    val uiState by viewModel.uiState.collectAsState()
    SendScreenRoot(
        uiState = uiState,
        onBackClick = onBackClick,
        onRetry = viewModel::retry,
        onSendClick = viewModel::onSendClick,
        onQrScanClick = viewModel::onQrScanClick,
        onAddressChange = viewModel::onAddressChange,
        onPasteClick = viewModel::onPasteClick,
        onPercentClick = viewModel::onPercentClick,
        onMaxClick = viewModel::onMaxClick,
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
    onPercentClick: (Int) -> Unit = {},
    onMaxClick: () -> Unit = {},
    onCoinSelectClick: () -> Unit = {},
    onNetworkSelectClick: () -> Unit = {},
) {
    XScaffold(
        topBar = {
            XTopAppBar(
                title = {
                    XText(
                        text = "Send",
                        style =
                            TextStyle(
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = (-0.5).sp,
                            ),
                    )
                },
                navigationIcon = {
                    XIconButton(
                        onClick = onBackClick,
                        modifier = Modifier.padding(8.dp),
                    ) {
                        XIcon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                        )
                    }
                },
                actions = {
                    if (uiState.state is UiState.Success || uiState.state is UiState.Uninitialized) {
                        XIconButton(
                            onClick = onQrScanClick,
                            modifier = Modifier.padding(8.dp),
                        ) {
                            XIcon(
                                imageVector = Icons.Default.QrCodeScanner,
                                contentDescription = "Scan QR",
                            )
                        }
                    }
                },
                backgroundColor = MaterialTheme.colorScheme.background,
            )
        },
        bottomBar = {
            when (uiState.state) {
                is UiState.Success -> SendBottomBar(onSendClick = onSendClick)
                is UiState.Failed -> RetryBottomBar(onRetryClick = onRetry)
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
                    onPercentClick = onPercentClick,
                    onMaxClick = onMaxClick,
                    onCoinSelectClick = onCoinSelectClick,
                    onNetworkSelectClick = onNetworkSelectClick,
                )

            is UiState.Failed -> FailedContent(paddingValues = paddingValues)
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
            modifier = Modifier.size(48.dp),
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
    onPercentClick: (Int) -> Unit,
    onMaxClick: () -> Unit,
    onCoinSelectClick: () -> Unit,
    onNetworkSelectClick: () -> Unit,
) {
    Column(
        modifier =
            Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp),
    ) {
        Spacer(modifier = Modifier.height(16.dp))
        RecipientAddressInput(
            value = uiModel.recipientAddress,
            onValueChange = onAddressChange,
            onPasteClick = onPasteClick,
        )
        Spacer(modifier = Modifier.height(32.dp))
        AmountInput(
            amount = uiModel.amount,
            coinSymbol = uiModel.coinSymbol,
            balance = uiModel.availableBalance,
            onPercentClick = onPercentClick,
            onMaxClick = onMaxClick,
        )
        Spacer(modifier = Modifier.height(40.dp))
        Column(
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            AssetSelectorRow(
                label = "Asset",
                iconVector = Icons.Default.CurrencyBitcoin,
                iconTint = Color(0xFFEAB308),
                iconBackground = Color(0xFFEAB308).copy(alpha = 0.2f),
                name = uiModel.coinName,
                subtitle = uiModel.coinSymbol,
                onClick = onCoinSelectClick,
            )
            AssetSelectorRow(
                label = "Network",
                iconVector = Icons.Default.Lan,
                iconTint = MaterialTheme.colorScheme.primary,
                iconBackground = MaterialTheme.colorScheme.primary.copy(alpha = 0.2f),
                name = uiModel.networkName,
                subtitle = uiModel.networkDescription,
                onClick = onNetworkSelectClick,
            )
        }
        Spacer(modifier = Modifier.height(32.dp))
        TransactionSummaryCard(
            networkFee = uiModel.networkFee,
            totalDeduct = uiModel.totalDeduct,
            estimatedArrival = uiModel.estimatedArrival,
        )
        Spacer(modifier = Modifier.height(24.dp))
    }
}

@Composable
private fun FailedContent(paddingValues: PaddingValues) {
    Column(
        modifier =
            Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Box(
            modifier =
                Modifier
                    .padding(bottom = 32.dp)
                    .background(
                        color = MaterialTheme.colorScheme.error.copy(alpha = 0.1f),
                        shape = CircleShape,
                    ).padding(24.dp),
            contentAlignment = Alignment.Center,
        ) {
            XIcon(
                imageVector = Icons.Default.ErrorOutline,
                contentDescription = "Error",
                modifier = Modifier.size(80.dp),
                tint = MaterialTheme.colorScheme.error,
            )
        }
        XText(
            text = "Transaction Failed",
            style =
                TextStyle(
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface,
                ),
            modifier =
                Modifier
                    .fillMaxWidth()
                    .padding(start = 16.dp, end = 16.dp, bottom = 16.dp),
            textAlign = TextAlign.Center,
        )
        XText(
            text = "Something went wrong. Please check your details and try again.",
            style =
                TextStyle(
                    fontSize = 16.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    lineHeight = 26.sp,
                ),
            modifier = Modifier.widthIn(max = 280.dp),
            textAlign = TextAlign.Center,
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
                            colors =
                                listOf(
                                    Color.Transparent,
                                    background.copy(alpha = 0.95f),
                                    background,
                                ),
                        ),
                ).padding(24.dp),
    ) {
        Column {
            XButton(
                onClick = onSendClick,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(24.dp),
                colors =
                    ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                        contentColor = MaterialTheme.colorScheme.onPrimary,
                    ),
                elevation = ButtonDefaults.buttonElevation(defaultElevation = 8.dp),
                contentPadding = PaddingValues(vertical = 16.dp),
            ) {
                XText(
                    text = "Send Bitcoin",
                    style =
                        TextStyle(
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                        ),
                )
            }
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
private fun RetryBottomBar(onRetryClick: () -> Unit) {
    val background = MaterialTheme.colorScheme.background
    Box(
        modifier =
            Modifier
                .fillMaxWidth()
                .background(
                    brush =
                        Brush.verticalGradient(
                            colors =
                                listOf(
                                    Color.Transparent,
                                    background.copy(alpha = 0.95f),
                                    background,
                                ),
                        ),
                ).padding(24.dp),
    ) {
        Column {
            XButton(
                onClick = onRetryClick,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(24.dp),
                colors =
                    ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                        contentColor = MaterialTheme.colorScheme.onPrimary,
                    ),
                elevation = ButtonDefaults.buttonElevation(defaultElevation = 8.dp),
                contentPadding = PaddingValues(vertical = 16.dp),
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
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}
