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

    private static final String WEATHER_API = "http://api.openweathermap.org/data/2.5/forecast/city?id=%s&units=%s";

    private Context context;
    private final ProgressDialog dialog;

    public WeatherFetcher(Context context){
        this.context = context;
        this.dialog = new ProgressDialog(context);
    }

    @Override
    protected void onPreExecute(){
        this.dialog.setMessage("Processing");
        this.dialog.show();
    }

    @Override
    protected void onPostExecute(JSONObject result){
        System.out.println(result);
        dialog.dismiss();
    }

    @Override
    protected JSONObject doInBackground(String... params) {
        try {
            URL url = new URL(String.format(WEATHER_API, params[0], params[1]));
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            int code = connection.getResponseCode();
            if(code != HttpURLConnection.HTTP_OK){
                return null;
            }
            connection.addRequestProperty("x-api-key", this.context.getString(R.string.open_weather_maps_app_id));
            try(BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()))){
                StringBuffer buffer = new StringBuffer(1024);
                String line = "";
                while((line = reader.readLine()) != null){
                    buffer.append(line).append("\n");
                }
                return new JSONObject(buffer.toString());
            }
        } catch (IOException | JSONException e) {
            e.printStackTrace();
        }
        return null;
    }
}
