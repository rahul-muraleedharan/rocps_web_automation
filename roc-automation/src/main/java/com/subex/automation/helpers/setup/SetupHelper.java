package com.subex.automation.helpers.setup;

import java.io.File;

import com.subex.automation.helpers.application.ControllerHelper;
import com.subex.automation.helpers.application.LoginHelper;
import com.subex.automation.helpers.application.ROCHelper;
import com.subex.automation.helpers.application.screens.SettingsHelper;
import com.subex.automation.helpers.component.ButtonHelper;
import com.subex.automation.helpers.component.FileHelper;
import com.subex.automation.helpers.component.GenericHelper;
import com.subex.automation.helpers.component.PropertyGridHelper;
import com.subex.automation.helpers.component.TabHelper;
import com.subex.automation.helpers.component.TextBoxHelper;
import com.subex.automation.helpers.data.DateHelper;
import com.subex.automation.helpers.data.StringHelper;
import com.subex.automation.helpers.data.ValidationHelper;
import com.subex.automation.helpers.db.CreateNewDB;
import com.subex.automation.helpers.db.DBHelper;
import com.subex.automation.helpers.file.XMLReader;
import com.subex.automation.helpers.report.Log4jHelper;
import com.subex.automation.helpers.report.ReportHelper;
import com.subex.automation.helpers.scripts.ExecuteScript;
import com.subex.automation.helpers.selenium.ROCAcceptanceTest;
import com.subex.automation.helpers.util.DownloadBinaries;
import com.subex.automation.helpers.util.FailureHelper;
import com.subex.automation.helpers.util.RemoteMachineHelper;
import com.subex.automation.helpers.util.RunInstaller;
import com.subex.automation.helpers.util.UnzipHelper;

public class SetupHelper extends ROCAcceptanceTest {
	
	private String ciwFile = null;
	private String licenseOperator = null;
	private String licenseKey = null;
	
	private static String downloadPath = null;
	private String serverFileName = null;
	private String tomcatPath = null;
	private String clientContextPath = null;
	private String warFileName = null;
	
	public SetupHelper(String downloadPath, String tomcatPath, String clientContextPath) throws Exception {
		try {
			ciwFile = configProp.getStringProperty("ciwFile", "spark.ciw");
			licenseOperator = configProp.getStringProperty("licenseOperator", "Subex Ltd");

			SetupHelper.downloadPath = downloadPath;
			serverFileName = configProp.getServerFileName();
			this.tomcatPath = tomcatPath;
			this.clientContextPath = clientContextPath;
			warFileName = clientContextPath + ".war";
		} catch (Exception e) {
			FailureHelper.setErrorMessage(e);
			throw e;
		}
	}

	private String getLicenseKey() throws Exception {
		try {
			if (licenseKey == null) {
				String licenseFile = configProp.getStringProperty("licenseFile", "License.txt");
				licenseKey = FileHelper.readFileContent(automationPath + "\\src\\main\\resources\\" + licenseFile);
				licenseKey = licenseKey.replace("\n", "");
			}
			return licenseKey;
		} catch (Exception e) {
			FailureHelper.setErrorMessage(e);
			throw e;
		}
	}
	
	private void cleanupReport(String stepname) throws Exception {
		try {
			ReportHelper.clearStepKeyContent();
			stepName = stepname;
		} catch (Exception e) {
			FailureHelper.setErrorMessage(e);
			throw e;
		}
	}
	
	private void updateReport(String message) throws Exception {
		try {
			ReportHelper.updateStepKey(stepName, "Green", message);
			ReportHelper.reportSuccess(null, false, null);
		} catch (Exception e) {
			FailureHelper.setErrorMessage(e);
			throw e;
		}
	}
	
	public void stopAllServices() throws Exception {
		try {
			ControllerHelper controller = new ControllerHelper();
			cleanupReport("Stop Services");
			
			if (TextBoxHelper.isPresent("Login_Username_TextBox") || ButtonHelper.isPresent("NavigationMenu")) {
				controller.stopServices();
			}
			else {
				controller.killProcess("Server");
				controller.killProcess("TaskControllerService");
				controller.killProcess("StreamControllerService");
			}
			
			controller.stopTomcat();
			updateReport("Done");
		} catch (Exception e) {
			FailureHelper.setErrorMessage(e);
			throw e;
		}
	}
	
	public void createDB(String type, String stepname) throws Exception {
		try {
			cleanupReport(stepname);
			
			CreateNewDB createDB = new CreateNewDB();
			createDB.createDataBase(type);
			
			updateReport("Created");
		} catch (Exception e) {
			FailureHelper.setErrorMessage(e);
			throw e;
		}
	}

	public void backupBinaries() throws Exception {
		try {
			cleanupReport("Backup Binaries");
			
			String currentDateTime = DateHelper.getCurrentDateTime("ddMMyyyy_HHmm");
			boolean backupCompleted = false;
			
			if (FileHelper.checkFileExists(downloadPath + "/" + serverFileName)) {
				String serverFile = currentDateTime + "_" + serverFileName;
				FileHelper.renameFile(applicationOS, downloadPath, serverFileName, serverFile);
				Log4jHelper.logInfo("'" + serverFileName + "' renamed to '" + serverFile + "'");
				backupCompleted = true;
			}
			
			if (FileHelper.checkFileExists(downloadPath + "/" + warFileName)) {
				String clientFile = currentDateTime + "_" + warFileName;
				FileHelper.renameFile(applicationOS, downloadPath, warFileName, clientFile);
				Log4jHelper.logInfo("'" + warFileName + "' renamed to '" + clientFile + "'");
				backupCompleted = true;
			}
			
			if (ValidationHelper.isTrue(configProp.getFreshInstallation())) {
				String datadir = configProp.getDataDirPath();
				if (FileHelper.checkDirectoryExists(applicationOS, datadir))
						FileHelper.cleanUpDir(applicationOS, datadir, true);
				if (FileHelper.isDirectoryEmpty(applicationOS, datadir + "/Task Logs"))
					FileHelper.renameFile(applicationOS, datadir + "/Task Logs", currentDateTime + "_" + "Task_Logs");
			}
			
			if (backupCompleted)
				updateReport("Completed");
		}
		catch (Exception e) {
			FailureHelper.setErrorMessage(e);
			throw e;
		}
	}
	
