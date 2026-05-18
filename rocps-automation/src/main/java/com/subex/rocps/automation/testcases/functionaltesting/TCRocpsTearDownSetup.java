package com.subex.rocps.automation.testcases.functionaltesting;

import com.subex.automation.helpers.application.ROCHelper;
import com.subex.automation.helpers.component.GenericHelper;
import com.subex.automation.helpers.report.Log4jHelper;
import com.subex.automation.helpers.setup.SetupHelper;
import com.subex.automation.helpers.util.FailureHelper;
import com.subex.rocps.automation.helpers.selenium.PSAcceptanceTest;

/**
 * Tears down a ROCPS client setup created by {@link TCRocpsCreateNewSetup}:
 * stops Task/Stream controllers and Tomcat, removes the deployed client and
 * downloaded binaries, and drops the Reference/Usage databases. All steps are
 * SSH/JDBC based (no browser).
 */
public class TCRocpsTearDownSetup extends PSAcceptanceTest
{

	private SetupHelper newSetupHelper() throws Exception
	{
		String downloadPath = GenericHelper.getPath( applicationOS, configProp.getBinaryDownloadPath() );
		String tomcatPath = GenericHelper.getPath( applicationOS, configProp.getTomcatPath() );
		ROCHelper rocHelper = new ROCHelper();
		String clientContextPath = rocHelper.getTomcatContextPath();
		return new SetupHelper( downloadPath, tomcatPath, clientContextPath );
	}

//	@org.testng.annotations.Test( priority = 1, description = "Stop Task Controller" )
//	public void stopTaskController() throws Exception
//	{
//		try
//		{
//			Log4jHelper.logInfo( "Stopping Task Controller\n" );
//			newSetupHelper().stopTaskControllerPS();
//			Log4jHelper.logInfo( "Stopped Task Controller\n" );
//		}
//		catch ( Exception e )
//		{
//			FailureHelper.reportFailure( e );
//			throw e;
//		}
//	}
//
//	@org.testng.annotations.Test( priority = 2, description = "Stop Stream Controller" )
//	public void stopStreamController() throws Exception
//	{
//		try
//		{
//			Log4jHelper.logInfo( "Stopping Stream Controller\n" );
//			newSetupHelper().stopStreamControllerPS();
//			Log4jHelper.logInfo( "Stopped Stream Controller\n" );
//		}
//		catch ( Exception e )
//		{
//			FailureHelper.reportFailure( e );
//			throw e;
//		}
//	}
//
//	@org.testng.annotations.Test( priority = 3, description = "Stop Tomcat services" )
//	public void stopTomcat() throws Exception
//	{
//		try
//		{
//			Log4jHelper.logInfo( "Stopping Tomcat\n" );
//			newSetupHelper().stopROCPSTomcat();
//			Log4jHelper.logInfo( "Stopped Tomcat\n" );
//		}
//		catch ( Exception e )
//		{
//			FailureHelper.reportFailure( e );
//			throw e;
//		}
//	}

//	@org.testng.annotations.Test( priority = 4, description = "Cleanup deployed client under tomcat webapps" )
//	public void cleanupClientDeploy() throws Exception
//	{
//		try
//		{
//			Log4jHelper.logInfo( "Cleaning deployed client\n" );
//			newSetupHelper().cleanupClientDeploy();
//			Log4jHelper.logInfo( "Cleaned deployed client\n" );
//		}
//		catch ( Exception e )
//		{
//			FailureHelper.reportFailure( e );
//			throw e;
//		}
//	}

//	@org.testng.annotations.Test( priority = 5, description = "Cleanup binary download path" )
//	public void cleanupBinaryDownloadPath() throws Exception
//	{
//		try
//		{
//			Log4jHelper.logInfo( "Cleaning binary download path\n" );
//			newSetupHelper().cleanupBinaryDownloadPath();
//			Log4jHelper.logInfo( "Cleaned binary download path\n" );
//		}
//		catch ( Exception e )
//		{
//			FailureHelper.reportFailure( e );
//			throw e;
//		}
//	}

	@org.testng.annotations.Test( priority = 6, description = "Drop Reference and Usage databases" )
	public void dropDatabases() throws Exception
	{
		try
		{
			SetupHelper setupHelper = newSetupHelper();
			Log4jHelper.logInfo( "Dropping Reference database\n" );
			setupHelper.dropDB( "Reference", "Drop Reference DB" );
			Log4jHelper.logInfo( "Dropping Usage database\n" );
			setupHelper.dropDB( "Usage", "Drop Usage DB" );
			Log4jHelper.logInfo( "Dropped Reference and Usage databases\n" );
		}
		catch ( Exception e )
		{
			FailureHelper.reportFailure( e );
			throw e;
		}
	}
}
