---
description: Modify existing KMP features with spec-first workflow. Automatically activates when user mentions "change feature", "modify feature", "update feature", "add to feature", or any request to modify an existing feature module.
allowed-tools: ["*"]
---

# Modifying KMP Features

Apply changes to existing KMP feature modules using a spec-first workflow: load specification, apply changes following architecture patterns, validate, update spec changelog.

## Automatic Activation

This skill activates when user mentions:
- "change feature", "modify feature", "update feature"
- "add to feature", "add [capability] to [feature]"
- "fix [feature]", "refactor [feature]"
- Any request modifying an existing feature module

## Workflow Overview

```
User Request: "modify {feature} to..."
    ↓
[0] Parse → Extract feature name, validate exists
    ↓
[1] Spec Check → Load .claude/docs/{name}/spec.md (or generate)
    ↓
[2] Understand → Read spec (architecture, state, navigation)
    ↓
[3] Plan → Determine affected layers (data/ui/integration)
    ↓
[4] Draft Spec Changes → Propose updates to spec sections
    ↓
[5] Review Gate → USER APPROVES spec changes before coding
    ↓
[6] Implement → Apply changes following patterns
    ↓
[7] Validate → Build + Format
    ↓
[8] Update Spec → Apply approved changes + changelog
    ↓
✅ Done
```

### Steps Summary

1. **Parse** - Extract feature name from request
2. **Spec Check** - Load or generate specification
3. **Understand** - Read spec to understand current implementation
4. **Plan** - Determine which layer(s) to modify
5. **Draft Spec Changes** - Propose specific updates to spec sections
6. **Review Gate** - Present changes to user for approval (REQUIRED)
7. **Implement** - Apply changes following architecture patterns
8. **Validate** - Run feature build
9. **Update Spec** - Apply approved spec changes with changelog entry

## Phase 0: Context Discovery

Extract project context from existing feature:

```bash
# Get package prefix from feature namespace
grep "namespace" feature/{featurename}/build.gradle.kts
# Output: namespace = "com.example.login" -> PKG_PREFIX = "com.example"
```

**Context values to extract:**
- `{PKG_PREFIX}` - Package prefix (extract from namespace, e.g., `com.example`)
- `{PKG_PATH}` - Package as path (dots to slashes, e.g., `com/example/`)

## Step 1: Parse Feature Name

Extract feature name from user request:
- "add sorting to productlist" -> `productlist`
- "modify the login screen" -> `login`
- "update orders feature" -> `orders`

**Validate feature exists:**
```bash
ls feature/{featurename}/src/commonMain/kotlin/
```

If feature does not exist, report error and list available features.

## Step 2: Spec Check

Check for existing specification:
```
.claude/docs/{featurename}/spec.md
```

**If missing:** Run `/audit-spec {featurename}` to create specification from implementation.

**If exists:** Proceed to Step 3.

## Step 3: Understand Current Implementation

Read the specification to understand:
- Current architecture and package structure
- Data models and API contracts
- State management approach
- Navigation routes and callbacks
- DI bindings

**Key sections to review:**
- Requirements (current behavior)
- Architecture > Key Classes
- State Management
- Navigation

## Step 4: Plan Changes

Determine which layers are affected:

| Change Type | Layers Affected | Architecture Reference |
|-------------|-----------------|----------------------|
| New API endpoint | data/ | architecture-data.md |
| New field in model | data/, presentation/ | architecture-data.md |
| New UI component | presentation/ui/ | architecture-ui.md |
| State change | presentation/ | architecture-ui.md |
| Navigation change | presentation/navigation/ | patterns.md |
| New screen | presentation/ + navigation/ | architecture-ui.md |

**Core patterns** (always applicable):
- `../creating-kmp-feature/references/patterns.md` - 10 critical rules, 4 integration points

**Load layer-specific architecture only when needed:**
- Data changes: `../creating-kmp-feature/architecture/data.md`
- UI changes: `../creating-kmp-feature/architecture/ui.md`
- Integration changes: `../creating-kmp-feature/architecture/integration.md`

## Step 5: Draft Spec Changes

**Purpose:** Propose specific updates to the spec BEFORE writing any code.

Based on the planned changes, draft modifications to relevant spec sections:

### 5.1 Identify Affected Spec Sections

| Change Type | Spec Sections to Update |
|-------------|------------------------|
| New requirement | Section 3 (Requirements) |
| New API endpoint | Section 5 (Interfaces) |
| New data model | Section 4 (Design > Data Models) |
| State changes | Section 6.2 (State Management) |
| New UI flow | Section 6.1 (User Flows) |
| New error case | Section 6.3 (Error Handling) |
| New scenario | Section 7 (Testing) |

### 5.2 Draft Changes Format

Present proposed changes using diff format:

```markdown
## Proposed Spec Changes: {featurename}

**Current Version:** {X.Y.Z}
**Proposed Version:** {X.Y+1.Z} (Minor - new capability) or {X.Y.Z+1} (Patch - fix/clarification)

---

### Changes to Section {N}: {Section Name}

```diff
  existing content
+ added content
- removed content
```

### New Section/Scenario (if applicable)

```markdown
#### Scenario: {New scenario name}
- GIVEN {precondition}
- WHEN {action}
- THEN {expected result}
```

---

### Rationale

{Brief explanation of why these changes are needed}
```

## Step 6: Review Gate (REQUIRED)

**Purpose:** Get explicit user approval for spec changes before implementation.

