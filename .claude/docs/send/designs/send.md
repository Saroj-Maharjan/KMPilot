# Send Screen

**Approved**: 2026-05-11

## Design Description

A premium mobile crypto send form screen using the KMPilot Gold dark theme. The warm obsidian canvas (#0F0D09) is punctuated by champagne gold (#F5D76E) accents. The hero amount display is the visual centrepiece — oversized, centred, anchored by a gold cursor underline. The form uses floating surface cards for recipient input and dual side-by-side selectors for asset and network. A surface-variant transaction summary card provides the fee breakdown. A sticky gold "Send Bitcoin" CTA button is fixed to the bottom with a gold-glow shadow.

## Visual Specifications

### Layout Structure
- `XScaffold` with transparent top bar, fixed footer button
- Main body: scrollable `Column`, 24dp horizontal padding, 96dp top padding, 128dp bottom padding
- Sections: hero → recipient card (40dp gap) → selector grid (20dp gap) → summary card (32dp gap) → security badge (32dp gap)

### Key Components
- **App bar**: transparent, back arrow (gold primary), "Send" title, empty right spacer
- **Hero amount**: 64sp ExtraBold centred, "BTC" gold pill, 128dp gold cursor underline, balance subtitle, 25%/50%/MAX chips
- **Recipient card**: surface bg, outlineVariant border, 24dp corners, 4dp gold left accent bar, transparent input, paste + QR gold icon buttons
- **Asset/Network grid**: 2-column, 16dp gap, each card: surface bg, outlineVariant border, 24dp corners, coin icon + name/subtitle + gold chevron
- **Transaction summary**: surfaceVariant bg, primary/30 border, 24dp corners, 3 rows (Network Fee / Total Deduct / Estimated Arrival), partial top-border divider before Estimated Arrival row
- **Security badge**: 50% opacity, verified icon + "Secured by KMPilot Vault" uppercase
- **CTA**: primary gold fill, onPrimary dark text, 56dp height, 24dp corners, gold-glow box-shadow

### Typography

| Usage | Size (sp) | Weight | Notes |
|-------|-----------|--------|-------|
| App bar title "Send" | headline-sm | Bold 700 | tracking-tight |
| Hero amount "0.00" | 64 | ExtraBold 800 | tracking-tighter |
| BTC pill text | 12 | Bold 700 | tracking-widest, uppercase |
| Balance label | 14 | Medium 500 | on-surface-variant |
| Balance dollar value | 14 | Medium 500 | on-surface (inline span) |
| Quick chips | 12 | Bold 700 | uppercase (MAX only) |
| Card section labels (ASSET, NETWORK, TO RECIPIENT) | 10 | Bold 700 | uppercase, tracking-widest |
| Input placeholder | 14 | Normal | italic |
| Coin name | 14 | Bold 700 | |
| Coin subtitle | 10 | Normal | on-surface-variant |
| Summary labels | 14 | Normal | on-surface-variant |
| Summary values | 14 | Medium/Bold | |
| Estimated Arrival value | 14 | Bold 700 | success green |
| Security badge | 10 | Bold 700 | uppercase, tracking-[0.2em] |
| CTA button | ~16 (text-md) | Bold 700 | |

### Spacing

| Context | Value (dp) |
|---------|------------|
| Screen horizontal padding | 24 |
| Screen top padding | 96 |
| Screen bottom padding | 128 |
| Hero section bottom margin | 40 |
| Recipient card bottom margin | 20 |
| Selector grid gap | 16 |
| Selector grid bottom margin | 32 |
| Summary card bottom margin | 32 |
| Security badge bottom margin | 16 |
| Footer padding | 24 all sides |
| Recipient card padding | 20 all sides |
| Asset/Network card padding | 16 all sides |
| Summary card padding | 20 all sides |
| Quick chip horizontal padding | 20 |
| Quick chip vertical padding | 8 |
| Quick chips gap | 12 |
| Recipient label bottom margin | 12 |
| Recipient icons gap | 12 |

## Screenshots
- Success: `send.png`
- Loading: `.claude/docs/_shared/designs/loading.png` (shared)
- Failed: `.claude/docs/_shared/designs/failed.png` (shared)
- Empty: N/A (form screen)

---

## Color Audit

Default theme for design: **dark**

> **Prompt drift**: 0 colors differ from the Stitch prompt — all inventory values match.

### Defined M3 Roles (already in XDarkColors)

| Role | Hex (inventory) | Usage in Design |
|------|-----------------|-----------------|
| background | #0F0D09 | Screen canvas, footer bg (80% opacity), decorative ambient glows (5% opacity) |
| surface | #1C1910 | Recipient card, Asset card, Network card background |
| surfaceVariant | #302B1C | Quick chip fill, network icon bg, transaction summary card bg |
| onSurface | #EDE8D5 | Hero amount, balance dollar value "$78,420", coin names, total deduct value, input text |
| onSurfaceVariant | #C4BA94 | Balance label, section labels, input placeholder (50% opacity), coin subtitles, summary labels, security badge |
| outline | #726A48 | Hover-state borders (not directly visible in static design) |
| outlineVariant | #3F3822 | Card borders (recipient, asset, network chips), summary divider border (30% opacity) |
| primary | #F5D76E | Back arrow icon, BTC pill text, cursor underline (+ gold glow shadow), quick chip text, paste/QR icon buttons, asset/network chevrons, recipient gold left accent bar, summary card border (30% alpha), CTA fill, ambient glow overlays (5% alpha) |
| onPrimary | #2C1900 | CTA button text |
| primaryContainer | #4A3200 | Tailwind config only — not rendered directly |
| onPrimaryContainer | #FFF0C0 | Tailwind config only — not rendered directly |
| error | #FFB4AB | Tailwind config (from shared failed screen) |
| onError | #690005 | Tailwind config (from shared failed screen) |

### Missing M3 Roles

*None — all design colors map to existing XDarkColors roles. No XTheme.kt changes required for M3 roles.*

### Custom Colors (XTheme.Colors.* — justified exceptions)

| Name | Hex | Justification |
|------|-----|---------------|
| XTheme.Colors.Success | #4ADE80 | No M3 semantic role for "success/fast" green. Already exists in `XTheme.Colors`. |
| XTheme.Colors.Bitcoin (NEW — add to XTheme.kt) | #F7931A | Bitcoin brand orange for coin icon. Coin-specific, no M3 equivalent. |
