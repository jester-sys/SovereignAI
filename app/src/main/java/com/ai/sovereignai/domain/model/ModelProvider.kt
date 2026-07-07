package com.ai.sovereignai.domain.model

sealed class ModelProvider {
    /**
     * Local on-device model
     */
    data class Local(val model: LocalModel) : ModelProvider()
    /**
     * API-based model
     */

    data class API(
        val provider: AIProvider,
        val modelId: String,
        val displayName: String
    ): ModelProvider()

    /**
     * Get unique key for model identification (used for pinning, etc.)
     */
    fun getModelKey(): String = when(this){
        is Local -> "local:${model.name}"
        is API -> "api:${provider.name}:${modelId}"
    }

}



/**
 * Available Deepseek models
 */
enum class DeepseekModel(
    val modelId: String,
    val displayName: String,
    val description: String
) {
    DEEPSEEK_CHAT(
        modelId = "deepseek-chat",
        displayName = "DeepSeek Chat (V3.2)",
        description = "Non-thinking mode - fast and efficient"
    ),
    DEEPSEEK_REASONER(
        modelId = "deepseek-reasoner",
        displayName = "DeepSeek Reasoner (V3.2)",
        description = "Thinking mode - deeper reasoning"
    );

    fun toModelProvider(): ModelProvider.API {
        return ModelProvider.API(
            provider = AIProvider.DEEPSEEK,
            modelId = modelId,
            displayName = displayName
        )
    }
}

/**
 * Available OpenAI models
 */
enum class OpenAIModel(
    val modelId: String,
    val displayName: String,
    val description: String
){
    // GPT-5 Series (Latest)
    GPT_5_5(
        modelId = "gpt-5.5",
        displayName = "GPT-5.5",
        description = "Newest frontier model for complex coding and professional work"
    ),
    GPT_5_4(
        modelId = "gpt-5.4",
        displayName = "GPT-5.4",
        description = "Most capable model for complex professional work"
    ),
    GPT_5_2(
        modelId = "gpt-5.2",
        displayName = "GPT-5.2",
        description = "Best for coding and agentic tasks"
    ),
    GPT_5_1(
        modelId = "gpt-5.1",
        displayName = "GPT-5.1",
        description = "Coding with configurable reasoning effort"
    ),

    // GPT-4.1 Series
    GPT_4_1(
        modelId = "gpt-4.1-2025-04-14",
        displayName = "GPT-4.1",
        description = "Strong, intelligent, base"
    ),

    // GPT-4o Series
    GPT_4O(
        modelId = "gpt-4o-2024-08-06",
        displayName = "GPT-4o",
        description = "Fast, intelligent, flexible"
    ),

    // o3 Series (Reasoning Models)
    O3(
        modelId = "o3",
        displayName = "o3",
        description = "Reasoning model for complex tasks"
    );

    fun toModelProvider(): ModelProvider.API{
        return ModelProvider.API(
            provider = AIProvider.OPENAI,
            modelId = modelId,
            displayName = displayName
        )
    }
}


/**
 * Available x.ai (Grok) models
 */
enum class XAIModel(
    val modelId: String,
    val displayName: String,
    val description: String
) {
    GROK_4_1_FAST_REASONING(
        modelId = "grok-4-1-fast-reasoning",
        displayName = "Grok 4.1 Fast Reasoning",
        description = "Fast reasoning with extended context"
    ),
    GROK_4_1_FAST_NON_REASONING(
        modelId = "grok-4-1-fast-non-reasoning",
        displayName = "Grok 4.1 Fast Non-Reasoning",
        description = "Fastest responses without reasoning"
    ),
    GROK_CODE_FAST_1(
        modelId = "grok-code-fast-1",
        displayName = "Grok Code Fast 1",
        description = "Optimized for code generation"
    ),
    GROK_4_FAST_REASONING(
        modelId = "grok-4-fast-reasoning",
        displayName = "Grok 4 Fast Reasoning",
        description = "Fast reasoning mode"
    ),
    GROK_4_FAST_NON_REASONING(
        modelId = "grok-4-fast-non-reasoning",
        displayName = "Grok 4 Fast Non-Reasoning",
        description = "Fast non-reasoning mode"
    ),
    GROK_4_0709(
        modelId = "grok-4-0709",
        displayName = "Grok 4 (0709)",
        description = "Stable snapshot from July 9"
    ),
    GROK_3_MINI(
        modelId = "grok-3-mini",
        displayName = "Grok 3 Mini",
        description = "Compact, efficient model"
    ),
    GROK_3(
        modelId = "grok-3",
        displayName = "Grok 3",
        description = "Full-featured Grok 3"
    );

    fun toModelProvider(): ModelProvider.API {
        return ModelProvider.API(
            provider = AIProvider.XAI,
            modelId = modelId,
            displayName = displayName
        )
    }
}

