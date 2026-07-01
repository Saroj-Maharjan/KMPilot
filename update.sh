#!/usr/bin/env bash
# Pull a newer KMPilot release into this project WITHOUT corrupting your code.
#
# How it works:
#   • Reads .kmpilot.json (written at install) for your installed version and
#     package prefix.
#   • Fetches upstream at your installed version (base) and the target release.
#   • Tier 1 (always): 3-way-merges package-agnostic tooling — .claude/skills,
#     .claude/agents, .claude/hooks, .claude/commands, CLAUDE.md, gradle wrapper.
#   • Tier 2 (--core): re-applies YOUR package rename to upstream core/ changes,
#     then 3-way-merges. Conflicts are written as <<<<<<< markers — NEVER
#     auto-resolved.
#   • Never touches feature/, your app modules (composeApp/androidApp/iosApp),
#     or your per-feature specs (.claude/docs/<feature>/).
#   • Never commits. You review `git diff`, resolve any conflicts, then commit.
#
# Usage:
#   ./update.sh [--to vX.Y.Z] [--from vX.Y.Z] [--core] [--dry-run] [--stash]
#
#   --to      target release tag (default: latest vX.Y.Z published upstream)
#   --from    base release tag to diff from (default: your installed version)
#   --core    also merge core/ modules (rename-aware; may produce conflicts)
#   --dry-run show what would change; write nothing
#   --stash   auto-stash a dirty working tree before merging
#
set -euo pipefail

# ── output helpers ──────────────────────────────────────────────────────────
info() { printf '   %s\n' "$*"; }
warn() { printf '⚠  %s\n' "$*" >&2; }
die()  { printf 'Error: %s\n' "$*" >&2; exit 1; }

# ── args ────────────────────────────────────────────────────────────────────
WITH_CORE=no
DRY_RUN=no
STASH=no
TO_OVERRIDE=""
FROM_OVERRIDE=""
while [[ $# -gt 0 ]]; do
    case "$1" in
        --core)     WITH_CORE=yes ;;
        --dry-run)  DRY_RUN=yes ;;
        --stash)    STASH=yes ;;
        --to)       TO_OVERRIDE="${2:-}"; shift ;;
        --to=*)     TO_OVERRIDE="${1#*=}" ;;
        --from)     FROM_OVERRIDE="${2:-}"; shift ;;
        --from=*)   FROM_OVERRIDE="${1#*=}" ;;
        -h|--help)  sed -n '2,25p' "$0"; exit 0 ;;
        *)          die "unknown argument '$1' (try --help)" ;;
    esac
    shift
done

command -v git >/dev/null 2>&1 || die "git is required"

# ── locate project root (dir holding .kmpilot.json) ─────────────────────────
ROOT="$(pwd)"
while [[ "$ROOT" != "/" && ! -f "$ROOT/.kmpilot.json" ]]; do ROOT="$(dirname "$ROOT")"; done
[[ -f "$ROOT/.kmpilot.json" ]] || die "no .kmpilot.json found — run this from inside a project installed by KMPilot's install.sh"
cd "$ROOT"
MANIFEST="$ROOT/.kmpilot.json"

# ── read manifest (no jq dependency) ────────────────────────────────────────
json_get() { sed -n "s/.*\"$1\"[[:space:]]*:[[:space:]]*\"\([^\"]*\)\".*/\1/p" "$MANIFEST" | head -n1; }
CUR_VER="$(json_get kmpilotVersion)"
NAME="$(json_get projectName)"
PKG="$(json_get packagePrefix)"
REPO="$(json_get templateRepo)"
U_PKG="$(json_get upstreamPkg)"
U_NAME="$(json_get upstreamName)"
[[ -n "$CUR_VER" && -n "$PKG" && -n "$REPO" && -n "$U_PKG" && -n "$U_NAME" ]] \
    || die ".kmpilot.json is missing required fields"

