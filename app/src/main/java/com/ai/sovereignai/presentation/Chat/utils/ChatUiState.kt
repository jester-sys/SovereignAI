package com.ai.sovereignai.presentation.Chat.utils

import android.content.Context
import android.net.Uri
import com.ai.sovereignai.data.repository.ConversationRepository
import com.ai.sovereignai.domain.model.AIConfig
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
    val conversation: List<MessageRole.Conversation> = emptyList(),
    val currentConversationId: String? = null,
    val currentConversation: MessageRole.Conversation? = null,
    val messages: List<Message> = emptyList(),
    val localModels: Map<LocalModel, LocalModelInfo> = emptyMap(),
    val availableModels: List<ModelProvider> = emptyList(),
    val selectedModel: ModelProvider? =null,
    val pinnedModel: Set<String> = emptySet(),
    val aiConfig: AIConfig = AIConfig(),
    val userContext: UserContext = UserContext(),
    val systemPrompt: List<SystemPrompt> = emptyList(),
    val personas: Map<String, Persona> = emptyMap(),
    val selecredSystemPromptId: String? = null,
    val activePersona: Persona? = null,
    val isLoading: Boolean = false,
    val streamingMessage: Message? = null,
    val shouldScrollToBottom: Boolean = false,
    val isDrawerOpen: Boolean = false,
    val showEditTitleDialog: Boolean = false,
    val isSearchOpen: Boolean = false,
    val showSystemPromptDialog: Boolean = false,
    val showExportDialog: Boolean = false,
    val showImportDialog: Boolean = false,
    val showDeleteDialog: Boolean = false,
    val showErrorDialog: Boolean = false,
    val errorDetails : ErrorDetails? = null,
    val showModelLoadErrorDialog: Boolean = false,
    val modelLoadErrorDetails: ErrorDetails? = null,
    val selectedMessageLogs: String? = null,
    val exportedChatText: String? = null,
    val importErrorMessage: String? = null,
    val importedConversationId: String? = null,
    val searchQuery:String = "",
    val currentSearchIndex: Int = 0,
    val searchMatchCount: Int = 0,
    val inputText: String = "",
    val replyToMessage: Message? = null,
    val isInitialConversationsLoad: Boolean = false,
    val showSourceChatDialog: Boolean = false,
    val selectedNewChatPersonaId: String? = null,
    val isListening: Boolean = false,
    val attachedImages: List<Uri> = emptyList(),
    val attachedFiles: List<Uri> = emptyList(),
    val isExporting: Boolean = false,
    val exportProgress:Float = 0f,
    val exportProgressMessage:String = "",
    val searchStatusMessage: Int = 0,
    val totalDocumentCount: Int = 0,
    val totalMemoriesCount : Int = 0


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