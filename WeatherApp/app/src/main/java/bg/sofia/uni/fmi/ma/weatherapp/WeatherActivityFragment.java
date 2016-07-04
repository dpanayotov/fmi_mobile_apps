package bg.sofia.uni.fmi.ma.weatherapp;

import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONObject;

import java.text.DateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.ExecutionException;

public class WeatherActivityFragment extends Fragment {

    private Typeface weatherFont;

    private TextView cityField;
    private TextView updatedField;
    private TextView detailsField;
    private TextView currentTemperatureField;
    private TextView weatherIcon;

   private Handler handler;

    public WeatherActivityFragment() {
        this.handler = new Handler();
    }

    @Override
    public void onCreate(Bundle savedInstance){
        super.onCreate(savedInstance);
        weatherFont = Typeface.createFromAsset(getActivity().getAssets(), "fonts/weather.ttf");
        Context context = getActivity().getApplicationContext();
        refreshWeather(new CityPreferences(context).getCurrentCity());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_weather, container, false);
        cityField = (TextView)rootView.findViewById(R.id.cityTextView);
        updatedField = (TextView)rootView.findViewById(R.id.updated_field);
        detailsField = (TextView)rootView.findViewById(R.id.details_field);
        currentTemperatureField = (TextView)rootView.findViewById(R.id.current_temperature_field);
        weatherIcon = (TextView)rootView.findViewById(R.id.weather_icon);
        weatherIcon.setTypeface(weatherFont);
        weatherIcon.setTextColor(ContextCompat.getColor(getContext(), R.color.colorAccent));
        return rootView;
    }

    private void refreshWeather(String currentCity) {
        AsyncTask<String, Integer, JSONObject> weather = new WeatherFetcher(getContext()).execute(currentCity.trim());
        try {
            final JSONObject weatherJson = weather.get();
            handler.post(new Runnable() {
                @Override
                public void run() {
                    if(weatherJson == null){
                        Toast.makeText(getActivity(), getActivity().getString(R.string.place_not_found), Toast.LENGTH_LONG).show();
                    }else{
                        displayWeather(weatherJson);
                    }
                }
            });
        } catch (InterruptedException | ExecutionException e) {
            Log.e("WeatherApp", "Could not fetch data for city " + currentCity, e);
        }
    }

    private void displayWeather(JSONObject json) {
        try {
            final String city = json.getString("name");
            cityField.setText(String.format("%s, %s", city.toUpperCase(Locale.US), json.getJSONObject("sys").getString("country")));
            cityField.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(getActivity(), DailyActivity.class);
                    intent.putExtra("city", city);
                    getActivity().startActivity(intent);
                }
            });

            JSONObject details = json.getJSONArray("weather").getJSONObject(0);
            JSONObject main = json.getJSONObject("main");
            JSONObject wind = json.getJSONObject("wind");
            detailsField.setText(
                    String.format("%s\nWind: %sm/s\nHumidity: %s%%\nPressure: %s hPa", details.getString("description").toUpperCase(Locale.US), wind.getDouble("speed"), main.getString("humidity"), main.getString("pressure")));

            currentTemperatureField.setText(
                    String.format("%s ℃", String.format("%.2f", main.getDouble("temp"))));

            DateFormat df = DateFormat.getDateTimeInstance();
            String updatedOn = df.format(new Date(json.getLong("dt") * 1000));
            updatedField.setText(String.format("Last update: %s", updatedOn));

            setWeatherIcon(details.getInt("id"),
                    json.getJSONObject("sys").getLong("sunrise") * 1000,
                    json.getJSONObject("sys").getLong("sunset") * 1000);

        }catch(Exception e){
            Log.e("WeatherApp", "A field is missing in the JSON data", e);
        }
    }

    private void setWeatherIcon(int actualId, long sunrise, long sunset) {
        int id = actualId / 100;
        String icon = "";
        if(actualId == 800){
            long currentTime = new Date().getTime();
            if(currentTime>=sunrise && currentTime<sunset) {
                icon = getActivity().getString(R.string.weather_sunny);
            } else {
                icon = getActivity().getString(R.string.weather_clear_night);
            }
        } else {
            switch(id) {
                case 2 : icon = getActivity().getString(R.string.weather_thunder);
                    break;
                case 3 : icon = getActivity().getString(R.string.weather_drizzle);
                    break;
                case 7 : icon = getActivity().getString(R.string.weather_foggy);
                    break;
                case 8 : icon = getActivity().getString(R.string.weather_cloudy);
                    break;
                case 6 : icon = getActivity().getString(R.string.weather_snowy);
                    break;
                case 5 : icon = getActivity().getString(R.string.weather_rainy);
                    break;
            }
        }
        weatherIcon.setText(icon);
    }

    public void changeCity(String city){
        refreshWeather(city);
    }
}
