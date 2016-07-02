package bg.sofia.uni.fmi.ma.weatherapp;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

/**
 * Created by Dimitar Panayotov on 02-Jul-16.
 */
public class WeatherFetcher extends AsyncTask<String, Integer, JSONObject> {

    private static final String WEATHER_API = "http://api.openweathermap.org/data/2.5/weather?q=%s&units=metric";

    private Context context;

    public WeatherFetcher(Context context){
        this.context = context;
    }

    @Override
    protected JSONObject doInBackground(String... params) {
        try {
            URL url = new URL(String.format(WEATHER_API, params));
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.addRequestProperty("x-api-key", this.context.getString(R.string.open_weather_maps_app_id));
            try(BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()))){
                StringBuilder buffer = new StringBuilder(1024);
                String line = "";
                while((line = reader.readLine()) != null){
                    buffer.append(line).append("\n");
                }
                System.out.println(buffer.toString());
                return new JSONObject(buffer.toString());
            }
        } catch (IOException | JSONException e) {
            e.printStackTrace();
        }
        return null;
    }
}
