# Stitch MCP Reference Guide

Quick reference for using Google Stitch MCP tools effectively within the UI Designer skill.

---

## Available Tools

| Tool | Purpose | When to Use |
|------|---------|-------------|
| `mcp__stitch__list_projects` | List all accessible Stitch projects | Preflight check, finding existing projects |
| `mcp__stitch__create_project` | Create a new Stitch project | When no project exists for the feature |
| `mcp__stitch__get_project` | Get project details | Verify project exists, get metadata |
| `mcp__stitch__list_screens` | List all screens in a project | After generation, to find screen IDs |
| `mcp__stitch__get_screen` | Get screen details + screenshot URL | Retrieve screen data. **Requires all 3 params**: `name` (full resource name), `projectId`, `screenId`. Use `screenshot.downloadUrl` from response |
| `mcp__stitch__generate_screen_from_text` | Generate a new screen from prompt | Initial screen design |
| `mcp__stitch__edit_screens` | Edit existing screens with prompt | Design iteration based on feedback |
| `mcp__stitch__generate_variants` | Generate design variants | When user wants to explore alternatives |

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
9. **Model selection**: Use `GEMINI_3_PRO` for Mode 1 (design only, highest quality). Use `GEMINI_3_FLASH` for Modes 2 & 3

---

## M3 Color Roles Reference

Full M3 role catalog, color rules, and usage guidance: **[m3-colors.md](m3-colors.md)**

Key points (read the full reference when writing Stitch prompts or doing Color Audits):
- **Source of truth**: `XTheme.kt` defines all active roles in `XLightColors` (lightColorScheme) and `XDarkColors` (darkColorScheme)
- Use the scheme matching `defaultTheme` (from Phase 0 Step 0.1.5) when writing Stitch prompts
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
| Screen scaffold | `XScaffold` | `core.designsystem` |
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

## Known Issues (Google Stitch API)

### Screens Not Visible After Generation

**Status**: Upstream bug in Google Stitch API. Not caused by this skill or MCP client.

**Problem**: After `generate_screen_from_text` or `edit_screens` completes successfully, `list_screens` returns empty — the newly created screen is not queryable via MCP. The screen exists server-side (`get_project` returns a valid `thumbnailScreenshot`), but it is not indexed for `list_screens` or `get_screen` until the project is opened in a browser.

**Workaround**: After any Stitch generation or edit operation, the skill must ask the user to open the project in their browser to trigger screen sync. See the **Screen Sync Procedure** in Phase 1.

**Tracking**: Reported on the [Google AI Developers Forum — Stitch](https://discuss.ai.google.dev/c/stitch/61). Remove this workaround once Google fixes the API.

---

## Screenshot Workflow

### Downloading Screenshots

`get_screen` returns both `screenshot.downloadUrl` and `htmlCode.downloadUrl`. Use `screenshot.downloadUrl` for visual reference. The `htmlCode.downloadUrl` is used in the **Compose Implementation Blueprint** step (Phase 1 Step 1.6.6) to extract structured design data for implementation.

```bash
# 1. Get screen data (all 3 params required)
mcp__stitch__get_screen:
  name: "projects/{projectId}/screens/{screenId}"
  projectId: "{projectId}"
  screenId: "{screenId}"
→ use response.screenshot.downloadUrl for screenshots
→ use response.htmlCode.downloadUrl for blueprint extraction (Step 1.6.6 only)

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

For Modes 2 & 3, Stitch HTML is parsed into a structured blueprint for implementation. Full spec, extraction prompt, and edge cases: **[blueprint-spec.md](blueprint-spec.md)**

See Phase 1 Step 1.6.6.

---

## stitch.json Schema

The tracking file stored at `.claude/docs/{featurename}/stitch.json`:

```json
{
  "projectId": "string - Stitch project ID",
  "projectName": "string - Full resource name (projects/{id})",
  "featureName": "string - KMP feature name (lowercase)",
  "deviceType": "string - Always MOBILE",
  "modelId": "string - GEMINI_3_PRO (Mode 1) or GEMINI_3_FLASH (Modes 2 & 3)",
  "theme": {
    "defaultTheme": "string - light or dark",
    "primaryHex": "string - effective primary brand color hex",
    "paletteCustomized": "boolean - true if user provided a custom primary hex"
  },
  "screens": {
    "screen_key": {
      "screenId": "string - Stitch screen ID (success state)",
      "screenName": "string - Full resource name",
      "description": "string - What the screen shows",
      "designFile": "string - Path to design description .md file",
      "screenshot": "string - Path to approved success screenshot .png file",
      "stateScreenshots": {
        "loading": "string - Path to loading state screenshot",
        "failed": "string - Path to failed state screenshot",
        "empty": "string - Path to empty state screenshot (list screens only)"
      },
      "stateScreenIds": {
        "loading": "string - Stitch screen ID for loading state",
        "failed": "string - Stitch screen ID for failed state",
        "empty": "string - Stitch screen ID for empty state (list screens only)"
      },
      "blueprint": "string - Path to blueprint .md file (Modes 2 & 3 only)",
      "approved": "boolean - User approved this design",
      "approvedAt": "string - ISO date"
    }
  },
  "implementation": {
    "implemented": "boolean - Code implementation completed (Modes 2 & 3)",
    "implementedAt": "string - ISO date",
    "method": "string - Implementation method (e.g., 'blueprint')"
  },
  "verification": {
    "verified": "boolean - Visual verification completed (Mode 3)",
    "verifiedAt": "string - ISO date",
    "matchScores": {
      "screen_key": "number - Match percentage"
    },
    "deviceScreenshots": {
      "screen_key": "string - Path to device screenshot"
    },
    "attempts": "number - Number of verification attempts"
  },
  "createdAt": "string - ISO date",
  "updatedAt": "string - ISO date"
}
```
