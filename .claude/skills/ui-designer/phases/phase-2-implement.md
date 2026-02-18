# Phase 2: Implementation

**Purpose**: Implement approved Stitch designs as production-ready code using the Compose Implementation Blueprint. The `ui-designer` skill does NOT edit feature files directly — it delegates to implementation skills.

**Prerequisites**: Phase 1 complete. All designs approved by user. Blueprint generated (Step 1.6.6).

---

## Checklist

```
Implementation Progress:
- [ ] Step 2.1: Update XTheme with missing M3 roles from Color Audit (MANDATORY)
- [ ] Step 2.2: Implement UI from blueprint
- [ ] Step 2.3: Confirm implementation completed
- [ ] Step 2.4: Handle change requests (if any)
```

---

## Step 2.1: Update XTheme with Missing M3 Roles (MANDATORY)

**Before any feature code is written**, add all missing M3 roles from the Color Audit (Phase 1 Step 1.6.5) to `lightColorScheme` in `XTheme.kt`.

### Procedure

1. **Read the Color Audit** from `.claude/docs/{featurename}/designs/{featurename}.md` (the "Missing M3 Roles" table)
2. **Read current `XTheme.kt`**: `core/designsystem/src/commonMain/kotlin/thisissadeghi/designsystem/XTheme.kt`
3. **Add every missing role** to the `lightColorScheme(...)` call with its hex value from the audit

Example — if the audit says `onSurface` (#1C1B1E) and `error` (#BA1A1A) are missing:

```kotlin
// Before
private val XColors =
    lightColorScheme(
        background = XTheme.Colors.PaleLavender,
        surface = Color.White,
        primary = Color(0xFFB02418),
    )

// After
private val XColors =
    lightColorScheme(
        background = XTheme.Colors.PaleLavender,
        surface = Color.White,
        primary = Color(0xFFB02418),
        onSurface = Color(0xFF1C1B1E),
        error = Color(0xFFBA1A1A),
    )
```

4. **If the audit has custom colors** (justified `XTheme.Colors.*` exceptions): Add them as extension properties in `Colors.kt` following the existing pattern
5. **Verify build**: `./gradlew :core:designsystem:assembleAndroidMain` to ensure the theme compiles

This step ensures that by the time feature code is written, every color it needs is available through `MaterialTheme.colorScheme.*`.

---

## Step 2.2: Implement UI from Blueprint

Invoke the appropriate implementation skill, passing the blueprint as the **primary source** and screenshots as visual cross-reference.

**For existing features** — invoke `/modifying-kmp-feature`:
```
Redesign the {featurename} feature UI to match the approved Stitch designs.

Implementation blueprint (primary source — contains exact component tree, design tokens, typography, spacing):
.claude/docs/{featurename}/designs/{featurename}_blueprint.md

Design screenshots (visual cross-reference only — use to verify layout/proportions):
- Success: .claude/docs/{featurename}/designs/{featurename}.png
- Loading: .claude/docs/{featurename}/designs/{featurename}_loading.png
- Failed: .claude/docs/{featurename}/designs/{featurename}_failed.png
- Empty: .claude/docs/{featurename}/designs/{featurename}_empty.png (if applicable)

Design spec: .claude/docs/{featurename}/designs/{featurename}.md

Architecture requirements:
- Use X-components exclusively (no Material3) — see core:designsystem
- Follow ScreenRoot pattern: FeatureScreen (ViewModel wrapper) + FeatureScreenRoot (testable)
- Handle all 4 UI states: Uninitialized / Loading / Success / Failed
- Use setState { copy() } for state updates, never _state.value =
- Use ImmutableList for collections in state
- Callbacks for navigation (onBackClick), not navController

Color requirements (STRICT — see m3-colors.md Color Rules):
- All missing M3 roles were already added to lightColorScheme in Step 2.1
- ALL colors MUST use MaterialTheme.colorScheme.{role} — never raw Color() hex values
- If a needed role is missing, STOP — add it to XTheme.kt first, then continue
```

**For new features** — invoke `/creating-kmp-feature` first to set up the data layer and integration points, then invoke `/modifying-kmp-feature` with the design context above for the UI layer.

---

## Step 2.3: Confirm Implementation Completed

After implementation finishes:

1. **Validate build**:
   ```bash
   ./gradlew :feature:{featurename}:assembleAndroidMain
   ./gradlew :feature:{featurename}:ktlintFormat
   ```

2. **Update stitch.json** with implementation status:

```json
{
  "implementation": {
    "implemented": true,
    "implementedAt": "{date}",
    "method": "blueprint"
  }
}
```

3. **Inform the user** that implementation is complete

**Mode 2** → Show completion report. Done.
**Mode 3** → Proceed to **Phase 3: Visual Verification**.

---

## Step 2.4: Handle Change Requests

If the user wants changes after seeing the implementation:

### Design-Level Changes (visual/layout)
1. **Go back to Stitch**: Use `mcp__stitch__edit_screens` to update the design
2. Present updated design to user for approval (Phase 1 iteration flow)
3. Re-generate the blueprint (Step 1.6.6) from updated HTML
4. Re-invoke `/modifying-kmp-feature` with updated blueprint + screenshots

### Code-Level Changes (implementation-specific)
1. Let the user invoke `/modifying-kmp-feature` directly for code-specific tweaks
2. These don't need to go through Stitch

### Iteration Loop
Maximum 5 design-level iterations. Code-level changes are handled entirely by the implementation skill.

---

## Output

After Phase 2 completes:
- UI implemented with design fidelity from Compose Implementation Blueprint
- Build passing + ktlint formatted
- stitch.json updated with implementation status
- User confirmed the result

**Mode 2** → Show Mode 2 completion report from SKILL.md. Done.
**Mode 3** → Proceed to **Phase 3: Visual Verification**.
