package com.subex.rocps.automation.testcases.functionaltesting;

import com.subex.automation.helpers.application.ROCHelper;
import com.subex.automation.helpers.component.GenericHelper;
import com.subex.automation.helpers.data.ValidationHelper;
import com.subex.automation.helpers.db.CreateNewDB;
import com.subex.automation.helpers.report.Log4jHelper;
import com.subex.automation.helpers.setup.SetupHelper;
import com.subex.automation.helpers.util.FailureHelper;
import com.subex.rocps.automation.helpers.selenium.PSAcceptanceTest;

public class TCRocpsCreateNewSetup extends PSAcceptanceTest {

    private SetupHelper newSetupHelper() throws Exception {
        String downloadPath = GenericHelper.getPath(applicationOS, configProp.getBinaryDownloadPath());
        String tomcatPath = GenericHelper.getPath(applicationOS, configProp.getTomcatPath());
        ROCHelper rocHelper = new ROCHelper();
        String clientContextPath = rocHelper.getTomcatContextPath();
        return new SetupHelper(downloadPath, tomcatPath, clientContextPath);
    }

    @org.testng.annotations.Test(priority = 1, description = "Create Reference database/user on remote server")
    public void createReferenceDB() throws Exception {
        try {
            if (!ValidationHelper.isTrue(configProp.getFreshInstallation())) {
                Log4jHelper.logInfo("Fresh installation flag is disabled - skipping Reference DB creation\n");
                return;
            }

            Log4jHelper.logInfo("Creating Reference database/user\n");
            CreateNewDB createDB = new CreateNewDB();
            createDB.createDataBase("Reference");
            Log4jHelper.logInfo("Reference database/user created\n");
        } catch (Exception e) {
            FailureHelper.reportFailure(e);
            throw e;
        }
    }

    @org.testng.annotations.Test(priority = 2, description = "Create Usage database/user on remote server")
    public void createUsageDB() throws Exception {
        try {
            if (!ValidationHelper.isTrue(configProp.getFreshInstallation())) {
                Log4jHelper.logInfo("Fresh installation flag is disabled - skipping Usage DB creation\n");
                return;
            }

            Log4jHelper.logInfo("Creating Usage database/user\n");
            CreateNewDB createDB = new CreateNewDB();
            createDB.createDataBase("Usage");
            Log4jHelper.logInfo("Usage database/user created\n");
        } catch (Exception e) {
            FailureHelper.reportFailure(e);
            throw e;
        }
    }

    @org.testng.annotations.Test(priority = 3, description = "Backup existing server/client binaries")
    public void backupBinaries() throws Exception {
        try {
            if (!ValidationHelper.isTrue(configProp.getBackupOldBinaries())) {
                Log4jHelper.logInfo("Backup old binaries flag is disabled - skipping binary backup\n");
                return;
            }

            Log4jHelper.logInfo("Starting older binaries backup\n");
            newSetupHelper().backupBinaries();
            Log4jHelper.logInfo("Older binaries backup completed\n");
        } catch (Exception e) {
            FailureHelper.reportFailure(e);
            throw e;
        }
    }

    @org.testng.annotations.Test(priority = 4, description = "Clean up download, deploy and tomcat directories")
    public void dirCleanup() throws Exception {
        try {
            Log4jHelper.logInfo("Starting directories cleanup\n");
            newSetupHelper().dirCleanup();
            Log4jHelper.logInfo("Directories cleanup completed\n");
        } catch (Exception e) {
            FailureHelper.reportFailure(e);
            throw e;
        }
    }

    @org.testng.annotations.Test(priority = 5, description = "Extract server zip into deploy directory")
    public void extractServerDeploy() throws Exception {
        try {
            Log4jHelper.logInfo("Extracting Server deploy\n");
            newSetupHelper().extractServerDeploy();
            Log4jHelper.logInfo("Extracted Server deploy\n");
        } catch (Exception e) {
            FailureHelper.reportFailure(e);
            throw e;
        }
    }

    @org.testng.annotations.Test(priority = 6, description = "Create/update installer.properties file")
    public void createInstallerPropertyFile() throws Exception {
        try {
            Log4jHelper.logInfo("Updating installer.properties file\n");
            newSetupHelper().createInstallerPropertyFile();
            Log4jHelper.logInfo("Updated installer.properties file\n");
        } catch (Exception e) {
            FailureHelper.reportFailure(e);
            throw e;
        }
    }

