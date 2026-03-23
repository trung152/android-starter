package com.example.androidstarter.feature.hello.data.di

import com.example.androidstarter.feature.hello.data.repository.HelloRepositoryImpl
import com.example.androidstarter.feature.hello.domain.repository.HelloRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * HelloModule - Nói cho Hilt biết: "Khi ai đó cần HelloRepository, hãy dùng HelloRepositoryImpl".
 *
 * Dùng @Binds (hiệu quả hơn @Provides) khi chỉ cần ánh xạ Interface -> Implementation.
 * @InstallIn(SingletonComponent::class): Repository là singleton, tránh tạo nhiều instance.
 */
@Module
@InstallIn(SingletonComponent::class)
abstract class HelloModule {

    @Binds
    @Singleton
    abstract fun bindHelloRepository(
        impl: HelloRepositoryImpl
    ): HelloRepository
}
