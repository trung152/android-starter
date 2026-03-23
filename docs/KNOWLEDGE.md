# KNOWLEDGE.md — Kiến thức tích lũy về Project

> Ghi lại những bài học, gotchas, patterns đã phát hiện trong quá trình phát triển.
> AI phải đọc file này khi gặp vấn đề tương tự để không lặp lại lỗi.

---

## Android Gotchas đã gặp

*(Sẽ được cập nhật khi phát sinh)*

### Template
```markdown
### [Tên vấn đề]
**Ngày phát hiện:** YYYY-MM-DD  
**Triệu chứng:** {Lỗi/behavior trông như thế nào}  
**Root cause:** {Nguyên nhân thật sự}  
**Fix:** {Cách giải quyết}  
**Bài học:** {Rút ra điều gì}
```

---

## Patterns đã dùng thành công

### Pattern 1: Offline-First với Room + Retrofit

**Dùng khi:** Feature cần hiển thị data ngay (từ cache) và sync từ server background.

**Template:**
```kotlin
override fun getItemList(): Flow<List<Item>> =
    dao.getAll()
        .map { entities -> entities.map { it.toDomainModel() } }
        .onStart { refreshFromNetwork() }

private suspend fun refreshFromNetwork() = runCatching {
    val remote = apiService.getItems()
    dao.upsertAll(remote.map { it.toEntity() })
}
```

### Pattern 2: ViewModel với loading/success/error states

**Dùng khi:** Mọi API call hoặc DB operation.

```kotlin
viewModelScope.launch {
    _uiState.update { it.copy(isLoading = true, errorMessage = null) }
    useCase()
        .onSuccess { data -> _uiState.update { it.copy(isLoading = false, data = data) } }
        .onFailure { err -> _uiState.update { it.copy(isLoading = false, errorMessage = err.message) } }
}
```

### Pattern 3: Hilt @Binds cho Repository

**Dùng khi:** Cần bind interface với implementation.

```kotlin
// Luôn dùng @Binds (không phải @Provides) để Hilt không tạo wrapper objects
@Module @InstallIn(SingletonComponent::class)
abstract class FeatureModule {
    @Binds @Singleton
    abstract fun bindRepo(impl: RepoImpl): RepoInterface
}
```

---

## Dependency Versions hiện tại

> Cập nhật khi upgrade dependency

| Thư viện | Version | Notes |
|---|---|---|
| AGP | 8.13.2 | |
| Kotlin | 2.0.21 | |
| KSP | 2.0.21-1.0.30 | Phải match Kotlin version |
| Compose BOM | 2025.04.00 | |
| Hilt | 2.56 | |
| Retrofit | 2.11.0 | |
| OkHttp | 4.12.0 | |
| Room | 2.7.1 | |
| Navigation Compose | 2.9.0 | |
| Coroutines | 1.9.0 | |

**Lưu ý KSP version:** Format `{kotlin_version}-{ksp_version}`, ví dụ `2.0.21-1.0.30`.
Nếu upgrade Kotlin, phải upgrade KSP matching version theo https://github.com/google/ksp/releases

---

## Test Infrastructure

### Dependencies cần thêm cho Unit Test
```toml
# libs.versions.toml
mockk = "1.13.10"
coroutines-test = "1.9.0"  # dùng cùng version với coroutines

[libraries]
mockk = { group = "io.mockk", name = "mockk", version.ref = "mockk" }
kotlinx-coroutines-test = { group = "org.jetbrains.kotlinx", name = "kotlinx-coroutines-test", version.ref = "coroutines" }
```

```kotlin
// app/build.gradle.kts
testImplementation(libs.mockk)
testImplementation(libs.kotlinx.coroutines.test)
```

### MainDispatcherRule (tạo 1 lần, reuse)
Đặt tại: `app/src/test/java/com/example/androidstarter/util/MainDispatcherRule.kt`
Xem template trong `tdd-android.md`
