#!/bin/bash
set -e

SIMULATOR_UDID="D9553442-291F-4F38-BE4B-BD6D0CEA1C0F"  # iPhone 17 iOS 26.4
PROJECT_DIR="$(cd "$(dirname "$0")" && pwd)"
XCODE_PROJECT="$PROJECT_DIR/iosApp/iosApp.xcodeproj"
DERIVED_DATA="$PROJECT_DIR/.derived-data"
BUNDLE_ID="com.nowjordanhappy.calculatorkmp"

echo "Booting simulator..."
xcrun simctl boot "$SIMULATOR_UDID" 2>/dev/null || true
open -a Simulator

echo "Building..."
xcodebuild \
  -project "$XCODE_PROJECT" \
  -scheme iosApp \
  -destination "platform=iOS Simulator,id=$SIMULATOR_UDID" \
  -configuration Debug \
  -derivedDataPath "$DERIVED_DATA" \
  build

APP_PATH="$DERIVED_DATA/Build/Products/Debug-iphonesimulator/CalculatorKMP.app"

if [ ! -d "$APP_PATH" ]; then
  echo "Error: app not found at $APP_PATH"
  exit 1
fi

echo "Installing $APP_PATH..."
xcrun simctl install "$SIMULATOR_UDID" "$APP_PATH"

echo "Launching..."
xcrun simctl launch "$SIMULATOR_UDID" "$BUNDLE_ID"
