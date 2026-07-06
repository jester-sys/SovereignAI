package com.ai.sovereignai.data.repository

import com.ai.sovereignai.data.local.YourOwnAIDatabase
import com.yourown.ai.data.repository.AiConfigRepository
import javax.inject.Inject


/**
 * Repository for managing knowledge documents
 */
class KnowledgeDocumentRepository @Inject constructor(
    private  val database : YourOwnAIDatabase,
    private  val documentEmbeddingRepository : DocumentEmbeddingRepository,
    private  val aiConfigRepository: AiConfigRepository

) {
}