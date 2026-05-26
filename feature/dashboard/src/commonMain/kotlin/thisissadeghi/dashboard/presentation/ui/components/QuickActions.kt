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
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kmpilot.feature.dashboard.generated.resources.Res
import kmpilot.feature.dashboard.generated.resources.add_circle
import kmpilot.feature.dashboard.generated.resources.download
import kmpilot.feature.dashboard.generated.resources.payments
import kmpilot.feature.dashboard.generated.resources.send
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.painterResource
import thisissadeghi.designsystem.XIcon
import thisissadeghi.designsystem.XText
import thisissadeghi.designsystem.XTheme

@Composable
internal fun QuickActions(
    onSendClick: () -> Unit,
    onReceiveClick: () -> Unit,
    onPayClick: () -> Unit,
    onTopUpClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        QuickActionButton(Res.drawable.send, "Send", onSendClick, Modifier.weight(1f))
        QuickActionButton(Res.drawable.download, "Receive", onReceiveClick, Modifier.weight(1f))
        QuickActionButton(Res.drawable.payments, "Pay", onPayClick, Modifier.weight(1f))
        QuickActionButton(Res.drawable.add_circle, "Top Up", onTopUpClick, Modifier.weight(1f))
    }
}

@Composable
private fun QuickActionButton(
    icon: DrawableResource,
    label: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier,
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
                    .clickable(onClick = onClick),
            contentAlignment = Alignment.Center,
        ) {
            XIcon(
                painter = painterResource(icon),
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(24.dp),
            )
        }
        XText(
            label,
            fontSize = 12.sp,
            fontWeight = FontWeight.Medium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
}

@Preview
@Composable
private fun QuickActionsPreview() {
    XTheme {
        QuickActions(
            onSendClick = {},
            onReceiveClick = {},
            onPayClick = {},
            onTopUpClick = {},
        )
    }
}
