# Tự review trước khi nộp

Danh sách này được chạy qua trước khi tạo commit cuối. Mục đích không phải để trang trí, mà để
những lỗi dễ đoán bị bắt tại đây thay vì trong buổi review với mentor.

## Đúng yêu cầu

- [x] Tên class đúng nguyên văn: `MyBigNumber`
- [x] Chữ ký đúng nguyên văn: `String sum(String stn1, String stn2)`, giữ cả tên tham số
- [x] Ví dụ `1234 + 897 = 2131` trong tài liệu có test riêng, kể cả bước `4 + 7 = 11, viết 1 nhớ 1`
- [x] Có logging
- [x] Unit test nằm ở source root riêng
- [x] Có `README.md` hướng dẫn build và chạy
- [x] Phiên bản TASK 1 là `0.0.1`, có nhánh `core` và tag `v0.0.1`
- [x] TASK 2 dùng Spring Boot, Thymeleaf, Bootstrap **5.2.0**
- [x] TASK 2 tái sử dụng TASK 1 qua dependency, không copy code
- [x] TASK 2 có phần hiển thị tiến trình tính toán
- [x] Bảng truy vết từng yêu cầu: `docs/REQUIREMENTS-TRACEABILITY.md`

## Đúng về mặt tính toán

- [x] `1234 + 897 = 2131`
- [x] `999 + 1 = 1000` — nhớ tràn ra chữ số mới
- [x] `9…9 + 1` với độ dài tới 1000 — nhớ dây chuyền
- [x] Hai số khác độ dài, cả hai chiều
- [x] `007 + 3 = 10` — số 0 ở đầu
- [x] `0 + 0 = 0`, `0000 + 00 = 0` — không trả chuỗi rỗng
- [x] `"" + "" = 0`
- [x] Vét cạn toàn bộ `0..999 × 0..999`
- [x] Vét cạn có số 0 ở đầu, độ rộng 1..4
- [x] Đối chiếu `BigInteger` trên mẫu ngẫu nhiên, seed cố định
- [x] Giao hoán, kết hợp, phần tử đơn vị
- [x] Hai số 1.000.000 chữ số, đối chiếu với `BigInteger`

Tổng cộng **1.707.466** phép so sánh đã chạy qua trước khi commit.

## Chất lượng code

- [x] Một lượt duyệt, `O(n)` thời gian và bộ nhớ
- [x] Không nối chuỗi trong vòng lặp
- [x] Không phép chia trong vòng lặp nóng
- [x] Không dùng `BigInteger`/`BigDecimal`/`long` trong `src/main`
- [x] Thư viện lõi không có dependency runtime nào
- [x] Không có annotation Spring trong thư viện lõi
- [x] Lớp lõi là stateless và an toàn đa luồng, có test chứng minh
- [x] Danh sách trả cho người gọi là bất biến
- [x] Thông báo lỗi nêu tên tham số, ký tự sai và vị trí
- [x] Biên dịch với `-Xlint:all`, không cảnh báo
- [x] Phiên bản dependency và plugin đều được ghim

## Vận hành

- [x] Đầu vào bị giới hạn độ dài, cấu hình được
- [x] Số bước hiển thị bị chặn trên, có cờ báo khi bị cắt bớt
- [x] Lỗi API trả về một cấu trúc thống nhất, không lộ stack trace
- [x] Request sai cú pháp trả 400 chứ không phải 500
- [x] Bootstrap phục vụ từ chính ứng dụng, chạy được khi mất mạng
- [x] Có health check
- [x] Có Dockerfile, chạy bằng user không phải root
- [x] Có CI chạy đúng các lệnh ghi trong README

## Đã tự hỏi và trả lời

**Nếu đầu vào có 100 triệu chữ số thì sao?** Tầng web từ chối ở mức 100.000, cấu hình được. Thư
viện lõi vẫn nhận, và vẫn chạy tuyến tính, giới hạn thật là heap.

**Nếu hai người cùng gọi một lúc?** `MyBigNumber` không có trạng thái; có test chạy 8 luồng trên
cùng một instance.

**Nếu tắt JavaScript?** Trang vẫn hiện đầy đủ kết quả và toàn bộ các cột trong khối `<noscript>`.

**Nếu ai đó nhập `<script>` vào ô nhập?** Form từ chối vì không phải chữ số; ngoài ra Thymeleaf
escape mặc định và JavaScript ghi bằng `textContent` chứ không phải `innerHTML`.

**Nếu mentor chỉ có 5 phút?** `README.md` §1 có đúng ba lệnh, và
`docs/REQUIREMENTS-TRACEABILITY.md` trả lời được câu "yêu cầu này nằm ở đâu" mà không cần đọc code.

## Chưa làm, và biết là chưa làm

- Chỉ hỗ trợ số nguyên không âm; đề không yêu cầu số âm hay số thập phân.
- Chưa có trừ, nhân, chia — ngoài phạm vi.
- Chưa có i18n; giao diện tiếng Việt, API và Javadoc tiếng Anh.
- Chưa đo bằng JMH; test hiệu năng hiện tại đủ để bắt lỗi sai bậc độ phức tạp, không phải để đo
  chính xác vài phần trăm.
