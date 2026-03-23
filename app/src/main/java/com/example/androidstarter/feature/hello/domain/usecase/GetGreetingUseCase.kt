package com.example.androidstarter.feature.hello.domain.usecase

import com.example.androidstarter.feature.hello.domain.model.Message
import com.example.androidstarter.feature.hello.domain.repository.HelloRepository
import javax.inject.Inject

/**
 * GetGreetingUseCase - Đóng gói một business logic duy nhất.
 *
 * Luồng dữ liệu:
 * [HelloRepositoryImpl (Data)] --> [GetGreetingUseCase (Domain)] --> [HelloViewModel (Presentation)]
 *
 * UseCase gọi repository thông qua interface, không biết implementation cụ thể.
 * ViewModel gọi UseCase, không biết repository hay data source.
 *
 * @Inject constructor: Hilt tự động inject HelloRepository vào đây.
 */
class GetGreetingUseCase @Inject constructor(
    private val repository: HelloRepository
) {
    /**
     * Sử dụng suspend fun + operator invoke() để gọi: useCase() thay vì useCase.execute()
     */
    suspend operator fun invoke(): Result<Message> = runCatching {
        repository.getGreeting()
    }
}
