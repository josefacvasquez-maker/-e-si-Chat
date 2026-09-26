package com.example.network

import android.content.Context
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

enum class CallStatus {
    IDLE,
    OUTGOING_RINGING,
    INCOMING_RINGING,
    CONNECTED,
    ENDED
}

data class ActiveCall(
    val contactName: String,
    val status: CallStatus,
    val durationSeconds: Int = 0,
    val isMuted: Boolean = false,
    val isSpeakerOn: Boolean = true,
    val audioWaveLevel: Float = 0.5f
)

class CallManager private constructor(private val context: Context) {

    private val scope = CoroutineScope(Dispatchers.Main + Job())

    private val _activeCall = MutableStateFlow<ActiveCall?>(null)
    val activeCall: StateFlow<ActiveCall?> = _activeCall.asStateFlow()

    private var callTimerJob: Job? = null

    /**
     * Start outgoing call to a registered user by their Name and Surname
     */
    fun startCall(contactName: String) {
        _activeCall.value = ActiveCall(
            contactName = contactName,
            status = CallStatus.OUTGOING_RINGING,
            durationSeconds = 0,
            isMuted = false,
            isSpeakerOn = true
        )
    }

    /**
     * Trigger incoming call from a registered contact
     * Displays incoming call screen with Accept & Reject buttons
     */
    fun triggerIncomingCall(callerName: String) {
        _activeCall.value = ActiveCall(
            contactName = callerName,
            status = CallStatus.INCOMING_RINGING,
            durationSeconds = 0
        )
    }

    /**
     * User accepts incoming call
     */
    fun answerCall() {
        _activeCall.value = _activeCall.value?.copy(status = CallStatus.CONNECTED)
        startCallTimer()
    }

    /**
     * User rejects incoming call
     */
    fun rejectCall() {
        endCall()
    }

    fun toggleMute() {
        val current = _activeCall.value ?: return
        _activeCall.value = current.copy(isMuted = !current.isMuted)
    }

    fun toggleSpeaker() {
        val current = _activeCall.value ?: return
        _activeCall.value = current.copy(isSpeakerOn = !current.isSpeakerOn)
    }

    private fun startCallTimer() {
        callTimerJob?.cancel()
        callTimerJob = scope.launch {
            var seconds = 0
            while (isActive && _activeCall.value?.status == CallStatus.CONNECTED) {
                delay(1000)
                seconds++
                val wave = (0.3f + (System.currentTimeMillis() % 7) * 0.1f)
                _activeCall.value = _activeCall.value?.copy(
                    durationSeconds = seconds,
                    audioWaveLevel = wave
                )
            }
        }
    }

    fun endCall() {
        callTimerJob?.cancel()
        callTimerJob = null
        _activeCall.value = _activeCall.value?.copy(status = CallStatus.ENDED)
        scope.launch {
            delay(500)
            _activeCall.value = null
        }
    }

    companion object {
        @Volatile
        private var INSTANCE: CallManager? = null

        fun getInstance(context: Context): CallManager {
            return INSTANCE ?: synchronized(this) {
                val instance = CallManager(context.applicationContext)
                INSTANCE = instance
                instance
            }
        }
    }
}
