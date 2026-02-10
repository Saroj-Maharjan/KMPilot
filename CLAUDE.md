# CLAUDE

## STOP - Mandatory Workflow Rules

**NEVER edit `feature/` files directly. ALWAYS invoke the required skill FIRST.**

| Action | Skill | Trigger Keywords |
|--------|-------|------------------|
| Create feature | `/creating-kmp-feature` | "new feature", "add feature", "create" |
| Modify feature | `/modifying-kmp-feature` | "add to", "change", "update", "fix", "modify" |
| Review feature | `/feature-review` | "review", "check", "audit" |
| Test feature | `/feature-test` | "test", "generate tests" |
| UI work | `/frontend-design` (see UI rule below) | "screen", "UI", "composable", "component", "layout", "design" |

**IMPORTANT:** Invoke the skill IMMEDIATELY upon recognizing feature work. Do NOT:
- Read files first to "understand the codebase"
- Explore the feature structure first
- Plan the implementation first

The skills contain workflows that handle exploration, planning, and implementation in the correct order.

**UI WORK RULE:** When ANY task involves creating or modifying Screens, Composables, or UI components:
1. **Check** the available skills list (in the system-reminder) for `frontend-design`
2. **If `frontend-design` IS listed** → invoke `/frontend-design` FIRST — MANDATORY, do NOT skip it

`/using-design-system` auto-activates for UI work and does not need explicit invocation. `/frontend-design` is required whenever it appears in the available skills list — when present, NEVER skip it.

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

## Reference
- Example: `feature/sample/`
- Versions: `gradle/libs.versions.toml`
- JVM: 21
