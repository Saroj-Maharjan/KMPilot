# Phase 1: Design in Stitch

**Purpose**: Generate UI screens in Google Stitch, iterate with user feedback, and export approved designs.

**Prerequisites**: Phase 0 complete. Stitch project ID and feature context available.

---

## Checklist

```
Design Progress:
- [ ] Step 1.1: Gather screen requirements
- [ ] Step 1.2: Cross-Screen Chrome Consistency Snapshot (MANDATORY when prior features exist)
- [ ] Step 1.3: Detect explicit user override (chrome)
- [ ] Step 1.4: Snapshot existing chrome
- [ ] Step 1.5: Build the Shared Conventions block
- [ ] Step 1.6: Confirm with user (only when overriding)
- [ ] Step 1.7: State Coverage Selection (MANDATORY) — user picks which optional states this feature needs
- [ ] Step 1.8: Design missing shared screens (conditional) — runs only when opted-in shared screen does not yet exist
- [ ] Step 1.9: Read current XTheme color scheme (MANDATORY)
- [ ] Step 1.10: Generate screens in Stitch
- [ ] Step 1.11: Present designs to user
- [ ] Step 1.12: Iterate on feedback (if needed)
- [ ] Step 1.13: Finalize approved success design
- [ ] Step 1.14: Generate state designs for selected optional states only
- [ ] Step 1.15: Acquire HTML & Token Inventories for selected states (MANDATORY)
- [ ] Step 1.16: Color Audit — reconciled against HTML inventories (MANDATORY)
- [ ] Step 1.17: Generate Implementation Blueprint
- [ ] Step 1.18: Update stitch-project.json
- [ ] Step 1.19: User final approval
```

---

## Step 1.1: Gather Screen Requirements

> **Scope**: This skill handles **one screen per invocation**. For multi-screen features, invoke `/ui-designer` once per screen.

### Screen Count Check (Fail Fast)

**Before parsing requirements**, count distinct screens implied by the user's prompt, the spec, or the PRD. Indicators of multiple screens: explicit lists ("login and dashboard"), connectives ("then", "after that", "followed by"), or screen-name plurals.

If **>1 screen** is detected, fail fast — do **not** silently scope down. Use `AskUserQuestion`:

> "Detected {N} screens in your request: {comma-separated list}. `/ui-designer` handles one screen per invocation. Which would you like to design first?"

Each option = one screen name (max 4; if more, show top 4 plus "Other"). Use the user's pick as the sole screen for this invocation. The remaining screens stay for subsequent `/ui-designer` runs.

If exactly **1 screen** is detected, proceed.

### Determine Screen Details

Determine what the screen needs to show. Sources (in priority order):

1. **User's prompt**: "design a login screen with email/password fields"
2. **Existing spec**: Load `.claude/docs/{featurename}/spec.md` if available (Section 4.5 UI Design)
3. **Existing PRD**: Load `.claude/docs/{featurename}/prd.md` if available
4. **Ask user**: If requirements are unclear, use `AskUserQuestion`

For the screen, capture:
- **Screen name** (e.g., "Main Screen", "Detail Screen", "Settings Screen")
- **Key elements** (buttons, lists, cards, forms, navigation)
- **States** (loading, success, empty, error - per the 4-state pattern)
- **Visual style notes** (dark theme, accent colors, typography preferences)

### List-Based Determination (`isListBased`)

A screen is **list-based** when its primary content is a collection of homogeneous items (`LazyColumn`, `LazyRow`, `LazyVerticalGrid`, `LazyVerticalStaggeredGrid`). This drives whether the Empty option is offered in Step 1.7 — an empty state only makes sense when there is a list that can be empty.

- **List-based**: feed, search results, message list, transactions, notifications, gallery — the success state is "many of the same thing".
- **Not list-based**: forms (login, settings, profile edit), detail views (product detail, transaction detail), modal/wizard steps, dashboards with fixed sections (a sub-list inside a dashboard does **not** count — design empty separately if needed).

If the determination is unambiguous from requirements, set `isListBased` directly. If it isn't, ask:

> **"Is this screen primarily a scrollable list/grid of items where 'no items yet' is a meaningful state?"** — Yes / No.

Record `isListBased` in working context for Step 1.7.

### Screen State Coverage

Only the **success state is mandatory**. Loading, failed, and empty are **optional per feature** — the user selects which they need in Step 1.7, gated on the per-state conditions below.

| State | Inclusion gate | Source when included |
|-------|----------------|----------------------|
| **Success** | Always | Generated per-feature in shared project |
| **Loading** | User opt-in | **Shared screen from project config** — reused as-is; never re-designed per-feature |
| **Failed** | User opt-in | **Shared screen from project config** — reused as-is; never re-designed per-feature |
| **Empty** | User opt-in **AND** `isListBased == true` — both required, neither is sufficient alone | Generated per-feature by editing the approved success screen, then iterated via single approve-or-edit loop |

If the user opts **out** of a state — or, for Empty, if `isListBased == false` — that state is **skipped entirely** for the feature: no design, no token inventory, no blueprint section, no implementation reference. Implementation skills will fall back to generic handling for skipped states (Rule 4 in `patterns.md` still applies — the feature code must still handle all UI states, just without a design reference for the skipped ones).

Steps 1.10–1.13 handle the **success state** variant selection. Step 1.14 generates the remaining state designs, gated on Step 1.7 selections.

