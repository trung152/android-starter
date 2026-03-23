# Android MAD Rules — Tiêu chuẩn bắt buộc cho dự án này

## ❌ Những điều TUYỆT ĐỐI CẤM

Các quy tắc sau là bất biến. Vi phạm bất kỳ quy tắc nào đều khiến code bị từ chối.

| Cấm | Thay thế bắt buộc |
|---|---|
| Java (`.java`) | 100% Kotlin (`.kt`) |
| XML Layouts (`res/layout/*.xml`) | Jetpack Compose Composables |
| `LiveData` | `StateFlow` / `Flow` |
| `SharedPreferences` | `DataStore` (Preferences hoặc Proto) |
| `AsyncTask`, Thread thủ công | Kotlin Coroutines + `viewModelScope` |
| RxJava / RxKotlin | Kotlin Flow |
| MVP / MVC | MVVM + Clean Architecture (UseCase, Repository) |
| `KAPT` | `KSP` (nhanh hơn, tiêu chuẩn mới) |
| Dependency version hardcode trong `build.gradle.kts` | Version Catalog `libs.versions.toml` |
| `findNavController()` (Fragment Nav) | Navigation Compose |

---

## ✅ Tiêu chuẩn BẮT BUỘC

### Ngôn ngữ & Build
- **Kotlin** phiên bản mới nhất trong `libs.versions.toml`
- **Gradle Kotlin DSL** (`.kts`) cho tất cả file build
- **Version Catalog** (`gradle/libs.versions.toml`) cho mọi dependency
- **KSP** cho code generation (Hilt, Room)

### UI
- **Jetpack Compose** + **Material 3** cho toàn bộ UI
- Mỗi màn hình phải có hàm `@Preview` riêng biệt
- Tách `Screen` (có ViewModel) khỏi `Content` (stateless, previewable):
  ```kotlin
  @Composable
  fun MyScreen(viewModel: MyViewModel = hiltViewModel()) { ... }

  @Composable
  internal fun MyContent(state: MyState, onAction: () -> Unit) { ... }
  ```

### Dependency Injection
- **Dagger Hilt** cho tất cả DI — không dùng manual DI hay Koin
- ViewModel phải dùng `@HiltViewModel` + `@Inject constructor`
- Module dùng `@Binds` khi chỉ ánh xạ interface → impl (hiệu quả hơn `@Provides`)

### Concurrency & State
- `viewModelScope.launch` cho background work trong ViewModel
- `StateFlow<UiState>` cho state management (không dùng `MutableLiveData`)
- `collectAsStateWithLifecycle()` trong Composable (lifecycle-aware)
- `Result<T>` với `runCatching { }` cho error handling

### Navigation
- **Navigation Compose** với sealed class `Screen`:
  ```kotlin
  sealed class Screen(val route: String) {
      data object Home : Screen("home")
  }
  ```
- Toàn bộ routes khai báo tập trung trong `AppNavigation.kt`

---

## 📏 Code Style

- **Package by Feature**: mỗi feature tự chứa 3 tầng domain/data/presentation
- File đặt tên theo **PascalCase** (Kotlin convention)
- Comment bằng tiếng Việt hoặc tiếng Anh (nhất quán trong cùng file)
- `TODO:` annotation cho mọi chỗ cần thay thế mock/placeholder