**This step is MANDATORY** - never skip directly to implementation.

### 6.1 Present Changes to User

```markdown
## Spec Change Review: {featurename}

**Requested modification:** "{user's original request}"

### Proposed Spec Updates

{List all sections being modified with diff previews}

### Summary of Changes

| Section | Change Type | Description |
|---------|-------------|-------------|
| 3.1 Requirements | Addition | Added FR-X.Y for {capability} |
| 6.2 State | Modification | Added {NewState} to UiState |
| 7.1 Test Scenarios | Addition | Added scenario for {behavior} |

### Proposed Changelog Entry

```markdown
- {YYYY-MM-DD} - {Brief description of change}
```

---

**Please review the proposed spec changes above.**

- [ ] **Approve** - Proceed with implementation
- [ ] **Modify** - Request changes (please specify)
- [ ] **Reject** - Do not proceed
```

### 6.2 Handle User Response

| Response | Action |
|----------|--------|
| Approved | Proceed to Step 7 (Implement) |
| Modify | Update draft per feedback → Re-present for approval |
| Reject | End workflow, do not implement |

### 6.3 Why This Gate Matters

- **Prevents scope creep** - User sees exactly what will change
- **Maintains spec authority** - Spec updated intentionally, not as afterthought
- **Enables rollback** - If implementation fails, spec wasn't yet changed
- **Documents decisions** - User explicitly approved the contract change

## Step 7: Implement Changes

Apply changes following existing patterns in the codebase.

### Design Enhancement (Automatic)

For UI changes, check if `frontend-design` skill is listed in system skills.

**If available** (REQUIRED):
1. Load `using-design-system/references/component-mappings.md`
2. Invoke `frontend-design` skill with:
   - Current screen context
   - Proposed changes
   - Constraint: "Use only X-components: [list from component-mappings.md]"
3. Follow design output

**If unavailable**: Use `using-design-system` skill directly.

**Critical Rules:**
1. Interface + Impl pairs for DataSource/Repository
2. Either<T> for fallible operations
3. setState { } for state updates (never direct assignment)
4. 4-state UI: Uninitialized/Loading/Success/Failed
5. X-components only (no Material3)
6. ImmutableList for collections
7. Callback parameters for navigation

**Pattern Discovery:**
- Reference existing code in the feature module
- Reference `feature/login/` as canonical example
- Use `using-design-system` skill for UI changes

## Step 8: Validate Build

Run incremental build:
```bash
./gradlew :feature:{featurename}:assembleAndroidMain
```

If build fails, fix errors and retry.

For formatting:
```bash
./gradlew :feature:{featurename}:ktlintFormat
```

## Step 9: Update Specification

**Important:** Apply the APPROVED spec changes from Step 6, do not regenerate from scratch.

### 9.1 Apply Approved Changes

Edit `.claude/docs/{featurename}/spec.md` to apply the changes that were approved in Step 6:
- Add new requirements/scenarios
- Update data models
- Modify state definitions
- Add test scenarios

### 9.2 Update Changelog

Add the approved changelog entry at the top of the "Last Updated" section:

```markdown
## Last Updated
- {YYYY-MM-DD} - {Brief description from approved changes}
- {previous entries preserved...}
```

### 9.3 Version Bump

Update the spec version in Metadata section:
- **Patch** (X.Y.Z+1): Clarifications, typo fixes
- **Minor** (X.Y+1.0): New capabilities, added features
- **Major** (X+1.0.0): Breaking changes, removed features

**Template reference:** See `templates/spec-changelog-entry.md` for format guidelines and examples.

### 9.4 Verify Spec Accuracy

After updating, quickly verify:
- [ ] All approved changes applied
- [ ] No contradictions with existing content
- [ ] Changelog entry added
- [ ] Version bumped appropriately

## Error Handling

**Build errors:** Load troubleshooting files:
- `.claude/skills/creating-kmp-feature/troubleshooting/index.md`
- `.claude/skills/creating-kmp-feature/troubleshooting/{layer}.md`

**Design system issues:** Activate `using-design-system` skill for component guidance.

## Quick Reference

**Core patterns** (single source of truth):
- `../creating-kmp-feature/references/patterns.md` - 10 critical rules, 4 integration points, naming conventions

**Architecture files** (load only when needed):
- `../creating-kmp-feature/architecture/data.md` - Data layer patterns
- `../creating-kmp-feature/architecture/ui.md` - UI/presentation patterns
- `../creating-kmp-feature/architecture/integration.md` - Integration patterns

**Templates:**
- `templates/spec-changelog-entry.md` - Spec update format
- `templates/spec-change-review.md` - Review gate format

**Commands:**
- Spec audit: `/audit-spec {featurename}` (for drift detection)
- Feature build: `./gradlew :feature:{featurename}:assembleAndroidMain`
- Format: `./gradlew :feature:{featurename}:ktlintFormat`

**Reference feature:** `feature/login/`

## Completion Checklist

Before completing modification:
- [ ] Spec changes drafted and presented to user
- [ ] **User approved spec changes** (Step 6 - REQUIRED)
- [ ] Changes follow architecture patterns
- [ ] Build passes: `./gradlew :feature:{featurename}:assembleAndroidMain`
- [ ] Code formatted: ktlint applied
- [ ] Spec updated with approved changes (not regenerated)
- [ ] Changelog entry added with date
- [ ] Version bumped in spec metadata
- [ ] All 4 UI states handled (if UI modified)
- [ ] X-components used (if UI modified)
