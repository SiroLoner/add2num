# Hướng dẫn đóng góp

## 1. Branch

Đặt tên branch theo mẫu:

```text
feature/WO-<issue-id>-<short-description>
fix/WO-<issue-id>-<short-description>
spike/WO-<issue-id>-<short-description>
```

## 2. Pull Request

1. Không push trực tiếp vào `main`.
2. Mỗi PR phải liên kết với một GitHub Issue đang hoạt động.
3. CI phải pass đầy đủ trước khi merge.
4. PR cần ít nhất một human reviewer.
5. Cập nhật tài liệu khi thay đổi behavior hoặc API.

## 3. Chính sách sử dụng AI

- Code do GitHub Copilot hoặc công cụ AI đề xuất được xem là **UNTRUSTED** cho đến khi được developer xác minh.
- Kiểm tra package import, API nội bộ, logic nghiệp vụ và mô hình dữ liệu; không tin mù quáng vào nội dung do AI sinh ra.
- Không đưa production key, database credential, dữ liệu khách hàng hoặc thông tin mật vào prompt AI.
- PR phải khai báo việc sử dụng AI và mô tả cách human verification đã được thực hiện.
