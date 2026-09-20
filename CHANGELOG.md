# Changelog

Định dạng theo [Keep a Changelog](https://keepachangelog.com/en/1.1.0/),
phiên bản theo [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [1.0.0] — TASK 2

### Added

- Module `add2num-web`: ứng dụng Spring Boot 3.3 + Thymeleaf + Bootstrap 5.2.0, tái sử dụng
  `add2num-core` dưới dạng sub-module Maven.
- Trang tính với ô nhập hai số, kiểm tra dữ liệu phía server và thông báo lỗi theo từng ô.
- Khối **Đặt tính rồi tính**: vẽ lại phép cộng như trên giấy, kèm hàng số nhớ.
- Khối **Tiến trình tính toán**: thanh tiến trình và bảng từng cột, có nút Chạy / Từng bước /
  Về đầu / Hiện tất cả; hoạt động được cả khi tắt JavaScript nhờ khối `<noscript>`.
- REST API `POST /api/v1/sum` với cấu trúc lỗi thống nhất.
- Health check qua Spring Boot Actuator.
- Giới hạn cấu hình được cho độ dài đầu vào, số bước hiển thị và bề rộng khối đặt tính.
- Dockerfile nhiều tầng, chạy bằng user không phải root.
- CI trên GitHub Actions, build với JDK 17 và 21.

## [0.0.1] — TASK 1

### Added

- `MyBigNumber.sum(String stn1, String stn2)`: cộng hai số nguyên không âm dạng chuỗi bằng thuật
  toán cộng tay một lượt, `O(n)` thời gian và bộ nhớ.
- `MyBigNumber.sumWithTrace(...)`, `SumResult`, `SumStep`: ghi lại từng cột của phép cộng, có giới
  hạn số bước để đầu vào cực lớn không làm cạn bộ nhớ.
- Logging qua `java.lang.System.Logger`: mức `DEBUG` cho mỗi phép tính, `TRACE` cho từng cột.
- Kiểm tra đầu vào với thông báo nêu rõ tên tham số, ký tự sai và vị trí.
- Bộ unit test gồm trường hợp biên, nhớ dây chuyền, test theo tính chất đại số, đối chiếu
  `BigInteger`, logging, an toàn đa luồng và hiệu năng.
- JaCoCo với ngưỡng bao phủ được thực thi: build fail nếu dưới 85% dòng / 75% nhánh.
- Module không có dependency runtime nào ngoài JDK.
