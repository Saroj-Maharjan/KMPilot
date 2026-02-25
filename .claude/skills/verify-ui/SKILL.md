---
description: Verify UI implementation against Stitch design using three-way HTML→Blueprint→Code token audit and desktop screenshots.
argument-hint: [feature-name]
allowed-tools: Task, Read, Write, Edit, Glob, Grep, Bash(mkdir *), Bash(ls *), Bash(curl *), Bash(rm *), Bash(./gradlew *), AskUserQuestion, mcp__stitch__get_screen, mcp__stitch__list_screens, mcp__stitch__get_project
---

# Verify UI

Verify that a feature's UI implementation matches the Stitch design at the token level. Produces desktop screenshots for visual reference and a three-way audit report.

**Architecture Reference:** @../_shared/patterns.md

## Prerequisites

- Feature implemented (build passes)
- Blueprint exists at `.claude/docs/{featurename}/designs/{featurename}_blueprint.md`
- stitch.json exists at `.claude/docs/{featurename}/stitch.json`

## Workflow

```
[USER INVOKES] → Preflight → Download HTML → Desktop Screenshots → Three-Way Audit → Present Results → Handle Mismatches → Cleanup → DONE
```

---

## Step 1: Preflight

1. **Parse feature name** from `$ARGUMENTS` or ask user
2. **Verify files exist**:
   - `.claude/docs/{featurename}/stitch.json` — load project ID, screen IDs, dimensions
   - `.claude/docs/{featurename}/designs/{featurename}_blueprint.md` — the blueprint
   - `feature/{featurename}/src/commonMain/kotlin/**/presentation/ui/` — implementation code
3. **Verify build passes**: `./gradlew :feature:{featurename}:assembleAndroidMain`

If any prerequisite is missing, stop and inform the user what's needed.

---

## Step 2: Download HTML from Stitch

Re-download HTML for each approved screen state using screen IDs from stitch.json:

1. For each state (success, loading, failed, empty if applicable):
   - Get screen ID from `stitch.json` (`screens.{key}.screenId` for success, `screens.{key}.stateScreenIds.{state}` for others)
   - Call `mcp__stitch__get_screen` with all 3 required params
   - Download HTML: `curl -sL -o /tmp/stitch_{featurename}_{state}.html {htmlCode.downloadUrl}`
   - Read the downloaded HTML file content

---

## Step 3: Generate Desktop Screenshots

Render the implemented screen headlessly via Compose Desktop's `ImageComposeScene` for human visual reference. This screenshot is **not scored** — it exists so the user can see what the implementation looks like.

### 3.1: Read screen dimensions

Load dimensions from `stitch.json` (`screens.{key}.dimensions.width` and `height` per state). These match the Stitch design pixel dimensions.

### 3.2: Generate verification test file

Create a temporary `desktopTest` file that renders `{Feature}ScreenRoot` with mock data:

```
feature/{name}/src/desktopTest/kotlin/{PKG_PATH}/{name}/verification/VerificationScreenshot.kt
```

The test must:
- Import `ImageComposeScene`, `Density`, `EncodedImageFormat` from Compose/Skia
- Use `@OptIn(ExperimentalComposeUiApi::class)`
- Render at Stitch dimensions with `Density(2f)` (Stitch uses 2x CSS pixels for mobile)
- Wrap content in `XTheme(darkTheme = {defaultTheme == "dark"})` — matching the app's theme
- Generate one test per state (Success, Loading, Failed, Empty if applicable)
- Output PNGs to `.claude/docs/{featurename}/designs/device/`

**Mock data**: Create minimal inline mock data that exercises all component variants. This is temporary code — deleted after verification.

**Output path**: Gradle sets `user.dir` to the module root (`feature/{name}/`). Navigate up to project root:
```kotlin
val projectRoot = File(System.getProperty("user.dir")).parentFile.parentFile
val outputDir = File(projectRoot, ".claude/docs/{featurename}/designs/device").also { it.mkdirs() }
```

### 3.3: Run the test

```bash
mkdir -p .claude/docs/{featurename}/designs/device
./gradlew :feature:{name}:desktopTest --tests "*VerificationScreenshot*"
```

Verify PNGs were created in `.claude/docs/{featurename}/designs/device/`.

### 3.4: Stitch Screenshots (Already Hi-Res)

Phase 1 always downloads full-resolution screenshots (with `=s0` suffix). Use the existing files directly for comparison:
- Success: `.claude/docs/{featurename}/designs/{featurename}.png`
- Loading: `.claude/docs/{featurename}/designs/{featurename}_loading.png`
- Failed: `.claude/docs/{featurename}/designs/{featurename}_failed.png`
- Empty: `.claude/docs/{featurename}/designs/{featurename}_empty.png` (if applicable)

---

## Step 4: Three-Way Token Audit

This is the **primary verification mechanism**. It compares every measurable design token across three sources:

1. **Stitch HTML** (ground truth — what the user approved)
2. **Blueprint** (intermediate translation — may have errors)
3. **Implementation code** (final output — may have errors)

### 4.1: Load all sources

- **HTML files**: Read from `/tmp/stitch_{featurename}_{state}.html`
- **Blueprint**: Read from `.claude/docs/{featurename}/designs/{featurename}_blueprint.md`
- **Code files**: Read all `.kt` files in `feature/{name}/src/commonMain/.../presentation/ui/` and `ui/components/`

### 4.2: Extract HTML values

For each HTML file, extract every Tailwind class and convert to its dp/sp/color value using the rules in the blueprint spec. Key extractions:

