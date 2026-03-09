# Helper Class Index — ROCPS Test Authoring Knowledge Base

> This file indexes ALL helper classes across the 3 projects. Use it to find the right helper for any test operation.

## Quick Reference: Which Helper Do I Use?

| I want to... | Use this helper | Project |
|---|---|---|
| Click a button | `ButtonHelper` | roc-automation-util |
| Type in a text box | `TextBoxHelper` | roc-automation-util |
| Select from dropdown | `ComboBoxHelper` | roc-automation-util |
| Check/uncheck checkbox | `CheckBoxHelper` | roc-automation-util |
| Click radio button | `RadioHelper` | roc-automation-util |
| Interact with grid/table | `GridHelper` | roc-automation-util |
| Search in a grid | `SearchGridHelper` | roc-automation-util |
| Set a date | `CalendarHelper` | roc-automation-util |
| Switch tabs | `TabHelper` | roc-automation-util |
| Navigate tree | `TreeHelper` | roc-automation-util |
| Upload a file | `FileUpload` | roc-automation-util |
| Download a file | `FileDownload` | roc-automation-util |
| Wait for loading | `GenericHelper.waitForLoadmask()` | roc-automation-util |
| Find element by OR key | `GenericHelper.getORProperty()` | roc-automation-util |
| Find WebElement | `ElementHelper` | roc-automation-util |
| Handle popup/dialog | `PopupHelper` | roc-automation-util |
| Click a link | `LinkHelper` | roc-automation-util |
| Mouse hover/drag | `MouseHelper` | roc-automation-util |
| Navigate to screen | `NavigationHelper` | roc-automation |
| Start/stop services | `ControllerHelper` | roc-automation |
| Export grid data | `ExportHelper` | roc-automation |
| Copy grid data | `CopyHelper` | roc-automation |
| Manage users | `UserHelper` | roc-automation |
| Create streams | `StreamHelper` | roc-automation |
| Configure measures | `MeasureHelper` | roc-automation |
| Create table definitions | `TableDefinitionHelper` | roc-automation |
| Read Excel data | `ExcelReader` / `ExcelHolder` | roc-automation-util / rocps-automation |
| Create billing stream | `Streams` | rocps-automation |
| Create bills | `Bills` | rocps-automation |
| Create bill request | `BillRequest` | rocps-automation |
| Configure bill breakdown | `BillBreakdownConfiguration` | rocps-automation |
| Create rate sheet | `RateSheetTemplateConfiguration` | rocps-automation |
| Import rate sheet | `RateSheetImportRequest` | rocps-automation |
| Create account | `Account` | rocps-automation |
| Create agent | `Agent` | rocps-automation |
| Create bill profile | `BillProfile` | rocps-automation |
| Create deal | `Deal` | rocps-automation |
| Configure event match rule | `EventMatchRule` | rocps-automation |
| Create event type | `EventType` | rocps-automation |
| Event normalization | `EventNormalization` | rocps-automation |
| Configure roaming | `RoamingConfiguration` | rocps-automation |
| Configure aggregation | `AggregationConfiguration` | rocps-automation |
| Create operator | `Operator` | rocps-automation |
| Manage approval workflows | `ApprovalWorkFlows` | rocps-automation |
| Configure GL codes | `GLCodeDefn` | rocps-automation |
| Configure accruals | `AccrualsModelling` | rocps-automation |
| Handle carrier invoices | `CarrierInvoiceImport` | rocps-automation |
| Configure settlements | `Settlement` | rocps-automation |
| Handle rerate requests | `RerateRequest` | rocps-automation |
| Configure cross FX rates | `CrossFXRate` | rocps-automation |
| Configure sales tax | `SalesTax`, `SalesTaxRate`, `SalesTaxGroup` | rocps-automation |

---

## PROJECT 1: roc-automation-util (Foundation Layer)

### Component Helpers (`com.subex.automation.helpers.component`)

