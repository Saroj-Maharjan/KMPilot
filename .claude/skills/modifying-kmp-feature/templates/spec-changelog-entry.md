# Spec Changelog Entry Template

Use this template when updating the spec after modifications.

## Format

Add this section at the top of the regenerated spec:

```markdown
## Last Updated
- {YYYY-MM-DD} - {Brief description of change}
- {previous entries...}
```

---

## Guidelines

### Brief Description Format

| Change Type | Format | Example |
|-------------|--------|---------|
| Added | "Added {capability}" | "Added sorting to product list" |
| Modified | "Updated {component/behavior}" | "Updated login error handling" |
| Fixed | "Fixed {issue}" | "Fixed navigation callback in orders" |
| Refactored | "Refactored {layer/component}" | "Refactored data layer to use new API" |

### Rules

- **One line per update** - Keep it concise
- **Date format**: YYYY-MM-DD
- **Preserve history** - Keep all previous entries below new one

---

## Complete Example

```markdown
## Last Updated
- 2025-01-04 - Added filtering by category and price range
- 2025-01-02 - Fixed loading state handling in ViewModel
- 2024-12-28 - Updated API endpoint to v2
- 2024-12-20 - Initial implementation
```

---

## Workflow

1. **Before regenerating spec:**
   - Copy existing "Last Updated" section from current spec
   - Save entries temporarily

2. **After regenerating spec:**
   - Add new entry at top with today's date
   - Paste previous entries below
   - Ensure chronological order (newest first)
