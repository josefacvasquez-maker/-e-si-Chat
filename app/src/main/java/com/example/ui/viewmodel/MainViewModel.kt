package com.example.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.local.CallLogEntity
import com.example.data.local.ChatEntity
import com.example.data.local.ContactEntity
import com.example.data.local.MessageEntity
import com.example.data.local.SecurityEntity
import com.example.data.local.UserEntity
import com.example.data.repository.AuthRepository
import com.example.data.repository.ChatRepository
import com.example.network.ActiveCall
import com.example.network.AntiBotChallenge
import com.example.network.AntiBotGuard
import com.example.network.CallManager
import com.example.network.CallStatus
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class MainViewModel(
    private val authRepository: AuthRepository,
    private val chatRepository: ChatRepository,
    private val callManager: CallManager,
    private val antiBotGuard: AntiBotGuard
) : ViewModel() {

    val currentUser: StateFlow<UserEntity?> = authRepository.currentUser

    val chats: StateFlow<List<ChatEntity>> = chatRepository.getAllChats()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val contacts: StateFlow<List<ContactEntity>> = chatRepository.getAllContacts()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val callLogs: StateFlow<List<CallLogEntity>> = chatRepository.getCallLogs()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val securitySettings: StateFlow<SecurityEntity?> = chatRepository.getSecuritySettings()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    val activeCall: StateFlow<ActiveCall?> = callManager.activeCall

    val antiBotShieldActive: StateFlow<Boolean> = antiBotGuard.isShieldActive
    val botsBlockedCount: StateFlow<Int> = antiBotGuard.botsBlockedCount
    val humanIntegrityScore: StateFlow<Int> = antiBotGuard.humanIntegrityScore

    private val _selectedChatId = MutableStateFlow<String?>(null)
    val selectedChatId: StateFlow<String?> = _selectedChatId.asStateFlow()

    @OptIn(ExperimentalCoroutinesApi::class)
    val currentChatMessages: StateFlow<List<MessageEntity>> = _selectedChatId
        .flatMapLatest { id ->
            if (id != null) chatRepository.getMessagesForChat(id) else flowOf(emptyList())
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _activeChallenge = MutableStateFlow<AntiBotChallenge?>(null)
    val activeChallenge: StateFlow<AntiBotChallenge?> = _activeChallenge.asStateFlow()

    private val _challengeFeedback = MutableStateFlow<String?>(null)
    val challengeFeedback: StateFlow<String?> = _challengeFeedback.asStateFlow()

    init {
        // Monitor call completion to log in call history
        viewModelScope.launch {
            activeCall.collect { call ->
                if (call?.status == CallStatus.ENDED && call.durationSeconds > 0) {
                    chatRepository.recordCallLog(
                        contactName = call.contactName,
                        durationSeconds = call.durationSeconds,
                        callType = "OUTGOING"
                    )
                }
            }
        }
    }

    fun selectChat(chatId: String?) {
        _selectedChatId.value = chatId
        if (chatId != null) {
            viewModelScope.launch {
                chatRepository.markChatAsRead(chatId)
            }
        }
    }

    fun sendMessage(chatId: String, content: String, imageUri: String? = null) {
        if (content.isBlank() && imageUri == null) return
        val user = currentUser.value
        val senderId = user?.id ?: "me"
        val senderName = user?.displayName ?: "Yo"

        viewModelScope.launch {
            chatRepository.sendMessage(
                chatId = chatId,
                content = content.trim(),
                senderId = senderId,
                senderName = senderName,
                imageUri = imageUri
            )
        }
    }

    fun addContactByName(fullName: String, onResult: (Boolean, String) -> Unit) {
        viewModelScope.launch {
            val result = chatRepository.addContactByName(fullName)
            if (result.isSuccess) {
                onResult(true, "Contacto añadido correctamente")
            } else {
                onResult(false, result.exceptionOrNull()?.message ?: "Error al añadir contacto")
            }
        }
    }

    fun startChatWithContact(contact: ContactEntity) {
        viewModelScope.launch {
            val chatId = chatRepository.createOrGetChatWithContact(contact)
            selectChat(chatId)
        }
    }

    fun createGroup(groupName: String, memberNames: List<String>) {
        if (groupName.isBlank()) return
        viewModelScope.launch {
            val chatId = chatRepository.createGroupChat(groupName, memberNames)
            selectChat(chatId)
        }
    }

    fun startVoiceCall(contactName: String) {
        callManager.startCall(contactName)
    }

    /**
     * Trigger incoming call from a registered contact so user can test accept and reject buttons!
     */
    fun triggerIncomingCall(callerName: String) {
        callManager.triggerIncomingCall(callerName)
    }

    fun answerCall() {
        callManager.answerCall()
    }

    fun rejectCall() {
        val call = activeCall.value
        if (call != null) {
            viewModelScope.launch {
                chatRepository.recordCallLog(
                    contactName = call.contactName,
                    durationSeconds = 0,
                    callType = "MISSED"
                )
            }
        }
        callManager.rejectCall()
    }

    fun endCall() {
        callManager.endCall()
    }

    fun toggleMute() {
        callManager.toggleMute()
    }

    fun toggleSpeaker() {
        callManager.toggleSpeaker()
    }

    fun triggerAntiBotChallenge() {
        _activeChallenge.value = antiBotGuard.createChallenge()
        _challengeFeedback.value = null
    }

    fun submitChallengeAnswer(challengeId: String, answer: String) {
        val verified = antiBotGuard.verifyAnswer(challengeId, answer)
        if (verified) {
            _challengeFeedback.value = "¡Verificación Antibot Aprobada! Tu sesión humana está 100% blindada."
            viewModelScope.launch {
                kotlinx.coroutines.delay(1200)
                _activeChallenge.value = null
                _challengeFeedback.value = null
            }
        } else {
            _challengeFeedback.value = "Respuesta incorrecta. Alerta antibot IA generada."
        }
    }

    fun dismissChallenge() {
        _activeChallenge.value = null
        _challengeFeedback.value = null
    }

    fun updateProfile(displayName: String, statusBio: String, avatarIndex: Int) {
        viewModelScope.launch {
            authRepository.updateProfile(displayName, statusBio, avatarIndex)
        }
    }

    fun updateSecurity(
        e2eActive: Boolean,
        antiBotActive: Boolean,
        antiBotDifficulty: String,
        appLockEnabled: Boolean,
        pin: String?,
        screenshotProtection: Boolean
    ) {
        antiBotGuard.setShieldActive(antiBotActive)
        viewModelScope.launch {
            val updated = SecurityEntity(
                id = 1,
                e2eEncryptionActive = e2eActive,
                antiBotActive = antiBotActive,
                antiBotDifficulty = antiBotDifficulty,
                appLockPin = pin,
                appLockEnabled = appLockEnabled,
                screenshotProtection = screenshotProtection
            )
            chatRepository.updateSecurity(updated)
        }
    }

    fun logout() {
        viewModelScope.launch {
            authRepository.logout()
            _selectedChatId.value = null
        }
    }
}
