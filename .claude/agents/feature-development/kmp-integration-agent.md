---
name: integration-agent
description: Specialized agent for integrating KMP features into the app (DI modules, navigation wiring, gradle configuration). Completes the 4 required integration points.
allowed-tools: ["Read", "Write", "Edit", "Bash(./gradlew:*)", "Bash(rm:*)", "Glob", "Grep"]
model: sonnet
color: green
---

# KMP Integration Agent

Integrates completed features through 4 required integration points.

**Base Instructions:** @../_base/common.md
**Architecture:** @../../skills/_shared/patterns.md (load on demand)
**Integration Patterns:** @../../skills/creating-kmp-feature/architecture/integration.md (load on demand)

## 4 Integration Points (ALL REQUIRED)

| # | Point | File | Pattern |
|---|-------|------|---------|
| 1 | Gradle Include | `settings.gradle.kts` | `include(":feature:{featurename}")` |
| 2 | Gradle Dependency | `composeApp/build.gradle.kts` | `implementation(project(":feature:{featurename}"))` |
| 3 | DI Init | `{INIT_KOIN_PATH}` | `{Feature}Modules.initialize()` |
| 4 | Navigation | `{NAV_HOST_PATH}` | `{featurename}(onBackClick = {...})` |

## Workflow

1. Create DI module (`di/{Feature}Modules.kt`)
2. Integration Point 1: Gradle Include
3. Integration Point 2: Gradle Dependency
4. Integration Point 3: DI Initialization
5. Integration Point 4: Navigation (read Screen for callbacks; if Welcome scaffold present, perform first-feature handoff — see below)
6. Validate: `./gradlew assembleDebug && ./gradlew ktlintFormat`
7. Generate spec.md (preserve WHY from PRD)

## First-feature Handoff

Fresh KMPilot projects (cloned via `install.sh`) ship with a `WelcomeScreen.kt` placeholder next to the nav host. Before wiring navigation, check **both** markers:

1. `composeApp/src/commonMain/kotlin/**/WelcomeScreen.kt` exists (use Glob)
2. `{NAV_HOST_PATH}` contains `startDestination = WelcomeRoute`

If **both** present, this is the first feature — perform the handoff:

- Replace `startDestination = WelcomeRoute` with `startDestination = {Feature}Route`
- Remove the `composable<WelcomeRoute> { WelcomeScreen() }` line (the new feature's `{featurename}(...)` extension takes its place)
- Delete the WelcomeScreen file: `rm -f <matched path from Glob>`

If **either** marker is missing, wire navigation normally: add `{featurename}(...)` alongside existing routes and leave `startDestination` untouched (user may have already customized it).

## Spec Generation

**CRITICAL**: Copy from PRD before it's deleted:
- Goals, Non-Goals, Background & Rationale, Design Decisions

**Spec Template:** @../../skills/_shared/spec-template.md

## Output Report

```
## Integration Complete: {featurename}

### Files Created/Modified
- di/{Feature}Modules.kt (created)
- settings.gradle.kts (modified)
- composeApp/build.gradle.kts (modified)
- {INIT_KOIN_PATH} (modified)
- {NAV_HOST_PATH} (modified)

### Integration Points
✅ 1. Gradle Include
✅ 2. Gradle Dependency
✅ 3. DI Initialization
✅ 4. Navigation Wiring

### Validation
✅ ./gradlew assembleDebug
✅ ./gradlew ktlintFormat

### Spec Generated
✅ .claude/docs/{featurename}/spec.md

Next: navController.navigate({Feature}Route)
```
