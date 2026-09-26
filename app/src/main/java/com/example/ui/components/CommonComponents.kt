package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material.icons.filled.Security
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
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
import com.example.data.repository.RecoveryDispatchNotification
import com.example.network.AntiBotChallenge
import com.example.ui.theme.NensiGreenOnline
import com.example.ui.theme.NensiRedDark
import com.example.ui.theme.NensiRedLight
import com.example.ui.theme.NensiRedPrimary
import com.example.ui.theme.NensiWhite

@Composable
fun NensiAvatar(
    name: String,
    avatarIndex: Int = 0,
    isOnline: Boolean = true,
    isGroup: Boolean = false,
    size: Int = 50,
    modifier: Modifier = Modifier
) {
    val avatarColors = listOf(
        NensiRedPrimary,
        Color(0xFFE53935),
        Color(0xFFC2185B),
        Color(0xFFD81B60),
        Color(0xFF8E0000),
        Color(0xFFB71C1C)
    )
    val bgColor = avatarColors[avatarIndex.coerceIn(0, avatarColors.size - 1)]

    Box(
        modifier = modifier.size(size.dp),
        contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = Modifier
                .size(size.dp)
                .clip(CircleShape)
                .background(bgColor),
            contentAlignment = Alignment.Center
        ) {
            if (isGroup) {
                Icon(
                    imageVector = Icons.Default.Group,
                    contentDescription = "Grupo",
                    tint = NensiWhite,
                    modifier = Modifier.size((size * 0.55f).toInt().dp)
                )
            } else if (name.isNotBlank()) {
                Text(
                    text = name.trim().take(1).uppercase(),
                    color = NensiWhite,
                    fontSize = (size * 0.42f).sp,
                    fontWeight = FontWeight.Bold
                )
            } else {
                Icon(
                    imageVector = Icons.Default.Person,
                    contentDescription = "Perfil",
                    tint = NensiWhite,
                    modifier = Modifier.size((size * 0.55f).toInt().dp)
                )
            }
        }

        if (isOnline) {
            Box(
                modifier = Modifier
                    .size((size * 0.28f).coerceAtLeast(12f).toInt().dp)
                    .align(Alignment.BottomEnd)
                    .clip(CircleShape)
                    .background(NensiGreenOnline)
                    .border(2.dp, NensiWhite, CircleShape)
            )
        }
    }
}

@Composable
fun InAppNotificationBanner(
    notification: RecoveryDispatchNotification?,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    AnimatedVisibility(
        visible = notification != null,
        enter = slideInVertically() + fadeIn(),
        exit = slideOutVertically() + fadeOut()
    ) {
        if (notification != null) {
            Card(
                modifier = modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 6.dp),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF1E2124)),
                elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .background(NensiRedPrimary),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Security,
                            contentDescription = "Seguridad",
                            tint = NensiWhite,
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = notification.title,
                            color = NensiWhite,
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = notification.message,
                            color = Color(0xFFE0E0E0),
                            fontSize = 12.sp,
                            lineHeight = 16.sp
                        )
                        if (notification.code != "OK") {
                            Spacer(modifier = Modifier.height(4.dp))
                            Surface(
                                shape = RoundedCornerShape(4.dp),
                                color = NensiRedDark,
                                modifier = Modifier.padding(top = 2.dp)
                            ) {
                                Text(
                                    text = "Código de recuperación: ${notification.code}",
                                    color = NensiWhite,
                                    fontWeight = FontWeight.ExtraBold,
                                    fontSize = 13.sp,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                                )
                            }
                        }
                    }

                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier.testTag("dismiss_notification_banner")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Cerrar",
                            tint = Color.LightGray
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun AddContactByNameDialog(
    onDismiss: () -> Unit,
    onAddContact: (fullName: String) -> Unit
) {
    var name by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.PersonAdd,
                    contentDescription = null,
                    tint = NensiRedPrimary
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Añadir Contacto",
                    fontWeight = FontWeight.Bold,
                    color = NensiRedDark,
                    fontSize = 17.sp
                )
            }
        },
        text = {
            Column {
                Text(
                    text = "Ingresa el nombre y apellido de la persona real para añadirla a tus contactos de Ñeñsi Chat.",
                    fontSize = 13.sp,
                    color = Color.Gray
                )

                Spacer(modifier = Modifier.height(14.dp))

                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Nombre y Apellido") },
                    placeholder = { Text("Ej: Andrea Morales") },
                    singleLine = true,
                    leadingIcon = {
                        Icon(Icons.Default.Person, contentDescription = null, tint = NensiRedPrimary)
                    },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = NensiRedPrimary,
                        focusedLabelColor = NensiRedPrimary
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("add_contact_name_input")
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (name.isNotBlank()) {
                        onAddContact(name.trim())
                        onDismiss()
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = NensiRedPrimary),
                modifier = Modifier.testTag("confirm_add_contact_button")
            ) {
                Text("Añadir Contacto")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancelar", color = Color.Gray)
            }
        }
    )
}

