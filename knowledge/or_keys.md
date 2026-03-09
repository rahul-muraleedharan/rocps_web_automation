# Object Repository Keys — ROCPS Test Authoring Knowledge Base

> This file indexes ALL OR (Object Repository) property files containing UI element locators.
> Total: 10 files, 3,700+ locators across roc-automation and rocps-automation projects.

## OR Files Overview

| File | Project | Lines | Scope |
|---|---|---|---|
| `OR.properties` | roc-automation | 1,144 | Core ROC framework, generic UI |
| `PS_OR.properties` | rocps-automation | 1,099 | ROCPS domain screens |
| `Measures_Audits_OR.properties` | roc-automation | 394 | Measures, Audits, KPIs |
| `CM_OR.properties` | roc-automation | 231 | Case Management |
| `Tariff_OR.properties` | roc-automation | 178 | Tariff, pricing, rates |
| `Users_OR.properties` | roc-automation | 96 | Roles, Users, Teams |
| `LDC_OR.properties` | roc-automation | 104 | LDC (Offline & Online) |
| `ROCView_OR.properties` | roc-automation | 99 | Dashboard Visualizer |
| `ROCRA_OR.properties` | roc-automation | 32 | Rate Analysis (LVR) |
| `Tariff_OR.properties` | rocps-automation | 178 | Tariff (copy) |

## Key Naming Convention
Format: `Module_Element_Type`
- Examples: `Login_Username_TextBox`, `Detail_operator_name_txtId`, `PSDetail_agcName_txtid`

## How OR Keys Are Used
```java
// In test code
String locator = GenericHelper.getORProperty("Login_Username_TextBox");
// GenericHelper resolves by trying: XPath → ID → Name → CSS class → CSS selector → Title → Text

// Direct usage with helpers
TextBoxHelper.type("Login_Username_TextBox", "admin");
ButtonHelper.click("LoginButton");
ComboBoxHelper.select("Login_Application_ComboBox", "ROCPS");
```

---

## OR.properties — Core Framework (1,144 lines)

### Login Screen
```properties
Login_Application_ComboBox=appNameInner
Login_Username_TextBox=//input[@id='username-input-area' or @id='username']
Login_Password_TextBox=//input[@id='password-input-area' or @id='password']
LoginButton=btn
Login_Error_Message=errorMessage
Successfull_Login_Dialog=//div[contains(text(),'Last Successful Login:')]
LoggedIn_User=id-welcome-name
```

### Loading & Wait
```properties
GXT_Loading_Mask=loading
Loading_Mask=//div[@class='phoenix-loading-image']/img[contains(@src,'loading.gif')]
Angular_Loading_Mask=//div[contains(@class,'loading-text')]
```

### Navigation Menu
```properties
NavigationMenu=//div[@id='navigationLabel' or text()='Navigation Menu']
NavigationMenu_Frame=pop-up
NavigationMenu_Wrapper=mega-menu-container
NavigationMenu_Screen=//table[@id='item-menu-try-redraw']//table[@id='item-menu']//div[@class='gwt-HTML' and text()='screenName']
ScreenSelect_Textbox=//table[@id='-searchBox1']//input[@type='text']
ScreenSelect_Xpath=//div[@id='navigationSearch']//table//div[@id='id-ScreenName' or @id='id- ScreenName' or @id='ScreenName']
```

### Screen Titles
```properties
HomePage_Title=grouptTitle
SearchScreen_Title=filterpanel-header-label
DetailScreen_Title=titlePanel
DetailScreen_ActionTitle=undefinedactionTypeLabel
DetailScreen_FormTitle=formTitle
Popup_Title=//div[contains(@class,'gwt-HTML roc-window-title')]
```

### Common Buttons
```properties
SearchButton=//button[@id='search-button' or text()='Search' or @id='search']
SaveButton=//button[text()='Save' or text()=' Save']
CancelButton=//button[text()='Cancel' or text()=' Cancel' or text()='Cancel ']
OKButton=//button[text()='O K' or text()='OK' or text()='Ok']
YesButton=//button[text()='Yes']
NoButton=//button[text()='No']
DiscardButton=//button[text()='Discard' or @id='discard' or text()=' Discard' or text()='Discard ']
CloseButton=//button[text()='Close' or text()=' Close' or text()='Close ']
ClearButton=//button[text()='Clear']
OK_Button_ByID=ok
Button_ById=//button[@id='idOrText']
Button_ByText=//button[text()='idOrText' or text()=' idOrText' or text()='idOrText ']
Button_ByDivId=//div[@id='idOrText']
Button_ByDivText=//div[text()='idOrText']
```

