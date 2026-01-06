---
description: Generate a living specification for an existing KMP feature
allowed-tools: ["Read", "Write", "Edit", "Glob", "Grep", "Bash", "mcp__serena__*"]
---

# Generate Feature Specification

Generate a living specification document for an existing KMP feature. This command creates a spec that documents what was actually built, serving as the source of truth for future modifications.

## Usage

```
/generate-spec {featurename}
```

**Example:**
```
/generate-spec login
/generate-spec productlist
/generate-spec customers
```

## Process

### Step 1: Validate Feature Exists

```bash
# Check feature directory exists
ls feature/{featurename}/src/commonMain/kotlin/
```

If feature doesn't exist, report error and exit.

### Step 2: Discover Package Prefix

```bash
# Extract namespace from build.gradle.kts
grep "namespace" feature/{featurename}/build.gradle.kts
```

Extract package prefix (e.g., `com.example.login` → `com.example`).

### Step 3: Analyze Implementation

Read the following files to understand the feature:

1. **Data Models**: `feature/{featurename}/src/**/data/model/*.kt`
2. **DataSource**: `feature/{featurename}/src/**/data/datasource/*.kt`
3. **Repository**: `feature/{featurename}/src/**/data/repository/*.kt`
4. **Ktor Resources**: `feature/{featurename}/src/**/data/remote/*.kt`
5. **ViewModel**: `feature/{featurename}/src/**/presentation/*ViewModel.kt`
6. **UiModel**: `feature/{featurename}/src/**/presentation/*UiModel.kt`
7. **Screen**: `feature/{featurename}/src/**/presentation/ui/*Screen.kt`
8. **Navigation**: `feature/{featurename}/src/**/presentation/navigation/*.kt`
9. **DI Module**: `feature/{featurename}/src/**/di/*Modules.kt`

### Step 4: Extract Information

From the analyzed files, extract:

- **Purpose**: From comments or class documentation
- **Data models**: Class names, fields, types
- **API endpoints**: From Ktor Resources if present
- **State structure**: UiModel fields and UiState type
- **State transitions**: From ViewModel logic
- **Navigation routes**: From @Serializable route objects
- **Navigation callbacks**: From Screen composable parameters
- **DI bindings**: From Modules object

### Step 5: Ensure Docs Folder Exists

```bash
mkdir -p .claude/docs/{featurename}
```

### Step 6: Generate Specification

Create `.claude/docs/{featurename}/spec.md`:

```markdown
# {FeatureName} Specification

## Purpose
{Extracted from implementation or inferred from functionality}

## Background
{Brief rationale for why this feature exists and key design decisions made. 2-3 sentences capturing the essential context that informed the implementation.}

## Last Updated
- {Current date} - Generated from existing implementation

## Requirements

### Requirement: {Core Capability}
The system SHALL {behavior description based on implemented functionality}.

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
│   ├── model/              # Feature-specific DTOs (@Serializable)
│   ├── datasource/         # Interface + Impl (injects ApiClient)
│   ├── repository/         # Interface + Impl (delegates to datasource)
│   └── remote/             # Ktor Resources (type-safe API endpoints)
├── presentation/
│   ├── {Feature}ViewModel.kt      # MutableStateFlow + setState {}
│   ├── {Feature}UiState.kt        # @Stable data class (optional)
│   ├── {Feature}UiModel.kt        # @Stable data classes
│   ├── ui/                        # Screens + components (X-components)
│   └── navigation/                # @Serializable routes + NavGraphBuilder extension
└── di/
    └── {Feature}Modules.kt        # BaseFeature object with Koin modules
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
| {Feature}RemoteDataSourceImpl | API operations implementation | data/datasource/ |
| {Feature}Repository | Business logic interface | data/repository/ |
| {Feature}RepositoryImpl | Business logic implementation | data/repository/ |
| {Feature}Resources | Ktor type-safe endpoints | data/remote/ |
| {Feature}ViewModel | State management (MutableStateFlow + setState) | presentation/ |
| {Feature}UiModel | UI state container (@Stable) | presentation/ |
| {Feature}Screen | UI composition (X-components) | presentation/ui/ |
| {Feature}Route | Navigation route (@Serializable) | presentation/navigation/ |
| {Feature}Modules | DI configuration (BaseFeature) | di/ |

### Data Models
```kotlin
{Actual data class definitions}
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
{Actual UiModel definition}
```

### State Transitions
{Inferred from ViewModel implementation}

## Navigation

- **Route:** `{Actual route object}`
- **Entry:** `navController.navigate({Route})`
- **Callbacks:** {Actual callbacks from Screen parameters}
```

### Step 7: Report Success

```markdown
## Specification Generated

### Feature: {featurename}

### Output:
✅ `.claude/docs/{featurename}/spec.md`

### Documented:
- {N} data models
- {N} classes
- {N} navigation callbacks
- All 4 integration points

### Next Steps:
1. Review the generated spec for accuracy
2. Update scenarios to reflect actual feature behavior
3. Use this spec as reference for future modifications
```

## Notes

- This command analyzes **existing** code and generates documentation
- The spec reflects what was **actually built**, not what was planned
- For new features, specs are generated automatically by the integration agent
- Update specs when features change to keep them current
