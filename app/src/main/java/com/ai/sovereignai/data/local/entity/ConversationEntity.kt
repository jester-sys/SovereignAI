package com.ai.sovereignai.data.local.entity

import android.icu.text.CaseMap
import androidx.room3.Entity
import androidx.room3.Index
import androidx.room3.PrimaryKey
import com.ai.sovereignai.domain.model.MessageRole


@Entity(
    tableName = "conversations",
    indices = [
        Index(value = ["createAt"]),
        Index(value = ["updatedAt"]),
        Index(value = ["systemPromptId"]),
        Index(value = ["personaId"]),

    ]
)


data class ConversationEntity(
    @PrimaryKey
    val id: String,

    val title: String,
    val systemPrompt: String,
    val systemPromptId: String? = null,
    val personaId: String? = null,
    val model: String,
    val provider: String,

    val createAt: Long,
    val updateAt: Long,


    val isPinned: Boolean = false,
    val isArchived: Boolean = false,

    val sourceConversationId: String? = null,

    val webSearchEnabled: Boolean = false,
    val xSearchEnabled: Boolean = false,
)

@Entity(
    tableName = "messages",
    indices = [
        Index(value = ["conversationId"]),
        Index(value = ["createdAt"]),
        Index(value = ["role"])
    ]
)
data class MessageEntity(
    @PrimaryKey
    val id: String,

    val conversationId: String,
    val content: String,

    val role: String,
    val createdAt: Long,

    val tokenCount: Int? = null,
    val model: String? = null,

    val isError: Boolean = false,
    val errorMessage: String? = null,

    // User interaction
    val isLiked: Boolean = false,

    // Swipe/alternative responses
    val swipeMessageId: String? = null,
    val swipeMessageText: String? = null,

    // Attachments
    val imageAttachments: String? = null,
    val fileAttachments: String? = null,

    //Settings snapshot — flags at the moment of message generation
    val temperature: Float? = null,
    val topP: Float? = null,
    val deepEmpathy: Boolean? = null,
    val memoryEnabled: Boolean = true,
    val messageHistoryLimit: Int? = null,
    val systemPrompt: String? = null,

    // Request logs for debugging
    val requestLogs: String? = null,
)

/**
 * Document Entity
 * Uploaded documents for RAG
 */
@Entity(
    tableName = "documents",
    indices = [
        Index(value = ["uploadedAt"]),
        Index(value = ["fileType"]),
        Index(value = ["conversationId"])
    ]
)
data class DocumentEntity(
    @PrimaryKey
    val id: String,

    val fileName: String,
    val fileType: String,
    val filePath: String,
    val fileSize: Long,

    val conversationId: String? = null,

    val uploadedAt: Long,

    val isProcessed: Boolean = false,
    val chunkCount: Int = 0,

    )

/**
 * Document Chunk Entity
 * Parts of the document for RAG search
 */

@Entity(
    tableName = "document_chunks",
    indices = [
        Index(value = ["documentId"]),
        Index(value = ["chunkIndex"]),
    ]
)
data class DocumentChunkEntity(
    @PrimaryKey
    val id: String,

    val documentId: String,
    val content: String,
    val chunkIndex: Int,

    val embedding: String? = null,

    val metadata: String? = null,


    )

/**
 * API Key Entity
 * Storage for API keys (will be encrypted using EncryptedSharedPreferences)
 * This table is only for metadata; the actual keys are stored in EncryptedSharedPreferences
 */
@Entity(
    tableName = "api_keys",
    indices = [
        Index(value = ["provider"]),

    ]
)
data class ApiKeyEntity(
    @PrimaryKey
    val id: String,

    val provider: String,
    val displayName: String,

    val isActive: Boolean = true,

    val addedAt: Long,
    val lastUsedAt: Long? = null,


    )

/**
 * Usage Stats Entity
 * Token usage and cost statistics
 */

@Entity(
    tableName = "usage_stats",
    indices = [
        Index(value = ["date"]),
        Index(value = ["provider"]),
        Index(value = ["model"]),
        Index(value = ["role"]),
    ]
)
data class UsageStatsEntity(
    @PrimaryKey
    val id: String,

    val date: String,

    val provider: String,
    val model: String,

    val inputTokens: Int,
    val outputTokens: Int,
    val totalTokens: Int,

    val estimatedCost: Double,

    val requestCount: Int,

    )

