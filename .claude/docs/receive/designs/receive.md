# Receive Screen

**Approved**: 2026-05-03

## Design Description

A focused crypto wallet Receive screen. The layout is intentionally minimal: coin/network asset selector → truncated wallet address pill with copy → network safety warning → sticky Share + Copy Address action buttons. No QR code, no bottom navigation. The app bar matches the Send screen style exactly (back arrow, centered bold title, flush background).

## Visual Specifications

- **Colors**: Dark theme (#0D0919 background, #231A38 surfaceVariant fills, #9D70FF primary accent)
- **Typography**: Manrope throughout. App bar 20sp bold −0.5sp tracking; coin name 16sp bold; muted text 12sp/14sp
- **Layout**: 16dp horizontal padding on main content, 24dp between sections (space-y-6), sticky bottom bar with 24dp padding
- **Components**: Asset selector card, address pill (rounded-full), warning banner (surface bg + error border), dual CTA buttons

## Screenshots

- Success: `receive.png`
- Loading: `receive_loading.png`
- Failed: `receive_failed.png`

---

## Color Audit

Default theme for design: **dark**

### Defined M3 Roles (already in XDarkColors — no XTheme changes needed)

| Role | Hex (inventory) | Usage in Design |
|------|-----------------|-----------------|
| `background` | #0D0919 | Screen bg, app bar bg, bottom button area backdrop |
| `surface` | #181228 | Warning banner background |
| `surfaceVariant` | #231A38 | Asset selector fill, address pill fill, Share button fill |
| `outline` | #4A3F6B | Asset selector border, address pill border, Share button border |
| `onSurface` | #E9E0FF | App bar title, coin name, address text, warning heading, Copy Address text |
| `onSurfaceVariant` | #C5BCE0 | Network name subtitle, expand icon, warning body text |
| `primary` | #9D70FF | Copy icon, copy button bg, loading spinner |
| `onPrimary` | #1A0054 | Copy Address button label text |
| `error` | #FFB4AB | Warning icon color, warning border (@ 40% opacity), failed state icon + container bg (@ 10%) |

### Missing M3 Roles

_(none — all design colors map to existing XDarkColors roles)_

### Custom Colors (XTheme.Colors.* — justified exceptions only)

| Name | Hex | Justification |
|------|-----|---------------|
| _(inline brand)_ | #EAB308 | Bitcoin brand orange for the BTC coin icon circle background — not a semantic M3 role |
