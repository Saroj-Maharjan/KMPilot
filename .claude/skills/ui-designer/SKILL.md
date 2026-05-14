---
description: Design UI screens in Google Stitch and produce a Compose Implementation Blueprint with HTML + token inventories persisted for downstream skills.
argument-hint: [feature-name]
allowed-tools: Task, Read, Write, Edit, Glob, Grep, Bash(mkdir *), Bash(ls *), Bash(curl *), Bash(rm *), Bash(touch *), Bash(python3 *), AskUserQuestion, mcp__stitch__create_project, mcp__stitch__get_project, mcp__stitch__list_projects, mcp__stitch__list_screens, mcp__stitch__get_screen, mcp__stitch__generate_screen_from_text, mcp__stitch__edit_screens, mcp__stitch__generate_variants, mcp__stitch__apply_design_system, mcp__stitch__create_design_system, mcp__stitch__update_design_system, mcp__stitch__list_design_systems
---

# UI Designer

Design UI screens in Google Stitch and produce a Compose Implementation Blueprint with HTML + token inventories persisted for downstream skills.

**Architecture Reference:** @../_shared/patterns.md

## Purpose

`ui-designer` is a **design-only** skill. It handles all Stitch MCP design work and produces a Compose Implementation Blueprint plus persisted HTML + token inventories under `.claude/docs/{featurename}/designs/extracted/`. The blueprint references project-wide rules (`patterns.md`, `m3-colors.md`, `X_COMPONENTS_CATALOG.md`) rather than restating them. It does NOT implement code or invoke other skills.

After `ui-designer` completes, the user can invoke `/modifying-kmp-feature` or `/creating-kmp-feature` to implement the design. Those skills auto-detect the blueprint and enter design-aware mode.

## Workflow

```
[USER INVOKES] → Project Init (one-time, phase-init.md) → Phase 0 (Preflight) → Phase 1 (Design) → [USER APPROVES DESIGN]
                       │ (if stitch-project.json absent                                                         │
                       │  or initState.completedAt null)                                             Blueprint saved
                       │                                                                                         │
                       └──────────────────────────────────────────────────────────────────────────── Next Steps → DONE
```

### Project Init (One-Time Per Repo)
Run when `.claude/docs/_project/stitch-project.json` does not exist or `initState.completedAt` is null. Creates the shared Stitch project, design system, and shared Loading/Failed state screens.
See: [Project Init](phases/phase-init.md)

### Phase 0: Preflight Checks
Verify MCP availability, resolve feature context, load project-wide config, and register or resume the feature in the shared project.
See: [Phase 0: Preflight](phases/phase-0-preflight.md)

### Phase 1: Design in Stitch
Generate screens, iterate with user, export approved designs as screenshots. Generate Implementation Blueprint.
See: [Phase 1: Design](phases/phase-1-design.md)

## Critical Rules

1. **User Confirmation Required** after Phase 1 (design approval) - never proceed without explicit approval
2. **All design changes go through Stitch** - never modify designs outside of Stitch MCP tools
3. **Screenshots stored at** `.claude/docs/{featurename}/designs/` - visible to user
4. **Single config architecture**: All Stitch state lives in `.claude/docs/_project/stitch-project.json`. It is the source of projectId, shared screen IDs, per-feature screen metadata, and `blueprintConsumed`. There are no per-feature `stitch.json` files.
5. **Stitch MCP is mandatory** - if not available, stop and ask user to configure it
6. **Blueprint is the handoff artifact** — contains Pre-Implementation Contract + Post-Implementation Checklist. Implementation skills consume it via `blueprintConsumed` flag in `stitch-project.json.features[featurename]`
7. **`blueprintConsumed` lifecycle** — ui-designer sets `blueprintConsumed: false` in `stitch-project.json.features[featurename]` when saving a new blueprint. Implementation skills set it to `true` after consuming the blueprint
8. **M3 Color Roles Only** - All design colors must map to M3 roles defined in `XTheme.kt`'s `XLightColors` and `XDarkColors`. After design approval, a Color Audit identifies missing roles which are documented in the blueprint's Pre-Implementation Contract. Feature code uses `MaterialTheme.colorScheme.*` exclusively — never hardcoded `Color()`. Custom `XTheme.Colors.*` extensions are last resort for non-semantic colors (gradients, decorative effects).
9. **Project Init is a prerequisite** — Phase 0 preflight checks for `.claude/docs/_project/stitch-project.json` before proceeding. If absent or incomplete, the user must run Project Init first (invoke `/ui-designer` without a feature name argument).
10. **Cross-Screen Chrome Consistency** — When generating a screen for a project that already has approved features, the **shared chrome** (top app bar style, bottom navigation presence/style, screen background) must match the existing feature screens. The only exception is when the user **explicitly** asks for a different chrome ("no bottom nav", "centered title bar", "full-screen modal", etc.). The chrome snapshot is captured in Phase 1 Step 1.1.5 and injected as a "Shared Conventions" block into the Stitch generation prompt (Step 1.2b).

## Stitch MCP Reference

For Stitch tool usage patterns and prompt engineering tips:
See: [Stitch MCP Reference](references/stitch-guide.md)

## Error Handling

| Error | Action |
|-------|--------|
| Stitch MCP not available | Stop. Ask user to configure Stitch MCP server |
| Stitch generation times out / connection reset | **Do NOT retry the generation call** (known Google Stitch bug — the generation often succeeded server-side and a retry creates a duplicate). Ask the user to open `https://stitch.withgoogle.com/projects/{projectId}` in their browser to trigger sync. Wait for confirmation, then call `list_screens` to locate the new screen. |
| Stitch generation fails with a non-timeout error | Retry with refined prompt. Max 3 attempts |
| Stitch project not found | Create new project automatically |
| stitch-project.json not found | Run Project Init first. Invoke `/ui-designer` without a feature name argument. |

## Completion Report

```
## UI Designer Complete: {FeatureName}

Stitch Project ID: {projectId} (shared project)
Design System ID: {designSystemAssetId}
Loading: designs/{featurename}_loading.png (shared)
Failed: designs/{featurename}_failed.png (shared)
Project config: .claude/docs/_project/stitch-project.json

| State | Screenshot |
|-------|------------|
| Success | designs/{featurename}.png |
| Loading | designs/{featurename}_loading.png (shared) |
| Failed | designs/{featurename}_failed.png (shared) |
| Empty | designs/{featurename}_empty.png |

Design spec: designs/{featurename}.md
Blueprint: designs/{featurename}_blueprint.md
Project config: .claude/docs/_project/stitch-project.json
blueprintConsumed: false (set in stitch-project.json.features[{featurename}])

## Next Steps

To implement this design:
- Existing feature → invoke `/modifying-kmp-feature {featurename}`
- New feature → invoke `/creating-kmp-feature {featurename}`

The implementation skill will auto-detect the blueprint and enter design-aware mode.

To verify the implementation against the design:
- invoke `/verify-ui {featurename}`
```
