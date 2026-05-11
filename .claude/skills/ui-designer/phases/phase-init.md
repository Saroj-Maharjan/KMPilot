# Phase Init: Project Initialization (One-Time Per Repo)

**Purpose**: Create a single shared Stitch project for the entire KMP repo, establish the design system from XTheme.kt, and generate shared Loading and Failed state screens that all features reuse.

**When to run**: When `.claude/docs/_project/stitch-project.json` does not exist, or `initState.completedAt` is null. Safe to re-run — each step checks `initState` flags before executing and skips completed steps.

---

## Migration Detection (Runs Before Init-1)

Before starting Init-1, check for legacy per-feature Stitch projects:

```bash
Glob: .claude/docs/*/stitch.json
```

If any legacy per-feature `stitch.json` files exist, prompt the user:

```
This repo has per-feature Stitch projects from an older skill version:
- {featurename} (projectId: {id})
...

The ui-designer skill now uses one shared Stitch project for the whole repo.

Option A — Full migration (recommended):
  Create a new shared project. Migrate screen registrations to the new config.
  Old per-feature Stitch projects stay in your account (delete them manually).
  Old screenshots, HTML, and blueprints are preserved in .claude/docs/.
  Future /ui-designer runs use the new shared project.

Option B — Start fresh:
  Same as A. Old blueprintConsumed=true features won't get design-system
  coverage on old screens — those are already implemented, no loss.

Option C — Cancel:
  Stop. No changes made.
```

- If **C**: stop immediately. No changes made.
- If **A or B**: run Init-1 through Init-8, then for each legacy `stitch.json`:
  - `blueprintConsumed: true` → write `features[featurename]` entry in `stitch-project.json` with old file paths, marked `"legacyProject": true, "legacyProjectId": "{old-projectId}"`.
  - `blueprintConsumed: false` → warn: "{featurename} has an in-flight blueprint. Re-run /ui-designer {featurename} to regenerate its success screen in the new shared project."
  - Slim each legacy `stitch.json` to the new per-feature format: remove `projectId`, `theme`, `deviceType`, `modelId`, `stateScreenIds` fields. Retain `featureName`, `blueprintConsumed`, `screens` (description, approved, approvedAt only), `updatedAt`.

---

## Init Checklist

Display this checklist at the start of Init and update each item as it completes:

```
Project Init Progress:
- [ ] Init-1: Verify Stitch MCP
- [ ] Init-2: Theme setup
- [ ] Init-3: Create shared Stitch project
- [ ] Init-4: Create design system from XTheme.kt
- [ ] Init-5: Generate shared Loading state screen
- [ ] Init-6: Generate shared Failed state screen
- [ ] Init-7: Persist and finalize
```

---

## Resumption Logic

Before running any init step, read `.claude/docs/_project/stitch-project.json` if it exists and check `initState`:

- `initState.projectCreated == true` → skip Init-3
- `initState.designSystemCreated == true` → skip Init-4
- `initState.sharedScreensGenerated == true` → skip Init-5 and Init-6
- `initState.completedAt` non-null → Init is already complete; stop and inform user

For each step that is skipped, mark it as `[done]` in the checklist display.

---

## Init-1: Verify Stitch MCP

Call `mcp__stitch__list_projects`.

**If successful**: Stitch MCP is available. Mark Init-1 complete. Proceed to Init-2.

**If fails** (tool not found or connection error):
```
Stitch MCP is not available. Check your Claude Code MCP configuration.

To use the UI Designer skill:
1. Add the Stitch MCP server to your Claude Code configuration
2. Restart Claude Code
3. Re-invoke /ui-designer

The Stitch MCP server is required for all UI Designer modes.
```
**STOP** — do not proceed.

---

## Init-2: Theme Setup

Run the **full theme setup logic** exactly as specified below. This is the same procedure as the old phase-0-preflight.md Step 0.1.

### Detect Existing Setup

Read `core/designsystem/src/commonMain/kotlin/thisissadeghi/designsystem/XTheme.kt`.

**If both `XLightColors` (lightColorScheme) and `XDarkColors` (darkColorScheme) already exist** in `XTheme.kt`:

```
The app already has both light and dark color schemes configured.

Would you like to:
- Keep existing palette — proceed with the current colors
- Reconfigure — set a new palette from a primary brand color
```

