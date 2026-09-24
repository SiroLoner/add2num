# Add2Num Domain Model

## Phạm vi

Add2Num có một bounded context nhỏ: **decimal addition**. Hệ thống nhận hai toán hạng nguyên không âm dạng chuỗi, tính tổng theo từng cột và tùy chọn trả về các bước trung gian.

Phạm vi này không bao gồm tài khoản người dùng, thanh toán, persistence, authorization nghiệp vụ hay các phép toán khác.

## Mô hình khái niệm

```text
AdditionRequest
    -> CalculationService
        -> MyBigNumber
            -> SumResult
                -> SumStep[*]
```

| Thành phần | Loại | Trách nhiệm | Invariant |
|---|---|---|---|
| `AdditionRequest` | API input | Nhận `first`, `second`, tùy chọn `maxSteps` | Toán hạng chỉ gồm chữ số; `maxSteps >= 0` |
| `CalculationService` | Application service | Chuẩn hóa input, áp dụng giới hạn triển khai và gọi core | Không sao chép thuật toán cộng |
| `MyBigNumber` | Domain service | Cộng hai chuỗi theo từng cột | Stateless; kết quả canonical; không dùng số nguyên giới hạn kích thước |
| `SumResult` | Value object | Đóng gói kết quả, trace và metadata | Danh sách bước bất biến; `totalSteps >= 0` |
| `SumStep` | Value object | Mô tả một cột cộng | Digit và carry nằm trong miền hợp lệ |

## Boundary và ownership

- `add2num-core` sở hữu thuật toán và contract toán học.
- `add2num-web` sở hữu HTTP, giới hạn request, trình bày lỗi và view model.
- Web không được đưa logic cộng vào controller hoặc service.
- Core không phụ thuộc vào persistence, session hoặc trạng thái request.
- Operand không được ghi vào log; chỉ log kích thước và metadata tính toán.

## Quyết định thiết kế

1. Chuỗi rỗng trong core biểu diễn zero để giữ contract gốc; web layer vẫn từ chối input rỗng từ HTTP.
2. Số 0 ở đầu được chấp nhận nhưng kết quả luôn canonical.
3. Trace có giới hạn để tránh biến input lớn thành một response hoặc object graph quá lớn.
4. Không tạo entity/database table vì ứng dụng hiện không có persistence.

## Open questions

- Có cần hỗ trợ số âm hoặc số thập phân trong một phiên bản khác không? Hiện tại: ngoài phạm vi.
- Có cần xác thực caller cho API không? Hiện tại API là endpoint demo nội bộ, chưa có auth.
- Có cần persistence lịch sử phép tính không? Hiện tại: không.