# Test Patterns & Examples — ROCPS Test Authoring Knowledge Base

> This file documents the 9 test patterns found across 266 test classes, with representative examples.

## Test Suite Overview

- **Total Test Classes:** 266
- **Functional Testing:** 236 classes
- **System Testing:** 30 classes
- **Framework:** TestNG 7.5.1
- **Base Class:** PSAcceptanceTest

---

## Pattern 1: Simple CRUD Operations (Most Common — ~150 classes)

**Structure:** Create → Edit → Delete → Undelete → Column Validation
**Methods per class:** 8-12

### Example: TCAccount.java
```java
package com.subex.rocps.automation.testcases.functionaltesting;

public class TCAccount extends PSAcceptanceTest {
    String path = System.getProperty("user.dir") + "\\src\\main\\resources\\";
    String workBookName = "FunctionalTestCases.xlsx";
    String sheetName = "Account";

    @Test(priority = 1, description = "Account creation",
          retryAnalyzer = Retry.class)
    public void accountCreation() throws Exception {
        try {
            Account acc = new Account(path, workBookName, sheetName, "AccountCustomer", 1);
            acc.accountCreation();
        } catch (Exception e) {
            FailureHelper.setErrorMessage(e);
            throw e;
        }
    }

    @Test(priority = 2, description = "Vendor account creation",
          retryAnalyzer = Retry.class)
    public void accountVendorCreation() throws Exception {
        try {
            Account acc = new Account(path, workBookName, sheetName, "AccountVendor", 1);
            acc.accountCreation();
        } catch (Exception e) {
            FailureHelper.setErrorMessage(e);
            throw e;
        }
    }

    // ... more creation variants (priority 3-6)

    @Test(priority = 7, description = "Account deletion")
    public void accountDelete() throws Exception {
        try {
            Account acc = new Account(path, workBookName, sheetName, "AccountDelete", 1);
            acc.accountDelete();
        } catch (Exception e) {
            FailureHelper.setErrorMessage(e);
            throw e;
        }
    }

    @Test(priority = 8, description = "Account undelete")
    public void accountUnDelete() throws Exception {
        try {
            Account acc = new Account(path, workBookName, sheetName, "AccountUnDelete", 1);
            acc.accountUnDelete();
        } catch (Exception e) {
            FailureHelper.setErrorMessage(e);
            throw e;
        }
    }

    @Test(priority = 9, description = "Search Screen column Validation")
    public void accountColVal() throws Exception {
        try {
            Account acc = new Account(path, workBookName, sheetName, "AccountSearchScreencolVal", 1);
            acc.searchScreenColumnsValidation();
        } catch (Exception e) {
            FailureHelper.setErrorMessage(e);
            throw e;
        }
    }

    @Test(priority = 10, description = "Edit account")
    public void editAccount() throws Exception {
        try {
            Account acc = new Account(path, workBookName, sheetName, "AccountEdit", 1);
            acc.editAccount();
        } catch (Exception e) {
            FailureHelper.setErrorMessage(e);
            throw e;
        }
    }
}
```

**Key characteristics:**
- Each method creates a new helper instance with different test data key
- No `dependsOnMethods` — tests are independent
- `retryAnalyzer` on creation methods, often omitted on delete/validation
- Single helper class used throughout

---

## Pattern 2: Complex Workflow with Dependency Chain (~30 classes)

**Structure:** Long sequential chain where each step depends on the previous
**Methods per class:** 12-25+

### Example: BillPreRequisites.java
```java
package com.subex.rocps.automation.testcases.systemtesting;

public class BillPreRequisites extends PSAcceptanceTest {
    String path = System.getProperty("user.dir") + "\\src\\main\\resources\\";
    String workBookName = "SystemTestCases.xlsx";
    String sheetName = "BillPreRequisites";

    @Test(priority = 1, description = "Create billing stream",
          retryAnalyzer = Retry.class, groups = {"Prerequisites4"})
    public void createBillingstream() throws Exception {
        try {
            Streams stream = new Streams(path, workBookName, sheetName, "BillingStreamConfig", 1);
            stream.billingStreamConfig();
        } catch (Exception e) {
            FailureHelper.setErrorMessage(e);
            throw e;
        }
    }

    @Test(priority = 2, description = "Create bill breakdown extra field",
          retryAnalyzer = Retry.class, groups = {"Prerequisites4"},
          dependsOnMethods = {"createBillingstream"})
    public void createBillBreakdownExtraField() throws Exception {
        try {
            BillBreakdownExtraField bbef = new BillBreakdownExtraField(
                path, workBookName, sheetName, "BillBreakdownExtraField", 1);
            bbef.billBreakdownExtraFieldCreation();
        } catch (Exception e) {
            FailureHelper.setErrorMessage(e);
            throw e;
        }
    }

    @Test(priority = 3, dependsOnMethods = {"createBillBreakdownExtraField"}, ...)
    public void createBillBreakdownConfig() throws Exception { ... }

    @Test(priority = 4, dependsOnMethods = {"createBillBreakdownConfig"}, ...)
    public void createBillBreakdownInput() throws Exception { ... }

    @Test(priority = 5, dependsOnMethods = {"createBillBreakdownInput"}, ...)
    public void createBillBreakdownInputGrp() throws Exception { ... }

    // ... continues through priority 13
    // Each method depends on the previous, forming a chain:
    // 1→2→3→4→5→6→7→8→9→10→11→12→13
}
```