---

## Step 1.2: Cross-Screen Chrome Consistency Snapshot (MANDATORY)

**Purpose**: Project screens must share consistent chrome (top app bar, bottom navigation, screen background). Snapshot the existing convention from approved features so the new screen inherits it — unless the user explicitly asks for a different chrome.

### When to run

- Inspect `stitch-project.json.features` and collect every entry where `approved == true` **and** `featurename != current featurename`.
- **If zero approved features exist** (this is the first feature being designed) → skip Steps 1.2–1.6 entirely and proceed to Step 1.7.
- **If one or more approved features exist** → proceed to Step 1.3.

---

## Step 1.3: Detect Explicit User Override (Chrome)

Scan the user's prompt and the requirements gathered in Step 1.1 for explicit chrome instructions. Treat any of the following as an **explicit override**:

- Top app bar: "no top bar", "no toolbar", "no app bar", "centered title", "large title", "transparent toolbar", "custom header", "hero header", "remove the top bar", "full-bleed".
- Bottom navigation: "no bottom nav", "without bottom bar", "remove bottom nav", "add bottom navigation" (when current convention has none), "tab bar", "full-screen modal", "dialog".
- Background: "different background", "image background", "gradient background".

If an explicit override is detected, record which element(s) are overridden and proceed. The override-affected element(s) are excluded from the conventions block (Step 1.5). All non-overridden elements still inherit from the snapshot.

If no override is detected, all elements inherit.

---

## Step 1.4: Snapshot Existing Chrome

Pick the **most recently approved** feature (max `approvedAt`) among the collected entries — this is the "reference feature".

Read its token inventory: `.claude/docs/{reference_feature}/designs/extracted/tokens_success.md`. Also read the HTML excerpt if needed: `.claude/docs/{reference_feature}/designs/extracted/stitch_success.html` (header and footer regions only).

Extract:

| Element | What to capture |
|---------|-----------------|
| **Top app bar** | Presence (yes/no), height (dp), background color (hex + M3 role), title alignment (start/center), title typography (size/weight), leading icon (back arrow / menu / none), trailing icons (count + style) |
| **Bottom navigation** | Presence (yes/no). If present: height (dp), background color (hex + M3 role), item count, item style (icon-only / icon+label), selected/unselected color treatment |
| **Screen background** | Color (hex + M3 role) or surface treatment |

If multiple approved features disagree on a property, surface the disagreement to the user via `AskUserQuestion`:

> "Existing features differ on {property}: {feature A → value A}, {feature B → value B}. Which should the new screen follow?"

Options = each distinct value seen, plus "Other (specify)".

---

## Step 1.5: Build the Shared Conventions Block

Produce a `Shared Conventions` markdown block to inject into the Stitch prompt in Step 1.10. Only include elements that are **not** explicitly overridden by the user.

Example:

```
Shared conventions inherited from existing project screens (reference: {reference_feature}):
- Top app bar: present, 56dp height, background #1C1910 (M3 surface), title start-aligned in onSurface, leading back arrow icon, no trailing icons.
- Bottom navigation: present, 80dp height, background #1C1910 (M3 surface), 4 items, icon+label, selected item in primary, unselected in onSurfaceVariant.
- Screen background: #0F0D09 (M3 background).
These elements MUST match the existing screens exactly. Do not introduce variations in height, color, alignment, or iconography.
```

If the user overrode an element, replace its line with an explicit instruction. Example for "no bottom nav":

```
- Bottom navigation: NONE for this screen (explicit override by user).
```

---

## Step 1.6: Confirm with User (only when overriding)

If any override was detected in Step 1.3, present the conventions block to the user via `AskUserQuestion` so they can confirm the deviation is intentional:

> "Existing features in this project use {summary of inherited chrome}. Your request overrides: {list of overridden elements}. Confirm or revise?"

| Option | Description |
|--------|-------------|
| Confirm override | Proceed with the override as specified |
| Inherit instead | Drop the override and inherit the existing convention |
| Revise | Provide a different override |

If user picks **Revise**, restart Step 1.3 with the new instruction.

If no override was detected, **do not ask** — silently apply the inherited conventions.

### Output

Carry forward into Step 1.10:
- `sharedConventionsBlock` — the markdown block to paste verbatim into the Stitch prompt.

Step 1.14 (empty state) does **not** need this output — its edit prompt says "Keep everything exactly the same", which automatically preserves whatever chrome the approved success screen ended up with.

---

## Step 1.7: State Coverage Selection (MANDATORY)

**Purpose**: Loading, failed, and empty are optional per feature. Capture the user's selection here so the rest of Phase 1 knows which states to design, tokenize, audit, and include in the blueprint.

Opt-in semantics:
- **Loading / Failed** — reuse the shared screens from Project Init. Opting out means no design reference; implementation falls back to generic handling.
- **Empty** — designed per-feature by editing the approved success screen. Offered **only when `isListBased == true`**.

### Ask the User

Use `AskUserQuestion` with `multiSelect: true`. The option list is dynamic — include **Empty state** only when `isListBased == true`; otherwise show only Loading and Failed.

> **"Which optional states does this feature need?"**

| Option | When shown | Description |
|--------|-----------|-------------|
| Loading state | Always | Reuse the shared loading screen |
| Failed state | Always | Reuse the shared failed screen |
| Empty state | Only if `isListBased == true` | Design a per-feature empty state (approve-or-edit loop after success) |

