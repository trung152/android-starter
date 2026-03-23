# Coding Conventions — Quy ước code Android Native

## 1. Khai báo biến & hằng số

### Naming conventions
```kotlin
// ✅ Đúng
val userName: String = "Trung"           // camelCase cho biến
const val MAX_RETRY_COUNT = 3            // SCREAMING_SNAKE_CASE cho const
var isLoading: Boolean = false           // prefix "is" cho Boolean

// ❌ Sai
val UserName = "Trung"                   // PascalCase chỉ dùng cho class
const val maxRetryCount = 3             // const phải SCREAMING_SNAKE_CASE
var loading = false                      // thiếu prefix is/has/can
```

### Quy tắc khai báo
- Ưu tiên `val` (immutable) hơn `var` — chỉ dùng `var` khi thực sự cần thay đổi
- Luôn **khai báo type rõ ràng** khi type không hiển nhiên từ giá trị
- Tránh khai báo biến trong scope rộng hơn nơi dùng
- Hằng số cấp file/companion: dùng `private const val`, đặt ở đầu file/companion object

```kotlin
// ✅ Đúng — type rõ ràng khi cần
val timeout: Long = 30_000L
val items: List<Product> = emptyList()

// ✅ Đúng — type ngầm định khi hiển nhiên
val name = "Hello"
val count = 0

// ✅ Hằng số tập trung trong companion object
class SomeViewModel : ViewModel() {
    companion object {
        private const val PAGE_SIZE = 20
        private const val DEBOUNCE_MS = 300L
    }
}
```

### Scope functions
| Function | Khi nào dùng |
|---|---|
| `let` | Null check, transform và trả về giá trị khác |
| `apply` | Khởi tạo/cấu hình object, trả về chính object |
| `also` | Side-effect (log, debug), trả về chính object |
| `run` | Tính toán phức tạp trong scope của object |
| `with` | Gọi nhiều phương thức trên cùng object (không phải extension) |

```kotlin
// ✅ Đúng
val user = User().apply {
    name = "Trung"
    age = 25
}

val result = user?.let { u ->
    processUser(u)  // chỉ chạy nếu user != null
}

// ❌ Sai — lồng scope functions không cần thiết
val result = user?.let { it.also { it.run { ... } } }
```

---

## 2. Màu sắc — Khai báo TẬP TRUNG

### Quy tắc cứng (KHÔNG được vi phạm)
> ❌ **TUYỆT ĐỐI KHÔNG** hardcode màu trong Composable hoặc bất kỳ file nào khác ngoài `Color.kt`

```kotlin
// ❌ Sai — hardcode màu trực tiếp
Text(color = Color(0xFF1565C0))
Box(modifier = Modifier.background(Color.Red))

// ✅ Đúng — dùng từ MaterialTheme (tự động light/dark)
Text(color = MaterialTheme.colorScheme.primary)
Box(modifier = Modifier.background(MaterialTheme.colorScheme.surface))

// ✅ Đúng — nếu cần màu custom ngoài scheme, khai báo trong Color.kt rồi import
// (Trong Color.kt): val BrandAccent = Color(0xFFFF6D00)
// (Trong composable): import và dùng BrandAccent
```

### Cách mở rộng ColorScheme cho màu custom
Khai báo trong `core/designsystem/theme/Color.kt` và tạo extension:
```kotlin
// Color.kt — khai báo tất cả màu tại đây
val BrandAccent = Color(0xFFFF6D00)
val SuccessGreen = Color(0xFF2E7D32)
val WarningAmber = Color(0xFFF57F17)
val InfoBlue = Color(0xFF0277BD)

// Không bao giờ khai báo Color ở file khác
```

---

## 3. Typography — Khai báo tập trung

### Quy tắc
- Không dùng `fontSize`, `fontWeight` hardcode trong Composable
- Tất cả text style dùng từ `MaterialTheme.typography.*`
- Nếu cần custom style, tạo `Type.kt` trong `core/designsystem/theme/`

```kotlin
// ❌ Sai
Text(text = "Title", fontSize = 24.sp, fontWeight = FontWeight.Bold)

// ✅ Đúng
Text(text = "Title", style = MaterialTheme.typography.headlineMedium)

// ✅ Nếu cần custom style — khai báo trong Type.kt
val AppTypography = Typography(
    headlineMedium = TextStyle(
        fontFamily = FontFamily(Font(R.font.inter_semi_bold)),
        fontSize = 24.sp,
        lineHeight = 32.sp
    ),
    // ...
)
```

---

## 4. Dimension / Spacing — Khai báo tập trung

### Quy tắc
- **Không** hardcode số dp/sp trực tiếp trong Composable
- Tạo file `core/designsystem/theme/Spacing.kt` chứa tất cả dimension tokens

```kotlin
// core/designsystem/theme/Spacing.kt
object Spacing {
    val none = 0.dp
    val xs = 4.dp
    val sm = 8.dp
    val md = 16.dp
    val lg = 24.dp
    val xl = 32.dp
    val xxl = 48.dp

    // Component-specific
    val screenPadding = 16.dp
    val cardPadding = 12.dp
    val iconSize = 24.dp
    val iconSizeLarge = 48.dp
    val buttonHeight = 48.dp
}

// Dùng trong Composable:
// ✅ Đúng
Modifier.padding(Spacing.md)
Modifier.size(Spacing.iconSize)

// ❌ Sai
Modifier.padding(16.dp)
Modifier.size(24.dp)
```

