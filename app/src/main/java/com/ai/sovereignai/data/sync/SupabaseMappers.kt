package com.ai.sovereignai.data.sync

import com.ai.sovereignai.data.local.entity.ConversationEntity
import com.ai.sovereignai.data.local.entity.MessageEntity

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

fun MemoryDto.toEntity(): MemoryEntity{
    return MemoryEntity(
        id = id,
        conversationId = conversation_id,
        message_id = message_id,

    )
}