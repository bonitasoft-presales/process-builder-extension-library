# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

Reusable Java 17+ library for the **Bonita 2024.3 (10.2.0)** ecosystem. Centralizes enums, constants, records, utilities, and execution logic shared across REST API extensions, connectors, actor filters, and process diagrams. Packaged as a JAR and published to GitHub Packages.

## Build Commands

```bash
# Standard build (compile + test + package + javadoc)
./mvnw clean install

# Run all tests only (unit + property-based)
./mvnw clean test

# Run only property-based tests
./mvnw test -Dtest=*PropertyTest

# Quick compile, skip tests
./mvnw clean install -Pquick

# Full verification + site reports (JaCoCo, PMD, Checkstyle, SpotBugs)
./mvnw clean verify site

# Full QA including mutation testing (~10-15 min)
./mvnw clean verify site -Pfull-qa

# Mutation testing only (PIT, ~8-10 min)
./mvnw clean test pitest:mutationCoverage

# CI profile (optimized jqwik tries)
./mvnw clean verify site -Pci

# Run a single test class
./mvnw test -Dtest=MyClassTest

# Run a single test method
./mvnw test -Dtest=MyClassTest#testMethodName
```

Build scripts in `scripts/` wrap these for convenience (`build.sh` / `build.bat`).

## Mandatory Post-Change Verification

Every change must compile and pass all tests before commit:

```bash
mvn clean install site
```

## Architecture

Package root: `com.bonitasoft.processbuilder`

| Package | Purpose |
|---------|---------|
| `constants` | Immutable constants (`Constants`, `SchemaConstants`) |
| `enums` | Domain enums (REST types, process states, authentication, actions, etc.) |
| `records` | Immutable Java records for DTOs (`RestAuthConfig`, `UserRecord`, `InvolvedUsersData`, etc.) |
| `execution` | REST connector execution engine: `ConnectorExecutionEngine` orchestrates `AuthPipeline` -> `HttpExecutor` with `ConnectorRequest`/`ConnectorResponse` and `TemplateSubstitution` |
| `extension` | Utility classes for Bonita API interactions: identity, process, membership, storage, JSON, HTML, SQL, email, password, validation, template resolution |
| `extension.template` | REST API template system with `RestApiTemplate`, auth configs (sealed hierarchy under `AuthConfig`), and `RestApiCatalog` |
| `mapper` | Mapping utilities (`UserMapper`) |
| `validation` | JSON schema validation (`JsonSchemaValidator`, `SchemaResolver`, `ProcessOptionValidator`) |
| `utils` | General utilities (`CompressionUtils`) |

**Key design principles:**
- Immutable data contracts (Java records, final fields) for thread-safety and near-zero GC overhead
- Stateless utility classes with static methods
- Bonita Engine APIs accessed via `TenantAccessor` pattern; never expose internal Bonita model classes
- Services must not cross-call; use Bonita Commands for cross-domain transactions

## Testing Requirements

Every class in `src/main/java` requires **two** test files:
- `MyClassTest.java` - Unit tests (JUnit 5 + AssertJ + Mockito)
- `MyClassPropertyTest.java` - Property-based tests (jqwik `@Property`)

**Coverage targets:** 95%+ line/branch, 85%+ mutation (PIT). Goal is 100%/95%.

Tests run in parallel (JUnit Jupiter concurrent mode). Surefire includes `*Test.java`, `*Tests.java`, `*PropertyTest.java`, `*PropertyTests.java`.

JVM args are required for Mockito/ByteBuddy on Java 17+: `--add-opens java.base/java.lang=ALL-UNNAMED --add-opens java.base/java.lang.reflect=ALL-UNNAMED`.

## Quality Tools

- **JaCoCo** - code coverage (report at `target/site/jacoco/index.html`)
- **PIT/Pitest** - mutation testing (report at `target/pit-reports/index.html`)
- **PMD + CPD** - static analysis and copy-paste detection
- **Checkstyle** - style enforcement (config at `src/main/resources/checkstyle.xml`)
- **SpotBugs** - bug pattern detection
- **OpenAPI Generator** - generates schema docs from `src/main/resources/schemas/openapi.yaml` during `pre-site`

## Detailed Context Files

The `memory-bank/` directory contains detailed rules for specific domains. Read the relevant file when working in that area:
- `00-overview.mdc` - Project purpose and mandates
- `01-architecture.mdc` - Architecture and dependency rules
- `02-datamodel.mdc` - BDM/JPQL naming, indexing, query rules
- `03-quality-standards.mdc` - Static analysis and coverage standards
- `04-performance-java17.mdc` - Java 17 features, Records, Sealed Types, performance patterns
- `05-bonita-integration.mdc` - Bonita REST API and Engine API integration rules
- `06-java-testing-guide.mdc` - AssertJ, Mockito, jqwik patterns and best practices

## Commit Convention

Use conventional commits (`feat`, `fix`, `refactor`, `test`, `docs`, etc.) in English. Releases are automated via GitHub Actions `tagAndRelease.yml` workflow using `maven-release-plugin` (tags as `v<version>`).
