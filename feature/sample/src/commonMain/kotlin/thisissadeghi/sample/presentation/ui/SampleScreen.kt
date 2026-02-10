package thisissadeghi.sample.presentation.ui

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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import thisissadeghi.common.UiState
import thisissadeghi.common.asString
import thisissadeghi.designsystem.XButton
import thisissadeghi.designsystem.XCard
import thisissadeghi.designsystem.XCircularProgressIndicator
import thisissadeghi.designsystem.XScaffold
import thisissadeghi.designsystem.XText
import thisissadeghi.designsystem.toolbar.XTopAppBar
import thisissadeghi.sample.data.model.SampleItem
import thisissadeghi.sample.presentation.SampleUiModel
import thisissadeghi.sample.presentation.SampleViewModel
import thisissadeghi.sample.presentation.ui.components.SampleCard

private val GhostGray = Color(0xFFE5E4E7)
private val TextMutedGray = Color(0xFF7D7887)
private val TitleDark = Color(0xFF323036)

/**
 * Sample screen with ViewModel dependency.
 */
@Composable
fun SampleScreen(
    viewModel: SampleViewModel,
    onItemClick: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    val uiState by viewModel.uiModelState.collectAsState()

    SampleScreenRoot(
        uiState = uiState,
        onItemClick = { item ->
            viewModel.onItemClick(item)
            onItemClick(item.id)
        },
        onRetry = viewModel::retry,
        modifier = modifier,
    )
}

/**
 * Sample screen root — ViewModel-independent for testing.
 * Demonstrates the 4-state UI pattern: Uninitialized, Loading, Success, Failed.
 */
@Composable
fun SampleScreenRoot(
    uiState: SampleUiModel,
    onItemClick: (SampleItem) -> Unit,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier,
) {
    XScaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            XTopAppBar(
                title = {
                    XText(
                        text = "Collection",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = TitleDark,
                    )
                },
            )
        },
    ) { paddingValues ->
        when (val itemsState = uiState.itemsState) {
            UiState.Uninitialized ->
                UninitializedContent(
                    modifier = Modifier.fillMaxSize().padding(paddingValues),
                )

            UiState.Loading ->
                LoadingContent(
                    modifier = Modifier.fillMaxSize().padding(paddingValues),
                )

            is UiState.Success ->
                if (itemsState.value.isEmpty()) {
                    EmptyContent(
                        modifier = Modifier.fillMaxSize().padding(paddingValues),
                    )
                } else {
                    SuccessContent(
                        items = itemsState.value,
                        onItemClick = onItemClick,
                        modifier = Modifier.padding(paddingValues),
                    )
                }

            is UiState.Failed ->
                ErrorContent(
                    error = itemsState.error.asString(),
                    onRetry = onRetry,
                    modifier = Modifier.fillMaxSize().padding(paddingValues),
                )
        }
    }
}

@Composable
private fun UninitializedContent(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center,
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            XText(
                text = "SAMPLE",
                fontSize = 52.sp,
                fontWeight = FontWeight.Black,
                color = GhostGray,
                letterSpacing = 14.sp,
            )
            XText(
                text = "Collection",
                style = MaterialTheme.typography.bodyLarge,
                color = TextMutedGray,
            )
        }
    }
}

@Composable
private fun LoadingContent(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center,
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            XCircularProgressIndicator()
            XText(
                text = "Loading collection",
                style = MaterialTheme.typography.bodyMedium,
                color = TextMutedGray,
                letterSpacing = 1.sp,
            )
        }
    }
}

@Composable
private fun SuccessContent(
    items: List<SampleItem>,
    onItemClick: (SampleItem) -> Unit,
    modifier: Modifier = Modifier,
) {
    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        item { CollectionHeader(count = items.size) }
        itemsIndexed(items) { index, item ->
            SampleCard(
                item = item,
                index = index,
                onClick = { onItemClick(item) },
            )
        }
        item { Spacer(Modifier.height(16.dp)) }
    }
}

@Composable
private fun CollectionHeader(count: Int) {
    Column(
        modifier =
            Modifier
                .fillMaxWidth()
                .padding(horizontal = 4.dp)
                .padding(top = 8.dp, bottom = 4.dp),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Bottom,
        ) {
            Column {
                XText(
                    text = "COLLECTION",
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextMutedGray,
                    letterSpacing = 3.sp,
                )
                XText(
                    text = "$count Items",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.ExtraBold,
                    color = TitleDark,
                )
            }
        }
        Spacer(Modifier.height(12.dp))
        Box(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .height(1.dp)
                    .background(GhostGray),
        )
        Spacer(Modifier.height(4.dp))
    }
}

@Composable
private fun EmptyContent(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center,
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            XText(
                text = "—",
                fontSize = 64.sp,
                fontWeight = FontWeight.Thin,
                color = GhostGray,
            )
            XText(
                text = "Nothing here",
                style = MaterialTheme.typography.titleMedium,
                color = TextMutedGray,
            )
            XText(
                text = "Items will appear here when available",
                style = MaterialTheme.typography.bodySmall,
                color = GhostGray,
                textAlign = TextAlign.Center,
            )
        }
    }
}

@Composable
private fun ErrorContent(
    error: String,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier.padding(24.dp),
        contentAlignment = Alignment.Center,
    ) {
        XCard(
            modifier = Modifier.fillMaxWidth(),
            elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
        ) {
            Column {
                // Top crimson accent strip
                Box(
                    modifier =
                        Modifier
                            .fillMaxWidth()
                            .height(3.dp)
                            .background(MaterialTheme.colorScheme.primary),
                )
                Column(
                    modifier = Modifier.padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    XText(
                        text = "Something went wrong",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = TitleDark,
                        textAlign = TextAlign.Center,
                    )
                    XText(
                        text = error,
                        style = MaterialTheme.typography.bodySmall,
                        color = TextMutedGray,
                        textAlign = TextAlign.Center,
                    )
                    Spacer(Modifier.height(4.dp))
                    XButton(onClick = onRetry) {
                        XText(
                            text = "Try Again",
                            style = MaterialTheme.typography.labelLarge,
                            fontWeight = FontWeight.SemiBold,
                        )
                    }
                }
            }
        }
    }
}
