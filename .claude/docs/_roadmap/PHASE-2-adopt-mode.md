# Phase 2 — Adopt into an existing project

**Goal:** let someone install the KMPilot pipeline **into a KMP repo they already have**, instead of generating a new project from a template.

**Why now:** `install.sh` is greenfield-only — clone → trim → rename → `rm -rf .git` (install.sh:728). Every developer with a KMP app already in production, which is exactly the group with real drift pain and real influence, has no way in. It shows in the numbers: 26 unique cloners in 14 days, 23 lifetime installs. People look, see "start over", and leave. This is the ~10× multiplier on addressable market and the prerequisite for the parked `migrate-feature` work.

**Branch:** `phase-2-adopt-mode`

> **Before you start:** read [`README.md`](README.md) → *Branch and PR conventions* and *`update.sh` delivery tiers*. Phase 1 must be merged — the compatibility report is powered by its checker.
>
> **Prerequisites — blocks step 1:**
> - A **scratch KMP target repo** outside KMPilot: minimal Compose Multiplatform, Koin and Ktor present, no KMPilot anything. Does not exist yet. Create it first (Claude can generate it), keep it outside this repo, and commit only its generation script here so the test is repeatable.
> - **Path decided (2026-08-01): `~/KMPProjects/adopt-target/`** — confirmed free, sibling of the other KMP projects, outside this repo. Generating it is the phase's first task.

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
| `update.sh` | tolerate `installMode: "adopt"`; guard app-module `MANUAL_PATHS`; sidecar redirect | TIER1 (merged) |
| `scripts/rename.sh` | `--paths=<dir,dir>` + `--no-readme` (scoped, README-safe rename) | stripped on install |
| `scripts/make-adopt-target.sh` | **new** — generates the scratch KMP fixture | stripped on install |
| `scripts/adopt-matrix.sh` | **new** — 17-variant compatibility matrix (mostly detection-only) | stripped on install |
| `.github/workflows/build.yml` | runs the matrix on every PR (~seconds, no Gradle) | not delivered |
| `.claude/skills/_shared/kmpilot_check.py` | `--all` with 0 features = exit 0; app module from `.kmpilot.json` (I2/I3/I4); NavHost found by content | OVERRIDE |
| `.claude/skills/_shared/patterns.md` | integration table uses `{APP_MODULE}` / `{CATALOG}` | OVERRIDE |
| `.claude/skills/create-feature/architecture/build-gradle-template.md` | `{CATALOG}` accessor + adopt-mode self-contained `android { }` block | OVERRIDE |
| `.claude/skills/create-feature/phases/phase-0-context.md` | Step 0.0 reads the manifest (`INSTALL_MODE`, `CATALOG`, `APP_MODULE`) | OVERRIDE |
| `.github/workflows/release.yml` | smoke check: published asset parses and still offers `--adopt` | not delivered |
| `README.md` | short Adopt door in Quick Start (detail lives in `ADOPTING.md`) | — |
| `ADOPTING.md` | **new** — what adopt does, what it refuses, the compatibility table | stripped on install |
| `CHANGELOG.md` | `[Tooling]` entries | — |

### Decided while building (differs from the plan above)

| Decision | Why |
|---|---|
| **Separate `kmpilotLibs` catalog** (`gradle/kmpilot.versions.toml`) rather than merging ~30 aliases into the target's `libs` | An alias that already exists under the same name but points at a *different artifact* (their `compose-ui` = androidx, ours = jetbrains) would silently bind the wrong dependency. A second catalog cannot collide, is idempotent by construction, and is deleted in one line. Cost: the 3 vendored `core/*` build files diverge from upstream, so `update.sh --core` will conflict there. |
| **Target's versions always win.** Kotlin / AGP / Compose / SDK levels are read from the target and written into `kmpilot.versions.toml` | Two Kotlin plugin versions on one build classpath is a hard failure. Below KMPilot's tested floor we warn, never refuse — refusing every repo not on Kotlin 2.4 would close the door this phase exists to open. |
| **Vendored `core/*` build files rewritten to be self-contained** (own `compileSdk`/`minSdk`/`androidResources`/JVM target) | They otherwise inherit KMPilot's root `allprojects {}` conventions, which an adopted repo has no reason to have — and its root build is not ours to rewrite. The same applies to generated feature modules, hence the `{CATALOG}` + self-contained block in the build template. |
| **`archTest` appended to the target's root `build.gradle.kts`** — the one edit to that file | The exit criteria require `./gradlew archTest` in the adopted repo. Additive, idempotent, guarded on the task name, and the checker still runs standalone without it. |
| **`KMPILOT_SOURCE_DIR`** stages from a local checkout instead of a release clone | An unreleased installer cannot be tested against a real target otherwise: the staged clone would be the last *published* tag, which predates the very `rename.sh` flags adopt mode calls. |
| **Sidecar rule compares content, not existence** | Writing `CLAUDE.kmpilot.md` whenever `CLAUDE.md` merely exists makes `--adopt --force` spawn sidecars of its own output. Three outcomes instead: absent → write, identical → skip, differs → sidecar. |

