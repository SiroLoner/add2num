# Lessons Learned

## Đã áp dụng

| Finding | Thay đổi | Bằng chứng |
|---|---|---|
| Input trace âm có thể bị đổi ngầm thành zero | Thêm `@Min(0)` cho `maxSteps` | `SumApiControllerTest.rejectsNegativeStepBudget` |
| Input lớn có thể tiêu tốn bộ nhớ | Giới hạn operand, trace và layout ở web layer | `Add2NumProperties`, API tests |
| AI có thể tạo thay đổi ngoài contract | Đưa domain model, API spec và guardrails vào `docs/` | Review checklist và tài liệu governance |
| Quality gate cần chạy lặp lại | Dùng Maven verify, JaCoCo và CI matrix | `.github/workflows/ci.yml` |

## Quy tắc cải tiến

- Mỗi finding phải có evidence, action, owner và cách xác minh.
- Không biến một lỗi đơn lẻ thành rule áp dụng rộng nếu chưa có pattern lặp lại.
- Review lại tài liệu này sau mỗi mini-project hoặc incident.