---
name: requesting-code-review
description: Dùng sau khi hoàn thành mỗi task lớn hoặc trước khi merge. AI tự review code theo checklist để bắt lỗi sớm.
---

# Skill: Requesting Code Review

## Core Principle

**Review sớm, review thường xuyên.**

Tốt hơn là bắt lỗi sau 1 task lớn hơn là sau 10 task.

---

## Khi nào Review

**Bắt buộc:**
- Sau khi hoàn thành một feature hoàn chỉnh
- Trước khi merge vào `main`
- Sau khi sửa bug phức tạp

**Nên làm:**
- Sau mỗi task lớn (UseCase + ViewModel + Screen xong)
- Khi bị stuck (fresh perspective)
- Trước khi refactor lớn

---

## Cách Review — Self-Check Checklist

### 🏛️ Clean Architecture

```
□ Dependency chạy đúng chiều: Presentation → Domain ← Data?
□ Domain layer có import android.* / androidx.* không? → Phải không có
□ ViewModel có gọi Repository trực tiếp không? → Phải qua UseCase
□ MutableStateFlow có bị expose public không? → Phải là private
□ DTO/Entity có bị leak lên Domain không? → Phải có Mapper
□ Hilt Module: @Binds cho interface binding (không phải @Provides)?
```

### ✅ Testing

```
□ UseCase có unit test không?
□ ViewModel có unit test không?
□ Test có cover cả success và failure path không?
□ Test name mô tả behavior (không phải method name)?
□ Test đã được run và pass chưa? (./gradlew testDebugUnitTest)
□ Không có test nào bị hardcode giá trị không có nghĩa?
```

### 📏 Code Conventions

```
□ Không hardcode màu trong Composable? (dùng MaterialTheme.colorScheme)
□ Không hardcode dp trong Modifier? (dùng Spacing.md, Spacing.lg...)
□ Không hardcode String trong Text()? (dùng stringResource())
□ Composable có @Preview không?
□ Screen và Content tách biệt (Screen có VM, Content stateless)?
□ Không có logic trong Composable?
```

### 🔨 Build & Runtime

```
□ Build thành công không? (./gradlew assembleDebug)
□ Tất cả test pass không? (./gradlew testDebugUnitTest)
□ Manifest có android:name=".BaseApplication" không?
□ Feature mới có được thêm vào AppNavigation không?
□ Dependency mới có được thêm vào libs.versions.toml không?
```

### 🛡️ Android-Specific

```
□ Coroutine chạy trên đúng Dispatcher không (IO, Default, Main)?
□ CancellationException không bị nuốt (catch và re-throw)?
□ viewModelScope được dùng (không phải GlobalScope)?
□ collectAsStateWithLifecycle() được dùng trong Composable (không phải collectAsState())?
```

---

## Phân loại Issues

**🔴 Critical — Phải fix trước khi tiếp tục:**
- Vi phạm Dependency Rule (Domain import Android framework)
- MutableStateFlow public
- Crash khi chạy app
- Test fail

**🟡 Important — Fix trước khi merge:**
- Thiếu error state handling trong ViewModel
- Không có @Preview
- Hardcode màu / spacing / string
- Thiếu test cho happy path

**⚪ Minor — Ghi nhớ, fix khi có dịp:**
- Comment thiếu
- Naming không nhất quán
- Thừa log debug

---

## Output Format

Sau khi review xong, báo cáo theo format:

```markdown
## Code Review Report — {Feature Name}

### ✅ Strengths
- [điểm tốt]

### 🔴 Critical Issues (phải fix ngay)
- [issue]: [file:line] → [đề xuất fix]

### 🟡 Important Issues (fix trước merge)
- [issue]: [file:line] → [đề xuất fix]

### ⚪ Minor Issues
- [issue]: [file:line]

### Assessment
[Ready to proceed / Has blockers / Needs rework]
```

---

## Sau khi Review

- **Critical issues** → Fix ngay, review lại
- **Important issues** → Fix trước khi merge main
- **Minor issues** → Create TODO comment, track lại sau
