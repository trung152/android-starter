---
description: SOP xây dựng feature mới end-to-end — từ yêu cầu business đến màn hình hoàn chỉnh
---

# Workflow: New Feature Development

## Skills Checklist — Đọc trước khi bắt đầu

| Skill | Dùng khi |
|---|---|
| `brainstorming.md` | **Bước 1** — luôn luôn, HARD-GATE trước code |
| `writing-plans.md` | **Bước 1.5** — sau khi design approved |
| `tdd-android.md` | **Bước 2–5** — mỗi lần viết UseCase, ViewModel |
| `requesting-code-review.md` | **Bước 7** — trước khi kết thúc feature |
| `systematic-debugging.md` | **Bất kỳ lúc nào** có lỗi/crash/behavior sai |

## Tổng quan luồng

```
[Brainstorm] → [Writing Plans] → [TDD: Domain] → [TDD: Data] → [TDD: ViewModel] → [UI] → [Nav] → [Review]
```

Thời gian ước tính: 15–45 phút tùy độ phức tạp.

---

## B1 — Thu thập yêu cầu ← `brainstorming.md` (HARD-GATE)

**AI phải hỏi user trước khi viết bất kỳ dòng code nào:**

```
Trước khi tôi bắt đầu, tôi cần hiểu rõ hơn:

1. Tên feature: (ví dụ: "product_list")
2. Domain Model: Dữ liệu chính là gì? Các fields nào cần thiết?
   (ví dụ: Product { id: String, name: String, price: Double, imageUrl: String })
3. Nguồn dữ liệu: Remote API / Room (offline) / cả hai?
4. Các hành động của User:
   - Chỉ xem danh sách?
   - Xem chi tiết?
   - Thêm/Sửa/Xóa?
5. Navigation: Từ màn hình nào vào? Có điều hướng đi đâu không?
```

**✅ Chỉ tiến sang B2 sau khi user xác nhận Domain Model và design đã được lưu vào `docs/designs/`.**

> → Sau đó, tạo Implementation Plan theo `writing-plans.md` trước khi viết code.

---

## B2 — Viết Domain Layer

**Dùng: `scaffold_feature.md` (Bước 1–2) + `tdd-android.md` cho UseCase**

Thứ tự tạo file:
1. `domain/model/{Model}.kt` — Data class thuần
2. `domain/repository/{Feature}Repository.kt` — Interface với các `suspend fun`
3. `domain/usecase/Get{Feature}UseCase.kt` — UseCase chính
4. (Tùy chọn) Thêm các UseCase phụ: `Create{Feature}UseCase`, `Delete{Feature}UseCase`

**Kiểm tra trước khi tiếp tục:**
- [ ] Không có import `android.*` hay `androidx.*` trong `domain/`
- [ ] Repository là `interface`, không phải `class`
- [ ] UseCase dùng `operator fun invoke()` + `Result<T>`

---

## B3 — Viết Data Layer

**Dùng: `scaffold_feature.md` (Bước 3) + `tdd-android.md` + `implement_local_db.md` nếu cần Room**

Quyết định nguồn dữ liệu:

### Nếu chỉ có Remote API:
1. Tạo `{Feature}ApiService.kt` (Retrofit interface)
2. Tạo `dto/{Model}Dto.kt` + extension `fun {Model}Dto.toDomainModel()`
3. Tạo `{Feature}RepositoryImpl.kt` — gọi API, map DTO → Domain
4. Đăng ký Retrofit service qua `NetworkModule` hoặc module riêng

### Nếu chỉ có Room (offline):
**Sử dụng Skill: `implement_local_db.md`**
1. Tạo Entity, DAO
2. Đăng ký vào `AppDatabase`
3. Thêm `@Provides` DAO trong `DatabaseModule`

### Nếu Offline-First (API + Room):
1. Làm cả hai trên
2. `{Feature}RepositoryImpl` đọc từ Room trước (`Flow<>`), refresh từ API ở background

**Tạo Hilt Module cuối cùng:**
```kotlin
@Binds @Singleton
abstract fun bindRepository(impl: {Feature}RepositoryImpl): {Feature}Repository
```