	public void dirCleanup() throws Exception {
		try {
			cleanupReport("Directory Cleanup");
			if (!FileHelper.checkDirectoryExists(applicationOS, downloadPath))
				FileHelper.cleanUpDir(applicationOS, downloadPath, true);
			String serverExtractedDir = serverFileName.replaceAll("(-bin)?\\.zip$", "");
			FileHelper.cleanUpDir(applicationOS, downloadPath + "/" + serverExtractedDir, false);
			
			if (ValidationHelper.isFalse(configProp.getBackupOldBinaries())) {
				FileHelper.deleteFile(applicationOS, downloadPath + "/" + serverFileName);
				FileHelper.deleteFile(applicationOS, downloadPath + "/" + warFileName);
			}
			
			FileHelper.cleanUpDir(applicationOS, tomcatPath + "/webapps/" + clientContextPath, false);
			FileHelper.cleanUpDir(applicationOS, tomcatPath + "/logs/", true);
			
			String workDirectory = tomcatPath + "/work/Catalina/localhost/";
			if (FileHelper.checkDirectoryExists(applicationOS, workDirectory))
				FileHelper.cleanUpDir(applicationOS, workDirectory, true);
			FileHelper.deleteFile(applicationOS, tomcatPath + "/webapps/" + warFileName);
			
			if (ValidationHelper.isTrue(configProp.getFreshInstallation())) {
				String datadir = configProp.getDataDirPath();
				FileHelper.cleanUpDir(applicationOS, datadir + "/Task Logs/", false);
				FileHelper.cleanUpDir(applicationOS, datadir + "/Manual Import Log/", false);
				FileHelper.cleanUpDir(applicationOS, datadir + "/ParseOutput/", false);
				FileHelper.cleanUpDir(applicationOS, datadir + "/Collected Files/", false);
				FileHelper.cleanUpDir(applicationOS, datadir + "/Online_LDC/", false);
			}
			
			updateReport("Completed");
		} catch (Exception e) {
			FailureHelper.setErrorMessage(e);
			throw e;
		}
	}
	
	private static boolean download(DownloadBinaries download, String downloadURL, String fileName, boolean useAuthentication) throws Exception {
		try {
			if (download.isURLAvailable(downloadURL)) {
				Log4jHelper.logInfo("Download url is available\n");
				
				if (applicationOS.equalsIgnoreCase("Windows")) {
					download.download(downloadURL, downloadPath + "/" + fileName);
				}
				else {
					FileHelper.deleteFileIfExists(applicationOS, downloadPath + "/" + fileName);
					downloadURL = downloadURL.replace("$", "\\$");
					String command = null;
					if (useAuthentication && ValidationHelper.isNotEmpty(configProp.getJenkinsUsername()))
						command = "cd " + downloadPath + " && curl -o " + fileName + " -u " + configProp.getJenkinsUsername() + ":" + configProp.getJenkinsPassword() + " \"" + downloadURL + "\"";
					else
						command = "cd " + downloadPath + " && curl -o " + fileName + " \"" + downloadURL + "\"";
					
					RemoteMachineHelper remoteMachine = new RemoteMachineHelper();
					remoteMachine.executeScripts(command, true);
				}
				
				ReportHelper.updateStepKey("JENKINS URL", "Blue", downloadURL);
				return true;
			}
			else {
				Log4jHelper.logInfo("Download url is not available\n");
				return false;
			}
		}
		catch (Exception e) {
			FailureHelper.setErrorMessage(e);
			throw e;
		}
	}
	
	public void serverDownload() throws Exception {
		try {
			cleanupReport("Server Binary");
			
			DownloadBinaries downloadBinaries = new DownloadBinaries();
			JenkinsHelper jenkinsHelper = new JenkinsHelper();
			String artifactoryURL = configProp.getArtifactoryURL();
			boolean isURLAvailable = false;
			String downloadURL = null;
			
			String jenkinsURL = configProp.getJenkinsURL();
			downloadURL = jenkinsHelper.getDownloadURL(jenkinsURL, "Server");
			Log4jHelper.logInfo("Checking if Server binary download url is available in Jenkins\n");
			Log4jHelper.logInfo("Download url : " + downloadURL + "\n");
			isURLAvailable = download(downloadBinaries, downloadURL, serverFileName, true);
			
			if (!isURLAvailable && ValidationHelper.isNotEmpty(artifactoryURL)) {
				downloadURL = jenkinsHelper.artifactoryServerURL(artifactoryURL);
				Log4jHelper.logInfo("Checking if Server binary download url is available in Artifactory\n");
				Log4jHelper.logInfo("Download url : " + downloadURL + "\n");
				isURLAvailable = download(downloadBinaries, downloadURL, serverFileName, false);
			}
			
			if (isURLAvailable) {
				updateReport("Downloaded");
			}
			else {
				FailureHelper.failTest("Server binary download did not happen as download URL was not available");
			}
		}
		catch (Exception e) {
			FailureHelper.setErrorMessage(e);
			throw e;
		}
	}
	
	public void clientDownload() throws Exception {
		try {
			cleanupReport("Client Binary");
			
			DownloadBinaries downloadBinaries = new DownloadBinaries();
			JenkinsHelper jenkinsHelper = new JenkinsHelper();
			String artifactoryURL = configProp.getArtifactoryURL();
			boolean isURLAvailable = false;
			String downloadURL = null;
			
			String jenkinsURL = configProp.getJenkinsURL();
			downloadURL = jenkinsHelper.getDownloadURL(jenkinsURL, "Client");
			Log4jHelper.logInfo("Checking if Client binary download url is available in Jenkins\n");
			Log4jHelper.logInfo("Download url : " + downloadURL + "\n");
			isURLAvailable = download(downloadBinaries, downloadURL, warFileName, true);
			
			if (!isURLAvailable && ValidationHelper.isNotEmpty(artifactoryURL)) {
				downloadURL = jenkinsHelper.artifactoryClientURL(artifactoryURL);
				Log4jHelper.logInfo("Checking if Client binary download url is available in Artifactory\n");
				Log4jHelper.logInfo("Download url : " + downloadURL + "\n");
				isURLAvailable = download(downloadBinaries, downloadURL, warFileName, false);
			}
			
			if (isURLAvailable) {
				FileHelper.copyFile(downloadPath, tomcatPath + "/webapps/", warFileName, warFileName, true);
				updateReport("Downloaded");
			}
			else {
				FailureHelper.failTest("Client binary download did not happen as download URL was not available");
			}
		} catch (Exception e) {
			FailureHelper.setErrorMessage(e);
			throw e;
		}
	}
	
	public void rocviewDownload() throws Exception {
		try {
			cleanupReport("ROCView Binary");
			String downloadLocation = configProp.getROCViewFolderLocation();
			FileHelper.cleanUpDir(applicationOS, downloadLocation + "/view", false);
			UnzipHelper unzip = new UnzipHelper();
			unzip.unzip(applicationOS, "view.zip", downloadLocation);
			
			updateReport("Downloaded");
		} catch (Exception e) {
			FailureHelper.setErrorMessage(e);
			throw e;
		}
	}
	
