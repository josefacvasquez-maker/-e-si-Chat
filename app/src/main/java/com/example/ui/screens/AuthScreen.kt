package com.example.ui.screens

import androidx.compose.foundation.background
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ui.components.InAppNotificationBanner
import com.example.ui.theme.NensiOffWhite
import com.example.ui.theme.NensiRedDark
import com.example.ui.theme.NensiRedLight
import com.example.ui.theme.NensiRedPrimary
import com.example.ui.theme.NensiWhite
import com.example.ui.viewmodel.AuthViewModel

@Composable
fun AuthScreen(
    authViewModel: AuthViewModel,
    modifier: Modifier = Modifier
) {
    val uiState by authViewModel.uiState.collectAsStateWithLifecycle()

    var fullName by remember { mutableStateOf("Josefa Vásquez") }
    var password by remember { mutableStateOf("nensi1234") }
    var confirmPassword by remember { mutableStateOf("nensi1234") }
    var rememberPassword by remember { mutableStateOf(true) }
    var showPassword by remember { mutableStateOf(false) }

    var recoveryNameInput by remember { mutableStateOf("") }
    var recoveryCodeInput by remember { mutableStateOf("") }
    var newPasswordInput by remember { mutableStateOf("") }

    val scrollState = rememberScrollState()

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(NensiOffWhite)
            .imePadding()
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
        ) {
            // Header: WhatsApp Carmine Red
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(NensiRedDark)
                    .padding(top = 40.dp, bottom = 28.dp, start = 20.dp, end = 20.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Box(
                        modifier = Modifier
                            .size(72.dp)
                            .clip(CircleShape)
                            .background(NensiWhite)
                            .padding(8.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Security,
                            contentDescription = "Ñeñsi Chat Logo",
                            tint = NensiRedPrimary,
                            modifier = Modifier.size(46.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = "Ñeñsi Chat",
                        color = NensiWhite,
                        fontSize = 26.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 0.5.sp
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = "Llamadas y chats por internet • Sin número ni SIM",
                        color = Color(0xFFFFCDD2),
                        fontSize = 13.sp,
                        textAlign = TextAlign.Center
                    )
                }
            }

            // Notification Banner for recovery codes if requested
            InAppNotificationBanner(
                notification = uiState.recentNotificationAlert,
                onDismiss = { authViewModel.dismissAlert() }
            )

            // Main Card Container (Pure White)
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = NensiWhite),
                elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    // Tabs: Iniciar Sesión / Crear Cuenta
                    TabRow(
                        selectedTabIndex = if (uiState.isLoginMode) 0 else 1,
                        containerColor = NensiWhite,
                        contentColor = NensiRedPrimary,
                        indicator = { tabPositions ->
                            TabRowDefaults.SecondaryIndicator(
                                Modifier.tabIndicatorOffset(tabPositions[if (uiState.isLoginMode) 0 else 1]),
                                color = NensiRedPrimary,
                                height = 3.dp
                            )
                        }
                    ) {
                        Tab(
                            selected = uiState.isLoginMode,
                            onClick = { if (!uiState.isLoginMode) authViewModel.toggleAuthMode() },
                            text = {
                                Text(
                                    text = "Iniciar Sesión",
                                    fontWeight = if (uiState.isLoginMode) FontWeight.Bold else FontWeight.Normal,
                                    fontSize = 15.sp,
                                    color = if (uiState.isLoginMode) NensiRedPrimary else Color.Gray
                                )
                            },
                            modifier = Modifier.testTag("tab_login")
                        )
                        Tab(
                            selected = !uiState.isLoginMode,
                            onClick = { if (uiState.isLoginMode) authViewModel.toggleAuthMode() },
                            text = {
                                Text(
                                    text = "Crear Cuenta",
                                    fontWeight = if (!uiState.isLoginMode) FontWeight.Bold else FontWeight.Normal,
                                    fontSize = 15.sp,
                                    color = if (!uiState.isLoginMode) NensiRedPrimary else Color.Gray
                                )
                            },
                            modifier = Modifier.testTag("tab_register")
                        )
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    // "Tu nombre y apellido" input
                    OutlinedTextField(
                        value = fullName,
                        onValueChange = { fullName = it },
                        label = { Text("Tu nombre y apellido") },
                        placeholder = { Text("Ej: Josefa Vásquez") },
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
                            .testTag("name_input")
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    // Password
                    OutlinedTextField(
                        value = password,
                        onValueChange = { password = it },
                        label = { Text("Contraseña") },
                        leadingIcon = {
                            Icon(Icons.Default.Lock, contentDescription = null, tint = NensiRedPrimary)
                        },
                        trailingIcon = {
                            IconButton(onClick = { showPassword = !showPassword }) {
                                Icon(
                                    imageVector = if (showPassword) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                                    contentDescription = "Ver contraseña",
                                    tint = Color.Gray
                                )
                            }
                        },
                        visualTransformation = if (showPassword) VisualTransformation.None else PasswordVisualTransformation(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = NensiRedPrimary,
                            focusedLabelColor = NensiRedPrimary
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("password_input")
                    )

                    if (!uiState.isLoginMode) {
                        Spacer(modifier = Modifier.height(14.dp))

                        // Confirm Password
                        OutlinedTextField(
                            value = confirmPassword,
                            onValueChange = { confirmPassword = it },
                            label = { Text("Confirmar contraseña") },
                            leadingIcon = {
                                Icon(Icons.Default.Lock, contentDescription = null, tint = NensiRedPrimary)
                            },
                            visualTransformation = if (showPassword) VisualTransformation.None else PasswordVisualTransformation(),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                            singleLine = true,
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = NensiRedPrimary,
                                focusedLabelColor = NensiRedPrimary
                            ),
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("confirm_password_input")
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Remember Password Checkbox
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Checkbox(
                            checked = rememberPassword,
                            onCheckedChange = { rememberPassword = it },
                            colors = CheckboxDefaults.colors(checkedColor = NensiRedPrimary),
                            modifier = Modifier.testTag("remember_password_checkbox")
                        )
                        Text(
                            text = "Recordar contraseña en este dispositivo",
                            fontSize = 13.sp,
                            color = Color.DarkGray
                        )
                    }

                    // Error Message display
                    if (uiState.errorMessage != null) {
                        Spacer(modifier = Modifier.height(6.dp))
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = Color(0xFFFFEBEE),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = uiState.errorMessage!!,
                                color = NensiRedDark,
                                fontSize = 13.sp,
                                modifier = Modifier.padding(10.dp)
                            )
                        }
                    }

                    // Success Message display
                    if (uiState.successMessage != null) {
                        Spacer(modifier = Modifier.height(6.dp))
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = Color(0xFFE8F5E9),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = uiState.successMessage!!,
                                color = Color(0xFF2E7D32),
                                fontSize = 13.sp,
                                modifier = Modifier.padding(10.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Submit Button (Iniciar Sesión o Crear Cuenta)
                    Button(
                        onClick = {
                            if (uiState.isLoginMode) {
                                authViewModel.login(fullName, password, rememberPassword)
                            } else {
                                authViewModel.register(fullName, password, confirmPassword, rememberPassword)
                            }
                        },
                        enabled = !uiState.isLoading,
                        colors = ButtonDefaults.buttonColors(containerColor = NensiRedPrimary),
                        shape = RoundedCornerShape(24.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp)
                            .testTag("auth_submit_button")
                    ) {
                        if (uiState.isLoading) {
                            CircularProgressIndicator(color = NensiWhite, modifier = Modifier.size(24.dp), strokeWidth = 2.dp)
                        } else {
                            Text(
                                text = if (uiState.isLoginMode) "Iniciar Sesión" else "Crear Cuenta",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = NensiWhite
                            )
                        }
                    }

                    // Forgot Password option (Only in Login mode)
                    if (uiState.isLoginMode) {
                        Spacer(modifier = Modifier.height(12.dp))
                        Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                            TextButton(
                                onClick = {
                                    recoveryNameInput = fullName
                                    authViewModel.openForgotPasswordDialog()
                                },
                                modifier = Modifier.testTag("forgot_password_button")
                            ) {
                                Text(
                                    text = "¿Olvidaste tu contraseña? Restablécela aquí",
                                    color = NensiRedPrimary,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }
                        }
                    }
                }
            }

            // Quick Info Footer (Pure Security & Antibot details)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Security,
                            contentDescription = null,
                            tint = NensiRedPrimary,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Cifrado de Extremo a Extremo & Blindaje Antibots IA",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = Color.Gray
                        )
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Solo personas reales registradas. No bots ni IA.",
                        fontSize = 11.sp,
                        color = Color.Gray
                    )
                }
            }
        }
    }

    // Password Recovery Dialog (No Gmail!)
    if (uiState.showForgotPasswordDialog) {
        AlertDialog(
            onDismissRequest = { authViewModel.dismissForgotPasswordDialog() },
            title = {
                Text(
                    text = "Recuperar Contraseña",
                    fontWeight = FontWeight.Bold,
                    color = NensiRedDark
                )
            },
            text = {
                Column {
                    Text(
                        text = "Ingresa tu nombre y apellido para generar tu código de recuperación de seguridad.",
                        fontSize = 13.sp,
                        color = Color.DarkGray
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedTextField(
                        value = recoveryNameInput,
                        onValueChange = { recoveryNameInput = it },
                        label = { Text("Tu nombre y apellido") },
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
                            .testTag("recovery_name_input")
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    OutlinedButton(
                        onClick = { authViewModel.requestRecoveryCode(recoveryNameInput) },
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = NensiRedPrimary),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("request_recovery_code_button")
                    ) {
                        Text("Generar Código de Recuperación")
                    }

                    if (uiState.generatedCodeForDemo != null) {
                        Spacer(modifier = Modifier.height(10.dp))
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = NensiRedLight,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(10.dp)) {
                                Text(
                                    text = "Código de seguridad generado:",
                                    fontSize = 12.sp,
                                    color = NensiRedDark
                                )
                                Text(
                                    text = uiState.generatedCodeForDemo!!,
                                    fontSize = 20.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = NensiRedPrimary
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedTextField(
                        value = recoveryCodeInput,
                        onValueChange = { recoveryCodeInput = it },
                        label = { Text("Código de 6 dígitos") },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = NensiRedPrimary,
                            focusedLabelColor = NensiRedPrimary
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("recovery_code_input")
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    OutlinedTextField(
                        value = newPasswordInput,
                        onValueChange = { newPasswordInput = it },
                        label = { Text("Nueva Contraseña") },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = NensiRedPrimary,
                            focusedLabelColor = NensiRedPrimary
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("recovery_new_password_input")
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        authViewModel.resetPasswordWithCode(recoveryNameInput, recoveryCodeInput, newPasswordInput)
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = NensiRedPrimary),
                    modifier = Modifier.testTag("submit_reset_password_button")
                ) {
                    Text("Restablecer")
                }
            },
            dismissButton = {
                TextButton(onClick = { authViewModel.dismissForgotPasswordDialog() }) {
                    Text("Cancelar", color = Color.Gray)
                }
            }
        )
    }
}
