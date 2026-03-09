# Test Authoring Conventions — ROCPS Test Authoring Knowledge Base

> This file defines ALL conventions that MUST be followed when writing new test classes.

---

## Class Structure

```java
package com.subex.rocps.automation.testcases.[systemtesting|functionaltesting];

// Import domain helpers
import com.subex.rocps.automation.helpers.application.MODULE.HelperClass;
// Import framework classes
import com.subex.rocps.automation.helpers.listener.Retry;
import com.subex.roc.helpers.FailureHelper;
import com.subex.rocps.automation.acceptance.PSAcceptanceTest;
import org.testng.annotations.Test;

public class TCClassName extends PSAcceptanceTest {

    // Standard data paths — ALWAYS declare these
    String path = System.getProperty("user.dir") + "\\src\\main\\resources\\";
    String workBookName = "SystemTestCases.xlsx";  // or FunctionalTestCases.xlsx
    String sheetName = "SheetName";

    // Test methods follow...
}
```

---

## Inheritance Chain
```
Assert (TestNG)
  → AcceptanceTest (roc-automation-util)
    → ROCAcceptanceTest (roc-automation)
      → PSAcceptanceTest (rocps-automation)
        → YOUR TEST CLASS
```

**ALWAYS extend `PSAcceptanceTest`.** Never extend any other class.

---

## Test Method Pattern

```java
@Test(
    priority = N,                                    // REQUIRED: sequential, start at 1
    description = "Human readable step description", // REQUIRED
    retryAnalyzer = Retry.class,                     // RECOMMENDED for create/action methods
    groups = {"GroupName"},                           // OPTIONAL: for selective execution
    dependsOnMethods = {"prerequisiteMethod"}         // OPTIONAL: if sequential dependency
)
public void testMethodName() throws Exception {
    try {
        // 1. Instantiate helper with test data
        HelperClass helper = new HelperClass(path, workBookName, sheetName, "TestDataKey", 1);

        // 2. Execute operation
        helper.operationMethod();

    } catch (Exception e) {
        FailureHelper.setErrorMessage(e);            // REQUIRED: always log before throw
        throw e;                                      // REQUIRED: always re-throw
    }
}
```

---

## Naming Conventions

### Test Classes
- **PascalCase**, prefixed with `TC` for test cases: `TCAccount`, `TCVoiceStream`
- **Exception**: System test prerequisite classes without TC prefix: `BillPreRequisites`, `ROCPreRequisites2`

### Test Methods
- **camelCase**, action-oriented
- CRUD pattern: `accountCreation()`, `accountDelete()`, `accountUnDelete()`, `editAccount()`
- Validation: `accountColVal()`, `searchScreenColumnsValidation()`
- With variant: `dealTieredInCreation()`, `dealCommittedOutCreation()`

### Test Data Keys
| Pattern | Example | When to use |
|---|---|---|
| Function-based | `AccountCustomer` | Simple entity creation |
| Action-based | `AccountDelete` | CRUD operations |
| Variant-based | `Test01Deal` | Multiple variants |
| Hierarchical | `T1_Destination` | Tiered/leveled data |
| Validation | `AccountSearchScreencolVal` | Column validation |

---

## Helper Instantiation Patterns

### Pattern 1: Single helper per method (most common)
```java
public void createAccount() throws Exception {
    try {
        Account acc = new Account(path, workBookName, sheetName, "AccountCustomer", 1);
        acc.accountCreation();
    } catch (Exception e) {
        FailureHelper.setErrorMessage(e);
        throw e;
    }
}
```

### Pattern 2: Multiple helpers in one method
```java
public void billProfileCreation() throws Exception {
    try {
        Account acc = new Account(path, workBookName, sheetName, "AccountCustomer", 1);
        acc.accountCreation();

        BillProfile bp = new BillProfile(path, workBookName, sheetName, "BillProfileInvoice", 1);
        bp.billProfileCreation();
    } catch (Exception e) {
        FailureHelper.setErrorMessage(e);
        throw e;
    }
}
```

### Pattern 3: Multiple instances of same helper
```java
public void createEventNormalization() throws Exception {
    try {
        for (int i = 1; i <= 4; i++) {
            EventNormalization en = new EventNormalization(
                path, workBookName, sheetName, "Normalization" + i, 1);
            en.createEventNormalization();
        }
    } catch (Exception e) {
        FailureHelper.setErrorMessage(e);
        throw e;
    }
}
```

### Pattern 4: Different sheet for rate/sub-operations
```java
public void dealRate() throws Exception {
    try {
        // Note: different sheetName for rate data
        DealRate rate = new DealRate(path, workBookName, "DealRate", "Test01DealRate", 1);
        rate.dealRateCreation();
    } catch (Exception e) {
        FailureHelper.setErrorMessage(e);
        throw e;
    }
}
```

---

## Annotation Rules

### Priority
- Start at 1, increment sequentially
- No gaps in priority numbers within a class
- Priority determines execution order

