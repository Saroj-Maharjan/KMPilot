---
description: Design UI screens in Google Stitch with optional code implementation and visual verification. Three modes available for design-only, design+implement, or full visual verification pipeline.
argument-hint: [feature-name]
allowed-tools: Task, Read, Write, Edit, Glob, Grep, Bash(mkdir *), Bash(ls *), Bash(curl *), Bash(rm *), Bash(./gradlew *), Bash(touch *), AskUserQuestion, Skill, mcp__stitch__create_project, mcp__stitch__get_project, mcp__stitch__list_projects, mcp__stitch__list_screens, mcp__stitch__get_screen, mcp__stitch__generate_screen_from_text, mcp__stitch__edit_screens, mcp__stitch__generate_variants
---

# UI Designer

Design UI screens in Google Stitch, optionally implement them in code, and visually verify on device.

**Architecture Reference:** @../_shared/patterns.md

## Modes

| Mode | Description | MCP Required |
|------|-------------|--------------|
| **1. Design Only** | Design screens in Stitch, export screenshots, get user approval | Stitch |
| **2. Design + Implement** | Design in Stitch, then implement UI code after approval | Stitch |
| **3. Design + Implement + Verify** | Full pipeline: design, implement, run on device, screenshot-compare | Stitch + Mobile |

## Delegation Model

**`ui-designer` does NOT edit feature files directly.** It is a design orchestrator that:
1. Handles all Stitch MCP design work (Phase 0 + Phase 1)
2. Delegates implementation to `/modifying-kmp-feature` (existing feature) or `/creating-kmp-feature` (new feature), passing the Compose Implementation Blueprint
3. Delegates visual verification to Phase 3 (which re-invokes the implementation skill if fixes are needed)

The implementation skills own hook markers, file edits, builds, and formatting.

## Workflow

```
[USER INVOKES] → Phase 0 (Preflight) → Phase 1 (Design) → [USER APPROVES DESIGN]
                                                                    │
                                          ┌─────────────────────────┤
                                          │                         │
                                    Mode 1: DONE            Mode 2 or 3:
                                                                    │
                                                     Phase 2 (Implement from Blueprint)
                                                              → [IMPLEMENTATION COMPLETES]
                                                                    │
                                          ┌─────────────────────────┤
                                          │                         │
                                    Mode 2: DONE              Mode 3:
                                                                    │
                                                          Phase 3 (Verify)
                                                              → [VISUAL MATCH ≥90%]
                                                              → [USER CONFIRMS] → DONE
```

### Phase 0: Preflight Checks
Verify MCP availability, detect/create Stitch project, resolve feature context.
See: [Phase 0: Preflight](phases/phase-0-preflight.md)

### Phase 1: Design in Stitch
Generate screens, iterate with user, export approved designs as screenshots. Generate Implementation Blueprint for Modes 2 & 3.
See: [Phase 1: Design](phases/phase-1-design.md)

### Phase 2: Implementation (Modes 2 & 3)
Implement from Compose Implementation Blueprint with design screenshots as visual cross-reference.
See: [Phase 2: Implement](phases/phase-2-implement.md)

### Phase 3: Visual Verification (Mode 3 Only)
Run on device, screenshot, compare with Stitch design. Retry if <90% match.
See: [Phase 3: Verify](phases/phase-3-verify.md)

## Critical Rules

1. **User Confirmation Required** after Phase 1 (design approval) - never proceed without explicit approval
2. **All design changes go through Stitch** - never modify designs outside of Stitch MCP tools
3. **Screenshots stored at** `.claude/docs/{featurename}/designs/` - visible to user
4. **Stitch project tracked at** `.claude/docs/{featurename}/stitch.json` - for project continuity
5. **Stitch MCP is mandatory** - if not available, stop and ask user to configure it
6. **Mobile MCP is mandatory for Mode 3** - if not available, stop and inform user. Mobile MCP tool names are dynamic (depend on server configuration, typically `mcp__claude_in_mobile__*`) and cannot be pre-listed in allowed-tools — the user must manually approve each mobile MCP tool call at runtime
7. **Never edit feature files directly** - always delegate to `/modifying-kmp-feature` or `/creating-kmp-feature`
8. **M3 Color Roles Only** - All design colors must map to M3 roles defined in `XTheme.kt`'s `lightColorScheme`. After design approval, a Color Audit identifies missing roles which are added to `lightColorScheme` before implementation. Feature code uses `MaterialTheme.colorScheme.*` exclusively — never hardcoded `Color()`. Custom `XTheme.Colors.*` extensions are last resort for non-semantic colors (gradients, decorative effects).

## Stitch MCP Reference

For Stitch tool usage patterns and prompt engineering tips:
See: [Stitch MCP Reference](references/stitch-guide.md)

## Error Handling

| Error | Action |
|-------|--------|
| Stitch MCP not available | Stop. Ask user to configure Stitch MCP server |
| Mobile MCP not available (Mode 3) | Stop. Ask user to configure mobile MCP server |
| Stitch generation fails | Retry with refined prompt. Max 3 attempts |
| Implementation skill failure (Mode 2/3) | Review error, adjust design context, re-invoke skill |
| Visual match <90% (Mode 3) | Prepare fix context, re-invoke implementation skill. Max 3 attempts |
| Stitch project not found | Create new project automatically |

## Completion Report

```
## UI Designer Complete: {FeatureName}

Stitch Project ID: {projectId}
Screenshots: designs/{featurename}.png (+ _loading, _failed, _empty)
Design spec: designs/{featurename}.md
Blueprint: {designs/{featurename}_blueprint.md (Modes 2 & 3) | N/A (Mode 1)}
Stitch config: .claude/docs/{featurename}/stitch.json
Build: {passing + ktlint formatted (Modes 2 & 3) | N/A (Mode 1)}
Device screenshots: {designs/device/device_{featurename}.png (Mode 3) | N/A}
Visual match: {percentage% (Mode 3) | N/A}
```
