package com.ai.sovereignai.presentation.Chat.utils

import android.content.Context
import android.net.Uri
import com.ai.sovereignai.data.repository.ConversationRepository
import com.ai.sovereignai.domain.model.AIConfig
import com.ai.sovereignai.domain.model.Conversation
import com.ai.sovereignai.domain.model.LocalModel
import com.ai.sovereignai.domain.model.LocalModelInfo
import com.ai.sovereignai.domain.model.Message
import com.ai.sovereignai.domain.model.MessageRole
import com.ai.sovereignai.domain.model.ModelProvider
import com.ai.sovereignai.domain.model.Persona
import com.ai.sovereignai.domain.model.SystemPrompt
import com.ai.sovereignai.domain.model.UserContext
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

data class ChatUiState(
    val conversations: List<Conversation> = emptyList(),
    val currentConversationId: String? = null,
    val currentConversation: Conversation? = null,
    val messages: List<Message> = emptyList(),
    val localModels: Map<LocalModel, LocalModelInfo> = emptyMap(),
    val availableModels: List<ModelProvider> = emptyList(),
    val selectedModel: ModelProvider? = null,
    val pinnedModels: Set<String> = emptySet(),
    val aiConfig: AIConfig = AIConfig(),
    val userContext: UserContext = UserContext(),
    val systemPrompts: List<SystemPrompt> = emptyList(),
    val personas: Map<String, Persona> = emptyMap(), // Map<systemPromptId, Persona>
    val selectedSystemPromptId: String? = null,
    val activePersona: Persona? = null, // Активная Persona для текущего чата
    val isLoading: Boolean = false,
    val streamingMessage: Message? = null,
    val shouldScrollToBottom: Boolean = false,
    val isDrawerOpen: Boolean = false,
    val showEditTitleDialog: Boolean = false,
    val showRequestLogsDialog: Boolean = false,
    val isSearchMode: Boolean = false,
    val showSystemPromptDialog: Boolean = false,
    val showExportDialog: Boolean = false,
    val showImportDialog: Boolean = false,
    val showErrorDialog: Boolean = false,
    val errorDetails: ErrorDetails? = null,
    val showModelLoadErrorDialog: Boolean = false,
    val modelLoadErrorMessage: String? = null,
    val selectedMessageLogs: String? = null,
    val exportedChatText: String? = null,
    val importErrorMessage: String? = null,
    val importedConversationId: String? = null,
    val searchQuery: String = "",
    val currentSearchIndex: Int = 0,
    val searchMatchCount: Int = 0,
    val inputText: String = "",
    val replyToMessage: Message? = null,
    val isInitialConversationsLoad: Boolean = true,
    val showSourceChatDialog: Boolean = false,
    val selectedSourceChatId: String? = null,
    val selectedNewChatPersonaId: String? = null,
    val isListening: Boolean = false,
    val attachedImages: List<android.net.Uri> = emptyList(),
    val attachedFiles: List<android.net.Uri> = emptyList(),
    val isExporting: Boolean = false,
    val exportProgress: Float = 0f,
    val exportProgressMessage: String = "",
    val searchStatusMessage: String? = null,
    val totalMemoriesCount: Int = 0,
    val totalDocumentsCount: Int = 0
)

enum class PromptType(val value: String){
    API("api"),
    LOCAL("local");

    companion object{
        fun fromString(value: String) : PromptType{
            return when(value.lowercase()){
                "api" -> PromptType.API
                "local" -> PromptType.LOCAL
                else -> API
            }
        }
    }


}
/**
 * Details about an error that occurred during message generation
 */
data class ErrorDetails(
    val errorMessage: String,
    val userMessageId: String,
    val userMessageContent: String,
    val assistantMessageId: String,
    val modelName: String
)

@HiltViewModel
class ChatViewModel @Inject constructor(
    @param:ApplicationContext private  val context: Context,
    private  val conversationRepository: ConversationRepository,

    )