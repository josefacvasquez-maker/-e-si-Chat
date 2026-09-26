package com.example.ui.screens

import androidx.compose.foundation.background
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
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Fingerprint
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Podcasts
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.Screenshot
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ui.theme.NensiOffWhite
import com.example.ui.theme.NensiRedAccent
import com.example.ui.theme.NensiRedDark
import com.example.ui.theme.NensiRedLight
import com.example.ui.theme.NensiRedPrimary
import com.example.ui.theme.NensiWhite
import com.example.ui.viewmodel.MainViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SecurityScreen(
    mainViewModel: MainViewModel,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val securitySettings by mainViewModel.securitySettings.collectAsStateWithLifecycle()
    val botsBlocked by mainViewModel.botsBlockedCount.collectAsStateWithLifecycle()
    val humanScore by mainViewModel.humanIntegrityScore.collectAsStateWithLifecycle()

    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }

    var e2eActive by remember(securitySettings) { mutableStateOf(securitySettings?.e2eEncryptionActive ?: true) }
    var antiBotActive by remember(securitySettings) { mutableStateOf(securitySettings?.antiBotActive ?: true) }
    var antiBotDifficulty by remember(securitySettings) { mutableStateOf(securitySettings?.antiBotDifficulty ?: "ESTRICTO") }
    var appLockEnabled by remember(securitySettings) { mutableStateOf(securitySettings?.appLockEnabled ?: false) }
    var pinCode by remember(securitySettings) { mutableStateOf(securitySettings?.appLockPin ?: "1234") }
    var screenshotProtection by remember(securitySettings) { mutableStateOf(securitySettings?.screenshotProtection ?: false) }

    val scrollState = rememberScrollState()

    Scaffold(
        modifier = modifier.fillMaxSize(),
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Seguridad Avanzada Ñeñsi",
                        fontWeight = FontWeight.Bold,
                        color = NensiWhite
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack, modifier = Modifier.testTag("security_back_button")) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Volver",
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
                .padding(16.dp)
        ) {
            // Anti-Bot IA Section Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = NensiWhite),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(40.dp)
                                    .clip(CircleShape)
                                    .background(NensiRedLight),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Security,
                                    contentDescription = null,
                                    tint = NensiRedPrimary,
                                    modifier = Modifier.size(24.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(
                                    text = "Escudo Antibot de IA",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 15.sp,
                                    color = NensiRedDark
                                )
                                Text(
                                    text = "Bloquea scrapers, bots y agentes sintéticos",
                                    fontSize = 11.sp,
                                    color = Color.Gray
                                )
                            }
                        }

                        Switch(
                            checked = antiBotActive,
                            onCheckedChange = { antiBotActive = it },
                            colors = SwitchDefaults.colors(checkedThumbColor = NensiWhite, checkedTrackColor = NensiRedPrimary),
                            modifier = Modifier.testTag("toggle_antibot_switch")
                        )
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(NensiOffWhite, RoundedCornerShape(8.dp))
                            .padding(10.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text("Puntuación de Integridad Humana", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                            Text("Basada en pruebas de comportamiento", fontSize = 10.sp, color = Color.Gray)
                        }
                        Surface(shape = RoundedCornerShape(12.dp), color = Color(0xFF2E7D32)) {
                            Text(
                                text = "$humanScore% HUMANO",
                                color = NensiWhite,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.ExtraBold,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(NensiOffWhite, RoundedCornerShape(8.dp))
                            .padding(10.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Bots y crawlers bloqueados:", fontSize = 12.sp, color = Color.Gray)
                        Text("$botsBlocked ataques repelidos", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = NensiRedPrimary)
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Button(
                        onClick = { mainViewModel.triggerAntiBotChallenge() },
                        colors = ButtonDefaults.buttonColors(containerColor = NensiRedPrimary),
                        shape = RoundedCornerShape(20.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("run_antibot_challenge_button")
                    ) {
                        Text("Ejecutar Prueba de Verificación Humana", fontSize = 13.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // E2E Encryption Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = NensiWhite),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(40.dp)
                                    .clip(CircleShape)
                                    .background(NensiRedLight),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Lock,
                                    contentDescription = null,
                                    tint = NensiRedPrimary,
                                    modifier = Modifier.size(24.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(
                                    text = "Cifrado Extremo a Extremo (E2EE)",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 15.sp,
                                    color = NensiRedDark
                                )
                                Text(
                                    text = "Mensajes y voz cifrados con claves locales",
                                    fontSize = 11.sp,
                                    color = Color.Gray
                                )
                            }
                        }

                        Switch(
                            checked = e2eActive,
                            onCheckedChange = { e2eActive = it },
                            colors = SwitchDefaults.colors(checkedThumbColor = NensiWhite, checkedTrackColor = NensiRedPrimary)
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = "Huella de clave privada local: 8A4F-29B1-E4C0-ÑEÑSI-SEC",
                        fontSize = 11.sp,
                        color = Color.Gray,
                        fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // App Lock & Screenshot Protection Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = NensiWhite),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    // App Lock Switch
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Fingerprint,
                                contentDescription = null,
                                tint = NensiRedPrimary,
                                modifier = Modifier.size(22.dp)
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text("Bloqueo de App con PIN", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                Text("Solicitar PIN al reabrir", fontSize = 11.sp, color = Color.Gray)
                            }
                        }

                        Switch(
                            checked = appLockEnabled,
                            onCheckedChange = { appLockEnabled = it },
                            colors = SwitchDefaults.colors(checkedThumbColor = NensiWhite, checkedTrackColor = NensiRedPrimary)
                        )
                    }

                    if (appLockEnabled) {
                        Spacer(modifier = Modifier.height(10.dp))
                        OutlinedTextField(
                            value = pinCode,
                            onValueChange = { if (it.length <= 4) pinCode = it },
                            label = { Text("PIN de 4 dígitos") },
                            visualTransformation = PasswordVisualTransformation(),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword),
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Screenshot Protection
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.VisibilityOff,
                                contentDescription = null,
                                tint = NensiRedPrimary,
                                modifier = Modifier.size(22.dp)
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text("Protección contra capturas", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                Text("Bloquea capturas de pantalla de chats", fontSize = 11.sp, color = Color.Gray)
                            }
                        }

                        Switch(
                            checked = screenshotProtection,
                            onCheckedChange = { screenshotProtection = it },
                            colors = SwitchDefaults.colors(checkedThumbColor = NensiWhite, checkedTrackColor = NensiRedPrimary)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Save Settings Button
            Button(
                onClick = {
                    mainViewModel.updateSecurity(
                        e2eActive = e2eActive,
                        antiBotActive = antiBotActive,
                        antiBotDifficulty = antiBotDifficulty,
                        appLockEnabled = appLockEnabled,
                        pin = pinCode,
                        screenshotProtection = screenshotProtection
                    )
                    scope.launch {
                        snackbarHostState.showSnackbar("Configuración de seguridad blindada guardada.")
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = NensiRedPrimary),
                shape = RoundedCornerShape(24.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
                    .testTag("save_security_button")
            ) {
                Text(
                    text = "Guardar Configuración de Seguridad",
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp,
                    color = NensiWhite
                )
            }
        }
    }
}