	public void extractServerDeploy() throws Exception {
		try {
			cleanupReport("Server Deploy");
			
			ROCHelper rocHelper = new ROCHelper();
			String downloadedServerFile = downloadPath + "/" + serverFileName;
			String delimiter = "\\\\";
			if (!applicationOS.equalsIgnoreCase("Windows"))
				delimiter = "/";
			
			UnzipHelper unZip = new UnzipHelper();
			unZip.unzip(applicationOS, downloadedServerFile, downloadPath);
			deployPath = configProp.getDeployPath();
			FileHelper.cleanUpDir(applicationOS, deployPath, "No");
			
			if (ValidationHelper.isTrue(configProp.getCopyCompleteDeploy())) {
				FileHelper.cleanUpDir(applicationOS, deployPath, "No");
				
				String[] deploy = deployPath.split(delimiter);
				String deployFolderName = deploy[deploy.length-1];
				int size = deployPath.length() - deployFolderName.length();
				if (deployPath.endsWith("/"))
					size--;
				String deployFolderPath = deployPath.substring(0, size);
				
				FileHelper.copyFile(downloadPath, deployFolderPath, "deploy", "", true);
			}
			else {
				String[] filesToCopy = configProp.getDeployFilesToCopy().split(",");
				String downloadedDeploy = rocHelper.getDeployPath(downloadedServerFile);
				
				for (int i = 0; i < filesToCopy.length; i++) {
					String[] file = filesToCopy[i].split(delimiter);
					String fileName = file[file.length-1];
					int size = filesToCopy[i].length() - fileName.length();
					
					if (size > 0) {
						String path = filesToCopy[i].substring(0, size);
						FileHelper.copyFile(downloadedDeploy + path, deployPath + path, filesToCopy[i], filesToCopy[i], true);
					}
					else
						FileHelper.copyFile(downloadedDeploy, deployPath, filesToCopy[i], filesToCopy[i], true);
				}
			}
			
			deployPath = rocHelper.getDeployPath(configProp.getDeployPath());
			updateReport("Extracted");
		} catch (Exception e) {
			FailureHelper.setErrorMessage(e);
			throw e;
		}
	}
	
	private static int[] getLineNumber(String dbType, int startLineNumber) throws Exception {
		try {
			int[] lineNumber = new int[6];
			startLineNumber += 2;
			int length = 6;
			
			if (dbType.equalsIgnoreCase("oracle")) {
				lineNumber = new int[5];
				length = 5;
			}
			
			for (int i = 0; i < length; i++)
				lineNumber[i] = startLineNumber + i;
			
			return lineNumber;
		}
		catch (Exception e) {
			FailureHelper.setErrorMessage(e);
			throw e;
		}
	}
	
	private static String[] getReplaceProperties(String dbType, String[] dbDetails) throws Exception {
		try {
			String[] propertyValue = new String[6];
			String unicode = "N";
			if (ValidationHelper.isTrue(dbDetails[7]))
				unicode = "Y";
			
			if (dbType.equalsIgnoreCase("oracle")) {
				propertyValue = new String[5];
				propertyValue[0] = "DB_TYPE=Oracle";
				propertyValue[1] = "HOST_NAME=" + dbDetails[1];
				propertyValue[2] = "INSTANCE=" + dbDetails[5];
				propertyValue[3] = "PORT_NO=" + dbDetails[6];
				propertyValue[4] = "UNICODE=" + unicode;
			}
			else if (dbType.equalsIgnoreCase("sqlserver") || dbType.equalsIgnoreCase("sql server")) {
				propertyValue[0] = "DB_TYPE=MS SQL Server";
				propertyValue[1] = "HOST_NAME=" + dbDetails[1];
				propertyValue[2] = "DATABASE=" + dbDetails[4];
				propertyValue[3] = "UNICODE=" + unicode;
				
				if (ValidationHelper.isEmpty(dbDetails[5])) {
					propertyValue[4] = "IS_NAMED_INSTANCE=Y";
					propertyValue[5] = "NAMED_INSTANCE=";
				}
				else {
					propertyValue[4] = "IS_NAMED_INSTANCE=N";
					propertyValue[5] = "NAMED_INSTANCE=" + dbDetails[5];
				}
			}
			else if (dbType.equalsIgnoreCase("postgres") || dbType.equalsIgnoreCase("postgresql")) {
				propertyValue[0] = "DB_TYPE=Postgres";
				propertyValue[1] = "HOST_NAME=" + dbDetails[1];
				
				if (ValidationHelper.isEmpty(dbDetails[6]))
					propertyValue[2] = "PORT_NO=5432";
				else
					propertyValue[2] = "PORT_NO=" + dbDetails[6];
				
				propertyValue[3] = "UNICODE=" + unicode;
				propertyValue[4] = "DATABASE=" + dbDetails[4];
				propertyValue[5] = "ENABLE_HA=" + dbDetails[8];
			}
			
			return propertyValue;
		}
		catch (Exception e) {
			FailureHelper.setErrorMessage(e);
			throw e;
		}
	}
	
	public void createInstallerPropertyFile() throws Exception {
		try {
			cleanupReport("Installer Property File");

			if (ValidationHelper.isEmpty(deployPath))
				deployPath = new ROCHelper().getDeployPath(configProp.getDeployPath());

			String srcDir = deployPath + "/config";
			if (srcDir.contains("//"))
				srcDir = srcDir.replace("//", "/");
			String fileName = "installer.properties";

			String[] dbDetails = DBHelper.getReferenceDatabase();
			String[] machine = configProp.getMachineName().split(",", -1);
			String dbType = StringHelper.convertToCamelCase(dbDetails[0]);

			String[] replaceProperties = getReplaceProperties(dbType, dbDetails);
			String[] propertyName = {"WORKSPACE_FILE", "SYS_OPERATOR_NAME", "SYS_LICENSE_KEY", "MACHINE_NAME", "HOST_ADDRESS", "DATA_DIR"};
			String[] propertyValue = {ciwFile, licenseOperator, getLicenseKey(), machine[0], configProp.getRemoteHostname(), configProp.getDataDirPath()};

			if (applicationOS.equalsIgnoreCase("Windows")) {
				int startLineNumber = FileHelper.getLineNumber(applicationOS, srcDir, fileName, dbType + " DB Details");
				int[] lineNumber = getLineNumber(dbType, startLineNumber);
				FileHelper.updateFile(applicationOS, srcDir, fileName, lineNumber, replaceProperties);
				FileHelper.updatePropertyFile(applicationOS, srcDir, fileName, propertyName, propertyValue);
			}
			else {
				String fileNameWithPath = (srcDir.endsWith("/") ? srcDir : srcDir + "/") + fileName;

				// The active DB section is the only uncommented one in installer.properties;
				// every other DB block is '#'-commented, so a per-key sed that matches only
				// uncommented lines updates the right section without line-number bookkeeping.
				for (String property : replaceProperties) {
					int separator = property.indexOf('=');
					if (separator < 0)
						continue;
					setRemoteInstallerProperty(fileNameWithPath, property.substring(0, separator), property.substring(separator + 1));
				}

				for (int i = 0; i < propertyName.length; i++)
					setRemoteInstallerProperty(fileNameWithPath, propertyName[i], propertyValue[i]);
			}
			
			updateReport("Updated");
		} catch (Exception e) {
			FailureHelper.setErrorMessage(e);
			throw e;
		}
	}

	/**
	 * Replaces (or appends, when absent) a single KEY/value entry in the remote
	 * installer.properties using sed over RemoteMachineHelper - the same remote
	 * edit pattern used by {@link #updateTimeZoneId()}. Only an uncommented
	 * occurrence is matched (commented-out DB sections are left intact); the
	 * existing indentation and key/value separator (= or :) are preserved.
	 */
	private void setRemoteInstallerProperty(String fileNameWithPath, String key, String value) throws Exception {
		try {
			if (value == null)
				value = "";

			String grepPattern = "^[[:space:]]*" + key + "[[:space:]]*[=:]";
			String sedValue = value.replace("\\", "\\\\").replace("&", "\\&").replace("|", "\\|");
			String sedExpr = "s|^([[:space:]]*" + key + "[[:space:]]*[=:]).*|\\1" + sedValue + "|";
			String appendLine = key + "=" + value;

			String command = "if grep -qE '" + shellSingleQuote(grepPattern) + "' \"" + fileNameWithPath + "\"; then "
					+ "sed -i -E '" + shellSingleQuote(sedExpr) + "' \"" + fileNameWithPath + "\"; "
					+ "else printf '%s\\n' '" + shellSingleQuote(appendLine) + "' >> \"" + fileNameWithPath + "\"; fi";

			new RemoteMachineHelper().executeScripts(command, true);
		} catch (Exception e) {
			FailureHelper.setErrorMessage(e);
			throw e;
		}
	}

