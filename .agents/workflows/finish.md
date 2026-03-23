---
description: Workflow kết thúc session — cập nhật docs/CONTEXT.md và các file liên quan để session tiếp theo không bị mất context
---

# Workflow: /finish — Kết thúc Session & Cập nhật Context

## Khi nào dùng

- Sắp kết thúc làm việc
- Trước khi chuyển sang task mới hoàn toàn khác
- Khi muốn "checkpoint" trạng thái hiện tại

---

## Các bước thực hiện

### Bước 1: Tóm tắt những gì đã làm

AI tự tổng hợp từ session hiện tại:
```
1. Feature/task nào đã hoàn thành?
2. Files nào đã được tạo/sửa?
3. Vấn đề nào đã gặp và giải quyết ra sao?
4. Quyết định kỹ thuật nào đã đưa ra?
5. Còn việc gì đang dở dang?
6. Việc tiếp theo nên làm là gì?
```

---

### Bước 2: Cập nhật `docs/CONTEXT.md`

Cập nhật **toàn bộ** các section sau:

```markdown
## 📍 Đang làm gì (Current Focus)

**Session cuối:** {ngày hôm nay}
**Trạng thái:** {mô tả ngắn}

**Đã hoàn thành trong session này:**
- {việc 1}
- {việc 2}

**Feature đang dở dang:**
- `feature/{name}` — đang ở bước {X}, còn cần làm {Y}
- (hoặc: Không có)

**Việc tiếp theo (Next Up):**
- {việc cần làm tiếp theo}
```

Cập nhật bảng **Features đã có:**
```markdown
| Feature | Trạng thái | Mô tả |
|---|---|---|
| `featureName` | 🔄 In progress | Đang làm... |
| `featureName` | ✅ Done | Xong |
```

Cập nhật **Files quan trọng đã tạo/sửa gần đây** (chỉ giữ 8-10 file gần nhất).

Cập nhật **Known Issues / TODOs** (thêm mới, đánh dấu đã xong).

---

### Bước 3: Cập nhật `docs/KNOWLEDGE.md` (nếu học được gì mới)

Nếu trong session này:
- Gặp bug/gotcha chưa có trong KNOWLEDGE.md → thêm vào section "Android Gotchas"
- Dùng pattern mới thành công → thêm vào section "Patterns"
- Upgrade dependency → cập nhật bảng version

---

### Bước 4: Thêm ADR vào `docs/DECISIONS.md` (nếu có quyết định mới)

Nếu trong session này đưa ra quyết định kiến trúc mới:
```markdown
## ADR-00X: {Tiêu đề}
**Ngày:** {hôm nay}
**Trạng thái:** Accepted
**Quyết định:** ...
**Lý do:** ...
**Hệ quả:** ...
```

---

### Bước 5: Cập nhật `docs/PROJECT.md` (nếu có thay đổi lớn)

Chỉ cập nhật khi:
- Thêm tech stack mới
- Thay đổi cấu trúc thư mục gốc
- Thay đổi constraints

---

### Bước 6: Báo cáo cho User

```markdown
## ✅ Session Checkpoint

**Đã cập nhật:**
- docs/CONTEXT.md → trạng thái hiện tại
- docs/KNOWLEDGE.md → {nếu có gì mới}
- docs/DECISIONS.md → ADR-00X (nếu có)

**Tóm tắt session:**
{3-5 bullet points những gì đã làm}

**Còn dở dang:**
{nếu có}

**Gợi ý tiếp theo:**
{việc nên làm lần sau}
```

---

## Lưu ý quan trọng

- **Không xóa lịch sử** — chỉ thêm/cập nhật, giữ nguyên records cũ
- **Ngắn gọn** — mỗi bullet point tối đa 1 dòng
- `CONTEXT.md` là file sống — cập nhật thường xuyên nhất
- `DECISIONS.md` là lịch sử — hiếm khi xóa, chỉ đánh dấu Superseded