---

## 5. Composable — Quy ước UI

### Đặt tên
```kotlin
// Composable: PascalCase, danh từ/cụm danh từ mô tả UI element
@Composable fun ProductCard() { }
@Composable fun LoadingIndicator() { }
@Composable fun ErrorMessage(message: String) { }

// Không đặt tên kiểu động từ:
// ❌ fun ShowProduct() — sai
// ✅ fun ProductCard() — đúng
```

### Cấu trúc Composable chuẩn
```kotlin
// 1. Modifier luôn là tham số với default = Modifier
@Composable
fun ProductCard(
    product: Product,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,           // Modifier là tham số cuối trước lambda
) {
    Card(modifier = modifier) { ... }
}

// 2. Lambda callback luôn là tham số cuối (trailing lambda)
@Composable
fun ActionButton(
    text: String,
    modifier: Modifier = Modifier,
    onClick: () -> Unit,                     // ✅ Lambda ở cuối
)
```

### Tái sử dụng & phân tách
- Composable > 50 dòng → tách thành các composable nhỏ hơn
- Không đặt business logic trong Composable (chỉ render + forward events)
- Tham số truyền vào Composable chỉ là **primitive types** hoặc **domain model** (không truyền cả ViewModel)

```kotlin
// ❌ Sai — truyền ViewModel vào composable con
@Composable
fun ProductList(viewModel: ProductViewModel) { ... }

// ✅ Đúng — truyền state và callback
@Composable
fun ProductList(
    products: List<Product>,
    onProductClick: (Product) -> Unit
) { ... }
```

### State hoisting
```kotlin
// Stateful (có ViewModel — chỉ ở Screen cấp cao nhất)
@Composable
fun ProductScreen(vm: ProductViewModel = hiltViewModel()) {
    val state by vm.uiState.collectAsStateWithLifecycle()
    ProductContent(state = state, onRefresh = vm::refresh)
}

// Stateless (previewable, testable, reusable)
@Composable
internal fun ProductContent(
    state: ProductState,
    onRefresh: () -> Unit
) { ... }
```

---

## 6. String Resources

### Quy tắc
- Tất cả string hiển thị với user phải khai báo trong `res/values/strings.xml`
- Không hardcode string trong code (trừ log/debug)
- Dùng `stringResource()` trong Composable

```kotlin
// ❌ Sai
Text(text = "Xin chào người dùng")

// ✅ Đúng
Text(text = stringResource(R.string.greeting_user))
```

---

## 7. Kotlin File & Package

### Cấu trúc file
- Một file chứa **một class/object chính** (cùng tên file)
- Extension functions: đặt trong file riêng `{Type}Extensions.kt`
- Utility functions thuần: đặt trong `core/utils/`

### Thứ tự khai báo trong file
```kotlin
// 1. Package declaration
package com.example.androidstarter.feature.product.domain.model

// 2. Imports (auto-sort bằng IDE)
import ...

// 3. Top-level constants (nếu có)
private const val MAX_NAME_LENGTH = 100

// 4. Main class/interface/object
data class Product(...)

// 5. Extension functions (nếu liên quan đến file này)
fun Product.isValid(): Boolean = name.isNotBlank()
```

---

## 8. Coroutines Hygiene

```kotlin
// ✅ Dispatcher rõ ràng cho I/O operations (Room, Retrofit)
withContext(Dispatchers.IO) {
    dao.getAll()
}

// ✅ Dispatcher mặc định cho CPU-intensive work
withContext(Dispatchers.Default) {
    heavyComputation()
}

// ❌ Không block Main thread
// Retrofit và Room Coroutines tự chuyển Dispatcher — không cần withContext thủ công

// ✅ Luôn handle cancellation
viewModelScope.launch {
    try {
        // work
    } catch (e: CancellationException) {
        throw e  // Không nuốt CancellationException
    } catch (e: Exception) {
        // handle error
    }
}
```

---

## 9. Comment & Documentation

```kotlin
// ✅ Comment giải thích "TẠI SAO", không phải "CÁI GÌ"
// Dùng delay để tránh race condition khi animation chưa kết thúc
delay(300)

// ❌ Thừa — code đã tự giải thích
// Gọi hàm getUser
val user = getUser(id)

// ✅ KDoc cho public API
/**
 * Lấy thông tin người dùng theo [id].
 * Trả về null nếu không tìm thấy.
 */
suspend fun getUser(id: String): User?
```

---

## 10. Tổng hợp nhanh (Quick Reference)

| Hạng mục | Quy tắc |
|---|---|
| Biến | `val` ưu tiên, camelCase, Boolean prefix `is/has/can` |
| Hằng số | `SCREAMING_SNAKE_CASE`, đặt trong `companion object` |
| Màu | Chỉ khai báo trong `Color.kt`, dùng `MaterialTheme.colorScheme` |
| Typography | Chỉ dùng `MaterialTheme.typography.*` hoặc `Type.kt` |
| Spacing | Khai báo tập trung trong `Spacing.kt` (object token) |
| Composable | PascalCase, Modifier là tham số, lambda ở cuối |
| String | Tất cả trong `strings.xml`, dùng `stringResource()` |
| Scope functions | `let` (null), `apply` (init), `also` (side-effect) |
| Coroutines | Không nuốt `CancellationException`, Dispatcher rõ ràng |
| Comment | Giải thích "tại sao", KDoc cho public API |
