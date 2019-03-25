package homounikumus1.com.myweatherviewer;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.Application;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import homounikumus1.com.myweatherviewer.screen.main_screen.MainActivity;
import io.realm.DynamicRealm;
import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.RealmMigration;
import io.realm.RealmSchema;
import io.realm.rx.RealmObservableFactory;

public class WeatherApp extends Application {
    private static WeatherApp sInstance;
    private static final Integer NotificationID = 298338823;
    public static final String ACCOUNT_TYPE = "homounikumus1.com.myweatherviewer";
    public static final String AUTHORITY = "homounikumus1.com.myweatherviewer";
    public static Account sAccount;

    @Override
    public void onCreate() {
        super.onCreate();
        sInstance = this;

        RealmConfiguration configuration = new RealmConfiguration.Builder(this)
                //.deleteRealmIfMigrationNeeded()
                .schemaVersion(1) // Must be bumped when the schema changes
                .migration(new MyMigration())
                .rxFactory(new RealmObservableFactory())
                .build();
        Realm.setDefaultConfiguration(configuration);

        final AccountManager am = AccountManager.get(this);
        if (sAccount == null) {
            sAccount = new Account("WV", ACCOUNT_TYPE);
        }
        if (am.addAccountExplicitly(sAccount, getPackageName(), new Bundle())) {
            ContentResolver.setSyncAutomatically(sAccount, AUTHORITY, true);
            ContentResolver.setIsSyncable(sAccount, AUTHORITY, 1);
            ContentResolver.addPeriodicSync(WeatherApp.sAccount, WeatherApp.AUTHORITY, Bundle.EMPTY, 43200);
        }
    }


    @NonNull
    public static WeatherApp getAppContext() {
        return sInstance;
    }

    private class MyMigration implements RealmMigration {
        @Override
        public void migrate(DynamicRealm realm, long oldVersion, long newVersion) {
            // DynamicRealm exposes an editable schema
            RealmSchema schema = realm.getSchema();

            if (oldVersion == 0) {
                /*schema.create("TodayWeather")
                        .addField("city", String.class)
                        .addField("lat", double.class)
                        .addField("lon", double.class)
                        .addField("pressure", String.class)
                        .addField("wind_speed", String.class)
                        .addField("sunrise", String.class)
                        .addField("iconURL", String.class)
                        .addField("date", Integer.class)
                        .addField("dayOfWeek", String.class)
                        .addField("temp", String.class)
                        .addField("humidity", String.class)
                        .addField("description", String.class)
                        .addField("timeZone", String.class)
                        .addField("isEx", boolean.class);
                schema.create("WeekWeather")
                        .addField("iconURL", String.class)
                        .addField("date", Integer.class)
                        .addField("temp", String.class)
                        .addField("humidity", String.class)
                        .addField("description", String.class)
                        .addField("isEx", boolean.class);
                schema.create("CitiesArrayWeather")
                        .addField("id", String.class)
                        .addField("city", String.class)
                        .addField("timeZone", String.class)
                        .addField("iconURL", String.class)
                        .addField("description", String.class)
                        .addField("temp", String.class)
                        .addField("lat", double.class)
                        .addField("lon", double.class)
                        .addField("isSelected", boolean.class)
                        .addField("isEx", boolean.class);

                schema.create("TimeZone")
                        .addField("timeZoneId", String.class);*/

                oldVersion++;
            }

            if (oldVersion == 1) {
                schema.create("Notify")
                        .addField("isShow", boolean.class);
                schema.create("Explanation")
                        .addField("explanation", boolean.class);

                oldVersion++;
            }
        }
    }

    public static void startNotification(String contentTitle, String contentText) {
        final String CHANNEL_ID = "my_channel_02";// The id of the channel.
        CharSequence name = getAppContext().getString(R.string.channel_name);// The user-visible name of the channel.
        int importance = 0;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            importance = NotificationManager.IMPORTANCE_HIGH;
        }
        NotificationChannel mChannel = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            mChannel = new NotificationChannel(CHANNEL_ID, name, importance);
        }

        NotificationCompat.Builder notification = new NotificationCompat.Builder(getAppContext());
        notification.setAutoCancel(true);
        notification.setContentTitle(contentTitle);
        notification.setContentText(contentText);
        //notification.setTicker("New Messages!");
        notification.setChannelId("my_channel_02");
        notification.setSmallIcon(R.drawable.icon_round);
        Uri alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        notification.setSound(alarmSound);
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(getAppContext());
        stackBuilder.addParentStack(MainActivity.class);

        Intent resultIntent = new Intent(getAppContext(), MainActivity.class);
        stackBuilder.addNextIntent(resultIntent);
        PendingIntent pIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
        notification.setContentIntent(pIntent);
        NotificationManager manager = (NotificationManager) getAppContext().getSystemService(Context.NOTIFICATION_SERVICE);
        if (manager != null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                manager.createNotificationChannel(mChannel);
            }
            manager.notify(NotificationID, notification.build());
        }
    }
}
