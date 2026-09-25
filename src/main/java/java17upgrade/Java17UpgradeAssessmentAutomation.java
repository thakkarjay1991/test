package java17upgrade;

import org.joda.time.LocalTime;

import java.io.*;
import java.util.*;

import static java.lang.module.ModuleDescriptor.*;
import static java17upgrade.Java17Constants.*;
//import static java17upgrade.AnypointExchangeAssets.buildTypeOfConnector;

public class Java17UpgradeAssessmentAutomation {
    public static boolean verboseDebug = false;
    public static void main(String[] args) {

        LocalTime startTime = new LocalTime();
        System.out.println("args length " + args.length);
        if (args.length < 8) {
            System.out.println("System Error : Valid arguments NOT passed. Link for How-to- Doc");
            return;
        }
        System.out.println("Java process starts at : " + startTime);

        System.out.println("mule-app                args[0]" + args[0]);
        System.out.println("list-of-java-classes    args[1]" + args[1]);
        System.out.println("list-of-dependencies    args[2]" + args[2]);
        System.out.println("list-of-plugins         args[3]" + args[3]);
        System.out.println("current-path            args[4]" + args[4]);

        System.out.println("loginType               args[5]" + args[5]);
        System.out.println("User/ClientId           args[6]" + args[6]);
        System.out.println("Pass/SecretId           args[7]" + args[7]);
        if (args.length >= 9) {
            System.out.println("debug mode              args[8]" + args[8]);
            verboseDebug = (args[8] != null && args[8].equals("verbose")? true : false);

        }

        String fileListOfJavaClasses = args[1];
        String fileListOfDependencies = args[2];
        String fileListOfPlugins = args[3];
        BufferedReader reader;
        String REPORT_MULE_APP = args[0];

        List<String[]> upgradeAssessmentReport = new ArrayList<String[]>();
        upgradeAssessmentReport.add(new String[] { CSV_REPPRT_HEADER_APP_NAME,
                CSV_REPPRT_HEADER_ASSET_TYPE,
                CSV_REPPRT_HEADER_ASSET_NAME,
                CSV_REPPRT_HEADER_CURRENT_VERSION,
                CSV_REPPRT_HEADER_J17_COMPATIBLE,
                CSV_REPPRT_HEADER_J17_AVAILABLE,
                CSV_REPPRT_J17_VERSION });

        processJavaClasses(args, fileListOfJavaClasses, upgradeAssessmentReport, REPORT_MULE_APP);
        processPlugins(args, fileListOfPlugins, upgradeAssessmentReport, REPORT_MULE_APP);
        processDependencies(args, fileListOfDependencies, upgradeAssessmentReport, REPORT_MULE_APP);


        UpgradeAssessmentReports.createOutputCSV(args[4], upgradeAssessmentReport);
        //System.out.println("fileName: " + args[0]); // user th
        LocalTime endTime = new LocalTime();
        System.out.println("Java process ends at : " + endTime);
        System.out.println("Total time taken in (millisec) > " + (endTime.getMillisOfSecond() - startTime.getMillisOfSecond()));

    }

