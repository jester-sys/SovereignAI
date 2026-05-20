package com.ai.sovereignai.data.sync

import com.ai.sovereignai.data.local.entity.ConversationEntity
import com.ai.sovereignai.data.local.entity.MemoryEntity
import com.ai.sovereignai.data.local.entity.MessageEntity
import kotlinx.serialization.Serializable

/**
 * Mappers between Supabase DTOs and local entities (Optimized)
 *
 * Optimized for Free Tier - removed:
 * - System Prompts (stored locally)
 * - Knowledge Documents (RAG stored locally)
 * - Document Embeddings (RAG stored locally)
 * - Embeddings from Memories (generated locally)
 */

// ===== ConversationDto ↔ ConversationEntity =====
fun ConversationDto.toEntity(): ConversationEntity {
    return ConversationEntity(
        id = id,
        title = title,
        createAt = created_at,
        updateAt = update_at,
        model = model,
        provider = provider,
        systemPrompt = "",
        systemPromptId = null,
        personaId = persona_id,
        sourceConversationId = source_conversation_id,
        isPinned = false,
        isArchived = is_archived

    )
}
fun ConversationEntity.toDto(deviceId: String): ConversationDto{
    return ConversationDto(
        id = id,
        title = title,
        model = model,
        provider = provider,
        created_at = createAt,
        update_at = updateAt,
        is_archived = isArchived,
        source_conversation_id = sourceConversationId,
        persona_id = personaId,
        device_id = deviceId,
        synced_at = System.currentTimeMillis()

    )
}

// ===== MessageDto ↔ MessageEntity =====

fun MessageDto.toEntity(): MessageEntity{
    return MessageEntity(
        id= id,
        conversationId = conversation_id,
        role = role,
        content = content,
        createdAt = created_at,
        model = model,
        swipeMessageText = swipe_message_text,
        imageAttachments = image_attachments,
        fileAttachments = file_attachments,
        isLiked = is_liked
    )
}

fun MessageEntity.toDto(deviceId: String) : MessageDto{
    return MessageDto(
        id = id,
        conversation_id = conversationId,
        role = role,
        content = content,
        created_at = createdAt,
        model = model,
        swipe_message_text = swipeMessageText,
        image_attachments = imageAttachments,
        file_attachments = fileAttachments,
        is_liked = isLiked,
        device_id = deviceId,
        synced_at = System.currentTimeMillis()

    )
}
// ===== MemoryDto ↔ MemoryEntity (WITHOUT embeddings) =====

fun MemoryDto.toEntity(): MemoryEntity {
    return MemoryEntity(
        id = id,
        conversationId = conversation_id,
        messageId = message_id,
        fact = fact,
        createdAt = created_at,
        isArchived = false,
        personaId = persona_id,
        embedding = null

    )
}

fun MemoryEntity.toDto(deviceId: String) : MemoryDto{
    // Note: embeddings are NOT synced to save space
    return MemoryDto(
        id = id,
        conversation_id = conversationId,
        message_id = messageId,
        fact = fact,
        created_at = createdAt,
        persona_id = personaId,
        device_id = deviceId,
        synced_at = System.currentTimeMillis()
    )

}

@Serializable
data class PersonaDto(
    val id: String,
    val system_prompt_id: String,
    val name: String,
    val description: String? = null,
    val is_for_api: Boolean = true,

    // AI Configuration
    val temperature: Float = 0.7f,
    val top_p: Float= 0.9f,
    val max_tokens: Int = 4096,
    val deep_empathy: Boolean = false,
    val memory_enabled: Boolean = false,
    val rag_enabled: Boolean = false,
    val message_history_limit: Int = 10,

    // Prompts
    val deep_empathy_prompt: String? = null,
    val deep_empathy_analysis_prompt: String? = null,
    val memory_extraction_prompt: String? = null,
    val context_instructions: String? = null,
    val memory_instructions: String? = null,
    val rag_instructions : String? = null,
    val swipe_message_prmpt: String? =null,

    // Memory Configuration
    val memory_limit: Int = 5,
    val memory_min_age_days: Int = 2,
    val memory_title: String? = null,

    // RAG Configuration
    val rag_chunk_size:Int = 512,
    val rag_chunk_overlap: Int = 64,
    val rag_chunk_limit:Int =5,
    val rag_title: String? = null,


    // Model Preference
    val preferred_model_id : String? = null,
    val perferred_provider: String? = null,

    // Document Links
    val linked_document_ids: String? = null,

    // Memory Scope
    val use_only_persona_memories: Boolean = false,
    val share_memories_globally: Boolean = true,

    // API Embeddings Configuration
    val use_api_embeddings: Boolean = false,
    val api_embeddings_provider: String = "openai",
    val api_embeddings_model: String = "text-embedding-3-small",

    val created_at: Long,
    val updated_at: Long,
    val device_id: String? = null,
    val syned_at: Long? = null
)

