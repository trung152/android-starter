---
name: scaffold_feature
description: Kỹ năng tạo cấu trúc 3 tầng Clean Architecture cho một feature mới bất kỳ. Dùng khi user yêu cầu "tạo feature X" hoặc "thêm màn hình Y".
---

# Skill: Scaffold New Feature

## Khi nào dùng Skill này?

Khi user yêu cầu tạo một tính năng mới, ví dụ:
- "Tạo feature đăng nhập"
- "Thêm màn hình danh sách sản phẩm"
- "Build tính năng profile người dùng"

## Thông tin cần thu thập từ User (bắt buộc hỏi trước)

1. **Tên feature** (ví dụ: `login`, `product_list`, `user_profile`)
2. **Domain Model chính** (tên + các fields): ví dụ `User(id: String, name: String, email: String)`
3. **Nguồn dữ liệu**: Remote API / Room Database / Cả hai (offline-first)?
4. **Các actions của User** trên màn hình: Load data? Submit form? Navigate?

---

## Các bước thực hiện

### Bước 1: Tạo cấu trúc thư mục

```
app/src/main/java/com/example/androidstarter/feature/{featureName}/
├── domain/
│   ├── model/
│   │   └── {Model}.kt
│   ├── repository/
│   │   └── {Feature}Repository.kt       ← Interface
│   └── usecase/
│       ├── Get{Feature}UseCase.kt
│       └── (các usecase khác nếu cần)
├── data/
│   ├── di/
│   │   └── {Feature}Module.kt           ← @Binds
│   ├── remote/                          ← (nếu có API)
│   │   ├── {Feature}ApiService.kt       ← Retrofit @GET/@POST
│   │   └── dto/
│   │       └── {Model}Dto.kt
│   ├── local/                           ← (nếu có Room)
│   │   └── {Model}Dao.kt
│   └── repository/
│       └── {Feature}RepositoryImpl.kt
└── presentation/
    ├── {Feature}State.kt
    ├── {Feature}ViewModel.kt
    └── {Feature}Screen.kt
```

---

### Bước 2: Tạo Domain Layer

#### `{Model}.kt` — Pure Kotlin, không import Android
```kotlin
package com.example.androidstarter.feature.{featureName}.domain.model

data class {Model}(
    val id: String,
    // ... các fields theo yêu cầu user
)
```

#### `{Feature}Repository.kt` — Interface thuần
```kotlin
package com.example.androidstarter.feature.{featureName}.domain.repository

interface {Feature}Repository {
    suspend fun get{Feature}(id: String): {Model}
    // Thêm các phương thức khác nếu cần
}
```

#### `Get{Feature}UseCase.kt`
```kotlin
package com.example.androidstarter.feature.{featureName}.domain.usecase

class Get{Feature}UseCase @Inject constructor(
    private val repository: {Feature}Repository
) {
    suspend operator fun invoke(id: String): Result<{Model}> = runCatching {
        repository.get{Feature}(id)
    }
}
```

---

### Bước 3: Tạo Data Layer

#### `{Feature}RepositoryImpl.kt`
```kotlin
class {Feature}RepositoryImpl @Inject constructor(
    // private val apiService: {Feature}ApiService,  // uncomment nếu có API
    // private val dao: {Model}Dao,                  // uncomment nếu có Room
) : {Feature}Repository {

    override suspend fun get{Feature}(id: String): {Model} {
        // TODO: thay mock bằng apiService.get{Feature}(id).toDomainModel()
        return {Model}(id = id, /* ... mock data */)
    }
}
```

#### `{Feature}Module.kt`
```kotlin
@Module
@InstallIn(SingletonComponent::class)
abstract class {Feature}Module {
    @Binds
    @Singleton
    abstract fun bind{Feature}Repository(impl: {Feature}RepositoryImpl): {Feature}Repository
}
```

---

### Bước 4: Tạo Presentation Layer

#### `{Feature}State.kt`
```kotlin
data class {Feature}State(
    val isLoading: Boolean = false,
    val data: {Model}? = null,
    val errorMessage: String? = null
)
```

#### `{Feature}ViewModel.kt`
```kotlin
@HiltViewModel
class {Feature}ViewModel @Inject constructor(
    private val get{Feature}UseCase: Get{Feature}UseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow({Feature}State())
    val uiState: StateFlow<{Feature}State> = _uiState.asStateFlow()

    fun load(id: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            get{Feature}UseCase(id)
                .onSuccess { data -> _uiState.update { it.copy(isLoading = false, data = data) } }
                .onFailure { err -> _uiState.update { it.copy(isLoading = false, errorMessage = err.message) } }
        }
    }
}
```

#### `{Feature}Screen.kt`
```kotlin
@Composable
fun {Feature}Screen(viewModel: {Feature}ViewModel = hiltViewModel()) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    {Feature}Content(state = state, onLoad = { viewModel.load("id") })
}

@Composable
internal fun {Feature}Content(state: {Feature}State, onLoad: () -> Unit) {
    // TODO: Build Compose UI tại đây
    Scaffold { padding ->
        Box(Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
            when {
                state.isLoading -> CircularProgressIndicator()
                state.data != null -> Text(text = state.data.toString())
                state.errorMessage != null -> Text(text = state.errorMessage, color = MaterialTheme.colorScheme.error)
            }
            Button(onClick = onLoad) { Text("Load") }
        }
    }
}

@Preview @Composable
private fun {Feature}ContentPreview() {
    AppTheme { {Feature}Content(state = {Feature}State(), onLoad = {}) }
}
```

---

### Bước 5: Đăng ký Navigation

Mở `core/navigation/AppNavigation.kt`, thêm:
```kotlin
// Trong sealed class Screen:
data object {Feature} : Screen("{featureName}")

// Trong NavHost:
composable(Screen.{Feature}.route) {
    {Feature}Screen()
}
```

---

## Checklist hoàn thành

- [ ] Không có import `android.*` trong tầng `domain/`
- [ ] `MutableStateFlow` chỉ là `private`
- [ ] Hilt module dùng `@Binds` (không dùng `@Provides`)
- [ ] Có `@Preview` composable
- [ ] Route đã được thêm vào `AppNavigation.kt`
- [ ] File mới đã được thêm vào Version Catalog nếu có dependency mới
