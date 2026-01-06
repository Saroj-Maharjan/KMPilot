# CLAUDE

KMP (Android + iOS) • Compose Multiplatform • Clean Architecture

## Workflow (Mandatory)

| Request | Skill |
|---------|-------|
| Create new feature/module | `creating-kmp-feature` |
| Modify existing feature | `modifying-kmp-feature` |
| UI/design/screen/component work | `frontend-design` |

## Critical Patterns
1. `setState { }` for state updates (never direct assignment)
2. `Either<T>` for all error handling
3. 4-state UI: Uninitialized/Loading/Success/Failed
4. X-components from `:core:designsystem` (NOT Material3)
5. Interface + Impl pairs for DataSource/Repository

Reference: `feature/sample/`

## Structure
```
{PROJECT}/
├── composeApp/      # App entry, initKoin, BaseAppNavHost
├── core/            # common, data, designsystem
└── feature/         # Isolated modules
```

## Commands
```bash
./gradlew build                                # Full build
./gradlew :feature:{name}:assembleAndroidMain  # Build feature
./gradlew :feature:{name}:ktlintFormat         # Format feature
```

## Dependencies
- Features → `:core:common`, `:core:designsystem`, `:core:data` (if API)
- Features MUST NOT depend on other features

## Documentation
- Specs: `.claude/docs/{name}/spec.md` (source of truth)
- PRD/tasks: Ephemeral (auto-deleted)

## Config
- Versions: `gradle/libs.versions.toml`
- JVM: 21

Context7 MCP for library docs.
