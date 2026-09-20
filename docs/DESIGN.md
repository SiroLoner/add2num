# Thiết kế và các quyết định

Tài liệu này giải thích **vì sao** code trông như hiện tại. Phần *đang làm gì* đã nằm trong Javadoc;
phần dưới đây là những lựa chọn có thể làm khác đi, và lý do đã chọn hướng này.

---

## 1. Thuật toán

### Ý tưởng

Đúng như đề bài mô tả: cộng như học sinh lớp 3.

```
      111          <- số nhớ
      1234
    +  897
    ------
      2131
```

Duyệt hai chuỗi **từ phải sang trái**. Ở mỗi cột, lấy chữ số của mỗi số (nếu số đó đã hết chữ số thì
coi như 0), cộng với số nhớ, viết chữ số cuối của tổng, giữ phần còn lại làm số nhớ cho cột sau.

### Vòng lặp lõi

```java
while (left >= 0 || right >= 0) {
    int leftDigit  = (left  >= 0) ? stn1.charAt(left)  - '0' : 0;
    int rightDigit = (right >= 0) ? stn2.charAt(right) - '0' : 0;

    int total    = leftDigit + rightDigit + carry;
    int carryOut = (total > 9) ? 1 : 0;
    int digit    = total - (carryOut * 10);

    buffer[write--] = (char) ('0' + digit);
    carry = carryOut;
    left--; right--;
}
```

Bốn chi tiết trong đoạn này là có chủ đích:

**Một lượt duy nhất.** Không đảo chuỗi, không tách `split`, không chuyển sang mảng trung gian.
Mỗi ký tự được đọc đúng một lần.

**Ghi vào `char[]` đã cấp phát sẵn, không nối chuỗi.** `String` trong Java là bất biến; nếu dùng
`result = digit + result` trong vòng lặp thì mỗi vòng sẽ sao chép lại toàn bộ kết quả và thuật toán
tụt từ `O(n)` xuống `O(n²)`. Với 1.000.000 chữ số, khác biệt là *mili giây* so với *hàng phút*.
Kích thước bộ đệm là `max(len1, len2) + 1` — thêm đúng một ô cho số nhớ cuối cùng, ví dụ
`999 + 1 = 1000`.

**Không có phép chia.** Tổng lớn nhất có thể là `9 + 9 + 1 = 19`, nên số nhớ chỉ có thể là 0 hoặc 1.
Một phép so sánh thay được cho `/ 10` và `% 10`. Phép chia số nguyên là lệnh đắt nhất có thể xuất
hiện trong vòng lặp này, và đây là vòng lặp chạy một lần cho mỗi chữ số.

**Ghi từ phải sang trái vào đúng vị trí cuối cùng.** Nhờ vậy không cần bước đảo ngược kết quả ở cuối.

### Độ phức tạp

Với `n` là độ dài của số dài hơn: **thời gian `O(n)`**, **bộ nhớ phụ `O(n)`** — đúng bằng chỗ chứa
kết quả, tức là mức tối thiểu.

Con số thực đo: hai số 1.000.000 chữ số cộng hết khoảng **35 ms**.
Có một test khẳng định khi tăng đầu vào gấp 4 lần thì thời gian **không** tăng gấp 16 lần, để một
thay đổi vô tình đưa thuật toán về bậc hai sẽ làm build đỏ ngay.

### Vì sao không dùng `BigInteger`

Vì đó chính là bài tập. `BigInteger` chỉ xuất hiện trong `src/test`, đóng vai trò lời giải tham
chiếu — một cách kiểm chứng độc lập, được viết bởi người khác, để đối chiếu hàng triệu kết quả.
Dùng nó trong `src/main` sẽ là làm sai đề; dùng nó trong `src/test` là tận dụng một công cụ kiểm
chứng có sẵn và rất đáng tin.

---

## 2. Các quyết định về hợp đồng

### Vì sao vẫn kiểm tra đầu vào

Tài liệu yêu cầu ghi rõ: *"Giả định: giá trị tham số truyền vào hàm là đúng, chỉ chứa các kí số hợp
lệ."* Tức là được phép bỏ qua validation.

Dự án vẫn kiểm tra, vì ba lý do:

1. **Giá gần như bằng không.** Một lượt quét tuyến tính nữa trên dữ liệu vốn đã phải đọc hết.
2. **Đổi một kết quả sai âm thầm lấy một lỗi rõ ràng.** Không kiểm tra thì `sum("12a4", "1")` vẫn
   chạy: `'a' - '0'` ra 49, và hàm trả về một con số trông hợp lý nhưng sai. Đó là kiểu lỗi tốn
   hàng giờ để truy.
