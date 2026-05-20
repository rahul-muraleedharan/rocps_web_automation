package com.subex.rocps.automation.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

import org.apache.commons.codec.binary.Base64;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import com.jcraft.jsch.ChannelSftp;

import com.subex.automation.helpers.data.ValidationHelper;
import com.subex.automation.helpers.report.Log4jHelper;
import com.subex.automation.helpers.util.DownloadBinaries;
import com.subex.automation.helpers.util.FailureHelper;
import com.subex.automation.helpers.util.RemoteMachineHelper;
import com.subex.rocps.automation.helpers.selenium.PSAcceptanceTest;

/**
 * Resolves and downloads ROCPS client (war) and server (zip) binaries from the
 * ROCPS release Jenkins. Release version, base URL and download destination are
 * read from psconfig.properties; Jenkins credentials reuse jenkinsUsername /
 * jenkinsPassword, same keys DownloadBinaries already expects.
 */
public class RocpsJenkinsHelper extends PSAcceptanceTest {

	private static final String CLIENT_JOB_SUFFIX = "-Client-Release";
	private static final String SERVER_JOB_SUFFIX = "-Server-Release";
	private static final String CLIENT_MODULE = "com.subex.rocpsro$entrypoint";
	private static final String SERVER_MODULE = "com.subex.rocpsro$rocps-server-distribution";
	private static final String JOB_PREFIX = "ROCPSRO-";

	public String clientURL() throws Exception {
		return clientURL( configuredRelease() );
	}

	public String clientURL( String release ) throws Exception {
		return resolveArtifactURL( moduleBuildURL( release, CLIENT_JOB_SUFFIX, CLIENT_MODULE ), clientFileName( release ) );
	}

	public String serverURL() throws Exception {
		return serverURL( configuredRelease() );
	}

	public String serverURL( String release ) throws Exception {
		return resolveArtifactURL( moduleBuildURL( release, SERVER_JOB_SUFFIX, SERVER_MODULE ), serverFileName( release ) );
	}

	public String clientFileName( String release ) {
		return "entrypoint-" + release + ".war";
	}

	public String serverFileName( String release ) {
		return "rocps-server-distribution-" + release + "-bin.zip";
	}

	public File downloadClient() throws Exception {
		return downloadClient( configuredRelease() );
	}

	public File downloadClient( String release ) throws Exception {
		return downloadTo( clientURL( release ), clientFileName( release ) );
	}

	public File downloadServer() throws Exception {
		return downloadServer( configuredRelease() );
	}

	public File downloadServer( String release ) throws Exception {
		return downloadTo( serverURL( release ), serverFileName( release ) );
	}

	private File downloadTo( String url, String fileName ) throws Exception {
		try {
			String configuredPath = configuredDownloadPath();
			while ( configuredPath.endsWith( "/" ) ) {
				configuredPath = configuredPath.substring( 0, configuredPath.length() - 1 );
			}
			String buildNo = configProp.getStringProperty( "local_buildno" );
			String targetDir = ValidationHelper.isNotEmpty( buildNo ) ? configuredPath + "/" + buildNo : configuredPath;
			boolean remote = ValidationHelper.isNotEmpty( configProp.getRemoteHostname() );

			File stagingDir = remote ? new File( System.getProperty( "java.io.tmpdir" ), "rocps-binaries" )
			                         : new File( targetDir );
			if ( !stagingDir.exists() && !stagingDir.mkdirs() ) {
				FailureHelper.failTest( "Could not create download directory: " + stagingDir.getAbsolutePath() );
			}
			File staged = new File( stagingDir, fileName );

			Log4jHelper.logInfo( "ROCPS Jenkins URL : " + url );
			Log4jHelper.logInfo( "Local staging     : " + staged.getAbsolutePath() );
			if ( remote ) {
				Log4jHelper.logInfo( "Remote target     : " + configProp.getRemoteHostname() + ":" + targetDir + "/" + fileName );
			}

			DownloadBinaries download = new DownloadBinaries();
			if ( !download.isURLAvailable( url ) ) {
				FailureHelper.failTest( "ROCPS binary not available at " + url );
			}
			download.download( url, staged.getAbsolutePath() );

			if ( remote ) {
				uploadToRemote( staged, targetDir, fileName );
			}
			return staged;
		}
		catch ( Exception e ) {
			FailureHelper.setErrorMessage( e );
			throw e;
		}
	}

