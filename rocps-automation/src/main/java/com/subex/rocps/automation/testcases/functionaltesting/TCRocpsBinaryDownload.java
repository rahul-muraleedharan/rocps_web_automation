package com.subex.rocps.automation.testcases.functionaltesting;

import java.io.File;

import com.subex.automation.helpers.report.Log4jHelper;
import com.subex.automation.helpers.util.FailureHelper;
import com.subex.rocps.automation.helpers.selenium.PSAcceptanceTest;
import com.subex.rocps.automation.utils.RocpsJenkinsHelper;

public class TCRocpsBinaryDownload extends PSAcceptanceTest
{

	@org.testng.annotations.Test( priority = 1, description = "Download ROCPS client WAR from Jenkins" )
	public void downloadClient() throws Exception
	{
		try
		{
			RocpsJenkinsHelper jenkins = new RocpsJenkinsHelper();
			File war = jenkins.downloadClient();
			assertTrue( war.exists() && war.length() > 0, "Client WAR was not downloaded: " + war.getAbsolutePath() );
			Log4jHelper.logInfo( "Client WAR downloaded to " + war.getAbsolutePath() + " (" + war.length() + " bytes)" );
		}
		catch ( Exception e )
		{
			FailureHelper.reportFailure( e );
			throw e;
		}
	}

	@org.testng.annotations.Test( priority = 2, description = "Download ROCPS server ZIP from Jenkins" )
	public void downloadServer() throws Exception
	{
		try
		{
			RocpsJenkinsHelper jenkins = new RocpsJenkinsHelper();
			File zip = jenkins.downloadServer();
			assertTrue( zip.exists() && zip.length() > 0, "Server ZIP was not downloaded: " + zip.getAbsolutePath() );
			Log4jHelper.logInfo( "Server ZIP downloaded to " + zip.getAbsolutePath() + " (" + zip.length() + " bytes)" );
		}
		catch ( Exception e )
		{
			FailureHelper.reportFailure( e );
			throw e;
		}
	}
}
