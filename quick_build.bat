@echo off
echo ========================================
echo Quick Build & Install
echo ========================================

echo.
echo Building and installing...
call gradlew.bat :app:installDebug

if %errorlevel% neq 0 (
    echo ERROR: Build/Install failed!
    pause
    exit /b 1
)

echo.
echo ========================================
echo SUCCESS! App installed successfully.
echo ========================================
echo.
pause 