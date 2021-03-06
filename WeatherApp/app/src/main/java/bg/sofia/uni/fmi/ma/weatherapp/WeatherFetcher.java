package bg.sofia.uni.fmi.ma.weatherapp;

import android.content.Context;
import android.os.AsyncTask;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class WeatherFetcher extends AsyncTask<String, Integer, JSONObject> {

    private static final String WEATHER_API = "http://api.openweathermap.org/data/2.5/weather?q=%s&units=metric";

    private Context context;

    public WeatherFetcher(Context context){
        this.context = context;
    }

    @Override
    protected JSONObject doInBackground(String... params) {
        try {
            return FetcherCommon.getResult(context, WEATHER_API, params);
        } catch (IOException | JSONException e) {
            e.printStackTrace();
        }
        return null;
    }
}