/**
 * Available OpenRouter models
 */
enum class OpenRouterModel(
    val modelId: String,
    val displayName: String,
    val description: String
) {
    // Claude 4.5 Series
    CLAUDE_SONNET_4_5(
        modelId = "anthropic/claude-sonnet-4.5",
        displayName = "Claude Sonnet 4.5",
        description = "Balanced performance and speed"
    ),
    CLAUDE_OPUS_4_6(
        modelId = "anthropic/claude-opus-4.6",
        displayName = "Claude Opus 4.6",
        description = "Most capable, best for complex tasks"
    ),
    CLAUDE_OPUS_4_7(
        modelId = "anthropic/claude-opus-4.7",
        displayName = "Claude Opus 4.7",
        description = "Next-generation Opus for long-running and asynchronous agents"
    ),
    CLAUDE_OPUS_4_5(
        modelId = "anthropic/claude-opus-4.5",
        displayName = "Claude Opus 4.5",
        description = "Most capable, best for complex tasks"
    ),
    CLAUDE_HAIKU_4_5(
        modelId = "anthropic/claude-haiku-4.5",
        displayName = "Claude Haiku 4.5",
        description = "Fast and efficient"
    ),

    // Claude 4 & 3.x Series
    CLAUDE_SONNET_4(
        modelId = "anthropic/claude-sonnet-4",
        displayName = "Claude Sonnet 4",
        description = "Stable Claude 4 with vision support"
    ),
    CLAUDE_3_7_SONNET(
        modelId = "anthropic/claude-3.7-sonnet",
        displayName = "Claude 3.7 Sonnet",
        description = "Enhanced 3.5 with better reasoning"
    ),
    CLAUDE_3_5_HAIKU(
        modelId = "anthropic/claude-3.5-haiku",
        displayName = "Claude 3.5 Haiku",
        description = "Fast, efficient with vision"
    ),

    // Llama 4 Series
    LLAMA_4_MAVERICK(
        modelId = "meta-llama/llama-4-maverick",
        displayName = "Llama 4 Maverick",
        description = "Flagship Llama 4 model with advanced reasoning"
    ),
    LLAMA_4_SCOUT(
        modelId = "meta-llama/llama-4-scout",
        displayName = "Llama 4 Scout",
        description = "Efficient Llama 4 variant for fast inference"
    ),

    // Llama 3.1 Series
    LLAMA_3_1_EURUALE(
        modelId = "sao10k/l3.1-euryale-70b",
        displayName = "Llama 3.1-euryale",
        description = "Euryale L3.1 70B v2.2 focused on creative roleplay"
    ),

    // Gemini 3 Series (with reasoning tokens)
    GEMINI_3_PRO_PREVIEW(
        modelId = "google/gemini-3-pro-preview",
        displayName = "Gemini 3 Pro Preview",
        description = "Advanced reasoning with thinking capabilities"
    ),
    GEMINI_3_1_PRO_PREVIEW_CUSTOMTOOLS(
        modelId = "google/gemini-3.1-pro-preview-customtools",
        displayName = "Gemini 3.1 Pro Preview Custom Tools",
        description = "Gemini 3.1 Pro variant optimized for reliable tool selection"
    ),
    GEMINI_3_FLASH_PREVIEW(
        modelId = "google/gemini-3-flash-preview",
        displayName = "Gemini 3 Flash Preview",
        description = "Fast reasoning with preview features"
    ),
    GEMINI_3_1_FLASH_LITE_PREVIEW(
        modelId = "google/gemini-3.1-flash-lite-preview",
        displayName = "Gemini 3.1 Flash Lite Preview",
        description = "High-efficiency Gemini 3.1 model for fast, low-cost workloads"
    ),

    // Gemini 2.5 Series (multimodal support)
    GEMINI_2_5_PRO(
        modelId = "google/gemini-2.5-pro",
        displayName = "Gemini 2.5 Pro",
        description = "Multimodal: text, image, audio, video"
    ),
    GEMINI_2_5_FLASH(
        modelId = "google/gemini-2.5-flash",
        displayName = "Gemini 2.5 Flash",
        description = "Fast multimodal with 1M+ context"
    ),

    // Gemma Series
    GEMMA_4_31B_IT(
        modelId = "google/gemma-4-31b-it",
        displayName = "Gemma 4 31B",
        description = "Multimodal Gemma with text and image input"
    ),

    // Nous: Hermes
    NOUS_HERMES_3_70B(
        modelId = "nousresearch/hermes-3-llama-3.1-70b",
        displayName = "Nous: Hermes 3 70B",
        description = "Focused on aligning LLMs to the user, with powerful steering capabilities and control given to the end user."
    ),

    // Cohere: Command R+ (08-2024)
    COHERE_COMMAND_R(
        modelId = "cohere/command-r-plus-08-2024",
        displayName = "Cohere: Command R+",
        description = "It's useful for roleplay, general consumer usecases, and Retrieval Augmented Generation (RAG). 128,000 context."
    ),

    // Mistral Large
    MISTRAL_LARGE(
        modelId = "mistralai/mistral-large",
        displayName = "Mistral Large",
        description = "It's a proprietary weights-available model and excels at reasoning, code, JSON, chat, and more."
    ),

    // Qwen3 Max
    QWEN_3_MAX(
        modelId = "qwen/qwen3-max",
        displayName = "Qwen 3 max",
        description = "major improvements in reasoning, instruction following, multilingual support, and long-tail knowledge coverage compared to the January 2025 version."
    ),

    QWEN_3_5_PLUS(
        modelId = "qwen/qwen3.5-plus-02-15",
        displayName = "Qwen 3.5 plus",
        description = "major improvements in reasoning, instruction following, multilingual support, and long-tail knowledge coverage compared to the January 2025 version."
    ),

    QWEN_3_14(
        modelId = "qwen/qwen3-14b",
        displayName = "Qwen 3.14b",
        description = "major improvements in reasoning, instruction following, multilingual support, and long-tail knowledge coverage compared to the January 2025 version."
    ),
    QWEN_3_6_PLUS(
        modelId = "qwen/qwen3.6-plus",
        displayName = "Qwen 3.6 Plus",
        description = "Strong agentic coding model with multimodal capabilities and 1M context"
    ),


    // GLM5
    GLM_5(
        modelId = "z-ai/glm-5",
        displayName = "GLM 5",
        description = "complex systems design and long-horizon agent workflows"
    ),
    GLM_5_1(
        modelId = "z-ai/glm-5.1",
        displayName = "GLM 5.1",
        description = "Major leap in coding capability for long-horizon autonomous tasks"
    ),

    // Ernie 4.5
    ERNIE_4_5(
        modelId = "baidu/ernie-4.5-vl-424b-a47b",
        displayName = "Ernie 4.5",
        description = "high-fidelity cross-modal reasoning, image understanding, and long-context generation (up to 131k tokens)"
    ),

    // Ernie 4.5
    KIMI_K_2_5(
        modelId = "moonshotai/kimi-k2.5",
        displayName = "Kimi K 2.5",
        description = "general reasoning, visual coding, and agentic tool-calling."
    ),

    // MiMo Series
    MIMO_V2_5_PRO(
        modelId = "xiaomi/mimo-v2.5-pro",
        displayName = "MiMo V2.5 Pro",
        description = "Flagship Xiaomi model for agentic capabilities and software engineering"
    ),
    MIMO_V2_OMNI(
        modelId = "xiaomi/mimo-v2-omni",
        displayName = "MiMo V2 Omni",
        description = "Omni-modal model with image, video, and audio input"
    ),

    // DeepSeek Series
    DEEPSEEK_V4_PRO(
        modelId = "deepseek/deepseek-v4-pro",
        displayName = "DeepSeek V4 Pro",
        description = "Large-scale MoE model for advanced reasoning and long-horizon workflows"
    ),
    DEEPSEEK_V4_FLASH(
        modelId = "deepseek/deepseek-v4-flash",
        displayName = "DeepSeek V4 Flash",
        description = "Efficiency-optimized DeepSeek V4 for fast inference and coding"
    ),

    // Deepseek v3.1 terminus
    DEEPSEEK_K_2_5(
        modelId = "deepseek/deepseek-v3.1-terminus",
        displayName = "Deepseek v3.1 terminus",
        description = "general reasoning, visual coding, and agentic tool-calling."
    ),

    // nex-agi/deepseek-v3.1-nex-n1
    DEEPSEEK_V_3_1_NEX_N_1(
        modelId = "nex-agi/deepseek-v3.1-nex-n1",
        displayName = "Deepseek v3.1 nex n1",
        description = "general reasoning, visual coding, and agentic tool-calling."
    ),

    // OpenAI GPT-4o Series
    GPT_4O_EXTENDED(
        modelId = "openai/gpt-4o:extended",
        displayName = "GPT-4o Extended",
        description = "128K context, text only (no vision)"
    ),
    GPT_4O_2024_05_13(
        modelId = "openai/gpt-4o-2024-05-13",
        displayName = "GPT-4o (2024-05-13)",
        description = "Stable GPT-4o snapshot with vision"
    );

    fun toModelProvider(): ModelProvider.API {
        return ModelProvider.API(
            provider = AIProvider.OPENROUTER,
            modelId = modelId,
            displayName = displayName
        )
    }
}

