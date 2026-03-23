---
name: systematic-debugging
description: Dùng cho MỌI lỗi kỹ thuật: crash, test fail, behavior sai, build fail. KHÔNG FIX trước khi tìm được root cause.
---

# Skill: Systematic Debugging

## The Iron Law

```
KHÔNG FIX GÌ KHI CHƯA TÌM ĐƯỢC ROOT CAUSE
```

Nếu chưa hoàn thành Phase 1, **không được đề xuất fix**.

Đặc biệt dùng skill này khi:
- Đang bị áp lực thời gian (lúc này dễ đoán mò nhất)
- "Chỉ một fix nhỏ thôi" trông có vẻ hiển nhiên
- Fix trước đó không hoạt động
- Bạn chưa hiểu rõ vấn đề

---

## 4 Phases — Phải hoàn thành theo thứ tự

### Phase 1: Root Cause Investigation

**Trước khi làm bất cứ điều gì khác:**

**1. Đọc kỹ thông báo lỗi**
- Đọc toàn bộ stack trace — không bỏ qua
- Ghi lại: class, line number, exception type, message
- Logcat tag nào? Thread nào (main/IO)?

```
Android-specific: Tìm trong Logcat với filter:
- Fatal Exception → crash
- AndroidRuntime → uncaught exception
- Tìm dòng "Caused by:" — đó thường là root cause thật sự
```

**2. Tái hiện nhất quán**
```
□ Crash xảy ra mỗi lần không? hay chỉ đôi khi?
□ Device cụ thể / API level cụ thể?
□ Chỉ sau khi làm action gì đó không?
□ Chỉ khi có network / không có network?
```

**3. Kiểm tra thay đổi gần nhất**
```bash
git diff HEAD~1 HEAD    # Thay đổi trong commit cuối
git log --oneline -10   # 10 commit gần nhất
```
- Thư viện mới nào được thêm vào `libs.versions.toml`?
- Config/permissions nào thay đổi trong Manifest?

**4. Trace data flow trong Clean Architecture**

Khi bug liên quan đến data sai hoặc state sai:
```
Tìm theo chiều ngược từ nơi bug xuất hiện:

[HelloScreen thấy state sai]
  ← [HelloViewModel emit state gì?] → log uiState
    ← [GetGreetingUseCase trả về gì?] → log Result
      ← [HelloRepositoryImpl nhận gì từ API/Room?] → log response
        ← [Retrofit/Room trả về gì thật sự?] → log raw response
```

**Android-specific diagnostics:**
```kotlin
// Thêm tạm vào ViewModel để trace:
Log.d("DEBUG_VM", "State updated: $newState")
Log.d("DEBUG_VM", "UseCase result: $result")

// Thêm tạm vào UseCase:
Log.d("DEBUG_UC", "Repository returned: $message")

// Xóa sau khi debug xong
```

---

### Phase 2: Pattern Analysis

**Tìm pattern trước khi fix:**

1. **Tìm code đang hoạt động tương tự**
   - Feature nào khác trong project hoạt động đúng?
   - So sánh RepositoryImpl khác đang hoạt động với cái bị lỗi

2. **So sánh với reference**
   - Đọc toàn bộ file, không skim
   - List ra mọi điểm khác biệt, dù nhỏ

3. **Android-specific patterns thường gặp:**
   ```
   □ Main thread violation? → StrictMode log hoặc NetworkOnMainThread
   □ Lifecycle issue? → ViewModel scope đúng chưa?
   □ Hilt injection fail? → @Inject thiếu? Module chưa install?
   □ Coroutine scope cancel sớm? → scope đúng chưa (viewModelScope vs GlobalScope)?
   □ StateFlow không emit? → MutableStateFlow.update() hay .value = ?
   □ Room query sai? → Kiểm tra Entity/DAO column names
   □ Null từ API? → DTO có @SerializedName đúng không?
   ```

---

### Phase 3: Hypothesis & Testing

**Phương pháp khoa học — một biến thay đổi mỗi lần:**

1. **Đặt hypothesis rõ ràng:**
   ```
   "Tôi nghĩ bug xảy ra vì [X] bởi vì [Y]"
   Viết ra cụ thể, không mơ hồ.
   ```

2. **Test hypothesis tối giản:**
   - Thay đổi NHỎ NHẤT có thể để test hypothesis
   - Một biến tại một thời điểm
   - **Không fix nhiều thứ cùng lúc**

3. **Verify kết quả:**
   - Hoạt động? → Phase 4
   - Không hoạt động? → Tạo hypothesis MỚI (không chồng fix)

4. **Khi không biết:**
   - Nói thẳng "Tôi chưa hiểu X"
   - Không giả vờ biết
   - Thêm instrumentation để thu thập thêm evidence

---

### Phase 4: Implementation

**Chỉ đến đây sau khi đã xác định root cause:**

1. Fix tại **nguồn gốc** (source), không phải tại triệu chứng (symptom)
2. **Viết test cho bug này trước khi fix** (xem `tdd-android.md`)
3. Verify fix hoạt động
4. Chạy toàn bộ test suite để kiểm tra regression
5. Xóa toàn bộ debug log đã thêm tạm

---

## Red Flags — Dừng lại ngay

```
⚠️  Đang stack nhiều fix chồng lên nhau → DỪNG, Phase 1 lại
⚠️  Fix thứ 3 vẫn không hoạt động → DỪNG, đọc lại stack trace
⚠️  "Chắc là do X" mà không có evidence → DỪNG, gather evidence
⚠️  Copy-paste fix từ StackOverflow không hiểu tại sao → DỪNG
```

## Các lỗi Android thường gặp và cách trace

| Lỗi | Kiểm tra đầu tiên |
|---|---|
| `NullPointerException` | Stack trace: dòng nào? null từ đâu? |
| `IllegalStateException: Fragment not attached` | Lifecycle: đang ở state gì? |
| `NetworkOnMainThreadException` | Retrofit/Room có được gọi từ coroutine không? |
| `Hilt injection failed` | `@AndroidEntryPoint`? `@HiltViewModel`? Module `@InstallIn`? |
| App không update UI | `StateFlow.update { }` hay `.value =`? `collectAsStateWithLifecycle()`? |
| `CancellationException` ngầm | Coroutine có bị cancel không? Scope là gì? |
| Room crash | Schema version tăng chưa? Migration viết chưa? |
