# Security Test Plan

## Phạm vi

Kế hoạch tập trung vào endpoint `POST /api/v1/sum`, input không tin cậy và các giới hạn tài nguyên của web layer. Đây là security review theo phạm vi ứng dụng, không phải chứng nhận OWASP đầy đủ.

## Kiểm tra bắt buộc

| Nhóm | Kiểm tra | Kết quả mong đợi |
|---|---|---|
| Input validation | Ký tự chữ, dấu âm, Unicode digit, JSON sai | `400`, không stack trace |
| Resource limits | Operand vượt `max-input-digits`, trace quá lớn | Request bị từ chối hoặc clamp theo contract |
| Content type | Gửi body không phải JSON | `415` hoặc `400` ổn định |
| Error disclosure | Lỗi validation và lỗi nội bộ | Không lộ stack trace, path hoặc operand |
| Logging | Operand hợp lệ và không hợp lệ | Log chỉ chứa kích thước/metadata, không chứa số đầy đủ |
| Output encoding | Input được hiển thị lại trên HTML | Thymeleaf escape mặc định; JavaScript dùng text-safe API |
| Dependencies | Dependency review trong Pull Request | PR bị chặn khi có dependency có advisory không được chấp nhận |

## Chạy local

```bash
mvn -B -ntp -P full clean verify
```

Dependency review chạy tự động trên Pull Request qua GitHub Actions. Findings cần được phân loại, gán owner và có risk acceptance nếu chưa thể sửa ngay.

## Khoảng trống hiện tại

- API chưa có authentication/authorization vì hiện được thiết kế như endpoint demo.
- Chưa có DAST hoặc load test trong CI.
- GitHub secret scanning phụ thuộc cấu hình của repository và cần được bật ở mức tổ chức/repository.