---
description: Audit, generate, or compare specifications for existing KMP features
allowed-tools: ["Read", "Write", "Edit", "Glob", "Grep", "Bash", "mcp__serena__*"]
---

# Audit Feature Specification

Audit, generate, or compare specifications for existing KMP features. This command analyzes implemented code and generates/updates documentation.

## When to Use This Command

| Scenario | Use This Command? | Reason |
|----------|-------------------|--------|
| **Legacy feature without spec** | Yes | Generate initial documentation |
| **Check spec-code consistency** | Yes | Detect drift between spec and implementation |
| **New feature creation** | No | Use `creating-kmp-feature` skill (PRD-first) |
| **Modifying existing feature** | No | Use `modifying-kmp-feature` skill (spec-first updates) |

> **Note:** For SDD best practices, specs should be written BEFORE implementation.
> This command is for auditing existing code or documenting legacy features.

## Usage

```bash
# Generate spec from existing code
/audit-spec {featurename}

# Compare existing spec with implementation (drift detection)
/audit-spec {featurename} --compare
```

**Examples:**
```bash
/audit-spec login
/audit-spec productlist --compare
/audit-spec customers
```

---

## Mode 1: Generate Spec (Default)

Generates a specification document from existing implementation.

### Process

#### Step 1: Validate Feature Exists

```bash
ls feature/{featurename}/src/commonMain/kotlin/
```

If feature doesn't exist, report error and list available features.

#### Step 2: Discover Package Prefix

```bash
grep "namespace" feature/{featurename}/build.gradle.kts
```

Extract package prefix (e.g., `com.example.login` → `com.example`).

#### Step 3: Analyze Implementation

Read the following files:

| Layer | Files |
|-------|-------|
| Data Models | `feature/{featurename}/src/**/data/model/*.kt` |
| DataSource | `feature/{featurename}/src/**/data/datasource/*.kt` |
| Repository | `feature/{featurename}/src/**/data/repository/*.kt` |
| Ktor Resources | `feature/{featurename}/src/**/data/remote/*.kt` |
| ViewModel | `feature/{featurename}/src/**/presentation/*ViewModel.kt` |
| UiModel | `feature/{featurename}/src/**/presentation/*UiModel.kt` |
| Screen | `feature/{featurename}/src/**/presentation/ui/*Screen.kt` |
| Navigation | `feature/{featurename}/src/**/presentation/navigation/*.kt` |
| DI Module | `feature/{featurename}/src/**/di/*Modules.kt` |

#### Step 4: Extract Information

From the analyzed files, extract:

- **Purpose**: From comments or class documentation
- **Data models**: Class names, fields, types
- **API endpoints**: From Ktor Resources if present
- **State structure**: UiModel fields and UiState type
- **State transitions**: From ViewModel logic
- **Navigation routes**: From @Serializable route objects
- **Navigation callbacks**: From Screen composable parameters
- **DI bindings**: From Modules object

#### Step 5: Check for Existing PRD (Preserve WHY)

**IMPORTANT:** Check if PRD exists with rationale content:

```bash
cat .claude/docs/{featurename}/prd.txt 2>/dev/null || echo "No PRD found"
```

**If PRD exists, copy these sections to spec:**
- Goals
- Non-Goals
- Background & Rationale
- Design Decisions

**If PRD doesn't exist** (legacy feature):
- Add `<!-- TODO: Fill in manually -->` comments
- Infer what you can from code comments

#### Step 6: Generate Specification

Create `.claude/docs/{featurename}/spec.md` using the template below.

#### Step 7: Report Success

```markdown
## Specification Generated

**Feature:** {featurename}
**Output:** `.claude/docs/{featurename}/spec.md`

**Documented:**
- {N} data models
- {N} classes
- {N} navigation callbacks
- All 4 integration points

**Action Required:**
1. Review generated spec for accuracy
2. Fill in TODO sections (Goals, Non-Goals, Rationale)
3. Update scenarios to reflect actual behavior
```

---

## Mode 2: Compare/Drift Detection (--compare)

Compares existing spec with current implementation to detect drift.

### Process

#### Step 1: Load Existing Spec

```bash
cat .claude/docs/{featurename}/spec.md
```

If no spec exists, suggest running without `--compare` first.

#### Step 2: Analyze Current Implementation

Same as Mode 1, Steps 2-4.

#### Step 3: Compare and Report

Generate a drift report:

