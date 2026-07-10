package com.ai.sovereignai.presentation.Chat

import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ai.sovereignai.data.preferences.SettingsManager
import com.ai.sovereignai.data.repository.ApiKeyRepository
import com.ai.sovereignai.data.repository.ConversationRepository
import com.ai.sovereignai.data.repository.KnowledgeDocumentRepository
import com.ai.sovereignai.data.repository.LocalModelRepository
import com.ai.sovereignai.data.repository.MemoryRepository
import com.ai.sovereignai.data.repository.MessageRepository
import com.ai.sovereignai.data.repository.PersonaRepository
import com.ai.sovereignai.data.repository.SystemPromptRepository
import com.ai.sovereignai.domain.model.AIConfig
import com.ai.sovereignai.domain.model.AIProvider
import com.ai.sovereignai.domain.model.DeepseekModel
import com.ai.sovereignai.domain.model.DownloadStatus
import com.ai.sovereignai.domain.model.LocalModel
import com.ai.sovereignai.domain.model.Message
import com.ai.sovereignai.domain.model.MessageRole
import com.ai.sovereignai.domain.model.ModelCapabilities
import com.ai.sovereignai.domain.model.ModelProvider
import com.ai.sovereignai.domain.model.OpenAIModel
import com.ai.sovereignai.domain.model.OpenRouterModel
import com.ai.sovereignai.domain.model.XAIModel
import com.ai.sovereignai.domain.service.KeyboardSoundManager
import com.ai.sovereignai.domain.service.LlamaService
import com.ai.sovereignai.presentation.Chat.utils.ChatUiState
import com.ai.sovereignai.util.ImageCompressor
import com.yourown.ai.data.repository.AiConfigRepository
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.UUID
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

        private  val _uiState = MutableStateFlow(ChatUiState())
    val uiState: StateFlow<ChatUiState> = _uiState.asStateFlow()

    init {
        loadConversations()
        observeLocalModels()
        observeApiKeys()
        observeSettings()
        loadSavedModel()
        observeSystemPrompts()
        observePersonas()
        initializeDefaultPrompts()
        observePinnedModels()
        observeKeyboardSoundSettings()
        observeMemoriesAndDocuments()
    }

    private fun observeMemoriesAndDocuments() {
        viewModelScope.launch {
            memoryRepository.getAllMemoryEntities().let { memories ->
                _uiState.update { it.copy(totalMemoriesCount = memories.size) }

            }
        }
        viewModelScope.launch {
            knowledgeDocumentRepository.getAllDocuments().collect { documents ->
                _uiState.update { it.copy(totalDocumentsCount = documents.size) }
            }
        }
    }

    private fun observeKeyboardSoundSettings() {
       viewModelScope.launch {
           settingsManager.keyboardSoundVolume.collect { volume ->
               keyboardSoundManager.setSoundVolume(volume)
           }
       }
    }

    private fun observePinnedModels() {
        viewModelScope.launch {
            settingsManager.pinnedModels.collect { pinnedModels ->
                _uiState.update { it.copy(pinnedModels= pinnedModels) }
            }
        }
    }

    private fun initializeDefaultPrompts() {
       viewModelScope.launch {
           systemPromptRepository.initialDefaultPrompts()
       }
    }

    private fun observePersonas() {
        viewModelScope.launch {
            personaRepository.getAllPersonas().collect { personas ->
                val uniquePersonas = personas
                    .groupBy { it.systemPromptId }
                    .mapValues { (_, duplicates) ->
                        duplicates.maxByOrNull { it.updatedAt } ?: duplicates.first()
                    }
                    .values
                    .toList()

                val personaMap = uniquePersonas.associateBy { it.systemPromptId }
                _uiState.update { it.copy(personas = personaMap) }
            }
        }
    }

    private fun observeSystemPrompts() {
        viewModelScope.launch {
            systemPromptRepository.getAllPrompts().collect { prompts ->
                _uiState.update { it.copy(systemPrompts = prompts) }
            }
        }
    }

    private fun loadSavedModel() {
        viewModelScope.launch {
            settingsManager.selectedModel.collect { savedModel ->
                if(savedModel != null && _uiState.value.selectedModel == null){
                    // Try to restore saved model
                    val provider = when(savedModel.type){
                        "local" -> {
                            LocalModel.entries.find { it.name == savedModel.modelId }?.let {
                                ModelProvider.Local(it)
                            }
                        }

                        "api" -> {
                            when (savedModel.provider) {
                                "DEEPSEEK" -> DeepseekModel.entries.find { it.modelId == savedModel.modelId }?.toModelProvider()
                                "OPENAI" -> OpenAIModel.entries.find { it.modelId == savedModel.modelId }?.toModelProvider()
                                "XAI" -> XAIModel.entries.find { it.modelId == savedModel.modelId }?.toModelProvider()
                                "OPENROUTER" -> OpenRouterModel.entries.find { it.modelId == savedModel.modelId }?.toModelProvider()
                                else -> null
                            }
                        }
                        else  -> null
                    }
                    provider?.let {
                        _uiState.update { state -> state.copy(selectedModel = it) }
                        if(it is ModelProvider.Local){
                            loadModelInBackground(it.model)
                        }
                    }
                }
                else if (savedModel == null && _uiState.value.selectedModel == null) {
                    autoSelectFirstModel()
                }
            }
        }
    }

    private fun autoSelectFirstModel() {
        val firstDownloaded = _uiState.value.localModels.entries.firstOrNull{
            it.value.status is DownloadStatus.Downloaded
        }
        if(firstDownloaded != null){
            val provider = ModelProvider.Local(firstDownloaded.key)
            _uiState.update { it.copy(selectedModel = provider) }
            loadModelInBackground(firstDownloaded.key)
            return
        }
    }

    private fun observeSettings() {
        viewModelScope.launch {
            aiConfigRepository.aiConfig.collect { config ->
                _uiState.update { it.copy(aiConfig = config) }
            }
        }
        viewModelScope.launch {
            aiConfigRepository.userContext.collect { context ->
                _uiState.update { it.copy(userContext = context) }
            }
        }
    }

    private fun observeApiKeys() {
        viewModelScope.launch {
            apiKeyRepository.apiKeys.collect { _ ->
                updateAvailableModels()
            }
        }
    }

    private fun observeLocalModels() {
       viewModelScope.launch {
           localModelRepository.models.collect { models ->
               _uiState.update { it.copy(localModels = models) }
               updateAvailableModels()
           }
       }
    }

    private fun updateAvailableModels() {
       val models = mutableListOf<ModelProvider>()

        // Add ALL local models
        _uiState.value.localModels.forEach { (model, _) ->
            models.add(ModelProvider.Local(model))
        }
        // Add API models if keys are set
        if (apiKeyRepository.hasApiKey(AIProvider.DEEPSEEK)){
            DeepseekModel.entries.forEach { models.add(it.toModelProvider()) }
        }

        if (apiKeyRepository.hasApiKey(AIProvider.OPENAI)){
            OpenAIModel.entries.forEach { models.add(it.toModelProvider()) }
        }
        if (apiKeyRepository.hasApiKey(AIProvider.XAI)) {
            XAIModel.entries.forEach { models.add(it.toModelProvider()) }
        }
        if (apiKeyRepository.hasApiKey(AIProvider.OPENROUTER)) {
            OpenRouterModel.entries.forEach { models.add(it.toModelProvider()) }
        }

        _uiState.update { it.copy(availableModels = models) }
    }

    private fun loadConversations() {
        viewModelScope.launch {
            conversationRepository.getAllConversations().collect { conversations ->
                val isInitial = _uiState.value.isInitialConversationsLoad

                // Sort conversations by timestamp (newest first)
                val sortedConversations = conversations.sortedByDescending { it.updatedAt }

                _uiState.update { it.copy(
                    conversations = sortedConversations,
                    isInitialConversationsLoad = false
                ) }

                // Only auto-select on FIRST load when no conversation is selected
                if(isInitial && _uiState.value.currentConversationId == null  && sortedConversations.isNotEmpty()){
                    selectConversation(sortedConversations.first().id)
                }

            }
        }
    }

    private fun selectConversation(conversationId: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(currentConversationId =  conversationId) }

            conversationRepository.getConversationById(conversationId)
                .distinctUntilChanged()
                .collect { conversation ->
                    _uiState.update {
                        it.copy(
                            currentConversation =   conversation,
                            messages = conversation?.message ?: emptyList()
                        )
                    }

                    // Restore model from conversation
                    conversation?.let { conv ->
                        if(conv.model !="No model selected" && conv.provider != "unknown") {
                            val restoredModel  = conversationManager.restoreModelFromConversation(conv.model, conv.provider)
                            if(restoredModel != null){
                                _uiState.update { it.copy(selectedModel =  restoredModel) }

                                if(restoredModel is ModelProvider.Local) {
                                    loadModelInBackground(restoredModel.model)
                                }
                            } else {
                                _uiState.update { it.copy(selectedModel = null) }
                            }
                        } else {
                            _uiState.update { it.copy(selectedModel = null) }
                        }

                        // Restore active Persona from conversation
                        if(conv.personalId != null){
                            val persona = personaRepository.getPersonaById(conv.personalId!!)
                            _uiState.update { it.copy(activePersona =  persona) }
                            Log.i("ChatViewModel", "Restored Persona: ${persona?.name}")
                        } else  {
                            _uiState.update { it.copy(activePersona = null) }
                        }

                    }
                }
        }
    }

    private fun loadModelInBackground(model: LocalModel) {
        viewModelScope.launch {
            Log.d("ChatViewModel", "Attempting to load model: ${model.displayName}")
            val result = llamaService.loadModel(model)
            result.onSuccess {
                Log.i("ChatViewModel", "Model loaded successfully: ${model.displayName}")
            }.onFailure {  error ->
                Log.e("ChatViewModel", "Failed to load model: ${error.message}", error)

                _uiState.update { it.copy(
                    showModelLoadErrorDialog = true,
                    modelLoadErrorMessage = error.message ?: "Unknown error loading model"
                ) }
            }
        }
    }

    /**
     * Get the effective AIConfig:
     * - If a Persona is active, use its configuration.
     * - Otherwise, use the global configuration.
     */
    private  fun getEffectiveConfig(): AIConfig {
        val activePersona = _uiState.value.activePersona
        val globalConfig = _uiState.value.aiConfig

        return  if(activePersona != null){
            AIConfig(
                temperature = activePersona.temperature,
                topP = activePersona.topP,
                maxTokens = activePersona.maxTokens,
                deepEmpathy = activePersona.deepEmpathy,
                memoryEnabled = activePersona.memoryEnabled,
                ragEnabled = activePersona.ragEnabled,
                messageHistoryLimit = activePersona.messageHistoryLimit,
                systemPrompt = activePersona.systemPrompt,
                deepEmpathyPrompt = activePersona.deepEmpathyPrompt,
                deepEmpathyAnalysisPrompt = activePersona.deepEmpathyAnalysisPrompt,
                memoryExtractionPrompt = activePersona.memoryExtractionPrompt,
                contextInstructions = activePersona.contextInstructions,
                memoryInstructions = activePersona.memoryInstructions,
                ragInstructions = activePersona.ragInstructions,
                swipeMessagePrompt = activePersona.swipeMessagePrompt,
                memoryLimit = activePersona.memoryLimit,
                memoryMinAgeDays = activePersona.memoryMinAgeDays,
                memoryTitle = activePersona.memoryTitle,
                ragChunkSize = activePersona.ragChunkSize,
                ragChunkOverlap = activePersona.ragChunkOverlap,
                ragChunkLimit = activePersona.ragChunkLimit,
                ragTitle = activePersona.ragTitle
            )
        } else {
            globalConfig
        }
    }
    fun togglePinnedModel(model:  ModelProvider){
        viewModelScope.launch {
            settingsManager.togglePinnedModel(model.getModelKey())
        }
    }
    fun toggleWebSearch(){
        val conversationId = _uiState.value.currentConversationId ?: return
        val currentState  = _uiState.value.currentConversation?.webSearchEnabled ?: false
        val newState = !currentState

        viewModelScope.launch {
            conversationRepository.updateWebSearchEnabled(conversationId, newState)

            // Show temporary banner
            val message = if(newState){
                "Web Search enabled - searching the internet"
            } else {
                "Web Search disabled"
            }
            _uiState.update { it.copy(searchStatusMessage = message) }
        }
    }

    fun toggleXSearch() {
        val conversationId = _uiState.value.currentConversationId ?:return
        val currentState = _uiState.value.currentConversation?.xSearchEnabled ?: false
        val newState = !currentState

        viewModelScope.launch {
            conversationRepository.updateXSearchEnabled(conversationId, newState)

            // Show temporary banner
            val message = if(newState){
                "𝕏 Search enabled - searching posts on X (Twitter)"
            } else {
                "𝕏 Search disabled"
            }
            _uiState.update { it.copy(searchStatusMessage =   message) }
        }
    }

    fun clearSearchStatusMessage() {
        _uiState.update { it.copy(searchStatusMessage = null) }
    }

    // ===== CONVERSATION MANAGEMENT =====

    fun showSourceChatDialog() {
        _uiState.update { it.copy(showSourceChatDialog = true, selectedSourceChatId = null, selectedNewChatPersonaId = null) }
    }

    fun hideSourceChatDialog() {
        _uiState.update { it.copy(showSourceChatDialog = false, selectedSourceChatId = null, selectedNewChatPersonaId = null) }
    }

    fun selectSourceChat(chatId: String?) {
        _uiState.update { it.copy(selectedSourceChatId = chatId) }
    }
    fun selectNewChatPersona(personaId: String?) {
        _uiState.update { it.copy(selectedNewChatPersonaId = personaId) }
    }

    suspend fun createNewConversation(sourceConversationId: String? = null): String {
        val selectedPersonaId = _uiState.value.selectedNewChatPersonaId

        val id = conversationManager.createNewConversation(
            conversationCount = _uiState.value.conversations.size,
            sourceConversationId = sourceConversationId
        )

        // If persona selected, apply it to the new conversation
        var personaModel: ModelProvider? = null
        if (selectedPersonaId != null) {
            val persona = personaRepository.getPersonaById(selectedPersonaId)
            if (persona != null) {
                conversationManager.updateConversationWithPersona(
                    conversationId = id,
                    systemPromptId = persona.systemPromptId,
                    systemPrompt = persona.systemPrompt,
                    personaId = persona.id
                )
                Log.d("ChatViewModel", "Applied persona '${persona.name}' to new conversation")

                // Restore preferred model from persona if set
                if (persona.preferredModelId != null && persona.preferredProvider != null) {
                    Log.d("ChatViewModel", "Attempting to restore model: modelId=${persona.preferredModelId}, provider=${persona.preferredProvider}")
                    Log.d("ChatViewModel", "Provider string length: ${persona.preferredProvider!!.length}, exact value: '${persona.preferredProvider}'")

                    personaModel = conversationManager.restoreModelFromConversation(
                        persona.preferredModelId!!,
                        persona.preferredProvider!!
                    )

                    if (personaModel != null) {
                        Log.d("ChatViewModel", "Successfully restored model from persona: ${persona.preferredModelId}")

                        // Save model to conversation immediately
                        val modelName = when (personaModel) {
                            is ModelProvider.Local -> personaModel.model.modelName
                            is ModelProvider.API -> personaModel.modelId
                        }
                        val providerName = when (personaModel) {
                            is ModelProvider.Local -> "local"
                            is ModelProvider.API -> personaModel.provider.displayName
                        }

                        conversationManager.updateConversationModel(
                            conversationId = id,
                            modelName = modelName,
                            providerName = providerName
                        )
                        Log.d("ChatViewModel", "Saved model to conversation: model=$modelName, provider=$providerName")
                    } else {
                        Log.w("ChatViewModel", "Failed to restore model from persona: modelId=${persona.preferredModelId}, provider='${persona.preferredProvider}'")
                    }
                } else {
                    Log.d("ChatViewModel", "Persona has no preferred model: modelId=${persona.preferredModelId}, provider=${persona.preferredProvider}")
                }
            }
        }

        selectConversation(id)

        // Set model from persona if available, otherwise clear selection
        _uiState.update { it.copy(selectedModel = personaModel) }

        // If it's a local model, load it in background
        if (personaModel is ModelProvider.Local) {
            loadModelInBackground(personaModel.model)
        }

        closeDrawer()
        return id
    }

    fun deleteConversation(conversationId : String){
        viewModelScope.launch {
            conversationManager.deleteConversation(conversationId)

            if(_uiState.value.currentConversationId == conversationId){
                val nextId = conversationManager.getNextConversationAfterDeletion(
                    conversationId,
                    _uiState.value.conversations
                )
                if(nextId !=null){
                    selectConversation(nextId)
                } else {
                    createNewConversation()
                }
            }
        }
    }
    fun showEditTitleDialog() {
        _uiState.update { it.copy(showEditTitleDialog = true) }
    }
    fun hideEditTitleDialog(){
        _uiState.update { it.copy(showEditTitleDialog = false) }
    }
    fun updateConversationTitle(title : String){
        viewModelScope.launch {
            _uiState.value.currentConversationId?.let { id ->
                conversationManager.updateConversationTitle(id, title)
                hideEditTitleDialog()
            }
        }
    }

    // ===== DRAWER =====
    fun openDrawer() {
        _uiState.update { it.copy(isDrawerOpen = true) }
    }
    fun closeDrawer() {
        _uiState.update { it.copy(isDrawerOpen = false) }
    }

    // ===== MODEL SELECTION =====
    fun selectModel(model: ModelProvider) {

        viewModelScope.launch {
            _uiState.value.currentConversationId?.let { conversationId ->
                val (modelName, providerName) = when (model) {
                    is ModelProvider.Local -> model.model.modelName to "local"
                    is ModelProvider.API -> model.modelId to model.provider.displayName
                }
                conversationManager.updateConversationModel(conversationId, modelName, providerName)
            }

            // Auto-set default system prompt
            when (model) {
                is ModelProvider.Local -> aiConfigRepository.updateSystemPrompt(AIConfig.DEFAULT_LOCAL_SYSTEM_PROMPT)
                is ModelProvider.API -> aiConfigRepository.updateSystemPrompt(AIConfig.DEFAULT_SYSTEM_PROMPT)
            }

            // Save as default
            when (model) {
                is ModelProvider.Local -> {
                    settingsManager.setSelectedModel("local", model.model.name)
                    loadModelInBackground(model.model)
                }
                is ModelProvider.API -> {
                    settingsManager.setSelectedModel("api", model.modelId, model.provider.name)
                }
            }
        }
    }
    fun downloadModel(model: LocalModel){
        viewModelScope.launch {
            localModelRepository.downloadModel(model)
        }
    }

    // ===== MESSAGES =====
    fun updateInputText(text: String) {
        _uiState.update { it.copy(inputText = text) }
    }
    fun setListeningState(isListening: Boolean) {
        _uiState.update { it.copy(isListening = isListening) }
    }

    fun setReplyToMessage(message: Message) {
        _uiState.update { it.copy(replyToMessage = message) }
    }
    fun clearReplyToMessage() {
        _uiState.update { it.copy(replyToMessage = null) }
    }
    fun addImage(uri : Uri){
        val currentImages = _uiState.value.attachedImages
        val selectedModel = _uiState.value.selectedModel ?: return

        // Check model capabilities
        val modelId = when(selectedModel){
            is ModelProvider.Local -> return // Local models don't support images
            is ModelProvider.API -> selectedModel.modelId
        }
        val capabilities = ModelCapabilities.forModel(modelId)
        val maxImages = capabilities.imageSupport?.maxImages ?: 0

        if(currentImages.size >= maxImages){
            Log.d("ChatViewModel", "Max images reached: $maxImages")
            return
        }

        _uiState.update { it.copy(attachedImages = currentImages + uri) }
    }
    fun removeImage(uri: Uri){
        val currentImages = _uiState.value.attachedImages
        _uiState.update { it.copy(attachedImages = currentImages -uri) }
    }
    fun clearImages() {
        _uiState.update { it.copy(attachedImages = emptyList()) }
    }
    fun addFile(uri : Uri){
        val currentFiles = _uiState.value.attachedFiles
        val selectedModel = _uiState.value.selectedModel ?: return

        // Check model capabilities
        val modelId = when(selectedModel){
            is ModelProvider.Local -> return // Local models don't support files
            is ModelProvider.API -> selectedModel.modelId
        }
        val capabilities = ModelCapabilities.forModel(modelId)
        val maxFiles = capabilities.documentSupport?.maxDocuments ?: 0

        if(currentFiles.size >= maxFiles){
            Log.d("ChatViewModel", "Max files reached: $maxFiles")
            return
        }
        _uiState.update { it.copy(attachedFiles = currentFiles + uri) }

        fun removeFile(uri : Uri){
            val currentFiles  = _uiState.value.attachedFiles
            _uiState.update { it.copy(attachedFiles = currentFiles - uri) }

        }
    }
    fun clearFiles() {
        _uiState.update { it.copy(attachedFiles = emptyList()) }
    }

    fun sendMessage(){
        val text = _uiState.value.inputText.trim()
        if(text.isEmpty()) return

        val conversationId = _uiState.value.currentConversationId ?:return
        val selectedModel = _uiState.value.attachedImages
        val attachedImages = _uiState.value.attachedImages
        val attachedFiles = _uiState.value.attachedFiles
        val replyMessage = _uiState.value.replyToMessage

        viewModelScope.launch {
            _uiState.update { state ->
                state.copy(
                    inputText = "",
                    replyToMessage = null,
                    attachedImages = emptyList(),
                    attachedFiles = emptyList(),
                    isLoading = true
                )
            }
            val userMessae = Message(
                id = UUID.randomUUID().toString(),
                conversationId = conversationId,
                role = MessageRole.USER,
                content = text,
                createdAt = System.currentTimeMillis(),
                swipeMessageId = replyMessage?.id,
                swipeMessageText = replyMessage?.content,
                imageAttachments = null,
                fileAttachments = null

            )
            val aiMessageId = UUID.randomUUID().toString()

            try {
                // Process images
                val imagePath = attachedImages.mapNotNull { uri ->
                    try {
                        ImageCompressor.saveCompressedImage(context, uri)
                    }catch (e: Exception) {
                        Log.e("ChatViewModel", "Error processing image", e)
                        null
                    }
                }
            } catch (e: Exception) {
                android.util.Log.e("ImageCompressor", "Error saving image", e)
                null
            }
        }
    }

}