fun ModelProvider.API.familyGroup(): String  = when (provider){
    AIProvider.OPENAI -> if (modelId.startsWith("o")) "OpenAI • o-series" else "OpenAI • GPT"
    AIProvider.DEEPSEEK -> "DeepSeek"
    AIProvider.XAI -> "xAI • Grok"
    AIProvider.OPENROUTER -> when{
        modelId.startsWith("anthropic/claude-") -> "Claude"
        modelId.startsWith("google/gemini-") -> "Gemini"
        modelId.startsWith("google/gemma") -> "Gemma"
        modelId.startsWith("deepseek/") || modelId.startsWith("nex-agi/deepseek") -> "DeepSeek"
        modelId.startsWith("openai/gpt-4o") -> "GPT-4o"
        modelId.startsWith("meta-llama/llama") -> "Llama"
        modelId.startsWith("qwen/") -> "Qwen"
        modelId.startsWith("z-ai/glm") -> "GLM"
       modelId.startsWith("baidu/ernie") -> "Ernie"
        modelId.startsWith("moonshotai/kimi") -> "Kimi"
        modelId.startsWith("xiaomi/mimo") -> "MiMo"
        modelId.startsWith("meta-llama/") || modelId.startsWith("sao10k/") || modelId.contains("llama") -> "Llama"
        modelId.startsWith("mistralai/") -> "Mistral"
        modelId.startsWith("cohere/") -> "Cohere"
        modelId.startsWith("nousresearch/") -> "Nous"
        else -> "Other"
    }
    else -> provider.displayName
}

fun ModelProvider.API.familySortOrder():Int  = when (familyGroup()){
    "OpenAI • GPT" -> 0
    "OpenAI • o-series" -> 1
    "xAI • Grok" -> 2
    "Claude" -> 3
    "Gemini" -> 4
    "Gemma" -> 5
    "DeepSeek" -> 6
    "Qwen" -> 7
    "GLM" -> 8
    "MiMo" ->9
    "Llama" -> 10
    "Kimi" ->11
    "Ernie" -> 12
    "Mistral" -> 13
    "Cohere" -> 14
    "Nous" ->15
    "Other" -> 16
    else -> 99




}



