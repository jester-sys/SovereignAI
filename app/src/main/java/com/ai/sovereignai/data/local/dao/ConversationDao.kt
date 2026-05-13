package com.ai.sovereignai.data.local.dao

import androidx.room3.Delete
import androidx.room3.Insert
import androidx.room3.Query
import androidx.room3.Update
import androidx.room3.Dao
import androidx.room3.OnConflictStrategy
import com.ai.sovereignai.data.local.entity.ApiKeyEntity
import com.ai.sovereignai.data.local.entity.ConversationEntity
import com.ai.sovereignai.data.local.entity.DocumentEntity
import com.ai.sovereignai.data.local.entity.MessageEntity
import com.ai.sovereignai.data.local.entity.UsageStatsEntity
import com.ai.sovereignai.domain.model.MessageRole
import kotlinx.coroutines.flow.Flow

@Dao
interface ConversationDao {
    @Query("SELECT * FROM conversations WHERE isArchived = 0 ORDER BY isPinned DESC, updatedAt DESC")
    fun getAllConversations(): Flow<List<ConversationEntity>>

    @Query("SELECT * FROM conversations WHERE id = :id")
    suspend fun getConversationById(id: String): ConversationEntity?

    @Query("SELECT * FROM conversations WHERE id = :id")
    fun observeConversationById(id: String): Flow<ConversationEntity?>


    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertConversation(conversation: ConversationEntity)

    @Update
    suspend fun updateConversation(conversation: ConversationEntity)

    @Delete
    suspend fun deleteConversation(conversation: ConversationEntity)

    @Query("UPDATE conversations SET isPinned = :isPinned WHERE id = :id")
    suspend fun setPinned(id: String, isPinned: Boolean)

    @Query("UPDATE conversations SET isArchived = :isArchived WHERE id = :id")
    suspend fun setArchived(id: String, isArchived: Boolean)

}

@Dao
interface MessageDao{
    @Query("SELECT * FROM messages ORDER BY createdAt ASC")
    fun getAllMessages(): Flow<List<MessageEntity>>

    @Query("SELECT * FROM messages WHERE conversationId = :conversationId ORDER BY createdAt ASC")
    suspend fun getMessagesByConversation(conversationId: String): Flow<List<MessageEntity>>

    @Query("SELECT * FROM messages WHERE conversationId = :conversationId ORDER BY createdAt ASC")
    suspend fun getMessagesByConversationSync(conversationId: String): List<MessageEntity>

    @Query("SELECT * FROM messages WHERE id = :id")
    suspend fun getMessageById(id: String): MessageEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMessage(message: MessageEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMessages(message: List<MessageEntity>)

    @Update
    suspend fun updateMessage(message: MessageEntity)

    @Delete
    suspend fun deleteMessage(message: MessageEntity)

    @Query("DELETE FROM messages WHERE conversationId = :conversationId")
    suspend fun deleteMessagesByConversation(conversationId: String)

    @Query("SELECT  SUM(tokenCount) FROM messages WHERE conversationId = :conversationId")
    suspend fun getConversationTokenByConversation(conversationId: String): Int?

}

@Dao
interface DocumentDao{
    @Query("SELECT * FROM documents ORDER BY uploadedAt DESC")
    fun getAllDocuments(): Flow<List<DocumentEntity>>

    @Query("SELECT * FROM documents WHERE id = :id")
    suspend fun getDocumentById(id: String): DocumentEntity?

    @Query("SELECT * FROM documents WHERE conversationId = :conversationId ")
    fun getDocumentsByConversation(conversationId: String): Flow<List<DocumentEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDocument(document: DocumentEntity)

    @Update
    suspend fun updateDocument(document: DocumentEntity)

    @Delete
    suspend fun deleteDocument(document: DocumentEntity)

    @Query("UPDATE documents SET isProcessed = :isProcessed, chunkCount = :chunkCount WHERE id = :id")
    suspend fun updateProcessingStatus(id: String, isProcessed: Boolean, chunkCount: Int)

}

@Dao
interface ApiKeyDao{
    @Query("SELECT * FROM api_keys WHERE isActive = 1 ORDER BY lastUsedAt DESC")
    fun getAllActiveApiKeys(): Flow<List<ApiKeyEntity>>

    @Query("SELECT * FROM api_keys WHERE provider = :provider")
    suspend fun getKeyByProvider(id: String): ApiKeyEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertApiKey(apiKey: ApiKeyEntity)

    @Update
    suspend fun updateApikey(apiKey: ApiKeyEntity)

    @Delete
    suspend fun deleteApiKey(apiKey: ApiKeyEntity)

    @Query("UPDATE api_keys SET lastUsedAt = :timestamp WHERE provider = :provider")
    suspend fun updateLastUsed(provider: String, timestamp: Long)

}

@Dao
interface UsageStatsDao{
    @Query("SELECT * FROM usage_stats WHERE date = :date ORDER BY provider, model")
    suspend fun getStatsBDate(date: String): List<UsageStatsEntity>

    @Query("SELECT * FROM usage_stats WHERE date BETWEEN :startDate AND :endDate ORDER BY date DESC")
    suspend fun getStatsByDateRange(startDate: String, endDate: String): List<UsageStatsEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertStats(stats: UsageStatsEntity)

    @Query("SELECT SUM(totalTokens) FROM usage_stats WHERE date = :date")
    suspend fun getTotalTokensByDate(date: String): Int?

    @Query("SELECT SUM(estimatedCost) FROM usage_stats WHERE date BETWEEN :startDate AND :endDate")
    suspend fun getTotalCostByDateRange(startDate: String, endDate: String): Double?


}