---

## Steps

1. Build a **scratch KMP target** outside this repo — a minimal Compose Multiplatform project with Koin and Ktor but no KMPilot anything. This is the fixture for the whole phase; commit its generation script (not the project) so the test is repeatable.
2. Implement detection + `--dry-run` first. Get the compat report accurate before writing a single file.
3. Implement vendoring + rename against the scratch target. Verify `./gradlew assembleDebug` still passes there.
4. Implement the wiring edits, idempotently. Re-run `--adopt --force` and confirm no duplicate `include` or dependency lines.
5. Write `.claude/`, `CLAUDE.md`, `.kmpilot.json`.
6. Run `/create-feature` inside the scratch target end to end. Then `./gradlew archTest` (Phase 1) there.
7. Teach `update.sh` about `installMode`.
8. README Quick Start gains a short Adopt door; `ADOPTING.md` carries the detail; CHANGELOG entry.
9. Open the PR with a transcript of the scratch-target run.

---

## Exit criteria

- [x] `--adopt --dry-run` prints an accurate file plan and writes nothing.
- [x] `--adopt` succeeds on a scratch KMP repo that is not KMPilot.
- [x] `./gradlew assembleDebug` passes in the scratch target after adoption.
- [x] `/create-feature` generates a working feature there — verified against a real
      hand-built KMP project (`Bookshelf`: `androidApp` + `shared` + two top-level feature
      modules + two of its own `core/*`), not the fixture. It scaffolded `feature/bookdetail`,
      spliced `kmpilotModules` into the host's **Compose** Koin bootstrap, registered
      navigation, and left every pre-existing module untouched bar one hoisted callback. **Prerequisites all verified;
      the skill run itself is outstanding** — it needs a Claude Code session opened *in*
      `~/KMPProjects/adopt-target`. What that run depends on has been proven by hand:
      KMPilot's `feature/receive`, transplanted into the adopted repo under the adopt-mode
      build template (`kmpilotLibs` accessors + self-contained `compileSdk`/`minSdk`/
      `androidResources`/JVM target), compiles, wires into the host's own `initKoin` and
      `NavHost`, and passes all 19 checks.
- [x] `./gradlew archTest` green there — both with zero features and with one wired in.
- [x] Re-running `--adopt --force` is idempotent — no duplicated Gradle lines
      (3 includes / 1 catalog / 3 project deps unchanged; only `installedAt` moves).
- [x] Refuses to run on a dirty tree, and refuses a second run without `--force`.
- [x] `update.sh` runs cleanly in an adopted project.
- [x] *(added)* Template mode is unregressed — a full `install.sh` run still trims the
      `app` tiers, writes the neutral `App*` state screens, and omits `installMode`
      (so `update.sh` reads it as `"template"`).
- [x] *(added)* **17/17 compatibility variants pass** (`scripts/adopt-matrix.sh`), covering
      the shapes one fixture cannot: extra targets, convention plugins, no catalog, no Koin,
      Koin's Compose bootstrap, Arrow, a rival design system, library-only `core/*`, stale
      `build/` dirs, `core/*` name collisions, Groovy DSL, non-KMP, dirty tree, re-adoption.

### Why the matrix exists

The scratch fixture is one point in a large space, and a happy path hides the failures
that matter. Its first run found **three silent `set -e` aborts** — adopt mode exiting
with *no message at all* on a repo with no version catalog, no matching plugin version,
or no `commonMain` module — plus confirmed the two defects found by inspection
(hardcoded target sets, convention-plugin false-refusal).

The governing principle, unchanged from *Risks* below: **a clean refusal is a pass.**
Breaking someone's build silently is the failure being tested for; wrongly telling a real
KMP project it is not one is the second. Adaptation is not the goal — being honest and
predictable about what is supported is.

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
