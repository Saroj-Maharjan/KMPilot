# Profile — Design Description

**Approved**: 2026-06-22

## Design Description

Two-screen feature on a deep warm obsidian canvas (#0F0D09).

**Primary — Profile screen**: Fixed top app bar (64dp, glass blur, primary back arrow). Scrollable content with a centred avatar section (80dp circle in surfaceVariant with 1dp primary border, initials "AJ" in primary), user name and email below. "ACCOUNT DETAILS" card (surface, 1dp outlineVariant border, 24dp corners): rows for Name, Email, and Member Tier (stars_fill icon + "Gold Private Banking" in primary). "PREFERENCES" card: biometric security row (shield icon + toggle in primary) and an edit icon button. Fixed bottom action bar with gradient fade: full-width "Edit Profile" CTA (primary fill, 56dp, 24dp corners).

**Secondary — Edit Profile screen** (kind: screen): Fixed top app bar (64dp, glass blur background): leading back button (arrow_back, primary), title "Edit Profile", trailing "Save" text button (primary). Scrollable form: centred avatar (80dp, same style) with a camera badge (primary-container circle, cancel_fill icon in primary). Two input fields — Full Name (person icon leading) and Email Address (mail icon leading) — both 56dp tall, bg-[#231F12] (= surfaceContainer), 24dp corners, outline border, primary focus glow. Hint text "Changes will be reflected across the app" (12sp, onSurfaceVariant). "Privacy Settings" info card (surface, outlineVariant border): privacy row (security icon + chevron_right) and notifications row (notifications icon + chevron_right). Fixed bottom "Save Changes" CTA (primary fill, 56dp, check icon).

**Loading state**: shared screen (`.claude/docs/_shared/designs/loading.png`).

## Visual Specifications
- Colors: all from XDarkColors; bg-[#231F12] = surfaceContainer; primary@10% = decorative glow; gradient uses background
- Typography: Manrope variable (already in XTheme.kt) — no font swap needed
- Layout: Screen height 1768dp (profile) / 1768dp (edit), width 780dp
- Components: XTopAppBar, XTextField, XButton (primary), XSwitch/toggle, row items, dividers

## Screenshots
- Success: `profile.png`
- Loading: `.claude/docs/_shared/designs/loading.png` (shared)
- Edit Profile (edit): `profile_edit.png`

<!-- COLOR_AUDIT:BEGIN -->
## Color Audit

Default theme for design: dark

### Defined M3 Roles (already in active scheme — XDarkColors)
| Role | Hex (inventory) | Usage in Design |
|------|-----------------|-----------------|
| background | #0F0D09 | Screen bg, gradient base, bottom bar bg |
| surface | #1C1910 | Account Details card bg, Preferences card bg |
| surfaceVariant | #302B1C | Avatar circle bg, notification icon container bg |
| surfaceContainer | #231F12 | Input field bg (bg-[#231F12]) |
| primaryContainer | #4A3200 | Avatar camera badge bg, privacy icon container bg |
| primary | #F5D76E | Back icon, avatar initials, member tier text/icon, edit icon, CTA bg, focus border, "Save" text, cancel_fill icon |
| onPrimary | #2C1900 | CTA label text, avatar thumb dark bg |
| onSurface | #EDE8D5 | Title, body text, input text, row values, section headers, privacy titles |
| onSurfaceVariant | #C4BA94 | Subtitle email, section labels, row label caps, input placeholders, icon tints, secondary descriptions |
| outline | #726A48 | Input border (default state) |
| outlineVariant | #3F3822 | Card border, row dividers, input focus glow ring offset |
| onPrimaryContainer | #FFF0C0 | (defined, not visually used — config only) |
| error | #FFB4AB | (config only, not visually used) |
| onError | #690005 | (config only) |
| errorContainer | #93000A | (config only) |
| onErrorContainer | #FFDAD6 | (config only) |

### Missing M3 Roles (must add to BOTH XLightColors and XDarkColors before implementation)
| Role | Active Scheme Hex | Counterpart Scheme Hex | Usage in Design |
|------|-------------------|----------------------|-----------------|
| (none) | — | — | All colors map to existing roles |

### Custom Colors (XTheme.Colors.* — justified exceptions only)
| Name | Hex | Justification |
|------|-----|---------------|
| primary@10% opacity | #F5D76E @ 10% | Decorative avatar glow — use `MaterialTheme.colorScheme.primary.copy(alpha = 0.10f)` inline; no new `XTheme.Colors` entry needed |
| Gradient (bottom bar) | background → transparent | `Brush.verticalGradient(listOf(Color.Transparent, MaterialTheme.colorScheme.background))`; no new token |
<!-- COLOR_AUDIT:END -->

<!-- TYPOGRAPHY_AUDIT:BEGIN -->
## Typography Audit

**Design typeface**: Manrope (variable, weights 400/500/600/700/800)
**Theme font**: Manrope (XFontFamily uses `manrope_variable`) — **matches current**

### Text node → M3 role
| Node (usage) | M3 Role | Measured (size/weight) | Override needed? |
|--------------|---------|------------------------|------------------|
| Screen title "Profile" / "Edit Profile" | titleLarge | 20sp / 700 (Bold) | yes — `.copy(fontSize = 20.sp, fontWeight = FontWeight.Bold)` |
| User name "Alex Johnson" (avatar section) | titleLarge | 20sp / 700 (Bold) | yes — same override |
| Avatar initials "AJ" | headlineSmall | 24sp / 700 (Bold) | yes — `.copy(fontWeight = FontWeight.Bold)` |
| Email below avatar (14sp body) | bodyMedium | 14sp / 400 | no |
| Section header caps ("ACCOUNT DETAILS", "PREFERENCES") | labelMedium | 12sp / 600 (SemiBold), uppercase, wide tracking | yes — `.copy(fontWeight = FontWeight.SemiBold)` + `letterSpacing` |
| Row label caps ("Name", "Email", "Member Tier") | labelSmall | 10sp / 400, uppercase | yes — `.copy(fontSize = 10.sp)` |
| Row value text ("Alex Johnson", email) | bodyLarge | 16sp / 500 (Medium) | yes — `.copy(fontWeight = FontWeight.Medium)` |
| "Gold Private Banking" tier label | titleMedium | 16sp / 600 (SemiBold) | yes — `.copy(fontWeight = FontWeight.SemiBold)` |
| Input labels ("Full Name", "Email Address") | labelLarge | 14sp / 400 | no |
| Input text content | bodyLarge | 16sp / 400 | no |
| "Save" trailing button | labelLarge | 14sp / 700 (Bold) | yes — `.copy(fontWeight = FontWeight.Bold)` |
| "Changes will be reflected…" hint | bodySmall | 12sp / 400 | no |
| Section sub-labels ("Manage your visibility…", "Alerts and status…") | bodySmall | 12sp / 400 | no |
| Section titles ("Privacy Settings", "Notifications") | titleSmall | body / 700 (Bold) | yes — `.copy(fontWeight = FontWeight.Bold)` |
| CTA label ("Edit Profile", "Save Changes") | titleMedium | 16sp / 700 (Bold) | yes — `.copy(fontWeight = FontWeight.Bold)` |
<!-- TYPOGRAPHY_AUDIT:END -->

<!-- MOTION_AUDIT:BEGIN -->
## Motion Audit

**Motion present**: no — static design

**Dropped (interaction + web-only)**: `active:scale-95`, `active:scale-[0.98]`, `transition-transform`, `transition-all`, `transition-colors`, `transition-opacity`, `hover:opacity-90`, `hover:opacity-80`, `focus:outline-none`, `focus:border-primary`, `input-focus-glow:focus-within`
<!-- MOTION_AUDIT:END -->
