# KMPilot v1 — Scope Manifest

> Source of truth for what ships in v1. If something isn't in this doc, it isn't in v1.
> Update this file when scope changes — do not create parallel planning docs.

**Status**: Draft locked 2026-05-14. Decisions 1–3 confirmed by maintainer.

---

## Identity

- **Name**: KMPilot
- **Tagline**: *"Describe a feature in one line. Get a production-quality KMP module."*
- **Meta-positioning** (launch narrative):
  > *"We encoded a full production architecture as a composable pipeline of AI skills. We proved it on KMP. Here's the pattern."*
- **Why this framing**: KMP-specific value wins the audience; the meta-pattern wins the front page. KMP alone is too small a TAM to be world-changing — the *pattern* is the export.

---

## What's in v1

### Hero workflow (the demo that sells everything)

- `/creating-kmp-feature` — one command, produces a complete feature module
- **Must run clean on a fresh machine, start-to-finish, in under 90 seconds**

### Core skills (must work reliably)

1. `/creating-kmp-feature`
2. `/modifying-kmp-feature`
3. `/feature-review`
4. `/feature-test`

### Foundation pieces

- **Installer**: two-track, ecosystem-native (no Node dependency)
  - Primary: `curl -fsSL https://raw.githubusercontent.com/ThisIsSadeghi/KMPilot/main/install.sh | sh -s MyProject com.example.myproject`
  - Secondary: GitHub "Use this template" button + `./scripts/rename.sh --name=… --pkg=…`
  - Both backed by one Bash script (`scripts/rename.sh`) — single source of truth
- **Hosting**: raw.githubusercontent.com URL (works the moment commits land on `main`, zero infrastructure)
- **Config file**: `kmpilot.config.yaml` capturing `pkgPrefix` and `featureDir` (the *only* config knobs for v1)
- **Example project**: one rendered output committed as a reference

### Docs

- README that sells the meta-pattern in 30 seconds
- ONE walkthrough video (60–90s, the hero demo)
- Skill catalog page (auto-generated from skill frontmatter if possible)

---

## What's NOT in v1 (deferred to v2+)

- **Adopt-mode** (`kmpilot init` in an existing repo) — too much surface area
- **Swappable design system** (X-components stays locked)
- **Swappable DI** (Koin stays locked)
- **`/ui-designer` + Stitch pipeline** — keep working internally but **not** in the launch demo (external dependency makes it unreliable to demo)
- **`/verify-ui`** — same reason
- **`/coverage`, `/audit-spec`, `/features-health`** — useful but not the hero
- **Cursor / Aider / other-IDE distribution** — Claude Code only for v1
- **Dedicated docs site** (Docusaurus/Nextra) — README is enough for launch

---

## Stack lock-ins (v1)

| Layer | Locked to | Rationale |
|---|---|---|
| Architecture | Clean (data/presentation/di) | Identity of the project |
| DI | Koin | Most popular in KMP |
| HTTP | Ktor + Resources | Idiomatic KMP |
| State | `setState` + `Either` + `UiState` | Project's invention, core differentiator |
| UI framework | Compose Multiplatform | No alternative worth supporting |
| Design system | X-components | Locked for v1 — adapters in v2 |
| JVM target | 21 | Matches current project |

**Config knobs in v1**: `pkgPrefix`, `featureDir`. That's it. Resist adding more.

---

## Hero demo (literal script)

```
$ curl -fsSL https://raw.githubusercontent.com/ThisIsSadeghi/KMPilot/main/install.sh \
    | sh -s MyStore com.acme.mystore
✓ Project scaffolded — packages renamed, git initialized
$ cd MyStore && claude
> /creating-kmp-feature build a product detail screen
   with reviews, rating, and add-to-cart
[60–90 seconds of skill output]
$ ./gradlew :feature:productdetail:desktopTest
✓ 12 tests passed
```

Screen-recording this is the entire marketing budget.

---

## Repo structure (v1)

Stay monorepo. Split only if growth demands it post-launch.

```
kmpilot/
├── .claude/          # the skills (existing)
├── installer/        # create-kmpilot-app (new)
├── template/         # what the installer renders (extract from feature/sample)
├── feature/sample/   # existing reference
├── core/             # existing
├── composeApp/       # existing
└── README.md         # rewritten for v1
```

---

## Launch criteria (binary checklist)

- [ ] Hero demo runs clean on a fresh machine, three times in a row
- [x] Installer works (via `curl | sh`, not `npx` — KMP ecosystem doesn't use Node)
- [x] README pitches the meta-pattern in the first paragraph
- [x] Demo video exists (embedded in README at GitHub user-attachments asset)
- [x] License chosen and added (MIT)
- [x] CONTRIBUTING.md exists
- [x] All 4 hero skills audited + e2e-validated for portability on renamed projects

6 of 7 checked. Last item is verifying the curl install actually runs cleanly on a fresh machine after commits land on `main`.

---

## Installer hosting — upgrade path

| Stage | URL | Cost | Prerequisite |
|---|---|---|---|
| **v1 (now)** | `raw.githubusercontent.com/ThisIsSadeghi/KMPilot/main/install.sh` | free | install.sh on `main` |
| **v1.1** | `thisissadeghi.github.io/KMPilot/install.sh` | free | enable GitHub Pages from `/docs` |
| **v2** | `kmpilot.dev/install.sh` | ~$15/yr | domain + DNS to GitHub Pages |

Each upgrade requires only a README edit — the URL changes, the install logic doesn't.

## Open decisions (deferred but tracked)

- **Branding / logo**: needed for launch, not for build.
- **Telemetry**: none for v1.
- **Monetization**: none for v1; OSS.

---

## What this doc is NOT

- Not a roadmap (roadmap goes in a separate file when v2 work starts)
- Not a spec (specs live at `.claude/docs/{name}/spec.md`)
- Not a changelog
- Not marketing copy (that lives in README and the launch post)
