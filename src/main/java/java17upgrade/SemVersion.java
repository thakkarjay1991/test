package java17upgrade;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import java.lang.module.ModuleDescriptor.Version;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class SemVersion {
    //public static String semVersionHighest(String[] jsonObjList) {
    public static String semVersionHighest(List<String> listOfVersions) {
        System.out.println("Entering into semversionSort and find latest");

        // https://stackoverflow.com/questions/198431/how-do-you-compare-two-version-strings-in-java
        //Stream<ModuleDescriptor.Version> sortedStream;
        //String finalSorted[] = Arrays.asList(jsonObjList)
        String finalSorted[] = listOfVersions
                .stream()
                .map(Version::parse)
                .sorted().map(e -> e.toString())
                .collect(Collectors.toList()).toArray(new String[0]);
        //.forEach(System.out::println);
        return finalSorted[finalSorted.length-1];
    }
}
