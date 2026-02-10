# Feature: Sample

## Metadata
| Field | Value |
|-------|-------|
| Version | 2.2.0 |
| Status | Approved |
| Author | System |
| Created | 2026-01-05 |
| Updated | 2026-02-10 |
| Reviewers | N/A |

---

## 1. Overview

### 1.1 Summary
A reference feature that demonstrates the standard KMP Clean Architecture patterns, state management, and UI composition using X-components design system. Serves as a canonical template for new feature development.

### 1.2 Goals
- Demonstrate the 4-state UI pattern (Uninitialized, Loading, Success, Failed)
- Showcase Clean Architecture layer separation (data, presentation, DI)
- Provide reference implementation for state management with `setState {}`
- Illustrate proper navigation patterns and DI setup

### 1.3 Non-Goals
- Active API usage in repository (infrastructure ready, mock data used for testing)
- Complex business logic (simplified for demonstration)
- Production-ready feature (educational reference only)

---

## 2. Context

### 2.1 Background
The sample feature was created to provide a working reference implementation of all KMP architecture patterns. It demonstrates the complete feature lifecycle from data layer to UI without requiring external dependencies, making it ideal for onboarding and pattern validation.

### 2.2 Dependencies
- `:core:common` - UiState, ErrorModel, setState extension
- `:core:designsystem` - X-components (XButton, XText, XCard, etc.)
- Koin - Dependency injection framework
- Kotlinx Serialization - Route serialization
- Compose Navigation - Screen navigation

### 2.3 Constraints
- Must use mock data only (no real API)
- Must demonstrate all critical patterns
- Must build successfully on both Android and iOS
- Must use X-components exclusively (no Material3)
- UI must demonstrate premium visual design using X-components

---

## 3. Requirements

### 3.1 Functional Requirements

| ID | Requirement | Priority |
|----|-------------|----------|
| FR-1 | Display list of sample items with title, description | Must |
| FR-2 | Show loading state while data is being fetched | Must |
| FR-3 | Handle empty state when no items available | Must |
| FR-4 | Handle error state with retry capability | Must |
| FR-5 | Support item selection and trigger navigation callback | Must |
| FR-6 | Display uninitialized state before data load begins | Should |
| FR-7 | Provide API infrastructure for future remote data integration | Should |

### 3.2 Non-Functional Requirements

| ID | Requirement | Target |
|----|-------------|--------|
| NFR-1 | State transitions | Explicit setState {} usage only |
| NFR-2 | Architecture compliance | 100% adherence to 10 critical rules |
| NFR-3 | Design system usage | X-components only, zero Material3 |
| NFR-4 | Code quality | ktlint passing, no warnings |

---

## 4. Design

### 4.1 Architecture

```
┌─────────────────────────────────────────────────┐
│                  UI Layer                        │
│  ┌─────────────────────────────────────────┐    │
│  │         SampleScreen                    │    │
│  │      (Collects StateFlow)               │    │
│  └──────────────────┬──────────────────────┘    │
│                     │                            │
│  ┌──────────────────▼──────────────────────┐    │
│  │       SampleScreenRoot                  │    │
│  │  (4-state rendering: U/L/S/F)           │    │
│  └──────────────────┬──────────────────────┘    │
│                     │                            │
│           ┌─────────▼─────────┐                  │
│           │   SampleViewModel │                  │
│           │ (MutableStateFlow)│                  │
│           └─────────┬─────────┘                  │
├─────────────────────┼───────────────────────────┤
│                     ▼      Domain Layer          │
│  ┌──────────────────────────────────────────┐   │
│  │         SampleRepository                 │   │
│  │   (Interface + Impl pair)                │   │
│  └──────────────────┬───────────────────────┘   │
├─────────────────────┼───────────────────────────┤
│                     ▼       Data Layer           │
│  ┌─────────────────────────────────────────┐    │
│  │    SampleLocalDataSource                │    │
│  │   (Interface + Impl with mock data)     │    │
│  └─────────────────────────────────────────┘    │
└─────────────────────────────────────────────────┘
```

### 4.2 Components

| Component | Responsibility |
|-----------|----------------|
| SampleLocalDataSource | Interface for local data operations |
| SampleLocalDataSourceImpl | Mock data implementation (3 sample items) |
| SampleRemoteDataSource | Interface for remote API operations |
| SampleRemoteDataSourceImpl | API implementation using ApiClient |
| SampleResources | Ktor Resource definitions for /api/sample/ endpoint |
| SampleRepository | Interface for business logic |
| SampleRepositoryImpl | Business logic implementation (currently uses local data) |
| SampleViewModel | State management using MutableStateFlow + setState |
| SampleUiModel | UI state container with 4-state pattern |
| SampleScreen | Main composable connecting ViewModel to UI |
| SampleScreenRoot | ViewModel-independent root for testing |
| SampleCard | Premium item card: crimson accent bar, bold title, ghost index number, XCard-based |
| SampleRoute | Navigation route (@Serializable data object) |
| SampleModules | DI configuration (BaseFeature object) |

### 4.3 Data Models

