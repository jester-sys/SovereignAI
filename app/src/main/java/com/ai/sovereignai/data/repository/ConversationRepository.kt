package com.ai.sovereignai.data.repository

import androidx.compose.foundation.text.input.rememberTextFieldState
import com.ai.sovereignai.data.local.dao.ConversationDao
import com.ai.sovereignai.data.local.dao.MessageDao
import com.ai.sovereignai.domain.model.MessageRole
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class ConversationRepository @Inject constructor(
    private val conversationDao: ConversationDao,
    private val messageDao: MessageDao
) {
    /**
     * Get all non-archived conversations with their messages
     */
    fun getAllConversations(): Flow<List<MessageRole.Conversation>> {

        return conversationDao.getAllConversations().map { conversations ->
            conversations.map { entity ->
                val messages = messageDao.getMessagesByConversation(entity.id)
                    .map { messageEntity ->
                        messageEntity.map { it.toDomain() }
                    }
            }
        }

    }
}

