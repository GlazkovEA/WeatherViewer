package homounikumus1.com.myweatherviewer.utils;

import android.content.SharedPreferences;
import android.database.Cursor;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import java.util.HashMap;
import java.util.Map;

import homounikumus1.com.data2.database.DataBaseHelper;
import homounikumus1.com.data2.model.weather.Weather;
import io.realm.Realm;
import io.realm.RealmResults;

import static android.content.Context.MODE_PRIVATE;
import static homounikumus1.com.myweatherviewer.WeatherApp.getAppContext;

public class DatabaseUtils {
    private static final String TAG = "DataBaseUtils";
    /**
     * Stores to cities received from database
     * for to find them from cityID
     */
    public static final Map<String, String> cites = new HashMap<>();
    /**
     * Stores to cities coordinates received from database
     * for to find them from cityName
     * <p>
     * That is because of that cities data that we save in database and received
     * from placesAPI not always correlated with data which we have from weatherAPI
     * by this I mean the correlation of the following data:
     * ID of cities, latitude and longitude of a city of a city
     */

    public static final Map<String, String> timeZoneMap = new HashMap<>();
    public static final Map<String, String> citesCoordinates = new HashMap<>();

    public static boolean addCityInDatabase(String city, double lat, double lon, String timeZone) {
        DataBaseHelper dataBaseHelper = new DataBaseHelper(getAppContext());
        dataBaseHelper.open();
        // check is this city already in the database, or not
        if (!dataBaseHelper.checkIsCityAlreadyExist(city)) {
            Log.d(TAG, city + " was added in database");
            dataBaseHelper.addRec(city, lat, lon, timeZone);
            return true;
        }
        dataBaseHelper.close();
        return false;
    }

    /**
     * Get amount of elements in database
     *
     * @return int
     */
    public static int amountOfElementsInDatabase() {
        DataBaseHelper dataBaseHelper = new DataBaseHelper(getAppContext());
        dataBaseHelper.open();
        int count = (int) dataBaseHelper.amountOfElementsInDatabase();
        dataBaseHelper.close();
        Log.d(TAG, "amount of elements in database = " + count);
        return count;
    }

    /**
     * Delete element from database
     *
     * @param city deleted city
     */
    public static void deleteCityFromDatabase(String city) {
        DataBaseHelper dataBaseHelper = new DataBaseHelper(getAppContext());
        Log.d(TAG, city + " was deleted from database");
        dataBaseHelper.open();
        dataBaseHelper.delRec(city);
        dataBaseHelper.close();
    }


    public static String loadCitesWeather() {
        citesCoordinates.clear();
        cites.clear();
        timeZoneMap.clear();

        DataBaseHelper dataBaseHelper = new DataBaseHelper(getAppContext());

        Log.d(TAG, "load cites saved in database ");

        dataBaseHelper.open();
        Cursor cursor = dataBaseHelper.getAllData();

        // get all cities which we have in database
        String city;
        String cityID;
        double lat;
        double lon;
        String timeZone;
        StringBuilder sb = new StringBuilder();

        if (cursor.moveToFirst()) {
            int cityColumn = cursor.getColumnIndex("CITY");
            int cityIDColumn = cursor.getColumnIndex("CITY_ID");
            int cityLatColumn = cursor.getColumnIndex("LAT");
            int cityLonColumn = cursor.getColumnIndex("LON");
            int timeZoneColumn = cursor.getColumnIndex("TIME_ZONE");
            do {
                city = cursor.getString(cityColumn);
                cityID = cursor.getString(cityIDColumn);
                lat = cursor.getDouble(cityLatColumn);
                lon = cursor.getDouble(cityLonColumn);
                timeZone = cursor.getString(timeZoneColumn);

                if (cityID != null && !cityID.equals("") && !cityID.equals("null")) {
                    sb.append(cityID);
                    sb.append(",");

                    String coordinates = lat + "&" + lon;
                    // put coordinates data because weatherAPI and placesAPI coordinates are not correlated
                    citesCoordinates.put(city, coordinates);
                    // put ID data because weatherAPI and placesAPI ID's are not correlated
                    cites.put(cityID, city);
                    timeZoneMap.put(cityID, timeZone);
                }
            } while (cursor.moveToNext());
        }

        String allCites = sb.toString();

        dataBaseHelper.close();
        if (allCites.length() != 0)
            return allCites.substring(0, allCites.length() - 1);
        else
            return "";
    }


    public static void checkIsAlreadyExist (Weather weather) {
        String id = weather.getId();
        DataBaseHelper dataBaseHelper = new DataBaseHelper(getAppContext());
        dataBaseHelper.open();
        if (!dataBaseHelper.checkIsIDIsAlreadyExist(weather.getCity(), id))
            dataBaseHelper.updateRec(weather.getCity(), id);
        dataBaseHelper.close();
    }

    public static boolean checkData () {
        Realm realm1 = Realm.getDefaultInstance();
        RealmResults<Weather> results1 = realm1.where(Weather.class).findAll();
        return results1.size() > 0;
    }