# ── derived rename identifiers (mirror scripts/rename.sh exactly) ───────────
PKG_PATH="${PKG//.//}"
NAME_LOWER="$(printf '%s' "$NAME" | tr '[:upper:]' '[:lower:]')"
U_PKG_PATH="$U_PKG"
U_NAME_LOWER="$(printf '%s' "$U_NAME" | tr '[:upper:]' '[:lower:]')"
U_APPNS="${U_PKG}.${U_NAME_LOWER}"
U_APPID="com.${U_APPNS}"

# Cross-platform sed -i (BSD/macOS vs GNU/Linux)
if [[ "$(uname -s)" == "Darwin" ]]; then sedi() { sed -i '' "$@"; }; else sedi() { sed -i "$@"; }; fi

# rename_stream <src> <dest>: rewrite upstream identifiers → this project's
# (two-phase sentinel, byte-for-byte consistent with scripts/rename.sh:101-108).
rename_stream() {
    cp "$1" "$2"
    sedi "s|${U_APPID}|@@K_APPID@@|g" "$2"
    sedi "s|${U_APPNS}|@@K_APPNS@@|g" "$2"
    sedi "s|${U_PKG}|@@K_PKG@@|g" "$2"
    sedi "s|@@K_APPID@@|${PKG}|g" "$2"
    sedi "s|@@K_APPNS@@|${PKG}|g" "$2"
    sedi "s|@@K_PKG@@|${PKG}|g" "$2"
    sedi "s|${U_NAME}|${NAME}|g" "$2"
    sedi "s|${U_NAME_LOWER}\\.|${NAME_LOWER}.|g" "$2"
}

# rename_path <upstream-path>: core sources live under kotlin/<upstreamPkg>/…;
# remap that one segment to this project's package path. Other paths unchanged.
rename_path() { printf '%s' "$1" | sed "s#/kotlin/${U_PKG_PATH}/#/kotlin/${PKG_PATH}/#"; }

# ── temp upstream checkout ──────────────────────────────────────────────────
TMP="$(mktemp -d)"
STASHED=no
cleanup() {
    rm -rf "$TMP"
    [[ "$STASHED" == "yes" ]] && { info "restoring stashed changes"; git -C "$ROOT" stash pop >/dev/null 2>&1 || true; }
}
trap cleanup EXIT
UP="$TMP/upstream"

# ── preflight: clean working tree (so merge results are reviewable) ─────────
if [[ "$DRY_RUN" == "no" ]]; then
    if ! git -C "$ROOT" diff --quiet 2>/dev/null || ! git -C "$ROOT" diff --cached --quiet 2>/dev/null; then
        if [[ "$STASH" == "yes" ]]; then
            git -C "$ROOT" stash push -u -m "kmpilot-update" >/dev/null; STASHED=yes
            info "stashed your working changes"
        else
            die "working tree not clean. Commit or stash first, or pass --stash."
        fi
    fi
fi

echo "→ Fetching upstream ($REPO)…"
git clone --quiet --no-checkout "$REPO" "$UP" || die "clone failed: $REPO"

# ── resolve base + target tags ──────────────────────────────────────────────
BASE_TAG="${FROM_OVERRIDE:-v$CUR_VER}"
if [[ -n "$TO_OVERRIDE" ]]; then
    TARGET_TAG="$TO_OVERRIDE"
else
    TARGET_TAG="$(git -C "$UP" tag -l 'v*' --sort=-v:refname | head -n1)"
fi
[[ -n "$TARGET_TAG" ]] || die "no release tags found upstream"
git -C "$UP" rev-parse -q --verify "refs/tags/${BASE_TAG}^{commit}" >/dev/null \
    || die "base tag '$BASE_TAG' not found upstream — pass --from=<a real release tag>"
git -C "$UP" rev-parse -q --verify "refs/tags/${TARGET_TAG}^{commit}" >/dev/null \
    || die "target tag '$TARGET_TAG' not found upstream"
TARGET_VER="${TARGET_TAG#v}"

