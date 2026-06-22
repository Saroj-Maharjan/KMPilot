package thisissadeghi.profile.presentation.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.em
import kmpilot.feature.profile.generated.resources.Res
import kmpilot.feature.profile.generated.resources.profile_biometric_security_label
import kmpilot.feature.profile.generated.resources.profile_section_preferences
import kmpilot.feature.profile.generated.resources.shield
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import thisissadeghi.designsystem.XIcon
import thisissadeghi.designsystem.XText
import thisissadeghi.designsystem.XTheme

@Composable
fun PreferencesCard(
    biometricEnabled: Boolean,
    onBiometricToggle: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier.padding(top = 16.dp)) {
        Column(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.surface, RoundedCornerShape(24.dp))
                    .border(1.dp, MaterialTheme.colorScheme.outlineVariant, RoundedCornerShape(24.dp))
                    .padding(20.dp),
        ) {
            XText(
                text = stringResource(Res.string.profile_section_preferences),
                style =
                    MaterialTheme.typography.labelMedium.copy(
                        fontWeight = FontWeight.SemiBold,
                        letterSpacing = 0.1f.em,
                    ),
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(bottom = 16.dp),
            )
            Row(
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    XIcon(
                        painter = painterResource(Res.drawable.shield),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                    XText(
                        text = stringResource(Res.string.profile_biometric_security_label),
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurface,
                    )
                }
                BiometricToggle(
                    checked = biometricEnabled,
                    onCheckedChange = onBiometricToggle,
                )
            }
        }
    }
}

@Composable
private fun BiometricToggle(
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier =
            modifier
                .size(width = 40.dp, height = 24.dp)
                .background(
                    if (checked) {
                        MaterialTheme.colorScheme.primary
                    } else {
                        MaterialTheme.colorScheme.surfaceVariant
                    },
                    CircleShape,
                ).clickable { onCheckedChange(!checked) },
    ) {
        Box(
            modifier =
                Modifier
                    .size(16.dp)
                    .offset(x = if (checked) 20.dp else 4.dp, y = 4.dp)
                    .background(
                        if (checked) {
                            MaterialTheme.colorScheme.onPrimary
                        } else {
                            MaterialTheme.colorScheme.onSurfaceVariant
                        },
                        CircleShape,
                    ),
        )
    }
}

@Preview
@Composable
private fun PreferencesCardPreview() {
    XTheme {
        PreferencesCard(
            biometricEnabled = true,
            onBiometricToggle = {},
        )
    }
}

@Preview
@Composable
private fun PreferencesCardPreviewDisabled() {
    XTheme {
        PreferencesCard(
            biometricEnabled = false,
            onBiometricToggle = {},
        )
    }
}
