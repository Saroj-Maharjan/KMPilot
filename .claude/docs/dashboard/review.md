# Code Review: Dashboard
**Date**: 2026-08-01 | **Spec**: v3.6.0

---

## Summary
Passed: 15/18 | Warnings: 3 | Critical: 0
**Status**: PASS WITH WARNINGS

---

## Mechanized Checks (`kmpilot_check.py dashboard`)

0 errors, 0 warnings across all 19 deterministic checks (R3, R5, R7, R8, R9, R11a/b/c, R12, R13, S1-S4, I1-I4). See `.claude/docs/_project/check-report.json` (generated 2026-08-01T05:11:15Z). Not re-derived below.

---

## Judgment Rules (delegated to code-reviewer agent)

| Rule | Verdict | Files | Findings |
|---|---|---|---|
| 1 — Interface + Impl | PASS | `data/datasource/`, `data/repository/` | Local + Remote datasource, Repository all interface+impl pairs. `DashboardLocalDataSourceImpl` bound in DI but unused in prod path — documented in spec §2.3, not a violation, just dead weight |
| 2 — Either\<T\> | PASS | `data/repository/DashboardRepositoryImpl.kt:10` | `getDashboard()` is the only fallible op; `Either<DashboardData>` correct at every layer |
| 4 — 4 UI States | PASS | `presentation/ui/DashboardScreen.kt:72-99` | Uninitialized/Loading → `AppLoadingState()`, Failed → `AppErrorState(...)` w/ retry + secondary action, Success → `DashboardContent`. All branches substantive |
| 6 — ImmutableList | N/A | `presentation/DashboardUiModel.kt:6-8` | UiModel holds only `UiState<DashboardData>`, no raw `List<T>` field directly on it. Lists live inside the DTO, rendered via plain `.forEach` (no `LazyColumn`/`items()`) — outside this rule's scope |
| 10 — Callback params | PASS | `presentation/ui/DashboardScreen.kt:38-44`, `presentation/navigation/DashboardNavigation.kt:12-16` | All callbacks (`onActionClick`, `onBackToDashboard`, `onAssetClick`, `onProfileClick`), zero `navController` |
| 11 (semantic) | **WARNING** | `presentation/DashboardViewModel.kt:16-17` | `T=DashboardData` DTO ✓, `RepositoryImpl` returns `Either<DashboardData>` ✓, but public flow named `uiModelState` not `uiModel` — see P2-2 |
| 12 (semantic) | PASS | all `components/*.kt` | UiModel carries no raw String fields; all copy via `stringResource(Res.string.*)` |
| 14 — Platform | N/A | — | Pure `network` profile (single GET via ApiClient), no platform types, no `expect`/`actual` |

## UI File Organization

| Check | Result | Detail |
|---|---|---|
| `DashboardContent` location | PASS | `presentation/ui/components/DashboardContent.kt` — correctly extracted (fixed since v3.4.0, see spec Appendix C changelog) |
| `Screen.kt` allowlist | PASS | `DashboardScreenRoot` + `DashboardScreen` only; Loading/Failed route to shared `AppLoadingState`/`AppErrorState`, no private shells |
| `DashboardScreenRoot`/`DashboardScreen` param naming | **WARNING** | Param named `uiState` not `uiModel` (`DashboardScreen.kt:60`, local var `:46`) — same root cause as Rule 11 finding |
| `DashboardHeader` sub-component | Low-priority note | Private composable w/ own `@Preview` inside `DashboardContent.kt:74-115` rather than its own file — deliberate (spec Appendix C v3.4.0), optional to extract further |
| `@Preview` composables | PASS | Canonical `androidx.compose.ui.tooling.preview.Preview` import used everywhere; all preview fns `private`, exempt from allowlist |

---

## Spec Compliance (`.claude/docs/dashboard/spec.md` v3.6.0)

| Section | Status | Details |
|---|---|---|
| Data Models | PASS | All DTOs match spec §4.3 field-for-field |
| Interfaces | PASS | Matches spec §5.2 |
| State | PASS | `DashboardUiModel` matches spec §6.2 |
| Navigation | **WARNING** | Spec §5.3 documents only `onActionClick`/`onBackToDashboard`; impl adds `onAssetClick`, `onProfileClick` (real, wired callbacks) — spec is stale |
| Functional Requirements | **WARNING** | Spec FR-6 (§3.1) says 4 quick actions; impl has 5 (Swap added, `QuickActions.kt:52-56`) — deliberate addition, spec never updated |

---

## Integration Points

| Point | Result | Location |
|---|---|---|
| 1. Gradle Include | PASS | `settings.gradle.kts` |
| 2. Gradle Dependency | PASS | `composeApp/build.gradle.kts` |
| 3. DI Init | PASS | `initKoin.kt` — `dashboardModule` in `modules(...)` |
| 4. Navigation | PASS | `BaseAppNavHost.kt` — `dashboard(...)` extension |

---

## Design-Aware Compliance

`blueprintConsumed: true`. Profile avatar, asset-click, and Swap action are post-blueprint additions from a later `/modify-feature` pass — never round-tripped back into the blueprint or spec. Expected (blueprints are one-time artifacts); fully captured by the Spec Compliance findings above, no separate action needed.

---

## Recommendations

### Critical (P1)
None.

### Warnings (P2)

**P2-1: ViewModel public flow named `uiModelState` instead of `uiModel` (Rule 11 / UI File Org convention)**
`DashboardViewModel` exposes `uiModelState`, which propagates as `uiState` naming into `DashboardScreen`/`DashboardScreenRoot`. Convention (and every other reviewed feature — `assetdetail`, `profile`, `swap`) uses `val uiModel: StateFlow<{Feature}UiModel>`. Since CLAUDE.md names `feature/dashboard/` as the reference implementation, this drift risks propagating into features copied from it.
Fix: Rename `_uiModelState`→`_uiModel`, `uiModelState`→`uiModel` in `DashboardViewModel.kt:16-17`; update `DashboardScreenRoot` param and `DashboardScreen` local var in `DashboardScreen.kt:46,60`.
Files: `presentation/DashboardViewModel.kt:16-17`, `presentation/ui/DashboardScreen.kt:46,60`

**P2-2: Spec Navigation section (§5.3) stale — missing `onAssetClick`/`onProfileClick`**
Implementation added two real, wired callbacks (profile-avatar click, portfolio-asset click) that the spec never documents.
Fix: Update spec §5.3 to list all 4 callbacks (`onActionClick`, `onBackToDashboard`, `onAssetClick`, `onProfileClick`).
Files: `.claude/docs/dashboard/spec.md:313-315`

**P2-3: Spec FR-6 (§3.1) stale — says 4 quick actions, impl has 5**
Swap was added as a 5th quick action (`QuickActions.kt:52-56`, wired end-to-end incl. sample data) but the spec's functional requirement was never updated.
Fix: Update spec §3.1 FR-6 to list 5 quick actions (Send, Receive, Pay, Top Up, Swap).
Files: `.claude/docs/dashboard/spec.md:67`

---

> **Next step —** run `/clear` to free the context window, then `/modify-feature dashboard apply fixes from @.claude/docs/dashboard/fixes.md` to address the review findings.
