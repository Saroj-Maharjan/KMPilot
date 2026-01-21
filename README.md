<div align="center">

# 🚀 KMPilot

**Kotlin Multiplatform Template with AI-Powered Development**

[![Claude Code](https://img.shields.io/badge/Built%20for-Claude%20Code-FF6B35?logo=anthropic&logoColor=white)](https://claude.ai/code)
[![Kotlin](https://img.shields.io/badge/Kotlin-2.2-7F52FF?logo=kotlin&logoColor=white)](https://kotlinlang.org)
[![Compose](https://img.shields.io/badge/Compose-Multiplatform-4285F4?logo=jetpackcompose&logoColor=white)](https://www.jetbrains.com/compose-multiplatform/)
[![Platform](https://img.shields.io/badge/Platform-Android%20%7C%20iOS-green)](/)
[![Architecture](https://img.shields.io/badge/Architecture-Clean-orange)](/)

**Requires [Claude Code](https://claude.ai/code)** — All agents and skills require Claude Code to function.

<!-- TODO: Add demo GIF here -->

</div>

---

## What is KMPilot?

A Kotlin Multiplatform template that follows Clean Architecture and Specification-Driven Development (SDD).

**The flow:** You describe a feature in plain English → AI generates a spec (PRD) → Breaks down tasks → Specialized agents build data/UI/integration layers → Living documentation auto-generated and stays synced with your code.

---

## 🧠 Specification-Driven Development

> **Specifications come first, guide AI implementation, then become living documentation.**

```
User Request: "Create profile feature with settings and edit screens"
    │
    ▼
┌─────────────────────────────────────────────────────────────────┐
│ Phase 0: Context Discovery (AUTO)                               │
│ Detect: PKG_PREFIX, INIT_KOIN_PATH, NAV_HOST_PATH, CORE_MODULES │
└─────────────────────────────────────────────────────────────────┘
    │
    ▼
┌─────────────────────────────────────────────────────────────────┐
│ Phase 1: PRD Generation                                         │
│ Analyze prompt → Generate PRD → Save to .claude/docs/profile/   │
└─────────────────────────────────────────────────────────────────┘
    │
    ▼  [USER CONFIRMS PRD]
    │
┌─────────────────────────────────────────────────────────────────┐
│ Phase 2: Task Generation                                        │
│ Break PRD into tasks → Assign to agents → Save task files       │
└─────────────────────────────────────────────────────────────────┘
    │
    ▼  [USER CONFIRMS TASKS]
    │
┌─────────────────────────────────────────────────────────────────┐
│ Phase 3: Implementation (Sequential or Parallel)                │
│  🔧 Data Agent    → Repository, DataSource                      │
│  🎨 UI Agent      → ViewModel, Screens                          │
│  🔗 Integration   → DI, Navigation, Living Spec                 │
└─────────────────────────────────────────────────────────────────┘
    │
    ▼
┌─────────────────────────────────────────────────────────────────┐
│ Phase 4: Cleanup                                                │
│ Verify spec.md → Remove prd.txt + tasks.md + task-*.md          │
└─────────────────────────────────────────────────────────────────┘
    │
    ▼
✅ Feature Complete (spec.md is source of truth)
```

---

## 🔄 Workflows & Use Cases

**Skills (Auto-Activate)**

### 1- Creating New Features
Follows the SDD workflow above (5 phases). AI generates everything from scratch: PRD → Tasks → Implementation → Living Spec.
```bash
> Create profile feature with settings and edit screens
```

### 2- Modifying Existing Features
Spec-first approach using `modifying-kmp-feature` skill:
1. Load existing spec from `.claude/docs/{feature}/spec.md`
2. Draft spec changes and get user approval (review gate)
3. Apply changes following established patterns
4. Validate build and update spec with changelog entry
```bash
> Add sorting and filtering to the product list feature
```

### 3- Design System Enforcement
`using-design-system` skill auto-activates when working on UI code, ensuring X-components are used instead of Material3. Prevents design system drift and maintains visual consistency across features.
```bash
> Create a product card component with image and price
```

### 4- Swift-Kotlin Bridging
`bridging-swift-kotlin` guides interface injection patterns for integrating iOS SDKs (biometrics, payments, camera, etc.) into your KMP codebase while maintaining Clean Architecture.
```bash
> Integrate iOS Face ID authentication into the login feature
```

---

**Agents (Manual Invocation)**

### 1- Testing Features
`test-orchestrator` coordinates 6 specialized test agents to generate complete test suites: fixtures, DataSource tests (MockEngine), Repository tests (Mokkery), ViewModel tests (Turbine), UI tests, and E2E integration tests.
```bash
> Use test-orchestrator agent to generate complete test suite for the login feature
```

### 2- Code Quality Review
`code-reviewer` validates features against Clean Architecture rules, checks 4 integration points, naming conventions, and design system compliance. Use after implementation to ensure quality standards.
```bash
> Use code-reviewer agent to review the profile feature
```

---

**Slash Commands**

### 1- Audit Specification
Audit, generate, or compare specifications for existing KMP features.
```bash
/audit-spec login
```

### 2- Coverage Report
Generate and open test coverage reports for all feature modules.
```bash
/coverage
```

---

## 📁 Project Structure

```
├── 📱 composeApp/          App entry (BaseAppNavHost, initKoin)
├── 🧱 core/
│   ├── common/             Either, UiState, BaseFeature
│   ├── data/               ApiClient, network layer
│   └── designsystem/       X-components (XButton, XText...)
├── ✨ feature/             AI-generated feature modules
│   └── {name}/
│       ├── data/           Models, DataSource, Repository
│       ├── presentation/   ViewModel, Screens, Navigation
│       └── di/             Koin module
└── 🤖 .claude/             AI tooling (agents, skills, docs)
```

---

## 🛠️ Technologies

**Core:** Kotlin 2.2 • Compose Multiplatform 1.9 • Coroutines & Flow

**Networking:** Ktor Client 3.3 • Kotlinx Serialization 1.9

**Data:** Room 2.8 • DataStore 1.1 • Kotlinx DateTime 0.7

**DI:** Koin 4.1 with auto-registration

**Navigation:** Navigation Compose 2.9 (type-safe)

**Testing:** Turbine 1.2 • Mokkery 3.0 • Kover 0.9

**Code Quality:** Ktlint 14.0 (auto-formatting)

---

## 📚 Documentation

**📖 [Wiki](https://github.com/ThisIsSadeghi/KMPilot/wiki)** — Complete reference for all AI tools, including skills (auto-activating workflows), agents (manual invocation), testing strategies, and detailed examples of each capability.

**📐 [CLAUDE.md](CLAUDE.md)** — Quick reference for architecture patterns and mandatory conventions. Includes critical rules (setState, Either, 4-state UI), project structure, and common commands. Used by AI agents to maintain consistency.

---

<div align="center">

**Built with** Kotlin • Compose Multiplatform • Ktor • Koin • Room

*AI-powered KMP development with Clean Architecture*

</div>
