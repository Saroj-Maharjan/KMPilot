#!/usr/bin/env bash
# Bootstrap a new KMPilot project — interactive, colorized installer.
#
# Engine (clone → trim → rename → fresh docs → manifest → git init) with an
# interactive, colorized presentation layer: a banner, colored step log, a
# clone spinner, and prompts for the project name / package when you don't
# pass them as arguments.
#
# Usage (remote):
#   curl -fsSL https://raw.githubusercontent.com/ThisIsSadeghi/KMPilot/main/install.sh \
#     | bash -s <ProjectName> [package.prefix]
#   # or run with no args for the guided prompts:
#   curl -fsSL https://raw.githubusercontent.com/ThisIsSadeghi/KMPilot/main/install.sh | bash
#
# Usage (local):
#   ./install.sh <ProjectName> [package.prefix]
#   ./install.sh                 # guided prompts
#
# Installs from the latest published vX.Y.Z release tag by default (reproducible).
# The new project keeps a ./update.sh you can run later to pull future releases
# without clobbering your code (see update.sh).
#
# Env vars:
#   KMPILOT_TEMPLATE_REPO   Git URL of the template (default: ThisIsSadeghi/KMPilot)
#   KMPILOT_TEMPLATE_BRANCH Branch or tag to install from (default: latest release
#                           tag; set to "main" for the bleeding edge)
#   NO_COLOR                Set to any value to disable colored output.
#   KMPILOT_ASSUME_YES      Set to 1 to skip the confirmation prompt.

set -euo pipefail

TEMPLATE_REPO="${KMPILOT_TEMPLATE_REPO:-https://github.com/ThisIsSadeghi/KMPilot.git}"
# Resolved after the git check below: defaults to the latest vX.Y.Z release tag
# (reproducible installs); override with KMPILOT_TEMPLATE_BRANCH=main for bleeding edge.
TEMPLATE_BRANCH="${KMPILOT_TEMPLATE_BRANCH:-}"
# Stamped to a release tag (vX.Y.Z) by .github/workflows/release.yml when it uploads
# this file as a release asset — so a released installer clones the EXACT tag it shipped
# with (script and template tree are the same release; no drift). Left as the placeholder
# on main, so a raw-main run falls through to "resolve the latest published tag" below.
PINNED_TAG="__KMPILOT_PINNED_TAG__"

# ─────────────────────────────────────────────────────────────────────────────
# Presentation helpers (color, tty, logging)
# ─────────────────────────────────────────────────────────────────────────────

# Colors on only when stdout is a real terminal and NO_COLOR is unset. Under
# curl | bash, stdin is the script but stdout is still the terminal, so this
# stays true and the output is colored.
if [[ -t 1 && -z "${NO_COLOR:-}" && "${TERM:-}" != "dumb" ]]; then
    BOLD=$'\033[1m'; DIM=$'\033[2m'; RESET=$'\033[0m'
    RED=$'\033[31m'; GREEN=$'\033[32m'; YELLOW=$'\033[33m'
    BLUE=$'\033[34m'; MAGENTA=$'\033[35m'; CYAN=$'\033[36m'
    BORDER=$'\033[38;5;39m'   # vivid azure for the banner frame
else
    BOLD=""; DIM=""; RESET=""
    RED=""; GREEN=""; YELLOW=""; BLUE=""; MAGENTA=""; CYAN=""; BORDER=""
fi

# Interactive prompts read from the controlling terminal, not stdin — so the
# guided flow works even when the script itself arrives on stdin (curl | bash).
if [[ -r /dev/tty ]]; then TTY=/dev/tty; else TTY=""; fi

STEP_NO=0
step() {   # numbered top-level step
    STEP_NO=$((STEP_NO + 1))
    printf '%s%s[%s]%s %s%s\n' "$BOLD" "$CYAN" "$STEP_NO" "$RESET" "$BOLD" "$1$RESET"
}
substep() { printf '    %s%s›%s %s\n' "$DIM" "$BLUE" "$RESET" "$1"; }
ok()      { printf '    %s✓%s %s\n' "$GREEN" "$RESET" "$1"; }
warn()    { printf '    %s⚠%s %s\n' "$YELLOW" "$RESET" "$1"; }
die()     { printf '\n%s✗ %s%s\n' "$RED" "$1" "$RESET" >&2; exit 1; }

