# Phase 3: Visual Verification

**Purpose**: Run the app on an Android device, navigate to the designed screen, capture a screenshot, and compare it with the Stitch design. If mismatch, prepare fix context and re-invoke the implementation skill.

**Prerequisites**: Phase 2 complete. Implementation finished. Mobile MCP available.

---

## Checklist

```
Verification Progress:
- [ ] Step 3.1: Build and install app
- [ ] Step 3.2: Navigate to designed screen
- [ ] Step 3.3: Capture device screenshot
- [ ] Step 3.4: Load Stitch design reference
- [ ] Step 3.5: Visual comparison
- [ ] Step 3.6: Handle mismatch (if <90%)
- [ ] Step 3.7: User final confirmation
```

---

## Step 3.1: Build and Install App

Build the debug APK and install on the connected device/emulator:

```bash
./gradlew assembleDebug
```

Then use the mobile MCP to install and launch the app. The exact tool names depend on the mobile MCP server configuration (typically `mcp__claude_in_mobile__*`). **Note**: Mobile MCP tools are not pre-listed in this skill's allowed-tools (names are dynamic) — the user must manually approve each mobile MCP tool call at runtime.

Expected mobile MCP capabilities:
- Install APK on device
- Launch app
- Navigate within the app
- Take screenshots
- Interact with UI elements (tap, scroll, swipe)

If the mobile MCP tools are not matching expected names, list available MCP tools and find the appropriate ones for:
1. Installing the app
2. Launching the app
3. Navigating to screens
4. Taking screenshots

---

## Step 3.2: Navigate to Designed Screen

Using the mobile MCP, navigate to the screen that was designed in Stitch:

1. **Launch the app** on the device
2. **Navigate** to the feature's screen using the app's navigation
   - If the screen is deep in the navigation hierarchy, navigate step by step
   - Use tap interactions to reach the target screen
3. **Wait** for the screen to fully render (loading state should complete)
4. **Ensure** the screen is in the Success state (with data displayed)

### Navigation Path

Determine the navigation path from the feature's route:
- Route: `{Feature}Route` defined in `{Feature}Navigation.kt`
- The screen may need to be triggered from the main navigation

If direct navigation is not possible through the app UI, consider:
- Adding a temporary deep link for testing
- Navigating through the app's normal flow

---

## Step 3.3: Capture Device Screenshot

Use the mobile MCP to take a screenshot of the current screen.

Save the screenshot to:
```
.claude/docs/{featurename}/designs/device/device_{screen_name}.png
```

Ensure the screenshot:
- Captures the full screen content
- Is taken after all animations/loading complete
- Shows the Success state (main content)
- Has the same orientation as the Stitch design (portrait/landscape)

---

## Step 3.4: Load Stitch Design Reference

Load the reference design for comparison:

1. **Design screenshot**: Read `.claude/docs/{featurename}/designs/{featurename}.png` (success state, via vision)
2. **Design description**: Read `.claude/docs/{featurename}/designs/{featurename}.md`

The primary comparison should be between:
- **Reference**: Approved design screenshot (`.claude/docs/{featurename}/designs/{featurename}.png`)
- **Actual**: Device screenshot (`.claude/docs/{featurename}/designs/device/device_{featurename}.png`)

---

## Step 3.5: Visual Comparison

Use Claude's vision capabilities to compare the two screenshots side-by-side.

### Comparison Criteria

Evaluate these aspects (weighted):

| Aspect | Weight | Description |
|--------|--------|-------------|
| Layout | 30% | Overall structure, positioning, spacing, alignment |
| Colors | 25% | Background, text, accent colors, gradients |
| Typography | 15% | Font sizes, weights, styles, text content |
| Components | 20% | Correct component types, shapes, sizes |
| States | 10% | Correct state rendered, loading indicators, etc. |

### Scoring

For each aspect, rate 0-100%:
- **95-100%**: Near-perfect match
- **85-94%**: Good match, minor differences
- **70-84%**: Noticeable differences
- **<70%**: Significant mismatch

