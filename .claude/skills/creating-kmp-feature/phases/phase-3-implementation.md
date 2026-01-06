# Phase 3: Orchestrated Implementation

**Purpose**: Invoke specialized agents to implement the feature layers.

**Prerequisites**: PRD and tasks confirmed by user.

---

## Checklist

```
Implementation Progress:
- [ ] Step 3.1: Choose execution strategy (Sequential or Parallel)
- [ ] Step 3.2: Invoke specialized agents
- [ ] Step 3.3: Verify spec generation
```

---

## Step 3.1: Execution Strategy

Ask user for preference:

| Strategy | Flow | When to Use |
|----------|------|-------------|
| **Sequential** | Data → UI → Integration | Safer, traditional |
| **Parallel** (Recommended) | Data + UI simultaneously → Integration | Faster |

---

## Step 3.2: Invoke Specialized Agents

### Option A: Sequential Execution

#### Step 1: Data Layer
```
Invoke kmp-data-layer-agent with:
- Feature name: {featurename}
- Task files: .claude/docs/{featurename}/task-*-data-*.md
- Project context:
  - PKG_PREFIX, PKG_PATH
  - CORE_COMMON_PKG, CORE_DATA_PKG
  - CORE_MODULES, DESIGN_SYSTEM_PKG
- Expected: Data layer complete + build validation
```

**Wait for completion** → Verify success

#### Step 2: UI Layer
```
Invoke kmp-ui-layer-agent with:
- Feature name: {featurename}
- Task files: .claude/docs/{featurename}/task-*-ui-*.md
- Project context:
  - PKG_PREFIX, PKG_PATH
  - CORE_COMMON_PKG, CORE_DESIGNSYSTEM_PKG
  - DESIGN_SYSTEM_PKG
- Expected: UI layer complete + build validation
```

**Wait for completion** → Verify success

#### Step 3: Integration
```
Invoke kmp-integration-agent with:
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
1. kmp-data-layer-agent with:
   - Feature name: {featurename}
   - Project context: PKG_PREFIX, PKG_PATH, CORE_COMMON_PKG,
     CORE_DATA_PKG, CORE_MODULES, DESIGN_SYSTEM_PKG

2. kmp-ui-layer-agent with:
   - Feature name: {featurename}
   - Project context: PKG_PREFIX, PKG_PATH, CORE_COMMON_PKG,
     CORE_DESIGNSYSTEM_PKG, DESIGN_SYSTEM_PKG
```

Each agent works in isolated context window.

**Wait for BOTH to complete** → Verify both succeeded

#### Step 2: Launch Integration Agent
```
Invoke kmp-integration-agent with:
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

## Step 3.3: Verify Spec Generation

After all agents complete, verify the living specification was generated:

```bash
ls -la .claude/docs/{featurename}/spec.md
```

**Expected**: The spec.md file should exist and contain the complete specification.

**If spec.md exists** → Proceed to Phase 4 (Cleanup)

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
- DESIGN_SYSTEM_PKG: {value}

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
- Ready to proceed to **Phase 4: Cleanup**
