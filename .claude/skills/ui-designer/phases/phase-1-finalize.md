# Phase 1 (cont.): Finalize — Save Design Description + Steps 1.15–1.19

> Continuation of [phase-1-design.md](phase-1-design.md) — same step numbering. **Always read this file after Step 1.13 (and Step 1.14 when states were selected).** Everything here runs regardless of which optional states were selected — state-conditional sub-steps carry their own `needs*` gates.

---

## Step 1.14 — Save Design Description (ALWAYS runs)

> Closing sub-step of Step 1.14. It is **not** gated on any `needs*` flag — run it even when every optional state was skipped.

Save `.claude/docs/{featurename}/designs/{featurename}.md`. The state rows are conditional on selections:

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
{if needsLoading} - Loading: `.claude/docs/_shared/designs/loading.png` (shared)
{if needsFailed}  - Failed: `.claude/docs/_shared/designs/failed.png` (shared)
{if needsEmpty}   - Empty: `{featurename}_empty.png`
```

Only emit a screenshot line for a state if the corresponding `needs*` flag is true. Do NOT list skipped states with a "(skipped)" placeholder — omit them entirely.

---

## Step 1.15: Acquire HTML & Token Inventories (Selected States Only)

Acquire HTML + token inventory for each **selected** state. Success is always acquired; loading/failed/empty only when their `needs*` flag is true.

| State | Condition | HTML source | Tokens source |
|-------|-----------|-------------|---------------|
| success | Always | Download from `features[featurename].successScreenId` | Extract per-feature |
| loading | `needsLoading` | Read from `_shared/designs/extracted/stitch_loading.html` | Read from `_shared/designs/extracted/tokens_loading.md` |
| failed | `needsFailed` | Read from `_shared/designs/extracted/stitch_failed.html` | Read from `_shared/designs/extracted/tokens_failed.md` |
| empty | `needsEmpty` | Download from `features[featurename].emptyScreenId` | Extract per-feature |

Skipped states are not processed in any sub-step below.

### Resume Check (Partial Failure Recovery)

Before downloading or tokenizing, check `.claude/docs/{featurename}/designs/extracted/` for existing files. For each state, only run the procedure below if **either** of these is missing:

- `stitch_{state}.html`
- `tokens_{state}.md`

If both exist for a state, skip it — the prior run's snapshot is the canonical source of truth and re-fetching the HTML may fail due to Stitch's one-time URLs.

### Procedure

1. **Create extraction directory:**
   ```bash
   mkdir -p .claude/docs/{featurename}/designs/extracted
   ```

2. **For loading and failed states (only if `needsLoading` / `needsFailed`) — no action needed.** Read directly from:
   - `.claude/docs/_shared/designs/extracted/stitch_loading.html` (only if `needsLoading`)
   - `.claude/docs/_shared/designs/extracted/tokens_loading.md` (only if `needsLoading`)
   - `.claude/docs/_shared/designs/extracted/stitch_failed.html` (only if `needsFailed`)
   - `.claude/docs/_shared/designs/extracted/tokens_failed.md` (only if `needsFailed`)

3. **For success and empty states — download from Stitch.** **Sequentially** (concurrent downloads can race the URL's single-use semantics). Success is always downloaded. Empty is downloaded **only if `needsEmpty == true`**:
   - screenId source:
     - success → `stitch-project.json.features[featurename].successScreenId`
     - empty → `stitch-project.json.features[featurename].emptyScreenId` (skip this entire line if `needsEmpty == false`)
   - Call `get_screen` for each screenId (see [Get Screen Call Pattern](../references/stitch-guide.md#get-screen-call-pattern)) and use `htmlCode.downloadUrl`, `width`, `height` from the response.
     ```
     projectId = stitch-project.json.projectId   ← always the shared project
     name      = "projects/{projectId}/screens/{screenId}"
     screenId  = {screenId}
     ```
   - Download: `curl -sL -o .claude/docs/{featurename}/designs/extracted/stitch_{state}.html {htmlCode.downloadUrl}`
   - Verify with `wc -c …` — if 0 bytes, call `mcp__stitch__get_screen` again to get a fresh URL and retry the curl once.
   - Record the screen dimensions (`width`, `height`) from the `get_screen` response.

4. **Tokenize success (always) and empty (only if `needsEmpty == true`)** with the shared extractor:
   ```bash
   python3 .claude/skills/_shared/extract_tokens.py \
     .claude/docs/{featurename}/designs/extracted/stitch_{state}.html \
     > .claude/docs/{featurename}/designs/extracted/tokens_{state}.md
   ```
   These inventories are the canonical, deterministic Tailwind→Compose conversion (spacing, font-size, colors with opacity, custom border-radius config, arbitrary values). Same script `/verify-ui` runs at audit time, so blueprint values and audit values come from the same source by construction.

   Do NOT re-tokenize loading/failed — those were already tokenized at Project Init time and live in `_shared/`.

5. **Generate the Material Symbols icons manifest** for every `<span class="material-symbols-*" data-icon="...">` found in the selected-state HTML files. `/ui-designer` is a **design-only** skill — it does NOT download XML files or mutate any source code. It only writes the declarative `icons.json` manifest that downstream implementation skills consume.

   ```bash
   python3 .claude/skills/_shared/download_assets.py \
     --type icons \
     --feature {featurename} \
     --project-root {repo_root} \
     --html .claude/docs/{featurename}/designs/extracted/stitch_success.html \
     [--html .claude/docs/_shared/designs/extracted/stitch_loading.html  if needsLoading] \
     [--html .claude/docs/_shared/designs/extracted/stitch_failed.html   if needsFailed] \
     [--html .claude/docs/{featurename}/designs/extracted/stitch_empty.html if needsEmpty] \
     --manifest-only
   ```

   The `--manifest-only` flag is **mandatory** in `/ui-designer`. Without it the script would download XML files and edit `DesignSystemResources.kt` — both source-tree mutations that belong to `/creating-kmp-feature` or `/modifying-kmp-feature` (which activate the protect-feature-files hook marker before mutating source). Running the script without `--manifest-only` from within `/ui-designer` violates the blueprint-artifact-contract.

   - Pass `--html` once per **selected** state (success always; loading/failed/empty per their `needs*` flag — same selection logic as the tokenizer above).
   - **Placement algorithm (usage-based, declarative)**: the manifest classifies each icon as chrome or domain based on cross-feature usage. The script scans every existing `.claude/docs/*/designs/extracted/icons.json` manifest, computes `users = {features that declare this icon} ∪ {current feature}`, and classifies `len(users) >= 2` → **chrome**, otherwise **domain**. The chrome icon's predicted path is `core/designsystem/.../composeResources/drawable/{ident}.xml`; the domain icon's is `feature/{featurename}/.../composeResources/drawable/{ident}.xml`. The implementing skill materializes the XML at the declared path.
   - **Doc-artifact promotion** is allowed and automatic in manifest-only mode: when adding a feature that pushes an icon from 1 → 2 users, every affected other feature's `icons.json` is updated in place to reflect the new chrome scope. These are all artifacts under `.claude/docs/` — never source.
   - **No source mutation**: the script never writes to `feature/*/composeResources/drawable/`, never edits `DesignSystemResources.kt`, never rewrites any `.kt` import. Those happen later in `/creating-kmp-feature` (or `/modifying-kmp-feature`) when the marker is active.
   - **Manifest fields** for each icon: `name`, `style`, `filled`, `scope`, `drawable_name`, predicted `drawable_path`, predicted `res_reference`, `source_url`, `download_status: "pending"`, `usage_count`, `users`, `occurrences`. Step 1.17 feeds this manifest to the blueprint generator so icon references resolve correctly.
   - Non-outlined styles (rounded, sharp) emit a warning — Stitch defaults to outlined, so divergence usually signals a design intent worth noting.
   - **Completeness check (MANDATORY after running)**: count all `<span class="material-symbols-*">` occurrences in each selected-state HTML file and compare against the icons generated in `icons.json`. If any span is absent from the manifest (script missed it due to HTML format variance — e.g. icon name as text content instead of `data-icon` attribute), add it manually to `icons.json` with `download_status: "pending"`. An icon absent from `icons.json` WILL produce a blueprint with `Icons.Default.*` fallback — that is always wrong.

   **Materialization happens later, in the implementation skill.** When the user runs `/creating-kmp-feature {featurename}` or `/modifying-kmp-feature {featurename}` in design-aware mode, it invokes the same script **without** `--manifest-only`. That run downloads each XML to its declared path, applies the JetBrains-required KMP cleanup pass (strip `android:tint`, `android:autoMirrored`, and translate **every** `@android:color/*` reference — in any color attribute — to its literal ARGB hex, since the `@android:color` namespace is unresolved by the KMP resource pipeline and crashes non-Android targets at runtime), extends `DesignSystemResources.kt` for any chrome additions, and inline-migrates any stale `feature/X/drawable/{ident}.xml` copies plus their Kotlin imports for icons whose scope flipped.

6. **Generate the `<img>` assets manifest** for every `<img>` tag found in the selected-state HTML files. Stitch images are all hosted on its CDN (`lh3.googleusercontent.com/aida-public/...`). The script classifies each into a **`delivery`**: **`bundled`** (a static design asset — hero, decorative background, logo → downloaded + bundled, rendered via `painterResource`) or **`remote`** (dynamic content — avatar, flag, thumbnail, repeated list image → NOT bundled, rendered at runtime via `AsyncImage(url = <data field>)`; the Stitch CDN URL is an ephemeral placeholder that never ships).

   `/ui-designer` is design-only — it writes only the declarative manifest. Downloads (bundled only) happen later in `/creating-kmp-feature` or `/modifying-kmp-feature`.

   ```bash
   python3 .claude/skills/_shared/download_assets.py \
     --type images \
     --feature {featurename} \
     --project-root {repo_root} \
     --html .claude/docs/{featurename}/designs/extracted/stitch_success.html \
     [--html .claude/docs/_shared/designs/extracted/stitch_loading.html  if needsLoading] \
     [--html .claude/docs/_shared/designs/extracted/stitch_failed.html   if needsFailed] \
     [--html .claude/docs/{featurename}/designs/extracted/stitch_empty.html if needsEmpty] \
     --manifest-only
   ```

   The `--manifest-only` flag is **mandatory** in `/ui-designer` (same rule as for `--type icons`).

   - **Filename derivation**: `{state}_{role}[_idx]` — state from the input HTML filename; role inferred from CSS context (e.g., `absolute + pointer-events-none + opacity-low → background`; `rounded-full + small width → avatar`; `aspect-square + small → thumbnail`; `w-full + tall → hero`; otherwise `image`). HTML comments adjacent to the `<img>` (Stitch labels like `<!-- Decorative Image ... -->`) override CSS heuristics when present.
   - **Delivery classification (heuristic)**: `remote` if role ∈ {avatar, thumbnail}, OR the image repeats ≥2× with the same CSS class signature (a data-bound collection — e.g. a row of country flags), OR its alt reads like an entity name; `bundled` for `background`/`hero`; otherwise `bundled` at **low confidence** (ambiguous → must be confirmed below). Remote entries also get a suggested `data_binding` field name and `placeholder_ref: DesignSystemResources.drawable.ds_image_placeholder`.
   - **Placement (bundled only, usage-based, identical to icons)**: cross-feature scan of all `images.json` manifests; `len(users) >= 2 → chrome` (placed in `core/designsystem/.../composeResources/drawable/`); else **domain** (feature's own dir). Remote images have no placement (`scope: "remote"`, no download, no `DesignSystemResources` entry).
   - **No KMP cleanup pass** needed (raster images don't have Android-specific attributes to scrub).
   - **Extension** is left as `unknown` in manifest-only mode for bundled images; full-mode downloads set it from the response's `Content-Type` header (PNG / JPEG / WebP). Remote images have `extension: "n/a"`.

   **6a. Confirm the delivery split with the user (MANDATORY).** After the script writes the manifest, read `images.json` and **print the proposed split as a table** (one row per image: `drawable_name · role · alt · delivery · delivery_reason`, grouped into BUNDLED and REMOTE) so the user sees the full picture — the bundle-vs-remote call is the one semantic judgment the heuristic can get wrong. Then confirm via **`AskUserQuestion`**, sized to the design (its options cap is 4 per question, so do **not** emit one option per image for large designs):
   - **If any `delivery_confidence: "low"` entries exist** — ask about *those only* (they are the genuinely uncertain ones; the high-confidence picks are stated in the table as the default). multiSelect, pre-checked at the heuristic's guess, ≤4 options per question; if more than 4 are low-confidence, batch across additional questions (max 4 questions/call). Question: *"These images are ambiguous — tick the ones that load **dynamically at runtime** (AsyncImage from data); unticked = bundled static asset."*
   - **If there are no low-confidence entries** — ask a single confirm: *"Accept this image delivery split?"* options `["Accept as shown", "Let me adjust"]`; on *Let me adjust* (or via the always-present "Other"), the user names which to flip in free text.

   Then **rewrite `images.json`** to reflect the confirmed answers: set each entry's `delivery` to the user's choice, set `delivery_locked: true` on **every** entry (confident + adjusted alike — it records that the split was user-confirmed), and for any `remote` add `data_binding` (suggest from role/alt — e.g. `flagUrl`, `avatarUrl`) + `compose_hint: "AsyncImage"` + `placeholder_ref: DesignSystemResources.drawable.ds_image_placeholder`; for any flipped to `bundled`, drop those remote-only fields. `delivery_locked: true` tells the implementation skill's full-mode run to honor the user's decision instead of re-deriving it.
   - **Blueprint emission** follows `delivery` (blueprint-spec rules 13 / 21b): `bundled` → `Image(painter = painterResource({res_reference}))`; `remote` → `AsyncImage(url = {data_binding}, loadingResId = DesignSystemResources.drawable.ds_image_placeholder, …)` + a Post-Implementation Checklist item to wire `{data_binding}` to the data layer.

6b. **Generate the font manifest** for the design typeface. Stitch embeds the text font as a Google Fonts `<link href="https://fonts.googleapis.com/css2?family=…">` plus `font-family: '…'` and a tailwind `fontFamily` config — all present in the success-state HTML. `/ui-designer` is design-only: it writes only the declarative `fonts.json` manifest; the actual `.ttf` download + `XTheme.kt` rewire happen later in the implementation skill.

   ```bash
   python3 .claude/skills/_shared/download_font.py \
     --project-root {repo_root} \
     --html .claude/docs/{featurename}/designs/extracted/stitch_success.html \
     --manifest .claude/docs/{featurename}/designs/extracted/fonts.json \
     --manifest-only
   ```

   - `--manifest-only` is **mandatory** in `/ui-designer` — no source mutation, no `.ttf` download. (Unlike icons/images, it does make one **read-only** lookup to the font source — the github contents API / css2 CSS — to classify the route and predict filenames; it never writes a font file or touches source.)
   - The script parses the css2 `<link>` out of the HTML (ignoring the `Material+Symbols` link), derives the family + weights, resolves the bundle-ready source (full variable `.ttf` from `github.com/google/fonts`, else the css2 TrueType subsets), and records `family`, `slug`, `route`, `weights`, `css_url`, predicted `files`, `res_accessors`, and the exact `font_family_lines` to wire into `XTypography`.
   - If the design has no css2 text-font link (rare), pass `--font "{FamilyName}"` instead, reading the family from the `font-family`/tailwind `fontFamily` config.
   - This manifest feeds the Step 1.16 Typography Audit (match vs swap) and the Step 1.17 blueprint generator.

7. **Do not delete.** The HTML, token inventories, `icons.json`, `images.json`, and `fonts.json` manifests live in `extracted/` from now on. The downloaded XMLs and image assets live under `composeResources/drawable/` in the appropriate module **after the implementation skill materializes them**. `/verify-ui` will detect and reuse all of these — see [verify-ui Step 2](../../verify-ui/SKILL.md) for the reuse contract.

---

## Step 1.16: Color, Typography & Motion Audit (MANDATORY)

This step runs three audits that map design tokens to app-global constructs: the **Color Audit** (colors → M3 color roles) below, then the **Typography Audit** (type → M3 type-scale roles + the font family), then the **Motion Audit** (animation → the 4 kept families + Compose primitives) at the end.

Audit every color used across the **selected** approved designs and map them to M3 roles. Color values are read from the **token inventories produced in Step 1.15**, not from prompts — Stitch can generate hex values that drift from what the prompt asked for, and the inventory is what `/verify-ui` will see.

Only audit token inventories for **selected** states (`needsLoading`/`needsFailed`/`needsEmpty`). Skipped states contribute zero colors. Success is always audited.

For any color found in `tokens_loading.md` or `tokens_failed.md` (only if those states are selected), annotate it with "(shared state screen)" in the audit output to make clear it originates from the shared Project Init screens.

### Procedure

1. **Re-read `XTheme.kt`** to get the current roles from the active scheme (`XLightColors` or `XDarkColors` per `defaultTheme`).

2. **Collect every color from the inventories** in `.claude/docs/{featurename}/designs/extracted/tokens_*.md` for the **selected** states only (always success; loading/failed/empty per their flags — loading/failed inventories live under `.claude/docs/_shared/designs/extracted/`). The extractor resolves each color class to its hex (custom Tailwind config + default palette + arbitrary values), so iterate through every inventory entry whose conversion contains a color.

3. **Reconcile against prompts.** Compare the inventory hexes against the "Defined" / "Proposed" hexes you specified in the Stitch prompts (Steps 1.10 and 1.14). If a color drifted (e.g., prompt asked for `#181228`, Stitch produced `#1A1A1F`), **the inventory wins** — record the inventory hex. Flag any drift in a single line at the top of the Color Audit so it's visible to the user.

4. **Component visual properties from the inventories.** For every component in the design, extract two things and flag any divergence in a "Component Overrides" section:

   - **Colors per visual state**: For each visual state of each component, look up the M3 role in `XTheme.kt`. If the role's hex matches the inventory hex → use the role. If it diverges → record an explicit color override.
   - **Sizing**: For each component, read the dp values from the inventory and compare to the X-component's actual rendered default in [`X_COMPONENTS_CATALOG.md`](../../_shared/X_COMPONENTS_CATALOG.md). If they differ → record an explicit size override.

5. **Map each color to an M3 role** using the [Complete M3 Role Catalog](../references/m3-colors.md#complete-m3-role-catalog).

6. **Classify each role**:
   - **Defined**: Already in the active scheme in `XTheme.kt`
   - **Missing**: Used in the design but not yet defined — must be added to **both** `XLightColors` and `XDarkColors` before implementation (Phase 2 Step 2.1 handles this)
   - **Custom**: Cannot map to any M3 role (gradients, decorative) — will use `XTheme.Colors.*` extension (must justify)

### Color Audit Output

Write the Color Audit section into the design description file (`.claude/docs/{featurename}/designs/{featurename}.md`). The section is delimited by HTML comment markers so re-runs (after edits, regenerations, or partial-failure recovery) **replace** the prior audit instead of stacking duplicates:

```markdown
<!-- COLOR_AUDIT:BEGIN -->
## Color Audit

Default theme for design: {light|dark}

> **Prompt drift**: {N} colors differ from the Stitch prompt — inventory values used. (Omit this line if N = 0.)

### Defined M3 Roles (already in active scheme — XLightColors or XDarkColors)
| Role | Hex (inventory) | Usage in Design |
|------|-----------------|-----------------|
| {role} | {hex} | {usage} |

### Missing M3 Roles (must add to BOTH XLightColors and XDarkColors before implementation)
| Role | Active Scheme Hex | Counterpart Scheme Hex | Usage in Design |
|------|-------------------|----------------------|-----------------|
| {role} | {hex for defaultTheme} | {derived counterpart hex} | {usage} |

### Custom Colors (XTheme.Colors.* — justified exceptions only)
| Name | Hex | Justification |
|------|-----|---------------|
| (none expected — only if needed) | | |
<!-- COLOR_AUDIT:END -->
```

**Write procedure**: If `<!-- COLOR_AUDIT:BEGIN -->` already exists in the file, replace everything from that marker through `<!-- COLOR_AUDIT:END -->` (inclusive) with the new audit. Otherwise append the entire block (markers included) to the end of the file. Never let two `COLOR_AUDIT` blocks coexist.

This audit is the input for Phase 2, where missing roles are added to **both** `XLightColors` and `XDarkColors` in `XTheme.kt` before any feature code is written.

### Typography Audit (MANDATORY)

Typography is app-global, exactly like color roles (see `_shared/patterns.md` → "Typography"). This audit does for type what the Color Audit does for fills: map every text node to an **M3 type-scale role**, and decide whether the design's typeface requires a one-time global **font swap**.

#### Procedure

1. **Read the design typeface** from `fonts.json` (Step 1.15 sub-step 6b) — `family`, `weights`, `route`, `font_family_lines`.
2. **Read the theme's current font**: open `XTheme.kt`, find `XFontFamily()`, note the family the `Font(Res.font.*)` resources belong to (e.g. `outfit_*` → Outfit).
3. **Map each text node to an M3 role**: from the token inventories' `font-size`/weight per node, pick the closest M3 type-scale role (`displayLarge`…`labelSmall`). Record size/weight divergences that would need an explicit `.copy(...)` override.
4. **Classify the font**: `matches current` (design family == theme family) → no swap; `swap required` (differs, e.g. design=Manrope vs theme=Outfit) → record the swap with the source from `fonts.json`.

#### Typography Audit Output

Write into the design description file (`.claude/docs/{featurename}/designs/{featurename}.md`), delimited so re-runs replace rather than stack:

```markdown
<!-- TYPOGRAPHY_AUDIT:BEGIN -->
## Typography Audit

**Design typeface**: {family} ({route}; weights {weights})
**Theme font**: {current family from XFontFamily} — **{matches current | swap required}**

> **Font swap**: design uses {family}, theme ships {current}. Source: {css_url or family}. (Omit this line when matches.)

### Text node → M3 role
| Node (usage) | M3 Role | Measured (size/weight) | Override needed? |
|--------------|---------|------------------------|------------------|
| {usage} | {role} | {e.g. 24sp / 700} | {no | yes — `.copy(fontWeight = Bold)`} |
<!-- TYPOGRAPHY_AUDIT:END -->
```

**Write procedure**: same marker-replace rule as the Color Audit — replace any existing `TYPOGRAPHY_AUDIT` block, never stack two.

This audit feeds the blueprint's **Typography Updates Required** (font swap + role overrides) in Step 1.17, and is materialized by the implementation skill (`download_font.py` + `XTypography` rewire) before any feature code is written.

### Motion Audit (MANDATORY)

`/ui-designer` is **capture-only** for motion — no prompt injection, no intensity question. This audit reads the animation the design already contains and maps it to Compose. Do **not** restate the policy or the family→primitive mapping — they live in [`_shared/motion.md`](../../_shared/motion.md); cite it.

#### Procedure

1. **Read the captured motion**: the `## Motion Inventory` section in each selected state's `tokens_{state}.md` (Step 1.15), plus the raw HTML's `<style>` / tailwind `animation` config / `<script>` if a token needs disambiguation.
2. **Bucket every token via the Web-Motion Policy** (`_shared/motion.md`): **DROP** touch press (`active:*`, `ripple`) and pointer/hover (`hover:*`, `group-hover:*`, `.interactive-card:hover/:active`, `focus:`, `cursor-*`); **KEEP** the 4 families (Ambient bg, Loading/Attention loop, Entrance, Value-driven) + the `prefers-reduced-motion` honor.
3. **Map each KEEP token** to a concrete Compose primitive + params (dur/easing/repeat/trigger) + **magnitude** (copied verbatim from the inventory's `### Keyframe magnitudes` — the only source for scale/translate/opacity/offset amounts; never invent) + target file (generic, reusable → DS `motion/`; one-off, feature-specific → feature `motion/{Feature}Motion.kt`), using motion.md's mapping + easing map. Durations/easings reference `XMotion` tokens.
4. **End with the explicit Dropped line** listing every dropped class/element for transparency.

#### Motion Audit Output

Write into the design description file (`.claude/docs/{featurename}/designs/{featurename}.md`), delimited so re-runs replace rather than stack (same marker-replace rule as Color/Typography):

```markdown
<!-- MOTION_AUDIT:BEGIN -->
## Motion Audit

**Motion present**: {yes | no — static design}

> If no motion: write the line above and the Dropped line (empty), nothing else.

### Kept motion → Compose
| Element | Family | Compose primitive | Params (dur/easing/repeat/trigger) | Magnitude | Target file |
|---------|--------|-------------------|------------------------------------|-----------|-------------|
| {element} | {family} | {primitive} | {params} | {value range from inventory} | {DS motion/ | feature motion/} |

**Reduced motion**: all kept rows gated by `rememberReducedMotion()` (DS `XMotion.kt` — `expect/actual`, reads OS setting). Durations/easings via `XMotion` tokens, not ad-hoc `tween(<literal>)`.

**Dropped (interaction + web-only)**: {comma-separated classes/elements, or "none"}
<!-- MOTION_AUDIT:END -->
```

**Write procedure**: replace any existing `MOTION_AUDIT` block; never stack two.

This audit feeds the blueprint's **`## Motion`** table in Step 1.17. Motion needs **no** asset download (unlike fonts/icons) — it materializes as pure Compose code in `motion/` files at implementation time.

---

## Step 1.17: Generate Implementation Blueprint

**Condition**: Always runs after design approval.

This step parses the Stitch HTML exports (downloaded/read in Step 1.15) into a structured Compose Implementation Blueprint that provides exact component trees, design tokens, typography, and spacing for implementation.

### Procedure

1. **Read the persisted inputs** for **selected states only** — issue all these Reads as **one batched response** (parallel tool calls), not one round-trip per file:
   - Success — `.claude/docs/{featurename}/designs/extracted/stitch_success.html` + `tokens_success.md` (always)
   - Loading — `.claude/docs/_shared/designs/extracted/stitch_loading.html` + `tokens_loading.md` (only if `needsLoading`)
   - Failed — `.claude/docs/_shared/designs/extracted/stitch_failed.html` + `tokens_failed.md` (only if `needsFailed`)
   - Empty — `.claude/docs/{featurename}/designs/extracted/stitch_empty.html` + `tokens_empty.md` (only if `needsEmpty`)

2. **Tab-nav-bar detection (MANDATORY before generating)**: Scan the success-state HTML for a multi-tab bottom navigation bar (a persistent row of ≥2 selectable tab items). If found:
   - **Do NOT map it to `XScreen(bottomBar = { NavBar() })`** — that violates `blueprint-spec.md:47` and `patterns.md Rule 13`.
   - Classify the feature as a **top-level tab** destination. If the PRD Navigation section says "pushed screen", STOP and fix the PRD first — the design overrides the default. Update the PRD Navigation section to read "top-level tab" and capture label, icon, and tab order.
   - In the blueprint Component Tree, emit the nav bar under a note: `[App-shell chrome — Integration Point 5. Do NOT add to XScreen.bottomBar. Wire via TopLevelDestination in App.kt.]`. Tab icons go in `composeApp/composeResources/drawable/` (NOT `icons.json` / `core:designsystem`); tab labels go in `composeApp/composeResources/values/strings.xml`. Both are app-module resources referenced via `{PROJECT_NAMESPACE}.composeapp.generated.resources.Res`.
   - A single sticky CTA (e.g. "Send", "Confirm") is NOT a tab nav bar — it goes in `XScreen.bottomBar` normally.

3. **Generate the blueprint**: Feed the selected state HTML files **together with their token inventories** to Claude using the extraction prompt template from [blueprint-spec.md](../references/blueprint-spec.md#extraction-prompt-template). The inputs are:
   - HTML file contents for selected states (labeled by state)
   - Token inventories for selected states (labeled by state) — authoritative for already-converted classes
   - The icons manifest at `.claude/docs/{featurename}/designs/extracted/icons.json` (from Step 1.15 sub-step 5) — authoritative for every Material Symbols `<span>` in the design, resolving each to its drawable name and Compose `res_reference`. The blueprint must use the exact `res_reference` from the manifest (no manual `Icons.Default.*` fallback).
   - The images manifest at `.claude/docs/{featurename}/designs/extracted/images.json` (from Step 1.15 sub-step 6, with the user-confirmed `delivery` from Step 6a) — authoritative for every `<img>` tag in the design. The blueprint branches on each entry's `delivery`: `bundled` → `Image(painter = painterResource({res_reference}))`; `remote` → `AsyncImage(url = {data_binding}, loadingResId = DesignSystemResources.drawable.ds_image_placeholder, …)` (design-system `AsyncImage`, `url=`) + a Post-Implementation Checklist item to wire `{data_binding}`. The Stitch CDN URL is never emitted in code.
   - The X-component mapping table (from [stitch-guide.md](../references/stitch-guide.md#mapping-stitch-designs-to-kmp-x-components))
   - The Color Audit M3 role mappings (from Step 1.16 output in `.claude/docs/{featurename}/designs/{featurename}.md`)
   - The Typography Audit (from Step 1.16) + the font manifest at `.claude/docs/{featurename}/designs/extracted/fonts.json` (from Step 1.15 sub-step 6b) — authoritative for the per-node M3 type-scale role mapping and the font swap (family + source). The blueprint fills the Typography Scale `M3 Role` column and the contract's *Typography Updates Required* from these.
   - The Motion Audit (from Step 1.16, the `MOTION_AUDIT` block in `.claude/docs/{featurename}/designs/{featurename}.md`) + the `## Motion Inventory` in each `tokens_{state}.md` — authoritative for the blueprint's `## Motion` table (kept families → primitive + target file) and its Dropped note. Omit the `## Motion` section when no motion is present.
   - The `needsLoading`, `needsFailed`, `needsEmpty` flags so the prompt knows which sections to emit

3. **Save the blueprint** to `.claude/docs/{featurename}/designs/{featurename}_blueprint.md` — with **exactly one `Write`**. Any later correction (verification failure in sub-step 4, manifest gap, user tweak) is applied with `Edit` on the affected section only; **never rewrite the whole file**. The blueprint covers **only selected states**; shared scaffold is described once. Use the canonical Component-Tree entries from [blueprint-spec.md → Component Tree](../references/blueprint-spec.md#component-tree):
   - `states.loading` true → shared-screen entry; false → "Skipped" marker.
   - `states.failed` true → shared-screen entry; false → "Skipped" marker.
   - `states.empty` true → emit the section; false → omit the section entirely (no "Skipped" placeholder — empty is a content variant, not a Rule-4 UI state).

4. **Verify** the blueprint file was written and contains the expected sections (Design Tokens, Typography Scale **with the `M3 Role` column filled**, Spacing Grid, Component Tree with selected states, String Inventory (every text node → a `{area}_{purpose}` key; Rule 12), **`## Motion` table when the Motion Audit found motion** (kept families + target-file column + Dropped note; omitted for a static design), Pre-Implementation Contract with Component Overrides **and Typography Updates Required** tables, Post-Implementation Checklist). **Also verify**:
   - Component Tree must NOT assign a tab nav bar to `XScreen(bottomBar = { ... })`. Any multi-tab nav bar must appear only as an app-shell Point-5 note (see sub-step 2 above). If found in `XScreen.bottomBar`, fix that Component-Tree section via `Edit` (do not rewrite the file).
   - Blueprint must NOT contain `Icons.Default.{Name}`, `androidx.compose.material.icons`, or the phrase "No XML download needed for font glyphs" / "use `Icons.Default.*`". Every Material Symbol must reference its `res_reference` from `icons.json`. If found, the manifest is incomplete — add missing icons to `icons.json` and patch the affected blueprint lines via `Edit` (do not rewrite the file).

---

## Step 1.18: Update stitch-project.json

Update `.claude/docs/_project/stitch-project.json` after design approval:

- `features[{featurename}]`:
  - `successScreenId`: captured in Step 1.13
  - `successScreenName`: `"projects/{projectId}/screens/{successScreenId}"`
  - `screenshot`: `"designs/{featurename}.png"`
  - `htmlPath`: `"designs/extracted/stitch_success.html"`
  - `tokensPath`: `"designs/extracted/tokens_success.md"`
  - `dimensions`: width/height from Step 1.15
  - `designFile`: `"designs/{featurename}.md"`
  - `blueprintFile`: `"designs/{featurename}_blueprint.md"`
  - `emptyScreenId`: the approved empty screen ID **if `needsEmpty == true`**, otherwise leave `null` (the `states` map was already written in Step 1.7 and does not change here)
  - **`"blueprintConsumed": false`** — signals to implementation skills that a new blueprint is available
  - `approved`: `true`
  - `approvedAt`: current ISO date
  - `updatedAt`: current ISO timestamp
- Update top-level `updatedAt`.

---

## Step 1.19: User Final Approval

Present all approved designs. Only emit table rows for **selected** states:

```
## UI Designer Complete: {FeatureName}

Stitch Project ID: {projectId} (shared project)
Design System ID: {designSystemAssetId}

| State | Screenshot |
|-------|------------|
| Success | designs/{featurename}.png |
{if needsLoading} | Loading | .claude/docs/_shared/designs/loading.png (shared) |
{if needsFailed}  | Failed  | .claude/docs/_shared/designs/failed.png (shared) |
{if needsEmpty}   | Empty   | designs/{featurename}_empty.png |

States selected: success{, loading if needsLoading}{, failed if needsFailed}{, empty if needsEmpty}
States skipped (no design reference): {comma-separated skipped states, or "none"}

Design spec: designs/{featurename}.md
Blueprint: designs/{featurename}_blueprint.md
Project config: .claude/docs/_project/stitch-project.json
blueprintConsumed: false (set in stitch-project.json.features[{featurename}])
```

Show completion report from SKILL.md and stop. The completion report's "Next step" must use **`Feature Exists`** from Phase 0 — not `StitchMode` — to recommend the correct implementation skill:
- `Feature Exists: no` → recommend `/creating-kmp-feature {featurename}`
- `Feature Exists: yes` → recommend `/modifying-kmp-feature {featurename}`

---

## Output

After Phase 1 completes:
- Success screenshot: `.claude/docs/{featurename}/designs/{featurename}.png` (always)
- Loading screenshot: `.claude/docs/_shared/designs/loading.png` — **only if `needsLoading`**
- Failed screenshot: `.claude/docs/_shared/designs/failed.png` — **only if `needsFailed`**
- Empty screenshot: `.claude/docs/{featurename}/designs/{featurename}_empty.png` — **only if `needsEmpty`**
- Design description: `.claude/docs/{featurename}/designs/{featurename}.md` (with state rows for selected states only)
- Implementation blueprint: `.claude/docs/{featurename}/designs/{featurename}_blueprint.md` (selected states only; skipped state sections marked "Skipped" for loading/failed, omitted for empty)
- Persisted HTML + token inventories for selected states: `.claude/docs/{featurename}/designs/extracted/stitch_{state}.html` and `tokens_{state}.md` (consumed by `/verify-ui`). Loading/failed inventories continue to live in `.claude/docs/_shared/`.
- Icons manifest: `.claude/docs/{featurename}/designs/extracted/icons.json` (consumed by Step 1.17 blueprint generator, `/creating-kmp-feature` / `/modifying-kmp-feature` in design-aware mode, and `/verify-ui`). The manifest declares predicted drawable paths and `res_reference` values for every icon, with `download_status: "pending"` until an implementation skill materializes them.
- Images manifest: `.claude/docs/{featurename}/designs/extracted/images.json` (consumed by the same downstream skills). Per `<img>` tag, carries a user-confirmed `delivery`: `bundled` entries declare predicted drawable path + `res_reference` (`download_status: "pending"`); `remote` entries declare `data_binding` + `placeholder_ref` (`download_status: "remote"`, no drawable). `delivery_locked: true` records the user's confirmation so full-mode runs don't re-derive it.
- stitch-project.json updated with approved screen, per-feature `states` selection map, screenshots for selected states, and `blueprintConsumed: false`
- All variant screenshots cleaned up
- User approval received

Show completion report from SKILL.md. Done.
