package thisissadeghi.kmpilot

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.rememberNavController
import thisissadeghi.dashboard.presentation.navigation.DashboardRoute
import thisissadeghi.dashboard.presentation.navigation.dashboard
import thisissadeghi.designsystem.XNavHost
import thisissadeghi.receive.presentation.navigation.ReceiveRoute
import thisissadeghi.receive.presentation.navigation.receive
import thisissadeghi.send.presentation.navigation.SendRoute
import thisissadeghi.send.presentation.navigation.send

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
                if (actionId == "qa_send") {
                    navController.navigate(SendRoute)
                }
                if (actionId == "qa_receive") {
                    navController.navigate(ReceiveRoute)
                }
                // Handle action — navigation or action handling to be wired later
            },
            onBackToDashboard = {
                navController.popBackStack(DashboardRoute, inclusive = false)
            },
        )
        send(onBackClick = { navController.popBackStack() })
        receive(onBackClick = { navController.popBackStack() })
    }
}
