package thisissadeghi.receive.presentation.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import org.koin.compose.viewmodel.koinViewModel
import thisissadeghi.receive.presentation.ui.ReceiveScreen

fun NavGraphBuilder.receive(onBackClick: () -> Unit) {
    composable<ReceiveRoute> {
        ReceiveScreen(
            viewModel = koinViewModel(),
            onBackClick = onBackClick,
        )
    }
}
