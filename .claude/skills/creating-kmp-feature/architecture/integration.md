# Integration Architecture Principles

Principles for integrating features into KMP apps. Every feature requires exactly 4 integration points.

**Note**: Uses placeholders that are resolved via Context Discovery:
- `{PKG_PREFIX}` - Package prefix (e.g., `com.example`, `com.myapp`)
- `{INIT_KOIN_PATH}` - Path to initKoin.kt file
- `{NAV_HOST_PATH}` - Path to navigation host file

## Integration Overview

**4 Required Integration Points**:
1. **Gradle Include** - Add module to project
2. **Gradle Dependency** - Link feature to app
3. **DI Initialization** - Register Koin modules
4. **Navigation Wiring** - Wire navigation callbacks

Missing any of these will result in build errors or runtime crashes.

## Spec Generation (Integration Agent Responsibility)

The integration agent generates `spec.md` as the living documentation for the feature.

**CRITICAL: Preserve WHY Sections from PRD**

Before PRD is deleted during cleanup, the integration agent MUST copy these sections to spec.md:

| PRD Section | Spec Section | Why It Matters |
|-------------|--------------|----------------|
| Goals | Goals | Documents intended outcomes |
| Non-Goals | Non-Goals | Prevents future scope creep |
| Background & Rationale | Background & Rationale | Explains why feature exists |
| Design Decisions | Design Decisions | Preserves architectural context |

**Process:**
1. Read PRD at `.claude/docs/{featurename}/prd.md`
2. Extract Goals, Non-Goals, Background & Rationale, Design Decisions
3. Include these sections verbatim in generated spec.md
4. Add implementation details from actual code
5. Spec.md now contains both WHY (from PRD) and WHAT (from code)

## Critical Rules (Integration)

1. **Lowercase Packages**: `{PKG_PREFIX}.featurename` (never `feature-name`, `featureName`, or `feature_name`)
2. **DI Pattern**: Extend `BaseFeature`, use `singleOf(::Impl).bind<Interface>()` + `viewModelOf(::ViewModel)`
3. **Navigation Callbacks**: Features receive callbacks, navigation logic stays in navigation host file

## 1. Gradle Include (Module Registration)

**Purpose**: Register feature module with Gradle build system

**File**: `settings.gradle.kts` (project root)

**Pattern**: Add `include(":feature:{featurename}")` at end of file

**Example**:
```kotlin
include(":feature:productdetail")
```

**Key Points**:
- Feature name matches directory name (lowercase, no hyphens)
- Add after existing feature includes
- Build will fail if this is missing

## 2. Gradle Dependency (App Link)

**Purpose**: Link feature module to main app

**File**: `composeApp/build.gradle.kts`

**Pattern**: Add `implementation(project(":feature:{featurename}"))` to dependencies block

**Example**:
```kotlin
sourceSets {
    commonMain {
        dependencies {
            // ... existing dependencies
            implementation(project(":feature:productdetail"))
        }
    }
}
```

**Key Points**:
- Goes in `commonMain` dependencies (KMP shared code)
- Feature name must match settings.gradle.kts
- Build will fail if this is missing or incorrect

## 3. DI Initialization (Koin Registration)

**Purpose**: Register feature's Koin modules with app's dependency injection system

**File**: `{INIT_KOIN_PATH}` (auto-detected via Context Discovery)

**Pattern**:
1. Import feature's `{Feature}Modules` object
2. Call `{Feature}Modules.initialize()` in appropriate function

**Example** (using detected `{PKG_PREFIX}`):
```kotlin
import {PKG_PREFIX}.productdetail.di.ProductDetailModules

private fun initializeFeatures() {
    // ... existing CommonModules / DataModules / FeatureModules initialize() calls
    ProductDetailModules.initialize()
}

fun initKoin(appDeclaration: KoinAppDeclaration = {}): KoinApplication {
    initializeFeatures()

    return startKoin {
        appDeclaration()
        modules(getAllModules())
    }
}
```

**Key Points**:
- Add the new `initialize()` call inside the existing `initializeFeatures()` private function (don't invent a new structure — match what's already in `initKoin.kt`).
- Import must use correct package name (lowercase).
- Order doesn't matter (features are independent).
- Runtime crash if missing (Koin will fail to resolve the ViewModel/Repository).

**Typical initKoin.kt structure**:
- `private fun initializeFeatures()` listing every `{Feature}Modules.initialize()` call
- `fun initKoin(appDeclaration: KoinAppDeclaration = {})` that calls `initializeFeatures()` then `startKoin`
- `getAllModules()` aggregates `FeatureRegistry.getAllKoinModules()` plus the app-level module

## 4. Navigation Wiring (Route Registration)

**Purpose**: Wire feature's navigation routes into app's navigation graph

**File**: `{NAV_HOST_PATH}` (auto-detected via Context Discovery)

**Pattern**:
1. Import feature's navigation extension function and route type
2. Call extension function inside `XNavHost` with callback parameters
3. Callbacks use `navController` to perform navigation

**Example** (using detected `{PKG_PREFIX}`):
```kotlin
import {PKG_PREFIX}.productdetail.presentation.navigation.ProductDetailRoute
import {PKG_PREFIX}.productdetail.presentation.navigation.productdetail

@Composable
fun BaseAppNavHost(navController: NavHostController) {
    XNavHost(
        navController = navController,
        startDestination = HomeRoute
    ) {
        // ... other features

        productdetail(
            onBackClick = { navController.navigateUp() },
            onOrderSuccess = { navController.navigate(OrdersRoute) }
        )
    }
}
```

