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
# Installs from the latest published vX.Y.Z release tag by default (reproducible).
# The new project keeps a ./update.sh you can run later to pull future releases
# without clobbering your code (see update.sh).
#
# Env vars:
#   KMPILOT_TEMPLATE_REPO   Git URL of the template (default: ThisIsSadeghi/KMPilot)
#   KMPILOT_TEMPLATE_BRANCH Branch or tag to install from (default: latest release
#                           tag; set to "main" for the bleeding edge)

set -euo pipefail

TEMPLATE_REPO="${KMPILOT_TEMPLATE_REPO:-https://github.com/ThisIsSadeghi/KMPilot.git}"
# Resolved after the git check below: defaults to the latest vX.Y.Z release tag
# (reproducible installs); override with KMPILOT_TEMPLATE_BRANCH=main for bleeding edge.
TEMPLATE_BRANCH="${KMPILOT_TEMPLATE_BRANCH:-}"

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

# Pick the install ref: explicit override wins; otherwise the newest published
# vX.Y.Z tag (so installs are pinned to a release); fall back to main if untagged.
if [[ -z "$TEMPLATE_BRANCH" ]]; then
    TEMPLATE_BRANCH="$(git ls-remote --tags --refs --sort=-v:refname "$TEMPLATE_REPO" 'v*' 2>/dev/null \
        | head -n1 | sed -E 's#.*refs/tags/##')"
    TEMPLATE_BRANCH="${TEMPLATE_BRANCH:-main}"
fi

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

write_manifest() {
    # Records identity + installed version so update.sh can later diff against
    # upstream and re-apply the package rename. Written AFTER rename (so the
    # upstream identifiers below are NOT rewritten) and committed in the initial
    # commit. Keystone artifact — without it, update.sh has no baseline or pkg.
    local version="unknown"
    [[ -f VERSION ]] && version="$(tr -d '[:space:]' < VERSION)"
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
    echo "→ Wrote .kmpilot.json (version ${version}, package ${PKG})"
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
# OLD identifiers); scripts/ is empty once it's gone. update.sh is KEPT — it is
# the downstream's entrypoint for pulling future releases.
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

# Stamp the update manifest (reads VERSION, so do it before dropping VERSION),
# then remove the upstream VERSION file — kmpilotVersion now lives in .kmpilot.json.
write_manifest
rm -f VERSION

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
