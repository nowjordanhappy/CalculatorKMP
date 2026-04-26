# CalculatorKMP

A calculator app built with **Kotlin Multiplatform** and **Compose Multiplatform**, sharing 100% of UI and logic across Android, iOS, and Desktop (macOS) from a single codebase.

## Features

### Basic Mode
- Arithmetic: `+`, `-`, `×`, `÷`
- Operator precedence (`×` `÷` before `+` `-`)
- Chained operations and multi-step expressions
- Percent (`%`) — context-aware: standalone divides by 100, after `+`/`-` computes percentage of left operand
- Sign toggle (`+/-`)
- Decimal point with leading-zero handling
- Live result preview while typing
- Smart delete — removes scientific function names (e.g. `sin(`) as a single unit
- AC / C toggle — shows AC when idle, C while expression is active

### Scientific Mode
- Trigonometry: `sin`, `cos`, `tan` and their inverses `sin⁻¹`, `cos⁻¹`, `tan⁻¹`
- Logarithms: `ln` (natural), `log` (base-10)
- `√x` (square root), `x²` (square), `xʸ` (power), `1/x` (reciprocal)
- Constants: `π`, `e`
- Parentheses `(` `)` with automatic balance on `=`
- DEG / RAD mode toggle
- Implicit multiplication — `2sin(30)`, `π(1+2)`, `(2+3)(4+5)` all work automatically

### Display
- Expression line + live result preview
- Horizontal scroll for long expressions
- Adaptive font size
- Scientific notation for very large or very small results (|x| ≥ 1×10¹⁰ or |x| < 1×10⁻⁶)

### Error Handling
- **Undefined** — division by zero, `sqrt` of negative, `ln`/`log` of non-positive, `asin`/`acos` out of domain, `0^0`, `0^(-1)`, `tan(90°)`, negative base with fractional exponent
- **Math Error** — empty function arguments, mismatched parentheses, malformed expressions
- Any key press after an error starts a fresh expression

### Theme
- System / Light / Dark — 3-state toggle button in the top-left corner
- Preference persisted across app restarts

### Layout
- Portrait: stacked layout (scientific rows above basic rows)
- Landscape / tablet / desktop: side-by-side layout (scientific panel left, basic panel right)
- Desktop window auto-resizes when switching between Basic and Scientific mode

## Tech Stack

| Layer | Library |
|---|---|
| UI | [Compose Multiplatform](https://www.jetbrains.com/lp/compose-multiplatform/) |
| Language | [Kotlin Multiplatform](https://kotlinlang.org/docs/multiplatform.html) |
| DI | [Koin](https://insert-koin.io/) |
| State | ViewModel + StateFlow (AndroidX Lifecycle KMP) |
| Settings | [Multiplatform Settings](https://github.com/russhwolf/multiplatform-settings) |

## Platforms

| Platform | Status |
|---|---|
| Android | ✅ Running |
| iOS Simulator | ✅ Running |
| iOS Device | ⚠️ Crashes (Kotlin 2.2.0 + CMP 1.9.0-beta01 iosArm64 bug — awaiting upstream fix) |
| Desktop (macOS) | ✅ Running |

## Project Structure

```
composeApp/
  androidMain/      # Android entry point (MainActivity)
  iosMain/          # iOS entry point (MainViewController)
  desktopMain/      # Desktop entry point (main.kt, window sizing)
  commonMain/       # App shell: AppViewModel, AppState, AppAction, CalculatorTheme, SettingsRepository

core/
  domain/           # Pure KMP — no Compose
    ExpressionProcessor.kt   # Facade: digit append, evaluate, format, percent, sign toggle
    ExpressionParser.kt      # Tokenizer + shunting-yard (operators, functions, constants, parens)
    ExpressionEvaluator.kt   # RPN evaluation, trig (DEG/RAD), error detection
    fsm/                     # Finite state machine — input validation before expression update

feature/
  calculator/
    presentation/   # CalculatorViewModel, CalculatorState, CalculatorScreen, all components

iosApp/             # iOS native wrapper (Xcode project)
build-logic/        # Gradle convention plugins
```

## Architecture

**MVI** — unidirectional data flow:

```
User action
    ↓
ViewModel.onAction(Action)
    ↓
FSM validates transition
    ↓
ExpressionProcessor updates expression
    ↓
State emitted via StateFlow
    ↓
Compose UI recomposes
```

Two ViewModels with clear separation:
- **`AppViewModel`** — app-level preferences (theme mode, basic/scientific mode). Persisted via `SettingsRepository`.
- **`CalculatorViewModel`** — calculator state (expression, result, error, DEG/RAD). Owns FSM and expression logic.

Both follow the same pattern: single `state: StateFlow<State>` + `onAction(Action)` dispatcher.

## Running the App

### Android
Open in Android Studio and run the `composeApp` configuration on an Android device or emulator.

### Desktop
```bash
./gradlew :composeApp:run
```

### iOS Simulator
```bash
xcrun simctl list devices | grep Booted
# Use the UDID to launch via Xcode or the provided shell script
```
