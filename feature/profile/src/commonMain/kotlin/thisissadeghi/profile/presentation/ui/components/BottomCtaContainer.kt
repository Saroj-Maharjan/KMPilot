package thisissadeghi.profile.presentation.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import thisissadeghi.designsystem.XTheme

@Composable
fun BottomCtaContainer(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit,
) {
    Box(
        modifier =
            modifier
                .fillMaxWidth()
                .background(
                    Brush.verticalGradient(
                        listOf(Color.Transparent, MaterialTheme.colorScheme.background),
                    ),
                ).padding(top = 48.dp, start = 24.dp, end = 24.dp, bottom = 24.dp),
        contentAlignment = Alignment.BottomCenter,
    ) {
        content()
    }
}

@Preview
@Composable
private fun BottomCtaContainerPreview() {
    XTheme {
        BottomCtaContainer {}
    }
}
