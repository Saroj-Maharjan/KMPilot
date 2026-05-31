# `/ui-designer` Flow Chart

End-to-end visual reference for what happens when a user invokes `/ui-designer {featurename}`. Mermaid diagrams render in GitHub, VS Code, and most markdown viewers. The textual breakdown below each diagram walks through the same flow step by step.

---

## 1. High-level flow

```mermaid
flowchart TD
    Start([User: /ui-designer featurename]):::user --> InitGate{stitch-project.json exists<br/>AND initState.completedAt set?}
    InitGate -->|No| ProjectInit[[Run Project Init<br/>phases/phase-init.md]]:::sub
    ProjectInit --> InitGate
    InitGate -->|Yes| Phase0Start

    subgraph Phase0[Phase 0 — Per-Feature Preflight]
        direction TB
        Phase0Start([Phase 0 begin]) --> S01[Step 0.1: Load config<br/>+ drift detection]
        S01 --> S02[Step 0.2: Verify Stitch MCP]
        S02 --> S03[Step 0.3: Resolve feature name]
        S03 --> S04[Step 0.4: Register or resume<br/>features featurename entry]
        S04 --> S05[Step 0.5: mkdir docs/featurename/designs]
    end

    S05 --> Phase1Start

    subgraph Phase1[Phase 1 — Design in Stitch]
        direction TB
        Phase1Start([Phase 1 begin]) --> S11[Step 1.1: Gather requirements<br/>+ determine isListBased]
        S11 --> S12Gate{Other approved features exist?}
        S12Gate -->|Yes| S12Run[Steps 1.2 → 1.6: Chrome snapshot<br/>+ override confirmation]
        S12Gate -->|No| S17
        S12Run --> S17
        S17[Step 1.7: State Selection<br/>set needsLoading / needsFailed / needsEmpty]:::decision
        S17 --> S18Gate{Any opted-in shared screen<br/>missing in sharedStateScreens?}
        S18Gate -->|Yes| S18Run[Step 1.8: Design missing shared screens<br/>runs phase-init On-Demand Procedures<br/>approve-or-edit loop each]:::gated
        S18Gate -->|No| S19
        S18Run --> S19[Step 1.9: Read current XTheme color scheme]
        S19 --> S110[Step 1.10: Generate success screen<br/>in Stitch]
        S110 --> S111[Step 1.11: Present designs]
        S111 --> S112Q{User picks}
        S112Q -->|Edit / Variants / Regenerate| S110
        S112Q -->|Approve| S113[Step 1.13: Finalize success]
        S113 --> S114[Step 1.14: State designs<br/>gated on selections]:::gated
        S114 --> S115[Step 1.15: HTML + token inventories<br/>selected states only]:::gated
        S115 --> S116[Step 1.16: Color audit<br/>selected inventories only]:::gated
        S116 --> S117[Step 1.17: Generate blueprint<br/>selected sections only]:::gated
        S117 --> S118[Step 1.18: Update stitch-project.json<br/>blueprintConsumed: false]
        S118 --> S119[Step 1.19: Final approval report]
    end

    S119 --> Done([Complete — user invokes<br/>/creating-kmp-feature or /modifying-kmp-feature]):::done

    classDef user fill:#1e3a8a,stroke:#1e40af,color:#fff
    classDef sub fill:#7c2d12,stroke:#9a3412,color:#fff
    classDef decision fill:#854d0e,stroke:#a16207,color:#fff
    classDef gated fill:#365314,stroke:#4d7c0f,color:#fff
    classDef done fill:#14532d,stroke:#166534,color:#fff
```

---

## 2. State Coverage decision tree (Step 1.1 → 1.7)

The dual gate for Empty is the most subtle part of the flow. This diagram makes it explicit.

