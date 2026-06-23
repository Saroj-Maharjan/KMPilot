<div align="center">

# KMPilot

**Spec-Driven Development for Kotlin Multiplatform.**

Design, build, test, and review features — with the architecture as the constraint, the LLM as the executor, and a living spec as the contract.

<br />

[![Kotlin](https://img.shields.io/badge/Kotlin-2.3-7F52FF?logo=kotlin&logoColor=white)](https://kotlinlang.org)
[![Compose Multiplatform](https://img.shields.io/badge/Compose%20Multiplatform-1.11-4285F4?logo=jetpackcompose&logoColor=white)](https://www.jetbrains.com/compose-multiplatform/)
[![AGP](https://img.shields.io/badge/AGP-9.2-3DDC84?logo=android&logoColor=white)](https://developer.android.com/build)
[![Android](https://img.shields.io/badge/Android-23+-34A853?logo=android&logoColor=white)](/)
[![iOS](https://img.shields.io/badge/iOS-15+-000000?logo=apple&logoColor=white)](/)
[![License](https://img.shields.io/badge/License-MIT-blue.svg)](LICENSE)

<br />

**Built for [Claude Code](https://claude.ai/code)**

<br />

https://github.com/user-attachments/assets/a1438483-68d3-4550-b876-9a62db0d1a21

<br />

[Documentation](https://github.com/ThisIsSadeghi/KMPilot/wiki) · [Report Bug](https://github.com/ThisIsSadeghi/KMPilot/issues) · [Request Feature](https://github.com/ThisIsSadeghi/KMPilot/issues)

</div>

<br />

## Quick Start

**Prerequisites:** JDK 21+ · Android Studio · Xcode 15+ (iOS) · [Claude Code](https://docs.anthropic.com/en/docs/claude-code)

**1. Install**

Replace `<MyApp>` and `<com.acme.myapp>` with your own values:

```bash
curl -fsSL https://raw.githubusercontent.com/ThisIsSadeghi/KMPilot/main/install.sh \
  | bash -s <MyApp> <com.acme.myapp>
```

- **`MyApp`** — project name (used for the folder, root project, and Android app label)
- **`com.acme.myapp`** — package prefix / application ID (used for `namespace`, `applicationId`, and source-set package roots)

Clones the latest release, renames packages, initializes fresh git — and leaves a `./update.sh` so you can pull future releases later (see [Staying up to date](#staying-up-to-date)).

> **Windows:** run the same command inside **Git Bash** (ships with [Git for Windows](https://git-scm.com/download/win)) or WSL — not PowerShell or `cmd`. The installer uses bash + GNU `sed`/`find`, both of which are present in Git Bash. iOS targets remain macOS-only.

**2. Open in Claude Code**

```bash
cd MyApp && claude
```

**3. Run skills along the lifecycle**

```
> /ui-designer checkout flow with delivery options          # design
> /creating-kmp-feature product detail screen with reviews  # scaffold
> /verify-ui productdetail                                  # verify UI vs design
> /feature-test productdetail                               # test all layers
> /feature-review productdetail                             # audit architecture
```

See [Skills](#skills) for the full catalog and [The Pattern](#the-pattern) for the flow.

<br />

## The Pattern

**Spec-Driven Development for KMP.** Every feature has a living spec at `.claude/docs/{name}/spec.md` that stays in sync with the code. Skills are phases of one lifecycle:

```
/ui-designer  →  /creating-kmp-feature  →  /verify-ui  →  /feature-test  →  /feature-review
   design              scaffold             verify UI       test              review
```

Each skill owns one phase and composes without coordinating. The blueprint from `/ui-designer` is picked up automatically by `/creating-kmp-feature`; the spec is regenerated as the code evolves; `/verify-ui`, `/feature-test`, and `/feature-review` audit against it. The architecture is the constraint; the LLM is the executor; the spec is the contract.

KMP is the proof point. The pattern generalizes to any opinionated stack.

<br />

## Skills

Slash-commands ordered by the lifecycle they cover. Run them inside Claude Code in your KMPilot project.

| Phase | Command | Does |
|---|---|---|
| Design | `/ui-designer {name}` | Design screens in Google Stitch, produce a Compose blueprint |
| Scaffold | `/creating-kmp-feature {prompt}` | Build a complete feature from a prompt (uses the blueprint if present) |
| Iterate | `/modifying-kmp-feature {prompt}` | Apply changes to an existing feature |
| Verify UI | `/verify-ui {name}` | Audit the implementation against the Stitch design |
| Test | `/feature-test {name}` | Generate fixtures, repository, ViewModel, and UI tests |
| Review | `/feature-review {name}` | Audit a feature against the 14 architecture rules |
| Spec | `/audit-spec {name}` | Regenerate or diff a feature's living spec |
| Coverage | `/coverage` | Test coverage report |
| Health | `/features-health` | Status report across all feature modules |

Plus two auto-activated skills: `using-design-system` (enforces X-components on UI work) and `bridging-swift-kotlin` (guides iOS SDK integration).

<br />

## What Gets Generated

Every feature module follows the same Clean Architecture shape:

```
feature/{name}/
├── data/
│   ├── model/                            # @Serializable DTOs
│   ├── remote/                           # Ktor Resources
│   ├── datasource/
│   │   ├── {Name}RemoteDataSource.kt     # interface
│   │   └── {Name}RemoteDataSourceImpl.kt
│   └── repository/
│       ├── {Name}Repository.kt           # interface
│       └── {Name}RepositoryImpl.kt
├── presentation/
│   ├── {Name}ViewModel.kt
│   ├── {Name}UiModel.kt                   # single state container (plain fields + UiState<DTO>)
│   ├── ui/
│   │   ├── {Name}Screen.kt               # Screen + testable ScreenRoot (3-name allowlist)
│   │   ├── {Name}Utils.kt                # optional: formatters, validators
│   │   └── components/                   # one file per @Composable (incl. {Name}Content.kt)
│   └── navigation/
│       └── {Name}Navigation.kt           # Route + NavGraphBuilder extension
└── di/
    └── {Name}Modules.kt                  # Koin bindings
```

Plus a living spec at `.claude/docs/{name}/spec.md` that updates when the code changes, and a full test suite (fixtures, repository, ViewModel, UI) when you run `/feature-test`.

<br />

## Project Structure

```
KMPilot/
├── composeApp/                 # Shared entry point
│   ├── BaseAppNavHost          # Feature routes registered here
│   └── initKoin                # Feature modules registered here
│
├── core/
│   ├── common/                 # Either, UiState, BaseViewModel
│   ├── data/                   # ApiClient, network config
│   └── designsystem/           # X-components (XButton, XTextField, XScaffold...)
│
├── feature/{name}/             # AI-generated feature modules
│   ├── data/                   # Models, DataSource, Repository
│   ├── presentation/           # ViewModel, Screens, Navigation
│   └── di/                     # Koin module
│
└── .claude/
    ├── agents/                 # Specialized AI agents
    ├── commands/               # Slash-command definitions
    ├── skills/                 # Skill workflows
    └── docs/{feature}/         # Living specifications (spec.md)
```

<br />

## Tech Stack

| Category | Technologies |
|:---------|:-------------|
| **Core** | Kotlin · Compose Multiplatform · Coroutines & Flow |
| **Network** | Ktor · Kotlinx Serialization |
| **Persistence** | Room · DataStore |
| **DI** | Koin |
| **Navigation** | Navigation Compose (type-safe) |
| **Testing** | Turbine · Mokkery · Kover |

<br />

## Staying up to date

The installer pins your project to a tagged release and leaves a `./update.sh` so you can pull later releases **without corrupting your code**:

```bash
./update.sh            # tooling only — .claude skills/agents/hooks, CLAUDE.md, gradle wrapper
./update.sh --core     # also merge core/ modules (rename-aware; conflicts are surfaced, never silent)
./update.sh --dry-run  # preview what would change; writes nothing
```

It re-applies your package rename to each upstream change and 3-way-merges it in. It **never touches** `feature/`, your app modules, or your per-feature specs, and it **never commits** — you review `git diff`, resolve any `<<<<<<<` markers, then commit. See [CHANGELOG.md](CHANGELOG.md) for each release's upgrade notes (tagged `[Tooling]` / `[Core]` / `[Breaking]`).

<br />

## Documentation

| Resource | Description |
|:---------|:------------|
| **[Wiki](https://github.com/ThisIsSadeghi/KMPilot/wiki)** | Complete reference for agents, skills, and architecture patterns |
| **[CHANGELOG.md](CHANGELOG.md)** | Release history + downstream upgrade notes |
| **[CLAUDE.md](CLAUDE.md)** | Rules and conventions that AI agents follow |

<br />

## Contributing

Contributions welcome. See [CONTRIBUTING.md](CONTRIBUTING.md) for guidelines.

<br />

---

<div align="center">

**MIT License**

</div>
