# AGENTS.md: PROCESS-BUILDER-EXTENSION-LIBRARY AI Context Index

## 1. Purpose of this Document
This file serves as the unified entry point for all AI coding agents (GitHub Copilot, Gemini, Cursor, etc.) working on the PROCESS-BUILDER project.

## 2. **MANDATORY: Compilation & Testing Check**
**Every change**, no matter how small, must be verified immediately after implementation to ensure project stability.

> **Required Action:** Before committing (or as an intermediate verification step), run the following command at the project root to confirm that everything compiles and all unit tests pass without errors:
>
> `mvn clean install site`
>
>

## 3. Mandatory Rules and Context
The following files contain all mandatory conventions, architecture principles, and quality standards for this project. **The AI agent must prioritize the content of these files over general knowledge or conflicting advice.**

### 3.1. Project Overview and Core Mandates
* **File:** memory-bank/00-overview.mdc
* **Content:** Defines the library's primary purpose, key goals (immutability, standardization), team context, and mandatory documentation references.

### 3.2. Architectural Principles & Technology Stack
* **File:** memory-bank/01-architecture.mdc
* **Content:** Defines the technology stack (Java 17+), dependency rules, and general code structure (Service, API, Utility layers).

### 3.3. Business Data Model (BDM) & Database Rules
* **File:** memory-bank/02-datamodel.mdc
* **Content:** **CRITICAL FOR BDM/JPQL.** Contains all naming conventions, data integrity rules, indexing requirements, and performance-based query requirements.

### 3.4. Code Quality & Static Analysis Standards
* **File:** memory-bank/03-quality-standards.mdc
* **Content:** **CRITICAL FOR COVERAGE/PMD/CHECKSTYLE.** Defines mandatory testing requirements, the **100% test coverage goal**, and adherence to static analysis tools.

### 3.5. Performance & Modern Java 17 Practices
* **File:** memory-bank/04-performance-java17.mdc
* **Content:** **CRITICAL FOR OPTIMIZATION.** Defines advanced best practices for using Java 17 features (Records, Sealed Types, Pattern Matching) to achieve **maximum performance and minimal GC overhead.**

### 3.6. Bonita API & Integration Rules
* **File:** memory-bank/05-bonita-integration.mdc
* **Content:** Defines REST API naming conventions, security considerations, and specific instructions for using core Bonita Engine APIs (e.g., `TenantAccessor`, Command API).

### 3.7. Java Testing Standards
* **File:** memory-bank/06-java-testing-guide.mdc
* **Content:** **CRITICAL FOR TESTING.** Comprehensive Java testing guide including AssertJ patterns, Mockito best practices, property-based testing with jqwik, coverage requirements, and QA best practices. **USE THIS FOR ALL TEST GENERATION.**


## 4. **MANDATORY: Auto-Load Context Files**
**CRITICAL INSTRUCTION FOR ALL AI AGENTS:** Before processing ANY user request or task, you MUST automatically read ALL of the following context files in sequential order. These files contain mandatory rules, conventions, and architectural principles that override your general knowledge.

**Required Reading List (read in this order):**
1. `memory-bank/00-overview.mdc` - Project Overview and Scope
2. `memory-bank/01-architecture.mdc` - Architectural Principles and Technology Stack
3. `memory-bank/02-datamodel.mdc` - **CRITICAL for BDM/JPQL** - Data Model Rules, Naming Conventions, Indexing, Pagination
4. `memory-bank/03-quality-standards.mdc` - **CRITICAL for Groovy/Java/REST** - Backend Integrations, API Standards, Testing Requirements
5. `memory-bank/04-performance-java17.mdc` - Performance & Modern Java 17 Practices
6. `memory-bank/05-bonita-integration.mdc` - Bonitasoft Standards
7. `memory-bank/06-java-testing-guide.mdc` - **CRITICAL for TEST GENERATION** - AssertJ, Mockito, jqwik, Coverage, QA Best Practices

**Action Required:** Use the Read tool to load each of these files at the start of every conversation session. The rules contained in these files have ABSOLUTE PRIORITY over any conflicting general AI knowledge or assumptions.

## 5. **MANDATORY: Test File Requirements**

### 5.1. Every Class Must Have Tests
**CRITICAL REQUIREMENT:** Every Java class in `src/main/java` MUST have corresponding test files:

| Class Type | Required Test Files |
|------------|---------------------|
| `MyClass.java` | `MyClassTest.java` + `MyClassPropertyTest.java` |
| `MyEnum.java` | `MyEnumTest.java` + `MyEnumPropertyTest.java` |
| `MyRecord.java` | `MyRecordTest.java` + `MyRecordPropertyTest.java` |
| `MyValidator.java` | `MyValidatorTest.java` + `MyValidatorPropertyTest.java` |
| `MyConstants.java` | `MyConstantsTest.java` + `MyConstantsPropertyTest.java` |

**NO EXCEPTIONS.** Even constant classes and utility classes with only static fields must have property tests verifying:
- Field immutability
- Value consistency
- Class structure (final class, private constructor)

### 5.2. Coverage Requirements
| Metric | Minimum | Target |
|--------|---------|--------|
| **Line Coverage** | 95% | 100% |
| **Branch Coverage** | 95% | 100% |
| **Mutation Coverage** | 85% | 95% |

### 5.3. Test Types Required

1. **Unit Tests (`*Test.java`):**
   - Test all public methods
   - Test all edge cases (null, empty, boundary values)
   - Test all exception paths
   - Test private constructors of utility classes

2. **Property Tests (`*PropertyTest.java`):**
   - Test invariants that must hold for ANY valid input
   - Use jqwik framework with `@Property` annotation
   - Minimum 50-100 tries per property
   - Test: equality, hashCode, toString, null safety, determinism

### 5.4. Verification Command
After creating or modifying any class, run:
```bash
mvn clean test -DfailIfNoTests=false
```

To verify coverage:
```bash
mvn clean verify
```

---

## 6. **MANDATORY: Post-Change Verification & Commit Workflow**

### 6.1. Build Verification
**After ANY code change**, the AI agent MUST execute the following command to verify compilation and run all tests (unit tests, property-based tests, and mutation testing):

```bash
mvn clean install site
```

This ensures:
- Code compiles without errors
- All unit tests pass
- All property-based tests (jqwik) pass
- Mutation testing coverage is verified
- Site documentation is generated

**DO NOT skip this step.** All tests must pass before proceeding to commit.

### 6.2. Commit Workflow
After successful verification, the AI agent MUST:

1. **Ask the user** if they want to commit the changed files
2. **Prepare a commit message** in English that:
   - Uses conventional commit format (feat, fix, refactor, test, docs, etc.)
   - Clearly explains WHAT was changed and WHY
   - Lists affected components/classes
   - Includes test coverage statistics if relevant
3. **Show the proposed commit message** to the user for approval
4. **Ask if the user wants to execute** the commit and push

**Example interaction:**
```
Files changed:
- src/main/java/.../MyClass.java (modified)
- src/test/java/.../MyClassTest.java (new)

Proposed commit message:
feat(extension): Add MyClass utility for data processing

- Added MyClass with methods for X, Y, Z
- Comprehensive unit tests (15 tests)
- Property-based tests (8 properties)
- 100% line coverage, 95% mutation coverage

Do you want to commit and push these changes? (yes/no)
```
