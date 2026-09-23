# WO-101: Context Engineering and AI review setup for ai-native-core

## 1. Objective
Establish the repository baseline for AI-assisted development by adding a rules pack, workspace instructions, and a working example of domain analysis and code review for the Work Order scenario.

## 2. Problem statement
Without clear repository rules and context, AI-generated code is likely to:
- invent behavior beyond the requested scope
- ignore security and validation boundaries
- mis-handle API contract assumptions
- produce unreviewed or unsafe draft logic

## 3. Acceptance criteria
- [ ] `docs/coding-rules.md` exists and defines the required Java coding conventions.
- [ ] `docs/api-rules.md` exists and defines API contract expectations.
- [ ] `docs/security-rules.md` exists and defines the minimum security guardrails.
- [ ] `.github/copilot-instructions.md` exists with repo-level instructions for Copilot.
- [ ] `docs/br-analysis-wo.md` exists and captures the business analysis for the Work Order feature.
- [ ] `docs/code-review-wo.md` exists and documents the review priority order.
- [ ] A working scratch implementation and unit test are included for validation.
- [ ] `mvn test` passes successfully.

## 4. Implementation notes
This issue is intentionally scoped to enabling safe and reviewable AI-assisted work. It is a scaffold and governance step, not a production feature implementation.

## 5. Proof
Executed validation:

```bash
Set-Location 'd:/ai-native-core'; mvn test
```

Evidence:
- BUILD SUCCESS
- 2 tests run
- 0 failures
- 0 errors

## 6. AI usage disclosure
GitHub Copilot was used to draft the rules and analysis artifacts. All outputs were reviewed against the repository guidance and validated with Maven tests.
