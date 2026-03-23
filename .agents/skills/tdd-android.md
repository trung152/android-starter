---
name: tdd-android
description: Test-Driven Development cho Android. Dùng khi viết UseCase, ViewModel, Repository, hoặc sửa bug. RED → GREEN → REFACTOR. Không có exception.
---

# Skill: Android TDD — Test-Driven Development

## The Iron Law

```
KHÔNG VIẾT PRODUCTION CODE NÀO TRƯỚC KHI CÓ FAILING TEST
```

Viết code trước rồi mới test? **Xóa đi. Bắt đầu lại.**

Không exception:
- Không giữ lại làm "reference"
- Không "adapt" trong khi viết test
- Không nhìn lại nó
- XÓA nghĩa là XÓA

---

## Khi nào dùng

**Luôn luôn:**
- Viết UseCase mới
- Viết ViewModel mới
- Viết RepositoryImpl mới
- Sửa bất kỳ bug nào

**Ngoại lệ (hỏi user trước):**
- Throwaway prototype
- Composable UI thuần túy (visual-only, không có logic)

---

## RED → GREEN → REFACTOR

### 🔴 RED — Viết test thất bại trước

**Layer ưu tiên test:**
- **UseCase** → JUnit + MockK (thuần Kotlin, nhanh nhất)
- **ViewModel** → JUnit + MockK + `TestCoroutineDispatcher`
- **Repository** → JUnit + MockK mock API/DAO

**Cấu trúc test chuẩn cho UseCase:**
```kotlin
// src/test/java/com/example/.../GetGreetingUseCaseTest.kt
@OptIn(ExperimentalCoroutinesApi::class)
class GetGreetingUseCaseTest {

    // MockK mock — mock interface, không phải concrete class
    private val repository: HelloRepository = mockk()
    private val useCase = GetGreetingUseCase(repository)

    @Test
    fun `invoke returns message from repository`() = runTest {
        // GIVEN — setup mock behavior
        val expected = Message(content = "Hello!")
        coEvery { repository.getGreeting() } returns expected

        // WHEN — gọi UseCase
        val result = useCase()

        // THEN — verify kết quả
        assertTrue(result.isSuccess)
        assertEquals(expected, result.getOrNull())
    }

    @Test
    fun `invoke returns failure when repository throws`() = runTest {
        // GIVEN
        coEvery { repository.getGreeting() } throws RuntimeException("Network error")

        // WHEN
        val result = useCase()

        // THEN
        assertTrue(result.isFailure)
    }
}
```

**Cấu trúc test chuẩn cho ViewModel:**
```kotlin
class HelloViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule() // xem phần setup bên dưới

    private val getGreetingUseCase: GetGreetingUseCase = mockk()
    private lateinit var viewModel: HelloViewModel

    @BeforeEach
    fun setup() {
        viewModel = HelloViewModel(getGreetingUseCase)
    }

    @Test
    fun `loadGreeting updates state to success`() = runTest {
        // GIVEN
        val message = Message("Hello!")
        coEvery { getGreetingUseCase() } returns Result.success(message)

        // WHEN
        viewModel.loadGreeting()
        advanceUntilIdle()

        // THEN
        val state = viewModel.uiState.value
        assertFalse(state.isLoading)
        assertEquals(message, state.message)
        assertNull(state.errorMessage)
    }
}
```

**Naming convention cho test:**
```kotlin
// Dùng backtick để tên test dễ đọc như câu tiếng Anh
@Test
fun `khi X thì kết quả là Y`() { }

// Pattern: [method/action]_[condition]_[expected result]
// Ví dụ:
fun `loadGreeting updates state to loading then success`()
fun `invoke returns failure when repository throws`()
```

#### ✅ Verify RED — Chạy test và xác nhận nó THẤT BẠI

```bash
# Trong Android Studio: Run test class
# Hoặc qua Gradle (từ root project):
./gradlew :app:testDebugUnitTest --tests "*.HelloUseCaseTest"
```

Xác nhận:
- Test **fail** (không phải compile error)
- Thông báo lỗi có nghĩa (feature chưa tồn tại, không phải typo)
- **Test pass ngay?** → Bạn đang test behavior đã có. Viết lại test.

---

### 🟢 GREEN — Code tối giản nhất để pass

Viết code **ít nhất** có thể để test pass.
- Không thêm feature chưa có test
- Không refactor code khác
- Không "cải thiện" ngoài scope test

```kotlin
// ✅ Đúng — chỉ đủ để pass
class GetGreetingUseCase @Inject constructor(
    private val repository: HelloRepository
) {
    suspend operator fun invoke(): Result<Message> = runCatching {
        repository.getGreeting()
    }
}

// ❌ Sai — over-engineer trước khi có test cho feature đó
class GetGreetingUseCase @Inject constructor(
    private val repository: HelloRepository,
    private val analyticsTracker: AnalyticsTracker, // YAGNI — chưa có test
    private val cacheManager: CacheManager,         // YAGNI
) { ... }
```

#### ✅ Verify GREEN — Chạy và xác nhận PASS

```bash
./gradlew :app:testDebugUnitTest --tests "*.HelloUseCaseTest"
```

Xác nhận:
- Test target **pass**
- **Tất cả** test khác vẫn pass (không regression)
- Output sạch (không có warning compilation)

---

### 🔵 REFACTOR — Dọn dẹp, giữ green

Chỉ refactor **sau khi** đã green:
- Bỏ duplication
- Đặt tên cho dễ hiểu
- Extract helper nhỏ

**Không được:** thêm behavior mới trong bước này.

Sau mỗi thay đổi nhỏ, chạy lại test để đảm bảo vẫn green.

---

## Setup Testing Dependencies

Thêm vào `app/build.gradle.kts`:
```kotlin
testImplementation("io.mockk:mockk:1.13.10")
testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.9.0")
testImplementation("junit:junit:4.13.2")
```

**MainDispatcherRule** (tạo 1 lần, dùng cho mọi ViewModel test):
```kotlin
// src/test/java/com/example/androidstarter/util/MainDispatcherRule.kt
class MainDispatcherRule(
    val testDispatcher: TestCoroutineDispatcher = TestCoroutineDispatcher()
) : TestWatcher() {
    override fun starting(description: Description) {
        Dispatchers.setMain(testDispatcher)
    }
    override fun finished(description: Description) {
        Dispatchers.resetMain()
        testDispatcher.cleanupTestCoroutines()
    }
}
```

---

## Checklist trước khi commit

- [ ] Test được viết TRƯỚC production code
- [ ] Đã chạy và xác nhận test FAIL (RED)
- [ ] Đã chạy và xác nhận test PASS (GREEN)
- [ ] Tất cả test khác vẫn pass (không regression)
- [ ] Refactor xong, vẫn green
- [ ] Tên test mô tả rõ behavior (không phải tên method)
