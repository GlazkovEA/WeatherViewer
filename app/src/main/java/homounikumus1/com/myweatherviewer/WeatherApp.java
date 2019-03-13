package homounikumus1.com.myweatherviewer;

import android.app.Application;
import android.support.annotation.NonNull;

import io.realm.DynamicRealm;
import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.RealmMigration;
import io.realm.RealmSchema;
import io.realm.rx.RealmObservableFactory;

public class WeatherApp extends Application {
    private static WeatherApp sInstance;

    @Override
    public void onCreate() {
        super.onCreate();
        sInstance = this;

        RealmConfiguration configuration = new RealmConfiguration.Builder(this)
                .schemaVersion(0) // Must be bumped when the schema changes
                .migration(new MyMigration())
                .rxFactory(new RealmObservableFactory())
                .build();
        Realm.setDefaultConfiguration(configuration);
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
                schema.create("TodayWeather")
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
                        .addField("timeZoneId", String.class);

                oldVersion++;
            }

            if (oldVersion == 1) {
                oldVersion++;
            }
        }
    }
}
