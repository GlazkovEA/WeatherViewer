package homounikumus1.com.myweatherviewer.utils;

import android.content.SharedPreferences;
import android.database.Cursor;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import java.util.HashMap;
import java.util.Map;

import homounikumus1.com.data2.database.DataBaseHelper;
import homounikumus1.com.data2.model.weather.Weather;
import io.realm.Realm;
import io.realm.RealmResults;

import static android.content.Context.MODE_PRIVATE;
import static homounikumus1.com.myweatherviewer.WeatherApp.getAppContext;

public class DatabaseUtils {
    private static final String TAG = "DataBaseUtils";
    /**
     * Stores to cities received from database
     * for to find them from cityID
     */
    public static final Map<String, String> cites = new HashMap<>();
    /**
     * Stores to cities coordinates received from database
     * for to find them from cityName
     * <p>
     * That is because of that cities data that we save in database and received
     * from placesAPI not always correlated with data which we have from weatherAPI
     * by this I mean the correlation of the following data:
     * ID of cities, latitude and longitude of a city of a city
     */

    public static final Map<String, String> timeZoneMap = new HashMap<>();
    public static final Map<String, String> citesCoordinates = new HashMap<>();

    public static boolean addCityInDatabase(String city, double lat, double lon, String timeZone) {
        DataBaseHelper dataBaseHelper = new DataBaseHelper(getAppContext());
        dataBaseHelper.open();
        // check is this city already in the database, or not
        if (!dataBaseHelper.checkIsCityAlreadyExist(city)) {
            Log.d(TAG, city + " was added in database");
            dataBaseHelper.addRec(city, lat, lon, timeZone);
            return true;
        }
        dataBaseHelper.close();
        return false;
    }

    /**
     * Get amount of elements in database
     *
     * @return int
     */
    public static int amountOfElementsInDatabase() {
        DataBaseHelper dataBaseHelper = new DataBaseHelper(getAppContext());
        dataBaseHelper.open();
        int count = (int) dataBaseHelper.amountOfElementsInDatabase();
        dataBaseHelper.close();
        Log.d(TAG, "amount of elements in database = " + count);
        return count;
    }

    /**
     * Delete element from database
     *
     * @param city deleted city
     */
    public static void deleteCityFromDatabase(String city) {
        DataBaseHelper dataBaseHelper = new DataBaseHelper(getAppContext());
        Log.d(TAG, city + " was deleted from database");
        dataBaseHelper.open();
        dataBaseHelper.delRec(city);
        dataBaseHelper.close();
    }


    public static String loadCitesWeather() {
        citesCoordinates.clear();
        cites.clear();
        timeZoneMap.clear();

        DataBaseHelper dataBaseHelper = new DataBaseHelper(getAppContext());

        Log.d(TAG, "load cites saved in database ");

        dataBaseHelper.open();
        Cursor cursor = dataBaseHelper.getAllData();

        // get all cities which we have in database
        String city;
        String cityID;
        double lat;
        double lon;
        String timeZone;
        StringBuilder sb = new StringBuilder();

        if (cursor.moveToFirst()) {
            int cityColumn = cursor.getColumnIndex("CITY");
            int cityIDColumn = cursor.getColumnIndex("CITY_ID");
            int cityLatColumn = cursor.getColumnIndex("LAT");
            int cityLonColumn = cursor.getColumnIndex("LON");
            int timeZoneColumn = cursor.getColumnIndex("TIME_ZONE");
            do {
                city = cursor.getString(cityColumn);
                cityID = cursor.getString(cityIDColumn);
                lat = cursor.getDouble(cityLatColumn);
                lon = cursor.getDouble(cityLonColumn);
                timeZone = cursor.getString(timeZoneColumn);

                if (cityID != null && !cityID.equals("") && !cityID.equals("null")) {
                    sb.append(cityID);
                    sb.append(",");

                    String coordinates = lat + "&" + lon;
                    // put coordinates data because weatherAPI and placesAPI coordinates are not correlated
                    citesCoordinates.put(city, coordinates);
                    // put ID data because weatherAPI and placesAPI ID's are not correlated
                    cites.put(cityID, city);
                    timeZoneMap.put(cityID, timeZone);
                }
            } while (cursor.moveToNext());
        }

        String allCites = sb.toString();

        dataBaseHelper.close();
        if (allCites.length() != 0)
            return allCites.substring(0, allCites.length() - 1);
        else
            return "";
    }


    public static void checkIsAlreadyExist (Weather weather) {
        String id = weather.getId();
        DataBaseHelper dataBaseHelper = new DataBaseHelper(getAppContext());
        dataBaseHelper.open();
        if (!dataBaseHelper.checkIsIDIsAlreadyExist(weather.getCity(), id))
            dataBaseHelper.updateRec(weather.getCity(), id);
        dataBaseHelper.close();
    }

    public static boolean checkData () {
        Realm realm1 = Realm.getDefaultInstance();
        RealmResults<Weather> results1 = realm1.where(Weather.class).findAll();
        return results1.size() > 0;
    }

    public static Weather getLastSavedObject () {
        Realm realm = Realm.getDefaultInstance();
        RealmResults<Weather> results = realm.where(Weather.class).findAll();
        if (results.size()>0)
            return results.first();
        else
            return null;
    }

    public static void explanationShowed(AppCompatActivity activity, boolean exp) {
        SharedPreferences sPref = activity.getPreferences(MODE_PRIVATE);
        SharedPreferences.Editor ed = sPref.edit();
        ed.putBoolean("explanation", exp);
        ed.apply();

    }

    public static boolean isExplanationShowed(AppCompatActivity activity) {
        SharedPreferences sPref = activity.getPreferences(MODE_PRIVATE);
        return sPref.getBoolean("explanation", false);
    }

    public static boolean isFirstStart() {
        return Realm.getDefaultInstance().where(Weather.class).findAll().size()>0;
    }
}
