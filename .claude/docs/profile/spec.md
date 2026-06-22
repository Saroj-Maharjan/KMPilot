# Profile Specification

## Metadata
| Field | Value |
|-------|-------|
| Version | 1.0.0 |
| Status | Active |
| Created | 2026-06-22 |
| Updated | 2026-06-22 |

## Purpose
User profile screen showing account details (name, email, member tier) and preferences (biometric toggle). Accessible via a profile icon button in the Dashboard header. Supports viewing and editing profile data.

## Goals
- Allow users to view their account details (name, email, member tier) in a dedicated profile screen
- Expose a biometric preference toggle on the profile screen
- Allow users to update their name and email via an edit form screen
- Provide access to the profile from the Dashboard header via a tappable avatar placeholder

## Non-Goals
- Real API integration (mock data for now; DataSource interface is in place for future swap)
- Photo upload or camera access (Change Photo button is a stub)
- Privacy Settings and Notifications settings (rows are stubs in Edit Profile)

## Background & Rationale
A profile screen is a standard navigation destination for user account management. The feature is accessed from the Dashboard header to keep the primary navigation surface clean. The DataSource uses mock data initially so the architecture (interface + impl) is ready to swap in a real endpoint without touching the rest of the stack.

## Platform Profile & Capabilities
| Field | Value |
|-------|-------|
| Platform Profile | network |
| Capabilities | none |
| Native view | No |
| Sourcing option | n/a |
| iOS-Swift bridge | No |

## Design Decisions
| Decision | Choice | Alternatives | Rationale |
|----------|--------|--------------|-----------|
| Avatar placeholder in Dashboard header | `Box` with `CircleShape` + `clickable` | `XIconButton` with person icon | `XIconButton` enforces 40dp min height which looked wrong in the compact header; `Box` avoids that constraint. `person.xml` is in the profile feature, not the design system — Dashboard must not import from another feature. |
| Edit Profile as child route | `composable<ProfileEditRoute>` inside `NavGraphBuilder.profile()` | Separate feature module | Features never depend on other features; edit-profile is a secondary screen of the same feature and shares the ViewModel |
| Mock DataSource | Hardcoded `ProfileData` in `ProfileDataSourceImpl` | Real API endpoint | DataSource interface is in place; mock lets the UI layer be built and tested immediately without a backend |
| Edit Profile shape | Shape B (form — no async UiState routing for the form itself) | Shape A (data-fetch) | The edit form does not load remote data; it pre-fills from the already-loaded `ProfileData` held on the ViewModel |

## Last Updated
- 2026-06-22 - Generated from implemented feature

## Requirements

### Requirement: Profile View
The system SHALL display profile data (name, email, member tier, biometric preference) once loaded.

#### Scenario: Profile loads successfully
- GIVEN the user taps the profile button on the Dashboard header
- WHEN `ProfileRepository.getProfile()` returns `Either.Success`
- THEN the Profile screen MUST show avatar initials, AccountDetailCard, and PreferencesCard
- AND the loading state MUST transition to success

#### Scenario: Profile handles errors
- GIVEN the user is on the Profile screen
- WHEN `ProfileRepository.getProfile()` returns `Either.Failure`
- THEN `AppErrorState` MUST be displayed with retry

### Requirement: Profile Edit
The system SHALL allow users to update their name and email.

#### Scenario: Edit profile navigates
- GIVEN the Profile screen is showing success state
- WHEN the user taps "Edit Profile"
- THEN the Edit Profile screen MUST be pushed onto the back stack

#### Scenario: Back from edit returns to profile
- GIVEN the user is on Edit Profile
- WHEN the user taps back or "Save Changes"
- THEN the app MUST pop back to the Profile screen

### Requirement: Dashboard Entry Point
The system SHALL provide a tappable profile button in the Dashboard header.

#### Scenario: Profile accessible from Dashboard
- GIVEN the app is running and the Dashboard is visible
- WHEN the user taps the circular avatar button in the header trailing slot
- THEN the Profile screen MUST be pushed

## Architecture

