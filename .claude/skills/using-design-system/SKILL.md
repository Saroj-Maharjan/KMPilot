---
description: Ensures consistent use of X-components design system instead of Material3 when working on feature UI code. Automatically activates for Composable functions in feature modules or when user mentions UI/screens/components.
allowed-tools: ["*"]
---

# Using Design System

Guides usage of X-components (custom design system) instead of Material3 in feature modules.

## Context Discovery

On activation, detect design system package:
1. Grep `import.*designsystem\.X` in `feature/**/*.kt`
2. Extract package (e.g., `com.example.designsystem`)
3. Store as `{DESIGN_SYSTEM_PKG}` for use in import patterns

## Automatic Activation

This skill activates automatically when:
- Working in `feature/*/ui/` directories
- Creating/modifying Composable functions
- User mentions: "UI", "screen", "component", "design system"

## Core Rule

**Use X-components from `:core:designsystem` instead of Material3 in all feature modules.**

## Component Mappings

Full mappings in: `references/component-mappings.md`

**Common replacements**:
- `Button` → `XButton` (7 variants)
- `TextField` → `XTextField`
- `Text` → `XText`
- `Scaffold` → `XScaffold`
- `CircularProgressIndicator` → `XCircularProgressIndicator`
- `coil3.compose.AsyncImage` → `{DESIGN_SYSTEM_PKG}.AsyncImage`

## Key Rules

1. **Imports**: `import {DESIGN_SYSTEM_PKG}.*` (avoid Material3 component imports in features)

2. **4-State UI** (mandatory): Uninitialized → Loading → Success → Failed
   - Loading: `XCircularProgressIndicator`
   - Failed: `XText` with error styling

3. **Theme**: Never wrap screens in `XTheme` (applied at App level), use `XScaffold` for structure

4. **Navigation**: Use `XNavHost` (pre-configured animations)

5. **ScreenRoot Pattern**: Always create `{Feature}Screen` + `{Feature}ScreenRoot`
   - Screen: ViewModel wrapper, collects state
   - ScreenRoot: ViewModel-independent, takes UiState + callbacks (testable)

## Usage Examples

Detailed patterns in: `references/usage-examples.md`

## Allowed Exceptions

Material3 allowed only in:
- `:core:designsystem` module (for creating wrappers)
- `MaterialTheme.colorScheme/typography` (accessing theme values)
- Compose Foundation (Row, Column, Box, Spacer)

## Validation Checklist

Before completing UI work, verify:
- ✅ No Material3 component imports in feature files
- ✅ All buttons use XButton variants
- ✅ All text uses XText
- ✅ Screen wrapped in XScaffold (NOT XTheme)
- ✅ XTopAppBar from `{DESIGN_SYSTEM_PKG}.toolbar`
- ✅ AsyncImage uses `url` parameter (not `model`)
- ✅ 4-state pattern implemented
- ✅ ScreenRoot pattern: Screen + ScreenRoot pair exists

## Referenced Files

- `references/component-mappings.md` - Complete Material3 → X-component mappings
- `references/usage-examples.md` - Detailed usage patterns and code examples