if [[ "$BASE_TAG" == "$TARGET_TAG" ]]; then
    echo "✓ Already on the latest release ($TARGET_TAG). Nothing to do."
    exit 0
fi
[[ "$DRY_RUN" == "yes" ]] && echo "→ Updating $BASE_TAG → $TARGET_TAG  (dry run)" || echo "→ Updating $BASE_TAG → $TARGET_TAG"

# ── path tiers ──────────────────────────────────────────────────────────────
TIER1_PATHS=(.claude/skills .claude/agents .claude/hooks .claude/commands
             .claude/settings.json .claude/docs/_shared
             CLAUDE.md gradlew gradlew.bat gradle/wrapper update.sh)
TIER2_PATHS=(core)
MANUAL_PATHS=(gradle/libs.versions.toml build.gradle.kts settings.gradle.kts
             composeApp androidApp iosApp)

applied=0; merged=0; conflicts=0; skipped=0; manual=0

up_show() { git -C "$UP" show "${1}:${2}" 2>/dev/null || true; }

# process_file <upstream-path> <status A|M|D> <rename yes|no>
process_file() {
    local up_path="$1" status="$2" rename="$3" down_path
    if [[ "$rename" == "yes" ]]; then down_path="$(rename_path "$up_path")"; else down_path="$up_path"; fi

    # Never overwrite the running updater in place — bash may re-read $0 mid-run and
    # corrupt this very invocation. Stage it as update.sh.new for the user to swap in
    # (surfaced in the summary below).
    if [[ "$down_path" == "update.sh" ]]; then down_path="update.sh.new"; fi

    if [[ "$status" == "D" ]]; then
        warn "upstream removed: $up_path — left your $down_path untouched (delete manually if unused)"
        manual=$((manual+1)); return
    fi

    local base_f="$TMP/base" theirs_f="$TMP/theirs" ours_f="$TMP/ours" result="$TMP/result"
    : > "$base_f"; : > "$theirs_f"

    if [[ "$rename" == "yes" ]]; then
        up_show "$TARGET_TAG" "$up_path" > "$TMP/raw"; rename_stream "$TMP/raw" "$theirs_f"
        if [[ "$status" != "A" ]]; then up_show "$BASE_TAG" "$up_path" > "$TMP/raw"; rename_stream "$TMP/raw" "$base_f"; fi
    else
        up_show "$TARGET_TAG" "$up_path" > "$theirs_f"
        if [[ "$status" != "A" ]]; then up_show "$BASE_TAG" "$up_path" > "$base_f"; fi
    fi

    # downstream missing this file → straight add
    if [[ ! -f "$down_path" ]]; then
        if [[ "$DRY_RUN" == "no" ]]; then mkdir -p "$(dirname "$down_path")"; cp "$theirs_f" "$down_path"; fi
        info "add:     $down_path"; applied=$((applied+1)); return
    fi
    cp "$down_path" "$ours_f"

    # already identical to upstream target → nothing to do
    if cmp -s "$ours_f" "$theirs_f"; then skipped=$((skipped+1)); return; fi

    set +e
    git merge-file -p -L "yours" -L "base ($BASE_TAG)" -L "upstream ($TARGET_TAG)" \
        "$ours_f" "$base_f" "$theirs_f" > "$result" 2>/dev/null
    local rc=$?
    set -e
    if [[ $rc -eq 0 ]]; then
        [[ "$DRY_RUN" == "no" ]] && cp "$result" "$down_path"
        info "merge:   $down_path"; merged=$((merged+1))
    elif [[ $rc -ge 1 && $rc -lt 128 ]]; then
        [[ "$DRY_RUN" == "no" ]] && cp "$result" "$down_path"
        warn "CONFLICT: $down_path — resolve the <<<<<<< markers"; conflicts=$((conflicts+1))
    else
        warn "merge error on $down_path (skipped)"; manual=$((manual+1))
    fi
}