### Action Menu
```properties
ActionMenu_Groups=//table[@id='menuBar']//div[@class='roc-menu-item']//div[@class='gwt-HTML']
GroupActionName=//table[@id='menuBar']//div[text()='actionName']
SubMenuActionName=//div[@id='subMenu']//div[text()='actionName']
SubSubMenuActionName=//div[@id='subSubMenu']//div[text()='actionName']
```

### Search Screen
```properties
Search_Panel=searchPanelContainer
SearchScreen_Panel=//div[@id='bottomPanel']//div[@id='searchPanel' or @class='gwt-SplitLayoutPanel' or @class='roc-fs-panel']
ExpandSearchFiler=//div[@id='collapse']/img[contains(@src,'collapse.png')]
CollapseSearchFilter=//div[@id='collapse']/img[contains(@src,'expand.png')]
AdvancedSearch=//div[text()='Advanced Search']
```

### Grid Components
```properties
SearchGrid=searchGrid
Grid_Div_ById=//div[@id='gridId']
Grid_Table_ById=//table[@id='gridId']
Empty_SearchGrid=//div[@id='gridId']//div[text()='No Results Found.']
Grid_Error_Icon=//*[@class='roc-datagrid-error-icon' or @class='roc-toolbar']//img[contains(@src,'error.png')]/parent::div
Grid_HeaderValue=//th//div[contains(text(),'headerValue')]
Grid_Row_ByDiv=//div[@class='MH' or ...]//table//tr[rowNo]
Grid_Column_ByDiv=/td[colNo]//div
Editable_Grid_Component=grid_cell_popuppanel
Grid_Filter_Panel=_grid_header_filter
Grid_SortAscending=_grid_header_context_menu_sort_ascending_item
Grid_SortDescending=_grid_header_context_menu_sort_descending_item
Pagination_TotalRecordsFetched=//table[@class='roc-datagrid-toolbar-bottom']/tbody/tr/td[3]//span[@class='retrieved-items-text']
Pagination_Combo=pagersearchGrid_gwt_uid_
```

### Popup & Dialog
```properties
Popup_Panel=window-scroll-panel
Popup_Wrapper=//div[contains(@class,'roc-WindowBox') or contains(@class,'roc-file-upload-dialog')]
ConfirmationPopup=//div[@class='gwt-DialogBox roc-WindowBox']//div[@id='popupId']
Dialog_Close_Icon=//a[@class='roc-dialog-close']
```

### TextBox Templates
```properties
TextBox_ById=//input[@id='idOrName']
TextBox_ByName=//input[@name='idOrName']
TextBox_DatePicker=//table[@id='idOrName']//input[contains(@class,'gwt-DateBox')]
TextBox_SearchBox=//div[@id='idOrName']//input[contains(@class,'searchBox')]
TextBox_With_DivWrapper=//div[@id='idOrName']//input
TextBox_With_TableWrapper=//table[@id='idOrName']//input
```

### ComboBox Templates
```properties
ComboBox_ById=//div[starts-with(@id,'comboId')]
ComboBox_Dropdown=//div[starts-with(@id,'floater_comboId')]
ComboBox_Dropdown_ByTitle=//li[@title='value' or ...]
ComboBox_Dropdown_ByText=//li[text()='value' or ...]
ComboBox_SearchBox_ById=//div[starts-with(@id,'floater_comboId')]//input[@type='text']
ComboBox_BySelect=//select[starts-with(@id,'idOrXpath') and @class='gwt-ListBox']
```

### Calendar Templates
```properties
Calendar_From_TextBox=-fromDateLabel
Calendar_To_TextBox=-toDateLabel
Calendar_Now=//table[@class='datePickerMonthSelector']//img[contains(@src,'now.png')]
```

