#!/bin/bash
# ============================================================================
# Build Script for Process Builder Extension Library (Linux/Mac)
# ============================================================================
#
# Usage: ./build.sh [command]
#
# Commands:
#   install     - Standard build: compile + test + package (default)
#   test        - Run all tests (unit + property-based)
#   pbt         - Run only Property-Based Tests
#   mutation    - Run PIT Mutation Testing (slow, ~8-10 min)
#   full-qa     - Full QA: tests + mutation testing + reports
#   quick       - Quick build: compile only, skip tests
#   site        - Generate all reports (JaCoCo, SpotBugs, PMD, etc.)
#   clean       - Clean build artifacts
#   help        - Show this help message
#
# Examples:
#   ./build.sh              - Run standard build
#   ./build.sh test         - Run all tests
#   ./build.sh mutation     - Run mutation testing
#   ./build.sh full-qa      - Run complete QA suite
#
# ============================================================================

set -e

# Navigate to project root (parent of scripts directory)
SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
cd "$SCRIPT_DIR/.."

# Colors for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

# Default command
COMMAND="${1:-install}"

show_help() {
    echo ""
    echo "============================================================================"
    echo " Build Script for Process Builder Extension Library"
    echo "============================================================================"
    echo ""
    echo " Usage: ./build.sh [command]"
    echo ""
    echo " Commands:"
    echo "   install     Standard build: compile + test + package (default)"
    echo "   test        Run all tests (unit + property-based)"
    echo "   pbt         Run only Property-Based Tests"
    echo "   mutation    Run PIT Mutation Testing (slow, ~8-10 min)"
    echo "   full-qa     Full QA: tests + mutation testing + reports"
    echo "   quick       Quick build: compile only, skip tests"
    echo "   site        Generate all reports (JaCoCo, SpotBugs, PMD, etc.)"
    echo "   clean       Clean build artifacts"
    echo "   help        Show this help message"
    echo ""
    echo " Examples:"
    echo "   ./build.sh              Run standard build"
    echo "   ./build.sh test         Run all tests"
    echo "   ./build.sh mutation     Run mutation testing"
    echo "   ./build.sh full-qa      Run complete QA suite"
    echo ""
    echo " Report Locations:"
    echo "   JaCoCo:     target/site/jacoco/index.html"
    echo "   PIT:        target/pit-reports/index.html"
    echo "   SpotBugs:   target/site/spotbugs.html"
    echo "   PMD:        target/site/pmd.html"
    echo "   Checkstyle: target/site/checkstyle.html"
    echo ""
}

run_install() {
    echo ""
    echo -e "${BLUE}[BUILD]${NC} Running standard build (compile + test + package)..."
    echo ""
    ./mvnw clean install
}

run_test() {
    echo ""
    echo -e "${BLUE}[TEST]${NC} Running all tests (unit + property-based)..."
    echo ""
    ./mvnw clean test
}

run_pbt() {
    echo ""
    echo -e "${BLUE}[PBT]${NC} Running Property-Based Tests only..."
    echo ""
    ./mvnw test -Dtest=*PropertyTest
}

run_mutation() {
    echo ""
    echo -e "${YELLOW}[MUTATION]${NC} Running PIT Mutation Testing..."
    echo -e "${YELLOW}[INFO]${NC} This may take 8-10 minutes..."
    echo ""
    ./mvnw clean test pitest:mutationCoverage
    echo ""
    echo -e "${GREEN}[INFO]${NC} Report available at: target/pit-reports/index.html"
}

run_fullqa() {
    echo ""
    echo -e "${YELLOW}[FULL-QA]${NC} Running complete QA suite (tests + mutation + reports)..."
    echo -e "${YELLOW}[INFO]${NC} This may take 10-15 minutes..."
    echo ""
    ./mvnw clean verify site -Pfull-qa
    echo ""
    echo -e "${GREEN}[INFO]${NC} Reports available at: target/site/index.html"
}

run_quick() {
    echo ""
    echo -e "${BLUE}[QUICK]${NC} Running quick build (compile only, skip tests)..."
    echo ""
    ./mvnw clean install -Pquick
}

run_site() {
    echo ""
    echo -e "${BLUE}[SITE]${NC} Generating all reports..."
    echo ""
    ./mvnw clean verify site
    echo ""
    echo -e "${GREEN}[INFO]${NC} Reports available at: target/site/index.html"
}

run_clean() {
    echo ""
    echo -e "${BLUE}[CLEAN]${NC} Cleaning build artifacts..."
    echo ""
    ./mvnw clean
}

# Process commands
case "$COMMAND" in
    help|-h|--help)
        show_help
        ;;
    install)
        run_install
        ;;
    test)
        run_test
        ;;
    pbt)
        run_pbt
        ;;
    mutation|pit)
        run_mutation
        ;;
    full-qa|fullqa)
        run_fullqa
        ;;
    quick)
        run_quick
        ;;
    site)
        run_site
        ;;
    clean)
        run_clean
        ;;
    *)
        echo -e "${RED}[ERROR]${NC} Unknown command: $COMMAND"
        echo ""
        show_help
        exit 1
        ;;
esac
