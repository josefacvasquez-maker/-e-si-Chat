package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.Security
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
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
import com.example.ui.components.NensiAvatar
import com.example.ui.theme.NensiOffWhite
import com.example.ui.theme.NensiRedDark
import com.example.ui.theme.NensiRedLight
import com.example.ui.theme.NensiRedPrimary
import com.example.ui.theme.NensiWhite
import com.example.ui.viewmodel.MainViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    mainViewModel: MainViewModel,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val currentUser by mainViewModel.currentUser.collectAsStateWithLifecycle()
    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }

    var displayName by remember(currentUser) { mutableStateOf(currentUser?.displayName ?: "Josefa Vásquez") }
    var statusBio by remember(currentUser) {
        mutableStateOf(currentUser?.statusBio ?: "¡Hola! Estoy usando Ñeñsi Chat.")
    }
    var selectedAvatarIndex by remember(currentUser) { mutableIntStateOf(currentUser?.avatarIndex ?: 0) }

    val scrollState = rememberScrollState()

    Scaffold(
        modifier = modifier.fillMaxSize(),
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Mi Perfil",
                        fontWeight = FontWeight.Bold,
                        color = NensiWhite
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack, modifier = Modifier.testTag("profile_back_button")) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Volver",
                            tint = NensiWhite
                        )
                    }
                },
                actions = {
                    IconButton(
                        onClick = {
                            mainViewModel.updateProfile(displayName, statusBio, selectedAvatarIndex)
                            scope.launch {
                                snackbarHostState.showSnackbar("Perfil actualizado con éxito.")
                            }
                        },
                        modifier = Modifier.testTag("profile_save_top_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Save,
                            contentDescription = "Guardar",
                            tint = NensiWhite
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = NensiRedDark,
                    titleContentColor = NensiWhite
                )
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(NensiOffWhite)
                .verticalScroll(scrollState)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Large Avatar & Edit Badge
            Box(
                contentAlignment = Alignment.BottomEnd,
                modifier = Modifier.padding(vertical = 16.dp)
            ) {
                NensiAvatar(
                    name = displayName,
                    avatarIndex = selectedAvatarIndex,
                    size = 110
                )

                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(NensiRedDark)
                        .border(2.dp, NensiWhite, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Edit,
                        contentDescription = "Editar avatar",
                        tint = NensiWhite,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }

            // Avatar Color Selector
            Text(
                text = "Selecciona el estilo de tu Avatar",
                fontSize = 12.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color.Gray
            )
            Spacer(modifier = Modifier.height(8.dp))

            Row(
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier.padding(bottom = 16.dp)
            ) {
                for (i in 0..5) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .border(
                                width = if (selectedAvatarIndex == i) 3.dp else 1.dp,
                                color = if (selectedAvatarIndex == i) NensiRedPrimary else Color.LightGray,
                                shape = CircleShape
                            )
                            .clickable { selectedAvatarIndex = i },
                        contentAlignment = Alignment.Center
                    ) {
                        NensiAvatar(
                            name = displayName,
                            avatarIndex = i,
                            isOnline = false,
                            size = 32
                        )
                    }
                }
            }

            // Profile Form Card (Pure White)
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = NensiWhite),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    // "Tu nombre y apellido"
                    OutlinedTextField(
                        value = displayName,
                        onValueChange = { displayName = it },
                        label = { Text("Tu nombre y apellido") },
                        leadingIcon = {
                            Icon(Icons.Default.Person, contentDescription = null, tint = NensiRedPrimary)
                        },
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = NensiRedPrimary,
                            focusedLabelColor = NensiRedPrimary
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("profile_name_input")
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    // Bio / Status
                    OutlinedTextField(
                        value = statusBio,
                        onValueChange = { statusBio = it },
                        label = { Text("Info. y Estado") },
                        leadingIcon = {
                            Icon(Icons.Default.Info, contentDescription = null, tint = NensiRedPrimary)
                        },
                        maxLines = 3,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = NensiRedPrimary,
                            focusedLabelColor = NensiRedPrimary
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("profile_bio_input")
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    // Quick status preset pills
                    Text("Estados predeterminados:", fontSize = 11.sp, color = Color.Gray)
                    Spacer(modifier = Modifier.height(4.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = NensiRedLight,
                            modifier = Modifier.clickable {
                                statusBio = "Disponible en Ñeñsi Chat ✨"
                            }
                        ) {
                            Text(
                                text = "✨ Disponible",
                                fontSize = 11.sp,
                                color = NensiRedDark,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }

                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = NensiRedLight,
                            modifier = Modifier.clickable {
                                statusBio = "Llamadas y chats por internet activos 📶"
                            }
                        ) {
                            Text(
                                text = "📶 En línea",
                                fontSize = 11.sp,
                                color = NensiRedDark,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Account Status & Security Card
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = NensiOffWhite,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.CheckCircle,
                                    contentDescription = null,
                                    tint = Color(0xFF2E7D32),
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "Cuenta activa en Ñeñsi Chat",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF2E7D32)
                                )
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "Nombre de usuario: $displayName",
                                fontSize = 12.sp,
                                color = Color.DarkGray
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = "Cifrado de Extremo a Extremo: Activado",
                                fontSize = 11.sp,
                                color = Color.Gray
                            )
                            Text(
                                text = "Blindaje Antibots IA: Habilitado",
                                fontSize = 11.sp,
                                color = Color.Gray
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    Button(
                        onClick = {
                            mainViewModel.updateProfile(displayName, statusBio, selectedAvatarIndex)
                            scope.launch {
                                snackbarHostState.showSnackbar("Cambios guardados con éxito.")
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = NensiRedPrimary),
                        shape = RoundedCornerShape(24.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                            .testTag("profile_save_button")
                    ) {
                        Icon(Icons.Default.Save, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Guardar Cambios",
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp,
                            color = NensiWhite
                        )
                    }
                }
            }
        }
    }
}