### Tab Templates
```properties
Tab_Panel=gwt-TabLayoutPanelTabs
Tab_ById=//div[@id='tabIdOrName']
Tab_Div_ByText=//div[text()='tabIdOrName']
Tab_Span_ByText=//span[text()='tabIdOrName']
```

### Label Templates
```properties
Label_ById=//div[@id='labelIdOrValue']
Common_Label=//*[text()='labelIdOrValue' or contains(text(),'labelIdOrValue')]
Common_Label_BySpan=//span[text()='labelIdOrValue']
```

### Tree Templates
```properties
Tree_ByDiv=//div[@id='treeId']
Tree_ByText=//*[text()=' treeText' or text()='treeText' or text()='treeText ']
```

### File Upload
```properties
FileUpload_Browse=//div[@class='roc-trigger roc-fileupload-trigger']
FileUpload_Browse_ById=//*[@id='uploadId']//div[@class='roc-trigger roc-fileupload-trigger']
```

### Services/Controller
```properties
ShutDown_Controller=Shutdown Controller
SC_Status_Icon=scStatus
StreamController_Name=stcName
TaskController_Name=tcrName
```

### Property Grid
```properties
PropertyGrid_Grid=property
PropertyGrid_TextBox=//div[text()='property']/parent::td/parent::tr//input[contains(@class,'TextBox')]
PropertyGrid_Dropdown_1=//div[text()='property']/parent::td/parent::tr//div[starts-with(@id,'gwt_uid_')]
PropertyGrid_CheckBox=//div[text()='property']/parent::td/parent::tr//input[@type='checkbox']
PropertyGrid_Date=//div[text()='property']/parent::td/parent::tr//input[contains(@class,'DateBox')]
```

### Configure Grid
```properties
ConfigureGrid_Icon=configureGridIcon
ConfigureGrid_Add=configureGridTBModel.add
ConfigureGrid_Delete=configureGridTBModel.delete
ConfigureGrid_MoveUp=configureGridTBModel.moveUp
ConfigureGrid_MoveDown=configureGridTBModel.moveDown
```

---

## PS_OR.properties — ROCPS Domain (1,099 lines)

### Account
```properties
accountName_ColHeader=grid_column_header_filtersearchGrid_paccName
accountName_Detail=paccName
accountType_Detail=paccCustomerType_gwt_uid_
accountRef_Detail=paccReference
accountStatus_Detail=paccStatusCd_gwt_uid_
accountParent_Detail=parentAccountDual$paccName
accountManagingAgent_Detail=agent
accountClassification_Detail=paccClassification_gwt_uid_
accountSalesRegion_Detail=salesRegion_gwt_uid_
accountPaymentType_Detail=paymentType_gwt_uid_
```

### Bill Profile
```properties
detail_bip_profileType_ComboId=billProfileType_gwt_uid_
detail_bip_account_entitySearchId=account
detail_bip_detail_tabXpath=//div[text()='Details']
detail_bip_profileName_txtId=pbipName
detail_bip_externalRef_txtId=pbipExternalRef
detail_bip_language_comboId=language_gwt_uid_
detail_bip_paymentTerm_comboId=paymentTerm_gwt_uid_
detail_bip_billingCycle_comboId=billingCycle_gwt_uid_
detail_bip_currency_timeline=//div[@id='currencyTimeLine']
```

### Event Identifier
```properties
Detail_eventDefn_eventName_txtId=peidName
Detail_eventValue_name_txtId=peivName
Detail_eventIdentifierValueGrp_txtID=peigName
```

### Operator
```properties
Detail_operator_name_txtId=poprName
Detail_operator_code_txtId=poprCode
```

### Dial String Set
```properties
Detail_dialString_name_txtID=pdstName
Detail_dialString_nameDetail_txtId=dsdNameEditor
```

### Agent
```properties
Detail_companyName_txtID=pageCompanyName
Detail_Franchise_comboID=franchise_gwt_uid_
```

### Switch
```properties
Detail_name_txtId=swtName
Detail_matchString_txtId=swtMatchStr
Detail_switchType_comboId=switchType_gwt_uid_
```

