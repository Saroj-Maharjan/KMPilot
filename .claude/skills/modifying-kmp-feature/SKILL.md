---
description: Modify existing KMP features with spec-first workflow. Invoke with /modifying-kmp-feature.
allowed-tools: ["Task", "Read", "Write", "Edit", "Glob", "Grep", "Bash(./gradlew:*)", "Bash(mkdir:*)", "Bash(touch:*)", "Bash(rm -f /tmp/.claude-kmpilot-skill-active)", "AskUserQuestion"]
---

# Modifying KMP Features

Apply changes to existing features using spec-first workflow.

**Architecture Reference:** @../_shared/patterns.md

## Hook Marker (Required)

Before editing any feature files, activate the skill marker so the PreToolUse hook allows edits:
```bash
touch /tmp/.claude-kmpilot-skill-active
```
After completion (or on any early exit), remove it:
```bash
rm -f /tmp/.claude-kmpilot-skill-active
```

## Workflow

**Parse** → **Spec Check** → **Design Artifact Detection** → **Understand** → **Plan** → **Draft Spec** → [USER APPROVES] → **Activate marker** → **Implement** → **Validate** → **Update Spec** → **Remove marker** → Done

### Step 1: Parse Feature Name
Extract from request: "add sorting to productlist" → `productlist`
Validate: `ls feature/{featurename}/src/commonMain/kotlin/`

### Step 2: Spec Check
Load `.claude/docs/{featurename}/spec.md`

If missing, **stop and instruct the user**:

```
No spec found for '{featurename}'. Please run /audit-spec {featurename} first
to generate one, then re-invoke /modifying-kmp-feature.
```

Do NOT auto-invoke `/audit-spec` — skills do not call each other; the user controls the pipeline.

### Step 2.5: Design Artifact Detection

Check for a Stitch design blueprint:

1. **Check blueprint exists**: `.claude/docs/{featurename}/designs/{featurename}_blueprint.md`
2. **Check stitch-project.json**: `.claude/docs/_project/stitch-project.json` — read `features[featurename].blueprintConsumed`
3. **Determine mode**:

| Blueprint exists? | `blueprintConsumed` | Mode |
|-------------------|---------------------|------|
| Yes | `false` | **Design-aware mode** — blueprint drives UI implementation |
| Yes | `true` | Normal mode — blueprint already consumed |
| No | N/A | Normal mode — no design artifact |

If entering **design-aware mode**, log:
```
Design artifact detected: .claude/docs/{featurename}/designs/{featurename}_blueprint.md
Entering design-aware mode. Blueprint will drive UI implementation.
```

### Step 3: Understand Current Implementation
Read spec sections: Requirements, Architecture, State Management, Navigation

### Step 4: Plan Changes
Determine affected layers and load architecture as needed:
- Data changes: @../creating-kmp-feature/architecture/data.md
- UI changes: @../creating-kmp-feature/architecture/ui.md
- Integration changes: @../creating-kmp-feature/architecture/integration.md

**Design-aware branch**: If in design-aware mode, read the blueprint's **Pre-Implementation Contract** section. Plan XTheme updates first (missing M3 roles from the contract's Color Audit). Include blueprint component tree in the UI plan.

### Step 5: Draft Spec Changes
Propose updates using diff format:
```markdown
## Proposed Spec Changes: {featurename}
**Current Version:** X.Y.Z → **Proposed:** X.Y+1.Z

### Section N: {Name}
```diff
  existing content
+ added content
- removed content
```
### Rationale
{Why these changes are needed}
```

### Step 6: Review Gate (REQUIRED)
Present changes to user with:
- [ ] **Approve** - Proceed with implementation
- [ ] **Modify** - Request changes
- [ ] **Reject** - Do not proceed

**Never skip this step.**

### Step 7: Implement Changes
Follow patterns from @../_shared/patterns.md

For UI changes: Load @../using-design-system/references/component-mappings.md

**UI file layout (strict allowlist)**: when adding or moving composables, respect the rules in `@../_shared/patterns.md` ("UI File Organization"):
- `{Feature}Screen.kt` accepts only the 5 allowlist names (`Screen`, `ScreenRoot`, and optionally `LoadingContent`/`FailedContent`/`EmptyContent`)
- Every other composable, including `{Feature}Content`, lives one-per-file under `presentation/ui/components/`
- Non-composable helpers live in `presentation/ui/{Feature}Utils.kt`, never under `components/`

