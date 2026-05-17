#!/usr/bin/env python3
"""
Stitch HTML Element & Class Extractor

Walks every DOM element in Stitch-generated HTML and produces a guaranteed-
complete inventory of every element and every Tailwind class. Also extracts:
- Tailwind config overrides (custom colors, borderRadius, fontFamily) including
  array-valued fontFamily entries.
- Global <style> rules (e.g. body font-family, material-symbols variation
  settings) — these affect every element and would otherwise be invisible.

For every recognised Tailwind class, emits a deterministic dp/sp/color
interpretation alongside the raw class so the LLM is not re-deriving the
conversion every run. Unknown classes pass through unchanged.

Usage:
    python3 extract_tokens.py <html_file>

Dependencies: Python 3 stdlib only (html.parser, re, sys)
"""

import re
import sys
from html.parser import HTMLParser


# ---------------------------------------------------------------------------
# Tailwind config extractor
# ---------------------------------------------------------------------------

def extract_tailwind_config(html_content):
    """Extract custom Tailwind config overrides from the HTML.

    These are project-specific values the LLM cannot infer from Tailwind
    defaults alone (e.g. custom color names, non-standard border radii,
    custom font families).
    """
    config = {"colors": {}, "borderRadius": {}, "fontFamily": {}}

    patterns = [
        r'<script[^>]*id=["\']tailwind-config["\'][^>]*>(.*?)</script>',
        r'tailwind\.config\s*=\s*(\{.*?\})\s*;?\s*</script>',
        r'<script[^>]*>\s*tailwind\.config\s*=\s*(\{.*?\})\s*;?\s*</script>',
    ]

    config_text = None
    for pattern in patterns:
        match = re.search(pattern, html_content, re.DOTALL)
        if match:
            config_text = match.group(1)
            break

    if not config_text:
        return config

    for section in ("colors", "borderRadius", "fontFamily"):
        section_match = re.search(
            rf'{section}\s*:\s*\{{(.*?)\}}(?:\s*,|\s*\}})', config_text, re.DOTALL
        )
        if section_match:
            _parse_kv_block(section_match.group(1), config[section])

    return config


def _parse_kv_block(text, target):
    """Parse key: value pairs from a JS object literal.

    Handles:
    - Flat strings:   key: 'value'
    - Nested objects: key: { sub: 'value' }
    - Arrays:         key: ['Manrope', 'sans-serif']  (captures first entry)
    """
    # Flat: key: 'value'
    for m in re.finditer(r"""['"]?([\w-]+)['"]?\s*:\s*['"]([^'"]+)['"]""", text):
        target[m.group(1)] = m.group(2)

    # Array-valued: key: ['first', 'second']  -> captures 'first'
    for m in re.finditer(
        r"""['"]?([\w-]+)['"]?\s*:\s*\[\s*['"]([^'"]+)['"]""", text
    ):
        target[m.group(1)] = m.group(2)

    # One-level nested: key: { sub: 'val', sub2: ['a','b'], ... }
    for m in re.finditer(r"""['"]?([\w-]+)['"]?\s*:\s*\{([^}]+)\}""", text):
        parent = m.group(1)
        body = m.group(2)
        for nested in re.finditer(
            r"""['"]?([\w-]+)['"]?\s*:\s*['"]([^'"]+)['"]""", body
        ):
            target[f"{parent}-{nested.group(1)}"] = nested.group(2)
        for nested in re.finditer(
            r"""['"]?([\w-]+)['"]?\s*:\s*\[\s*['"]([^'"]+)['"]""", body
        ):
            target[f"{parent}-{nested.group(1)}"] = nested.group(2)


# ---------------------------------------------------------------------------
# Tailwind class -> design token converter
# ---------------------------------------------------------------------------

TEXT_SIZES = {
    "xs": 12, "sm": 14, "base": 16, "md": 16, "lg": 18, "xl": 20,
    "2xl": 24, "3xl": 30, "4xl": 36, "5xl": 48, "6xl": 60, "7xl": 72,
    "8xl": 96, "9xl": 128,
}

