package com.ai.sovereignai.domain.model

import android.media.tv.AitInfo

data class Persona(
    val id: String,
    val name: String,
    val description: String = "",
    val systemPromptId:String,
    val systemPrompt: String,
    val isForApi: Boolean = false,
    val isDefault: Boolean = false,

    // ===== AI Configuration =====
// Copied from the global AIConfig during creation, then can be modified
    val temperature: Float = 0.7f,
    val topP: Float = 0.9f,
    val maxTokens: Int = 4096,
    val deepEmpathy: Boolean = false,
    val memoryEnabled: Boolean = true,
    val ragEnabled: Boolean = false,
    val messageHistoryLimit: Int = 10,

    // ===== Prompts =====
    val deepEmpathyPrompt: String = AIConfig.DEFAULT_DEEP_EMPATHY_ANALYSIS_PROMPT,
    val deepEmpathyAnalysisPrompt: String = AIConfig.DEFAULT_DEEP_EMPATHY_ANALYSIS_PROMPT,
    val contextInstructions: String = AIConfig.DEFAULT_CONTEXT_INSTRUCTIONS,
    val memoryInstructions: String = AIConfig.DEFAULT_MEMORY_INSTRUCTIONS,
    val ragInstructions: String = AIConfig.DEFAULT_RAG_INSTRUCTIONS,
    val swipeMessagePrompt: String = AIConfig.DEFAULT_SWIPE_MESSAGE_PROMPT,

    // ===== Memory Configuration =====
    val memoryLimit: Int =5,
    val memoryMinAgeDays: Int = 2,
    val memoryTitle: String = "Your memories",

    // ===== RAG Configuration =====
    val ragchunkSize:Int =152,
    val ragChunkOverlap : Int = 64,
    val ragchunkLimit: Int = 5,
    val ragTitle: String = "Your library of texts",

    // ===== Model Preference =====
    val perferredModelId: String? = null,
    val preferredProvider: String? = null,

    // ===== Document Links =====
    val linkedDocumentIds: List<String> = emptyList(),

    // ===== Memory Scope =====
    //If true, use only the memories of this persona.
    val useOnlyPersonaMemories: Boolean = false,
    //If false, do not share memories with other personas.
    val shareMemoriesGlobally: Boolean = true,

    val createAt:Long,
    val updateAt:Long
){
    //Create a Persona from the SystemPrompt and the global AIConfig.

    fun fromSystemPrompt(
        id: String,
        systemPromptid: String,
        systemPromptName:String,
        systemPromptContent: String,
        description: String = "",
        config: AIConfig,
        isForApi: Boolean = true
    ): Persona{

        return Persona(
            id = id,
            name = systemPromptName,
            description = description,
            systemPromptId = systemPromptid,
            systemPrompt = systemPromptContent,
            isForApi = isForApi,
            temperature = config.temperature,
            topP = config.topP,
            maxTokens = config.maxTokens,
            deepEmpathy = config.deepEmpathy,
            memoryEnabled = config.memoryEnabled,
            ragEnabled = config.ragEnabled,
            messageHistoryLimit = config.messageHistoryLimit,
            deepEmpathyPrompt = config.deepEmpathyPrompt,
            deepEmpathyAnalysisPrompt = config.deepEmpathyAnalysisPrompt,
            contextInstructions = config.contextInstructions,
            memoryInstructions = config.memoryInstructions,
            ragInstructions = config.ragInstructions,
            swipeMessagePrompt = config.swipeMessagePrompt,
            memoryLimit = config.memoryLimit,
            memoryMinAgeDays = config.memoryMinAgeDays,
            memoryTitle = config.memoryTitle,
            ragTitle = config.memoryTitle,
            ragchunkSize = config.ragChunkSize,
            ragChunkOverlap = config.ragChunkOverlap,
            createAt = System.currentTimeMillis(),
            updateAt = System.currentTimeMillis()
        )
    }
    // Convert a Persona to an AIConfig for use in chat.

    fun toAIConfig(): AIConfig {
        return AIConfig(
            systemPrompt = if (isForApi)  systemPrompt else AIConfig.DEFAULT_SYSTEM_PROMPT,
            localSystemPrompt = if (!isForApi) systemPrompt else AIConfig.DEFAULT_LOCAL_SYSTEM_PROMPT,
            memoryExtractionPrompt = AIConfig.DEFAULT_MEMORY_EXTRACTION_PROMPT,
            temperature =   temperature,
            topP = topP,
            maxTokens = maxTokens,
            deepEmpathy = deepEmpathy,
            deepEmpathyPrompt = deepEmpathyAnalysisPrompt,
            memoryEnabled = memoryEnabled,
            memoryLimit =   memoryLimit,
            memoryMinAgeDays = memoryMinAgeDays,
            memoryTitle = memoryTitle,
            memoryInstructions = memoryInstructions,
            ragEnabled = ragEnabled,
            ragChunkSize = ragchunkSize,
            ragChunkOverlap = ragChunkOverlap,
            ragChunkLimit = ragchunkLimit,
            ragTitle = ragTitle,
            ragInstructions = ragInstructions,
            contextInstructions = contextInstructions,
            swipeMessagePrompt = swipeMessagePrompt,
            messageHistoryLimit = messageHistoryLimit
        )
    }

}

