package com.example.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "users")
data class UserEntity(
    @PrimaryKey
    val id: String, // Normalized lowercase full name e.g. "josefa vasquez"
    val displayName: String, // "Tu nombre y apellido" e.g. "Josefa Vásquez"
    val passwordHash: String,
    val statusBio: String = "¡Hola! Estoy usando Ñeñsi Chat.",
    val avatarIndex: Int = 0,
    val isRemembered: Boolean = false,
    val recoveryCode: String? = null,
    val createdAt: Long = System.currentTimeMillis()
)
