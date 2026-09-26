package com.example.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.local.UserEntity
import com.example.data.repository.AuthRepository
import com.example.data.repository.RecoveryDispatchNotification
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class AuthUiState(
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val successMessage: String? = null,
    val isLoginMode: Boolean = true,
    val showForgotPasswordDialog: Boolean = false,
    val pendingFullName: String = "",
    val generatedCodeForDemo: String? = null,
    val recentNotificationAlert: RecoveryDispatchNotification? = null
)

class AuthViewModel(private val authRepository: AuthRepository) : ViewModel() {

    private val _uiState = MutableStateFlow(AuthUiState())
    val uiState: StateFlow<AuthUiState> = _uiState.asStateFlow()

    val currentUser: StateFlow<UserEntity?> = authRepository.currentUser

    init {
        viewModelScope.launch {
            authRepository.recoveryNotifications.collect { notification ->
                _uiState.value = _uiState.value.copy(
                    recentNotificationAlert = notification,
                    generatedCodeForDemo = notification.code
                )
            }
        }
        viewModelScope.launch {
            authRepository.checkAutoLogin()
        }
    }

    fun toggleAuthMode() {
        _uiState.value = _uiState.value.copy(
            isLoginMode = !_uiState.value.isLoginMode,
            errorMessage = null,
            successMessage = null
        )
    }

    fun login(fullName: String, pass: String, rememberMe: Boolean) {
        if (fullName.isBlank() || pass.isBlank()) {
            _uiState.value = _uiState.value.copy(errorMessage = "Por favor ingresa tu nombre y apellido y tu contraseña.")
            return
        }

        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)
            val result = authRepository.login(fullName, pass, rememberMe)
            _uiState.value = _uiState.value.copy(
                isLoading = false,
                errorMessage = result.exceptionOrNull()?.message,
                successMessage = if (result.isSuccess) "Sesión iniciada correctamente" else null
            )
        }
    }

    fun register(fullName: String, pass: String, confirmPass: String, rememberMe: Boolean) {
        if (fullName.isBlank() || pass.isBlank()) {
            _uiState.value = _uiState.value.copy(errorMessage = "Por favor ingresa tu nombre y apellido y tu contraseña.")
            return
        }
        if (pass != confirmPass) {
            _uiState.value = _uiState.value.copy(errorMessage = "Las contraseñas no coinciden.")
            return
        }
        if (pass.length < 4) {
            _uiState.value = _uiState.value.copy(errorMessage = "La contraseña debe tener al menos 4 caracteres.")
            return
        }

        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)
            val result = authRepository.register(fullName, pass, rememberMe)
            if (result.isSuccess) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    successMessage = "¡Cuenta creada exitosamente!"
                )
            } else {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    errorMessage = result.exceptionOrNull()?.message
                )
            }
        }
    }

    fun openForgotPasswordDialog() {
        _uiState.value = _uiState.value.copy(showForgotPasswordDialog = true, errorMessage = null)
    }

    fun dismissForgotPasswordDialog() {
        _uiState.value = _uiState.value.copy(showForgotPasswordDialog = false)
    }

    fun requestRecoveryCode(fullName: String) {
        if (fullName.isBlank()) {
            _uiState.value = _uiState.value.copy(errorMessage = "Ingresa tu nombre y apellido para buscar tu cuenta.")
            return
        }
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)
            val result = authRepository.requestPasswordRecovery(fullName)
            if (result.isSuccess) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    pendingFullName = fullName,
                    generatedCodeForDemo = result.getOrNull(),
                    successMessage = "Código de recuperación generado correctamente."
                )
            } else {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    errorMessage = result.exceptionOrNull()?.message
                )
            }
        }
    }

    fun resetPasswordWithCode(fullName: String, code: String, newPass: String) {
        val targetName = if (fullName.isNotBlank()) fullName else _uiState.value.pendingFullName
        if (targetName.isBlank() || code.isBlank() || newPass.isBlank()) {
            _uiState.value = _uiState.value.copy(errorMessage = "Ingresa tu nombre, el código y tu nueva contraseña.")
            return
        }

        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)
            val result = authRepository.resetPasswordWithCode(targetName, code, newPass)
            if (result.isSuccess) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    showForgotPasswordDialog = false,
                    isLoginMode = true,
                    successMessage = "Contraseña restablecida correctamente. Ya puedes iniciar sesión."
                )
            } else {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    errorMessage = result.exceptionOrNull()?.message
                )
            }
        }
    }

    fun dismissAlert() {
        _uiState.value = _uiState.value.copy(recentNotificationAlert = null)
    }

    fun logout() {
        viewModelScope.launch {
            authRepository.logout()
            _uiState.value = AuthUiState(isLoginMode = true)
        }
    }
}