#### ButtonHelper
Provides UI interaction methods for button components.
```
isPresent(String idOrXpath): boolean
isPresent(String buttonWrapper, String idOrXpath): boolean
isEnabled(String idOrXpath): boolean
isEnabled(String buttonWrapper, String idOrXpath): boolean
isDisabled(String idOrXpath): boolean
isTextPresent(String idOrXpath, String expectedText): boolean
getText(String idOrXpath): String
click(String idOrXpath): void
click(String buttonWrapper, String idOrXpath): void
clickIfPresent(String idOrXpath): void
clickIfEnabled(String idOrXpath): void
assertPresent(String idOrXpath): void
assertEnabled(String idOrXpath): void
assertDisabled(String idOrXpath): void
```

#### TextBoxHelper
Provides UI interaction methods for text box components.
```
isPresent(String idOrXpath): boolean
clear(String idOrXpath): void
type(String idOrXpath, String value): void
type(String txtBoxWrapper, String idOrXpath, String value): void
isEnabled(String idOrXpath): boolean
isDisabled(String idOrXpath): boolean
isValuePresent(String idOrXpath, String expectedValue): boolean
getValue(String idOrXpath): String
isMandatory(String idOrXpath): boolean
hasValidation(String idOrXpath): boolean
assertPresent(String idOrXpath): void
assertEnabled(String idOrXpath): void
assertDisabled(String idOrXpath): void
```

#### CheckBoxHelper
Provides UI interaction methods for checkbox components.
```
isPresent(String idOrXpath): boolean
check(String idOrXpath): void
check(String checkBoxWrapper, String idOrXpath): void
uncheck(String idOrXpath): void
isChecked(String idOrXpath): boolean
isNotChecked(String idOrXpath): boolean
isEnabled(String idOrXpath): boolean
assertPresent(String idOrXpath): void
assertEnabled(String idOrXpath): void
```

#### ComboBoxHelper
Provides UI interaction methods for combo box (dropdown) components.
```
isPresent(String idOrXpath): boolean
select(String value): void
select(String idOrXpath, String value): void
select(String comboBoxWrapper, String idOrXpath, String value): void
select(String gridId, String idOrXpath, int rowNum, int columnNum, String value): void
isValuePresent(String idOrXpath, String expectedValue): boolean
isEnabled(String idOrXpath): boolean
containsValue(String idOrXpath, String value): boolean
containsValues(String idOrXpath, String[] values): boolean
getValue(String idOrXpath): String
getAllValues(String idOrXpath): String[]
getValuesCount(String idOrXpath): int
assertPresent(String idOrXpath): void
assertEnabled(String idOrXpath): void
```

#### RadioHelper
Provides UI interaction methods for radio button components.
```
isPresent(String idOrXpath): boolean
click(String idOrXpath): void
clickIfEnabled(String idOrXpath): void
isChecked(String idOrXpath): boolean
isEnabled(String idOrXpath): boolean
assertPresent(String idOrXpath): void
```

