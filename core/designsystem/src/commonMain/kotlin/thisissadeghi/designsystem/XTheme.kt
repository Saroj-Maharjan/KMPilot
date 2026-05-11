package thisissadeghi.designsystem

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

object XTheme {
    object Icons

    object Colors {
        // Semantic status colors — no M3 role equivalent
        val Success = Color(0xFF4ADE80) // Income, savings progress, on-track budgets
        val Danger = Color(0xFFFF6B6B) // Over-budget, expenses, overdue bills
        val Bitcoin = Color(0xFFF7931A) // Bitcoin brand orange for coin icon bg
    }
}

@Composable
fun XTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        content = content,
        colorScheme = XDarkColors,
        shapes = Shapes,
        typography = MaterialTheme.typography,
    )
}

private val Shapes =
    Shapes(
        small = RoundedCornerShape(6.dp),
        medium = RoundedCornerShape(12.dp),
        large = RoundedCornerShape(20.dp),
    )

internal val XLightColors =
    lightColorScheme(
        primary = Color(0xFF6B5000),
        onPrimary = Color(0xFFFFFFFF),
        primaryContainer = Color(0xFFFDEDB5),
        onPrimaryContainer = Color(0xFF221A00),
        background = Color(0xFFFFFBF0),
        surface = Color(0xFFFFFDF6),
        onBackground = Color(0xFF1E1B10),
        onSurface = Color(0xFF1E1B10),
        onSurfaceVariant = Color(0xFF4E4730),
        surfaceVariant = Color(0xFFEAE0C5),
        outline = Color(0xFF7A7250),
        outlineVariant = Color(0xFFCDC1A0),
        error = Color(0xFFB3261E),
        onError = Color(0xFFFFFFFF),
        errorContainer = Color(0xFFF9DEDC),
        onErrorContainer = Color(0xFF410E0B),
    )

internal val XDarkColors =
    darkColorScheme(
        primary = Color(0xFFF5D76E),
        onPrimary = Color(0xFF2C1900),
        primaryContainer = Color(0xFF4A3200),
        onPrimaryContainer = Color(0xFFFFF0C0),
        background = Color(0xFF0F0D09),
        surface = Color(0xFF1C1910),
        onBackground = Color(0xFFEDE8D5),
        onSurface = Color(0xFFEDE8D5),
        onSurfaceVariant = Color(0xFFC4BA94),
        surfaceVariant = Color(0xFF302B1C),
        outline = Color(0xFF726A48),
        outlineVariant = Color(0xFF3F3822),
        error = Color(0xFFFFB4AB),
        onError = Color(0xFF690005),
        errorContainer = Color(0xFF93000A),
        onErrorContainer = Color(0xFFFFDAD6),
    )

/*
@Composable
private fun XTypography() =
    Typography(
        defaultFontFamily =
            FontFamily(
                Font(Res.font.outfit_medium, FontWeight.Medium),
                Font(Res.font.outfit_regular, FontWeight.Normal),
                Font(Res.font.outfit_bold, FontWeight.Bold),
            ),
    )*/
