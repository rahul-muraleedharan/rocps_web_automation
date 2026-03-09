# Excel Test Data Schema — ROCPS Test Authoring Knowledge Base

> This file documents the Excel-driven test data structure used across the ROCPS automation suite.

## Workbooks

| Workbook | Location | Usage |
|---|---|---|
| `SystemTestCases.xlsx` | `rocps-automation/src/main/resources/` | System/integration tests |
| `FunctionalTestCases.xlsx` | `rocps-automation/src/main/resources/` | Functional tests |
| `FunctionalTestingDeals.xlsx` | `rocps-automation/src/main/resources/` | Deal-related tests |
| `AutomateRahulTestCases.xlsx` | `rocps-automation/src/main/resources/` | Custom test scenarios |

---

## Data Access Architecture

### ExcelReader (`roc-automation-util`)
```java
ExcelReader excelData = new ExcelReader();
Map<String, ArrayList<String>> dataMap = excelData.readDataByColumn(
    path,           // System.getProperty("user.dir") + "\\src\\main\\resources\\"
    workBookName,   // "SystemTestCases.xlsx"
    sheetName,      // "BillPreRequisites"
    testCaseName,   // "BillingStreamConfig"
    occurrence      // 1 (optional, for multiple test data sets)
);
```

### ExcelHolder (`rocps-automation`)
Converts ExcelReader output to usable Maps.
```java
ExcelHolder excelHolderObj = new ExcelHolder(dataMap);
Map<String, String> rowData = excelHolderObj.dataMap(0);  // Get first row
String value = ExcelHolder.getKey(rowData, "AccountName"); // Get field value
int totalCols = excelHolderObj.totalColumns();
```

### Standard Test Class Data Setup
```java
public class TCAccount extends PSAcceptanceTest {
    String path = System.getProperty("user.dir") + "\\src\\main\\resources\\";
    String workBookName = "FunctionalTestCases.xlsx";
    String sheetName = "Account";

    @Test(priority = 1)
    public void accountCreation() {
        Account acc = new Account(path, workBookName, sheetName, "AccountCustomer", 1);
        acc.accountCreation();
    }
}
```

---

## Data Delimiters

| Delimiter | Character | Usage | Parse Method |
|---|---|---|---|
| First Level | `\|` (pipe) | Multiple alternatives in one cell | `split("\\|", -1)` or `PSStringUtils.stringSplitFirstLevel()` |
| Second Level | `;` (semicolon) | Multiple records/rows within cell | `split("\\;", -1)` or `PSStringUtils.stringSplitSecondLevel()` |
| Third Level | `!` (exclamation) | Nested field delimiters | `split("\\!", -1)` or `PSStringUtils.stringSplitThirdLevel()` |

### Delimiter Examples
```
# Pipe — multiple banks for one account
BankName: "HSBC|Barclays|Citibank"
BankAccountNumber: "1234|5678|9012"

# Semicolon — multiple address lines
AddressLine: "Line1;Line2;Line3"
ContactName: "John;Jane;Bob"

# Special values
$$NOW$$     → current date/time (substituted at runtime)
$$partition$$ → config property substitution
```

---

## Sheet Layout Format

```
Row 0:  [Column Headers]
Row 1+: [Data Rows - parameter name/value pairs]

Empty row = end of data for that test case.
```

### Data Key Naming Conventions
```
AccountCustomer          — function-based
BillProfileInvoice       — function-based
T1_Destination           — hierarchical (tier 1)
Test01Deal               — variant-based
AccountDelete            — action-based
BillRequestSearchScreencolVal — validation screen
```

---

## Complete Sheet Name Catalog (200+ sheets)

### Core Configuration
| Sheet | Purpose |
|---|---|
| Elements | Element setup |
| Bands | Band configuration |
| TariffClass | Tariff class definition |
| Tariff | Tariff configuration |
| FastEntry | Fast entry setup |

### Network Configuration
| Sheet | Purpose |
|---|---|
| DialStringSet | Dial string configuration |
| Route | Route configuration |
| RouteGrp | Route group setup |
| Switch | Switch configuration |
| RuleStringSet | Rule string set |

### Match & Rate
| Sheet | Purpose |
|---|---|
| EventIdentifierDefinition | Event ID definition |
| EventIdentifierValue | Event ID values |
| EventIdentiferValueGroup | Event ID value groups |
| EventLegCodeGroup | Event leg code groups |
| EventMatchRuleGroup | Event match rule groups |
| EventMatchRule | Individual event match rules |
| EventType | Event type definitions |
| EventDefn | Event definitions |
| EventNormalization | Normalization rules |
| EventModellingDefn | Event modelling definitions |
| EventModellingInst | Event modelling instances |
| EventExtraRatingField | Extra rating fields |
| EventUsage | Event usage tracking |
| EventUsageRequest | Usage requests |
| EventError | Error handling |

