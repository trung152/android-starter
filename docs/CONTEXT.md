# CONTEXT.md — Working Context (Cập nhật sau mỗi session)

> **AI:** Đọc file này đầu tiên khi bắt đầu session mới để hiểu trạng thái hiện tại của project.
> Đừng hỏi user về những gì đã ghi ở đây.

---

## 📍 Đang làm gì (Current Focus)

**Session cuối:** 2026-03-23  
**Trạng thái:** Base template hoàn chỉnh ✅

**Đã hoàn thành trong session này:**
- Tạo Android Base Project Template với Clean Architecture
- Setup `.agents/` workspace với rules, skills, workflows
- Adapt skills từ obra/superpowers (TDD, debugging, brainstorming, plans, review)
- Tạo coding conventions rules
- Setup persistent context system (docs/ folder này)

**Feature đang dở dang:** _(chưa có)_

**Việc tiếp theo (Next Up):**
- Thêm feature đầu tiên thật sự vào app (user sẽ quyết định)
- Có thể thêm `core/database/AppDatabase.kt` nếu cần Room
- Có thể thêm `core/designsystem/theme/Spacing.kt`

---

## 🧩 Features đã có

| Feature | Trạng thái | Mô tả |
|---|---|---|
| `hello` | ✅ Demo | Minh họa Clean Architecture 3 tầng, mock data |

---

## 🔧 Files quan trọng đã tạo/sửa gần đây

| File | Thay đổi |
|---|---|
| `gradle/libs.versions.toml` | Thêm Hilt, KSP, Retrofit, Room, Navigation, Coroutines |
| `app/build.gradle.kts` | Thêm tất cả dependencies, enableBuildConfig |
| `BaseApplication.kt` | `@HiltAndroidApp` |
| `MainActivity.kt` | `@AndroidEntryPoint` + AppNavigation |
| `core/navigation/AppNavigation.kt` | NavHost + sealed Screen |
| `core/network/NetworkModule.kt` | Retrofit + OkHttp Singleton |
| `core/designsystem/theme/Color.kt` | Material 3 Light/Dark palette |
| `core/designsystem/theme/Theme.kt` | Dynamic Color + status bar |

---

## ⚠️ Known Issues / TODOs

- `HelloRepositoryImpl` đang dùng mock data — cần thay bằng API thật khi có feature thật
- BASE_URL trong `NetworkModule.kt` là placeholder — cần thay khi có API
- Chưa có `Spacing.kt` token file
- Chưa có `AppDatabase.kt` (Room) — tạo khi feature đầu tiên cần local DB

---

## 💡 Decisions gần đây của User

- Không dùng GSD 2 (tool khác hệ sinh thái, không phù hợp Android)
- Đang dùng Antigravity làm AI coding assistant chính
- Adapt obra/superpowers skills thay vì cài GSD
- Màu sắc, spacing, typography đều phải khai báo tập trung trong `core/designsystem/`

---

## 🔗 Tài nguyên tham khảo

- [Android MAD Guidelines](https://developer.android.com/series/mad-skills)
- [Now in Android](https://github.com/android/nowinandroid) — Reference app của Google
- [obra/superpowers](https://github.com/obra/superpowers) — Skill framework đang dùng
