# Phase 4: Cleanup Ephemeral Artifacts

**Purpose**: Remove temporary planning files, keep spec.md as source of truth.

**Prerequisites**: All agents completed successfully, spec.md generated.

---

## Checklist

```
Cleanup Progress:
- [ ] Step 4.1: Verify spec.md exists
- [ ] Step 4.2: Remove ephemeral artifacts
- [ ] Step 4.3: Generate final report
```

---

## Step 4.1: Verify spec.md Exists

```bash
ls -la .claude/docs/{featurename}/spec.md
```

**If spec.md exists**: Proceed to cleanup

**If spec.md missing**: Do NOT proceed. Check integration agent output.

---

## Step 4.2: Remove Ephemeral Artifacts

Remove temporary planning files:

```bash
rm -f .claude/docs/{featurename}/prd.md
rm -f .claude/docs/{featurename}/tasks.md
rm -f .claude/docs/{featurename}/task-*.md
```

### What Gets Deleted

| File | Purpose | Why Delete |
|------|---------|------------|
| `prd.md` | Planning document | Superseded by spec.md |
| `tasks.md` | Task summary | Work complete |
| `task-*.md` | Individual tasks | Work complete |

### What Remains

| File | Purpose | Permanent |
|------|---------|-----------|
| `spec.md` | Living specification | ✅ Source of truth |
| `review.md` | Code review results | ✅ If exists |
| `fixes.md` | Applied fixes | ✅ If exists |

---

## Step 4.3: Generate Final Report

```markdown
## Feature Complete: {FeatureName}

### Implementation Summary
✅ Data layer implemented
✅ UI layer implemented
✅ Integration complete
✅ Build passing + ktlint formatted

### Documentation
✅ Living spec: `.claude/docs/{featurename}/spec.md`
✅ Ephemeral artifacts cleaned

### Files Created

#### Feature Module
- `feature/{featurename}/build.gradle.kts`
- `feature/{featurename}/src/commonMain/kotlin/{PKG_PATH}/{featurename}/`
  - `data/model/*.kt`
  - `data/remote/*.kt`
  - `data/datasource/*.kt`
  - `data/repository/*.kt`
  - `presentation/*.kt`
  - `presentation/ui/*.kt`
  - `presentation/navigation/*.kt`
  - `di/*.kt`

#### Integration Points Modified
- `settings.gradle.kts`
- `composeApp/build.gradle.kts`
- `{INIT_KOIN_PATH}`
- `{NAV_HOST_PATH}`

### Next Steps
1. Test navigation: `navController.navigate({FeatureName}Route)`
2. Review spec: `.claude/docs/{featurename}/spec.md`
3. Run `/feature-review {featurename}` if desired
```

---

## Suggesting Code Review

After feature completion, suggest running the code review:

```
"Feature implementation complete! Would you like to run /feature-review
to validate against Clean Architecture guidelines?"
```

If user agrees, run: `/feature-review {featurename}`

---

## Output

Feature creation workflow complete:
- All code implemented
- Build passing
- spec.md is the permanent source of truth
- Ephemeral artifacts cleaned up
- Feature ready for testing
