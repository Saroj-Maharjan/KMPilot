# Phase 1 — Deterministic architecture checker

**Goal:** make the 14 rules enforceable by a failing build instead of a prompt. `./gradlew archTest` reports violations with `file:line` and exits non-zero.

**Why now:** the rules currently live as prose greps inside `.claude/agents/code-quality/code-reviewer.md` — real checks, written in English, executed by a model, non-reproducible, and unavailable to anyone not running Claude Code. Every skeptic asks the same question: *"so an AI checks the AI?"* There is no answer today. This phase is also the **instrument** Phase 2 needs for its compatibility report, and the one the parked benchmark needs for its dataset.

**This phase transcribes existing detection logic. It does not invent new rules.** Source of truth: `code-reviewer.md` Phase 2 gate (lines 29–42), the Phase 3 rule table (50–68), and the Phase 4 integration table (70–78).

**Branch:** `phase-1-arch-checker`

> **Before you start:** read [`README.md`](README.md) → *Branch and PR conventions* and *`update.sh` delivery tiers*.
>
> **Prerequisites:** none — everything needed is already in the repo. `python3` is already a hard requirement of `/design-ui`, and `CLAUDE.md` auto-loads `patterns.md` so the 14 rules are in context without being read explicitly.

---

## In scope

### The checker script

**New file:** `.claude/skills/_shared/kmpilot_check.py`

Joins the three existing shared Python tools — `extract_tokens.py`, `download_assets.py`, `download_font.py` — all invoked as `python3 .claude/skills/_shared/<name>.py`. Same precedent, no new dependency (`python3` is already a hard requirement of `/design-ui`).

**CLI:**

```bash
python3 .claude/skills/_shared/kmpilot_check.py --all           # every feature/ module
python3 .claude/skills/_shared/kmpilot_check.py dashboard       # one feature
python3 .claude/skills/_shared/kmpilot_check.py --all --json-only   # suppress human output
```

Resolve `{PKG_PREFIX}` the same way `create-feature/phases/phase-0-context.md:38-41` does: strip the last segment from a `package` declaration in `core/common/src/**/*.kt`. Do not hardcode `thisissadeghi`.

### Mechanized checks

| ID | Rule | Detection | Severity |
|---|---|---|---|
| `R3` | setState | `_uiModel\.value\s*=` or `_uiState\.value\s*=` in `presentation/` → expect 0 | error |
| `R5` | X-components | Material3 **component** imports → expect 0. Use the explicit forbidden list at `code-reviewer.md:56`. **Allowed:** `material3.MaterialTheme`, `material3.Shapes`, `material3.darkColorScheme`, `material3.lightColorScheme` | error |
| `R7` | Lowercase packages | `package` declaration segments contain no `-`, `_`, or uppercase | error |
| `R8` | DI binding | `singleOf\(.*\)\.bind<` present in `di/*Modules.kt`; a top-level `val {featurename}Module` exists | error |
| `R9` | No UseCases | any type named `*UseCase` → expect 0 | error |
| `R11a` | No `*UiState.kt` | `presentation/**/*UiState.kt` → expect 0 files | error |
| `R11b` | Exactly one UiModel | `presentation/*UiModel.kt` → expect exactly 1 | error |
| `R11c` | No data→presentation import | `import\s+\S+\.presentation\.` under `data/` → expect 0 | error |
| `R12` | No hardcoded strings | `(text\|label\|placeholder\|contentDescription)\s*=\s*"` and `\bX(Text\|Button)\s*\(\s*"` in `presentation/ui/**/*.kt`. **Allowlist:** inside a `@Preview` fixture · control sentinel compared in logic · single-glyph symbol (`$` `₿` `%` `✓`) · repository-supplied data. Also: `strings.xml` must exist when the feature renders text | error |
| `R13` | Single app-shell Scaffold | `\bScaffold\b` or `XScaffold` in `presentation/ui/**/*.kt` → expect 0. Also `contentWindowInsets\|consumeWindowInsets\|safeDrawing\|statusBarsPadding\|imePadding` → expect 0. Plain `navigationBarsPadding()` on a sticky bottom bar → **warning** (prefer `windowInsetsPadding(WindowInsets.navigationBars.exclude(WindowInsets.ime))`) | error / warning |
| `S1` | `Screen.kt` allowlist | Per `presentation/ui/*Screen.kt`: top-level `@Composable fun` names ⊆ {`{Feature}Screen`, `{Feature}ScreenRoot`, `EmptyContent`}. `@Preview`-annotated functions **exempt**. A private `LoadingContent`/`FailedContent` is itself a violation | warning |
| `S2` | Utility placement | Any file under `presentation/ui/components/` containing zero `@Composable` declarations → misplaced utility | warning |
| `S3` | `.app`-tier boundary | Generic (non-`app`) file importing `{PKG_PREFIX}.designsystem.app` / `.data.app` / `.common.app`. **Exclude the one sanctioned file:** `core/data/**/DataModules.kt` | error |
| `S4` | Preview import | `import org\.jetbrains\.compose\.ui\.tooling\.preview\.` → expect 0 (use `androidx.compose.ui.tooling.preview.Preview`) | warning |
| `I1` | Gradle include | `include(":feature:{name}")` in `settings.gradle.kts` | error |
| `I2` | Gradle dependency | `project(":feature:{name}")` in `composeApp/build.gradle.kts` | error |
| `I3` | DI init | `{name}Module` inside `modules(...)` in `initKoin.kt` | error |
| `I4` | Navigation | `{name}(` registered in the NavHost | error |

