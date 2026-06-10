# Stitch MCP Reference Guide

Quick reference for using Google Stitch MCP tools effectively within the UI Designer skill.

## Contents
- **Available Tools** — the Stitch MCP tool table
- **Get Screen Call Pattern** — the 3-param construction for `get_screen`
- **Prompt Engineering for Stitch** — prompt structure + good/bad examples
- **M3 Color Roles Reference** — pointer to m3-colors.md
- **Design Iteration Patterns** — refine colors / layout / components; variants
- **Mapping Stitch Designs to KMP X-Components** — visual-element → X-component table
- **Motion** — capture-only policy (pointer to `_shared/motion.md`)
- **Known Issues (Google Stitch API)** — the screen-sync workaround + canonical no-blind-retry rationale
- **Screenshot Workflow** — download + naming conventions
- **Compose Implementation Blueprint** — pointer to blueprint-spec.md
- **Config File Architecture → Project-Wide Config** — the authoritative `stitch-project.json` schema (single source of truth)

---

## Available Tools

| Tool | Purpose | When to Use |
|------|---------|-------------|
| `mcp__stitch__list_projects` | List all accessible Stitch projects | Preflight check, finding existing projects |
| `mcp__stitch__create_project` | Create a new Stitch project | Project Init only (one shared project per repo) |
| `mcp__stitch__get_project` | Get project details | Verify project exists, get metadata |
| `mcp__stitch__list_screens` | List all screens in a project | After generation, to find screen IDs |
| `mcp__stitch__get_screen` | Get screen details + screenshot URL | Retrieve screen data. **Requires all 3 params** — see [Get Screen Call Pattern](#get-screen-call-pattern) below |
| `mcp__stitch__generate_screen_from_text` | Generate a new screen from prompt | Initial screen design |
| `mcp__stitch__edit_screens` | Edit existing screens with prompt | Design iteration based on feedback |
| `mcp__stitch__generate_variants` | Generate design variants | When user wants to explore alternatives |
| `mcp__stitch__apply_design_system` | Apply a design system to selected screens | Not needed — screens auto-inherit the project design system on generation. Reserved for manual re-sync if drift is detected. |
| `mcp__stitch__create_design_system` | Create a new design system for a project | Project Init only (Init-4) |
| `mcp__stitch__update_design_system` | Update an existing design system | When drift detected between XTheme.kt and Stitch |
| `mcp__stitch__list_design_systems` | List all design systems in a project | Debugging, verifying design system exists |

---

## Get Screen Call Pattern

`mcp__stitch__get_screen` requires three params that are derivable from a single `screenId` plus the `projectId` from `stitch-project.json`. Construct them like this **every time** the tool is called — never document the three params separately at each call site:

```
projectId = stitch-project.json.projectId   ← always the shared project
name      = "projects/{projectId}/screens/{screenId}"
screenId  = {screenId}
```

**`projectId` is always read from `stitch-project.json.projectId` (the shared project). Never use a per-feature `stitch.json` for `projectId` — per-feature stitch.json files no longer contain a projectId field after the shared-project migration.**

Throughout this skill, instructions of the form *"call `get_screen` for {screenId}"* mean: build the three params using the pattern above, then invoke the tool.

The response carries both `screenshot.downloadUrl` (for `.png` screenshots) and `htmlCode.downloadUrl` (for raw HTML — used in Step 1.15 only).

---

## Prompt Engineering for Stitch

### Structure Your Prompts

Good Stitch prompts follow this pattern:

```
[Screen Type] for [Platform] with [Theme].

[Layout Description]:
- [Top section]: [details]
- [Main content]: [details]
- [Bottom section]: [details]

[Visual Specifications]:
- Background: [color]
- Text: [color, size, weight]
- Accent: [color]
- Spacing: [values]

[Component Details]:
- [Component 1]: [description]
- [Component 2]: [description]
```

### Good Prompt Example

```
A mobile product detail screen with a dark premium theme.

Defined colors (from XTheme lightColorScheme):
- Primary accent: #B02418 (M3: primary)

Proposed colors (to be added to XTheme after approval):
- Background: #0D0D0F (M3: background)
- Card surfaces: #1A1A1F (M3: surface)
- Primary text: #F5F5F7 (M3: onBackground)
- Secondary text: #7D7887 (M3: onSurfaceVariant)
- Accent blue: #1152D4 (M3: tertiary)
- Text on accent: #FFFFFF (M3: onTertiary)

Layout:
- Top: Modal top app bar with back arrow and product name in onBackground color
- Hero: Full-width product image (16:9 aspect ratio) with subtle gradient overlay
- Content: Scrollable column with 16dp horizontal padding
  - Product title in bold 24sp onBackground text
  - Price in 20sp tertiary color
  - Description in 14sp onSurfaceVariant with 8dp top margin
  - Specifications list: key-value pairs in surface-colored cards
- Bottom: Sticky "Add to Cart" button, full-width, tertiary background, onTertiary text

The design should feel premium, with generous whitespace and subtle shadows.
```

### Bad Prompt Example

```
Make a product page
```

This is too vague. Stitch needs specific visual details to generate quality designs.

### Tips for Better Results

1. **Read `XTheme.kt` first** — know exactly which M3 roles are defined before writing any prompt
2. **Use M3 color role names** with hex values from `lightColorScheme` (see [M3 Color Roles](#m3-color-roles-reference))
3. **Annotate colors** as `{hex} (M3: {role})` so implementation intent is clear
4. **Include dimensions** in dp/sp
5. **Describe spacing** explicitly (padding, margins, gaps)
6. **Name the components** you want (cards, buttons, lists)
7. **Describe states** if designing non-default states
8. **Reference platform patterns** ("like an iOS settings screen" or "Material Design-inspired")
9. **Model selection**: Always use `GEMINI_3_FLASH`

---

## M3 Color Roles Reference

Full M3 role catalog, color rules, and usage guidance: **[m3-colors.md](m3-colors.md)**

Key points (read the full reference when writing Stitch prompts or doing Color Audits):
- **Source of truth**: `XTheme.kt` defines all active roles in `XLightColors` (lightColorScheme) and `XDarkColors` (darkColorScheme)
- Use the scheme matching `defaultTheme` (from Phase 0 Step 0.1) when writing Stitch prompts
- Every design color **must** map to an M3 role — annotate as `{hex} (M3: {role})`
- Feature code uses `MaterialTheme.colorScheme.*` exclusively — never raw `Color()` hex
- Missing roles must be added to **both** `XLightColors` and `XDarkColors` before implementation (Phase 2 Step 2.1)

---

## Design Iteration Patterns

### Refining Colors
```
Edit prompt: "Change the background (M3: background) to #121218 and make the
accent color (M3: tertiary) teal #14B8A6 instead of blue. Keep everything else the same."
```

### Adjusting Layout
```
Edit prompt: "Move the action button from the bottom sticky bar to inline
below the description. Add more vertical spacing between sections (16dp → 24dp)."
```

### Adding Components
```
Edit prompt: "Add a horizontal chip row below the title showing product tags.
Use surfaceVariant-colored chips with onSurfaceVariant text."
```

### Generating Variants

Use `generate_variants` with these creative ranges:

| Range | Description | Use When |
|-------|-------------|----------|
| `REFINE` | Subtle changes, close to original | Fine-tuning an approved direction |
| `EXPLORE` | Balanced variations (default) | Seeing different approaches |
| `REIMAGINE` | Radical differences | Early exploration phase |

Aspects to focus on:
- `LAYOUT` - Different arrangement of elements
- `COLOR_SCHEME` - Alternative color palettes
- `IMAGES` - Different image styles/placeholders
- `TEXT_FONT` - Typography variations
- `TEXT_CONTENT` - Different copy/content

---

## Mapping Stitch Designs to KMP X-Components

When translating designs to code, use this mapping:

| Stitch Visual Element | X-Component | Import |
|-----------------------|-------------|--------|
| Top navigation bar | `XTopAppBar` | `core.designsystem` |
| Modal/secondary nav bar | `XModalTopAppBar` | `core.designsystem` |
| Primary action button | `XButton` | `core.designsystem` |
| Text button | `XTextButton` | `core.designsystem` |
| Outlined button | `XOutlinedButton` | `core.designsystem` |
| Icon button | `XIconButton` | `core.designsystem` |
| Card container | `XCard` | `core.designsystem` |
| Body text | `XText` | `core.designsystem` |
| Text input | `XTextField` | `core.designsystem` |
| Search bar | `SearchField` | `core.designsystem` |
| Loading spinner | `XCircularProgressIndicator` | `core.designsystem` |
| Remote image | `AsyncImage` | `core.designsystem` |
| Screen container | `XScreen` (Rule 13 — not `XScaffold`) | `core.designsystem` |
| Dialog/modal | `XDialog` | `core.designsystem` |
| Dropdown menu | `XDropDown` | `core.designsystem` |
| Radio selection | `XRadioButton` | `core.designsystem` |
| Snackbar message | `XSnackbarHost` | `core.designsystem` |
| Pull to refresh | `XPullRefresh` | `core.designsystem` |
| Icon | `XIcon` | `core.designsystem` |
| Toggle/switch | `XSwitch` | `core.designsystem` |
| Horizontal divider | `XHorizontalDivider` | `core.designsystem` |
| Vertical divider | `XVerticalDivider` | `core.designsystem` |
| Money/currency text | `MoneyText` | `core.designsystem` |
| Floating action button | `XFloatingActionButton` | `core.designsystem` |
| Bottom navigation bar | `XNavigationBar` + `XNavigationBarItem` | `core.designsystem` |
| Scrollable tabs | `XPrimaryScrollableTabRow` | `core.designsystem` |
| Modal bottom sheet | `XModalBottomSheet` | `core.designsystem` |
| Checkbox | `XCheckbox` | `core.designsystem` |
| Slider / range input | `XSlider` | `core.designsystem` |
| Filter chip / tag | `XFilterChip` | `core.designsystem` |
| Exposed dropdown (select) | `XExposedDropdownMenuBox` + `XDropdownMenuItem` | `core.designsystem` |
| Segmented toggle / connected button group | `XSelectionButtonContainer` + `XSelectionButton` | `core.designsystem` |

---

## Motion

`/ui-designer` is **capture-only** for animation: it never injects a motion directive into a Stitch prompt. The user controls motion through their own design prompts; the pipeline implements whatever motion the design's HTML contains. The extractor (`extract_tokens.py`) emits a `## Motion Inventory` per state, the Step 1.16 Motion Audit buckets it, the blueprint's `## Motion` table carries it to implementation, and `/verify-ui` Step 5.10 audits its presence.

Press/hover feedback (touch `active:*`, `ripple`; pointer `hover:*`/`group-hover:*`) is **dropped** (android + ios targets). The 4 kept families (Ambient bg, Loading/Attention loop, Entrance, Value-driven) + reduced-motion map to dedicated Compose motion files. Full policy, family→primitive mapping, easing map, and file layout: **[`_shared/motion.md`](../../_shared/motion.md)**.

---

## Known Issues (Google Stitch API)

### Screens Not Visible After Generation

**Status**: Upstream bug in Google Stitch API. Not caused by this skill or MCP client.

**Problem**: After `generate_screen_from_text` or `edit_screens` completes successfully, `list_screens` returns empty — the newly created screen is not queryable via MCP. The screen exists server-side (`get_project` returns a valid `thumbnailScreenshot`), but it is not indexed for `list_screens` or `get_screen` until the project is opened in a browser.

**Workaround**: A timeout/connection error from `generate_screen_from_text` or `edit_screens` does NOT mean the generation failed — it often succeeded in the background. **Never auto-retry the generation call on timeout/connection reset** — retrying produces duplicate screens because the original request usually completed server-side. Instead, ask the user to open `https://stitch.withgoogle.com/projects/{projectId}` in their browser to trigger the project sync, wait for their confirmation, then call `list_screens` to locate the new screen. See the **Screen Sync Procedure** in Phase 1.

**`edit_screens` vs `generate_screen_from_text`**: Use `edit_screens` to modify an existing screen (fix layout, change colors, remove elements). Use `generate_screen_from_text` only when creating a brand new screen from scratch. Using `generate_screen_from_text` to "fix" an existing screen creates a duplicate and pollutes the project.

**Tracking**: Reported on the [Google AI Developers Forum — Stitch](https://discuss.ai.google.dev/c/stitch/61). Remove this workaround once Google fixes the API.

---

## Screenshot Workflow

### Downloading Screenshots

`get_screen` returns both `screenshot.downloadUrl` (for `.png` previews) and `htmlCode.downloadUrl` (used in the HTML acquisition step, Phase 1 Step 1.15, which feeds the Color Audit (1.16) and the blueprint (1.17)). For the call signature, see [Get Screen Call Pattern](#get-screen-call-pattern).

```bash
# 1. Call get_screen for {screenId} per the Get Screen Call Pattern
#    → use response.screenshot.downloadUrl

# 2. Download screenshot
curl -sL -o .claude/docs/{featurename}/designs/{featurename}_v{N}.png {downloadUrl}

# 3. Display inline (Claude vision)
Read the .png file → shown to user in conversation
```

### Naming Conventions

| Phase | Filename | Purpose |
|-------|----------|---------|
| During selection | `{featurename}_v1.png`, `{featurename}_v2.png`, ... | Interim variants for user to compare |
| Success (approved) | `{featurename}.png` | Final approved success state |
| Loading state | `{featurename}_loading.png` | Loading/progress indicator state |
| Failed state | `{featurename}_failed.png` | Error state with retry action |
| Empty state | `{featurename}_empty.png` | Empty list state (list screens only) |
| Design description | `{featurename}.md` | Visual specs for all states |

### On Each Iteration

When the user requests edits or regeneration:
1. **Delete all** existing `{featurename}_v*.png` files
2. Run the Stitch operation
3. Re-download all screenshots with fresh `_v{N}` numbering
4. Display all inline for user to pick again

When the user requests **variants**:
1. **Delete all** existing `{featurename}_v*.png` files
2. **Download the original screen** as `{featurename}_v1.png` (preserve it for comparison)
3. Run the Stitch `generate_variants` operation
4. Download new variant screenshots as `{featurename}_v2.png`, `_v3.png`, etc.
5. Display all inline (original + variants) for user to pick

### On Final Approval

1. Rename selected `{featurename}_v{N}.png` → `{featurename}.png`
2. Delete all remaining `{featurename}_v*.png` files

---

## Compose Implementation Blueprint

Stitch HTML is always parsed into a structured blueprint after design approval. The blueprint is the self-contained handoff artifact for implementation skills. Full spec, extraction prompt, and edge cases: **[blueprint-spec.md](blueprint-spec.md)**

See Phase 1 Step 1.17.

---

## Config File Architecture

The `/ui-designer` skill uses a **single config file** for all Stitch state:

| File | Scope | Purpose |
|------|-------|---------|
| `.claude/docs/_project/stitch-project.json` | Repo-wide | Shared Stitch project, design system, shared state screens, all feature registrations, `blueprintConsumed` flag, verification results |

---

### Project-Wide Config: `.claude/docs/_project/stitch-project.json`

Created once by Project Init (`phase-init.md`). The `projectId` in this file is the only valid source for all Stitch API calls.

```json
{
  "projectId": "string — Stitch project ID (shared across the entire repo)",
  "projectName": "string — Full resource name (projects/{id})",
  "repoName": "string — KMP repo name",
  "deviceType": "string — Always MOBILE",
  "modelId": "string — Always GEMINI_3_FLASH",
  "designSystem": {
    "assetId": "string — Design system asset ID",
    "name": "string — Design system resource name",
    "colorMode": "string — LIGHT or DARK",
    "sourceOfTruth": "string — Always XTheme.kt",
    "xthemePath": "string — Repo-specific path to XTheme.kt, discovered at Init-2 from core/designsystem/build.gradle.kts namespace",
    "syncedAt": "string — ISO timestamp of last XTheme.kt → Stitch sync",
    "themeSnapshot": {
      "defaultTheme": "string — light or dark",
      "primaryHex": "string — primary brand color hex",
      "paletteCustomized": "boolean",
      "light": {
        "primary": "string — hex",
        "background": "string — hex",
        "surface": "string — hex",
        "error": "string — hex",
        "onSurfaceVariant": "string — hex"
      },
      "dark": {
        "primary": "string — hex",
        "background": "string — hex",
        "surface": "string — hex",
        "error": "string — hex",
        "onSurfaceVariant": "string — hex"
      }
    }
  },
  "sharedStateScreens": {
    "loading": {
      "screenId": "string — Stitch screen ID",
      "screenName": "string — Full resource name",
      "screenshot": "string — path to .png",
      "htmlPath": "string — path to .html",
      "tokensPath": "string — path to tokens .md",
      "dimensions": { "width": "number", "height": "number" },
      "generatedAt": "string — ISO timestamp",
      "designSystemApplied": "boolean",
      "codeImplemented": "boolean — false until an implementation skill rewrites AppLoadingState.kt to match this design; true after. Reset to false if the design is updated in Stitch."
    },
    "failed": {
      "screenId": "string — Stitch screen ID",
      "screenName": "string — Full resource name",
      "screenshot": "string — path to .png",
      "htmlPath": "string — path to .html",
      "tokensPath": "string — path to tokens .md",
      "dimensions": { "width": "number", "height": "number" },
      "generatedAt": "string — ISO timestamp",
      "designSystemApplied": "boolean",
      "codeImplemented": "boolean — false until an implementation skill rewrites AppErrorState.kt to match this design; true after. Reset to false if the design is updated in Stitch."
    }
  },
  "features": {
    "{featurename}": {
      "successScreenId": "string — Stitch screen ID for success state",
      "successScreenName": "string — Full resource name",
      "emptyScreenId": "string or null — Stitch screen ID for empty state (only when states.empty == true)",
      "states": {
        "loading": "boolean — true if this feature opts in to the shared loading screen",
        "failed":  "boolean — true if this feature opts in to the shared failed screen",
        "empty":   "boolean — true if this feature has a per-feature empty design"
      },
      "screenshot": "string — path to success .png",
      "htmlPath": "string — path to success .html",
      "tokensPath": "string — path to success tokens .md",
      "dimensions": { "width": "number", "height": "number" },
      "designFile": "string — path to .md design description",
      "blueprintFile": "string — path to _blueprint.md",
      "blueprintConsumed": "boolean — false when ui-designer saves a new blueprint, true after implementation skill consumes it",
      "approved": "boolean",
      "approvedAt": "string — ISO date",
      "createdAt": "string — ISO timestamp",
      "updatedAt": "string — ISO timestamp",
      "legacyProject": "boolean — optional, true if migrated from legacy per-feature project",
      "legacyProjectId": "string — optional, old per-feature projectId",
      "verification": {
        "verified": "boolean — Token-level verification completed",
        "verifiedAt": "string — ISO date",
        "auditReport": "string — Path to audit report .md",
        "xComponentsCompliant": "boolean",
        "criticalIssues": "number",
        "attempts": "number"
      }
    }
  },
  "initState": {
    "projectCreated": "boolean",
    "designSystemCreated": "boolean",
    "completedAt": "string or null — ISO timestamp when init finalized (Init-5)",
    "sharedScreensGenerated": "boolean — LEGACY only; the old init flow auto-generated shared loading/failed screens at init time. The new flow defers them to on-demand creation by Phase 1 Step 1.8. Not written by new init; safe to ignore."
  },
  "createdAt": "string — ISO timestamp",
  "updatedAt": "string — ISO timestamp"
}
```

