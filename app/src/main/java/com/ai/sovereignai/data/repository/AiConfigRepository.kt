package com.ai.sovereignai.data.repository

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences

import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

private val Context.aiConfigDataStore: DataStore<Preferences> by preferencesDataStore(name = "ai_config")

/**
 * Repository for AI configuration and user context
 */
@Singleton
class AiConfigRepository  @Inject constructor(
    @ApplicationContext private val context: Context,
    private  val promptTranslationManager:
)