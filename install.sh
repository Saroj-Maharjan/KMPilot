#!/usr/bin/env bash
# Bootstrap a new KMPilot project.
#
# Usage (remote):
#   curl -fsSL https://raw.githubusercontent.com/ThisIsSadeghi/KMPilot/main/install.sh \
#     | sh -s <ProjectName> [package.prefix]
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
    # Strip KMPilot's example features from the cloned target and write a
    # minimal Welcome screen so the empty shell still compiles and runs.
    # Runs BEFORE rename.sh so source files still use the template's
    # original identifiers (thisissadeghi.*) — rename.sh will rewrite the
    # new files along with everything else.
    #
    # NOTE: This trims the CLONE only. The KMPilot repo itself keeps
    # feature/dashboard/ (and any other sample features) as a working
    # reference implementation.
    echo "→ Trimming template to a fresh project shell..."

    # 1. Remove example feature modules from the cloned target
    rm -rf feature/dashboard feature/send feature/receive

    # 2. Drop feature gradle wiring
    sedi '/include(":feature:dashboard")/d' settings.gradle.kts
    sedi '/include(":feature:send")/d'      settings.gradle.kts
    sedi '/include(":feature:receive")/d'   settings.gradle.kts
    sedi '/project(":feature:dashboard")/d' composeApp/build.gradle.kts
    sedi '/project(":feature:send")/d'      composeApp/build.gradle.kts
    sedi '/project(":feature:receive")/d'   composeApp/build.gradle.kts
    # Root build.gradle.kts holds kover-aggregation deps per feature
    sedi '/"kover"(project(":feature:dashboard"))/d' build.gradle.kts
    sedi '/"kover"(project(":feature:send"))/d'      build.gradle.kts
    sedi '/"kover"(project(":feature:receive"))/d'   build.gradle.kts

    # 3. Drop feature DI init
    local koin="composeApp/src/commonMain/kotlin/thisissadeghi/kmpilot/initKoin.kt"
    sedi '/import thisissadeghi\.dashboard\./d' "$koin"
    sedi '/import thisissadeghi\.send\./d'      "$koin"
    sedi '/import thisissadeghi\.receive\./d'   "$koin"
    sedi '/DashboardModules\.initialize()/d'    "$koin"
    sedi '/SendModules\.initialize()/d'         "$koin"
    sedi '/ReceiveModules\.initialize()/d'      "$koin"

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
                    commands = listOf("/creating-kmp-feature"),
                )
                Spacer(Modifier.height(12.dp))
                PathCard(
                    index = "02",
                    title = "Design-first",
                    description = "Design your screens in Stitch, then let the blueprint drive the scaffold.",
                    commands = listOf("/ui-designer", "/creating-kmp-feature"),
                )

                Spacer(Modifier.height(56.dp))
                EyebrowLabel("IN YOUR TOOLBOX")
                Spacer(Modifier.height(16.dp))

                CommandRow("/modifying-kmp-feature", "Change an existing feature")
                CommandRow("/feature-test", "Generate the test suite")
                CommandRow("/feature-review", "Audit the architecture")
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
    #    agent in /creating-kmp-feature swaps this for the first feature's
    #    route (and deletes WelcomeScreen.kt) — see kmp-integration-agent.md.
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
}

echo "→ Cloning KMPilot template (branch: $TEMPLATE_BRANCH) into $NAME/"
git clone --depth=1 --branch "$TEMPLATE_BRANCH" --quiet "$TEMPLATE_REPO" "$NAME"

cd "$NAME"
rm -rf .git

trim_template

echo "→ Renaming project..."
bash scripts/rename.sh --name="$NAME" --pkg="$PKG"

# Template-only files we don't need in a user project. scripts/rename.sh is a
# one-shot installer tool (it has already run above and still embeds KMPilot's
# OLD identifiers); scripts/ is empty once it's gone.
rm -f install.sh
rm -rf scripts

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
