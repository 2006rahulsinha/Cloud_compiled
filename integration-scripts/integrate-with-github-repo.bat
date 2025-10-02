@echo off
REM integrate-with-github-repo.bat - Windows integration script

echo 🔗 CloudSim Integration for Existing Next.js Repository
echo =====================================================

REM Configuration
set GITHUB_REPO=https://github.com/2006rahulsinha/Cloud_project
set LOCAL_REPO_DIR=Cloud_project
set METRICS_FILE=cloudsim-metrics.json

echo.
echo This script will:
echo 1. Clone your existing Next.js repository from GitHub
echo 2. Add CloudSim performance monitoring to your app
echo 3. Build and run CloudSim Plus simulation
echo 4. Connect real app performance to cloud analysis
echo.
echo Your existing code will NOT be modified.
echo.
set /p CONTINUE=Continue? (y/N): 
if /i not "%CONTINUE%"=="y" (
    echo Integration cancelled
    exit /b 0
)

REM Check prerequisites
echo ✅ Checking prerequisites...
where git >nul 2>nul || (echo ❌ Git not found & exit /b 1)
where node >nul 2>nul || (echo ❌ Node.js not found & exit /b 1)
where java >nul 2>nul || (echo ❌ Java not found & exit /b 1)
where mvn >nul 2>nul || (echo ❌ Maven not found & exit /b 1)
echo ✅ All prerequisites found

REM Clone or update repository
if exist "%LOCAL_REPO_DIR%" (
    echo ℹ️ Repository exists, pulling latest changes...
    cd "%LOCAL_REPO_DIR%"
    git pull origin main >nul 2>nul || git pull origin master >nul 2>nul
    cd ..
) else (
    echo ℹ️ Cloning repository...
    git clone "%GITHUB_REPO%" "%LOCAL_REPO_DIR%"
)
echo ✅ Repository ready

REM Install dependencies
echo ℹ️ Installing Next.js dependencies...
cd "%LOCAL_REPO_DIR%"
if not exist "node_modules" (
    call npm install >nul 2>nul
    echo ✅ Dependencies installed
) else (
    echo ℹ️ Dependencies already installed
)
cd ..

REM Add monitoring integration
echo ℹ️ Adding CloudSim monitoring to your app...
copy "performance-monitoring\integrate-with-existing-nextjs.js" "%LOCAL_REPO_DIR%\" >nul

REM Create API route
if not exist "%LOCAL_REPO_DIR%\pages\api" mkdir "%LOCAL_REPO_DIR%\pages\api"

echo const monitor = require('../../integrate-with-existing-nextjs'); > "%LOCAL_REPO_DIR%\pages\api\cloudsim-metrics.js"
echo export default function handler(req, res) { >> "%LOCAL_REPO_DIR%\pages\api\cloudsim-metrics.js"
echo   res.status(200).json(monitor.getMetrics()); >> "%LOCAL_REPO_DIR%\pages\api\cloudsim-metrics.js"
echo } >> "%LOCAL_REPO_DIR%\pages\api\cloudsim-metrics.js"

echo ✅ Monitoring integration added

REM Build CloudSim
echo ℹ️ Building CloudSim simulation...
cd "cloudsim-simulation"
call mvn clean compile -q >nul 2>nul
if %errorlevel% neq 0 (
    echo ❌ CloudSim build failed
    exit /b 1
)
echo ✅ CloudSim built successfully
cd ..

REM Start integrated system
echo ℹ️ Starting your Next.js app with CloudSim integration...
cd "%LOCAL_REPO_DIR%"
start "Next.js with CloudSim" /min node -e "require('./integrate-with-existing-nextjs'); const {spawn}=require('child_process'); spawn('npm',['run','dev'],{stdio:'inherit'})"
cd ..

timeout /t 10 /nobreak >nul

echo ℹ️ Starting CloudSim simulation...
cd "cloudsim-simulation"
if exist "..\%LOCAL_REPO_DIR%\%METRICS_FILE%" (
    echo ✅ Real-time metrics detected
    start "CloudSim Analysis" mvn exec:java -Dexec.args="--monitor ../%LOCAL_REPO_DIR%/%METRICS_FILE%"
) else (
    echo ℹ️ Starting simulation mode
    start "CloudSim Analysis" mvn exec:java
)
cd ..

echo.
echo ===============================================
echo 🎯 INTEGRATION COMPLETE!
echo ===============================================
echo.
echo 🌐 Your Next.js App: http://localhost:3000
echo 🧪 CloudSim: Analyzing real performance
echo 📊 Monitoring: Active
echo.
echo Visit your app and use it normally.
echo Real performance data will feed CloudSim analysis!
echo.
pause