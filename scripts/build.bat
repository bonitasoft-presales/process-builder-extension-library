@echo off
REM ============================================================================
REM Build Script for Process Builder Extension Library (Windows)
REM ============================================================================
REM
REM Usage: build.bat [command]
REM
REM Commands:
REM   install     - Standard build: compile + test + package (default)
REM   test        - Run all tests (unit + property-based)
REM   pbt         - Run only Property-Based Tests
REM   mutation    - Run PIT Mutation Testing (slow, ~8-10 min)
REM   full-qa     - Full QA: tests + mutation testing + reports
REM   quick       - Quick build: compile only, skip tests
REM   site        - Generate all reports (JaCoCo, SpotBugs, PMD, etc.)
REM   clean       - Clean build artifacts
REM   help        - Show this help message
REM
REM Examples:
REM   build.bat              - Run standard build
REM   build.bat test         - Run all tests
REM   build.bat mutation     - Run mutation testing
REM   build.bat full-qa      - Run complete QA suite
REM
REM ============================================================================

setlocal enabledelayedexpansion

REM Navigate to project root (parent of scripts directory)
cd /d "%~dp0.."

REM Default command
set "COMMAND=%~1"
if "%COMMAND%"=="" set "COMMAND=install"

REM Process commands
if /i "%COMMAND%"=="help" goto :help
if /i "%COMMAND%"=="install" goto :install
if /i "%COMMAND%"=="test" goto :test
if /i "%COMMAND%"=="pbt" goto :pbt
if /i "%COMMAND%"=="mutation" goto :mutation
if /i "%COMMAND%"=="pit" goto :mutation
if /i "%COMMAND%"=="full-qa" goto :fullqa
if /i "%COMMAND%"=="quick" goto :quick
if /i "%COMMAND%"=="site" goto :site
if /i "%COMMAND%"=="clean" goto :clean

echo [ERROR] Unknown command: %COMMAND%
echo.
goto :help

:help
echo.
echo ============================================================================
echo  Build Script for Process Builder Extension Library
echo ============================================================================
echo.
echo  Usage: build.bat [command]
echo.
echo  Commands:
echo    install     Standard build: compile + test + package (default)
echo    test        Run all tests (unit + property-based)
echo    pbt         Run only Property-Based Tests
echo    mutation    Run PIT Mutation Testing (slow, ~8-10 min)
echo    full-qa     Full QA: tests + mutation testing + reports
echo    quick       Quick build: compile only, skip tests
echo    site        Generate all reports (JaCoCo, SpotBugs, PMD, etc.)
echo    clean       Clean build artifacts
echo    help        Show this help message
echo.
echo  Examples:
echo    build.bat              Run standard build
echo    build.bat test         Run all tests
echo    build.bat mutation     Run mutation testing
echo    build.bat full-qa      Run complete QA suite
echo.
echo  Report Locations:
echo    JaCoCo:    target\site\jacoco\index.html
echo    PIT:       target\pit-reports\index.html
echo    SpotBugs:  target\site\spotbugs.html
echo    PMD:       target\site\pmd.html
echo    Checkstyle: target\site\checkstyle.html
echo.
goto :end

:install
echo.
echo [BUILD] Running standard build (compile + test + package)...
echo.
call mvnw.cmd clean install
goto :end

:test
echo.
echo [TEST] Running all tests (unit + property-based)...
echo.
call mvnw.cmd clean test
goto :end

:pbt
echo.
echo [PBT] Running Property-Based Tests only...
echo.
call mvnw.cmd test -Dtest=*PropertyTest
goto :end

:mutation
echo.
echo [MUTATION] Running PIT Mutation Testing...
echo [INFO] This may take 8-10 minutes...
echo.
call mvnw.cmd clean test pitest:mutationCoverage
echo.
echo [INFO] Report available at: target\pit-reports\index.html
goto :end

:fullqa
echo.
echo [FULL-QA] Running complete QA suite (tests + mutation + reports)...
echo [INFO] This may take 10-15 minutes...
echo.
call mvnw.cmd clean verify site -Pfull-qa
echo.
echo [INFO] Reports available at: target\site\index.html
goto :end

:quick
echo.
echo [QUICK] Running quick build (compile only, skip tests)...
echo.
call mvnw.cmd clean install -Pquick
goto :end

:site
echo.
echo [SITE] Generating all reports...
echo.
call mvnw.cmd clean verify site
echo.
echo [INFO] Reports available at: target\site\index.html
goto :end

:clean
echo.
echo [CLEAN] Cleaning build artifacts...
echo.
call mvnw.cmd clean
goto :end

:end
endlocal
