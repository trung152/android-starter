---
name: brainstorming
description: Dùng TRƯỚC KHI viết bất kỳ dòng code nào. Biến ý tưởng mơ hồ thành design rõ ràng qua đối thoại. Lưu design document trước khi tiến hành.
---

# Skill: Brainstorming — Biến Ý Tưởng Thành Design

<HARD-GATE>
KHÔNG viết code, scaffold bất kỳ file, hay thực hiện bất kỳ hành động implementation nào cho đến khi:
1. Đã trình bày design cho user
2. User đã approve design đó

Áp dụng cho MỌI request, dù trông "đơn giản" đến đâu.
</HARD-GATE>

## Anti-Pattern: "Cái này đơn giản quá, không cần design"

Những feature "đơn giản" thường ẩn chứa:
- Edge cases không ai nghĩ đến
- Conflict với architecture hiện tại
- Dependency phức tạp hơn dự kiến
- UX flow cần làm rõ

**→ Không có feature nào quá nhỏ để không brainstorm.**

---

## Process

### Bước 1: Hiểu Context (AI tự làm)

Trước khi hỏi user, AI tự đọc:
- `android_mad_rules.md` — constraints công nghệ
- `clean_architecture_rules.md` — constraints kiến trúc
- `coding_conventions.md` — conventions
- Các feature liên quan đã có trong `feature/` directory

### Bước 2: Đặt câu hỏi từng cái một

**Hỏi theo thứ tự, từng câu một, chờ trả lời trước khi hỏi tiếp:**

```
Q1: Feature này giải quyết vấn đề gì cho user?
    (Hiểu mục tiêu business trước)

Q2: Domain Model chính là gì?
    (Tên + Fields: ví dụ Product { id, name, price })

Q3: User sẽ làm gì trên màn hình này?
    (Danh sách hành động: load, tap, submit, delete...)

Q4: Dữ liệu đến từ đâu?
    (Remote API / Room / cả hai / mock?)

Q5: Có điều hướng vào/ra màn hình này không?
    (Từ màn hình nào? Sau action gì?)

Q6: Có edge case đặc biệt nào cần xử lý không?
    (Empty state, error state, loading state, permission...)
```

### Bước 3: Trình bày Design (từng section, chờ approve)

Trình bày **từng phần**, chờ confirm trước khi tiếp:

**Section A: Domain Model**
```
Tôi đề xuất Domain Model như sau:
[Model name và fields]
→ Bạn có muốn thêm/bớt field nào không?
```

**Section B: Architecture Flow**
```
Luồng data sẽ là:
[UI] → [ViewModel] → [UseCase] → [Repository] → [API/Room]
→ Approach này có phù hợp với expectation của bạn không?
```

**Section C: UI/UX**
```
Màn hình sẽ có:
- State Loading: [mô tả]
- State Success: [mô tả]
- State Error: [mô tả]
- State Empty: [mô tả]
→ Có màn hình nào trong app hiện tại tôi nên follow design không?
```

**Section D: Files sẽ được tạo**
```
Tôi sẽ tạo các files sau:
feature/{name}/domain/model/{Model}.kt
feature/{name}/domain/repository/{Feature}Repository.kt
...
→ Bạn confirm để tôi bắt đầu implement không?
```

### Bước 4: Lưu Design Document

Sau khi user approve, tạo file:
```
docs/designs/YYYY-MM-DD-{feature-name}.md
```

Template:
```markdown
# Design: {Feature Name}
Date: {date}
Status: Approved

## Problem
{user's actual problem}

## Domain Model
{approved model}

## Architecture
{approved flow}

## UI States
{loading / success / error / empty}

## Files to Create
{complete list}

## Open Questions
{anything still unclear}
```

---

## Key Principles

- **Một câu hỏi tại một thời điểm** — không dump nhiều câu hỏi cùng lúc
- **Trình bày design theo sections** — từng phần, từng confirm
- **Không assume** — nếu không chắc điều gì, hỏi
- **Design trước, code sau** — luôn luôn