	/** Escapes a value so it is safe inside a single-quoted shell token. */
	private static String shellSingleQuote(String value) {
		return value.replace("'", "'\\''");
	}

	/**
	 * Sets, on the remote host, the &lt;param-value&gt; that immediately follows
	 * the &lt;param-name&gt;{@code paramName}&lt;/param-name&gt; line in a
	 * web.xml-style file (context-param or init-param). The sed advances to the
	 * next line ({@code n}) and rewrites &lt;param-value&gt;...&lt;/param-value&gt;,
	 * so it works whether the value is empty (fresh install) or already set
	 * (re-run safe).
	 */
	private void setRemoteWebXmlParam(String fileNameWithPath, String paramName, String value) throws Exception {
		try {
			if (value == null)
				value = "";

			String dir = fileNameWithPath.substring(0, fileNameWithPath.lastIndexOf('/'));
			String fileName = fileNameWithPath.substring(fileNameWithPath.lastIndexOf('/') + 1);
			String sedValue = value.replace("\\", "\\\\").replace("&", "\\&").replace("#", "\\#");
			String sedScript = "\\#<param-name>" + paramName + "</param-name>#{n;s#<param-value>[^<]*</param-value>#<param-value>"
					+ sedValue + "</param-value>#;}";
			String command = "cd \"" + dir + "\" && sed -i '" + shellSingleQuote(sedScript) + "' " + fileName;

			new RemoteMachineHelper().executeScripts(command, true);
		} catch (Exception e) {
			FailureHelper.setErrorMessage(e);
			throw e;
		}
	}

	/**
	 * Remote shell prefix shared by the pre-installer and the silent installer:
	 * (1) switches the remote 'java' alternative to the major version configured
	 * via psconfig 'javaVersion' - the box has several JDKs and
	 * update-alternatives/alternatives --config is interactive, so the matching
	 * path is resolved and applied with --set non-interactively (sudo password
	 * fed via sudo -S); (2) exports SPARK_TEMP from psconfig 'sparkTempPath',
	 * creating the directory if absent. Fails fast on Windows or empty sparkTempPath.
	 */
	private String buildRemoteEnvPrefix() throws Exception {
		if (applicationOS.equalsIgnoreCase("Windows"))
			FailureHelper.failTest("Remote installer steps are supported only on the remote Linux server");

		String sparkTemp = configProp.getStringProperty("sparkTempPath");
		if (ValidationHelper.isEmpty(sparkTemp))
			FailureHelper.failTest("sparkTempPath is not set in psconfig.properties");

		String javaVersion = configProp.getStringProperty("javaVersion", "11");

		return selectRemoteJava(javaVersion) + " && "
			+ "export SPARK_TEMP=\"" + sparkTemp + "\" && mkdir -p \"" + sparkTemp + "\" && "
			+ "java -version";
	}

	/**
	 * Remote shell snippet that switches the system 'java' alternative to the
	 * given major version ("8", "11", ...). The remote box may use Debian
	 * update-alternatives or Red Hat alternatives; both accept `--display java`
	 * and `--set java <path>` (Red Hat rejects `--list java`). Path naming of
	 * JDKs varies, so the candidate paths are pulled from the `... - priority N`
	 * lines and each is matched on the version it actually reports
	 * (`java -version`: "11.0.20" -> 11, legacy "1.8.0_x" -> 8).
	 */
	private String selectRemoteJava(String javaVersion) throws Exception {
		return
			"ALT=$(command -v update-alternatives 2>/dev/null || command -v alternatives 2>/dev/null); "
			+ "if [ -z \"$ALT\" ]; then echo 'No update-alternatives/alternatives on remote server' >&2; exit 1; fi; "
			+ "JAVA_BIN=''; "
			+ "for J in $(\"$ALT\" --display java 2>/dev/null | awk '/^\\/.*priority/{print $1}'); do "
			+ "V=$(\"$J\" -version 2>&1 | awk -F'\"' '/ version /{print $2; exit}'); "
			+ "M=$(echo \"$V\" | awk -F. '{if ($1==1) print $2; else print $1}'); "
			+ "if [ \"$M\" = \"" + javaVersion + "\" ]; then JAVA_BIN=\"$J\"; break; fi; "
			+ "done; "
			+ "if [ -z \"$JAVA_BIN\" ]; then echo \"No Java " + javaVersion + " among: $(\"$ALT\" --display java 2>&1 | tr '\\n' ' ')\" >&2; exit 1; fi; "
			+ "echo '" + shellSingleQuote(configProp.getRemotePassword()) + "' | sudo -S \"$ALT\" --set java \"$JAVA_BIN\"";
	}

	/**
	 * Remote idiom that enters {@code dir}, fails clearly if {@code scriptName}
	 * is absent, strips CRLF with tr (dos2unix is not guaranteed on RHEL minimal)
	 * and makes the script executable. Caller appends {@code && ./<script> ...}.
	 */
	private String prepareRemoteScript(String dir, String scriptName) {
		return "cd " + dir + " && "
			+ "if [ ! -f " + scriptName + " ]; then echo \"" + scriptName + " not found in $(pwd)\" >&2; exit 1; fi && "
			+ "tr -d '\\r' < " + scriptName + " > " + scriptName + ".unix && mv -f " + scriptName + ".unix " + scriptName + " && "
			+ "chmod 777 " + scriptName;
	}

	/**
	 * Runs presilentinstallation.sh from the deploy 'bin' directory on the
	 * remote server, passing the Reference DB user name and password as
	 * arguments, with the shared Java-version/SPARK_TEMP remote env applied.
	 */
	public void runPreInstaller() throws Exception {
		try {
			cleanupReport("Pre Installer");

			if (ValidationHelper.isEmpty(deployPath))
				deployPath = new ROCHelper().getDeployPath(configProp.getDeployPath());

			String scriptName = "presilentinstallation.sh";
			String command = buildRemoteEnvPrefix() + " && "
				+ prepareRemoteScript(deployPath + "/bin", scriptName) + " && "
				+ "./" + scriptName + " " + configProp.getDbUserName() + " " + configProp.getDbPassword();

			Log4jHelper.logInfo("Running Pre-installer....\n");
			new RemoteMachineHelper().executeScripts(command);

			updateReport("Succeeded");
			Log4jHelper.logInfo("Pre-installer ran successfully\n");
		} catch (Exception e) {
			FailureHelper.setErrorMessage(e);
			throw e;
		}
	}

