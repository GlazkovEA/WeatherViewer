package homounikumus1.com.data2.repository;

import android.support.annotation.NonNull;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import homounikumus1.com.data2.model.CitiesList;
import homounikumus1.com.data2.model.City;
import homounikumus1.com.data2.model.weather.CitiesArrayWeather;
import homounikumus1.com.data2.model.weather.Weather;
import homounikumus1.com.data2.model.weather.WeekWeather;
import homounikumus1.com.data2.network.ApiFactory;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import io.realm.Realm;
import io.realm.RealmResults;

public class WeatherRepository implements WeatherRepositoryInterface {
    private String data;

    public void setData(String data) {
        this.data = data;
    }

    public String getData() {
        return data;
    }

    @NonNull
    @Override
    public Observable<List<CitiesArrayWeather>> getCityArray(String data, String lang) {
        return ApiFactory.getWeatherService().getCityArray(data, lang)
                // get Cities list
                .map(CitiesList::getCities)
                // conversion in model object which we can save in realm
                .map(this::getCitiesArrayWeatherObject)
                .flatMap(weathers -> {
                    // save CitiesArrayWeather in database
                    Realm.getDefaultInstance().executeTransaction(realm -> {
                        realm.delete(CitiesArrayWeather.class);
                        realm.insert(weathers);
                    });
                    return Observable.just(weathers);
                })
                .onErrorResumeNext(throwable -> {
                    // load from database in errors cases
                    Realm realm = Realm.getDefaultInstance();
                    RealmResults<CitiesArrayWeather> results = realm.where(CitiesArrayWeather.class).findAll();
                    // mark object that it's was loaded from database to inform user about it after
                    List<CitiesArrayWeather> list = realm.copyFromRealm(results);
                    if (list.size() > 0)
                        list.get(0).setEx(true);

                    if (list.size() > 0)
                        return Observable.just(list);
                    else
                        return Observable.error(new IOException());
                })
                .cache()
                .subscribeOn(Schedulers.io()) // do async
                .observeOn(AndroidSchedulers.mainThread()); // handle results in main thread

    }

    @NonNull
    @Override
    public Observable<List<WeekWeather>> getWeekWeather(double lat, double lon, String timeZone, String lang) {
        return ApiFactory.getWeatherService().getWeekWeatherByCoordinates(lat, lon, lang)
                .map(CitiesList::getCities)
                .map(cities -> {
                    return getWeekWeatherObject(cities, timeZone);
                })
                .flatMap(weathers -> {
                    Realm.getDefaultInstance().executeTransaction(realm -> {
                        realm.delete(WeekWeather.class);
                        realm.insert(weathers);
                    });
                    return Observable.just(weathers);
                })
                .onErrorResumeNext(throwable -> {
                    Realm realm = Realm.getDefaultInstance();
                    RealmResults<WeekWeather> results = realm.where(WeekWeather.class).findAll();
                    List<WeekWeather> list = realm.copyFromRealm(results);//new ArrayList<>();//realm.copyFromRealm(results);

                    if (list.size() > 0)
                        list.get(0).setEx(true);

                    if (list.size() > 0)
                        return Observable.just(list);
                    else
                        return Observable.error(new IOException());
                })
                .cache()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    @NonNull
    @Override
    public Observable<Weather> getOneDayWeather(double lat, double lon, String cityName, String timeZone, String lang) {
        return ApiFactory.getWeatherService().getWeatherByCoordinates(lat, lon, lang)

                .map(city -> {
                    return getTodaysWeather(city, lat, lon, cityName, timeZone);
                })
                .flatMap(weathers -> {
                    Realm.getDefaultInstance().executeTransaction(realm -> {
                        realm.delete(Weather.class);
                        realm.insert(weathers);
                    });

                    return Observable.just(weathers);
                })
                .onErrorResumeNext(throwable -> {
                    Realm realm = Realm.getDefaultInstance();
                    RealmResults<Weather> results = realm.where(Weather.class).findAll();

                    Weather weather = null;
                    if (results.size() > 0)
                        weather = realm.copyFromRealm(results.get(0));
                    if (weather != null)
                        weather.setEx(true);

                    if (weather != null)
                        return Observable.just(weather);
                    else
                        return Observable.error(new Exception());
                })
                .cache()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }


    private Weather getTodaysWeather(City city, double lat, double lon, String cityName, String timeZone) {
        if (city == null || city.getMain() == null || city.getWeather() == null || city.getSys() == null || city.getWind() == null)
            return null;

        Weather weather;
        // if data received from geodata
        if (cityName == null) {
            weather = new Weather(
                    city.getId(),
                    city.getDt(),
                    city.getName(),
                    city.getWeather().getDescription(),
                    city.getWeather().getIcon(),
                    city.getMain().getTemp(),
                    city.getMain().getPressure(),
                    city.getMain().getHumidity(),
                    city.getWind().getSpeed(),
                    city.getSys().getSunrise(),
                    city.getSys().getSunset(),
                    Long.parseLong(city.getId()),
                    lat,
                    lon,
                    timeZone
            );
            // all other cases
        } else {
            double lat2 = city.getCoord().getLat();
            double lon2 = city.getCoord().getLon();
            weather = new Weather(
                    city.getId(),
                    city.getDt(),
                    cityName,
                    city.getWeather().getDescription(),
                    city.getWeather().getIcon(),
                    city.getMain().getTemp(),
                    city.getMain().getPressure(),
                    city.getMain().getHumidity(),
                    city.getWind().getSpeed(),
                    city.getSys().getSunrise(),
                    city.getSys().getSunset(),
                    Long.parseLong(city.getId()),
                    lat2,
                    lon2,
                    timeZone
            );
        }
        return weather;
    }

    private List<WeekWeather> getWeekWeatherObject(List<City> cities, String timeZone) {
        List<WeekWeather> weathers = new ArrayList<>();
        for (City w : cities) {
            if (w.getMain() != null || w.getWeather() != null)
                weathers.add(new WeekWeather(
                        w.getDt(),
                        w.getMain().getTemp(),
                        w.getMain().getHumidity(),
                        Objects.requireNonNull(w.getWeather()).getDescription(),
                        w.getWeather().getIcon(),
                        timeZone));
        }
        return weathers;
    }

    private List<CitiesArrayWeather> getCitiesArrayWeatherObject(List<City> cities) {
        List<CitiesArrayWeather> list = new ArrayList<>();
        if (cities == null)
            return null;

        // here some data will be empty, they will be added from the database later
        for (City w : cities) {
            if (w.getWeather() == null || w.getMain() == null)
                break;
            list.add(new CitiesArrayWeather(
                    w.getId(),
                    "",
                    w.getWeather().getDescription(),
                    w.getMain() != null ? w.getMain().getTemp() : 0,
                    w.getWeather().getIcon(),
                    0,
                    0,
                    ""
            ));
        }
        return list;
    }

}