```mermaid
flowchart TD
    R[Step 1.1: Requirements gathered] --> L{List-Based?<br/>positive indicators vs<br/>negative indicators}
    L -->|Unambiguous yes| LY[isListBased = true]
    L -->|Unambiguous no| LN[isListBased = false]
    L -->|Ambiguous| LQ[Ask user:<br/>Yes / No]
    LQ -->|Yes| LY
    LQ -->|No| LN

    LY --> Ask3[Step 1.7 question<br/>3 options: Loading / Failed / Empty]
    LN --> Ask2[Step 1.7 question<br/>2 options: Loading / Failed]

    Ask3 --> Pick3[User selects subset]
    Ask2 --> Pick2[User selects subset]

    Pick3 --> Compute3[needsLoading = picked Loading<br/>needsFailed = picked Failed<br/>needsEmpty = picked Empty AND isListBased]
    Pick2 --> Compute2[needsLoading = picked Loading<br/>needsFailed = picked Failed<br/>needsEmpty = false<br/>option was never shown]

    Compute3 --> Persist[Write features featurename states<br/>to stitch-project.json]
    Compute2 --> Persist

    Persist --> Down[Flags drive Steps 1.14 through 1.19]

    classDef gate fill:#854d0e,stroke:#a16207,color:#fff
    class L,LQ gate
```

**Dual-gate truth table for Empty:**

| `isListBased` | User picked "Empty state" | `needsEmpty` |
|---------------|---------------------------|--------------|
| false         | (option not shown)         | **false**    |
| true          | no                         | **false**    |
| true          | yes                        | **true**     |

`needsEmpty` is `true` **only** when both conditions hold. There is no path where a user can force Empty on a non-list screen.

---

## 3. Step 1.8 — on-demand shared screen design

```mermaid
flowchart TD
    Enter([Step 1.8 begin]) --> Check{For each opted-in state in loading, failed:<br/>does sharedStateScreens.state.screenId exist<br/>AND _shared/designs/state.png exist?}
    Check -->|All shared screens exist<br/>or none opted in| Skip[No-op — proceed to Step 1.9]
    Check -->|At least one missing| Inform[Inform user:<br/>'Designing shared state screen now<br/>before featurename success screen']

    Inform --> RunL{Loading missing<br/>AND needsLoading?}
    RunL -->|Yes| InitL[Run On-Demand Loading procedure:<br/>generate + approve-or-edit loop<br/>persist to sharedStateScreens.loading]
    RunL -->|No| RunF
    InitL --> RunF{Failed missing<br/>AND needsFailed?}
    RunF -->|Yes| InitF[Run On-Demand Failed procedure:<br/>generate + approve-or-edit loop<br/>persist to sharedStateScreens.failed]
    RunF -->|No| Verify
    InitF --> Verify[Verify all expected<br/>sharedStateScreens.state.screenId<br/>are non-null]
    Verify --> Exit([Step 1.8 done — proceed to Step 1.9])
    Skip --> Exit

    classDef gen fill:#365314,stroke:#4d7c0f,color:#fff
    classDef skip fill:#1e3a8a,stroke:#1e40af,color:#fff
    class InitL,InitF,Verify gen
    class Skip skip
```

The "Run On-Demand procedure" boxes invoke the **On-Demand Procedures** in `phase-init.md` (Generate Shared Loading Screen, Generate Shared Failed Screen) — same generation prompts, same approve-or-edit loops, same persistence targets. There is no separate implementation; Step 1.8 is the trigger, the on-demand procedures own the work.

---

## 4. Step 1.14 — per-state branching

```mermaid
flowchart TD
    Enter([Step 1.14 begin]) --> L{needsLoading?}
    L -->|true| LRef[Reference shared screen<br/>_shared/designs/loading.png]
    L -->|false| LSkip[Skip Loading entirely]
    LRef --> F
    LSkip --> F

    F{needsFailed?}
    F -->|true| FRef[Reference shared screen<br/>_shared/designs/failed.png]
    F -->|false| FSkip[Skip Failed entirely]
    FRef --> E
    FSkip --> E

    E{needsEmpty?}
    E -->|false| ESkip[Skip Empty entirely<br/>no edit_screens call]
    E -->|true| EGen[Initial Generation:<br/>edit_screens on success screen<br/>centered empty illustration prompt]
    EGen --> ELoop[Approve-or-Edit Loop]
    ELoop --> EQ{User decision}
    EQ -->|Edit| EEdit[Capture edit prompt<br/>edit_screens<br/>re-download]
    EEdit --> ELoop
    EQ -->|Approve| EPersist[Write emptyScreenId<br/>to stitch-project.json]

    ESkip --> Exit([Step 1.14 done])
    EPersist --> Exit

    classDef skip fill:#7f1d1d,stroke:#991b1b,color:#fff
    classDef ref fill:#1e3a8a,stroke:#1e40af,color:#fff
    classDef gen fill:#365314,stroke:#4d7c0f,color:#fff
    class LSkip,FSkip,ESkip skip
    class LRef,FRef ref
    class EGen,EEdit,EPersist gen
```

