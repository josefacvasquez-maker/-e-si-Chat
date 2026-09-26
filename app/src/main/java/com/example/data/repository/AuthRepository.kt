package com.example.data.repository

import com.example.data.local.UserDao
import com.example.data.local.UserEntity
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlin.random.Random

data class RecoveryDispatchNotification(
    val recipientName: String,
    val title: String,
    val code: String,
    val message: String,
    val timestamp: Long = System.currentTimeMillis()
)

class AuthRepository(private val userDao: UserDao) {

    private val _currentUser = MutableStateFlow<UserEntity?>(null)
    val currentUser: StateFlow<UserEntity?> = _currentUser.asStateFlow()

    private val _recoveryNotifications = MutableSharedFlow<RecoveryDispatchNotification>(extraBufferCapacity = 5)
    val recoveryNotifications: SharedFlow<RecoveryDispatchNotification> = _recoveryNotifications.asSharedFlow()

    private val pendingRecoveryCodes = mutableMapOf<String, String>()

    suspend fun checkAutoLogin(): Boolean {
        val remembered = userDao.getRememberedUser()
        if (remembered != null) {
            _currentUser.value = remembered
            return true
        }
        return false
    }

    suspend fun login(fullName: String, password: String, rememberMe: Boolean): Result<UserEntity> {
        val cleanName = fullName.trim()
        val normalizedId = cleanName.lowercase()

        val user = userDao.getUserByNameSync(cleanName) ?: userDao.getUserByIdSync(normalizedId)
            ?: return Result.failure(Exception("No existe ninguna cuenta con el nombre y apellido '$cleanName'."))

        if (user.passwordHash != password) {
            return Result.failure(Exception("Contraseña incorrecta. Puedes restablecerla con tu código de recuperación."))
        }

        if (rememberMe) {
            userDao.clearRemembered()
            userDao.setRemembered(user.id, true)
        }

        val updatedUser = user.copy(isRemembered = rememberMe)
        _currentUser.value = updatedUser

        return Result.success(updatedUser)
    }

    suspend fun register(
        fullName: String,
        password: String,
        rememberMe: Boolean = true
    ): Result<UserEntity> {
        val cleanName = fullName.trim()
        val normalizedId = cleanName.lowercase()

        val existing = userDao.getUserByNameSync(cleanName) ?: userDao.getUserByIdSync(normalizedId)
        if (existing != null) {
            return Result.failure(Exception("Ya existe una cuenta registrada con el nombre '$cleanName'. Inicia sesión."))
        }

        if (rememberMe) {
            userDao.clearRemembered()
        }

        val newUser = UserEntity(
            id = normalizedId,
            displayName = cleanName,
            passwordHash = password,
            isRemembered = rememberMe,
            recoveryCode = null
        )
        userDao.insertUser(newUser)

        if (rememberMe) {
            userDao.setRemembered(normalizedId, true)
        }

        _currentUser.value = newUser

        return Result.success(newUser)
    }

    suspend fun requestPasswordRecovery(fullName: String): Result<String> {
        val cleanName = fullName.trim()
        val normalizedId = cleanName.lowercase()
        val user = userDao.getUserByNameSync(cleanName) ?: userDao.getUserByIdSync(normalizedId)
            ?: return Result.failure(Exception("No se encontró ningún usuario con el nombre '$cleanName'."))

        val recoveryCode = String.format("%06d", Random.nextInt(100000, 999999))
        pendingRecoveryCodes[normalizedId] = recoveryCode

        val updatedUser = user.copy(recoveryCode = recoveryCode)
        userDao.updateUser(updatedUser)

        _recoveryNotifications.tryEmit(
            RecoveryDispatchNotification(
                recipientName = user.displayName,
                title = "Ñeñsi Chat • Código de recuperación",
                code = recoveryCode,
                message = "Código de seguridad para ${user.displayName}: $recoveryCode. Úsalo para crear tu nueva contraseña."
            )
        )

        return Result.success(recoveryCode)
    }

    suspend fun resetPasswordWithCode(fullName: String, code: String, newPassword: String): Result<Unit> {
        val cleanName = fullName.trim()
        val normalizedId = cleanName.lowercase()
        val user = userDao.getUserByNameSync(cleanName) ?: userDao.getUserByIdSync(normalizedId)
            ?: return Result.failure(Exception("Usuario no encontrado."))

        val validCode = pendingRecoveryCodes[normalizedId] ?: user.recoveryCode
        if (validCode == null || validCode != code.trim()) {
            return Result.failure(Exception("El código de recuperación es incorrecto o ha caducado."))
        }

        val updated = user.copy(passwordHash = newPassword, recoveryCode = null)
        userDao.updateUser(updated)
        pendingRecoveryCodes.remove(normalizedId)

        _recoveryNotifications.tryEmit(
            RecoveryDispatchNotification(
                recipientName = user.displayName,
                title = "Ñeñsi Chat • Contraseña actualizada",
                code = "OK",
                message = "¡Tu contraseña ha sido actualizada con éxito! Ya puedes iniciar sesión."
            )
        )

        return Result.success(Unit)
    }

    suspend fun updateProfile(
        displayName: String,
        statusBio: String,
        avatarIndex: Int
    ): Result<UserEntity> {
        val current = _currentUser.value ?: return Result.failure(Exception("No hay sesión activa"))
        val updated = current.copy(
            displayName = displayName.trim(),
            statusBio = statusBio.trim(),
            avatarIndex = avatarIndex
        )
        userDao.updateUser(updated)
        _currentUser.value = updated
        return Result.success(updated)
    }

    suspend fun logout() {
        val current = _currentUser.value
        if (current != null) {
            userDao.setRemembered(current.id, false)
        }
        userDao.clearRemembered()
        _currentUser.value = null
    }
}
