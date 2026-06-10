package thisissadeghi.assetdetail.presentation.ui.motion

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.snap
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.ui.graphics.Color
import thisissadeghi.designsystem.XTheme
import thisissadeghi.designsystem.motion.XMotion
import thisissadeghi.designsystem.motion.rememberReducedMotion

@Composable
fun animatedPrice(targetPrice: Float): State<Float> =
    animateFloatAsState(
        targetValue = targetPrice,
        animationSpec =
            if (rememberReducedMotion()) {
                snap()
            } else {
                spring(dampingRatio = Spring.DampingRatioNoBouncy, stiffness = Spring.StiffnessLow)
            },
        label = "animatedPrice",
    )

@Composable
fun animatedBadgeColor(isPositive: Boolean): State<Color> =
    animateColorAsState(
        targetValue = if (isPositive) XTheme.Colors.Success else XTheme.Colors.Danger,
        animationSpec =
            if (rememberReducedMotion()) {
                snap()
            } else {
                tween(durationMillis = XMotion.VALUE, easing = XMotion.Standard)
            },
        label = "animatedBadgeColor",
    )
