---
description: Review a KMP feature against architecture patterns and spec
allowed-tools: ["Task", "Read", "Glob", "Grep", "Write"]
---

# Review Feature Implementation

Review a KMP feature against Clean Architecture, 11 critical rules, and 4 integration points.

**Architecture Reference:** @../skills/_shared/patterns.md

## Usage

```bash
/feature-review {featurename}
```

## Process

1. **Validate**: `ls feature/{featurename}/src/commonMain/kotlin/`
2. **Spawn Agent**: Delegate to `code-reviewer` agent
3. **Generate Reports**: `.claude/docs/{featurename}/review.md` and `fixes.md`

## What Gets Checked

### Architecture Rules (11)
1. Interface + Impl pairs
2. Either<T> returns
3. setState usage
4. 4 UI states
5. X-components (Material3 *components* forbidden — `MaterialTheme.colorScheme/typography` access is allowed because `XTheme` wraps MaterialTheme)
6. ImmutableList
7. Lowercase packages
8. DI binding pattern
9. No UseCases
10. Callback parameters
11. Single UiModel + DTO-wrapped UiState (no `*UiState.kt`; `UiState<T>` wraps DTOs from `data/model/`; no `presentation` imports in `data/`)

### Integration Points (4)
1. settings.gradle.kts
2. composeApp/build.gradle.kts
3. initKoin.kt
4. BaseAppNavHost.kt

### Spec Compliance (if spec exists)
- Data Models, Interfaces, State, Navigation

### Design-Aware Compliance (if blueprint exists)
- Blueprint present at `.claude/docs/{featurename}/designs/{featurename}_blueprint.md`
- `blueprintConsumed: true` in `.claude/docs/_project/stitch-project.json` under `features.{featurename}`
- A `false` flag with a blueprint present means implementation skipped the design pipeline

### UI File Organization
- `{Feature}Screen.kt` allowlist (5 slots, nothing else): `{Feature}Screen`, `{Feature}ScreenRoot`, and optionally `LoadingContent` / `FailedContent` / `EmptyContent` — the three state shells appear only when the design specifies a dedicated screen for that state
- Every other composable, **including `{Feature}Content`** and its sub-components, lives in `presentation/ui/components/{Name}.kt` — one file per component
- **Utilities** (non-`@Composable` helpers: formatters, validators) live in `presentation/ui/{Feature}Utils.kt`, never under `components/`
- **`@Preview` composables** live in the **same file** as the composable they preview (marked `private`), and are exempt from the allowlist
- Preview import must be `androidx.compose.ui.tooling.preview.Preview` (CMP 1.11.0+); the older `org.jetbrains.compose...` is deprecated
- Reference: `patterns.md` "UI File Organization" section

## Output

| Status | Meaning |
|--------|---------|
| **PASS** | All rules and integrations pass |
| **PASS WITH WARNINGS** | Minor issues, non-blocking |
| **FAIL** | Critical violations found |

## After Review

Reports are saved to:
- `.claude/docs/{featurename}/review.md` — full review
- `.claude/docs/{featurename}/fixes.md` — actionable fixes

Optional: run `/audit-spec {featurename} --compare` to check spec drift.

Pick the matching literal footer based on the review status and emit it as the very last line of output.

**If status is PASS:**

```
---

> **Next step —** run `/feature-test {featurename}` to generate comprehensive tests for the feature.
```

**If status is PASS WITH WARNINGS or FAIL:**

```
---

> **Next step —** run `/modifying-kmp-feature {featurename} apply fixes from @.claude/docs/{featurename}/fixes.md` to address the review findings.
```