**Previews (mandatory for new components)**: when this modification **adds a new component**, you must also:

1. **Check feature build.gradle.kts** for preview deps:
   ```kotlin
   sourceSets.commonMain.dependencies { implementation(libs.compose.ui.tooling.preview) }
   dependencies { androidRuntimeClasspath(libs.compose.ui.tooling) }
   ```
   If either is missing, add it as part of this modification.

2. **Generate a `@Preview` composable** in the same file as the new component, marked `private`, wrapped in `XTheme`, with realistic sample data. Use the canonical import `androidx.compose.ui.tooling.preview.Preview` (CMP 1.11.0+ — common). Never use the deprecated `org.jetbrains.compose.ui.tooling.preview.Preview`.

   See `@../creating-kmp-feature/architecture/ui.md` → "Previews" for the full pattern, including `@PreviewParameter` for multi-variant previews.

**Design-aware branch**: If in design-aware mode, implement in this order:
1. **XTheme update** — Add all missing M3 roles from the blueprint's Pre-Implementation Contract to **both** `XLightColors` and `XDarkColors` in `XTheme.kt`. Verify build: `./gradlew :core:designsystem:assembleAndroidMain`
2. **X-Component Constraint Check** — Collect the unique set of design system source files needed by the blueprint's Component Tree (one file may define many composables — e.g. `XButton.kt` defines `XButton`, `XOutlinedButton`, `XIconButton`, `XTextIconButton`, `XOutlinedIconButton`). Read each file in full and catalog **every composable defined in it**, not just the one the blueprint named. For each composable, extract:
   - `defaultMinSize` constraints (e.g. `XButton` enforces `minWidth=100.dp, minHeight=44.dp`)
   - Default parameter values that differ from what the blueprint intends (e.g. `XIconButton` defaults to a visible `surface` background)
   - Hardcoded internal padding that overrides `contentPadding` (e.g. `XTextField` hardcodes `top=10.dp, bottom=10.dp`)
   - Any internal `Modifier` applied via `.then(...)` that the caller cannot override

   Reading the whole file matters: the implementation may legitimately reach for a sibling composable in the same file (e.g. use `XOutlinedButton` instead of `XButton` for a pill), and you need its constraints too.

   For each conflict found, decide the resolution **before writing any code**:
   - Override via modifier: `Modifier.defaultMinSize(Dp.Unspecified)` to remove a min-size floor
   - Override via parameter: pass explicit `colors`, `shape`, or `contentPadding` to win over the default
   - Accept as architectural limitation: note it — do not fight it with hacks

3. **Component implementation** — Implement UI from the blueprint's Component Tree. Use the blueprint as the primary source, design screenshots as visual cross-reference only. Apply constraint resolutions from step 2.
4. **Post-Implementation Checklist** — Verify every item in the blueprint's Post-Implementation Checklist:
   - All XTheme missing roles added to BOTH schemes
   - Every component in blueprint exists in implementation
   - Every Modifier in blueprint is present in code
   - All colors use `MaterialTheme.colorScheme.{role}` — no raw `Color()` hex
   - Component override sizes/colors applied

### Step 8: Validate Build
```bash
./gradlew :feature:{featurename}:assembleAndroidMain
./gradlew :feature:{featurename}:ktlintFormat
```

### Step 9: Update Specification
Apply APPROVED changes (don't regenerate). Add changelog:
```markdown
## Last Updated
- {YYYY-MM-DD} - {Brief description}
```
Version bump: Patch (X.Y.Z+1) for fixes, Minor (X.Y+1.0) for features

**Design-aware branch**: Also:
- Add UI Design section to spec referencing the blueprint and design screenshots
- Set `"blueprintConsumed": true` in `.claude/docs/_project/stitch-project.json` under `features[{featurename}]`

## Error Handling

Build errors: Load @../creating-kmp-feature/troubleshooting/index.md
Design system: Activate `/using-design-system`

## Completion Checklist

- [ ] Spec changes drafted and USER APPROVED
- [ ] Build passes
- [ ] Code formatted (ktlint)
- [ ] Spec updated with approved changes
- [ ] Changelog entry added
- [ ] Version bumped
- [ ] (Design-aware) blueprintConsumed set to true in stitch-project.json
