# DECISIONS.md — Architectural Decision Records (ADR)

Ghi lại các quyết định kỹ thuật quan trọng và lý do chọn.
AI phải đọc file này trước khi đề xuất thay đổi kiến trúc.

---

## ADR-001: Chọn Jetpack Compose thay vì XML

**Ngày:** 2026-03-23  
**Trạng thái:** Accepted

**Quyết định:** 100% Jetpack Compose, không có XML layout nào.

**Lý do:**
- Google khuyến nghị Compose là tương lai của Android UI
- State management declarative — dễ test, ít bug hơn View system
- Tích hợp tốt hơn với coroutines và StateFlow

**Hệ quả:** Không dùng `findNavController()`, không dùng Fragment transaction.

---

## ADR-002: Chọn Dagger Hilt thay vì Koin/Manual DI

**Ngày:** 2026-03-23  
**Trạng thái:** Accepted

**Quyết định:** Dagger Hilt là DI framework duy nhất.

**Lý do:**
- Hilt là tiêu chuẩn chính thức của Google cho Android DI
- Compile-time safety — phát hiện lỗi DI sớm
- Tích hợp sẵn với ViewModel, WorkManager, Navigation

**Hệ quả:** Tất cả ViewModel phải `@HiltViewModel`, tất cả Entry point phải `@AndroidEntryPoint`.

---

## ADR-003: KSP thay vì KAPT

**Ngày:** 2026-03-23  
**Trạng thái:** Accepted

**Quyết định:** Dùng KSP (Kotlin Symbol Processing) cho code generation.

**Lý do:**
- KSP nhanh hơn KAPT 2x (không cần compile sang Java stub)
- KAPT đang deprecated dần
- Hilt và Room đều hỗ trợ KSP

**Hệ quả:** `ksp(libs.hilt.compiler)` và `ksp(libs.room.compiler)`, không phải `kapt(...)`.

---

## ADR-004: Package by Feature thay vì Package by Layer

**Ngày:** 2026-03-23  
**Trạng thái:** Accepted

**Quyết định:** Cấu trúc `feature/{name}/{domain,data,presentation}`.

**Lý do:**
- Dễ scale thành multi-module (mỗi feature = 1 module tiềm năng)
- Cohesion cao: code liên quan đến feature nằm gần nhau
- Dễ delete feature mà không ảnh hưởng feature khác

**Hệ quả:** Tất cả feature mới phải follow `feature/{name}/` structure. Shared code vào `core/`.

---

## ADR-005: StateFlow thay vì LiveData

**Ngày:** 2026-03-23  
**Trạng thái:** Accepted

**Quyết định:** `StateFlow<UiState>` cho state management, `collectAsStateWithLifecycle()` trong Composable.

**Lý do:**
- StateFlow là Kotlin-first, không phụ thuộc Android framework
- `collectAsStateWithLifecycle()` xử lý lifecycle tự động
- LiveData đang được Google recommend migrate sang Flow

**Hệ quả:** Không có `MutableLiveData`, không có `LiveData.observe()` trong codebase này.

---

## Template thêm ADR mới

```markdown
## ADR-00X: {Tiêu đề quyết định}

**Ngày:** YYYY-MM-DD  
**Trạng thái:** Accepted / Superseded by ADR-00Y / Deprecated

**Quyết định:** {Quyết định gì}

**Lý do:** {Tại sao chọn cái này}

**Hệ quả:** {Ảnh hưởng đến code như thế nào}
```