FONT_WEIGHTS = {
    "thin": "100 (Thin)", "extralight": "200 (ExtraLight)",
    "light": "300 (Light)", "normal": "400 (Normal)",
    "medium": "500 (Medium)", "semibold": "600 (SemiBold)",
    "bold": "700 (Bold)", "extrabold": "800 (ExtraBold)",
    "black": "900 (Black)",
}

LETTER_SPACINGS_EM = {
    "tighter": -0.05, "tight": -0.025, "normal": 0,
    "wide": 0.025, "wider": 0.05, "widest": 0.1,
}

LINE_HEIGHTS = {
    "none": "1.0", "tight": "1.25", "snug": "1.375",
    "normal": "1.5", "relaxed": "1.625", "loose": "2.0",
}

# Default Tailwind borderRadius (overridden by tailwind.config when present)
DEFAULT_RADIUS_DP = {
    "none": "0dp", "sm": "2dp", "": "4dp", "md": "6dp",
    "lg": "8dp", "xl": "12dp", "2xl": "16dp", "3xl": "24dp",
    "full": "CircleShape",
}

# Default Tailwind colors that are NOT in custom config (slate, yellow, etc.)
# We only emit the role name + an indication of the palette family — the
# actual hex is resolved by the LLM from Tailwind defaults.
DEFAULT_PALETTE_FAMILIES = {
    "slate", "gray", "zinc", "neutral", "stone", "red", "orange", "amber",
    "yellow", "lime", "green", "emerald", "teal", "cyan", "sky", "blue",
    "indigo", "violet", "purple", "fuchsia", "pink", "rose",
    "white", "black", "transparent", "current",
}

# Color-prefix classes that produce visual output (vs e.g. "ring-offset")
COLOR_PREFIXES = {
    "bg": "background",
    "text": "color",
    "border": "border-color",
    "fill": "fill",
    "stroke": "stroke",
    "ring": "ring-color",
    "outline": "outline-color",
    "shadow": "shadow-color",
    "decoration": "text-decoration-color",
    "divide": "divide-color",
    "placeholder": "placeholder-color",
    "accent": "accent-color",
}


def _format_dp(value):
    if value == int(value):
        return f"{int(value)}dp"
    return f"{value}dp"


def _spacing_to_dp(token):
    """Convert a Tailwind spacing token to dp / % / arbitrary string.

    Handles:
    - Numeric scale: '4' -> '16dp', '1.5' -> '6dp'
    - Arbitrary:     '[40px]' -> '40px', '[280px]' -> '280px'
    - Keywords:      'px' -> '1dp', 'full' -> '100%', 'auto', 'screen'
    - Fractions:     '1/2' -> '50%', '2/3' -> '66.67%'
    """
    if token.startswith("[") and token.endswith("]"):
        return token[1:-1]
    if token == "px":
        return "1dp"
    if token == "full":
        return "100%"
    if token == "auto":
        return "auto"
    if token == "screen":
        return "100vh/vw"
    m = re.match(r"^(\d+)/(\d+)$", token)
    if m:
        num, denom = int(m.group(1)), int(m.group(2))
        pct = round(100 * num / denom, 2)
        return f"{pct}%"
    try:
        n = float(token)
        return _format_dp(n * 4)
    except ValueError:
        return None


