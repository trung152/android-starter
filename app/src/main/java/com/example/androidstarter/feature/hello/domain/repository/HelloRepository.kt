package com.example.androidstarter.feature.hello.domain.repository

import com.example.androidstarter.feature.hello.domain.model.Message

/**
 * HelloRepository - Contract (Interface) của tầng Domain.
 *
 * Domain KHÔNG biết nguồn dữ liệu đến từ đâu (API, Room, mock...).
 * Tầng Data sẽ implement interface này.
 * Dependency Rule: Data -> Domain (một chiều).
 */
interface HelloRepository {
    suspend fun getGreeting(): Message
}
