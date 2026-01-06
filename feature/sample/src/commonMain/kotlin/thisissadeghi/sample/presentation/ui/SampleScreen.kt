package thisissadeghi.sample.presentation.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import thisissadeghi.common.UiState
import thisissadeghi.common.asString
import thisissadeghi.sample.data.model.SampleItem
import thisissadeghi.sample.presentation.SampleUiModel
import thisissadeghi.sample.presentation.SampleViewModel
import thisissadeghi.sample.presentation.ui.components.SampleCard

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
 * Sample screen root - ViewModel-independent for testing.
 * Demonstrates the 4-state UI pattern: Uninitialized, Loading, Success, Failed.
 */
@Composable
fun SampleScreenRoot(
    uiState: SampleUiModel,
    onItemClick: (SampleItem) -> Unit,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Scaffold(modifier = modifier.fillMaxSize()) { paddingValues ->
        when (val itemsState = uiState.itemsState) {
            UiState.Uninitialized -> {
                // Initial empty state
                Box(
                    modifier =
                        Modifier
                            .fillMaxSize()
                            .padding(paddingValues),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(
                        text = "Welcome to Sample Feature",
                        style = MaterialTheme.typography.headlineMedium,
                    )
                }
            }

            UiState.Loading -> {
                // Loading state
                Box(
                    modifier =
                        Modifier
                            .fillMaxSize()
                            .padding(paddingValues),
                    contentAlignment = Alignment.Center,
                ) {
                    CircularProgressIndicator()
                }
            }

            is UiState.Success -> {
                // Success state - show list
                if (itemsState.value.isEmpty()) {
                    Box(
                        modifier =
                            Modifier
                                .fillMaxSize()
                                .padding(paddingValues),
                        contentAlignment = Alignment.Center,
                    ) {
                        Text(
                            text = "No items found",
                            style = MaterialTheme.typography.bodyLarge,
                        )
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier.padding(paddingValues),
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                    ) {
                        items(itemsState.value) { item ->
                            SampleCard(
                                item = item,
                                onClick = { onItemClick(item) },
                            )
                        }
                    }
                }
            }

            is UiState.Failed -> {
                // Error state
                Column(
                    modifier =
                        Modifier
                            .fillMaxSize()
                            .padding(paddingValues),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center,
                ) {
                    Text(
                        text = "Error: ${itemsState.error.asString()}",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.error,
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Button(onClick = onRetry) {
                        Text("Retry")
                    }
                }
            }
        }
    }
}
