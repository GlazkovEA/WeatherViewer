package homounikumus1.com.data2;

import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.List;

import homounikumus1.com.data2.model.weather.CitiesArrayWeather;
import homounikumus1.com.data2.model.weather.Weather;
import homounikumus1.com.data2.model.weather.WeekWeather;
import homounikumus1.com.data2.network.RequestsHandler;
import homounikumus1.com.data2.repository.Provider;
import homounikumus1.com.data2.repository.WeatherRepository;
import io.reactivex.Observable;
import io.reactivex.observers.TestObserver;
import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.RealmResults;
import io.realm.rx.RealmObservableFactory;

import static org.junit.Assert.*;

/**
 * Instrumented test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
public class RepInstrumentedTest {
    private WeatherRepository repository;
    @Rule
    public RxSchedulersTestRule mRule = new RxSchedulersTestRule();

    @Before
    public void setUp() throws Exception {
        RequestsHandler.setContext(InstrumentationRegistry.getTargetContext());
        repository = new WeatherRepository();

        RealmConfiguration configuration = new RealmConfiguration.Builder(InstrumentationRegistry.getTargetContext())
                .schemaVersion(0)
                .rxFactory(new RealmObservableFactory())
                .build();
        Realm.setDefaultConfiguration(configuration);
    }

    @Test
    public void testOneDay() {
        TestObserver<Weather> testObserver = new TestObserver<>();

        repository.getOneDayWeather(6,4,"London", "zome","ru")
                .flatMap(Observable::fromArray)
                .subscribe(testObserver);

        Realm realm = Realm.getDefaultInstance();
        RealmResults<Weather> results = realm.where(Weather.class).findAll();

        Weather weather = new Weather();
        if (results.size()>0)
            weather = realm.copyFromRealm(results.get(0));

        assertEquals(weather.getCity(), "London");
        testObserver.assertNoErrors();
        testObserver.assertValueCount(1);
    }

    @Test
    public void testWeek() {
        TestObserver<List<WeekWeather>> listTestObserver = new TestObserver<>();
        repository.getWeekWeather(3,5,"ew", "ru")
                .flatMap(Observable::fromArray)
                .subscribe(listTestObserver);

        listTestObserver.assertNoErrors();
        listTestObserver.assertValueCount(1);
        assertEquals(listTestObserver.values().get(0).size(), 36);
    }

    @Test
    public void citiesList() {
        TestObserver<List<CitiesArrayWeather>> listTestObserver = new TestObserver<>();
        repository.getCityArray("data", "ru")
                .flatMap(Observable::fromArray)
                .subscribe(listTestObserver);

        listTestObserver.assertNoErrors();
        listTestObserver.assertValueCount(1);
        assertEquals(listTestObserver.values().get(0).size(), 3);
    }

    @Test
    public void restoredFromCache() {
        TestObserver<Weather> testObserver = new TestObserver<>();

        repository.getOneDayWeather(6,4,"London", "zome","ru")
                .flatMap(Observable::fromArray)
                .subscribe(testObserver);

        Realm realm = Realm.getDefaultInstance();
        RealmResults<Weather> results = realm.where(Weather.class).findAll();

        Weather weather = null;
        if (results.size()>0)
            weather = realm.copyFromRealm(results.get(0));

        assertEquals(weather.getCity(), "London");
        testObserver.assertNoErrors();
        testObserver.assertValueCount(1);


        TestObserver<Weather> testObserver2 = new TestObserver<>();

        Provider.getWaetherRepository().setData("error");
        repository.getOneDayWeather(0,0,null, null, null).subscribe(testObserver2);

        Weather weather2 = null;
        if (results.size()>0)
            weather2 = realm.copyFromRealm(results.get(0));

        assertEquals(weather2.getCity(), "London");
        testObserver.assertNoErrors();
        testObserver.assertValueCount(1);
    }

    @Test
    public void restoredFromCacheWeek() {
        TestObserver<List<WeekWeather>> testObserver = new TestObserver<>();

        repository.getWeekWeather(9,9, "zone", "ru")
                .flatMap(Observable::fromArray)
                .subscribe(testObserver);

        Realm realm = Realm.getDefaultInstance();
        RealmResults<WeekWeather> results = realm.where(WeekWeather.class).findAll();

        List<WeekWeather> weather = null;
        if (results.size()>0)
            weather = realm.copyFromRealm(results);

        testObserver.assertNoErrors();
        testObserver.assertValueCount(1);
        assertEquals(testObserver.values().get(0).size(), 36);
        assertEquals(weather.get(1).getDescription(), "clear sky");


        TestObserver<List<WeekWeather>> testObserver2 = new TestObserver<>();

        Provider.getWaetherRepository().setData("error");
        repository.getWeekWeather(0,0,null, null).subscribe(testObserver2);

        List<WeekWeather> weather2 = null;
        if (results.size()>0)
            weather2 = realm.copyFromRealm(results);

        testObserver.assertNoErrors();
        testObserver.assertValueCount(1);
        assertEquals(testObserver.values().get(0).size(), 36);
        assertEquals(weather2.get(1).getDescription(), "clear sky");
    }


    @Test
    public void restoredFromCacheCitiesList() {
        TestObserver<List<CitiesArrayWeather>> testObserver = new TestObserver<>();

        repository.getCityArray("London", "ru")
                .flatMap(Observable::fromArray)
                .subscribe(testObserver);

        Realm realm = Realm.getDefaultInstance();
        RealmResults<CitiesArrayWeather> results = realm.where(CitiesArrayWeather.class).findAll();

        List<CitiesArrayWeather> weather = null;
        if (results.size()>0)
            weather = realm.copyFromRealm(results);

        testObserver.assertNoErrors();
        testObserver.assertValueCount(1);
        assertEquals(testObserver.values().get(0).size(), 3);
        assertEquals(weather.get(1).getIconURL(), "01n.png");


        TestObserver<List<CitiesArrayWeather>> testObserver2 = new TestObserver<>();

        Provider.getWaetherRepository().setData("error");
        repository.getCityArray(null, null).subscribe(testObserver2);

        List<CitiesArrayWeather> weather2 = null;
        if (results.size()>0)
            weather2 = realm.copyFromRealm(results);

        testObserver.assertNoErrors();
        testObserver.assertValueCount(1);
        assertEquals(testObserver.values().get(0).size(), 3);
        assertEquals(weather2.get(1).getIconURL(), "01n.png");
    }

    @Test
    public void error () {
        TestObserver<Weather> testObserver = new TestObserver<>();

        Provider.getWaetherRepository().setData("error");

        repository.getOneDayWeather(6,4,"come city", "zome","ru")
                .flatMap(Observable::fromArray)
                .subscribe(testObserver);

        testObserver.assertError(Exception.class);
    }

    @Test
    public void weekError () {
        TestObserver<List<WeekWeather>> testObserver = new TestObserver<>();

        Provider.getWaetherRepository().setData("error");
        repository.getWeekWeather(0,0,null, null)
                .subscribe(testObserver);

        testObserver.assertError(Exception.class);
    }

    @Test
    public void citiesListError () {
        TestObserver<List<CitiesArrayWeather>> testObserver = new TestObserver<>();

        Provider.getWaetherRepository().setData("error");
        repository.getCityArray(null, null).subscribe(testObserver);
        testObserver.assertError(Exception.class);
    }

    @Test
    public void useAppContext() {
        // Context of the app under test.
        Context appContext = InstrumentationRegistry.getTargetContext();
        assertEquals("homounikumus1.com.data2.test", appContext.getPackageName());
    }


    @After
    public void tearDown() throws Exception {
        Provider.getWaetherRepository().setData(null);
        Realm.getDefaultInstance().executeTransaction(realm2 -> realm2.deleteAll());
    }
}