```kotlin
/**
 * Sample data model demonstrating the pattern.
 */
@Serializable
data class SampleItem(
    val id: String,
    val title: String,
    val description: String,
    val imageUrl: String? = null,
)
```

### 4.4 Package Structure

```
feature/sample/src/commonMain/kotlin/thisissadeghi/sample/
├── data/
│   ├── model/
│   │   └── SampleItem.kt           # Data model (@Serializable)
│   ├── remote/
│   │   └── SampleResources.kt      # Ktor Resource for /api/sample/
│   ├── datasource/
│   │   ├── SampleLocalDataSource.kt        # Interface
│   │   ├── SampleLocalDataSourceImpl.kt    # Mock implementation
│   │   ├── SampleRemoteDataSource.kt       # Interface (remote)
│   │   └── SampleRemoteDataSourceImpl.kt   # API implementation
│   └── repository/
│       ├── SampleRepository.kt             # Interface
│       └── SampleRepositoryImpl.kt         # Implementation (uses local, can switch to remote)
├── presentation/
│   ├── SampleViewModel.kt          # MutableStateFlow + setState {}
│   ├── SampleUiModel.kt            # @Stable data class
│   ├── ui/
│   │   ├── SampleScreen.kt         # Main composable
│   │   └── components/
│   │       └── SampleCard.kt       # Item card component
│   └── navigation/
│       └── SampleNavigation.kt     # @Serializable route + extension
└── di/
    └── SampleModules.kt            # BaseFeature object with Koin modules
```

### 4.5 UI Design

**SampleCard Layout:**
- `XCard` (white, 1dp elevation) with `IntrinsicSize.Min` row
- Left: 3dp crimson accent bar (primary color), full card height
- Center: `XText` bold title + muted description
- Right: ghost index number (36sp Black, `GhostGray #E5E4E7`)

**SampleScreenRoot States:**
- Uninitialized: oversized ghost "SAMPLE" wordmark + "Collection" subtitle
- Loading: `XCircularProgressIndicator` + "Loading collection" label
- Success: `XTopAppBar` "Collection" title + editorial header (COLLECTION label / item count / divider) + card list
- Empty: em-dash decorative mark + "Nothing here" + subtitle text
- Error: centered `XCard` with top crimson strip, error message, `XButton` "Try Again"

**Private color tokens:**
- `TitleDark = Color(0xFF323036)`
- `TextMutedGray = Color(0xFF7D7887)`
- `GhostGray = Color(0xFFE5E4E7)`

---

## 5. Interfaces

### 5.1 API Contracts

**Endpoint:** `GET /api/sample/`

**Description:** Fetches all sample items.

**Authentication:** Not required (demonstration endpoint)

**Response:** `200 OK`
```kotlin
List<SampleItem>
```

**Response Model:**
```kotlin
@Serializable
data class SampleItem(
    val id: String,
    val title: String,
    val description: String,
    val imageUrl: String? = null,
)
```

**Notes:**
- API infrastructure is implemented but not currently used by repository
- Repository uses mock local data for testing purposes
- Easy to switch to remote data source by changing repository implementation

### 5.2 Internal Contracts

```kotlin
/**
 * Local data source for sample items.
 */
interface SampleLocalDataSource {
    /**
     * Fetches all sample items.
     *
     * Contract:
     * - Returns list of sample items (may be empty)
     * - Never throws exceptions
     * - Synchronous operation (mock data)
     */
    fun getItems(): List<SampleItem>
}

/**
 * Remote data source for sample items via API.
 */
interface SampleRemoteDataSource {
    /**
     * Fetches sample items from remote API.
     *
     * Contract:
     * - Returns Either<List<SampleItem>>
     * - Left: ErrorModel on failure
     * - Right: List of sample items (may be empty)
     * - Suspending operation (network call)
     */
    suspend fun getSampleItems(): Either<List<SampleItem>>
}

/**
 * Repository for sample feature business logic.
 *
 * Note: Currently uses local mock data for testing.
 * Can easily switch to remote data source by injecting
 * SampleRemoteDataSource instead of SampleLocalDataSource.
 */
interface SampleRepository {
    /**
     * Fetches sample items from data source.
     *
     * Contract:
     * - Returns list of sample items (may be empty)
     * - Never throws exceptions
     * - Synchronous operation (no suspend needed for mock)
     */
    fun getItems(): List<SampleItem>
}
```

### 5.3 External Integrations

Navigation callbacks:
- `onItemClick: (String) -> Unit` - Triggered when user taps item, passes item ID

---

## 6. Behavior

### 6.1 User Flows

#### View Sample Items Flow
1. User navigates to Sample screen via `navController.navigate(SampleRoute)`
2. Screen enters Uninitialized state
3. ViewModel calls `loadItems()` on init
4. State transitions to Loading
5. Repository fetches mock data from DataSource
6. On success: State transitions to Success with item list
7. On error: State transitions to Failed with error
8. User sees list rendered based on state

#### Item Selection Flow
1. User taps on item card
2. ViewModel's `onItemClick()` is called
3. ViewModel updates `selectedItem` in state using `setState {}`
4. Navigation callback is triggered with item ID

