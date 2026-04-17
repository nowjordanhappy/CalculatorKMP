# CalculatorKMP

A modern calculator app built with **Kotlin Multiplatform** and **Compose Multiplatform**, targeting Android, iOS, and Desktop from a single codebase.

## Features

- Basic calculator operations (+, -, ×, ÷)
- Operator precedence (× ÷ before + -)
- Percent and sign toggle
- Live expression preview
- Adaptive font size + horizontal scroll for long expressions
- Dark and light theme support

## Tech Stack

- [Kotlin Multiplatform](https://kotlinlang.org/docs/multiplatform.html)
- [Compose Multiplatform](https://www.jetbrains.com/lp/compose-multiplatform/)
- [Koin](https://insert-koin.io/) — dependency injection
- [ViewModel + StateFlow](https://developer.android.com/topic/libraries/architecture/viewmodel) — state management

## Platforms

| Platform | Status |
|---|---|
| Android | ✅ |
| iOS | ✅ |
| Desktop | ✅ |

## Project Structure

```
composeApp/        # Shared app entry point
core/
  domain/          # Calculator logic, expression evaluator
feature/
  calculator/
    presentation/  # ViewModel, State, UI screen
iosApp/            # iOS native entry point
build-logic/       # Convention plugins
```

## Architecture

- **MVI** — unidirectional data flow (Action → ViewModel → State → UI)
- **Multi-module** — core and feature modules separated
- Input validation at action time, not evaluation time
