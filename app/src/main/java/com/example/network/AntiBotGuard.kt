package com.example.network

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.UUID
import kotlin.random.Random

data class AntiBotChallenge(
    val id: String = UUID.randomUUID().toString(),
    val question: String,
    val options: List<String>,
    val correctAnswer: String,
    val explanation: String
)

class AntiBotGuard private constructor() {

    private val _isShieldActive = MutableStateFlow(true)
    val isShieldActive: StateFlow<Boolean> = _isShieldActive.asStateFlow()

    private val _botsBlockedCount = MutableStateFlow(14)
    val botsBlockedCount: StateFlow<Int> = _botsBlockedCount.asStateFlow()

    private val _humanIntegrityScore = MutableStateFlow(100)
    val humanIntegrityScore: StateFlow<Int> = _humanIntegrityScore.asStateFlow()

    private val _lastVerificationTime = MutableStateFlow(System.currentTimeMillis())
    val lastVerificationTime: StateFlow<Long> = _lastVerificationTime.asStateFlow()

    private val activeChallenges = mutableMapOf<String, String>()

    fun setShieldActive(active: Boolean) {
        _isShieldActive.value = active
    }

    fun incrementBlockedBots() {
        _botsBlockedCount.value += 1
    }

    /**
     * Generates a dynamic Human Verification Challenge designed to thwart automated AI LLM bots & scrapers
     */
    fun createChallenge(): AntiBotChallenge {
        val challenges = listOf(
            AntiBotChallenge(
                question = "¿Cuál elemento es un objeto físico no digital?",
                options = listOf("Manzana", "Algoritmo", "Token API", "Vector embedding"),
                correctAnswer = "Manzana",
                explanation = "Filtro de corporeidad humana contra agentes sintéticos"
            ),
            AntiBotChallenge(
                question = "Resuelve la prueba antibot: ¿Cuánto es 14 + 27 invertido?",
                options = listOf("14", "41", "44", "72"),
                correctAnswer = "14", // 14 + 27 = 41, inverted = 14
                explanation = "Prueba de procesamiento semántico inverso"
            ),
            AntiBotChallenge(
                question = "¿Qué símbolo representa un mensaje seguro sin internet en Ñeñsi Chat?",
                options = listOf("📡 Antena P2P", "☁️ Nube pública", "🤖 Bot IA", "🔗 Enlace externo"),
                correctAnswer = "📡 Antena P2P",
                explanation = "Validación de contexto de protocolo de red local"
            ),
            AntiBotChallenge(
                question = "¿Qué color NO es primario en la interfaz de Ñeñsi Chat?",
                options = listOf("Verde Neón", "Rojo Carmesí", "Blanco Puro", "Rojo Oscuro"),
                correctAnswer = "Verde Neón",
                explanation = "Validación perceptual de interfaz de usuario"
            )
        )
        val selected = challenges[Random.nextInt(challenges.size)]
        val shuffledOptions = selected.options.shuffled()
        activeChallenges[selected.id] = selected.correctAnswer

        return selected.copy(options = shuffledOptions)
    }

    fun verifyAnswer(challengeId: String, answer: String): Boolean {
        val expected = activeChallenges[challengeId]
        val isCorrect = expected != null && expected.equals(answer, ignoreCase = true)
        activeChallenges.remove(challengeId)
        if (isCorrect) {
            _humanIntegrityScore.value = 100
            _lastVerificationTime.value = System.currentTimeMillis()
        } else {
            _botsBlockedCount.value += 1
            _humanIntegrityScore.value = (_humanIntegrityScore.value - 15).coerceAtLeast(40)
        }
        return isCorrect
    }

    companion object {
        @Volatile
        private var INSTANCE: AntiBotGuard? = null

        fun getInstance(): AntiBotGuard {
            return INSTANCE ?: synchronized(this) {
                val instance = AntiBotGuard()
                INSTANCE = instance
                instance
            }
        }
    }
}
