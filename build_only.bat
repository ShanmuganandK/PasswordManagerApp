@echo off
echo ========================================
echo Password Manager App - Build Only
echo ========================================

echo.
echo Building debug APK...
call gradlew.bat :app:assembleDebug

if %errorlevel% neq 0 (
    echo ERROR: Build failed!
    pause
    exit /b 1
)

echo.
echo ========================================
echo SUCCESS! APK built successfully.
echo ========================================
echo.
echo APK Location: app\build\outputs\apk\debug\app-debug.apk
echo.
echo To install on device/emulator:
echo 1. Connect device or start emulator
echo 2. Run: adb install app\build\outputs\apk\debug\app-debug.apk
echo.
pause 