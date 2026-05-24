# Phase 4: Orchestrated Implementation

**Purpose**: Invoke specialized agents to implement the feature layers.

**Prerequisites**: PRD and tasks confirmed by user.

---

## Checklist

```
Implementation Progress:
- [ ] Step 4.1: Choose execution strategy (Sequential or Parallel)
- [ ] Step 4.2: Invoke specialized agents
- [ ] Step 4.3: Verify spec generation
```

---

## Step 4.1: Execution Strategy

Ask user for preference:

| Strategy | Flow | When to Use |
|----------|------|-------------|
| **Sequential** | Data → UI → Integration | Safer, traditional |
| **Parallel** (Recommended) | Data + UI simultaneously → Integration | Faster |

---

## Step 4.2: Invoke Specialized Agents

### Design-Aware Blueprint Passthrough

If in **design-aware mode** (Phase 1 detected an unconsumed blueprint):

1. **Before UI agent**: Read the blueprint's **Pre-Implementation Contract** → extract XTheme missing roles
2. **XTheme update**: Add all missing M3 roles from the contract to **both** `XLightColors` and `XDarkColors` in `XTheme.kt`. Verify build: `./gradlew :core:designsystem:assembleAndroidMain`
3. **X-Component Constraint Check**: Collect the unique set of design system source files needed by the blueprint's Component Tree (one file may define many composables — e.g. `XButton.kt` defines `XButton`, `XOutlinedButton`, `XIconButton`, `XTextIconButton`, `XOutlinedIconButton`). Read each file in full and catalog **every composable defined in it**, not just the one the blueprint named. For each composable, extract:
   - `defaultMinSize` constraints (e.g. `XButton` enforces `minWidth=100.dp, minHeight=44.dp`)
   - Default parameter values that differ from the blueprint's intent (e.g. `XIconButton` defaults to a visible `surface` background)
   - Hardcoded internal padding that overrides `contentPadding` (e.g. `XTextField` hardcodes `top=10.dp, bottom=10.dp`)
   - Any internal `Modifier` applied via `.then(...)` that the caller cannot override

   Reading the whole file matters: the UI agent may legitimately reach for a sibling composable in the same file, and it needs those constraints too.

   For each conflict, decide the resolution before the UI agent writes any code:
   - Override via modifier: `Modifier.defaultMinSize(Dp.Unspecified)`
   - Override via parameter: explicit `colors`, `shape`, or `contentPadding`
   - Accept as architectural limitation: note it in the agent prompt

   **Pass the conflict list to the UI agent** as additional context alongside the blueprint.
4. **Pass blueprint to UI agent**: Include the blueprint path, design screenshots, and constraint conflict list as context. The blueprint's Component Tree is the primary source for UI implementation; design screenshots are visual cross-reference only.

### Option A: Sequential Execution

#### Step 1: Data Layer
```
Invoke data-layer-agent with:
- Feature name: {featurename}
- Task files: .claude/docs/{featurename}/task-*-data-*.md
- Project context:
  - PKG_PREFIX, PKG_PATH
  - CORE_COMMON_PKG, CORE_DATA_PKG
  - CORE_MODULES, CORE_DESIGNSYSTEM_PKG
- Expected: Data layer complete + build validation
```

**Wait for completion** → Verify success

#### Step 2: UI Layer
```
Invoke ui-layer-agent with:
- Feature name: {featurename}
- Task files: .claude/docs/{featurename}/task-*-ui-*.md
- Project context:
  - PKG_PREFIX, PKG_PATH
  - CORE_COMMON_PKG, CORE_DESIGNSYSTEM_PKG
- Design-aware context (if applicable):
  - Blueprint: .claude/docs/{featurename}/designs/{featurename}_blueprint.md
  - Success screenshot: .claude/docs/{featurename}/designs/{featurename}.png
  - State coverage: read `features[{featurename}].states` from `.claude/docs/_project/stitch-project.json`.
    Include shared screenshots only for selected states (`.claude/docs/_shared/designs/loading.png` if `states.loading`, `.claude/docs/_shared/designs/failed.png` if `states.failed`).
    Include `.claude/docs/{featurename}/designs/{featurename}_empty.png` if `states.empty`. Skipped states have no screenshot; the blueprint marks them "Skipped" so the agent uses generic handling.
- Expected: UI layer complete + build validation
```

