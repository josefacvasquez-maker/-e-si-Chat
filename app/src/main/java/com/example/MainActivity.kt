package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.Modifier
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.data.local.AppDatabase
import com.example.data.repository.AuthRepository
import com.example.data.repository.ChatRepository
import com.example.network.AntiBotGuard
import com.example.network.CallManager
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.viewmodel.AuthViewModel
import com.example.ui.viewmodel.MainViewModel

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val database = AppDatabase.getInstance(applicationContext)
        val callManager = CallManager.getInstance(applicationContext)
        val antiBotGuard = AntiBotGuard.getInstance()

        val authRepository = AuthRepository(database.userDao())
        val chatRepository = ChatRepository(
            chatDao = database.chatDao(),
            messageDao = database.messageDao(),
            contactDao = database.contactDao(),
            callLogDao = database.callLogDao(),
            securityDao = database.securityDao(),
            userDao = database.userDao()
        )

        val authViewModel = ViewModelProvider(this, object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return AuthViewModel(authRepository) as T
            }
        })[AuthViewModel::class.java]

        val mainViewModel = ViewModelProvider(this, object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return MainViewModel(
                    authRepository = authRepository,
                    chatRepository = chatRepository,
                    callManager = callManager,
                    antiBotGuard = antiBotGuard
                ) as T
            }
        })[MainViewModel::class.java]

        setContent {
            MyApplicationTheme {
                NensiApp(
                    authViewModel = authViewModel,
                    mainViewModel = mainViewModel,
                    modifier = Modifier.fillMaxSize()
                )
            }
        }
    }
}
