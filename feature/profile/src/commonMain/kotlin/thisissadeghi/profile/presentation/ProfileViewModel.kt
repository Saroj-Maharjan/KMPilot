package thisissadeghi.profile.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import thisissadeghi.common.Either
import thisissadeghi.common.UiState
import thisissadeghi.common.setState
import thisissadeghi.profile.data.repository.ProfileRepository

class ProfileViewModel(
    private val repository: ProfileRepository,
) : ViewModel() {
    private val _uiModel = MutableStateFlow(ProfileUiModel())
    val uiModel = _uiModel.asStateFlow()

    init {
        loadProfile()
    }

    fun loadProfile() {
        _uiModel.setState { copy(profileState = UiState.Loading) }
        viewModelScope.launch {
            when (val result = repository.getProfile()) {
                is Either.Success -> {
                    val data = result.data
                    _uiModel.setState {
                        copy(
                            profileState = UiState.Success(data),
                            nameInput = data.name,
                            emailInput = data.email,
                            biometricEnabled = data.biometricEnabled,
                        )
                    }
                }
                is Either.Failure -> {
                    _uiModel.setState { copy(profileState = UiState.Failed(result.error)) }
                }
            }
        }
    }

    fun retry() {
        loadProfile()
    }

    fun onBiometricToggle(enabled: Boolean) {
        _uiModel.setState { copy(biometricEnabled = enabled) }
    }

    fun onNameChange(value: String) {
        _uiModel.setState { copy(nameInput = value) }
    }

    fun onEmailChange(value: String) {
        _uiModel.setState { copy(emailInput = value) }
    }

    fun saveProfile() {
        // Stub — no-op for now
    }

    fun onChangePhotoClick() {
        // Stub — no-op for now
    }
}
