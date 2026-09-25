package java17upgrade;

import com.opencsv.CSVWriter;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static java17upgrade.Java17Constants.*;

public class UpgradeAssessmentReports {
    public static void createOutputCSV(String path, List<String[]> assessmentReport) {
        String completeFilePath = path+"/"+JAVA_17_UPGRADE_ASSESSMENT_REPORT;
        File file = new File(completeFilePath);
        try {
            // create FileWriter object with file as parameter
            FileWriter outputfile = new FileWriter(file);
            // create CSVWriter object filewriter object as parameter
            CSVWriter writer = new CSVWriter(outputfile);
            // create a List which contains String array
            writer.writeAll(assessmentReport);
            // closing writer connection
            writer.close();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }
}