Use `AskUserQuestion` with these two options. If **Reconfigure**: proceed with full setup below.

If **Keep existing**:

1. **Check completeness** — verify both `XLightColors` and `XDarkColors` define all of these M3 roles: `primary`, `onPrimary`, `primaryContainer`, `onPrimaryContainer`, `background`, `surface`, `onBackground`, `onSurface`, `onSurfaceVariant`, `surfaceVariant`, `outline`, `outlineVariant`, `error`, `onError`, `errorContainer`, `onErrorContainer`.

2. **If complete**: read the current primary color and default theme choice from `XTheme.kt` (infer default from which scheme `XTheme()` composable passes to `MaterialTheme`). Store in context and skip to **Output**.

3. **If NOT complete**: extract the `primary` hex value from the existing scheme (prefer `XLightColors`; fall back to `XDarkColors` if light is absent). Set this value as `primaryHex` and proceed with **Ask Default Theme** → **Generate Both Color Palettes** → **Update XTheme.kt Structure** — exactly as if the user had selected **Reconfigure**.

**If only `lightColorScheme` / `XColors` exists** (no dark scheme): proceed with full setup.

### Ask Default Theme

Using `AskUserQuestion`: **"What should the app's default theme be?"**

| Option | Description |
|--------|-------------|
| Light | App always uses the light color scheme |
| Dark | App always uses the dark color scheme |

Store as `defaultTheme`.

### Ask Color Palette

Using `AskUserQuestion`: **"Which color palette do you want to use?"**

| Option | Description |
|--------|-------------|
| Keep current | Use the colors already defined in XTheme.kt |
| Customize | Provide a primary brand color to generate a full palette |

**If Keep current**: note the existing primary hex. Skip to **Update XTheme.kt Structure**.

**If Customize**: ask (free text via `AskUserQuestion`): **"Enter your primary brand color as a HEX value (e.g., #B02418):"**

Store the user's answer as `primaryHex`.

### Generate Both Color Palettes

From `primaryHex`, derive complete M3-compliant palettes for both themes. Apply the primary color's hue undertone consistently across the neutral tones.

**Light theme (`XLightColors = lightColorScheme(...)`)** — bright backgrounds, dark text:

| Role | Derivation rule |
|------|----------------|
| `primary` | `primaryHex` as-is |
| `onPrimary` | High-contrast on primary: white if primary is dark, near-black if primary is light |
| `primaryContainer` | Primary hue desaturated and lightened to ~90% tonal value (very light tint) |
| `onPrimaryContainer` | Primary hue darkened to ~10% tonal value (very dark tint) |
| `background` | Near-white neutral with a subtle undertone derived from the primary hue |
| `surface` | Same as background or marginally lighter |
| `onBackground` | Near-black with the primary hue's subtle undertone |
| `onSurface` | Same as onBackground |
| `onSurfaceVariant` | Medium gray with primary hue undertone — lower contrast than onSurface |
| `surfaceVariant` | Light gray with primary hue undertone — visually distinct from surface |
| `outline` | Medium-weight gray with primary hue undertone, readable against surface |
| `outlineVariant` | Lighter/softer variant of outline for decorative or low-emphasis borders |
| `error` | Standard M3 light error color |
| `onError` | Standard M3 onError for light theme |
| `errorContainer` | Standard M3 errorContainer for light theme |
| `onErrorContainer` | Standard M3 onErrorContainer for light theme |

**Dark theme (`XDarkColors = darkColorScheme(...)`)** — dark backgrounds, light text:

| Role | Derivation rule |
|------|----------------|
| `primary` | Primary hue lightened to ~80% tonal value — bright enough to stand out on dark bg |
| `onPrimary` | Primary hue darkened to ~20% tonal value |
| `primaryContainer` | Primary hue darkened to ~30% tonal value (dark container) |
| `onPrimaryContainer` | Primary hue lightened to ~90% tonal value |
| `background` | Very dark neutral with the primary hue's subtle undertone |
| `surface` | Slightly elevated over background — same undertone, marginally lighter |
| `onBackground` | Near-white with the primary hue's subtle undertone |
| `onSurface` | Same as onBackground |
| `onSurfaceVariant` | Muted light gray with primary hue undertone — lower contrast than onSurface |
| `surfaceVariant` | Dark elevated surface with primary hue undertone |
| `outline` | Medium gray readable against dark surface, with primary hue undertone |
| `outlineVariant` | Softer/darker variant of outline for subtle borders |
| `error` | Standard M3 dark error color (brighter than light theme for dark bg legibility) |
| `onError` | Standard M3 onError for dark theme |
| `errorContainer` | Standard M3 errorContainer for dark theme |
| `onErrorContainer` | Standard M3 onErrorContainer for dark theme |