### Partner Configuration
| Sheet | Purpose |
|---|---|
| Account | Account setup (pipe-delimited banks) |
| AccountCategory | Account categories |
| BillProfile | Billing profile configuration |
| Agent | Agent configuration |
| Operator | Operator setup |
| Bank | Bank reference data |

### Billing
| Sheet | Purpose |
|---|---|
| Streams | Stream configuration |
| BillBreakdownExtraField | Extra fields |
| BillBreakdownConfig | Breakdown configuration |
| BillInput | Input parameters |
| BillInputGrp | Input groups |
| BillBreakdownOutput | Output structure |
| BillOutputGrp | Output groups |
| BillPackage | Package setup |
| BillingCycle | Cycle configuration |
| BillingGroupCode | Group codes |
| BillRequest | Request parameters |
| BillActions | Action configurations |
| BillDataset | Bill datasets |
| BillReportConfiguration | Report config |
| Credit Notes | Credit note data |
| Bill Approval Workflow | Approval workflows |
| TestBill | Test billing |
| HotBill | Hot bill configuration |
| HotBill-DateRange | Date range specifics |
| HotBill-ConsolidateAlways | Consolidation rules |
| HotBill-ConsolidateNever | Non-consolidation |

### Aggregation
| Sheet | Purpose |
|---|---|
| AggregationConfiguration | Aggregation config |
| AggregationProcessor | Processor setup |
| AggregationResult | Result validation |
| AggregationResults | Multiple results |
| EventAndAggregation | Event aggregation |
| EventAndAggregationPrerequisite | Prerequisites |
| EstimationProcessor | Estimation setup |

### Accruals
| Sheet | Purpose |
|---|---|
| AccrualPrerequisites | Accrual prerequisites |
| AccrualsModelling | Accruals modelling |
| AccrualsOverviewMod | Overview modelling |
| AccountingPeriod | Period definition |
| AccountingPeriodDefn | Period definitions |
| GlCodeDefn | GL code definitions |
| GlCdInstance | GL code instances |

### Roaming
| Sheet | Purpose |
|---|---|
| RoamingConfig | Roaming configuration |
| RoamingDfn | Roaming definition |
| RoamingDfnGrp | Definition groups |
| RoamingService | Roaming services |
| RoamingFileStatus | File status |
| RoamingExpGroup | Experience groups |
| RoamingTaxation | Tax handling |
| RoamingServMatchExpr | Service match expressions |
| RoamingTapOutPrerequisite | TAP preprocessing |
| RoamingTapOutServerCases | TAP server cases |
| RoamingRapOutServerCases | RAP output cases |
| RoamingRapInServerCases | RAP input cases |
| RoamingTapInServerCases | TAP input cases |
| IMSIManagement | IMSI management |
| TestSIMManagement | Test SIM handling |
| NRTRDEFileStatus | NRTRDE file status |
| NRTRDEReport | NRTRDE reporting |

### Deals
| Sheet | Purpose |
|---|---|
| Deal | Deal configuration |
| DealImport | Deal import |
| DealImportTemplate | Import templates |
| DealServer | Server-side deal cases |
| DealTrafficType | Deal traffic types |

### Rate Sheets
| Sheet | Purpose |
|---|---|
| RateSheet | Rate sheet configuration |
| RateSheetImportRequest | Import requests |
| Ratesheet | Alternative naming |
| RatesheetServerSideDest | Destination server cases |
| AutoRateSheetConfig | Auto configuration |

### Carrier Invoice
| Sheet | Purpose |
|---|---|
| CarrierInvoice | Invoice configuration |
| CarrierInvoiceImport | Import parameters |
| CarrierInvoiceTemplateManual | Template setup |
| CarrierInvoiceEmailConfig | Email configuration |
| CarrierInvoiceExcelTemplate | Excel template mapping |

### BCR & Routing
| Sheet | Purpose |
|---|---|
| BCRPlan | BCR planning |
| BCRProduct | BCR product setup |
| BCRRequest | BCR requests |
| RoutingRule | Routing rules |
| RankAnalysis | Rank analysis |
| ConsolidatedBCRPlan | Consolidated BCR |
| BCRPlanSelection | Plan selection |
| TechnicalPlan | Technical planning |
| RoutePeriorityPercenRouting | Route priority/percentage |
| NetworkExclusion | Network exclusions |

