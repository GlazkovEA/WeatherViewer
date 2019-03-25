package homounikumus1.com.myweatherviewer.service;

import android.accounts.Account;
import android.content.AbstractThreadedSyncAdapter;
import android.content.ContentProviderClient;
import android.content.ContentResolver;
import android.content.Context;
import android.content.SyncResult;
import android.os.Bundle;

import homounikumus1.com.data2.model.weather.Weather;
import homounikumus1.com.data2.repository.Provider;
import homounikumus1.com.myweatherviewer.R;
import homounikumus1.com.myweatherviewer.WeatherApp;
import homounikumus1.com.myweatherviewer.utils.DatabaseUtils;
import homounikumus1.com.myweatherviewer.utils.LocationUtils;

public class SyncAdapter extends AbstractThreadedSyncAdapter {
    private Context context;
    // Global variables
    // Define a variable to contain a content resolver instance
    private ContentResolver mContentResolver;


    /**
     * Set up the sync adapter
     */
    public SyncAdapter(Context context, boolean autoInitialize) {
        super(context, autoInitialize);
        this.context = context;
        /*
         * If your app uses a content resolver, get an instance of it
         * from the incoming Context
         */
        mContentResolver = context.getContentResolver();
    }

    /**
     * Set up the sync adapter. This form of the
     * constructor maintains compatibility with Android 3.0
     * and later platform versions
     */
    public SyncAdapter(
            Context context,
            boolean autoInitialize,
            boolean allowParallelSyncs) {
        super(context, autoInitialize, allowParallelSyncs);
        this.context = context;
        /*
         * If your app uses a content resolver, get an instance of it
         * from the incoming Context
         */
        mContentResolver = context.getContentResolver();
    }

    @Override
    public void onPerformSync(Account account, Bundle extras, String authority, ContentProviderClient provider, SyncResult syncResult) {
        // get last object saved from database
        Weather weather = DatabaseUtils.getLastSavedObject();
        final String lang = context.getString(R.string.lang);
        // get coordinated
        final double[] latLon = LocationUtils.getCoordinates();
        String cityName = null;
        String timeZone = null;
        double lat;
        double lon;
        // If we get coordinates update data and send notification if it's enable
        if (latLon != null) {
            lat  = latLon[0];
            lon = latLon[1];

            Provider.getWaetherRepository().getWeekWeather(lat, lon, timeZone, lang);
            Provider.getWaetherRepository().getOneDayWeather(lat, lon,cityName, timeZone, lang).
                    subscribe(weather1 -> {
                        if (DatabaseUtils.isShowNotification())
                            WeatherApp.startNotification(weather1.getCity(), weather1.getDescription() + "  " + weather1.getTemp());
                    }, throwable -> {});
        } else {
            //in other cases only update las city which was on the main screen
            if (weather!=null) {
                cityName = weather.getCity();
                lat = weather.getLat();
                lon = weather.getLon();
                timeZone = weather.getTimeZone();
                Provider.getWaetherRepository().getWeekWeather(lat, lon, timeZone, lang);
                Provider.getWaetherRepository().getOneDayWeather(lat, lon, cityName, timeZone, lang);
            }
        }
    }
}
