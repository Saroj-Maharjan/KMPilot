# Sample Specification

## Purpose
A reference feature that demonstrates the standard KMP Clean Architecture patterns, state management, and UI composition. Serves as a template for new feature development.

## Background
The sample feature was created to showcase best practices for KMP feature development, including the 4-state UI pattern (Uninitialized, Loading, Success, Failed), Clean Architecture layer separation, and proper dependency injection setup. It uses local mock data to demonstrate the pattern without requiring external dependencies.

## Last Updated
- 2026-01-05 - Generated from existing implementation

## Requirements

### Requirement: Display Sample Items
The system SHALL provide a list view of sample items demonstrating the data flow from repository to UI.

#### Scenario: Sample items load successfully
- GIVEN the user navigates to Sample screen
- WHEN the data loads successfully
- THEN a list of sample items MUST be displayed
- AND the loading state MUST transition to success

#### Scenario: Sample items list is empty
- GIVEN the user is on Sample screen
- WHEN the repository returns an empty list
- THEN a "No items found" message MUST be displayed

#### Scenario: Sample items fail to load
- GIVEN the user is on Sample screen
- WHEN an error occurs during data loading
- THEN an error message MUST be displayed
- AND a retry button MUST be available

### Requirement: Item Selection
The system SHALL allow users to select individual items and navigate to item details.

#### Scenario: User selects an item
- GIVEN the user is viewing the sample items list
- WHEN the user taps on an item
- THEN the item MUST be marked as selected in the UI state
- AND the navigation callback MUST be triggered with the item ID

## Architecture

### Package Structure
```
feature/sample/src/commonMain/kotlin/thisissadeghi/sample/
├── data/
│   ├── model/
│   │   └── SampleItem.kt           # Data model (@Serializable)
│   ├── datasource/
│   │   ├── SampleLocalDataSource.kt        # Interface
│   │   └── SampleLocalDataSourceImpl.kt    # Mock implementation
│   └── repository/
│       ├── SampleRepository.kt             # Interface
│       └── SampleRepositoryImpl.kt         # Implementation
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

### Data Flow
```
[LocalDataSource] → Repository → ViewModel → [UI]
   List<SampleItem>  List<SampleItem>  UiState<List<SampleItem>>
```

### Key Classes
| Class | Purpose | Location |
|-------|---------|----------|
| SampleLocalDataSource | Local data operations interface | data/datasource/ |
| SampleLocalDataSourceImpl | Mock data implementation | data/datasource/ |
| SampleRepository | Business logic interface | data/repository/ |
| SampleRepositoryImpl | Business logic implementation | data/repository/ |
| SampleViewModel | State management (MutableStateFlow + setState) | presentation/ |
| SampleUiModel | UI state container (4-state pattern) | presentation/ |
| SampleScreen | UI composition (Material3) | presentation/ui/ |
| SampleScreenRoot | ViewModel-independent root for testing | presentation/ui/ |
| SampleCard | Item card component | presentation/ui/components/ |
| SampleRoute | Navigation route (@Serializable data object) | presentation/navigation/ |
| SampleModules | DI configuration (BaseFeature) | di/ |

### Data Models
```kotlin
/**
 * Sample data model demonstrating the pattern.
 */
data class SampleItem(
    val id: String,
    val title: String,
    val description: String,
    val imageUrl: String? = null,
)
```

## Integration Points

| Point | File | Status |
|-------|------|--------|
| Module include | settings.gradle.kts | ✅ |
| Dependency | composeApp/build.gradle.kts | ✅ |
| DI init | initKoin.kt | ✅ |
| Navigation | BaseAppNavHost.kt | ✅ |

## State Management

### UiState Structure
```kotlin
/**
 * UI model for the sample feature.
 * Demonstrates the 4-state UI pattern: Uninitialized, Loading, Success, Failed.
 */
data class SampleUiModel(
    val itemsState: UiState<List<SampleItem>> = UiState.Uninitialized,
    val selectedItem: SampleItem? = null,
)
```

### State Transitions
1. **Initial State**: `itemsState = UiState.Uninitialized`, `selectedItem = null`
2. **Loading**: `setState { copy(itemsState = UiState.Loading) }`
3. **Success**: `setState { copy(itemsState = UiState.Success(items)) }`
4. **Failed**: `setState { copy(itemsState = UiState.Failed(ErrorModel.Exception(e))) }`
5. **Item Selection**: `setState { copy(selectedItem = item) }`
6. **Retry**: Triggers `loadItems()` which resets to Loading state

### ViewModel Actions
- `loadItems()`: Fetches items from repository (called on init)
- `onItemClick(item: SampleItem)`: Updates selected item in state
- `retry()`: Re-triggers data loading on error

## Navigation

- **Route:** `SampleRoute` (data object)
- **Entry:** `navController.navigate(SampleRoute)`
- **Callbacks:**
  - `onItemClick: (String) -> Unit` - Triggered when user taps an item, passes item ID

## UI Components

### SampleScreen
Main composable that connects ViewModel to UI:
- Collects `uiModelState` as StateFlow
- Delegates to `SampleScreenRoot` for rendering
- Handles ViewModel actions (`onItemClick`, `retry`)

### SampleScreenRoot
ViewModel-independent composable for testing:
- Implements 4-state pattern with `when` expression
- **Uninitialized**: Shows welcome message
- **Loading**: Shows CircularProgressIndicator
- **Success**: Shows LazyColumn with SampleCard items or empty state
- **Failed**: Shows error message with retry button

### SampleCard
Reusable item card component displaying item details

## Mock Data
The feature uses local mock data from `SampleLocalDataSourceImpl`:
- 3 sample items with varying properties
- Demonstrates optional `imageUrl` field
- Simulates successful data retrieval

## Dependencies
- `:core:common` - UiState, ErrorModel, setState extension
- Material3 - UI components (Scaffold, Button, Text, etc.)
- Koin - Dependency injection
- Kotlinx Serialization - Route serialization
- Compose Navigation - Screen navigation