banner() {
    printf '\n'
    printf '%s%s  ╭───────────────────────────────────────────────╮%s\n' "$BOLD" "$BORDER" "$RESET"
    printf '%s%s  │%s  %s%sKMPilot%s  %s·%s  Kotlin Multiplatform scaffolder  %s%s│%s\n' \
        "$BOLD" "$BORDER" "$RESET" "$BOLD" "$CYAN" "$RESET" "$DIM" "$RESET" "$BOLD$BORDER" "" "$RESET"
    printf '%s%s  ╰───────────────────────────────────────────────╯%s\n' "$BOLD" "$BORDER" "$RESET"
    printf '%s     an Android + iOS app from a handful of Claude Code commands%s\n\n' "$DIM" "$RESET"
}

# Run a command with a braille spinner. Captures the child exit code safely
# under `set -e` (a bare failing `wait` would abort before we could read $?).
spinner() {
    local msg="$1"; shift
    if [[ ! -t 1 ]]; then          # non-terminal: no animation, just run
        "$@"; return $?
    fi
    "$@" &
    local pid=$! frames='⠋⠙⠹⠸⠼⠴⠦⠧⠇⠏' i=0 rc=0
    tput civis 2>/dev/null || true # hide cursor
    while kill -0 "$pid" 2>/dev/null; do
        printf '\r    %s%s%s %s' "$CYAN" "${frames:i++%${#frames}:1}" "$RESET" "$msg"
        sleep 0.08
    done
    if wait "$pid"; then rc=0; else rc=$?; fi
    tput cnorm 2>/dev/null || true # restore cursor
    printf '\r\033[K'              # clear the spinner line
    return $rc
}

# ─────────────────────────────────────────────────────────────────────────────
# Input: arguments, then guided prompts for anything missing
# ─────────────────────────────────────────────────────────────────────────────

command -v git >/dev/null 2>&1 || die "git is required but was not found on PATH."

NAME="${1:-}"
PKG_ARG="${2:-}"

# Validators (kept permissive but enough to avoid a broken rename downstream).
valid_name() { [[ "$1" =~ ^[A-Za-z][A-Za-z0-9]*$ ]]; }
valid_pkg()  { [[ "$1" =~ ^[a-z][a-z0-9]*(\.[a-z][a-z0-9]+)+$ ]]; }

prompt_name() {
    [[ -n "$TTY" ]] || die "No project name given and no terminal to prompt on.
Pass one:  install.sh <ProjectName> [package.prefix]"
    local ans
    while :; do
        printf '%s?%s %sProject name%s %s(PascalCase, e.g. MyStore)%s: ' \
            "$CYAN" "$RESET" "$BOLD" "$RESET" "$DIM" "$RESET" > /dev/tty
        read -r ans < "$TTY" || die "Aborted."
        if valid_name "$ans"; then NAME="$ans"; return; fi
        warn "Use letters and digits only, starting with a letter."
    done
}

prompt_pkg() {
    local default="dev.kmpilot.$(echo "$NAME" | tr '[:upper:]' '[:lower:]')"
    if [[ -n "$PKG_ARG" ]]; then PKG="$PKG_ARG"; return; fi
    if [[ -z "$TTY" ]]; then PKG="$default"; return; fi
    local ans
    while :; do
        printf '%s?%s %sPackage prefix%s %s[%s]%s: ' \
            "$CYAN" "$RESET" "$BOLD" "$RESET" "$DIM" "$default" "$RESET" > /dev/tty
        read -r ans < "$TTY" || die "Aborted."
        ans="${ans:-$default}"
        if valid_pkg "$ans"; then PKG="$ans"; return; fi
        warn "Use a dotted lowercase package, e.g. com.acme.store"
    done
}

