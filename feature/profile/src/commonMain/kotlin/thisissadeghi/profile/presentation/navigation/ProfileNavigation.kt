package thisissadeghi.profile.presentation.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import kotlinx.serialization.Serializable
import org.koin.compose.viewmodel.koinViewModel
import thisissadeghi.profile.presentation.ui.ProfileEditScreen
import thisissadeghi.profile.presentation.ui.ProfileScreen

@Serializable
data object ProfileRoute

@Serializable
data object ProfileEditRoute

fun NavGraphBuilder.profile(
    navController: NavController,
    onBackClick: () -> Unit,
) {
    composable<ProfileRoute> {
        ProfileScreen(
            viewModel = koinViewModel(),
            onBackClick = onBackClick,
            onEditClick = { navController.navigate(ProfileEditRoute) },
        )
    }
    composable<ProfileEditRoute> {
        ProfileEditScreen(
            viewModel = koinViewModel(),
            onBackClick = { navController.popBackStack() },
        )
    }
}
