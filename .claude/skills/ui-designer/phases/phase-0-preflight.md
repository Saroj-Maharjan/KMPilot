# Phase 0: Preflight Checks

**Purpose**: Verify MCP availability, determine mode, resolve feature context, and set up Stitch project.

---

## Checklist

```
Preflight Progress:
- [ ] Step 0.1: Ask user for mode
- [ ] Step 0.1.5: Theme & color setup
- [ ] Step 0.2: Verify Stitch MCP availability
- [ ] Step 0.3: Verify Mobile MCP availability (Mode 3 only)
- [ ] Step 0.4: Resolve feature context
- [ ] Step 0.5: Set up Stitch project and initialize stitch.json
```

---

## Step 0.1: Mode Selection

Ask the user using `AskUserQuestion`:

**"Which mode would you like?"**

| Option | Label | Description |
|--------|-------|-------------|
| 1 | Design Only | Design screens in Stitch, export screenshots. No code changes. |
| 2 | Design + Implement | Design in Stitch, then implement UI in code after approval. |
| 3 | Design + Implement + Verify | Full pipeline: design, implement, run on device, compare screenshots. |

Store the selected mode. Device type is always `MOBILE`. Derive `modelId` from the mode:

| Mode | modelId |
|------|---------|
| 1 | `GEMINI_3_PRO` |
| 2, 3 | `GEMINI_3_FLASH` |

---

## Step 0.1.5: Theme & Color Setup

**Purpose**: Establish the app's color palette for both light and dark themes before any design or code work begins.

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

---

### Ask Default Theme

Using `AskUserQuestion`: **"What should the app's default theme be?"**

| Option | Description |
|--------|-------------|
| Light | App always uses the light color scheme |
| Dark | App always uses the dark color scheme |

Store as `defaultTheme`.

---

### Ask Color Palette

Using `AskUserQuestion`: **"Which color palette do you want to use?"**

| Option | Description |
|--------|-------------|
| Keep current | Use the colors already defined in XTheme.kt |
| Customize | Provide a primary brand color to generate a full palette |

**If Keep current**: note the existing primary hex. Skip to **Update XTheme.kt Structure**.

**If Customize**: ask (free text via `AskUserQuestion`): **"Enter your primary brand color as a HEX value (e.g., #B02418):"**

Store the user's answer as `primaryHex`.

---

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

---

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

---

### Output

Store in context for downstream phases:
- `defaultTheme`: `light` or `dark`
- `primaryHex`: effective primary color hex
- `paletteCustomized`: `true` or `false`

---

## Step 0.2: Verify Stitch MCP

**Required for ALL modes.**

Attempt to call `mcp__stitch__list_projects`. This verifies the Stitch MCP server is configured and accessible.

**If successful**: Stitch MCP is available. Proceed.

**If fails** (tool not found or connection error):
```
Stitch MCP is not configured. To use the UI Designer skill:

1. Add the Stitch MCP server to your Claude Code configuration
2. Restart Claude Code
3. Re-invoke /ui-designer

The Stitch MCP server is required for all UI Designer modes.
```
**STOP** - Do not proceed without Stitch MCP.

---

## Step 0.3: Verify Mobile MCP (Mode 3 Only)

**Required ONLY for Mode 3.**

Check for mobile MCP tools. The expected MCP server name is `claude_in_mobile` (tools prefixed `mcp__claude_in_mobile__*`).

Attempt to detect mobile MCP by checking available tool names. If no mobile MCP tools are found:

```
Mobile MCP (claude-in-mobile) is not configured. Mode 3 requires it for:
- Building and running the app on Android device/emulator
- Navigating to the designed screen
- Taking device screenshots for visual comparison

To configure:
1. Add the claude-in-mobile MCP server to your Claude Code configuration
2. Ensure an Android device/emulator is connected
3. Restart Claude Code
4. Re-invoke /ui-designer with Mode 3

Alternative: You can proceed with Mode 2 (Design + Implement) without the mobile MCP.
```

Ask user if they want to:
- Switch to Mode 2 (proceed without verification)
- Stop and configure mobile MCP first

---

