package com.example.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "messages")
data class MessageEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val chatId: String,
    val senderId: String,
    val senderName: String,
    val content: String,
    val imageUri: String? = null,
    val timestamp: Long = System.currentTimeMillis(),
    val isIncoming: Boolean,
    val isEncrypted: Boolean = true,
    val status: String = "READ", // SENDING, SENT, DELIVERED, READ
    val messageType: String = "TEXT" // TEXT, IMAGE, CALL_EVENT
)