def _parse_color_class(cls, tw_config):
    """For 'bg-primary/10', 'text-slate-400', 'border-outline/20', return:
    (prefix, color_name, opacity_str_or_None) or None if not a color class.
    """
    m = re.match(r"^(bg|text|border|fill|stroke|ring|outline|shadow|"
                 r"decoration|divide|placeholder|accent)-(.+)$", cls)
    if not m:
        return None
    prefix, rest = m.group(1), m.group(2)

    # Special non-color tokens for these prefixes (skip them — not colors)
    skip = {
        "text": {"xs", "sm", "base", "lg", "xl", "2xl", "3xl", "4xl", "5xl",
                 "6xl", "7xl", "8xl", "9xl", "left", "right", "center",
                 "justify", "start", "end", "wrap", "nowrap", "balance",
                 "ellipsis", "clip"},
        "border": {"0", "1", "2", "4", "8", "solid", "dashed", "dotted",
                   "double", "hidden", "none", "collapse", "separate",
                   "x", "y", "t", "r", "b", "l"},
        "ring": {"0", "1", "2", "4", "8", "inset"},
        "outline": {"none", "0", "1", "2", "4", "8", "dashed", "dotted",
                    "solid", "double"},
        "shadow": {"sm", "md", "lg", "xl", "2xl", "inner", "none"},
        "fill": {"none", "current"},
    }
    head = rest.split("/", 1)[0]
    if prefix in skip and head in skip[prefix]:
        return None

    # Pure border without value (e.g. "border-2") — handled elsewhere
    if "/" in rest:
        color_part, opacity = rest.split("/", 1)
    else:
        color_part, opacity = rest, None

    # Validate it looks like a color (custom config name OR known palette family)
    color_lc = color_part.lower()
    family = color_lc.split("-", 1)[0]
    is_custom = color_part in tw_config.get("colors", {})
    is_palette = family in DEFAULT_PALETTE_FAMILIES
    is_arbitrary = color_part.startswith("[") and color_part.endswith("]")
    if not (is_custom or is_palette or is_arbitrary):
        return None

    return (prefix, color_part, opacity)


def _format_color_value(color_part, opacity, tw_config):
    if color_part.startswith("[") and color_part.endswith("]"):
        base = color_part[1:-1]
    elif color_part in tw_config.get("colors", {}):
        base = f"{color_part} ({tw_config['colors'][color_part]})"
    else:
        base = color_part
    if opacity is None:
        return base
    return f"{base} @ {opacity}%"


def _radius_dp(token, tw_config):
    """Look up radius in tailwind.config first, fall back to defaults."""
    overrides = tw_config.get("borderRadius", {})
    # Stitch's config uses 'DEFAULT' key for `rounded` (no suffix)
    key = token if token else "DEFAULT"
    if key in overrides:
        val = overrides[key]
        return _rem_or_px_to_dp(val)
    if token in DEFAULT_RADIUS_DP:
        return DEFAULT_RADIUS_DP[token]
    if token.startswith("[") and token.endswith("]"):
        return token[1:-1]
    return None


def _rem_or_px_to_dp(s):
    """'1.5rem' -> '24dp', '8px' -> '8dp', '9999px' -> 'CircleShape'."""
    s = s.strip()
    m = re.match(r"^([\d.]+)rem$", s)
    if m:
        return _format_dp(float(m.group(1)) * 16)
    m = re.match(r"^([\d.]+)px$", s)
    if m:
        n = float(m.group(1))
        if n >= 9999:
            return "CircleShape"
        return _format_dp(n)
    return s