### Package Structure
```
feature/profile/src/commonMain/kotlin/thisissadeghi/profile/
├── data/
│   ├── model/
│   │   └── ProfileData.kt
│   ├── datasource/
│   │   ├── ProfileDataSource.kt
│   │   └── ProfileDataSourceImpl.kt
│   └── repository/
│       ├── ProfileRepository.kt
│       └── ProfileRepositoryImpl.kt
├── presentation/
│   ├── ProfileViewModel.kt
│   ├── ProfileUiModel.kt
│   ├── ui/
│   │   ├── ProfileScreen.kt
│   │   ├── ProfileEditScreen.kt
│   │   └── components/
│   │       ├── ProfileContent.kt
│   │       ├── ProfileEditContent.kt
│   │       ├── ProfileAvatarSection.kt
│   │       ├── ProfileEditAvatarSection.kt
│   │       ├── AccountDetailCard.kt
│   │       ├── PreferencesCard.kt
│   │       ├── ProfileDetailRow.kt
│   │       ├── ProfilePrivacyCard.kt
│   │       ├── PrivacyRowItem.kt
│   │       ├── ProfileInputForm.kt
│   │       └── BottomCtaContainer.kt
│   └── navigation/
│       └── ProfileNavigation.kt
└── di/
    └── ProfileModules.kt
```

### Data Flow
```
[Mock] → ProfileDataSourceImpl → ProfileRepositoryImpl → ProfileViewModel → [UI]
          Either<Error,ProfileData>  Either<ProfileData>   UiState<ProfileData> (in ProfileUiModel)
```

### Key Classes
| Class | Purpose | Location |
|-------|---------|----------|
| ProfileDataSource | Load profile data interface | data/datasource/ |
| ProfileDataSourceImpl | Mock data implementation | data/datasource/ |
| ProfileRepository | Data coordination interface | data/repository/ |
| ProfileRepositoryImpl | Thin delegation; returns `Either<ProfileData>` | data/repository/ |
| ProfileViewModel | State management for both Profile and Edit Profile | presentation/ |
| ProfileScreen | Profile view screen (ViewModel wrapper) | presentation/ui/ |
| ProfileEditScreen | Edit profile form screen (ViewModel wrapper) | presentation/ui/ |

### Data Models
```kotlin
data class ProfileData(
    val name: String,
    val email: String,
    val memberTier: String,
    val biometricEnabled: Boolean,
)
```

## Integration Points

| Point | File | Status |
|-------|------|--------|
| Module include | settings.gradle.kts | ✅ |
| Dependency | composeApp/build.gradle.kts | ✅ |
| DI init | initKoin.kt | ✅ |
| Navigation | BaseAppNavHost.kt | ✅ |
| Bottom-bar tab (optional) | App.kt + navigation/TopLevelDestination.kt | N/A |

### Dashboard Integration
- `onProfileClick: () -> Unit` added to `DashboardNavigation.dashboard()`, `DashboardScreen`, `DashboardScreenRoot`, `DashboardContent`, `DashboardHeader`
- Profile button rendered as a circular `Box` (36dp, `CircleShape`, `MaterialTheme.colorScheme.primary`) in the `DashboardHeader` trailing slot
- Tapping navigates to `ProfileRoute`

## State Management

### UiModel Structure
```kotlin
data class ProfileUiModel(
    val profileState: UiState<ProfileData> = UiState.Uninitialized,
    val nameInput: String = "",
    val emailInput: String = "",
    val biometricEnabled: Boolean = false,
) {
    val name: String get() = (profileState as? UiState.Success)?.value?.name ?: ""
    val email: String get() = (profileState as? UiState.Success)?.value?.email ?: ""
    val memberTier: String get() = (profileState as? UiState.Success)?.value?.memberTier ?: ""
    val initials: String get() = name.split(" ").take(2)
        .mapNotNull { it.firstOrNull()?.uppercaseChar()?.toString() }.joinToString("")
}
```

### State Transitions
- `Uninitialized` → `Loading` on `init { loadProfile() }`
- `Loading` → `Success(ProfileData)` on successful repository call
- `Loading` → `Failed(ErrorModel)` on repository error
- `Failed` → `Loading` on `retry()`

## Navigation

- **Primary Route:** `ProfileRoute` — pushed from Dashboard via `onProfileClick`
- **Child Route:** `ProfileEditRoute` — registered inside `NavGraphBuilder.profile()`, pushed from Profile via `onEditClick`
- **Entry:** `navController.navigate(ProfileRoute)`
- **Callbacks:** `onBackClick: () -> Unit`, `onEditClick: () -> Unit` (Profile); `onBackClick: () -> Unit` (EditProfile)
- **Nav extension:** `fun NavGraphBuilder.profile(navController: NavController, onBackClick: () -> Unit)` — passes `navController` internally for `ProfileEditRoute` navigation

## Design
Blueprint: `.claude/docs/profile/designs/profile_blueprint.md`
