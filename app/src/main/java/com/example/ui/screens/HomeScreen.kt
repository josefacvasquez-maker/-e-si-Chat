package com.example.ui.screens

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.CallMade
import androidx.compose.material.icons.filled.CallReceived
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material.icons.filled.DoneAll
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.GroupAdd
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.PhoneCallback
import androidx.compose.material.icons.filled.PhoneInTalk
import androidx.compose.material.icons.filled.Security
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.local.CallLogEntity
import com.example.data.local.ChatEntity
import com.example.data.local.ContactEntity
import com.example.ui.components.AddContactByNameDialog
import com.example.ui.components.AntiBotDialog
import com.example.ui.components.CreateGroupDialog
import com.example.ui.components.NensiAvatar
import com.example.ui.theme.NensiCheckmarkBlue
import com.example.ui.theme.NensiOffWhite
import com.example.ui.theme.NensiRedDark
import com.example.ui.theme.NensiRedLight
import com.example.ui.theme.NensiRedPrimary
import com.example.ui.theme.NensiWhite
import com.example.ui.viewmodel.MainViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    mainViewModel: MainViewModel,
    onOpenChat: (String) -> Unit,
    onOpenProfile: () -> Unit,
    onOpenSecurity: () -> Unit,
    onLogout: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val chats by mainViewModel.chats.collectAsStateWithLifecycle()
    val contacts by mainViewModel.contacts.collectAsStateWithLifecycle()
    val callLogs by mainViewModel.callLogs.collectAsStateWithLifecycle()
    val antiBotShieldActive by mainViewModel.antiBotShieldActive.collectAsStateWithLifecycle()
    val activeChallenge by mainViewModel.activeChallenge.collectAsStateWithLifecycle()
    val challengeFeedback by mainViewModel.challengeFeedback.collectAsStateWithLifecycle()

    var selectedTab by remember { mutableIntStateOf(0) } // 0: CHATS, 1: LLAMADAS, 2: CONTACTOS
    var showMenu by remember { mutableStateOf(false) }
    var showAddContactDialog by remember { mutableStateOf(false) }
    var showCreateGroupDialog by remember { mutableStateOf(false) }

    val tabs = listOf("CHATS", "LLAMADAS", "CONTACTOS")

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            Column(modifier = Modifier.background(NensiRedDark)) {
                TopAppBar(
                    title = {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "Ñeñsi Chat",
                                fontWeight = FontWeight.Bold,
                                fontSize = 20.sp,
                                color = NensiWhite
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = if (antiBotShieldActive) Color(0xFFC62828) else Color.DarkGray,
                                modifier = Modifier.padding(start = 2.dp)
                            ) {
                                Text(
                                    text = "🛡️ Antibot IA",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = NensiWhite,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                        }
                    },
                    actions = {
                        IconButton(
                            onClick = { mainViewModel.triggerAntiBotChallenge() },
                            modifier = Modifier.testTag("antibot_trigger_button")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Security,
                                contentDescription = "Prueba Antibot IA",
                                tint = NensiWhite
                            )
                        }

                        IconButton(
                            onClick = { showMenu = !showMenu },
                            modifier = Modifier.testTag("home_overflow_menu_button")
                        ) {
                            Icon(
                                imageVector = Icons.Default.MoreVert,
                                contentDescription = "Más opciones",
                                tint = NensiWhite
                            )
                        }

                        DropdownMenu(
                            expanded = showMenu,
                            onDismissRequest = { showMenu = false }
                        ) {
                            DropdownMenuItem(
                                text = { Text("Añadir contacto (Nombre y Apellido)") },
                                onClick = {
                                    showMenu = false
                                    showAddContactDialog = true
                                },
                                leadingIcon = {
                                    Icon(Icons.Default.PersonAdd, contentDescription = null, tint = NensiRedPrimary)
                                },
                                modifier = Modifier.testTag("menu_add_contact")
                            )
                            DropdownMenuItem(
                                text = { Text("Crear Nuevo Grupo") },
                                onClick = {
                                    showMenu = false
                                    showCreateGroupDialog = true
                                },
                                leadingIcon = {
                                    Icon(Icons.Default.GroupAdd, contentDescription = null, tint = NensiRedPrimary)
                                },
                                modifier = Modifier.testTag("menu_create_group")
                            )
                            DropdownMenuItem(
                                text = { Text("Mi Perfil") },
                                onClick = {
                                    showMenu = false
                                    onOpenProfile()
                                },
                                modifier = Modifier.testTag("menu_profile")
                            )
                            DropdownMenuItem(
                                text = { Text("Seguridad Avanzada & Antibot") },
                                onClick = {
                                    showMenu = false
                                    onOpenSecurity()
                                },
                                modifier = Modifier.testTag("menu_security")
                            )
                            DropdownMenuItem(
                                text = { Text("Cerrar Sesión", color = NensiRedPrimary, fontWeight = FontWeight.Bold) },
                                onClick = {
                                    showMenu = false
                                    onLogout()
                                },
                                modifier = Modifier.testTag("menu_logout")
                            )
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = NensiRedDark,
                        titleContentColor = NensiWhite
                    )
                )

                // WhatsApp Red TabRow
                TabRow(
                    selectedTabIndex = selectedTab,
                    containerColor = NensiRedDark,
                    contentColor = NensiWhite,
                    indicator = { tabPositions ->
                        TabRowDefaults.SecondaryIndicator(
                            Modifier.tabIndicatorOffset(tabPositions[selectedTab]),
                            color = NensiWhite,
                            height = 3.dp
                        )
                    }
                ) {
                    tabs.forEachIndexed { index, title ->
                        Tab(
                            selected = selectedTab == index,
                            onClick = { selectedTab = index },
                            text = {
                                Text(
                                    text = title,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.sp,
                                    color = if (selectedTab == index) NensiWhite else Color(0xFFFFCDD2)
                                )
                            },
                            modifier = Modifier.testTag("home_tab_$index")
                        )
                    }
                }
            }
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    when (selectedTab) {
                        0 -> showAddContactDialog = true
                        1 -> selectedTab = 2 // Switch to contacts to call
                        2 -> showAddContactDialog = true
                    }
                },
                containerColor = NensiRedPrimary,
                contentColor = NensiWhite,
                shape = CircleShape,
                modifier = Modifier.testTag("home_fab")
            ) {
                Icon(
                    imageVector = when (selectedTab) {
                        1 -> Icons.Default.Call
                        2 -> Icons.Default.PersonAdd
                        else -> Icons.Default.Chat
                    },
                    contentDescription = "Acción Principal",
                    tint = NensiWhite
                )
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(NensiOffWhite)
        ) {
            when (selectedTab) {
                0 -> RealChatsTab(
                    chats = chats,
                    onOpenChat = onOpenChat,
                    onNewChatClick = { showAddContactDialog = true }
                )
                1 -> RealCallsTab(
                    callLogs = callLogs,
                    onMakeCall = { name ->
                        mainViewModel.startVoiceCall(name)
                    }
                )
                2 -> RealContactsTab(
                    contacts = contacts,
                    onStartChat = { contact ->
                        mainViewModel.startChatWithContact(contact)
                    },
                    onStartCall = { contact ->
                        mainViewModel.startVoiceCall(contact.displayName)
                    },
                    onSimulateIncomingCall = { contact ->
                        mainViewModel.triggerIncomingCall(contact.displayName)
                    },
                    onAddContactClick = { showAddContactDialog = true },
                    onCreateGroupClick = { showCreateGroupDialog = true }
                )
            }
        }

        // Add Contact by Name Dialog
        if (showAddContactDialog) {
            AddContactByNameDialog(
                onDismiss = { showAddContactDialog = false },
                onAddContact = { name ->
                    mainViewModel.addContactByName(name) { success, msg ->
                        Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
                    }
                }
            )
        }

        // Create Group Dialog
        if (showCreateGroupDialog) {
            CreateGroupDialog(
                onDismiss = { showCreateGroupDialog = false },
                onCreateGroup = { groupName, memberNames ->
                    mainViewModel.createGroup(groupName, memberNames)
                }
            )
        }

        // Antibot Challenge Dialog
        if (activeChallenge != null) {
            AntiBotDialog(
                challenge = activeChallenge!!,
                feedback = challengeFeedback,
                onAnswer = { answer ->
                    mainViewModel.submitChallengeAnswer(activeChallenge!!.id, answer)
                },
                onDismiss = { mainViewModel.dismissChallenge() }
            )
        }
    }
}

