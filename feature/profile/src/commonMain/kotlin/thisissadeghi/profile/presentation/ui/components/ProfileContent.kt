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
import thisissadeghi.common.UiState
import thisissadeghi.designsystem.XTheme
import thisissadeghi.profile.data.model.ProfileData
import thisissadeghi.profile.presentation.ProfileUiModel

@Composable
fun ProfileContent(
    uiModel: ProfileUiModel,
    onBiometricToggle: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier =
            modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(start = 24.dp, end = 24.dp, bottom = 128.dp),
    ) {
        ProfileAvatarSection(
            name = uiModel.name,
            email = uiModel.email,
        )
        AccountDetailCard(
            name = uiModel.name,
            email = uiModel.email,
            tier = uiModel.memberTier,
        )
        PreferencesCard(
            biometricEnabled = uiModel.biometricEnabled,
            onBiometricToggle = onBiometricToggle,
        )
    }
}

@Preview
@Composable
private fun ProfileContentPreview() {
    XTheme {
        ProfileContent(
            uiModel =
                ProfileUiModel(
                    profileState =
                        UiState.Success(
                            ProfileData(
                                name = "Ali Sadeghi",
                                email = "alisadeghi.dev@gmail.com",
                                memberTier = "Gold Private Banking",
                                biometricEnabled = true,
                            ),
                        ),
                    biometricEnabled = true,
                ),
            onBiometricToggle = {},
        )
    }
}