3. **Thư viện sẽ được người khác gọi.** Module `add2num-web` gọi nó, và một ngày nào đó sẽ có thứ
   khác gọi nữa. Một hợp đồng chỉ nằm trong Javadoc thì không có gì bảo đảm nó được tôn trọng.

Thông báo lỗi nêu rõ **tên tham số, ký tự vi phạm, và vị trí**: `stn1 must contain decimal digits
only, but found 'a' (U+0061) at index 2`. Một thông báo lỗi chỉ ghi "invalid input" thì chỉ tốt
hơn việc im lặng một chút.

Việc kiểm tra được tách thành hàm riêng thay vì nhét vào vòng lặp cộng, để vòng lặp nóng giữ được
kích thước nhỏ, dễ được JIT inline, và để thông báo lỗi biết chính xác chỉ số vi phạm.

### Vì sao loại bỏ số 0 ở đầu kết quả

Đề không nói gì về `"007" + "3"`. Hai lựa chọn: trả `"010"` (đúng nghĩa đen của cách cộng tay) hoặc
trả `"10"` (dạng chuẩn của một con số).

Chọn `"10"`, vì kết quả của một phép cộng là **một con số**, và cách viết chuẩn của con số đó không
có số 0 dẫn đầu. Nếu giữ `"010"` thì `sum(sum(a,b), c)` có thể khác `sum(a, sum(b,c))` về mặt chuỗi,
và tính chất kết hợp — vốn được test kiểm tra — sẽ vỡ.

Một ngoại lệ quan trọng: kết quả bằng 0 vẫn trả `"0"`, không trả chuỗi rỗng.

### Vì sao chuỗi rỗng được chấp nhận

`"" + ""` trả `"0"` thay vì ném lỗi hay trả `""`. Chuỗi rỗng không chứa ký tự không hợp lệ nào, nên
khó lòng gọi nó là "dữ liệu bẩn"; và một ô nhập liệu để trống rất tự nhiên sẽ đi tới đây. Trả chuỗi
rỗng thì người gọi phải tự xử lý một giá trị không phải số.

---

## 3. Kiến trúc

### Vì sao chia hai module

Đề yêu cầu TASK 2 *"tái sử dụng kết quả Task 1 như một sub-module hoặc thư viện .jar"*. Cách trung
thực nhất với yêu cầu đó là một dự án Maven nhiều module, trong đó `add2num-web` khai báo
`add2num-core` là dependency.

Lợi ích cụ thể: `add2num-core` build ra một file `.jar` độc lập, có thể đem dùng ở nơi khác, và
**không thể** vô tình tham chiếu ngược vào tầng web. Ranh giới được trình biên dịch bảo vệ, không
chỉ nằm trong quy ước.

### Vì sao `add2num-core` không có dependency nào

`add2num-core/pom.xml` không có dependency ở scope `compile` hay `runtime`. Logging đi qua
`java.lang.System.Logger` — một API của chính JDK — thay vì SLF4J.

Lý do: một thư viện nhỏ kéo theo một framework logging là một thư viện kém lịch sự. Ứng dụng nhúng
nó vào sẽ phải chịu thêm một cây phụ thuộc và một nguồn xung đột phiên bản, để đổi lấy vài dòng log.
`System.Logger` chuyển tiếp được sang SLF4J, Log4j hay `java.util.logging` tuỳ ứng dụng cấu hình,
nên thư viện không ép ai vào lựa chọn nào.

Spring Boot bắc cầu `java.util.logging` sang Logback, nên trong `add2num-web` các dòng log này
xuất hiện bình thường.

`MyBigNumber` cũng không mang annotation Spring nào. Việc biến nó thành bean là trách nhiệm của
tầng web (`CoreConfiguration`), để thư viện còn dùng được ngoài Spring.

### Vì sao có `sumWithTrace` bên cạnh `sum`

`sum` là chữ ký đề bài yêu cầu, và nó phải nhanh: không tạo object phụ, không ghi lại gì.

Nhưng TASK 2 cần *"hiển thị tiến trình tính toán"*. Nếu tầng web tự dựng lại tiến trình, nó sẽ phải
cài lại thuật toán lần thứ hai — và hai bản cài đặt sẽ lệch nhau vào một ngày nào đó.

