package com.example.androidstarter.feature.hello.presentation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.androidstarter.core.designsystem.theme.AppTheme

/**
 * HelloScreen - Composable UI cho feature Hello World.
 *
 * Nguyên tắc: Screen chỉ đọc State từ ViewModel và gọi callback.
 * Không có business logic nào trong Composable.
 *
 * Luồng khi User nhấn Button:
 * [Button.onClick] --> [viewModel.loadGreeting()] --> [GetGreetingUseCase]
 *     --> [HelloRepositoryImpl] --> Result<Message>
 *     --> [_uiState.update] --> [uiState] (StateFlow)
 *     --> [collectAsStateWithLifecycle] --> re-compose [HelloScreen]
 */
@Composable
fun HelloScreen(
    viewModel: HelloViewModel = hiltViewModel()
) {
    // collectAsStateWithLifecycle: lifecycle-aware, tự pause khi app vào background
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    HelloContent(
        state = state,
        onLoadClick = viewModel::loadGreeting
    )
}

/**
 * HelloContent - Tách UI khỏi ViewModel để dễ Preview và test.
 */
@Composable
private fun HelloContent(
    state: HelloState,
    onLoadClick: () -> Unit
) {
    Scaffold { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier.padding(24.dp)
            ) {
                // Loading indicator
                if (state.isLoading) {
                    CircularProgressIndicator()
                }

                // Hiển thị message từ Domain
                if (state.hasData) {
                    Text(
                        text = state.message!!.content,
                        style = MaterialTheme.typography.headlineMedium,
                        textAlign = TextAlign.Center,
                        color = MaterialTheme.colorScheme.primary
                    )
                }

                // Hiển thị lỗi nếu có
                state.errorMessage?.let { error ->
                    Text(
                        text = error,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.error
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Trigger UseCase từ UI
                Button(onClick = onLoadClick) {
                    Text(text = if (state.hasData) "Reload" else "Get Greeting")
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun HelloContentPreview() {
    AppTheme {
        HelloContent(
            state = HelloState(message = com.example.androidstarter.feature.hello.domain.model.Message("Hello from Clean Architecture! 🚀")),
            onLoadClick = {}
        )
    }
}
