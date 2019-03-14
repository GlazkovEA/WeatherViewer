package homounikumus1.com.data2.repository;

import java.util.List;
import homounikumus1.com.data2.model.weather.CitiesArrayWeather;
import homounikumus1.com.data2.model.weather.Weather;
import homounikumus1.com.data2.model.weather.WeekWeather;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import io.realm.Realm;
import io.realm.RealmResults;

public class StartRepository {

    public Observable<List<CitiesArrayWeather>> getCityArray() {
        Realm realm = Realm.getDefaultInstance();
        RealmResults<CitiesArrayWeather> results = realm.where(CitiesArrayWeather.class).findAll();
        List<CitiesArrayWeather> list = realm.copyFromRealm(results);

        return Observable.just(list).onErrorResumeNext(throwable -> {
            return Observable.error(new Exception());
        })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public Observable<List<WeekWeather>> getWeekWeather() {
        Realm realm = Realm.getDefaultInstance();
        RealmResults<WeekWeather> results = realm.where(WeekWeather.class).findAll();
        List<WeekWeather> list = realm.copyFromRealm(results);

        return Observable.just(list).onErrorResumeNext(throwable -> {
            return Observable.error(new Exception());
        })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public Observable<Weather> getOneDayWeather() {
        Realm realm = Realm.getDefaultInstance();
        RealmResults<Weather> results = realm.where(Weather.class).findAll();

        if (results.size() > 0) {
            return Observable.just(realm.copyFromRealm(results.get(0)))
                    .onErrorResumeNext(throwable -> {
                        return Observable.error(new Exception());
                    })
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread());
        } else return Observable.error(new Exception());
    }
}