**Key Points**:
- Extension function name is lowercase feature name (e.g., `productdetail`)
- Route type is PascalCase (e.g., `ProductDetailRoute`)
- Callbacks handle all navigation (features don't have navController)
- Each callback uses navController to navigate or pop
- Navigation logic centralized in BaseAppNavHost.kt

**Common callback patterns**:
- `onBackClick = { navController.navigateUp() }` - Go back
- `onNavigateTo{Feature} = { navController.navigate({Feature}Route) }` - Navigate to another screen
- `onNavigateTo{Feature} = { id -> navController.navigate({Feature}Route(id)) }` - Navigate with parameters

## DI Pattern (feature/di/{Feature}Modules.kt)

**Purpose**: Define feature's dependency injection modules

**Pattern**:
- Object that extends `BaseFeature`
- Overrides `getKoinModules()` to return list of Koin modules
- Overrides `initialize()` to trigger auto-registration
- Uses `singleOf` + `bind` for interface/impl pairs
- Uses `viewModelOf` for ViewModels

**Structure**:
```kotlin
package {PKG_PREFIX}.{featurename}.di

import {PKG_PREFIX}.common.di.base.BaseFeature
import org.koin.core.module.Module
import org.koin.core.module.dsl.singleOf
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.bind
import org.koin.dsl.module

object {Feature}Modules : BaseFeature({Feature}Modules::class.simpleName.toString()) {
    override fun getKoinModules(): List<Module> = listOf(
        module {
            // DataSource (if exists)
            singleOf(::{Feature}RemoteDataSourceImpl).bind<{Feature}RemoteDataSource>()

            // Repository
            singleOf(::{Feature}RepositoryImpl).bind<{Feature}Repository>()

            // ViewModel
            viewModelOf(::{Feature}ViewModel)
        }
    )

    override fun initialize() {
        {Feature}Modules
    }
}
```

**Key Points**:
- Object name: `{Feature}Modules` (matches class name, PascalCase)
- Extends `BaseFeature` with feature name as parameter
- `singleOf(::Impl).bind<Interface>()` creates singleton with interface binding
- `viewModelOf(::ViewModel)` registers ViewModel
- `initialize()` returns the object itself (triggers BaseFeature registration)
- Order matters: DataSource before Repository, Repository before ViewModel

## Navigation Pattern (feature/presentation/navigation/)

**Purpose**: Define feature routes and navigation integration point

**Pattern**:
- Define `@Serializable` route object(s)
- Create `NavGraphBuilder` extension function
- Use `composable<Route>` type-safe builder
- Accept callback parameters for navigation
- Inject ViewModel with `koinViewModel()`

**Structure**:
```kotlin
package {PKG_PREFIX}.{featurename}.presentation.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import kotlinx.serialization.Serializable
import org.koin.compose.viewmodel.koinViewModel

// Route definition(s)
@Serializable
data class {Feature}Route(val param: Type)  // or data object for no params

// Navigation extension
fun NavGraphBuilder.{featurename}(
    onBackClick: () -> Unit,
    onNavigateTo{Destination}: (Type) -> Unit,
) {
    composable<{Feature}Route> { backStackEntry ->
        val route = backStackEntry.toRoute<{Feature}Route>()
        {Feature}Screen(
            param = route.param,  // Extract parameters
            onBackClick = onBackClick,
            onNavigateTo{Destination} = onNavigateTo{Destination},
            viewModel = koinViewModel(viewModelStoreOwner = backStackEntry)
        )
    }
}
```

**Key Points**:
- Extension function name: lowercase feature name (e.g., `productdetail`, not `productDetail`)
- Route name: PascalCase with `Route` suffix (e.g., `ProductDetailRoute`)
- Route parameters: constructor properties (e.g., `val productId: Int`)
- No parameters: use `data object` instead of `data class`
- Extract params with `backStackEntry.toRoute<{Feature}Route>()`
- Pass callbacks through to Screen composable
- ViewModel scoped to backStackEntry for proper lifecycle

**Multiple screens in one feature**:
If feature has multiple screens, create multiple routes and multiple composable entries:
```kotlin
@Serializable
data class MainRoute(val id: Int)

@Serializable
data class DetailRoute(val id: Int, val subId: String)

fun NavGraphBuilder.feature(
    onBackClick: () -> Unit,
) {
    composable<MainRoute> { /* ... */ }
    composable<DetailRoute> { /* ... */ }
}
```

## Build Validation

**Incremental validation** (during development):
```bash
./gradlew :feature:{featurename}:assembleAndroidMain
```

**Full validation** (integration complete):
```bash
./gradlew assembleDebug
./gradlew ktlintFormat
```

**What full validation checks**:
- All 4 integration points configured correctly
- No compilation errors
- No DI configuration issues
- Code formatting with ktlint
- All dependencies resolved

## Common Integration Errors

**1. Missing Gradle Include**:
- Error: "Project ':feature:{featurename}' not found"
- Fix: Add `include(":feature:{featurename}")` to settings.gradle.kts

**2. Wrong Package Name**:
- Error: "Unresolved reference: {featurename}"
- Fix: Ensure lowercase package naming everywhere (import statements, package declarations)

**3. Missing DI Initialization**:
- Error: Runtime crash "No definition found for type {ViewModel/Repository}"
- Fix: Add `{Feature}Modules.initialize()` to initKoin.kt

**4. Missing Navigation Wiring**:
- Error: Navigation doesn't work, screen not found
- Fix: Add navigation extension call in BaseAppNavHost.kt

**5. Wrong Extension Function Name**:
- Error: "Unresolved reference" in BaseAppNavHost.kt
- Fix: Extension function must be lowercase (e.g., `productdetail`, not `productDetail`)
