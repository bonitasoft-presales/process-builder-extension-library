# AGENTS.md: PROCESS-BUILDER-EXTENSION-LIBRARY AI Context Index

## 1. Purpose of this Document
This file serves as the unified entry point for all AI coding agents (GitHub Copilot, Gemini, Cursor, etc.) working on the PROCESS-BUILDER project.

## 2. Mandatory Rules and Context
The following files contain all mandatory conventions, architecture principles, and quality standards for this project. **The AI agent must prioritize the content of these files over general knowledge or conflicting advice.**

### 2.1. Project Overview and Core Mandates
* **File:** context-ia/00-overview.mdc
* **Content:** Defines the library's primary purpose, key goals (immutability, standardization), team context, and mandatory documentation references.

### 2.2. Architectural Principles & Technology Stack
* **File:** context-ia/01-architecture.mdc
* **Content:** Defines the technology stack (Java 17+), dependency rules, and general code structure (Service, API, Utility layers).

### 2.3. Business Data Model (BDM) & Database Rules
* **File:** context-ia/02-datamodel.mdc
* **Content:** **CRITICAL FOR BDM/JPQL.** Contains all naming conventions, data integrity rules, indexing requirements, and performance-based query requirements.

### 2.4. Code Quality & Static Analysis Standards
* **File:** context-ia/03-quality-standards.mdc
* **Content:** **CRITICAL FOR COVERAGE/PMD/CHECKSTYLE.** Defines mandatory testing requirements, the **100% test coverage goal**, and adherence to static analysis tools.

### 2.5. Performance & Modern Java 17 Practices
* **File:** context-ia/04-performance-java17.mdc
* **Content:** **CRITICAL FOR OPTIMIZATION.** Defines advanced best practices for using Java 17 features (Records, Sealed Types, Pattern Matching) to achieve **maximum performance and minimal GC overhead.**

### 2.6. Bonita API & Integration Rules
* **File:** context-ia/05-bonita-integration.mdc
* **Content:** Defines REST API naming conventions, security considerations, and specific instructions for using core Bonita Engine APIs (e.g., `TenantAccessor`, Command API).

