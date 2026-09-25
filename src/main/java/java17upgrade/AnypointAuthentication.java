package java17upgrade;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import okhttp3.*;

import java.io.IOException;

public class AnypointAuthentication {
    public static String getBearerTokenUserPass(String userName, String password) {
        System.out.println("Entering into getBearerTokenUserPass");
        String bearerToken = null;
        OkHttpClient client = ProxyHttpClient.create();
        MediaType mediaType = MediaType.parse("application/x-www-form-urlencoded");
        RequestBody body = RequestBody.create(mediaType, "username="+ userName +"&password="+password);
        Request request = new Request.Builder()
                .url("https://anypoint.mulesoft.com/accounts/login")
                .method("POST", body)
                .addHeader("Content-Type", "application/x-www-form-urlencoded")
                .build();
        Response response;
        try {
            response = client.newCall(request).execute();
            bearerToken = response.body().string();
            JsonElement jsonElement = JsonParser.parseString(bearerToken);
            JsonObject jsonObject = jsonElement.getAsJsonObject();
            bearerToken = jsonObject.get("access_token").toString().replaceAll("\"", "");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return bearerToken;
    }

    public static String getBearerTokenClientSecret(String clientId, String secret) {
        System.out.println("Entering into getBearerTokenClientSecret");
        String bearerToken = null;
        OkHttpClient client = ProxyHttpClient.create();
        MediaType mediaType = MediaType.parse("application/x-www-form-urlencoded");
        //RequestBody body = RequestBody.create(mediaType, "username=user&password=pass");

        RequestBody body = RequestBody.create(mediaType, "client_id="+clientId+
                                                        "&client_secret="+secret+
                                                        "&grant_type=client_credentials");
        Request request = new Request.Builder()
                .url("https://anypoint.mulesoft.com/accounts/api/v2/oauth2/token")
                .method("POST", body)
                .addHeader("Content-Type", "application/x-www-form-urlencoded")
                .build();

        Response response;
        try {
            response = client.newCall(request).execute();
            bearerToken = response.body().string();
            // System.out.println("bearerToken " + bearerToken);
            JsonElement jsonElement = JsonParser.parseString(bearerToken);
            JsonObject jsonObject = jsonElement.getAsJsonObject();
            bearerToken = jsonObject.get("access_token").toString().replaceAll("\"", "");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return bearerToken;
    }
}
