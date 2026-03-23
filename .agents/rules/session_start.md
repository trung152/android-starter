# Session Start Rules — Quy tắc đầu mỗi session

## 🔴 Bắt buộc: Đọc trước khi code

| File | Đọc khi nào |
|---|---|
| `docs/CONTEXT.md` | **Luôn luôn** — đầu mọi session, trước mọi feature mới |
| `docs/DECISIONS.md` | Khi đề xuất thay đổi tech stack / kiến trúc |
| `docs/KNOWLEDGE.md` | Khi gặp bug hoặc implement pattern cũ (Room, Retrofit...) |
| `docs/PROJECT.md` | Khi context bị reset hoàn toàn, cần nhớ lại constraints |

---

## Quy trình bắt đầu session chuẩn

```
1. Đọc docs/CONTEXT.md
2. Xác nhận hiểu trạng thái: "Tôi thấy session trước đang làm X, còn dở Y..."
3. Hỏi user muốn tiếp tục hay làm việc mới
4. Đọc file liên quan khác nếu cần (DECISIONS, KNOWLEDGE)
5. Bắt đầu làm việc
```

---

## Quy trình kết thúc session

Khi user gõ `/finish`:
1. Đọc `.agents/workflows/finish.md`
2. Follow từng bước trong workflow đó
3. Cập nhật `docs/CONTEXT.md` là **bắt buộc**
4. Cập nhật các file khác nếu cần

---

## Nguyên tắc không được bỏ qua

- ❌ KHÔNG tự chọn tech stack mới mà không kiểm tra DECISIONS.md
- ❌ KHÔNG bắt đầu implement feature mà không đọc CONTEXT.md
- ❌ KHÔNG hỏi user "project này đang làm gì" nếu CONTEXT.md có câu trả lời
- ✅ NẾU CONTEXT.md outdated hoặc thiếu → hỏi user để cập nhật, sau đó làm việc