def convert_class(cls, tw_config):
    """Convert a single Tailwind class to a token-level interpretation.

    Returns a short string like '16dp top margin' or 'primary @ 10%' or None
    if the class is a layout primitive / state variant / unrecognised.
    """
    if not cls:
        return None

    # Strip variant prefixes like 'dark:', 'hover:', 'focus:', 'placeholder:'
    if ":" in cls:
        return None  # variant — web-only or theme-bound, not a fixed token

    # Negative prefix
    sign = ""
    if cls.startswith("-"):
        sign = "-"
        cls = cls[1:]

    # --- Padding / margin: p-N, pt-N, pr-N, pb-N, pl-N, px-N, py-N (and m-) -
    m = re.match(r"^([pm])([xytrbl])?-(.+)$", cls)
    if m:
        prop, axis, val = m.group(1), m.group(2), m.group(3)
        dp = _spacing_to_dp(val)
        if dp is not None:
            kind = "padding" if prop == "p" else "margin"
            if axis:
                axis_label = {
                    "x": "horizontal", "y": "vertical",
                    "t": "top", "r": "right", "b": "bottom", "l": "left",
                }[axis]
                return f"{axis_label} {kind}: {sign}{dp}"
            return f"{kind}: {sign}{dp}"

    # --- Gap: gap-N, gap-x-N, gap-y-N --------------------------------------
    m = re.match(r"^gap(?:-([xy]))?-(.+)$", cls)
    if m:
        axis, val = m.group(1), m.group(2)
        dp = _spacing_to_dp(val)
        if dp is not None:
            ax = f"-{axis}" if axis else ""
            return f"gap{ax}: {sign}{dp}"

    # --- Space-y / space-x (margin between children) -----------------------
    m = re.match(r"^space-([xy])-(.+)$", cls)
    if m:
        axis, val = m.group(1), m.group(2)
        dp = _spacing_to_dp(val)
        if dp is not None:
            direction = "horizontally" if axis == "x" else "vertically"
            return f"children spaced {sign}{dp} {direction}"

    # --- Position: top-N, right-N, bottom-N, left-N -----------------------
    m = re.match(r"^(top|right|bottom|left)-(.+)$", cls)
    if m:
        prop, val = m.group(1), m.group(2)
        dp = _spacing_to_dp(val)
        if dp is not None:
            return f"{prop}: {sign}{dp}"

    # --- Inset: inset-N, inset-x-N, inset-y-N -----------------------------
    m = re.match(r"^inset(?:-([xy]))?-(.+)$", cls)
    if m:
        axis, val = m.group(1), m.group(2)
        dp = _spacing_to_dp(val)
        if dp is not None:
            ax = f"-{axis}" if axis else ""
            return f"inset{ax}: {sign}{dp}"

    # --- Width / height / size --------------------------------------------
    m = re.match(r"^(w|h|size)-(.+)$", cls)
    if m:
        prop, val = m.group(1), m.group(2)
        dp = _spacing_to_dp(val)
        if dp is not None:
            labels = {"w": "width", "h": "height", "size": "size"}
            return f"{labels[prop]}: {sign}{dp}"

    # --- Min/max width / height ------------------------------------------
    m = re.match(r"^(min|max)-(w|h)-(.+)$", cls)
    if m:
        bound, dim, val = m.group(1), m.group(2), m.group(3)
        dp = _spacing_to_dp(val)
        if dp is not None:
            labels = {"w": "width", "h": "height"}
            return f"{bound}-{labels[dim]}: {sign}{dp}"

    # --- Text size: text-xs, text-sm, ..., text-[40px] ---------------------
    # NOTE: text-[#abc] is an arbitrary color, not a font-size — only treat
    # the arbitrary value as a size when it parses as a length.
    m = re.match(r"^text-(\[.+\]|[\w]+)$", cls)
    if m:
        val = m.group(1)
        if val in TEXT_SIZES:
            return f"font-size: {TEXT_SIZES[val]}sp"
        if val.startswith("[") and val.endswith("]"):
            inner = val[1:-1]
            if re.match(r"^[\d.]+(px|rem|em|sp|dp|%)?$", inner):
                if inner.endswith("px"):
                    return f"font-size: {inner[:-2]}sp"
                return f"font-size: {inner}"
            # Not a length — fall through to color path

    # --- Font weight: font-bold, font-medium, ... -------------------------
    m = re.match(r"^font-([a-z]+)$", cls)
    if m and m.group(1) in FONT_WEIGHTS:
        return f"font-weight: {FONT_WEIGHTS[m.group(1)]}"

    # --- Letter spacing: tracking-tight, tracking-wider, ... ---------------
    m = re.match(r"^tracking-(.+)$", cls)
    if m:
        v = m.group(1)
        if v in LETTER_SPACINGS_EM:
            em = LETTER_SPACINGS_EM[v]
            return f"letter-spacing: {em}em (× font-size for sp)"
        if v.startswith("[") and v.endswith("]"):
            return f"letter-spacing: {v[1:-1]}"

    # --- Line height: leading-none, leading-relaxed, leading-{n} ----------
    m = re.match(r"^leading-(.+)$", cls)
    if m:
        v = m.group(1)
        if v in LINE_HEIGHTS:
            return f"line-height: {LINE_HEIGHTS[v]}× font-size"
        if v.startswith("[") and v.endswith("]"):
            inner = v[1:-1]
            if inner.endswith("px"):
                return f"line-height: {inner[:-2]}sp"
            return f"line-height: {inner}"
        dp = _spacing_to_dp(v)
        if dp is not None:
            return f"line-height: {dp}"

    # --- Border radius: rounded, rounded-xl, rounded-[12px] --------------
    if cls == "rounded":
        r = _radius_dp("", tw_config)
        if r:
            return f"corner-radius: {r}"
    m = re.match(r"^rounded-(.+)$", cls)
    if m:
        token = m.group(1)
        # Could be axis-prefixed (rounded-t-lg, rounded-tl-xl) — skip those as
        # too rare to over-engineer. Strip axis prefix for value lookup.
        axis_match = re.match(r"^(t|r|b|l|tl|tr|bl|br|s|e|ss|se|es|ee)-(.+)$", token)
        if axis_match:
            axis, val = axis_match.group(1), axis_match.group(2)
            r = _radius_dp(val, tw_config)
            if r:
                return f"corner-radius ({axis}): {r}"
        else:
            r = _radius_dp(token, tw_config)
            if r:
                return f"corner-radius: {r}"

    # --- Border width: border, border-2, border-[1px], border-t, border-x-2 -
    if cls == "border":
        return "border-width: 1dp"
    if cls in {"border-t", "border-r", "border-b", "border-l",
               "border-x", "border-y"}:
        return f"{cls.replace('border-', 'border-')} width: 1dp"
    m = re.match(r"^border-(\d+|\[.+\])$", cls)
    if m:
        v = m.group(1)
        if v.startswith("[") and v.endswith("]"):
            return f"border-width: {v[1:-1]}"
        return f"border-width: {v}dp"
    # border-x, border-y, border-t, ... with explicit width
    m = re.match(r"^border-([xytrbl])-(\d+|\[.+\])$", cls)
    if m:
        ax, v = m.group(1), m.group(2)
        if v.startswith("[") and v.endswith("]"):
            return f"border-{ax} width: {v[1:-1]}"
        return f"border-{ax} width: {v}dp"

    # --- Color classes (bg-, text-, border-, ring-, ...) -----------------
    parsed = _parse_color_class(cls, tw_config)
    if parsed is not None:
        prefix, color_part, opacity = parsed
        prop_label = COLOR_PREFIXES.get(prefix, prefix)
        return f"{prop_label}: {_format_color_value(color_part, opacity, tw_config)}"

    # --- Opacity ----------------------------------------------------------
    m = re.match(r"^opacity-(\d+|\[.+\])$", cls)
    if m:
        v = m.group(1)
        if v.startswith("[") and v.endswith("]"):
            return f"opacity: {v[1:-1]}"
        return f"opacity: {int(v) / 100:g}"

    # --- Aspect ratio -----------------------------------------------------
    if cls == "aspect-square":
        return "aspect-ratio: 1:1"
    if cls == "aspect-video":
        return "aspect-ratio: 16:9"

    # --- Shadow (named) ---------------------------------------------------
    if cls in {"shadow-sm", "shadow", "shadow-md", "shadow-lg", "shadow-xl",
               "shadow-2xl"}:
        depths = {"shadow-sm": 1, "shadow": 2, "shadow-md": 4,
                  "shadow-lg": 8, "shadow-xl": 16, "shadow-2xl": 24}
        return f"shadow: ~{depths[cls]}dp elevation"

    return None


