---
description: Generate comprehensive tests for a KMP feature
allowed-tools: ["Task", "Read", "Edit", "Glob", "Grep", "Bash(./gradlew:*)", "AskUserQuestion"]
---

# Generate Feature Tests

**Usage:** `/feature-test {featurename}`

## Phase 1: Discovery (Direct Execution)

### 1.1 Detect Namespaces

```bash
grep "namespace" feature/{name}/build.gradle.kts     # PKG_PREFIX
grep "namespace" core/common/build.gradle.kts        # CORE_COMMON_PKG
grep "namespace" core/data/build.gradle.kts          # CORE_DATA_PKG
```

### 1.2 Extract Context (YAML Only)

```
Glob: feature/{name}/src/commonMain/kotlin/**/*.kt
```

**Extract 5-line YAML summaries** (not full code):

```yaml
entities: [{name, fields}]
dataSource: {interface, implementation, methods}
repository: {interface, implementation, dependencies}
viewModel: {class, dependencies, actions}
screen: {composable, rootComposable, callbacks}
```

### 1.3 Add Test Dependencies

**Check if test dependencies exist in `feature/{name}/build.gradle.kts`:**

```bash
grep "commonTest" feature/{name}/build.gradle.kts
```

**If missing, add the following to the gradle file:**

```kotlin
// In plugins block (if not present):
alias(libs.plugins.kover)
alias(libs.plugins.mokkery)

// Add after commonMain.dependencies:
commonTest {
    dependencies {
        implementation(libs.bundles.testing.common) // kotlin-test, kotlinx-coroutines-test, turbine
        implementation(libs.compose.ui.test)
        implementation(libs.ktor.client.mock)
        implementation(libs.ktor.client.content.negotiation)
        implementation(libs.ktor.serialization.kotlinx.json)
        implementation(libs.ktor.client.resources)
    }
}

val desktopTest by getting {
    dependencies {
        implementation(compose.desktop.currentOs)
    }
}
```

## Phase 2: Project Sync

**After gathering all dependencies and context, request manual sync:**

Use `AskUserQuestion` with:
```
question: "Please sync the project to ensure all dependencies are resolved. After syncing, confirm to continue with test generation."
header: "Sync Required"
options:
  - label: "Sync Complete"
    description: "I have synced the project and all dependencies are resolved"
```

**Wait for user confirmation before proceeding to Phase 3.**

## Phase 3: Spawn Agents

### 3.1 Fixtures (Sequential)

**Single Task call, WAIT for completion:**

```
Task: test-fixtures
Prompt: "Feature: {name}
Package: {PKG_PREFIX}.{name}
CORE_COMMON_PKG: {CORE_COMMON_PKG}
CORE_DATA_PKG: {CORE_DATA_PKG}

Entities: {yaml}
UiState: {yaml}"
```

### 3.2 Data Layer (Parallel - 2 agents)

**Both in SAME message:**

```
Task: test-datasource
Prompt: "Feature: {name}, Package: {PKG_PREFIX}.{name}
Fixtures: {PKG_PREFIX}.{name}.fixtures.{Feature}Fixtures
CORE_COMMON_PKG: {CORE_COMMON_PKG}, CORE_DATA_PKG: {CORE_DATA_PKG}
DataSource: {yaml}"

Task: test-repository
Prompt: "Feature: {name}, Package: {PKG_PREFIX}.{name}
Fixtures: {PKG_PREFIX}.{name}.fixtures.{Feature}Fixtures
CORE_COMMON_PKG: {CORE_COMMON_PKG}
Repository: {yaml}"
```

### 3.3 Presentation + Integration (Parallel - 3 agents)

**All THREE in SAME message:**

```
Task: test-viewmodel
Prompt: "Feature: {name}, Package: {PKG_PREFIX}.{name}
Fixtures: {PKG_PREFIX}.{name}.fixtures.{Feature}Fixtures
CORE_COMMON_PKG: {CORE_COMMON_PKG}
ViewModel: {yaml}"

Task: test-ui
Prompt: "Feature: {name}, Package: {PKG_PREFIX}.{name}
Fixtures: {PKG_PREFIX}.{name}.fixtures.{Feature}Fixtures
UiFixtures: {PKG_PREFIX}.{name}.fixtures.{Feature}UiFixtures
CORE_COMMON_PKG: {CORE_COMMON_PKG}
Screen: {yaml}
Test ScreenRoot, NOT Screen."

Task: test-integration
Prompt: "Feature: {name}, Package: {PKG_PREFIX}.{name}
Fixtures: {PKG_PREFIX}.{name}.fixtures.{Feature}Fixtures
CORE_COMMON_PKG: {CORE_COMMON_PKG}, CORE_DATA_PKG: {CORE_DATA_PKG}
Stack: {yaml}"
```

## Phase 4: Run Tests

```bash
./gradlew :feature:{name}:cleanDesktopTest :feature:{name}:desktopTest
```

## Phase 5: Coverage

```bash
./gradlew :feature:{name}:koverHtmlReport
```

Parse `feature/{name}/build/reports/kover/report.xml` for line/branch coverage.

## Phase 6: Summary

```markdown
## Test Generation: {feature}

| Test | File |
|------|------|
| Fixtures | .../fixtures/{Feature}Fixtures.kt |
| UiFixtures | .../fixtures/{Feature}UiFixtures.kt |
| DataSource | .../data/datasource/{Feature}RemoteDataSourceTest.kt |
| Repository | .../data/{Feature}RepositoryImplTest.kt |
| ViewModel | .../presentation/{Feature}ViewModelTest.kt |
| UI | .../presentation/ui/{Feature}ScreenTest.kt |
| Integration | .../integration/{Feature}IntegrationTest.kt |

**Tests:** PASSED/FAILED | **Coverage:** Line X% / Branch Y%

View: `open feature/{name}/build/reports/kover/html/index.html`
```

## Execution Flow

```
Phase 1: Read files → extract YAML
Phase 2: AskUserQuestion(sync) → WAIT for confirmation
Phase 3.1: Task(fixtures) → WAIT
Phase 3.2: Task(datasource) + Task(repository) → WAIT (parallel)
Phase 3.3: Task(viewmodel) + Task(ui) + Task(integration) → WAIT (parallel)
Phase 4-6: Run tests → coverage → report
```

**Max parallel: 3 agents** (memory-safe)

## Rules

1. YOU do discovery - no agent for Phase 1
2. Add test dependencies to gradle if missing (Phase 1.3)
3. Request sync and wait for user confirmation (Phase 2)
4. YAML summaries only - never full file contents
5. Fixtures first - wait before spawning others
6. Parallel batches - multiple Task calls in same message
7. UI tests target ScreenRoot - not Screen wrapper
