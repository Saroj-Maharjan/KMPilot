# Phase 1: Prompt Analysis & PRD Generation

**Purpose**: Analyze user's request and generate an adaptive PRD (Product Requirements Document).

**Prerequisites**: Phase 0 (Context Discovery) completed.

---

## Checklist

```
PRD Generation Progress:
- [ ] Step 1.1: Analyze the prompt
- [ ] Step 1.2: Determine complexity (Simple/Medium/Complex)
- [ ] Step 1.3: Generate PRD using appropriate template
- [ ] Step 1.4: Save PRD and request confirmation
```

---

## Step 1.1: Analyze the Prompt

Extract the following from the user's prompt:

### 1. Feature Name
- Identify the core feature name (e.g., "settings", "productcatalog", "chat")
- Convert to **lowercase**, no spaces/hyphens/underscores
- This becomes `{featurename}` for packages

### 2. Feature Scope
- Core functionality
- User interactions
- Data operations

### 3. Data Requirements
- Does it need API integration? (Ktor Resources + DataSource + API models)
- Does it use local data only? (Local models + mocked data)
- What are the data entities? (User, Product, Message, etc.)

### 4. UI Requirements
- How many screens?
- What components are needed?
- Navigation flows?
- Form inputs or read-only displays?

### 5. Dependencies
- Which core modules? (common, data, designsystem)
- External libraries? (DataStore, etc.)

---

## Step 1.2: Determine Complexity

| Complexity | Criteria | Task Count |
|------------|----------|------------|
| **Simple** | UI-only, no API, < 3 screens | 3-5 tasks |
| **Medium** | CRUD with API, single-entity, basic business logic | 6-10 tasks |
| **Complex** | Multiple screens, complex logic, multiple entities | 10-15 tasks |

### Template Selection

```
If Simple:
  → Use [templates/prd-simple.md](../templates/prd-simple.md)
  → Skip architecture loading (lightweight)

If Medium/Complex:
  → Use [templates/prd-complex.md](../templates/prd-complex.md)
  → Reference architecture principles
  → Scan :core:designsystem for reusable X-components
```

---

## Step 1.3: Generate PRD

Load the appropriate template and fill in:

- Feature name and description
- Requirements (from prompt analysis)
- Data architecture (if API needed)
- UI design (screens, components)
- Implementation plan (complexity, task count, groups)
- Acceptance criteria (functional scenarios)
- Integration points summary

### Key Sections

**Acceptance Criteria** must include scenarios in this format:
```markdown
#### Scenario: {Feature} loads successfully
- GIVEN the user navigates to {Feature} screen
- WHEN the data loads successfully
- THEN the content MUST be displayed
- AND the loading state MUST transition to success
```

**Integration Points** must list the 4 required changes:
| File | Change Type | Description |
|------|-------------|-------------|
| settings.gradle.kts | MODIFIED | Add module include |
| composeApp/build.gradle.kts | MODIFIED | Add feature dependency |
| initKoin.kt | MODIFIED | Add DI initialization |
| BaseAppNavHost.kt | MODIFIED | Add navigation wiring |

---

## Step 1.4: Save PRD and Request Confirmation

1. **Create directory**:
   ```bash
   mkdir -p .claude/docs/{featurename}
   ```

2. **Save PRD**:
   ```
   Write to: .claude/docs/{featurename}/prd.txt
   ```

3. **Display PRD to user** (Read tool)

4. **Show summary**:
   ```
   PRD generated ({complexity}, {X} estimated tasks, API: {yes/no})
   - Simple: "Used lightweight template (no architecture loading)"
   - Complex: "Used detailed template with architecture context"
   ```

5. **Request confirmation**:
   ```
   "Review the PRD. Confirm to proceed with task generation, or request changes."
   ```

6. **Wait for user approval** before proceeding to Phase 2

---

## Token Efficiency

| Complexity | Architecture Tokens |
|------------|---------------------|
| Simple | ~0 (template only) |
| Complex | ~990 (loads architecture files when needed) |

---

## Output

After user confirms PRD:
- PRD saved to `.claude/docs/{featurename}/prd.txt`
- User has approved the approach
- Ready to proceed to **Phase 2: Task Generation**
