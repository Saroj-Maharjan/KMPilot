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
[4] Implement → Apply changes following patterns
    ↓
[5] Validate → Build + Format
    ↓
[6] Update Spec → Regenerate with changelog
    ↓
✅ Done
```

### Steps Summary

1. **Parse** - Extract feature name from request
2. **Spec Check** - Load or generate specification
3. **Understand** - Read spec to understand current implementation
4. **Plan** - Determine which layer(s) to modify
5. **Implement** - Apply changes following architecture patterns
6. **Validate** - Run feature build
7. **Update Spec** - Regenerate spec from implementation

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

**If missing:** Run `/generate-spec {featurename}` to create specification from implementation.

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

## Step 5: Implement Changes

Apply changes following existing patterns in the codebase.

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

## Step 6: Validate Build

Run incremental build:
```bash
./gradlew :feature:{featurename}:assembleAndroidMain
```

If build fails, fix errors and retry.

For formatting:
```bash
./gradlew :feature:{featurename}:ktlintFormat
```

## Step 7: Update Specification

1. Copy existing "Last Updated" entries from spec (if any)
2. Regenerate the spec: `/generate-spec {featurename}`
3. Add new changelog entry at top following template: `templates/spec-changelog-entry.md`
   ```markdown
   ## Last Updated
   - {YYYY-MM-DD} - {Brief description of change}
   - {previous entries...}
   ```

**Template reference:** See `templates/spec-changelog-entry.md` for format guidelines and examples.

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

**Commands:**
- Spec generation: `/generate-spec {featurename}`
- Feature build: `./gradlew :feature:{featurename}:assembleAndroidMain`
- Format: `./gradlew :feature:{featurename}:ktlintFormat`

**Reference feature:** `feature/login/`

## Completion Checklist

Before completing modification:
- [ ] Changes follow architecture patterns
- [ ] Build passes: `./gradlew :feature:{featurename}:assembleAndroidMain`
- [ ] Code formatted: ktlint applied
- [ ] Spec regenerated: `/generate-spec {featurename}`
- [ ] All 4 UI states handled (if UI modified)
- [ ] X-components used (if UI modified)