**Key characteristics:**
- `dependsOnMethods` creates execution chain
- `groups` annotation for selective execution (e.g., "Prerequisites4")
- Each method uses a DIFFERENT helper class (Streams, BillBreakdownExtraField, etc.)
- All methods share the same `sheetName` but different `testCaseName` keys

---

## Pattern 3: Multi-Variant Creation with Rate Configuration (~50 classes)

**Structure:** Multiple creation scenarios with variations, each may have rate configuration
**Methods per class:** 15-25

### Example: TCDeal.java
```java
package com.subex.rocps.automation.testcases.functionaltesting;

public class TCDeal extends PSAcceptanceTest {
    String path = System.getProperty("user.dir") + "\\src\\main\\resources\\";
    String workBookName = "FunctionalTestingDeals.xlsx";  // Note: separate deals workbook
    String sheetName = "Deal";

    @Test(priority = 1, description = "search screen column validation",
          retryAnalyzer = Retry.class)
    public void dealColVal() throws Exception {
        try {
            Deal deal = new Deal(path, workBookName, sheetName, "DealSearchScreencolVal", 1);
            deal.searchScreenColumnsValidation();
        } catch (Exception e) {
            FailureHelper.setErrorMessage(e);
            throw e;
        }
    }

    @Test(priority = 2, description = "Tiered Incoming deal creation",
          retryAnalyzer = Retry.class)
    public void dealTieredInCreation() throws Exception {
        try {
            Deal deal = new Deal(path, workBookName, sheetName, "Test01Deal", 1);
            deal.dealCreation();
        } catch (Exception e) {
            FailureHelper.setErrorMessage(e);
            throw e;
        }
    }

    @Test(priority = 3, description = "Rate configuration for tiered deal",
          retryAnalyzer = Retry.class,
          dependsOnMethods = {"dealTieredInCreation"})
    public void dealTieredInRate() throws Exception {
        try {
            DealRate rate = new DealRate(path, workBookName, "DealRate", "Test01DealRate", 1);
            rate.dealRateCreation();
        } catch (Exception e) {
            FailureHelper.setErrorMessage(e);
            throw e;
        }
    }

    // More variants: TieredOutgoing, CommittedIn, BalancedIO, etc.
    // Each variant = creation method + rate method pair
    // Delete/Undelete, ViewResults, Audit, ChangeOwner at the end
}
```

**Key characteristics:**
- Uses SEPARATE workbook (`FunctionalTestingDeals.xlsx`)
- Creation + Rate configuration in pairs
- Multiple deal variants with different test data keys
- `DealRate` is a SEPARATE helper from `Deal`

---

## Pattern 4: Infrastructure Setup with Multiple Helpers (~15 classes)

**Structure:** Complex setup involving event modelling, normalization, aggregation, streams, triggers
**Methods per class:** 10-15

