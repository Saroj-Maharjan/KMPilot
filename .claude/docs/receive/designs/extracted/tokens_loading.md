# Token Inventory: stitch_loading.html

## Tailwind Config Overrides

_(none found)_

## Global Styles

Inline `<style>` rules that apply globally — these affect every matching element regardless of class list.

```css
body { background-color: #0D0919; color: #e7e0ec; }
        .material-symbols-outlined { font-variation-settings: 'FILL' 0, 'wght' 400, 'GRAD' 0, 'opsz' 24; }
        .bottom-gradient { background: linear-gradient(to top, #0D0919 0%, transparent 100%); }
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

1. `font-body-md`
2. `antialiased`
3. `min-h-screen` → min-height: 100vh/vw
4. `flex`
5. `flex-col`

### <!-- TopAppBar -->

**[3] `<nav>`**

1. `flex`
2. `justify-between`
3. `items-center`
4. `px-4` → horizontal padding: 16dp
5. `py-4` → vertical padding: 16dp
6. `w-full` → width: 100%
7. `bg-[#0D0919]` → background: #0D0919
8. `sticky` (positioning: sticky — Compose: Box overlay or BottomBar slot)
9. `top-0` → top: 0dp
10. `z-50` (z-index — Compose has no z-index; layering is order-based)

**[4] `<button>`**

1. `text-[#E9E0FF]` → color: #E9E0FF
2. `hover:bg-white/5` (state variant — handled by Compose interaction states)
3. `transition-colors` (CSS transition — no Compose equivalent at token level)
4. `active:scale-95` (state variant — handled by Compose interaction states)
5. `duration-200`
6. `p-2` → padding: 8dp
7. `rounded-full` → corner-radius: CircleShape

- [5] `<span>` `material-symbols-outlined` — "arrow_back"
**[6] `<h1>` — "Receive"**

1. `text-[20px]` → font-size: 20sp
2. `font-bold` → font-weight: 700 (Bold)
3. `text-[#E9E0FF]` → color: #E9E0FF
4. `font-manrope`
5. `antialiased`
6. `tracking-[-0.5px]` → letter-spacing: -0.5px

**[7] `<div>`**

1. `w-11` → width: 44dp

### <!-- Main Content -->

**[8] `<main>`**

1. `flex-1`
2. `flex`
3. `items-center`
4. `justify-center`
5. `p-4` → padding: 16dp

**[9] `<div>`**

1. `inline-block`
2. `h-12` → height: 48dp
3. `w-12` → width: 48dp
4. `animate-spin`
5. `rounded-full` → corner-radius: CircleShape
6. `border-4` → border-width: 4dp
7. `border-solid`
8. `border-[#9D70FF]` → border-width: #9D70FF
9. `border-r-transparent`
10. `align-[-0.125em]`
11. `motion-reduce:animate-[spin_1.5s_linear_infinite]`

- [10] `<span>` `!absolute !-m-px !h-px !w-px !overflow-hidden !whitespace-nowrap !border-0 !p-0 ![clip:rect(0,0,0,0)]` — "Loading..."
---
**Total elements**: 10 | **Visual**: 7 | **Layout-only**: 3 | **Total classes**: 56 | **Auto-converted**: 20 (35%)

