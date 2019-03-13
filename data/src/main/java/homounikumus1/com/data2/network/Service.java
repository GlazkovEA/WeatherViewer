package homounikumus1.com.data2.network;

import android.support.annotation.NonNull;

import homounikumus1.com.data2.model.CitiesList;
import homounikumus1.com.data2.model.City;
import homounikumus1.com.data2.model.TimeZone;
import io.reactivex.Observable;
import retrofit2.http.GET;
import retrofit2.http.Query;


public interface Service {

    /**
     * Use {@link Observable}
     *
     * <p>
     * This method returns the observable object for the weather forecast obtained by the
     * latitude and longitude coordinates, which contains weather information detailed information
     * about the current moment
     * <p>
     *
     * Some additional information can be received from here https://openweathermap.org/current
     * @param lat - latitude
     * @param lon - longitude
     * @param lang - localization
     * @return observable object
     */
    @GET("data/2.5/weather?units=metric")
    Observable<City> getWeatherByCoordinates(@Query("lat") double lat, @Query("lon") double lon, @NonNull @Query("lang") String lang);

    /**
     * Use {@link Observable}
     *
     * <p>
     * This method returns the observable object for the weather forecast obtained by the
     * latitude and longitude coordinates, which contains weather information for 5 days.
     * Information is divided into blocks with a forecast period of 3 hours
     * <p>
     *
     * Some additional information can be received from here https://openweathermap.org/forecast5
     * @param lat - latitude
     * @param lon - longitude
     * @param lang - localization
     * @return observable object
     */
    @GET("data/2.5/forecast?units=metric")
    Observable<CitiesList> getWeekWeatherByCoordinates(@Query("lat") double lat, @Query("lon") double lon, @NonNull @Query("lang") String lang);

    /**
     * Use {@link Observable}
     *
     * <p>
     *  This method returns the observable object which contains information about time zone from requested location
     * <p>
     *
     * Some additional information can be received from here
     * https://developers.google.com/maps/documentation/timezone/intro
     * @param location - place
     * @param time - ms
     * @return observable object
     */
    @GET("json?")
    Observable<TimeZone> getTimeZone(@NonNull @Query("location") String location, @NonNull @Query("timestamp") Long time);

    /**
     * Use {@link Observable}
     *
     * <p>
     * This method returns the observable object for the weather forecast obtained by the cities ID's,
     * which contains weather information  about the current moment in every city which ID was recived.
     * Only 20 ID's can be passed as a request
     * <p>
     *
     * Some additional information can be received from here https://openweathermap.org/current
     * @param query - cities id's
     * @param lang - localization
     * @return observable object
     */
    @GET("data/2.5/group?units=metric")
    Observable<CitiesList> getCityArray(@NonNull @Query("id") String query, @NonNull @Query("lang") String lang);
}
