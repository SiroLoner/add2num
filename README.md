# AI-Native Core Service

## 1. Mục đích

Repository này là nền tảng cho một Core Service API được phát triển theo mô hình AI-native SDLC. GitHub Copilot có thể hỗ trợ phân tích yêu cầu, sinh code và tạo test; mọi thay đổi vẫn phải được kiểm tra bởi con người và CI.

## 2. Công nghệ và yêu cầu

- Java 17+
- Spring Boot 3.3.4
- Maven 3.8+
- JUnit 5
- PostgreSQL 16 khi dự án cần lưu trữ dữ liệu
- Docker tùy theo môi trường triển khai

## 3. Bắt đầu

```bash
git clone <repository-url>
cd ai-native-core
mvn clean verify
```

Repository hiện là scaffold; chưa có module ứng dụng thực thi. Khi Core Service được triển khai, bổ sung Spring Boot module và chạy:

```bash
mvn spring-boot:run
```

## 4. Cấu trúc repository

- `.github/`: Issue template, Pull Request template và CI workflow.
- `docs/`: domain model, API specification và coding rules.
- `src/`: mã nguồn ứng dụng.
- `tests/`: tài liệu và test bổ sung theo unit/integration.
- `scripts/`: script hỗ trợ phát triển và vận hành.

## 5. Governance

- Mỗi feature hoặc bug fix phải bắt đầu từ một GitHub Issue.
- Không push trực tiếp vào branch chính.
- Mọi Pull Request phải có test và human review.
- Code được AI hỗ trợ được xem là chưa đáng tin cậy cho đến khi được đọc, xác minh và kiểm thử.
- Không đưa secret, credential hoặc dữ liệu khách hàng vào prompt AI hay repository.

Xem [CONTRIBUTING.md](CONTRIBUTING.md) để biết quy trình chi tiết.
