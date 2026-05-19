package thisissadeghi.dashboard.presentation.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Payments
import androidx.compose.material.icons.filled.Receipt
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import thisissadeghi.dashboard.data.model.QuickAction
import thisissadeghi.designsystem.XIcon
import thisissadeghi.designsystem.XText

@Composable
internal fun QuickActionsSection(
    actions: List<QuickAction>,
    onActionClick: (String) -> Unit,
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        actions.forEach { action ->
            Column(
                modifier = Modifier.weight(1f),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                Box(
                    modifier =
                        Modifier
                            .size(56.dp)
                            .background(
                                MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f),
                                CircleShape,
                            ).border(1.dp, MaterialTheme.colorScheme.outlineVariant, CircleShape)
                            .clickable { onActionClick(action.id) },
                    contentAlignment = Alignment.Center,
                ) {
                    XIcon(
                        imageVector = quickActionIcon(action.iconName),
                        contentDescription = action.label,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(24.dp),
                    )
                }
                XText(
                    action.label,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }
    }
}

private fun quickActionIcon(iconName: String): ImageVector =
    when (iconName) {
        "send" -> Icons.AutoMirrored.Filled.Send
        "receive" -> Icons.Filled.Download
        "pay" -> Icons.Filled.Payments
        "topup" -> Icons.Filled.AddCircle
        else -> Icons.Filled.Receipt
    }
