# Receive Screen

**Approved**: 2026-05-11

## Design Description

A premium mobile crypto receive screen using the KMPilot Gold dark theme. The warm obsidian canvas (#0F0D09) serves as the backdrop. The asset selector is a full-width rounded pill card at the top. Below it, a gold-bordered card acts as the visual hero, housing the "Your Bitcoin address" label and the monospace address pill with an inline copy icon. A decorative gold radial glow overlays the card interior. A network warning banner below uses an error-tinted treatment. At the bottom, a sticky surface-colored bottom bar (with rounded top corners and an upward shadow) contains two equal-width CTAs: "Share" (secondary) and "Copy Address" (gold primary).

## Visual Specifications

### Layout Structure
- `XScaffold` with custom top bar `Row` (not `XTopAppBar` — left-aligned title required), sticky `bottomBar`
- Main body: non-scrollable `Column`, 16dp horizontal padding, 64dp top offset (below fixed bar), 128dp bottom padding

### Key Components
- **App bar**: Custom `Row` — transparent bg, fixed at top, 64dp height, 16dp horizontal padding; left: `XIconButton` (40×40dp, gold arrow_back); center-left: "Receive" `XText` (20sp Bold 700 tracking-tight onSurface, 8dp left margin); right: `Spacer(Modifier.size(40.dp))`
- **Asset selector**: Full-width pill `Button`, surface bg, outline border, CircleShape, 12dp padding; left cluster: 40dp orange circle + "₿" + coin/network text; right: expand_more icon in outline color
- **Address card**: `XCard` with explicit surface bg, 1dp primary border, 20dp corners, 32dp padding; decorative gold radial glow overlay (drawBehind); "Your Bitcoin address" label (14sp, Medium 500, onSurfaceVariant, 24dp bottom margin); address pill (surfaceVariant bg, outline border, 24dp corners, monospace text, gold copy icon)
- **Network warning banner**: errorContainer/20% bg, error/40% border, 24dp corners, 16dp padding; warning icon (error color) + title + body text
- **Bottom bar**: surface bg, rounded-t 20dp, upward shadow, outline/30% top border; 16dp horizontal + 16dp top + 32dp bottom padding; two `flex-1` buttons with 16dp gap

### Typography

| Usage | Size (sp) | Weight | Letter Spacing | Color Role |
|-------|-----------|--------|----------------|------------|
| App bar title "Receive" | 20 | Bold 700 | -0.025em | onSurface |
| Coin name "Bitcoin" | 14 | Bold 700 | 0 | onSurface |
| Network "Bitcoin Network" | 12 | Normal 400 | 0 | onSurfaceVariant |
| "Your Bitcoin address" label | 14 | Medium 500 | 0 | onSurfaceVariant |
| Address text (monospace) | 12 | Normal 400 | -0.025em | onSurface |
| Warning title | 14 | Bold 700 | 0 | onSurface |
| Warning body | 12 | Normal 400 | 0 | onSurfaceVariant, 1.625× line height |
| Share button label | 14 | SemiBold 600 | 0 | onSurface |
| Copy Address button label | 14 | Bold 700 | 0 | onPrimary |

### Spacing

| Context | Value (dp) |
|---------|------------|
| Screen horizontal padding | 16 |
| Main body top offset (below fixed bar) | 64 |
| Main body bottom padding | 128 |
| Section gap (asset selector → address card) | 24 |
| Section gap (address card → warning) | 24 |
| Address card padding | 32 |
| "Your Bitcoin address" label bottom margin | 24 |
| Warning banner padding | 16 |
| Warning icon ↔ text column gap | 12 |
| Warning title ↔ body gap | 4 |
| Bottom bar horizontal padding | 16 |
| Bottom bar top padding | 16 |
| Bottom bar bottom padding | 32 |
| Gap between Share and Copy Address buttons | 16 |
| Button height | 56 |

## Screenshots
- Success: `receive.png`
- Loading: `.claude/docs/_shared/designs/loading.png` (shared)
- Failed: `.claude/docs/_shared/designs/failed.png` (shared)
- Empty: N/A (detail screen, not a list)

---

## Color Audit

Default theme for design: **dark**

> **Prompt drift**: 0 colors differ from the Stitch prompt — all inventory values match.

### Defined M3 Roles (already in XDarkColors)

| Role | Hex (inventory) | Usage in Design |
|------|-----------------|-----------------|
| background | #0F0D09 | Screen canvas (body bg) |
| surface | #1C1910 | Asset selector bg, address card bg, bottom bar bg |
| surfaceVariant | #302B1C | Address pill bg, Share button bg |
| primary | #F5D76E | Back arrow icon, address card border (1dp), copy icon color, Copy Address button fill, gold glow radial gradient |
| onPrimary | #2C1900 | Copy Address button label |
| primaryContainer | #4A3200 | Tailwind config only — not rendered |
| onPrimaryContainer | #FFF0C0 | Tailwind config only — not rendered |
| onSurface | #EDE8D5 | App bar title, coin name, address text, warning title, Share button text |
| onSurfaceVariant | #C4BA94 | Network subtitle, "Your Bitcoin address" label, warning body |
| outline | #726A48 | Asset selector border, address pill border, Share button border, chevron icon, footer top border (30% opacity) |
| outlineVariant | #3F3822 | Tailwind config only — not rendered |
| error | #FFB4AB | Warning icon, warning border (40% opacity) |
| errorContainer | #93000A | Warning banner bg (20% opacity) |
| onError | #690005 | Tailwind config only — not rendered |
| onErrorContainer | #FFDAD6 | Tailwind config only — not rendered |

### Missing M3 Roles

*None — all design colors map to existing XDarkColors roles. No XTheme.kt changes required.*

### Custom Colors (XTheme.Colors.* — justified exceptions)

| Name | Hex | Justification |
|------|-----|---------------|
| XTheme.Colors.Bitcoin | #F7931A | Bitcoin brand orange for coin icon container. Already defined in XTheme.kt. No M3 semantic equivalent. |
| Color.White | #FFFFFF | "₿" symbol text on #F7931A orange circle — hardcoded white on brand-colored bg. |
