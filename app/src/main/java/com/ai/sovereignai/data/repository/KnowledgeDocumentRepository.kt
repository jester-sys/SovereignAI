package com.ai.sovereignai.data.repository

import com.ai.sovereignai.data.local.YourOwnAIDatabase
import com.ai.sovereignai.data.local.entity.KnowledgeDocumentEntity
import com.ai.sovereignai.data.mapper.toDomain
import com.ai.sovereignai.domain.model.KnowledgeDocument
import com.yourown.ai.data.repository.AiConfigRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.util.UUID
import javax.inject.Inject


/**
 * Repository for managing knowledge documents
 */
class KnowledgeDocumentRepository @Inject constructor(
    private  val database : YourOwnAIDatabase,
    private  val documentEmbeddingRepository : DocumentEmbeddingRepository,
    private  val aiConfigRepository: AiConfigRepository

) {

    private val dao = database.knowledgeDocumentDao()

    /**
     * Get all documents as Flow
     */

    fun getAllDocuments() : Flow<List<KnowledgeDocument>> {
        return  dao.getAllDocuments().map { entities ->
            entities.map { it.toDomain() }
        }
    }

    /**
     * Get document by ID
     */
    suspend fun getDocumentById(id: String): KnowledgeDocument? {
        return dao.getDocumentById(id)?.toDomain()
    }

    /**
     * Create new document and process it for RAG
     */
    suspend fun createDocument(
        name: String,
        content: String,
        linkedPersonaIds: List<String> = emptyList()
    ): Result<String> {
        return  try {
            val now = System.currentTimeMillis()
            val documentId = UUID.randomUUID().toString()
            val entity = KnowledgeDocumentEntity(
                id = documentId,
                name = name,
                content = content,
                createdAt = now,
                updatedAt = now,
                sizeBytes = content.toByteArray().size,
                linkedPersonaIds = com.google.gson.Gson().toJson(linkedPersonaIds)
            )
            dao.insertDocument(entity)

            // Get current RAG settings
            val config = aiConfigRepository.getAIConfig()

            // Process document for RAG (chunk and embed)
            if (config.ragEnabled) {
                documentEmbeddingRepository.processDocument(
                    documentId = documentId,
                    documentName = name,
                    content = content,
                    chunkSize = config.ragChunkSize,
                    chunkOverlap = config.ragChunkOverlap
                )
            }
            Result.success(documentId)
        } catch (e : Exception) {
            Result.failure(e)
        }
    }


}