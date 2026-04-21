# CalculatorKMP — Claude Code Context

## Project

Kotlin Multiplatform + Compose Multiplatform calculator app targeting Android, iOS, and Desktop (macOS). Single shared UI via CMP.

## Module Structure

```
:composeApp                        ← shell: DI wiring, entry points per platform
  androidMain/MainActivity.kt      ← setContent { App() }
  iosMain/MainViewController.kt    ← ComposeUIViewController { App() }
  desktopMain/main.kt              ← Window { App(forceWide, layoutConfig) }
  commonMain/App.kt                ← CalculatorTheme { CalculatorScreenRoot(...) }

:feature:calculator:presentation   ← all UI + ViewModel (commonMain)
  CalculatorScreen.kt              ← layout logic, LayoutConfig, WideButtonArea
  CalculatorViewModel.kt           ← state, FSM wiring, all action handlers
  CalculatorState.kt               ← expression, result, error, isScientific, isRad
  CalculatorAction.kt              ← sealed class of all user actions
  components/
    CalcButton.kt                  ← button with ButtonType, buttonHeight?, aspectRatio fallback
    CalcRow.kt                     ← Row(fillMaxWidth, spacedBy 12dp)
    CalculatorButtonGrid.kt        ← 5 rows basic buttons, accepts buttonHeight?
    ScientificButtonGrid.kt        ← 4 rows sci + optional DEG/RAD row
    CalculatorDisplay.kt           ← expression + result display, horizontal scroll
    ModeMenu.kt                    ← ⋮ dropdown, pendingToggle delay for wide layout

:core:domain                       ← pure KMP, no Compose
  CalculatorFSM.kt                 ← FSM orchestrator
  BasicStrategy.kt                 ← allowed transitions for basic mode
  CalculatorUtils.kt               ← expression evaluation
  Operations.kt                    ← math helpers
  Constants.kt                     ← operators, point symbol
```

## Layout System

### isWide Decision
```kotlin
val isWide = forceWide || stableMaxWidth > maxHeight || stableMaxWidth > WIDE_BREAKPOINT
```
- `forceWide = true` on desktop (always wide, never portrait)
- `maxWidth > maxHeight` catches landscape phones and tablets
- `maxWidth > 600dp` catches tablets in portrait

### Three Layout Cases
1. **Portrait** (`!isWide`): Column with optional ScientificButtonGrid stacked above CalculatorButtonGrid
2. **Wide basic** (`isWide && !isScientific`): Box with CalculatorButtonGrid at CenterEnd, fixed width
3. **Wide scientific** (`isWide && isScientific`): Box with ScientificButtonGrid at CenterStart + CalculatorButtonGrid at CenterEnd

### LayoutConfig (desktop only)
Passed from `main.kt` with pre-computed fixed sizes. Prevents layout depending on `maxWidth` during macOS window animation.
```kotlin
data class LayoutConfig(val panelWidth: Dp, val degRadHeight: Dp)
// panelWidth = 288dp, degRadHeight = ~63dp (square button size)
```

### stableMaxWidth (desktop debounce)
Desktop uses a 250ms debounce on `maxWidth` to avoid recompositions during window animation. Mobile uses `SideEffect { stableMaxWidth = maxWidth }` for immediate rotation response.

### Button Height
- Portrait basic: `null` → `aspectRatio(1f)` square buttons
- Portrait scientific: computed from available height, `coerceIn(40..56dp)`
- Wide mobile (landscape/tablet): computed from available height, `coerceIn(36..64dp)`
- Desktop: `null` → `aspectRatio(1f)` square buttons (LayoutConfig ensures correct size)

### Panel Widths
- Desktop: both `panelWidth` and `scientificPanelWidth` = `layoutConfig.panelWidth` (288dp)
- Mobile: `panelWidth = availableWidth`, `scientificPanelWidth = (availableWidth - 12dp) / 2`

## Desktop Window Sizing

```kotlin
// main.kt
private fun computeWindowSize(isScientific: Boolean): DpSize {
    // basic: 320dp wide, scientific: 620dp wide, same height
}
private fun computeLayoutConfig(): LayoutConfig { ... }

// Window grows/shrinks on scientific toggle
onIsScientificChanged = { isScientific ->
    windowState.size = computeWindowSize(isScientific)
}
```

### ModeMenu Delay
On wide layouts, the scientific toggle is deferred 200ms after menu closes so the dropdown dismiss animation completes before the window resizes.
```kotlin
// ModeMenu.kt — pendingToggle pattern
LaunchedEffect(showMenu) {
    if (!showMenu && pendingToggle) {
        if (delayToggle) delay(200)
        pendingToggle = false
        onAction(CalculatorAction.OnScientificToggle)
    }
}
```

## Known Issues / Constraints

### Desktop Window Animation Glitch
macOS animates window resize over ~200ms. `BoxWithConstraints` recomposes on every frame. Approaches tried and their results:
- `AnimatedVisibility` (expandHorizontally/shrinkHorizontally) — buttons resize during animation, looks bad
- `movableContentOf` — helps preserve basic grid identity but stale capture issue for changing values
- `wrapContentWidth()` — panels overlap since Box doesn't sum children widths
- `animateContentSize()` — conflicts with window resize, not smooth
- Fixed width + `Modifier.align(Alignment.End)` — basic grid stays right-anchored (macOS right-edge anchor), reduces glitch
- **Current approach**: Box with CenterStart (sci) + CenterEnd (basic), fixed widths from LayoutConfig, no Compose animation. Window resize IS the animation.

### iOS Physical Device
Kotlin 2.2.0 + CMP 1.9.0-beta01 crashes on iosArm64 (physical device). Simulator works fine. Awaiting upstream fix.

### iOS Simulator in Android Studio
AS device picker broken for iOS. Use shell script run configuration:
```bash
xcrun simctl list devices | grep Booted
# Then run via Xcode or shell script targeting that simulator UDID
```

## FSM (Finite State Machine)

States: `Initial → NumberEntered → OperatorEntered → Result → Error`
- `BasicStrategy` defines allowed transitions
- `CalculatorFSM.syncFromExpression()` re-derives FSM state from expression string (used after delete)
- Scientific functions: `OnScientificFunction(label)` action exists, logic pending (Step 5)

## Pending Work (Step 5)
- 5a: Parser — parentheses + `^` power operator
- 5b: Parser — function calls (`sin`, `cos`, `ln`, etc.)
- 5c: FSM — `ScientificStrategy` with new allowed actions
- 5d: DEG/RAD wired to trig evaluation

## Coding Conventions
- No Co-Authored-By in commits
- No comments unless the WHY is non-obvious
- No unused imports
- `Dp * Int` works, `Int * Dp` does not — always write `12.dp * n` not `n * 12.dp`
