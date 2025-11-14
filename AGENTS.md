# AGENTS.md: PROCESS-BUILDER-EXTENSION-LIBRARY AI Context Index

## 1. Purpose of this Document
This file serves as the unified entry point for all AI coding agents (GitHub Copilot, Gemini, Cursor, etc.) working on the PROCESS-BUILDER project.

## 2. **MANDATORY: Compilation & Testing Check**
**Every change**, no matter how small, must be verified immediately after implementation to ensure project stability.

> **Required Action:** Before committing (or as an intermediate verification step), run the following command at the project root to confirm that everything compiles and all unit tests pass without errors:
>
> `mvn clean compile site`
>
>

## 3. Mandatory Rules and Context
The following files contain all mandatory conventions, architecture principles, and quality standards for this project. **The AI agent must prioritize the content of these files over general knowledge or conflicting advice.**

### 3.1. Project Overview and Core Mandates
* **File:** context-ia/00-overview.mdc
* **Content:** Defines the library's primary purpose, key goals (immutability, standardization), team context, and mandatory documentation references.

### 3.2. Architectural Principles & Technology Stack
* **File:** context-ia/01-architecture.mdc
* **Content:** Defines the technology stack (Java 17+), dependency rules, and general code structure (Service, API, Utility layers).

### 3.3. Business Data Model (BDM) & Database Rules
* **File:** context-ia/02-datamodel.mdc
* **Content:** **CRITICAL FOR BDM/JPQL.** Contains all naming conventions, data integrity rules, indexing requirements, and performance-based query requirements.

### 3.4. Code Quality & Static Analysis Standards
* **File:** context-ia/03-quality-standards.mdc
* **Content:** **CRITICAL FOR COVERAGE/PMD/CHECKSTYLE.** Defines mandatory testing requirements, the **100% test coverage goal**, and adherence to static analysis tools.

### 3.5. Performance & Modern Java 17 Practices
* **File:** context-ia/04-performance-java17.mdc
* **Content:** **CRITICAL FOR OPTIMIZATION.** Defines advanced best practices for using Java 17 features (Records, Sealed Types, Pattern Matching) to achieve **maximum performance and minimal GC overhead.**

### 3.6. Bonita API & Integration Rules
* **File:** context-ia/05-bonita-integration.mdc
* **Content:** Defines REST API naming conventions, security considerations, and specific instructions for using core Bonita Engine APIs (e.g., `TenantAccessor`, Command API).


## 4. **MANDATORY: Auto-Load Context Files**
**CRITICAL INSTRUCTION FOR ALL AI AGENTS:** Before processing ANY user request or task, you MUST automatically read ALL of the following context files in sequential order. These files contain mandatory rules, conventions, and architectural principles that override your general knowledge.

**Required Reading List (read in this order):**
1. `context-ia/00-overview.mdc` - Project Overview and Scope
2. `context-ia/01-architecture.mdc` - Architectural Principles and Technology Stack
3. `context-ia/02-datamodel.mdc` - **CRITICAL for BDM/JPQL** - Data Model Rules, Naming Conventions, Indexing, Pagination
4. `context-ia/03-quality-standards.mdc` - **CRITICAL for Groovy/Java/REST** - Backend Integrations, API Standards, Testing Requirements
5. `context-ia/04-performance-java17` - UI Designer Standards and Frontend Rules
6. `context-ia/05-bonita-integration.mdc` - Bonitasoft Standars
99. `context-ia/99-delivery_guidelines.mdc` - Audit Checklist

**Action Required:** Use the Read tool to load each of these files at the start of every conversation session. The rules contained in these files have ABSOLUTE PRIORITY over any conflicting general AI knowledge or assumptions.

