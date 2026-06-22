# Changelog

All notable changes to KMPilot are documented here, following
[Keep a Changelog](https://keepachangelog.com/en/1.1.0/) and
[Semantic Versioning](https://semver.org/spec/v2.0.0.html).

To pull a release into a project created with `install.sh`, see
[Staying up to date](README.md#staying-up-to-date). Upgrade notes are tagged
**[Tooling]** (auto-applied by `update.sh`), **[Core]** (`update.sh --core`,
may conflict), or **[Breaking]** (manual steps required).

## [Unreleased]

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