    public static Weather getLastSavedObject () {
        Realm realm = Realm.getDefaultInstance();
        RealmResults<Weather> results = realm.where(Weather.class).findAll();
        if (results.size()>0)
            return results.first();
        else
            return null;
    }

    public static void explanationShowed(AppCompatActivity activity, boolean exp) {
        SharedPreferences sPref = activity.getPreferences(MODE_PRIVATE);
        SharedPreferences.Editor ed = sPref.edit();
        ed.putBoolean("explanation", exp);
        ed.apply();

    }

    public static boolean isExplanationShowed(AppCompatActivity activity) {
        SharedPreferences sPref = activity.getPreferences(MODE_PRIVATE);
        return sPref.getBoolean("explanation", false);
    }

    public static boolean isFirstStart() {
        return Realm.getDefaultInstance().where(Weather.class).findAll().size()>0;
    }
}
/**     vectorDrawables.useSupportLibrary = true
 }

 buildTypes {
 release {
 minifyEnabled false
 proguardFiles getDefaultProguardFile('proguard-android.txt'), 'proguard-rules.pro'
 }

 }
 compileOptions {
 sourceCompatibility = '1.8'
 targetCompatibility = '1.8'
 sourceCompatibility JavaVersion.VERSION_1_8
 targetCompatibility JavaVersion.VERSION_1_8
 }

 flavorDimensions "version"
 productFlavors {
 prod {
 dimension "version"
 applicationIdSuffix ".prod"
 versionNameSuffix "-prod"
 }

 mock {
 dimension "version"
 applicationIdSuffix ".mock"
 versionNameSuffix "-mock"
 }
 }

 packagingOptions {
 exclude 'META-INF/DEPENDENCIES'
 exclude 'META-INF/LICENSE'
 exclude 'META-INF/LICENSE.txt'
 exclude 'META-INF/license.txt'
 exclude 'META-INF/NOTICE'
 exclude 'META-INF/NOTICE.txt'
 exclude 'META-INF/notice.txt'
 exclude 'META-INF/ASL2.0'
 exclude 'META-INF/rxjava.properties'
 }
 }

 import io.realm.transformer.RealmTransformer
 android.registerTransform(new RealmTransformer())


 dependencies {
 //realm
 implementation 'io.realm:realm-android-library:1.2.0'
 implementation 'io.realm:realm-annotations:1.2.0'
 annotationProcessor "io.realm:realm-annotations-processor:1.2.0"
 //rx
 implementation "io.reactivex.rxjava2:rxandroid:2.1.0"
 implementation "io.reactivex.rxjava2:rxjava:2.2.3"

 implementation 'com.squareup.retrofit2:adapter-rxjava2:2.4.0'

 //glide
 implementation 'com.github.bumptech.glide:glide:4.4.0'
 implementation 'com.google.android.gms:play-services-places:16.0.0'
 annotationProcessor 'com.github.bumptech.glide:compiler:4.4.0'

 implementation 'de.hdodenhof:circleimageview:2.2.0'
 //cardview
 ///noinspection GradleCompatible
 implementation 'com.android.support:cardview-v7:28.0.0'
 implementation 'com.android.support:recyclerview-v7:28.0.0'
 implementation 'com.google.android.gms:play-services-location:16.0.0'
 implementation fileTree(include: ['*.jar'], dir: 'libs')
 //noinspection GradleCompatible
 implementation 'com.android.support:appcompat-v7:28.0.0'
 implementation 'com.android.support.constraint:constraint-layout:1.1.3'
 implementation 'com.android.support:design:28.0.0'
 testImplementation 'junit:junit:4.12'
 androidTestImplementation 'com.android.support.test:runner:1.0.2'
 androidTestImplementation 'com.android.support.test.espresso:espresso-core:3.0.2'

 implementation "com.squareup.retrofit2:retrofit:2.4.0"
 implementation "com.squareup.retrofit2:converter-gson:2.3.0"

 implementation "com.squareup.okhttp3:okhttp:3.10.0"
 implementation "com.squareup.okhttp3:logging-interceptor:3.6.0"

 implementation "com.jakewharton:butterknife:8.5.0"
 annotationProcessor 'com.jakewharton:butterknife-compiler:8.5.0'

 //module
 implementation project(':data')


 //tests
 testImplementation "junit:junit:4.12"
 testImplementation "org.mockito:mockito-core:1.10.19"
 testImplementation "org.powermock:powermock-api-mockito:1.6.5"
 testImplementation "org.powermock:powermock-module-junit4:1.6.5"

 //maybe delete it
 testImplementation("org.robolectric:robolectric:3.2.1") {
 exclude group: 'commons-logging', module: 'commons-logging'
 exclude group: 'org.apache.httpcomponents', module: 'httpclient'
 }

 androidTestImplementation "com.android.support.test:runner:1.0.2"
 androidTestImplementation "com.android.support.test:rules:1.0.2"
 androidTestImplementation "com.android.support.test.espresso:espresso-core:3.0.2"
 androidTestImplementation "com.android.support.test.espresso:espresso-intents:3.0.2"
 androidTestImplementation "com.android.support.test.espresso:espresso-contrib:3.0.2"

 }

 */