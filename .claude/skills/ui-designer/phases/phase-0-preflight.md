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
```

Proceed to **Phase 1: Design in Stitch**.
