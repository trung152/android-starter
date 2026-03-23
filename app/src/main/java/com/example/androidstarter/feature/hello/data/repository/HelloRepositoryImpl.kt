package com.example.androidstarter.feature.hello.data.repository

import com.example.androidstarter.feature.hello.domain.model.Message
import com.example.androidstarter.feature.hello.domain.repository.HelloRepository
import javax.inject.Inject

/**
 * HelloRepositoryImpl - Tầng Data: Implement contract từ Domain.
 *
 * Trong thực tế, đây là nơi gọi:
 *   - Retrofit API (remote data source)
 *   - Room DAO (local data source)
 *   - và merge kết quả (offline-first strategy)
 *
 * Hiện tại: mock data để chứng minh luồng hoạt động.
 *
 * @Inject constructor: Hilt inject các dependency (API, DAO...) vào đây.
 */
class HelloRepositoryImpl @Inject constructor(
    // Khi có API thật, inject thêm: private val apiService: HelloApiService,
    // Khi có Room, inject thêm: private val helloDao: HelloDao,
) : HelloRepository {

    override suspend fun getGreeting(): Message {
        // TODO: Thay bằng apiService.getGreeting() hoặc helloDao.getGreeting()
        return Message(content = "Hello from Clean Architecture! 🚀")
    }
}