```markdown
## Spec Drift Report: {featurename}

**Spec Path:** `.claude/docs/{featurename}/spec.md`
**Spec Version:** {version from metadata}
**Analysis Date:** {current date}

---

### Summary

| Category | Status |
|----------|--------|
| Data Models | {✅ In sync / ⚠️ N drifts} |
| Interfaces | {✅ In sync / ⚠️ N drifts} |
| State Management | {✅ In sync / ⚠️ N drifts} |
| Navigation | {✅ In sync / ⚠️ N drifts} |
| Error Handling | {✅ In sync / ⚠️ N drifts} |

---

### Drift Details

#### {Category} Drift

**File:** `{file path}:{line}`

| Aspect | Spec Says | Code Has |
|--------|-----------|----------|
| {aspect} | {spec value} | {code value} |

**Missing from spec:**
```kotlin
{code that exists but isn't documented}
```

**Recommendation:** {specific action to fix}

---

### Proposed Spec Updates

Based on this audit, apply these changes to the spec:

#### Section {N}: {Section Name}
```diff
  existing content
+ added content
- removed content
```

---

### Actions Required

- [ ] Review drift findings
- [ ] Apply proposed spec updates (or update code to match spec)
- [ ] Add changelog entry
```

---

## Spec Template

```markdown
# {FeatureName} Specification

## Metadata
| Field | Value |
|-------|-------|
| Version | 1.0.0 |
| Status | Active |
| Created | {date} |
| Updated | {date} |

## Purpose
{Extracted from implementation or inferred from functionality}

## Goals
<!-- TODO: Fill in - what should this feature achieve? -->
- {Goal 1}
- {Goal 2}

## Non-Goals
<!-- TODO: Fill in - what is explicitly out of scope? -->
- {Non-goal 1}

## Background & Rationale
<!-- TODO: Fill in - why does this feature exist? -->
{Brief rationale for why this feature exists and key design decisions.}

## Design Decisions
<!-- TODO: Fill in - key architectural choices -->
| Decision | Choice | Alternatives | Rationale |
|----------|--------|--------------|-----------|
| {Decision} | {Choice} | {Alternatives} | {Why} |

## Last Updated
- {date} - Generated from existing implementation

## Requirements

### Requirement: {Core Capability}
The system SHALL {behavior description}.

#### Scenario: {Feature} loads successfully
- GIVEN the user navigates to {Feature} screen
- WHEN the data loads successfully
- THEN the content MUST be displayed
- AND the loading state MUST transition to success

#### Scenario: {Feature} handles errors
- GIVEN the user is on {Feature} screen
- WHEN an error occurs
- THEN an error overlay MUST be displayed
- AND a retry option MUST be available

## Architecture

### Package Structure
```
feature/{featurename}/src/commonMain/kotlin/{pkg_prefix}/{featurename}/
├── data/
│   ├── model/
│   ├── datasource/
│   ├── repository/
│   └── remote/
├── presentation/
│   ├── {Feature}ViewModel.kt
│   ├── {Feature}UiModel.kt
│   ├── ui/
│   └── navigation/
└── di/
    └── {Feature}Modules.kt
```

### Data Flow
```
[API] → RemoteDataSource → Repository → ViewModel → [UI]
         Either<Error,T>    Either<T>    UiState<T>
```

### Key Classes
| Class | Purpose | Location |
|-------|---------|----------|
| {Feature}RemoteDataSource | API operations interface | data/datasource/ |
| {Feature}RemoteDataSourceImpl | API implementation | data/datasource/ |
| {Feature}Repository | Business logic interface | data/repository/ |
| {Feature}RepositoryImpl | Business logic impl | data/repository/ |
| {Feature}ViewModel | State management | presentation/ |
| {Feature}Screen | UI composition | presentation/ui/ |

### Data Models
```kotlin
{Actual data class definitions from code}
```

## Integration Points

| Point | File | Status |
|-------|------|--------|
| Module include | settings.gradle.kts | ✅ |
| Dependency | composeApp/build.gradle.kts | ✅ |
| DI init | initKoin.kt | ✅ |
| Navigation | BaseAppNavHost.kt | ✅ |

## State Management

### UiState Structure
```kotlin
{Actual UiModel definition from code}
```

### State Transitions
{Inferred from ViewModel implementation}

## Navigation

- **Route:** `{Actual route object}`
- **Entry:** `navController.navigate({Route})`
- **Callbacks:** {From Screen parameters}
```

---

## Notes

- This command analyzes **existing** code and generates documentation
- The spec reflects what was **actually built**, not what was planned
- For new features, use `creating-kmp-feature` skill (PRD-first workflow)
- For modifications, use `modifying-kmp-feature` skill (spec-first updates)
- Generated specs have TODO markers for WHY sections - fill these in manually
- Run with `--compare` periodically to detect spec drift