#### Retry Flow
1. User is in Failed state (error occurred)
2. User taps "Retry" button
3. ViewModel calls `retry()` which triggers `loadItems()`
4. State transitions back to Loading
5. Process repeats from step 5 of View Flow

### 6.2 State Management

#### UiState Structure

```kotlin
/**
 * UI model for the sample feature.
 * Demonstrates the 4-state UI pattern: Uninitialized, Loading, Success, Failed.
 */
@Stable
data class SampleUiModel(
    val itemsState: UiState<List<SampleItem>> = UiState.Uninitialized,
    val selectedItem: SampleItem? = null,
)
```

#### State Transitions

```
Uninitialized ──[loadItems()]──► Loading
                                    │
                    ┌───────────────┴───────────────┐
                    │                               │
             [success]                          [failure]
                    │                               │
                    ▼                               ▼
                Success                         Failed
                    │                               │
                    │                               │
             [refresh]──► Loading           [retry]─┘
                    │
                    └──────────────────────────────┘
```

**State Definitions:**
1. **Uninitialized**: Initial state, no data loaded yet
2. **Loading**: Data fetch in progress, show loading indicator
3. **Success**: Data loaded successfully, display items (or empty state if list is empty)
4. **Failed**: Error occurred, show error message with retry button

**ViewModel Actions:**
- `loadItems()`: Fetches items from repository (called on init)
- `onItemClick(item: SampleItem)`: Updates selected item using `setState { copy(selectedItem = item) }`
- `retry()`: Re-triggers `loadItems()` after error

### 6.3 Error Handling

| Error Scenario | User Message | Action Available |
|----------------|--------------|------------------|
| Generic exception | Error message in premium card overlay | "Try Again" XButton |
| Empty list | "No items found" | None (not an error state) |

---

## 7. Testing

### 7.1 Test Scenarios

| Scenario | Given | When | Then |
|----------|-------|------|------|
| Items load successfully | User navigates to Sample | Data loads from mock source | List of 3 items displayed, state = Success |
| Items list is empty | Mock source returns empty list | Data loads | "No items found" message shown |
| Items fail to load | Mock source throws exception | Data load attempted | Error overlay with retry button shown |
| Retry after error | Error state displayed | User taps retry | Loading state shown, data reloaded |
| Select item | Items displayed successfully | User taps item card | selectedItem updated, callback triggered |
| Navigate back | User on Sample screen | User taps back | Returns to previous screen |

### 7.2 Acceptance Criteria

**Display & States:**
- [ ] Uninitialized state shown initially
- [ ] Loading indicator shown during data fetch
- [ ] Success state displays all items correctly
- [ ] Empty state shows when list is empty
- [ ] Failed state shows error message and retry button

**State Management:**
- [ ] All state updates use `setState {}`
- [ ] No direct state assignment anywhere in ViewModel
- [ ] StateFlow properly collected in composable

**Architecture:**
- [ ] Interface + Impl pairs created for DataSource and Repository
- [ ] 4 integration points completed (settings.gradle.kts, build.gradle.kts, initKoin, navigation)
- [ ] X-components used exclusively (no Material3)
- [ ] ktlint passes without errors

**Navigation:**
- [ ] SampleRoute defined as @Serializable data object
- [ ] NavGraphBuilder extension created
- [ ] Navigation callback works correctly

---

## Appendix

### A. Glossary

| Term | Definition |
|------|------------|
| 4-state pattern | UI state management with Uninitialized, Loading, Success, Failed states |
| setState {} | Extension function for safe state updates in ViewModel |
| Either<T> | Functional error handling type (not used in Sample - no API) |
| BaseFeature | Koin module pattern for feature DI setup |
| X-components | Custom design system components (`:core:designsystem`) |
| ScreenRoot | ViewModel-independent composable for testing |

### B. References

- Core common library: `core/common/`
- Design system: `core/designsystem/`
- Architecture patterns: `.claude/skills/creating-kmp-feature/references/patterns.md`

### C. Changelog

| Version | Date | Changes |
|---------|------|---------|
| 2.2.0 | 2026-02-10 | Premium UI redesign: SampleCard with crimson accent bar + ghost index number; SampleScreenRoot with XScaffold/XTopAppBar, editorial header, premium state screens. Full X-components compliance. |
| 2.1.0 | 2026-01-20 | Added API infrastructure (Ktor Resource, RemoteDataSource) for /api/sample/ endpoint. Repository still uses mock data for testing, can be easily switched to remote. |
| 2.0 | 2026-01-20 | Updated spec to match new SDD patterns with full template structure |
| 1.0 | 2026-01-05 | Initial spec generated from existing implementation |

### D. Integration Points

| Point | File | Status |
|-------|------|--------|
| Module include | settings.gradle.kts | ✅ Completed |
| Dependency | composeApp/build.gradle.kts | ✅ Completed |
| DI initialization | initKoin.kt | ✅ Completed |
| Navigation wiring | BaseAppNavHost.kt | ✅ Completed |
