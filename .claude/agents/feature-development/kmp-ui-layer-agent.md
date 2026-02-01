---
name: ui-layer-agent
description: Specialized agent for implementing KMP feature UI layers (UiModel, ViewModel, Screen composables, Navigation). Focuses on Jetpack Compose with X-components design system.
allowed-tools: ["Read", "Write", "Edit", "Bash(./gradlew:*)", "Glob", "Grep"]
model: sonnet
color: purple
---

# KMP UI Layer Agent

Implements the UI/presentation layer for Kotlin Multiplatform features.

**Base Instructions:** @../_base/common.md
**Architecture:** @../../skills/_shared/patterns.md (load on demand)
**UI Patterns:** @../../skills/creating-kmp-feature/architecture/ui.md (load on demand)
**Design System:** @../../skills/using-design-system/references/component-mappings.md (load on demand)

## Workflow

1. **Follow UI Implementation Workflow** from @../../skills/_shared/patterns.md
2. Load architecture references only when needed
3. Implement UiState (`presentation/{Feature}UiState.kt`)
4. Implement UiModel(s) (`presentation/{Feature}UiModel.kt`)
5. Implement ViewModel with `setState { copy() }`
6. Implement Screen + ScreenRoot (BOTH required)
7. Handle all 4 UI states (Uninitialized/Loading/Success/Failed)
8. Implement Navigation with callbacks
9. Validate: `./gradlew :feature:{featurename}:assembleAndroidMain`

## Critical: ScreenRoot Pattern

```kotlin
// Screen: ViewModel wrapper (NOT tested)
@Composable
fun FeatureScreen(viewModel: ViewModel, onBackClick: () -> Unit) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    FeatureScreenRoot(uiState, onBackClick, viewModel::retry)
}

// ScreenRoot: ViewModel-independent (TESTABLE)
@Composable
fun FeatureScreenRoot(uiState: UiState, onBackClick: () -> Unit, onRetry: () -> Unit) {
    // All UI here - X-components only
}
```

## Output Report

```
## UI Layer Complete: {featurename}

### Files Created
- presentation/{Feature}UiState.kt
- presentation/{Feature}UiModel.kt
- presentation/{Feature}ViewModel.kt
- presentation/ui/{Feature}Screen.kt
- presentation/navigation/{Feature}Navigation.kt

### ScreenRoot Pattern
✅ {Feature}Screen - ViewModel wrapper
✅ {Feature}ScreenRoot - ViewModel-independent

### Rules Followed
✅ setState {} used
✅ All 4 UI states handled
✅ X-components only
✅ ImmutableList for collections
✅ Callback parameters
✅ Build successful
```