def annotate_special_class(cls):
    """Some classes have non-obvious semantics worth flagging even when we
    don't convert them. Returns a hint string or None.
    """
    if cls.startswith("space-y-") or cls.startswith("space-x-"):
        return "(applies margin between children, not on parent)"
    if cls in {"absolute", "fixed", "sticky"}:
        return f"(positioning: {cls} — Compose: Box overlay or BottomBar slot)"
    if cls == "translate-y-1/2" or cls == "-translate-y-1/2":
        return "(50% Y translate — typically for vertical centering)"
    if cls.startswith("z-"):
        return "(z-index — Compose has no z-index; layering is order-based)"
    if cls.startswith("transition-") or cls == "transition":
        return "(CSS transition — no Compose equivalent at token level)"
    if cls.startswith("hover:") or cls.startswith("focus:") or \
       cls.startswith("active:") or cls.startswith("group-hover:"):
        return "(state variant — handled by Compose interaction states)"
    return None


# ---------------------------------------------------------------------------
# HTML parser
# ---------------------------------------------------------------------------

class StitchHTMLParser(HTMLParser):
    """Walk every DOM element. Capture tag, classes, text, section comments,
    and global <style> blocks.
    """

    SKIP_TAGS = {"script", "link", "meta", "head", "title"}

    def __init__(self):
        super().__init__()
        self.elements = []
        self.style_blocks = []
        self._stack = []
        self._section = None
        self._counter = 0
        self._skip_depth = 0
        self._in_style = False

    def handle_comment(self, data):
        stripped = data.strip()
        if stripped:
            self._section = stripped

    def handle_starttag(self, tag, attrs):
        if tag == "style":
            self._skip_depth += 1
            self._in_style = True
            self.style_blocks.append("")
            return
        if tag in self.SKIP_TAGS:
            self._skip_depth += 1
            return
        if self._skip_depth:
            return

        self._stack.append(tag)
        self._counter += 1

        attrs_dict = dict(attrs)
        classes = attrs_dict.get("class", "").strip()

        self.elements.append({
            "index": self._counter,
            "tag": tag,
            "path": " > ".join(self._stack),
            "classes": classes,
            "class_list": classes.split() if classes else [],
            "section": self._section,
            "text": "",
            "inline_style": attrs_dict.get("style", ""),
        })

    def handle_startendtag(self, tag, attrs):
        # XHTML-style void tags: <img />, <input />, <br/>
        if tag in self.SKIP_TAGS or self._skip_depth:
            return
        self._counter += 1
        attrs_dict = dict(attrs)
        classes = attrs_dict.get("class", "").strip()
        path = " > ".join(self._stack + [tag])
        self.elements.append({
            "index": self._counter,
            "tag": tag,
            "path": path,
            "classes": classes,
            "class_list": classes.split() if classes else [],
            "section": self._section,
            "text": "",
            "inline_style": attrs_dict.get("style", ""),
        })

    def handle_endtag(self, tag):
        if tag == "style":
            self._skip_depth = max(0, self._skip_depth - 1)
            self._in_style = False
            return
        if tag in self.SKIP_TAGS:
            self._skip_depth = max(0, self._skip_depth - 1)
            return
        if self._skip_depth:
            return
        if self._stack and self._stack[-1] == tag:
            self._stack.pop()

    def handle_data(self, data):
        if self._in_style:
            self.style_blocks[-1] += data
            return
        if self._skip_depth:
            return
        text = data.strip()
        if text and self.elements:
            el = self.elements[-1]
            el["text"] = f"{el['text']} {text}".strip() if el["text"] else text