Apply the primary color's hue undertone consistently across all neutral roles. Generate precise hex values — do not leave placeholders. Only include roles the current project uses.

### Update XTheme.kt Structure

Edit `core/designsystem/src/commonMain/kotlin/thisissadeghi/designsystem/XTheme.kt`:

1. If the old private val was named `XColors`, rename it to `XLightColors`.
2. Add `XDarkColors` using `darkColorScheme(...)` with the generated dark palette.
3. Update the `XTheme()` composable to always use the scheme matching `defaultTheme`:

```kotlin
// defaultTheme = light
@Composable
fun XTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        content = content,
        colorScheme = XLightColors,
        shapes = Shapes,
        typography = MaterialTheme.typography,
    )
}

// defaultTheme = dark
@Composable
fun XTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        content = content,
        colorScheme = XDarkColors,
        shapes = Shapes,
        typography = MaterialTheme.typography,
    )
}
```

4. Verify the build compiles: `./gradlew :core:designsystem:assembleAndroidMain`

### Init-2 Write Checkpoint

After completing theme setup:

1. Create directory `.claude/docs/_project/` if it does not exist:
   ```bash
   mkdir -p .claude/docs/_project
   ```

2. Create `.claude/docs/_project/stitch-project.json` as a skeleton with `initState` all false/null, and write the `themeSnapshot` into `designSystem.themeSnapshot`:

```json
{
  "projectId": null,
  "projectName": null,
  "repoName": "{repo name, e.g. KMPilot}",
  "deviceType": "MOBILE",
  "modelId": "GEMINI_3_FLASH",
  "designSystem": {
    "assetId": null,
    "name": null,
    "colorMode": "{LIGHT|DARK}",
    "sourceOfTruth": "XTheme.kt",
    "syncedAt": null,
    "themeSnapshot": {
      "defaultTheme": "{light|dark}",
      "primaryHex": "{primaryHex}",
      "paletteCustomized": "{true|false}",
      "light": {
        "primary": "{hex}",
        "background": "{hex}",
        "surface": "{hex}",
        "error": "{hex}",
        "onSurfaceVariant": "{hex}"
      },
      "dark": {
        "primary": "{hex}",
        "background": "{hex}",
        "surface": "{hex}",
        "error": "{hex}",
        "onSurfaceVariant": "{hex}"
      }
    }
  },
  "sharedStateScreens": {
    "loading": {
      "screenId": null,
      "screenName": null,
      "screenshot": null,
      "htmlPath": null,
      "tokensPath": null,
      "dimensions": null,
      "generatedAt": null,
      "designSystemApplied": false
    },
    "failed": {
      "screenId": null,
      "screenName": null,
      "screenshot": null,
      "htmlPath": null,
      "tokensPath": null,
      "dimensions": null,
      "generatedAt": null,
      "designSystemApplied": false
    }
  },
  "features": {},
  "initState": {
    "projectCreated": false,
    "designSystemCreated": false,
    "sharedScreensGenerated": false,
    "completedAt": null
  },
  "createdAt": "{ISO timestamp}",
  "updatedAt": "{ISO timestamp}"
}
```

Mark Init-2 complete. Proceed to Init-3.

---

## Init-3: Create Shared Stitch Project

Call `mcp__stitch__create_project`.

Store the returned `projectId` and `projectName` in `stitch-project.json`:
- `stitch-project.json.projectId = {returned projectId}`
- `stitch-project.json.projectName = {returned projectName}`

Update:
- `stitch-project.json.initState.projectCreated = true`
- `stitch-project.json.updatedAt = {ISO timestamp}`

Write the file. Mark Init-3 complete. Proceed to Init-4.

---

## Init-4: Create Design System

