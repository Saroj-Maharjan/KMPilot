package thisissadeghi.receive.presentation.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kmpilot.feature.receive.generated.resources.Res
import kmpilot.feature.receive.generated.resources.warning_message
import kmpilot.feature.receive.generated.resources.warning_title
import org.jetbrains.compose.resources.stringResource
import thisissadeghi.designsystem.XIcon
import thisissadeghi.designsystem.XText

@Composable
fun NetworkWarningBanner(modifier: Modifier = Modifier) {
    Row(
        modifier =
            modifier
                .fillMaxWidth()
                .background(
                    MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.2f),
                    RoundedCornerShape(24.dp),
                ).border(
                    1.dp,
                    MaterialTheme.colorScheme.error.copy(alpha = 0.4f),
                    RoundedCornerShape(24.dp),
                ).padding(16.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        XIcon(
            imageVector = Icons.Default.Warning,
            modifier = Modifier.size(24.dp),
            tint = MaterialTheme.colorScheme.error,
        )
        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
            XText(
                text = stringResource(Res.string.warning_title),
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface,
            )
            XText(
                text = stringResource(Res.string.warning_message),
                fontSize = 12.sp,
                lineHeight = (12 * 1.625).sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}
