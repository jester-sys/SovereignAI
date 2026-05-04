package com.ai.sovereignai.presentation.Chat.utils

import com.ai.sovereignai.domain.model.MessageRole

data class ChatUiState(
    val conversation: List<MessageRole.Conversation> = emptyList(),

    )