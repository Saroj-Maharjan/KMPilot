# Phase 2 — Adopt into an existing project

**Goal:** let someone install the KMPilot pipeline **into a KMP repo they already have**, instead of generating a new project from a template.

**Why now:** `install.sh` is greenfield-only — clone → trim → rename → `rm -rf .git` (install.sh:728). Every developer with a KMP app already in production, which is exactly the group with real drift pain and real influence, has no way in. It shows in the numbers: 26 unique cloners in 14 days, 23 lifetime installs. People look, see "start over", and leave. This is the ~10× multiplier on addressable market and the prerequisite for the parked `migrate-feature` work.

**Branch:** `phase-2-adopt-mode`

> **Before you start:** read [`README.md`](README.md) → *Branch and PR conventions* and *`update.sh` delivery tiers*. Phase 1 must be merged — the compatibility report is powered by its checker.
>
> **Prerequisites — blocks step 1:**
> - A **scratch KMP target repo** outside KMPilot: minimal Compose Multiplatform, Koin and Ktor present, no KMPilot anything. Does not exist yet. Create it first (Claude can generate it), keep it outside this repo, and commit only its generation script here so the test is repeatable.
> - Decide where it lives (e.g. `~/KMPProjects/adopt-target/`) and state the path at the start of the session.

---

## In scope

### `install.sh --adopt`

Add a mode to the existing installer rather than writing a second script — it already owns the colour/banner layer, TTY prompting, name/package validation, `sedi` helpers, and the `.kmpilot.json` writer. All of that is reused.

**Flow:**

1. **Detect.** Run in the target repo's root. Confirm `settings.gradle.kts` exists and the Kotlin Multiplatform plugin is applied somewhere. Derive the target's package prefix from its existing sources (same strip-last-segment logic as `phase-0-context.md:38-41`). Inventory what it already has: Koin? Ktor? Compose Multiplatform? DataStore? its own `Result`/`Either` type? its own design system?
2. **Compatibility report** (powered by the Phase 1 checker). Example shape:

   ```
   Detected: Kotlin Multiplatform ✓  Compose Multiplatform ✓  Koin ✓  Ktor ✓
   Missing:  Either type            no design system         no UiState

   Plan: vendor core/common, core/data, core/designsystem as new modules
   Enforceable here today: 9 of 14 rules (R1,R2,R4,R10,R14 need the core types)
   ```

   The user confirms before anything is written.
3. **Vendor.** Copy `core/common`, `core/data`, `core/designsystem` into the target, renamed to its package prefix (reuse `scripts/rename.sh`). Strip the `app` tiers exactly as install.sh:525-528 does — a downstream project's own `data.app`/`common.app` content is theirs to write.
4. **Wire.** Add `include(":core:…")` lines to their `settings.gradle.kts`; add the three `implementation(project(":core:…"))` entries to their app module. Both edits must be idempotent — re-running `--adopt` must not duplicate lines.
5. **Configure.** Write `CLAUDE.md`, `.claude/` (skills, agents, commands, hooks, `settings.json`), and `.kmpilot.json`.
6. **Prove it.** Print the next command (`/create-feature …`) and tell the user to run `./gradlew archTest` after their first feature.

### `.kmpilot.json` additions

Per the release back-compat contract, **add fields only** — never rename or repurpose a shipped one.

```json
{
  "kmpilotVersion": "0.2.0",
  "projectName": "TheirApp",
  "packagePrefix": "com.acme.theirapp",
  "installMode": "adopt",
  "adoptedCoreModules": ["core:common", "core:data", "core:designsystem"],
  "templateRepo": "…", "upstreamPkg": "thisissadeghi", "upstreamName": "KMPilot",
  "installedAt": "…"
}
```

`installMode` defaults to `"template"` when absent, so every existing manifest stays valid. `update.sh` must tolerate both modes — an adopted project has no `composeApp`/`androidApp`/`iosApp` of KMPilot's shape, so its `MANUAL_PATHS` handling needs a guard.

### Safety rails

Adopt-mode writes into a repo the user already cares about. Non-negotiable:

- **Refuse to run on a dirty working tree.** `git status --porcelain` must be empty.
- **Refuse to run twice** without `--force` (detect an existing `.kmpilot.json`).
- **Never delete or overwrite** anything outside the paths it creates. If a target path exists, stop and report rather than clobber.
- **Dry-run first:** `--adopt --dry-run` prints every file it would write or edit and exits.

