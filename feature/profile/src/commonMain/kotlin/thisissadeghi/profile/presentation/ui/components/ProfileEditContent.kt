package thisissadeghi.profile.presentation.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import thisissadeghi.designsystem.XTheme
import thisissadeghi.profile.presentation.ProfileUiModel

@Composable
fun ProfileEditContent(
    uiModel: ProfileUiModel,
    onNameChange: (String) -> Unit,
    onEmailChange: (String) -> Unit,
    onChangePhotoClick: () -> Unit,
    onPrivacyClick: () -> Unit,
    onNotificationsClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier =
            modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(start = 24.dp, end = 24.dp, bottom = 128.dp),
    ) {
        ProfileEditAvatarSection(
            initials = uiModel.initials,
            onChangePhotoClick = onChangePhotoClick,
        )
        ProfileInputForm(
            name = uiModel.nameInput,
            email = uiModel.emailInput,
            onNameChange = onNameChange,
            onEmailChange = onEmailChange,
        )
        ProfilePrivacyCard(
            onPrivacyClick = onPrivacyClick,
            onNotificationsClick = onNotificationsClick,
        )
    }
}

@Preview
@Composable
private fun ProfileEditContentPreview() {
    XTheme {
        ProfileEditContent(
            uiModel =
                ProfileUiModel(
                    nameInput = "Ali Sadeghi",
                    emailInput = "ali@example.com",
                ),
            onNameChange = {},
            onEmailChange = {},
            onChangePhotoClick = {},
            onPrivacyClick = {},
            onNotificationsClick = {},
        )
    }
}
