package com.example.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "call_logs")
data class CallLogEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val contactName: String,
    val timestamp: Long = System.currentTimeMillis(),
    val durationSeconds: Int = 0,
    val callType: String = "OUTGOING", // OUTGOING, INCOMING, MISSED
    val isEncrypted: Boolean = true
)

@Entity(tableName = "security_settings")
data class SecurityEntity(
    @PrimaryKey
    val id: Int = 1,
    val e2eEncryptionActive: Boolean = true,
    val antiBotActive: Boolean = true,
    val antiBotDifficulty: String = "ESTRICTO",
    val appLockPin: String? = null,
    val appLockEnabled: Boolean = false,
    val screenshotProtection: Boolean = false
)
