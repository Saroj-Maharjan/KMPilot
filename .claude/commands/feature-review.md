---
description: Review a KMP feature against architecture patterns and spec
allowed-tools: ["Task", "Read", "Glob", "Grep", "Write"]
---

# Review Feature Implementation

Review a KMP feature against Clean Architecture, 10 critical rules, and 4 integration points.

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

### Architecture Rules (10)
1. Interface + Impl pairs
2. Either<T> returns
3. setState usage
4. 4 UI states
5. X-components
6. ImmutableList
7. Lowercase packages
8. DI binding pattern
9. No UseCases
10. Callback parameters

### Integration Points (4)
1. settings.gradle.kts
2. composeApp/build.gradle.kts
3. initKoin.kt
4. BaseAppNavHost.kt

### Spec Compliance (if spec exists)
- Data Models, Interfaces, State, Navigation

## Output

| Status | Meaning |
|--------|---------|
| **PASS** | All rules and integrations pass |
| **PASS WITH WARNINGS** | Minor issues, non-blocking |
| **FAIL** | Critical violations found |

## After Review

```bash
cat .claude/docs/{featurename}/review.md  # View review
cat .claude/docs/{featurename}/fixes.md   # View fixes
/audit-spec {featurename} --compare       # Check drift
```
