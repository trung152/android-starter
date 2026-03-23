---
name: writing-plans
description: Tạo implementation plan chi tiết từ design đã được approve. Mỗi task là 2-5 phút, có exact file paths, complete code, và lệnh verify cụ thể.
---

# Skill: Writing Plans — Kế hoạch Implementation Chi Tiết

## Overview

Tạo plan như thể người thực hiện **không biết gì về codebase này và có taste tệ về test**.
Document mọi thứ: file nào cần tạo/sửa, code cụ thể, lệnh test cụ thể, expected output.

**Announce khi bắt đầu:** "Tôi đang dùng writing-plans skill để tạo implementation plan."

**Prerequisite:** Design đã được approve bởi user (xem `brainstorming.md`).

**Lưu plan tại:** `docs/plans/YYYY-MM-DD-{feature-name}.md`

---

## Bước 1: Map File Structure Trước

Trước khi viết tasks, xác định tất cả files sẽ được tạo/sửa:

```markdown
## Files

### Tạo mới
- `app/src/main/java/com/example/androidstarter/feature/{name}/domain/model/{Model}.kt`
- `app/src/main/java/com/example/androidstarter/feature/{name}/domain/repository/{Feature}Repository.kt`
- `app/src/main/java/com/example/androidstarter/feature/{name}/domain/usecase/Get{Feature}UseCase.kt`
- `app/src/main/java/com/example/androidstarter/feature/{name}/data/di/{Feature}Module.kt`
- `app/src/main/java/com/example/androidstarter/feature/{name}/data/repository/{Feature}RepositoryImpl.kt`
- `app/src/main/java/com/example/androidstarter/feature/{name}/presentation/{Feature}State.kt`
- `app/src/main/java/com/example/androidstarter/feature/{name}/presentation/{Feature}ViewModel.kt`
- `app/src/main/java/com/example/androidstarter/feature/{name}/presentation/{Feature}Screen.kt`
- `app/src/test/java/com/example/androidstarter/feature/{name}/domain/usecase/Get{Feature}UseCaseTest.kt`
- `app/src/test/java/com/example/androidstarter/feature/{name}/presentation/{Feature}ViewModelTest.kt`

### Sửa
- `app/src/main/java/com/example/androidstarter/core/navigation/AppNavigation.kt` (thêm route)
```

---

## Cấu trúc Task — Bite-Sized (2-5 phút mỗi task)

**Header bắt buộc của plan document:**
```markdown
# {Feature Name} — Implementation Plan

> **Cho AI agent:** Đọc và follow `new_feature_workflow.md` để implement plan này task-by-task.
> Dùng checkbox (- [ ]) để track tiến độ.

**Goal:** {Một câu mô tả feature này làm gì}
**Architecture:** Clean Architecture — Package by Feature
**Tech Stack:** Kotlin, Jetpack Compose, Hilt, Coroutines, StateFlow

---
```

**Template của mỗi Task:**
````markdown
### Task N: {Tên component rõ ràng}

**Files:**
- Tạo: `đường/dẫn/tuyệt/đối/FileName.kt`
- Sửa: `đường/dẫn/tuyệt/đối/ExistingFile.kt`
- Test: `đường/dẫn/tới/test/FileTest.kt`

- [ ] **Step 1: Viết failing test**

```kotlin
// Content đầy đủ của test — không "add validation here"
@Test
fun `{describe behavior exactly}`() = runTest {
    // GIVEN
    // WHEN
    // THEN
}
```

- [ ] **Step 2: Chạy test, xác nhận FAIL**

```bash
./gradlew :app:testDebugUnitTest --tests "*.{TestClassName}.{testMethodName}"
```
Expected: FAIL với message "{expected failure message}"

- [ ] **Step 3: Viết production code**

```kotlin
// Toàn bộ content của file — không viết tắt
package com.example.androidstarter...

class {ClassName} @Inject constructor(...) {
    ...
}
```

- [ ] **Step 4: Chạy test, xác nhận PASS**

```bash
./gradlew :app:testDebugUnitTest --tests "*.{TestClassName}"
```
Expected: ALL PASS

- [ ] **Step 5: Commit**

```bash
git add {files}
git commit -m "feat({feature}): add {component name}"
```
````

---

## Thứ tự Tasks cho Android Feature

Luôn follow thứ tự này (dependency trước):

```
Task 1: Domain Model (không test vì pure data class)
Task 2: Repository Interface (không test vì interface)
Task 3: UseCase + Tests (TDD — test trước)
Task 4: RepositoryImpl + Tests (TDD — test trước)
Task 5: Hilt Module (không test — DI config)
Task 6: ViewModel + Tests (TDD — test trước)
Task 7: UiState (không test vì data class)
Task 8: Composable Screen (build verify, không unit test)
Task 9: Navigation Route (integration — chạy app verify)
```

---

## Scope Check

Nếu feature cover nhiều subsystem độc lập → tách thành nhiều plan riêng.
Mỗi plan nên produce working, testable software khi hoàn thành.

---

## Remember

- **Exact file paths luôn luôn** — không viết "trong thư mục feature"
- **Code đầy đủ trong plan** — không viết "thêm validation ở đây"
- **Lệnh chạy chính xác với expected output** — không viết "chạy test"
- **DRY, YAGNI, TDD, commit thường xuyên**
- **Reference skill liên quan** khi cần: `tdd-android.md`, `systematic-debugging.md`
