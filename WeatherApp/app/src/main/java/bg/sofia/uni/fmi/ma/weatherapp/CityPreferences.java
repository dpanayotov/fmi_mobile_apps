package bg.sofia.uni.fmi.ma.weatherapp;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by Dimitar Panayotov on 02-Jul-16.
 */
public class CityPreferences {

    private SharedPreferences prefs;

    public CityPreferences(Activity activity){
        prefs = activity.getPreferences(Context.MODE_PRIVATE);
    }

    public String getCurrentCity(){
        return prefs.getString("city", "Sofia, BG");
    }

    public void setCurrentCity(String city){
        prefs.edit().putString("city", city).commit();
    }

    public void addCity(String city){
        Set<String> cities = getCitites();
        cities.add(city);
        prefs.edit().putStringSet("cities", cities);
    }

    public Set<String> getCitites(){
        return prefs.getStringSet("cities", new HashSet<String>());
    }
}
