# Compose Implementation Blueprint: Profile

## Design Tokens

| Hex | M3 Role | Usage |
|-----|---------|-------|
| #0F0D09 | background | Screen bg, gradient base, bottom bar fade |
| #1C1910 | surface | Account Details card bg, Preferences card bg, Privacy card bg |
| #302B1C | surfaceVariant | Avatar circle bg, Notification icon container bg |
| #231F12 | surfaceContainer | Text field container fill |
| #4A3200 | primaryContainer | Avatar camera badge bg, Privacy icon container bg |
| #F5D76E | primary | Back icon tint, avatar initials, CTAs bg, focus border, "Save" text, badge icon, member tier text/icon |
| #2C1900 | onPrimary | CTA label text |
| #FFF0C0 | onPrimaryContainer | (config present — not visually used in success) |
| #EDE8D5 | onSurface | Title text, body text, input text, row values, section headings |
| #C4BA94 | onSurfaceVariant | Subtitle email, row label caps, field labels, icon tints, secondary body |
| #726A48 | outline | Input border (unfocused) |
| #3F3822 | outlineVariant | Card borders, row dividers |

**Custom constructs (no new XTheme.Colors entry required):**
- Avatar glow: `MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)` — decorative, inline only
- Bottom gradient: `Brush.verticalGradient(listOf(Color.Transparent, MaterialTheme.colorScheme.background))` — inline in bottomBar slot

---

## Typography Scale

> Typography is app-global (see `_shared/patterns.md` → "Typography"). Each text node maps to an **M3 type-scale role** — implementation uses `style = MaterialTheme.typography.{role}`, not raw `fontSize`. The `Size`/`Weight`/`Letter Spacing` columns are measured from the token inventory; they exist to pick the closest role and flag overrides.

| Usage | M3 Role | Size (sp) | Weight | Letter Spacing | Text Transform | Color Role |
|-------|---------|-----------|--------|----------------|----------------|------------|
| Screen title ("Profile", "Edit Profile") | titleLarge | 20 | Bold (700) | 0 | none | onSurface |
| User display name (avatar section) | titleLarge | 20 | Bold (700) | −0.025em | none | onSurface |
| Avatar initials | headlineSmall | 24 | Bold (700) | 0 | none | primary |
| Email below avatar | bodyMedium | 14 | Normal (400) | 0 | none | onSurfaceVariant |
| Section header caps | labelMedium | 12 | SemiBold (600) | 0.1em | uppercase | onSurfaceVariant |
| Row label caps | labelSmall | 10 | Normal (400) | 0.05em | uppercase | onSurfaceVariant |
| Row value text | bodyLarge | 16 | Medium (500) | 0 | none | onSurface |
| Member tier label | titleMedium | 16 | SemiBold (600) | 0 | none | primary |
| Input field labels | labelLarge | 14 | Normal (400) | 0 | none | onSurfaceVariant |
| Input field text | bodyLarge | 16 | Normal (400) | 0 | none | onSurface |
| Top-bar "Save" action | labelLarge | 14 | Bold (700) | 0 | none | primary |
| Hint text below form | bodySmall | 12 | Normal (400) | 0 | none | onSurfaceVariant |
| Privacy/Notification sub-label | bodySmall | 12 | Normal (400) | 0 | none | onSurfaceVariant |
| Privacy/Notification title | titleSmall | body | Bold (700) | 0 | none | onSurface |
| CTA label ("Edit Profile", "Save Changes") | titleMedium | 16 | Bold (700) | 0 | none | onPrimary |

---

## Spacing Grid

| Context | Property | Value (dp) |
|---------|----------|------------|
| Screen | horizontal padding | 24 |
| Screen | bottom padding (scroll content) | 128 |
| TopAppBar | height | 64 |
| TopAppBar | horizontal padding | 16 |
| TopAppBar | internal gap | 16 |
| Avatar section | top margin | 32 (profile) / 0 (edit, in vertical padding 32 each side) |
| Avatar circle | size | 80 × 80 |
| Account Details card | top margin | 24 |
| Preferences card | top margin | 16 |
| Card | padding | 20 (profile) / 24 (edit privacy card) |
| Row item | vertical padding | 16 |
| Row divider | height | 1 |
| Section header | bottom margin | 8 |
| CTA container | padding | 24 (all) |
| CTA container | extra top padding | 48 (gradient bleed) |
| CTA button | height | 56 |
| Form section | vertical gap between fields | 24 |
| Input field | height | 56 |
| Input field | leading icon left padding | 16 → content left offset 48 (pl-12) |
| Privacy icon container | size | 40 × 40 |
| Camera badge | size | 28 × 28 |
| Toggle | size | 40 × 24 (w-10 × h-6 — custom, see overrides) |
| Toggle thumb | size | 16 × 16 |