# process_tier <rename yes|no> <path...>
process_tier() {
    local rename="$1"; shift
    while IFS=$'\t' read -r status path; do
        [[ -z "${status:-}" ]] && continue
        case "${status:0:1}" in
            A) process_file "$path" A "$rename" ;;
            D) process_file "$path" D "$rename" ;;
            *) process_file "$path" M "$rename" ;;
        esac
    done < <(git -C "$UP" diff --name-status --no-renames "$BASE_TAG" "$TARGET_TAG" -- "$@")
}

echo "→ Tier 1 — tooling (.claude, CLAUDE.md, gradle wrapper)…"
process_tier no "${TIER1_PATHS[@]}"

if [[ "$WITH_CORE" == "yes" ]]; then
    echo "→ Tier 2 — core/ (rename-aware)…"
    process_tier yes "${TIER2_PATHS[@]}"
else
    core_changed="$(git -C "$UP" diff --name-only --no-renames "$BASE_TAG" "$TARGET_TAG" -- "${TIER2_PATHS[@]}" 2>/dev/null | grep -c . || true)"
    [[ "${core_changed:-0}" -gt 0 ]] && info "core/ changed in $core_changed file(s) — re-run with --core to merge them"
fi

# ── manual-review report ────────────────────────────────────────────────────
changed_manual="$(git -C "$UP" diff --name-only --no-renames "$BASE_TAG" "$TARGET_TAG" -- "${MANUAL_PATHS[@]}" 2>/dev/null || true)"
if [[ -n "$changed_manual" ]]; then
    warn "Manual review — upstream changed these; reconcile by hand (NOT auto-applied):"
    printf '%s\n' "$changed_manual" | sed 's/^/      /' >&2
    manual=$(( manual + $(printf '%s\n' "$changed_manual" | grep -c .) ))
    if printf '%s\n' "$changed_manual" | grep -qE 'libs\.versions\.toml|iosApp/iosApp/Info\.plist'; then
        warn "  ↳ Your APP version lives in these (android-versionName/versionCode, iOS CFBundleShortVersionString/CFBundleVersion)."
        warn "    That version is YOURS and independent of KMPilot's — merge only dependency/config changes;"
        warn "    do NOT copy KMPilot's version numbers over your own."
    fi
fi

# ── bump manifest version (unless dry run) ──────────────────────────────────
if [[ "$DRY_RUN" == "no" ]]; then
    sedi "s|\"kmpilotVersion\"[[:space:]]*:[[:space:]]*\"[^\"]*\"|\"kmpilotVersion\": \"${TARGET_VER}\"|" "$MANIFEST"
fi

# ── summary ─────────────────────────────────────────────────────────────────
echo ""
echo "── Update summary ($BASE_TAG → $TARGET_TAG) ──"
echo "   added:     $applied"
echo "   merged:    $merged"
echo "   unchanged: $skipped"
echo "   conflicts: $conflicts"
echo "   manual:    $manual"
echo ""
if [[ "$DRY_RUN" == "yes" ]]; then
    echo "Dry run — nothing written. Re-run without --dry-run to apply."
    exit 0
fi
# The updater changed and was staged as update.sh.new (couldn't cp over the running script's
# inode mid-run). Swapping it in with `mv` (rename) IS safe — this process keeps executing its
# original inode, the new file only takes effect next run. update.sh is tracked in git, so the
# change shows in `git diff` and is revertible. Only exists when DRY_RUN=no, so no guard needed.
if [[ -f update.sh.new ]]; then
    mv -f update.sh.new update.sh && chmod +x update.sh
    echo "↻ Updated the updater itself (update.sh) — change is in your git diff; revert if unwanted."
    echo ""
fi
echo "Release notes: ${REPO%.git}/blob/main/CHANGELOG.md"
if [[ "$conflicts" -gt 0 ]]; then
    echo "Resolve the <<<<<<< conflict markers, then:  git diff  →  git add -A  →  git commit"
    exit 1
fi
echo "Review with:  git diff   then commit when satisfied (this script never commits)."
