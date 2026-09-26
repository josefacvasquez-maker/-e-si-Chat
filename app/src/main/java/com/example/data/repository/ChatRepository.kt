package com.example.data.repository

import com.example.data.local.CallLogDao
import com.example.data.local.CallLogEntity
import com.example.data.local.ChatDao
import com.example.data.local.ChatEntity
import com.example.data.local.ContactDao
import com.example.data.local.ContactEntity
import com.example.data.local.MessageDao
import com.example.data.local.MessageEntity
import com.example.data.local.SecurityDao
import com.example.data.local.SecurityEntity
import com.example.data.local.UserDao
import kotlinx.coroutines.flow.Flow
import java.util.UUID

class ChatRepository(
    private val chatDao: ChatDao,
    private val messageDao: MessageDao,
    private val contactDao: ContactDao,
    private val callLogDao: CallLogDao,
    private val securityDao: SecurityDao,
    private val userDao: UserDao
) {
    fun getAllChats(): Flow<List<ChatEntity>> = chatDao.getAllChatsFlow()

    fun getAllContacts(): Flow<List<ContactEntity>> = contactDao.getAllContacts()

    fun getMessagesForChat(chatId: String): Flow<List<MessageEntity>> =
        messageDao.getMessagesForChat(chatId)

    fun getCallLogs(): Flow<List<CallLogEntity>> = callLogDao.getCallLogsFlow()

    fun getSecuritySettings(): Flow<SecurityEntity?> = securityDao.getSecuritySettingsFlow()

    suspend fun getChat(chatId: String): ChatEntity? = chatDao.getChatById(chatId)

    suspend fun markChatAsRead(chatId: String) = chatDao.resetUnread(chatId)

    suspend fun sendMessage(
        chatId: String,
        content: String,
        senderId: String,
        senderName: String,
        imageUri: String? = null
    ): Long {
        val now = System.currentTimeMillis()
        val message = MessageEntity(
            chatId = chatId,
            senderId = senderId,
            senderName = senderName,
            content = content,
            imageUri = imageUri,
            timestamp = now,
            isIncoming = false,
            isEncrypted = true,
            status = "READ",
            messageType = if (imageUri != null) "IMAGE" else "TEXT"
        )
        val id = messageDao.insertMessage(message)
        val preview = if (imageUri != null) "📷 Foto" else content
        chatDao.updateLastMessage(chatId, preview, now)
        return id
    }

    suspend fun addContactByName(fullName: String, statusBio: String = ""): Result<ContactEntity> {
        val cleanName = fullName.trim()
        if (cleanName.isBlank()) {
            return Result.failure(Exception("Por favor ingresa un nombre y apellido."))
        }
        val normalizedId = cleanName.lowercase()

        // Check if already in contacts
        val existing = contactDao.getContactByName(cleanName) ?: contactDao.getContactById(normalizedId)
        if (existing != null) {
            return Result.success(existing)
        }

        // Check if user exists in registered database
        val registeredUser = userDao.getUserByNameSync(cleanName) ?: userDao.getUserByIdSync(normalizedId)

        val newContact = ContactEntity(
            id = normalizedId,
            displayName = registeredUser?.displayName ?: cleanName,
            statusBio = if (statusBio.isNotBlank()) statusBio else (registeredUser?.statusBio ?: "Disponible en Ñeñsi Chat"),
            avatarIndex = registeredUser?.avatarIndex ?: (cleanName.hashCode().let { kotlin.math.abs(it) % 6 })
        )
        contactDao.insertContact(newContact)
        return Result.success(newContact)
    }

    suspend fun createOrGetChatWithContact(contact: ContactEntity): String {
        val existing = chatDao.getChatByContactName(contact.displayName)
        if (existing != null) {
            return existing.chatId
        }

        val chatId = "chat_" + UUID.randomUUID().toString().take(8)
        val now = System.currentTimeMillis()
        val chat = ChatEntity(
            chatId = chatId,
            name = contact.displayName,
            contactName = contact.displayName,
            lastMessage = "Chat iniciado con ${contact.displayName}",
            lastTimestamp = now,
            unreadCount = 0,
            isGroup = false,
            avatarIndex = contact.avatarIndex,
            isOnline = true,
            customStatus = contact.statusBio
        )
        chatDao.insertChat(chat)
        return chatId
    }

    suspend fun createGroupChat(groupName: String, memberNames: List<String>): String {
        val chatId = "group_" + UUID.randomUUID().toString().take(8)
        val now = System.currentTimeMillis()
        val chat = ChatEntity(
            chatId = chatId,
            name = groupName.trim(),
            contactName = memberNames.firstOrNull() ?: "",
            lastMessage = "Grupo creado con ${memberNames.size} participantes",
            lastTimestamp = now,
            unreadCount = 0,
            isGroup = true,
            groupMembers = memberNames.joinToString(", "),
            avatarIndex = 4,
            isOnline = true,
            customStatus = "Grupo • ${memberNames.size} participantes"
        )
        chatDao.insertChat(chat)
        return chatId
    }

    suspend fun recordCallLog(
        contactName: String,
        durationSeconds: Int,
        callType: String
    ) {
        val log = CallLogEntity(
            contactName = contactName,
            timestamp = System.currentTimeMillis(),
            durationSeconds = durationSeconds,
            callType = callType
        )
        callLogDao.insertCallLog(log)
    }

    suspend fun updateSecurity(settings: SecurityEntity) {
        securityDao.insertOrUpdateSecurity(settings)
    }
}
