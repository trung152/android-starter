# 🚀 Android Starter — Base Project Template

Android Native base project chuẩn **Modern Android Development (MAD)**, sẵn sàng làm nền tảng phát triển bất kỳ app nào.

---

## Tech Stack

| | |
|---|---|
| **Language** | Kotlin 2.0.21 |
| **UI** | Jetpack Compose + Material 3 |
| **Architecture** | Clean Architecture + Package by Feature |
| **DI** | Dagger Hilt 2.56 (KSP) |
| **Network** | Retrofit 2.11 + OkHttp 4.12 |
| **Local DB** | Room 2.7.1 |
| **Navigation** | Navigation Compose 2.9.0 |
| **Concurrency** | Kotlin Coroutines + StateFlow |
| **Build** | Gradle KTS + Version Catalog |

---

## Cấu trúc dự án

```
app/src/main/java/com/example/androidstarter/
├── BaseApplication.kt              @HiltAndroidApp
├── MainActivity.kt                 @AndroidEntryPoint · Single Activity
├── core/
│   ├── designsystem/theme/         Color.kt · Theme.kt (Material 3)
│   ├── navigation/                 AppNavigation.kt (NavHost)
│   └── network/                    NetworkModule.kt (Retrofit + OkHttp)
└── feature/
    └── hello/                      ← Feature mẫu Clean Architecture
        ├── domain/                 Model · Repository interface · UseCase
        ├── data/                   RepositoryImpl · Hilt Module
        └── presentation/           State · ViewModel · Screen (Compose)
```

---

## Luồng dữ liệu (Clean Architecture)

```
[Screen] ──event──▶ [ViewModel] ──invoke──▶ [UseCase]
   ▲                     │                      │
   └──StateFlow──────────┘             [Repository interface]
                                               ▲
                                    [RepositoryImpl] ──▶ [API / Room]
```

**Dependency Rule:** `Presentation → Domain ← Data`  
Domain không biết gì về Android framework hay data source.

---

## Bắt đầu feature mới

1. Dùng `/new_feature_workflow` để AI tạo đủ 3 tầng tự động
2. Thêm route vào `core/navigation/AppNavigation.kt`
3. Thay mock data trong `RepositoryImpl` bằng API thật khi sẵn sàng

---

## Quy tắc quan trọng

- ❌ Không Java · Không XML layouts · Không LiveData · Không RxJava
- ✅ Mọi dependency qua `gradle/libs.versions.toml`
- ✅ Màu sắc, spacing, typography khai báo tập trung trong `core/designsystem/`
- ✅ KSP (không KAPT)

> Chi tiết đầy đủ: [`.agents/rules/`](.agents/rules/) · [`docs/`](docs/)

---

## AI Workspace

```
.agents/
├── rules/      Android MAD rules · Clean Arch rules · Coding conventions
├── skills/     scaffold_feature · tdd-android · systematic-debugging · ...
└── workflows/  /new_feature_workflow · /finish
```

**Slash commands:**
- `/new_feature_workflow` — Tạo feature mới end-to-end
- `/finish` — Lưu context, kết thúc session

---

## Yêu cầu

- Android Studio Meerkat hoặc mới hơn
- JDK 11+
- Android SDK API 26+