---

## Component Tree

### Shared Screen Container (all states)

```
// → ProfileScreen.kt (ViewModel wrapper — pass uiModel + callbacks only)
// → ProfileScreenRoot (state router)
XScreen(
    topBar = {
        XTopAppBar(
            title = stringResource(Res.string.profile_title),
            navigationIcon = {
                XIconButton(
                    icon = painterResource(DesignSystemResources.drawable.arrow_back),
                    contentDescription = stringResource(DesignSystemResources.string.cd_back),
                    colors = XButtonDefaults.iconButtonColors(containerColor = Color.Transparent, contentColor = MaterialTheme.colorScheme.primary),
                    onClick = onBackClick
                )
            }
        )
    },
    bottomBar = { /* per-state slot below */ },
) { /* per-state content */ }
```

---

### Success State

```
// → ProfileScreenRoot content block
Column(
    modifier = Modifier
        .fillMaxSize()
        .verticalScroll(rememberScrollState())
        .padding(horizontal = 24.dp, bottom = 128.dp),
) {
    ProfileAvatarSection(name = uiModel.name, email = uiModel.email)         // → components/ProfileAvatarSection.kt
    AccountDetailCard(name = uiModel.name, email = uiModel.email, tier = uiModel.memberTier) // → components/AccountDetailCard.kt
    PreferencesCard(biometricEnabled = uiModel.biometricEnabled, onBiometricToggle = onBiometricToggle) // → components/PreferencesCard.kt
}

// → XScreen bottomBar
BottomCtaContainer(
    modifier = Modifier.windowInsetsPadding(WindowInsets.navigationBars.exclude(WindowInsets.ime))
) {
    XButton(
        text = stringResource(Res.string.profile_edit_cta),
        leadingIcon = painterResource(Res.drawable.edit),
        onClick = onEditClick,
        modifier = Modifier.fillMaxWidth().height(56.dp),
        shape = RoundedCornerShape(24.dp),
    )
}
```

---

### Loading State
Shared screen — see: `.claude/docs/_shared/designs/loading.png`
Token inventory: `.claude/docs/_shared/designs/extracted/tokens_loading.md`
Routes to: `AppLoadingState()` from `thisissadeghi.designsystem.app`

---

### Failed State
**Skipped** (user opted out). Implementation must still handle Rule 4's Failed UI state; route to `AppErrorState(title, message, onRetry)` from `thisissadeghi.designsystem.app`.

---

### Secondary Screens

#### Edit Profile (edit) — screen

A full sibling screen: own `ProfileEditScreen` + `ProfileEditScreenRoot`, own `ProfileEditRoute`, registered inside the feature's `NavGraphBuilder.profile()` extension. Pushed from `ProfileScreen` via `onEditClick` callback (Rule 10 — no `navController` passed). Routes back via `onBackClick`.