confirm() {
    [[ "${KMPILOT_ASSUME_YES:-0}" == "1" ]] && return 0
    [[ -n "$TTY" ]] || return 0     # non-interactive: proceed (matches install.sh)
    local ans
    printf '\n    %sProceed?%s %s[Y/n]%s ' "$BOLD" "$RESET" "$DIM" "$RESET" > /dev/tty
    read -r ans < "$TTY" || die "Aborted."
    case "${ans:-y}" in [Yy]*|"") return 0 ;; *) die "Cancelled." ;; esac
}

banner

[[ -n "$NAME" ]] || prompt_name
valid_name "$NAME" || die "Invalid project name '$NAME' (letters and digits, start with a letter)."
prompt_pkg

if [[ -e "$NAME" ]]; then die "'$NAME' already exists in $(pwd)."; fi

# Resolve the install ref, in priority order:
#   1. KMPILOT_TEMPLATE_BRANCH — explicit override (e.g. =main for bleeding edge)
#   2. PINNED_TAG              — stamped into this script by the release workflow
#   3. newest published vX.Y.Z — for raw-main runs (unstamped script)
#   4. main                    — last resort when the repo has no tags yet
if [[ -z "$TEMPLATE_BRANCH" ]]; then
    if [[ "$PINNED_TAG" != '__KMPILOT_PINNED_TAG__' && -n "$PINNED_TAG" ]]; then
        TEMPLATE_BRANCH="$PINNED_TAG"
    else
        TEMPLATE_BRANCH="$(git ls-remote --tags --refs --sort=-v:refname "$TEMPLATE_REPO" 'v*' 2>/dev/null \
            | head -n1 | sed -E 's#.*refs/tags/##')"
        TEMPLATE_BRANCH="${TEMPLATE_BRANCH:-main}"
    fi
fi

# Recap what we're about to build.
printf '\n'
printf '    %sProject%s   %s%s%s\n'  "$DIM" "$RESET" "$BOLD" "$NAME" "$RESET"
printf '    %sPackage%s   %s\n'      "$DIM" "$RESET" "$PKG"
printf '    %sTemplate%s  %s %s(%s)%s\n' "$DIM" "$RESET" "$TEMPLATE_REPO" "$DIM" "$TEMPLATE_BRANCH" "$RESET"
printf '    %sTarget%s    %s/%s\n'   "$DIM" "$RESET" "$(pwd)" "$NAME"
confirm
printf '\n'

# Cross-platform sed -i (BSD/macOS vs GNU/Linux)
if [[ "$(uname -s)" == "Darwin" ]]; then
    sedi() { sed -i '' "$@"; }
else
    sedi() { sed -i "$@"; }
fi

# ─────────────────────────────────────────────────────────────────────────────
# Core template surgery — logic identical to install.sh, only the echoes restyled
# ─────────────────────────────────────────────────────────────────────────────