### Explicitly left to judgment

`R1` (interface+impl semantics, incl. the shared-`data.app` exception) · `R2` (which operations are fallible) · `R4` (all four UI states genuinely handled) · `R10` (callbacks vs `navController`, partly greppable) · `R14` (only applies when the spec's Platform Profile is `platform-capability` / `native-view` / `mixed`).

These stay in `code-reviewer.md`. The agent gets **better** because it stops re-deriving the mechanical half.

### Report format

**Output:** `.claude/docs/_project/check-report.json`

```json
{
  "generatedAt": "2026-07-30T12:00:00Z",
  "pkgPrefix": "thisissadeghi",
  "features": ["dashboard", "send"],
  "violations": [
    {
      "feature": "send",
      "rule": "R12",
      "severity": "error",
      "file": "feature/send/src/commonMain/kotlin/thisissadeghi/send/presentation/ui/components/SendContent.kt",
      "line": 42,
      "message": "hardcoded user-facing string \"Send Bitcoin\""
    }
  ],
  "summary": { "error": 1, "warning": 0, "checked": 19 }
}
```

Exit code **1** when any `error`-severity violation exists, **0** otherwise. Warnings never fail the build.

Human-readable output goes to stdout in `file:line  RULE  message` form so CI logs are readable without parsing JSON.

### Gradle task

Register in root `build.gradle.kts`:

```kotlin
tasks.register<Exec>("archTest") {
    group = "verification"
    description = "Checks feature modules against the KMPilot architecture rules."
    commandLine("python3", ".claude/skills/_shared/kmpilot_check.py", "--all")
}
```

Keep it a **thin wrapper** — all logic stays in the script so it remains runnable standalone (CI, pre-commit, downstream projects that do not want the Gradle task).

`build.gradle.kts` is in `update.sh`'s `MANUAL_PATHS` (update.sh:175). Existing installs will **not** receive this automatically → ship a `[Manual]` CHANGELOG entry containing the exact block to paste.

### Rewire the reviewer

- `.claude/commands/review-feature.md`: read `check-report.json` first; report mechanical findings from it verbatim; only then run the 5 judgment rules.
- `.claude/agents/code-quality/code-reviewer.md`: replace the mechanized rows of the Phase 3 table with "consume `check-report.json`", keeping the judgment rows intact. Do **not** delete the prose patterns — move them into a comment block in the Python script so the rationale travels with the code.

### CI gate

Add an `archTest` step to `.github/workflows/build.yml` (created in Phase 0). Order it **before** `assembleDebug` — the checker is seconds, the build is minutes.

---

## Out of scope

- Auto-fixing violations. Report only.
- Any rule not already documented in `code-reviewer.md`.
- A downstream health badge (parked).
- Touching `feature/` source to fix pre-existing violations. If the checker finds real violations in the shipped example features, record them in the PR description and fix them in a **separate** follow-up branch — a checker PR that also edits 6 features is unreviewable.

---

## Files touched

| Path | Change | `update.sh` tier |
|---|---|---|
| `.claude/skills/_shared/kmpilot_check.py` | new | OVERRIDE — auto-delivers |
| `.claude/commands/review-feature.md` | consume JSON | OVERRIDE |
| `.claude/agents/code-quality/code-reviewer.md` | drop mechanized rows | OVERRIDE |
| `build.gradle.kts` | register `archTest` | **MANUAL** — needs CHANGELOG note |
| `.github/workflows/build.yml` | add gate step | not delivered |
| `CHANGELOG.md` | `[Manual]` upgrade note | not delivered |
| `.claude/skills/_shared/patterns.md` | one line pointing at `archTest` | OVERRIDE |

---

## Steps

1. Read `code-reviewer.md:29-78` and transcribe every mechanized pattern into a rule table inside the script, one function per rule, each returning a list of violation records.
2. Implement `{PKG_PREFIX}` resolution + feature discovery (`feature/*/build.gradle.kts`, skipping Gradle's `build/`).
3. Implement the R12 allowlist carefully — it is the highest false-positive risk. `@Preview` fixtures, sentinels, single glyphs, and repository data must all pass.
4. Run against all 6 shipped features. Triage every hit: genuine violation vs checker bug. Expect several genuine ones.
5. Add the Gradle task; confirm `./gradlew archTest`.
6. Build the negative test: copy `feature/dashboard` to a scratch module, inject one violation per mechanized rule, confirm each is caught with the right `file:line`. Delete the scratch module before the PR.
7. Rewire `review-feature.md` + `code-reviewer.md`.
8. Add the CI step and the CHANGELOG `[Manual]` entry.
9. Open the PR with the triage list from step 4.

---

## Exit criteria

- [ ] `./gradlew archTest` runs and exits 0 on a clean tree.
- [ ] All 19 mechanized checks implemented and individually proven by the negative test.
- [ ] Zero false positives across the 6 shipped example features.
- [ ] `check-report.json` written and schema-stable.
- [ ] `/review-feature dashboard` reads the JSON rather than re-deriving.
- [ ] CI turns red on an injected violation.
- [ ] CHANGELOG carries the `[Manual]` paste-in block for existing installs.

---

## Verification

```bash
# clean run
./gradlew archTest && echo "PASS"

# standalone
python3 .claude/skills/_shared/kmpilot_check.py --all
cat .claude/docs/_project/check-report.json | python3 -m json.tool | head -40

# negative test (scratch module, deleted before PR)
cp -r feature/dashboard feature/checkerprobe
# inject: `_uiModel.value =`, a material3.Button import, a `*UiState.kt`,
# a hardcoded XText("Hello"), a Scaffold in Screen.kt, a stray @Composable
# in Screen.kt, a non-composable file under components/
python3 .claude/skills/_shared/kmpilot_check.py checkerprobe   # expect 1 hit per injection
rm -rf feature/checkerprobe
```

Then run `/review-feature dashboard` and confirm the output cites the JSON and only reasons about R1/R2/R4/R10/R14.

---

## Risks

- **R12 false positives are the main threat.** A checker that cries wolf on `@Preview` strings gets disabled on day one. If the allowlist proves hard to get right, ship R12 as **warning** severity in this phase and promote it to `error` in a follow-up once it is quiet.
- **Pre-existing violations in shipped features** will surface. That is the checker working. Keep the fixes out of this PR.
- **`python3` on Windows.** The installer already documents Git Bash for Windows and `/design-ui` already requires `python3`, so this adds no new constraint — but the Gradle `Exec` task should fail with a readable message if `python3` is absent rather than a raw exec error.
- **Regex-based Kotlin parsing is approximate.** Multi-line declarations and string interpolation will need care. Accept approximation for warnings; keep `error`-severity checks to patterns that cannot reasonably false-positive.

---

## Downstream delivery

The script and the two reviewer docs land in **OVERRIDE** paths → existing installs get them on the next `./update.sh` with no merge conflict. The `archTest` registration in `build.gradle.kts` is **MANUAL** → existing installs paste one block, guided by the CHANGELOG. New installs get everything.
