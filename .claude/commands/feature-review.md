---
description: Review a KMP feature against architecture patterns and spec
allowed-tools: ["Task", "Read", "Glob", "Grep", "Write", "mcp__serena__find_symbol", "mcp__serena__search_for_pattern"]
---

# Review Feature Implementation

Review a KMP feature module against Clean Architecture guidelines, 10 critical rules, 4 integration points, and spec compliance.

## Usage

```bash
/feature-review {featurename}
```

**Examples:**
```bash
/feature-review login
/feature-review profile
/feature-review productcatalog
```

## What Gets Checked

### Architecture Rules (10 Checks)

| Rule | Check |
|------|-------|
| 1. Interface + Impl | DataSource and Repository have both interface and Impl |
| 2. Either<T> | All fallible operations return Either<T> |
| 3. setState | ViewModel uses `setState { }` extension, not direct assignment |
| 4. 4 UI States | Screen handles Uninitialized, Loading, Success, Failed |
| 5. X-Components | Uses X-components from designsystem, not Material3 components |
| 6. ImmutableList | Collections use `.toImmutableList()` |
| 7. Lowercase packages | Package names are lowercase (no hyphens/camelCase) |
| 8. DI Binding | Uses `singleOf(::Impl).bind<Interface>()` pattern |
| 9. No UseCases | ViewModels invoke repositories directly |
| 10. Callback params | Screens take callbacks, not navController |

### Integration Points (4 Checks)

| Point | File | Pattern |
|-------|------|---------|
| 1 | settings.gradle.kts | `include(":feature:{name}")` |
| 2 | composeApp/build.gradle.kts | `implementation(project(":feature:{name}"))` |
| 3 | initKoin.kt | `{Feature}Modules.initialize()` |
| 4 | BaseAppNavHost.kt | Navigation route + callback wiring |

### Spec Compliance (If spec.md exists)

| Section | Validation |
|---------|------------|
| Data Models | Compare spec vs actual models |
| Interfaces | Verify method signatures match |
| State Management | Validate UiState structure |
| Navigation | Check routes and callbacks |

## Process

### Step 1: Validate Feature Exists

Check that the feature module exists:

```bash
ls feature/{featurename}/src/commonMain/kotlin/
```

If feature doesn't exist, report error and list available features.

### Step 2: Invoke Code Reviewer

Spawn the `code-reviewer` agent with the feature name:

```
Task: code-reviewer
Prompt: Review feature: {featurename}
```

The reviewer will:
1. **Load context** - Glob all feature files, read spec.md if exists
2. **Check spec compliance** - Compare implementation against spec
3. **Check architecture rules** - Grep-based pattern matching for violations
4. **Check integration points** - Verify all 4 points are wired
5. **Analyze code quality** - Naming, structure, patterns

### Step 3: Generate Reports

Two output files are created:

**`.claude/docs/{featurename}/review.md`**
- Summary with pass/warning/fail counts
- Spec compliance table (if spec exists)
- Detailed findings per rule and integration point
- Recommendations (Critical, Warnings, Suggestions)

**`.claude/docs/{featurename}/fixes.md`**
- Specific code fixes with file:line references
- Current code vs fixed code blocks
- Explanations for each fix
- Optional automated script

## Outcome Statuses

| Status | Meaning |
|--------|---------|
| **PASS** | All rules and integrations pass |
| **PASS WITH WARNINGS** | Minor issues, non-blocking |
| **FAIL** | Critical violations found |

## After Review

If issues are found:

```bash
# View detailed review
cat .claude/docs/{featurename}/review.md

# View specific fixes
cat .claude/docs/{featurename}/fixes.md

# Apply fixes and re-review
/feature-review {featurename}
```

If spec drift is detected:

```bash
# Compare spec with implementation
/audit-spec {featurename} --compare

# Update spec to match implementation (or vice versa)
```

## Notes

- Review uses **Grep-first** approach for efficiency - only reads files when needed
- All findings include **file:line** references for easy navigation
- Spec compliance only checked if `.claude/docs/{featurename}/spec.md` exists
- For features without spec, consider running `/audit-spec {featurename}` first
- X-component violations list the specific Material3 import that should be replaced