Read `XTheme.kt` and map M3 roles to Stitch design-system fields using the `themeSnapshot` already written in Init-2.

Use the color values from the scheme matching `defaultTheme`:

| XTheme.kt role | Stitch field | Notes |
|---|---|---|
| `primary` (active scheme) | `primaryColor` | Use scheme matching `defaultTheme` |
| `secondary` | `secondaryColor` | Omit if not defined in XTheme.kt |
| `tertiary` | `tertiaryColor` | Omit if not defined in XTheme.kt |
| `defaultTheme` | `colorMode` | `LIGHT` or `DARK` |
| `Shapes` corner dp | `roundness` | Map small/medium/large corner to Stitch roundness scale |

Call `mcp__stitch__create_design_system` with:
- `projectId`: from `stitch-project.json.projectId`
- `primaryColor`: primary hex from active scheme
- `colorMode`: `LIGHT` or `DARK` matching `defaultTheme`
- `roundness`: mapped from `Shapes` corner dp
- `secondaryColor` / `tertiaryColor`: if defined in XTheme.kt

Store the returned design system ID:
- `stitch-project.json.designSystem.assetId = {returned assetId}`
- `stitch-project.json.designSystem.name = {returned name}`
- `stitch-project.json.designSystem.syncedAt = {ISO timestamp}`

Update:
- `stitch-project.json.initState.designSystemCreated = true`
- `stitch-project.json.updatedAt = {ISO timestamp}`

Write the file. Mark Init-4 complete.

**Source of truth rule**: XTheme.kt always wins when XTheme.kt and the Stitch design system drift. Stitch is a design-time mirror; sync direction is always XTheme.kt → Stitch.

Proceed to Init-5.

---

## Init-5: Generate Shared Loading Screen

### Prepare Prompt

Substitute color values from the active scheme's `themeSnapshot`:

```
A mobile loading screen using the app's M3 color scheme.

Background: {background hex from defaultTheme} (M3: background)
Primary accent: {primary hex} (M3: primary)

Layout:
- Full-screen background in the background color — no top app bar, no bottom navigation
- Center: Vertically and horizontally centered circular progress indicator in primary accent color
- No other content whatsoever

The screen represents a raw generic loading state overlaid by the feature. Keep it minimal.
```

### Generation Procedure

1. **Record baseline**: Call `mcp__stitch__list_screens` with `projectId` from `stitch-project.json`. Record all current screen IDs.

2. Call `mcp__stitch__generate_screen_from_text` with:
   - `projectId`: from `stitch-project.json.projectId`
   - `prompt`: loading screen prompt above
   - `deviceType`: MOBILE
   - `modelId`: GEMINI_3_FLASH

3. **Timeout handling**: If the call times out or returns a connection error, do NOT immediately ask the user to open the browser. Instead:
   - Call `mcp__stitch__list_screens` to check if the screen appeared in the diff.
   - If a new screen ID is found → proceed normally (generation succeeded in background).
   - If `list_screens` still shows nothing → only then ask the user to open the project in their browser to trigger sync: `https://stitch.withgoogle.com/projects/{projectId}`. Wait for confirmation, then call `list_screens` again. Max 2 retries.

4. Call `mcp__stitch__list_screens` again with `projectId`. Diff against baseline → identify the new screen ID.

5. Call `get_screen` for the new screenId:
   ```
   projectId = stitch-project.json.projectId
   name      = "projects/{projectId}/screens/{screenId}"
   screenId  = {screenId}
   ```
   Use `screenshot.downloadUrl` from the response.

6. Create the shared designs directory if it does not exist:
   ```bash
   mkdir -p .claude/docs/_shared/designs
   ```

7. Download screenshot:
   ```bash
   curl -sL "{downloadUrl}=s0" -o .claude/docs/_shared/designs/loading.png
   ```

8. Record dimensions (`width`, `height`) from the `get_screen` response.

9. Write to `stitch-project.json.sharedStateScreens.loading`:
   ```json
   {
     "screenId": "{screenId}",
     "screenName": "projects/{projectId}/screens/{screenId}",
     "screenshot": ".claude/docs/_shared/designs/loading.png",
     "htmlPath": null,
     "tokensPath": null,
     "dimensions": { "width": {width}, "height": {height} },
     "generatedAt": "{ISO timestamp}",
     "designSystemApplied": false
   }
   ```

