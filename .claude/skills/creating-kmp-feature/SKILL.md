---
description: Creates complete KMP features with Clean Architecture through PRD generation, task breakdown, orchestrated implementation, and spec-driven cleanup. Invoke with /creating-kmp-feature.
allowed-tools: ["Task", "Read", "Write", "Edit", "Glob", "Grep", "Bash(./gradlew:*)", "Bash(mkdir:*)", "Bash(touch:*)", "Bash(rm -f /tmp/.claude-kmpilot-skill-active)", "AskUserQuestion"]
---

# Creating KMP Features

Orchestrates complete feature creation using a 5-phase workflow.

**Architecture Reference:** @../_shared/patterns.md

## Hook Marker (Required)

Before editing any feature files, activate the skill marker so the PreToolUse hook allows edits:
```bash
touch /tmp/.claude-kmpilot-skill-active
```
After Phase 4 completes (or on any early exit), remove it:
```bash
rm -f /tmp/.claude-kmpilot-skill-active
```

## Workflow

**Phase 0** → **Phase 0.5** → **Phase 1** → [USER CONFIRMS] → **Phase 2** → [USER CONFIRMS] → **Activate marker** → **Phase 3** → **Phase 4** → **Remove marker** → Done

### Phase 0: Context Discovery (Auto)
Detect: `PKG_PREFIX`, `INIT_KOIN_PATH`, `NAV_HOST_PATH` from existing features.
See: @phases/phase-0-context.md

### Phase 0.5: Design Artifact Detection

Check for a Stitch design blueprint:

1. **Check blueprint exists**: `.claude/docs/{featurename}/designs/{featurename}_blueprint.md`
2. **Check stitch.json**: `.claude/docs/{featurename}/stitch.json` — read `blueprintConsumed` field
3. **Determine mode**:

| Blueprint exists? | `blueprintConsumed` | Mode |
|-------------------|---------------------|------|
| Yes | `false` | **Design-aware mode** — blueprint drives UI layer |
| Yes | `true` | Normal mode — blueprint already consumed |
| No | N/A | Normal mode — no design artifact |

If entering **design-aware mode**, log:
```
Design artifact detected: .claude/docs/{featurename}/designs/{featurename}_blueprint.md
Entering design-aware mode. Blueprint will drive UI layer implementation.
```

If blueprint exists and `blueprintConsumed == false`, the blueprint's Pre-Implementation Contract will auto-populate the UI section of the PRD in Phase 1.

### Phase 1: PRD Generation
Analyze prompt → Generate PRD → Save to `.claude/docs/{name}/prd.md`
Template: @templates/prd-simple.md or @templates/prd-complex.md
See: @phases/phase-1-prd.md

**Design-aware note**: If blueprint exists, incorporate the blueprint's design tokens, component tree, and color audit into the PRD's UI section automatically.

### Phase 2: Task Generation
Break PRD into tasks → Assign to agents → Save task files
Template: @templates/task-template.md
See: @phases/phase-2-tasks.md

### Phase 3: Implementation
**Parallel** (recommended): Data + UI agents together → Integration agent
**Sequential**: Data → UI → Integration
See: @phases/phase-3-implementation.md

| Agent | Layer | Runs |
|-------|-------|------|
| `data-layer-agent` | Models, DataSource, Repository, Ktor | First (or parallel) |
| `ui-layer-agent` | UiModel, ViewModel, Screens, Navigation | Second (or parallel) |
| `integration-agent` | DI, 4 integration points, spec.md | Last |

### Phase 4: Cleanup
Verify spec.md → Remove prd.md + tasks.md + task-*.md
See: @phases/phase-4-cleanup.md

## Critical Rules

1. **User Confirmation Required** after Phase 1 and Phase 2 - never proceed without explicit approval
2. **Documentation**: `.claude/docs/{featurename}/` - PRD/tasks ephemeral, spec.md permanent
3. **Validate build** after each layer: `./gradlew :feature:{name}:assembleAndroidMain`

## Error Handling

On build failure, load troubleshooting:
- Data: @troubleshooting/data.md
- UI: @troubleshooting/ui.md
- Integration: @troubleshooting/integration.md

## Completion Report

```
## Feature Complete: {FeatureName}
- Data layer implemented
- UI layer implemented
- Integration complete (4 points)
- Build passing + ktlint formatted
- Living spec: .claude/docs/{featurename}/spec.md
- Ephemeral artifacts cleaned
{Design-aware: "- blueprintConsumed set to true in stitch.json"}

Next: navController.navigate({FeatureName}Route)
```
