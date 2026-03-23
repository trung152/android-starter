# PROJECT.md — Android Base Template Project

## Mô tả dự án

**Tên:** androidStarter  
**Package:** `com.example.androidstarter`  
**Min SDK:** 26 | **Target SDK:** 36  
**Mục tiêu:** Android Base Project Template chuẩn MAD, dùng làm nền tảng phát triển mọi app Android native.

> Tech stack, constraints, và coding conventions → xem `.agents/rules/`

---

## Kiến trúc

- **Pattern:** Clean Architecture + Package by Feature
- **Layers:** `Domain` (UseCase, Repository interface, Model) ← `Data` (RepositoryImpl, DTO, DAO) → `Presentation` (ViewModel, State, Screen)
- **Dependency Rule:** `Presentation → Domain ← Data` (Domain không biết gì về các tầng kia)
- **Navigation:** Single Activity + Navigation Compose (`sealed class Screen`)
- **DI Scope:** `SingletonComponent` cho Repository, `@HiltViewModel` cho ViewModel

---

## Cấu trúc thư mục

```
app/src/main/java/com/example/androidstarter/
├── BaseApplication.kt           @HiltAndroidApp
├── MainActivity.kt              @AndroidEntryPoint + AppNavigation
├── core/
│   ├── designsystem/theme/      Color.kt, Theme.kt
│   ├── navigation/              AppNavigation.kt (NavHost + Screen routes)
│   └── network/                 NetworkModule.kt (Retrofit + OkHttp)
└── feature/
    └── hello/                   Feature mẫu — minh họa 3 tầng
        ├── domain/              model, repository interface, usecase
        ├── data/                repositoryImpl, hilt module
        └── presentation/        State, ViewModel, Screen
```

---

## Trạng thái dự án

- ✅ Base template hoàn chỉnh (build OK)
- ✅ Feature `hello` minh họa Clean Architecture 3 tầng (mock data)
- ✅ `.agents/` workspace: rules, skills, workflows
- ✅ `docs/` persistent context system


