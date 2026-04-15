# Discord Testing Suite (CEN4072)

## Overview
This project is a Selenium-based automated testing suite for the Discord web application, developed for the CEN4072 Software Testing course at FGCU.

The goal of this project is to design and implement a structured, maintainable test suite using TestNG, while demonstrating concepts such as system testing, end-to-end testing, and test integration.

---

## Current Branch Purpose
This branch (`chris-suite-setup`) focuses on:

- Refactoring the project into a clean, maintainable structure
- Establishing a shared test architecture (`BaseTest`)
- Standardizing naming conventions and setup
- Preparing the foundation for the full test suite

---

## Environment Setup (IMPORTANT)

To ensure consistency between team members, use the following setup:

- **Browser:** Google Chrome (same version family recommended)
- **Java:** Java 17+ (currently tested on Java 25)
- **Build Tool:** Maven
- **Testing Framework:** TestNG
- **Automation Tool:** Selenium WebDriver

---

## Project Structure (Maven Standard)
```text
discordTestingSuite/
├── src/
│   ├── main/java/
│   └── test/java/
│       └── org/example/
├── pom.xml
├── testng.xml
```

---

## Architecture

### BaseTest
Provides shared setup and teardown for all test classes:
- WebDriver initialization
- WebDriverWait
- Actions
- Browser lifecycle management

All test classes should extend `BaseTest`.

---

### LoginTests
Handles authentication-related test cases:
- Valid login
- Invalid password
- Empty input fields
- Navigation to register page
- Navigation to forgot password

---

## Planned Test Classes (Minimum Requirement)

The project will include at least 8 test classes:

1. LoginTests
2. NavigationTests
3. ServerTests
4. ChannelTests
5. DirectMessageTests
6. UserProfileTests
7. SettingsTests
8. LogoutOrSessionTests

---

## Priority Order (Recommended)

To ensure stability and progress:

1. LoginTests
2. NavigationTests
3. SettingsTests
4. UserProfileTests
5. DirectMessageTests
6. ServerTests
7. ChannelTests
8. LogoutOrSessionTests
9. Optional extras (if time permits)

---

## Coding Standards

- Use **PascalCase** for class names
- Use clear, descriptive method names
- Keep code **self-documenting**
- Add comments only when necessary
- Avoid duplication by using shared base classes

---

## Package Naming

Planned package structure:
```
edu.fgcu.cen4072.discordtests
```

(Currently using `org.example`, will be updated during refactor)

---

## Test Design Approach

Although the assignment references "unit tests", this project primarily implements:

- **System Testing**
- **End-to-End Testing**
- **Integration via TestNG suite**

This is due to the use of Selenium on a real-world web application.

---

## Known Constraints

Discord presents several automation challenges:

- Dynamic UI elements
- Possible rate limiting
- Bot detection behaviors
- Frequent UI updates

To address this:
- Use explicit waits (WebDriverWait)
- Avoid rapid repeated actions
- Avoid parallel execution
- Keep tests stable and focused

---

## Credentials (Temporary)

Currently using a test account:

- Email: discord.testing.fgcu@gmail.com
- Username: fgcu_test_user

⚠️ NOTE: These are temporary and will be moved to a configuration-based approach soon.

---

## Git Workflow

- `main` → stable branch
- `nolanTests` → teammate work
- `chris-suite-setup` → refactor and setup work

Work should be done in feature branches and merged when stable.

---

## Current Status

- BaseTest implemented
- LoginTests refactored to use BaseTest
- Test execution verified (5/5 passing)
- Environment confirmed working

---

## Next Steps

- Move credentials to config (remove hardcoding)
- Refactor remaining classes to use BaseTest
- Implement remaining test classes
- Configure testng.xml for full suite execution
- Improve locator stability and reliability

---

## Team Notes

- Standardize environment before running tests
- Avoid modifying core structure without coordination
- Focus on stability before expanding test coverage
- Communicate major changes via commits or README updates