### Settlements & Payments
| Sheet | Purpose |
|---|---|
| Settlements | Settlement configuration |
| SettlementsPrerequisite | Settlement prerequisites |
| PrePayments | Prepayment configuration |
| PrePaymentsServerCases | Server cases |
| PaymentsAndCollections | Payment handling |
| PaymentCollectionPrerequisite | Payment setup |

### Sales & Deals
| Sheet | Purpose |
|---|---|
| SalesProposal | Sales proposals |
| SalesOffer | Sales offers |
| MarkupRule | Markup rules |
| OfferRule | Offer rules |
| ProposalRule | Proposal rules |

### Reference Data
| Sheet | Purpose |
|---|---|
| Currency | Currency configuration |
| CrossFXRate | Cross currency rates |
| CrossFXRateGroup | FX rate groups |
| TrafficType | Traffic types |
| CallTypeGroup | Call type grouping |
| CallTypeLevel1 / Level2 | Call type hierarchy |
| TariffType | Tariff types |
| ChargeType | Charge types |
| Entities | Entity master data |
| TadigCodes | TADIG code reference |
| SalesTax | Sales tax config |
| SalesTaxGrp | Sales tax groups |
| SalesTaxRate | Sales tax rates |
| SignallingType | Signalling types |

### Discount & Override
| Sheet | Purpose |
|---|---|
| SurchargeRule | Surcharge rules |
| SurchargeRuleServerCases | Server cases |
| Overrides | Override configurations |
| FloorCeilingPrice | Price floors/ceilings |

### Dispute & Reconciliation
| Sheet | Purpose |
|---|---|
| Dispute | Dispute handling |
| DisputeResolution | Resolution parameters |
| ReconRequest | Reconciliation requests |
| ReconConfig | Reconciliation config |
| InvoiceReconConfig | Invoice reconciliation |

### System & Admin
| Sheet | Purpose |
|---|---|
| Configurations | System configurations |
| SystemFieldList | Field definitions |
| SystemTariffMapping | Tariff mappings |
| Prerating | Pre-rating setup |
| AlertEvents | Alert event setup |
| AmountThreshold | Amount thresholds |
| EmailConfig | Email settings |
| UserLogin | Login data |

### File & Upload
| Sheet | Purpose |
|---|---|
| FileUpload | Upload configuration |
| FileUploadCategory | Upload categories |
| FileUploadPreRequisites | Prerequisites |
| UploadFileType | File type definitions |
| BulkLoadStream | Bulk loading |
| BulkEntityExport | Entity export |
| XDRExtraction | XDR extraction |
| ExtractFileLocation | File locations |

### Prerequisites (Setup Sheets)
| Sheet | Purpose |
|---|---|
| Pre-requisites | ROC prerequisites |
| ROPreRequisites2 | Additional setup |
| BillPreRequisites | Bill setup |
| PSPrerequisites | PS setup |
| ROCPreRequisites | ROC configuration |
| VoiceStream | Voice stream setup |

### Quality & Monitoring
| Sheet | Purpose |
|---|---|
| QualityManager | Quality management |
| QualityRule | Quality rules |
| QualityMetric | Quality metrics |
| QualityThreshold | Threshold settings |
| QOSConfiguration | QOS configuration |

### Reporting
| Sheet | Purpose |
|---|---|
| ReportAndExtDefn | Report definitions |
| ReportAndExtScheduler | Scheduling |
| ReportModelling | Modelling |
| ReportColumnMapping | Column mappings |

### Products
| Sheet | Purpose |
|---|---|
| ProductInstance | Product instances |
| ProductInstanceServer | Server-side instances |
| ProductBundle | Bundle configuration |
| ProductArgumentType | Argument types |

---

## Data File Locations

```
rocps-automation/src/main/resources/Data/
├── FunctionalTesting/
│   ├── BulkLoadStream/     # Bulk load test files
│   ├── CarrierInvoice/     # Carrier invoice files
│   ├── DealImport/         # Deal import files
│   ├── RateSheet/          # Rate sheet files
│   ├── CDRS/               # CDR test files
│   └── RoamingFilePath/    # Roaming file data
```

---

## Key Notes

1. **Row 0 = Headers** — First row always contains column headers
2. **Empty Row = End** — Data reading stops at first empty row
3. **Occurrence** — Same test case name can appear multiple times; use `occurrence` parameter to select
4. **Null Safety** — ExcelHolder validates keys and throws exceptions for missing required values
5. **Case Sensitive** — Sheet names and parameter names are case-sensitive
6. **Path Format** — Paths use Windows format `\\` in code
