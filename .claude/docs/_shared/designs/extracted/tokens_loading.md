# Token Inventory: stitch_loading.html

## Tailwind Config Overrides

- **colors.background**: `#0F0D09`
- **colors.error**: `#FFB4AB`
- **colors.error-container**: `#93000A`
- **colors.on-error**: `#690005`
- **colors.on-error-container**: `#FFDAD6`
- **colors.on-primary**: `#2C1900`
- **colors.on-primary-container**: `#FFF0C0`
- **colors.on-surface**: `#EDE8D5`
- **colors.on-surface-variant**: `#C4BA94`
- **colors.outline**: `#726A48`
- **colors.outline-variant**: `#3F3822`
- **colors.primary**: `#F5D76E`
- **colors.primary-container**: `#4A3200`
- **colors.surface**: `#1C1910`
- **colors.surface-variant**: `#302B1C`
- **borderRadius.2xl**: `20px`
- **borderRadius.DEFAULT**: `0.5rem`
- **borderRadius.full**: `9999px`
- **borderRadius.lg**: `1rem`
- **borderRadius.xl**: `1.5rem`
- **fontFamily.body**: `Manrope`
- **fontFamily.display**: `Manrope`
- **fontFamily.headline**: `Manrope`
- **fontFamily.label**: `Manrope`

## Global Styles

Inline `<style>` rules that apply globally — these affect every matching element regardless of class list.

```css
body {
            font-family: 'Manrope', sans-serif;
            background-color: #0F0D09;
            margin: 0;
            padding: 0;
            overflow: hidden;
        }
        .material-symbols-outlined {
            font-variation-settings: 'FILL' 0, 'wght' 400, 'GRAD' 0, 'opsz' 24;
        }
body {
      min-height: max(884px, 100dvh);
    }
body {
      min-height: max(884px, 100dvh);
    }
```

## Elements

Each class is followed by its deterministic token interpretation when one applies (e.g. `mt-4 → margin-top: 16dp`). Classes with no annotation are layout primitives, state variants, or unrecognised — interpret them yourself.

**Element formats:**
- **Visual elements** (any class converts to a visual token, or has inline style) get a full block with one line per class.
- **Layout-only elements** (only structural classes like `flex`, `items-center`, `justify-between`) get a single compact line — they still appear in order so structural mismatches (Row vs Column, arrangement, alignment) remain visible.

- [1] `<html>` `dark`
**[2] `<body>`**

1. `bg-background` → background: background (#0F0D09)
2. `text-on-surface` → color: on-surface (#EDE8D5)
3. `antialiased`

### <!-- Main Canvas: Full-screen centered loading state.
        In accordance with the Shell Visibility & Relevance mandate, 
        TopAppBar and BottomNavBar are suppressed for this focused, task-based loading screen. -->

**[3] `<main>`**

1. `relative`
2. `h-screen` → height: 100vh/vw
3. `w-screen` → width: 100vh/vw
4. `flex`
5. `flex-col`
6. `items-center`
7. `justify-center`
8. `bg-background` → background: background (#0F0D09)
9. `p-6` → padding: 24dp

### <!-- Loading Container -->

- [4] `<div>` `relative flex items-center justify-center`
### <!-- Exterior Decorative Ring (Subtle Glow) -->

**[5] `<div>`**

1. `absolute` (positioning: absolute — Compose: Box overlay or BottomBar slot)
2. `w-24` → width: 96dp
3. `h-24` → height: 96dp
4. `rounded-full` → corner-radius: CircleShape
5. `border` → border-width: 1dp
6. `border-outline-variant/30` → border-color: outline-variant (#3F3822) @ 30%
7. `blur-sm`

### <!-- Background Track for the circular indicator -->

**[6] `<div>`**

1. `w-16` → width: 64dp
2. `h-16` → height: 64dp
3. `rounded-full` → corner-radius: CircleShape
4. `border-4` → border-width: 4dp
5. `border-surface-variant` → border-color: surface-variant (#302B1C)

### <!-- We use a partial border to simulate a modern loading arc without CSS animation -->

**[7] `<div>`**

1. `absolute` (positioning: absolute — Compose: Box overlay or BottomBar slot)
2. `w-16` → width: 64dp
3. `h-16` → height: 64dp
4. `rounded-full` → corner-radius: CircleShape
5. `border-4` → border-width: 4dp
6. `border-transparent` → border-color: transparent
7. `border-t-primary`
8. `border-r-primary/40`
9. `rotate-45`

### <!-- Central Brand Identity Detail -->

**[8] `<div>`**

1. `absolute` (positioning: absolute — Compose: Box overlay or BottomBar slot)
2. `w-2` → width: 8dp
3. `h-2` → height: 8dp
4. `rounded-full` → corner-radius: CircleShape
5. `bg-primary` → background: primary (#F5D76E)
6. `shadow-[0_0_8px_rgba(245,215,110,0.6)]` → shadow-color: 0_0_8px_rgba(245,215,110,0.6)

### <!-- Optional Branding Anchor Layer (Silent/Subtle) -->

**[9] `<div>`**

1. `absolute` (positioning: absolute — Compose: Box overlay or BottomBar slot)
2. `bottom-12` → bottom: 48dp
3. `left-0` → left: 0dp
4. `w-full` → width: 100%
5. `flex`
6. `justify-center`
7. `opacity-10` → opacity: 0.1
8. `pointer-events-none`

**[10] `<div>`**

1. `w-12` → width: 48dp
2. `h-1` → height: 4dp
3. `bg-gradient-to-r`
4. `from-transparent`
5. `via-primary`
6. `to-transparent`
7. `rounded-full` → corner-radius: CircleShape

### <!-- Background Visual Context: 
        Hidden texture layer to maintain the "Digital Luxury" feel. -->

**[11] `<div>`**

1. `fixed` (positioning: fixed — Compose: Box overlay or BottomBar slot)
2. `inset-0` → inset: 0dp
3. `pointer-events-none`
4. `z-[-1]` (z-index — Compose has no z-index; layering is order-based)
5. `opacity-20` → opacity: 0.2

**[12] `<div>`**

1. `absolute` (positioning: absolute — Compose: Box overlay or BottomBar slot)
2. `inset-0` → inset: 0dp
3. `bg-[radial-gradient(circle_at_center,_var(--tw-gradient-stops))]` → background: radial-gradient(circle_at_center,_var(--tw-gradient-stops))
4. `from-surface-variant/20`
5. `via-background`
6. `to-background`

---
**Total elements**: 12 | **Visual**: 10 | **Layout-only**: 2 | **Total classes**: 70 | **Auto-converted**: 37 (52%)

