#!/usr/bin/env bash
# Bootstrap a new KMPilot project.
#
# Usage (remote):
#   curl -fsSL https://kmpilot.dev/install.sh | sh -s <ProjectName> [package.prefix]
#
# Usage (local):
#   ./install.sh <ProjectName> [package.prefix]
#
# Env vars:
#   KMPILOT_TEMPLATE_REPO   Git URL of the template (default: ThisIsSadeghi/KMPilot)
#   KMPILOT_TEMPLATE_BRANCH Branch or tag to install from (default: main)

set -euo pipefail

TEMPLATE_REPO="${KMPILOT_TEMPLATE_REPO:-https://github.com/ThisIsSadeghi/KMPilot.git}"
TEMPLATE_BRANCH="${KMPILOT_TEMPLATE_BRANCH:-main}"

if [[ $# -lt 1 ]]; then
    cat >&2 <<USAGE
Usage: install.sh <ProjectName> [package.prefix]

Arguments:
  ProjectName      PascalCase display name (e.g., MyStore)
  package.prefix   Optional dotted package (defaults to dev.kmpilot.<lower>)

Examples:
  install.sh MyStore
  install.sh MyStore com.acme.store
USAGE
    exit 1
fi

NAME="$1"
PKG="${2:-dev.kmpilot.$(echo "$NAME" | tr '[:upper:]' '[:lower:]')}"

command -v git >/dev/null 2>&1 || { echo "Error: git is required" >&2; exit 1; }

if [[ -e "$NAME" ]]; then
    echo "Error: '$NAME' already exists in $(pwd)" >&2
    exit 1
fi

# Cross-platform sed -i (BSD/macOS vs GNU/Linux)
if [[ "$(uname -s)" == "Darwin" ]]; then
    sedi() { sed -i '' "$@"; }
else
    sedi() { sed -i "$@"; }
fi

trim_template() {
    # Strip send/receive features and KMPilot-specific artifacts from the
    # cloned target. Runs BEFORE rename.sh so source files still use the
    # template's original identifiers (thisissadeghi.*) — rename.sh will
    # rewrite the slimmed nav host along with everything else.
    echo "→ Trimming template to dashboard-only sample..."

    # 1. Remove send/receive feature modules
    rm -rf feature/send feature/receive

    # 2. Drop send/receive from gradle wiring
    sedi '/include(":feature:send")/d'    settings.gradle.kts
    sedi '/include(":feature:receive")/d' settings.gradle.kts
    sedi '/project(":feature:send")/d'    composeApp/build.gradle.kts
    sedi '/project(":feature:receive")/d' composeApp/build.gradle.kts

    # 3. Drop send/receive DI init
    local koin="composeApp/src/commonMain/kotlin/thisissadeghi/kmpilot/initKoin.kt"
    sedi '/import thisissadeghi\.send\./d'    "$koin"
    sedi '/import thisissadeghi\.receive\./d' "$koin"
    sedi '/SendModules\.initialize()/d'       "$koin"
    sedi '/ReceiveModules\.initialize()/d'    "$koin"

    # 4. Replace nav host with a dashboard-only version. The dashboard's
    #    QuickAction mock data still emits "send"/"receive" string IDs
    #    (same as "pay"/"topup", which never had destinations) — the
    #    placeholder onActionClick shows where to wire navigation for
    #    new features you build.
    cat > composeApp/src/commonMain/kotlin/thisissadeghi/kmpilot/BaseAppNavHost.kt <<'NAVHOST_EOF'
package thisissadeghi.kmpilot

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.rememberNavController
import thisissadeghi.designsystem.XNavHost
import thisissadeghi.dashboard.presentation.navigation.DashboardRoute
import thisissadeghi.dashboard.presentation.navigation.dashboard

/**
 * Main app navigation host
 */
@Composable
fun BaseAppNavHost(modifier: Modifier) {
    val navController = rememberNavController()

    XNavHost(
        modifier = modifier,
        navController = navController,
        startDestination = DashboardRoute,
    ) {
        dashboard(
            onActionClick = { actionId ->
                // Wire navigation for each quick action here, e.g.:
                //   if (actionId == "send") navController.navigate(SendRoute)
            },
            onBackToDashboard = {
                navController.popBackStack(DashboardRoute, inclusive = false)
            },
        )
    }
}
NAVHOST_EOF

    # 5. Strip KMPilot-specific .claude artifacts so the new project
    #    starts with clean docs (dashboard/spec.md is kept as an example).
    rm -rf .claude/docs/send .claude/docs/receive
    rm -rf .claude/docs/_shared/designs
    rm -rf .claude/docs/dashboard/designs
    rm -f  .claude/docs/dashboard/dashboard_audit.md \
           .claude/docs/dashboard/dashboard_blueprint.md \
           .claude/docs/dashboard/dashboard.md \
           .claude/docs/dashboard/dashboard.png
    rm -f  .claude/docs/_project/stitch-project.json
    rm -f  .claude/settings.local.json

    # 6. Stray macOS metadata
    find . -name ".DS_Store" -type f -delete 2>/dev/null || true
}

echo "→ Cloning KMPilot template (branch: $TEMPLATE_BRANCH) into $NAME/"
git clone --depth=1 --branch "$TEMPLATE_BRANCH" --quiet "$TEMPLATE_REPO" "$NAME"

cd "$NAME"
rm -rf .git

trim_template

echo "→ Renaming project..."
bash scripts/rename.sh --name="$NAME" --pkg="$PKG"

# Template-only files we don't need in a user project
rm -f install.sh

# iOS: bootstrap CocoaPods so Xcode can build out of the box. macOS only —
# pod install is required before the first iOS build because the Xcode project
# has a [CP] Check Pods Manifest.lock build phase that aborts the build until
# Pods/ and Podfile.lock exist.
if [[ "$(uname -s)" == "Darwin" ]]; then
    if command -v pod >/dev/null 2>&1; then
        echo "→ Running pod install in iosApp/..."
        if (cd iosApp && pod install >/dev/null 2>&1); then
            echo "  ✓ Pods installed"
        else
            echo "  ⚠ pod install failed — run 'cd $NAME/iosApp && pod install' manually"
            echo "    Common fix: brew install cocoapods"
        fi
    else
        echo "→ Skipping pod install (CocoaPods not installed)"
        echo "  For iOS builds: brew install cocoapods && cd $NAME/iosApp && pod install"
    fi
fi

echo "→ Initializing fresh git repository..."
git init --quiet
git add -A
git -c user.email=kmpilot@local -c user.name=kmpilot commit --quiet \
    -m "Initial commit from KMPilot template"

echo ""
echo "✓ '$NAME' ready at $(pwd)"
echo ""
echo "Next:"
echo "  cd $NAME"
echo "  claude"