```
// → ProfileEditScreen.kt (ViewModel wrapper)
// → ProfileEditScreenRoot (form shape — no async UiState routing needed for the form itself)
XScreen(
    topBar = {
        XTopAppBar(
            title = stringResource(Res.string.profile_edit_title),
            navigationIcon = {
                XIconButton(
                    icon = painterResource(DesignSystemResources.drawable.arrow_back),
                    contentDescription = stringResource(DesignSystemResources.string.cd_back),
                    colors = XButtonDefaults.iconButtonColors(containerColor = Color.Transparent, contentColor = MaterialTheme.colorScheme.primary),
                    onClick = onBackClick
                )
            },
            actions = {
                XTextButton(
                    text = stringResource(Res.string.profile_edit_save_action),
                    onClick = onSaveClick,
                    colors = XButtonDefaults.textButtonColors(contentColor = MaterialTheme.colorScheme.primary),
                    contentPadding = PaddingValues(horizontal = 8.dp),
                )
            }
        )
    },
    bottomBar = {
        BottomCtaContainer(
            modifier = Modifier.windowInsetsPadding(WindowInsets.navigationBars.exclude(WindowInsets.ime))
        ) {
            XButton(
                text = stringResource(Res.string.profile_edit_save_changes_cta),
                leadingIcon = painterResource(Res.drawable.check),
                onClick = onSaveClick,
                modifier = Modifier.fillMaxWidth().height(56.dp),
                shape = RoundedCornerShape(24.dp),
            )
        }
    },
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 24.dp, bottom = 128.dp),
    ) {
        ProfileEditAvatarSection(       // → components/ProfileEditAvatarSection.kt
            initials = uiModel.initials,
            onChangePhotoClick = onChangePhotoClick,
        )
        ProfileInputForm(               // → components/ProfileInputForm.kt
            name = uiModel.nameInput,
            email = uiModel.emailInput,
            onNameChange = onNameChange,
            onEmailChange = onEmailChange,
        )
        ProfilePrivacyCard(             // → components/ProfilePrivacyCard.kt
            onPrivacyClick = onPrivacyClick,
            onNotificationsClick = onNotificationsClick,
        )
    }
}
```

---

## Component Specs

### ProfileAvatarSection
*File: `components/ProfileAvatarSection.kt`*

```
Column(
    modifier = Modifier.fillMaxWidth().padding(top = 32.dp),
    horizontalAlignment = CenterHorizontally,
) {
    Box {
        // Glow effect (decorative, behind avatar)
        Box(
            Modifier
                .size(80.dp)
                .scale(1.25f)
                .background(
                    MaterialTheme.colorScheme.primary.copy(alpha = 0.1f),
                    CircleShape
                )
                .blur(24.dp)   // experimental — omit if unsupported
        )
        // Avatar circle
        Box(
            Modifier
                .size(80.dp)
                .background(MaterialTheme.colorScheme.surfaceVariant, CircleShape)
                .border(1.dp, MaterialTheme.colorScheme.primary, CircleShape),
            contentAlignment = Center
        ) {
            XText(
                text = initials,
                style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.primary,
            )
        }
    }
    XText(
        text = name,
        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold, letterSpacing = (-0.025f).em),
        color = MaterialTheme.colorScheme.onSurface,
        modifier = Modifier.padding(top = 12.dp),
    )
    XText(
        text = email,
        style = MaterialTheme.typography.bodyMedium,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        modifier = Modifier.padding(top = 4.dp),
    )
}
```

### AccountDetailCard
*File: `components/AccountDetailCard.kt`*

```
Column(modifier = Modifier.padding(top = 24.dp)) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surface, RoundedCornerShape(24.dp))
            .border(1.dp, MaterialTheme.colorScheme.outlineVariant, RoundedCornerShape(24.dp))
            .padding(20.dp)
            .shadow(1.dp, RoundedCornerShape(24.dp))
    ) {
        XText(
            text = stringResource(Res.string.profile_section_account_details),
            style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.SemiBold, letterSpacing = 0.1f.em),
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(bottom = 8.dp),
        )
        ProfileDetailRow(label = Res.string.profile_label_name, value = name)    // → components/ProfileDetailRow.kt
        XHorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
        ProfileDetailRow(label = Res.string.profile_label_email, value = email)
        XHorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
        ProfileMemberTierRow(tier = tier)   // member tier has icon — separate composable in same file (private)
    }
}
```

### ProfileDetailRow (repeated pattern — 2+ uses)
*File: `components/ProfileDetailRow.kt`*

```
Column(modifier = Modifier.fillMaxWidth().padding(vertical = 16.dp)) {
    XText(
        text = stringResource(labelRes),
        style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp, letterSpacing = 0.05f.em),
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        modifier = Modifier.fillMaxWidth(),
    )
    Spacer(Modifier.height(4.dp))
    XText(
        text = value,
        style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Medium),
        color = MaterialTheme.colorScheme.onSurface,
        modifier = Modifier.fillMaxWidth(),
    )
}
```

### PreferencesCard
*File: `components/PreferencesCard.kt`*