- **Spacing**: Every `p-{N}`, `px-{N}`, `py-{N}`, `pt-{N}`, `pb-{N}`, `gap-{N}`, `space-y-{N}`, `mb-{N}`, `size-{N}` → dp values
- **Typography**: Every `text-{size}`, `font-{weight}`, `tracking-{type}` → sp/weight/letterSpacing values
- **Colors**: Every `bg-{color}`, `text-{color}`, `border-{color}` with alpha variants (`/{N}`) → hex + alpha
- **Shapes**: Every `rounded-{size}` → dp (check `tailwind.config` for overrides)
- **Borders**: Every `border`, `border-{side}-{N}`, `divide-{axis}` → width + color
- **Shadows**: Every `shadow-{size}` → elevation dp
- **Layout**: `flex`/`flex-col`/`flex-row`, `items-{align}`, `justify-{align}` → Compose layout equivalents

### 4.3: Compare three-way

For each extracted value, check the blueprint and code for the corresponding value. Produce a report:

```markdown
## Three-Way Audit Report: {FeatureName}

### {ComponentName}

| Property | HTML (ground truth) | Blueprint | Code | Verdict |
|----------|-------------------|-----------|------|---------|
| top padding | pt-8 = 32dp | 32.dp | 8.dp | CODE MISMATCH |
| bottom padding | p-4 = 16dp | 8.dp | 8.dp | BLUEPRINT + CODE MISMATCH |
```

**Verdicts**:
- **OK** — all three match
- **CODE MISMATCH** — HTML and Blueprint agree, Code differs. Fix target: code.
- **BLUEPRINT MISMATCH** — HTML correct, Blueprint wrong, Code may match either. Fix target: code (use HTML value).
- **BLUEPRINT + CODE MISMATCH** — Both Blueprint and Code differ from HTML the same way. The blueprint mistranslated, code faithfully followed the wrong spec. Fix target: code (use HTML value).
- **ALL DIFFER** — all three have different values. Fix target: code (use HTML value).

### 4.4: Classify mismatches

Group mismatches by severity:

| Severity | Criteria | Action |
|----------|----------|--------|
| **Critical** | Spacing >=8dp off, wrong color role, missing component, wrong font size | Must fix |
| **Minor** | Spacing 1-4dp off, shadow omitted, letter-spacing off, decorative detail | Report to user, fix if requested |
| **Data-only** | Different mock data text/values | Ignore — not a code issue |

Save the audit report to `.claude/docs/{featurename}/designs/{featurename}_audit.md`.

---

## Step 5: Present Results

Show the user:

1. **Desktop screenshots** — read both the device PNGs and Stitch PNGs inline via `Read` for visual comparison
2. **Audit summary** — critical and minor mismatches count
3. **Full audit table** — all mismatches with verdicts

```
## Verification Report: {FeatureName}

### Desktop Screenshots (visual reference)
| State | Stitch Design | Desktop Render |
|-------|--------------|----------------|
| Success | designs/{featurename}.png | designs/device/desktop_{featurename}.png |
| Loading | designs/{featurename}_loading.png | designs/device/desktop_{featurename}_loading.png |
| Failed | designs/{featurename}_failed.png | designs/device/desktop_{featurename}_failed.png |

### Token Audit Results
- Critical mismatches: {N}
- Minor mismatches: {N}
- Total properties checked: {N}

{Full audit table}
```

---

## Step 6: Handle Mismatches

### If critical mismatches exist

1. **Save audit report** with fix instructions — for each critical mismatch, specify the exact file, current value, and correct value (from HTML)
2. **Inform the user** and recommend they invoke the implementation skill to fix:

```
Critical mismatches found. To fix them:

  /modifying-kmp-feature {featurename}

The audit report at .claude/docs/{featurename}/designs/{featurename}_audit.md
contains exact fix instructions for each mismatch. The implementation skill
will auto-detect this in design-aware mode.
```

**This skill does NOT invoke `/modifying-kmp-feature`** — the user controls the pipeline.

### If only minor mismatches exist

Present them to the user and ask:

| Option | Description |
|--------|-------------|
| Accept (Recommended) | Minor differences are acceptable |
| Fix all | Fix every minor mismatch (user should invoke implementation skill) |

---

## Step 7: Cleanup

After user confirms:

1. **Delete the verification test file**:
   ```
   feature/{name}/src/desktopTest/kotlin/{PKG_PATH}/{name}/verification/VerificationScreenshot.kt
   ```
   Remove the `verification/` directory if empty. Remove `desktopTest/` directory tree if empty.

2. **Delete HTML source files** (no longer needed after verification):
   ```bash
   rm -f /tmp/stitch_{featurename}_*.html
   ```

3. **Update stitch.json** with verification results:
   ```json
   {
     "verification": {
       "verified": true,
       "verifiedAt": "{date}",
       "auditReport": "designs/{featurename}_audit.md",
       "deviceScreenshots": {
         "{screen_name}": "designs/device/desktop_{screen_name}.png"
       },
       "attempts": {number}
     }
   }
   ```

4. Show completion report.

---

## Completion Report

```
## Verify UI Complete: {FeatureName}

### Audit Results
| State | Critical | Minor | Status |
|-------|----------|-------|--------|
| Success | {N} | {N} | {PASS/FAIL} |
| Loading | {N} | {N} | {PASS/FAIL} |
| Failed | {N} | {N} | {PASS/FAIL} |

Desktop screenshots: .claude/docs/{featurename}/designs/device/
Audit report: .claude/docs/{featurename}/designs/{featurename}_audit.md

{If critical mismatches: "To fix: /modifying-kmp-feature {featurename}"}
```