---

## B4 — Viết Presentation Layer (ViewModel)

**Dùng: `scaffold_feature.md` (Bước 4) + `tdd-android.md` (ViewModel test template)**

1. Xác định đầy đủ `{Feature}State` (tất cả trạng thái UI cần thiết)
2. Viết `{Feature}ViewModel` với `@HiltViewModel`
3. Mapping UseCase result → State update trong `viewModelScope.launch`

**Template State đầy đủ:**
```kotlin
data class {Feature}State(
    val isLoading: Boolean = false,
    val items: List<{Model}> = emptyList(),  // List hoặc single item
    val selectedItem: {Model}? = null,
    val errorMessage: String? = null,
    val isSuccess: Boolean = false           // Cho submit/delete actions
)
```

---

## B5 — Viết Presentation Layer (UI / Compose)

**Dùng: `scaffold_feature.md` (Bước 4 — Screen) + `coding_conventions.md` (Composable rules)**

1. Tách `{Feature}Screen` (có ViewModel) và `{Feature}Content` (stateless)
2. Implement UI theo State: `when { isLoading, hasData, hasError }`
3. Thêm `@Preview` với mock State

**UX checklist:**
- [ ] Loading state có `CircularProgressIndicator`
- [ ] Error state hiển thị thông báo + nút Retry
- [ ] Empty state có thông báo phù hợp
- [ ] Tất cả clickable đều có `contentDescription` cho accessibility

---

## B6 — Đăng ký Navigation

Mở `core/navigation/AppNavigation.kt`:

```kotlin
// 1. Thêm route
sealed class Screen(val route: String) {
    // ... existing
    data object {Feature} : Screen("{featureName}")
}

// 2. Thêm composable vào NavHost
composable(Screen.{Feature}.route) {
    {Feature}Screen()
}

// 3. Nếu cần truyền argument:
composable(
    route = "${Screen.{Feature}.route}/{id}",
    arguments = listOf(navArgument("id") { type = NavType.StringType })
) { backStackEntry ->
    {Feature}Screen(id = backStackEntry.arguments?.getString("id") ?: "")
}
```

---

## B7 — Review & Self-Check ← `requesting-code-review.md`

Trước khi báo cáo hoàn thành với user, AI phải tự kiểm tra:

### Kiến trúc
- [ ] Dependencies đi đúng chiều: `Presentation → Domain ← Data`
- [ ] Domain layer không có dependency Android
- [ ] ViewModel không gọi Repository trực tiếp
- [ ] `MutableStateFlow` chỉ là `private`

### Code Quality
- [ ] Hilt Module dùng `@Binds` (không phải `@Provides` cho interface binding)
- [ ] DTO/Entity đã được map sang Domain Model (không leak DTO lên trên)
- [ ] Tất cả `suspend fun` chạy trong đúng scope (không block main thread)

### UI/UX
- [ ] Có `@Preview` composable
- [ ] Loading / Error / Empty / Content states đều được handle
- [ ] Route đã được thêm vào `AppNavigation`

### Build
- [ ] Dependency mới (nếu có) đã được thêm vào `libs.versions.toml`
- [ ] `app/build.gradle.kts` đã được cập nhật nếu cần

---

## Kết thúc Workflow

Sau khi hoàn thành tất cả bước, AI báo cáo cho user:

```
✅ Feature [{feature_name}] đã hoàn thành:

📁 Cấu trúc tạo ra:
  - domain/model/{Model}.kt
  - domain/repository/{Feature}Repository.kt  
  - domain/usecase/Get{Feature}UseCase.kt
  - data/di/{Feature}Module.kt
  - data/repository/{Feature}RepositoryImpl.kt
  - presentation/{Feature}State.kt
  - presentation/{Feature}ViewModel.kt
  - presentation/{Feature}Screen.kt

🔗 Navigation: Screen.{Feature} → route = "{featureName}"

⚠️  TODO (cần user thực hiện):
  - Thay mock data trong {Feature}RepositoryImpl bằng API thật
  - Thiết kế UI cụ thể trong {Feature}Content
```
