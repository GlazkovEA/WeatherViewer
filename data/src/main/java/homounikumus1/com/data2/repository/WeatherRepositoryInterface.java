package homounikumus1.com.data2.repository;

import android.support.annotation.NonNull;


import java.util.List;

import homounikumus1.com.data2.model.weather.CitiesArrayWeather;
import homounikumus1.com.data2.model.weather.Weather;
import homounikumus1.com.data2.model.weather.WeekWeather;
import io.reactivex.Observable;

public interface WeatherRepositoryInterface {
    @NonNull
    Observable<List<CitiesArrayWeather>> getCityArray(String data, String lang);

    @NonNull
    Observable<Weather> getOneDayWeather(double lat, double lon, String cityName, String timeZone, String lang);

    @NonNull
    Observable<List<WeekWeather>> getWeekWeather(double lat, double lon, String timeZone, String lang);
}