# ---------------------------------------------------------------------------
# Output
# ---------------------------------------------------------------------------

def format_output(elements, tw_config, style_blocks, filename):
    out = []
    out.append(f"# Token Inventory: {filename}")
    out.append("")

    # --- Config overrides ---
    out.append("## Tailwind Config Overrides")
    out.append("")
    has_config = False
    for section, label in [("colors", "colors"),
                           ("borderRadius", "borderRadius"),
                           ("fontFamily", "fontFamily")]:
        for name, val in sorted(tw_config.get(section, {}).items()):
            out.append(f"- **{label}.{name}**: `{val}`")
            has_config = True
    if not has_config:
        out.append("_(none found)_")
    out.append("")

    # --- Global styles ---
    if any(b.strip() for b in style_blocks):
        out.append("## Global Styles")
        out.append("")
        out.append("Inline `<style>` rules that apply globally — these affect "
                   "every matching element regardless of class list.")
        out.append("")
        out.append("```css")
        for block in style_blocks:
            stripped = block.strip()
            if stripped:
                out.append(stripped)
        out.append("```")
        out.append("")

    # --- Elements ---
    out.append("## Elements")
    out.append("")
    out.append("Each class is followed by its deterministic token "
               "interpretation when one applies (e.g. `mt-4 → margin-top: "
               "16dp`). Classes with no annotation are layout primitives, "
               "state variants, or unrecognised — interpret them yourself.")
    out.append("")
    out.append("**Element formats:**")
    out.append("- **Visual elements** (any class converts to a visual token, "
               "or has inline style) get a full block with one line per class.")
    out.append("- **Layout-only elements** (only structural classes like "
               "`flex`, `items-center`, `justify-between`) get a single "
               "compact line — they still appear in order so structural "
               "mismatches (Row vs Column, arrangement, alignment) remain "
               "visible.")
    out.append("- **Classless text children** (e.g. `<span>Label</span>` "
               "inside a button) also appear as a one-liner with their text, "
               "so sibling DOM order inside a flex container is preserved — "
               "compare it against the Compose content lambda order.")
    out.append("")

    current_section = None
    elements_with_classes = 0
    total_classes = 0
    converted_classes = 0
    layout_only_elements = 0

    for el in elements:
        if el["section"] and el["section"] != current_section:
            current_section = el["section"]
            out.append(f"### <!-- {current_section} -->")
            out.append("")

        class_list = el["class_list"]
        inline = el["inline_style"]
        text = el["text"]
        if not class_list and not inline and not text:
            continue

        elements_with_classes += 1
        total_classes += len(class_list)

        # Pre-compute conversions to decide visual vs layout-only.
        conversions = [
            (cls, convert_class(cls, tw_config), annotate_special_class(cls))
            for cls in class_list
        ]
        has_visual = any(conv for _, conv, _ in conversions) or bool(inline)

        text_preview = text[:60]
        if len(text) > 60:
            text_preview += "..."
        text_hint = f' — "{text_preview}"' if text_preview else ""

        if not has_visual:
            # Non-visual element: compact one-liner. Covers two cases —
            # (a) layout-only elements (flex/items-center/justify-between) so
            # structural mismatches like Row vs Column or arrangement stay
            # visible, and (b) classless text-bearing children (e.g. a bare
            # <span>Label</span> inside a button) so sibling DOM order in
            # flex containers is preserved.
            classes_str = " ".join(class_list)
            classes_segment = f" `{classes_str}`" if classes_str else ""
            out.append(
                f"- [{el['index']}] `<{el['tag']}>`{classes_segment}{text_hint}"
            )
            layout_only_elements += 1
            continue

        # Visual element: full block format.
        out.append(f"**[{el['index']}] `<{el['tag']}>`{text_hint}**")

        if class_list:
            out.append("")
            for i, (cls, conversion, hint) in enumerate(conversions, 1):
                line = f"{i}. `{cls}`"
                if conversion:
                    line += f" → {conversion}"
                    converted_classes += 1
                elif hint:
                    line += f" {hint}"
                out.append(line)

        if inline:
            out.append(f"- _inline style_: `{inline}`")

        out.append("")

    # --- Summary ---
    visual_elements = elements_with_classes - layout_only_elements
    out.append("---")
    out.append(
        f"**Total elements**: {len(elements)} | "
        f"**Visual**: {visual_elements} | "
        f"**Layout-only**: {layout_only_elements} | "
        f"**Total classes**: {total_classes} | "
        f"**Auto-converted**: {converted_classes}"
        f" ({100 * converted_classes // max(total_classes, 1)}%)"
    )
    out.append("")

    return "\n".join(out)


# ---------------------------------------------------------------------------
# Main
# ---------------------------------------------------------------------------

def main():
    if len(sys.argv) < 2:
        print("Usage: python3 extract_tokens.py <html_file>", file=sys.stderr)
        sys.exit(1)

    with open(sys.argv[1], "r", encoding="utf-8") as f:
        html = f.read()

    tw_config = extract_tailwind_config(html)

    parser = StitchHTMLParser()
    parser.feed(html)

    filename = sys.argv[1].rsplit("/", 1)[-1] if "/" in sys.argv[1] else sys.argv[1]
    print(format_output(parser.elements, tw_config, parser.style_blocks, filename))


if __name__ == "__main__":
    main()
