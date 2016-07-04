package bg.sofia.uni.fmi.ma.weatherapp;

import android.content.Context;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by Dimitar Panayotov on 03-Jul-16.
 */
public class FetcherCommon {

    public static JSONObject getResult(Context context, String apiEndpoint, String... params) throws IOException, JSONException {
        String[] validated = new String[params.length];
        for(int i = 0; i<params.length; i++){
            validated[i] = params[i].replaceAll(" ", "");
        }
        URL url = new URL(String.format(apiEndpoint, validated));
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.addRequestProperty("x-api-key", context.getString(R.string.open_weather_maps_app_id));
        try(BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()))){
            StringBuilder buffer = new StringBuilder(1024);
            String line;
            while((line = reader.readLine()) != null){
                buffer.append(line).append("\n");
            }
            System.out.println(buffer.toString());
            return new JSONObject(buffer.toString());
        }
    }
}
