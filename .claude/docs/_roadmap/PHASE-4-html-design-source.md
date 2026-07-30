# Phase 4 — HTML design source

**Goal:** let `/design-ui` take **any HTML source**, not just Google Stitch. Primary new source: a Figma dev-mode HTML export.

**Why now:** the flagship design step is gated on a Google Labs product that needs a Stitch-issued API key and whose generation calls time out — the skill's own troubleshooting notes say a retry creates duplicate screens (`design-ui/SKILL.md:118`). That is the first thing a new user touches and the most likely place they quit. Figma's user base dwarfs Stitch's, and a Figma export carries **exact** tokens, so `/verify-ui`'s three-way audit stays honest.

**Why it is cheap:** Step 1.15 already separates *acquire* from *tokenize*, and everything downstream of acquisition takes HTML **file paths**.

```
acquire:   get_screen (Stitch MCP) → htmlCode.downloadUrl → curl -o stitch_{state}.html
tokenize:  python3 extract_tokens.py   stitch_{state}.html > tokens_{state}.md
manifests: python3 download_assets.py  --html stitch_{state}.html --manifest-only
           python3 download_font.py    --html stitch_success.html
```

Only the first line is Stitch-specific. Steps 2–3 are already source-agnostic and need **no changes**.

**Branch:** `phase-4-html-design-source`

> **Before you start:** read [`README.md`](README.md) → *Branch and PR conventions* and *`update.sh` delivery tiers*.
>
> **Prerequisites — blocks step 1 (the go/no-go gate):**
> - One **real Figma dev-mode HTML export** as the fixture. Does not exist yet, and Claude cannot produce it — export it by hand (dev mode → copy as code → save as `.html`) and state its path at the start of the session. Prefer an **auto-layout** frame; absolutely-positioned exports tokenize worse.
> - Step 1 runs the existing Python tools against that fixture *before any skill file is touched*. If the tokenizer cannot handle Figma output, the phase's scope changes — surface it, don't absorb it.

---

## In scope

### `/design-ui --from-html <path|url>`

New acquisition branch in `phase-1-design.md` / `phase-1-finalize.md`:

1. Skip Project Init's Stitch project creation and every `mcp__stitch__*` call.
2. Place the source HTML at `.claude/docs/{featurename}/designs/extracted/stitch_success.html` — **keep the existing filename** so every downstream reference, resume check, and manifest argument keeps working unchanged.
3. Run `extract_tokens.py`, `download_assets.py --manifest-only`, and `download_font.py` exactly as they run today.
4. Continue at **Step 1.16** (colour / typography / motion audit) → blueprint. Nothing after 1.15 changes.

For a URL source, `curl -sL -o` into the same path, then the same `wc -c` zero-byte check the Stitch path already does.

### Screen dimensions

The Stitch path records `width`/`height` from the `get_screen` response. An HTML file has no such envelope. Options, in order of preference:

1. Parse a viewport/root width from the HTML if present.
2. Prompt the user once via `AskUserQuestion` (offer 390×844 as the phone default).
3. Fall back to the phone default and record `dimensionsSource: "assumed"` in `stitch-project.json`.

Blueprint consumers use dimensions for layout ratios, so an assumed value must be visible, not silent.

### Config additions

`.claude/docs/_project/stitch-project.json` gains, per feature:

```json
"features": {
  "settings": {
    "designSource": "html",
    "sourceHtmlOrigin": "figma-export",
    "dimensionsSource": "assumed"
  }
}
```

`designSource` defaults to `"stitch"` when absent, so every existing config stays valid. `/verify-ui` reads it only to label its report — the audit logic is identical because it audits against HTML either way.

### Multi-state and secondary screens

Stitch mode generates loading / failed / empty / secondary screens on demand. In HTML mode the user supplies whatever they have:

- `--from-html <success.html>` — success only. Loading/Failed fall back to the shared `AppLoadingState`/`AppErrorState`, which is the documented default anyway.
- `--from-html-state loading=<path> --from-html-state empty=<path>` — optional extra states, written to the same `stitch_{state}.html` filenames.
- Secondary screens follow the same `stitch_{role}.html` convention.

Keep the flag surface small in this phase: success-only is the 90% case. Extra states can land as a follow-up if asked for.

### Documentation

- A short **"Exporting from Figma"** section in `design-ui/references/` — dev-mode → copy as code → save as `.html`, plus the one caveat that absolutely-positioned exports tokenize worse than auto-layout frames.
- State plainly that **screenshots are not supported, and why**: HTML inferred from a PNG would make `/verify-ui` audit code against a guess and quietly falsify the "verified against the design" claim. If sketch-mode is ever built, it must stamp `tokenSource: approximated` and `/verify-ui` must say so in its report.

---

## Out of scope

