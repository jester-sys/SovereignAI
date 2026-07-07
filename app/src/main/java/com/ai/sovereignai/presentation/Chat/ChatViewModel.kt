package com.ai.sovereignai.presentation.Chat

import android.content.Context
import androidx.lifecycle.ViewModel
import com.ai.sovereignai.data.preferences.SettingsManager
import com.ai.sovereignai.data.repository.ApiKeyRepository
import com.ai.sovereignai.data.repository.ConversationRepository
import com.ai.sovereignai.data.repository.KnowledgeDocumentRepository
import com.ai.sovereignai.data.repository.LocalModelRepository
import com.ai.sovereignai.data.repository.MemoryRepository
import com.ai.sovereignai.data.repository.MessageRepository
import com.ai.sovereignai.data.repository.PersonaRepository
import com.ai.sovereignai.data.repository.SystemPromptRepository
import com.ai.sovereignai.domain.service.KeyboardSoundManager
import com.ai.sovereignai.domain.service.LlamaService
import com.yourown.ai.data.repository.AiConfigRepository
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class ChatViewModel @Inject constructor(
    @ApplicationContext private  val  context: Context,
    private  val  conversationRepository: ConversationRepository,
    private  val messageRepository : MessageRepository,
    private  val localModelRepository: LocalModelRepository,
    private  val apiKeyRepository: ApiKeyRepository,
    private  val aiConfigRepository: AiConfigRepository,
    private val systemPromptRepository : SystemPromptRepository,
    private val personaRepository : PersonaRepository,
    private val settingsManager: SettingsManager,
    private val llamaService: LlamaService,
    private val keyboardSoundManager: KeyboardSoundManager,
    private  val memoryRepository : MemoryRepository,
    private val knowledgeDocumentRepository : KnowledgeDocumentRepository,
    // New managers
    private val conversationManager : ChatConversationManager,
    private  val messageHandler : ChatMessageHandler,
    private  val importExportManager : ChatImportExportManager

    ) : ViewModel() {

    }