@Composable
fun RealChatsTab(
    chats: List<ChatEntity>,
    onOpenChat: (String) -> Unit,
    onNewChatClick: () -> Unit
) {
    if (chats.isEmpty()) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(32.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Box(
                    modifier = Modifier
                        .size(80.dp)
                        .clip(CircleShape)
                        .background(NensiRedLight),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Chat,
                        contentDescription = null,
                        tint = NensiRedPrimary,
                        modifier = Modifier.size(44.dp)
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "No tienes chats activos aún",
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    color = Color.DarkGray
                )

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = "Inicia un chat con personas reales de tus contactos sin números de teléfono.",
                    fontSize = 13.sp,
                    color = Color.Gray,
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                )

                Spacer(modifier = Modifier.height(20.dp))

                Button(
                    onClick = onNewChatClick,
                    colors = ButtonDefaults.buttonColors(containerColor = NensiRedPrimary),
                    shape = RoundedCornerShape(20.dp)
                ) {
                    Icon(Icons.Default.PersonAdd, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Añadir Contacto")
                }
            }
        }
    } else {
        LazyColumn(modifier = Modifier.fillMaxSize()) {
            items(chats, key = { it.chatId }) { chat ->
                RealChatItem(chat = chat, onClick = { onOpenChat(chat.chatId) })
            }
        }
    }
}

