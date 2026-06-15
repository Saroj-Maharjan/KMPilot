# Local Persistence & App-State

How **persisted local state** is structured in KMP, following Clean Architecture. This is the
counterpart to [data.md](data.md): that doc covers per-feature **remote** data (Ktor/`ApiClient`,
`Either<T>`); this one covers **on-device persistence** — theme, locale, auth token, flags, cached
structured data.

**Note**: Uses `{PKG_PREFIX}` placeholder for package prefix (resolved via Context Discovery).

## Where it lives — `:core:data`, not the feature

Remote data layers are **per-feature** (`feature/{name}/.../data/`). Persisted **app-state** is
**core infrastructure**: it lives once in `:core:data` and is shared app-wide. Theme, locale, and
token are app-global, not owned by any single feature — so they are written here by hand, not
generated per feature.

A feature that genuinely owns its *own* persisted state follows the same pattern inside its own
module, but that is rare; default to remote-only feature data layers.

## Two co-equal backends — pick by data shape

There are **two** local backends. Neither is a fallback for the other — choose by the **shape** of
the data:

| Backend | Use for | Class |
|---------|---------|-------|
| **DataStore (key-value)** | Simple scalar/flag state: theme mode, locale tag, auth token, booleans | `PreferencesManager` (`data/local/pref/`) |
| **Room (relational)** | Structured / queryable / multi-row data: cached lists, entities, joins | `AppDatabase` (`data/local/db/`) |

DataStore is the common case and the only one wired today; `AppDatabase` is a **placeholder** for
when a concern needs a real database. Do not force relational data into KV, or scalar flags into a
DB.

## Package layout (`:core:data`)

```
{PKG_PREFIX}.data/
├── local/
│   ├── pref/PreferencesManager.kt   # KV backend — generic, infra (see "Rule-1 exception")
│   └── db/AppDatabase.kt            # Room backend — placeholder for structured data
├── datasource/local/{domain}/       # {X}LocalDataSource  (interface + impl, internal)
│   └── theme/  locale/  token/
├── repository/{domain}/             # {X}Repository       (impl internal; interface per visibility rule)
│   └── theme/  locale/  token/
└── di/LocalDataSourceModule.kt      # all local DI: backends + every datasource + repository
```

**Packaging is layer-first** (`datasource/local/{domain}/`, `repository/{domain}/`) — mirrors the
remote layout. **Domain-first exception**: a concern that carries domain *helpers* fitting no single
layer may instead group everything under one `data/{domain}/` root (utils + datasource + repository
together). Use this only when the helpers force it; layer-first is the default.

## The two pairs per concern

Each persisted concern is **one `LocalDataSource` pair + one `Repository` pair**, both delegating
down to a backend.

### 1. LocalDataSource (`datasource/local/{domain}/`)

**Purpose**: Read/write one concern's value from a backend. The local twin of `RemoteDataSource`.

**Pattern**:
- **Interface + Impl**, both `internal` (Rule 1 still holds for datasources).
- Impl injects a backend (`PreferencesManager` or `AppDatabase`), maps to/from storage keys/rows.
- Naming: `{Entity}LocalDataSource` / `{Entity}LocalDataSourceImpl`.