### Event Match Rule Group
```properties
PSDetail_emrName_txtId=pmrgName
```

### Route & Route Group
```properties
PS_Detail_routeGroup_name_txtId=pergName
PS_Detail_Route_Name_txtID=route$rutName
```

### Event Type
```properties
PS_Detail_EventType_Name_txtID=petyName
```

### Event Modelling
```properties
PS_Detail_modelingDefn_name_txtID=pemdName
```

### Event Normalization
```properties
PS_Detail_eventNormalization_name_txtID=pnrmName
```

### Aggregation
```properties
PSDetail_agcName_txtid=pagcConfigName
PSDetail_agctablePrefix_txtId=pagcTablePrefix
PS_Detail_AggrProcessor_name_txtID=paorName
PS_Detail_AggrProcessor_type_ComboID=component_gwt_uid_
```

### Event Match Rule
```properties
PS_EvtMatchRuleName_TxtId=pemrName
```

### Streams
```properties
# Stream name and configuration locators
# (Part of larger stream management section)
```

### Cross FX Rates
```properties
# Source/target currency, rate, reverse rate locators
```

### Bill Packages
```properties
# Package name, franchise, sales tax, number component locators
```

### Rate Sheet Template
```properties
# Template name, code, description, file upload locators
```

### Credit Notes
```properties
# Credit note with currency, status, transaction date locators
```

---

## Tariff_OR.properties — Tariff Management (178 lines)

### Elements & Bands
```properties
ElementSet_Name=estName
ElementSet_Combo=elementSetEd_gwt_uid_
Elements_Name=eltName
Elements_Country=country_gwt_uid_
Bands_Name=bndName
Bands_BandType=bandType_gwt_uid_
BandType_Name=bdtName
BandType_ClassCode=bdtClassCd
```

### Tariff Configuration
```properties
TariffClass_Name=tcsName
TariffClass_TariffType=tariffType_gwt_uid_
Tariff_Name=tffName
Tariff_TariffClass=tariffClass_gwt_uid_
TariffType_Code=tftCode
TariffType_Name=tftName
TariffMetricType_Name=tfmName
TariffRateName_Name=trnName
```

### Switch & Route
```properties
Switch_Name=swtName
Switch_MatchString=swtMatchStr
RouteClass_Name=rtcName
RouteType_Name=rttName
```

### Fast Entry
```properties
FastEntry_EffectiveDate=effectiveDate
```

---

## Users_OR.properties — User Management (96 lines)

```properties
Roles_RoleName=rolName
Users_Username=usrName
Users_NewPassword=usrPassword
Users_UserDisplayName=usrDisplayName
Teams_Name=teaName
Teams_Users_Grid=teamUserGrid
Teams_Users_ComboBox=userCombo_gwt_uid_
Teams_Save=teamDetail.save
```

---

## CM_OR.properties — Case Management (231 lines)

```properties
CaseProperty_Name=propertyDfn.prdName
BusinessRule_Name=sbrdName
Workstep_Name=wstName
Workflow_Name=wflName
CaseTemplate_Name=cteName
CaseTemplate_Workflow=workFlow_gwt_uid_
CaseGroup_Name=cgdName
```

---

## Measures_Audits_OR.properties — Measures & Audits (394 lines)

Key sections: Measure Input, Reporting Table, QueryMeasure, ComparisonMeasure, DataMatchMeasure, SQLMeasure, TrendMeasure, KPI Definition, Audit Definition/Request/Schedule, Zen Definitions.

---

## LDC_OR.properties — LDC (104 lines)

```properties
OfflineLDC_Name=sldcName
OnlineLDC_Name=oldcName
```

---

## ROCView_OR.properties — Dashboard (99 lines)

```properties
ROCView_Title=pageTitle
ROCView_Dashboard_Dropdown=selectedDashboard
Visualizer_Bar_Chart=barChartId
Visualizer_Line_Chart=lineChartId
Visualizer_Table_Title=//input[contains(@class,'gridster-item-content')]
```

---

## ROCRA_OR.properties — Rate Analysis (32 lines)

```properties
LVRConfiguration_Grid=lvrConfig
LVRIdentifier_ReportingTable_Panel=cell-list-container
```
