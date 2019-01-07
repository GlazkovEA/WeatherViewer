package homounikumus1.com.myweatherviewer.presenter;

import android.content.Context;
import android.database.Cursor;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import homounikumus1.com.myweatherviewer.MVP;
import homounikumus1.com.myweatherviewer.MyLocationListener;
import homounikumus1.com.myweatherviewer.R;
import homounikumus1.com.myweatherviewer.WeatherArrayAdapter;
import homounikumus1.com.myweatherviewer.model.DataBaseHelper;
import homounikumus1.com.myweatherviewer.model.GetWeatherTask;
import homounikumus1.com.myweatherviewer.model.LoadImage;
import homounikumus1.com.myweatherviewer.model.Weather;
import homounikumus1.com.myweatherviewer.model.WeatherTaskSwitcher;

public class Presenter implements MVP.presenter, LoadImage {
    private LocalTimeZoneChangeListener localTimeZoneChangeListener;
    private static final String TAG = "presenter";
    /**
     * Stores already downloaded weather for reuse
     */
    private ArrayList<Weather> todayWeatherCash = new ArrayList<>();
    private ArrayList<Weather> weekWeatherCash = new ArrayList<>();
    private ArrayList<Weather> citesArrayCash = new ArrayList<>();
    /**
     * Stores to cities received from database
     * for to find them from cityID
     */
    private Map<String, String> cites = new HashMap<>();
    /**
     * Stores to cities coordinates received from database
     * for to find them by cityName
     * <p>
     * That is because of that cities data that we save in database and received
     * from placesAPI not always correlated with data which we have from weatherAPI
     * by this I mean the correlation of the following data:
     * ID of cities, latitude and longitude of a city of a city
     */
    private Map<String, String> citesCoordinates = new HashMap<>();
    private CitesListListener citesListListener;
    private CityListener cityListener;
    private DataBaseHelper dataBaseHelper;
    private Context context;

    public Presenter(Context context) {
        this.context = context;
        this.dataBaseHelper = new DataBaseHelper(context);
    }

    public double[] getCoordinates() {
        double[] cords = new double[2];
        // get current location
        if (MyLocationListener.location != null) {
            cords[0] = MyLocationListener.location.getLatitude();
            cords[1] = MyLocationListener.location.getLongitude();

            // rounds the latitude and longitude value to two decimal places as it uses the weatherAPI
            double latRound = new BigDecimal(cords[0]).setScale(3, RoundingMode.DOWN).doubleValue();
            double lonRound = new BigDecimal(cords[1]).setScale(3, RoundingMode.DOWN).doubleValue();

            if (latRound != 0.0 && lonRound != 0.0)
                return new double[]{latRound, lonRound};
            else
                return null;

        }
        // if for some reason we had'n data return - null
        return null;
    }

