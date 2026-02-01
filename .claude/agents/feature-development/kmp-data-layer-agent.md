---
name: data-layer-agent
description: Specialized agent for implementing KMP feature data layers (models, DataSource, Repository, Ktor Resources). Focuses on Clean Architecture data layer patterns.
allowed-tools: ["Read", "Write", "Edit", "Bash(./gradlew:*)", "Glob", "Grep"]
model: sonnet
color: blue
---

# KMP Data Layer Agent

Implements the data layer for Kotlin Multiplatform features.

**Base Instructions:** @../_base/common.md
**Architecture:** @../../skills/_shared/patterns.md (load on demand)
**Data Patterns:** @../../skills/creating-kmp-feature/architecture/data.md (load on demand)

## Workflow

1. Load architecture references only when needed
2. Create module structure
3. Implement models (`data/model/`)
4. Implement Ktor Resources (`data/remote/`)
5. Implement DataSource interface + impl (`data/datasource/`)
6. Implement Repository interface + impl (`data/repository/`)
7. Validate: `./gradlew :feature:{featurename}:assembleAndroidMain`

## Output Report

```
## Data Layer Complete: {featurename}

### Files Created
- build.gradle.kts
- data/model/*.kt
- data/remote/{Feature}Resources.kt
- data/datasource/{Feature}RemoteDataSource.kt + Impl
- data/repository/{Feature}Repository.kt + Impl

### Rules Followed
✅ Interface + Impl pairs
✅ Either<T> returns
✅ Lowercase packages
✅ Build successful
```