**Wait for completion** → Verify success

#### Step 3: Integration
```
Invoke integration-agent with:
- Feature name: {featurename}
- Task files: .claude/docs/{featurename}/task-*-integration-*.md
- Project context:
  - PKG_PREFIX, PKG_PATH
  - CORE_COMMON_PKG, CORE_DATA_PKG, CORE_DESIGNSYSTEM_PKG
  - INIT_KOIN_PATH, NAV_HOST_PATH, CORE_MODULES
- Expected: All 4 integration points + full build + ktlint + spec.md
```

**Wait for completion** → Verify success

---

### Option B: Parallel Execution (Recommended)

#### Step 1: Launch Data + UI Agents in Parallel

**In ONE message**, invoke BOTH agents simultaneously:

```
1. data-layer-agent with:
   - Feature name: {featurename}
   - Project context: PKG_PREFIX, PKG_PATH, CORE_COMMON_PKG,
     CORE_DATA_PKG, CORE_MODULES, CORE_DESIGNSYSTEM_PKG

2. ui-layer-agent with:
   - Feature name: {featurename}
   - Project context: PKG_PREFIX, PKG_PATH, CORE_COMMON_PKG,
     CORE_DESIGNSYSTEM_PKG
   - Design-aware context (if applicable):
     - Blueprint: .claude/docs/{featurename}/designs/{featurename}_blueprint.md
     - Success screenshot: .claude/docs/{featurename}/designs/{featurename}.png
     - State coverage: read `features[{featurename}].states` from `.claude/docs/_project/stitch-project.json` and include shared/empty screenshots only for selected states (loading/failed live under `.claude/docs/_shared/designs/`; empty under `.claude/docs/{featurename}/designs/{featurename}_empty.png`). Skipped states have no screenshot.
```

Each agent works in isolated context window.

**Wait for BOTH to complete** → Verify both succeeded

#### Step 2: Launch Integration Agent
```
Invoke integration-agent with:
- Feature name: {featurename}
- Project context: PKG_PREFIX, PKG_PATH, CORE_COMMON_PKG,
  CORE_DATA_PKG, CORE_DESIGNSYSTEM_PKG, INIT_KOIN_PATH,
  NAV_HOST_PATH, CORE_MODULES
- Integrates both data and UI layers
- Completes 4 integration points
- Final validation + formatting
- Generates spec.md
```

**Wait for completion** → Verify success

---

### Post-Agent: Design-Aware Finalization

If in **design-aware mode**, after all agents complete successfully:

1. **Verify Post-Implementation Checklist** from the blueprint
2. **Set `blueprintConsumed: true`** in `.claude/docs/_project/stitch-project.json` under `features[{featurename}]`

---

## Step 4.3: Verify Spec Generation

After all agents complete, verify the living specification was generated:

```bash
ls -la .claude/docs/{featurename}/spec.md
```

**Expected**: The spec.md file should exist and contain the complete specification.

**If spec.md exists** → Proceed to Phase 5 (Cleanup)

**If spec.md missing** → Check integration agent output, may need to re-invoke

---

## Agent Context Passing

When invoking each agent, include the full project context from Phase 0:

```markdown
## Project Context

- PKG_PREFIX: {value}
- PKG_PATH: {value}
- CORE_COMMON_PKG: {value}
- CORE_DATA_PKG: {value}
- CORE_DESIGNSYSTEM_PKG: {value}
- INIT_KOIN_PATH: {value}
- NAV_HOST_PATH: {value}

## Feature

- Name: {featurename}
- Docs: .claude/docs/{featurename}/
```

---

## Error Handling

| Error | Action |
|-------|--------|
| Agent build failure | Agent loads troubleshooting, fixes, retries |
| Agent reports failure | Review output, fix issues, re-invoke |
| Timeout | Check agent status, may need to restart |

---

## Output

After all agents complete:
- Data layer implemented and validated
- UI layer implemented and validated
- Integration complete (4 points)
- Build passing + ktlint formatted
- spec.md generated
- (Design-aware) blueprintConsumed set to true in stitch-project.json
- Ready to proceed to **Phase 5: Cleanup**
