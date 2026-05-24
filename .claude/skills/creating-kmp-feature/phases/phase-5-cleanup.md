# Phase 5: Cleanup Ephemeral Artifacts

**Purpose**: Remove temporary planning files, keep spec.md as source of truth.

**Prerequisites**: All agents completed successfully, spec.md generated.

---

## Checklist

```
Cleanup Progress:
- [ ] Step 5.1: Verify spec.md exists
- [ ] Step 5.2: Rule 11 guardrail (grep gate)
- [ ] Step 5.3: Remove ephemeral artifacts
- [ ] Step 5.4: Generate final report
```

---

## Step 5.1: Verify spec.md Exists

```bash
ls -la .claude/docs/{featurename}/spec.md
```

**If spec.md exists**: Proceed to cleanup

**If spec.md missing**: Do NOT proceed. Check integration agent output.

---

## Step 5.2: Guardrail Checks (Grep Gate)

Mechanical checks that the feature follows architectural conventions. All must pass.

```bash
# (a) No data→presentation imports (Rule 11)
grep -rEn 'import\s+\S+\.presentation\.' \
  feature/{featurename}/src/commonMain/kotlin/**/data/ \
  && echo "❌ Rule 11 violation: data layer imports from presentation" && exit 1

# (b) No *UiState.kt file (Rule 11)
find feature/{featurename}/src/commonMain/kotlin -name '*UiState.kt' \
  | grep . && echo "❌ Rule 11 violation: *UiState.kt found; collapse into *UiModel.kt" && exit 1

# (c) @Preview exists in UI files
grep -rn "@Preview" feature/{featurename}/src/commonMain/kotlin \
  | grep -v "^Binary" | grep . \
  || echo "⚠️ No @Preview composables found in feature/{featurename}. Add previews per patterns.md § Previews."
```

**If (a) or (b) fails**: Stop. Surface the violation to the user. Do NOT proceed to artifact cleanup.

**If (c) finds no previews**: Surface the warning but do NOT block cleanup — previews are required but a missing preview does not break the build or architecture. The user may choose to add them via `/modifying-kmp-feature`.

**If all pass**: Proceed to Step 5.3.

---

## Step 5.3: Remove Ephemeral Artifacts

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

## Step 5.4: Generate Final Report

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
