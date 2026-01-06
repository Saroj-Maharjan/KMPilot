package thisissadeghi.sample.presentation.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import kotlinx.serialization.Serializable
import org.koin.compose.viewmodel.koinViewModel
import thisissadeghi.sample.presentation.ui.SampleScreen

/**
 * Navigation route for the sample feature.
 */
@Serializable
data object SampleRoute

/**
 * Navigation extension for adding sample feature to NavGraph.
 */
fun NavGraphBuilder.sample(onItemClick: (String) -> Unit) {
    composable<SampleRoute> {
        SampleScreen(
            viewModel = koinViewModel(),
            onItemClick = onItemClick,
        )
    }
}
