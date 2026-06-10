package thisissadeghi.dashboard.presentation.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import kotlinx.serialization.Serializable
import org.koin.compose.viewmodel.koinViewModel
import thisissadeghi.dashboard.presentation.ui.DashboardScreen

@Serializable
data object DashboardRoute

fun NavGraphBuilder.dashboard(
    onActionClick: (String) -> Unit,
    onBackToDashboard: () -> Unit,
    onAssetClick: (String) -> Unit,
) {
    composable<DashboardRoute> {
        DashboardScreen(
            viewModel = koinViewModel(),
            onActionClick = onActionClick,
            onBackToDashboard = onBackToDashboard,
            onAssetClick = onAssetClick,
        )
    }
}
