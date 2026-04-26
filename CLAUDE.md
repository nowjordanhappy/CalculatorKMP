# CalculatorKMP — Claude Code Context

## Project

Kotlin Multiplatform + Compose Multiplatform calculator app targeting Android, iOS, and Desktop (macOS). Single shared UI via CMP.

## Module Structure

```
:composeApp                        ← shell: DI wiring, entry points per platform
  androidMain/MainActivity.kt      ← setContent { App() }
  iosMain/MainViewController.kt    ← ComposeUIViewController { App() }
  desktopMain/main.kt              ← Window { App(forceWide, layoutConfig) }
  commonMain/
    App.kt                         ← single collectAsState, passes state to CalculatorScreenRoot
    AppViewModel.kt                ← app-level state (theme, isScientific), onAction(AppAction)
    AppState.kt                    ← themeMode, isScientific
    AppAction.kt                   ← OnThemeChange, OnScientificToggle
    CalculatorTheme.kt             ← MaterialTheme wrapper, respects ThemeMode
    settings/SettingsRepository.kt ← plain persistence (no StateFlow); read by AppViewModel on init
    di/AppModule.kt                ← Koin: SettingsRepository (single), AppViewModel (viewModel)

:feature:calculator:presentation   ← all UI + ViewModel (commonMain)
  CalculatorScreen.kt              ← layout logic, LayoutConfig, WideButtonArea
  CalculatorViewModel.kt           ← calculator state, FSM wiring, all action handlers
  CalculatorState.kt               ← expression, result, error, isRad, isAcMode
  CalculatorAction.kt              ← sealed interface of all calculator user actions
  ButtonLabels.kt                  ← string constants for all button labels
  components/
    CalcButton.kt                  ← button with ButtonType, buttonHeight?, aspectRatio fallback
    CalcRow.kt                     ← Row(fillMaxWidth, spacedBy 12dp)
    CalculatorButtonGrid.kt        ← 5 rows basic buttons, accepts buttonHeight?
    ScientificButtonGrid.kt        ← 4 rows sci + optional DEG/RAD row
    CalculatorDisplay.kt           ← expression + result display, horizontal scroll
    ModeMenu.kt                    ← ⋮ dropdown, pendingToggle delay for wide layout
    ThemeToggleButton.kt           ← 3-state cycle icon (SYSTEM→LIGHT→DARK), top-left corner

:core:domain                       ← pure KMP, no Compose
  ExpressionProcessor.kt          ← top-level facade: appendDigit, evaluate, formatDisplay, formatResult, applyPercent, applySignToggle, needsImplicitMultiply
  ExpressionParser.kt             ← tokenizer + shunting-yard, handles ^, functions, constants, parens
  ExpressionEvaluator.kt          ← RPN evaluator, trig (DEG/RAD), all math operations
  CalculatorError.kt              ← UNDEFINED (÷0, domain errors), MATH_ERROR (malformed)
  EvaluationResult.kt             ← Success, Error, NoOp
  ThemeMode.kt                    ← SYSTEM, LIGHT, DARK
  Constants.kt                    ← operators, point symbol
  fsm/
    CalculatorFSM.kt              ← FSM orchestrator, syncFromExpression
    BasicStrategy.kt              ← transitions for basic mode
    ScientificStrategy.kt         ← extends basic: Function, OpenParen, CloseParen, Constant, PowerSuffix
    FSMAction.kt                  ← Digit, Operator, Point, Resolve, Delete, Clear, Percent, SignToggle,
                                     Function, OpenParen, CloseParen, Constant, PowerSuffix
    FSMState.kt                   ← Empty, FirstOperand, OperatorEntered, SecondOperand, Result, Error
    FSMTransition.kt              ← Allow(nextState), Block
```

## App-Level State (AppViewModel)

`isScientific` and `themeMode` are app preferences, not calculator state. They live in `AppViewModel` (not `CalculatorViewModel`).

```kotlin
// AppViewModel owns theme + scientific mode
val state: StateFlow<AppState>
fun onAction(action: AppAction)

// SettingsRepository is plain persistence — no StateFlow
val themeMode: ThemeMode   // computed property
val isScientific: Boolean  // computed property
fun setTheme(mode: ThemeMode)
fun toggleScientific(): Boolean
```

`main.kt` reads `settingsRepo.isScientific` synchronously (before the window opens) to set the correct initial window size, then observes `AppViewModel.state.isScientific` inside the Window composable for resize on toggle.

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
// main.kt — initial size read from repo before window opens
val initialIsScientific = GlobalContext.get().get<SettingsRepository>().isScientific
val windowState = rememberWindowState(size = computeWindowSize(initialIsScientific))

// Inside Window composable — observe AppViewModel for resize on toggle
val appViewModel = koinViewModel<AppViewModel>()
val appState by appViewModel.state.collectAsState()
LaunchedEffect(appState.isScientific) { windowState.size = computeWindowSize(appState.isScientific) }
```
- Basic: 320dp wide; Scientific: 620dp wide (300dp added for sci panel), same height

### ModeMenu Delay
On wide layouts, the scientific toggle is deferred 200ms after menu closes so the dropdown dismiss animation completes before the window resizes.
```kotlin
// ModeMenu.kt — pendingToggle pattern
LaunchedEffect(showMenu) {
    if (!showMenu && pendingToggle) {
        if (delayToggle) delay(200)
        pendingToggle = false
        onScientificToggle()
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

States: `Empty → FirstOperand → OperatorEntered → SecondOperand → Result → Error`
- `BasicStrategy` defines transitions for basic mode
- `ScientificStrategy` extends basic: adds Function, OpenParen, CloseParen, Constant, PowerSuffix
- `CalculatorFSM.syncFromExpression()` re-derives FSM state from expression string (used after delete)
- On error state: all actions except `Clear` are blocked — digit/operator/function handlers check `wasError` and reset FSM + clear expression before proceeding

## Scientific Mode

### Buttons
```
Row 1: sin   cos   tan   1/x
Row 2: sin⁻¹ cos⁻¹ tan⁻¹ xʸ
Row 3: ln    log   √x    x²
Row 4: π     e     (     )
Row 5 (wide only): DEG / RAD
```
DEG/RAD also appears as a menu item in the ⋮ menu when in portrait scientific mode.

### Implicit Multiplication
Automatically inserted in two places:
- `ExpressionProcessor.appendDigit`: inserts `×` when a digit follows `)`, `π`, or `e`
- `handleScientificFunction`: inserts `×` (via FSM operator + expression prefix) before functions, `(`, and constants when the current expression ends with a digit, `.`, `)`, `π`, or `e`

### Expression Evaluation
`ExpressionParser` tokenizes and applies shunting-yard algorithm supporting:
- Binary operators: `+`, `-`, `×`, `÷`, `^`
- Functions: `sin`, `cos`, `tan`, `asin`, `acos`, `atan`, `ln`, `log`, `sqrt`
- Constants: `π` (3.14159…), `e` (2.71828…)
- Parentheses (auto-balanced on `=` press)
- DEG/RAD passed through to trig functions

## Coding Conventions
- No Co-Authored-By in commits
- No comments unless the WHY is non-obvious
- No unused imports
- `Dp * Int` works, `Int * Dp` does not — always write `12.dp * n` not `n * 12.dp`
- All ViewModels: single state data class + `onAction(Action)` dispatcher, no separate public mutator functions
