# Token Inventory: stitch_failed.html

## Tailwind Config Overrides

- **colors.background**: `#0F0D09`
- **colors.danger**: `#FF6B6B`
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
- **colors.success**: `#4ADE80`
- **colors.surface**: `#1C1910`
- **colors.surface-variant**: `#302B1C`
- **borderRadius.2xl**: `1.5rem`
- **borderRadius.DEFAULT**: `0.5rem`
- **borderRadius.full**: `9999px`
- **borderRadius.lg**: `1rem`
- **borderRadius.md**: `0.75rem`
- **borderRadius.sm**: `0.375rem`
- **borderRadius.xl**: `1.25rem`
- **fontFamily.body**: `Manrope`
- **fontFamily.display**: `Manrope`
- **fontFamily.headline**: `Manrope`
- **fontFamily.label**: `Manrope`

## Global Styles

Inline `<style>` rules that apply globally — these affect every matching element regardless of class list.

```css
body {
      margin: 0;
      padding: 0;
      background-color: #0F0D09;
      font-family: 'Manrope', sans-serif;
      -webkit-font-smoothing: antialiased;
    }
    .material-symbols-outlined {
      font-variation-settings: 'FILL' 0, 'wght' 400, 'GRAD' 0, 'opsz' 48;
      display: inline-block;
      line-height: 1;
      text-transform: none;
      letter-spacing: normal;
      word-wrap: normal;
      white-space: nowrap;
      direction: ltr;
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

1. `bg-[#0F0D09]` → background: #0F0D09
2. `text-[#EDE8D5]` → color: #EDE8D5
3. `flex`
4. `items-center`
5. `justify-center`
6. `min-h-screen` → min-height: 100vh/vw

### <!-- Error Screen Canvas -->

**[3] `<main>`**

1. `flex`
2. `flex-col`
3. `items-center`
4. `justify-center`
5. `w-full` → width: 100%
6. `max-w-md`
7. `px-8` → horizontal padding: 32dp
8. `text-center`

### <!-- Hero Visual / Icon -->

**[4] `<div>`**

1. `mb-8` → bottom margin: 32dp
2. `flex`
3. `flex-col`
4. `items-center`

**[5] `<div>`**

1. `relative`
2. `mb-6` → bottom margin: 24dp

### <!-- Subtle Glow Effect Behind Icon -->

**[6] `<div>`**

1. `absolute` (positioning: absolute — Compose: Box overlay or BottomBar slot)
2. `inset-0` → inset: 0dp
3. `bg-[#FFB4AB]` → background: #FFB4AB
4. `opacity-10` → opacity: 0.1
5. `blur-3xl`
6. `rounded-full` → corner-radius: CircleShape

**[7] `<span>` — "warning"**

1. `material-symbols-outlined`
2. `text-[80px]` → font-size: 80sp
3. `text-[#FFB4AB]` → color: #FFB4AB
4. `relative`
5. `z-10` (z-index — Compose has no z-index; layering is order-based)

### <!-- Error Message Content -->

**[8] `<h1>` — "Something went wrong"**

1. `font-headline`
2. `text-[20px]` → font-size: 20sp
3. `font-semibold` → font-weight: 600 (SemiBold)
4. `text-[#C4BA94]` → color: #C4BA94
5. `tracking-tight` → letter-spacing: -0.025em (× font-size for sp)
6. `leading-relaxed` → line-height: 1.625× font-size

**[9] `<p>` — "An unexpected error occurred. Please try again or check your..."**

1. `font-body`
2. `text-sm` → font-size: 14sp
3. `text-[#726A48]` → color: #726A48
4. `mt-2` → top margin: 8dp
5. `max-w-[240px]` → max-width: 240px

### <!-- Actions -->

**[10] `<div>`**

1. `w-full` → width: 100%
2. `flex`
3. `flex-col`
4. `items-center`
5. `gap-4` → gap: 16dp

**[11] `<button>` — "Retry"**

1. `w-full` → width: 100%
2. `max-w-[200px]` → max-width: 200px
3. `h-[56px]` → height: 56px
4. `bg-[#F5D76E]` → background: #F5D76E
5. `text-[#2C1900]` → color: #2C1900
6. `font-bold` → font-weight: 700 (Bold)
7. `text-base` → font-size: 16sp
8. `rounded-md` → corner-radius: 12dp
9. `hover:opacity-90` (state variant — handled by Compose interaction states)
10. `active:scale-95` (state variant — handled by Compose interaction states)
11. `transition-all` (CSS transition — no Compose equivalent at token level)
12. `flex`
13. `items-center`
14. `justify-center`

### <!-- Secondary Action (Ghost style for visual hierarchy) -->

**[12] `<button>` — "Return to Dashboard"**

1. `text-[#C4BA94]` → color: #C4BA94
2. `font-medium` → font-weight: 500 (Medium)
3. `text-sm` → font-size: 14sp
4. `hover:text-[#F5D76E]` (state variant — handled by Compose interaction states)
5. `transition-colors` (CSS transition — no Compose equivalent at token level)
6. `py-2` → vertical padding: 8dp

### <!-- Decorative Image (Abstract Premium Texture) -->

**[13] `<div>`**

1. `absolute` (positioning: absolute — Compose: Box overlay or BottomBar slot)
2. `bottom-0` → bottom: 0dp
3. `left-0` → left: 0dp
4. `w-full` → width: 100%
5. `h-[265px]` → height: 265px
6. `pointer-events-none`
7. `opacity-20` → opacity: 0.2

**[14] `<img>`**

1. `w-full` → width: 100%
2. `h-full` → height: 100%
3. `object-cover`

### <!-- Accessibility Meta -->

- [15] `<div>` `sr-only` — "Error Page: Something went wrong. System encountered an issu..."
---
**Total elements**: 15 | **Visual**: 13 | **Layout-only**: 2 | **Total classes**: 79 | **Auto-converted**: 43 (54%)

