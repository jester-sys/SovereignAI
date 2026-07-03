package com.ai.sovereignai.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.ai.sovereignai.data.local.dao.*
import  com.ai.sovereignai.data.local.entity.*
import com.ai.sovereignai.data.repository.MessageRepository
import com.ai.sovereignai.domain.model.MemoryEntry


/**
 * YourOwnAI Room Database
 *
 * Contains all tables for local storage:
 * - Conversations
 * - Messages
 * - Memories (long-term memory)
 * - Documents (uploaded files)
 * - DocumentChunks (document chunks for RAG)
 * - ApiKeys (API key metadata)
 * - UsageStats (usage statistics)
 * - SystemPrompts (system prompts)
 * - KnowledgeDocuments (text documents for context)
 * - Personas (profiles with AI settings)
 * - UserBiography (user biography)
 * - BiographyChunks (biography fragments with embeddings)
 */

@Database(
    entities = [
        ConversationEntity::class,
        MessageRepository::class,
        MemoryEntry::class,
        DocumentEntity::class,
        DocumentChunkEntity::class,
        ApiKeyEntity::class,
        UsageStatsEntity::class,
        SystemPromptEntity::class,
        KnowledgeDocumentEntity::class,
        PersonaEntity::class,
        BiographyEntity::class,
        BiographyChunkEntity::class
    ],
    version = 1,
    exportSchema = true
)

abstract  class YourOwnAIDatabase : RoomDatabase() {

    abstract fun conversationDao(): ConversationDao
    abstract fun messageDao(): MessageDao
    abstract fun memoryDao(): MemoryDao
    abstract fun documentDao(): DocumentDao
    abstract fun documentChunkDao(): DocumentChunkDao
    abstract fun apiKeyDao(): ApiKeyDao
    abstract fun usageStatsDao(): UsageStatsDao
    abstract fun systemPromptDao(): SystemPromptDao
    abstract fun knowledgeDocumentDao(): KnowledgeDocumentDao
    abstract fun personaDao(): PersonaDao
    abstract fun biographyDao(): BiographyDao
    abstract fun biographyChunkDao(): BiographyChunkDao

    companion object {
        const val DATABASE_NAME = "yourown_ai_database"
    }
}