# Phase 4: Cleanup Ephemeral Artifacts

**Purpose**: Remove temporary planning files, keep spec.md as source of truth.

**Prerequisites**: All agents completed successfully, spec.md generated.

---

## Checklist

```
Cleanup Progress:
- [ ] Step 4.1: Verify spec.md exists
- [ ] Step 4.2: Rule 11 guardrail (grep gate)
- [ ] Step 4.3: Remove ephemeral artifacts
- [ ] Step 4.4: Generate final report
```

---

## Step 4.1: Verify spec.md Exists

```bash
ls -la .claude/docs/{featurename}/spec.md
```

**If spec.md exists**: Proceed to cleanup

**If spec.md missing**: Do NOT proceed. Check integration agent output.

---

## Step 4.2: Rule 11 Guardrail (Grep Gate)

Mechanical check that the feature follows the single-UiModel / DTO-wrapped-UiState convention. Two greps; both must return empty.

```bash
# (a) No data→presentation imports
grep -rEn 'import\s+\S+\.presentation\.' \
  feature/{featurename}/src/commonMain/kotlin/**/data/ \
  && echo "❌ Rule 11 violation: data layer imports from presentation" && exit 1

# (b) No *UiState.kt file (collapsed into *UiModel.kt under Rule 11)
find feature/{featurename}/src/commonMain/kotlin -name '*UiState.kt' \
  | grep . && echo "❌ Rule 11 violation: *UiState.kt found; collapse into *UiModel.kt" && exit 1
```

**If either check fails**: Stop. Surface the violation to the user with the file path. Do NOT proceed to artifact cleanup — the feature is broken architecturally and the ephemeral artifacts (prd.md, tasks.md) are still needed for the fix.

**If both pass**: Proceed to Step 4.3.

---

## Step 4.3: Remove Ephemeral Artifacts

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

## Step 4.4: Generate Final Report

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

### What's next
- Test navigation: `navController.navigate({FeatureName}Route)`
- Review spec: `.claude/docs/{featurename}/spec.md`

---

> **Next step —** run `/feature-review {featurename}` to validate against Clean Architecture guidelines.
```

---

## Output

Feature creation workflow complete:
- All code implemented
- Build passing
- spec.md is the permanent source of truth
- Ephemeral artifacts cleaned up
- Feature ready for testing
