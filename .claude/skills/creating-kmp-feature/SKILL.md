---
description: Creates complete KMP features with Clean Architecture through PRD generation, task breakdown, orchestrated implementation, and spec-driven cleanup. Automatically activates when user mentions "create feature", "new module", or "add feature".
allowed-tools: ["*"]
---

# Creating KMP Features (Orchestrator)

Orchestrates complete Kotlin Multiplatform feature creation using a structured 5-phase workflow.

## Contents

- [Quick Workflow](#quick-workflow)
- [Critical Rules](#critical-rules)
- [Phases](#phases)
- [Specialized Agents](#specialized-agents)
- [Templates](#templates)
- [Error Handling](#error-handling)

---

## Quick Workflow

```
User Request
    │
    ▼
┌─────────────────────────────────────────────────────────────────┐
│ Phase 0: Context Discovery (AUTO)                               │
│ Detect: PKG_PREFIX, INIT_KOIN_PATH, NAV_HOST_PATH, CORE_MODULES │
└─────────────────────────────────────────────────────────────────┘
    │
    ▼
┌─────────────────────────────────────────────────────────────────┐
│ Phase 1: PRD Generation                                         │
│ Analyze prompt → Generate PRD → Save to .claude/docs/{name}/    │
└─────────────────────────────────────────────────────────────────┘
    │
    ▼  [USER CONFIRMS PRD]
    │
┌─────────────────────────────────────────────────────────────────┐
│ Phase 2: Task Generation                                        │
│ Break PRD into tasks → Assign to agents → Save task files       │
└─────────────────────────────────────────────────────────────────┘
    │
    ▼  [USER CONFIRMS TASKS]
    │
┌─────────────────────────────────────────────────────────────────┐
│ Phase 3: Implementation (Sequential or Parallel)                │
│                                                                 │
│  Option A (Sequential):  Data Agent → UI Agent → Integration    │
│  Option B (Parallel):    Data + UI Agents → Integration         │
└─────────────────────────────────────────────────────────────────┘
    │
    ▼
┌─────────────────────────────────────────────────────────────────┐
│ Phase 4: Cleanup                                                │
│ Verify spec.md → Remove prd.txt + tasks.md + task-*.md          │
└─────────────────────────────────────────────────────────────────┘
    │
    ▼
✅ Feature Complete (spec.md is source of truth)
```

---

## Critical Rules

### User Confirmation Required

- After PRD generation → Show PRD → Wait for approval
- After task generation → Show tasks → Wait for approval
- **Never proceed without explicit confirmation**

### Documentation Storage

- Location: `.claude/docs/{featurename}/`
- Ephemeral (deleted after spec): `prd.txt`, `tasks.md`, `task-*.md`
- Permanent: `spec.md` (single source of truth)

### Architecture Requirements

- Follow **10 critical rules** (see [references/patterns.md](references/patterns.md))
- Complete **4 integration points**
- Validate build after each layer

---

## Phases

| Phase | Purpose | Details |
|-------|---------|---------|
| 0 | Context Discovery | [phases/phase-0-context.md](phases/phase-0-context.md) |
| 1 | PRD Generation | [phases/phase-1-prd.md](phases/phase-1-prd.md) |
| 2 | Task Generation | [phases/phase-2-tasks.md](phases/phase-2-tasks.md) |
| 3 | Implementation | [phases/phase-3-implementation.md](phases/phase-3-implementation.md) |
| 4 | Cleanup | [phases/phase-4-cleanup.md](phases/phase-4-cleanup.md) |

**Execution order**: Phase 0 (auto) → Phase 1 → [confirm] → Phase 2 → [confirm] → Phase 3 → Phase 4

---

## Specialized Agents

| Agent | Layer | Model | Runs |
|-------|-------|-------|------|
| `kmp-data-layer-agent` | Models, DataSource, Repository, Ktor Resources | Sonnet | First (or parallel with UI) |
| `kmp-ui-layer-agent` | UiModel, ViewModel, Screens, Navigation | Sonnet | Second (or parallel with Data) |
| `kmp-integration-agent` | DI module, 4 integration points, spec.md | Sonnet | Last (after both layers) |

### Parallel Execution (Recommended)

Launch Data + UI agents together in ONE message. Both work in isolated contexts.
After both complete, launch Integration agent.

### Sequential Execution

Data → UI → Integration (safer, traditional approach)

---

## Templates

### PRD Templates

| Complexity | Use When | Template |
|------------|----------|----------|
| Simple | UI-only, no API, < 3 screens | [templates/prd-simple.md](templates/prd-simple.md) |
| Complex | API integration, multiple screens | [templates/prd-complex.md](templates/prd-complex.md) |

### Task Template

All tasks follow: [templates/task-template.md](templates/task-template.md)

---

## Error Handling

### Build Errors

Agents will load layer-specific troubleshooting and retry:
- Data layer: [troubleshooting/data.md](troubleshooting/data.md)
- UI layer: [troubleshooting/ui.md](troubleshooting/ui.md)
- Integration: [troubleshooting/integration.md](troubleshooting/integration.md)

### Common Issues

| Issue | Solution |
|-------|----------|
| Unclear prompt | Ask clarifying questions → Update PRD → Reconfirm |
| Agent failure | Review output → Fix issues → Re-invoke agent |
| Build failure | Load troubleshooting → Fix → Retry build |

### Build Commands

```bash
# Incremental (per layer)
./gradlew :feature:{name}:assembleAndroidMain

# Full (integration)
./gradlew assembleDebug

# Format
./gradlew ktlintFormat
```

---

## Architecture References

Agents load these as needed (progressive disclosure):

| File | Content | Used By |
|------|---------|---------|
| [references/patterns.md](references/patterns.md) | 10 critical rules, 4 integration points | All agents |
| [architecture/data.md](architecture/data.md) | Data layer principles | Data agent |
| [architecture/ui.md](architecture/ui.md) | UI layer principles | UI agent |
| [architecture/integration.md](architecture/integration.md) | Integration principles | Integration agent |

**Key Philosophy**: Agents internalize principles and apply intelligently, not copy-paste templates.

---

## Final Report Template

```markdown
## Feature Complete: {FeatureName}

✅ Data layer implemented
✅ UI layer implemented
✅ Integration complete
✅ Build passing + ktlint formatted
✅ Living spec: `.claude/docs/{featurename}/spec.md`
✅ Ephemeral artifacts cleaned

**Next:** Test with `navController.navigate({FeatureName}Route)`
```