	/**
	 * Runs the ROCPS silent installer (silentinstaller.sh) from the deploy 'bin'
	 * directory on the remote server, using the same Java-version/SPARK_TEMP
	 * remote env as the pre-installer. The pre-silent step is intentionally
	 * omitted here - it is run separately by {@link #runPreInstaller()}. After
	 * the run the latest silent_installer log is checked for the completion
	 * marker before the step is reported as succeeded.
	 */
	public void runInstallerPS() throws Exception {
		try {
			cleanupReport("Installer Run");

			if (ValidationHelper.isEmpty(deployPath))
				deployPath = new ROCHelper().getDeployPath(configProp.getDeployPath());

			String scriptName = "silentinstaller.sh";
			String command = buildRemoteEnvPrefix() + " && "
				+ prepareRemoteScript(deployPath + "/bin", scriptName) + " && "
				+ "./" + scriptName;

			Log4jHelper.logInfo("Running Silent Installer....\n");
			RemoteMachineHelper remoteMachine = new RemoteMachineHelper();
			remoteMachine.executeScripts(command);

			String logFileMessage = "The installation is now complete";
			String logFile = FileHelper.getLastModifiedFile(applicationOS, deployPath + "/logs", "silent_installer_");
			String result = null;
			if (ValidationHelper.isNotEmpty(logFile)) {
				logFile = logFile.trim();
				String[] line = remoteMachine.executeScripts("grep -F \"" + logFileMessage + "\" \"" + deployPath + "/logs/" + logFile + "\" || true");
				if (ValidationHelper.isNotEmpty(line))
					result = line[0];
			}

			if (ValidationHelper.isEmpty(result) || !result.contains(logFileMessage))
				FailureHelper.failTest("Silent Installer run failed. Please refer " + logFile + " log file for details");

			updateReport("Succeeded");
			Log4jHelper.logInfo("Silent Installer ran successfully\n");
		} catch (Exception e) {
			FailureHelper.setErrorMessage(e);
			throw e;
		}
	}

	public void runInstaller() throws Exception {
		try {
			cleanupReport("Installer Run");
			RunInstaller installer = new RunInstaller();
			installer.runInstaller(configProp.getJava11Path());

			updateReport("Succeeded");
		} catch (Exception e) {
			FailureHelper.setErrorMessage(e);
			throw e;
		}
	}
	
	/**
	 * Locates the client war already downloaded under the binary download path
	 * (the "entrypoint-*.war" produced by the ROCPS client build) and copies it
	 * into tomcat/webapps as &lt;clientContextPath&gt;.war so the subsequent
	 * {@link #extractClientWar()} step can extract it. The exact build/release
	 * suffix is discovered rather than assumed, so it tolerates the
	 * rocpsReleaseVersion vs local_buildno naming difference.
	 */
	public void locateClientWar() throws Exception {
		try {
			cleanupReport("Locate Client War");

			String searchDir = downloadPath.endsWith("/") ? downloadPath.substring(0, downloadPath.length() - 1) : downloadPath;
			Log4jHelper.logInfo("Searching for client war (entrypoint*.war) under : " + searchDir + "\n");

			String sourceWarName = findEntry(searchDir, "entrypoint", ".war", false);
			if (!ValidationHelper.isNotEmpty(sourceWarName)) {
				FailureHelper.failTest("No 'entrypoint*.war' found under '" + searchDir + "'");
			}
			Log4jHelper.logInfo("Found client war : " + sourceWarName + "\n");

			FileHelper.copyFile(searchDir, tomcatPath + "/webapps/", sourceWarName, warFileName, true);
			Log4jHelper.logInfo("Copied '" + sourceWarName + "' to '" + tomcatPath + "/webapps/" + warFileName + "'\n");

			updateReport("Located");
		} catch (Exception e) {
			FailureHelper.setErrorMessage(e);
			throw e;
		}
	}

	/**
	 * Returns the name of the first entry in the given directory whose name
	 * starts with {@code prefix} and ends with {@code suffix}, matching either
	 * a regular file or a sub-directory per {@code wantDirectory}; null if none.
	 * Lists the directory on the remote host when a remoteHostname is
	 * configured, otherwise uses the local filesystem. The exact
	 * release/build suffix is discovered rather than assumed, so it tolerates
	 * the rocpsReleaseVersion vs local_buildno naming difference.
	 */
	private String findEntry(String dir, String prefix, String suffix, boolean wantDirectory) throws Exception {
		try {
			if (ValidationHelper.isNotEmpty(configProp.getRemoteHostname())) {
				RemoteMachineHelper remoteMachine = new RemoteMachineHelper();
				String[] cmdResult = remoteMachine.executeScripts("ls -1p \"" + dir + "\" 2>/dev/null", false);
				StringBuilder listing = new StringBuilder();
				if (ValidationHelper.isNotEmpty(cmdResult)) {
					for (String chunk : cmdResult) {
						if (chunk != null)
							listing.append(chunk);
					}
				}
				for (String token : listing.toString().split("\\s+")) {
					token = token.trim();
					boolean isDir = token.endsWith("/");
					String name = isDir ? token.substring(0, token.length() - 1) : token;
					if (isDir == wantDirectory && name.startsWith(prefix) && name.endsWith(suffix))
						return name;
				}
			}
			else {
				File[] files = new File(dir).listFiles();
				if (files != null) {
					for (File file : files) {
						String name = file.getName();
						if (file.isDirectory() == wantDirectory && name.startsWith(prefix) && name.endsWith(suffix))
							return name;
					}
				}
			}
			return null;
		} catch (Exception e) {
			FailureHelper.setErrorMessage(e);
			throw e;
		}
	}

	public void extractClientWar() throws Exception {
		try {
			cleanupReport("Client War");
			String dir = tomcatPath + "/webapps/";
			
			if (applicationOS.equalsIgnoreCase("Windows")) {
				File directory = new File(dir + clientContextPath);
				Boolean dirCreation = directory.exists() || directory.mkdir() ? true : false;
					
				if(dirCreation) {
					UnzipHelper unZip = new UnzipHelper();
					unZip.unzip(applicationOS, dir + warFileName, dir + clientContextPath);
					FileHelper.deleteFile(applicationOS, dir + warFileName);
				}
			}
			else {
				String[] filePath = {dir};
				String[] fileName = {warFileName};
				RemoteMachineHelper remoteMachine = new RemoteMachineHelper();
				remoteMachine.extractFiles(filePath, fileName);
			}
			
			ReportHelper.updateStepKey(stepName, "Green", "Extracted");
			ReportHelper.reportSuccess(null, false, null);
			
		} catch (Exception e) {
			FailureHelper.setErrorMessage(e);
			throw e;
		}
	}
	
	public void extractRocview() throws Exception {
		try {
			cleanupReport("ROC View");
			FileHelper.copyFile(configProp.getROCViewFolderLocation(), tomcatPath + "/webapps/" + clientContextPath, "view", "view", true);
			updateReport("Extracted");
		} catch (Exception e) {
			FailureHelper.setErrorMessage(e);
			throw e;
		}
	}
	
