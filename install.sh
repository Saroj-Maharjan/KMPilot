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

echo "→ Cloning KMPilot template (branch: $TEMPLATE_BRANCH) into $NAME/"
git clone --depth=1 --branch "$TEMPLATE_BRANCH" --quiet "$TEMPLATE_REPO" "$NAME"

cd "$NAME"
rm -rf .git

echo "→ Renaming project..."
bash scripts/rename.sh --name="$NAME" --pkg="$PKG"

# Template-only files we don't need in a user project
rm -f install.sh

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
