# Add2Num

Cộng hai số nguyên **rất lớn** được biểu diễn dưới dạng chuỗi, đúng theo cách đặt tính rồi tính của
học sinh tiểu học, kèm một ứng dụng web cho phép xem lại từng bước của phép tính.

Đây là bài nộp cho *Challenge for Software Developers* (Remote Internship Program), gồm hai phần:

| | Nội dung | Kết quả nộp |
|---|---|---|
| **TASK 1** | Thư viện lõi cộng hai số lớn | module `add2num-core`, nhánh `core`, tag `v0.0.1` |
| **TASK 2** | Ứng dụng web Spring Boot + Thymeleaf + Bootstrap | module `add2num-web`, nhánh `main`, tag `v1.0.0` |

---

## 1. Chạy thử trong 30 giây

Yêu cầu: **JDK 17 trở lên** và **Maven 3.8 trở lên**.

```bash
git clone <URL-repo>
cd add2num

mvn clean verify          # build + chạy toàn bộ test + kiểm tra độ bao phủ
mvn -pl add2num-web spring-boot:run
```

Mở http://localhost:8080

Nếu chỉ quan tâm TASK 1:

```bash
git checkout core         # hoặc: git checkout v0.0.1
mvn clean verify
```

---

## 2. Bài toán và lời giải

Yêu cầu gốc (`Add2Num_High-level-requirement_v1.8.pdf`): viết hàm cộng hai số lớn dạng chuỗi,
mô phỏng cách cộng tay — duyệt từ phải sang trái, cộng từng cặp chữ số cùng với số nhớ, viết chữ số
cuối của tổng, nhớ phần còn lại sang cột kế tiếp.

```java
MyBigNumber calculator = new MyBigNumber();

calculator.sum("1234", "897");     // "2131"
calculator.sum("999", "1");        // "1000"
calculator.sum("007", "3");        // "10"

SumResult traced = calculator.sumWithTrace("1234", "897");
traced.steps().forEach(step -> System.out.println(step.describe()));
// 4 + 7 + 0 = 11 -> write 1, carry 1
// 3 + 9 + 1 = 13 -> write 3, carry 1
// 2 + 8 + 1 = 11 -> write 1, carry 1
// 1 + 0 + 1 = 2  -> write 2, carry 0
```

Độ dài của số **chỉ bị giới hạn bởi bộ nhớ**. Không dùng `long`, `double`, `BigInteger` hay
`BigDecimal` ở bất kỳ đâu trong phép tính — `BigInteger` chỉ xuất hiện trong tầng test, với vai trò
là lời giải tham chiếu để đối chiếu kết quả.

Một phép cộng hai số **1.000.000 chữ số** mất khoảng **35 ms** trên máy thường.

### Hợp đồng của hàm (contract)

| Tình huống | Hành vi | Lý do |
|---|---|---|
| Chuỗi chỉ chứa chữ số `0-9` | Cộng bình thường | Yêu cầu gốc |
| Có số 0 ở đầu: `"007" + "3"` | Trả `"10"` | Kết quả luôn ở dạng chuẩn, không có số 0 thừa |
| Cả hai rỗng: `"" + ""` | Trả `"0"` | Trả chuỗi rỗng sẽ là một bất ngờ khó chịu cho người gọi |
| `null` | Ném `IllegalArgumentException` | Nêu rõ tham số nào sai |
| Ký tự không phải chữ số | Ném `IllegalArgumentException` | Nêu rõ ký tự gì, ở vị trí nào |