	/**
	 * Pushes the locally staged binary to the configured remote test server
	 * (remoteHostname/remoteUsername/remotePassword in psconfig.properties),
	 * creating the remote target directory with `mkdir -p` if necessary.
	 */
	private void uploadToRemote( File localFile, String remoteDir, String remoteFileName ) throws Exception {
		RemoteMachineHelper remote = new RemoteMachineHelper();
		remote.executeScripts( "mkdir -p \"" + remoteDir + "\"", true );
		remote.createSFTPConnection();
		ChannelSftp channel = sftpChannel;
		String dest = remoteDir.endsWith( "/" ) ? remoteDir + remoteFileName : remoteDir + "/" + remoteFileName;
		channel.put( localFile.getAbsolutePath(), dest, ChannelSftp.OVERWRITE );
		Log4jHelper.logInfo( "Uploaded " + localFile.getName() + " (" + localFile.length() + " bytes) to remote " + dest );
	}

	private String moduleBuildURL( String release, String jobSuffix, String module ) throws Exception {
		String job = JOB_PREFIX + release + jobSuffix;
		return viewBase( release ) + "/job/" + job + "/lastSuccessfulBuild/" + module;
	}

	/**
	 * Asks Jenkins for the build's artifact list, finds the entry whose
	 * fileName matches and returns its direct-download URL
	 * (<buildURL>/artifact/<relativePath>). The Maven module URL Jenkins
	 * uses for browsing renders an HTML page with download icons rather
	 * than streaming the binary, so it cannot be fetched directly.
	 */
	private String resolveArtifactURL( String buildURL, String fileName ) throws Exception {
		String apiURL = buildURL + "/api/json?tree=" + URLEncoder.encode( "artifacts[fileName,relativePath]", StandardCharsets.UTF_8 );
		HttpURLConnection con = (HttpURLConnection) new URL( apiURL ).openConnection();
		String authStringEnc = jenkinsAuthHeader();
		if ( ValidationHelper.isNotEmpty( authStringEnc ) ) {
			con.setRequestProperty( "Authorization", "Basic " + authStringEnc );
		}
		con.setRequestMethod( "GET" );

		int code = con.getResponseCode();
		if ( code != 200 ) {
			con.disconnect();
			FailureHelper.failTest( "Could not query Jenkins build API '" + apiURL + "' (HTTP " + code + ")" );
		}

		StringBuilder body = new StringBuilder();
		try ( BufferedReader br = new BufferedReader( new InputStreamReader( con.getInputStream(), StandardCharsets.UTF_8 ) ) ) {
			String line;
			while ( ( line = br.readLine() ) != null ) {
				body.append( line );
			}
		}
		finally {
			con.disconnect();
		}

		JSONObject root = (JSONObject) new JSONParser().parse( body.toString() );
		JSONArray artifacts = (JSONArray) root.get( "artifacts" );
		if ( artifacts != null ) {
			for ( Object o : artifacts ) {
				JSONObject a = (JSONObject) o;
				if ( fileName.equals( a.get( "fileName" ) ) ) {
					return buildURL + "/artifact/" + a.get( "relativePath" );
				}
			}
		}
		FailureHelper.failTest( "Artifact '" + fileName + "' not found in Jenkins build at " + buildURL );
		return null;
	}

	private String jenkinsAuthHeader() throws Exception {
		String userName = configProp.getJenkinsUsername();
		String password = configProp.getJenkinsPassword();
		if ( ValidationHelper.isEmpty( userName ) && ValidationHelper.isEmpty( password ) ) {
			return null;
		}
		String authString = userName + ":" + password;
		return new String( Base64.encodeBase64( authString.getBytes( StandardCharsets.UTF_8 ) ), StandardCharsets.UTF_8 );
	}

	private String configuredRelease() throws Exception {
		String release = configProp.getStringProperty( "rocpsReleaseVersion" );
		if ( ValidationHelper.isEmpty( release ) ) {
			FailureHelper.failTest( "rocpsReleaseVersion is not set in psconfig.properties" );
		}
		return release;
	}

	private String configuredDownloadPath() throws Exception {
		return configProp.getStringProperty( "rocpsDownloadPath" );
	}

	private String viewBase( String release ) throws Exception {
		String base = configProp.getStringProperty( "rocpsJenkinsBaseURL" );
		if ( base.endsWith( "/" ) ) {
			base = base.substring( 0, base.length() - 1 );
		}
		return base + "/view/" + release;
	}
}
