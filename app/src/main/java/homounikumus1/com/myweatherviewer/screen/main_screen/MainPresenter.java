package homounikumus1.com.myweatherviewer.screen.main_screen;

import android.annotation.SuppressLint;
import android.os.Handler;
import android.support.annotation.NonNull;

import homounikumus1.com.data2.model.weather.Weather;
import homounikumus1.com.data2.repository.Provider;
import homounikumus1.com.myweatherviewer.R;
import homounikumus1.com.myweatherviewer.loader.LifecycleHandler;
import homounikumus1.com.myweatherviewer.utils.DatabaseUtils;
import homounikumus1.com.myweatherviewer.utils.LocationUtils;
import io.reactivex.disposables.Disposable;

public class MainPresenter {
    private static final String TAG = "MainPresenter";
    private final LifecycleHandler handler;
    private final MView mView;
    private Handler refreshHandler;
    private Handler startHandler;
    private Disposable startSubscriber;
    private Disposable startSubscriberWeek;
    private Disposable todayWeather;
    private Disposable weekWeather;

    public MainPresenter(@NonNull MView mView, @NonNull LifecycleHandler handler) {
        this.mView = mView;
        this.handler = handler;
    }

    /**
     * init delegate - if it is first start - start activity with search window
     *
     * @param lang - localization
     */
    @SuppressLint("CheckResult")
    public void init(String lang, boolean recreate) {
        if (DatabaseUtils.isFirstStart()) {
              if (recreate) {
                  startHandler = new Handler();
                  startHandler.post(()->{
                      start(false, lang);
                  });
             } else {
            startHandler = new Handler();
            startHandler.post(() -> {
                startSubscriber = Provider.getStartRepository().getOneDayWeather().subscribe(mView::showTodayWeather, throwable -> {
                });
                startSubscriberWeek = Provider.getStartRepository().getWeekWeather().subscribe(mView::showWeekWeather, throwable -> {
                });
            });
            refreshHandler = new Handler();
            refreshHandler.postDelayed(() -> {
                start(false, lang);
            }, 200);
             }
        } else {
            mView.startSearchActivity();
        }
    }

    public void destroy() {
        if (startHandler !=null)
            startHandler.removeCallbacksAndMessages(null);
        if (refreshHandler != null)
            refreshHandler.removeCallbacksAndMessages(null);
        if (startSubscriber != null)
            startSubscriber.dispose();
        if (startSubscriberWeek != null)
            startSubscriberWeek.dispose();
        if (todayWeather != null)
            todayWeather.dispose();
        if (weekWeather != null)
            weekWeather.dispose();
    }

    /**
     * start main screen
     *
     * @param update - if true it's from refresh
     * @param lang   - localization
     */
    public void start(boolean update, String lang) {
        Weather weather = DatabaseUtils.getLastSavedObject();
        if (weather != null) {
            getWeekWeather(weather.getLat(), weather.getLon(), weather.getTimeZone(), update, lang);
            getOneDayWeather(weather.getLat(), weather.getLon(), weather.getCity(), weather.getTimeZone(), update, lang);
        }
    }

    /**
     * request to update
     *
     * @param lat      - latitude
     * @param lon      - longitude
     * @param city     - city name
     * @param timeZone - time zone for the date
     * @param lang     - localization
     */
    public void update(double lat, double lon, String city, String timeZone, String lang) {
        getWeekWeather(lat, lon, timeZone, true, lang);
        getOneDayWeather(lat, lon, city, timeZone, true, lang);
    }

    private void getWeekWeather(double lat, double lon, String timeZone, boolean update, String lang) {
        weekWeather = Provider.getWaetherRepository().getWeekWeather(lat, lon, timeZone, lang)
                // when subscribe on observable - show progress bar
                .doOnSubscribe(disposable -> {
                    mView.showLoading();
                })
                // when process terminated - hide progress bar
                .doOnTerminate(mView::hideLoading)
                // handle life cycle
                .compose(handler.choice(update, R.id.week_weather_request))
                // show weather or error
                //.cache()
                .subscribe(mView::showWeekWeather, throwable -> {
                    handler.clear(R.id.week_weather_request);
                    mView.showError(0);
                });
    }

    private void getOneDayWeather(double lat, double lon, String cityName, String timeZone, boolean update, String lang) {
        todayWeather = Provider.getWaetherRepository().getOneDayWeather(lat, lon, cityName, timeZone, lang)
                // when subscribe on observable - show progress bar
                .doOnSubscribe(disposable -> {
                    mView.showLoading();
                })
                .compose(handler.choice(update, R.id.one_day_weather_request))
                .subscribe(weather -> {
                    DatabaseUtils.checkIsAlreadyExist(weather);
                    mView.showTodayWeather(weather);
                }, throwable -> {
                    handler.clear(R.id.one_day_weather_request);
                    mView.showError(1);
                });
    }

    /**
     * Get place from geodata
     */
    public void getPlace(String lang) {
        final double[] latLon = LocationUtils.getCoordinates();
        if (latLon != null)
            update(latLon[0], latLon[1], null, null, lang);
        else
            mView.showGeoExplanation();
    }


    public boolean isExplanationShowed () {
        return DatabaseUtils.isExplanationShowed();
    }

    public void explanationShowed(boolean b) {
        DatabaseUtils.explanationShowed(b);
    }
}