#### GridHelper
Provides UI interaction methods for grid/table components. Most heavily used helper.
```
// Basic Operations
isPresent(String gridId): boolean
click(String gridId): void
clickRow(String gridId, int rowNum, int colNum): void
clickRow(String gridId, String cellValue): void
clickRow(String gridId, int rowNum, String valueColumnHeader): void
clickRow(String gridId, String cellValue, String valueColumnHeader): void
clickRow(String gridWrapper, String gridId, String cellValue, String valueColumnHeader, String clickColumnHeader): void
doubleClick(String gridId, int rowNum, int colNum): void
doubleClick(String gridId, int rowNum, String columnHeader): void
doubleClick(String gridId, String cellValue, String columnHeader): void
clickMultipleCells(String gridId, int startRowNum, int noOfRows): void
rightClick(String gridId, int rowNum, int colNum): void
rightClick(String gridId, String cellValue, String columnHeader): void
rightClickSubMenu(String gridId, String mainActionId, String subActionId): void

// Data Retrieval
getRowCount(String gridId): int
getRowCount(String gridId, String cellValue, String columnHeader): int
getCellValue(String gridId, int rowNum, int columnNum): String
getCellValue(String gridId, int rowNum, String columnHeader): String
getRowValues(String gridId, int rowNum): ArrayList<String>
getColumnValues(String gridId, int columnNum): ArrayList<String>
getColumnValues(String gridId, String columnHeader): ArrayList<String>
getDateValue(String gridId, int rowNum, String columnHeader): String
getBooleanValue(String gridId, int rowNum, String columnHeader): boolean
getRowNumber(String gridId, String cellValue, int colNum): int
getRowNumber(String gridId, String cellValue, String columnHeader): int
getRowNumberContains(String gridId, String cellValue, String columnHeader): int
getRowNumbers(String gridId, String cellValue, String columnHeader): int[]
getColumnNumber(String gridId, String columnHeader): int

// Verification
isValuePresent(String gridId, String value): boolean
isValuePresent(String gridId, String value, String columnHeader): boolean
isValuePresent(String gridId, int rowNo, String value, String columnHeader): boolean
isRowValuesPresent(String gridId, String assertValue, String assertColumnHeader, String gridvalues): boolean
isDatePresent(String gridId, String expectedValue, int rowNum, String columnHeader): boolean
isRowDeleted(String gridId, String cellValue, String columnHeader): boolean
hasNoResult(String gridId): boolean

// Inline Editing
updateGridTextBox(String gridId, String textBoxId, int rowNum, String columnName, String value): void
updateGridComboBox(String gridId, String comboId, int rowNum, String valueColumnHeader, String value): void
updateGridCheckBox(String gridId, String checkBoxId, int rowNum, String columnName, boolean value): void
updateGridEntityCombo(String gridId, String entityComboId, int rowNum, String columnName, String value): void

// Sorting & Pagination
sortGrid(String gridId, String headerValue): void
sortAscending(String gridId, String headerValue): void
sortDescending(String gridId, String headerValue): void
setPagination(String gridId, String paginationLimit): void

// Scrolling
scrollDown(String gridId, String firstVisibleRowNo, String columnHeader): void
scrollUp(String gridId, String firstVisibleRowNo, String columnHeader): void
scrollToTop(String gridId, String value, String columnHeader): void
scrollToBottom(String gridId, String value, String columnHeader): void
```
Note: Most GridHelper methods also accept an optional `gridWrapper` parameter as the first argument.

#### SearchGridHelper
Provides search and filter operations for grid components.
```
// Constructor
SearchGridHelper(String gridID)
SearchGridHelper(String gridWrapperID, String gridID)

// Search Methods
searchWithTextBox(String txtBoxIdOrXpath, String value, String columnHeader): int
searchWithComboBox(String comboIdOrXpath, String value, String columnHeader): int
searchWithAdvancedSearch(String txtBoxIdOrXpath, String value, String columnHeader): int
gridFilterSearchWithTextBox(String txtBoxIdOrXpath, String value, String columnHeader): int
gridFilterSearchWithComboBox(String comboIdOrXpath, String value, String columnHeader): int
gridFilterAdvancedSearch(String txtBoxIdOrXpath, String value, String columnHeader): int
gridFilterSearchWithCalendar(String calendarIdOrXpath, String type, String fromDate, String toDate, String columnHeader): void

// Utilities
setPagination(String value): void
getTotalRecordsFetched(): String
sortAscending(String columnHeader): void
sortDescending(String columnHeader): void
```

#### TabHelper
```
isPresent(String idOrXpath): boolean
gotoTab(String idOrXpath): void
gotoTab(String tabWrapper, String idOrXpath): void
isEnabled(String idOrXpath): boolean
isSelected(String idOrXpath): boolean
assertPresent(String idOrXpath): void
```

#### CalendarHelper
```
isPresent(String calendarID): boolean
setNow(String calendarID): void
setOnDate(String calendarID, String onDate): void
setAfterDate(String calendarID, String afterDate): void
setBeforeDate(String calendarID, String beforeDate): void
setBetweenDate(String calendarID, String fromDate, String toDate): void
setToday(String calendarID): void
setLast7Days(String calendarID): void
setLast30Days(String calendarID): void
setDate(String calendarID, String type, String fromDate, String toDate): void
```

#### GenericHelper
Core utility methods for waits, OR resolution, and common operations.
```
getORProperty(String key): String
waitForSave(): void
waitForSave(int waitTimeInSecs): void
waitForLoadmask(): void
waitForLoadmask(int waitTimeInSecs): void
waitForLoadmaskToAppear(int waitTimeInSecs): void
waitForElement(String xpath, String waitInSec): void
waitForElement(String xpath, int waitSec): void
```