trim_template() {
    # Strip KMPilot's example features from the cloned target and write a
    # minimal Welcome screen so the empty shell still compiles and runs.
    # Runs BEFORE rename.sh so source files still use the template's
    # original identifiers (thisissadeghi.*) — rename.sh will rewrite the
    # new files along with everything else.
    #
    # NOTE: This trims the CLONE only. The KMPilot repo itself keeps
    # feature/dashboard/ (and any other sample features) as a working
    # reference implementation.

    # 1-3. Strip EVERY example feature module and its wiring. Generic on purpose:
    #       a hardcoded list silently drifts whenever a sample feature is added
    #       (it did — assetdetail/swap/profile shipped to fresh installs). Looping
    #       over feature/*/ keeps the clean-slate guarantee no matter what ships.
    local koin="composeApp/src/commonMain/kotlin/thisissadeghi/kmpilot/initKoin.kt"
    if [[ -d feature ]]; then
        for fdir in feature/*/; do
            # Only real modules (have a build.gradle.kts); skips gradle's build/ dir.
            [[ -f "${fdir}build.gradle.kts" ]] || continue
            local fname
            fname="$(basename "$fdir")"
            rm -rf "$fdir"
            sedi "/include(\":feature:${fname}\")/d"            settings.gradle.kts
            sedi "/project(\":feature:${fname}\")/d"            composeApp/build.gradle.kts
            sedi "/\"kover\"(project(\":feature:${fname}\"))/d" build.gradle.kts
            sedi "/import thisissadeghi\\.${fname}\\./d"        "$koin"
            sedi "/^[[:space:]]*${fname}Module,/d"              "$koin"
        done
    fi

    # 3a. Replace the mock-API BASE_URL with a neutral placeholder. The template
    #     ships KMPilot's own mock API URL, which rename.sh would otherwise
    #     mangle (it rewrites every occurrence of `thisissadeghi` and `KMPilot`
    #     across the tree, producing a malformed URL in the new project).
    sedi 's|https://thisissadeghi\.github\.io/KMPilot/mock-api/|https://api.example.com/|g' \
        composeApp/build.gradle.kts

    # 4. Write a Welcome screen so the empty shell compiles and runs.
    #    User deletes this file when adding their first feature.
    cat > composeApp/src/commonMain/kotlin/thisissadeghi/kmpilot/WelcomeScreen.kt <<'WELCOME_EOF'
package thisissadeghi.kmpilot

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.serialization.Serializable

@Serializable
object WelcomeRoute

@Composable
fun WelcomeScreen() {
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background,
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 32.dp, vertical = 56.dp),
            contentAlignment = Alignment.TopCenter,
        ) {
            Column(modifier = Modifier.widthIn(max = 560.dp)) {
                EyebrowLabel("// STARTER")
                Spacer(Modifier.height(16.dp))
                Text(
                    text = "Welcome to your new app.",
                    style = MaterialTheme.typography.displaySmall,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.onBackground,
                )
                Spacer(Modifier.height(12.dp))
                Text(
                    text = "This is a starter screen. Replace it with your first feature — the scaffolding agent takes care of the wiring.",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )

                Spacer(Modifier.height(56.dp))
                EyebrowLabel("CHOOSE A PATH  ·  NOT BOTH")
                Spacer(Modifier.height(20.dp))

                PathCard(
                    index = "01",
                    title = "Code-first",
                    description = "Skip the design step and scaffold straight into Kotlin.",
                    commands = listOf("/create-feature"),
                )
                Spacer(Modifier.height(12.dp))
                PathCard(
                    index = "02",
                    title = "Design-first",
                    description = "Design your screens in Stitch, then let the blueprint drive the scaffold.",
                    commands = listOf("/design-ui", "/create-feature"),
                )

                Spacer(Modifier.height(56.dp))
                EyebrowLabel("IN YOUR TOOLBOX")
                Spacer(Modifier.height(16.dp))

                CommandRow("/modify-feature", "Change an existing feature")
                CommandRow("/test-feature", "Generate the test suite")
                CommandRow("/review-feature", "Audit the architecture")
                CommandRow("/verify-ui", "Compare implementation against Stitch design")

                Spacer(Modifier.height(40.dp))
                HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
                Spacer(Modifier.height(16.dp))
                Text(
                    text = "Run any command inside Claude Code. The scaffolding agent removes this screen as soon as your first feature is wired in.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }
    }
}

@Composable
private fun EyebrowLabel(text: String) {
    Text(
        text = text,
        style = MaterialTheme.typography.labelSmall,
        fontFamily = FontFamily.Monospace,
        letterSpacing = 2.sp,
        color = MaterialTheme.colorScheme.primary,
    )
}

@Composable
private fun PathCard(
    index: String,
    title: String,
    description: String,
    commands: List<String>,
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = MaterialTheme.colorScheme.surface,
        tonalElevation = 1.dp,
        shape = RoundedCornerShape(12.dp),
    ) {
        Row(
            modifier = Modifier.padding(20.dp),
            horizontalArrangement = Arrangement.spacedBy(20.dp),
        ) {
            Text(
                text = index,
                style = MaterialTheme.typography.titleMedium,
                fontFamily = FontFamily.Monospace,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.primary,
            )
            Column(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface,
                )
                Spacer(Modifier.height(6.dp))
                Text(
                    text = description,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
                Spacer(Modifier.height(14.dp))
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    commands.forEachIndexed { i, cmd ->
                        if (i > 0) {
                            Text(
                                text = "→",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                            )
                        }
                        CodeChip(cmd)
                    }
                }
            }
        }
    }
}

@Composable
private fun CodeChip(text: String) {
    Surface(
        color = MaterialTheme.colorScheme.surfaceVariant,
        shape = RoundedCornerShape(6.dp),
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.bodyMedium,
            fontFamily = FontFamily.Monospace,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
        )
    }
}

@Composable
private fun CommandRow(command: String, description: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(20.dp),
        verticalAlignment = Alignment.Top,
    ) {
        Box(modifier = Modifier.widthIn(min = 220.dp)) {
            CodeChip(command)
        }
        Text(
            text = description,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(top = 4.dp),
        )
    }
}
WELCOME_EOF

    # 5. Replace nav host with a Welcome-only version. The integration
    #    agent in /create-feature swaps this for the first feature's
    #    route (and deletes WelcomeScreen.kt) — see integrator.md.
    cat > composeApp/src/commonMain/kotlin/thisissadeghi/kmpilot/BaseAppNavHost.kt <<'NAVHOST_EOF'
package thisissadeghi.kmpilot

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import thisissadeghi.designsystem.XNavHost

/**
 * Main app navigation host.
 * Routes to WelcomeScreen until the first feature is wired in.
 */
@Composable
fun BaseAppNavHost(modifier: Modifier) {
    val navController = rememberNavController()

    XNavHost(
        modifier = modifier,
        navController = navController,
        startDestination = WelcomeRoute,
    ) {
        composable<WelcomeRoute> { WelcomeScreen() }
    }
}
NAVHOST_EOF

    # 6. Strip KMPilot's own dev history. Skills recreate
    #    .claude/docs/{feature}/ as needed.
    rm -rf .claude/docs
    rm -f  .claude/settings.local.json

    # 7. Stray macOS metadata
    find . -name ".DS_Store" -type f -delete 2>/dev/null || true

    # 8. KMPilot-only project files (upstream meta + sample mock data, not
    #    wanted in a user project). LICENSE/CONTRIBUTING are KMPilot's own;
    #    mock-api/finance only fed the (now-removed) dashboard sample, and
    #    .github/workflows/pages.yml only existed to publish that mock data;
    #    assets/ held KMPilot README screenshots; the .pbxproj.backup is a
    #    stray Xcode backup that rename.sh's *.pbxproj glob does not rewrite.
    rm -f  CONTRIBUTING.md LICENSE
    rm -rf .github assets mock-api
    rm -f  iosApp/iosApp.xcodeproj/project.pbxproj.backup

    # 9. Neutralize the design system's `app/` tier. `:core:designsystem` is split into
    #    generic primitives (XButton, XText, Placeholder, ItemPickerModal, …) that ship to every
    #    project, and a `designsystem.app` package holding the project's own composed UI.
    #    AppLoadingState/AppErrorState (in `designsystem.app`) are intentionally content-free
    #    (copy + navigation are caller parameters), so they are KEPT as-is — downstream redesigns
    #    them via the design pipeline. Project composites in `designsystem.app` (e.g. MoneyText)
    #    are project/example content and are stripped in 9b.
    #    The generic `designsystem/motion/` package (XMotion + rememberReducedMotion expect/actual,
    #    Modifier.shimmer, PulseDot, AmbientMeshBackground, BokehCanvas, Modifier.pulseGlow,
    #    RevealOnAppear) is brand-neutral generic-tier and is KEPT (not stripped) — the motion
    #    pipeline reuses it; per-feature motion is generated on demand.
    local ds="core/designsystem/src/commonMain"
    # 9a. Replace KMPilot's logotype (referenced by the generic XTopLogo) with a neutral mark.
    cat > "$ds/composeResources/drawable/app_logo_type.xml" <<'LOGO_EOF'
<vector xmlns:android="http://schemas.android.com/apk/res/android"
    android:width="44dp"
    android:height="44dp"
    android:viewportWidth="24"
    android:viewportHeight="24">
    <path
        android:fillColor="#9E9E9E"
        android:pathData="M12,2 A10,10 0 1 0 12,22 A10,10 0 1 0 12,2 Z" />
</vector>
LOGO_EOF

    # 9b. Strip the project example `app/` tiers across the core library modules. The `app/`
    #     package in each module is the project's own example/domain layer — generic code never
    #     imports it (the boundary rule), so removing it yields a clean, generic-only template:
    #       • common.app       — project/example value types (e.g. Money/Currency)
    #       • designsystem.app — project composites (e.g. MoneyText); the content-free App* state
    #                            screens are KEPT (see 9 above)
    #       • data.app         — project/domain persisted + shared-remote data (empty in the
    #                            baseline template; may be populated by a real project)
    #     Guards are no-ops when a tier is absent, so this is safe whether or not `data.app` exists.
    rm -rf core/common/src/commonMain/kotlin/thisissadeghi/common/app
    find core/designsystem/src/commonMain/kotlin/thisissadeghi/designsystem/app -name '*.kt' \
        ! -name 'AppErrorState.kt' ! -name 'AppLoadingState.kt' -delete 2>/dev/null || true
    rm -rf core/data/src/commonMain/kotlin/thisissadeghi/data/app
    # Drop the data.app DI strip seam (import + includes entry) — no-op if data.app never existed.
    [ -f core/data/src/commonMain/kotlin/thisissadeghi/data/di/DataModules.kt ] && \
        sedi '/appDataModule/d' core/data/src/commonMain/kotlin/thisissadeghi/data/di/DataModules.kt || true

    # 9c. Reset the KEPT `App*` state screens to NEUTRAL defaults. `designsystem.app` keeps the
    #     AppLoadingState/AppErrorState *contracts* (the signatures every feature calls), but their
    #     bodies in the template are KMPilot's own design — overwrite them with generic baselines so
    #     a fresh project ships a plain spinner / plain error layout (no branded illustration), then
    #     redesigns via the design pipeline. The branded drawables they no longer use
    #     (failed_background, warning) are left in place but orphaned — harmless, and available to
    #     the redesign.
    local app="$ds/kotlin/thisissadeghi/designsystem/app"
    cat > "$app/AppLoadingState.kt" <<'APPLOADING_EOF'
package thisissadeghi.designsystem.app

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import thisissadeghi.designsystem.XCircularProgressIndicator

/**
 * Shared, project-level **Loading** state — the neutral default written on install. Reused by every
 * feature for Rule 4's `UiState.Loading`. Redesign per project via the design pipeline. Built only
 * from generic primitives ([XCircularProgressIndicator]); never imported by generic design-system code.
 */
@Composable
fun AppLoadingState(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center,
    ) {
        XCircularProgressIndicator()
    }
}
APPLOADING_EOF
    cat > "$app/AppErrorState.kt" <<'APPERROR_EOF'
