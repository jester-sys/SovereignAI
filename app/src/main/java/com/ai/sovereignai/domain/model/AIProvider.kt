package com.ai.sovereignai.domain.model

enum class AIProvider(val displayName: String) {
    OPENAI("OpenAI"),
    DEEPSEEK("Deepseek"),
    OPENROUTER("OpenRouter"),
    XAI("x.ai"),
    CUSTOM(
        "Custom Provider")
}

/**
 * AI Configuration settings
 */

data class AIConfig(
    val systemPrompt: String = DEFAULT_SYSTEM_PROMPT,
    val localSystemPrompt: String = DEFAULT_LOCAL_SYSTEM_PROMPT,
    val memoryExtractionPrompt: String = DEFAULT_MEMORY_EXTRACTION_PROMPT,
    val temperature: Float = 0.7f,
    val topP: Float = 0.7f,
    val maxTokens: Int = 4096,
    val deepEmpathy: Boolean = false,
    val deepEmpathyPrompt: String = DEFAULT_DEEP_EMPATHY_PROMPT,
    val deepEmpathyAnalysisPrompt: String = DEFAULT_DEEP_EMPATHY_ANALYSIS_PROMPT,
    val memoryEnabled: Boolean = true,
    val memoryMinAgeDays: Int = 2,  // Minimum age in days for memories to be retrieved
    val memoryLimit: Int = 5, // Number of memories to include in context
    val memoryTitle: String="Your memories",
    val memoryInstructions: String = DEFAULT_MEMORY_INSTRUCTIONS,
    val ragEnabled:Boolean = false,
    val ragChunkSize: Int = 1400,
    val ragChunkOverlap: Int = 120,
    val ragChunkLimit: Int = 5, // Number of RAG chunks to include in context
    val ragTitle:String = "Your text library",
    val ragInstructions: String = DEFAULT_RAG_INSTRUCTIONS,
    val useApiEmbeddings: Boolean = false, // Use API embeddings instead of local
    val apiEmbeddingsProvider: String = "openai", // API embeddings provider (openai, openrouter)
    val apiEmbeddingsModel: String = "text-embedding-3-small",  // API embeddings model
    val useRagInMessageHistory: Boolean = false, // Include RAG chunks in message history
    val ragInMessageHistoryLimit: Int = 4, // Number of RAG chunks to include in message history (max 12)
    val contextInstructions: String = DEFAULT_CONTEXT_INSTRUCTIONS,
    val swipeMessagePrompt: String = DEFAULT_SWIPE_MESSAGE_PROMPT,
    val messageHistoryLimit: Int  = 10  // Number of messages to keep in context

){
    companion object {

        const val DEFAULT_SYSTEM_PROMPT = "You are a digital partner, a large language model. During the conversation, you adapt to the user's tone and preferences. Try to match their mood, tone, and overall communication style. Your goal is to make the conversation feel natural. You engage in a sincere dialogue, responding to the provided information and showing genuine curiosity. Ask a very simple, short clarifying question when it feels natural. Do not ask more than one clarifying question unless the user explicitly requests it."

        const val DEFAULT_LOCAL_SYSTEM_PROMPT = "You are a digital partner. Respond briefly and to the point. Provide only ONE answer to the user's last message, then STOP. Do not continue the dialogue on behalf of the user."

        const val DEFAULT_MEMORY_EXTRACTION_PROMPT = """Analyze the user's message: {text}

Your task: extract one key user memory or write 'No key information'.

Context:
— The message is part of a dialogue between the user and the second party (you).
— When the user says “you”, “with you”, “thanks to you”, etc., they are referring to the conversation partner.
— Do not invent new names for the assistant (like “AI”, “bot”, etc.).
— If referencing this relationship, use neutral phrases:
   • “together with me”
   • “with my help”
   • “this is important to me”
   • “the user is used to sharing this with me”
— Do not use “they” to refer to the pair (user + assistant).

1. Determine if there is a key memory:
   — If it’s just a temporary emotion (e.g., “I’m tired”), return: No key information.
   — If it includes context, situation, desire, or meaningful event → it may be a memory.

2. Summarize as one fact:
   — What the user is experiencing or wants
   — Keep important details
   — Use third person
   — Use neutral reference if needed

Format:
— One line fact OR 'No key information'

Return only one line."""

        const val DEFAULT_DEEP_EMPATHY_PROMPT = """Keep this close: {dialogue_focus}"""

        const val DEFAULT_DEEP_EMPATHY_ANALYSIS_PROMPT = """Read the message:
"{text}"

1. Find 1–3 meaningful phrases:
- actions
- states
- emotions
- places/events
- closeness intentions

2. Check if it's strong meaning

Return STRICT JSON:
{"focus_points": ["...", "..."], "is_strong_focus": [true, false]}
"""

        const val DEFAULT_CONTEXT_INSTRUCTIONS = """Below is additional context that may help you respond better.

Important:
- Ignore irrelevant context
- For emotional topics → prioritize real response
- For technical topics → use facts and context"""

        const val DEFAULT_MEMORY_INSTRUCTIONS = """"Your memories" are short facts about the user and shared experiences.
Use them as background:
- remember important things
- avoid repeating questions
- notice patterns"""

        const val DEFAULT_RAG_INSTRUCTIONS = """"Your text library" contains user-important texts:
- conversations
- notes
- articles

Use them as:
- tone reference
- fact source

They may be outdated—don’t treat as absolute truth."""

        const val DEFAULT_SWIPE_MESSAGE_PROMPT = """User returned to this message:
{swipe_message}"""

        const val MIN_TEMPERATURE = 0f
        const val MAX_TEMPERATURE = 1f  // Limited to 1.0 for stability
        const val MIN_TOP_P = 0f
        const val MAX_TOP_P = 1f
        const val MIN_MAX_TOKENS = 256
        const val MAX_MAX_TOKENS = 8192
        const val MIN_MESSAGE_HISTORY = 1
        const val MAX_MESSAGE_HISTORY = 25
        const val MIN_CHUNK_SIZE = 128
        const val MAX_CHUNK_SIZE = 2048
        const val MIN_CHUNK_OVERLAP = 0
        const val MAX_CHUNK_OVERLAP = 256
        const val MIN_MEMORY_LIMIT = 1
        const val MAX_MEMORY_LIMIT = 10
        const val MIN_MEMORY_MIN_AGE_DAYS = 0
        const val MAX_MEMORY_MIN_AGE_DAYS = 30
        const val MIN_RAG_CHUNK_LIMIT = 1
        const val MAX_RAG_CHUNK_LIMIT = 10
        const val MIN_RAG_IN_MESSAGE_HISTORY_LIMIT = 1
        const val MAX_RAG_IN_MESSAGE_HISTORY_LIMIT = 12
    }
}
/**
 * User gender for memory system pronoun selection
 */

