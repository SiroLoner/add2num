# Bảng truy vết yêu cầu

Mỗi gạch đầu dòng trong đề bài được nối tới đoạn code thực hiện nó và tới test chứng minh nó chạy
đúng. Bảng này tồn tại vì lý do rất thực tế: đọc review của người làm trước, thứ khiến họ mất điểm
không phải là thuật toán sai, mà là **bỏ sót chi tiết trong yêu cầu**.

Nguồn: `change-for-java-software-developer.html` và `Add2Num_High-level-requirement_v1.8.pdf`.

---

## TASK 1 — `add2num-core`

| # | Yêu cầu | Thực hiện ở | Test chứng minh |
|---|---|---|---|
| 1.1 | Cộng hai số lớn dạng chuỗi | `MyBigNumber.sum(String, String)` | `MyBigNumberTest.OrdinaryAddition` |
| 1.2 | Mô phỏng cách cộng tay: duyệt từ phải sang trái, cộng từng cặp chữ số | `MyBigNumber.add`, vòng `while` duyệt ngược | `MyBigNumberTraceTest.RecordedSteps.recordsEveryColumn` |
| 1.3 | Viết chữ số cuối của tổng, nhớ phần còn lại sang cột sau | `carryOut`/`digit` trong vòng lặp | `MyBigNumberTraceTest.carriesChainCorrectly` |
| 1.4 | Ví dụ trong tài liệu: `4 + 7 = 11`, viết 1 nhớ 1 | — | `MyBigNumberTest.SpecificationExample` (cả kết quả lẫn bước đầu tiên) |
| 1.5 | Tên class là `MyBigNumber` | `com.caesar.add2num.core.MyBigNumber` | biên dịch được là đủ |
| 1.6 | Chữ ký `String sum(String stn1, String stn2)` | giữ nguyên **tên tham số** `stn1`, `stn2` như tài liệu | `MyBigNumberTest` gọi đúng chữ ký đó |
| 1.7 | Giả định đầu vào hợp lệ, không bắt buộc validate | vẫn validate — xem `DESIGN.md §2` | `MyBigNumberTest.ContractEnforcement` |
| 1.8 | Có logging (dùng `print` cũng được) | `System.Logger`: `DEBUG` mỗi phép tính, `TRACE` mỗi cột | `MyBigNumberLoggingTest` — bắt log thật và khẳng định nội dung |
| 1.9 | Unit test đặt ở project/thư mục riêng | `add2num-core/src/test/java` | 6 lớp test |
| 1.10 | Test case thể hiện cách thuật toán chạy | `MyBigNumberTraceTest` in ra từng cột | như trên |
| 1.11 | Repo GIT công khai | GitHub/GitLab (xem README §1) | — |
| 1.12 | Gắn tag / nhánh phiên bản `0.0.1` | nhánh `core`, tag `v0.0.1`, `<version>0.0.1</version>` | `git tag -l` |
| 1.13 | `README.md` hướng dẫn build và chạy | `README.md` §1 | — |
| 1.14 | Người khác clone về phải tự kiểm chứng được | chỉ cần JDK 17 + Maven, không cấu hình thêm | CI chạy đúng các lệnh trong README |

## TASK 2 — `add2num-web`

| # | Yêu cầu | Thực hiện ở | Test chứng minh |
|---|---|---|---|
| 2.1 | Ứng dụng web cho phép người dùng cộng hai số lớn | `CalculatorController` + `templates/index.html` | `CalculatorControllerTest.addsTwoNumbers` |
| 2.2 | Dùng Spring MVC hoặc Spring Boot | Spring Boot 3.3.4 | `Add2NumWebApplicationTests.contextLoads` |
| 2.3 | Dùng Thymeleaf | `spring-boot-starter-thymeleaf`, template thật được render trong test | `CalculatorControllerTest.rendersTheForm` |
| 2.4 | Dùng Bootstrap 5.2.0 | WebJar `org.webjars:bootstrap:5.2.0`, phục vụ từ chính ứng dụng | `CalculatorControllerTest.servesBootstrapLocally` |
| 2.5 | Tái sử dụng Task 1 như sub-module hoặc `.jar` | `add2num-web` phụ thuộc `add2num-core`; không có dòng thuật toán nào ở tầng web | `Add2NumWebApplicationTests.reusesTheCoreLibrary` |
| 2.6 | Có phần hiển thị **tiến trình tính toán** | thanh tiến trình + bảng từng cột + nút Chạy/Từng bước, dữ liệu từ `SumResult.steps()` | `CalculatorControllerTest.exposesTheStepsAsJson`, `ColumnLayoutTest` |
| 2.7 | Nộp lên GIT server theo cấu trúc nhánh đã nêu | nhánh `main`, tag `v1.0.0` | `git log --graph` |
| 2.8 | Có thể đính kèm tài liệu và ghi chú | `docs/` + README | — |
| 2.9 | Thời lượng ước tính 2 ngày | — | — |

---

## Ba điểm người làm trước bị mentor trừ

Review công khai trên trang thử thách nêu ba điểm. Dưới đây là cách dự án này xử lý từng điểm.

### "Bỏ sót nhiều chi tiết trong yêu cầu"

Đó là lý do bảng trên tồn tại. Những chi tiết nhỏ dễ trôi nhất đã được giữ đúng nguyên văn:

- tên class đúng là `MyBigNumber`, không phải `BigNumberUtil` hay `Add2Num`;
- tên tham số đúng là `stn1`, `stn2`, không đổi thành `a`, `b` cho gọn;
- phiên bản là `0.0.1` cho TASK 1, không phải `1.0.0`;
- nhánh tên đúng là `core`;
- Bootstrap đúng phiên bản `5.2.0`, không phải "bản 5 mới nhất";
- có `README.md` hướng dẫn build, và điều kiện "người khác tự kiểm chứng được" được CI kiểm tra
  bằng cách chạy đúng các lệnh ghi trong README trên một máy trống.

### "Code chưa tối ưu"

- Thuật toán một lượt, `O(n)` thời gian, `O(n)` bộ nhớ — bằng đúng kích thước kết quả.
- Ghi vào `char[]` cấp phát sẵn, không nối chuỗi trong vòng lặp (tránh `O(n²)`).
- Không có phép chia số nguyên trong vòng lặp nóng.
- `ArrayList` của trace được cấp phát sẵn đúng dung lượng.
- Log ở mức `TRACE` được bọc trong `isLoggable`, nên không tốn một phép nối chuỗi nào khi tắt log.
- Trace bị chặn trên để một đầu vào khổng lồ không làm cạn bộ nhớ.
- Có test khẳng định độ phức tạp vẫn là tuyến tính, để một lần refactor vô ý không âm thầm phá nó.

### "Độ bao phủ test hời hợt"

- 6 lớp test cho riêng module lõi, chia theo **điều khoản của hợp đồng** chứ không theo method.
- Test theo tính chất: giao hoán, kết hợp, phần tử đơn vị, bề rộng kết quả, đối chiếu `BigInteger`
  trên hàng nghìn mẫu ngẫu nhiên với **seed cố định** để lỗi tái lập được.
- Test cho cả những thứ hay bị bỏ qua: logging, an toàn đa luồng, tính bất biến của danh sách trả
  về, thông báo lỗi có nêu đúng chỉ số.
- Tầng web được test qua Spring MVC và Thymeleaf thật, nên lỗi template làm hỏng build.
- JaCoCo **chặn build** nếu độ bao phủ tụt dưới 85% dòng / 75% nhánh — một ngưỡng được thực thi
  đáng giá hơn một con số được báo cáo.
