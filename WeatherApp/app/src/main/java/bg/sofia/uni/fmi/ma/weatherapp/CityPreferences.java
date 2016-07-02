package bg.sofia.uni.fmi.ma.weatherapp;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by Dimitar Panayotov on 02-Jul-16.
 */
public class CityPreferences {

    private static final String CITY = "city";
    private static final String CITIES = "cities";
    public static final String FILE_NAME = "cityPrefs";

    private SharedPreferences prefs;

    public CityPreferences(Context ctx){
        this.prefs = ctx.getSharedPreferences(FILE_NAME, Context.MODE_PRIVATE);
    }

    public String getCurrentCity(){
        return prefs.getString(CITY, "Sofia");
    }

    public void setCurrentCity(String city){
        prefs.edit().putString(CITY, city).commit();
    }

    public void addCity(String city){
        Set<String> cities = getCitites();
        cities.add(city);
        prefs.edit().putStringSet(CITIES, cities).commit();
    }

    public Set<String> getCitites(){
        return prefs.getStringSet(CITIES, new HashSet<String>());
    }


}