```
Column(modifier = Modifier.padding(top = 16.dp)) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surface, RoundedCornerShape(24.dp))
            .border(1.dp, MaterialTheme.colorScheme.outlineVariant, RoundedCornerShape(24.dp))
            .padding(20.dp)
    ) {
        XText(
            text = stringResource(Res.string.profile_section_preferences),
            style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.SemiBold, letterSpacing = 0.1f.em),
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(bottom = 16.dp),
        )
        // Biometric row
        Row(
            modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp), verticalAlignment = Alignment.CenterVertically) {
                XIcon(
                    painter = painterResource(Res.drawable.shield),
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                )
                XText(
                    text = stringResource(Res.string.profile_biometric_security_label),
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurface,
                )
            }
            // Custom-sized toggle (40×24dp — see Component Overrides)
            BiometricToggle(
                checked = biometricEnabled,
                onCheckedChange = onBiometricToggle,
            )  // private composable in same file
        }
    }
}
```

> **BiometricToggle** (private helper in `PreferencesCard.kt`): The design toggle is 40×24dp with 16×16dp thumb — smaller than XSwitch's M3 default (52×32dp). Render as a custom `Box`: checked track `background(primary, CircleShape)` sized `40×24dp`, thumb `background(onPrimary, CircleShape)` `16×16dp` positioned by `offset(x = if(checked) 16.dp else 4.dp)`. Alternatively use `XSwitch(colors = SwitchDefaults.colors(checkedThumbColor = onPrimary, checkedTrackColor = primary))` with `Modifier.size(40.dp, 24.dp)` and accept minor pixel divergence.

### BottomCtaContainer
*File: `components/BottomCtaContainer.kt`*

```
Box(
    modifier = modifier
        .fillMaxWidth()
        .background(
            Brush.verticalGradient(listOf(Color.Transparent, MaterialTheme.colorScheme.background))
        )
        .padding(top = 48.dp, start = 24.dp, end = 24.dp, bottom = 24.dp),
    contentAlignment = Alignment.BottomCenter,
) {
    content()
}
```

### ProfileEditAvatarSection
*File: `components/ProfileEditAvatarSection.kt`*

```
Column(
    modifier = Modifier.fillMaxWidth().padding(vertical = 32.dp),
    horizontalAlignment = CenterHorizontally,
) {
    Box {
        // Avatar circle (same as profile, same token values)
        Box(
            Modifier
                .size(80.dp)
                .background(MaterialTheme.colorScheme.surfaceVariant, CircleShape)
                .border(1.dp, MaterialTheme.colorScheme.primary, CircleShape),
            contentAlignment = Center,
        ) {
            XText(
                text = initials,
                style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.primary,
            )
        }
        // Camera badge (bottom-end overlay)
        Box(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .size(28.dp)
                .background(MaterialTheme.colorScheme.primaryContainer, CircleShape)
                .border(1.dp, MaterialTheme.colorScheme.background, CircleShape),
            contentAlignment = Center,
        ) {
            XIcon(
                painter = painterResource(Res.drawable.cancel_fill),
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(14.dp),
            )
        }
    }
    XTextButton(
        text = stringResource(Res.string.profile_edit_change_photo),
        onClick = onChangePhotoClick,
        colors = XButtonDefaults.textButtonColors(contentColor = MaterialTheme.colorScheme.primary),
        modifier = Modifier.padding(top = 16.dp),
    )
}
```

### ProfileInputForm
*File: `components/ProfileInputForm.kt`*

