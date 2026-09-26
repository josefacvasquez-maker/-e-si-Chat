package com.example.ui.screens

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.DoneAll
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.Security
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.local.ChatEntity
import com.example.data.local.MessageEntity
import com.example.ui.components.NensiAvatar
import com.example.ui.theme.NensiChatBackground
import com.example.ui.theme.NensiCheckmarkBlue
import com.example.ui.theme.NensiIncomingBubble
import com.example.ui.theme.NensiOffWhite
import com.example.ui.theme.NensiOutgoingBubble
import com.example.ui.theme.NensiRedDark
import com.example.ui.theme.NensiRedPrimary
import com.example.ui.theme.NensiWhite
import com.example.ui.viewmodel.MainViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatDetailScreen(
    chatId: String,
    mainViewModel: MainViewModel,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val chats by mainViewModel.chats.collectAsStateWithLifecycle()
    val chat = chats.firstOrNull { it.chatId == chatId } ?: ChatEntity(
        chatId = chatId,
        name = "Chat",
        lastMessage = "",
        lastTimestamp = System.currentTimeMillis()
    )

    val messages by mainViewModel.currentChatMessages.collectAsStateWithLifecycle()
    var inputText by remember { mutableStateOf("") }
    val listState = rememberLazyListState()

    // Real Photo Picker launcher for sending photos using camera / gallery!
    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        if (uri != null) {
            mainViewModel.sendMessage(
                chatId = chat.chatId,
                content = if (inputText.isNotBlank()) inputText else "📷 Foto enviada",
                imageUri = uri.toString()
            )
            inputText = ""
        }
    }

    LaunchedEffect(messages.size) {
        if (messages.isNotEmpty()) {
            listState.animateScrollToItem(messages.size - 1)
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(NensiChatBackground)
            .imePadding()
    ) {
        // Red Top App Bar
        TopAppBar(
            title = {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    NensiAvatar(
                        name = chat.name,
                        avatarIndex = chat.avatarIndex,
                        isOnline = chat.isOnline,
                        isGroup = chat.isGroup,
                        size = 38
                    )

                    Spacer(modifier = Modifier.width(10.dp))

                    Column {
                        Text(
                            text = chat.name,
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp,
                            color = NensiWhite,
                            maxLines = 1
                        )
                        Text(
                            text = if (chat.isGroup) "Grupo de chat seguro" else "En línea • Cifrado Seguro",
                            fontSize = 11.sp,
                            color = Color(0xFFFFCDD2)
                        )
                    }
                }
            },
            navigationIcon = {
                IconButton(onClick = onBack, modifier = Modifier.testTag("chat_back_button")) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Volver",
                        tint = NensiWhite
                    )
                }
            },
            actions = {
                // Real Calling Button (Calls real contact by Name)
                IconButton(
                    onClick = {
                        mainViewModel.startVoiceCall(contactName = chat.name)
                    },
                    modifier = Modifier.testTag("chat_voice_call_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.Call,
                        contentDescription = "Llamar por internet",
                        tint = NensiWhite
                    )
                }

                IconButton(
                    onClick = { mainViewModel.triggerAntiBotChallenge() },
                    modifier = Modifier.testTag("chat_security_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.Security,
                        contentDescription = "Escudo Antibot",
                        tint = NensiWhite
                    )
                }
            },
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = NensiRedDark,
                titleContentColor = NensiWhite
            )
        )

        // Security / Encryption info pill
        Surface(
            color = Color(0xFFFFF9C4),
            shape = RoundedCornerShape(8.dp),
            modifier = Modifier
                .padding(horizontal = 24.dp, vertical = 8.dp)
                .align(Alignment.CenterHorizontally)
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Security,
                    contentDescription = null,
                    tint = Color(0xFFF57F17),
                    modifier = Modifier.size(13.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "🔒 Mensajes cifrados de extremo a extremo • Antibots activo",
                    fontSize = 11.sp,
                    color = Color(0xFF5D4037)
                )
            }
        }

        // Messages List
        LazyColumn(
            state = listState,
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .padding(horizontal = 12.dp)
        ) {
            items(messages, key = { it.id }) { msg ->
                RealMessageBubble(message = msg)
                Spacer(modifier = Modifier.height(4.dp))
            }
        }

        // Bottom Input Bar (White bar with Camera icon and Red Send button)
        Surface(
            color = NensiWhite,
            shadowElevation = 6.dp,
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp, vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Camera Button to send photos!
                IconButton(
                    onClick = {
                        photoPickerLauncher.launch(
                            PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                        )
                    },
                    modifier = Modifier.testTag("chat_camera_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.CameraAlt,
                        contentDescription = "Enviar Foto",
                        tint = NensiRedPrimary,
                        modifier = Modifier.size(26.dp)
                    )
                }

                // Text Input
                OutlinedTextField(
                    value = inputText,
                    onValueChange = { inputText = it },
                    placeholder = { Text("Mensaje...", fontSize = 15.sp, color = Color.Gray) },
                    shape = RoundedCornerShape(24.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = NensiRedPrimary,
                        unfocusedBorderColor = Color(0xFFE0E0E0),
                        focusedContainerColor = NensiOffWhite,
                        unfocusedContainerColor = NensiOffWhite
                    ),
                    maxLines = 4,
                    modifier = Modifier
                        .weight(1f)
                        .padding(horizontal = 4.dp)
                        .testTag("chat_input_text")
                )

                // Send Button
                IconButton(
                    onClick = {
                        if (inputText.isNotBlank()) {
                            mainViewModel.sendMessage(chat.chatId, inputText)
                            inputText = ""
                        }
                    },
                    modifier = Modifier
                        .size(44.dp)
                        .clip(CircleShape)
                        .background(NensiRedPrimary)
                        .testTag("chat_send_button")
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.Send,
                        contentDescription = "Enviar",
                        tint = NensiWhite,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun RealMessageBubble(message: MessageEntity) {
    val isOutgoing = !message.isIncoming
    val timeFormat = remember { SimpleDateFormat("HH:mm", Locale.getDefault()) }
    val timeStr = remember(message.timestamp) { timeFormat.format(Date(message.timestamp)) }

    Box(
        modifier = Modifier.fillMaxWidth(),
        contentAlignment = if (isOutgoing) Alignment.CenterEnd else Alignment.CenterStart
    ) {
        Card(
            shape = RoundedCornerShape(
                topStart = 14.dp,
                topEnd = 14.dp,
                bottomStart = if (isOutgoing) 14.dp else 2.dp,
                bottomEnd = if (isOutgoing) 2.dp else 14.dp
            ),
            colors = CardDefaults.cardColors(
                containerColor = if (isOutgoing) NensiOutgoingBubble else NensiIncomingBubble
            ),
            elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
            modifier = Modifier.widthIn(max = 290.dp)
        ) {
            Column(modifier = Modifier.padding(8.dp)) {
                // Sender name if incoming
                if (message.isIncoming && message.senderName.isNotBlank()) {
                    Text(
                        text = message.senderName,
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp,
                        color = NensiRedDark
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                }

                // If message is a Photo
                if (message.imageUri != null) {
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = Color(0xFFEF9A9A),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(160.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Icon(
                                    imageVector = Icons.Default.Image,
                                    contentDescription = "Foto",
                                    tint = NensiWhite,
                                    modifier = Modifier.size(48.dp)
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "📷 Foto recibida",
                                    color = NensiWhite,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.sp
                                )
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                }

                // Text Content
                if (message.content.isNotBlank() && message.content != "📷 Foto enviada") {
                    Text(
                        text = message.content,
                        fontSize = 14.sp,
                        color = Color(0xFF1E2124),
                        lineHeight = 18.sp
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                }

                // Timestamp & Checkmarks
                Row(
                    modifier = Modifier.align(Alignment.End),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = timeStr,
                        fontSize = 10.sp,
                        color = Color.Gray
                    )

                    if (isOutgoing) {
                        Spacer(modifier = Modifier.width(3.dp))
                        Icon(
                            imageVector = Icons.Default.DoneAll,
                            contentDescription = "Visto",
                            tint = NensiCheckmarkBlue,
                            modifier = Modifier.size(14.dp)
                        )
                    }
                }
            }
        }
    }
}