## Step 0.4: Resolve Feature Context

Extract feature information from arguments, user's request, or ask for it.

### Detect Feature Name

**Priority order:**
1. **$ARGUMENTS** — If the user invoked `/ui-designer productdetail`, use `$ARGUMENTS` directly as the feature name
2. **Parse from prompt** — "design the product detail screen" → `productdetail`
3. **Ask the user** — If neither source provides a clear feature name, use `AskUserQuestion`:
   - What is the feature name? (lowercase, no hyphens: e.g., `productdetail`)

### Check Feature Existence

Use the `Glob` tool with pattern `feature/{featurename}/src/commonMain/kotlin/**/*.kt` to check if the feature exists.

| Result | Meaning | Action |
|--------|---------|--------|
| Files found | Existing feature | Load context from existing code |
| No matches | New feature | Note: data layer may need separate creation |

### Feature Status (for Modes 2 & 3)

If implementing, note the feature status for Phase 2 handoff:

| Feature Status | Implementation Skill | Notes |
|---------------|---------------------|-------|
| **Exists** | `/modifying-kmp-feature` | Will modify existing UI to match Stitch design |
| **Does not exist** | `/creating-kmp-feature` | Will create full feature with Stitch design as UI spec |

If the feature doesn't exist yet and Mode 2/3 is selected, inform the user:
```
Feature '{featurename}' doesn't exist yet. When we proceed to implementation,
/creating-kmp-feature will be invoked to create the complete feature
(data layer + UI + integration) with your Stitch design as the UI specification.

If you only need the UI layer, create the feature structure first with
/creating-kmp-feature, then use /ui-designer to redesign the UI.
```

Ask user how to proceed.

---

## Step 0.5: Set Up Stitch Project and Initialize stitch.json

Each feature gets its **own Stitch project** to isolate designs and avoid clutter (Stitch MCP has no delete screen API, so shared projects accumulate orphaned screens across iterations).

### Find or Create Per-Feature Project

1. **Load stitch.json**: Look for `.claude/docs/{featurename}/stitch.json` (see [stitch.json schema](../references/stitch-guide.md#stitchjson-schema) for format)
2. **If exists and has `projectId`**: Call `mcp__stitch__get_project` with `name` set to the stored `projectName` to verify it still exists. If valid, use it. Skip to "Create Docs Directory."
3. **If not exists or project invalid**: Create a new project:
   ```
   mcp__stitch__create_project
   ```
   Stitch auto-generates the project title. Store the returned `projectId` and `projectName` (format: `projects/{id}`).

### Create Docs Directory
```bash
mkdir -p .claude/docs/{featurename}/designs
mkdir -p .claude/docs/{featurename}/designs/device  # Mode 3 only
```

### Create Initial stitch.json

If stitch.json doesn't exist, create `.claude/docs/{featurename}/stitch.json` with the initial fields:

```json
{
  "projectId": "{projectId}",
  "projectName": "projects/{projectId}",
  "featureName": "{featurename}",
  "deviceType": "MOBILE",
  "modelId": "{GEMINI_3_PRO or GEMINI_3_FLASH}",
  "theme": {
    "defaultTheme": "{light|dark}",
    "primaryHex": "{primaryHex}",
    "paletteCustomized": "{true|false}"
  },
  "screens": {},
  "createdAt": "{ISO date}",
  "updatedAt": "{ISO date}"
}
```

If stitch.json already exists (reusing an existing project), update `updatedAt`.

---

## Output

After preflight completes, the following context is available:

```
Mode: {1|2|3}
Model ID: {GEMINI_3_PRO|GEMINI_3_FLASH}
Feature: {featurename}
Feature Exists: {yes|no}
Stitch Project ID: {projectId}
Stitch Project Name: {projectName}
Device Type: MOBILE
Docs Path: .claude/docs/{featurename}/
Designs Path: .claude/docs/{featurename}/designs/
Stitch Config: .claude/docs/{featurename}/stitch.json
Default Theme: {light|dark}
Primary Color: {primaryHex}
Palette Customized: {true|false}
```

Proceed to **Phase 1: Design in Stitch**.