    private static void processPlugins(String[] args, String fileName, List<String[]> upgradeAssessmentReport,
                                       String REPORT_MULE_APP) {
        BufferedReader reader;
        try {
            reader = new BufferedReader(new FileReader(fileName));
            String line = reader.readLine();

            while (line != null) {
                if (verboseDebug) System.out.println("Plugin Found > " + line);
                String pluginNameWithVersionType = line.split(" Plugin Resolved: ")[1];
                String pluginName = pluginNameWithVersionType.substring(
                        0,
                        pluginNameWithVersionType.lastIndexOf("-"));

                String pluginVersion = pluginNameWithVersionType.substring(
                        pluginNameWithVersionType.lastIndexOf("-")+1,
                        pluginNameWithVersionType.length()-4);

                System.out.println("Plugin name > " + pluginName);
                //Could use if (mulePlugins.contains(pluginName))
                // but better logic is to have compare as subString as things could go wrong
                if (isPluginKnown(mulePlugins, pluginName)) {
                    if (pluginName.equals("mule-maven-plugin")) {
                        // https://docs.mulesoft.com/release-notes/mule-maven-plugin/mule-maven-plugin-release-notes
                        String minVerMMPJ17 = "4.1.0";
                        String latestVerMMPJ17 = "4.2.0";
                        if (Version.parse(pluginVersion).compareTo(Version.parse(minVerMMPJ17)) < 0) {
                            upgradeAssessmentReport.add(new String[] {
                                    REPORT_MULE_APP,
                                    REPORT_MULESOFT_PLUGIN,
                                    pluginName,
                                    pluginVersion,
                                    REPORT_NO,
                                    REPORT_YES ,
                                    latestVerMMPJ17 });
                        } else {
                            upgradeAssessmentReport.add(new String[] {
                                    REPORT_MULE_APP,
                                    REPORT_MULESOFT_PLUGIN,
                                    pluginName,
                                    pluginVersion,
                                    REPORT_YES,
                                    REPORT_YES ,
                                    latestVerMMPJ17 });
                        }
                    }

                    if (pluginName.equals("munit-maven-plugin")) {
                        // https://docs.mulesoft.com/release-notes/munit/munit-release-notes
                        String minVerMunitJ17 = "3.1.0";
                        String latestVerMunitJ17 = "3.2.1";
                        if (Version.parse(pluginVersion).compareTo(Version.parse(minVerMunitJ17)) < 0) {
                            upgradeAssessmentReport.add(new String[] {
                                    REPORT_MULE_APP,
                                    REPORT_MULESOFT_PLUGIN,
                                    pluginName,
                                    pluginVersion,
                                    REPORT_NO,
                                    REPORT_YES ,
                                    latestVerMunitJ17 });
                        } else {
                            upgradeAssessmentReport.add(new String[] {
                                    REPORT_MULE_APP,
                                    REPORT_MULESOFT_PLUGIN,
                                    pluginName,
                                    pluginVersion,
                                    REPORT_YES,
                                    REPORT_YES ,
                                    latestVerMunitJ17 });
                        }
                    }

                } else if (isPluginKnown(nonMuleWellKnownPlugins, pluginName)) {
                    //if (nonMuleWellKnownPlugins.contains(pluginName)) {
                    // ignore for now. TBD-2
                    System.out.println("derived and ignored - continue to double check..");
                } else {
                    upgradeAssessmentReport.add(new String[] {
                            REPORT_MULE_APP,
                            REPORT_ENTERNAL_PLUGIN,
                            pluginName,
                            pluginVersion,
                            MANUAL_CHECK_REQUIRED,
                            MANUAL_CHECK_REQUIRED ,
                            MANUAL_CHECK_REQUIRED });
                }
                line = reader.readLine();
            }

            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void processJavaClasses(String[] args, String fileName, List<String[]> upgradeAssessmentReport,
                                           String REPORT_MULE_APP) {
        BufferedReader reader;
        try {
            reader = new BufferedReader(new FileReader(fileName));
            String line = reader.readLine();

            while (line != null) {
                System.out.println("List of Java Classes to be added > " + line);
                // Normalise Windows back-slashes so the split works regardless of
                // whether the file list was produced by a shell or a .bat script.
                String normalisedLine = line.replace('\\', '/');
                String[] javaClassParts = normalisedLine.split("src/main/java");
                String javaClassName = javaClassParts.length > 1 ? javaClassParts[1] : normalisedLine;
                upgradeAssessmentReport.add(new String[] {REPORT_MULE_APP,
                        REPORT_JAVA_CLASS,
                        javaClassName, // removed stuffs before src/main/java
                        MANUAL_CHECK_REQUIRED,
                        MANUAL_CHECK_REQUIRED,
                        MANUAL_CHECK_REQUIRED ,
                        MANUAL_CHECK_REQUIRED });
                line = reader.readLine();
            }

            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void processDependencies(String[] args, String fileName, List<String[]> upgradeAssessmentReport,
                                            String REPORT_MULE_APP) {
        BufferedReader reader;
        try {
            reader = new BufferedReader(new FileReader(fileName));
            String line = reader.readLine();

            while (line != null) {
                if (verboseDebug) System.out.println("line > " + line);
                String[] tokens = null;
                if (line.contains("[INFO] +- ") ) {
                    tokens = line.split("\\[INFO\\] \\+\\- ", 0);
                } else if (line.contains("[INFO] \\- ")) {
                    tokens = line.split("\\[INFO\\] \\\\- ", 0);
                } else {
                    // Not a dependency row (e.g. mvn header/separator lines such as
                    // "[INFO] ---< ... >---" or "[INFO] --- dependency:tree ---").
                    // Windows findstr pulls these in; skip them so we never index
                    // past the end of the split result.
                    if (verboseDebug) System.out.println("skipping non-dependency line > " + line);
                    line = reader.readLine();
                    continue;
                }

                // Defensive: if the marker was present but nothing followed it, skip.
                if (tokens.length < 2 || tokens[1].trim().isEmpty()) {
                    line = reader.readLine();
                    continue;
                }

                String[] assetDetails = tokens[1].split(":");
                if (assetDetails.length < 4) {
                    // Not a well-formed groupId:artifactId:type[:classifier]:version[:scope] row.
                    if (verboseDebug) System.out.println("skipping malformed dependency line > " + line);
                    line = reader.readLine();
                    continue;
                }
                String groupId      = assetDetails[0];
                String artifactId   = assetDetails[1];
                String currentVersion = "";
                if (line.contains(MULE_PLUGIN))
                    currentVersion      = assetDetails[4];
                else
                    currentVersion      = assetDetails[3];

                String classifier   = assetDetails[3];

                if (line.contains(MULE_PLUGIN)) {
                    System.out.println("this IS mule-plugin");
                    HashMap<String, String> assetMap = AnypointExchangeAssets.buildTypeOfConnector(args[4]);
                    try {
                        String bearerToken = (args[6].equals("UserPass")?
                                                AnypointAuthentication.getBearerTokenUserPass(args[6], args[7]):
                                                AnypointAuthentication.getBearerTokenClientSecret(args[6], args[7]));
                        // take is this out later
                        if (verboseDebug) System.out.println("bearerToken " + bearerToken);

                        // we can still further drill if this is MuleSoft Provided vs Partner vs Custom
                        String typeOfConnector = assetMap.get(groupId+"::"+artifactId);
                        typeOfConnector = ((typeOfConnector!= null
                                            && (typeOfConnector.equals(REPORT_MULESOFT_CONNECTOR)
                                            || typeOfConnector.equals(REPORT_PARTNER_CONNECTOR)))
                                            ? typeOfConnector : REPORT_CUSTOM_CONNECTOR);

                        boolean java17Supported = AnypointExchangeAssets.checkJava17Support(bearerToken, groupId,
                                artifactId, currentVersion, classifier);
                        System.out.println("java17Supported >>>> " + java17Supported);
                        if (java17Supported == false) {
                            String latestVersion = AnypointExchangeAssets.getLatestVersionAndJava17Support(bearerToken,
                                    groupId, artifactId, currentVersion, classifier);
                            boolean java17SupportedForLatest = AnypointExchangeAssets.checkJava17Support(bearerToken,
                                    groupId, artifactId, latestVersion, classifier);

                            System.out.println("latestVersion " + latestVersion);
                            System.out.println("latestVersion supports java? " + java17SupportedForLatest);

                            if (java17SupportedForLatest) {
                                upgradeAssessmentReport.add(new String[] {
                                        REPORT_MULE_APP,
                                        typeOfConnector,
                                        artifactId,
                                        currentVersion,
                                        REPORT_NO,
                                        REPORT_YES,
                                        latestVersion });
                            } else {
                                upgradeAssessmentReport.add(new String[] {
                                        REPORT_MULE_APP,
                                        typeOfConnector,
                                        artifactId,
                                        currentVersion,
                                        REPORT_NO,
                                        REPORT_NO,
                                        latestVersion });
                            }

                        } else {
                            upgradeAssessmentReport.add(new String[] {
                                    REPORT_MULE_APP,
                                    typeOfConnector,
                                    artifactId,
                                    currentVersion,
                                    REPORT_YES,
                                    REPORT_YES,
                                    currentVersion });
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else {
                    System.out.println("this is NOT mule-plugin, marking it as REPORT_ENTERNAL_DEPENDENCY");
                    upgradeAssessmentReport.add(new String[] {
                            REPORT_MULE_APP,
                            REPORT_ENTERNAL_DEPENDENCY,
                            artifactId,
                            currentVersion,
                            MANUAL_CHECK_REQUIRED,
                            MANUAL_CHECK_REQUIRED,
                            MANUAL_CHECK_REQUIRED });
                }
                // read next line
                line = reader.readLine();
            }

            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static boolean isPluginKnown(Set<String> stringSet, String matchString) {
        for (String s : stringSet) {
            if (matchString.indexOf(s) > 0) return true;
        }
        return false;
    }
}
