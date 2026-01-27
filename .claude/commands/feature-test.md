---
description: Generate comprehensive tests for a KMP feature
allowed-tools: ["Task", "Read", "Glob", "Grep", "Bash(./gradlew:*)"]
---

# Generate Feature Tests

Generate comprehensive test coverage for a KMP feature module. This command orchestrates test generation across all layers: fixtures, DataSource, Repository, ViewModel, UI, and integration tests.

## Usage

```bash
/feature-test {featurename}
```

**Examples:**
```bash
/feature-test login
/feature-test profile
/feature-test productcatalog
```

## What Gets Generated

| Test Type | Output Path | Coverage |
|-----------|-------------|----------|
| Fixtures | `.../fixtures/{Feature}Fixtures.kt` | Domain model factories |
| UI Fixtures | `.../fixtures/{Feature}UiFixtures.kt` | UiState factories for all 4 states |
| DataSource | `.../data/datasource/{Feature}RemoteDataSourceTest.kt` | MockEngine HTTP tests |
| Repository | `.../data/{Feature}RepositoryImplTest.kt` | Mokkery mock tests |
| ViewModel | `.../presentation/{Feature}ViewModelTest.kt` | Turbine StateFlow tests |
| UI | `.../presentation/ui/{Feature}ScreenTest.kt` | Compose UI tests (ScreenRoot) |
| Integration | `.../integration/{Feature}IntegrationTest.kt` | Full-stack E2E tests |

## Process

### Step 1: Validate Feature Exists

Check that the feature module exists:

```bash
ls feature/{featurename}/src/commonMain/kotlin/
```

If feature doesn't exist, report error and list available features.

### Step 2: Invoke Test Orchestrator

Spawn the `test-orchestrator` agent with the feature name:

```
Task: test-orchestrator
Prompt: Generate comprehensive tests for feature: {featurename}
```

The orchestrator will:
1. **Discover context** - Read all feature files once, extract structured data
2. **Generate fixtures first** - Domain and UI fixtures
3. **Parallel phase 1** - DataSource + Repository tests (+ Database if .sq files exist)
4. **Parallel phase 2** - ViewModel + UI + Integration tests
5. **Run tests** - `./gradlew :feature:{name}:desktopTest`
6. **Generate coverage** - Kover HTML report with metrics

### Step 3: Report Results

Display the test generation summary with:
- Test counts per type
- Pass/fail status
- Coverage metrics (line and branch)
- Spec scenario mapping (if spec.md exists)

## Spec-Aware Testing

If `.claude/docs/{featurename}/spec.md` exists, the orchestrator will:
- Extract requirement scenarios from the spec
- Pass scenarios to ViewModel and UI test agents
- Map generated tests to spec requirements
- Report spec coverage in the summary

## After Completion

```bash
# Run tests manually
./gradlew :feature:{featurename}:desktopTest

# View coverage report
open feature/{featurename}/build/reports/kover/html/index.html

# Run specific test class
./gradlew :feature:{featurename}:desktopTest --tests "*ViewModelTest"
```

## Notes

- Tests follow the **ScreenRoot pattern** - UI tests target `{Feature}ScreenRoot`, not `{Feature}Screen`
- All tests use **fixtures** for test data consistency
- Database tests only generated if `.sq` SQLDelight files exist
- Coverage threshold is 80% (configured in Kover)
- If tests fail, the orchestrator will report which specific tests need fixes
