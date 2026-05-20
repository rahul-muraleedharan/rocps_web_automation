package com.subex.rocps.automation.helpers.application.genericHelpers;

import com.subex.automation.helpers.config.PropertyReader;
import com.subex.automation.helpers.data.ValidationHelper;
import com.subex.automation.helpers.report.Log4jHelper;
import com.subex.automation.helpers.selenium.AcceptanceTest;
import com.subex.automation.helpers.util.FailureHelper;
import com.subex.automation.helpers.util.JavaVersionHelper;
import com.subex.automation.helpers.util.RemoteMachineHelper;

public class PSRunStreamController implements Runnable
{

	private PropertyReader propConfig;
	private String deployPath;
	private String scPort;
	private String scExeFilename;

	private String remoteEnvPrefix = "";

	public PSRunStreamController( PropertyReader propConfig ) throws Exception
	{
		this.propConfig = propConfig;
		this.deployPath = AcceptanceTest.deployPath;
		this.scPort = propConfig.getStringProperty( "scPort", "7800" );
		this.scExeFilename = propConfig.getStringProperty( "streamControllerExeFile", "sc.sh" );

		this.remoteEnvPrefix = buildRemoteEnvPrefix( propConfig );
	}

	public void run()
	{
		try
		{
			Log4jHelper.logInfo( "Starting Stream Controller..." );
			if ( propConfig.getOS().equalsIgnoreCase( "Windows" ) )
			{
				if ( deployPath.startsWith( "\"" ) )
					Runtime.getRuntime().exec( "cmd /c start /min title StreamController ^& eclipse\\scripts\\RunStreamController.bat " + deployPath );
				else
					Runtime.getRuntime().exec( "cmd /c start /min title StreamController ^& eclipse\\scripts\\RunStreamController.bat \"" + deployPath + "\"" );
			}
			else
			{
				String command = "cd " + deployPath + "/bin && chmod 777 " + scExeFilename + " && ./" + scExeFilename + " -PORT=" + scPort;

				if ( ValidationHelper.isNotEmpty( remoteEnvPrefix ) )
					command = remoteEnvPrefix + " && " + command;

				RemoteMachineHelper remoteMachine = new RemoteMachineHelper();
				remoteMachine.executeScripts( command, "Stream controller started" );
			}
		}
		catch ( Exception e )
		{
			try
			{
				FailureHelper.setErrorMessage( e );
				throw e;
			}
			catch ( Exception e1 )
			{
				e1.printStackTrace();
			}
		}
	}

	/**
	 * Builds the remote shell prefix prepended to sc.sh: switches the system
	 * 'java' alternative to psconfig 'javaVersion' (via update-alternatives) and
	 * exports SPARK_TEMP. Honors the legacy 'java11Path' as a direct-path
	 * backstop when 'javaVersion' is not configured. Returns an empty string if
	 * neither is configured, leaving the existing default Java in place.
	 */
	private static String buildRemoteEnvPrefix( PropertyReader propConfig ) throws Exception
	{
		String javaVersion = propConfig.getStringProperty( "javaVersion", "" );
		String sparkTempPath = propConfig.getStringProperty( "sparkTempPath", "" );
		String java11Path = propConfig.getStringProperty( "java11Path", "" );

		StringBuilder prefix = new StringBuilder();

		if ( ValidationHelper.isNotEmpty( javaVersion ) )
		{
			prefix.append( JavaVersionHelper.selectRemoteJavaCommand( javaVersion, propConfig.getRemotePassword() ) );
		}
		else if ( ValidationHelper.isNotEmpty( java11Path ) )
		{
			prefix.append( "export JAVA_HOME=" ).append( java11Path ).append( " && export PATH=$JAVA_HOME/bin:$PATH" );
		}

		String sparkTempExport = JavaVersionHelper.exportSparkTempCommand( sparkTempPath );
		if ( ValidationHelper.isNotEmpty( sparkTempExport ) )
		{
			if ( prefix.length() > 0 )
				prefix.append( " && " );
			prefix.append( sparkTempExport );
		}

		return prefix.toString();
	}

}
