package com.example.androidstarter.feature.hello.presentation

import com.example.androidstarter.feature.hello.domain.model.Message

/**
 * HelloState - Trạng thái UI duy nhất cho HelloScreen.
 *
 * Nguyên tắc Unidirectional Data Flow (UDF):
 *   ViewModel emit State -> UI consume State (read-only).
 *   UI không modify state trực tiếp, chỉ gửi event lên ViewModel.
 */
data class HelloState(
    val isLoading: Boolean = false,
    val message: Message? = null,
    val errorMessage: String? = null
) {
    // Computed property tiện cho UI check trạng thái
    val hasData: Boolean get() = message != null
}
