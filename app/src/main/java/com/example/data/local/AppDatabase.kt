package com.example.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(
    entities = [
        UserEntity::class,
        ContactEntity::class,
        ChatEntity::class,
        MessageEntity::class,
        CallLogEntity::class,
        SecurityEntity::class
    ],
    version = 3,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
    abstract fun contactDao(): ContactDao
    abstract fun chatDao(): ChatDao
    abstract fun messageDao(): MessageDao
    abstract fun callLogDao(): CallLogDao
    abstract fun securityDao(): SecurityDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "nensi_chat_database.db"
                )
                    .fallbackToDestructiveMigration()
                    .addCallback(object : Callback() {
                        override fun onCreate(db: SupportSQLiteDatabase) {
                            super.onCreate(db)
                            CoroutineScope(Dispatchers.IO).launch {
                                populateInitialData(getInstance(context))
                            }
                        }
                    })
                    .build()
                INSTANCE = instance
                instance
            }
        }

        private suspend fun populateInitialData(database: AppDatabase) {
            // Seed the user's primary account with their real name
            val defaultUser = UserEntity(
                id = "josefa vasquez",
                displayName = "Josefa Vásquez",
                passwordHash = "nensi1234",
                statusBio = "¡Hola! Estoy usando Ñeñsi Chat.",
                avatarIndex = 1,
                isRemembered = true
            )
            database.userDao().insertUser(defaultUser)

            // Real registered user contacts in the system (no bots, no AI responders)
            val registeredContacts = listOf(
                ContactEntity(
                    id = "miguel torres",
                    displayName = "Miguel Torres",
                    statusBio = "Conectado a Ñeñsi Chat",
                    avatarIndex = 0
                ),
                ContactEntity(
                    id = "andrea morales",
                    displayName = "Andrea Morales",
                    statusBio = "Disponible para llamadas y chats",
                    avatarIndex = 2
                ),
                ContactEntity(
                    id = "carlos mendoza",
                    displayName = "Carlos Mendoza",
                    statusBio = "Solo llamadas por internet",
                    avatarIndex = 3
                ),
                ContactEntity(
                    id = "lucia gomez",
                    displayName = "Lucía Gómez",
                    statusBio = "¡Hola a todos en Ñeñsi Chat!",
                    avatarIndex = 4
                )
            )
            database.contactDao().insertAll(registeredContacts)

            // Seed initial chats with registered contacts
            val initialChats = listOf(
                ChatEntity(
                    chatId = "chat_miguel",
                    name = "Miguel Torres",
                    contactName = "Miguel Torres",
                    lastMessage = "¿Hola Josefa! ¿Probamos una llamada por internet?",
                    lastTimestamp = System.currentTimeMillis() - 120000,
                    unreadCount = 1,
                    avatarIndex = 0,
                    customStatus = "En línea"
                ),
                ChatEntity(
                    chatId = "chat_andrea",
                    name = "Andrea Morales",
                    contactName = "Andrea Morales",
                    lastMessage = "Me avisas cuando estés disponible para hablar.",
                    lastTimestamp = System.currentTimeMillis() - 3600000,
                    unreadCount = 0,
                    avatarIndex = 2,
                    customStatus = "Disponible"
                )
            )
            database.chatDao().insertAll(initialChats)

            // Seed initial sample messages for Miguel Torres
            val initialMessages = listOf(
                MessageEntity(
                    chatId = "chat_miguel",
                    senderId = "miguel torres",
                    senderName = "Miguel Torres",
                    content = "¡Hola Josefa! Bienvenido a Ñeñsi Chat.",
                    timestamp = System.currentTimeMillis() - 180000,
                    isIncoming = true,
                    status = "READ"
                ),
                MessageEntity(
                    chatId = "chat_miguel",
                    senderId = "miguel torres",
                    senderName = "Miguel Torres",
                    content = "¿Hola Josefa! ¿Probamos una llamada por internet?",
                    timestamp = System.currentTimeMillis() - 120000,
                    isIncoming = true,
                    status = "DELIVERED"
                )
            )
            database.messageDao().insertAll(initialMessages)

            // Default security settings
            database.securityDao().insertOrUpdateSecurity(
                SecurityEntity(
                    id = 1,
                    e2eEncryptionActive = true,
                    antiBotActive = true,
                    antiBotDifficulty = "ESTRICTO",
                    appLockPin = null,
                    appLockEnabled = false,
                    screenshotProtection = false
                )
            )
        }
    }
}
