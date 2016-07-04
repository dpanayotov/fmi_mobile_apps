package bg.sofia.uni.fmi.ma.weatherapp;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

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

        initializeFloatingAddButton();
    }

    private void initializeFloatingAddButton() {
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
                        String city = input.getText().toString().trim();
                        boolean added = new CityPreferences(getApplicationContext()).addCity(city);
                        if(!added){
                            Toast.makeText(that, that.getString(R.string.already_added_city), Toast.LENGTH_LONG).show();
                        }else{
                            changeCity(city);
                        }
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
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Select a city");
        LayoutInflater inflater = getLayoutInflater();
        View citiesListView = inflater.inflate(R.layout.cities_list, null);
        builder.setView(citiesListView);
        final ListView listView = (ListView) citiesListView.findViewById(R.id.listView);
        Set<String> userCities = new CityPreferences(getApplicationContext()).getCities();
        List<String> cities = new ArrayList<>(userCities);
        final ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, cities);
        listView.setAdapter(adapter);

        builder.setView(listView);
        final AlertDialog dialog = builder.show();

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String city = adapter.getItem(position);
                changeCity(city);
                dialog.dismiss();
            }
        });

        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                String city = adapter.getItem(position);
                adapter.remove(city);
                new CityPreferences(getApplicationContext()).removeCity(city);
                adapter.notifyDataSetChanged();
                return false;
            }
        });
    }

    private void changeCity(String city) {
        WeatherActivityFragment fragment = (WeatherActivityFragment) getSupportFragmentManager().findFragmentById(R.id.container);
        fragment.changeCity(city);
        new CityPreferences(getApplicationContext()).setCurrentCity(city);
    }
}
