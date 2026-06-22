package thisissadeghi.profile.presentation

import thisissadeghi.common.UiState
import thisissadeghi.profile.data.model.ProfileData

data class ProfileUiModel(
    val profileState: UiState<ProfileData> = UiState.Uninitialized,
    val nameInput: String = "",
    val emailInput: String = "",
    val biometricEnabled: Boolean = false,
) {
    val name: String get() = (profileState as? UiState.Success)?.value?.name ?: ""
    val email: String get() = (profileState as? UiState.Success)?.value?.email ?: ""
    val memberTier: String get() = (profileState as? UiState.Success)?.value?.memberTier ?: ""
    val initials: String
        get() =
            name
                .split(" ")
                .take(2)
                .mapNotNull { it.firstOrNull()?.uppercaseChar()?.toString() }
                .joinToString("")
}
