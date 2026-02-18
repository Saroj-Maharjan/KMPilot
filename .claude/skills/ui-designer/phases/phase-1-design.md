# Phase 1: Design in Stitch

**Purpose**: Generate UI screens in Google Stitch, iterate with user feedback, and export approved designs.

**Prerequisites**: Phase 0 complete. Stitch project ID and feature context available.

---

## Checklist

```
Design Progress:
- [ ] Step 1.1: Gather screen requirements
- [ ] Step 1.2a: Read current XTheme color scheme (MANDATORY)
- [ ] Step 1.2b: Generate screens in Stitch
- [ ] Step 1.3: Present designs to user
- [ ] Step 1.4: Iterate on feedback (if needed)
- [ ] Step 1.5: Finalize approved success design
- [ ] Step 1.6: Generate state designs (loading, failed, empty)
- [ ] Step 1.6.5: Color Audit — map all design colors to M3 roles (MANDATORY)
- [ ] Step 1.6.6: Generate Implementation Blueprint (Modes 2 & 3)
- [ ] Step 1.7: Update stitch.json
- [ ] Step 1.8: User final approval
```

---

## Step 1.1: Gather Screen Requirements

> **Scope**: This skill handles **one screen per invocation**. For multi-screen features, invoke `/ui-designer` once per screen.

Determine what the screen needs to show. Sources (in priority order):

1. **User's prompt**: "design a login screen with email/password fields"
2. **Existing spec**: Load `.claude/docs/{featurename}/spec.md` if available (Section 4.5 UI Design)
3. **Existing PRD**: Load `.claude/docs/{featurename}/prd.txt` if available
4. **Ask user**: If requirements are unclear, use `AskUserQuestion`

For the screen, capture:
- **Screen name** (e.g., "Main Screen", "Detail Screen", "Settings Screen")
- **Key elements** (buttons, lists, cards, forms, navigation)
- **States** (loading, success, empty, error - per the 4-state pattern)
- **Visual style notes** (dark theme, accent colors, typography preferences)

### Screen State Coverage (Mandatory)

Every screen **must** have designs for these states:

| State | Required | Description |
|-------|----------|-------------|
| **Success** | Always | Main content — designed first, user picks from variants |
| **Loading** | Always | Progress indicator while data loads |
| **Failed** | Always | Error message + retry action |
| **Empty** | List screens only | Empty state when no items exist |

Steps 1.2–1.5 handle the **success state** variant selection. Step 1.6 generates the remaining state designs based on the approved success design.

---

## Step 1.2: Generate Screens and Download Screenshots

### Step 1.2a: Read Current Color Scheme (MANDATORY)

**Before writing any Stitch prompt**, read the XTheme file to extract all currently defined M3 roles:

```
Read: core/designsystem/src/commonMain/kotlin/thisissadeghi/designsystem/XTheme.kt
```

Parse the `lightColorScheme(...)` call and list every role with its hex value. For example:
```
Currently defined M3 roles:
- background: #F3F2F7
- surface: #FFFFFF
- primary: #B02418
```

This is the **only** color palette you can reference as "defined" in the Stitch prompt. Any other colors needed by the design are "proposed" and will be added to `lightColorScheme` after approval.

### Model Selection

Use the `modelId` resolved in Phase 0 (stored in stitch.json) for **all** Stitch generation calls in this phase.

### Step 1.2b: Generate Screens

**Before generating**, call `mcp__stitch__list_screens` and record the current screen IDs. This baseline is needed to identify newly created screens after generation.

For each screen, call `mcp__stitch__generate_screen_from_text` with:

```
projectId: {from Phase 0}
prompt: {detailed screen description - see prompt engineering below}
deviceType: MOBILE
modelId: {from stitch.json}
```

### Prompt Engineering for Stitch

Write detailed, visual prompts. Include:

1. **Layout structure**: "A scrollable list with a top app bar..."
2. **Component details**: "Each card has: left accent bar, title text, description text..."
3. **M3 color block** (see below): Every color annotated with its M3 role
4. **Typography**: "Bold title in light gray, muted description text..."
5. **Spacing/sizing**: "16dp padding, 8dp gaps between cards..."
6. **Interaction hints**: "Tappable cards with ripple effect..."

### Color Rules for Stitch Prompts