- Screenshot / image input (rejected on principle above).
- A Figma REST API integration or Figma MCP. File-based export only — no API key, no new dependency.
- Changing `extract_tokens.py`, `download_assets.py`, or `download_font.py`. If any of them turns out to need a change for non-Stitch HTML, that is a finding to record, not scope to absorb quietly.
- Changing the blueprint spec or `/verify-ui`'s audit logic.

---

## Files touched

| Path | Change | `update.sh` tier |
|---|---|---|
| `.claude/skills/design-ui/SKILL.md` | `--from-html` in usage + preflight | OVERRIDE |
| `.claude/skills/design-ui/phases/phase-0-preflight.md` | skip Stitch checks in HTML mode | OVERRIDE |
| `.claude/skills/design-ui/phases/phase-1-design.md` | acquisition branch | OVERRIDE |
| `.claude/skills/design-ui/phases/phase-1-finalize.md` | Step 1.15 branch | OVERRIDE |
| `.claude/skills/design-ui/phases/phase-init.md` | HTML mode needs no Stitch project | OVERRIDE |
| `.claude/skills/design-ui/references/figma-export.md` | new | OVERRIDE |
| `.claude/skills/verify-ui/SKILL.md` | label `designSource` in the report | OVERRIDE |
| `README.md` | design sources | — |
| `CHANGELOG.md` | `[Tooling]` entry | — |

No Python changes expected.

---

## Steps

1. Export one real Figma frame to HTML as the fixture. Run `extract_tokens.py` on it **by hand first** — before touching any skill file, confirm the existing tokenizer produces a usable `tokens_success.md` from Figma output. If it does not, stop and reassess scope.
2. Same manual check for `download_assets.py --manifest-only` (Figma icon markup will differ from Material Symbols `<span data-icon>`) and `download_font.py`.
3. Add the `--from-html` branch to preflight + Step 1.15, keeping filenames identical.
4. Add `designSource` / `dimensionsSource` to `stitch-project.json` handling, defaulting to `"stitch"`.
5. Handle dimensions per the preference order above.
6. Run `/design-ui --from-html` end to end and confirm a complete blueprint.
7. Implement the feature from that blueprint, then run `/verify-ui` and confirm the audit behaves normally and labels the source.
8. Write `figma-export.md`; note the screenshot rejection.
9. Open the PR with the fixture HTML and the generated blueprint attached.

---

## Exit criteria

- [ ] `/design-ui --from-html <figma-export.html>` produces a complete blueprint with **zero** `mcp__stitch__*` calls.
- [ ] `tokens_success.md` and `icons.json` generated by the **unmodified** shared Python scripts.
- [ ] `stitch-project.json` records `designSource: "html"`; absent field still reads as `"stitch"`.
- [ ] Stitch mode is byte-for-byte unaffected — re-run an existing feature's design and diff the artifacts.
- [ ] `/verify-ui` audits an implementation of the HTML-sourced blueprint and labels the source.
- [ ] `figma-export.md` documents the export path and the screenshot rejection.

---

## Verification

```bash
# 1. prove the existing tooling handles Figma HTML before changing any skill
python3 .claude/skills/_shared/extract_tokens.py /tmp/figma-export.html | head -40
python3 .claude/skills/_shared/download_assets.py --html /tmp/figma-export.html --manifest-only …
python3 .claude/skills/_shared/download_font.py  --html /tmp/figma-export.html …

# 2. end to end, in Claude Code
#    /design-ui --from-html /tmp/figma-export.html
#    → expect a blueprint at .claude/docs/{name}/designs/{name}_blueprint.md
#    /create-feature {name}
#    /verify-ui {name}

# 3. regression: Stitch mode unchanged
#    re-run /design-ui for an existing feature and diff designs/extracted/
```

---

## Risks

- **Figma HTML may tokenize poorly.** Dev-mode exports are often absolutely-positioned with inline styles rather than the utility-class markup `extract_tokens.py` was written against. Step 1 exists precisely to find this out before committing to the phase. If the tokenizer needs real work, that is a scope change worth surfacing — not silently absorbing.
- **Icon extraction is Stitch-shaped.** `download_assets.py` looks for `<span class="material-symbols-*" data-icon="…">`. Figma exports inline SVG instead. Expect a gap: the icons manifest may come back empty, which is a degraded-but-honest outcome (the blueprint just carries no icon mappings). Record it; do not fake it.
- **Dimension assumptions leak into layout.** An assumed viewport width silently distorts ratio-based layout guidance. Keep `dimensionsSource` visible in the blueprint.
- **Two acquisition paths mean two paths to regress.** The Stitch regression diff in the exit criteria is not optional.

---

## Downstream delivery

Everything lands in `.claude/skills/**` → **OVERRIDE** tier, so existing installs receive `--from-html` on the next `./update.sh` with no merge conflict and no action required.