The loop tops out at 10 iterations.

---

## 5. What lives where after Phase 1

```mermaid
flowchart LR
    SP[(stitch-project.json)]:::cfg --> Feat[features.featurename]
    Feat --> S[states: loading/failed/empty bools]:::gate
    Feat --> SID[successScreenId + emptyScreenId]
    Feat --> Paths[htmlPath / tokensPath / screenshot]
    Feat --> Flag[blueprintConsumed: false]

    Shared[(_shared/designs/)]:::shared --> SL[loading.png + extracted/]
    Shared --> SF[failed.png + extracted/]

    Per[(docs/featurename/designs/)]:::per --> Suc[featurename.png]
    Per --> Emp[featurename_empty.png<br/>only if needsEmpty]:::gate
    Per --> Desc[featurename.md]
    Per --> BP[featurename_blueprint.md]
    Per --> Ext[extracted/stitch_success.html<br/>extracted/tokens_success.md<br/>+ empty.* only if needsEmpty]:::gate

    classDef cfg fill:#1e3a8a,stroke:#1e40af,color:#fff
    classDef shared fill:#365314,stroke:#4d7c0f,color:#fff
    classDef per fill:#7c2d12,stroke:#9a3412,color:#fff
    classDef gate fill:#854d0e,stroke:#a16207,color:#fff
```

Loading/Failed shared screens are **never copied** into the per-feature directory — downstream skills read them straight from `_shared/`.

---

## 6. Step-by-step narrative

| Step | What it does | User interaction? | Persists to JSON? |
|------|--------------|-------------------|-------------------|
| 0.1 | Load project config; check XTheme drift | Only if drift detected | Only on drift sync |
| 0.2 | Verify Stitch MCP available | Only on failure (guided setup) | No |
| 0.3 | Resolve feature name | Only if missing/ambiguous | No |
| 0.4 | Register/resume feature entry | No | **Yes** — seeds entry with `states` defaults |
| 0.5 | Create docs dir | No | No |
| 1.1 | Gather requirements + determine `isListBased` | If requirements unclear OR list-based ambiguous | No |
| 1.2 | Chrome snapshot from prior features (gate: when to run) | No — gate only | No |
| 1.3 | Detect explicit chrome override | No — derived from earlier prompt | No |
| 1.4 | Snapshot existing chrome from all approved features | Only if structural divergence between chrome archetypes (cosmetic auto-resolves) | No |
| 1.5 | Build the Shared Conventions block | No | No |
| 1.6 | Confirm with user (only when overriding) | Only if user override detected | No |
| 1.7 | State Selection (the new gate) | **Always** — multi-select | **Yes** — writes `features.states` |
| 1.8 | Design missing shared loading/failed on demand | Only when opted-in shared screen is absent: approve-or-edit loop per state | **Yes** — writes `sharedStateScreens.{state}` and `_shared/designs/` artifacts |
| 1.9 | Read current XTheme color scheme | No | No |
| 1.10 | Generate success screen in Stitch | Possibly: browser-sync prompt on timeout | No |
| 1.11 | Present designs | **Always** — Approve / Edit / Variants / Regen | No |
| 1.12 | Iterate (loops to 1.11) | Per iteration | No |
| 1.13 | Finalize approved success design | Asks user to clean up Stitch UI | **Yes** — writes `successScreenId`, `successScreenName` |
| 1.14 | State designs gated on selections | Empty: approve-or-edit loop (if `needsEmpty`) | **Yes** — writes `emptyScreenId` when empty approved |
| 1.15 | Acquire HTML + tokens for selected states | No (only browser-sync on timeout) | No |
| 1.16 | Color audit reconciled against inventories | No | No |
| 1.17 | Generate blueprint (selected sections) | No | No |
| 1.18 | Final write of `features.{featurename}` | No | **Yes** — full metadata + `blueprintConsumed: false` |
| 1.19 | Final report shown to user | Read-only | No |

---

## 7. Skip semantics — what "skipped" actually means

