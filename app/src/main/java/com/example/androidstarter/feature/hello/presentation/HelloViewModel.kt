package com.example.androidstarter.feature.hello.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.androidstarter.feature.hello.domain.usecase.GetGreetingUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * HelloViewModel - Cầu nối giữa Domain và UI.
 *
 * Luồng dữ liệu (Data Flow):
 * [HelloScreen] --trigger--> [HelloViewModel.loadGreeting()]
 *      --> [GetGreetingUseCase.invoke()]
 *      --> [HelloRepositoryImpl.getGreeting()]
 *      --> emit Result<Message> ngược lên
 * [HelloViewModel] update [_uiState]
 * [HelloScreen] observe [uiState] và re-compose
 *
 * @HiltViewModel: Hilt tự inject GetGreetingUseCase vào đây.
 */
@HiltViewModel
class HelloViewModel @Inject constructor(
    private val getGreetingUseCase: GetGreetingUseCase
) : ViewModel() {

    // Bên trong ViewModel: MutableStateFlow để có thể update
    private val _uiState = MutableStateFlow(HelloState())

    // Expose ra ngoài: StateFlow bất biến (read-only cho UI)
    val uiState: StateFlow<HelloState> = _uiState.asStateFlow()

    fun loadGreeting() {
        viewModelScope.launch {
            // 1. Báo UI đang loading
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }

            // 2. Gọi UseCase (suspend fun, chạy trên coroutine)
            getGreetingUseCase()
                .onSuccess { message ->
                    // 3a. Thành công: cập nhật message, tắt loading
                    _uiState.update { it.copy(isLoading = false, message = message) }
                }
                .onFailure { error ->
                    // 3b. Thất bại: hiển thị lỗi, tắt loading
                    _uiState.update {
                        it.copy(isLoading = false, errorMessage = error.message ?: "Unknown error")
                    }
                }
        }
    }
}
