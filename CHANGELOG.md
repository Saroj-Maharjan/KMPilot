# Changelog

All notable changes to KMPilot are documented here, following
[Keep a Changelog](https://keepachangelog.com/en/1.1.0/) and
[Semantic Versioning](https://semver.org/spec/v2.0.0.html).

To pull a release into a project created with `install.sh`, see
[Staying up to date](README.md#staying-up-to-date). Upgrade notes are tagged
**[Tooling]** (auto-applied by `update.sh`), **[Core]** (`update.sh --core`,
may conflict), or **[Breaking]** (manual steps required).

## [Unreleased]

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

[Unreleased]: https://github.com/ThisIsSadeghi/KMPilot/compare/v0.1.0...HEAD
[0.1.0]: https://github.com/ThisIsSadeghi/KMPilot/releases/tag/v0.1.0
