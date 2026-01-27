---
description: Show health status for all feature modules
allowed-tools: ["Bash(./gradlew:*)", "Bash(ls:*)", "Glob", "Grep", "Read"]
model: haiku
---

# Feature Health Dashboard

Display health status for all feature modules in the project. Shows spec, tests, and review status at a glance.

## Usage

```bash
/features-health
```

## Process

### Step 1: Discover All Features

```bash
ls -d feature/*/build.gradle.kts 2>/dev/null | xargs -I{} dirname {} | xargs -I{} basename {}
```

### Step 2: Check Each Feature

For each feature, check:

#### Spec Status
```bash
# Check if spec exists
test -f .claude/docs/{feature}/spec.md && echo "exists" || echo "missing"
```

#### Tests Status
```bash
# Check if test files exist
count=$(ls feature/{feature}/src/commonTest/kotlin/**/*Test.kt 2>/dev/null | wc -l)

# Check last test run result (if available)
test -d feature/{feature}/build/test-results && echo "results available" || echo "not run"
```

#### Review Status
```bash
# Check if review has been done
test -f .claude/docs/{feature}/review.md && echo "exists" || echo "missing"
```

### Step 3: Generate Report

Output format:

```markdown
## Feature Health Report

| Feature | Spec | Tests | Review | Actions |
|---------|------|-------|--------|---------|
| sample | ✅ | ✅ 7 files | ✅ | - |
| profile | ✅ | ⚠️ 2 files | ❌ | Run `/feature-review profile` |
| login | ❌ | ❌ 0 files | ❌ | Run `/audit-spec login` |

### Summary
- **Total Features:** 3
- **With Spec:** 2/3
- **With Tests:** 2/3
- **With Review:** 1/3

### Recommended Actions
1. `login` - Missing spec, run `/audit-spec login`
2. `login` - No tests, run `/feature-test login`
3. `profile` - No review, run `/feature-review profile`
```

## Health Criteria

| Check | ✅ Pass | ⚠️ Warning | ❌ Fail |
|-------|---------|------------|---------|
| Spec | spec.md exists | - | spec.md missing |
| Tests | 5+ test files | 1-4 test files | 0 test files |
| Review | review.md exists | - | review.md missing |

## After Health Check

Based on findings:

```bash
# For missing specs
/audit-spec {featurename}

# For missing/low tests
/feature-test {featurename}

# For missing review
/feature-review {featurename}

# For full coverage report
/coverage
```

## Notes

- Spec check looks for `.claude/docs/{feature}/spec.md`
- Test count is based on `*Test.kt` files in commonTest
- Review check looks for `.claude/docs/{feature}/review.md`
- Does not run tests (use `/feature-test` for that)