Follow the [Color Rules (Strict)](../references/m3-colors.md#color-rules-strict) from the Stitch reference guide. Key point: every hex value in a prompt must be annotated with an M3 role as either **defined** (already in `lightColorScheme`) or **proposed** (to be added after approval).

**Example prompt**:
```
A mobile screen using the app's M3 color scheme.

Defined colors (from XTheme lightColorScheme):
- Background: #F3F2F7 (M3: background)
- Card surfaces: #FFFFFF (M3: surface)
- Primary accent: #B02418 (M3: primary)

Proposed colors (to be added to XTheme after approval):
- Body text: #1C1B1E (M3: onSurface)
- Secondary text: #49454F (M3: onSurfaceVariant)
- Text on primary buttons: #FFFFFF (M3: onPrimary)
- Subtle borders: #CAC4D0 (M3: outlineVariant)

Top: App bar with background color, centered title "Products" in onSurface color, back arrow in onSurface.

Body: Scrollable list of product cards. Each card:
- Surface color (#FFFFFF) with 12dp corner radius
- Title in bold onSurface text
- Description in onSurfaceVariant text
- Primary-colored action icon on the right

Bottom: Primary-colored button with onPrimary text.

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

1. **Handle connection errors**: If the tool call fails with a connection error, the generation may still be in progress. Wait ~30 seconds, then run the **Screen Sync Procedure** above. Only retry the generation (max 3 attempts) if the screen was NOT created.

2. **Check `output_components`** in the response:
   - If contains **text**: Display it to the user
   - If contains **suggestions**: Present the suggestions to the user via `AskUserQuestion` (each suggestion as an option). If the user accepts one, call `generate_screen_from_text` again with the accepted suggestion as `prompt`. Repeat until the response contains no more suggestions.

3. **Run the Screen Sync Procedure**, then **download only new screenshots**: Call `mcp__stitch__list_screens` to get the current screen list. Compare with the screen list from before the generation call to identify only the **newly created screens**. For each new screen:
   - Call `mcp__stitch__get_screen` with all 3 required params: `name: "projects/{projectId}/screens/{screenId}"`, `projectId: "{projectId}"`, `screenId: "{screenId}"` — use `screenshot.downloadUrl` from the response
   - Download via `curl -sL -o {path} {downloadUrl}`
   - Save to `.claude/docs/{featurename}/designs/{featurename}_v{N}.png` (N = 1, 2, 3...)

---

## Step 1.3: Present Designs to User

Show **all variant screenshots inline** so the user can compare without leaving Claude Code:

1. For each downloaded screenshot, use `Read` on the `.png` file to display it inline
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

If user picks **Approve** → proceed to Step 1.5.
If user picks **Edit**, **More variants**, or **Regenerate** → proceed to Step 1.4.

---

## Step 1.4: Iterate on Feedback

### After Any Stitch Operation (shared procedure)

After every edit, variant, or regeneration call:
1. **Delete all** existing `{featurename}_v*.png` files from the designs directory
2. **Run the Screen Sync Procedure** (Step 1.2b) to ensure new screens are visible
3. Call `mcp__stitch__list_screens` to get the updated screen list
4. Compare with the pre-operation screen list to identify **newly created screens**
5. **Download screenshots** — the numbering depends on the operation type:
   - **For variants**: The original screen (the one variants were generated from) is kept as `{featurename}_v1.png`. Download each new variant screen as `{featurename}_v2.png`, `{featurename}_v3.png`, etc. This gives the user the original + N variants to compare side by side.
   - **For edits/regeneration**: Download only the newly created screens as `{featurename}_v1.png`, `{featurename}_v2.png`, etc. (the original is replaced by the edit result).
6. **Display all inline** via `Read` and return to Step 1.3

### If User Requests Edits

1. **Record baseline**: Call `mcp__stitch__list_screens` to record the current screen list before editing.
2. **Edit** using `mcp__stitch__edit_screens` with:
```
projectId: {projectId}
selectedScreenIds: [{screenId of variant to edit}]
prompt: {user's edit request}
deviceType: MOBILE
modelId: {from stitch.json}
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
modelId: {from stitch.json}
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

## Step 1.5: Finalize Approved Success Design

After user selects their preferred variant:

1. **Identify the approved screen**: Edits/variants generate new screens in Stitch. Download only the **screen corresponding to the user's selection** from Step 1.3 — this may not be the most recently generated screen.
2. **Rename** the selected `{featurename}_v{N}.png` → **`{featurename}.png`**
3. **Delete all** remaining `{featurename}_v*.png` files
4. **Ask user to clean up Stitch**: Since Stitch MCP has no delete API, ask the user to manually remove old/unused screens from the Stitch website to keep the project clean:

```
Your design is approved! To keep the Stitch project clean, you may want to
remove the old/unused screens from the project:

1. Open the Stitch project in your browser
2. Delete any screens that are no longer needed (old variants, earlier iterations)
3. Keep only the approved screen: "{approved_screen_title}"

This is optional but recommended for clarity.
```

---

## Step 1.6: Generate State Designs

Generate the remaining states by **editing the approved success screen**. This uses `mcp__stitch__edit_screens` on the success screen so the entire structure is preserved (colors, toolbar, bottom navigation, spacing, etc.) — only the content area changes for each state.

**Important**: Each `edit_screens` call generates a new screen. The original success screen remains unchanged.

### State Generation Procedure

For each state below, follow this procedure:

1. **Record baseline**: Call `mcp__stitch__list_screens` before the edit call
2. **Call** `mcp__stitch__edit_screens` with the state-specific prompt (see table below)
3. **Handle errors**: If the call fails with a connection error, wait ~30 seconds and run the **Screen Sync Procedure** (Step 1.2b). Retry max 3 times only if no new screen appeared.
4. **Run the Screen Sync Procedure** (Step 1.2b) to ensure the new screen is visible
5. **Identify new screen**: Compare screen list with baseline to find the newly created screen
6. **Download**: Save screenshot as `{featurename}_{state}.png`
7. **Record**: Store the screen ID as `{state}ScreenId`

Common params for all state edits:
```
projectId: {projectId}
selectedScreenIds: [{approved_success_screenId}]
deviceType: MOBILE
modelId: {from stitch.json}
```

### State-Specific Prompts

| State | Filename Suffix | Prompt | Condition |
|-------|----------------|--------|-----------|
| **Loading** | `_loading` | "Keep everything exactly the same (toolbar, background, colors, bottom navigation, overall structure). Only replace the main content area with a centered circular progress indicator using the same accent color. Remove all list items / detail content and show only the loading indicator in the content area." | Always |
| **Failed** | `_failed` | "Keep everything exactly the same (toolbar, background, colors, bottom navigation, overall structure). Only replace the main content area with a centered error icon, an error message in muted text, and a 'Retry' button using the same accent color. Remove all list items / detail content and show only the error state in the content area." | Always |
| **Empty** | `_empty` | "Keep everything exactly the same (toolbar, background, colors, bottom navigation, overall structure). Only replace the main content area with a centered icon or illustration indicating no items, and a message like 'No {items} yet' in muted text. Remove all list items and show only the empty state in the content area." | List screens only |

### Present State Designs

Display all state screenshots inline via `Read` and ask user for approval:

| Option | Description |
|--------|-------------|
| Approve all | All state designs look good |
| Edit | Request changes to specific states |

If user requests edits, use `mcp__stitch__edit_screens` on the specific state screen, re-download, and present again.

### Save Design Description

Save `.claude/docs/{featurename}/designs/{featurename}.md`:

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
- Loading: `{featurename}_loading.png`
- Failed: `{featurename}_failed.png`
- Empty: `{featurename}_empty.png` (list screens only)
```

---

## Step 1.6.5: Color Audit (MANDATORY)

After all state designs are approved (success + loading + failed + empty), audit every color used across all designs and map them to M3 roles.

### Procedure

1. **Re-read `XTheme.kt`** to get the current `lightColorScheme` roles
2. **Collect all colors from Stitch prompts**: Gather every color specified in the "Defined colors" and "Proposed colors" blocks from all Stitch prompts used during this phase (Steps 1.2b and 1.6). These prompts are the authoritative source of color values — do NOT attempt to extract hex values from screenshots via vision, as that is imprecise.
3. **Map each color to an M3 role** using the [Complete M3 Role Catalog](../references/m3-colors.md#complete-m3-role-catalog)
4. **Classify each role**:
   - **Defined**: Already in `lightColorScheme` in `XTheme.kt`
   - **Missing**: Used in the design but not yet in `lightColorScheme` — must be added before implementation
   - **Custom**: Cannot map to any M3 role (gradients, decorative) — will use `XTheme.Colors.*` extension (must justify)

### Color Audit Output

Append to the design description file (`.claude/docs/{featurename}/designs/{featurename}.md`):

```markdown
## Color Audit

### Defined M3 Roles (already in lightColorScheme)
| Role | Hex | Usage in Design |
|------|-----|-----------------|
| background | #F3F2F7 | Screen background |
| surface | #FFFFFF | Card backgrounds |
| primary | #B02418 | Action buttons, active indicators |

### Missing M3 Roles (must add to lightColorScheme before implementation)
| Role | Hex | Usage in Design |
|------|-----|-----------------|
| onSurface | #1C1B1E | Primary body text, titles |
| onSurfaceVariant | #49454F | Secondary text, captions |
| onPrimary | #FFFFFF | Text on primary buttons |
| error | #BA1A1A | Error icon and retry button in failed state |
| outline | #79747E | Input field borders |

### Custom Colors (XTheme.Colors.* — justified exceptions only)
| Name | Hex | Justification |
|------|-----|---------------|
| (none expected — only if needed) | | |
```

This audit is the input for Phase 2, where missing roles are added to `XTheme.kt` before any feature code is written.

---

## Step 1.6.6: Generate Implementation Blueprint

**Condition**: Only runs when **mode is 2 or 3**. Skip for Mode 1.

This step parses the Stitch HTML export into a structured Compose Implementation Blueprint that provides exact component trees, design tokens, typography, and spacing for implementation. The HTML is transient — downloaded, parsed, deleted.

### Procedure

1. **Download HTML for each approved screen state** (success + loading + failed + empty if applicable):
   a. Call `mcp__stitch__get_screen` with all 3 required params to get `htmlCode.downloadUrl`
   b. Download: `curl -sL -o /tmp/stitch_{featurename}_{state}.html {htmlCode.downloadUrl}`
   c. Read the downloaded HTML file content

2. **Generate the blueprint**: Feed ALL state HTML files together with context to Claude using the extraction prompt template from [blueprint-spec.md](../references/blueprint-spec.md#extraction-prompt-template). The inputs are:
   - All downloaded HTML file contents (labeled by state: success, loading, failed, empty)
   - The X-component mapping table (from [stitch-guide.md](../references/stitch-guide.md#mapping-stitch-designs-to-kmp-x-components))
   - The Color Audit M3 role mappings (from Step 1.6.5 output in `.claude/docs/{featurename}/designs/{featurename}.md`)

3. **Save the blueprint** to `.claude/docs/{featurename}/designs/{featurename}_blueprint.md`
   - The blueprint covers all states in a single file
   - Shared scaffold (toolbar, background, bottom nav) is described once
   - Per-state content sections capture only the differences

4. **Clean up temp files**: Delete all `/tmp/stitch_{featurename}_*.html` files

5. **Verify** the blueprint file was written and contains all expected sections (Design Tokens, Typography Scale, Spacing Grid, Component Tree with all states)

---

## Step 1.7: Update stitch.json

Update `.claude/docs/{featurename}/stitch.json` following the [stitch.json schema](../references/stitch-guide.md#stitchjson-schema).

Record the approved screen with all state screenshot paths. Include `screenId`, `screenName`, `description`, `designFile`, `screenshot` (success), `stateScreenshots` (loading, failed, empty), `stateScreenIds` (loadingScreenId, failedScreenId, emptyScreenId — recorded in Step 1.6), `approved: true`, and `approvedAt` date.

If Step 1.6.6 generated a blueprint, also record `"blueprint": "designs/{featurename}_blueprint.md"` in the screen entry.

---

## Step 1.8: User Final Approval

Present all approved designs:

```
## Design Approved: {FeatureName}

Stitch Project ID: {projectId}

| State | Screenshot |
|-------|------------|
| Success | designs/{featurename}.png |
| Loading | designs/{featurename}_loading.png |
| Failed | designs/{featurename}_failed.png |
| Empty | designs/{featurename}_empty.png |

Design spec: .claude/docs/{featurename}/designs/{featurename}.md

Ready to proceed?
```

**Mode 1**: This is the final step. Show completion report and stop.

**Mode 2 or 3**: Proceed to Phase 2 (Implementation).

---

## Output

After Phase 1 completes:
- Success screenshot: `.claude/docs/{featurename}/designs/{featurename}.png`
- Loading screenshot: `.claude/docs/{featurename}/designs/{featurename}_loading.png`
- Failed screenshot: `.claude/docs/{featurename}/designs/{featurename}_failed.png`
- Empty screenshot: `.claude/docs/{featurename}/designs/{featurename}_empty.png` (list screens)
- Design description: `.claude/docs/{featurename}/designs/{featurename}.md`
- Implementation blueprint: `.claude/docs/{featurename}/designs/{featurename}_blueprint.md` (Modes 2 & 3)
- stitch.json updated with approved screen and all state screenshots
- All variant screenshots cleaned up
- User approval received

**Mode 1** → Show completion report. Done.
**Mode 2/3** → Proceed to **Phase 2: Implementation**.