### Example: TCVoiceStream.java
```java
package com.subex.rocps.automation.testcases.systemtesting;

public class TCVoiceStream extends PSAcceptanceTest {
    String path = System.getProperty("user.dir") + "\\src\\main\\resources\\";
    String workBookName = "SystemTestCases.xlsx";
    String sheetName = "VoiceStream";

    @Test(priority = 1, groups = {"Prerequisites3"}, retryAnalyzer = Retry.class)
    public void editEventModellingDefn() throws Exception {
        try {
            EventModellingDefinition emd = new EventModellingDefinition(
                path, workBookName, sheetName, "EventModellingDefn", 1);
            emd.editEventModellingDefn();
        } catch (Exception e) {
            FailureHelper.setErrorMessage(e);
            throw e;
        }
    }

    @Test(priority = 2, dependsOnMethods = {"editEventModellingDefn"}, ...)
    public void editEventModellingInst() throws Exception {
        EventModellingInstance emi = new EventModellingInstance(...);
        emi.editEventModellingInst();
    }

    @Test(priority = 3, dependsOnMethods = {"editEventModellingInst"}, ...)
    public void editEventtype() throws Exception {
        EventType et = new EventType(...);
        et.editEventType();
    }

    @Test(priority = 4, dependsOnMethods = {"editEventtype"}, ...)
    public void createEventNormalization() throws Exception {
        // Creates 4 normalization instances in sequence
        EventNormalization en1 = new EventNormalization(..., "Normalization1", 1);
        en1.createEventNormalization();
        EventNormalization en2 = new EventNormalization(..., "Normalization2", 1);
        en2.createEventNormalization();
        // ... en3, en4
    }

    // priority 5: createAggregationConfig → configureAggregation() + changeAggregationStatus()
    // priority 6: createAggregationProcessor
    // priority 7: createStream → complex try-catch for stream creation
    // priority 8: fileSource
    // priority 9: fileCollection
    // priority 10: createTrigger
    // priority 11: createRecurringTask
    // priority 12: createAggregationCompMapping
}
```

**Key characteristics:**
- Uses 13+ different helper classes in one test class
- Multiple helper instantiations within a single method (normalization example)
- Complex dependency chain for infrastructure setup
- Group: "Prerequisites3" for selective execution

---

## Pattern 5: Request/Import Processing (~30 classes)

**Structure:** Request creation → Schedule → View
**Methods per class:** 3-6

### Example: TCCarrierInvoiceImportRequest.java
```java
public class TCCarrierInvoiceImportRequest extends PSAcceptanceTest {
    String path = System.getProperty("user.dir") + "\\src\\main\\resources\\";
    String workBookName = "FunctionalTestCases.xlsx";
    String sheetName = "CarrierInvoiceImport";

    @Test(priority = 1, description = "column validation")
    public void ciImportColVal() throws Exception {
        try {
            CarrierInvoiceImport ci = new CarrierInvoiceImport(
                path, workBookName, sheetName, "CIImportColVal", 1);
            ci.searchScreenColumnsValidation();
        } catch (Exception e) {
            FailureHelper.setErrorMessage(e);
            throw e;
        }
    }

    @Test(priority = 2)
    public void ciImportRequest1() throws Exception {
        CarrierInvoiceImport ci = new CarrierInvoiceImport(
            path, workBookName, sheetName, "CIImportRequest1", 1);
        ci.ciImportCreation();
    }

    @Test(priority = 3)
    public void ciImportRequest2() throws Exception {
        CarrierInvoiceImport ci = new CarrierInvoiceImport(
            path, workBookName, sheetName, "CIImportRequest2", 1);
        ci.ciImportCreation();
        ci.scheduleNow();  // Additional action after creation
    }

    @Test(priority = 4)
    public void ciImportViewTemplate() throws Exception {
        CarrierInvoiceImport ci = new CarrierInvoiceImport(
            path, workBookName, sheetName, "CIImportViewTemplate", 1);
        ci.viewTemplate();
    }
}
```

---

## Pattern 6: Multi-Step with Cascading Helpers (~20 classes)

**Structure:** Single method creates multiple entities using different helpers

### Example: TCRerateRequest.java (billProfileCreation method)
```java
@Test(priority = 1, retryAnalyzer = Retry.class)
public void billProfileCreation() throws Exception {
    try {
        // Step 1: Create Account
        Account acc = new Account(path, workBookName, sheetName, "AccountCustomer", 1);
        acc.accountCreation();

        // Step 2: Create first Bill Profile
        BillProfile bp1 = new BillProfile(path, workBookName, sheetName, "BillProfileInvoice", 1);
        bp1.billProfileCreation();

        // Step 3: Create second Bill Profile
        BillProfile bp2 = new BillProfile(path, workBookName, sheetName, "BillProfileSelfBill", 1);
        bp2.billProfileCreation();
    } catch (Exception e) {
        FailureHelper.setErrorMessage(e);
        throw e;
    }
}
```

---

## Pattern 7: Approval Workflow Tests (~10 classes)

**Structure:** User/Team setup → Workflow creation → Status changes → Edit

