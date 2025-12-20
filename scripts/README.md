# Build Scripts

Scripts to facilitate the execution of different types of builds and tests.

## Usage

### Windows
```cmd
scripts\build.bat [command]
```

### Linux/Mac
```bash
chmod +x scripts/build.sh  # First time only
./scripts/build.sh [command]
```

## Available Commands

| Command | Description |
|---------|-------------|
| `install` | Standard build: compile + test + package (default) |
| `test` | Run all tests (unit + property-based) |
| `pbt` | Run only Property-Based Tests |
| `mutation` | Run PIT Mutation Testing |
| `full-qa` | Full QA: tests + mutation + reports |
| `quick` | Quick build: compile only, skip tests |
| `site` | Generate all reports (JaCoCo, SpotBugs, etc.) |
| `clean` | Clean build artifacts |
| `help` | Show help |

## Examples

```bash
# Standard build
./scripts/build.sh

# Run all tests
./scripts/build.sh test

# Mutation testing (when you need to validate test quality)
./scripts/build.sh mutation

# Full QA before a release
./scripts/build.sh full-qa

# Quick build for development
./scripts/build.sh quick
```

## Report Locations

| Report | Path |
|--------|------|
| JaCoCo (Coverage) | `target/site/jacoco/index.html` |
| PIT (Mutations) | `target/pit-reports/index.html` |
| SpotBugs | `target/site/spotbugs.html` |
| PMD | `target/site/pmd.html` |
| Checkstyle | `target/site/checkstyle.html` |
| CPD (Duplicates) | `target/site/cpd.html` |

## When to Use Each Command

| Situation | Command |
|-----------|---------|
| Normal development | `install` or `quick` |
| Before commit/push | `test` |
| Validate new tests | `mutation` |
| Before release | `full-qa` |
| Review code quality | `site` |

## GitHub Actions

You can also run mutation testing from GitHub:

1. Go to the **Actions** tab on GitHub
2. Select **Mutation Testing**
3. Click **Run workflow**
4. View the report in the workflow summary or download from artifacts