@Composable
fun RealChatItem(
    chat: ChatEntity,
    onClick: () -> Unit
) {
    val timeFormat = remember { SimpleDateFormat("HH:mm", Locale.getDefault()) }
    val formattedTime = remember(chat.lastTimestamp) { timeFormat.format(Date(chat.lastTimestamp)) }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .background(NensiWhite)
            .padding(horizontal = 16.dp, vertical = 12.dp)
            .testTag("chat_item_${chat.chatId}"),
        verticalAlignment = Alignment.CenterVertically
    ) {
        NensiAvatar(
            name = chat.name,
            avatarIndex = chat.avatarIndex,
            isOnline = chat.isOnline,
            isGroup = chat.isGroup,
            size = 52
        )

        Spacer(modifier = Modifier.width(14.dp))

        Column(modifier = Modifier.weight(1f)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = chat.name,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    color = Color(0xFF1E2124),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Text(
                    text = formattedTime,
                    fontSize = 12.sp,
                    color = if (chat.unreadCount > 0) NensiRedPrimary else Color.Gray,
                    fontWeight = if (chat.unreadCount > 0) FontWeight.Bold else FontWeight.Normal
                )
            }

            Spacer(modifier = Modifier.height(4.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    modifier = Modifier.weight(1f),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.DoneAll,
                        contentDescription = "Entregado",
                        tint = NensiCheckmarkBlue,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = chat.lastMessage,
                        fontSize = 14.sp,
                        color = Color.Gray,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                if (chat.unreadCount > 0) {
                    Box(
                        modifier = Modifier
                            .size(20.dp)
                            .clip(CircleShape)
                            .background(NensiRedPrimary),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "${chat.unreadCount}",
                            color = NensiWhite,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun RealCallsTab(
    callLogs: List<CallLogEntity>,
    onMakeCall: (name: String) -> Unit
) {
    if (callLogs.isEmpty()) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(32.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Box(
                    modifier = Modifier
                        .size(80.dp)
                        .clip(CircleShape)
                        .background(NensiRedLight),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.PhoneInTalk,
                        contentDescription = null,
                        tint = NensiRedPrimary,
                        modifier = Modifier.size(44.dp)
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "Sin registro de llamadas",
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    color = Color.DarkGray
                )

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = "Llama a tus contactos registrados por internet con audio nítido sin costo ni SIM.",
                    fontSize = 13.sp,
                    color = Color.Gray,
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                )
            }
        }
    } else {
        LazyColumn(modifier = Modifier.fillMaxSize()) {
            items(callLogs) { log ->
                val timeFormat = remember { SimpleDateFormat("dd/MM HH:mm", Locale.getDefault()) }
                val timeStr = remember(log.timestamp) { timeFormat.format(Date(log.timestamp)) }

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(NensiWhite)
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    NensiAvatar(
                        name = log.contactName,
                        avatarIndex = (log.contactName.hashCode().let { kotlin.math.abs(it) % 6 }),
                        size = 48
                    )

                    Spacer(modifier = Modifier.width(14.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = log.contactName,
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp,
                            color = Color(0xFF1E2124)
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = when (log.callType) {
                                    "INCOMING" -> Icons.Default.CallReceived
                                    "MISSED" -> Icons.Default.PhoneCallback
                                    else -> Icons.Default.CallMade
                                },
                                contentDescription = null,
                                tint = if (log.callType == "MISSED") NensiRedPrimary else Color(0xFF2E7D32),
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "$timeStr • ${if (log.durationSeconds > 0) "${log.durationSeconds}s" else "Perdida"}",
                                fontSize = 12.sp,
                                color = Color.Gray
                            )
                        }
                    }

                    IconButton(
                        onClick = { onMakeCall(log.contactName) },
                        modifier = Modifier.testTag("call_again_button_${log.id}")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Call,
                            contentDescription = "Volver a llamar",
                            tint = NensiRedPrimary
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun RealContactsTab(
    contacts: List<ContactEntity>,
    onStartChat: (ContactEntity) -> Unit,
    onStartCall: (ContactEntity) -> Unit,
    onSimulateIncomingCall: (ContactEntity) -> Unit,
    onAddContactClick: () -> Unit,
    onCreateGroupClick: () -> Unit
) {
    LazyColumn(modifier = Modifier.fillMaxSize()) {
        // Quick Action Buttons
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 14.dp, vertical = 8.dp),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = NensiWhite),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
            ) {
                Column {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable(onClick = onAddContactClick)
                            .padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                                .background(NensiRedPrimary),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.PersonAdd, contentDescription = null, tint = NensiWhite)
                        }
                        Spacer(modifier = Modifier.width(14.dp))
                        Text(
                            text = "Añadir contacto (Nombre y Apellido)",
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 15.sp,
                            color = NensiRedDark
                        )
                    }

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable(onClick = onCreateGroupClick)
                            .padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                                .background(Color(0xFFE53935)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.GroupAdd, contentDescription = null, tint = NensiWhite)
                        }
                        Spacer(modifier = Modifier.width(14.dp))
                        Text(
                            text = "Crear nuevo grupo",
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 15.sp,
                            color = NensiRedDark
                        )
                    }
                }
            }
        }

        item {
            Text(
                text = "CONTACTOS REGISTRADOS (${contacts.size})",
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Gray,
                modifier = Modifier.padding(start = 16.dp, top = 8.dp, bottom = 4.dp)
            )
        }

        items(contacts, key = { it.id }) { contact ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 14.dp, vertical = 4.dp),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = NensiWhite),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        NensiAvatar(
                            name = contact.displayName,
                            avatarIndex = contact.avatarIndex,
                            size = 46
                        )

                        Spacer(modifier = Modifier.width(12.dp))

                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = contact.displayName,
                                fontWeight = FontWeight.Bold,
                                fontSize = 15.sp,
                                color = Color(0xFF1E2124)
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = contact.statusBio,
                                fontSize = 12.sp,
                                color = Color.Gray,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }

                        // Chat button
                        IconButton(
                            onClick = { onStartChat(contact) },
                            modifier = Modifier.testTag("contact_chat_${contact.id}")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Chat,
                                contentDescription = "Chatear",
                                tint = NensiRedPrimary
                            )
                        }

                        // Outgoing Call button
                        IconButton(
                            onClick = { onStartCall(contact) },
                            modifier = Modifier.testTag("contact_call_${contact.id}")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Call,
                                contentDescription = "Llamar",
                                tint = Color(0xFF2E7D32)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    // Button to test incoming call simulation with real contact!
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = NensiRedLight,
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onSimulateIncomingCall(contact) }
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.PhoneCallback,
                                contentDescription = null,
                                tint = NensiRedDark,
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Probar llamada entrante de ${contact.displayName} (Aceptar / Rechazar)",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = NensiRedDark
                            )
                        }
                    }
                }
            }
        }
    }
}
