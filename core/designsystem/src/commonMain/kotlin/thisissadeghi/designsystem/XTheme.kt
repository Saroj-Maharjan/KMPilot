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
    }
}

@Composable
fun XTheme(
    darkTheme: Boolean = true,
    content: @Composable () -> Unit,
) {
    MaterialTheme(
        content = content,
        colorScheme = if (darkTheme) XDarkColors else XLightColors,
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
        primary = Color(0xFF7B2FFF),
        onPrimary = Color(0xFFFFFFFF),
        primaryContainer = Color(0xFFEDE0FF),
        onPrimaryContainer = Color(0xFF21005D),
        background = Color(0xFFF8F5FF),
        surface = Color(0xFFFFFBFF),
        onBackground = Color(0xFF1C1B1F),
        onSurface = Color(0xFF1C1B1F),
        onSurfaceVariant = Color(0xFF49454E),
        surfaceVariant = Color(0xFFE7E0EC),
        outline = Color(0xFF7A757F),
        outlineVariant = Color(0xFFCAC4CF),
        error = Color(0xFFB3261E),
        onError = Color(0xFFFFFFFF),
        errorContainer = Color(0xFFF9DEDC),
        onErrorContainer = Color(0xFF410E0B),
    )

internal val XDarkColors =
    darkColorScheme(
        primary = Color(0xFF9D70FF), // Design primary accent
        onPrimary = Color(0xFF1A0054), // Text on primary buttons
        primaryContainer = Color(0xFF350070), // Tinted container
        onPrimaryContainer = Color(0xFFEDE0FF),
        background = Color(0xFF0D0919), // Deep dark indigo background
        surface = Color(0xFF181228), // Card / elevated surface
        onBackground = Color(0xFFE9E0FF), // Primary text (bright)
        onSurface = Color(0xFFE9E0FF), // Primary text on surface
        onSurfaceVariant = Color(0xFFC5BCE0), // Muted text
        surfaceVariant = Color(0xFF231A38), // Slightly elevated over surface
        outline = Color(0xFF4A3F6B), // Subtle border
        outlineVariant = Color(0xFF1E1A2E), // Divider / progress track
        error = Color(0xFFFFB4AB), // Error / coral accent
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
