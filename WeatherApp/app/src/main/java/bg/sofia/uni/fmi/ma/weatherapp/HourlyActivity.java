package bg.sofia.uni.fmi.ma.weatherapp;

import android.graphics.Typeface;
import android.os.AsyncTask;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.DateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.ExecutionException;

public class HourlyActivity extends AppCompatActivity {

    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link android.support.v4.app.FragmentStatePagerAdapter}.
     */
    private SectionsPagerAdapter mSectionsPagerAdapter;

    /**
     * The {@link ViewPager} that will host the section contents.
     */
    private ViewPager mViewPager;
    private static JSONObject json;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hourly);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        String city = getIntent().getStringExtra("city");
        getWeather(city);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_daily, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void getWeather(String currentCity) {
        AsyncTask<String, Integer, JSONObject> task = new ForecastFetcher(getApplicationContext()).execute(currentCity);
        try {
            json = task.get();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        private static final String ARG_SECTION_NUMBER = "section_number";

        private Typeface weatherFont;

        private TextView cityField;
        private TextView updatedField;
        private TextView detailsField;
        private TextView currentTemperatureField;
        private TextView weatherIcon;

        public PlaceholderFragment() {
        }

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        public static PlaceholderFragment newInstance(int sectionNumber) {
            PlaceholderFragment fragment = new PlaceholderFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);
            return fragment;
        }

        @Override
        public void onCreate(Bundle savedInstance){
            super.onCreate(savedInstance);
            weatherFont = Typeface.createFromAsset(getActivity().getAssets(), "fonts/weather.ttf");
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_hourly, container, false);
            int item = getArguments().getInt(ARG_SECTION_NUMBER);
            cityField = (TextView)rootView.findViewById(R.id.cityTextView);
            updatedField = (TextView)rootView.findViewById(R.id.updated_field);
            detailsField = (TextView)rootView.findViewById(R.id.details_field);
            currentTemperatureField = (TextView)rootView.findViewById(R.id.current_temperature_field);
            weatherIcon = (TextView)rootView.findViewById(R.id.weather_icon);
            weatherIcon.setTypeface(weatherFont);
            weatherIcon.setTextColor(ContextCompat.getColor(getContext(), R.color.colorAccent));
            displayWeather(json, item);
            return rootView;
        }

        private void displayWeather(JSONObject json, int item) {
            try {
                String cityName = json.getJSONObject("city").getString("name");
                String country = json.getJSONObject("city").getString("country");
                cityField.setText(String.format("%s, %s", cityName.toUpperCase(Locale.US), country));

                JSONArray days = json.getJSONArray("list");
                System.out.println(days.length());
                JSONObject obj = days.getJSONObject(item);

                JSONObject details = obj.getJSONArray("weather").getJSONObject(0);
                JSONObject main = obj.getJSONObject("main");
                double minTemp = main.getDouble("temp_min");
                double temp_max = main.getDouble("temp_max");
                JSONObject wind = obj.getJSONObject("wind");
                detailsField.setText(
                        String.format("%s\nWind: %sm/s\nHumidity: %s%%\nPressure: %s hPa", details.getString("description").toUpperCase(Locale.US), wind.getDouble("speed"), main.getString("humidity"), main.getString("pressure")));

                currentTemperatureField.setText(
                        String.format("%s ℃", String.format("%.2f", main.getDouble("temp"))));

                DateFormat df = DateFormat.getDateTimeInstance();
                String updatedOn = df.format(new Date(obj.getLong("dt") * 1000));
                updatedField.setText(String.format("Weather for: %s", updatedOn));

                setWeatherIcon(details.getInt("id"));

            }catch(Exception e){
                Log.e("WeatherApp", "A field is missing in the JSON data", e);
            }
        }

        private void setWeatherIcon(int actualId) {
            int id = actualId / 100;
            String icon = "";
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
            weatherIcon.setText(icon);
        }
    }

    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            // getItem is called to instantiate the fragment for the given page.
            // Return a PlaceholderFragment (defined as a static inner class below).
            return PlaceholderFragment.newInstance(position + 1);
        }

        @Override
        public int getCount() {
            // Show 5 total pages.
            return 5;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return "SECTION 1";
                case 1:
                    return "SECTION 2";
                case 2:
                    return "SECTION 3";
                case 3:
                    return "SECTION 4";
                case 4:
                    return "SECTION 5";
            }
            return null;
        }
    }
}