/**
 * System Prompt Entity
 * Saved system prompts
 */

@Entity(
    tableName = "system_prompts",
    indices = [
        Index(value =["createdAt"] ),
        Index(value = ["isDefault"]),
        Index(value = ["promptType"]),
    ]
)
data class SystemPromptEntity(
    @PrimaryKey
    val id: String,

    val name: String,
    val content: String,

    val promptType: String,
    val isDefault: Boolean = false,

    val createdAt: Long,
    val updatedAt: Long,

    val usageCount: Int = 0,

    )


/**
 * Knowledge Document Entity
 * Text documents for context (from settings)
 */

@Entity(
    tableName = "knowledge_documents",
    indices = [
        Index(value = ["createdAt"]),
        Index(value = ["name"]),

    ]
)
data class KnowledgeDocumentEntity(
    @PrimaryKey
    val id: String,

    val name: String,
    val content: String,

    val createAt: Long,
    val updateAt: Long,

    val sizeBytes: Int = 0,
    val linkedPersonaId: String? = null,
)

/**
 * Persona Entity
 * Profiles with AI settings, documents, and memory scope
 */

@Entity(
    tableName = "personas",
    indices = [
        Index(value = ["createdAt"]),
        Index(value = ["name"]),
        Index(value = ["isForApi"]),
        Index(value = ["systemPromptId"]),

    ]
)
data class PersonaEntity(
    @PrimaryKey
    val id: String,

    val name: String,
    val description: String = "",
    val systemPromptId: String,
    val systemPrompt: String,
    val isForApi: Boolean = false,


    // AI Configuration
    val temperature: Float = 0.7f,
    val topP: Float = 1.9f,
    val maxTokens: Int = 4096,
    val deepEmpathy: Boolean = false,
    val memoryEnabled: Boolean = false,
    val ragEnabled: Boolean = false,
    val messageHistoryLimit: Int = 10,

    // Prompt
    val deepEmpathyPrompt : String,
    val deepEmpathyAnalysisPrompt: String,
    val memoryExtractionPrompt: String,
    val contextExtractionPrompt: String,
    val contextInstructions: String,
    val memoryInstructions: String,
    val ragInstructions: String,
    val swipeMessagePrompt: String,

    // Memory Configuration
    val memoryLimit: Int = 5,
    val memoryMiniAgeDays: Int = 2,
    val memoryTitle: String = "Your memories",

    // RAG Configuration
    val ragChunkSize: Int = 512,
    val ragChunkOverlap: Int = 64,
    val ragChunkLimit: Int = 5,
    val ragTitle: String = "Your text library",

    // Model Preference
    val preferredModelId: String?  = null,
    val preferredProvider: String? = null,

    // Document Links
    val linkedDocumentIds: String? = "[]",  // JSON array of document IDs

    // Memory Scope
    val useOnlyPersonalMemories: Boolean = false,
    val shareMemoriesGlobally: Boolean = false,

    // API Embeddings Configuration
    val useApiEmbeddings: Boolean = false,
    val apiEmbeddingsProvider: String? = "openai",
    val apiEmbeddingsModel: String? = "text-embedding-3-small",

    val createAt: Long,
    val updateAt: Long,


)

/**
 * User Biography Entity
 * User-generated biography based on memory clusters
 */
@Entity(tableName = "user_biography")
data class BiographyEntity(
    @PrimaryKey
    val id: String,

    val userValue: String = "",
    val profile: String = "",
    val painPoints: String = "",
    val joys: String = "",
    val fears: String = "",
    val loves: String = "",
    val currentSituation: String = "",

    val lastUpdated: Long = System.currentTimeMillis(),
    val processedClusters: Int = 0
)
/**
 * Biography Chunk Entity
 * Biography fragments with embeddings for semantic search
 */

@Entity(
    tableName = "biography_chunks",
    indices = [
        Index(value = ["biographyId"]),
        Index(value = ["Section"])
    ]
)
data class BiographyChunkEntity(
    @PrimaryKey
    val id: String,

    val biographyId: String = "default",
    val section: String,
    val text: String,
    val embedding: String,

    val createdAt: Long = System.currentTimeMillis(),
)