Calculate weighted average. Target: **90% overall match**.

### Comparison Report

Generate a detailed comparison:

```
## Visual Comparison: {Screen Name}

| Aspect | Score | Notes |
|--------|-------|-------|
| Layout | {score}% | {observations} |
| Colors | {score}% | {observations} |
| Typography | {score}% | {observations} |
| Components | {score}% | {observations} |
| States | {score}% | {observations} |
| **Overall** | **{weighted_score}%** | |

### Differences Found
1. {specific difference}
2. {specific difference}
...

### Match Result: {PASS (>=90%) | FAIL (<90%)}
```

---

## Step 3.6: Handle Mismatch (if <90%)

If the visual match is below 90%, prepare fix context and delegate to the implementation skill.

### Attempt 1-3: Fix via Implementation Skill

1. **Identify specific differences** from the comparison report
2. **Update the design description** (`.claude/docs/{featurename}/designs/{featurename}.md`) with fix instructions:

   Append a `## Fix Instructions` section:
   ```markdown
   ## Fix Instructions (Attempt {N})

   The following visual differences were found between the Stitch design and device screenshot:

   1. {specific difference with exact values to fix}
   2. {specific difference with exact values to fix}
   ...

   Priority fixes (highest impact on match score):
   - {fix 1}
   - {fix 2}

   Reference screenshots:
   - Design: designs/{featurename}.png
   - Device screenshot: designs/device/device_{featurename}.png
   ```

3. **Re-invoke `/modifying-kmp-feature`** with the updated context:
   ```
   Fix UI visual differences in feature '{featurename}' to better match the Stitch design.

   Design spec with fix instructions: .claude/docs/{featurename}/designs/{featurename}.md
   See the "Fix Instructions (Attempt {N})" section for specific changes needed.

   Design: .claude/docs/{featurename}/designs/{featurename}.png
   Current device screenshot: .claude/docs/{featurename}/designs/device/device_{featurename}.png
   ```

4. After implementation skill completes, **re-run Steps 3.1 through 3.5** (build, deploy, screenshot, compare)

### After 3 Attempts

If still below 90% after 3 fix attempts:

```
## Visual Verification: Unable to reach 90% match

Current match: {score}%
Attempts: 3/3

### Remaining Differences
{list of differences that couldn't be resolved}

### Possible Reasons
- X-component rendering differs from Stitch design tool
- Platform-specific rendering differences
- Design system constraints limit exact replication

### Options
1. Accept current match ({score}%) and proceed
2. Adjust the Stitch design to match platform constraints
3. Continue manual refinement
```

Ask user how to proceed using `AskUserQuestion`.

---

## Step 3.7: User Final Confirmation

Present the verification results:

```
## Visual Verification Complete: {FeatureName}

### Comparison Results
| Screen | Design | Device Screenshot | Match |
|--------|--------|-------------------|-------|
| {featurename} | designs/{featurename}.png | designs/device/device_{featurename}.png | {score}% |

### Overall Match: {average_score}%
### Threshold: 90%
### Status: {PASSED | ACCEPTED AT {score}%}

All screenshots saved to: .claude/docs/{featurename}/designs/

Would you like to:
1. Confirm and complete
2. Make additional adjustments
3. Redesign in Stitch and re-implement
```

---

## Cleanup

After user confirms:

1. Update stitch.json with verification results:
   ```json
   {
     "verification": {
       "verified": true,
       "verifiedAt": "{date}",
       "matchScores": {
         "{screen_name}": {score}
       },
       "deviceScreenshots": {
         "{screen_name}": "designs/device/device_{screen_name}.png"
       },
       "attempts": {number}
     }
   }
   ```

2. Show final completion report (Mode 3 from SKILL.md)

---

## Output

After Phase 3 completes:
- Device screenshots captured and saved
- Visual comparison completed (>=90% match or user-accepted)
- stitch.json updated with verification data
- User confirmed completion

**Done.** Show Mode 3 completion report from SKILL.md.