#### ElementHelper
Low-level element finding and interaction.
```
isElementPresent(String xpath): boolean
isElementPresent(String wrapperId, String xpath): boolean
isElementPresent(WebElement element, String xpath): boolean
getElement(String idOrXpath): WebElement
getElement(String wrapperId, String idOrXpath): WebElement
getElement(WebElement element, String idOrXpath): WebElement
```

#### Other Component Helpers (Brief)
- **LinkHelper** — click links, verify link text
- **MouseHelper** — hover, drag-and-drop, context menu
- **PopupHelper** — handle modal popups, confirmation dialogs
- **TreeHelper** — expand/collapse/select tree nodes
- **LabelHelper** — verify label text, get label values
- **ImageHelper** — verify image presence, get image attributes
- **TextAreaHelper** — type in text areas, get text area content
- **FileUpload** — upload files using Robot class (not sendKeys)
- **FileDownload** — download and verify file downloads
- **EntityComboHelper** — search-enabled combo boxes with entity popups
- **PropertyGridHelper** — interact with property grid components

### Application Helpers
- **BrowserHelper** — browser management (open, close, navigate)
- **LoginHelper** — application login/logout
- **NavigationHelper** — screen navigation
- **ROCHelper** — session management, handleSessionTimeout()

### Data Helpers
- **DateHelper** — date formatting and calculations
- **StringHelper** — string manipulation utilities
- **ValidationHelper** — data validation utilities
- **DataGenerationHelper** — generate test data

### Database Helpers
- **DatabaseHelper** / **DBHelper** — execute SQL queries
- **DBConnectionHelper** — manage DB connections
- **PostgreSQLHelper** — PostgreSQL-specific operations

### File Helpers
- **ExcelReaderHelper** / **ExcelWriterHelper** — Excel file I/O

### Report Helpers
- **ReportHelper** — ExtentReports integration
- **FailureHelper** — `setErrorMessage(e)`, `reportFailure(e)`, `failTest(message)`

---

## PROJECT 2: roc-automation (Middle Layer)

### ROCAcceptanceTest (Base Test Class)
`com.subex.automation.helpers.selenium.ROCAcceptanceTest extends AcceptanceTest`

TestNG lifecycle methods:
- `@BeforeSuite` — `initializeSuite()` — loads OR, config, report
- `@AfterSuite` — `stopDriver()` — closes browser and connections
- `@BeforeClass` — `startApp()` — login and test setup
- `@AfterClass` — `endReport()` — finalize report
- `@BeforeMethod` — `handleTestMethodName()` — session timeout handling
- `@AfterMethod` — `updateReport()` — report update with pass/fail

### Key Application Helpers

#### ControllerHelper
Start/stop Stream Controller, Task Controller, Tomcat, Server Service.
```
isSCRunning(): boolean
startTomcat(): void
startStreamController(): void
startTaskController(String tcbatFilename): void
stopTomcat(): void
stopStreamController(): void
stopTaskController(String tcName): void
restartTomcat(): void
```

#### ExportHelper
Export grid data to Excel.
```
exportSelectedRows(String gridId, int rowNum, int colNum): String
exportSelectedRows(String gridId, int rowNum, String columnHeader): String
exportConfiguredRows(): String
exportAllRows(): void
```

#### CopyHelper
Copy grid data operations.
```
copySelectedCell(String gridId, int rowNum, int colNum): void
copySelectedRow(String gridId, int rowNum, int colNum): void
copyAllRows(String gridId): void
getCopyContent(): String
closeCopyPopup(): void
```

#### UserHelper
User management.
```
createUser(String path, String fileName, String sheetName, String testCaseName, int occurance): void
createUser(String partition, String userName, String password, String firstName, ...): void
changePassword(String userName, String currentPassword, String newPassword, String confirmPassword): void
```

#### StreamHelper
Stream configuration.
```
createStream(String path, String workBookName, String workSheetName, String testCaseName, int occurence): void
createStream(String partition, String streamName, String[] stageTypes, String[] stageNames, ...): void
updateStream(String streamName, String[] stageTypes, ...): void
navigateToStream(String streamName): int
saveStream(String streamName, String detailScreenTitle): void
```