---

## Out of scope

- **The capability-map model** (enforce rules against *their* `Result`/design system/DI instead of vendoring ours). It is the destination, but every skill currently hardcodes `Either`, `UiState`, and `X*` — that refactor is speculative before a single adopter exists.
- **Maven Central publishing** of `core/*` (parked). Vendoring avoids it entirely for now.
- **Android→KMP migration** (`migrate-feature`) — parked, and it builds on this phase.
- Rewriting a target's existing architecture. Adopt-mode adds a pipeline; it does not refactor their code.

---

## Files touched

| Path | Change | `update.sh` tier |
|---|---|---|
| `install.sh` | `--adopt`, `--dry-run`, `--force`; detection + vendoring + compat report | not delivered (regenerated per release) |
| `update.sh` | tolerate `installMode: "adopt"`; guard app-module `MANUAL_PATHS` | TIER1 (merged) |
| `scripts/rename.sh` | possibly extend for a core-only rename | stripped on install |
| `.github/workflows/release.yml` | publish nothing new; verify `--adopt` help text in the smoke check | not delivered |
| `README.md` | Adopt door in Quick Start | — |
| `CHANGELOG.md` | `[Tooling]` entry | — |

---

## Steps

1. Build a **scratch KMP target** outside this repo — a minimal Compose Multiplatform project with Koin and Ktor but no KMPilot anything. This is the fixture for the whole phase; commit its generation script (not the project) so the test is repeatable.
2. Implement detection + `--dry-run` first. Get the compat report accurate before writing a single file.
3. Implement vendoring + rename against the scratch target. Verify `./gradlew assembleDebug` still passes there.
4. Implement the wiring edits, idempotently. Re-run `--adopt --force` and confirm no duplicate `include` or dependency lines.
5. Write `.claude/`, `CLAUDE.md`, `.kmpilot.json`.
6. Run `/create-feature` inside the scratch target end to end. Then `./gradlew archTest` (Phase 1) there.
7. Teach `update.sh` about `installMode`.
8. README Quick Start gains the Adopt path; CHANGELOG entry.
9. Open the PR with a transcript of the scratch-target run.

---

## Exit criteria

- [ ] `--adopt --dry-run` prints an accurate file plan and writes nothing.
- [ ] `--adopt` succeeds on a scratch KMP repo that is not KMPilot.
- [ ] `./gradlew assembleDebug` passes in the scratch target after adoption.
- [ ] `/create-feature` generates a working feature there.
- [ ] `./gradlew archTest` green there.
- [ ] Re-running `--adopt --force` is idempotent — no duplicated Gradle lines.
- [ ] Refuses to run on a dirty tree, and refuses a second run without `--force`.
- [ ] `update.sh` runs cleanly in an adopted project.

---

## Verification

```bash
# in the scratch target, NOT in KMPilot
git status --porcelain          # must be empty before adopting
bash /path/to/install.sh --adopt --dry-run
bash /path/to/install.sh --adopt
./gradlew assembleDebug
./gradlew archTest
# then, inside Claude Code in that repo:
#   /create-feature a simple profile screen
./gradlew archTest
./update.sh --dry-run           # must not explode on installMode: adopt
```

---

## Risks

- **The design system is the adoption blocker.** A team that already owns a component library will not accept `XButton`. Vendoring is still the right first step — it proves whether anyone wants this at all — but expect this to be the top piece of feedback, and treat it as the signal to start the capability-map work.
- **Invasiveness.** Writing three Gradle modules plus `.claude/` into someone's repo is a big diff. `--dry-run`, the dirty-tree refusal, and never-overwrite are what make it acceptable.
- **Target-shape variance is unbounded.** Someone's `settings.gradle.kts` may use version catalogs differently, a different module layout, or `libs.versions.toml` names that collide. Detect and **report unsupported shapes** rather than guessing; a clear "I can't safely adopt this repo, here's why" beats a broken edit.
- **`rename.sh` was written for whole-tree renames.** A core-only rename may need a scoped mode; check before assuming reuse.

---

## Downstream delivery

`install.sh` is a release asset, so adopt-mode reaches users the moment a release ships it. `update.sh` is TIER1 (3-way merged) so the `installMode` tolerance propagates. Existing template-mode projects are unaffected — `installMode` is absent and defaults to `"template"`.
