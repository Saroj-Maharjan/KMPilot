package thisissadeghi.swap.presentation.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import kotlinx.serialization.Serializable
import org.koin.compose.viewmodel.koinViewModel
import thisissadeghi.swap.presentation.ui.SwapScreen

@Serializable
object SwapRoute

fun NavGraphBuilder.swap(
    onBackClick: () -> Unit,
    onSwapComplete: () -> Unit,
) {
    composable<SwapRoute> {
        SwapScreen(
            viewModel = koinViewModel(),
            onBackClick = onBackClick,
            onSwapComplete = onSwapComplete,
        )
    }
}