Tài liệu yêu cầu cho phép **giả định đầu vào luôn hợp lệ**, tức là có thể bỏ qua khâu kiểm tra.
Dự án vẫn kiểm tra, vì lý do giải thích trong [`docs/DESIGN.md`](docs/DESIGN.md#vì-sao-vẫn-kiểm-tra-đầu-vào).

---

## 3. Cấu trúc dự án

```
add2num/
├── pom.xml                     # parent POM: ghim phiên bản, cấu hình plugin dùng chung
├── add2num-core/               # TASK 1 — thư viện thuần Java, KHÔNG dependency runtime
│   └── src/main/java/com/caesar/add2num/core/
│       ├── MyBigNumber.java    # thuật toán, đúng tên class và tên method theo đề
│       ├── SumStep.java        # một cột của phép cộng
│       └── SumResult.java      # kết quả + danh sách các bước
├── add2num-web/                # TASK 2 — Spring Boot + Thymeleaf + Bootstrap 5.2.0
│   └── src/main/java/com/caesar/add2num/web/
│       ├── config/             # binding cấu hình, đăng ký bean của thư viện lõi
│       ├── service/            # giới hạn đầu vào, chuyển lỗi thư viện sang lỗi nghiệp vụ
│       ├── web/                # controller + form của trang HTML
│       ├── view/               # view model cho template
│       └── api/                # REST API JSON
├── docs/
│   ├── DESIGN.md               # thuật toán, độ phức tạp, các quyết định thiết kế
│   ├── REQUIREMENTS-TRACEABILITY.md  # bảng truy vết từng gạch đầu dòng của đề
│   └── REVIEW-CHECKLIST.md     # tự review trước khi nộp
├── .github/workflows/ci.yml    # build + test tự động
└── Dockerfile
```

`add2num-web` dùng `add2num-core` như một **sub-module Maven**, đúng theo yêu cầu *"tái sử dụng kết
quả Task 1 như sub-module hoặc thư viện .jar"*. Không một dòng nào của thuật toán bị viết lại ở
tầng web.

---

## 4. Ứng dụng web

| Đường dẫn | Mô tả |
|---|---|
| `GET /` | Trang nhập hai số |
| `POST /` | Tính và hiển thị kết quả cùng tiến trình |
| `POST /api/v1/sum` | API JSON cho script hoặc dịch vụ khác |
| `GET /actuator/health` | Health check |

Trang kết quả gồm ba phần:

1. **Kết quả** — tổng, số chữ số, số cột đã tính, số lần nhớ, thời gian thực thi.
2. **Đặt tính rồi tính** — vẽ lại phép cộng như trên giấy, hàng số nhớ ở trên cùng
   (chỉ hiện với phép tính đủ hẹp để còn đọc được).
3. **Tiến trình tính toán** — thanh tiến trình cùng bảng từng cột, có nút *Chạy / Từng bước /
   Về đầu / Hiện tất cả*. Đây là phần đáp ứng yêu cầu *"a section displaying the progress of the
   calculation"*.

Bootstrap 5.2.0 được phục vụ từ chính ứng dụng qua WebJars, nên trang **chạy được khi không có
mạng** và không phụ thuộc CDN.

### Dùng API

```bash
curl -s localhost:8080/api/v1/sum \
  -H 'Content-Type: application/json' \
  -d '{"first":"1234","second":"897","maxSteps":10}'
```

```json
{
  "value": "2131",
  "digitCount": 4,
  "totalSteps": 4,
  "returnedSteps": 4,
  "truncated": false,
  "carryCount": 3,
  "elapsedMillis": 0.021,
  "steps": [
    { "position": 0, "place": "units", "leftDigit": 4, "rightDigit": 7, "carryIn": 0,
      "total": 11, "digit": 1, "carryOut": 1,
      "description": "4 + 7 + 0 = 11 -> write 1, carry 1" }
  ]
}
```

Mọi lỗi đều trả về cùng một cấu trúc, nên client không phải rẽ nhánh theo mã trạng thái trước khi
đọc nội dung lỗi.

### Cấu hình

| Khoá | Mặc định | Ý nghĩa |
|---|---|---|
| `add2num.max-input-digits` | `100000` | Số chữ số tối đa cho mỗi toán hạng |
| `add2num.max-trace-steps` | `500` | Số cột tối đa gửi xuống trình duyệt |
| `add2num.max-column-layout-width` | `40` | Bề rộng tối đa còn vẽ kiểu đặt tính |

Đây là **giới hạn an toàn**, không phải tuỳ chọn thẩm mỹ: nếu không có chúng, một request mang một
trăm triệu chữ số đủ sức làm cạn bộ nhớ của cả server.

---

## 5. Kiểm thử

```bash
mvn test            # bộ test thường, bỏ qua test hiệu năng
mvn -P full verify  # chạy tất cả, gồm test hiệu năng và kiểm tra độ bao phủ
```

Bộ test không chỉ kiểm tra "chạy ra đúng số", mà bảo vệ từng điều khoản của hợp đồng:

- **Ví dụ trong đề** — `1234 + 897`, kể cả bước đầu tiên `4 + 7 = 11, viết 1 nhớ 1`.
- **Trường hợp biên** — chuỗi rỗng, số 0 ở đầu, hai số khác độ dài, `null`, ký tự lạ.
- **Nhớ dây chuyền** — `9…9 + 1` với độ dài 1, 2, 3, 5, 17, 64, 255, 1000.
- **Đối chiếu `BigInteger`** — hàng nghìn cặp số ngẫu nhiên, seed cố định để lỗi luôn tái lập được.
- **Tính chất đại số** — giao hoán, kết hợp, phần tử đơn vị, bề rộng kết quả.
- **Logging** — bắt log thật và khẳng định nội dung, thay vì tin là nó có chạy.
- **Hiệu năng** — 1.000.000 chữ số, và kiểm tra thời gian tăng **tuyến tính** chứ không phải bậc hai.
- **Đa luồng** — 8 luồng dùng chung một instance.
- **Tầng web** — chạy qua Spring MVC và template Thymeleaf thật, nên một biểu thức hỏng trong
  template sẽ làm hỏng build chứ không đợi đến lúc mở trình duyệt.

Độ bao phủ của `add2num-core` được đo bằng JaCoCo và **build sẽ fail** nếu tụt xuống dưới
85% dòng / 75% nhánh. Báo cáo: `add2num-core/target/site/jacoco/index.html`.

Ngoài ra thuật toán đã được đối chiếu với `BigInteger` qua **1.707.466 phép so sánh**, bao gồm
vét cạn toàn bộ cặp `0..999 × 0..999` và các biến thể có số 0 ở đầu.

---

## 6. Nhánh và phiên bản

```
* v1.0.0  (main)  feat(web): ứng dụng web + REST API, tái sử dụng core
* v0.0.1  (core)  feat(core): thư viện cộng hai số lớn
```

- Nhánh `core` là kết quả **TASK 1** đứng độc lập, gắn tag `v0.0.1` đúng như tài liệu yêu cầu.
- Nhánh `main` là kết quả **TASK 2**, bổ sung module web lên trên.

Tài liệu yêu cầu gợi ý clone về theo đường dẫn
`D:\Projects\<git-provider>\<account>\<projectname>`; dự án không phụ thuộc vào vị trí thư mục nên
đặt ở đâu cũng build được.

---

## 7. Docker

```bash
docker build -t add2num .
docker run --rm -p 8080:8080 add2num
```

---

## 8. Tài liệu thêm

- [`docs/DESIGN.md`](docs/DESIGN.md) — thuật toán, độ phức tạp, và **vì sao** từng quyết định được
  chọn, kèm những phương án đã cân nhắc rồi loại.
- [`docs/REQUIREMENTS-TRACEABILITY.md`](docs/REQUIREMENTS-TRACEABILITY.md) — bảng truy vết từng
  gạch đầu dòng của đề bài tới đoạn code và test tương ứng.
- [`docs/REVIEW-CHECKLIST.md`](docs/REVIEW-CHECKLIST.md) — phần tự review trước khi nộp.

## Giấy phép

[MIT](LICENSE)
