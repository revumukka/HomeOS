#!/bin/sh
# Builds out/tvhome.apk. Needs Java 17 and the Android SDK (platform 34, build-tools 34).
#   macOS:  brew install openjdk@17 && brew install --cask android-commandlinetools
#           sdkmanager "platforms;android-34" "build-tools;34.0.0"
# Use a different app list with: APPS=my-apps.txt sh build.sh
set -e
cd "$(dirname "$0")"

[ -n "$JAVA_HOME" ] || for j in /opt/homebrew/opt/openjdk@17 /usr/local/opt/openjdk@17 /usr/lib/jvm/java-17-openjdk-amd64; do [ -d "$j" ] && JAVA_HOME=$j && break; done
[ -n "$JAVA_HOME" ] && export PATH="$JAVA_HOME/bin:$PATH"
SDK=${ANDROID_HOME:-${ANDROID_SDK_ROOT:-}}
[ -n "$SDK" ] || for d in /opt/homebrew/share/android-commandlinetools "$HOME/Library/Android/sdk" "$HOME/Android/Sdk"; do [ -d "$d" ] && SDK=$d && break; done
BT=$SDK/build-tools/34.0.0; JAR=$SDK/platforms/android-34/android.jar
[ -f "$JAR" ] && [ -d "$BT" ] || { echo "Android SDK not found. Set ANDROID_HOME, and install platforms;android-34 and build-tools;34.0.0." >&2; exit 1; }

rm -rf out; mkdir -p out/classes out/dex out/gen/com/ansub/tvhome

# Turn the app list into Apps.java.
{
  echo "package com.ansub.tvhome;"
  echo "final class Apps { static final String[] LIST = {"
  grep -v '^[[:space:]]*#' "${APPS:-apps.txt}" | tr -d ' \t\r' | grep -v '^$' | sed 's/.*/    "&",/'
  echo "}; }"
} > out/gen/com/ansub/tvhome/Apps.java

"$BT/aapt2" compile --dir res -o out/res.zip
"$BT/aapt2" link -o out/base.apk -I "$JAR" --manifest AndroidManifest.xml -A assets --min-sdk-version 21 --target-sdk-version 34 out/res.zip
# shellcheck disable=SC2046 # javac and d8 want one argument per file, so the list is split on purpose
javac -nowarn -Xlint:-options -encoding UTF-8 -source 1.8 -target 1.8 -bootclasspath "$JAR:$BT/core-lambda-stubs.jar" \
  -d out/classes $(find src out/gen -name "*.java")
# shellcheck disable=SC2046 # same here
"$BT/d8" --min-api 21 --release --lib "$JAR" --output out/dex $(find out/classes -name '*.class')
cp out/base.apk out/unsigned.apk; (cd out/dex && zip -q ../unsigned.apk classes.dex)
"$BT/zipalign" -f 4 out/unsigned.apk out/aligned.apk

# A signing key is made on first build. Keep key.jks: updates must be signed with the same key.
# (Releases are signed with their own key: set KEYSTORE and KEYSTORE_PASS.)
APP_NAME="HomeScreen"
VARIANT="Release"
VERSION_NAME="7.1.2"
VERSION_CODE="73"
PLAY_APK="out/${APP_NAME}_${VARIANT}_v${VERSION_NAME}_${VERSION_CODE}.apk"
UNIVERSAL_APK="out/${APP_NAME}-Universal-TV-Foldable-Tablet-${VARIANT}-v${VERSION_NAME}-b${VERSION_CODE}.apk"
LEGACY_PLAY_APK="out/HomeOS_${VARIANT}_v${VERSION_NAME}_${VERSION_CODE}.apk"

KS=${KEYSTORE:-key.jks}; PASS=${KEYSTORE_PASS:-tvhome123}
[ -f "$KS" ] || keytool -genkeypair -keystore "$KS" -storepass "$PASS" -keypass "$PASS" -alias tvhome \
  -keyalg RSA -keysize 2048 -validity 10000 -dname "CN=HomeScreen, O=RevanthMukka, C=US" >/dev/null 2>&1
"$BT/apksigner" sign --v1-signing-enabled true --v2-signing-enabled true --v3-signing-enabled true \
  --ks "$KS" --ks-pass "pass:$PASS" --out out/tvhome.apk out/aligned.apk
"$BT/apksigner" verify out/tvhome.apk
cp out/tvhome.apk "$PLAY_APK"
cp out/tvhome.apk "$UNIVERSAL_APK"
cp out/tvhome.apk "$LEGACY_PLAY_APK"
cp out/tvhome.apk "out/${APP_NAME}.apk"
cp out/tvhome.apk "out/HomeOS.apk"

DOCS_APP_DIR="$HOME/Documents/HomeScreen App"
mkdir -p "$DOCS_APP_DIR"
cp "$PLAY_APK" "$UNIVERSAL_APK" "out/${APP_NAME}.apk" "$DOCS_APP_DIR/"
echo "Built $PLAY_APK and $UNIVERSAL_APK (copied to $DOCS_APP_DIR)"
