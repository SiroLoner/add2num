# AI Development Guardrails

## Quy tắc bắt buộc

- Chỉ dùng Copilot trong phạm vi task và file đã được xác định.
- Không đưa secret, token, file `.env`, dữ liệu khách hàng hoặc dữ liệu production vào prompt.
- Không chấp nhận endpoint, dependency, field hoặc business rule mới nếu không có trong spec.
- Mọi thay đổi do AI hỗ trợ phải được đọc diff và human verify.
- Mọi acceptance criterion mới phải có test tương ứng hoặc được ghi rõ lý do chưa thể test.
- Giữ `add2num-core` độc lập với web presentation và không sao chép thuật toán sang module web.

## Prompt contract đề xuất

Mỗi prompt triển khai nên nêu:

1. Requirement hoặc phần trong `docs/ADDITION-API-SPEC.md`.
2. File được phép thay đổi.
3. File không được thay đổi.
4. Acceptance criteria.
5. Lệnh test cần chạy.
6. Yêu cầu báo cáo giả định và phần chưa chắc chắn.

## Human verification checklist

- [ ] Diff chỉ nằm trong scope.
- [ ] Không có secret hoặc dữ liệu nhạy cảm.
- [ ] Contract khớp `docs/ADDITION-API-SPEC.md`.
- [ ] Test mới bao phủ hành vi thay đổi.
- [ ] Build, test và quality gate đã chạy.
- [ ] Tài liệu/traces đã được cập nhật.