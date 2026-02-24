# Phase 3: Verification

**Purpose**: Verify that the implementation code matches the Stitch HTML design at the token level (every dp, sp, color, shape, modifier). Generate a desktop screenshot for human visual reference.

**Prerequisites**: Phase 2 complete. Implementation finished. HTML files saved at `.claude/docs/{featurename}/designs/html/` (from Phase 1 Step 1.6.6).

---

## Checklist

```
Verification Progress:
- [ ] Step 3.1: Generate desktop screenshot (human reference)
- [ ] Step 3.2: Three-way HTML → Blueprint → Code audit (primary verification)
- [ ] Step 3.3: Present results to user
- [ ] Step 3.4: Handle mismatches (if any)
- [ ] Step 3.5: User final confirmation
- [ ] Step 3.6: Cleanup
```

---

## Step 3.1: Generate Desktop Screenshot

Render the implemented screen headlessly via Compose Desktop's `ImageComposeScene` for human visual reference. This screenshot is **not scored** — it exists so the user can see what the implementation looks like.

### 3.1.1: Read screen dimensions

Load dimensions from `stitch.json` (`screens.{key}.dimensions.width` and `height` per state). These match the Stitch design pixel dimensions.

### 3.1.2: Generate verification test file

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

**Mock data**: Create minimal inline mock data that exercises all component variants (e.g., over-budget items, positive/negative changes, overdue bills). This is temporary code — deleted after verification.

**Output path**: Gradle sets `user.dir` to the module root (`feature/{name}/`). Navigate up to project root:
```kotlin
val projectRoot = File(System.getProperty("user.dir")).parentFile.parentFile
val outputDir = File(projectRoot, ".claude/docs/{featurename}/designs/device").also { it.mkdirs() }
```

### 3.1.3: Run the test

```bash
./gradlew :feature:{name}:desktopTest --tests "*VerificationScreenshot*"
```

Verify PNGs were created in `.claude/docs/{featurename}/designs/device/`.

### 3.1.4: Stitch Screenshots (Already Hi-Res)

Phase 1 always downloads full-resolution screenshots (with `=s0` suffix). No additional download is needed — use the existing Phase 1 files directly for comparison:
- Success: `.claude/docs/{featurename}/designs/{featurename}.png`
- Loading: `.claude/docs/{featurename}/designs/{featurename}_loading.png`
- Failed: `.claude/docs/{featurename}/designs/{featurename}_failed.png`
- Empty: `.claude/docs/{featurename}/designs/{featurename}_empty.png` (if applicable)

---

## Step 3.2: Three-Way Token Audit (Primary Verification)

This is the **primary verification mechanism**. It compares every measurable design token across three sources:

1. **Stitch HTML** (ground truth — what the user approved)
2. **Blueprint** (intermediate translation — may have errors)
3. **Implementation code** (final output — may have errors)

### 3.2.1: Load all sources

- **HTML files**: Read from `.claude/docs/{featurename}/designs/html/stitch_{featurename}_{state}.html`
- **Blueprint**: Read from `.claude/docs/{featurename}/designs/{featurename}_blueprint.md`
- **Code files**: Read all `.kt` files in `feature/{name}/src/commonMain/.../presentation/ui/` and `ui/components/`

### 3.2.2: Extract HTML values

For each HTML file, extract every Tailwind class and convert to its dp/sp/color value using the rules in [blueprint-spec.md](../references/blueprint-spec.md). Key extractions:

- **Spacing**: Every `p-{N}`, `px-{N}`, `py-{N}`, `pt-{N}`, `pb-{N}`, `gap-{N}`, `space-y-{N}`, `mb-{N}`, `size-{N}` → dp values
- **Typography**: Every `text-{size}`, `font-{weight}`, `tracking-{type}` → sp/weight/letterSpacing values
- **Colors**: Every `bg-{color}`, `text-{color}`, `border-{color}` with alpha variants (`/{N}`) → hex + alpha
- **Shapes**: Every `rounded-{size}` → dp (check `tailwind.config` for overrides)
- **Borders**: Every `border`, `border-{side}-{N}`, `divide-{axis}` → width + color
- **Shadows**: Every `shadow-{size}` → elevation dp
- **Layout**: `flex`/`flex-col`/`flex-row`, `items-{align}`, `justify-{align}` → Compose layout equivalents

### 3.2.3: Compare three-way

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

### 3.2.4: Classify mismatches

Group mismatches by severity:

| Severity | Criteria | Action |
|----------|----------|--------|
| **Critical** | Spacing ≥8dp off, wrong color role, missing component, wrong font size | Must fix |
| **Minor** | Spacing 1-4dp off, shadow omitted, letter-spacing off, decorative detail | Report to user, fix if requested |
| **Data-only** | Different mock data text/values | Ignore — not a code issue |

Save the audit report to `.claude/docs/{featurename}/designs/{featurename}_audit.md`.

---

## Step 3.3: Present Results to User

Show the user:

1. **Desktop screenshots** — read both the device PNGs and Stitch PNGs inline via `Read` for visual comparison (this is the validation step — reading images here is intentional)
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

## Step 3.4: Handle Mismatches

### If critical mismatches exist

1. **Prepare fix instructions** from the audit report — for each critical mismatch, specify the exact file, current value, and correct value (from HTML)
2. **Re-invoke `/modifying-kmp-feature`** with fix context:
   ```
   Fix UI token mismatches in feature '{featurename}' to match the Stitch HTML design.

   Audit report: .claude/docs/{featurename}/designs/{featurename}_audit.md
   See the "Critical" mismatches — each has the exact file, property, current value, and correct value.

   HTML source files (ground truth): .claude/docs/{featurename}/designs/html/
   ```
3. After implementation completes, **re-run Steps 3.1-3.2** (re-render screenshots, re-audit)
4. Maximum **3 fix attempts**. After 3 attempts, present remaining mismatches to user and ask how to proceed.

### If only minor mismatches exist

Present them to the user and ask:

| Option | Description |
|--------|-------------|
| Accept (Recommended) | Minor differences are acceptable |
| Fix all | Fix every minor mismatch |
| Fix selected | User picks which ones to fix |

---

## Step 3.5: User Final Confirmation

Present verification results:

```
## Verification Complete: {FeatureName}

### Results
| State | Critical | Minor | Status |
|-------|----------|-------|--------|
| Success | 0 | {N} | PASS |
| Loading | 0 | {N} | PASS |
| Failed | 0 | {N} | PASS |

### Screenshots
Desktop renders saved to: .claude/docs/{featurename}/designs/device/

### Audit Report
Full report: .claude/docs/{featurename}/designs/{featurename}_audit.md

Would you like to:
1. Confirm and complete
2. Make additional adjustments
3. Redesign in Stitch and re-implement
```

---

## Step 3.6: Cleanup

After user confirms:

1. **Delete the verification test file**:
   ```
   feature/{name}/src/desktopTest/kotlin/{PKG_PATH}/{name}/verification/VerificationScreenshot.kt
   ```
   Remove the `verification/` directory if empty. Remove `desktopTest/` directory tree if empty.

2. **Delete HTML source files** (no longer needed after verification):
   ```bash
   rm -rf .claude/docs/{featurename}/designs/html/
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

4. Show final completion report (Mode 3 from SKILL.md)

---

## Output

After Phase 3 completes:
- Desktop screenshots saved for reference
- Three-way audit completed (0 critical mismatches or user-accepted)
- Audit report saved
- HTML files and temp test cleaned up
- stitch.json updated with verification data
- User confirmed completion

**Done.** Show Mode 3 completion report from SKILL.md.
