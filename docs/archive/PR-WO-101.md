## 1. Specification

- Issue: WO-101
- Spec/document:
  - [docs/WO-101-issue.md](WO-101-issue.md)
  - [docs/WO-101-lab-summary.md](WO-101-lab-summary.md)
  - [docs/br-analysis-wo.md](br-analysis-wo.md)
  - [docs/code-review-wo.md](code-review-wo.md)

## 2. Summary

- Add the repository-level Rules Pack for coding, API, and security guardrails.
- Add workspace Copilot instructions to guide AI-assisted development in the project.
- Add a scratch implementation and test to validate the behavior expected by the lab.
- Add Business Requirements Analysis for the Work Order feature and review notes. 
- Confirm the project compiles and the validation tests pass.

## 3. Test Plan & Proof

- [x] Unit tests đã được thêm hoặc cập nhật.
- [x] Integration tests đã được thêm hoặc cập nhật khi cần.
- [x] `mvn verify` đã chạy thành công.
- [x] Log hoặc bằng chứng kiểm thử:

```text
Set-Location 'd:/ai-native-core'; mvn test

[INFO] BUILD SUCCESS
[INFO] Tests run: 2, Failures: 0, Errors: 0, Skipped: 0
```

## 4. AI Usage Disclosure

- [x] Có sử dụng GitHub Copilot hoặc AI cho boilerplate, test hoặc autocomplete.
- [x] Đã đọc và xác minh toàn bộ logic do AI hỗ trợ.
- [x] Đã kiểm tra package, API, edge case và security impact.

## 5. Security Checklist

- [x] Không có secret, API key hoặc credential hardcoded.
- [x] Input public đã được validation phù hợp.
- [x] Log không làm lộ dữ liệu nhạy cảm.

## 6. Pre-merge Checklist

- [x] Branch đã cập nhật với `main`.
- [x] Tài liệu đã được cập nhật.
- [x] Có human approval bắt buộc.
