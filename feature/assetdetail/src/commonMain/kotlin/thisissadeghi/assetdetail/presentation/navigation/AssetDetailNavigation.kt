package thisissadeghi.assetdetail.presentation.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import kotlinx.serialization.Serializable
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.parameter.parametersOf
import thisissadeghi.assetdetail.presentation.AssetDetailViewModel
import thisissadeghi.assetdetail.presentation.ui.AssetDetailScreen

@Serializable
data class AssetDetailRoute(
    val assetId: String,
)

fun NavGraphBuilder.assetdetail(onBackClick: () -> Unit) {
    composable<AssetDetailRoute> { backStackEntry ->
        val route: AssetDetailRoute = backStackEntry.toRoute()
        val viewModel =
            koinViewModel<AssetDetailViewModel>(
                parameters = { parametersOf(route.assetId) },
            )
        AssetDetailScreen(viewModel = viewModel, onBackClick = onBackClick)
    }
}
