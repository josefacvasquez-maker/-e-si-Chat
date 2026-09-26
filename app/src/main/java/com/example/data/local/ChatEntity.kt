package com.example.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "chats")
data class ChatEntity(
    @PrimaryKey
    val chatId: String,
    val name: String,
    val contactName: String = "",
    val lastMessage: String,
    val lastTimestamp: Long,
    val unreadCount: Int = 0,
    val isGroup: Boolean = false,
    val groupMembers: String = "", // Comma-separated member names
    val avatarIndex: Int = 0,
    val isOnline: Boolean = true,
    val isE2EVerified: Boolean = true,
    val customStatus: String = "En línea"
)
