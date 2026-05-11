# Compose Implementation Blueprint Spec

The Stitch HTML export is parsed into a structured blueprint that provides exact component trees, design tokens, typography, and spacing for implementation.

**Condition**: Always generated after design approval. Used as the handoff artifact for implementation skills (paired with the persisted HTML + token inventories in `extracted/`).

---

## Blueprint Format

Single markdown file at `.claude/docs/{featurename}/designs/{featurename}_blueprint.md`:

```markdown
# Compose Implementation Blueprint: {FeatureName}

## Design Tokens

| Hex | M3 Role | Usage |
|-----|---------|-------|
| #0D0D0F | background | Screen background |
| #1A1A1F | surface | Card backgrounds |

## Typography Scale

| Usage | Size (sp) | Weight | Letter Spacing | Text Transform | Color Role |
|-------|-----------|--------|----------------|----------------|------------|
| Screen title | 24 | Bold (700) | 0 | none | onBackground |
| Body text | 14 | Normal (400) | 0.25 | none | onSurfaceVariant |

## Spacing Grid

| Context | Property | Value (dp) |
|---------|----------|------------|
| Screen | horizontal padding | 16 |
| Card list | gap between items | 8 |

## Component Tree

### Shared Scaffold (all states)
- `XScaffold`
  - topBar: `XTopAppBar` (title, navigationIcon: back arrow)
  - content: **[state-specific content slot]**

### Success State
- `LazyColumn` (spacedBy 8.dp, contentPadding: 16.dp)
  - `{ComponentName}` (extracted named component)
    - `XCard` → `Row` → `AsyncImage` + `Column` with `XText` elements

### Loading State
Shared screen — see: `.claude/docs/_shared/designs/loading.png`
Token inventory: `.claude/docs/_shared/designs/extracted/tokens_loading.md`
- `Box` (fillMaxSize, Center) → `XCircularProgressIndicator`

### Failed State
Shared screen — see: `.claude/docs/_shared/designs/failed.png`
Token inventory: `.claude/docs/_shared/designs/extracted/tokens_failed.md`
- `Column` (fillMaxSize, center) → `XIcon` + `XText("Something went wrong")` + `XButton("Retry")`

### Empty State (list screens only)
- `Column` (fillMaxSize, center) → `XIcon` + `XText("No {items} yet")`

## Pre-Implementation Contract

> Architecture rules, color rules, and X-component defaults are project-wide and live in their canonical sources — do not restate them here:
> - Architecture rules → [`_shared/patterns.md`](../../_shared/patterns.md)
> - Color rules → [`m3-colors.md`](m3-colors.md) (sections "Color Rules (Strict)" and "Complete M3 Role Catalog")
> - X-component default-render behavior → [`_shared/X_COMPONENTS_CATALOG.md`](../../_shared/X_COMPONENTS_CATALOG.md)
>
> This contract captures only feature-specific data the implementer cannot derive from those references.

### XTheme Updates Required

| Role | Active Scheme Hex | Counterpart Scheme Hex | Usage |
|------|-------------------|----------------------|-------|
| {role} | {hex} | {hex} | {usage} |

### Color Audit

#### Defined Roles
| Role | Hex | Usage |
|------|-----|-------|
| {role} | {hex} | {usage} |

#### Missing Roles (must add before implementation)
| Role | Active Scheme Hex | Counterpart Scheme Hex | Usage |
|------|-------------------|----------------------|-------|
| {role} | {hex} | {hex} | {usage} |

#### Custom Colors (justified exceptions only)
| Name | Hex | Justification |
|------|-----|---------------|

#### Component Overrides (divergences from X-component defaults)

> **Audit-aware**: `/verify-ui` reads this table directly (its only blueprint dependency). Every row here is a CRITICAL check at audit time — if the override is missing in code, verify-ui flags it. Keep one row per concrete divergence between the HTML inventory and the X-component default in `X_COMPONENTS_CATALOG.md`.

| Component | Property | HTML Value | X-component Default | Override Required |
|-----------|----------|-----------|-------------------|------------------|

## Post-Implementation Checklist

- [ ] All XTheme missing roles added to BOTH XLightColors and XDarkColors
- [ ] Every component in blueprint Component Tree exists in implementation
- [ ] Every Modifier in blueprint (border, shadow, alpha, padding, size) is present in code
- [ ] All colors use MaterialTheme.colorScheme.{role} — no raw Color() hex
- [ ] Component override sizes/colors from Pre-Implementation Contract applied
- [ ] Build passes: `./gradlew :feature:{name}:assembleAndroidMain`
- [ ] Code formatted: `./gradlew :feature:{name}:ktlintFormat`
```

---

## Decomposition Rules

