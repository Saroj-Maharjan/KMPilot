package thisissadeghi.sample.presentation.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import kotlinx.serialization.Serializable
import org.koin.compose.viewmodel.koinViewModel
import thisissadeghi.sample.presentation.ui.SampleScreen

@Serializable
data object SampleRoute

fun NavGraphBuilder.sample(
    onActionClick: (String) -> Unit,
    onBackToDashboard: () -> Unit,
) {
    composable<SampleRoute> {
        SampleScreen(
            viewModel = koinViewModel(),
            onActionClick = onActionClick,
            onBackToDashboard = onBackToDashboard,
        )
    }
}
