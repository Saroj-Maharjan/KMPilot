#!/usr/bin/env bash
# Rename a KMPilot template clone to your own project.
#
# Usage:
#   scripts/rename.sh --name=<ProjectName> --pkg=<package.prefix>
#
# Example:
#   scripts/rename.sh --name=MyStore --pkg=com.acme.store

set -euo pipefail

# Always operate on the project containing this script, regardless of cwd.
# Without this, find . hits the caller's directory and can rewrite the wrong tree.
SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
cd "${SCRIPT_DIR}/.."

# Template's current identifiers (KMPilot's own values)
OLD_NAME="KMPilot"
OLD_PKG="thisissadeghi"
OLD_APP_NS="thisissadeghi.kmpilot"
OLD_APP_ID="com.thisissadeghi.kmpilot"
OLD_PKG_PATH="thisissadeghi"

NEW_NAME=""
NEW_PKG=""

for arg in "$@"; do
    case "$arg" in
        --name=*) NEW_NAME="${arg#*=}" ;;
        --pkg=*)  NEW_PKG="${arg#*=}" ;;
        -h|--help)
            cat <<USAGE
KMPilot rename — convert this template into your project.

Usage:
  scripts/rename.sh --name=<ProjectName> --pkg=<package.prefix>

Arguments:
  --name   Project display name (PascalCase, e.g., MyStore)
  --pkg    Package prefix (lowercase, dotted; e.g., com.acme.store)

Example:
  scripts/rename.sh --name=MyStore --pkg=com.acme.store
USAGE
            exit 0
            ;;
        *)
            echo "Error: unknown argument '$arg'" >&2
            echo "Try: scripts/rename.sh --help" >&2
            exit 1
            ;;
    esac
done

if [[ -z "$NEW_NAME" || -z "$NEW_PKG" ]]; then
    echo "Error: both --name and --pkg are required" >&2
    echo "Try: scripts/rename.sh --help" >&2
    exit 1
fi
if [[ ! "$NEW_NAME" =~ ^[A-Za-z][A-Za-z0-9_]*$ ]]; then
    echo "Error: --name must be a valid identifier (letters/digits/underscore, start with letter)" >&2
    exit 1
fi
if [[ ! "$NEW_PKG" =~ ^[a-z][a-z0-9]*(\.[a-z][a-z0-9]*)+$ ]]; then
    echo "Error: --pkg must be lowercase dotted (e.g., com.acme.store)" >&2
    exit 1
fi

# Guard: refuse if template marker is gone (already renamed)
if ! grep -rq "$OLD_PKG" \
    --include="*.kts" --include="*.kt" --include="*.xml" \
    --exclude-dir=.git --exclude-dir=build --exclude-dir=.gradle \
    --exclude-dir=.idea --exclude-dir=.kotlin . 2>/dev/null; then
    echo "Error: template marker '$OLD_PKG' not found — project already renamed?" >&2
    exit 1
fi

NEW_PKG_PATH="${NEW_PKG//.//}"
NEW_NAME_LOWER="$(printf '%s' "$NEW_NAME" | tr '[:upper:]' '[:lower:]')"
OLD_NAME_LOWER="$(printf '%s' "$OLD_NAME" | tr '[:upper:]' '[:lower:]')"

# Cross-platform sed -i (BSD/macOS vs GNU/Linux)
if [[ "$(uname -s)" == "Darwin" ]]; then
    sedi() { sed -i '' "$@"; }
else
    sedi() { sed -i "$@"; }
fi

echo "→ Renaming text references..."

# Replace across Kotlin sources, gradle scripts, XML, proguard, properties.
# Order: most specific first so longer matches win.
# The final pass handles Compose Multiplatform Resources: the plugin derives
# its generated `Res` class package from rootProject.name.lowercase(), so any
# `import kmpilot.<module>.generated.resources.*` must be rewritten.
while IFS= read -r -d '' file; do
    # Package identifiers via sentinels: two-phase OLD→sentinel→NEW so a NEW_PKG
    # that contains OLD_PKG as a substring (e.g. new "thisissadeghi.kickoff"
    # over old "thisissadeghi") is not re-matched by a later pass and doubled
    # into "thisissadeghi.kickoff.kickoff". Most specific first.
    sedi "s|${OLD_APP_ID}|@@KMPILOT_APPID@@|g" "$file"
    sedi "s|${OLD_APP_NS}|@@KMPILOT_APPNS@@|g" "$file"
    sedi "s|${OLD_PKG}|@@KMPILOT_PKG@@|g" "$file"
    sedi "s|@@KMPILOT_APPID@@|${NEW_PKG}|g" "$file"
    sedi "s|@@KMPILOT_APPNS@@|${NEW_PKG}|g" "$file"
    sedi "s|@@KMPILOT_PKG@@|${NEW_PKG}|g" "$file"
    sedi "s|${OLD_NAME}|${NEW_NAME}|g" "$file"
    sedi "s|${OLD_NAME_LOWER}\\.|${NEW_NAME_LOWER}.|g" "$file"
