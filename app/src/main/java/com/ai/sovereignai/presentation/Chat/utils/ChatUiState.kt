package com.ai.sovereignai.presentation.Chat.utils

import com.ai.sovereignai.domain.model.LocalModel
import com.ai.sovereignai.domain.model.LocalModelInfo
import com.ai.sovereignai.domain.model.Message
import com.ai.sovereignai.domain.model.MessageRole

data class ChatUiState(
    val conversation: List<MessageRole.Conversation> = emptyList(),
    val currentConversationId: String? = null,
    val currentConversation: MessageRole.Conversation? = null,
    val messages: List<Message> = emptyList(),
    val localModels: Map<LocalModel, LocalModelInfo> = emptyMap(),
    val availableModels: List<ModelProvider> = emptyList(),


    )