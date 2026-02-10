#!/bin/bash
# protect-feature-files.sh
# Blocks direct edits to feature/ files unless a skill is active.
# Used as a PreToolUse hook on Edit|Write tool calls.

INPUT=$(cat)
FILE_PATH=$(echo "$INPUT" | jq -r '.tool_input.file_path // empty')

# Only check files under feature/ directories
if [[ "$FILE_PATH" == *"/feature/"* ]] || [[ "$FILE_PATH" == feature/* ]]; then
  # Allow test files (commonTest) - test agents write these directly
  if [[ "$FILE_PATH" == *"/commonTest/"* ]] || [[ "$FILE_PATH" == *"/test/"* ]]; then
    exit 0
  fi

  # Allow build.gradle.kts edits (test dependency setup, integration agent)
  if [[ "$FILE_PATH" == *"build.gradle.kts"* ]]; then
    exit 0
  fi

  # Block direct feature source edits - must use skills
  echo "Blocked: Cannot edit feature source files directly. Use /creating-kmp-feature or /modifying-kmp-feature skill first." >&2
  exit 2
fi

exit 0
