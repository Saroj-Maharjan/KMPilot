---
name: bridging-swift-kotlin
description: Bridge Swift to Kotlin Multiplatform via Interface Injection. Use when integrating iOS SDKs, calling Swift from Kotlin, accessing iOS-only APIs, implementing biometrics/payments/camera, or connecting native frameworks to KMP.
---

# Bridging Swift-Kotlin

Kotlin interface in `iosMain` → Swift implements it in `iosApp` → Inject via Koin DI.

## Context Discovery (First Step)

Before implementing a bridge, detect project configuration:

1. **Package Prefix**: Glob `feature/*/build.gradle.kts`, grep `namespace = "..."`, extract prefix (e.g., `com.example.login` → `com.example`)
2. **Store as**: `{PKG_PREFIX}` for use in package paths below

## Critical: Swift Inheritance

Swift class inherits from `<ModulePrefix><InterfaceName>`:

| Module | Kotlin | Swift Inherits |
|--------|--------|----------------|
| `core:data` | `RecaptchaBridge` | `DataRecaptchaBridge` |
| `feature:auth` | `AuthBridge` | `AuthAuthBridge` |

Use Xcode autocomplete (`Ctrl+Space`) to find exact protocol name.

## Steps

### 1. Bridge Interface

```kotlin
// <module>/src/iosMain/kotlin/{PKG_PREFIX}/<featurename>/<Feature>Bridge.kt
interface <Feature>Bridge {
    suspend fun execute(param: String): String
}
```

### 2. Provider

```kotlin
// <module>/src/iosMain/kotlin/{PKG_PREFIX}/<featurename>/IOS<Feature>Provider.kt
class IOS<Feature>Provider(private val bridge: <Feature>Bridge) : <Feature>Provider {
    override suspend fun execute(): Either<String> =
        try { Either.Success(bridge.execute("param")) }
        catch (e: Exception) { Either.Failure(ErrorModel.Exception(e)) }
}
```

### 3. Swift Implementation

```swift
// iosApp/iosApp/<Feature>/<Feature>BridgeImpl.swift
import ComposeApp

class <Feature>BridgeImpl: <ModulePrefix><Feature>Bridge {
    func execute(param: String, completionHandler: @escaping (String?, Error?) -> Void) {
        Task {
            do { completionHandler(try await nativeOperation(param), nil) }
            catch { completionHandler(nil, error) }
        }
    }
}
```

### 4. DI Connection

```kotlin
// composeApp/src/iosMain/kotlin/{PKG_PREFIX}/<appname>/MainViewController.kt
fun MainViewController(bridge: <Feature>Bridge) = ComposeUIViewController(
    configure = { initKoin { modules(module { single { bridge } }) } }
) { App() }
```

```swift
// iosApp/iosApp/ContentView.swift
struct ComposeView: UIViewControllerRepresentable {
    func makeUIViewController(context: Context) -> UIViewController {
        MainViewControllerKt.MainViewController(bridge: <Feature>BridgeImpl())
    }
    func updateUIViewController(_ uiViewController: UIViewController, context: Context) {}
}
```

### 5. Register Provider

```kotlin
// <module>/src/iosMain/kotlin/{PKG_PREFIX}/<featurename>/di/Module.ios.kt
actual val platformModule = module {
    singleOf(::IOS<Feature>Provider).bind<<Feature>Provider>()
}
```

## Signature Mapping

| Kotlin | Swift |
|--------|-------|
| `suspend fun foo()` | `func foo(completionHandler: @escaping (Error?) -> Void)` |
| `suspend fun foo(): T` | `func foo(completionHandler: @escaping (T?, Error?) -> Void)` |
| `suspend fun foo(a: String, b: Int): T` | `func foo(a: String, b: Int32, completionHandler: @escaping (T?, Error?) -> Void)` |

Types: `Int`→`Int32`, `Long`→`Int64`, `Boolean` return→`KotlinBoolean`, `Boolean` param→`Bool`

## Export Module

```kotlin
// composeApp/build.gradle.kts
iosTarget.binaries.framework { export(project(":core:data")) }
```

## Checklist

- [ ] Context Discovery: Detected `{PKG_PREFIX}` from existing feature namespace
- [ ] Interface in `<module>/src/iosMain/kotlin/{PKG_PREFIX}/<featurename>/`
- [ ] Provider wraps in try-catch returning `Either<T>`
- [ ] Swift in `iosApp/iosApp/` inherits `<ModulePrefix><InterfaceName>`
- [ ] Completion handler signature exact match (see Signature Mapping)
- [ ] MainViewController in `composeApp/src/iosMain/` accepts + registers bridge
- [ ] ContentView passes implementation
- [ ] Module exported in `composeApp/build.gradle.kts`
- [ ] `./gradlew :composeApp:embedAndSignAppleFrameworkForXcode`