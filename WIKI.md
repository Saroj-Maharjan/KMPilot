# KMPilot - AI Tools Reference

Comprehensive reference for AI-powered development tools in KMPilot.

## Table of Contents

- [Overview](#overview)
- [Skills](#skills)
- [Agents](#agents)
- [Commands](#commands)
- [Workflows](#workflows)
- [Project Requirements](#project-requirements)
- [Troubleshooting](#troubleshooting)

---

## Overview

KMPilot uses Claude Code with specialized skills, agents, and commands to automate feature development following Clean Architecture and Specification-Driven Development (SDD).

**Key Features:**
- Zero-config context discovery (auto-detects package structure)
- Spec-first development (living documentation)
- Clean Architecture enforcement
- Parallel agent execution
- Comprehensive test generation

---

## Skills

Skills activate automatically based on context—no manual invocation needed.

### `creating-kmp-feature`

**Auto-activates when:** User mentions "create feature", "new module", "add feature"

**Complete Feature Generation Workflow:**

```
Phase 0: Context Discovery (AUTO)
├── Detect PKG_PREFIX from feature/*/build.gradle.kts
├── Find initKoin.kt path (contains startKoin)
├── Find BaseAppNavHost.kt path (contains NavHost)
└── Detect core module packages

Phase 1: PRD Generation
├── Analyze user prompt
├── Generate Product Requirements Document
├── Save to .claude/docs/{feature}/prd.txt
└── ⏸️ Wait for user approval

Phase 2: Task Generation
├── Break PRD into implementation tasks
├── Assign tasks to agents (data, ui, integration)
├── Save to .claude/docs/{feature}/tasks.md
└── ⏸️ Wait for user approval

Phase 3: Implementation (Parallel)
├── 🔧 data-layer-agent (runs in parallel)
│   ├── Create models with @Serializable
│   ├── Create Ktor Resources (type-safe routes)
│   ├── Implement RemoteDataSource (interface + impl)
│   ├── Implement Repository (interface + impl)
│   └── Validate build
│
├── 🎨 ui-layer-agent (runs in parallel)
│   ├── Create UiModel (presentation models)
│   ├── Implement ViewModel with 4-state pattern
│   ├── Create Composable Screens with X-components
│   ├── Setup Navigation with type-safe routes
│   └── Validate build
│
└── 🔗 integration-agent (runs after data + ui)
    ├── Create DI module with Koin
    ├── Add to settings.gradle.kts
    ├── Add dependency to composeApp
    ├── Register in initKoin.kt
    ├── Wire navigation in BaseAppNavHost.kt
    ├── Generate living specification
    └── Validate full build

Phase 4: Cleanup
├── Verify spec.md exists
├── Remove prd.txt (ephemeral)
├── Remove tasks.md (ephemeral)
└── Remove task-*.md files (ephemeral)
```

**Examples:**

**Simple Feature (UI-only, no API):**
```bash
> Create settings feature with toggle switches for notifications and dark mode
```
Generates: ViewModel, Screen, Navigation (no DataSource/Repository)

**Complex Feature (API + multiple screens):**
```bash
> Create product catalog with list, search, filters, and detail screens
```
Generates: Models, Ktor Resources, DataSource, Repository, UiModels, ViewModels, Screens, Navigation

**Feature with Authentication:**
```bash
> Create login feature with email/password and OAuth support
```
Generates: LoginRequest/Response models, AuthRepository, LoginViewModel, LoginScreen, token storage

**What Gets Generated:**

**Data Layer:**
```kotlin
// Models
@Serializable
data class Product(
    val id: String,
    val name: String,
    val price: Double,
    val imageUrl: String?
)

// Ktor Resources
@Resource("/products")
class ProductResource {
    @Resource("{id}")
    data class Id(val parent: ProductResource, val id: String)
}

// DataSource
interface ProductRemoteDataSource {
    suspend fun getProducts(): Either<List<Product>>
    suspend fun getProduct(id: String): Either<Product>
}

class ProductRemoteDataSourceImpl(
    private val client: ApiClient
) : ProductRemoteDataSource {
    override suspend fun getProducts(): Either<List<Product>> =
        client.get(ProductResource())
}

// Repository
interface ProductRepository {
    suspend fun getProducts(): Either<List<Product>>
    suspend fun getProduct(id: String): Either<Product>
}

class ProductRepositoryImpl(
    private val dataSource: ProductRemoteDataSource
) : ProductRepository {
    override suspend fun getProducts(): Either<List<Product>> =
        dataSource.getProducts()
}
```

**UI Layer:**
```kotlin
// UiModel
data class ProductUiModel(
    val id: String,
    val name: String,
    val price: String, // Formatted: "$99.99"
    val imageUrl: String?
)

// ViewModel
class ProductListViewModel(
    private val repository: ProductRepository
) : ViewModel() {
    private val _state = MutableStateFlow<ProductListUiState>(Uninitialized)
    val state: StateFlow<ProductListUiState> = _state.asStateFlow()

    fun loadProducts() {
        viewModelScope.launch {
            setState { Loading }
            when (val result = repository.getProducts()) {
                is Success -> setState {
                    Success(result.data.map { it.toUiModel() })
                }
                is Failure -> setState { Failed(result.error) }
            }
        }
    }
}

// Screen
@Composable
fun ProductListScreen(
    viewModel: ProductListViewModel = koinViewModel(),
    onProductClick: (String) -> Unit
) {
    val state by viewModel.state.collectAsState()

    XScaffold(
        title = { XText("Products") }
    ) {
        when (val currentState = state) {
            is Uninitialized -> LaunchedEffect(Unit) {
                viewModel.loadProducts()
            }
            is Loading -> XLoadingIndicator()
            is Success -> ProductList(
                products = currentState.products,
                onProductClick = onProductClick
            )
            is Failed -> XErrorView(error = currentState.error)
        }
    }
}

// Navigation
@Serializable
data class ProductListRoute(
    val onProductClick: (String) -> Unit
)
```

**Integration:**
```kotlin
// DI Module
class ProductModule : BaseFeature {
    override fun Module.install() {
        single<ProductRepository> { ProductRepositoryImpl(get()) }
        single<ProductRemoteDataSource> { ProductRemoteDataSourceImpl(get()) }
        viewModel { ProductListViewModel(get()) }
    }
}

// initKoin.kt (auto-registered)
modules(ProductModule().module)

// BaseAppNavHost.kt (auto-wired)
composable<ProductListRoute> {
    ProductListScreen(
        onProductClick = { id -> navController.navigate(ProductDetailRoute(id)) }
    )
}
```

---

### `modifying-kmp-feature`

**Auto-activates when:** User mentions "change feature", "modify feature", "update feature", "add to feature"

**Spec-First Modification Workflow:**

```
Phase 0: Context Discovery (AUTO)
└── Detect project structure

Phase 1: Load Specification
├── Check for .claude/docs/{feature}/spec/*.md
├── If missing: Generate spec using /generate-spec
└── Load spec into context

Phase 2: Understand Current Implementation
├── Parse spec for architecture patterns
├── Identify data models, API contracts
├── Understand state management
└── Map navigation structure

Phase 3: Plan Changes
├── Determine affected layers (data/ui/integration)
├── Load relevant architecture references
├── Plan implementation approach
└── Identify files to modify

Phase 4: Implement Changes
├── Apply changes following established patterns
├── Maintain consistency with existing code
└── Follow 10 critical rules

Phase 5: Validate
├── Run incremental build
├── Run ktlintFormat
└── Fix any errors

Phase 6: Update Specification
├── Regenerate spec from implementation
├── Add changelog entry at top
└── Preserve previous changelog entries
```

**Examples:**

**Add Functionality:**
```bash
> Add sorting by price and date to the product list feature
```
Changes: ProductListViewModel (add sort state), ProductListScreen (add sort UI)

**Refactor:**
```bash
> Refactor the login screen to use a stepper for multi-step authentication
```
Changes: LoginUiState (add step state), LoginScreen (add stepper UI)

**Fix Issues:**
```bash
> Fix the loading state not showing in the profile feature
```
Changes: ProfileViewModel (fix state transition), ProfileScreen (verify loading UI)

**Add New Screen:**
```bash
> Add a forgot password screen to the login feature
```
Changes: Add ForgotPasswordScreen, update navigation, add ViewModel

**Spec Changelog Entry:**
```markdown
## Last Updated

2025-01-05 - Added sorting by price and date
- ProductListUiState: Added sortBy field
- ProductListViewModel: Added setSortOrder function
- ProductListScreen: Added sort dropdown menu

2025-01-03 - Initial implementation
- Created product list with search and filters
```

---

### `using-design-system`

**Auto-activates when:** Working in `feature/*/ui/` directories, creating Composables, mentions "UI", "screen", "component"

**Design System Enforcement:**

Ensures X-components are used instead of Material3, preventing design drift.

**Component Mappings:**
```kotlin
// ❌ Material3          // ✅ X-Components
Scaffold           →     XScaffold
Button             →     XButton
OutlinedButton     →     XOutlinedButton
TextButton         →     XTextButton
Text               →     XText
TextField          →     XTextField
OutlinedTextField  →     XOutlinedTextField
Card               →     XCard
Icon               →     XIcon
IconButton         →     XIconButton
Switch             →     XSwitch
Checkbox           →     XCheckbox
RadioButton        →     XRadioButton
Divider            →     XDivider
CircularProgressIndicator → XLoadingIndicator
```

**Examples:**

**Creating a Login Form:**
```bash
> Create a login form with email, password, and submit button
```

AI generates:
```kotlin
@Composable
fun LoginForm(
    email: String,
    password: String,
    onEmailChange: (String) -> Unit,
    onPasswordChange: (String) -> Unit,
    onSubmit: () -> Unit
) {
    Column {
        XTextField(
            value = email,
            onValueChange = onEmailChange,
            label = { XText("Email") }
        )

        XTextField(
            value = password,
            onValueChange = onPasswordChange,
            label = { XText("Password") },
            visualTransformation = PasswordVisualTransformation()
        )

        XButton(
            onClick = onSubmit,
            text = "Login"
        )
    }
}
```

**Creating a Product Card:**
```bash
> Create a product card component with image, title, price, and add to cart button
```

AI generates using XCard, XText, XButton (not Material3 components)

**Theme Usage:**
```kotlin
// ✅ Correct
XTheme {
    XScaffold { }
}

// ❌ Wrong
MaterialTheme {
    Scaffold { }
}
```

---

### `bridging-swift-kotlin`

**Auto-activates when:** User mentions "Swift bridge", "iOS integration", "native SDK"

**Swift-to-Kotlin Integration Patterns:**

Uses interface injection to integrate iOS-specific SDKs while maintaining Clean Architecture.

**Pattern:**
```
1. Define Kotlin interface (commonMain)
2. Implement in Kotlin (androidMain) - usually stub
3. Implement in Swift (iosMain via expect/actual)
4. Inject via Koin DI
```

**Examples:**

**Biometric Authentication:**
```bash
> Integrate iOS Face ID authentication into the login feature
```

Generates:

```kotlin
// commonMain - Interface
interface BiometricAuthenticator {
    suspend fun authenticate(reason: String): Either<Boolean>
}

// androidMain - Android implementation
actual class BiometricAuthenticatorImpl : BiometricAuthenticator {
    override suspend fun authenticate(reason: String): Either<Boolean> {
        // Android BiometricPrompt implementation
    }
}

// iosMain - expect/actual bridge
expect class BiometricAuthenticatorImpl() : BiometricAuthenticator

// iosMain Swift bridge
@Composable
actual fun rememberBiometricAuthenticator(): BiometricAuthenticator {
    return remember { BiometricAuthenticatorSwiftBridge() }
}
```

```swift
// Swift implementation
class BiometricAuthenticatorSwiftBridge: BiometricAuthenticator {
    func authenticate(reason: String) async -> Either<Bool> {
        let context = LAContext()
        // Face ID implementation
    }
}
```

**Camera Integration:**
```bash
> Add iOS camera capture to the profile photo feature
```

**Payment Processing:**
```bash
> Integrate Apple Pay into the checkout feature
```

**Push Notifications:**
```bash
> Add iOS push notification handling to the messaging feature
```

---

## Agents

Specialized agents invoked by skills or manually.

### Feature Development Agents

#### `data-layer-agent`

Implements the data layer for KMP features.

**Invoked by:** `creating-kmp-feature` skill (Phase 3)

**Generates:**
- Domain models with `@Serializable`
- Ktor Resources (type-safe API routes)
- RemoteDataSource (interface + implementation)
- Repository (interface + implementation)
- Either<T> error handling

**Example Input:**
```
Feature: Product Catalog
Models: Product (id, name, price, imageUrl)
Endpoints: GET /products, GET /products/{id}
```

**Example Output:**
```kotlin
// feature/productcatalog/data/model/Product.kt
@Serializable
data class Product(
    val id: String,
    val name: String,
    val price: Double,
    val imageUrl: String?
)

// feature/productcatalog/data/remote/ProductResource.kt
@Resource("/products")
class ProductResource {
    @Resource("{id}")
    data class Id(val parent: ProductResource, val id: String)
}

// feature/productcatalog/data/remote/ProductRemoteDataSource.kt
interface ProductRemoteDataSource {
    suspend fun getProducts(): Either<List<Product>>
    suspend fun getProduct(id: String): Either<Product>
}

class ProductRemoteDataSourceImpl(
    private val client: ApiClient
) : ProductRemoteDataSource {
    override suspend fun getProducts(): Either<List<Product>> =
        client.get(ProductResource())

    override suspend fun getProduct(id: String): Either<Product> =
        client.get(ProductResource.Id(ProductResource(), id))
}

// feature/productcatalog/data/repository/ProductRepository.kt
interface ProductRepository {
    suspend fun getProducts(): Either<List<Product>>
    suspend fun getProduct(id: String): Either<Product>
}

class ProductRepositoryImpl(
    private val dataSource: ProductRemoteDataSource
) : ProductRepository {
    override suspend fun getProducts(): Either<List<Product>> =
        dataSource.getProducts()

    override suspend fun getProduct(id: String): Either<Product> =
        dataSource.getProduct(id)
}
```

**Validates:**
- Build passes: `./gradlew :feature:productcatalog:assembleAndroidMain`
- Ktlint formatting applied

---

#### `ui-layer-agent`

Implements the UI layer for KMP features.

**Invoked by:** `creating-kmp-feature` skill (Phase 3)

**Generates:**
- UiModel (presentation models)
- ViewModel with 4-state pattern
- Composable Screens with X-components
- Navigation with type-safe routes
- setState { } for state updates

**Example Input:**
```
Feature: Product Catalog
Screens: ProductListScreen, ProductDetailScreen
State: Loading, Success (with products), Failed
Actions: loadProducts, searchProducts, filterByCategory
```

**Example Output:**
```kotlin
// feature/productcatalog/presentation/model/ProductUiModel.kt
data class ProductUiModel(
    val id: String,
    val name: String,
    val priceFormatted: String, // "$99.99"
    val imageUrl: String?
)

fun Product.toUiModel() = ProductUiModel(
    id = id,
    name = name,
    priceFormatted = "$${"%.2f".format(price)}",
    imageUrl = imageUrl
)

// feature/productcatalog/presentation/viewmodel/ProductListViewModel.kt
sealed interface ProductListUiState : UiState {
    data object Uninitialized : ProductListUiState
    data object Loading : ProductListUiState
    data class Success(
        val products: ImmutableList<ProductUiModel>,
        val searchQuery: String = ""
    ) : ProductListUiState
    data class Failed(val error: ErrorResponse) : ProductListUiState
}

class ProductListViewModel(
    private val repository: ProductRepository
) : ViewModel() {
    private val _state = MutableStateFlow<ProductListUiState>(Uninitialized)
    val state: StateFlow<ProductListUiState> = _state.asStateFlow()

    fun loadProducts() {
        viewModelScope.launch {
            setState { Loading }
            when (val result = repository.getProducts()) {
                is Success -> setState {
                    Success(result.data.map { it.toUiModel() }.toImmutableList())
                }
                is Failure -> setState { Failed(result.error) }
            }
        }
    }

    fun searchProducts(query: String) {
        val currentState = state.value
        if (currentState is Success) {
            setState { currentState.copy(searchQuery = query) }
        }
    }
}

// feature/productcatalog/presentation/ui/ProductListScreen.kt
@Composable
fun ProductListScreen(
    viewModel: ProductListViewModel = koinViewModel(),
    onProductClick: (String) -> Unit,
    onBackClick: () -> Unit
) {
    val state by viewModel.state.collectAsState()

    XScaffold(
        title = { XText("Products") },
        onBackClick = onBackClick
    ) { padding ->
        when (val currentState = state) {
            is Uninitialized -> {
                LaunchedEffect(Unit) { viewModel.loadProducts() }
            }
            is Loading -> {
                Box(Modifier.fillMaxSize(), contentAlignment = Center) {
                    XLoadingIndicator()
                }
            }
            is Success -> {
                ProductListContent(
                    products = currentState.products,
                    searchQuery = currentState.searchQuery,
                    onSearchChange = viewModel::searchProducts,
                    onProductClick = onProductClick,
                    modifier = Modifier.padding(padding)
                )
            }
            is Failed -> {
                XErrorView(
                    error = currentState.error,
                    onRetry = viewModel::loadProducts
                )
            }
        }
    }
}

// feature/productcatalog/presentation/navigation/ProductCatalogNavigation.kt
@Serializable
data class ProductListRoute(
    val onProductClick: (String) -> Unit,
    val onBackClick: () -> Unit
)

@Serializable
data class ProductDetailRoute(
    val productId: String,
    val onBackClick: () -> Unit
)
```

**Validates:**
- Build passes
- All 4 UI states handled
- X-components used (no Material3)
- setState { } used (no direct assignment)

---

#### `integration-agent`

Completes the 4 integration points and generates living specification.

**Invoked by:** `creating-kmp-feature` skill (Phase 3, after data + ui)

**Completes 4 Integration Points:**
1. **settings.gradle.kts** - Include module
2. **composeApp/build.gradle.kts** - Add dependency
3. **initKoin.kt** - Register DI module
4. **BaseAppNavHost.kt** - Wire navigation

**Example:**

**1. settings.gradle.kts:**
```kotlin
include(":feature:productcatalog")
```

**2. composeApp/build.gradle.kts:**
```kotlin
dependencies {
    implementation(projects.feature.productcatalog)
}
```

**3. initKoin.kt:**
```kotlin
fun initKoin() {
    startKoin {
        modules(
            // ... other modules
            ProductCatalogModule().module
        )
    }
}
```

**4. BaseAppNavHost.kt:**
```kotlin
@Composable
fun BaseAppNavHost(navController: NavHostController) {
    XNavHost(navController, startDestination = HomeRoute) {
        // ... other routes

        composable<ProductListRoute> {
            ProductListScreen(
                onProductClick = { id ->
                    navController.navigate(ProductDetailRoute(id))
                },
                onBackClick = { navController.popBackStack() }
            )
        }

        composable<ProductDetailRoute> { backStackEntry ->
            val route = backStackEntry.toRoute<ProductDetailRoute>()
            ProductDetailScreen(
                productId = route.productId,
                onBackClick = { navController.popBackStack() }
            )
        }
    }
}
```

**Generates Living Specification:**

`.claude/docs/productcatalog/spec/productcatalog.md`

**Validates:**
- Full build passes: `./gradlew assembleDebug`
- All integration points complete
- Spec generated successfully

---

### Testing Agents

#### `test-orchestrator`

Coordinates test generation across all layers.

**Manual Invocation:**
```bash
> Use test-orchestrator agent to generate complete test suite for the login feature
```

**What it does:**
1. Analyzes feature implementation
2. Extracts models, DataSource, Repository, ViewModel, Screens
3. Spawns 6 specialized test agents in parallel
4. Ensures test consistency

**Spawns:**
- `test-fixtures` - Generate model fixtures
- `test-datasource` - Generate DataSource tests
- `test-repository` - Generate Repository tests
- `test-viewmodel` - Generate ViewModel tests
- `test-ui` - Generate UI tests
- `test-integration` - Generate E2E tests

---

#### `test-fixtures`

Generates test fixtures for domain models.

**Example Output:**
```kotlin
// feature/login/src/commonTest/kotlin/fixtures/LoginFixtures.kt
object LoginFixtures {
    fun loginRequest(
        email: String = "test@example.com",
        password: String = "password123"
    ) = LoginRequest(email, password)

    fun loginResponse(
        token: String = "sample-token-123",
        user: User = UserFixtures.user()
    ) = LoginResponse(token, user)

    fun user(
        id: String = "user-123",
        name: String = "Test User",
        email: String = "test@example.com"
    ) = User(id, name, email)
}
```

---

#### `test-datasource`

Generates DataSource tests using MockEngine.

**Example Output:**
```kotlin
// feature/login/src/commonTest/kotlin/data/remote/LoginRemoteDataSourceTest.kt
class LoginRemoteDataSourceTest {
    private lateinit var mockEngine: MockEngine
    private lateinit var dataSource: LoginRemoteDataSource

    @BeforeTest
    fun setup() {
        mockEngine = MockEngine { request ->
            when (request.url.encodedPath) {
                "/auth/login" -> respond(
                    content = Json.encodeToString(LoginFixtures.loginResponse()),
                    status = HttpStatusCode.OK,
                    headers = headersOf(HttpHeaders.ContentType, "application/json")
                )
                else -> respond("", HttpStatusCode.NotFound)
            }
        }

        val client = ApiClient(mockEngine)
        dataSource = LoginRemoteDataSourceImpl(client)
    }

    @Test
    fun `login with valid credentials returns success`() = runTest {
        val request = LoginFixtures.loginRequest()
        val result = dataSource.login(request)

        assertTrue(result is Success)
        assertEquals("sample-token-123", (result as Success).data.token)
    }

    @Test
    fun `login with invalid credentials returns failure`() = runTest {
        // Configure mock to return error
        // Test failure case
    }
}
```

---

#### `test-repository`

Generates Repository tests using Mokkery.

**Example Output:**
```kotlin
// feature/login/src/commonTest/kotlin/data/repository/LoginRepositoryTest.kt
class LoginRepositoryTest {
    private val mockDataSource = mock<LoginRemoteDataSource>()
    private val repository = LoginRepositoryImpl(mockDataSource)

    @Test
    fun `login success delegates to datasource`() = runTest {
        val request = LoginFixtures.loginRequest()
        val response = LoginFixtures.loginResponse()

        every { mockDataSource.login(request) } returns Success(response)

        val result = repository.login(request)

        assertTrue(result is Success)
        assertEquals(response, (result as Success).data)
        verify { mockDataSource.login(request) }
    }

    @Test
    fun `login failure propagates error`() = runTest {
        val request = LoginFixtures.loginRequest()
        val error = ErrorResponse("Invalid credentials")

        every { mockDataSource.login(request) } returns Failure(error)

        val result = repository.login(request)

        assertTrue(result is Failure)
        assertEquals(error, (result as Failure).error)
    }
}
```

---

#### `test-viewmodel`

Generates ViewModel tests using Turbine for Flow testing.

**Example Output:**
```kotlin
// feature/login/src/commonTest/kotlin/presentation/viewmodel/LoginViewModelTest.kt
class LoginViewModelTest {
    private val mockRepository = mock<LoginRepository>()
    private lateinit var viewModel: LoginViewModel

    @BeforeTest
    fun setup() {
        viewModel = LoginViewModel(mockRepository)
    }

    @Test
    fun `initial state is Uninitialized`() = runTest {
        viewModel.state.test {
            assertEquals(Uninitialized, awaitItem())
        }
    }

    @Test
    fun `login success transitions to Success state`() = runTest {
        val response = LoginFixtures.loginResponse()
        every { mockRepository.login(any()) } returns Success(response)

        viewModel.state.test {
            assertEquals(Uninitialized, awaitItem())

            viewModel.login("test@example.com", "password")
            assertEquals(Loading, awaitItem())

            val successState = awaitItem()
            assertTrue(successState is Success)
            assertEquals(response.token, (successState as Success).token)
        }
    }

    @Test
    fun `login failure transitions to Failed state`() = runTest {
        val error = ErrorResponse("Invalid credentials")
        every { mockRepository.login(any()) } returns Failure(error)

        viewModel.state.test {
            assertEquals(Uninitialized, awaitItem())

            viewModel.login("test@example.com", "wrong")
            assertEquals(Loading, awaitItem())

            val failedState = awaitItem()
            assertTrue(failedState is Failed)
            assertEquals(error, (failedState as Failed).error)
        }
    }
}
```

---

#### `test-ui`

Generates Compose UI tests.

**Example Output:**
```kotlin
// feature/login/src/androidTest/kotlin/presentation/ui/LoginScreenTest.kt
@RunWith(AndroidJUnit4::class)
class LoginScreenTest {
    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun loginScreen_initialState_showsForm() {
        composeTestRule.setContent {
            XTheme {
                LoginScreen(
                    onLoginSuccess = {},
                    onBackClick = {}
                )
            }
        }

        composeTestRule.onNodeWithText("Email").assertIsDisplayed()
        composeTestRule.onNodeWithText("Password").assertIsDisplayed()
        composeTestRule.onNodeWithText("Login").assertIsDisplayed()
    }

    @Test
    fun loginScreen_enterCredentials_enablesButton() {
        composeTestRule.setContent {
            XTheme {
                LoginScreen(
                    onLoginSuccess = {},
                    onBackClick = {}
                )
            }
        }

        composeTestRule.onNodeWithText("Email").performTextInput("test@example.com")
        composeTestRule.onNodeWithText("Password").performTextInput("password123")

        composeTestRule.onNodeWithText("Login").assertIsEnabled()
    }

    @Test
    fun loginScreen_loadingState_showsIndicator() {
        // Test loading state
    }
}
```

---

#### `test-integration`

Generates E2E integration tests (MockEngine → ViewModel).

**Example Output:**
```kotlin
// feature/login/src/commonTest/kotlin/integration/LoginIntegrationTest.kt
class LoginIntegrationTest {
    private lateinit var viewModel: LoginViewModel

    @BeforeTest
    fun setup() {
        val mockEngine = MockEngine { request ->
            when (request.url.encodedPath) {
                "/auth/login" -> {
                    val body = request.body.toString()
                    if (body.contains("test@example.com")) {
                        respond(
                            content = Json.encodeToString(LoginFixtures.loginResponse()),
                            status = HttpStatusCode.OK
                        )
                    } else {
                        respond(
                            content = Json.encodeToString(ErrorResponse("Invalid")),
                            status = HttpStatusCode.Unauthorized
                        )
                    }
                }
                else -> respond("", HttpStatusCode.NotFound)
            }
        }

        val apiClient = ApiClient(mockEngine)
        val dataSource = LoginRemoteDataSourceImpl(apiClient)
        val repository = LoginRepositoryImpl(dataSource)
        viewModel = LoginViewModel(repository)
    }

    @Test
    fun `end to end login flow with valid credentials`() = runTest {
        viewModel.state.test {
            assertEquals(Uninitialized, awaitItem())

            viewModel.login("test@example.com", "password")
            assertEquals(Loading, awaitItem())

            val successState = awaitItem()
            assertTrue(successState is Success)
        }
    }

    @Test
    fun `end to end login flow with invalid credentials`() = runTest {
        viewModel.state.test {
            assertEquals(Uninitialized, awaitItem())

            viewModel.login("wrong@example.com", "wrong")
            assertEquals(Loading, awaitItem())

            val failedState = awaitItem()
            assertTrue(failedState is Failed)
        }
    }
}
```

---

### Code Quality Agents

#### `code-reviewer`

Reviews feature implementations against architecture rules.

**Manual Invocation:**
```bash
> Use code-reviewer agent to review the login feature
```

**What it validates:**

**10 Critical Rules:**
1. Interface + Impl pairs for DataSource/Repository
2. Either<T> for all fallible operations
3. setState { } for state updates (never direct assignment)
4. 4-state UI: Uninitialized/Loading/Success/Failed
5. X-components only (no Material3)
6. ImmutableList for collections
7. Callback parameters for navigation
8. Lowercase packages (no hyphens/underscores)
9. @Serializable on all models
10. Type-safe navigation routes

**4 Integration Points:**
1. settings.gradle.kts includes module
2. composeApp/build.gradle.kts has dependency
3. initKoin.kt registers DI module
4. BaseAppNavHost.kt wires navigation

**Example Output:**
```
Reviewing feature: login

✓ Data Layer
  ✓ LoginRemoteDataSource: Interface + Impl found
  ✓ LoginRepository: Interface + Impl found
  ✓ Either<T> used for all suspend functions
  ✓ Models have @Serializable

✓ UI Layer
  ✓ LoginUiState: All 4 states present
  ✓ LoginViewModel: Uses setState { }
  ✓ LoginScreen: Uses X-components
  ✓ Navigation: Type-safe routes with callbacks

✓ Integration
  ✓ settings.gradle.kts: Module included
  ✓ composeApp/build.gradle.kts: Dependency added
  ✓ initKoin.kt: DI module registered
  ✓ BaseAppNavHost.kt: Navigation wired

⚠ Issues Found:
  ⚠ LoginScreen.kt:45 - Using Material3 Button instead of XButton
  ⚠ LoginViewModel.kt:32 - Direct state assignment instead of setState { }

✗ Review Failed: 2 issues found
```

---

## Commands

Slash commands for manual operations.

### `/generate-spec <featurename>`

Generates living documentation from an existing feature implementation.

**Usage:**
```bash
/generate-spec login
```

**What it does:**
1. Analyzes feature implementation in `feature/login/`
2. Extracts models, API contracts, state management, navigation
3. Generates comprehensive specification
4. Saves to `.claude/docs/login/spec/login.md`

**Output Includes:**
- Feature overview and requirements
- Architecture decisions
- Data models with field descriptions
- API endpoints and contracts
- State management patterns
- Navigation routes and callbacks
- Implementation details

**Example Output:**
```markdown
# Login Feature Specification

## Overview
User authentication feature with email/password login.

## Data Models
- LoginRequest: email (String), password (String)
- LoginResponse: token (String), user (User)

## API Endpoints
- POST /auth/login → Either<LoginResponse>

## State Management
- LoginUiState: Uninitialized | Loading | Success | Failed

## Navigation
- LoginRoute → onLoginSuccess: () -> Unit
```

**When to use:**
- After manually creating a feature (to generate docs)
- When specs are missing for existing features
- To document legacy features

---

### `/coverage`

Generates test coverage reports for all feature modules.

**Usage:**
```bash
/coverage
```

**What it does:**
1. Runs Kover coverage tool on all feature modules
2. Generates HTML reports with line-by-line analysis
3. Opens reports in browser
4. Shows coverage percentages by module

**Output:**
- HTML coverage reports in `build/reports/kover/`
- Coverage summary in terminal
- Line, branch, and instruction coverage metrics

**Example Output:**
```
Running coverage for all features...
✓ feature:login - 87% coverage
✓ feature:profile - 92% coverage
✓ feature:settings - 78% coverage

Overall coverage: 85%
Report: build/reports/kover/html/index.html
```

**When to use:**
- Before merging features
- To identify untested code paths
- During code review
- To meet coverage requirements

---

## Workflows

### Complete Feature Creation

**Input:**
```bash
> Create product catalog feature with list, search, and detail screens
```

**Output:**
```
Phase 0: Context Discovery
✓ PKG_PREFIX: com.example
✓ INIT_KOIN_PATH: composeApp/src/commonMain/kotlin/com/example/initKoin.kt
✓ NAV_HOST_PATH: composeApp/src/commonMain/kotlin/com/example/BaseAppNavHost.kt

Phase 1: PRD Generation
✓ Generated: .claude/docs/productcatalog/prd.txt
→ User approved

Phase 2: Task Generation
✓ Generated: .claude/docs/productcatalog/tasks.md
→ User approved

Phase 3: Implementation
✓ data-layer-agent complete (12 files created)
✓ ui-layer-agent complete (8 files created)
✓ integration-agent complete (spec + 4 integration points)

Phase 4: Cleanup
✓ Removed prd.txt, tasks.md, task-*.md
✓ Kept spec: .claude/docs/productcatalog/spec/productcatalog.md

✅ Feature Complete!
   Navigate: navController.navigate(ProductListRoute)
```

---

### Feature Modification

**Input:**
```bash
> Add pagination to the product list feature
```

**Output:**
```
Phase 0: Context Discovery
✓ Detected project structure

Phase 1: Load Specification
✓ Loaded: .claude/docs/productcatalog/spec/productcatalog.md

Phase 2: Plan Changes
✓ Affected layers: data, ui
✓ Changes: Add pagination to API, update ViewModel, update UI

Phase 3: Implement
✓ Modified ProductRepository (add page parameter)
✓ Modified ProductListViewModel (add loadMore function)
✓ Modified ProductListScreen (add LazyColumn pagination)

Phase 4: Validate
✓ Build passed
✓ Ktlint formatted

Phase 5: Update Spec
✓ Regenerated spec with changelog
✓ Added: "2025-01-05 - Added pagination to product list"

✅ Modification Complete!
```

---

## Project Requirements

### Directory Structure

```
YourProject/
├── composeApp/
│   └── src/commonMain/kotlin/{pkg}/
│       ├── initKoin.kt        # Must contain startKoin
│       └── BaseAppNavHost.kt  # Must contain NavHost
├── core/
│   ├── common/          # Either, UiState, BaseFeature, setState
│   ├── data/            # ApiClient, network layer
│   └── designsystem/    # X-components (XTheme, XButton, etc.)
└── feature/
    └── {featurename}/   # Feature modules (lowercase)
```

### Required Patterns

- **Either<T>** for error handling
- **UiState** with 4 states (Uninitialized/Loading/Success/Failed)
- **BaseFeature** for DI auto-registration
- **setState { }** for state updates
- **X-components** for UI (no Material3)
- **Koin** for dependency injection
- **Type-safe navigation** with callbacks

---

## Troubleshooting

### Build Errors After Feature Generation

**Check the 4 integration points:**

1. **settings.gradle.kts**
```kotlin
include(":feature:yourfeature")
```

2. **composeApp/build.gradle.kts**
```kotlin
implementation(projects.feature.yourfeature)
```

3. **initKoin.kt**
```kotlin
modules(YourFeatureModule().module)
```

4. **BaseAppNavHost.kt**
```kotlin
composable<YourFeatureRoute> {
    YourFeatureScreen()
}
```

### Package Prefix Not Detected

Create at least one feature with namespace:
```kotlin
android {
    namespace = "com.yourpackage.featurename"
}
```

### Spec Not Found for Modification

Generate it first:
```bash
/generate-spec yourfeature
```

---

**For more details, see:**
- `.claude/skills/` - Skill implementation details
- `.claude/agents/` - Agent specifications
- `CLAUDE.md` - Architecture rules and conventions
