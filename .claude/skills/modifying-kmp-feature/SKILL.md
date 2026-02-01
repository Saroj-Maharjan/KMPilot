---
description: Modify existing KMP features with spec-first workflow. Invoke with /modifying-kmp-feature.
allowed-tools: ["Task", "Read", "Write", "Edit", "Glob", "Grep", "Bash(./gradlew:*)", "AskUserQuestion"]
---

# Modifying KMP Features

Apply changes to existing features using spec-first workflow.

**Architecture Reference:** @../_shared/patterns.md

## Workflow

**Parse** → **Spec Check** → **Understand** → **Plan** → **Draft Spec** → [USER APPROVES] → **Implement** → **Validate** → **Update Spec** → ✅ Done

### Step 1: Parse Feature Name
Extract from request: "add sorting to productlist" → `productlist`
Validate: `ls feature/{featurename}/src/commonMain/kotlin/`

### Step 2: Spec Check
Load `.claude/docs/{featurename}/spec.md`
If missing: Run `/audit-spec {featurename}` first

### Step 3: Understand Current Implementation
Read spec sections: Requirements, Architecture, State Management, Navigation

### Step 4: Plan Changes
Determine affected layers and load architecture as needed:
- Data changes: @../creating-kmp-feature/architecture/data.md
- UI changes: @../creating-kmp-feature/architecture/ui.md
- Integration changes: @../creating-kmp-feature/architecture/integration.md

### Step 5: Draft Spec Changes
Propose updates using diff format:
```markdown
## Proposed Spec Changes: {featurename}
**Current Version:** X.Y.Z → **Proposed:** X.Y+1.Z

### Section N: {Name}
```diff
  existing content
+ added content
- removed content
```
### Rationale
{Why these changes are needed}
```

### Step 6: Review Gate (REQUIRED)
Present changes to user with:
- [ ] **Approve** - Proceed with implementation
- [ ] **Modify** - Request changes
- [ ] **Reject** - Do not proceed

**Never skip this step.**

### Step 7: Implement Changes
Follow patterns from @../_shared/patterns.md (includes UI Implementation Workflow)

For UI changes: Load @../using-design-system/references/component-mappings.md

### Step 8: Validate Build
```bash
./gradlew :feature:{featurename}:assembleAndroidMain
./gradlew :feature:{featurename}:ktlintFormat
```

### Step 9: Update Specification
Apply APPROVED changes (don't regenerate). Add changelog:
```markdown
## Last Updated
- {YYYY-MM-DD} - {Brief description}
```
Version bump: Patch (X.Y.Z+1) for fixes, Minor (X.Y+1.0) for features

## Error Handling

Build errors: Load @../creating-kmp-feature/troubleshooting/index.md
Design system: Activate `/using-design-system`

## Completion Checklist

- [ ] Spec changes drafted and USER APPROVED
- [ ] Build passes
- [ ] Code formatted (ktlint)
- [ ] Spec updated with approved changes
- [ ] Changelog entry added
- [ ] Version bumped
