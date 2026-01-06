# PRD Template: Simple Feature

Use for: UI-only features, no API, < 3 screens

---

```markdown
# PRD: {Feature Name}

## Overview
{1-2 sentence description of what the feature does}

## Implementation Plan

| Aspect | Value |
|--------|-------|
| Complexity | Simple |
| Estimated Tasks | 3-5 |
| Layers | UI + Integration (no data layer) |
| API Required | No |

## UI Requirements

### Screens
1. **{ScreenName}Screen** - {purpose}

### Components
- {Component1} - {purpose}
- {Component2} - {purpose}

### State
- Local state managed in ViewModel
- No API calls required

## Acceptance Criteria

### Functional Scenarios

#### Scenario: {Feature} displays correctly
- GIVEN the user navigates to {Feature} screen
- WHEN the screen initializes
- THEN the UI MUST be displayed correctly
- AND all interactive elements MUST be functional

#### Scenario: {Feature} handles user interactions
- GIVEN the user is on {Feature} screen
- WHEN the user interacts with {element}
- THEN the expected action MUST occur
- AND the UI MUST update appropriately

### Technical Verification
- [ ] Build passes: `./gradlew assembleDebug`
- [ ] Navigation works correctly
- [ ] X-components used (no Material3)
- [ ] Code formatted: `./gradlew ktlintFormat`

## Integration Points

| File | Change Type | Description |
|------|-------------|-------------|
| settings.gradle.kts | MODIFIED | Add module include |
| composeApp/build.gradle.kts | MODIFIED | Add feature dependency |
| initKoin.kt | MODIFIED | Add DI initialization |
| BaseAppNavHost.kt | MODIFIED | Add navigation wiring |

## Dependencies
- `:core:common`
- `:core:designsystem`
```

---

## Usage Notes

- No data layer needed (no Repository, DataSource, Ktor Resources)
- ViewModel manages local state only
- Focus on UI implementation and navigation
- Minimal task count (3-5)
