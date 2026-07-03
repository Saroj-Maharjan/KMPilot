# CLAUDE

## STOP - Mandatory Workflow Rules

**NEVER edit `feature/` files directly. ALWAYS invoke the required skill FIRST.**

| Action | Skill | Trigger Keywords |
|--------|-------|------------------|
| Create feature | `/create-feature` | "new feature", "add feature", "create" |
| Modify feature | `/modify-feature` | "add to", "change", "update", "fix", "modify" |
| Review feature | `/review-feature` | "review", "check", "audit" |
| Test feature | `/test-feature` | "test", "generate tests" |
| Design UI | `/design-ui` | "design screen", "Stitch", "design UI", "mockup" |
| Verify UI | `/verify-ui` | "verify UI", "audit UI", "check implementation" |
| Bridge iOS↔Kotlin | `/bridge-swift` | "iOS SDK", "Swift", "call Swift", "native framework", "MapKit", "biometrics", "Apple Pay" |

**IMPORTANT:** Invoke the skill IMMEDIATELY upon recognizing feature work. Do NOT:
- Read files first to "understand the codebase"
- Explore the feature structure first
- Plan the implementation first

The skills contain workflows that handle exploration, planning, and implementation in the correct order.

`/using-design-system` auto-activates for UI work and does not need explicit invocation.

### Design Pipeline

The design pipeline uses a **blueprint artifact contract** for decoupled skill coordination:

1. `/design-ui` designs screens in Stitch and produces a self-contained blueprint (sets `blueprintConsumed: false` in `stitch-project.json`)
2. `/create-feature` or `/modify-feature` auto-detect the blueprint and enter **design-aware mode** (sets `blueprintConsumed: true` after implementation)
3. `/verify-ui` audits the implementation against the Stitch design (three-way token audit)

Each skill is independently invocable — no skill calls another skill. The user controls the pipeline.

### Platform Capabilities & Native Views (Rule 14)

Features needing a device capability (GPS, camera, BLE, biometrics) or a native view (map, camera preview, WebView) are handled **inside** `/create-feature` / `/modify-feature` — no separate command. Those skills classify the feature's **Platform Profile** (`network` / `platform-capability` / `native-view` / `mixed`) in Phase 2, then route work to `platform` (capability behind a `commonMain` DataSource → `Either<T>`, per-platform actuals incl. desktop) and `ui-layer` (the `expect @Composable` native-view interop). When an iOS `actual` needs Swift, they finish the Kotlin side and route you to `/bridge-swift` (the iOS-Swift leg). Full patterns: `.claude/skills/create-feature/architecture/platform.md`.

---

## Build Commands
```bash
./gradlew :feature:{name}:assembleAndroidMain  # Incremental build
./gradlew assembleDebug                         # Full build
./gradlew :feature:{name}:ktlintFormat          # Format code
./gradlew :feature:{name}:desktopTest           # Run tests
```

## Architecture
KMP + Compose Multiplatform + Clean Architecture

All rules, patterns, naming conventions, and feature structure: @.claude/skills/_shared/patterns.md

## Gotchas
- Package names: lowercase only (`productdetail` not `product-detail`)
- Features NEVER depend on other features
- Specs live at `.claude/docs/{name}/spec.md`
- **Core module tiers (generic vs `app/`)**: the two-tier split applies to every core library module that holds project-specific content — `:core:designsystem` (`designsystem.app`: `App*` state screens + project composites/brand drawables), `:core:data` (`data.app`: project/domain persisted state **and** shared cross-feature remote — endpoints/wire models/datasource), and `:core:common` (`common.app`: project/example value types). Generic code must NEVER import its module's `.app` tier (stripped/neutralized by `install.sh`) — it's a package convention; the lone sanctioned exception is `DataModules.kt` referencing `appDataModule`. Full map: `_shared/patterns.md` → "Core Module Tiers"
- Loading/Failed UI is **shared**: features call `AppLoadingState`/`AppErrorState` from `designsystem.app` (copy + nav passed as params), never a private `LoadingContent`/`FailedContent`. Empty state stays per-feature

## Reference
- Example: `feature/dashboard/`
- Versions: `gradle/libs.versions.toml`
- JVM: 21