### RetryAnalyzer
- **Include** on: creation, configuration, action methods
- **Omit** on: delete/undelete, simple validation (optional)
- Always use: `retryAnalyzer = com.subex.rocps.automation.helpers.listener.Retry.class`
- Allows up to 2 retries on failure

### Groups
- Used primarily in system testing for selective execution
- Common groups: `"Prerequisites3"`, `"Prerequisites4"`
- Matches TestNG suite XML `<groups>` configuration

### dependsOnMethods
- Use when a test CANNOT run unless a prior test passes
- Single: `dependsOnMethods = {"createAccount"}`
- Multiple: `dependsOnMethods = {"method1", "method2"}`
- Creates a chain: if dependency fails, dependent tests are SKIPPED (not failed)

### Enabled
- Default is `true` — omit the flag unless disabling
- Use `enabled = false` to temporarily disable a test
- Prefer commenting out the method for long-term disabling

---

## Error Handling

### ALWAYS use this pattern:
```java
try {
    // test logic
} catch (Exception e) {
    FailureHelper.setErrorMessage(e);  // Log error to report
    throw e;                            // Re-throw to fail the test
}
```

### NEVER:
- Swallow exceptions without re-throwing
- Use `FailureHelper.reportFailure()` without throwing (test will appear to pass)
- Add `@Test` on constructors (TestNG 7.x rejects this)

---

## Wait Strategy

```java
// After navigation or save — wait for loading overlay to disappear
GenericHelper.waitForLoadmask(30);  // seconds

// After page load — explicit wait for specific element
WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("elementId")));

// NEVER use:
// Thread.sleep()    — fragile, slow
// TimeUnit          — deprecated in Selenium 4.x, use Duration.ofSeconds()
```

---

## File Upload

```java
// GWT file dialogs don't support sendKeys — ALWAYS use Robot class
FileUpload.uploadWithRobot(filePath);

// NEVER use:
// element.sendKeys(filePath) — won't work with GWT file dialogs
```

---

## Navigation

```java
// Navigate to a screen using helper
ROCPSHelper.navigateToMenu("Bills > Billing Streams");
GenericHelper.waitForLoadmask(30);

// Or use NavigationHelper
NavigationHelper.navigateToScreen("screenName", "screenTitle");
```

---

## WebDriver Rules

- **Static WebDriver** — single browser instance shared across ALL test classes
- **NEVER create new WebDriver instances** in test classes
- Use the inherited `driver` field from AcceptanceTest
- Session timeout is handled automatically by `ROCHelper.handleSessionTimeout()` in `@BeforeMethod`

---

## Data from Excel

```java
// Standard pattern
String value = ExcelReader.getCellData("SheetName", rowNum, "ColumnName");

// Multi-value fields (pipe-separated)
String[] banks = bankName.split("\\|", -1);
for (String bank : banks) { ... }

// Using ExcelHolder (ROCPS-specific)
ExcelHolder holder = new ExcelHolder(dataMap);
Map<String, String> row = holder.dataMap(0);
String accountName = ExcelHolder.getKey(row, "AccountName");
```

---

## Things to NEVER Do

1. **Never put `@Test` on constructors** — TestNG 7.x rejects this
2. **Never create new WebDriver instances** — use the shared static driver
3. **Never use `Thread.sleep()`** — use `GenericHelper.waitForLoadmask()` or `WebDriverWait`
4. **Never use `TimeUnit`** — use `Duration.ofSeconds()` (Selenium 4.x)
5. **Never hardcode locators** — use OR keys via `GenericHelper.getORProperty()`
6. **Never use `sendKeys` for file upload** — use `FileUpload.uploadWithRobot()`
7. **Never swallow exceptions** — always `FailureHelper.setErrorMessage(e)` + `throw e`
8. **Never skip `waitForLoadmask()`** — required after navigation and save actions
9. **Never modify `roc-automation-util` classes** without rebuilding all 3 projects
10. **Never use raw Selenium API** when a component helper exists

---

## Checklist for New Test Classes

- [ ] Extends `PSAcceptanceTest`
- [ ] Package is `com.subex.rocps.automation.testcases.[systemtesting|functionaltesting]`
- [ ] `path`, `workBookName`, `sheetName` declared as class fields
- [ ] Every `@Test` method has `priority` (starting at 1)
- [ ] Every `@Test` method has `description`
- [ ] `retryAnalyzer = Retry.class` on creation/action methods
- [ ] `dependsOnMethods` used where sequential dependency exists
- [ ] All methods throw `Exception`
- [ ] Try-catch with `FailureHelper.setErrorMessage(e)` + `throw e`
- [ ] Correct helper class used (check `helper_index.md`)
- [ ] Test data key matches a row in the Excel sheet
- [ ] `GenericHelper.waitForLoadmask()` after navigation/save operations
- [ ] No raw Selenium code (use component helpers)
- [ ] No hardcoded locators (use OR keys)
