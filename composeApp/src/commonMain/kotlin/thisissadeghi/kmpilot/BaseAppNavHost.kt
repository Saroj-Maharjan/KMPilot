package thisissadeghi.kmpilot

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.rememberNavController
import thisissadeghi.designsystem.XNavHost
import thisissadeghi.sample.presentation.navigation.SampleRoute
import thisissadeghi.sample.presentation.navigation.sample

/**
 * Main app navigation host
 */
@Composable
fun BaseAppNavHost(modifier: Modifier) {
    val navController = rememberNavController()

    XNavHost(
        modifier = modifier,
        navController = navController,
        startDestination = SampleRoute,
    ) {
        sample(
            onActionClick = { actionId ->
                // Handle action — navigation or action handling to be wired later
            },
        )
    }
}