	public void updateUsageDBPort() throws Exception {
		try {
			String database = configProp.getUsageDatabase();
			if (database.equalsIgnoreCase("Oracle")) {
				String usagePort = configProp.getUsagePortNumber();
				
				if (ValidationHelper.isNotEmpty(usagePort) && !usagePort.equals("1521")) {
					Log4jHelper.logInfo("Updating Usage DB Port in Data Source Type\n");
					cleanupReport("Usage DB Port");
					ExecuteScript.exeQuery("update property_inst set pri_value = 'jdbc:oracle:thin:@[%MACHINE%]:" + usagePort + ":%INSTANCE%' WHERE "
							+ "prd_id = (SELECT prd_id FROM property_dfn WHERE PRD_KEY ='HibernateConnectionUrl') and "
							+ "pig_id = (select pig_id from property_inst_group where pig_name like 'Oracle Properties' and pdg_id = (select pdg_id from property_dfn_group where pdg_key like 'DataSourceTypeProperties'))");
					
					updateReport("Updated");
					Log4jHelper.logInfo("Usage DB Port " + usagePort + " updated in Data Source Type\n");
				}
			}
		} catch (Exception e) {
			FailureHelper.setErrorMessage(e);
			throw e;
		}
	}
	
	public void updateServerName() throws Exception {
		try {
			cleanupReport("Server Name");
			String contextPath = GenericHelper.getPath(applicationOS, (tomcatPath + "/webapps/" + clientContextPath));
			String sparkConfigFile = "SparkPageConfig.prop";
			String serverName = configProp.getMachineName();
			Log4jHelper.logInfo("Updating ServerName in Tomcat " + sparkConfigFile + " file\n");
			
			String[] propertyName = {"Spark.ServerName"};
			if (configProp.getProduct().equalsIgnoreCase("ROCRA") || configProp.getProduct().equalsIgnoreCase("ROC RA") || configProp.getProduct().equalsIgnoreCase("RA"))
				propertyName[0] = "Moneta.ServerName";
			String[] propertyValue = {serverName};
			FileHelper.updatePropertyFile(applicationOS, contextPath, sparkConfigFile, propertyName, propertyValue);
			
			updateReport("Updated");
			Log4jHelper.logInfo("ServerName '" + serverName + "' updated in Tomcat " + sparkConfigFile + " file\n");
		} catch (Exception e) {
			FailureHelper.setErrorMessage(e);
			throw e;
		}
	}
	
	/**
	 * ROCPS variant of {@link #updateServerName()}: updates the server name in
	 * Tomcat's SparkPageConfig.prop under the client context. The property key
	 * is "rocps.ServerName" (vs "Spark.ServerName" for Spark). The Linux/remote
	 * branch uses the sed-over-SSH edit ({@link #setRemoteInstallerProperty})
	 * because FileHelper.updatePropertyFile is broken for the split
	 * automation-box / remote-server setup.
	 */
	public void updateROCPSServerName() throws Exception {
		try {
			cleanupReport("Server Name");
			String contextPath = GenericHelper.getPath(applicationOS, (tomcatPath + "/webapps/" + clientContextPath));
			String sparkConfigFile = "SparkPageConfig.prop";
			String serverName = configProp.getMachineName();
			Log4jHelper.logInfo("Updating ROCPS ServerName in Tomcat " + sparkConfigFile + " file\n");

			String propertyName = "rocps.ServerName";
			if (applicationOS.equalsIgnoreCase("Windows")) {
				FileHelper.updatePropertyFile(applicationOS, contextPath, sparkConfigFile, new String[]{propertyName}, new String[]{serverName});
			}
			else {
				setRemoteInstallerProperty(contextPath + "/" + sparkConfigFile, propertyName, serverName);
			}

			updateReport("Updated");
			Log4jHelper.logInfo("ROCPS ServerName '" + serverName + "' updated in Tomcat " + sparkConfigFile + " file\n");
		} catch (Exception e) {
			FailureHelper.setErrorMessage(e);
			throw e;
		}
	}

	public void updateTimeZoneId() throws Exception {
		try {
			cleanupReport("Time Zone ID");
			String contextPath = GenericHelper.getPath(applicationOS, (tomcatPath + "/webapps/" + clientContextPath + "/WEB-INF"));
			String webFilename = "web.xml";
			String timeZoneId = configProp.getTimeZoneId();
			Log4jHelper.logInfo("Updating Time Zone ID in Tomcat " + webFilename + " file\n");
			
			if (applicationOS.equalsIgnoreCase("Windows")) {
				String fileNameWithPath = contextPath + "/" + webFilename;
				String[][] stringToReplace = {{"UTC", timeZoneId}};
				XMLReader.replaceLine(fileNameWithPath, stringToReplace);				
			}
			else {
				String command = "cd " + contextPath + " && sed -ie 's#UTC#" + timeZoneId + "#g' " + webFilename;
				RemoteMachineHelper remoteMachine = new RemoteMachineHelper();
				remoteMachine.executeScripts(command);
			}
			
			updateReport("Updated");
			Log4jHelper.logInfo("Time Zone ID '" + timeZoneId + "' updated in Tomcat " + webFilename + " file\n");
		} catch (Exception e) {
			FailureHelper.setErrorMessage(e);
			throw e;
		}
	}
	
	/**
	 * ROCPS variant of {@link #updateTimeZoneId()}. Spark's web.xml carries a
	 * literal "UTC" placeholder; ROCPS instead has an empty
	 * &lt;param-value&gt; under the &lt;param-name&gt;TIMEZONEID&lt;/param-name&gt;
	 * context-param, so the "UTC" sed never matches. This sets the
	 * &lt;param-value&gt; on the line following that param-name (works whether it
	 * is empty or already populated, so it is re-run safe).
	 */
	public void updateROCPSTimeZoneId() throws Exception {
		try {
			cleanupReport("Time Zone ID");
			String contextPath = GenericHelper.getPath(applicationOS, (tomcatPath + "/webapps/" + clientContextPath + "/WEB-INF"));
			String webFilename = "web.xml";
			String timeZoneId = configProp.getTimeZoneId();
			Log4jHelper.logInfo("Updating ROCPS Time Zone ID (TIMEZONEID) in Tomcat " + webFilename + " file\n");

			if (applicationOS.equalsIgnoreCase("Windows")) {
				String fileNameWithPath = contextPath + "/" + webFilename;
				String[][] stringToReplace = {{"<param-value></param-value>", "<param-value>" + timeZoneId + "</param-value>"}};
				XMLReader.replaceLine(fileNameWithPath, stringToReplace);
			}
			else {
				setRemoteWebXmlParam(contextPath + "/" + webFilename, "TIMEZONEID", timeZoneId);
			}

			updateReport("Updated");
			Log4jHelper.logInfo("ROCPS Time Zone ID '" + timeZoneId + "' updated in Tomcat " + webFilename + " file\n");
		} catch (Exception e) {
			FailureHelper.setErrorMessage(e);
			throw e;
		}
	}