Nên `sumWithTrace` dùng **chung đúng vòng lặp** với `sum`, chỉ khác ở chỗ có ghi lại các bước. Cái
tiến trình hiển thị trên web vì vậy luôn là tiến trình thật của phép tính đã cho ra kết quả.

### Vì sao giới hạn số bước ghi lại

Một cặp số 10.000.000 chữ số sẽ tạo ra 10.000.000 object `SumStep` — tốn bộ nhớ gấp nhiều lần chính
kết quả, cho một danh sách mà không ai đọc hết.

`sumWithTrace` nhận tham số `maxTraceSteps`. Vượt ngưỡng thì các cột còn lại **vẫn được tính**, chỉ
không được ghi lại, và `SumResult.truncated()` báo cho tầng trên biết danh sách chỉ là một phần.
Giao diện nhờ đó nói được "hiển thị 500 trong 10.000 cột" thay vì lặng lẽ hiện thiếu.

### Vì sao view model là class chứ không phải record

`SumStep` và `SumResult` trong thư viện lõi là `record` — chúng là dữ liệu bất biến thuần tuý.

Nhưng các lớp trong `web/view` lại là class thường với getter `getX()`. Lý do: chúng được đọc bởi
Thymeleaf (qua SpEL) và bởi Jackson. Getter theo quy ước JavaBean được **mọi** phiên bản của cả hai
hiểu; khả năng đọc trực tiếp accessor của record thì phụ thuộc phiên bản. Đây là chỗ mà sự nhàm chán
đáng giá hơn sự hiện đại: không một record nào của thư viện lõi bị đưa thẳng vào template.

### Vì sao `ApiExceptionHandler` bị giới hạn phạm vi

`@RestControllerAdvice(assignableTypes = SumApiController.class)`.

Không có `assignableTypes`, advice này sẽ bắt luôn cả lỗi của controller Thymeleaf và trả JSON cho
một người đang dùng trình duyệt. Cùng lý do đó, handler `HttpMessageNotReadableException` được khai
báo riêng: nếu không, cái handler `Exception.class` ở cuối sẽ biến một request JSON sai cú pháp
thành lỗi 500 — đổ lỗi cho server một việc mà client làm sai.

---

## 4. Những phương án đã cân nhắc rồi loại

| Phương án | Vì sao loại |
|---|---|
| Đảo chuỗi rồi cộng từ trái sang | Thêm hai lần cấp phát và hai lần duyệt, không được gì |
| Cộng theo khối 9 chữ số (dùng `long`) | Nhanh hơn khoảng 3 lần, nhưng không còn là "cách cộng của học sinh lớp 3" và không thể hiện được từng cột — trái với cả đề bài lẫn yêu cầu hiển thị tiến trình |
| `StringBuilder.insert(0, digit)` | Mỗi lần chèn đầu chuỗi là một lần dịch toàn bộ nội dung: `O(n²)` |
| Dùng `%` và `/` cho số nhớ | Phép chia số nguyên trong vòng lặp nóng, trong khi một phép so sánh là đủ |
| Streaming tiến trình bằng SSE/WebSocket | Phép tính xong trong vài chục mili giây; streaming chỉ thêm trạng thái, thêm chỗ hỏng, mà không giúp người dùng thấy nhanh hơn |
| Trace theo `Consumer<SumStep>` callback | Linh hoạt hơn, nhưng đẩy việc quản lý bộ nhớ sang người gọi; một giới hạn tường minh an toàn hơn |
| `spring-boot-starter-parent` làm parent POM | Sẽ kéo Spring vào cả module core; parent POM tự viết + import BOM giữ core sạch |
| Bootstrap qua CDN | Ứng dụng sẽ hỏng giao diện khi không có mạng; WebJars nhúng sẵn 5.2.0 vào jar |

---

## 5. Giới hạn đã biết

- Chỉ cộng **số nguyên không âm**. Không có dấu âm, không có phần thập phân. Đề không yêu cầu, và
  thêm vào sẽ làm loãng phần cốt lõi.
- Độ dài số bị chặn bởi giới hạn của `String` trong Java (khoảng 2 tỉ ký tự) và bởi heap. Muốn vượt
  qua thì phải chuyển sang xử lý theo luồng, và khi đó chữ ký hàm trong đề sẽ không còn phù hợp.
- Tầng web giới hạn mỗi toán hạng ở 100.000 chữ số. Đây là lựa chọn vận hành, không phải giới hạn
  của thuật toán; đổi `add2num.max-input-digits` là xong.