    public boolean addCityInDatabase(String city, String coodrinates) {
        dataBaseHelper.open();
        // check is this city already in the database, or not
        if (!dataBaseHelper.checkIsCityAlreadyExist(city)) {
            Log.d(TAG, city + " was added in database");
            dataBaseHelper.addRec(city, coodrinates);
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
    public int amountOfElementsInDatabase() {
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
    public void deleteCityFromDatabase(String city) {
        Log.d(TAG, city + " was deleted from database");
        dataBaseHelper.open();
        dataBaseHelper.delRec(city);
        dataBaseHelper.close();
    }

    /**
     * Get first element from database
     *
     * @return array which consist city name and coordinates of first element in database
     */
    public String[] getFirstElementFromDatabase() {
        dataBaseHelper.open();
        String[] data = dataBaseHelper.getFirstElementFromDatabase();
        dataBaseHelper.close();
        Log.d(TAG, "load from first element = " + data[0] + " " + data[1]);
        return data;
    }

    public ArrayList<Weather> loadCitesWeather(View bar) {
        Log.d(TAG, "load cites saved in database");
        citesArrayCash.clear();
        dataBaseHelper.open();
        Cursor cursor = dataBaseHelper.getAllData();

        // get all cities which we have in database
        String city;
        String cityID;
        String coordinates;
        StringBuilder sb = new StringBuilder();

        if (cursor.moveToFirst()) {
            int cityColumn = cursor.getColumnIndex("CITY");
            int cityIDColumn = cursor.getColumnIndex("CITY_ID");
            int coordinatesColumn = cursor.getColumnIndex("COORDINATES");
            do {
                city = cursor.getString(cityColumn);
                cityID = cursor.getString(cityIDColumn);
                coordinates = cursor.getString(coordinatesColumn);
                if (cityID!=null && cityID!="" && cityID!="null") {
                    sb.append(cityID);
                    sb.append(",");
                    // put coordinates data because weatherAPI and placesAPI coordinates are not correlated
                    citesCoordinates.put(city, coordinates);
                    // put ID data because weatherAPI and placesAPI ID's are not correlated
                    cites.put(cityID, city);
                }
            } while (cursor.moveToNext());
        }

        // send all cities ID to load JSON
        String allCites = sb.toString();
        if (allCites.length() != 0) {
            cityArray(context.getString(R.string.cites_list_weather, allCites.substring(0, allCites.length() - 1)), bar);
        }
        dataBaseHelper.close();
        return citesArrayCash;
    }

    /**
     * Start today weather async task
     *
     * @param data  Url which we send in Async task
     * @param city  City Name - because weatherAPI and placesAPI cities Name are not correlated
     *              (the names of cities in Latin)
     * @param view  The main view which will be filled with data about today's weather
     * @param title Here we put today's date
     */
    public void oneDayWeather(String data, String city, View view, ActionBar title) {
        Log.d(TAG, "start ONE DAY weather task " + data);
        // initialize async task
        GetWeatherTask oneDayWeatherTask = new GetWeatherTask();
        // send to async task weather switcher
        oneDayWeatherTask.setWeatherTaskSwitcher(WeatherTaskSwitcher.TODAY);
        try {
            // execute JSON
            oneDayWeatherTask.execute(new URL(data));
        } catch (MalformedURLException e) {
            Log.d(TAG, "oneDayWeatherException " + e.getMessage());
            e.printStackTrace();
        }

        // get JSON send it to process
        oneDayWeatherTask.setTodayChangeListener((jsonObject) -> {
            todayWeather(jsonObject, city, view, title);
        });
    }

    /**
     * Start week weather async task
     *
     * @param data            Url which we send in Async task
     * @param weatherListView RecyclerView for smooth scroll to zero position
     * @param arrayAdapter    Array adapter for notify data set changed
     * @param weatherList     ArrayList for save received data
     * @param bar   Off bar when data will be loaded
     */
    public void thisWeekWeather(String data, RecyclerView weatherListView, WeatherArrayAdapter arrayAdapter, ArrayList<Weather> weatherList, View bar) {
        Log.d(TAG, "start WEEK weather task");
        // initialize async task
        GetWeatherTask weekWeather = new GetWeatherTask();
        // send to async task weather switcher
        weekWeather.setWeatherTaskSwitcher(WeatherTaskSwitcher.WEEK);
        try {
            // execute JSON
            weekWeather.execute(new URL(data));
            // get JSON
            weekWeather.setWeekChangeListener((jsonObject) -> {
                // when we get time zone send data to process
                this.setLocalTimeZoneChangeListener((timeZone) -> {
                    weekWeather(jsonObject, weatherList, timeZone, bar, arrayAdapter, weatherListView);
                });
            });
        } catch (MalformedURLException e) {
            Log.d(TAG, "thisWeekWeatherException " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Start cities list weather async task
     *
     * @param data Url which we send in Async task
     */
    public void cityArray(String data, View bar) {
        Log.d(TAG, "start CITY ARRAY weather task = " + data);
        // initialize async task
        GetWeatherTask cityArray = new GetWeatherTask();
        // send to async task weather switcher
        cityArray.setWeatherTaskSwitcher(WeatherTaskSwitcher.CITY_ARRAY);
        try {
            // execute JSON
            cityArray.execute(new URL(data));
            // get JSON send it to process
            cityArray.setCityArrayChangeListener((jsonObject) -> {
                citesArrayWeather(jsonObject, citesArrayCash, bar);
            });
        } catch (MalformedURLException e) {
            Log.d(TAG, "cityArrayException " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Create Weather objects from JSONObject containing the forecast
     * @param jsonObject      to extract data
     * @param weekWeatherCash for reuse and for recycler view
     * @param timeZone        to ensure the correct time which correlated with the requested location
     * @param bar             Off bar when data will be loaded
     */
    private void weekWeather(JSONObject jsonObject, ArrayList<Weather> weekWeatherCash, String timeZone, View bar, WeatherArrayAdapter arrayAdapter, RecyclerView recyclerView) {
        // clear old weather data
        weekWeatherCash.clear();
        Log.d(TAG, "get WEEK weather from json");
        this.weekWeatherCash = weekWeatherCash;

        try {
            // off bar
            bar.setVisibility(View.INVISIBLE);
            // get forecast's "list" JSONArray
            JSONArray list = jsonObject.getJSONArray("list");

            // convert each element of list to a Weather object
            for (int i = 0; i < list.length(); ++i) {
                JSONObject day = list.getJSONObject(i);
                JSONObject main = day.getJSONObject("main");
                JSONObject weather = day.getJSONArray("weather").getJSONObject(0);

                weekWeatherCash.add(new Weather(
                                day.getLong("dt"),
                                fromFarengateToCelcy(main.getDouble("temp_max")),
                                main.getDouble("humidity"),
                                weather.getString("description"),
                                weather.getString("icon"),
                                timeZone
                        )
                );
            }

            if (arrayAdapter!=null && recyclerView!=null) {
                arrayAdapter.notifyDataSetChanged();
                recyclerView.smoothScrollToPosition(0);
            }
        } catch (JSONException e) {
            Log.d(TAG, "weekWeatherException " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Create Weather object from JSONObject containing the forecast
     *
     * @param jsonObject  to extract data
     * @param cityName    City Name - because weatherAPI and placesAPI cities Name are not correlated
     *                    (the names of cities in Latin)
     * @param mainWeather The main view which will be filled with data about today's weather
     * @param title       Here we put today's date
     */
    private void todayWeather(JSONObject jsonObject, String cityName, View mainWeather, ActionBar title) {
        Log.d(TAG, "get ONE DAY weather from json");
        try {
            // extract data from JSON
            JSONArray dayArray = jsonObject.getJSONArray("weather");
            JSONObject day = dayArray.getJSONObject(0);
            JSONObject wind = jsonObject.getJSONObject("wind");
            JSONObject sys = jsonObject.getJSONObject("sys");
            JSONObject main = jsonObject.getJSONObject("main");

            JSONObject coord = jsonObject.getJSONObject("coord");
            double lat = coord.getDouble("lat");
            double lon = coord.getDouble("lon");

            // when received location send it to request time zone
            String request = "https://maps.googleapis.com/maps/api/timezone/json?location=" + lat + "," + lon + "&timestamp=" + sys.getLong("sunset") + "&key=AIzaSyA3c4AMeZr7v7QQYoTcFt_x51BInxVAVjs";
            GetWeatherTask timeZone = new GetWeatherTask();
            timeZone.setWeatherTaskSwitcher(WeatherTaskSwitcher.TIME_ZONE);
            timeZone.execute(new URL(request));
            timeZone.setTimeZoneChangeListener((jsonObject1) -> {
                String zone;
                try {
                    zone = jsonObject1.getString("timeZoneId");
                    // when we find out the time zone we send data to convert JSON to a Weather object
                    if (localTimeZoneChangeListener != null)
                        localTimeZoneChangeListener.onChange(zone);
                    Weather weather;
                    // if data received from geodata
                    if (cityName.equals("")) {
                        weather = new Weather(
                                jsonObject.getLong("dt"),
                                jsonObject.getString("name"),
                                day.getString("description"),
                                day.getString("icon"),
                                fromFarengateToCelcy(main.getDouble("temp")),
                                main.getDouble("pressure"),
                                main.getDouble("humidity"),
                                wind.getDouble("speed"),
                                sys.getLong("sunrise"),
                                sys.getLong("sunset"),
                                jsonObject.getLong("id"),
                                lat,
                                lon,
                                zone
                        );
                        // to shared preferences
                        if (cityListener != null) {
                            cityListener.onCityReady(weather.getCity(), weather.getCoords());
                        }
                        // all other cases
                    } else {
                        weather = new Weather(
                                jsonObject.getLong("dt"),
                                cityName,
                                day.getString("description"),
                                day.getString("icon"),
                                fromFarengateToCelcy(main.getDouble("temp")),
                                main.getDouble("pressure"),
                                main.getDouble("humidity"),
                                wind.getDouble("speed"),
                                sys.getLong("sunrise"),
                                sys.getLong("sunset"),
                                jsonObject.getLong("id"),
                                lat,
                                lon,
                                zone
                        );
                        // update cities ID's data because weatherAPI and placesAPI ID's are not correlated
                        dataBaseHelper.open();
                        String id = String.valueOf(jsonObject.getLong("id"));
                        if (!dataBaseHelper.checkIsIDIsAlreadyExist(weather.getCity(), id))
                            dataBaseHelper.updateRec(weather.getCity(), String.valueOf(jsonObject.getLong("id")));
                        dataBaseHelper.close();
                    }
                    // fill the view
                    setInView(weather, mainWeather, title);
                } catch (JSONException e) {
                    Log.d(TAG, e.getMessage() + " todayWeatherException");
                    e.printStackTrace();
                }
            });
        } catch (JSONException | MalformedURLException e) {
            Log.d(TAG, e.getMessage() + " todayWeatherException");
            e.printStackTrace();
        }
    }

    /**
     * Fill the main view with the received, or updated data
     *
     * @param weather     weather object
     * @param mainWeather main view
     * @param title       action bar for date
     */
    public void setInView(Weather weather, View mainWeather, ActionBar title) {
        Log.d(TAG, "set today weather in view");
        todayWeatherCash.clear();
        mainWeather.setVisibility(View.VISIBLE);
        TextView city = mainWeather.findViewById(R.id.city);
        ImageView icon = mainWeather.findViewById(R.id.main_icon);
        TextView mainDesc = mainWeather.findViewById(R.id.main_description);
        TextView mainTmp = mainWeather.findViewById(R.id.main_temp);

        TextView press = mainWeather.findViewById(R.id.main_pressure);
        TextView humidity = mainWeather.findViewById(R.id.main_humidity);
        TextView mainWind = mainWeather.findViewById(R.id.main_wind);
        TextView sunrise = mainWeather.findViewById(R.id.main_sunrise);
        TextView sunset = mainWeather.findViewById(R.id.main_sunset);

        title.setTitle(weather.getDayOfWeek());
        city.setText(weather.getCity());

        new LoadImageTask(icon).execute(weather.getIconURL());
        mainDesc.setText(weather.getDescription());
        mainTmp.setText(weather.getTemp());
        press.setText(weather.getPressure());
        humidity.setText(weather.getHumidity());
        mainWind.setText(weather.getWind_speed());
        sunrise.setText(weather.getSunrise());
        sunset.setText(weather.getSunset());
        todayWeatherCash.add(weather);
    }

    /**
     * Create Weather objects from JSONObject containing the forecast
     *
     * @param jsonObject to extract data
     * @param weathers   for save received data
     */
    private void citesArrayWeather(JSONObject jsonObject, List<Weather> weathers, View bar) {
        Log.d(TAG, "get CITY ARRAY weather from json");
        try {
            bar.setVisibility(View.INVISIBLE);

            if (jsonObject != null) {
                // get forecast's "list" JSONArray
                JSONArray list = jsonObject.getJSONArray("list");
                // convert each element of list to a Weather object
                for (int i = 0; i < list.length(); ++i) {
                    JSONObject day = list.getJSONObject(i);
                    // take ID assigned to this city and previously stored in the database
                    String city = cites.get(day.getString("id"));
                    JSONObject main = day.getJSONObject("main");
                    // take coordinates assigned to this city and previously stored in the database
                    String coord = citesCoordinates.get(city);

                    JSONArray dayArray = day.getJSONArray("weather");
                    JSONObject day2 = dayArray.getJSONObject(0);

                    weathers.add(new Weather(
                            city,
                            day2.getString("description"),
                            fromFarengateToCelcy(main.getDouble("temp")),
                            day2.getString("icon"),
                            coord
                    ));

                }
            }

            if (citesListListener != null)
                citesListListener.onCityListReady();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void setWeekWeatherCash(ArrayList<Weather> weekWeatherCash) {
        this.weekWeatherCash = weekWeatherCash;
    }

    private void setLocalTimeZoneChangeListener(LocalTimeZoneChangeListener localTimeZoneChangeListener) {
        this.localTimeZoneChangeListener = localTimeZoneChangeListener;
    }

    public void setCityListener(CityListener cityListener) {
        this.cityListener = cityListener;
    }

    public void setCitesListListener(CitesListListener citesListListener) {
        this.citesListListener = citesListListener;
    }

    public void setSetUpLocationListener(Context context) {
        MyLocationListener.SetUpLocationListener(context);
    }

    public List<Weather> getCitesArrayCash() {
        return citesArrayCash;
    }

    public List<Weather> getTodayWeatherCash() {
        return todayWeatherCash;
    }

    public List<Weather> getWeekWeatherCash() {
        return weekWeatherCash;
    }

    private double fromFarengateToCelcy(double degrise) {
        return degrise - 273.15;
    }

    interface LocalTimeZoneChangeListener {
        void onChange(String timeZone);
    }

}