Record selections:

- `needsLoading = "Loading state" in selections`
- `needsFailed = "Failed state" in selections`
- **`needsEmpty = isListBased && ("Empty state" in selections)`** — the dual gate. If `isListBased == false`, `needsEmpty` is `false` regardless of user input.

### Persist Selection

Write `stitch-project.json.features[featurename].states = { loading: {needsLoading}, failed: {needsFailed}, empty: {needsEmpty} }` so the selection survives across resumes. Update `features[featurename].updatedAt` and top-level `updatedAt`.

These booleans drive Steps 1.8, 1.14, 1.15, 1.16, 1.17, 1.18, 1.19.

---

## Step 1.8: Design Missing Shared Screens (Conditional)

**Purpose**: Shared Loading/Failed screens are deferred at Project Init — they're designed lazily by the first feature that opts in. If this feature opted in (Step 1.7) and the corresponding shared screen doesn't yet exist, design it **now, before the feature's success screen**. The result lives at `_shared/designs/` and is inherited by every future feature that opts in.

If neither shared screen needs creation, this step is a **no-op**.

### Detect missing shared screens

For each state in `[loading, failed]`:

```
needs{State} == true
  AND (sharedStateScreens.{state}.screenId is null
       OR .claude/docs/_shared/designs/{state}.png is missing
       OR .claude/docs/_shared/designs/extracted/stitch_{state}.html is missing)
  → mark {state} as "missing — must design"
```

If neither state is marked missing, skip the rest of this step and proceed to Step 1.9.

### Inform the user

For each state marked missing:

> "The shared {state} screen doesn't exist in this project yet. Designing it now before your {featurename} success screen — it will be reused by every future feature that opts in to {state} state."

### Run the on-demand procedure

For each missing state, invoke the canonical procedure documented in [`phase-init.md`](phase-init.md) → **On-Demand Procedures** section:

- Loading → run **On-Demand: Generate Shared Loading Screen** end-to-end (generation, screen sync, approve-or-edit loop, HTML download + tokenize, persistence to `sharedStateScreens.loading`).
- Failed → run **On-Demand: Generate Shared Failed Screen** end-to-end.

Both procedures already write to `_shared/designs/` and `stitch-project.json.sharedStateScreens.{state}` — no additional persistence work here.

Run the procedures **sequentially** (Loading first if both are missing) so each completes before the next starts. Each can use up to 10 approve-or-edit iterations.

### Verify and proceed

After every required procedure completes, re-read `stitch-project.json` and confirm `sharedStateScreens.{state}.screenId` is non-null for each previously-missing state. Then proceed to Step 1.9.

---

## Step 1.9: Read Current Color Scheme (MANDATORY)

**Before writing any Stitch prompt**, read the XTheme file to extract all currently defined M3 roles:

```
Read: core/designsystem/src/commonMain/kotlin/thisissadeghi/designsystem/XTheme.kt
```

Parse the color scheme that matches the `defaultTheme` established in Phase 0 Step 0.1:
- `defaultTheme = light` → parse `XLightColors` / `lightColorScheme(...)`
- `defaultTheme = dark` → parse `XDarkColors` / `darkColorScheme(...)`

List every role with its hex value. For example:
```
Default theme: dark
Currently defined M3 roles (XDarkColors):
- background: #1C1B1E
- surface: #2B2930
- primary: #E8B85A
```

This is the **only** color palette you can reference as "defined" in the Stitch prompt. Any other colors needed by the design are "proposed" and will be added to **both** `XLightColors` and `XDarkColors` after approval (Phase 2 Step 2.1).

---

## Step 1.10: Generate Screens

### Model Selection

Use `modelId: GEMINI_3_FLASH` for **all** Stitch generation calls in this phase.

### Generate

**Before generating**, call `mcp__stitch__list_screens` and record the current screen IDs. This baseline is needed to identify newly created screens after generation.

For each screen, call `mcp__stitch__generate_screen_from_text` with:

```
projectId: {from stitch-project.json.projectId — the shared project, never a per-feature projectId}
prompt: {detailed screen description - see prompt engineering below}
deviceType: MOBILE
modelId: GEMINI_3_FLASH
```

### Prompt Engineering for Stitch

Write detailed, visual prompts. Include:

1. **Shared Conventions block** from Step 1.5 (if it was produced) — paste verbatim near the top of the prompt, before layout description. Skip only if Steps 1.2–1.6 were skipped (first feature).
2. **Layout structure**: "A scrollable list with a top app bar..."
3. **Component details**: "Each card has: left accent bar, title text, description text..."
4. **M3 color block** (see below): Every color annotated with its M3 role
5. **Typography**: "Bold title in light gray, muted description text..."
6. **Spacing/sizing**: "16dp padding, 8dp gaps between cards..."
7. **Interaction hints**: "Tappable cards with ripple effect..."

### Color Rules for Stitch Prompts

