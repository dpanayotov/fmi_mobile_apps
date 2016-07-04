package bg.sofia.uni.fmi.ma.weatherapp;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.HashSet;
import java.util.Set;

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

    public boolean addCity(String city){
        Set<String> cities = getCities();
        for (String s : cities){
            if(s.equalsIgnoreCase(city)){
                return false;
            }
        }
        boolean add = cities.add(city);
        setCities(cities);
        return add;
    }

    public void setCities(Set<String> cities) {
        prefs.edit().putStringSet(CITIES, cities).commit();
    }

    public Set<String> getCities(){
        return prefs.getStringSet(CITIES, new HashSet<String>());
    }


    public void removeCity(String city) {
        Set<String> cities = getCities();
        cities.remove(city);
        setCities(cities);
    }
}
