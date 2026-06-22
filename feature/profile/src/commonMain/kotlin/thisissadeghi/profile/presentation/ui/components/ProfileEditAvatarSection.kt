package thisissadeghi.profile.presentation.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import kmpilot.feature.profile.generated.resources.Res
import kmpilot.feature.profile.generated.resources.cancel_fill
import kmpilot.feature.profile.generated.resources.profile_edit_change_photo
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import thisissadeghi.designsystem.XIcon
import thisissadeghi.designsystem.XText
import thisissadeghi.designsystem.XTextButton
import thisissadeghi.designsystem.XTheme

@Composable
fun ProfileEditAvatarSection(
    initials: String,
    onChangePhotoClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier =
            modifier
                .fillMaxWidth()
                .padding(vertical = 32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Box {
            // Avatar circle
            Box(
                modifier =
                    Modifier
                        .size(80.dp)
                        .background(MaterialTheme.colorScheme.surfaceVariant, CircleShape)
                        .border(1.dp, MaterialTheme.colorScheme.primary, CircleShape),
                contentAlignment = Alignment.Center,
            ) {
                XText(
                    text = initials,
                    style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.primary,
                )
            }
            // Camera badge (bottom-end overlay)
            Box(
                modifier =
                    Modifier
                        .align(Alignment.BottomEnd)
                        .size(28.dp)
                        .background(MaterialTheme.colorScheme.primaryContainer, CircleShape)
                        .border(1.dp, MaterialTheme.colorScheme.background, CircleShape),
                contentAlignment = Alignment.Center,
            ) {
                XIcon(
                    painter = painterResource(Res.drawable.cancel_fill),
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(14.dp),
                )
            }
        }
        XTextButton(
            onClick = onChangePhotoClick,
            colors =
                ButtonDefaults.textButtonColors(
                    contentColor = MaterialTheme.colorScheme.primary,
                ),
            modifier = Modifier.padding(top = 16.dp),
        ) {
            XText(stringResource(Res.string.profile_edit_change_photo))
        }
    }
}

@Preview
@Composable
private fun ProfileEditAvatarSectionPreview() {
    XTheme {
        ProfileEditAvatarSection(
            initials = "AS",
            onChangePhotoClick = {},
        )
    }
}
