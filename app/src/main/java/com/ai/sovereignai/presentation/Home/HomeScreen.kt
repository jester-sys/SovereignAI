package com.ai.sovereignai.presentation.Home

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
fun HomeScreen(
    onNavigateToChat :(String) -> Unit,
    onNavigateToVoiceChat :(String) -> Unit,
    onNavigateToSettings: () -> Unit,
    viewModel:ChatViewModel= hiltViewModel()
) {


}