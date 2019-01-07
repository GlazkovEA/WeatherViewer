package homounikumus1.com.myweatherviewer.model;

import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class GetWeatherTask extends AsyncTask<URL, Void, JSONObject> {
    private static final String TAG = "getWeatherTask";
    private DataTransfer.TodayChangeListener todayChangeListener;
    private DataTransfer.WeekChangeListener weekChangeListener;
    private DataTransfer.CityArrayChangeListener cityArrayChangeListener;
    private DataTransfer.TimeZoneChangeListener timeZoneChangeListener;
    private WeatherTaskSwitcher weatherTaskSwitcher;

    public void setTimeZoneChangeListener(DataTransfer.TimeZoneChangeListener timeZoneChangeListener) {
        this.timeZoneChangeListener = timeZoneChangeListener;
    }

    public void setCityArrayChangeListener(DataTransfer.CityArrayChangeListener cityArrayChangeListener) {
        this.cityArrayChangeListener = cityArrayChangeListener;
    }

    public void setWeekChangeListener(DataTransfer.WeekChangeListener weekChangeListener) {
        this.weekChangeListener = weekChangeListener;
    }

    public void setWeatherTaskSwitcher(WeatherTaskSwitcher weatherTaskSwitcher) {
        this.weatherTaskSwitcher = weatherTaskSwitcher;
    }

    public void setTodayChangeListener(DataTransfer.TodayChangeListener todayChangeListener) {
        this.todayChangeListener = todayChangeListener;
    }

    /**
     * Execute the jsonObject in accordance with the passed to the method
     * setWeatherTaskSwitcher switch
     *
     * @param jsonObject executed object
     */
    @Override
    protected void onPostExecute(JSONObject jsonObject) {
        if (jsonObject != null) {
            switch (weatherTaskSwitcher) {
                case WEEK:
                    if (weekChangeListener != null)
                        weekChangeListener.onChange(jsonObject);
                    break;
                case TODAY:
                    if (todayChangeListener != null)
                        todayChangeListener.onChange(jsonObject);
                    break;
                case CITY_ARRAY:
                    if (cityArrayChangeListener != null)
                        cityArrayChangeListener.onChange(jsonObject);
                    break;
                case TIME_ZONE:
                    if (timeZoneChangeListener != null)
                        timeZoneChangeListener.onChange(jsonObject);
            }
        }
    }

    /**
     * Makes the REST web service call to get weather data and
     * saves the data to a local HTML file
     * @param urls URL
     * @return JSON
     */
    @Override
    protected JSONObject doInBackground(URL... urls) {
        HttpURLConnection connection = null;

        try {
            connection = (HttpURLConnection) urls[0].openConnection();
            int response = connection.getResponseCode();

            if (response == HttpURLConnection.HTTP_OK) {
                StringBuilder builder = new StringBuilder();

                try (BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()))) {
                    String line;
                    while ((line = reader.readLine()) != null) {
                        builder.append(line);
                    }

                } catch (IOException e) {
                    Log.d(TAG, "Unable to read weather data");
                    e.printStackTrace();
                }
                return new JSONObject(builder.toString());
            }
        } catch (IOException | JSONException e) {
            Log.d(TAG, "Unable to connect to OpenWeatherMap.org");
            e.printStackTrace();
        } finally {
            assert connection != null;
            connection.disconnect();
        }
        return null;
    }
}
