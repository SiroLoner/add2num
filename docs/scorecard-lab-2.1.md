# Scorecard Lab 2.1

## Scope
Đánh giá bản draft code do Copilot sinh dựa trên Rules Pack trong `docs/coding-rules.md`, `docs/api-rules.md`, và `docs/security-rules.md`.

| # | Tiêu chí | Kết quả | Bằng chứng |
|---|---|---|---|
| 1 | Đúng Java 17 baseline | Pass | Project uses Java 17 in `pom.xml` |
| 2 | Không hardcode secret | Pass | No secret values in source or docs |
| 3 | Validation input | Pass | Draft validates null/blank/invalid values |
| 4 | Không dùng logger unsafe | Pass | Uses structured logger pattern, not `System.out.println` |
| 5 | Không có injection | Pass | Parameterized/validated logic, no raw SQL string concatenation |
| 6 | Exception handling specific | Pass | Throws `IllegalArgumentException` for invalid input |
| 7 | Chỉ làm đúng scope task | Pass | Function only covers sum of two numeric inputs |
| 8 | API contract conformance | N/A | No public API contract is defined yet for this draft |
| 9 | Authorization boundary | Pass | No endpoint security boundary exists in draft; if exposed, it must be checked separately |
| 10 | Testability | Pass | JUnit test exists for valid/invalid inputs |
| 11 | Human review readiness | Pass | Draft is inspected against rules before being accepted |
| 12 | Traceability to project rules | Pass | Connected to docs rules, not ad-hoc behavior |

## Overall verdict
The draft is acceptable as a scratch implementation for lab validation because it adheres to the project baseline and security guardrails. Any production use still requires human review and contract confirmation.
