package java17upgrade;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class Java17Constants {
    static String JAVA_17_SUPPORTED = "is-java-17-supported";
    static String COMPLETE_ASSET_LIST = "complete-asset-list";

    public static String JAVA_17_UPGRADE_ASSESSMENT_REPORT = "Java.17.Upgrade.Assessment.Report.csv";
    static String ASSET_URL = "https://anypoint.mulesoft.com/exchange/api/v2/assets/";
    static String[] MULE_CONNECTOR_GOUPIDS = {
            "org.mule.connectors",
            "com.mulesoft.munit",
            "com.mulesoft.connectors"
    };

    public static String REPORT_MULESOFT_CONNECTOR = "MuleSoft-Connector";
    public static String REPORT_PARTNER_CONNECTOR = "MuleSoft-Partner-Connector";
    public static String REPORT_MULESOFT_PLUGIN = "MuleSoft-Plugin";
    public static String REPORT_CUSTOM_CONNECTOR = "Custom-Connector";
    public static String REPORT_ENTERNAL_DEPENDENCY = "External-Dependency";
    public static String REPORT_ENTERNAL_PLUGIN = "External-Plugin";
    public static String REPORT_JAVA_CLASS = "Java-Class";
    public static String REPORT_NO = "N";
    public static String REPORT_YES = "Y";
    public static String MULE_PLUGIN = "mule-plugin";
    public static String MANUAL_CHECK_REQUIRED = "manual-check-required";

    static Set<String> mulePlugins = new HashSet<>(Arrays.asList(
            "mule-maven-plugin",
            "munit-maven-plugin"
    ));

    // don't need to worry about this (ignoreList from mvn resolved plugins list
    static Set<String> nonMuleWellKnownPlugins = new HashSet<>(Arrays.asList(
            "exchange-mule-maven-plugin",
            "maven-surefire-plugin",
            "maven-clean-plugin",
            "maven-site-plugin",
            "maven-compiler-plugin",
            "maven-resources-plugin",
            "maven-deploy-plugin",
            "versions-maven-plugin",
            "maven-install-plugin"
    ));

    public static String CSV_REPPRT_HEADER_APP_NAME = "Mule-AppName(artifactId)";
    public static String CSV_REPPRT_HEADER_ASSET_TYPE = "AssetType";
    public static String CSV_REPPRT_HEADER_ASSET_NAME = "AssetName";
    public static String CSV_REPPRT_HEADER_CURRENT_VERSION = "CurrentVersion";
    public static String CSV_REPPRT_HEADER_J17_COMPATIBLE = "Currently-Java17-Compatible";
    public static String CSV_REPPRT_HEADER_J17_AVAILABLE = "Java-17-Support-Available";
    public static String CSV_REPPRT_J17_VERSION = "Java-17-Support-Version-Latest";

}
