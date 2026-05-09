package com.ai.sovereignai.data.local.dao

import androidx.room3.Query
import androidx.room3.vo.Dao
import com.ai.sovereignai.domain.model.MessageRole
import kotlinx.coroutines.flow.Flow

@androidx.room3.Dao
interface ConversationDao {
    @Query("SELECT * FROM conversations WHERE isArchived = 0 ORDER BY isPinned DESC, updatedAt DESC")
    fun getAllConversations(): Flow<List<ConversationEntity>>




}