@Composable
fun CreateGroupDialog(
    onDismiss: () -> Unit,
    onCreateGroup: (groupName: String, members: List<String>) -> Unit
) {
    var groupName by remember { mutableStateOf("") }
    var membersInput by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.Group,
                    contentDescription = null,
                    tint = NensiRedPrimary
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Crear Nuevo Grupo",
                    fontWeight = FontWeight.Bold,
                    color = NensiRedDark,
                    fontSize = 17.sp
                )
            }
        },
        text = {
            Column {
                OutlinedTextField(
                    value = groupName,
                    onValueChange = { groupName = it },
                    label = { Text("Nombre del Grupo") },
                    placeholder = { Text("Ej: Amigos, Familia...") },
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = NensiRedPrimary,
                        focusedLabelColor = NensiRedPrimary
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("create_group_name_input")
                )

                Spacer(modifier = Modifier.height(10.dp))

                OutlinedTextField(
                    value = membersInput,
                    onValueChange = { membersInput = it },
                    label = { Text("Participantes (Nombres separados por comas)") },
                    placeholder = { Text("Ej: Miguel Torres, Andrea Morales") },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = NensiRedPrimary,
                        focusedLabelColor = NensiRedPrimary
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("create_group_members_input")
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (groupName.isNotBlank()) {
                        val members = membersInput.split(",")
                            .map { it.trim() }
                            .filter { it.isNotBlank() }
                        onCreateGroup(groupName.trim(), members)
                        onDismiss()
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = NensiRedPrimary),
                modifier = Modifier.testTag("confirm_create_group_button")
            ) {
                Text("Crear Grupo")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancelar", color = Color.Gray)
            }
        }
    )
}

@Composable
fun AntiBotDialog(
    challenge: AntiBotChallenge,
    feedback: String?,
    onAnswer: (String) -> Unit,
    onDismiss: () -> Unit
) {
    var selectedOption by remember { mutableStateOf<String?>(null) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.Security,
                    contentDescription = null,
                    tint = NensiRedPrimary
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Verificación Antibot IA",
                    fontWeight = FontWeight.Bold,
                    color = NensiRedDark,
                    fontSize = 17.sp
                )
            }
        },
        text = {
            Column {
                Text(
                    text = "Demuestra que eres un usuario humano real respondiendo esta comprobación cognitiva:",
                    fontSize = 13.sp,
                    color = Color.DarkGray
                )

                Spacer(modifier = Modifier.height(12.dp))

                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = NensiRedLight,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = challenge.question,
                        fontWeight = FontWeight.Bold,
                        color = NensiRedDark,
                        fontSize = 14.sp,
                        modifier = Modifier.padding(12.dp)
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                challenge.options.forEach { option ->
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { selectedOption = option }
                            .padding(vertical = 4.dp)
                    ) {
                        RadioButton(
                            selected = selectedOption == option,
                            onClick = { selectedOption = option },
                            colors = RadioButtonDefaults.colors(selectedColor = NensiRedPrimary)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(text = option, fontSize = 14.sp)
                    }
                }

                if (feedback != null) {
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = feedback,
                        color = if (feedback.contains("Aprobada")) Color(0xFF2E7D32) else NensiRedPrimary,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    selectedOption?.let { onAnswer(it) }
                },
                enabled = selectedOption != null,
                colors = ButtonDefaults.buttonColors(containerColor = NensiRedPrimary),
                modifier = Modifier.testTag("submit_antibot_button")
            ) {
                Text("Validar Humano")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cerrar", color = Color.Gray)
            }
        }
    )
}
