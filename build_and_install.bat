@echo off
echo ========================================
echo Password Manager App - Build & Install
echo ========================================

echo.
echo [0/5] Setting up Android SDK path...

REM Try to find Android SDK in common locations
set ANDROID_SDK_PATH=

REM Check if ANDROID_HOME is set
if defined ANDROID_HOME (
    set ANDROID_SDK_PATH=%ANDROID_HOME%
    echo Found ANDROID_HOME: %ANDROID_SDK_PATH%
) else (
    REM Check common installation paths
    if exist "%LOCALAPPDATA%\Android\Sdk" (
        set ANDROID_SDK_PATH=%LOCALAPPDATA%\Android\Sdk
        echo Found Android SDK at: %ANDROID_SDK_PATH%
    ) else if exist "C:\Users\%USERNAME%\AppData\Local\Android\Sdk" (
        set ANDROID_SDK_PATH=C:\Users\%USERNAME%\AppData\Local\Android\Sdk
        echo Found Android SDK at: %ANDROID_SDK_PATH%
    ) else if exist "C:\Android\Sdk" (
        set ANDROID_SDK_PATH=C:\Android\Sdk
        echo Found Android SDK at: %ANDROID_SDK_PATH%
    ) else (
        echo ERROR: Android SDK not found!
        echo Please set ANDROID_HOME environment variable or install Android SDK
        echo Common locations:
        echo - %LOCALAPPDATA%\Android\Sdk
        echo - C:\Users\%USERNAME%\AppData\Local\Android\Sdk
        echo - C:\Android\Sdk
        pause
        exit /b 1
    )
)

REM Add Android SDK tools to PATH
set PATH=%ANDROID_SDK_PATH%\platform-tools;%ANDROID_SDK_PATH%\emulator;%PATH%

echo.
echo [1/5] Cleaning previous build...
call gradlew.bat clean
if %errorlevel% neq 0 (
    echo ERROR: Clean failed!
    pause
    exit /b 1
)

echo.
echo [2/5] Building debug APK...
call gradlew.bat :app:assembleDebug
if %errorlevel% neq 0 (
    echo ERROR: Build failed!
    pause
    exit /b 1
)

echo.
echo [3/5] Checking for connected devices...
adb devices
echo.

echo [4/5] Installing APK...
adb install -r app\build\outputs\apk\debug\app-debug.apk
if %errorlevel% neq 0 (
    echo ERROR: Install failed!
    echo Make sure you have a device or emulator connected
    pause
    exit /b 1
)

echo.
echo [5/5] Starting the app...
adb shell am start -n com.example.passwordmanager/.ui.LoginActivity

echo.
echo ========================================
echo SUCCESS! App has been built and installed.
echo ========================================
echo.
echo APK Location: app\build\outputs\apk\debug\app-debug.apk
echo.
pause 