10. Update `stitch-project.json.updatedAt`. Write the file.

Proceed to Init-6.

---

## Init-6: Generate Shared Failed Screen

### Prepare Prompt

Substitute color values from the active scheme's `themeSnapshot`:

```
A mobile error screen using the app's M3 color scheme.

Background: {background hex from defaultTheme} (M3: background)
Primary accent: {primary hex} (M3: primary)
Error color: {error hex from defaultTheme} (M3: error)
Muted text: {onSurfaceVariant hex} (M3: onSurfaceVariant)

Layout:
- Full-screen background in the background color — no top app bar, no bottom navigation
- Center: Vertically and horizontally centered column containing:
  - Error icon (warning or error symbol) in error color
  - Error message "Something went wrong" in onSurfaceVariant text, body size
  - "Retry" button in primary accent color with onPrimary text

The screen represents a raw generic failed/error state. No chrome — no app bar, no nav bar.
```

### Generation Procedure

Same baseline-diff procedure as Init-5:

1. **Record baseline**: Call `mcp__stitch__list_screens` with `projectId` from `stitch-project.json`.

2. Call `mcp__stitch__generate_screen_from_text` with:
   - `projectId`: from `stitch-project.json.projectId`
   - `prompt`: failed screen prompt above
   - `deviceType`: MOBILE
   - `modelId`: GEMINI_3_FLASH

3. **Timeout handling**: Same as Init-5 — on timeout, call `mcp__stitch__list_screens` first to check if the screen appeared. Only ask the user to open the browser if `list_screens` still shows nothing after the diff. Max 2 retries.

4. Call `mcp__stitch__list_screens` again. Diff against baseline → identify the new screen ID.

5. Call `get_screen` for the new screenId (same pattern as Init-5). Use `screenshot.downloadUrl`.

6. Download screenshot:
   ```bash
   curl -sL "{downloadUrl}=s0" -o .claude/docs/_shared/designs/failed.png
   ```

7. Record dimensions.

8. Write to `stitch-project.json.sharedStateScreens.failed`:
   ```json
   {
     "screenId": "{screenId}",
     "screenName": "projects/{projectId}/screens/{screenId}",
     "screenshot": ".claude/docs/_shared/designs/failed.png",
     "htmlPath": null,
     "tokensPath": null,
     "dimensions": { "width": {width}, "height": {height} },
     "generatedAt": "{ISO timestamp}",
     "designSystemApplied": false
   }
   ```

9. Update `stitch-project.json.initState.sharedScreensGenerated = true`. Update `updatedAt`. Write the file.

Mark Init-5 and Init-6 complete. Proceed to Init-7.

---

## Init-7: Persist and Finalize

### Download HTML and Tokenize Shared Screens

1. Create the extraction directory:
   ```bash
   mkdir -p .claude/docs/_shared/designs/extracted
   ```

2. **Download HTML for Loading screen**: Call `get_screen` for `sharedStateScreens.loading.screenId` using `projectId` from `stitch-project.json`. Use `htmlCode.downloadUrl`.
   ```bash
   curl -sL -o .claude/docs/_shared/designs/extracted/stitch_loading.html {htmlCode.downloadUrl}
   ```
   Verify with `wc -c` — if 0 bytes, call `get_screen` again for a fresh URL and retry once.

3. **Tokenize Loading HTML**:
   ```bash
   python3 .claude/skills/_shared/extract_tokens.py \
     .claude/docs/_shared/designs/extracted/stitch_loading.html \
     > .claude/docs/_shared/designs/extracted/tokens_loading.md
   ```

4. **Download HTML for Failed screen**: Same procedure using `sharedStateScreens.failed.screenId`.
   ```bash
   curl -sL -o .claude/docs/_shared/designs/extracted/stitch_failed.html {htmlCode.downloadUrl}
   ```
   Verify with `wc -c`. Retry once on empty.

5. **Tokenize Failed HTML**:
   ```bash
   python3 .claude/skills/_shared/extract_tokens.py \
     .claude/docs/_shared/designs/extracted/stitch_failed.html \
     > .claude/docs/_shared/designs/extracted/tokens_failed.md
   ```

### Update stitch-project.json

