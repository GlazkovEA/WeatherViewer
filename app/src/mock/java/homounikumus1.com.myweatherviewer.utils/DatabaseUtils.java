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
    public static final Map<String, String> cites = new HashMap<>();
    public static final Map<String, String> timeZoneMap = new HashMap<>();
    public static final Map<String, String> citesCoordinates = new HashMap<>();
    private static Integer amountOfElementsInDatabase = null;
    private static String citiesList = null;

    public static void setCitiesList(String citiesList) {
        DatabaseUtils.citiesList = citiesList;
    }

    public static void setAmountOfElementsInDatabase(Integer amountOfElementsInDatabase) {
        DatabaseUtils.amountOfElementsInDatabase = amountOfElementsInDatabase;
    }

    public static void clean () {
        DataBaseHelper dataBaseHelper = new DataBaseHelper(getAppContext());
        dataBaseHelper.open();
        dataBaseHelper.clean();
        dataBaseHelper.close();
    }

    public static boolean addCityInDatabase(String city, double lat, double lon, String timeZone) {
        if (amountOfElementsInDatabase!=null) {
            DataBaseHelper dataBaseHelper = new DataBaseHelper(getAppContext());
            dataBaseHelper.open();
            // check is this city already in the database, or not
            if (!dataBaseHelper.checkIsCityAlreadyExist(city)) {
                // Log.d(TAG, city + " was added in database");
                dataBaseHelper.addRec(city, lat, lon, timeZone);
                return true;
            }
            dataBaseHelper.close();
            return false;
        } else
            return false;
    }

    public static int amountOfElementsInDatabase() {
        if (amountOfElementsInDatabase!=null)
            return amountOfElementsInDatabase;
        else {
            DataBaseHelper dataBaseHelper = new DataBaseHelper(getAppContext());
            dataBaseHelper.open();
            int count = (int) dataBaseHelper.amountOfElementsInDatabase();
            dataBaseHelper.close();
            // Log.d(TAG, "amount of elements in database = " + count);
            return count;
        }
    }

    public static void deleteCityFromDatabase(String city) {
        if (amountOfElementsInDatabase==null) {
            DataBaseHelper dataBaseHelper = new DataBaseHelper(getAppContext());
            //Log.d(TAG, city + " was deleted from database");
            dataBaseHelper.open();
            dataBaseHelper.delRec(city);
            dataBaseHelper.close();
        }
    }


    public static String loadCitesWeather() {
        if (citiesList!=null) {
            return citiesList;
        } else {
            citesCoordinates.clear();
            cites.clear();
            timeZoneMap.clear();

            DataBaseHelper dataBaseHelper = new DataBaseHelper(getAppContext());

            //Log.d(TAG, "load cites saved in database ");

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
    }


    public static void checkIsAlreadyExist (Weather weather) {
        if (amountOfElementsInDatabase==null) {
            String id = weather.getId();
            DataBaseHelper dataBaseHelper = new DataBaseHelper(getAppContext());
            dataBaseHelper.open();
            if (!dataBaseHelper.checkIsIDIsAlreadyExist(weather.getCity(), id))
                dataBaseHelper.updateRec(weather.getCity(), id);
            dataBaseHelper.close();
        }
    }

    public static boolean checkData () {
        if (amountOfElementsInDatabase!=null) {
            return amountOfElementsInDatabase > 0;
        } else {
            Realm realm1 = Realm.getDefaultInstance();
            RealmResults<Weather> results1 = realm1.where(Weather.class).findAll();
            return results1.size() > 0;
        }
    }

    public static Weather getLastSavedObject () {
        if (amountOfElementsInDatabase!=null) {
            if (amountOfElementsInDatabase > 0)
                return new Weather();
            else
                return null;
        } else {
            Realm realm = Realm.getDefaultInstance();
            RealmResults<Weather> results = realm.where(Weather.class).findAll();
            if (results.size()>0)
                return results.first();
            else
                return null;
        }
    }

    public static boolean isShowNotification () {


        return false;
    }

    public static void setIsNotificationShow (boolean isShow) {

    }

    public static void explanationShowed(boolean exp) {


    }

    public static boolean isExplanationShowed() {

        return false;
    }

    public static boolean isFirstStart() {
        if (amountOfElementsInDatabase!=null) {
            return amountOfElementsInDatabase > 0;
        } else {
            return Realm.getDefaultInstance().where(Weather.class).findAll().size()>0;
        }
    }
}