| Condition | Style |
|-----------|-------|
| HTML body ≤150 lines AND ≤3 visual sections | **Flat** — one component tree per state, everything in `{Feature}Screen.kt` |
| HTML body >150 lines OR >3 sections | **Decomposed** — skeleton with `[slot]` placeholders + individual component specs |

**Repeated patterns** (2+ occurrences with same structure) must be extracted as named components.

**For decomposed blueprints**, annotate each named component with its target file based on this rule:
- **`{Feature}Screen.kt`** — structural glue: state routing, top-level layout scaffold, state screens (Loading, Error)
- **`components/{ComponentName}.kt`** — self-contained UI units: composables that have meaning independently of the screen, own their internal structure, or have private sub-composables/helper functions

Example annotation in the blueprint component tree:
```
// → {Feature}Screen.kt
- LazyColumn scaffold (sequences all sections)

// → components/ProductCard.kt
- ProductCard (owns image, title, price layout + ratingColor helper)
```

---

## Extraction Prompt Template

Feed this prompt with:
1. Raw HTML content (all state files, labeled by state)
   - success: `.claude/docs/{featurename}/designs/extracted/stitch_success.html`
   - loading: `.claude/docs/_shared/designs/extracted/stitch_loading.html` **(shared state)**
   - failed: `.claude/docs/_shared/designs/extracted/stitch_failed.html` **(shared state)**
   - empty: `.claude/docs/{featurename}/designs/extracted/stitch_empty.html` (if applicable)
2. **Token inventories** from `extract_tokens.py` (one per state, labeled by state) — authoritative for already-converted classes
   - success: `.claude/docs/{featurename}/designs/extracted/tokens_success.md`
   - loading: `.claude/docs/_shared/designs/extracted/tokens_loading.md` **(shared state)**
   - failed: `.claude/docs/_shared/designs/extracted/tokens_failed.md` **(shared state)**
   - empty: `.claude/docs/{featurename}/designs/extracted/tokens_empty.md` (if applicable)
