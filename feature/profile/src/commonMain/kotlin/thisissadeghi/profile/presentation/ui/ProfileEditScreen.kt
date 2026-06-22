package thisissadeghi.profile.presentation.ui

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.exclude
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.ime
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import kmpilot.feature.profile.generated.resources.Res
import kmpilot.feature.profile.generated.resources.check
import kmpilot.feature.profile.generated.resources.profile_edit_save_action
import kmpilot.feature.profile.generated.resources.profile_edit_save_changes_cta
import kmpilot.feature.profile.generated.resources.profile_edit_title
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import thisissadeghi.designsystem.DesignSystemResources
import thisissadeghi.designsystem.XButton
import thisissadeghi.designsystem.XButtonDefaults
import thisissadeghi.designsystem.XIcon
import thisissadeghi.designsystem.XIconButton
import thisissadeghi.designsystem.XScreen
import thisissadeghi.designsystem.XText
import thisissadeghi.designsystem.XTextButton
import thisissadeghi.designsystem.XTheme
import thisissadeghi.designsystem.toolbar.XTopAppBar
import thisissadeghi.profile.presentation.ProfileUiModel
import thisissadeghi.profile.presentation.ProfileViewModel
import thisissadeghi.profile.presentation.ui.components.BottomCtaContainer
import thisissadeghi.profile.presentation.ui.components.ProfileEditContent

@Composable
fun ProfileEditScreen(
    viewModel: ProfileViewModel,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val uiModel by viewModel.uiModel.collectAsStateWithLifecycle()
    ProfileEditScreenRoot(
        uiModel = uiModel,
        onBackClick = onBackClick,
        onSaveClick = viewModel::saveProfile,
        onNameChange = viewModel::onNameChange,
        onEmailChange = viewModel::onEmailChange,
        onChangePhotoClick = viewModel::onChangePhotoClick,
        onPrivacyClick = {},
        onNotificationsClick = {},
        modifier = modifier,
    )
}

@Composable
fun ProfileEditScreenRoot(
    uiModel: ProfileUiModel,
    onBackClick: () -> Unit,
    onSaveClick: () -> Unit,
    onNameChange: (String) -> Unit,
    onEmailChange: (String) -> Unit,
    onChangePhotoClick: () -> Unit,
    onPrivacyClick: () -> Unit,
    onNotificationsClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    XScreen(
        modifier = modifier,
        topBar = {
            XTopAppBar(
                title = { XText(stringResource(Res.string.profile_edit_title)) },
                navigationIcon = {
                    XIconButton(
                        onClick = onBackClick,
                        colors =
                            ButtonDefaults.buttonColors(
                                containerColor = Color.Transparent,
                                contentColor = MaterialTheme.colorScheme.primary,
                            ),
                    ) {
                        XIcon(
                            painter = painterResource(DesignSystemResources.drawable.arrow_back),
                            contentDescription = stringResource(DesignSystemResources.string.cd_back),
                        )
                    }
                },
                actions = {
                    XTextButton(
                        onClick = onSaveClick,
                        colors =
                            ButtonDefaults.textButtonColors(
                                contentColor = MaterialTheme.colorScheme.primary,
                            ),
                        contentPadding = PaddingValues(horizontal = 8.dp),
                    ) {
                        XText(
                            text = stringResource(Res.string.profile_edit_save_action),
                            style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold),
                        )
                    }
                },
                backgroundColor = MaterialTheme.colorScheme.background,
            )
        },
        bottomBar = {
            BottomCtaContainer(
                modifier =
                    Modifier.windowInsetsPadding(
                        WindowInsets.navigationBars.exclude(WindowInsets.ime),
                    ),
            ) {
                XButton(
                    onClick = onSaveClick,
                    modifier =
                        Modifier
                            .fillMaxWidth()
                            .height(56.dp),
                    shape = RoundedCornerShape(24.dp),
                ) {
                    XIcon(
                        painter = painterResource(Res.drawable.check),
                        contentDescription = null,
                        modifier = Modifier.size(XButtonDefaults.IconSize),
                    )
                    Spacer(Modifier.size(XButtonDefaults.IconSpacing))
                    XText(
                        text = stringResource(Res.string.profile_edit_save_changes_cta),
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onPrimary,
                    )
                }
            }
        },
    ) {
        ProfileEditContent(
            uiModel = uiModel,
            onNameChange = onNameChange,
            onEmailChange = onEmailChange,
            onChangePhotoClick = onChangePhotoClick,
            onPrivacyClick = onPrivacyClick,
            onNotificationsClick = onNotificationsClick,
        )
    }
}

@Preview
@Composable
private fun ProfileEditScreenRootPreview() {
    XTheme {
        ProfileEditScreenRoot(
            uiModel =
                ProfileUiModel(
                    nameInput = "Ali Sadeghi",
                    emailInput = "ali@example.com",
                ),
            onBackClick = {},
            onSaveClick = {},
            onNameChange = {},
            onEmailChange = {},
            onChangePhotoClick = {},
            onPrivacyClick = {},
            onNotificationsClick = {},
        )
    }
}
