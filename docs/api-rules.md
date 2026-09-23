# API Rules

## 1. REST resource naming
- Dùng danh từ số nhiều cho resource: `/api/work-orders`, không phải `/api/workorder`.
- Dùng plural naming cho collection và resource identifier rõ ràng.
- Không tạo endpoint mới nếu feature chưa có contract rõ ràng.

## 2. HTTP methods
- `GET`: đọc dữ liệu.
- `POST`: tạo mới resource.
- `PUT`: replace toàn bộ resource khi có contract rõ.
- `PATCH`: cập nhật một phần resource.
- `DELETE`: xóa resource nếu nghiệp vụ cho phép.
- Không dùng `GET` cho action thao tác thay đổi trạng thái nếu không có contract rõ.

## 3. Schema conformance
- Không tự ý thêm field JSON ngoài contract đã được quy định.
- Không trả về dữ liệu không được yêu cầu trong API spec.
- Khi response error, dùng chuẩn lỗi thống nhất và không lộ chi tiết kỹ thuật cho client.

## 4. Validation and request contract
- Dùng DTO và bean validation cho request body.
- `@Valid` hoặc validation tương đương bắt buộc nếu request cần kiểm tra dữ liệu.
- Không chấp nhận null, rỗng, hoặc enum không hợp lệ mà không trả lỗi rõ ràng.

## 5. Error handling
- Dùng Problem Details / RFC 7807 style payload khi ứng dụng web API.
- Response error nên có `type`, `title`, `status`, `detail`, và có thể có `instance` hoặc extension fields hợp lệ.
- Không trả về raw SQL, stack trace, hoặc `e.getMessage()` cho client.

## 6. Authorization and boundary
- Phải kiểm tra quyền tại API boundary trước khi thao tác dữ liệu.
- Không coi client có quyền chỉ vì request được gửi đến endpoint.
- Nếu nghiệp vụ cần role/ownership, phải kiểm tra rõ ràng trong controller hoặc service layer.

## 7. Status code expectations
- Tạo mới thành công thường trả `201 Created`.
- Dữ liệu không hợp lệ => `400 Bad Request` hoặc `422 Unprocessable Entity`.
- Chưa xác thực => `401 Unauthorized`.
- Không đủ quyền => `403 Forbidden`.
- Không tìm thấy resource => `404 Not Found`.

## 8. Review check
- Mỗi API mới hoặc thay đổi phải được đồng bộ với docs/api-spec.md và test contract.
- Không thêm field, endpoint hoặc status code nếu không có yêu cầu và bằng chứng.
