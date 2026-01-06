package thisissadeghi.designsystem

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

object XTheme {
    object Icons

    object Colors
}

@Composable
fun XTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        content = content,
        colorScheme = XColors,
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

private val XColors =
    lightColorScheme(
        background = XTheme.Colors.PaleLavender,
        surface = Color.White,
        primary = Color(0xFFB02418),
//        primaryVariant = Color(0xFFD61905),
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
