package com.ai.sovereignai.domain.model

import com.ai.sovereignai.presentation.Chat.utils.PromptType

data class SystemPrompt(
    val id: String,
    val name: String,
    val content: String,
    val type: PromptType,
    val isDefault: Boolean,
    val createdAt: Long,
    val updateAt: Long,
    val usageCount: Int
)