package thisissadeghi.kmpilot

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.rememberNavController
import thisissadeghi.assetdetail.presentation.navigation.AssetDetailRoute
import thisissadeghi.assetdetail.presentation.navigation.assetdetail
import thisissadeghi.dashboard.presentation.navigation.DashboardRoute
import thisissadeghi.dashboard.presentation.navigation.dashboard
import thisissadeghi.designsystem.XNavHost
import thisissadeghi.profile.presentation.navigation.ProfileRoute
import thisissadeghi.profile.presentation.navigation.profile
import thisissadeghi.receive.presentation.navigation.ReceiveRoute
import thisissadeghi.receive.presentation.navigation.receive
import thisissadeghi.send.presentation.navigation.SendRoute
import thisissadeghi.send.presentation.navigation.send
import thisissadeghi.swap.presentation.navigation.SwapRoute
import thisissadeghi.swap.presentation.navigation.swap

/**
 * Main app navigation host
 */
@Composable
fun BaseAppNavHost(modifier: Modifier) {
    val navController = rememberNavController()

    XNavHost(
        modifier = modifier,
        navController = navController,
        startDestination = DashboardRoute,
    ) {
        dashboard(
            onActionClick = { actionId ->
                if (actionId == "send") {
                    navController.navigate(SendRoute)
                }
                if (actionId == "receive") {
                    navController.navigate(ReceiveRoute)
                }
                if (actionId == "swap") {
                    navController.navigate(SwapRoute)
                }
            },
            onBackToDashboard = {
                navController.popBackStack(DashboardRoute, inclusive = false)
            },
            onAssetClick = { assetId -> navController.navigate(AssetDetailRoute(assetId)) },
            onProfileClick = { navController.navigate(ProfileRoute) },
        )
        send(onBackClick = { navController.popBackStack() })
        receive(onBackClick = { navController.popBackStack() })
        assetdetail(onBackClick = { navController.popBackStack() })
        swap(
            onBackClick = { navController.popBackStack() },
            onSwapComplete = { navController.popBackStack(DashboardRoute, inclusive = false) },
        )
        profile(
            navController = navController,
            onBackClick = { navController.popBackStack() },
        )
    }
}