### Example: TCApprovalWorkflows.java
```java
@Test(priority = 1) public void userCreation() { ... UserHelper ... }
@Test(priority = 2) public void teamCreation() { ... TeamHelper ... }
@Test(priority = 3) public void approvalWorkflowsCreation() { ... ApprovalWorkFlows ... }
@Test(priority = 4) public void approvalWorkflowsMultipleCreation() { ... }
@Test(priority = 5) public void approvalWorkflowsMultipleTeams() { ... }
@Test(priority = 6) public void approvalWorkflowsMultipleUSers() { ... }
@Test(priority = 7) public void approvalWorkflowsDelete() { ... }
@Test(priority = 8) public void approvalWorkflowsUnDelete() { ... }
@Test(priority = 9) public void approvalWorkflowschangeStatus() { ... }
@Test(priority = 10) public void editApprovalWorkflows() { ... }
```

---

## Pattern 8: Single-Method Test Classes (~20 classes)

**Structure:** One primary operation, minimal setup

### Example: TCEventAndAggregation.java
```java
public class TCEventAndAggregation extends PSAcceptanceTest {
    String path = System.getProperty("user.dir") + "\\src\\main\\resources\\";
    String workBookName = "FunctionalTestCases.xlsx";
    String sheetName = "EventAndAggregation";

    @Test(priority = 1, description = "Configure Event and Aggregation")
    public void configureEventAndAggregation() throws Exception {
        try {
            EventAndAggregation ea = new EventAndAggregation(
                path, workBookName, sheetName, "EventAndAggregationConfig", 1);
            ea.configureEventAndAggregation();
        } catch (Exception e) {
            FailureHelper.setErrorMessage(e);
            throw e;
        }
    }
}
```

---

## Pattern 9: Configuration with Custom Workbook (~5 classes)

### Example: MyTest.java
```java
public class MyTest extends PSAcceptanceTest {
    String path = System.getProperty("user.dir") + "\\src\\main\\resources\\";
    String workBookName = "AutomateRahulTestCases.xlsx";  // Custom workbook
    String sheetName = "Configurations";

    @Test(priority = 1) public void createBank() { ... Franchise ... }
    @Test(priority = 2, dependsOnMethods = {"createBank"})
    public void createAgent() { ... Agent ... }
    @Test(priority = 3, dependsOnMethods = {"createAgent"})
    public void createAccount() { ... Account ... }
    @Test(priority = 4, dependsOnMethods = {"createAccount"})
    public void createBillProfile() { ... BillProfile ... }
    // ... continues through priority 8
}
```

---

## Common Helpers Used Across Tests

### By Frequency
1. `Account` — most commonly used across functional tests
2. `BillProfile` — billing profile configuration
3. `Streams` — stream setup (system tests)
4. `Deal` / `DealRate` — deal management
5. `EventMatchRule` / `EventMatchRuleGroup` — match rules
6. `EventType` — event type configuration
7. `BillBreakdownConfiguration` — bill breakdown setup
8. `RateSheetTemplateConfiguration` — rate sheets
9. `Operator` — operator setup
10. `CarrierInvoiceImport` — carrier invoice handling

### By Module
| Module | Helpers Used |
|---|---|
| Partner Config | Account, Agent, BillProfile, Franchise |
| Bills | Bills, BillRequest, BillBreakdownConfiguration, BillPackage, SalesTax* |
| Tariffs | PSTariffHelper, RateSheetTemplateConfiguration, TariffClassHelper |
| Events | EventType, EventNormalization, EventModellingDefinition/Instance |
| Match & Rate | EventMatchRule, EventMatchRuleGroup, Operator, CrossFXRate |
| Streams | Streams, FileSourceHelper, FileCollectionHelper, TriggerHelper, RecurringTaskHelper |
| Aggregation | AggregationConfiguration, AggregationProcessor, AggrComponentMapping |
| Roaming | RoamingConfiguration, RoamingDefinition, IMSIManagement |
| Deals | Deal, DealRate, DealImportTemplate |
| Carrier Invoice | CarrierInvoiceImport, CarrierInvoiceExcelTemplate |
| Admin | UserHelper, TeamHelper, ApprovalWorkFlows |

---

## Annotation Usage Summary

| Annotation | Usage |
|---|---|
| `priority = N` | Sequential execution order (1, 2, 3...) |
| `retryAnalyzer = Retry.class` | Allow up to 2 retries on failure |
| `groups = {"GroupName"}` | Selective execution (Prerequisites3, Prerequisites4) |
| `dependsOnMethods = {"method"}` | Create execution chain |
| `description = "..."` | Human-readable step description |
| `enabled = true/false` | Enable/disable test method |
