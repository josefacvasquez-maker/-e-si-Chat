package com.example.ui.screens

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.CallEnd
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.MicOff
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.VolumeDown
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.network.ActiveCall
import com.example.network.CallStatus
import com.example.ui.components.NensiAvatar
import com.example.ui.theme.NensiRedDark
import com.example.ui.theme.NensiRedPrimary
import com.example.ui.theme.NensiWhite

@Composable
fun CallScreen(
    activeCall: ActiveCall,
    onAnswer: () -> Unit,
    onReject: () -> Unit,
    onEndCall: () -> Unit,
    onToggleMute: () -> Unit,
    onToggleSpeaker: () -> Unit,
    modifier: Modifier = Modifier
) {
    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.25f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulseScale"
    )

    val minutes = activeCall.durationSeconds / 60
    val seconds = activeCall.durationSeconds % 60
    val durationText = String.format("%02d:%02d", minutes, seconds)

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        NensiRedDark,
                        Color(0xFF8E0000),
                        Color(0xFF1E2124)
                    )
                )
            )
            .padding(24.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // Top Call Info & Security Badge
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.padding(top = 40.dp)
            ) {
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = Color(0x33FFFFFF)
                ) {
                    Text(
                        text = when (activeCall.status) {
                            CallStatus.INCOMING_RINGING -> "📞 Llamada entrante"
                            CallStatus.OUTGOING_RINGING -> "Llamando..."
                            CallStatus.CONNECTED -> "Llamada de voz segura"
                            else -> "Finalizando"
                        },
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = NensiWhite,
                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp)
                    )
                }

                Spacer(modifier = Modifier.height(18.dp))

                Text(
                    text = activeCall.contactName,
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold,
                    color = NensiWhite
                )

                Spacer(modifier = Modifier.height(10.dp))

                // Duration or Status
                if (activeCall.status == CallStatus.CONNECTED) {
                    Text(
                        text = durationText,
                        fontSize = 22.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color(0xFFFFCDD2)
                    )
                } else if (activeCall.status == CallStatus.INCOMING_RINGING) {
                    Text(
                        text = "Llamada de voz por internet",
                        fontSize = 14.sp,
                        color = Color(0xFFFFCDD2)
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Security,
                        contentDescription = null,
                        tint = Color(0xFFFFCDD2),
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "Cifrado de extremo a extremo • Sin SIM",
                        fontSize = 11.sp,
                        color = Color(0xFFFFCDD2)
                    )
                }
            }

            // Center: Avatar with audio ripple/pulsing wave
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier.size(200.dp)
            ) {
                if (activeCall.status == CallStatus.CONNECTED || activeCall.status == CallStatus.INCOMING_RINGING) {
                    Box(
                        modifier = Modifier
                            .size(190.dp)
                            .scale(pulseScale)
                            .clip(CircleShape)
                            .background(Color(0x33FFFFFF))
                    )
                    Box(
                        modifier = Modifier
                            .size(150.dp)
                            .scale(pulseScale * 0.9f)
                            .clip(CircleShape)
                            .background(Color(0x44FFFFFF))
                    )
                }

                NensiAvatar(
                    name = activeCall.contactName,
                    avatarIndex = (activeCall.contactName.hashCode().let { kotlin.math.abs(it) % 6 }),
                    isOnline = true,
                    size = 120
                )
            }

            // Bottom Actions: Incoming has Accept & Reject; Connected has Mute/Speaker/End; Outgoing has End
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.padding(bottom = 32.dp)
            ) {
                when (activeCall.status) {
                    CallStatus.INCOMING_RINGING -> {
                        // Accept & Reject Buttons!
                        Row(
                            horizontalArrangement = Arrangement.SpaceEvenly,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            // Reject Button (Red CallEnd)
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                IconButton(
                                    onClick = onReject,
                                    modifier = Modifier
                                        .size(68.dp)
                                        .clip(CircleShape)
                                        .background(NensiRedPrimary)
                                        .border(2.dp, NensiWhite, CircleShape)
                                        .testTag("reject_call_button")
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.CallEnd,
                                        contentDescription = "Rechazar llamada",
                                        tint = NensiWhite,
                                        modifier = Modifier.size(34.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = "Rechazar",
                                    color = NensiWhite,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }

                            // Accept Button (Green Call)
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                IconButton(
                                    onClick = onAnswer,
                                    modifier = Modifier
                                        .size(68.dp)
                                        .clip(CircleShape)
                                        .background(Color(0xFF2E7D32))
                                        .border(2.dp, NensiWhite, CircleShape)
                                        .testTag("accept_call_button")
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Call,
                                        contentDescription = "Aceptar llamada",
                                        tint = NensiWhite,
                                        modifier = Modifier.size(34.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = "Aceptar",
                                    color = NensiWhite,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }
                        }
                    }

                    CallStatus.CONNECTED -> {
                        // In Call Controls: Mute, End, Speaker
                        Row(
                            horizontalArrangement = Arrangement.SpaceEvenly,
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            // Mute button
                            IconButton(
                                onClick = onToggleMute,
                                modifier = Modifier
                                    .size(54.dp)
                                    .clip(CircleShape)
                                    .background(if (activeCall.isMuted) Color(0x99FFFFFF) else Color(0x33FFFFFF))
                                    .testTag("call_mute_button")
                            ) {
                                Icon(
                                    imageVector = if (activeCall.isMuted) Icons.Default.MicOff else Icons.Default.Mic,
                                    contentDescription = "Silenciar micrófono",
                                    tint = NensiWhite,
                                    modifier = Modifier.size(26.dp)
                                )
                            }

                            // End Call button
                            IconButton(
                                onClick = onEndCall,
                                modifier = Modifier
                                    .size(68.dp)
                                    .clip(CircleShape)
                                    .background(NensiRedPrimary)
                                    .border(2.dp, NensiWhite, CircleShape)
                                    .testTag("end_call_button")
                            ) {
                                Icon(
                                    imageVector = Icons.Default.CallEnd,
                                    contentDescription = "Finalizar llamada",
                                    tint = NensiWhite,
                                    modifier = Modifier.size(34.dp)
                                )
                            }

                            // Speaker button
                            IconButton(
                                onClick = onToggleSpeaker,
                                modifier = Modifier
                                    .size(54.dp)
                                    .clip(CircleShape)
                                    .background(if (activeCall.isSpeakerOn) Color(0x99FFFFFF) else Color(0x33FFFFFF))
                                    .testTag("call_speaker_button")
                            ) {
                                Icon(
                                    imageVector = if (activeCall.isSpeakerOn) Icons.Default.VolumeUp else Icons.Default.VolumeDown,
                                    contentDescription = "Altavoz",
                                    tint = NensiWhite,
                                    modifier = Modifier.size(26.dp)
                                )
                            }
                        }
                    }

                    else -> {
                        // Outgoing Ringing: End Call Button
                        IconButton(
                            onClick = onEndCall,
                            modifier = Modifier
                                .size(68.dp)
                                .clip(CircleShape)
                                .background(NensiRedPrimary)
                                .border(2.dp, NensiWhite, CircleShape)
                                .testTag("end_outgoing_call_button")
                        ) {
                            Icon(
                                imageVector = Icons.Default.CallEnd,
                                contentDescription = "Cancelar llamada",
                                tint = NensiWhite,
                                modifier = Modifier.size(34.dp)
                            )
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Cancelar",
                            color = NensiWhite,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            }
        }
    }
}
