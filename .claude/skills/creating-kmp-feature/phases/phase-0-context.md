# Phase 0: Context Discovery

**Purpose**: Auto-detect project configuration before analyzing user's prompt. This enables the skill and agents to work across different KMP projects without hardcoded values.

**When**: Run automatically as the first step, before PRD generation.

---

## Checklist

```
Context Discovery Progress:
- [ ] Step 0.1: Detect package prefix (PKG_PREFIX)
- [ ] Step 0.2: Detect integration paths (INIT_KOIN_PATH, NAV_HOST_PATH)
- [ ] Step 0.3: Detect core modules (CORE_MODULES)
- [ ] Step 0.4: Detect design system package (DESIGN_SYSTEM_PKG)
- [ ] Step 0.5: Detect core module namespaces
- [ ] Step 0.6: Store context for agent invocation
```

---

## Step 0.1: Detect Package Prefix

1. **Find a feature's build.gradle.kts**:
   ```
   Glob: feature/*/build.gradle.kts (take first match)
   ```

2. **Extract namespace**:
   ```
   Grep for: namespace = "..."
   Example: namespace = "com.example.login" → extract "com.example.login"
   ```

3. **Derive package prefix**:
   ```
   Remove the last segment: "com.example.login" → "com.example"
   Store as: {PKG_PREFIX}
   ```

**Fallback**: If no features exist yet, grep for package declarations in `core/common/src/**/*.kt` to find the base package.

---

## Step 0.2: Detect Integration Paths

1. **Find initKoin file**:
   ```
   Grep "startKoin" in composeApp/src/**/*.kt
   Store path as: {INIT_KOIN_PATH}
   Example: composeApp/src/commonMain/kotlin/com/example/myapp/initKoin.kt
   ```

2. **Find navigation host file**:
   ```
   Grep "XNavHost|NavHost" in composeApp/src/**/*.kt (exclude designsystem)
   Store path as: {NAV_HOST_PATH}
   Example: composeApp/src/commonMain/kotlin/com/example/myapp/BaseAppNavHost.kt
   ```

---

## Step 0.3: Detect Core Modules

```
Glob: core/*/build.gradle.kts
Extract module names from paths
Store as: {CORE_MODULES} (e.g., common, data, designsystem)
```

---

## Step 0.4: Detect Design System Package

```
Grep "import.*designsystem\.X" in feature/**/*.kt
Extract package pattern
Store as: {DESIGN_SYSTEM_PKG} (e.g., com.example.designsystem)
```

---

## Step 0.5: Detect Core Module Namespaces

For portability across projects, detect each core module's namespace:

1. **Read core module build.gradle.kts files**:
   ```
   core/common/build.gradle.kts → grep `namespace = "..."` → {CORE_COMMON_PKG}
   core/data/build.gradle.kts → grep `namespace = "..."` → {CORE_DATA_PKG}
   core/designsystem/build.gradle.kts → grep `namespace = "..."` → {CORE_DESIGNSYSTEM_PKG}
   ```

2. **Derive PKG_PATH** (for file paths):
   ```
   Convert {PKG_PREFIX} dots to slashes:
   - "acme" → "acme"
   - "com.example" → "com/example"
   Store as: {PKG_PATH}
   ```

3. **Fallback**: If namespace not found, derive as `{PKG_PREFIX}.{module}`

---

## Step 0.6: Store Context for Agent Invocation

Store all detected values to pass to specialized agents:

```
PROJECT_CONTEXT:
  PKG_PREFIX: {detected value}
  PKG_PATH: {PKG_PREFIX with dots → slashes}
  CORE_COMMON_PKG: {detected from core/common}
  CORE_DATA_PKG: {detected from core/data}
  CORE_DESIGNSYSTEM_PKG: {detected from core/designsystem}
  INIT_KOIN_PATH: {detected path}
  NAV_HOST_PATH: {detected path}
  CORE_MODULES: {list of modules}
  DESIGN_SYSTEM_PKG: {detected package}
```

**Important**: Pass this context when invoking any specialized agent in Phase 3.

---

## Output

After completing context discovery, you should have all values needed for:
- Generating correct package names in PRD
- Passing context to specialized agents
- Knowing exact paths for integration points

Proceed to **Phase 1: PRD Generation**.
