package thisissadeghi.swap.presentation.ui.motion

import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import thisissadeghi.designsystem.motion.XMotion
import thisissadeghi.designsystem.motion.rememberReducedMotion

@Composable
fun shimmerBrush(): Brush {
    if (rememberReducedMotion()) {
        // Reduced motion: static gradient, no animation — jump to end state
        return Brush.linearGradient(
            colors =
                listOf(
                    MaterialTheme.colorScheme.onSurface,
                    MaterialTheme.colorScheme.primary,
                    MaterialTheme.colorScheme.onSurface,
                ),
        )
    }
    val transition = rememberInfiniteTransition(label = "shimmer")
    val offset by transition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec =
            infiniteRepeatable(
                animation = tween(durationMillis = XMotion.SHIMMER, easing = XMotion.Linear),
                repeatMode = RepeatMode.Restart,
            ),
        label = "shimmerOffset",
    )
    val colors =
        listOf(
            MaterialTheme.colorScheme.onSurface,
            MaterialTheme.colorScheme.primary,
            MaterialTheme.colorScheme.onSurface,
        )
    return Brush.linearGradient(
        colors = colors,
        start = Offset(offset * 1000f - 500f, 0f),
        end = Offset(offset * 1000f + 500f, 0f),
    )
}

@Composable
fun syncRotation(): Float {
    if (rememberReducedMotion()) return 0f
    val transition = rememberInfiniteTransition(label = "sync")
    val rotation by transition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec =
            infiniteRepeatable(
                // 4s — inventory-specified, overrides default XMotion.SHIMMER (2000ms)
                animation = tween(durationMillis = 4000, easing = XMotion.Linear),
                repeatMode = RepeatMode.Restart,
            ),
        label = "syncRotation",
    )
    return rotation
}
