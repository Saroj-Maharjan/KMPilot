package thisissadeghi.profile.presentation.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import kmpilot.feature.profile.generated.resources.Res
import kmpilot.feature.profile.generated.resources.notifications
import kmpilot.feature.profile.generated.resources.profile_notifications_subtitle
import kmpilot.feature.profile.generated.resources.profile_notifications_title
import kmpilot.feature.profile.generated.resources.profile_privacy_settings_subtitle
import kmpilot.feature.profile.generated.resources.profile_privacy_settings_title
import kmpilot.feature.profile.generated.resources.security
import thisissadeghi.designsystem.XHorizontalDivider
import thisissadeghi.designsystem.XTheme

@Composable
fun ProfilePrivacyCard(
    onPrivacyClick: () -> Unit,
    onNotificationsClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier.padding(top = 40.dp)) {
        Column(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.surface, RoundedCornerShape(24.dp))
                    .border(1.dp, MaterialTheme.colorScheme.outlineVariant, RoundedCornerShape(24.dp))
                    .padding(24.dp),
        ) {
            PrivacyRowItem(
                iconRes = Res.drawable.security,
                iconContainerColor = MaterialTheme.colorScheme.primaryContainer,
                iconTint = MaterialTheme.colorScheme.primary,
                titleRes = Res.string.profile_privacy_settings_title,
                subtitleRes = Res.string.profile_privacy_settings_subtitle,
                onClick = onPrivacyClick,
            )
            XHorizontalDivider(
                color = MaterialTheme.colorScheme.outlineVariant,
                modifier = Modifier.padding(vertical = 16.dp),
            )
            PrivacyRowItem(
                iconRes = Res.drawable.notifications,
                iconContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                iconTint = MaterialTheme.colorScheme.onSurfaceVariant,
                titleRes = Res.string.profile_notifications_title,
                subtitleRes = Res.string.profile_notifications_subtitle,
                onClick = onNotificationsClick,
            )
        }
    }
}

@Preview
@Composable
private fun ProfilePrivacyCardPreview() {
    XTheme {
        ProfilePrivacyCard(
            onPrivacyClick = {},
            onNotificationsClick = {},
        )
    }
}
