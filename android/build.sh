#!/usr/bin/env bash
# Rebuilds the Android text to speech bridge that ships inside the mod jar.
#
#   android/native/tts_bridge.c              -> src/main/resources/mcaccess/android/<abi>/libmcaccess_android_tts.so
#   android/art/src/**/*.java                -> src/main/resources/mcaccess/android/mcaccess_android_tts.dex
#
# The dex is compiled against android.jar and converted with d8 (minimal API 26,
# because the bridge is loaded with InMemoryDexClassLoader). Both kinds of file
# are committed, so building the mod itself stays a plain Gradle build.
#
# Requirements: the Android NDK (r25 or newer) and an installed Android SDK
# platform. Both are located through the usual environment variables:
#
#   ANDROID_NDK_HOME  or ANDROID_NDK_ROOT
#   ANDROID_HOME      or ANDROID_SDK_ROOT
set -euo pipefail

here="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
repo="$(cd "${here}/.." && pwd)"
resources="${repo}/src/main/resources/mcaccess/android"

ndk="${ANDROID_NDK_HOME:-${ANDROID_NDK_ROOT:-}}"
if [[ -z "${ndk}" ]]; then
    echo "ANDROID_NDK_HOME is not set" >&2
    exit 1
fi
sdk="${ANDROID_HOME:-${ANDROID_SDK_ROOT:-}}"
if [[ -z "${sdk}" ]]; then
    echo "ANDROID_HOME is not set" >&2
    exit 1
fi

api=26
android_jar="$(ls -1 "${sdk}"/platforms/android-*/android.jar | sort -V | tail -n 1)"
d8="$(ls -1 "${sdk}"/build-tools/*/d8 | sort -V | tail -n 1)"
toolchain="${ndk}/toolchains/llvm/prebuilt/linux-x86_64/bin"
work="$(mktemp -d)"
trap 'rm -rf "${work}"' EXIT

echo "NDK:   ${ndk}"
echo "jar:   ${android_jar}"
echo "d8:    ${d8}"

# --- native library, one per ABI the launcher can run a JVM for
declare -A triples=(
    [arm64-v8a]="aarch64-linux-android${api}"
    [armeabi-v7a]="armv7a-linux-androideabi${api}"
    [x86_64]="x86_64-linux-android${api}"
)
for abi in "${!triples[@]}"; do
    mkdir -p "${resources}/${abi}"
    echo "compiling ${abi}"
    "${toolchain}/${triples[${abi}]}-clang" \
        -shared -fPIC -O2 -Wall -Wextra -Wno-unused-parameter \
        -o "${resources}/${abi}/libmcaccess_android_tts.so" \
        "${here}/native/tts_bridge.c" \
        -llog
done

# --- dex with the bridge class
mkdir -p "${work}/classes" "${work}/dex"
javac --release 11 -nowarn \
    -classpath "${android_jar}" \
    -d "${work}/classes" \
    $(find "${here}/art/src" -name '*.java')
"${d8}" --min-api ${api} --lib "${android_jar}" --output "${work}/dex" \
    $(find "${work}/classes" -name '*.class')
mv "${work}/dex/classes.dex" "${resources}/mcaccess_android_tts.dex"

echo "done:"
ls -l "${resources}" "${resources}"/*/
