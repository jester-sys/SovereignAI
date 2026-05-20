package com.ai.sovereignai.data.mapper

import com.ai.sovereignai.data.local.entity.ConversationEntity
import com.ai.sovereignai.data.local.entity.KnowledgeDocumentEntity
import com.ai.sovereignai.data.local.entity.MessageEntity
import com.ai.sovereignai.data.local.entity.PersonaEntity
import com.ai.sovereignai.domain.model.Conversation
import com.ai.sovereignai.domain.model.KnowledgeDocument
import com.ai.sovereignai.domain.model.Message
import com.ai.sovereignai.domain.model.MessageRole
import com.ai.sovereignai.domain.model.Persona
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

/**
 * Mappers for converting between Entity and Domain models
 */

// Message Mappers
fun MessageEntity.toDomain() : Message{
    return Message(
        id = id,
        conversationId = conversationId,
        role = MessageRole.fromString(role),
        content = content,
        createAt = createdAt,
        tokenCount = tokenCount,
        model = model,
        isError = isError,
        errorMessage = errorMessage,
        isLiked = isLiked,
        swipeMessageText = swipeMessageText,
        imageAttachments = imageAttachments,
        fileAttachments = fileAttachments,
        temperature = temperature,
        topP = topP,
        deepEmpathy = deepEmpathy,
        memoryEnabled = memoryEnabled,
        messageHistoryLimit = messageHistoryLimit,
        systemPrompt = systemPrompt,
        requestLogs = requestLogs

    )
}

fun Message.toEntity(): MessageEntity{
    return MessageEntity(
        id = id,
        conversationId = conversationId,
        role = role.toStringValue(),
        content = content,
        createdAt = createAt,
        tokenCount = tokenCount,
        model = model,
        isError = isError,
        errorMessage = errorMessage,
        isLiked = isError,
        swipeMessageId = swipeMessageId,
        swipeMessageText = swipeMessageText,
        imageAttachments = imageAttachments,
        fileAttachments = fileAttachments,
        temperature = temperature,
        topP = topP,
        deepEmpathy = deepEmpathy,
        memoryEnabled = memoryEnabled,
        messageHistoryLimit = messageHistoryLimit,
        systemPrompt = systemPrompt,
        requestLogs = requestLogs

    )
}


// Conversation Mappers

fun ConversationEntity.toDomain(message: List<Message> = emptyList()): Conversation {
    return Conversation(
        id = id,
        title = title,
        systemPrompt = systemPrompt,
        systemPromptId = systemPromptId,
        personalId = personaId,
        model = model,
        provider = provider,
        createdAt = createAt,
        updatedAt = updateAt,
        isPinned = isPinned,
        isArchived = isArchived,
        sourceConversationId = sourceConversationId,
        webSearchEnabled = webSearchEnabled,
        xSearchEnabled = xSearchEnabled,
        message = message
    )
}

