---
description: Ensures consistent use of X-components design system instead of Material3 when working on feature UI code. Automatically activates for Composable functions in feature modules or when user mentions UI/screens/components.
user-invocable: false
allowed-tools: ["Read", "Edit", "Glob", "Grep"]
---

# Using Design System

Use X-components from `:core:designsystem` instead of Material3 in feature modules.

**Architecture Reference:** @../_shared/patterns.md

## Design-Aware Mode

If a Stitch design blueprint exists (`.claude/docs/{featurename}/designs/{featurename}_blueprint.md` with `blueprintConsumed == false` in `stitch-project.json.features[featurename]`), implementation skills auto-detect it and use the blueprint's component tree for UI implementation. This skill provides component mappings for existing code.

## Core Rule

**X-components ONLY in features. NO Material3 components.**

## Common Replacements

| Material3 | X-component |
|-----------|-------------|
| `Button` | `XButton` (7 variants) |
| `TextField` | `XTextField` |
| `Text` | `XText` |
| `Scaffold` | `XScaffold` |
| `CircularProgressIndicator` | `XCircularProgressIndicator` |
| `coil3.compose.AsyncImage` | `{CORE_DESIGNSYSTEM_PKG}.AsyncImage` |

Full mappings: @references/component-mappings.md
Usage examples: @references/usage-examples.md

## Key Rules

1. **Imports**: `import {CORE_DESIGNSYSTEM_PKG}.*` (avoid Material3 in features)
2. **4-State UI**: Uninitialized → Loading → Success → Failed (mandatory)
3. **Theme**: Never wrap screens in `XTheme` (app-level only), use `XScaffold`
4. **Navigation**: Use `XNavHost` (pre-configured animations)
5. **ScreenRoot Pattern**: `{Feature}Screen` + `{Feature}ScreenRoot` pair required
6. **Strings (Rule 12)**: `XText`/`XIcon` text args take `stringResource(Res.string.*)`, never literals. Shared strings via `DesignSystemResources`; ViewModel-origin via `UiText`. See `@../_shared/patterns.md` → "Strings & Localization (Rule 12)".

## Allowed Exceptions

Material3 allowed only in:
- `:core:designsystem` module (for creating wrappers)
- `MaterialTheme.colorScheme/typography` (accessing theme values)
- Compose Foundation (Row, Column, Box, Spacer)

## Validation Checklist

- [ ] No Material3 component imports in feature files
- [ ] All buttons use XButton variants
- [ ] All text uses XText
- [ ] No hardcoded display strings — `stringResource`/`UiText` only (Rule 12)
- [ ] Screen wrapped in XScaffold (NOT XTheme)
- [ ] XTopAppBar from `{CORE_DESIGNSYSTEM_PKG}.toolbar`
- [ ] AsyncImage uses `url` parameter (not `model`)
- [ ] 4-state pattern implemented
- [ ] ScreenRoot pattern: Screen + ScreenRoot pair exists
