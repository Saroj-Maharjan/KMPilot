package thisissadeghi.send.presentation.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import org.koin.compose.viewmodel.koinViewModel
import thisissadeghi.send.presentation.ui.SendScreen

fun NavGraphBuilder.send(onBackClick: () -> Unit) {
    composable<SendRoute> {
        SendScreen(
            viewModel = koinViewModel(),
            onBackClick = onBackClick,
        )
    }
}
