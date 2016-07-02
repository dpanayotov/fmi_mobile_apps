package bg.sofia.uni.fmi.ma.weatherapp;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class WeatherActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weather);

        if(savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction().add(R.id.container, new WeatherActivityFragment()).commit();
        }
        final Activity that = this;
       FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(that);
                builder.setTitle("Add city");
                LayoutInflater inflater = getLayoutInflater();
                View addCityView = inflater.inflate(R.layout.city_add, null);
                builder.setView(addCityView);
                final EditText input = (EditText) addCityView.findViewById(R.id.addCityInput);
                builder.setCancelable(false).setPositiveButton("Add", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String city = input.getText().toString();
                        new CityPreferences(getApplicationContext()).addCity(city);
                    }
                }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                builder.show();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_weather, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == R.id.change_city){
            showCititesList();
        }
        return false;
    }

    private void showCititesList() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Select a city");
        LayoutInflater inflater = getLayoutInflater();
        View citiesListView = inflater.inflate(R.layout.cities_list, null);
        builder.setView(citiesListView);
        ListView listView = (ListView) citiesListView.findViewById(R.id.listView);
        Set<String> userCities = new CityPreferences(getApplicationContext()).getCitites();
        List<String> cities = new ArrayList<>(userCities);
        final ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, cities);
        //TODO: add OnItemLongClickListener to adapter?
        builder.setAdapter(adapter, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String city = adapter.getItem(which);
                changeCity(city);
            }
        });
        builder.show();
    }

    private void changeCity(String city) {
        WeatherActivityFragment fragment = (WeatherActivityFragment) getSupportFragmentManager().findFragmentById(R.id.container);
        fragment.changeCity(city);
        new CityPreferences(getApplicationContext()).setCurrentCity(city);
    }
}
