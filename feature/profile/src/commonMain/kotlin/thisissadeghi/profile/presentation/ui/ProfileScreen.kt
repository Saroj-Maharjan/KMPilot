package thisissadeghi.profile.presentation.ui

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
import kmpilot.feature.profile.generated.resources.edit
import kmpilot.feature.profile.generated.resources.profile_edit_cta
import kmpilot.feature.profile.generated.resources.profile_error_message
import kmpilot.feature.profile.generated.resources.profile_error_title
import kmpilot.feature.profile.generated.resources.profile_title
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import thisissadeghi.common.ErrorModel
import thisissadeghi.common.UiState
import thisissadeghi.designsystem.DesignSystemResources
import thisissadeghi.designsystem.XButton
import thisissadeghi.designsystem.XButtonDefaults
import thisissadeghi.designsystem.XIcon
import thisissadeghi.designsystem.XIconButton
import thisissadeghi.designsystem.XScreen
import thisissadeghi.designsystem.XText
import thisissadeghi.designsystem.XTheme
import thisissadeghi.designsystem.app.AppErrorState
import thisissadeghi.designsystem.app.AppLoadingState
import thisissadeghi.designsystem.toolbar.XTopAppBar
import thisissadeghi.profile.data.model.ProfileData
import thisissadeghi.profile.presentation.ProfileUiModel
import thisissadeghi.profile.presentation.ProfileViewModel
import thisissadeghi.profile.presentation.ui.components.BottomCtaContainer
import thisissadeghi.profile.presentation.ui.components.ProfileContent

@Composable
fun ProfileScreen(
    viewModel: ProfileViewModel,
    onBackClick: () -> Unit,
    onEditClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val uiModel by viewModel.uiModel.collectAsStateWithLifecycle()
    ProfileScreenRoot(
        uiModel = uiModel,
        onBackClick = onBackClick,
        onEditClick = onEditClick,
        onRetry = viewModel::retry,
        onBiometricToggle = viewModel::onBiometricToggle,
        modifier = modifier,
    )
}

@Composable
fun ProfileScreenRoot(
    uiModel: ProfileUiModel,
    onBackClick: () -> Unit,
    onEditClick: () -> Unit,
    onRetry: () -> Unit,
    onBiometricToggle: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
) {
    XScreen(
        modifier = modifier,
        topBar = {
            XTopAppBar(
                title = { XText(stringResource(Res.string.profile_title)) },
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
                backgroundColor = MaterialTheme.colorScheme.background,
            )
        },
        bottomBar = {
            if (uiModel.profileState is UiState.Success) {
                BottomCtaContainer(
                    modifier =
                        Modifier.windowInsetsPadding(
                            WindowInsets.navigationBars.exclude(WindowInsets.ime),
                        ),
                ) {
                    XButton(
                        onClick = onEditClick,
                        modifier =
                            Modifier
                                .fillMaxWidth()
                                .height(56.dp),
                        shape = RoundedCornerShape(24.dp),
                    ) {
                        XIcon(
                            painter = painterResource(Res.drawable.edit),
                            contentDescription = null,
                            modifier = Modifier.size(XButtonDefaults.IconSize),
                        )
                        Spacer(Modifier.size(XButtonDefaults.IconSpacing))
                        XText(
                            text = stringResource(Res.string.profile_edit_cta),
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.onPrimary,
                        )
                    }
                }
            }
        },
    ) {
        when (uiModel.profileState) {
            UiState.Uninitialized, UiState.Loading -> AppLoadingState()
            is UiState.Failed ->
                AppErrorState(
                    title = stringResource(Res.string.profile_error_title),
                    message = stringResource(Res.string.profile_error_message),
                    onRetry = onRetry,
                )
            is UiState.Success ->
                ProfileContent(
                    uiModel = uiModel,
                    onBiometricToggle = onBiometricToggle,
                )
        }
    }
}

@Preview
@Composable
private fun ProfileScreenRootPreviewSuccess() {
    XTheme {
        ProfileScreenRoot(
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
            onBackClick = {},
            onEditClick = {},
            onRetry = {},
            onBiometricToggle = {},
        )
    }
}

@Preview
@Composable
private fun ProfileScreenRootPreviewLoading() {
    XTheme {
        ProfileScreenRoot(
            uiModel = ProfileUiModel(profileState = UiState.Loading),
            onBackClick = {},
            onEditClick = {},
            onRetry = {},
            onBiometricToggle = {},
        )
    }
}

@Preview
@Composable
private fun ProfileScreenRootPreviewFailed() {
    XTheme {
        ProfileScreenRoot(
            uiModel =
                ProfileUiModel(
                    profileState = UiState.Failed(ErrorModel.Message("Network error")),
                ),
            onBackClick = {},
            onEditClick = {},
            onRetry = {},
            onBiometricToggle = {},
        )
    }
}
