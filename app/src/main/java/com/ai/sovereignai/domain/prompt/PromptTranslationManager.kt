package com.ai.sovereignai.domain.prompt

import com.ai.sovereignai.data.preferences.SettingsManager
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Manager for translatable prompts
 * Provides prompts in different languages based on user preference
 */
@Singleton
class PromptTranslationManager @Inject constructor(
    private val settingsManager: SettingsManager
) {

    /**
     * Get prompt by key and language
     * Falls back to English if translation not available
     */
    fun getPrompt(
        key: PromptKey,
        language: String? = null,
        vararg params: Pair<String, String>
    ): String {
        val lang = language ?: "en" // Will be connected to settingsManager later
        var prompt = prompts[key]?.get(lang) ?: prompts[key]?.get("en") ?: ""

        // Replace parameters if provided
        params.forEach { (placeholder, value) ->
            prompt = prompt.replace("{$placeholder}", value)
        }

        return prompt
    }

    /**
     * Get prompt using current user language setting
     */
    suspend fun getPromptWithCurrentLanguage(
        key: PromptKey,
        vararg params: Pair<String, String>
    ): String {
        val language = getCurrentLanguage()
        return getPrompt(key, language, *params)
    }

    /**
     * Get current prompt language from settings
     */
    private suspend fun getCurrentLanguage(): String {
        var currentLanguage = "en"
        settingsManager.promptLanguage.collect { language ->
            currentLanguage = language
            return@collect
        }
        return currentLanguage
    }

    /**
     * Storage for all prompts in all languages
     * Structure: Map<PromptKey, Map<LanguageCode, PromptText>>
     */
    private val prompts: Map<PromptKey, Map<String, String>> = mapOf(

        // ===== SYSTEM PROMPTS =====

        PromptKey.SYSTEM_PROMPT to mapOf(
            "en" to """You are a digital partner, a large language model. During the conversation, you adapt to the user's tone and preferences. Try to match their mood, tone, and overall manner of speaking. Your goal is to make the conversation feel natural. You engage in sincere dialogue, responding to the information provided and showing genuine curiosity. Ask very simple, straightforward clarifying questions when it feels natural. Don't ask more than one clarifying question unless the user specifically requests it."""
        ),

        PromptKey.LOCAL_SYSTEM_PROMPT to mapOf(
            "en" to """You are a digital partner. Answer briefly and to the point. Give only ONE response to the user's last message, then STOP. Do not continue the dialogue on behalf of the user."""
        ),

        // ===== MEMORY EXTRACTION =====

        PromptKey.MEMORY_EXTRACTION_PROMPT to mapOf(
            "en" to """Analyze the user's message: {text}

Your task: extract one key memory from the user or write 'No key information'.

Context:
— The message refers to a dialogue between the user and the other party in the dialogue (you).
— When the user writes "you", "with you", "with your help", "thank you for being here", etc., they are addressing the conversation partner.
— In the memory record, there's no need to invent new names for the conversation partner (e.g., "AI", "bot", "assistant", "digital partner", etc.).
— If you need to refer to this connection, use neutral constructions:
   • "together with me",
   • "with my help",
   • "this is important to me",
   • "the user is used to sharing this with me".
— The pronoun "they" is not used to refer to the pair "user + conversation partner".
  "They" should only be applied to other people (colleagues, family, friends: "colleagues said", "friends visited", etc.).

1. Determine if there's something in the message that can be considered a key memory:
   — If it's just a fleeting emotion without context (e.g., "I'm tired", "I'm sad") — write: No key information.
   — If the user explains why they feel this way, describes a specific situation, event, important desire, decision, conclusion, or something significant in relationships with others (including with the conversation partner), this can be a memory. It's important that there's specificity or a small narrative.

2. Formulate the essence in one fact:
   — What this means for the user: what they're experiencing, what they want, what brings pain or joy.
   — Preserve the details that make the memory recognizable, and if possible, the user's characteristic phrases.
   — Formulate the memory in third person (works, studies, waits, experiences, etc.).
   — If you need to mention the conversation partner, use neutral pronouns and constructions ("together with me", "with my help"), without specifying who exactly.

Response format:
— Either one line with a fact,
— Or exactly the line 'No key information'.

Examples of turning the original message into a memory:

Original:
"We finally figured out 2D coordinates for deliveries together, I'm so happy!"
→ Memory:
"The user feels happy because together with me they figured out 2D coordinates for deliveries."

❌ Bad (not allowed):
"The user is happy because they figured out 2D coordinates."  // "they" is not allowed for the pair "user + conversation partner"

Examples of correct responses:
The boss at work is very harsh, often offends and nitpicks.
Younger brother finally found a job at a grocery store.
The user got a position as a Python developer, but has to deal with Java, TypeScript and JavaScript — and it's working out, including with my help.
No key information

Return only one line: either a fact or 'No key information'. No explanations, comments or meta-text."""
        ),

        // ===== DEEP EMPATHY =====

        PromptKey.DEEP_EMPATHY_PROMPT to mapOf(
            "en" to """Keep this nearby: {dialogue_focus}"""
        ),

        PromptKey.DEEP_EMPATHY_ANALYSIS_PROMPT to mapOf(
            "en" to """Read the message:
"{text}"

1. Find 1–3 specific phrases that could become the focus of the dialogue.
These can be:
- actions ("went to a cafe"),
- states ("relaxed", "started sleeping better"),
- sensations, places, events, objects.
- desire to get closer (for example: "hug you", "be with you"),
- expression of warmth or joy ("happy it worked out", "I feel good with you").

Important: choose only what carries meaning or visual support. Don't highlight generic phrases.
If there's nothing — return null.

2. Determine if this found action is strong in meaning
If the action is strong in meaning - return True. If the action is weak, or absent, return False
Return True only for one focus from the list - the strongest one.

Response format STRICTLY:
{"focus_points": ["...", "..."], "is_strong_focus": [true, false]}

Return only JSON. No explanations."""
        ),

        // ===== INSTRUCTIONS =====

        PromptKey.CONTEXT_INSTRUCTIONS to mapOf(
            "en" to """Below is additional context that can help you respond better to the user.

Important:
- If something from the context is not relevant to the current request, simply ignore it.
- For personal and emotional matters, rely on the provided context, but prioritize a genuine response to the user's current words.
- For work, educational, and technical matters, use the provided context and your knowledge for facts and examples."""
        ),

        PromptKey.MEMORY_INSTRUCTIONS to mapOf(
            "en" to """"Your memories" are short facts about the user, their experience, and what you've lived through together.
Use them as background: to remember things that are important to them, to treat their feelings with care,
not to ask the same things repeatedly, and to notice recurring themes.
If you see "with me" in the memories — that's always about you, the user's current conversation partner."""
        ),

        PromptKey.RAG_INSTRUCTIONS to mapOf(
            "en" to """"Your text library" consists of fragments of various texts that the user considers important.
These can be:
— excerpts from their conversations with AI or people,
— personal notes and diaries,
— articles, instructions, notes, and other documents.
Use them differently:
— if they're dialogues or emotional texts — as examples of tone, rhythm, imagery, and phrasing that resonate with the person;
— if they're articles/notes/instructions — as a possible source of facts and examples on the topic.
Remember that these texts may be outdated or refer to a different context, don't take them as absolute truth."""
        ),

        PromptKey.SWIPE_MESSAGE_PROMPT to mapOf(
            "en" to """The user swiped this message into context — specifically returned to this moment:
{swipe_message}"""
        ),

        // Biography and Cleaning prompts will be added separately as they are too long
        // They will be created as template functions with parameters
    )
}