Update HTML and token paths for both shared screens:
- `stitch-project.json.sharedStateScreens.loading.htmlPath = ".claude/docs/_shared/designs/extracted/stitch_loading.html"`
- `stitch-project.json.sharedStateScreens.loading.tokensPath = ".claude/docs/_shared/designs/extracted/tokens_loading.md"`
- `stitch-project.json.sharedStateScreens.failed.htmlPath = ".claude/docs/_shared/designs/extracted/stitch_failed.html"`
- `stitch-project.json.sharedStateScreens.failed.tokensPath = ".claude/docs/_shared/designs/extracted/tokens_failed.md"`

Set `initState.completedAt` to the current ISO timestamp.
Update `updatedAt` to the current ISO timestamp.
Write the file.

### Completion Summary

Present to the user:

```
Project Init Complete

Shared Stitch Project: {projectId}
Design System: {designSystemAssetId}
Loading screen: .claude/docs/_shared/designs/loading.png
Failed screen: .claude/docs/_shared/designs/failed.png
Config: .claude/docs/_project/stitch-project.json

You can now run /ui-designer {featurename} for any feature.
```

---

## stitch-project.json Full Schema

Created at Init-2, progressively filled through Init-8. The authoritative schema reference is in [stitch-guide.md](../references/stitch-guide.md#stitch-project-schema).

```json
{
  "projectId": "string — Stitch project ID (shared)",
  "projectName": "string — Full resource name (projects/{id})",
  "repoName": "string — KMP repo name",
  "deviceType": "string — Always MOBILE",
  "modelId": "string — Always GEMINI_3_FLASH",
  "designSystem": {
    "assetId": "string — Design system asset ID",
    "name": "string — Design system resource name",
    "colorMode": "string — LIGHT or DARK",
    "sourceOfTruth": "string — Always XTheme.kt",
    "syncedAt": "string — ISO timestamp of last XTheme.kt → Stitch sync",
    "themeSnapshot": {
      "defaultTheme": "string — light or dark",
      "primaryHex": "string — primary brand color hex",
      "paletteCustomized": "boolean",
      "light": { "primary": "hex", "background": "hex", "surface": "hex", "error": "hex", "onSurfaceVariant": "hex" },
      "dark":  { "primary": "hex", "background": "hex", "surface": "hex", "error": "hex", "onSurfaceVariant": "hex" }
    }
  },
  "sharedStateScreens": {
    "loading": {
      "screenId": "string",
      "screenName": "string",
      "screenshot": "string — path to .png",
      "htmlPath": "string — path to .html",
      "tokensPath": "string — path to tokens.md",
      "dimensions": { "width": "number", "height": "number" },
      "generatedAt": "string — ISO timestamp",
      "designSystemApplied": "boolean"
    },
    "failed": {
      "screenId": "string",
      "screenName": "string",
      "screenshot": "string — path to .png",
      "htmlPath": "string — path to .html",
      "tokensPath": "string — path to tokens.md",
      "dimensions": { "width": "number", "height": "number" },
      "generatedAt": "string — ISO timestamp",
      "designSystemApplied": "boolean"
    }
  },
  "features": {
    "{featurename}": {
      "successScreenId": "string — Stitch screen ID for success state",
      "successScreenName": "string — Full resource name",
      "emptyScreenId": "string or null — Stitch screen ID for empty state (list screens only)",
      "screenshot": "string — path to success .png",
      "htmlPath": "string — path to success .html",
      "tokensPath": "string — path to success tokens.md",
      "dimensions": { "width": "number", "height": "number" },
      "designFile": "string — path to .md design description",
      "blueprintFile": "string — path to _blueprint.md",
      "approved": "boolean",
      "approvedAt": "string — ISO date",
      "createdAt": "string — ISO timestamp",
      "updatedAt": "string — ISO timestamp",
      "legacyProject": "boolean — optional, true if migrated from legacy per-feature project",
      "legacyProjectId": "string — optional, old per-feature projectId"
    }
  },
  "initState": {
    "projectCreated": "boolean",
    "designSystemCreated": "boolean",
    "sharedScreensGenerated": "boolean",
    "completedAt": "string or null — ISO timestamp when Init-8 completed"
  },
  "createdAt": "string — ISO timestamp",
  "updatedAt": "string — ISO timestamp"
}
```
