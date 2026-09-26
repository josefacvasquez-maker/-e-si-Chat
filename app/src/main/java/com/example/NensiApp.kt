package com.example

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.network.CallStatus
import com.example.ui.screens.AuthScreen
import com.example.ui.screens.CallScreen
import com.example.ui.screens.ChatDetailScreen
import com.example.ui.screens.HomeScreen
import com.example.ui.screens.ProfileScreen
import com.example.ui.screens.SecurityScreen
import com.example.ui.theme.NensiOffWhite
import com.example.ui.viewmodel.AuthViewModel
import com.example.ui.viewmodel.MainViewModel

enum class NensiScreen {
    HOME,
    CHAT_DETAIL,
    PROFILE,
    SECURITY
}

@Composable
fun NensiApp(
    authViewModel: AuthViewModel,
    mainViewModel: MainViewModel,
    modifier: Modifier = Modifier
) {
    val currentUser by authViewModel.currentUser.collectAsStateWithLifecycle()
    val activeCall by mainViewModel.activeCall.collectAsStateWithLifecycle()
    val selectedChatId by mainViewModel.selectedChatId.collectAsStateWithLifecycle()

    var currentScreen by remember { mutableStateOf(NensiScreen.HOME) }

    // If user is not authenticated, show AuthScreen
    if (currentUser == null) {
        AuthScreen(
            authViewModel = authViewModel,
            modifier = modifier
        )
        return
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(NensiOffWhite)
    ) {
        when (currentScreen) {
            NensiScreen.HOME -> {
                HomeScreen(
                    mainViewModel = mainViewModel,
                    onOpenChat = { chatId ->
                        mainViewModel.selectChat(chatId)
                        currentScreen = NensiScreen.CHAT_DETAIL
                    },
                    onOpenProfile = { currentScreen = NensiScreen.PROFILE },
                    onOpenSecurity = { currentScreen = NensiScreen.SECURITY },
                    onLogout = {
                        authViewModel.logout()
                        mainViewModel.logout()
                    }
                )
            }

            NensiScreen.CHAT_DETAIL -> {
                BackHandler {
                    mainViewModel.selectChat(null)
                    currentScreen = NensiScreen.HOME
                }
                selectedChatId?.let { id ->
                    ChatDetailScreen(
                        chatId = id,
                        mainViewModel = mainViewModel,
                        onBack = {
                            mainViewModel.selectChat(null)
                            currentScreen = NensiScreen.HOME
                        }
                    )
                } ?: run {
                    currentScreen = NensiScreen.HOME
                }
            }

            NensiScreen.PROFILE -> {
                BackHandler { currentScreen = NensiScreen.HOME }
                ProfileScreen(
                    mainViewModel = mainViewModel,
                    onBack = { currentScreen = NensiScreen.HOME }
                )
            }

            NensiScreen.SECURITY -> {
                BackHandler { currentScreen = NensiScreen.HOME }
                SecurityScreen(
                    mainViewModel = mainViewModel,
                    onBack = { currentScreen = NensiScreen.HOME }
                )
            }
        }

        // Active Voice Call Overlay (Rings or in-call full screen with Accept & Reject buttons)
        if (activeCall != null && activeCall?.status != CallStatus.ENDED) {
            BackHandler {
                mainViewModel.endCall()
            }
            CallScreen(
                activeCall = activeCall!!,
                onAnswer = { mainViewModel.answerCall() },
                onReject = { mainViewModel.rejectCall() },
                onEndCall = { mainViewModel.endCall() },
                onToggleMute = { mainViewModel.toggleMute() },
                onToggleSpeaker = { mainViewModel.toggleSpeaker() }
            )
        }
    }
}
