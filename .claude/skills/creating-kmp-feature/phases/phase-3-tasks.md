# Phase 3: Task Generation

**Purpose**: Break down PRD into implementation-ready tasks with agent assignments.

**Prerequisites**: PRD confirmed by user.

---

## Checklist

```
Task Generation Progress:
- [ ] Step 3.1: Determine task count based on complexity
- [ ] Step 3.2: Generate structured task files
- [ ] Step 3.3: Define task groups for validation
- [ ] Step 3.4: Create summary file (tasks.md)
- [ ] Step 3.5: Request user confirmation
```

---

## Step 3.1: Determine Task Count

Based on PRD complexity:

### Simple (UI-only, no API)
- **Tasks**: 3-5
- **Groups**:
  - Group 1: Foundation + UI (2-3 tasks)
  - Group 2: Integration (1-2 tasks)

### Medium (CRUD with API)
- **Tasks**: 6-10
- **Groups**:
  - Group 1: Foundation + Data (3-4 tasks)
  - Group 2: Presentation + UI (2-3 tasks)
  - Group 3: Integration (1-2 tasks)

### Complex (Multiple screens/entities)
- **Tasks**: 10-15
- **Groups**:
  - Group 1: Foundation + Data (4-6 tasks)
  - Group 2: Presentation + UI (4-6 tasks)
  - Group 3: Integration (2-3 tasks)

---

## Step 3.2: Generate Task Files

Create individual task files in `.claude/docs/{featurename}/`:
- `task-1-{title}.md`
- `task-2-{title}.md`
- etc.

Use template: [templates/task-template.md](../templates/task-template.md)

### Agent Assignment

| Group | Agent | Tasks |
|-------|-------|-------|
| Data | `data-layer-agent` | Module structure, models, DataSource, Repository, Ktor Resources |
| UI | `ui-layer-agent` | UiModel, ViewModel, Screen composables, Navigation |
| Integration | `integration-agent` | DI module, 4 integration points |

### Scenario Guidance

| Task Type | Include Scenarios? |
|-----------|-------------------|
| UI tasks, ViewModel behavior, navigation | ✅ Yes |
| Module structure, build config, DI setup, pure data models | ❌ Skip |

---

## Step 3.3: Define Task Groups for Validation

### Group 1: Data Layer
- **Agent**: `data-layer-agent`
- **Tasks**: Module structure, models, DataSource, Repository, Ktor Resources
- **Validate after**:
  ```bash
  ./gradlew :feature:{featurename}:assembleAndroidMain
  ```

### Group 2: UI Layer
- **Agent**: `ui-layer-agent`
- **Tasks**: UiModel, ViewModel, Screen composables, Navigation
- **Validate after**:
  ```bash
  ./gradlew :feature:{featurename}:assembleAndroidMain
  ```

### Group 3: Integration
- **Agent**: `integration-agent`
- **Tasks**: DI module, 4 integration points
- **Validate after**:
  ```bash
  ./gradlew assembleDebug
  ./gradlew ktlintFormat
  ```

---

## Step 3.4: Create Summary File

Create `tasks.md` as overview:

```markdown
# Tasks: {FeatureName}

## Summary
- **Total Tasks**: {N}
- **Complexity**: {Simple/Medium/Complex}
- **Groups**: Data ({X}) | UI ({Y}) | Integration ({Z})

## Task List

### Group 1: Data Layer (data-layer-agent)
- [ ] Task 1: {title}
- [ ] Task 2: {title}

### Group 2: UI Layer (ui-layer-agent)
- [ ] Task 3: {title}
- [ ] Task 4: {title}

### Group 3: Integration (integration-agent)
- [ ] Task 5: {title}
```

---

## Step 3.5: Request Confirmation

1. **Display first 2-3 task files** as examples

2. **Display `tasks.md`** overview

3. **Show summary**:
   ```
   "{X} tasks in {Y} groups (Data: {N}, UI: {M}, Integration: {K})"
   ```

4. **Ask** — emit as the very last line of output, styled to catch the eye:
   ```
   ---

   > **Next step —** review the tasks at `.claude/docs/{featurename}/tasks.md` and confirm to proceed with implementation, or request changes.
   ```

5. **Wait for user approval** before proceeding to Phase 4

---

## Output

After user confirms tasks:
- Individual task files saved to `.claude/docs/{featurename}/task-*.md`
- Summary file saved to `.claude/docs/{featurename}/tasks.md`
- Agent assignments clear for each task
- Ready to proceed to **Phase 4: Implementation**
