# Changelog

All notable changes to KMPilot are documented here. The format follows
[Keep a Changelog](https://keepachangelog.com/en/1.1.0/), and KMPilot adheres to
[Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## How downstream projects update

A project created with `install.sh` keeps a `./update.sh`. Run it to pull a newer
release **without corrupting your code**:

```bash
./update.sh            # tooling only (.claude, CLAUDE.md, gradle wrapper)
./update.sh --core     # also merge core/ modules (rename-aware, may conflict)
./update.sh --dry-run  # preview; writes nothing
```

`update.sh` never touches `feature/`, your app modules, or your per-feature specs,
and never commits. Conflicts are written as `<<<<<<<` markers for you to resolve.
Each release below carries **Upgrade notes** tagged by how the change reaches you:

- **[Tooling]** — applied automatically by `update.sh` (Tier 1, package-agnostic).
- **[Core]** — applied by `update.sh --core` (Tier 2, rename-aware; may conflict).
- **[Breaking]** — read before updating; may need manual steps.

### Versioning contract

| Bump | Meaning | Update impact |
|------|---------|---------------|
| **MAJOR** | Breaks the framework contract — skill API, `core` public API, the 14 architecture rules, or package layout. | Read **[Breaking]** notes; review carefully. |
| **MINOR** | Additive — new skills/agents, new optional `core` APIs. | `[Tooling]` safe; `[Core]` additions opt-in. |
| **PATCH** | Fixes — skill/agent fixes, doc fixes, `core` bug fixes. | `[Tooling]` always safe. |

---

## [Unreleased]

_Changes landing after 0.1.0 will be listed here._

## [0.1.0] — 2026-06-22

First public release.

### Added
- Spec-Driven Development pipeline for Kotlin Multiplatform: `/ui-designer` →
  `/creating-kmp-feature` → `/verify-ui` → `/feature-test` → `/feature-review`,
  coordinated through a per-feature living spec (`.claude/docs/{name}/spec.md`).
- `install.sh` one-command bootstrap: clone → trim to a clean shell → rename
  package/identifiers → fresh git. Installs from the latest release tag by
  default (override with `KMPILOT_TEMPLATE_BRANCH`).
- `update.sh` downstream updater (tiered, rename-aware, conflict-safe, never
  commits) plus the `.kmpilot.json` install manifest that makes updates possible.
- Clean Architecture core (`:core:common`, `:core:data`, `:core:designsystem`)
  with the X-component design system, light/dark `XTheme`, and a runtime locale.
- Reference features (`dashboard`, plus `assetdetail`, `swap`, `profile`,
  `send`, `receive`) demonstrating the generated feature shape. All are stripped
  from a fresh install — your project starts on a Welcome screen.

### Upgrade notes
- Initial release — nothing to upgrade from.

[Unreleased]: https://github.com/ThisIsSadeghi/KMPilot/compare/v0.1.0...HEAD
[0.1.0]: https://github.com/ThisIsSadeghi/KMPilot/releases/tag/v0.1.0