package thisissadeghi.designsystem.app

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import org.jetbrains.compose.resources.stringResource
import thisissadeghi.designsystem.DesignSystemResources
import thisissadeghi.designsystem.XButton
import thisissadeghi.designsystem.XText

/**
 * Shared, project-level **Failed** state — the neutral default written on install. Reused by every
 * feature for Rule 4's `UiState.Failed`. Copy and navigation are **parameters** ([title]/[message]
 * from the feature's own strings, [onRetry] the primary action, [retryLabel] defaulting to the
 * shared label, [secondaryAction] an optional nav slot), so nothing app-specific is baked in.
 * Redesign per project via the design pipeline. Built only from generic primitives ([XButton],
 * [XText]); never imported by generic design-system code.
 */
@Composable
fun AppErrorState(
    title: String,
    message: String,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier,
    retryLabel: String = stringResource(DesignSystemResources.string.retry_label),
    secondaryAction: (@Composable () -> Unit)? = null,
) {
    Column(
        modifier = modifier.fillMaxSize().padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        XText(
            text = title,
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.onSurface,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(bottom = 8.dp),
        )
        XText(
            text = message,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center,
            modifier = Modifier.widthIn(max = 320.dp).padding(bottom = 24.dp),
        )
        XButton(onClick = onRetry) {
            XText(text = retryLabel)
        }
        secondaryAction?.invoke()
    }
}
APPERROR_EOF
}

