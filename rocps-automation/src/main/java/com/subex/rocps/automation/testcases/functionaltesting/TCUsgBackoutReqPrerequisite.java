package com.subex.rocps.automation.testcases.functionaltesting;

import com.subex.automation.helpers.application.screens.BandHelper;
import com.subex.automation.helpers.application.screens.ElementCreateHelper;
import com.subex.automation.helpers.application.screens.TariffClassHelper;
import com.subex.automation.helpers.application.screens.TariffHelper;
import com.subex.automation.helpers.util.FailureHelper;
import com.subex.rocps.automation.helpers.application.matchandrate.EventIdentiferDefinition;
import com.subex.rocps.automation.helpers.application.matchandrate.EventMatchRule;
import com.subex.rocps.automation.helpers.application.matchandrate.EventMatchRuleGroup;
import com.subex.rocps.automation.helpers.application.matchandrate.Operator;
import com.subex.rocps.automation.helpers.application.networkConfiguraiton.Route;
import com.subex.rocps.automation.helpers.application.networkConfiguraiton.RouteGroup;
import com.subex.rocps.automation.helpers.application.networkConfiguraiton.Switch;
import com.subex.rocps.automation.helpers.application.partnerConfiguration.Account;
import com.subex.rocps.automation.helpers.application.partnerConfiguration.Agent;
import com.subex.rocps.automation.helpers.application.partnerConfiguration.BillProfile;
import com.subex.rocps.automation.helpers.application.referenceTable.ElementSet;
import com.subex.rocps.automation.helpers.application.system.Streams;
import com.subex.rocps.automation.helpers.application.tariffs.PSTariffHelper;
import com.subex.rocps.automation.helpers.selenium.PSAcceptanceTest;

public class TCUsgBackoutReqPrerequisite extends PSAcceptanceTest
{
	String path = System.getProperty( "user.dir" ) + "\\src\\main\\resources\\";

	String workBookName = "UsageBackoutRequestTestCases.xlsx";
	String sheetName = "Prerequistes";

	@org.testng.annotations.Test(priority = 1, enabled = true, description = "edit Voice stream add'UsageBackout Master Request and UsageBackout stream stages'", retryAnalyzer = com.subex.rocps.automation.helpers.listener.Retry.class)
	public void editVoiceStreamForUsgBackoutReq() throws Exception {
		try {
			String partition = null;
			Streams streamObj = new Streams();
			streamObj.editStreamConfig(path, workBookName, "UsageBackoutRequest", "Subex Voice Streams", 1);
			streamObj.usgBackoutMasterReqStreamStageConfig(path, workBookName, "UsageBackoutRequest", "UsageBackoutMasterRequest_StreamStage", 1);
			streamObj.usgBackoutStreamStageConfig(path, workBookName, "UsageBackoutRequest", "UsageBackout_StreamStage", 1);
			streamObj.saveStreamDetail();

		} catch (Exception e) {
			FailureHelper.setErrorMessage(e);
			throw e;
		}
	}

	@org.testng.annotations.Test(priority = 2, enabled = true, description = "Bill Profile, Route for usgBackoutReqPrerequistes", retryAnalyzer = com.subex.rocps.automation.helpers.listener.Retry.class)
	public void usgBackoutReqPrerequistes() throws Exception {
		try {
			Agent agobj = new Agent(path, workBookName, sheetName, "Agent", 1);
			agobj.agentCreation();

			Account accobj = new Account(path, workBookName, sheetName, "Account_UBR", 1);
			accobj.accountCreation();

			BillProfile billObj = new BillProfile(path, workBookName, sheetName, "BillProfile_UBR", 1);
			billObj.billProfileCreation();

			Switch switchObj = new Switch(path, workBookName, sheetName, "Switch_UBR");
			switchObj.configureSwitch();
			Switch switchObj2 = new Switch(path, workBookName, sheetName, "SwitchAdditional");
			switchObj2.configureSwitch();

			Operator ope1Obj = new Operator(path, workBookName, sheetName, "Operator_UBR", 1);
			ope1Obj.operatorCreation();

			RouteGroup routeGrpObj = new RouteGroup(path, workBookName, sheetName, "RouteGroup Transit_UBR");
			routeGrpObj.routeGrpCreation();

			Route routeColObj = new Route(path, workBookName, sheetName, "Route Transit_UBR");
			routeColObj.routeCreation();

		} catch (Exception e) {
			FailureHelper.reportFailure(e);
			throw e;
		}
	}

	@org.testng.annotations.Test(priority = 3, enabled = true, description = "Element, Band, Tariff for usgBackoutReqPrerequistes", retryAnalyzer = com.subex.rocps.automation.helpers.listener.Retry.class)
	public void usgBackoutReqPrerequistes2() throws Exception {
		try {
			ElementSet elesetObj = new ElementSet(path, workBookName, sheetName, "ElementSet Transit", 1);
			elesetObj.elementSetCreation();

			ElementCreateHelper eleObj = new ElementCreateHelper();
			eleObj.createElement(path, workBookName, sheetName, "Elements Transit", 1);

			BandHelper bandObj = new BandHelper();
			bandObj.createBand(path, workBookName, sheetName, "Bands Transit", 1);

			TariffClassHelper trffClassObj = new TariffClassHelper();
			trffClassObj.createTariffClass(path, workBookName, sheetName, "TariffClass Transit", 1);

			PSTariffHelper pstrffObj1 = new PSTariffHelper();
			pstrffObj1.createTariff(path, workBookName, sheetName, "Tariff Transit", 1);
			pstrffObj1.createFastEntry(path, workBookName, sheetName, "Tariff Transit FastEntry", 1);

		} catch (Exception e) {
			FailureHelper.reportFailure(e);
			throw e;
		}
	}

	@org.testng.annotations.Test( priority = 4, enabled = true, description = "Event matchRule for usgBackoutReqPrerequistes2", retryAnalyzer = com.subex.rocps.automation.helpers.listener.Retry.class )
	public void usgBackoutReqPrerequistes3() throws Exception

	{
		try
		{
            EventIdentiferDefinition eidObj = new EventIdentiferDefinition(path, workBookName, sheetName, "EventDefn FullMatch");
            eidObj.eventCreation();

			EventMatchRuleGroup eventValObj = new EventMatchRuleGroup( path, workBookName, sheetName, "EMRG Transit", 1 );
			eventValObj.configureEventMatchRuleGroup();

			EventMatchRule emrObj = new EventMatchRule( path, workBookName, sheetName, "EMR Transit_Disp" );
			emrObj.configureEventMatchRule();

		}
		catch ( Exception e )
		{
			FailureHelper.reportFailure( e );
			throw e;
		}
	}

}
