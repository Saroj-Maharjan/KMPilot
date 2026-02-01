# CLAUDE

## STOP - Mandatory Workflow Rules

**NEVER edit `feature/` files directly. ALWAYS invoke the required skill FIRST.**

| Action | Skill | Trigger Keywords |
|--------|-------|------------------|
| Create feature | `/creating-kmp-feature` | "new feature", "add feature", "create" |
| Modify feature | `/modifying-kmp-feature` | "add to", "change", "update", "fix", "modify" |
| Review feature | `/feature-review` | "review", "check", "audit" |
| Test feature | `/feature-test` | "test", "generate tests" |

**IMPORTANT:** Invoke the skill IMMEDIATELY upon recognizing feature work. Do NOT:
- Read files first to "understand the codebase"
- Explore the feature structure first
- Plan the implementation first

The skills contain workflows that handle exploration, planning, and implementation in the correct order.

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

**Critical Rules** (violations break the build or cause bugs):
1. `setState { copy() }` - NEVER `_state.value =`
2. `Either<T>` for errors - NEVER throw exceptions
3. 4-state UI: Uninitialized / Loading / Success / Failed
4. X-components from `:core:designsystem` - NO Material3
5. Interface + Impl pairs for DataSource/Repository

Full patterns: @.claude/skills/_shared/patterns.md

## Feature Structure
```
feature/{name}/src/commonMain/kotlin/{pkg}/{name}/
├── data/          # model/, datasource/, repository/, remote/
├── presentation/  # ViewModel, UiState, ui/, navigation/
└── di/            # {Feature}Modules.kt
```

## Gotchas
- Package names: lowercase only (`productdetail` not `product-detail`)
- Features NEVER depend on other features
- Specs live at `.claude/docs/{name}/spec.md`

## Reference
- Example: `feature/sample/`
- Versions: `gradle/libs.versions.toml`
- JVM: 21