fun Conversation.toEntity(): ConversationEntity{
    return ConversationEntity(
        id = id,
        title = title,
        systemPrompt = systemPrompt,
        systemPromptId = systemPromptId,
        personaId = personalId,
        model = model,
        provider = provider,
        createAt = createdAt,
        updateAt = updatedAt,
        isPinned = isPinned,
        isArchived = isArchived,
        sourceConversationId = sourceConversationId,
        webSearchEnabled = webSearchEnabled,
        xSearchEnabled = xSearchEnabled

    )
}
// Persona Mappers
fun PersonaEntity.toDomain(): Persona{
    val gson = Gson()
    val linkedDocumentIds = try{
        gson.fromJson<List<String>>(
            linkedDocumentIds,
            object : TypeToken<List<String>>() {}.type
        ) ?: emptyList()

    }catch (e: Exception){
        emptyList()
    }
    return Persona(
        id = id,
        name = name,
        description = description,
        systemPromptId = systemPromptId,
        systemPrompt = systemPrompt,
        isForApi = isForApi,
        temperature = temperature,
        topP = topP,
        maxTokens = maxTokens,
        deepEmpathy = deepEmpathy,
        memoryEnabled = memoryEnabled,
        ragEnabled = ragEnabled,
        messageHistoryLimit = messageHistoryLimit,
        deepEmpathyPrompt = deepEmpathyPrompt,
        deepEmpathyAnalysisPrompt = deepEmpathyAnalysisPrompt,
        memoryExtractionPrompt = memoryExtractionPrompt,
        contextInstructions = contextInstructions,
        memoryInstructions = memoryInstructions,
        ragInstructions = ragInstructions,
        swipeMessagePrompt = swipeMessagePrompt,
        memoryLimit = memoryLimit,
        memoryMinAgeDays = memoryMiniAgeDays,
        memoryTitle = memoryTitle,
        ragChunkSize = ragChunkSize,
        ragChunkOverlap = ragChunkOverlap,
        ragChunkLimit = ragChunkLimit,
        ragTitle = ragTitle,
        preferredModelId = preferredModelId,
        preferredProvider = preferredProvider,
        linkedDocumentIds = linkedDocumentIds,
        useOnlyPersonaMemories = useOnlyPersonalMemories,
        shareMemoriesGlobally = shareMemoriesGlobally,
        createAt = createAt,
        updateAt = updateAt,

    )

}

fun Persona.toEntity(): PersonaEntity{
    val gson = Gson()
    val linkedDocumentIdsJson = gson.toJson(linkedDocumentIds)

    return PersonaEntity(
        id = id,
        name = name,
        description = description,
        systemPromptId = systemPromptId,
        systemPrompt = systemPrompt,
        isForApi = isForApi,
        temperature = temperature,
        topP = topP,
        maxTokens = maxTokens,
        deepEmpathy = deepEmpathy,
        memoryEnabled = memoryEnabled,
        ragEnabled = ragEnabled,
        messageHistoryLimit = messageHistoryLimit,
        deepEmpathyPrompt = deepEmpathyPrompt,
        deepEmpathyAnalysisPrompt = deepEmpathyAnalysisPrompt,
        memoryExtractionPrompt = memoryExtractionPrompt,
        contextInstructions = contextInstructions,
        memoryInstructions = memoryInstructions,
        ragInstructions = ragInstructions,
        swipeMessagePrompt = swipeMessagePrompt,
        memoryLimit = memoryLimit,
        memoryMiniAgeDays = memoryMinAgeDays,
        memoryTitle = memoryTitle,
        ragChunkSize = ragChunkSize,
        ragChunkOverlap = ragChunkOverlap,
        ragChunkLimit = ragChunkLimit,
        ragTitle = ragTitle,
        preferredModelId = preferredModelId,
        preferredProvider = preferredProvider,
        linkedDocumentIds = linkedDocumentIdsJson,
        useOnlyPersonalMemories = useOnlyPersonaMemories,
        shareMemoriesGlobally = shareMemoriesGlobally,
        createAt = createAt,
        updateAt = updateAt,

    )
}

// KnowledgeDocument Mappers

fun KnowledgeDocumentEntity.toDomain(): KnowledgeDocument{
    val gson = Gson()
    val linkedPersonaIds = try {
        gson.fromJson<List<String>>(
            linkedPersonaIds,
            object : TypeToken<List<String>>() {}.type
        ) ?: emptyList()
    }catch (e: Exception){
        emptyList()
    }
    return KnowledgeDocument(
        id = id,
        name = name,
        content = content,
        createdAt = createdAt,
        updatedAt = updatedAt,
        sizeBytes = sizeBytes,
        linkedPersonaIds = linkedPersonaIds
    )
}

fun KnowledgeDocument.toEntity(): KnowledgeDocumentEntity {
    val gson = Gson()
    val linkedPersonaIdsJson = gson.toJson(linkedPersonaIds)

    return KnowledgeDocumentEntity(
        id = id,
        name = name,
        content = content,
        createdAt = createdAt,
        updatedAt = updatedAt,
        sizeBytes = sizeBytes,
        linkedPersonaIds = linkedPersonaIdsJson
    )
}