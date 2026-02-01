---
description: Show health status for all feature modules
allowed-tools: ["Bash(ls:*)", "Glob", "Grep", "Read"]
model: haiku
---

# Feature Health Dashboard

Display health status for all feature modules.

## Usage

```bash
/features-health
```

## Process

1. **Discover Features**: `ls -d feature/*/build.gradle.kts`
2. **Check Each Feature**:
   - Spec: `.claude/docs/{feature}/spec.md` exists?
   - Tests: `*Test.kt` files in commonTest?
   - Review: `.claude/docs/{feature}/review.md` exists?

## Output

```markdown
## Feature Health Report

| Feature | Spec | Tests | Review | Actions |
|---------|------|-------|--------|---------|
| sample | ✅ | ✅ 7 files | ✅ | - |
| profile | ✅ | ⚠️ 2 files | ❌ | /feature-review profile |
| login | ❌ | ❌ 0 files | ❌ | /audit-spec login |

### Summary
- **Total:** 3 features
- **With Spec:** 2/3
- **With Tests:** 2/3
- **With Review:** 1/3
```

## Health Criteria

| Check | ✅ Pass | ⚠️ Warning | ❌ Fail |
|-------|---------|------------|---------|
| Spec | exists | - | missing |
| Tests | 5+ files | 1-4 files | 0 files |
| Review | exists | - | missing |

## After Health Check

```bash
/audit-spec {feature}      # Missing spec
/feature-test {feature}    # Missing tests
/feature-review {feature}  # Missing review
/coverage                  # Full coverage report
```
