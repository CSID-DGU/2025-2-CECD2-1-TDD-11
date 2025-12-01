@echo off
echo ================================
echo Web Build Test
echo ================================
echo.

echo [1/3] Type Check...
call npm run type-check
if %errorlevel% neq 0 (
    echo FAILED: Type check failed
    exit /b 1
)
echo PASSED
echo.

echo [2/3] Build Test...
call npm run build
if %errorlevel% neq 0 (
    echo FAILED: Build failed
    exit /b 1
)
echo PASSED
echo.

echo [3/3] Preview Server...
echo Starting preview server...
echo Press Ctrl+C to stop
call npm run preview