3. X-component mapping table (from [stitch-guide.md](stitch-guide.md#mapping-stitch-designs-to-kmp-x-components))
4. Color Audit M3 role mappings (from Phase 1 Step 1.8)

```
You are a design-to-code translator. Convert this Stitch HTML export into a Compose Implementation Blueprint.

INPUT:
- HTML content (Tailwind CSS classes encode all visual properties)
- Token inventories (one per state) from extract_tokens.py — pre-resolves dp/sp/colors deterministically
- X-Component mapping table (maps HTML elements to Compose X-components)
- Color Audit (maps hex colors to M3 roles)

RULES:
1. Use the dp/sp/color values from the token inventory directly. The script handles px→dp,
   font-size→sp, opacity slashes (`bg-x/N`), arbitrary values (`text-[40px]`), tailwind config
   color resolution, and custom border-radius config. Do NOT re-derive these from raw HTML.
   Only consult the HTML directly when the inventory leaves a class unannotated (the script's
   auto-conversion rate is ~65%; the rest are layout primitives and unrecognised classes).
2. Map structural Tailwind classes to Compose containers and arrangements (the script does not
   handle these — they require translation, not conversion):
   - flex/flex-col → Column, flex-row → Row
   - items-center → CenterVertically/CenterHorizontally
     ⚠ Compose caveat: In CSS, `items-center` on a full-width flex container centers
       children automatically. In Compose, `CenterHorizontally` is a no-op unless the
       Column itself has `fillMaxWidth()`. Similarly, `textAlign = TextAlign.Center` on
       XText is invisible unless that XText has `fillMaxWidth()`. When the HTML has
       `flex-col items-center text-center`, translate as:
       Column(Modifier.fillMaxWidth(), horizontalAlignment = CenterHorizontally)
       with fillMaxWidth() on each XText child.
   - justify-center → Arrangement.Center, justify-between → Arrangement.SpaceBetween
   - gap-{N} / space-y-{N} → Arrangement.spacedBy(...) — value already in inventory
   - w-full → Modifier.fillMaxWidth()
   - overflow-y-auto → LazyColumn or verticalScroll
   - grid grid-cols-{N} → LazyVerticalGrid(columns = GridCells.Fixed(N)). If grid items have
     different heights (no uniform aspect-* or h-*), use LazyVerticalStaggeredGrid instead.
   - grid gap-{N} → horizontalArrangement + verticalArrangement spacedBy(...)
   - fixed/sticky bottom → XScaffold bottomBar
   - shadow-sm/shadow/shadow-md/shadow-lg/shadow-xl/shadow-2xl → Modifier.shadow(elevation, shape).
     Map: sm=1dp, default=2dp, md=4dp, lg=8dp, xl=12dp, 2xl=16dp.
     Custom `shadow-[...]` glow effects → note as `[decorative shadow — omit or use drawBehind]`
   - shrink-0 → no effect if using fixed-size Modifier.size(); for flex children use weight()
   - divide-y divide-{color} → XHorizontalDivider between items with explicit color param
   - mb-{N} on LazyColumn items → absorbed by spacedBy. Standalone: Spacer or padding
   **Compound padding override** (judgment, not in inventory): when shorthand + directional
   coexist (e.g., `p-4 pt-8`), the directional class overrides ONLY that side — other sides
   keep the shorthand value. The inventory lists each class separately; the LLM must compose
   them into a single PaddingValues(...) call.
3. Map HTML elements to X-components using the provided mapping table.
4. Map all colors to M3 roles using the Color Audit. Use MaterialTheme.colorScheme.{role}, never hex.
4b. **Component visual fidelity verification (MANDATORY for every component)**:
    For every component in the blueprint, verify two things — never assume X-component defaults match:
    (a) **Colors**: Use the inventory's resolved color value for every visual state of every component (bg-*, text-*, border-*, etc.). Look up the M3 role via the Color Audit, then verify that role's hex in `XTheme.kt` equals the inventory hex. If it matches → use `MaterialTheme.colorScheme.{role}`. If it diverges → write an explicit color override using the inventory's hex, annotated with the mismatch reason.
    (b) **Sizing**: Use the inventory's resolved dp values for every component. Compare to the X-component's actual rendered default. If they differ → write an explicit size override in the blueprint.
    The principle: the inventory (derived from the HTML) is the source of truth. X-component defaults are assumptions that must be verified, not trusted.
5. Identify shared scaffold common across all states — describe ONCE.
6. Per state, describe only the differing content area.
7. Extract repeated patterns (2+ occurrences) as named components.
8. Output ONLY the blueprint markdown.
9. **Wrapper container padding propagation**: When a wrapper element (div/main/section
   that contains a list or scrollable content) has px-{N}/py-{N}/p-{N}, do NOT move
   that padding down to each child. Apply it as `contentPadding` on the LazyColumn/LazyRow,
   or as `Modifier.padding()` on the outer Column/Box. Children start from zero offset.
   Example: outer div with px-6 wrapping cards →
     LazyColumn(contentPadding = PaddingValues(horizontal = 24.dp))
   NOT → each card with Modifier.padding(horizontal = 24.dp)
10. **Explicit background on every colored surface**: Every element with an explicit
    background color (bg-{color}) must get Modifier.background(MaterialTheme.colorScheme.{role})
    in Compose. CSS backgrounds are not inherited by child composables — if a card has
    bg-surface, the Compose Column/Box for that card MUST declare .background(surface)
    explicitly, even if the parent already has a different background set.
11. **Border radius**: Use the dp value from the inventory directly — the script resolves
    `tailwind.config.borderRadius` overrides and falls back to standard Tailwind defaults when
    no custom config exists. `rounded-full` resolves to `CircleShape`. Map the inventory's dp
    value to `RoundedCornerShape(N.dp)`.
12. **System inset padding**: Stitch HTML assumes padding starts from the screen edge (no system
    bars). On device, `XScaffold`'s `paddingValues` already includes system bar insets (status bar,
    navigation bar). When the HTML has a top padding like `pt-6` (24dp) or `pt-12` (48dp) on the
    outermost container, this value INCLUDES the status bar area. In Compose, subtract the system
    inset from that value since `paddingValues` already handles it. Typically use `8.dp` top padding
    for the first content element after applying `paddingValues`.
13. **Image mapping**: Stitch HTML `<img>` tags map to `AsyncImage` (for remote URLs) or a
    placeholder `Box` (for dummy/test screens). In the blueprint, note the image dimensions from
    the HTML (`w-full h-[200px]` → `fillMaxWidth().height(200.dp)`), shape (`rounded-full` →
    `CircleShape`, `rounded-lg` → use border radius config), and content scale (`object-cover` →
    `ContentScale.Crop`, `object-contain` → `ContentScale.Fit`). For test screens without real
    data, use a colored `Box` with the same dimensions as a placeholder.
14. **Bottom sheet mapping**: When Stitch HTML shows a bottom sheet pattern (a container pinned to
    the bottom with `rounded-t-*` top corners, a drag handle bar, and a semi-transparent overlay/scrim),
    implement it using `XModalBottomSheet` — NOT a custom `Box` overlay. `XModalBottomSheet` wraps
    M3's `ModalBottomSheet` and provides drag-to-dismiss gesture, smooth animation, proper scrim,
    and accessibility out of the box. Extract only the sheet CONTENT into a separate composable.
    The sheet's shape, containerColor, and scrimColor are set via `XModalBottomSheet` params.
15. **Pager mapping**: When Stitch HTML shows pagination indicators (a row of dots/pills), step
    counters ("Step X of Y"), or back/next navigation buttons together, this is a **paged flow** —
    implement as `HorizontalPager` with `rememberPagerState`, NOT as a static `Column`. The HTML
    only shows one static page, but the indicators signal multiple swipeable pages. Page indicator
    dots sync with `pagerState.currentPage`. Back/Next buttons use `pagerState.animateScrollToPage()`.
    Create a data list for all pages and render each inside the pager lambda.
16. **Custom div patterns for interactive components**: Stitch renders interactive form components as
    styled divs. Recognize these HTML patterns and map to the correct interactive X-component:
    - **Slider**: A thin horizontal bar (`h-1 rounded-full`) with circular thumb elements
      (`h-5 w-5 rounded-full`) positioned along it → `XSlider`, NOT nested `Box` composables.
      The visual shows track + thumb, but the implementation needs drag interaction.
    - **Dropdown trigger**: A row with text + `expand_more`/`chevron_down` Material icon →
      `XExposedDropdownMenuBox` anchor with `XDropdownMenuItem` menu, NOT a plain clickable `Row`.
      The visual shows a static selector, but the implementation needs a popup menu.
17. **Independent state parsing**: Each state screen has its own HTML file with its own
    tailwind config. Do NOT carry values (border radius, padding, colors, font sizes)
    from one state's HTML to another. Re-read the `tailwind.config` `<script>` tag and
    CSS classes independently for each state file. A failed state may define
    `rounded-card: 12px` while the success state uses `rounded-xl: 1.5rem` — these are
    different values that must be translated separately.
18. **No silent omissions**: The mapping table above is not exhaustive. For ANY Tailwind
    class not listed, look up its CSS value and translate to the equivalent Compose modifier.
    Never silently skip a class. If a class has no Compose equivalent (e.g., `cursor-pointer`,
    `transition-all`), note it as `[omitted: {class} — no Compose equivalent]` in a comment.
    Every visual property in the HTML must appear in the blueprint — either as a Compose value
    or as an explicit omission note.
19. **Pre-Implementation Contract**: After the Component Tree, emit a `## Pre-Implementation Contract`
    section. **Do not restate** project-wide architecture rules, color rules, or X-component defaults —
    they live in `_shared/patterns.md`, `m3-colors.md`, and `_shared/X_COMPONENTS_CATALOG.md`. Open the
    section with the boilerplate reference block from blueprint-spec.md, then include only the
    feature-specific tables:
    - **XTheme Updates Required**: Every M3 role from the Color Audit that is missing from XTheme.kt,
      with hex values for both active and counterpart schemes.
    - **Color Audit**: Full color audit tables (Defined, Missing, Custom).
    - **Component Overrides**: One row per concrete divergence between the HTML inventory and the
      X-component default in `X_COMPONENTS_CATALOG.md`. `/verify-ui` reads this table directly and
      treats each missing override as a CRITICAL — keep it accurate and minimal.
20. **Post-Implementation Checklist**: After the Pre-Implementation Contract, emit a
    `## Post-Implementation Checklist` with verification items: XTheme updates, component completeness,
    modifier fidelity, color fidelity, component override application, build validation, ktlint format.

X-COMPONENT MAPPING TABLE:
{paste from stitch-guide.md}

COLOR AUDIT:
{paste from design description .md}

TOKEN INVENTORIES:
{paste contents of .claude/docs/{featurename}/designs/extracted/tokens_{state}.md for each state, labeled by state}

HTML CONTENT:
{paste all state HTML files, labeled by state}
```

---

## Edge Case Handling

| HTML Element | Blueprint Representation |
|-------------|--------------------------|
| `<svg>`/`<canvas>` (icon-like) | `XIcon` with descriptive name |
| `<svg>`/`<canvas>` (decorative) | `[Canvas/Path decoration]` with description |
| CSS transitions/animations | `animate{Type}AsState` with target values |
| CSS gradients | `Brush.linearGradient(...)` with direction based on CSS angle |
| `<svg><circle>` with `stroke-dasharray`/`stroke-dashoffset` | `XCircularProgressIndicator(progress = { 1 - (dashoffset / dasharray) })` in a `Box` with text overlay |
| `<img>` tags | `AsyncImage(size, cornerRadius, contentScale)` |
| Material Icons | `XIcon(icon = Icons.Default.{Name})` |
| `position: fixed` bottom | `XScaffold` `bottomBar` parameter |
| CSS `box-shadow` | `Modifier.shadow(elevation, shape)` |
| `opacity: {N}` | `Modifier.alpha({N}f)` |
| `bg-clip-text text-transparent bg-gradient-to-*` | `XText(style = TextStyle(brush = Brush.linearGradient(...)))` — NOT a `Box` with gradient background behind transparent text |
| Multiple `<circle>` elements on same `<svg>` with different `stroke` colors and `stroke-dasharray` | `Canvas { drawArc() }` per segment — NOT multiple `XCircularProgressIndicator` stacked |
