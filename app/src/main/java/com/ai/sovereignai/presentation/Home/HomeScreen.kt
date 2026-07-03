package com.ai.sovereignai.presentation.Home

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import com.ai.sovereignai.presentation.Chat.ChatViewModel

@Composable
fun HomeScreen(
    onNavigateToChat :(String) -> Unit,
    onNavigateToVoiceChat :(String) -> Unit,
    onNavigateToSettings: () -> Unit,
    viewModel: ChatViewModel = hiltViewModel()
) {


}