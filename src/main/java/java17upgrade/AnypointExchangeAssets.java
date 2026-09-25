package java17upgrade;

import com.google.gson.*;
import okhttp3.*;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

import static java17upgrade.Java17Constants.*;
public class AnypointExchangeAssets {
    public static boolean checkJava17Support(String bearerToken, String groupId, String artifactId, String version,
                                             String classifier) {
        System.out.println("Enetring into checkJava17Support..");
        OkHttpClient client = ProxyHttpClient.create();
        MediaType mediaType = MediaType.parse("application/json");
        RequestBody body = RequestBody.create(mediaType, "{id : 100}");

        // building this kind of asset url
        // https://anypoint.mulesoft.com/exchange/api/v2/assets/com.mulesoft.connectors/mule4-azure-eventhubs-connector/1.1.0/asset
        String assetUrl = ASSET_URL
                        + groupId + "/" + artifactId + "/" + version+ "/asset";
        System.out.println("bearerToken >> " + bearerToken);
        System.out.println("assetUrl >> " + assetUrl);
        Request request = new Request.Builder()
                .url(assetUrl)
                .method("GET", null)
                .addHeader("Content-Type", "application/json")
                .addHeader("authorization", "bearer " + bearerToken)
                .build();
        String uglyJsonString = null;
        try {
            Response response = client.newCall(request).execute();
            uglyJsonString = response.body().string();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        System.out.println("uglyJsonString " + uglyJsonString);
        //System.out.println("response : " + response.body().string());

        // https://stackoverflow.com/questions/4105795/pretty-print-json-in-java
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        JsonElement jsonElement = JsonParser.parseString(uglyJsonString);
        JsonObject jsonObject = jsonElement.getAsJsonObject();
        String prettyJsonString = gson.toJson(jsonElement);
        //System.out.println("prettyJsonString " + prettyJsonString);
        JsonArray attributes = jsonObject.get("attributes").getAsJsonArray();
        try {
            Iterator<JsonElement> iterator = attributes.iterator();
            while(iterator.hasNext()) {
                JsonElement jsonElementInner = JsonParser.parseString(String.valueOf(iterator.next()));
                JsonObject jsonObjectInner = jsonElementInner.getAsJsonObject();
                System.out.println(" jsonObjectInner" + jsonObjectInner);
                String jsonObjectInnerKey   = jsonObjectInner.get("key") != null
                                            ? jsonObjectInner.get("key").getAsString() : "";
                String jsonObjectInnerValue = jsonObjectInner.get("value") != null
                                            ? jsonObjectInner.get("value").getAsString() : "";
                if (jsonObjectInnerKey.equalsIgnoreCase(Java17Constants.JAVA_17_SUPPORTED)
                        && jsonObjectInnerValue.equalsIgnoreCase("true")) {
                    return true;
                }
            }
        } catch (Exception e) {
            System.out.println(" e " + e.toString());
            return false;
        }
        return false;
    }

    public static String getLatestVersionAndJava17Support(String bearerToken, String groupId, String artifactId,
                                                          String version, String classifier) {
        System.out.println("Entering into getLatestVersion");
        OkHttpClient client = ProxyHttpClient.create();
        MediaType mediaType = MediaType.parse("application/json");
        RequestBody body = RequestBody.create(mediaType, "{id : 100}");
        // building like this
        //"https://anypoint.mulesoft.com/exchange/api/v2/assets/com.mulesoft.connectors/mule4-azure-eventhubs-connector/asset"
        String assetUrl = ASSET_URL + groupId + "/" + artifactId + "/asset";
        System.out.println("assetUrl " + assetUrl);
        Request request = new Request.Builder()
                .url(assetUrl)
                .method("GET", null)
                .addHeader("Content-Type", "application/json")
                .addHeader("authorization", "bearer " + bearerToken)
                .build();
        String uglyJsonString = null;
        try {
            Response response = client.newCall(request).execute();
            uglyJsonString = response.body().string();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        System.out.println("uglyJsonString " + uglyJsonString);
        //System.out.println("response : " + response.body().string());

        // https://stackoverflow.com/questions/4105795/pretty-print-json-in-java
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        JsonElement jsonElement = JsonParser.parseString(uglyJsonString);
        JsonObject jsonObject = jsonElement.getAsJsonObject();
        //String prettyJsonString = gson.toJson(jsonElement);
        //System.out.println("prettyJsonString " + prettyJsonString);
        //System.out.println("availableVersions " + availableVersions.get(0));
        //Gson googleJson = new Gson();
        /*
        JsonArray availableVersions = jsonObject.get("versions").getAsJsonArray();
        String[] jsonObjList = new Gson().fromJson(availableVersions, String[].class);
        Arrays.asList(jsonObjList).
                forEach(System.out::println);
        List<String> listOfVersions = new ArrayList<String>();
        listOfVersions = Arrays.asList(jsonObjList);
        */

        String latestVersion = jsonObject.get("version").toString().replaceAll("\"", "");
        System.out.println("latestVersion " + latestVersion);
       // listOfVersions.add(latestVersion);
        //return SemVersion.semVersionHighest(listOfVersions);
        return latestVersion;
    }
    public static HashMap<String, String> buildTypeOfConnector(String currentPath) {
        // make this to dynamic call
        /*

        curl 'https://anypoint.mulesoft.com/exchange/api/v2/ang/stream/_search?_boost=
                assets.AssetNode.name^10&_boost=assets.AssetNode.description^2
                &_boost=customer-metadata.CustomerMetadataNode.searchableValues^4
                &_boost=customer-metadata.CustomerMetadataNode.tags^4
                &_size=2000&_sort=asc:assets.AssetNode.ranking&_sort=desc:asset-minor-versions.MinorVersionNode.updatedAt
                &_source=asset-major-versions.AssetMajorNode.*
                &_source=asset-minor-versions.%40id&_source=asset-minor-versions.MinorVersionNode.*
                &_source=asset-minor-versions.VersionNode.*
                &_source=assets.%40id&_source=assets.%40type&_source=assets.AssetNode.*&_source=assets.FileNode.*
                &_source=customer-metadata.CategoryNode.*&_source=customer-metadata.CustomerMetadataNode.*
                &_source=system-metadata.SystemMetadataNode.*
                &asset-minor-versions.VersionNode.generatedFrom=http%3A%2F%2Fnone
                &assets.@type=http%3A%2F%2Fanypoint.com%2Fvocabs%2Fdigital-repository%23MuleConnectorAsset
                &k:asset-minor-versions.VersionNode.status=deprecated
                &k:asset-minor-versions.VersionNode.status=development
                &k:asset-minor-versions.VersionNode.status=published
                &k:assets.AssetNode.organizationId=68ef9520-24e9-4cf2-b2f5-620025690913' \
          -H 'Connection: keep-alive' \
          -H 'accept: application/stream+json' \
          -H 'authorization: bearer e94b40f3-ce71-4959-9a14-6bc9d281f3af'

         */
        String fileName = currentPath + "/" + COMPLETE_ASSET_LIST;
        BufferedReader reader;
        HashMap<String, String> assetMap = new HashMap<>();
        try {
            reader = new BufferedReader(new FileReader(fileName));
            String line = reader.readLine();

            while (line != null) {
                if (line.contains("MinorVersionNode.groupId")) {
                    //System.out.println(line);
                    Gson gson = new GsonBuilder().setPrettyPrinting().create();
                    JsonElement jsonElement = JsonParser.parseString(line);

                    JsonObject jsonObject = jsonElement.getAsJsonObject();
                    JsonArray groupIdArray = jsonObject
                            .getAsJsonObject("value")
                            .getAsJsonObject("_source")
                            .getAsJsonObject("assets")
                            .getAsJsonArray("AssetNode.groupId");
                    String groupId = String.valueOf(groupIdArray.asList().get(0)).replaceAll("\"", "");

                    JsonArray assetIdArray = jsonObject
                            .getAsJsonObject("value")
                            .getAsJsonObject("_source")
                            .getAsJsonObject("assets")
                            .getAsJsonArray("AssetNode.assetId");
                    String assetId = String.valueOf(assetIdArray.asList().get(0)).replaceAll("\"", "");

                    JsonArray userFirstNameArray = jsonObject
                            .getAsJsonObject("value")
                            .getAsJsonObject("_source")
                            .getAsJsonObject("users")
                            .getAsJsonArray("UserNode.firstName");
                    String userFirstName = String.valueOf(userFirstNameArray.asList().get(0))
                                                 .replaceAll("\"", "");

                    JsonArray userLastNameArray = jsonObject
                            .getAsJsonObject("value")
                            .getAsJsonObject("_source")
                            .getAsJsonObject("users")
                            .getAsJsonArray("UserNode.lastName");
                    String userLastName = String.valueOf(userLastNameArray.asList().get(0))
                                            .replaceAll("\"", "");

                    // ReadNote :
                    // open community, unknowns, partners all are clubbed into Partner ie. either MuleSoft or not.
                    String type = (( userLastName.equalsIgnoreCase("Organization")
                                    && userFirstName.equalsIgnoreCase("MuleSoft"))
                                    ? REPORT_MULESOFT_CONNECTOR : REPORT_PARTNER_CONNECTOR);
                    assetMap.put(groupId+"::"+assetId, type);
                }
                line = reader.readLine();
            }
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("muleGroupList " + assetMap);
        return assetMap;
    }
}