#### ReferenceTableHelper
Reference table operations (DSL, DSC, Schema, Token, Usage Server).
```
dataSourceLocation(...): void
dataSourceConnection(...): void
usageServer(...): void
schema(...): void
schemaType(...): void
token(...): void
```

#### MeasureHelper
Measure configuration (reporting tables, case templates, input measures).
```
addReportingTable(String entityComboId, boolean isExistingTable, String rtTableName, ...): void
linkCaseTemplate(String entityComboId, String caseTemplateName, String[][] casePropertyMapping): void
addInputMeasure(String sourceGridId, String[] inputMeasure, String[] inputTable): void
addHavingClause(...): void
saveMeasure(String measureType, String name, String detailScreenTitle): void
```

#### TableDefinitionHelper
Table definition management.
```
createTableDefinition(String path, String workBookName, ...): void
editTableDefinition(...): void
addTableColumn(...): void
addTableIndex(...): void
deleteTableColumn(...): void
```

#### ConfigureGridHelper
Configure Grid screen column management.
```
updateConfigureGrid(...): void
addColumnToConfigureGrid(...): void
deleteColumnFromConfigureGrid(...): void
addColorFilter(...): void
attachColorFilter(...): void
```

---

## PROJECT 3: rocps-automation (Product Layer)

### PSAcceptanceTest (ROCPS Base Class)
`com.subex.rocps.automation.acceptance.PSAcceptanceTest extends ROCAcceptanceTest`

All ROCPS test classes and domain helpers extend this class.

### Domain Helper Pattern
Each business module follows this class hierarchy:
- **XXX.java** — Main orchestrator (extends PSAcceptanceTest), instantiated from test classes
- **XXXDetailImpl.java** — Form field interactions
- **XXXActionImpl.java** — Action execution (new, edit, delete, status change)
- **XXXSearchImpl.java** — Grid search and navigation

Constructor patterns (Excel-driven):
```java
new HelperClass(String path, String workBookName, String sheetName, String testCaseName)
new HelperClass(String path, String workBookName, String sheetName, String testCaseName, int occurence)
```

### Bills Module (63 classes)
**Package:** `com.subex.rocps.automation.helpers.application.bills`

| Class | Purpose |
|---|---|
| `Bills` | Main bill orchestrator — generation, search, validation |
| `BillRequest` | Bill request creation and scheduling |
| `BillBreakdownConfiguration` | Configure bill breakdown components |
| `BillBreakdownOutput` / `BillBreakdownOutputGroup` | Output breakdown structure |
| `BillBreakDownInput` / `BillBreakdownInputGroup` | Input breakdown structure |
| `BillBreakdownExtraField` | Extra fields for breakdown |
| `BillingCycle` | Billing cycle configuration |
| `BillingGroupCode` | Billing group codes |
| `BillPackage` | Bill package configuration |
| `TestBill` | Test bill operations |
| `CreditNotes` | Credit notes for bills |
| `RerateRequest` | Rerate request operations |
| `SalesTax` / `SalesTaxRate` / `SalesTaxGroup` | Sales tax configuration |
| `BillReportConfigurationHelper` | Bill report settings |
| `BillActionImpl` (hotbill) | Production bill actions |
| `BillSearchImpl` (hotbill) | Bill search/filter |

### Accruals Module (17 classes)
**Package:** `com.subex.rocps.automation.helpers.application.accruals`

| Class | Purpose |
|---|---|
| `AccountingPeriods` | Manage accounting periods |
| `AccountingPeriodDefinition` | Define period structure |
| `AccrualsModelling` | Configure accruals modelling |
| `AccrualsOverview` | Accruals overview/dashboard |
| `EstimationProcessor` | Process estimation calculations |
| `GLCodeDefn` | GL (General Ledger) code definitions |
| `GLCodeInstance` | GL code instances |

### Roaming Module (58 classes)
**Package:** `com.subex.rocps.automation.helpers.application.roaming`

