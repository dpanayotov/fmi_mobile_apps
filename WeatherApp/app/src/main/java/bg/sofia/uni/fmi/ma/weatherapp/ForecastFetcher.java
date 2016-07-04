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

/**
 * Created by Dimitar Panayotov on 03-Jul-16.
 */
public class ForecastFetcher extends AsyncTask<String, Integer, JSONObject> {

    private static final String FORECAST_API = "http://api.openweathermap.org/data/2.5/forecast?q=%s&units=metric";

    private Context context;

    public ForecastFetcher(Context context){
        this.context = context;
    }

    @Override
    protected JSONObject doInBackground(String... params) {
        try {
            return FetcherCommon.getResult(context, FORECAST_API, params);
        } catch (IOException | JSONException e) {
            e.printStackTrace();
        }
        return null;
    }
}
