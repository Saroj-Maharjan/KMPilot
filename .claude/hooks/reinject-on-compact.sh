#!/bin/bash
# reinject-on-compact.sh
# Re-injects critical architecture rules after context compaction.
# Used as a SessionStart hook with "compact" matcher.

# Clean up stale skill marker on compaction
rm -f /tmp/.claude-kmpilot-skill-active

cat <<'RULES'
## Critical Architecture Rules (Re-injected After Compaction)

**10 Rules:**
1. Interface + Impl pairs for DataSource/Repository
2. Either<T> for errors - NEVER throw exceptions
3. setState { copy() } - NEVER _state.value =
4. 4 UI states: Uninitialized / Loading / Success / Failed
5. X-components from :core:designsystem - NO Material3
6. ImmutableList with .toImmutableList()
7. Lowercase packages only
8. DI: singleOf(::Impl).bind<Interface>() + BaseFeature
9. No UseCases - ViewModels call repositories directly
10. Callback params (onBackClick) - not navController

**4 Integration Points (all required):**
1. settings.gradle.kts - include(":feature:{name}")
2. composeApp/build.gradle.kts - implementation(project(":feature:{name}"))
3. initKoin.kt - {Feature}Modules.initialize()
4. BaseAppNavHost.kt - {featurename}(onBackClick = {...})

**Mandatory Workflow:** NEVER edit feature/ files directly. Use /creating-kmp-feature or /modifying-kmp-feature.

Full patterns: @.claude/skills/_shared/patterns.md
RULES
