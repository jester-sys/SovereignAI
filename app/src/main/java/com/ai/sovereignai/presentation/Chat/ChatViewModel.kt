package com.ai.sovereignai.presentation.Chat

import android.content.Context
import com.ai.sovereignai.data.repository.ApiKeyRepository
import com.ai.sovereignai.data.repository.ConversationRepository
import com.ai.sovereignai.data.repository.LocalModelRepository
import com.ai.sovereignai.data.repository.MessageRepository
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class ChatViewModel @Inject constructor(
    @ApplicationContext private  val  context: Context,
    private  val  conversationRepository: ConversationRepository,
    private  val messageRepository : MessageRepository,
    private  val localModelRepository: LocalModelRepository,
    private  val apiKeyRepository: ApiKeyRepository

    )