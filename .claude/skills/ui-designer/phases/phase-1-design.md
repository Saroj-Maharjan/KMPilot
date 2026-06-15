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
- [ ] Step 1.15: Acquire HTML & Token Inventories for selected states (MANDATORY) — includes Material Symbols icons manifest (sub-step 5), `<img>` assets manifest (sub-step 6), and font manifest (sub-step 6b); all manifest-only — no XML/image/font downloads here
- [ ] Step 1.16: Color, Typography & Motion Audit — reconciled against HTML inventories (MANDATORY)
- [ ] Step 1.17: Generate Implementation Blueprint
- [ ] Step 1.18: Update stitch-project.json
- [ ] Step 1.19: User final approval
```

> **Staged files (token efficiency — read each when you reach it, do NOT preload):** Steps 1.1–1.13 live in this file. Step 1.14 state designs → [phase-1-states.md](phase-1-states.md) (only when an optional state was selected in Step 1.7). "Save Design Description" + Steps 1.15–1.19 → [phase-1-finalize.md](phase-1-finalize.md) (always).

> **Resume fast-path (StitchMode: stitch-resume):** if Phase 0 loaded a **non-null `successScreenId`** with `approved == false`, Steps 1.1–1.13 are already complete — the success design was user-approved and finalized in a prior session. Do **not** re-ask requirements or regenerate the success screen. Re-derive the state selections from the persisted `features[{featurename}].states` map (Step 1.7 already wrote it), then continue at Step 1.14: read [phase-1-states.md](phase-1-states.md) if any state is `true`, else go straight to [phase-1-finalize.md](phase-1-finalize.md). When writing "Save Design Description" in a resumed session, derive the description from the approved screenshot (`designs/{featurename}.png`) and the extracted HTML once Step 1.15 has run.

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
4. **Ask user (Vague-Requirements Template)**: see below

For the screen, capture:
- **Screen name** (e.g., "Main Screen", "Detail Screen", "Settings Screen")
- **Key elements** (buttons, lists, cards, forms, navigation)
- **States** (loading, success, empty, error - per the 4-state pattern)
- **Visual style notes** (dark theme, accent colors, typography preferences)

#### Vague-Requirements Template

**When to use**: No spec exists, no PRD exists, **and** the user's prompt doesn't already convey both purpose and primary content shape. Examples that should trigger this template: "design a dashboard", "make a settings screen". Examples that should NOT trigger it: "design a settings screen with toggles for notifications and dark mode" (already specific enough).

Issue a **single `AskUserQuestion` call with 3 questions**:

| # | Question text | Header | Type | Options |
|---|--------------|--------|------|---------|
| 1 | "What does the user do on this screen?" | Purpose | Free text (single-select with one "Describe…" option; users use Other) | "Describe the primary action" |
| 2 | "What's the primary content shape?" | Content | Single-select | List/feed of items · Form with inputs · Detail/info view · Dashboard with mixed sections |
| 3 | "Any specific elements or actions to highlight? (e.g., search at top, FAB for new item) — leave blank if none" | Elements | Free text via Other | "Specify…" · "None — use defaults" |

After the user answers:
- Use Q1 → screen purpose for the Stitch prompt's opening line.
- Use Q2 → derives `isListBased` (List/feed → `true`; the other three → `false`). **Skip the standalone List-Based Determination question below** unless Q2 was answered with "Other" and the answer is ambiguous.
- Use Q3 → adds 1-3 must-have UI elements to the Stitch prompt; otherwise fall back to chrome inheritance (Step 1.2) + content-shape defaults.

Do **not** ask a visual-style question here — chrome inheritance covers non-first features, and the first feature's style emerges through the Step 1.11–1.12 approve/edit loop.

### List-Based Determination (`isListBased`)

A screen is **list-based** when its primary content is a collection of homogeneous items (`LazyColumn`, `LazyRow`, `LazyVerticalGrid`, `LazyVerticalStaggeredGrid`). This drives whether the Empty option is offered in Step 1.7 — an empty state only makes sense when there is a list that can be empty.

- **List-based**: feed, search results, message list, transactions, notifications, gallery — the success state is "many of the same thing".
- **Not list-based**: forms (login, settings, profile edit), detail views (product detail, transaction detail), modal/wizard steps, dashboards with fixed sections (a sub-list inside a dashboard does **not** count — design empty separately if needed).

If the determination is unambiguous from requirements (or already answered by Q2 of the Vague-Requirements Template above), set `isListBased` directly. If it isn't, ask:

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

**Read chrome from ALL approved features**, not just one. For each approved feature, read its token inventory at `.claude/docs/{feature}/designs/extracted/tokens_success.md` (and HTML header/footer regions if needed).

Extract the following per feature:

| Element | What to capture |
|---------|-----------------|
| **Top app bar** | Presence (yes/no), height (dp), background color (hex + M3 role), title alignment (start/center), title typography (size/weight), leading icon (back arrow / menu / none), trailing icons (count + style) |
| **Bottom navigation** | Presence (yes/no). If present: height (dp), background color (hex + M3 role), item count, item style (icon-only / icon+label), selected/unselected color treatment. **CRITICAL**: A multi-tab nav bar (≥2 persistent tabs) is **app-shell chrome**, NOT per-screen inherited chrome — record its tokens for Point 5 wiring but do NOT include it in the Shared Conventions block (Step 1.5). A single sticky CTA button is NOT a tab nav bar. |
| **Screen background** | Color (hex + M3 role) or surface treatment |

### Pattern Divergence Detection (MANDATORY before picking a reference)

After extracting chrome from all features, compare them. Classify any divergence as either **structural** or **cosmetic**:

- **Structural divergence** — the leading icon differs (back arrow vs. none), or the bar is present in some features and absent in others, or the title area has a fundamentally different layout (single title line vs. greeting + title). This signals that the project has multiple chrome *archetypes* (e.g., hub screens vs. detail screens).
- **Cosmetic divergence** — same structure but minor token differences (slightly different padding, font weight). Resolve by using the most recently approved feature's value (max `approvedAt`); no user question needed.

**If structural divergence is detected** — do NOT auto-select a reference. Ask the user via `AskUserQuestion`:

> "Existing screens use different chrome patterns. Which should '{featurename}' follow?"

Options: one option per distinct structural pattern found, described concisely by its chrome shape and the `{featurename}` that exemplifies it (e.g., "Hub screen, like {featureA} — no back arrow, greeting-style header" / "Detail screen, like {featureB} — back arrow, transparent bar"). Max 4 options plus "Other (specify)".

Use the user's chosen pattern as the reference for Step 1.5. Record which feature exemplifies that pattern as `{reference_feature}`.

**If the user picks "Other (specify)"** — their free-text describes a chrome that matches no existing archetype. Treat it as an **explicit chrome override** and route it into the override flow:
- Parse the free-text for top-app-bar / bottom-nav / background instructions and record the affected element(s) as overrides exactly as Step 1.3 does (they are excluded from the Shared Conventions block in Step 1.5).
- Still set `{reference_feature}` to the most recently approved feature (max `approvedAt`) so any **non-overridden** element inherits as usual.
- This override is carried into Step 1.6, which confirms the deviation before generating.

**If no structural divergence** — pick the most recently approved feature (max `approvedAt`) and record it as `{reference_feature}`. No user question needed.

---

## Step 1.5: Build the Shared Conventions Block

Produce a `Shared Conventions` markdown block to inject into the Stitch prompt in Step 1.10. Only include elements that are **not** explicitly overridden by the user.

Example:

```
Shared conventions inherited from existing project screens (reference: {reference_feature}):
- Top app bar: present, 56dp height, background #1C1910 (M3 surface), title start-aligned in onSurface, leading back arrow icon, no trailing icons.
- Screen background: #0F0D09 (M3 background).
These elements MUST match the existing screens exactly. Do not introduce variations in height, color, alignment, or iconography.
```

> **Tab nav bar is NOT a per-screen convention.** Never include a multi-tab bottom navigation bar in the Shared Conventions block. Its presence signals a top-level tab feature (Integration Point 5, app-shell) — that is captured in the PRD, not here. Only per-screen sticky CTAs belong in the conventions block.

If the user overrode an element, replace its line with an explicit instruction. Example for "no top bar":

```
- Top app bar: NONE for this screen (explicit override by user).
```

---

## Step 1.6: Confirm with User (only when overriding)

If any override was detected in Step 1.3 — **or routed from a Step 1.4 "Other (specify)" chrome pattern** — present the conventions block to the user via `AskUserQuestion` so they can confirm the deviation is intentional:

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
Read the file at: stitch-project.json.designSystem.xthemePath
(the discovered path to XTheme.kt; resolved once at Init-2)
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

## Next: Steps 1.14–1.19 (staged files — read when you reach this point, not before)

Step 1.13 complete. Continue:

1. **If any optional state was selected in Step 1.7** (`needsLoading` / `needsFailed` / `needsEmpty`) → Read [phase-1-states.md](phase-1-states.md) and run Step 1.14's state designs now.
2. **Then — always** → Read [phase-1-finalize.md](phase-1-finalize.md) and run "Save Design Description" (the always-run closer of Step 1.14) plus Steps 1.15–1.19. When no optional state was selected, skip phase-1-states.md entirely and go straight here.

> **Context checkpoint (optional):** everything needed from here on is persisted — `stitch-project.json` (`successScreenId`, `states`, `emptyScreenId`) and the screenshots under `designs/`. If this session is context-heavy (many edit iterations, large designs), offer the user: run `/clear`, then re-invoke `/ui-designer {featurename}` — Phase 0 resumes (`StitchMode: stitch-resume`) and the **Resume fast-path** at the top of this file continues at Step 1.14 losslessly, in a fresh context window.
