# Integration Troubleshooting

Quick fixes for common integration errors.

## Module Not Included

**Error:** `Project ':feature:x' not found in root project`

**Fix:** Add to `settings.gradle.kts`:
```kotlin
include(":feature:x")
```

## Feature Dependency Not Found

**Error:** `Could not find :feature:x`

**Fix:** Add to `composeApp/build.gradle.kts`:
```kotlin
dependencies {
    implementation(project(":feature:x"))
}
```

## DI Module Not Initialized

**Error:** `No definition found for class` (at runtime)

**Fix:** Initialize in `initKoin.kt`:
```kotlin
import {PKG_PREFIX}.featurename.di.FeatureModules

private fun initializeFeatures() {
    // ... existing features
    FeatureModules.initialize()
}
```

## Navigation Extension Not Found

**Error:** `Unresolved reference: featurename (in NavGraphBuilder)`

**Fix:** Import extension in `BaseAppNavHost.kt`:
```kotlin
import {PKG_PREFIX}.featurename.presentation.navigation.featurename
import {PKG_PREFIX}.featurename.presentation.navigation.FeatureRoute
```

## Koin Binding Missing

**Error:** `No definition found for UserRepository`

**Fix:** Bind interfaces in DI module:
```kotlin
// Wrong: singleOf(::UserRepositoryImpl)
// Right: singleOf(::UserRepositoryImpl).bind<UserRepository>()
```

## BaseFeature Not Extended

**Error:** DI module doesn't auto-register

**Fix:** Ensure module extends BaseFeature:
```kotlin
// Wrong:
object ProfileModules {
    val modules = listOf(...)
}

// Right:
object ProfileModules : BaseFeature(ProfileModules::class.simpleName.toString()) {
    override fun getKoinModules(): List<Module> = listOf(...)
    override fun initialize() { ProfileModules }
}
```

## Navigation Route Not Found

**Error:** `Unresolved reference: ProfileRoute`

**Fix:** Ensure route is @Serializable and imported:
```kotlin
// In ProfileNavigation.kt:
@Serializable
data object ProfileRoute

// In BaseAppNavHost.kt:
import {PKG_PREFIX}.profile.presentation.navigation.ProfileRoute
```

## Build Commands

```bash
# Full validation (required for integration)
./gradlew assembleDebug

# Clean build if caching issues
./gradlew clean assembleDebug

# Format code
./gradlew ktlintFormat

# With stacktrace for detailed errors
./gradlew assembleDebug --stacktrace
```

## Integration Checklist

If build fails, verify all 4 integration points:
1. ✅ `settings.gradle.kts` includes module
2. ✅ `composeApp/build.gradle.kts` has dependency
3. ✅ `initKoin.kt` initializes modules
4. ✅ `BaseAppNavHost.kt` wires navigation

## General Strategy

1. Read error line number carefully
2. Check architecture/integration.md for correct pattern
3. Verify all 4 integration points completed
4. Fix and rebuild with `assembleDebug`
5. If stuck after 3 attempts, escalate to user