| Class | Purpose |
|---|---|
| `RoamingDefinition` | Roaming agreements and configurations |
| `RoamingConfiguration` | TAP, RAP, HUR, NRTRDE settings |
| `RoamingAgreementConfig` | Roaming agreement details |
| `IMSIManagement` | IMSI configurations |
| `TestSIMManagement` | Test SIM configurations |
| `HURFiles` | HUR file operations |
| `RoamingFileStatus` | File processing status |
| `NRTRDEFileStatus` | NRTRDE file status |
| `NRTRDEReport` | NRTRDE report generation |

### Tariffs/Rate Sheets Module (6 classes)
**Package:** `com.subex.rocps.automation.helpers.application.tariffs`

| Class | Purpose |
|---|---|
| `RateSheetImportRequest` | Import rate sheets |
| `RateSheetTemplateConfiguration` | Configure rate sheet templates |
| `AutoRateSheetConfig` | Auto-generate rate sheet configurations |
| `RateSheetValidation` | Validate rate sheet data |
| `PSTariffHelper` | General tariff utilities |
| `PSFastEntryHelper` | Fast entry mode for tariff data |

### Match & Rate Module (20 classes)
**Package:** `com.subex.rocps.automation.helpers.application.matchandrate`

| Class | Purpose |
|---|---|
| `EventMatchRule` | Event matching rules |
| `EventMatchRuleGroup` | Group matching rules |
| `EventNormalization` | Normalize event data |
| `EventType` | Event type definitions |
| `EventIdentiferDefinition` | Event identifier definitions |
| `EventIdentifierValue` / `EventIdentifierValueGroup` | Identifier values |
| `EventExtraRatingField` | Extra rating fields |
| `PreRatingMatchRule` | Pre-rating match rules |
| `EventModellingDefinition` / `EventModellingInstance` | Event modelling |
| `CrossFXRate` | Cross-currency FX rates |
| `SurchargeRule` | Surcharge rules |
| `Operator` | Operator configuration |

### Partner Configuration Module
**Package:** `com.subex.rocps.automation.helpers.application.partnerConfiguration`

| Class | Purpose |
|---|---|
| `Account` | Partner account setup |
| `Agent` | Agent configuration |
| `BillProfile` | Billing profile |
| `Franchise` | Franchise configuration |

### Other Key Modules

| Module | Key Classes |
|---|---|
| **Deal** | `Deal`, `DealRate`, `MergerResults` |
| **Carrier Invoice** | `CarrierInvoiceImport`, `CarrierInvoiceExcelTemplate` |
| **Aggregation** | `AggregationConfiguration`, `AggregationProcessor`, `AggregationResult` |
| **Settlements** | `Settlement`, `SettlementSearch`, `Payments` |
| **Sales** | `SalesProposal`, `SalesOffer`, `MarkupRule`, `OfferRule` |
| **BCR Management** | `BCRPlan`, `BCRRequest`, `BCRProduct`, `RoutingRule` |
| **Approval Workflows** | `ApprovalWorkFlows` |
| **Alerts** | `AlertsEvents`, `AlertGroup`, `AlertValidation` |
| **Bulk Entity** | `BulkEntityExport`, multiple Selection helpers (17+) |
| **Quality** | Quality management classes |
| **Prepayments** | `PrePaymentsDetailImpl`, `PrePaymentsSearchImpl` |

### Generic ROCPS Helpers
**Package:** `com.subex.rocps.automation.helpers.application.genericHelpers`

| Class | Purpose |
|---|---|
| `PSGenericHelper` | Generic grid operations, waits |
| `PSActionImpl` | Common action implementations (new, edit, delete, changeStatus) |
| `PSEntityComboHelper` | Entity combo helper |
| `DataVerificationHelper` | Data verification operations |
| `AdvanceSearchFiltersHelper` | Advanced search filter operations |
| `GridFilterSearchHelper` | Grid-based search and filter |
| `PSSearchGridHelper` | PS-specific search grid helper |

---

## Summary Statistics

| Project | Helper Classes | Methods (approx) |
|---|---|---|
| roc-automation-util | 80+ | 1,000+ |
| roc-automation | 85+ | 800+ |
| rocps-automation | 548+ | 3,000+ |
| **Total** | **713+** | **4,800+** |
