package thisissadeghi.swap.presentation.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import kmpilot.feature.swap.generated.resources.Res
import kmpilot.feature.swap.generated.resources.cd_swap_direction
import kmpilot.feature.swap.generated.resources.swap_vert
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import thisissadeghi.designsystem.XIcon
import thisissadeghi.designsystem.XIconButton
import thisissadeghi.designsystem.XTheme

@Composable
fun SwapDirectionToggle(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier.fillMaxWidth().height(16.dp),
        contentAlignment = Alignment.Center,
    ) {
        XIconButton(
            onClick = onClick,
            modifier =
                Modifier
                    .size(48.dp)
                    .shadow(elevation = 8.dp, shape = CircleShape),
            colors =
                ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant,
                    contentColor = MaterialTheme.colorScheme.primary,
                ),
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
        ) {
            XIcon(
                painter = painterResource(Res.drawable.swap_vert),
                contentDescription = stringResource(Res.string.cd_swap_direction),
                modifier = Modifier.size(24.dp),
            )
        }
    }
}

@Preview
@Composable
private fun SwapDirectionTogglePreview() {
    XTheme {
        SwapDirectionToggle(onClick = {})
    }
}
