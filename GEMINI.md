# Gemini Project Configuration

This document provides the context and guidelines for the AI software engineering agent, Gemini, to effectively contribute to this project. Adhering to these standards is crucial for maintaining code quality, consistency, and a smooth development workflow.

## 1. Project Overview

*   **Purpose:** [A brief, one-sentence description of the project's goal. e.g., "A Kotlin Multiplatform library for...", "A React-based web application for..."]
*   **Problem Solved:** [What problem does this project solve for its users? e.g., "It simplifies X by providing Y."]

## 2. Getting Started

To ensure the agent can build, run, and test the project, the following commands and prerequisites are defined.

*   **Prerequisites:**
    This project is designed to be flexible with tool versions. The following table lists the *minimum required* versions and the versions the project was *last known to work with*. If you encounter issues, please ensure your environment meets at least the minimum requirements.

    | Tool      | Minimum Version | Tested Version |
    | :-------- | :-------------- | :------------- |
    | JDK       | 11              | 17.0.8         |
    | Node.js   | 18.x            | 20.10.0        |
    | ...       | ...             | ...            |
*   **Build Command:**
    ```bash
    # <BUILD_COMMAND>
    ```
*   **Run Command:**
    ```bash
    # <RUN_COMMAND>
    ```
*   **Test Command:**
    ```bash
    # <TEST_COMMAND>
    ```

## 3. Technology Stack

*   **Languages:** [e.g., Kotlin, TypeScript, Python]
*   **Frameworks/Runtimes:** [e.g., Ktor, Node.js, React, Spring Boot]
*   **Key Libraries:** [e.g., Koin for DI, kotlinx.serialization for JSON, Jest for testing]
*   **Package/Dependency Manager:** [e.g., Gradle, NPM, Pip]

## 4. Architectural Principles

This project prioritizes a clean, maintainable, and scalable architecture. Our approach is guided by respecting the conventions of our technology stack while adhering to universal design principles.

### Adherence to Conventions

The primary guide for code structure, naming, and patterns should be the established conventions of the technologies listed in the `Technology Stack` section.

*   **Framework-specific patterns:** If the project uses a framework (e.g., Rails, Django, React, Spring), follow its idiomatic way of organizing code (e.g., MVC, feature-based folders).
*   **Language-specific idioms:** Employ the common practices and styles of the primary programming language.

### Universal Design Principles

Beyond specific conventions, the following timeless principles must be applied:

*   **Separation of Concerns (SoC):** The codebase must be organized into distinct layers with clear responsibilities. Logic from different domains (e.g., presentation/UI, business logic, data access) should not be mixed.
*   **Dependency Rule:** Dependencies must flow in one direction. Higher-level modules (like business logic) should not depend on lower-level modules (like data access or UI). Abstractions should be used to invert control.
*   **Single Responsibility Principle (SRP):** Every class, function, or module should have only one reason to change. Avoid large, multi-purpose entities in favor of small, focused ones.
*   **Consistency:** The code should be internally consistent. If a certain pattern or approach is used in one place, it should be used everywhere else it applies.

## 5. Coding Conventions & Best Practices

### The Prime Directive: Mimic Existing Code

**This is the most important rule.** Before writing any new code, you must first understand the style, patterns, and idioms of the surrounding code. The primary goal is to write code that is indistinguishable from what is already there.

*   **Pattern Adherence:** If the existing code uses a specific design pattern (e.g., the Builder pattern for object creation), you must use it for similar tasks.
*   **Style Consistency:** Match the local coding style for everything: naming conventions, formatting, commenting, and file structure.
*   **No Unilateral Refactoring:** Do not introduce a new pattern or style without explicit instruction. If you identify a potential improvement, you must propose it first. Your primary role is to extend and maintain the existing codebase's conventions, not to reinvent them.

### Code Style and Quality Enforcement

Maintaining a consistent and high-quality codebase is enforced through automated tooling.

1.  **Use Project-Defined Tools:** Before making any changes, identify and use the project's established formatting and linting tools (e.g., Ktlint, ESLint, Black, Ruff). Configuration files for these tools (e.g., `.eslintrc.js`, `pyproject.toml`) are the source of truth for the project's style.
2.  **Propose a Standard if Missing:** If the project lacks a defined formatter or linter, the agent is responsible for proposing one. This involves:
    *   Researching the most popular, community-accepted tool for the project's primary language (e.g., `Ktlint` for Kotlin, `Prettier` for TypeScript).
    *   Proposing the addition and configuration of this tool.
    *   Configuring the tool with the strictest, most opinionated rule set available to ensure maximum consistency.

### General Rules

1.  **Compiles & Passes Checks:** All code must compile successfully and pass all linter/static analysis checks before being considered complete.
2.  **No Deprecated Features:** Do not use deprecated language features or library functions.
3.  **Immutability:** Prefer immutable data structures and `val`/`const` over `var`/`let` wherever possible.

### Style and Idioms

*   **Follow Existing Style:** Mimic the formatting, naming, and architectural patterns of the existing codebase.
*   **Leverage Language Features:** Use modern, idiomatic language constructs. For example, prefer functional programming constructs (e.g., `map`, `filter`, `reduce`) over imperative loops where it improves readability and conciseness.
*   **Readability:** Write clear, self-documenting code. Add comments only to explain the *why* behind complex logic, not the *what*.

### Dependency Management

*   **Justification Required:** Do not add a new third-party library without a strong justification.
*   **Version Control:** Do not downgrade library versions. When upgrading, ensure changes are tested thoroughly.

## 6. Development Workflow

### Git & Commits

*   **Commit Messages:** Follow the [Conventional Commits](https://www.conventionalcommits.org/) specification. Each commit message should be clear and concise.
    *   Example: `feat(auth): add support for PKCE flow`
    *   Example: `fix(parser): handle malformed JSON input gracefully`
*   **Atomic Commits:** Each commit should represent a single, logical change.

### Testing

A robust and fast test suite is critical for maintaining project quality and a rapid development feedback loop.

#### The Testing Pyramid

This project adheres to the Testing Pyramid model. The test suite should be composed of:

1.  **Unit Tests (Vast Majority):** These tests are small, fast, and verify a single unit of work in isolation (e.g., a single function or class). They should form the largest portion of our tests.
2.  **Integration Tests (Some):** These tests verify that several units work correctly together (e.g., an API endpoint interacting with a database). They are slower and more complex than unit tests.
3.  **End-to-End (E2E) Tests (Very Few):** These tests validate a full user workflow from the UI to the database. They are the slowest and most brittle and should be used sparingly for the most critical paths.

#### Performance Budget

The entire test suite **must** complete in **under 60 seconds**. A fast feedback loop is non-negotiable.

If adding or modifying tests causes the suite to exceed this limit, the priority is to:
1.  First, look for opportunities to optimize existing slow tests.
2.  If optimization is not enough, re-evaluate and consider removing lower-value or redundant tests.
3.  The performance budget must be maintained.