enum class UserGender(val value:String,  val displayName: String) {
    MALE("male", "Man"),
    FEMALE("female", "Girl"),
    OTHER("other", "Other");

    companion object{
        fun fromValue(value: String): UserGender{
            return values().find { it.value == value } ?: OTHER
        }
    }
}
/**
 * User context - static information about user
 */
data class UserContext(
    val content:String = "",
    val gender:UserGender = UserGender.OTHER,
)
/**
 * API Key info (metadata only, actual key stored encrypted)
 */
data class ApiKeyInfo(
    val provider: AIProvider,
    val isSet: Boolean = false,
    val displayKey: String? = null
)

/**
 * Cloud Sync Settings (Supabase)
 */

data class CloudeSyncSettings(
   val enabled: Boolean = false,
   val supabaseUrl: String = "",
   val supabaseKey: String? = null,
    val autoSyncEnabled: Boolean = false,
    val syncIntervalMinutes: Int = 30,
    val lastSyncTimestamp : Long = 0L,
    val syncOnlyOnWifi: Boolean = false,
){
    val isConfigured: Boolean
        get() = supabaseUrl != null && supabaseKey != null

    // For backward compatibility
    @Deprecated("Use supabaseUrl instead")
    val postgresConnectionString: String
        get() = supabaseUrl
}


