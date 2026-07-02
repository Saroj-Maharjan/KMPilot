# Phase 1 (cont.): Step 1.14 — State Designs (Selected States Only)

> Continuation of [phase-1-design.md](phase-1-design.md) — same step numbering. **Read this file only when at least one optional state was selected in Step 1.7** (`needsLoading` / `needsFailed` / `needsEmpty`). If none was selected, skip this file entirely and go straight to [phase-1-finalize.md](phase-1-finalize.md).

---

## Step 1.14: Generate State Designs (Selected States Only)

This step is gated on the selections from Step 1.7 (`needsLoading`, `needsFailed`, `needsEmpty`).

| State | Action when selected | Action when NOT selected |
|-------|---------------------|--------------------------|
| Loading | Reuse shared screen — no generation | **Skip entirely** — no design, no token, no blueprint section |
| Failed | Reuse shared screen — no generation | **Skip entirely** |
| Empty | Generate by editing the approved success screen, then iterate via approve-or-edit loop | **Skip entirely** |

### Loading State

Skip if `needsLoading == false`. Otherwise no generation needed — reference the shared screen at `.claude/docs/_shared/designs/loading.png` (HTML and tokens under `_shared/designs/extracted/`; screen ID at `stitch-project.json.sharedStateScreens.loading.screenId`).

### Failed State

Skip if `needsFailed == false`. Otherwise no generation needed — reference the shared screen at `.claude/docs/_shared/designs/failed.png` (HTML and tokens under `_shared/designs/extracted/`; screen ID at `stitch-project.json.sharedStateScreens.failed.screenId`).

### Empty State

Skip entirely if `needsEmpty == false`. Otherwise generate by editing the approved success screen and iterate with the user via a single approve-or-edit loop.

**Resume check**: If `.claude/docs/{featurename}/designs/{featurename}_empty.png` already exists AND `stitch-project.json.features[featurename].emptyScreenId` is non-null, skip generation — the prior run's screenshot is still valid. Jump to **Approve-or-Edit Loop** below using the stored `emptyScreenId`.

#### Initial Generation

The empty state is **derived from** the approved success screen with `generate_variants` (creates a **new** screen; the success screen is preserved). Never call `edit_screens` — it is broken via MCP ([Edit-as-Variant Pattern](../references/stitch-guide.md#edit-as-variant-pattern-all-design-edits)).

1. **Record baseline**: Call `mcp__stitch__list_screens` with `projectId` from `stitch-project.json`.
2. **Call** `mcp__stitch__generate_variants` with:
   ```
   projectId: {stitch-project.json.projectId}
   selectedScreenIds: [{approved_success_screenId}]
   prompt: "Keep everything exactly the same (toolbar, background, colors, bottom navigation, overall structure). Only replace the main content area with a centered icon or illustration indicating no items, and a message like 'No {items} yet' in muted text. Remove all list items and show only the empty state in the content area."
   deviceType: MOBILE
   modelId: GEMINI_3_FLASH
   variantOptions:
     variantCount: 1
     creativeRange: "REFINE"
   ```
3. **Handle timeout / connection errors**: If the call times out or fails with a connection reset, **do NOT retry** — this is a known Google Stitch bug where the request usually completed server-side and retrying produces duplicate screens. Run the **Screen Sync Procedure** (Step 1.10) immediately. Only retry the generation if `list_screens` confirms no new screen appeared after the browser sync (max 3 attempts total).
4. **Screen Sync Procedure**: Ask the user to open the project in their browser and confirm the new screen is visible. Wait for confirmation before calling `list_screens`. Max 2 sync attempts.
5. **Identify new screen**: Compare screen list with baseline to find the newly created screen ID. This is the working `emptyScreenId` (the success screen is untouched).
6. **Download**: `curl -sL "{downloadUrl}=s0" -o .claude/docs/{featurename}/designs/{featurename}_empty.png`

#### Approve-or-Edit Loop

After each generation (initial or post-edit), tell the user the empty state screenshot is ready and give its file path — **do not read/display inline** — then ask via `AskUserQuestion`:

> **"How does the empty state design look?"**

| Option | Description |
|--------|-------------|
| Approve (Recommended) | Use this design as the final empty state |
| Edit | Request specific changes to the empty state |

**If Approve** → exit the loop. Proceed to **Persist Empty State**.

**If Edit** — apply the [Edit-as-Variant Pattern](../references/stitch-guide.md#edit-as-variant-pattern-all-design-edits) (`edit_screens` is broken via MCP; the edit produces a **new** screen that becomes the working empty screen):
1. Use `AskUserQuestion` (free text via "Other") to capture the user's edit request, OR collect the request inline if the user already specified it.
2. Record baseline by calling `mcp__stitch__list_screens`.
3. Call `mcp__stitch__generate_variants` with:
   ```
   projectId: {stitch-project.json.projectId}
   selectedScreenIds: [{current emptyScreenId}]
   prompt: "{user's edit request}. Keep everything else exactly the same."
   deviceType: MOBILE
   modelId: GEMINI_3_FLASH
   variantOptions:
     variantCount: 1
     creativeRange: "REFINE"
   ```
4. Apply the same timeout/connection-reset handling as the initial generation (Screen Sync Procedure, no blind retries).
5. Diff `list_screens` against baseline to find the new screen ID. Update the working `emptyScreenId` to this new ID.
6. Call `get_screen` for the new ID and re-download as `.claude/docs/{featurename}/designs/{featurename}_empty.png` (overwrite).
7. Return to the top of the Approve-or-Edit Loop.

**Iteration limit**: Maximum 10 edit iterations for the empty state. If not converging, ask the user to clarify requirements before continuing.

#### Persist Empty State

After the user approves:

- `stitch-project.json.features[featurename].emptyScreenId = {approved emptyScreenId}`
- Update `stitch-project.json.features[featurename].updatedAt`. Write the file.

---

**Step 1.14 state designs done → continue in [phase-1-finalize.md](phase-1-finalize.md)** ("Save Design Description" always runs there, then Steps 1.15–1.19).
