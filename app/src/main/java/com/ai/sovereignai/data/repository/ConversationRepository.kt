package com.ai.sovereignai.data.repository

import androidx.compose.foundation.text.input.rememberTextFieldState
import com.ai.sovereignai.data.local.dao.ConversationDao
import com.ai.sovereignai.data.local.dao.MessageDao
import com.ai.sovereignai.data.local.entity.ConversationEntity
import com.ai.sovereignai.data.mapper.toDomain
import com.ai.sovereignai.data.mapper.toEntity
import com.ai.sovereignai.domain.model.Conversation
import com.ai.sovereignai.domain.model.MessageRole
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import java.util.UUID
import javax.inject.Inject
import kotlin.collections.map

class ConversationRepository @Inject constructor(
    private val conversationDao: ConversationDao,
    private val messageDao: MessageDao
) {
    /**
     * Get all non-archived conversations with their messages
     */
    fun getAllConversations(): Flow<List<Conversation>> {

        return conversationDao.getAllConversations().map { conversations ->
            conversations.map { entity ->
                val messages = messageDao.getMessagesByConversation(entity.id)
                    .map { messageEntity ->
                        messageEntity.map { it.toDomain() }
                    }
                entity.toDomain(emptyList())
            }
        }

    }

    /**
     * Get conversation by ID with messages
     */

    fun getConversationById(id: String): Flow<Conversation?> {
        return combine(
            conversationDao.observeConversationById(id),
            messageDao.getMessagesByConversation(id)
        ) { conversation, messages ->

            conversation?.toDomain(messages.map { it.toDomain() })

        }
    }

    /**
     * Create new conversation
     */

    suspend fun  createConversation(
        title: String,
        systemPrompt: String,
        model: String,
        provider: String,
        systemPromptId: String? = null,
        sourceConversationId: String? = null,
    ): String {
        val id = UUID.randomUUID().toString()
        val now = System.currentTimeMillis()

        val conversation  = ConversationEntity(
            id = id,
            title = title,
            systemPrompt = systemPrompt,
            systemPromptId = systemPromptId,
            model = model,
            provider = provider,
            createAt = now,
            updateAt = now,
            sourceConversationId = sourceConversationId
        )

        conversationDao.insertConversation(conversation)
        return id
    }

    /**
     * Update conversation
     */

    suspend fun  updateConversation(conversation: Conversation){
        conversationDao.updateConversation(conversation.toEntity())
    }

    /**
     * Update conversation title
     */

    suspend fun  updateConversationTitle(id: String, title: String){

        val conversation = conversationDao.getConversationById(id)
        conversation?.let {
            conversationDao.updateConversation(
                it.copy(
                    title = title,
                    updateAt = System.currentTimeMillis()
                )
            )
        }
    }

    /**
     * Update conversation model
     */

    suspend fun  updateConversationModel(id: String,model: String,provider: String){
        val conversation = conversationDao.getConversationById(id)
        conversation?.let {
            conversationDao.updateConversation(
                it.copy(
                    model = model,
                    provider = provider,
                    updateAt = System.currentTimeMillis()
                )
            )
        }
    }

    /**
     * Update conversation system prompt
     */

    suspend fun  updateConversationSystemPrompt(id: String, systemPrompt: String, systemPromptId: String?) {
        val conversation = conversationDao.getConversationById(id)
        conversation?.let {
            conversationDao.updateConversation(
                it.copy(
                    systemPrompt = systemPrompt,
                    systemPromptId = systemPromptId,
                    updateAt = System.currentTimeMillis()
                )
            )
        }

    }
    suspend fun  updateConversationPersona(id: String, personaId: String?) {

        val conversation = conversationDao.getConversationById(id)
        conversation?.let {
            conversationDao.updateConversation(
                it.copy(
                    personaId = personaId,
                    updateAt = System.currentTimeMillis()
                )
            )
        }
    }


    /**
     * Update web search enabled state for conversation
     */

    suspend fun updateWebSearchEnabled(id: String, enabled: Boolean){
        val conversation = conversationDao.getConversationById(id)
        conversation?.let {
            conversationDao.updateConversation(
                it.copy(
                    webSearchEnabled = enabled,
                    updateAt = System.currentTimeMillis()
                )
            )
        }
    }

    /**
     * Update X search enabled state for conversation (xAI Grok only)
     */

    suspend fun  updateXSearchEnabled(id: String, enabled: Boolean){
        val conversation = conversationDao.getConversationById(id)
        conversation?.let {
            conversationDao.updateConversation(
                it.copy(
                    xSearchEnabled = enabled,
                    updateAt = System.currentTimeMillis()
                )
            )
        }

    }
    /**
     * Update conversation's updatedAt timestamp
     * Called when a new message is added to keep conversations sorted by last activity
     */

    suspend fun updateConversationTimestamp(id: String){
        val conversation = conversationDao.getConversationById(id)
        conversation?.let {
            conversationDao.updateConversation(
                it.copy(
                    updateAt = System.currentTimeMillis()
                )
            )
        }

    }


    /**
     * Delete conversation
     */

    suspend fun  deleteConversation(id: String){
        val conversation = conversationDao.getConversationById(id)
        conversation?.let {
            messageDao.deleteMessagesByConversation(id)
            conversationDao.deleteConversation(it)
        }
    }
    /**
     * Pin/unpin conversation
     */
    suspend fun setPinned(id: String, isPinned: Boolean){
        conversationDao.setPinned(id, isPinned)
    }

    /**
     * Archive conversation
     */
    suspend fun  setArchived(id: String, isArchived: Boolean){
        conversationDao.setArchived(id, isArchived)
    }

    /**
     * Get next conversation number for auto-naming (Chat 1, Chat 2, etc)
     */
    suspend fun  getNextConversationNumber(): Int{
        // Get all conversations and find the highest number
        val conversation = conversationDao.getAllConversations()
        // This is a simplified version - in real app would need better logic
        return 1 //
    }

    /**
     * Upsert conversation (for cloud sync)
     * Uses UPDATE for existing records to avoid REPLACE which triggers CASCADE DELETE
     * on messages and memories (ForeignKey onDelete = CASCADE)
     */

    suspend fun  upsertConversation(conversation: ConversationEntity) : Unit = withContext(
        Dispatchers.IO){
        val existing  = conversationDao.getConversationById(conversation.id)
        if(existing != null){
            // UPDATE existing: preserve local-only fields not synced to cloud
            conversationDao.updateConversation(
                conversation.copy(
                    isPinned = existing.isPinned,
                    systemPrompt = existing.systemPrompt,
                    systemPromptId = existing.systemPromptId,
                    webSearchEnabled = existing.webSearchEnabled,
                    xSearchEnabled = existing.xSearchEnabled,

                )
            )
        }else{
            // INSERT new record (no CASCADE risk since it's a new row)
            conversationDao.insertConversation(conversation)
        }
    }

    /**
     * Upsert multiple conversations (for cloud sync)
     */

    suspend fun upsertConversations(conversation: ConversationEntity) : Unit = withContext(
        Dispatchers.IO){
        val existing  = conversationDao.getConversationById(conversation.id)

        if(existing != null){
            // UPDATE existing: preserve local-only fields not synced to cloud
            conversationDao.updateConversation(
                conversation.copy(
                    isPinned = existing.isPinned,
                    systemPrompt = existing.systemPrompt,
                    systemPromptId = existing.systemPromptId,
                    webSearchEnabled = existing.webSearchEnabled,
                    xSearchEnabled = existing.xSearchEnabled,
                )
            )
        }
        else {
            // INSERT new record (no CASCADE risk since it's a new row)
            conversationDao.insertConversation(conversation)
        }
    }

    /**
     * Upsert multiple conversations (for cloud sync)
     */

    suspend fun upsertConversations(conversations: List<ConversationEntity>) : Unit = withContext(
        Dispatchers.IO){
        conversations.forEach { conversation ->
            upsertConversation(conversation)
        }
    }

    /**
     * Get conversation by ID synchronously (for local sync server)
     */

    suspend fun getConversationByIdSync(id: String): Conversation? = withContext(Dispatchers.IO){
        val entity = conversationDao.getConversationById(id)
        val messages = messageDao.getMessagesByConversationSync(id).map {
            it.toDomain()
        }
        entity?.toDomain(messages)
    }

}