    @org.testng.annotations.Test(priority = 7, description = "Run preinstaller.sh (Java 11 + SPARK_TEMP)")
    public void runPreInstaller() throws Exception {
        try {
            Log4jHelper.logInfo("Running Pre-installer\n");
            newSetupHelper().runPreInstaller();
            Log4jHelper.logInfo("Pre-installer ran successfully\n");
        } catch (Exception e) {
            FailureHelper.reportFailure(e);
            throw e;
        }
    }

    @org.testng.annotations.Test(priority = 8, description = "Run silent installer (pre-silent step omitted)")
    public void runInstallerPS() throws Exception {
        try {
            Log4jHelper.logInfo("Running Silent Installer\n");
            newSetupHelper().runInstallerPS();
            Log4jHelper.logInfo("Silent Installer ran successfully\n");
        } catch (Exception e) {
            FailureHelper.reportFailure(e);
            throw e;
        }
    }

    @org.testng.annotations.Test(priority = 9, description = "Locate downloaded client war and copy it into tomcat webapps directory")
    public void locateClientWar() throws Exception {
        try {
            Log4jHelper.logInfo("Locating Client war file\n");
            newSetupHelper().locateClientWar();
            Log4jHelper.logInfo("Located Client war file\n");
        } catch (Exception e) {
            FailureHelper.reportFailure(e);
            throw e;
        }
    }

    @org.testng.annotations.Test(priority = 10, description = "Extract client war file into tomcat webapps directory")
    public void extractClientWar() throws Exception {
        try {
            Log4jHelper.logInfo("Extracting Client war file\n");
            newSetupHelper().extractClientWar();
            Log4jHelper.logInfo("Extracted Client war file\n");
        } catch (Exception e) {
            FailureHelper.reportFailure(e);
            throw e;
        }
    }

    @org.testng.annotations.Test(priority = 11, description = "Update rocps.ServerName in SparkPageConfig.prop")
    public void updateROCPSServerName() throws Exception {
        try {
            Log4jHelper.logInfo("Updating ROCPS ServerName\n");
            newSetupHelper().updateROCPSServerName();
            Log4jHelper.logInfo("Updated ROCPS ServerName\n");
        } catch (Exception e) {
            FailureHelper.reportFailure(e);
            throw e;
        }
    }

    @org.testng.annotations.Test(priority = 12, description = "Update TIMEZONEID param-value in web.xml")
    public void updateTimeZoneId() throws Exception {
        try {
            Log4jHelper.logInfo("Updating Time Zone ID\n");
            newSetupHelper().updateROCPSTimeZoneId();
            Log4jHelper.logInfo("Updated Time Zone ID\n");
        } catch (Exception e) {
            FailureHelper.reportFailure(e);
            throw e;
        }
    }

    @org.testng.annotations.Test(priority = 13, description = "Update SparkConfig.prop.path param-value in web.xml")
    public void updateClientConfigPath() throws Exception {
        try {
            Log4jHelper.logInfo("Updating SparkConfig.prop.path\n");
            newSetupHelper().updateClientConfigPath();
            Log4jHelper.logInfo("Updated SparkConfig.prop.path\n");
        } catch (Exception e) {
            FailureHelper.reportFailure(e);
            throw e;
        }
    }

    @org.testng.annotations.Test(priority = 14, description = "Copy hibernate.cfg.xml into client WEB-INF/classes")
    public void copyHibernateConfig() throws Exception {
        try {
            Log4jHelper.logInfo("Copying hibernate.cfg.xml\n");
            newSetupHelper().copyHibernateConfig();
            Log4jHelper.logInfo("Copied hibernate.cfg.xml\n");
        } catch (Exception e) {
            FailureHelper.reportFailure(e);
            throw e;
        }
    }

    @org.testng.annotations.Test(priority = 15, description = "Start Tomcat on Java 8")
    public void startTomcat() throws Exception {
        try {
            Log4jHelper.logInfo("Starting Tomcat\n");
            newSetupHelper().startROCPSTomcat();
            Log4jHelper.logInfo("Started Tomcat\n");
        } catch (Exception e) {
            FailureHelper.reportFailure(e);
            throw e;
        }
    }
}