	/**
	 * Sets the SparkConfig.prop.path init-param in the deployed web.xml to the
	 * Tomcat context directory (tomcat/webapps/&lt;clientContextPath&gt;), which
	 * is where {@link #extractClientWar()} extracted the client. The Linux/remote
	 * branch uses the param-name-anchored sed ({@link #setRemoteWebXmlParam}).
	 */
	public void updateClientConfigPath() throws Exception {
		try {
			cleanupReport("SparkConfig Path");
			String tomcatBase = tomcatPath.endsWith("/") ? tomcatPath.substring(0, tomcatPath.length() - 1) : tomcatPath;
			String contextPath = GenericHelper.getPath(applicationOS, (tomcatBase + "/webapps/" + clientContextPath + "/WEB-INF"));
			String webFilename = "web.xml";
			String configPath = GenericHelper.getPath(applicationOS, (tomcatBase + "/webapps/" + clientContextPath));
			Log4jHelper.logInfo("Updating SparkConfig.prop.path in Tomcat " + webFilename + " file\n");

			if (applicationOS.equalsIgnoreCase("Windows")) {
				String fileNameWithPath = contextPath + "/" + webFilename;
				String[][] stringToReplace = {{"<param-value></param-value>", "<param-value>" + configPath + "</param-value>"}};
				XMLReader.replaceLine(fileNameWithPath, stringToReplace);
			}
			else {
				setRemoteWebXmlParam(contextPath + "/" + webFilename, "SparkConfig.prop.path", configPath);
			}

			updateReport("Updated");
			Log4jHelper.logInfo("SparkConfig.prop.path '" + configPath + "' updated in Tomcat " + webFilename + " file\n");
		} catch (Exception e) {
			FailureHelper.setErrorMessage(e);
			throw e;
		}
	}

	/**
	 * Copies hibernate.cfg.xml from the downloaded server distribution
	 * (binaryDownloadPath/rocps-server-distribution-*&#47;config) into the
	 * deployed client at tomcat/webapps/&lt;clientContextPath&gt;/WEB-INF/classes.
	 * The server-distribution directory's exact build/release suffix is
	 * discovered rather than assumed.
	 */
	public void copyHibernateConfig() throws Exception {
		try {
			cleanupReport("Hibernate Config");
			String searchDir = downloadPath.endsWith("/") ? downloadPath.substring(0, downloadPath.length() - 1) : downloadPath;
			Log4jHelper.logInfo("Searching for server distribution (rocps-server-distribution-*) under : " + searchDir + "\n");

			String serverDistDir = findEntry(searchDir, "rocps-server-distribution-", "", true);
			if (!ValidationHelper.isNotEmpty(serverDistDir)) {
				FailureHelper.failTest("No 'rocps-server-distribution-*' directory found under '" + searchDir + "'");
			}
			Log4jHelper.logInfo("Found server distribution : " + serverDistDir + "\n");

			String sourceDir = searchDir + "/" + serverDistDir + "/config";
			String destinationDir = tomcatPath + "/webapps/" + clientContextPath + "/WEB-INF/classes";
			FileHelper.copyFile(sourceDir, destinationDir, "hibernate.cfg.xml", "hibernate.cfg.xml", true);
			Log4jHelper.logInfo("Copied 'hibernate.cfg.xml' from '" + sourceDir + "' to '" + destinationDir + "'\n");

			updateReport("Copied");
		} catch (Exception e) {
			FailureHelper.setErrorMessage(e);
			throw e;
		}
	}

	public void startTomcat() throws Exception {
		try {
			cleanupReport("Tomcat");
			
			String sourceDir = deployPath + "/config";
			String destinationDir = tomcatPath + "/webapps/" + clientContextPath + "/WEB-INF/classes";
			FileHelper.copyFile(sourceDir, destinationDir, "hibernate.cfg.xml", "hibernate.cfg.xml", true);
			ControllerHelper controller = new ControllerHelper();
			controller.startTomcat();
			
			updateReport("Started");
		} catch (Exception e) {
			FailureHelper.setErrorMessage(e);
			throw e;
		}
	}
	
	/**
	 * ROCPS variant of {@link #startTomcat()}: omits the deploy hibernate.cfg.xml
	 * copy (ROCPS stages hibernate.cfg.xml via {@link #copyHibernateConfig()}
	 * from the server distribution instead) and switches the remote 'java'
	 * alternative to version 8 before starting Tomcat (ROCPS Tomcat runs on
	 * Java 8 while the installer ran on Java 11). Unlike
	 * ControllerHelper.startTomcat(), it does NOT use a Selenium browser or the
	 * client URL to detect readiness (the setup suite has no browser, and
	 * clientUrl points at the https UI port, not the local http Tomcat). It
	 * starts startup.sh over SSH and polls the remote catalina.out for the
	 * "Server startup in" line, so it is independent of protocol/port.
	 */
	public void startROCPSTomcat() throws Exception {
		try {
			cleanupReport("Tomcat");

			if (applicationOS.equalsIgnoreCase("Windows"))
				FailureHelper.failTest("startROCPSTomcat is supported only on the remote Linux server");

			String tomcatBase = tomcatPath.endsWith("/") ? tomcatPath.substring(0, tomcatPath.length() - 1) : tomcatPath;
			String catalinaOut = tomcatBase + "/logs/catalina.out";

			Log4jHelper.logInfo("Switching remote Java to version 8 and starting Tomcat\n");
			// Truncate catalina.out first so the readiness grep only sees this run.
			String command = selectRemoteJava("8")
				+ " && cd \"" + tomcatBase + "/bin\" && chmod 777 *.sh"
				+ " && mkdir -p \"" + tomcatBase + "/logs\" && : > \"" + catalinaOut + "\""
				+ " && ./startup.sh";
			RemoteMachineHelper remoteMachine = new RemoteMachineHelper();
			remoteMachine.executeScripts(command, true);

			int waitTime = configProp.getClientStartWaitTimeMins();
			Log4jHelper.logInfo("Waiting up to " + waitTime + " min for Tomcat startup (catalina.out)\n");

			String readyCheck = "grep -aq 'Server startup in' \"" + catalinaOut + "\" && echo TOMCAT_READY || echo TOMCAT_WAIT";
			boolean up = false;
			for (int i = 0; i < waitTime && !up; i++) {
				GenericHelper.waitInSeconds("60");
				String[] cmdResult = remoteMachine.executeScripts(readyCheck, false);
				StringBuilder out = new StringBuilder();
				if (ValidationHelper.isNotEmpty(cmdResult)) {
					for (String chunk : cmdResult) {
						if (chunk != null)
							out.append(chunk);
					}
				}
				up = out.toString().contains("TOMCAT_READY");
			}

			if (up)
				Log4jHelper.logInfo("Tomcat startup completed (catalina.out: 'Server startup in')\n");
			else
				Log4jHelper.logInfo("Tomcat start issued but 'Server startup in' not seen within " + waitTime + " min\n");

			updateReport("Started");
		} catch (Exception e) {
			FailureHelper.setErrorMessage(e);
			throw e;
		}
	}

	// ---------------------------------------------------------------------
	// ROCPS teardown - all SSH/JDBC based (the setup suite has no browser, so
	// the Selenium-driven ControllerHelper.stop* methods cannot be used).
	// ---------------------------------------------------------------------

	/** Resolves the static deployPath if a standalone teardown step runs before any suite-init set it. */
	private void ensureDeployPath() throws Exception {
		if (ValidationHelper.isEmpty(deployPath))
			deployPath = new ROCHelper().getDeployPath(configProp.getDeployPath());
	}