done < <(find . -type f \
    \( -name "*.kt" -o -name "*.kts" -o -name "*.xml" -o -name "*.gradle" -o -name "*.pro" \
       -o -name "*.swift" -o -name "*.plist" -o -name "*.pbxproj" -o -name "*.xcconfig" \
       -o -name "Podfile" \) \
    -not -path "./.git/*" \
    -not -path "*/build/*" \
    -not -path "./.gradle/*" \
    -not -path "./.idea/*" \
    -not -path "./.kotlin/*" \
    -not -path "*/Pods/*" \
    -print0)

echo "→ Moving package directories..."

while IFS= read -r -d '' dir; do
    parent="$(dirname "$dir")"
    new_dir="${parent}/${NEW_PKG_PATH}"
    if [[ -e "$new_dir" ]]; then
        echo "  skip (exists): $new_dir"
        continue
    fi
    # Move via a temp sibling first. Guards the case where NEW_PKG_PATH nests
    # under OLD_PKG_PATH (new prefix starts with old, e.g.
    # thisissadeghi → thisissadeghi/kickoff): a direct mv "$dir" "$new_dir"
    # would target a subdir of itself and fail with "Invalid argument".
    tmp="$(mktemp -d "${parent}/.kmpilot-rename.XXXXXX")"
    mv "$dir" "${tmp}/pkg"
    mkdir -p "$(dirname "$new_dir")"
    mv "${tmp}/pkg" "$new_dir"
    rmdir "$tmp"
    echo "  ${dir#./} → ${new_dir#./}"
done < <(find . -type d -name "${OLD_PKG_PATH}" -path "*/kotlin/${OLD_PKG_PATH}" -print0)

# Flatten the redundant project-name subdir for modules whose namespace was
# <prefix>.<projectname> (composeApp, androidApp). After the move, those
# sources sit at <pkg-path>/<oldname-lower>/ but the package is now <pkg>,
# so we lift contents up one level.
while IFS= read -r -d '' dir; do
    parent="$(dirname "$dir")"
    find "$dir" -mindepth 1 -maxdepth 1 -exec mv -- {} "$parent/" \;
    rmdir "$dir"
    echo "  flatten: ${dir#./}"
done < <(find . -type d -path "*/kotlin/${NEW_PKG_PATH}/${OLD_NAME_LOWER}" -print0)

echo "→ Writing fresh README..."
cat > README.md <<'README_EOF'
# __NAME__

Kotlin Multiplatform project scaffolded from [KMPilot](https://github.com/ThisIsSadeghi/KMPilot).

## Quick start

```bash
./gradlew assembleDebug
```

The app opens on a Welcome screen. Add your first feature with Claude Code —
it wires navigation and removes the Welcome screen for you.

## AI-assisted development

This project ships with Claude Code skills for feature scaffolding, testing, and review.

```bash
claude
> /create-feature ...
```

Available skills are in `.claude/skills/`.

## Project structure

- `composeApp/` — shared Compose Multiplatform UI
- `androidApp/` — Android entry point
- `iosApp/` — iOS entry point (Xcode project)
- `core/` — `common`, `data`, `designsystem` modules
- `feature/` — feature modules (one per business domain)

See `CLAUDE.md` for architecture conventions.
README_EOF
sedi "s|__NAME__|${NEW_NAME}|g" README.md

echo ""
echo "✓ Renamed to '${NEW_NAME}' (package: ${NEW_PKG})"
echo ""
echo "Next:"
echo "  1. Review:  git diff"
echo "  2. Build:   ./gradlew assembleDebug"
echo "  3. Start:   claude"