**No `Either` here.** A KV/DB read is non-fallible from the app's view — there is no network error
surface to model. So local datasources return the **plain value or `null`** (or a `Flow`), *not*
`Either<T>`. This is the deliberate contrast with remote (data.md), where `Either<T>` wraps network
failure. (Rule 2's `Either` is for fallible network/IO ops.)

```kotlin
// datasource/local/theme/ThemeLocalDataSource.kt
internal interface ThemeLocalDataSource {
    suspend fun getThemeMode(): AppThemeMode?      // null ⇒ nothing stored yet
    suspend fun setThemeMode(mode: AppThemeMode)
}

// datasource/local/theme/ThemeLocalDataSourceImpl.kt
internal class ThemeLocalDataSourceImpl(
    private val preferencesManager: PreferencesManager,
) : ThemeLocalDataSource {
    override suspend fun getThemeMode(): AppThemeMode? =
        preferencesManager.getString(KEY).first()?.let { name ->
            AppThemeMode.entries.firstOrNull { it.name == name }
        }
    override suspend fun setThemeMode(mode: AppThemeMode) =
        preferencesManager.putString(KEY, mode.name)

    private companion object { const val KEY = "theme_mode" }
}
```

### 2. Repository (`repository/{domain}/`)

**Purpose**: The public API for the concern. The local twin of the remote `Repository`.

**App-state shape** (theme, locale): seed from the datasource on init, expose an in-memory
`StateFlow` as the **synchronous read source** for callers, and **write through** on every set
(update the flow, persist async). Callers (app root, settings screen) never touch the datasource.

```kotlin
// repository/theme/ThemeRepository.kt  — interface
interface ThemeRepository {
    val themeMode: StateFlow<AppThemeMode>
    fun setThemeMode(mode: AppThemeMode)
}

// repository/theme/ThemeRepositoryImpl.kt  — impl (internal)
internal class ThemeRepositoryImpl(
    private val localDataSource: ThemeLocalDataSource,
) : ThemeRepository {
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Default)
    private val _themeMode = MutableStateFlow(AppThemeMode.SYSTEM)
    override val themeMode: StateFlow<AppThemeMode> = _themeMode.asStateFlow()

    init { scope.launch { localDataSource.getThemeMode()?.let { _themeMode.value = it } } }

    override fun setThemeMode(mode: AppThemeMode) {
        _themeMode.value = mode                              // synchronous read updates immediately
        scope.launch { localDataSource.setThemeMode(mode) }  // persist write-through
    }
}
```

The concern's model/enum (e.g. `AppThemeMode`) lives in the **repository** package — the datasource
imports it upward. Local repos never import from `presentation` (Rule 11 still holds).

## Naming — `*Repository`, never `*Manager`

Persisted-state types are **`{Entity}Repository`**, not `{Entity}Manager`. "Manager" is reserved for
the one KV-backend infra class, `PreferencesManager`. A `ThemeManager`/`LocaleManager` is a naming
violation — use `ThemeRepository`/`LanguageRepository`.

## Visibility

| Type | Visibility |
|------|-----------|
| `{X}LocalDataSource` interface + impl | **`internal`** — always |
| `{X}RepositoryImpl` | **`internal`** — always |
| `{X}Repository` **interface** | **public** if a feature/app consumes it (theme, locale) **OR** kept as deliberate API headroom (token); otherwise `internal` |

`TokenRepository` is the documented **headroom** case: public even though only `:core:data` uses it
today, because the auth feature will consume it. Don't make a repo public "just in case" beyond a
named, intended consumer.

## `PreferencesManager` — Rule-1 exception (infra, not a DataSource)

`PreferencesManager` is a **plain class with no interface** — a deliberate exception to Rule 1
(interface + impl). It is not a per-concern DataSource; it is the **single shared KV backend** that
every `*LocalDataSource` delegates to. Giving it an interface would add a seam with no second
implementation. Do not "fix" it by adding an interface.

```kotlin
// data/local/pref/PreferencesManager.kt
class PreferencesManager(private val dataStorePreference: DataStore<Preferences>) {
    suspend fun putString(key: String, value: String) { /* edit */ }
    fun getString(key: String): Flow<String?> = /* data.map { it[key] } */
    suspend fun clear() { /* edit { clear() } */ }
}
```

## DI — one module, `localDataSourceModule`

All local persistence DI lives in `di/LocalDataSourceModule.kt`: the backend(s), then every
datasource, then every repository.

```kotlin
internal val localDataSourceModule = module {
    single { PreferencesManager(get()) }
    single<DataStore<Preferences>> { /* PreferenceDataStoreFactory.createWithPath(...) */ }

    // Local data sources (internal)
    singleOf(::ThemeLocalDataSourceImpl).bind<ThemeLocalDataSource>()
    singleOf(::LanguageLocalDataSourceImpl).bind<LanguageLocalDataSource>()
    singleOf(::TokenLocalDataSourceImpl).bind<TokenLocalDataSource>()

    // Repositories
    singleOf(::ThemeRepositoryImpl).bind<ThemeRepository>()
    singleOf(::LanguageRepositoryImpl).bind<LanguageRepository>()
    singleOf(::TokenRepositoryImpl).bind<TokenRepository>()
}
```

The **DataStore file path is platform-specific** → provided via an `internal expect val
platformLocalDataSourceModule` with one `actual` per target (android/ios/desktop), composed into the
data module's aggregate. (Same `internal expect/actual val platformModule` mechanism as Rule 14.)

## Module roles (where app-global state is consumed)

Persisted state in `:core:data` is read at the edges by other modules — see
[patterns.md → Module Dependencies](../../_shared/patterns.md) for the full role map. In short:

- **`:core:common`** — primitives only (Either, UiState, UiText, ext, util). **No** state holders or
  platform stores.
- **`:core:designsystem`** — app-global UI runtime context: `XTheme(darkTheme: Boolean)` and the
  `LocalAppLocale` CompositionLocal (+ its android/ios/desktop actuals).
- **`composeApp`** — app-shell glue: collects `ThemeRepository.themeMode` → maps to `darkTheme`;
  `ProvideAppLocale` feeds `LanguageRepository`'s tag into `LocalAppLocale`.
- **`:core:data`** — owns all persisted state (the repositories above).

## Canonical examples

`theme`, `locale`, and `token` are the three reference concerns — app-global, hand-authored core
infra. (App-specific persisted concerns follow the identical pattern but are not part of the
template baseline.)