	/** Force-kills, on the remote host, every process whose command line matches the pgrep -f pattern. */
	private void killRemoteByPattern(String pattern) throws Exception {
		String command = "PIDS=$(pgrep -f '" + shellSingleQuote(pattern) + "'); "
			+ "if [ -n \"$PIDS\" ]; then kill -9 $PIDS; echo \"KILLED $PIDS\"; else echo 'NONE'; fi";
		new RemoteMachineHelper().executeScripts(command, false);
	}

	public void stopTaskControllerPS() throws Exception {
		try {
			cleanupReport("Stop Task Controller");
			ensureDeployPath();
			Log4jHelper.logInfo("Stopping Task Controller process(es)\n");
			killRemoteByPattern(deployPath + ".*TaskController");
			updateReport("Stopped");
		} catch (Exception e) {
			FailureHelper.setErrorMessage(e);
			throw e;
		}
	}

	public void stopStreamControllerPS() throws Exception {
		try {
			cleanupReport("Stop Stream Controller");
			ensureDeployPath();
			Log4jHelper.logInfo("Stopping Stream Controller process(es)\n");
			killRemoteByPattern(deployPath + ".*StreamController");
			updateReport("Stopped");
		} catch (Exception e) {
			FailureHelper.setErrorMessage(e);
			throw e;
		}
	}

	/**
	 * Stops Tomcat via shutdown.sh, then force-kills any process still running
	 * from the Tomcat directory. No browser/driver interaction (unlike
	 * ControllerHelper.stopTomcat()).
	 */
	public void stopROCPSTomcat() throws Exception {
		try {
			cleanupReport("Stop Tomcat");

			if (applicationOS.equalsIgnoreCase("Windows"))
				FailureHelper.failTest("stopROCPSTomcat is supported only on the remote Linux server");

			String tomcatBase = tomcatPath.endsWith("/") ? tomcatPath.substring(0, tomcatPath.length() - 1) : tomcatPath;
			Log4jHelper.logInfo("Stopping Tomcat\n");

			String command = "cd \"" + tomcatBase + "/bin\" && chmod 777 *.sh && ( ./shutdown.sh || true ); "
				+ "sleep 5; PIDS=$(pgrep -f \"" + tomcatBase + "\"); "
				+ "if [ -n \"$PIDS\" ]; then kill -9 $PIDS; echo \"KILLED $PIDS\"; else echo 'NONE'; fi";
			new RemoteMachineHelper().executeScripts(command, false);

			updateReport("Stopped");
		} catch (Exception e) {
			FailureHelper.setErrorMessage(e);
			throw e;
		}
	}

	/** Removes the exploded client webapp and its .war from tomcat/webapps. */
	public void cleanupClientDeploy() throws Exception {
		try {
			cleanupReport("Cleanup Client Deploy");
			String tomcatBase = tomcatPath.endsWith("/") ? tomcatPath.substring(0, tomcatPath.length() - 1) : tomcatPath;
			String webapps = tomcatBase + "/webapps/";

			Log4jHelper.logInfo("Removing deployed client '" + clientContextPath + "' from " + webapps + "\n");
			FileHelper.cleanUpDir(applicationOS, webapps + clientContextPath, false);
			FileHelper.deleteFile(applicationOS, webapps + warFileName);

			updateReport("Cleaned");
		} catch (Exception e) {
			FailureHelper.setErrorMessage(e);
			throw e;
		}
	}

	/** Wipes the binary download path (keeping the empty directory). */
	public void cleanupBinaryDownloadPath() throws Exception {
		try {
			cleanupReport("Cleanup Binary Download Path");
			Log4jHelper.logInfo("Cleaning binary download path : " + downloadPath + "\n");
			FileHelper.cleanUpDir(applicationOS, downloadPath, true);
			updateReport("Cleaned");
		} catch (Exception e) {
			FailureHelper.setErrorMessage(e);
			throw e;
		}
	}

	/** Drops a database/user (Reference/Usage) without recreating it. */
	public void dropDB(String type, String stepname) throws Exception {
		try {
			cleanupReport(stepname);
			Log4jHelper.logInfo("Dropping " + type + " database/user\n");
			new CreateNewDB().dropDataBase(type);
			updateReport("Dropped");
		} catch (Exception e) {
			FailureHelper.setErrorMessage(e);
			throw e;
		}
	}

	public void updateRootPassword() throws Exception {
		try {
			cleanupReport("Update Root Password");
			String newPassword = configProp.getApplicationPassword();
			
			if (newPassword.equals("welcome")) {
				newPassword = "welcome1";
				FileHelper.updatePropertyFile(configFile, "applicationPassword", newPassword);
				if (!configFile.equals("config.properties"))
					FileHelper.updatePropertyFile(automationPath + "\\src\\main\\resources\\config.properties", "applicationPassword", newPassword);
			}
			
			LoginHelper login = new LoginHelper();
			login.login(configProp.getApplicationName(), "Root", "welcome", newPassword);
			
			updateReport("Done");
		} catch (Exception e) {
			FailureHelper.setErrorMessage(e);
			throw e;
		}
	}
	
	private void checkProperty(String[] propertyName, String propertyType) throws Exception {
		try {
			TabHelper.gotoTab(propertyType);
			SettingsHelper settings = new SettingsHelper();
			String wrapperID = settings.getWrapperID(propertyType);
			
			for (int i = 0; i < propertyName.length; i++) {
				boolean isPresent = PropertyGridHelper.isPropertyPresent(wrapperID, propertyName[i].replace("\n", ""));
				if (!isPresent)
					FailureHelper.failTest("Property '" + propertyName[i] + "' is missing in " + propertyType);
			}
		} catch (Exception e) {
			FailureHelper.setErrorMessage(e);
			throw e;
		}
	}
	
	public void checkDefaultSettingsProperties() throws Exception {
		try {
			cleanupReport("Check Settings Properties");
			SettingsHelper settings = new SettingsHelper();
			settings.navigateToSettings();
			
			String tempProperties = FileHelper.readFileContent(automationPath + "\\src\\main\\resources\\Settings_User_Properties.txt");
			String[] userProperties = tempProperties.split(";", -1);
			checkProperty(userProperties, "User Properties");
			
			tempProperties = FileHelper.readFileContent(automationPath + "\\src\\main\\resources\\Settings_Client_Properties.txt");
			String[] clientProperties = tempProperties.split(";", -1);
			checkProperty(clientProperties, "Client Properties");
			
			tempProperties = FileHelper.readFileContent(automationPath + "\\src\\main\\resources\\Settings_Server_Properties.txt");
			String[] serverProperties = tempProperties.split(";", -1);
			checkProperty(serverProperties, "Server Properties");
			
			tempProperties = FileHelper.readFileContent(automationPath + "\\src\\main\\resources\\Settings_System_Properties.txt");
			String[] systemProperties = tempProperties.split(";", -1);
			checkProperty(systemProperties, "System Properties");
			
			if (TabHelper.isPresent("Dice Properties")) {
				tempProperties = FileHelper.readFileContent(automationPath + "\\src\\main\\resources\\Settings_Dice_Properties.txt");
				String[] diceProperties = tempProperties.split(";", -1);
				checkProperty(diceProperties, "Dice Properties");
			}
			
			settings.closeSettings();
			updateReport("Done");
		} catch (Exception e) {
			FailureHelper.setErrorMessage(e);
			throw e;
		}
	}
}