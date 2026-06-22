package thisissadeghi.profile.presentation.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import kmpilot.feature.profile.generated.resources.Res
import kmpilot.feature.profile.generated.resources.profile_notifications_subtitle
import kmpilot.feature.profile.generated.resources.profile_notifications_title
import kmpilot.feature.profile.generated.resources.security
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import thisissadeghi.designsystem.DesignSystemResources
import thisissadeghi.designsystem.XIcon
import thisissadeghi.designsystem.XText
import thisissadeghi.designsystem.XTheme

@Composable
fun PrivacyRowItem(
    iconRes: DrawableResource,
    iconContainerColor: Color,
    iconTint: Color,
    titleRes: StringResource,
    subtitleRes: StringResource,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier =
            modifier
                .fillMaxWidth()
                .clickable(onClick = onClick),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Box(
                modifier =
                    Modifier
                        .size(40.dp)
                        .background(iconContainerColor, RoundedCornerShape(16.dp)),
                contentAlignment = Alignment.Center,
            ) {
                XIcon(
                    painter = painterResource(iconRes),
                    tint = iconTint,
                )
            }
            Column {
                XText(
                    text = stringResource(titleRes),
                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.onSurface,
                )
                XText(
                    text = stringResource(subtitleRes),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }
        XIcon(
            painter = painterResource(DesignSystemResources.drawable.chevron_right),
            tint = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
}

@Preview
@Composable
private fun PrivacyRowItemPreview() {
    XTheme {
        PrivacyRowItem(
            iconRes = Res.drawable.security,
            iconContainerColor = MaterialTheme.colorScheme.primaryContainer,
            iconTint = MaterialTheme.colorScheme.primary,
            titleRes = Res.string.profile_notifications_title,
            subtitleRes = Res.string.profile_notifications_subtitle,
            onClick = {},
        )
    }
}
