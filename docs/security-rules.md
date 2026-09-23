# Security Rules

## 1. Secrets and credentials
- Không hardcode secret, password, access token, API key, hoặc connection string trong source code, test, comment hoặc markdown.
- Không đưa secret vào prompt hoặc log.
- Dùng environment variables hoặc secret manager cho mọi setting nhạy cảm.

## 2. Input validation and sanitization
- Validate dữ liệu ở boundary của controller, request DTO và service entry point.
- Không tin tưởng input từ client, HTTP header, query string, file upload hoặc DB.
- Sanitize dữ liệu trước khi render hoặc persist nếu cần thiết.

## 3. Injection prevention
- Không ghép raw user input vào SQL, JPQL, query string, shell command hoặc file path.
- Dùng PreparedStatement, ORM, repository, hoặc query builder theo API đã định nghĩa.
- Không thực thi SQL/command từ string do người dùng điều khiển.

## 4. Authorization and access control
- Kiểm tra authorization ở API hoặc service boundary phù hợp với role/ownership.
- Không cho phép truy cập theo logic "nếu request tới endpoint thì được phép".
- Dùng RBAC hoặc kiểm tra ownership/chức năng cụ thể để ngăn abuse.

## 5. Error handling and logging
- Không log secret, token, password, PII hoặc raw SQL query.
- Không trả về stack trace hoặc raw exception detail cho client.
- Log lỗi bằng logger phù hợp và chỉ với context cần thiết.

## 6. Business security check
- Với các thao tác nhạy cảm như tạo, cập nhật, xóa hoặc chuyển trạng thái, kiểm tra điều kiện nghiệp vụ trước khi viết dữ liệu.
- Đảm bảo các ràng buộc như ownership, status transition, và enable/disable action được kiểm soát.

## 7. Review gate
- Mọi bản draft AI sinh phải được review security trước khi merge.
- Nếu không thể chứng minh an toàn, không được chấp nhận.
