# Addition API Specification

## Endpoint

`POST /api/v1/sum`

Request và response dùng `application/json`.

## Request

```json
{
  "first": "1234",
  "second": "897",
  "maxSteps": 10
}
```

| Field | Required | Contract |
|---|---:|---|
| `first` | Có | Chuỗi chữ số thập phân, cho phép whitespace ở hai đầu; không vượt quá `add2num.max-input-digits` |
| `second` | Có | Như `first` |
| `maxSteps` | Không | Số nguyên không âm; bỏ qua nghĩa là dùng giới hạn mặc định; giá trị lớn bị clamp theo cấu hình |

## Success response

HTTP `200 OK`:

```json
{
  "value": "2131",
  "digitCount": 4,
  "totalSteps": 4,
  "returnedSteps": 4,
  "truncated": false,
  "carryCount": 3,
  "elapsedMillis": 0.021,
  "steps": []
}
```

`steps` trong ví dụ được rút gọn; thứ tự thực tế từ cột units lên cột cao hơn.

## Error response

- `400 Bad Request`: JSON sai, field thiếu/sai, operand vượt giới hạn hoặc `maxSteps` âm.
- `415 Unsupported Media Type`: request không phải JSON.
- `500 Internal Server Error`: lỗi nội bộ không dự kiến; response không chứa stack trace.

Ví dụ lỗi validation:

```json
{
  "status": 400,
  "error": "Bad Request",
  "message": "The request contains invalid operands.",
  "fields": [
    { "field": "maxSteps", "message": "maxSteps must not be negative" }
  ]
}
```

## Acceptance criteria

- Với `1234` và `897`, API trả `2131` và trace có bước đầu `4 + 7 + 0 = 11`.
- Với operand không phải chữ số, API trả `400` và chỉ ra field lỗi.
- Với `maxSteps < 0`, API trả `400`, không âm thầm đổi request thành `0`.
- Với `maxSteps` vượt giới hạn, API clamp số bước trả về nhưng vẫn giữ kết quả đầy đủ.
- Không có operand hoặc stack trace nhạy cảm trong log lỗi.