```
Column(
    modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp),
    verticalArrangement = Arrangement.spacedBy(24.dp),
) {
    // Full Name field
    XTextField(
        value = name,
        onValueChange = onNameChange,
        label = { XText(stringResource(Res.string.profile_edit_field_full_name)) },
        leadingIcon = {
            XIcon(
                painter = painterResource(Res.drawable.person),
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        },
        singleLine = true,
        shape = RoundedCornerShape(24.dp),                          // override: default CircleShape
        colors = XTextFieldDefaults.Colors.copy(
            focusedContainerColor = MaterialTheme.colorScheme.surfaceContainer,
            unfocusedContainerColor = MaterialTheme.colorScheme.surfaceContainer,
            unfocusedIndicatorColor = MaterialTheme.colorScheme.outline,
            focusedIndicatorColor = MaterialTheme.colorScheme.primary,
        ),
        modifier = Modifier.fillMaxWidth().height(56.dp),
    )
    // Email Address field
    XTextField(
        value = email,
        onValueChange = onEmailChange,
        label = { XText(stringResource(Res.string.profile_edit_field_email)) },
        leadingIcon = {
            XIcon(
                painter = painterResource(Res.drawable.mail),
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        },
        singleLine = true,
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
        shape = RoundedCornerShape(24.dp),
        colors = XTextFieldDefaults.Colors.copy(
            focusedContainerColor = MaterialTheme.colorScheme.surfaceContainer,
            unfocusedContainerColor = MaterialTheme.colorScheme.surfaceContainer,
            unfocusedIndicatorColor = MaterialTheme.colorScheme.outline,
            focusedIndicatorColor = MaterialTheme.colorScheme.primary,
        ),
        modifier = Modifier.fillMaxWidth().height(56.dp),
    )
    XText(
        text = stringResource(Res.string.profile_edit_changes_hint),
        style = MaterialTheme.typography.bodySmall,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        textAlign = TextAlign.Center,
        modifier = Modifier.fillMaxWidth().padding(top = 4.dp, horizontal = 16.dp),
    )
}
```

### ProfilePrivacyCard
*File: `components/ProfilePrivacyCard.kt`*

```
Column(modifier = Modifier.padding(top = 40.dp)) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surface, RoundedCornerShape(24.dp))
            .border(1.dp, MaterialTheme.colorScheme.outlineVariant, RoundedCornerShape(24.dp))
            .padding(24.dp)
    ) {
        PrivacyRowItem(                         // → PrivacyRowItem.kt (below, repeated pattern)
            iconRes = Res.drawable.security,
            iconContainerColor = MaterialTheme.colorScheme.primaryContainer,
            iconTint = MaterialTheme.colorScheme.primary,
            titleRes = Res.string.profile_privacy_settings_title,
            subtitleRes = Res.string.profile_privacy_settings_subtitle,
            onClick = onPrivacyClick,
        )
        XHorizontalDivider(
            color = MaterialTheme.colorScheme.outlineVariant,
            modifier = Modifier.padding(vertical = 16.dp),
        )
        PrivacyRowItem(
            iconRes = Res.drawable.notifications,
            iconContainerColor = MaterialTheme.colorScheme.surfaceVariant,
            iconTint = MaterialTheme.colorScheme.onSurfaceVariant,
            titleRes = Res.string.profile_notifications_title,
            subtitleRes = Res.string.profile_notifications_subtitle,
            onClick = onNotificationsClick,
        )
    }
}
```

### PrivacyRowItem (repeated pattern — 2+ uses)
*File: `components/PrivacyRowItem.kt`*

```
Row(
    modifier = Modifier.fillMaxWidth().clickable(onClick = onClick),
    horizontalArrangement = Arrangement.SpaceBetween,
    verticalAlignment = Alignment.CenterVertically,
) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .background(iconContainerColor, RoundedCornerShape(16.dp)),
            contentAlignment = Center,
        ) {
            XIcon(painter = painterResource(iconRes), tint = iconTint)
        }
        Column {
            XText(
                text = stringResource(titleRes),
                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.onSurface,
            )
            XText(
                text = stringResource(subtitleRes),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
    XIcon(
        painter = painterResource(DesignSystemResources.drawable.chevron_right),
        tint = MaterialTheme.colorScheme.onSurfaceVariant,
    )
}
```

---

## String Inventory

> Rule 12: all user-facing strings → `composeResources/values/strings.xml`. Excludes repository data (user name, email at runtime). Shared strings (Retry / common errors) come from `DesignSystemResources`.

