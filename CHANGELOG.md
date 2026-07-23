# Changelog

All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.1.0/).

## [Unreleased]

## [2.0.1] - 2026-07-22

### Changed
- Target SDK updated to API 36 (Android 16)

## [2.0.0] - 2026-06-06

### Added
- Basic calculator: arithmetic, operator precedence, chained operations
- Scientific mode: sin, cos, tan, asin, acos, atan, ln, log, √x, x², xʸ, 1/x, π, e, parentheses
- DEG / RAD toggle
- Implicit multiplication (2sin(30), π(1+2), (2+3)(4+5))
- Portrait and wide adaptive layout (landscape, tablet, desktop)
- Desktop window auto-resizes on Basic ↔ Scientific toggle
- Theme toggle: System / Light / Dark — persisted across restarts
- Live result preview while typing
- Horizontal scroll for long expressions
- Smart delete (removes function names as a single unit)
- AC / C toggle
- Error system: Undefined (÷0, domain errors) and Math Error (malformed expressions)
- Localization: English, Spanish, French, German, Portuguese, Italian
- Accessibility content descriptions on all buttons
- Android, iOS, and Desktop (macOS, Windows, Linux) targets
- Firebase Analytics + Crashlytics (Android)
- CI release pipeline: tag `v*` → GitHub Release + Play Store internal track

[2.0.1]: https://github.com/nowjordanhappy/CalculatorKMP/compare/v2.0.0...v2.0.1
[2.0.0]: https://github.com/nowjordanhappy/CalculatorKMP/releases/tag/v2.0.0
