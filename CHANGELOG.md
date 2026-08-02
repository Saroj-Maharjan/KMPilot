# Changelog

All notable changes to KMPilot are documented here, following
[Keep a Changelog](https://keepachangelog.com/en/1.1.0/) and
[Semantic Versioning](https://semver.org/spec/v2.0.0.html).

To pull a release into a project created with `install.sh`, see
[Staying up to date](README.md#staying-up-to-date). Upgrade notes are tagged
**[Tooling]** (auto-applied by `update.sh`), **[Core]** (`update.sh --core`,
may conflict), or **[Breaking]** (manual steps required).

## [Unreleased]

### Added

- **[Tooling]** **Adopt mode — `install.sh --adopt`.** Installs the pipeline **into a KMP
  repo you already have** instead of generating a new project. Run it from the target
  repo's root; `--dry-run` prints the compatibility report and the exact file plan and
  writes nothing, `--force` re-applies over an existing adoption.

  ```bash
  bash install.sh --adopt --dry-run   # report + file plan, writes nothing
  bash install.sh --adopt             # vendor core/, wire it up, install .claude/
  ```

  It detects the target's `rootProject.name`, app module, package prefix (longest common
  package in the app module — never assumes `composeApp` or a single-segment prefix), and
  its Koin / Ktor / Compose Multiplatform / DataStore inventory, then vendors
  `core/common`, `core/data` and `core/designsystem` renamed to the target's own package.

  Deliberate boundaries, because this writes into a repo somebody already cares about:
  - **A separate version catalog.** KMPilot's dependencies land in
    `gradle/kmpilot.versions.toml`, registered as `kmpilotLibs`; the target's own `libs`
    catalog is never edited, so no alias can collide or silently bind a different artifact.
    The target's Kotlin / AGP / Compose / SDK versions are read and reused — KMPilot never
    overrides them, so there is never a second Kotlin plugin on the build classpath.
  - **Self-contained vendored modules.** The three `core/*` build files are rewritten to
    declare their own `compileSdk`/`minSdk`/`androidResources`/JVM target instead of
    inheriting KMPilot's root `allprojects {}` conventions — the target's root build stays
    the target's.
  - **Never clobbers.** Existing paths are reported and skipped; an existing `CLAUDE.md` or
    `.claude/settings.json` is kept and KMPilot's is written beside it as `*.kmpilot.*`.
    The target's Kotlin is never edited — the Koin wiring is printed as a snippet.
  - **Refuses** on a dirty working tree, outside a git repo, on a non-KMP project, and on a
    second run without `--force`. Re-running is idempotent: no duplicated Gradle lines.
- **[Tooling]** **`{APP_MODULE}` / `{CATALOG}` awareness across the pipeline.** The checker
  and the create-feature skill no longer assume KMPilot's own layout: the app module
  (`composeApp` in a generated project, anything at all in an adopted one) and the catalog
  accessor (`libs` vs `kmpilotLibs`) are read from `.kmpilot.json`, with detection as a
  fallback. Integration points I2/I3/I4 resolve against the real app module, and the
  NavHost is found by content when it is not named `*NavHost*.kt`.
- **[Tooling]** **Adopt compatibility matrix** — `scripts/adopt-matrix.sh` generates the
  scratch KMP fixture once per project shape, mutates it, and asserts that `--adopt`
  adopts, warns, or **refuses cleanly** as documented. Detection-only (pure bash, no
  Gradle for all but four), so seventeen variants run in seconds and gate every PR.

  It paid for itself immediately: the first run found **three silent `set -e` aborts** —
  a `pipefail`'d `grep` that legitimately matches nothing, and two functions whose
  trailing conditional became their exit status. Each killed adopt mode with no message
  at all on repos with no version catalog, no matching plugin version, or no
  `commonMain` module. A single happy-path fixture hid all three.

  Variants covered: baseline · `iosX64` · unsupported targets · convention plugins ·
  no catalog · no Koin · Koin's Compose bootstrap · Arrow · own design system ·
  library-only `core/*` modules · no trailing newline · stale `build/` dirs ·
  `core/*` name collision · Groovy DSL · non-KMP · dirty tree · already adopted.
  [`ADOPTING.md`](ADOPTING.md) is the published form of this matrix.
- **[Tooling]** **Target-set derivation.** Vendored `core/*` hardcoded four Kotlin targets.
  `iosX64` is now added automatically when the host declares it (it folds into the same
  `iosMain` source set, so no new actual is needed), and a host declaring a target KMPilot
  has no actuals for (`wasmJs`, `js`, `macos*`, `watchos*`, `linux*`) is **refused with the
  reason** rather than left with a module that has no matching variant.
- **[Tooling]** **Convention-plugin projects no longer false-refuse.** Detection scanned
  only `settings.gradle.kts`, the root build, the catalog and `include()`d modules — so a
  project applying KMP through a `build-logic` / `buildSrc` convention plugin was told it
  was "not a Kotlin Multiplatform project". Detection now also scans included builds and
  convention-plugin sources, and treats a module with `src/commonMain` as proof on its own.
- **[Tooling]** **App-module detection rewritten** after a real adoption picked a *library*
  module. The old fallback took the first module with a `commonMain` in alphabetical order;
  in a project with `core/model`, `core/network`, `search`, `favorites` and `shared` — and
  no `startKoin` or `NavHost` to go on — that was `core/model`. Everything downstream
  inherited it: the package prefix became `com.example.bookshelf.model` and the whole
  vendored core was renamed into a data module's package.

  Now: anything under `core/` is excluded as a library by convention; the signals are
  `startKoin`, then a `NavHost` / `setContent` / `@Composable fun App`, then **the module
  the Android application module depends on**; and when none of those fire, the guess is
  *confirmed with the user* instead of silently used. `--app-module=<module>` overrides it
  outright for non-interactive runs.
- **[Tooling]** **Appends no longer splice.** A Gradle file whose last line lacked a
  trailing newline turned every append into
  `include(":core:network")include(":core:common")` — not valid Kotlin. Every append site
  now ensures the newline first.
- **[Tooling]** **A project with no Koin at all gets a working `initKmpilotKoin()`.**
  Previously adopt printed "add `modules(kmpilotModules)` to your `startKoin`" to projects
  that had no `startKoin` to add it to. Still new-files-only — it is written beside the
  glue, never spliced into the project's own entry points.
- **[Tooling]** **The Koin wiring is automated — by the integrator, not the installer.**
  `install.sh --adopt` still never edits the host project's Kotlin: a regex cannot safely
  parse the shapes a real `startKoin` takes (`modules(a, b)`, `modules(listOf(...))`,
  multi-line, split across helpers, several call sites), and a wrong guess breaks a build in
  a repo the installer was trusted with. The `integrator` agent is already editing that exact
  block for integration point 3, reads the file as a model rather than a pattern, and now
  owns the bootstrap too — so the wiring happens automatically the first time a feature is
  scaffolded, which is the first moment it is actually needed.

  `.kmpilot.json` gained **`koinBootstrap`**: `"host"` (the project starts Koin — add
  `kmpilotModules` to that call) or `"supplied"` (adopt wrote an `InitKmpilotKoin.kt` that
  nothing calls yet — wire the call at the platform entry points, then flip the field).
- **[Tooling]** **`KMPILOT-NEXT-STEPS.md`.** Everything adopt could not do for itself is
  written to a file instead of only printed — terminal output scrolls away, and the file
  shows up in the diff like everything else. Delete it when done.
- **[Tooling]** **Koin's Compose bootstrap is recognised.** Detection only grepped
  `startKoin`, but a Compose Multiplatform app commonly bootstraps with `KoinApplication` /
  `koinConfiguration` / `KoinMultiplatformApplication` and never calls it. Adopt therefore
  concluded "no DI", wrote a dead `InitKmpilotKoin.kt` into a project that already had Koin,
  and recorded the wrong `koinBootstrap`. Both `install.sh` and the checker now match every
  entry point.
- **[Tooling]** **A `core/*` name collision is refused, not silently half-applied.** A host
  that already owns a module at `core/common` had it correctly left alone — but the app
  module still got `implementation(project(":core:common"))`, which then resolved to *their*
  module and broke every `Either` / `UiState` import with nothing explaining why. Adopt now
  stops before writing anything. (A `--force` re-run over KMPilot's own vendored modules is
  unaffected.)
- **[Tooling]** **Stale `build/` directories no longer skip vendoring.** `core/common/`
  surviving as a build-output-only directory — git-ignored, so `git clean` leaves it — read
  as "already exists", skipped the copy, and still wrote the `include`: a project Gradle
  cannot configure. The existence test now requires a real module.
- **[Tooling]** **The checker no longer matches prose.** File roles were assigned by content
  grep without blanking comments, so an integrator comment reading *"this app has no global
  startKoin{}"* made that file register as the Koin entry point, and a comment about a
  `NavHostController` stand-in nearly did the same for the nav host. Comments and string
  literals are blanked first — matching prose is worse than matching nothing, because it
  passes checks that should fail.
- **[Tooling]** **`I4` no longer fails an adopted project that has no NavHost.** Voyager,
  Decompose and plain hoisted state are valid navigation choices; in adopt mode a missing
  NavHost is a warning naming what to check, not a build failure. Template mode keeps it an
  error, since KMPilot ships one.
- **[Tooling]** **Generated report kept out of your diffs.** `archTest` rewrites
  `check-report.json` on every run; an adopted repo has no ignore rule for it, so it would
  churn forever. Adopt writes `.claude/docs/_project/.gitignore` — inside the directory
  KMPilot owns, so the project's own ignore file is still never touched.
- **[Tooling]** **Arrow warning.** A project using `arrow.core.Either` is told, before
  anything is written, that vendoring adds a second distinct type named `Either`.
- **[Tooling]** **Fewer refusals, more questions.** An unusual `rootProject.name` is
  sanitized the way Compose sanitizes it rather than rejected; an undetectable package
  prefix now prompts on a TTY (and honours the positional `<Name> <package.prefix>`
  arguments) instead of exiting.
- **[Tooling]** `scripts/rename.sh` gained `--paths=<dir,dir>` and `--no-readme`, so a
  rename can be scoped to `core/` without rewriting a whole tree or overwriting a README.
  (Upstream-only; `scripts/` is stripped on install.)

- **[Tooling]** **Deterministic architecture checker.** `.claude/skills/_shared/kmpilot_check.py`
  mechanizes the greppable half of the 14 rules — 19 checks (R3 setState, R5 Material3
  components, R7 packages, R8 DI, R9 UseCases, R11a/b/c UiModel/UiState, R12 hardcoded
  strings + `strings.xml`, R13 Scaffold/insets, the `Screen.kt` `@Composable` allowlist,
  `components/` placement, the `.app`-tier boundary, the preview import, and the 4
  integration points). It writes `.claude/docs/_project/check-report.json` and exits
  non-zero on any `error`-severity violation, so a rule violation now fails a build
  instead of depending on a model noticing it.

  ```bash
  ./gradlew archTest                                                   # every feature
  python3 .claude/skills/_shared/kmpilot_check.py {featurename}         # one feature
  python3 .claude/skills/_shared/kmpilot_check.py --all --baseline      # errors → warnings, exit 0
  ```

  `--baseline` is the pre-adoption tier: identical checks, every error reported as a
  warning, exit code always 0 — for measuring how far a codebase that has **not**
  adopted KMPilot yet sits from the rules. A KMPilot project itself, this template
  included, is held to every rule as an error. `--root <path>` points the checker at
  another repo without installing anything into it.

  On a terminal the findings print grouped by feature with shortened paths and wrapped
  messages; piped or in CI they collapse to one greppable `file:line severity RULE message`
  line each (`--compact` forces that form, `NO_COLOR` disables color). The verdict line
  states the exit code outright.

- **[Tooling]** `/review-feature` and the `code-reviewer` agent now **consume**
  `check-report.json` instead of re-deriving the mechanical checks by grep, and spend
  their effort on the five judgment rules (1, 2, 4, 10, 14). A review and a CI run can
  no longer disagree.

### Changed

- **[Tooling]** `kmpilot_check.py --all` on a repo with **no** feature modules is now a
  legitimate empty result (empty report, exit 0) instead of a usage error — a freshly
  installed or freshly adopted project runs `./gradlew archTest` before its first feature
  exists. Naming no features *and* omitting `--all` is still an error.
- **[Tooling]** `update.sh` understands `installMode: "adopt"` (absent ⇒ `"template"`, so
  every existing manifest stays valid). In an adopted project it drops KMPilot's app-shell
  modules from the manual-review list, reports catalog changes against
  `gradle/kmpilot.versions.toml` rather than the project's own, and routes updates for
  `CLAUDE.md` / `.claude/settings.json` to the `*.kmpilot.*` sidecar when the project kept
  its own.

- **[Breaking]** *(manual, one block)* **Register the `archTest` Gradle task.** `build.gradle.kts`
  is a `MANUAL` path — `./update.sh` surfaces it but never merges it — so existing installs
  must paste this into the **root** `build.gradle.kts` (anywhere at top level; the script it
  calls arrives automatically with `./update.sh`):

  ```kotlin
  // Deterministic architecture checker (see .claude/skills/_shared/kmpilot_check.py).
  // Thin wrapper on purpose — all logic lives in the script so it stays runnable
  // standalone from CI, a pre-commit hook, or a project without this task.
  tasks.register<Exec>("archTest") {
      group = "verification"
      description = "Checks feature modules against the KMPilot architecture rules."
      workingDir = rootDir
      commandLine("python3", ".claude/skills/_shared/kmpilot_check.py", "--all")
      doFirst {
          val onPath =
              System.getenv("PATH")
                  ?.split(File.pathSeparator)
                  ?.any { dir -> File(dir, "python3").canExecute() || File(dir, "python3.exe").canExecute() }
                  ?: false
          if (!onPath) {
              throw GradleException(
                  "archTest needs python3 on PATH (macOS: `brew install python`, " +
                      "Windows: python.org installer + Git Bash). Or run the checker directly: " +
                      "python3 .claude/skills/_shared/kmpilot_check.py --all",
              )
          }
      }
  }
  ```

  Skipping this is safe — the checker still runs standalone via `python3`, and
  `/review-feature` invokes it that way. You only lose `./gradlew archTest`.
  `python3` is already a hard requirement of `/design-ui`, so this adds no new dependency.

  Expect the first run to report real violations. Add
  `.claude/docs/_project/check-report.json` to `.gitignore` — it is regenerated every run.

## [0.1.3] — 2026-07-12

### Changed
- **[Tooling]** `install.sh` is now an **interactive, colorized installer**. Run it
  with no arguments (`curl … | bash`) and it walks a guided flow — banner, numbered
  step log, clone spinner, and prompts for the project name / package with inline
  validation. Passing both as arguments stays fully non-interactive (unchanged for
  scripting). New env vars: `NO_COLOR` (disable color) and `KMPILOT_ASSUME_YES=1`
  (skip the confirmation prompt). Template surgery, rename, and manifest logic are
  unchanged.

## [0.1.2] — 2026-07-04

### Added
- **[Tooling]** **Upstream-owned rule layers**: `.claude/skills`, `.claude/agents`,
  `.claude/commands` and `.claude/hooks` are now applied **as-is from the release** —
  no 3-way merge, no `<<<<<<<` conflicts. Downstream projects follow upstream rules:
  local edits to shipped rule files are overridden (`force:` in the log, recoverable
  via git), and upstream deletions/renames apply even over local edits. Files you
  created yourself (paths KMPilot never shipped, e.g. your own skills) are never
  touched. `settings.json`, `CLAUDE.md`, the gradle wrapper and `update.sh` keep the
  conflict-surfacing 3-way merge so your own additions survive.
- **[Tooling]** `update.sh` **re-execs under the target release's updater**: when
  `update.sh` itself changed in the release being pulled, the run transparently
  restarts under the new updater so its merge logic drives *this* update, not just
  the next one. Loop-guarded via `KMPILOT_REEXEC`; the stash/preflight moved after
  the re-exec point so a stashed working tree is always restored. A re-exec'd run
  writes `update.sh` directly (no `update.sh.new` staging — that now only happens
  when the running process is the project's own `update.sh`).
- **[Tooling]** `update.sh` ends every run — including "already on the latest
  release" — with a **stale sweep**: template files the target release no longer
  ships are deleted. In the upstream-owned rule layers any file whose path ever
  shipped in a release is swept (local edits included); on merged paths only
  byte-identical copies are swept. Files at paths KMPilot never shipped are never
  touched. Re-running `./update.sh` after updating with a pre-0.1.2 updater heals
  the stale skill/agent/command copies it left behind — no flags needed.

### Migrating from 0.1.0 / 0.1.1
Old updaters can't benefit from fixes they predate, so update the updater **before**
updating the project:

```bash
curl -fsSL https://github.com/ThisIsSadeghi/KMPilot/releases/latest/download/update.sh -o update.sh
./update.sh
```

(A project installed from 0.1.0 has an updater that never updates itself; 0.1.1's
updates itself only after running its old merge logic once. From 0.1.2 onward this
is automatic — the updater always re-execs under the target release's updater.)

### Fixed
- **[Tooling]** `/design-ui`'s `edit_screens` MCP tool silently no-ops (upstream Stitch
  bug — success reported, edit never applied). Banned; all edit flows now route through
  `generate_variants` (variantCount 1 + REFINE) instead.
- **[Tooling]** Compaction hook re-injected only 11 of the 14 architecture rules,
  dropping Rules 12–14 (string resources, single app-shell Scaffold, platform
  capability) after a `/compact`.
- **[Tooling]** `update.sh` now applies upstream **renames and deletions**. On merged
  paths, renamed files (detected via git rename tracking) carry your local edits to
  the new path and the old copy is removed; files deleted upstream are removed when
  your copy is unmodified, and a locally edited copy is only flagged. (In the
  upstream-owned rule layers, deletions/renames apply unconditionally — see Added.)
  Previously a release that renamed skills left both the old and new skill
  directories on disk, keeping stale skills discoverable.
- **[Tooling]** `update.sh` preserves the executable bit on newly added files (new
  hooks would otherwise land non-executable and silently never fire) and never
  text-merges binary files (e.g. `gradle-wrapper.jar`) — it takes upstream's copy when
  yours is unmodified and flags it for manual reconciliation otherwise.
- **[Tooling]** `update.sh` no longer misleads after a conflicted run: the exit message
  explains that `.kmpilot.json` is already bumped and how to abandon the update
  wholesale, so a partial revert can't silently shift the next update's base.

## [0.1.1] — 2026-07-01

### Changed
- **[Tooling]** `install.sh` is now **release-pinned**. The release workflow stamps
  the published tag into the installer, so a released `install.sh` clones the exact
  tag it shipped with — the installer and the cloned template are always the same
  release. Install from `releases/latest/download/install.sh`; set
  `KMPILOT_TEMPLATE_BRANCH=main` for the bleeding edge.
- **[Tooling]** `install.sh` stamps `.kmpilot.json` `kmpilotVersion` from the resolved
  release **tag** (not the `VERSION` file), so `update.sh`'s baseline can never drift
  from the tag actually installed.
- **[Tooling]** `update.sh` now updates **itself** — when the updater changes upstream it
  swaps the new version in automatically (atomic rename, safe mid-run; tracked in git, so it
  shows in `git diff`). It also warns that your app version (android/iOS) is yours and never
  touched by updates, and points at the upstream changelog for release notes.
- Fresh installs get a **minimal project `README.md` + empty `CHANGELOG.md`** instead of
  inheriting KMPilot's own.

### Added
- **Release automation** — `.github/workflows/release.yml` (publishes assets on a `v*`
  tag, guards that tag == `VERSION` == `libs.versions.toml`) and `scripts/release.sh`
  (bumps the version, rolls the changelog, commits + tags; never pushes).

### Migrating from 0.1.0
Existing projects are **unaffected in place** — nothing here rewrites an already-installed
tree. To move onto the self-updating `update.sh`, re-pull it once:

```bash
curl -fsSL https://raw.githubusercontent.com/ThisIsSadeghi/KMPilot/main/update.sh -o update.sh
```

Then run `./update.sh` as usual. If your `update.sh` reports `base tag not found`, pass an
explicit baseline: `./update.sh --from v0.1.0`.

## [0.1.0] — 2026-06-22

First public release — an AI-driven Spec-Driven Development template for
Kotlin Multiplatform + Compose Multiplatform.

### Added
- **Spec-Driven pipeline** — `/ui-designer` → `/creating-kmp-feature` →
  `/verify-ui` → `/feature-test` → `/feature-review`, coordinated through a
  per-feature living spec at `.claude/docs/{name}/spec.md`.
- **One-command install** — `install.sh` clones the latest release, trims to a
  clean shell, renames packages and identifiers, and re-initializes git.
- **Downstream updater** — `update.sh` (tiered, rename-aware, conflict-safe,
  never commits) with the `.kmpilot.json` manifest that drives updates.
- **Clean Architecture core** — `:core:common`, `:core:data`, and
  `:core:designsystem`, with the X-component design system, light/dark `XTheme`,
  and a runtime locale.
- **Reference feature** — a `dashboard` showcase demonstrating the generated
  feature shape; reference features are stripped on install, so a fresh project
  starts on a Welcome screen.

[Unreleased]: https://github.com/ThisIsSadeghi/KMPilot/compare/v0.1.3...HEAD
[0.1.0]: https://github.com/ThisIsSadeghi/KMPilot/releases/tag/v0.1.0
[0.1.1]: https://github.com/ThisIsSadeghi/KMPilot/releases/tag/v0.1.1
[0.1.2]: https://github.com/ThisIsSadeghi/KMPilot/releases/tag/v0.1.2
[0.1.3]: https://github.com/ThisIsSadeghi/KMPilot/releases/tag/v0.1.3