| Key | Default (English) value | Screen | Notes |
|-----|-------------------------|--------|-------|
| profile_title | Profile | Profile top bar | |
| cd_back | Back | both | content description (shared — use DesignSystemResources if already defined) |
| profile_section_account_details | ACCOUNT DETAILS | Profile card header | uppercase applied via `TextTransform` in style, or via `toUpperCase()` |
| profile_section_preferences | PREFERENCES | Profile card header | |
| profile_label_name | Name | Account detail row | |
| profile_label_email | Email | Account detail row | |
| profile_label_member_tier | Member Tier | Account detail row | |
| profile_member_tier_value | Gold Private Banking | Tier row value | only if not from data layer |
| profile_biometric_security_label | Biometric Security | Preferences row | |
| profile_edit_cta | Edit Profile | Profile bottom CTA | |
| profile_edit_title | Edit Profile | Edit Profile top bar | |
| profile_edit_save_action | Save | Edit Profile top bar trailing | |
| profile_edit_field_full_name | Full Name | Input label | |
| profile_edit_field_email | Email Address | Input label | |
| profile_edit_changes_hint | Changes will be reflected across the app | Form hint text | |
| profile_edit_change_photo | Change Photo | Avatar section text button | |
| profile_privacy_settings_title | Privacy Settings | Privacy card row title | |
| profile_privacy_settings_subtitle | Manage your visibility and data | Privacy card row subtitle | |
| profile_notifications_title | Notifications | Privacy card row title | |
| profile_notifications_subtitle | Alerts and status updates | Privacy card row subtitle | |
| profile_edit_save_changes_cta | Save Changes | Edit Profile bottom CTA | |

---

## Pre-Implementation Contract

> Architecture rules, color rules, and X-component defaults are project-wide and live in their canonical sources — do not restate them here:
> - Architecture rules → [`_shared/patterns.md`](../../_shared/patterns.md)
> - Color rules → [`m3-colors.md`](../references/m3-colors.md)
> - X-component default-render behavior → [`_shared/X_COMPONENTS_CATALOG.md`](../../_shared/X_COMPONENTS_CATALOG.md)
>
> This contract captures only feature-specific data the implementer cannot derive from those references.

### XTheme Updates Required

| Role | Active Scheme Hex | Counterpart Scheme Hex | Usage |
|------|-------------------|----------------------|-------|
| (none) | — | — | All colors already defined in XDarkColors / XLightColors |

### Typography Updates Required

**Font swap**: none — design uses Manrope, theme already ships Manrope variable (`manrope_variable`).

**Type-scale role overrides:**

| Node | Chosen Role | Stock Role Value | Measured Value | Override |
|------|-------------|------------------|----------------|----------|
| Screen title, user name (h1, h2) | titleLarge | 22sp / Normal (400) | 20sp / Bold (700) | `style = MaterialTheme.typography.titleLarge.copy(fontSize = 20.sp, fontWeight = FontWeight.Bold)` |
| Avatar initials | headlineSmall | 24sp / Normal (400) | 24sp / Bold (700) | `style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold)` |
| Section header caps | labelMedium | 12sp / Medium (500) | 12sp / SemiBold (600) + uppercase + wide tracking | `style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.SemiBold, letterSpacing = 0.1f.em)` + `text.uppercase()` |
| Row label caps | labelSmall | 11sp / Medium (500) | 10sp / Normal (400) + uppercase | `style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp, letterSpacing = 0.05f.em)` + `text.uppercase()` |
| Row value | bodyLarge | 16sp / Normal (400) | 16sp / Medium (500) | `style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Medium)` |
| Member tier label | titleMedium | 16sp / Medium (500) | 16sp / SemiBold (600) | `style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold)` |
| "Save" trailing action | labelLarge | 14sp / Medium (500) | 14sp / Bold (700) | `style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold)` |
| Privacy/Notification title | titleSmall | 14sp / Medium (500) | body / Bold (700) | `style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)` |
| CTA label | titleMedium | 16sp / Medium (500) | 16sp / Bold (700) | `style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)` |

### Color Audit

#### Defined Roles
| Role | Hex | Usage |
|------|-----|-------|
| background | #0F0D09 | Screen bg, gradient |
| surface | #1C1910 | Cards |
| surfaceVariant | #302B1C | Avatar bg, notification icon container |
| surfaceContainer | #231F12 | Input field fill (bg-[#231F12]) |
| primaryContainer | #4A3200 | Camera badge, privacy icon container |
| primary | #F5D76E | Icons, borders, CTAs, tier text |
| onPrimary | #2C1900 | CTA text |
| onSurface | #EDE8D5 | Primary text |
| onSurfaceVariant | #C4BA94 | Secondary text, icon tints |
| outline | #726A48 | Input border unfocused |
| outlineVariant | #3F3822 | Card borders, dividers |