| Artifact | If state skipped |
|----------|-----------------|
| Stitch screen | Not referenced; for empty, `emptyScreenId` stays `null` |
| Screenshot file | None on disk (loading/failed: not even a copy of the shared `.png`) |
| Token inventory | Not read in Step 1.15; not consulted in Step 1.16 color audit |
| Blueprint section — loading/failed | Replaced with explicit `**Skipped**` marker pointing implementation at generic handling |
| Blueprint section — empty | **Section omitted entirely** (empty is a content variant, not a Rule-4 UI state) |
| `/verify-ui` audit | State excluded from the audit state list; no audit entry produced |
| Implementation by `/creating-kmp-feature` / `/modifying-kmp-feature` | Feature code must still satisfy Rule 4 (handle all UI states) — generic fallback used; no design reference passed to the UI agent |

---

## 8. Worked example — `/ui-designer carsdashboard`

First feature in a fresh project, non-list dashboard, user picks Loading + Failed. Because init no longer auto-generates shared screens, Step 1.8 kicks in and designs both before the success screen.

```mermaid
sequenceDiagram
    autonumber
    actor U as User
    participant S as ui-designer skill
    participant J as stitch-project.json
    participant M as Stitch MCP

    U->>S: /ui-designer carsdashboard
    S->>J: read — init complete after Init-5 finalize
    S->>M: get_project — verify shared project
    S->>J: features.carsdashboard absent — register seed entry
    S->>U: AskUserQuestion — what should this dashboard show
    U->>S: hero stats + recent activity + quick actions
    Note over S: isListBased = false<br/>negative indicator: dashboard
    S->>U: State Selection — Loading / Failed only, Empty NOT shown
    U->>S: pick Loading + Failed
    S->>J: write features.carsdashboard.states — l:true f:true e:false
    Note over S: Step 1.8 — shared screens missing<br/>sharedStateScreens.loading/failed.screenId == null
    S->>U: designing shared loading screen now before success
    S->>M: generate_screen_from_text — loading prompt
    S->>U: present shared loading screen — Approve / Edit
    U->>S: Approve
    S->>M: get_screen + curl HTML for loading
    S->>J: write sharedStateScreens.loading — screenId, htmlPath, tokensPath
    S->>U: now designing shared failed screen
    S->>M: generate_screen_from_text — failed prompt
    S->>U: present shared failed screen — Approve / Edit
    U->>S: Approve
    S->>M: get_screen + curl HTML for failed
    S->>J: write sharedStateScreens.failed
    Note over S: Both shared screens now exist<br/>future features inherit them
    S->>M: list_screens — baseline for success
    S->>M: generate_screen_from_text — success, dark theme prompt
    S->>U: open project in browser to sync
    U->>S: confirmed
    S->>M: list_screens — find new screen
    S->>U: present carsdashboard_v1.png — Approve
    U->>S: Approve
    S->>J: write successScreenId
    Note over S: Step 1.14 — Loading/Failed reference shared refs<br/>just created, Empty skipped
    S->>M: get_screen + curl HTML for success
    S->>S: extract tokens for success, read shared tokens for loading/failed
    S->>S: color audit + blueprint generation
    S->>J: full write — blueprintConsumed false
    S->>U: Completion report — states success/loading/failed, skipped empty
```

For a **second** feature (e.g. `/ui-designer transactions`) that also picks Loading + Failed: Step 1.8 sees the shared screens already exist (created during carsdashboard) and is a **no-op**. The transactions feature proceeds directly to its own success-screen design.

---

## 9. Cross-references

- Skill entry: [`ui-designer/SKILL.md`](../../../skills/ui-designer/SKILL.md)
- Phase Init (project bootstrap + On-Demand Procedures for shared screens): [`ui-designer/phases/phase-init.md`](../../../skills/ui-designer/phases/phase-init.md)
- Per-feature preflight: [`ui-designer/phases/phase-0-preflight.md`](../../../skills/ui-designer/phases/phase-0-preflight.md)
- Design phase (Steps 1.1 → 1.19, all gating logic): [`ui-designer/phases/phase-1-design.md`](../../../skills/ui-designer/phases/phase-1-design.md)
- Blueprint template + skipped-state markers: [`ui-designer/references/blueprint-spec.md`](../../../skills/ui-designer/references/blueprint-spec.md)
- Stitch MCP usage patterns + known issues: [`ui-designer/references/stitch-guide.md`](../../../skills/ui-designer/references/stitch-guide.md)
- Downstream auditor honoring `states`: [`verify-ui/SKILL.md`](../../../skills/verify-ui/SKILL.md)
