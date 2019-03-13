package homounikumus1.com.data2.repository;

import android.support.annotation.NonNull;
import android.util.Log;

import homounikumus1.com.data2.model.TimeZone;
import homounikumus1.com.data2.network.ApiFactory;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import io.realm.Realm;
import io.realm.RealmResults;

public class TimeZoneRepository implements TimeZoneInterface {


    @NonNull
    @Override
    public Observable<TimeZone> getTimeZone(String data) {
        return ApiFactory.getTimeService().getTimeZone(data, 0L)
                .flatMap(timeZone -> {
                    // save TimeZone object in database
                    Realm.getDefaultInstance().executeTransaction(realm -> {
                        realm.delete(TimeZone.class);
                        realm.insert(timeZone);
                    });
                    return Observable.just(timeZone);
                })
                .onErrorResumeNext(throwable -> {
                    // get from database in error's cases
                    Realm realm = Realm.getDefaultInstance();
                    RealmResults<TimeZone> results = realm.where(TimeZone.class).findAll();
                    return Observable.just(realm.copyFromRealm(results.get(0)));
                })
                .cache()
                .subscribeOn(Schedulers.io()) // do async
                .observeOn(AndroidSchedulers.mainThread()); // handle results in main thread
    }
}