Follow the [Color Rules (Strict)](../references/m3-colors.md#color-rules-strict) from the Stitch reference guide. Key point: every hex value in a prompt must be annotated with an M3 role as either **defined** (already in `lightColorScheme`) or **proposed** (to be added after approval).

**Example prompt** (light theme, prior feature `receive` already approved):
```
A mobile screen using the app's M3 color scheme.

Shared conventions inherited from existing project screens (reference: receive):
- Top app bar: present, 56dp height, background #FFFFFF (M3 surface), title start-aligned in onSurface, leading back arrow icon, no trailing icons.
- Bottom navigation: present, 80dp height, background #FFFFFF (M3 surface), 4 items, icon+label, selected item in primary, unselected in onSurfaceVariant.
- Screen background: #F3F2F7 (M3 background).
These elements MUST match the existing screens exactly. Do not introduce variations in height, color, alignment, or iconography.

Defined colors (from XTheme lightColorScheme):
- Background: #F3F2F7 (M3: background)
- Card surfaces: #FFFFFF (M3: surface)
- Primary accent: #B02418 (M3: primary)

Proposed colors (to be added to XTheme after approval):
- Body text: #1C1B1E (M3: onSurface)
- Secondary text: #49454F (M3: onSurfaceVariant)
- Text on primary buttons: #FFFFFF (M3: onPrimary)
- Subtle borders: #CAC4D0 (M3: outlineVariant)

Top: App bar per Shared Conventions above, title "Products" in onSurface color.

Body: Scrollable list of product cards. Each card:
- Surface color (#FFFFFF) with 12dp corner radius
- Title in bold onSurface text
- Description in onSurfaceVariant text
- Primary-colored action icon on the right

Bottom navigation per Shared Conventions above (Products tab selected).

16dp horizontal padding, 8dp vertical spacing between cards.
```

### Screen Sync Procedure (Required After Every Stitch Generation/Edit)

Due to a known upstream bug in the Google Stitch API, newly generated or edited screens are not immediately visible to `list_screens` until the project is opened in a browser. This is a Google server-side issue, not a problem with this skill or the MCP client. See [Known Issues](../references/stitch-guide.md#known-issues-google-stitch-api) for details.

**After every `generate_screen_from_text`, `edit_screens`, or `generate_variants` call**, follow this procedure:

1. **Ask the user** to open the Stitch project in their browser:
```
The screen has been generated. Due to a known Google Stitch API limitation,
new screens are not immediately available via MCP until the project is
opened in a browser.

Please open the project in your browser and confirm once you can see the new/edited screen:
https://stitch.withgoogle.com/projects/{projectId}
```

2. **Wait for user confirmation** before calling `list_screens`.

3. Once confirmed, call `mcp__stitch__list_screens`. If the new screen still does not appear, ask the user to refresh the browser page and confirm again. Max 2 retries.

This procedure is referenced as **"Screen Sync Procedure"** throughout this phase. Apply it every time a Stitch operation creates or modifies a screen.

---

### Handle Generation Response

After each `generate_screen_from_text` call:

1. **Handle timeout / connection errors**: If the tool call times out or fails with a connection reset, **do NOT retry the generation call** — this is a known Google Stitch bug where the request usually completed server-side and retrying produces duplicate screens. Run the **Screen Sync Procedure** above immediately: ask the user to open the project in their browser, wait for confirmation, then call `list_screens` to locate the new screen. Only retry the generation if `list_screens` confirms no new screen was created after the browser sync (max 3 attempts total).

2. **Check `output_components`** in the response:
   - If contains **text**: Display it to the user
   - If contains **suggestions**: Present the suggestions to the user via `AskUserQuestion` (each suggestion as an option). If the user accepts one, call `generate_screen_from_text` again with the accepted suggestion as `prompt`. Repeat until the response contains no more suggestions.

3. **Run the Screen Sync Procedure**, then **download only new screenshots**: Call `mcp__stitch__list_screens` to get the current screen list. Compare with the screen list from before the generation call to identify only the **newly created screens**. For each new screen:
   - Call `get_screen` for the new `screenId` (see [Get Screen Call Pattern](../references/stitch-guide.md#get-screen-call-pattern)) and use `screenshot.downloadUrl` from the response
   - Download hi-res via `curl -sL "{downloadUrl}=s0" -o {path}` (always use `=s0` suffix for full resolution)
   - Save to `.claude/docs/{featurename}/designs/{featurename}_v{N}.png` (N = 1, 2, 3...)

---

## Step 1.11: Present Designs to User

Tell the user the designs are ready and list the file paths — **do not read/display images inline**:

1. List each variant path: `designs/{featurename}_v1.png`, `designs/{featurename}_v2.png`, etc.
2. Label each variant: **"Variant 1: {screen title}"**, **"Variant 2: {screen title}"**, etc.
3. **Ask user** using `AskUserQuestion`:

**If multiple variants exist** — ask **2 questions in a single call**:

**Question 1: "Which variant do you prefer?"** (max 4 options)

| Option | Description |
|--------|-------------|
| Variant 1 | {screen title from Stitch} |
| Variant 2 | {screen title from Stitch} |
| ... | (max 4 — if more variants exist, show top 4; user can type "Other") |

**If only 1 variant exists** — skip Question 1 (the choice is implicit) and only ask Question 2.

**Question 2: "What would you like to do?"**

| Option | Description |
|--------|-------------|
| Approve (Recommended) | Use the selected variant as the final design |
| Edit | Request specific changes to the selected variant |
| More variants | Generate additional design alternatives |
| Regenerate | Start over with a different prompt |

If user picks **Approve** → proceed to Step 1.13.
If user picks **Edit**, **More variants**, or **Regenerate** → proceed to Step 1.12.

---

## Step 1.12: Iterate on Feedback

### After Any Stitch Operation (shared procedure)

After every edit, variant, or regeneration call:
1. **Delete all** existing `{featurename}_v*.png` files from the designs directory
2. **Run the Screen Sync Procedure** (Step 1.10) to ensure new screens are visible
3. Call `mcp__stitch__list_screens` to get the updated screen list
4. Compare with the pre-operation screen list to identify **newly created screens**
5. **Download screenshots** — the numbering depends on the operation type (always use `=s0` suffix for hi-res):
   - **For variants**: The original screen (the one variants were generated from) is kept as `{featurename}_v1.png`. Download each new variant screen as `{featurename}_v2.png`, `{featurename}_v3.png`, etc. This gives the user the original + N variants to compare side by side.
   - **For edits/regeneration**: Download only the newly created screens as `{featurename}_v1.png`, `{featurename}_v2.png`, etc. (the original is replaced by the edit result).
6. **Notify user** that screenshots are ready, listing their file paths — **do not read/display inline** — and return to Step 1.11

### If User Requests Edits

1. **Record baseline**: Call `mcp__stitch__list_screens` to record the current screen list before editing.
2. **Edit** using `mcp__stitch__edit_screens` with:
```
projectId: {projectId}
selectedScreenIds: [{screenId of variant to edit}]
prompt: {user's edit request}
deviceType: MOBILE
modelId: GEMINI_3_FLASH
```
3. Follow the **After Any Stitch Operation** procedure above.

### If User Wants Variants

1. **Record baseline**: Call `mcp__stitch__list_screens` to record the current screen list before generating variants.
2. **Save original screen**: Before generating, download the current screen's screenshot as `{featurename}_v1.png` so it is preserved alongside the new variants.
3. **Generate** using `mcp__stitch__generate_variants` with:
```
projectId: {projectId}
selectedScreenIds: [{screenId}]
prompt: "Generate variants of the {screen description} design with different {user-specified aspects or 'layout and color'}"
deviceType: MOBILE
modelId: GEMINI_3_FLASH
variantOptions:
  variantCount: 3
  creativeRange: "EXPLORE"
  aspects: [user-specified or all]
```
4. Follow the **After Any Stitch Operation** procedure above. The original screen is already saved as `_v1.png`; new variants are numbered starting from `_v2.png`. The user sees all 4 (original + 3 variants) to compare.

### If User Wants Regeneration

Record the current screen list via `mcp__stitch__list_screens`. Call `mcp__stitch__generate_screen_from_text` with a revised prompt based on user feedback. Then follow the **After Any Stitch Operation** procedure above.

### Iteration Limit

Maximum 10 iterations per screen. If not converging, ask user to clarify requirements.

---

## Step 1.13: Finalize Approved Success Design

After user selects their preferred variant:

1. **Identify the approved screen**: Edits/variants generate new screens in Stitch. Download only the **screen corresponding to the user's selection** from Step 1.11 — this may not be the most recently generated screen.
2. **Rename** the selected `{featurename}_v{N}.png` → **`{featurename}.png`**
3. **Delete all** remaining `{featurename}_v*.png` files
4. **Write successScreenId and successScreenName to project-wide config**:
   - `stitch-project.json.features[featurename].successScreenId = {approved screenId}`
   - `stitch-project.json.features[featurename].successScreenName = "projects/{projectId}/screens/{screenId}"`
   - Update `stitch-project.json.features[featurename].updatedAt`. Write the file.
5. **Ask user to clean up Stitch**: Since Stitch MCP has no delete API, ask the user to manually remove old/unused screens from the Stitch website to keep the project clean:

```
Your design is approved! To keep the Stitch project clean, you may want to
remove the old/unused screens from the project:

1. Open the Stitch project in your browser
2. Delete any screens that are no longer needed (old variants, earlier iterations)
3. Keep only the approved screen: "{approved_screen_title}"

This is optional but recommended for clarity.
```

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

1. **Record baseline**: Call `mcp__stitch__list_screens` with `projectId` from `stitch-project.json`.
2. **Call** `mcp__stitch__edit_screens` with:
   ```
   projectId: {stitch-project.json.projectId}
   selectedScreenIds: [{approved_success_screenId}]
   prompt: "Keep everything exactly the same (toolbar, background, colors, bottom navigation, overall structure). Only replace the main content area with a centered icon or illustration indicating no items, and a message like 'No {items} yet' in muted text. Remove all list items and show only the empty state in the content area."
   deviceType: MOBILE
   modelId: GEMINI_3_FLASH
   ```
3. **Handle timeout / connection errors**: If the call times out or fails with a connection reset, **do NOT retry `edit_screens`** — this is a known Google Stitch bug where the request usually completed server-side and retrying produces duplicate screens. Run the **Screen Sync Procedure** (Step 1.10) immediately. Only retry the edit if `list_screens` confirms no new screen appeared after the browser sync (max 3 attempts total).
4. **Screen Sync Procedure**: Ask the user to open the project in their browser and confirm the new screen is visible. Wait for confirmation before calling `list_screens`. Max 2 sync attempts.
5. **Identify new screen**: Compare screen list with baseline to find the newly created screen ID. This is the working `emptyScreenId`.
6. **Download**: `curl -sL "{downloadUrl}=s0" -o .claude/docs/{featurename}/designs/{featurename}_empty.png`

#### Approve-or-Edit Loop

After each generation (initial or post-edit), tell the user the empty state screenshot is ready and give its file path — **do not read/display inline** — then ask via `AskUserQuestion`:

> **"How does the empty state design look?"**

| Option | Description |
|--------|-------------|
| Approve (Recommended) | Use this design as the final empty state |
| Edit | Request specific changes to the empty state |

**If Approve** → exit the loop. Proceed to **Persist Empty State**.

**If Edit**:
1. Use `AskUserQuestion` (free text via "Other") to capture the user's edit request, OR collect the request inline if the user already specified it.
2. Record baseline by calling `mcp__stitch__list_screens`.
3. Call `mcp__stitch__edit_screens` with:
   ```
   projectId: {stitch-project.json.projectId}
   selectedScreenIds: [{current emptyScreenId}]
   prompt: {user's edit request}
   deviceType: MOBILE
   modelId: GEMINI_3_FLASH
   ```
4. Apply the same timeout/connection-reset handling as the initial generation (Screen Sync Procedure, no blind retries).
5. Diff `list_screens` against baseline to find the new screen ID. Update the working `emptyScreenId` to this new ID.
6. Re-download as `.claude/docs/{featurename}/designs/{featurename}_empty.png` (overwrite).
7. Return to the top of the Approve-or-Edit Loop.

**Iteration limit**: Maximum 10 edit iterations for the empty state. If not converging, ask the user to clarify requirements before continuing.

#### Persist Empty State

After the user approves:

- `stitch-project.json.features[featurename].emptyScreenId = {approved emptyScreenId}`
- Update `stitch-project.json.features[featurename].updatedAt`. Write the file.

### Save Design Description

Save `.claude/docs/{featurename}/designs/{featurename}.md`. The state rows are conditional on selections:

```markdown
# {Screen Name}

**Approved**: {date}

## Design Description
{Detailed description of the approved design}

## Visual Specifications
- Colors: {M3 role → hex mappings used}
- Typography: {font styles}
- Layout: {layout structure}
- Components: {key X-components}

## Screenshots
- Success: `{featurename}.png`
{if needsLoading} - Loading: `.claude/docs/_shared/designs/loading.png` (shared)
{if needsFailed}  - Failed: `.claude/docs/_shared/designs/failed.png` (shared)
{if needsEmpty}   - Empty: `{featurename}_empty.png`
```

Only emit a screenshot line for a state if the corresponding `needs*` flag is true. Do NOT list skipped states with a "(skipped)" placeholder — omit them entirely.

---

## Step 1.15: Acquire HTML & Token Inventories (Selected States Only)

Acquire HTML + token inventory for each **selected** state. Success is always acquired; loading/failed/empty only when their `needs*` flag is true.

| State | Condition | HTML source | Tokens source |
|-------|-----------|-------------|---------------|
| success | Always | Download from `features[featurename].successScreenId` | Extract per-feature |
| loading | `needsLoading` | Read from `_shared/designs/extracted/stitch_loading.html` | Read from `_shared/designs/extracted/tokens_loading.md` |
| failed | `needsFailed` | Read from `_shared/designs/extracted/stitch_failed.html` | Read from `_shared/designs/extracted/tokens_failed.md` |
| empty | `needsEmpty` | Download from `features[featurename].emptyScreenId` | Extract per-feature |

Skipped states are not processed in any sub-step below.

### Resume Check (Partial Failure Recovery)

Before downloading or tokenizing, check `.claude/docs/{featurename}/designs/extracted/` for existing files. For each state, only run the procedure below if **either** of these is missing:

- `stitch_{state}.html`
- `tokens_{state}.md`

If both exist for a state, skip it — the prior run's snapshot is the canonical source of truth and re-fetching the HTML may fail due to Stitch's one-time URLs.

### Procedure

1. **Create extraction directory:**
   ```bash
   mkdir -p .claude/docs/{featurename}/designs/extracted
   ```

2. **For loading and failed states (only if `needsLoading` / `needsFailed`) — no action needed.** Read directly from:
   - `.claude/docs/_shared/designs/extracted/stitch_loading.html` (only if `needsLoading`)
   - `.claude/docs/_shared/designs/extracted/tokens_loading.md` (only if `needsLoading`)
   - `.claude/docs/_shared/designs/extracted/stitch_failed.html` (only if `needsFailed`)
   - `.claude/docs/_shared/designs/extracted/tokens_failed.md` (only if `needsFailed`)

3. **For success and empty states — download from Stitch.** **Sequentially** (concurrent downloads can race the URL's single-use semantics). Success is always downloaded. Empty is downloaded **only if `needsEmpty == true`**:
   - screenId source:
     - success → `stitch-project.json.features[featurename].successScreenId`
     - empty → `stitch-project.json.features[featurename].emptyScreenId` (skip this entire line if `needsEmpty == false`)
   - Call `get_screen` for each screenId (see [Get Screen Call Pattern](../references/stitch-guide.md#get-screen-call-pattern)) and use `htmlCode.downloadUrl`, `width`, `height` from the response.
     ```
     projectId = stitch-project.json.projectId   ← always the shared project
     name      = "projects/{projectId}/screens/{screenId}"
     screenId  = {screenId}
     ```
   - Download: `curl -sL -o .claude/docs/{featurename}/designs/extracted/stitch_{state}.html {htmlCode.downloadUrl}`
   - Verify with `wc -c …` — if 0 bytes, call `mcp__stitch__get_screen` again to get a fresh URL and retry the curl once.
   - Record the screen dimensions (`width`, `height`) from the `get_screen` response.

4. **Tokenize success (always) and empty (only if `needsEmpty == true`)** with the shared extractor:
   ```bash
   python3 .claude/skills/_shared/extract_tokens.py \
     .claude/docs/{featurename}/designs/extracted/stitch_{state}.html \
     > .claude/docs/{featurename}/designs/extracted/tokens_{state}.md
   ```
   These inventories are the canonical, deterministic Tailwind→Compose conversion (spacing, font-size, colors with opacity, custom border-radius config, arbitrary values). Same script `/verify-ui` runs at audit time, so blueprint values and audit values come from the same source by construction.

   Do NOT re-tokenize loading/failed — those were already tokenized at Project Init time and live in `_shared/`.

5. **Do not delete.** The HTML and token inventories live in `extracted/` from now on. `/verify-ui` will detect and reuse them — see [verify-ui Step 2](../../verify-ui/SKILL.md) for the reuse contract.

---

## Step 1.16: Color Audit (MANDATORY)

Audit every color used across the **selected** approved designs and map them to M3 roles. Color values are read from the **token inventories produced in Step 1.15**, not from prompts — Stitch can generate hex values that drift from what the prompt asked for, and the inventory is what `/verify-ui` will see.

Only audit token inventories for **selected** states (`needsLoading`/`needsFailed`/`needsEmpty`). Skipped states contribute zero colors. Success is always audited.

For any color found in `tokens_loading.md` or `tokens_failed.md` (only if those states are selected), annotate it with "(shared state screen)" in the audit output to make clear it originates from the shared Project Init screens.

### Procedure

1. **Re-read `XTheme.kt`** to get the current roles from the active scheme (`XLightColors` or `XDarkColors` per `defaultTheme`).

2. **Collect every color from the inventories** in `.claude/docs/{featurename}/designs/extracted/tokens_*.md` for the **selected** states only (always success; loading/failed/empty per their flags — loading/failed inventories live under `.claude/docs/_shared/designs/extracted/`). The extractor resolves each color class to its hex (custom Tailwind config + default palette + arbitrary values), so iterate through every inventory entry whose conversion contains a color.

3. **Reconcile against prompts.** Compare the inventory hexes against the "Defined" / "Proposed" hexes you specified in the Stitch prompts (Steps 1.10 and 1.14). If a color drifted (e.g., prompt asked for `#181228`, Stitch produced `#1A1A1F`), **the inventory wins** — record the inventory hex. Flag any drift in a single line at the top of the Color Audit so it's visible to the user.

4. **Component visual properties from the inventories.** For every component in the design, extract two things and flag any divergence in a "Component Overrides" section:

   - **Colors per visual state**: For each visual state of each component, look up the M3 role in `XTheme.kt`. If the role's hex matches the inventory hex → use the role. If it diverges → record an explicit color override.
   - **Sizing**: For each component, read the dp values from the inventory and compare to the X-component's actual rendered default in [`X_COMPONENTS_CATALOG.md`](../../_shared/X_COMPONENTS_CATALOG.md). If they differ → record an explicit size override.

5. **Map each color to an M3 role** using the [Complete M3 Role Catalog](../references/m3-colors.md#complete-m3-role-catalog).

6. **Classify each role**:
   - **Defined**: Already in the active scheme in `XTheme.kt`
   - **Missing**: Used in the design but not yet defined — must be added to **both** `XLightColors` and `XDarkColors` before implementation (Phase 2 Step 2.1 handles this)
   - **Custom**: Cannot map to any M3 role (gradients, decorative) — will use `XTheme.Colors.*` extension (must justify)

### Color Audit Output

Append to the design description file (`.claude/docs/{featurename}/designs/{featurename}.md`):

```markdown
## Color Audit

Default theme for design: {light|dark}

> **Prompt drift**: {N} colors differ from the Stitch prompt — inventory values used. (Omit this line if N = 0.)

### Defined M3 Roles (already in active scheme — XLightColors or XDarkColors)
| Role | Hex (inventory) | Usage in Design |
|------|-----------------|-----------------|
| {role} | {hex} | {usage} |

### Missing M3 Roles (must add to BOTH XLightColors and XDarkColors before implementation)
| Role | Active Scheme Hex | Counterpart Scheme Hex | Usage in Design |
|------|-------------------|----------------------|-----------------|
| {role} | {hex for defaultTheme} | {derived counterpart hex} | {usage} |

### Custom Colors (XTheme.Colors.* — justified exceptions only)
| Name | Hex | Justification |
|------|-----|---------------|
| (none expected — only if needed) | | |
```

This audit is the input for Phase 2, where missing roles are added to **both** `XLightColors` and `XDarkColors` in `XTheme.kt` before any feature code is written.

---

## Step 1.17: Generate Implementation Blueprint

**Condition**: Always runs after design approval.

This step parses the Stitch HTML exports (downloaded/read in Step 1.15) into a structured Compose Implementation Blueprint that provides exact component trees, design tokens, typography, and spacing for implementation.

### Procedure

1. **Read the persisted inputs** for **selected states only**:
   - Success — `.claude/docs/{featurename}/designs/extracted/stitch_success.html` + `tokens_success.md` (always)
   - Loading — `.claude/docs/_shared/designs/extracted/stitch_loading.html` + `tokens_loading.md` (only if `needsLoading`)
   - Failed — `.claude/docs/_shared/designs/extracted/stitch_failed.html` + `tokens_failed.md` (only if `needsFailed`)
   - Empty — `.claude/docs/{featurename}/designs/extracted/stitch_empty.html` + `tokens_empty.md` (only if `needsEmpty`)

2. **Generate the blueprint**: Feed the selected state HTML files **together with their token inventories** to Claude using the extraction prompt template from [blueprint-spec.md](../references/blueprint-spec.md#extraction-prompt-template). The inputs are:
   - HTML file contents for selected states (labeled by state)
   - Token inventories for selected states (labeled by state) — authoritative for already-converted classes
   - The X-component mapping table (from [stitch-guide.md](../references/stitch-guide.md#mapping-stitch-designs-to-kmp-x-components))
   - The Color Audit M3 role mappings (from Step 1.16 output in `.claude/docs/{featurename}/designs/{featurename}.md`)
   - The `needsLoading`, `needsFailed`, `needsEmpty` flags so the prompt knows which sections to emit

3. **Save the blueprint** to `.claude/docs/{featurename}/designs/{featurename}_blueprint.md`. The blueprint covers **only selected states**; shared scaffold is described once. Use the canonical Component-Tree entries from [blueprint-spec.md → Component Tree](../references/blueprint-spec.md#component-tree):
   - `states.loading` true → shared-screen entry; false → "Skipped" marker.
   - `states.failed` true → shared-screen entry; false → "Skipped" marker.
   - `states.empty` true → emit the section; false → omit the section entirely (no "Skipped" placeholder — empty is a content variant, not a Rule-4 UI state).

4. **Verify** the blueprint file was written and contains the expected sections (Design Tokens, Typography Scale, Spacing Grid, Component Tree with selected states, Pre-Implementation Contract with Component Overrides table, Post-Implementation Checklist).

---

## Step 1.18: Update stitch-project.json

Update `.claude/docs/_project/stitch-project.json` after design approval:

- `features[{featurename}]`:
  - `successScreenId`: captured in Step 1.13
  - `successScreenName`: `"projects/{projectId}/screens/{successScreenId}"`
  - `screenshot`: `"designs/{featurename}.png"`
  - `htmlPath`: `"designs/extracted/stitch_success.html"`
  - `tokensPath`: `"designs/extracted/tokens_success.md"`
  - `dimensions`: width/height from Step 1.15
  - `designFile`: `"designs/{featurename}.md"`
  - `blueprintFile`: `"designs/{featurename}_blueprint.md"`
  - `emptyScreenId`: the approved empty screen ID **if `needsEmpty == true`**, otherwise leave `null` (the `states` map was already written in Step 1.7 and does not change here)
  - **`"blueprintConsumed": false`** — signals to implementation skills that a new blueprint is available
  - `approved`: `true`
  - `approvedAt`: current ISO date
  - `updatedAt`: current ISO timestamp
- Update top-level `updatedAt`.

---

## Step 1.19: User Final Approval

Present all approved designs. Only emit table rows for **selected** states:

```
## UI Designer Complete: {FeatureName}

Stitch Project ID: {projectId} (shared project)
Design System ID: {designSystemAssetId}

| State | Screenshot |
|-------|------------|
| Success | designs/{featurename}.png |
{if needsLoading} | Loading | .claude/docs/_shared/designs/loading.png (shared) |
{if needsFailed}  | Failed  | .claude/docs/_shared/designs/failed.png (shared) |
{if needsEmpty}   | Empty   | designs/{featurename}_empty.png |

States selected: success{, loading if needsLoading}{, failed if needsFailed}{, empty if needsEmpty}
States skipped (no design reference): {comma-separated skipped states, or "none"}

Design spec: designs/{featurename}.md
Blueprint: designs/{featurename}_blueprint.md
Project config: .claude/docs/_project/stitch-project.json
blueprintConsumed: false (set in stitch-project.json.features[{featurename}])
```

Show completion report from SKILL.md and stop. The user controls the next step — they can invoke an implementation skill if they want to proceed with code.

---

## Output

After Phase 1 completes:
- Success screenshot: `.claude/docs/{featurename}/designs/{featurename}.png` (always)
- Loading screenshot: `.claude/docs/_shared/designs/loading.png` — **only if `needsLoading`**
- Failed screenshot: `.claude/docs/_shared/designs/failed.png` — **only if `needsFailed`**
- Empty screenshot: `.claude/docs/{featurename}/designs/{featurename}_empty.png` — **only if `needsEmpty`**
- Design description: `.claude/docs/{featurename}/designs/{featurename}.md` (with state rows for selected states only)
- Implementation blueprint: `.claude/docs/{featurename}/designs/{featurename}_blueprint.md` (selected states only; skipped state sections marked "Skipped" for loading/failed, omitted for empty)
- Persisted HTML + token inventories for selected states: `.claude/docs/{featurename}/designs/extracted/stitch_{state}.html` and `tokens_{state}.md` (consumed by `/verify-ui`). Loading/failed inventories continue to live in `.claude/docs/_shared/`.
- stitch-project.json updated with approved screen, per-feature `states` selection map, screenshots for selected states, and `blueprintConsumed: false`
- All variant screenshots cleaned up
- User approval received

Show completion report from SKILL.md. Done.
