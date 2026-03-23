# Clean Architecture Rules — Quy tắc luồng phụ thuộc

## Sơ đồ tổng quát

```
┌──────────────────────────────────────────────────────────┐
│                    PRESENTATION                          │
│  (ViewModel, UiState, Composable Screen)                │
│          ↓ chỉ biết về UseCase                          │
├──────────────────────────────────────────────────────────┤
│                      DOMAIN                             │
│  (UseCase, Repository Interface, Model)                 │
│          ↑ được implement bởi Data                      │
├──────────────────────────────────────────────────────────┤
│                       DATA                              │
│  (RepositoryImpl, API Service, DAO, Mapper)             │
└──────────────────────────────────────────────────────────┘
```

**Dependency Rule (bất biến):**
> Các tầng bên trong (Domain) KHÔNG BAO GIỜ biết đến các tầng bên ngoài (Data, Presentation).
> Phụ thuộc chỉ được đi vào trong: `Presentation → Domain ← Data`

---

## Quy tắc từng tầng

### 🟢 Domain Layer — Vùng lõi sạch nhất

**Được phép:**
- Pure Kotlin classes (data class, sealed class, interface, object)
- `suspend fun` và `Flow<T>`
- Không có **bất kỳ** import nào từ `android.*`, `androidx.*`, `retrofit2.*`, `room.*`

**Bắt buộc:**
- Mỗi UseCase là một class riêng, chỉ làm **một việc duy nhất**
- Dùng `operator fun invoke()` để gọi UseCase như một function
- Repository phải là **interface**, không phải concrete class

**Template UseCase:**
```kotlin
class GetSomethingUseCase @Inject constructor(
    private val repository: SomethingRepository // interface
) {
    suspend operator fun invoke(id: String): Result<Something> = runCatching {
        repository.getSomething(id)
    }
}
```

---

### 🔵 Data Layer — Nguồn dữ liệu cụ thể

**Được phép:**
- Import từ `retrofit2.*`, `androidx.room.*`, `okhttp3.*`
- Data Transfer Objects (DTO), Entity, Mapper
- Cụ thể hóa (implement) Repository interface từ Domain

**Cấm:**
- Import bất kỳ gì từ tầng Presentation (ViewModel, State...)
- Trả về DTO/Entity thô lên Domain — phải map sang Domain Model

**Template RepositoryImpl:**
```kotlin
class SomethingRepositoryImpl @Inject constructor(
    private val apiService: SomethingApiService, // Retrofit
    private val dao: SomethingDao,               // Room
) : SomethingRepository {  // ← implement interface từ Domain

    override suspend fun getSomething(id: String): Something {
        return apiService.getSomething(id).toDomainModel() // map DTO → Domain
    }
}
```

**Hilt binding (bắt buộc dùng `@Binds`):**
```kotlin
@Module
@InstallIn(SingletonComponent::class)
abstract class SomethingModule {
    @Binds @Singleton
    abstract fun bindSomethingRepository(impl: SomethingRepositoryImpl): SomethingRepository
}
```

---

### 🟡 Presentation Layer — UI và State

**Được phép:**
- Import `androidx.*`, `dagger.hilt.*`, Compose dependencies
- Gọi UseCase, KHÔNG gọi Repository trực tiếp
- `StateFlow`, `viewModelScope`

**Cấm:**
- Gọi `repository.xxx()` trực tiếp từ ViewModel (phải qua UseCase)
- Business logic trong Composable — chỉ render state
- `MutableStateFlow` expose ra ngoài ViewModel (chỉ expose `StateFlow`)

**Template ViewModel:**
```kotlin
@HiltViewModel
class SomethingViewModel @Inject constructor(
    private val getSomethingUseCase: GetSomethingUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(SomethingState())
    val uiState: StateFlow<SomethingState> = _uiState.asStateFlow()

    fun loadData(id: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            getSomethingUseCase(id)
                .onSuccess { data -> _uiState.update { it.copy(isLoading = false, data = data) } }
                .onFailure { err -> _uiState.update { it.copy(isLoading = false, error = err.message) } }
        }
    }
}
```

---

## Kiểm tra vi phạm (Checklist trước khi commit)

- [ ] File trong `domain/` có import `android.*` hoặc `androidx.*` không? → **Loại bỏ**
- [ ] ViewModel có gọi repository trực tiếp không? → **Phải qua UseCase**
- [ ] `MutableStateFlow` có bị expose ra `public` không? → **Chỉ expose `StateFlow`**
- [ ] DTO/Entity có được trả thẳng lên Domain không? → **Phải có Mapper**
- [ ] Hilt module có dùng `@Provides` cho interface binding không? → **Đổi sang `@Binds`**