#### Missing Roles (must add before implementation)
| Role | Active Scheme Hex | Counterpart Scheme Hex | Usage |
|------|-------------------|----------------------|-------|
| (none) | — | — | — |

#### Custom Colors (justified exceptions only)
| Name | Hex | Justification |
|------|-----|---------------|
| primary@10% | `primary.copy(alpha=0.1f)` | Decorative avatar glow — inline opacity modifier; no new XTheme.Colors entry |
| Gradient bottom bar | `Color.Transparent → background` | `Brush.verticalGradient(...)` — structural gradient; no new XTheme.Colors entry |

#### Component Overrides (divergences from X-component defaults)

| Component | Property | HTML Value | X-component Default | Override Required |
|-----------|----------|------------|---------------------|-------------------|
| XTextField (name + email) | shape | RoundedCornerShape(24.dp) | CircleShape (singleLine) | `shape = RoundedCornerShape(24.dp)` |
| XTextField (name + email) | containerColor | surfaceContainer (#231F12) | surface | `colors = XTextFieldDefaults.Colors.copy(focusedContainerColor = surfaceContainer, unfocusedContainerColor = surfaceContainer)` |
| XTextField (name + email) | unfocusedBorderColor | outline (#726A48) | onSurface@12% | `unfocusedIndicatorColor = MaterialTheme.colorScheme.outline` |
| XButton (both CTAs) | shape | RoundedCornerShape(24.dp) | CircleShape | `shape = RoundedCornerShape(24.dp)` |
| XButton (both CTAs) | height | 56dp | ~40dp (content-driven) | `Modifier.height(56.dp)` |
| XIconButton (top bar back) | containerColor | Color.Transparent | surface | `colors = XButtonDefaults.iconButtonColors(containerColor = Color.Transparent, contentColor = primary)` |
| BiometricToggle | size | 40×24dp | 52×32dp (XSwitch M3 default) | Custom `Box` implementation or `Modifier.size(40.dp, 24.dp)` on XSwitch |

---

## Post-Implementation Checklist

- [ ] No XTheme updates needed (all M3 roles already defined) ✓
- [ ] Every text node uses `style = MaterialTheme.typography.{role}` with recorded `.copy(...)` overrides — no raw `fontSize`/`fontWeight`
- [ ] `XTextField` shape = `RoundedCornerShape(24.dp)`, containerColor = `surfaceContainer`, unfocusedBorderColor = `outline`
- [ ] `XButton` (both CTAs) shape = `RoundedCornerShape(24.dp)`, `Modifier.height(56.dp)`
- [ ] `XIconButton` back button in both screens: `containerColor = Color.Transparent`, `contentColor = primary`
- [ ] BiometricToggle: 40×24dp, track = primary, thumb = onPrimary
- [ ] `BottomCtaContainer`: `windowInsetsPadding(WindowInsets.navigationBars.exclude(WindowInsets.ime))` on modifier — NOT `navigationBarsPadding()` (would stack on shell `imePadding()`)
- [ ] Avatar glow: `primary.copy(alpha = 0.1f)` — inline; no XTheme.Colors entry
- [ ] Bottom gradient: `Brush.verticalGradient(listOf(Color.Transparent, MaterialTheme.colorScheme.background))` — inline; no XTheme.Colors entry
- [ ] All colors use `MaterialTheme.colorScheme.{role}` — no raw `Color(0xFF...)` literals
- [ ] No images — images.json is empty ✓
- [ ] Every String Inventory key exists in `composeResources/values/strings.xml` and referenced via `stringResource`; no hardcoded display literals
- [ ] `ProfileEditRoute` registered inside `NavGraphBuilder.profile()` as a child `composable<ProfileEditRoute> { ... }` entry
- [ ] `ProfileScreen` pushes to `ProfileEditScreen` via `onEditClick` callback (no `navController` passed)
- [ ] No motion — design is static; no `## Motion` section ✓
- [ ] Build passes: `./gradlew :feature:profile:assembleAndroidMain`
- [ ] Code formatted: `./gradlew :feature:profile:ktlintFormat`
