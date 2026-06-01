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

1. **Before UI agent**: Read the blueprint's **Pre-Implementation Contract** → extract XTheme missing roles. Also read the manifests at:
   - `.claude/docs/{featurename}/designs/extracted/icons.json` (Material Symbols, from `/ui-designer` sub-step 5)
   - `.claude/docs/{featurename}/designs/extracted/images.json` (`<img>` assets, from `/ui-designer` sub-step 6)

   Every entry in both manifests has `download_status: "pending"`.
2. **Materialize Material Symbols XML drawables** by running the shared downloader **without** `--manifest-only` so it actually downloads, applies the JetBrains-required KMP cleanup pass, extends `DesignSystemResources.kt` for any chrome additions, and inline-migrates any stale `feature/X/drawable/{ident}.xml` plus their Kotlin imports for icons that just promoted from domain to chrome:

   ```bash
   python3 .claude/skills/_shared/download_assets.py \
     --type icons \
     --feature {featurename} \
     --project-root {repo_root} \
     --html .claude/docs/{featurename}/designs/extracted/stitch_success.html \
     [--html .claude/docs/_shared/designs/extracted/stitch_loading.html  if needsLoading] \
     [--html .claude/docs/_shared/designs/extracted/stitch_failed.html   if needsFailed] \
     [--html .claude/docs/{featurename}/designs/extracted/stitch_empty.html if needsEmpty]
   ```

   - **Pass `--html` for the same selected states** the manifest covers (`needsLoading`/`needsFailed`/`needsEmpty` flags from `stitch-project.json.features[{featurename}].states`).
   - **Why before XTheme**: icons are a deterministic script with no LLM reasoning; running it first surfaces network/404 failures early before any UI work begins. If any download fails, the script reports `http-404` etc. in its summary — fix the failing icon name in the design, re-run `/ui-designer` to refresh the manifest, then retry.
   - **What the script does in full mode** (matches the manifest's predictions):
     - Downloads each XML from `google/material-design-icons` master, applies the cleanup pass (strip `android:tint`, strip `android:autoMirrored`, replace `@android:color/white` with `#000000`), writes to the path the manifest declared.
     - For chrome icons: extends `core/designsystem/.../DesignSystemResources.kt` idempotently (`val {ident} = Res.drawable.{ident}` inside `object drawable`).
     - For promoted icons (those whose `users` set includes another feature): deletes the stale `feature/{other}/.../drawable/{ident}.xml` AND rewrites every `.kt` under `feature/{other}/src/` from `Res.drawable.{ident}` → `DesignSystemResources.drawable.{ident}` (imports fixed). Doc-artifact promotion of other features' manifests was already done by `/ui-designer`; this step just brings their source in sync.
   - **Idempotent**: existing XMLs are skipped, existing `DesignSystemResources` entries are skipped, Kotlin files that no longer reference `Res.drawable.{ident}` are skipped. Safe to re-run after a failed mid-flight.
   - After this step, run `./gradlew :core:designsystem:assembleAndroidMain` to confirm `DesignSystemResources.kt` additions compile cleanly.
3. **Materialize `<img>` assets** by running the same downloader with `--type images`:

   ```bash
   python3 .claude/skills/_shared/download_assets.py \
     --type images \
     --feature {featurename} \
     --project-root {repo_root} \
     --html .claude/docs/{featurename}/designs/extracted/stitch_success.html \
     [--html .claude/docs/_shared/designs/extracted/stitch_loading.html  if needsLoading] \
     [--html .claude/docs/_shared/designs/extracted/stitch_failed.html   if needsFailed] \
     [--html .claude/docs/{featurename}/designs/extracted/stitch_empty.html if needsEmpty]
   ```

   - Same skill-ownership model as icons: downloads each `<img>` from its Stitch CDN URL, detects extension from `Content-Type` (PNG / JPEG / WebP), places files at the path the manifest declared, extends `DesignSystemResources.kt` for chrome assets, inline-migrates stale references in other features.
   - Idempotent: existing files are skipped via `skip-exists`.
   - The blueprint references images by `res_reference` (e.g. `Res.drawable.failed_background`); the UI agent emits `Image(painter = painterResource(...))` exactly as declared. **Do NOT emit `AsyncImage` for these — that composable is reserved for runtime data, not bundled design assets.**
4. **XTheme update**: Add all missing M3 roles from the contract to **both** `XLightColors` and `XDarkColors` in `XTheme.kt`. Verify build: `./gradlew :core:designsystem:assembleAndroidMain`
5. **X-Component Constraint Check**: Collect the unique set of design system source files needed by the blueprint's Component Tree (one file may define many composables — e.g. `XButton.kt` defines `XButton`, `XOutlinedButton`, `XIconButton`, `XTextIconButton`, `XOutlinedIconButton`). Read each file in full and catalog **every composable defined in it**, not just the one the blueprint named. For each composable, extract:
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
6. **Pass blueprint to UI agent**: Include the blueprint path, design screenshots, constraint conflict list, **and the now-materialized icons and images manifests** as context. The blueprint's Component Tree references icons and images by `res_reference` (e.g. `Res.drawable.qr_code_scanner`, `DesignSystemResources.drawable.arrow_back` for icons; `Res.drawable.failed_background` for images); the UI agent emits `XIcon(painter = painterResource({res_reference}))` for icons and `Image(painter = painterResource({res_reference}))` for images exactly as declared. The blueprint's Component Tree is the primary source for UI implementation; design screenshots are visual cross-reference only.

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
- Localization (Rule 12): create `composeResources/values/strings.xml`; ALL display text via `stringResource(Res.string.*)` — no hardcoded literals. If a blueprint is present, use its String Inventory keys.
- Expected: UI layer complete (incl. strings.xml) + build validation
```

**Wait for completion** → Verify success

#### Step 3: Integration
```
Invoke integration-agent with:
- Feature name: {featurename}
- Task files: .claude/docs/{featurename}/task-*-integration-*.md
- Project context:
  - PKG_PREFIX, PKG_PATH, PROJECT_NAMESPACE
  - CORE_COMMON_PKG, CORE_DATA_PKG, CORE_DESIGNSYSTEM_PKG
  - INIT_KOIN_PATH, NAV_HOST_PATH, CORE_MODULES
- Bottom-bar tab: read the PRD Navigation section — if the feature is a top-level tab, pass its label/icon/order (Integration Point 5); otherwise it is a pushed screen (skip point 5)
- Expected: integration points 1–4 (+ point 5 if a tab) + full build + ktlint + spec.md
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
   - Localization (Rule 12): create `composeResources/values/strings.xml`; ALL display text via `stringResource(Res.string.*)` — no hardcoded literals. If a blueprint is present, use its String Inventory keys.
```

Each agent works in isolated context window.

**Wait for BOTH to complete** → Verify both succeeded

#### Step 2: Launch Integration Agent
```
Invoke integration-agent with:
- Feature name: {featurename}
- Project context: PKG_PREFIX, PKG_PATH, PROJECT_NAMESPACE, CORE_COMMON_PKG,
  CORE_DATA_PKG, CORE_DESIGNSYSTEM_PKG, INIT_KOIN_PATH,
  NAV_HOST_PATH, CORE_MODULES
- Bottom-bar tab: read the PRD Navigation section — if a top-level tab, pass label/icon/order (point 5); else pushed screen
- Integrates both data and UI layers
- Completes integration points 1–4 (+ point 5 if a tab)
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