write_fresh_docs() {
    # Replace KMPilot's own README + CHANGELOG (upstream marketing / release history) with
    # a minimal project README and an empty changelog, so the new project owns its docs
    # instead of inheriting the template's. Runs AFTER rename.sh so the upstream KMPilot
    # credit/links written below are NOT rewritten by the rename. Uses a quoted heredoc
    # (literal) + a placeholder sed so the code fences and links stay intact.
    cat > README.md <<'README_EOF'
# __PROJECT_NAME__

A Kotlin Multiplatform + Compose Multiplatform app, generated from
[KMPilot](https://github.com/ThisIsSadeghi/KMPilot).

## Build

```bash
./gradlew assembleDebug          # Android
# open iosApp/iosApp.xcodeproj in Xcode for iOS
```

## Build features with Claude Code

Run the scaffolding commands inside [Claude Code](https://claude.ai/code):

```
/create-feature            # scaffold a new feature
/modify-feature           # change an existing one
/test-feature                    # generate its test suite
/review-feature                  # audit the architecture
```

## Staying up to date

Pull newer KMPilot releases without touching your code:

```bash
./update.sh            # tooling only (.claude skills/agents/hooks, CLAUDE.md, gradle wrapper)
./update.sh --core     # also merge core/ modules (rename-aware; conflicts surfaced, never silent)
./update.sh --dry-run  # preview what would change; writes nothing
```

Release notes live in the upstream
[CHANGELOG](https://github.com/ThisIsSadeghi/KMPilot/blob/main/CHANGELOG.md).
README_EOF
    sedi "s/__PROJECT_NAME__/${NAME}/g" README.md

    cat > CHANGELOG.md <<'CHANGELOG_EOF'
# Changelog

All notable changes to this project are documented here.

## [Unreleased]
CHANGELOG_EOF
}

write_manifest() {
    # Records identity + installed version so update.sh can later diff against
    # upstream and re-apply the package rename. Written AFTER rename (so the
    # upstream identifiers below are NOT rewritten) and committed in the initial
    # commit. Keystone artifact — without it, update.sh has no baseline or pkg.
    # Prefer the resolved release tag — it is authoritative, and update.sh diffs from
    # v$version, so the manifest MUST match the tag actually cloned. Fall back to the
    # VERSION file only for bleeding-edge (main) installs that aren't on a tag.
    local version
    if [[ "$TEMPLATE_BRANCH" =~ ^v[0-9]+\.[0-9]+\.[0-9]+ ]]; then
        version="${TEMPLATE_BRANCH#v}"
    elif [[ -f VERSION ]]; then
        version="$(tr -d '[:space:]' < VERSION)"
    else
        version="unknown"
    fi
    local now
    now="$(date -u +%Y-%m-%dT%H:%M:%SZ)"
    cat > .kmpilot.json <<MANIFEST_EOF
{
  "kmpilotVersion": "${version}",
  "projectName": "${NAME}",
  "packagePrefix": "${PKG}",
  "templateRepo": "${TEMPLATE_REPO}",
  "upstreamPkg": "thisissadeghi",
  "upstreamName": "KMPilot",
  "installedAt": "${now}"
}
MANIFEST_EOF
    ok "Wrote .kmpilot.json (version ${version}, package ${PKG})"
}

# ─────────────────────────────────────────────────────────────────────────────
# Orchestration
# ─────────────────────────────────────────────────────────────────────────────

step "Cloning KMPilot template"
substep "ref: ${BOLD}${TEMPLATE_BRANCH}${RESET}"
spinner "Cloning into ${NAME}/ ..." \
    git -c advice.detachedHead=false clone --depth=1 --branch "$TEMPLATE_BRANCH" --quiet "$TEMPLATE_REPO" "$NAME" \
    || die "Clone failed. Check the repo URL / ref and your network."
ok "Cloned into ${BOLD}${NAME}/${RESET}"

cd "$NAME"
rm -rf .git

step "Trimming to a fresh project shell"
trim_template
ok "Example features removed, Welcome screen written"

step "Renaming project to ${BOLD}${NAME}${RESET}"
substep "package ${PKG}"
# rename.sh is chatty; capture its output so it doesn't break the styled log,
# and surface it only if the rename fails.
RENAME_LOG="$(mktemp)"
if spinner "Rewriting identifiers ..." \
        bash -c 'bash scripts/rename.sh --name="$1" --pkg="$2" >"$3" 2>&1' _ "$NAME" "$PKG" "$RENAME_LOG"; then
    rm -f "$RENAME_LOG"
    ok "Identifiers rewritten"
else
    cat "$RENAME_LOG" >&2; rm -f "$RENAME_LOG"; die "rename.sh failed."
fi

# Template-only files we don't need in a user project. scripts/rename.sh is a
# one-shot installer tool (it has already run above and still embeds KMPilot's
# OLD identifiers); scripts/ is empty once it's gone. update.sh is KEPT — it is
# the downstream's entrypoint for pulling future releases. install.sh is the
# installer itself; already run, and not wanted in the generated project.
rm -f install.sh
rm -rf scripts

# iOS: bootstrap CocoaPods so Xcode can build out of the box. macOS only —
# pod install is required before the first iOS build because the Xcode project
# has a [CP] Check Pods Manifest.lock build phase that aborts the build until
# Pods/ and Podfile.lock exist.
if [[ "$(uname -s)" == "Darwin" ]]; then
    step "iOS setup"
    if command -v pod >/dev/null 2>&1; then
        if spinner "Running pod install ..." bash -c 'cd iosApp && pod install >/dev/null 2>&1'; then
            ok "Pods installed"
        else
            warn "pod install failed — run 'cd $NAME/iosApp && pod install' manually"
            substep "Common fix: brew install cocoapods"
        fi
    else
        warn "CocoaPods not installed — skipping pod install"
        substep "For iOS builds: brew install cocoapods && cd $NAME/iosApp && pod install"
    fi
fi

# Replace the template's README/CHANGELOG with fresh project docs (post-rename, so the
# upstream KMPilot credit links survive), then stamp the update manifest, then drop the
# upstream VERSION file — kmpilotVersion now lives in .kmpilot.json.
step "Writing project docs + manifest"
write_fresh_docs
write_manifest
rm -f VERSION
ok "README.md + CHANGELOG.md written"

step "Initializing git repository"
git init --quiet
git add -A
git -c user.email=kmpilot@local -c user.name=kmpilot commit --quiet \
    -m "Initial commit from KMPilot template"
ok "Initial commit created"

# ─────────────────────────────────────────────────────────────────────────────
# Done
# ─────────────────────────────────────────────────────────────────────────────
printf '\n'
printf '%s%s  ✓ %s is ready%s  %s%s\n' "$BOLD" "$GREEN" "$NAME" "$RESET" "$DIM" "$RESET"
printf '%s    %s%s\n\n' "$DIM" "$(pwd)$RESET" ""
printf '  %sNext%s\n' "$BOLD" "$RESET"
printf '    %s$%s cd %s\n'   "$DIM" "$RESET" "$NAME"
printf '    %s$%s claude\n'  "$DIM" "$RESET"
printf '\n'
printf '  %sthen try%s  %s/design-ui%s  or  %s/create-feature%s\n\n' \
    "$DIM" "$RESET" "$CYAN" "$RESET" "$CYAN" "$RESET"
