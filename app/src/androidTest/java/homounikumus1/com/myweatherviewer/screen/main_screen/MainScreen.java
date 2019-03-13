package homounikumus1.com.myweatherviewer.screen.main_screen;

import android.support.test.InstrumentationRegistry;
import android.support.test.espresso.intent.Intents;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import homounikumus1.com.data2.model.weather.Weather;
import homounikumus1.com.data2.network.RequestsHandler;
import homounikumus1.com.data2.repository.Provider;
import homounikumus1.com.myweatherviewer.R;
import homounikumus1.com.myweatherviewer.utils.DatabaseUtils;
import io.realm.Realm;

import static android.support.test.espresso.Espresso.closeSoftKeyboard;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.typeText;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.contrib.RecyclerViewActions.scrollToPosition;
import static android.support.test.espresso.intent.matcher.IntentMatchers.hasComponent;
import static android.support.test.espresso.matcher.RootMatchers.withDecorView;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.junit.Assert.assertEquals;

/**
 * Instrumented test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
public class MainScreen {
    @Rule
    public final ActivityTestRule<MainActivity> mActivityRule = new ActivityTestRule<>(MainActivity.class);
    private static final String MONITOR = "MONITOR";
    private MainActivity mActivity = null;
    @Before
    public void setUp() throws Exception {
        mActivity = mActivityRule.getActivity();
        Intents.init();
        RequestsHandler.setContext(InstrumentationRegistry.getTargetContext());
    }

    /**
     * The second test
     * @throws InterruptedException
     */
    @Test
    public void scrollAndProgressBar() throws InterruptedException {

        onView(withId(R.id.progress_bar_bar)).check(matches(isDisplayed()));

        synchronized (mActivityRule) {
            mActivityRule.wait(5000);
        }

        onView(withId(R.id.progress_bar_bar)).check(matches(not(isDisplayed())));

        onView(withId(R.id.city)).check(matches(withText("Екатеринбург")));
        onView(withId(R.id.listView))
                .perform(scrollToPosition(15))
                .perform(scrollToPosition(8))
                .perform(scrollToPosition(1))
                .perform(scrollToPosition(2))
                .perform(scrollToPosition(10))
                .perform(scrollToPosition(19));
    }

    /**
     * The third test
     * @throws InterruptedException
     */
    @Test
    public void getGeoData() throws InterruptedException {
        Weather weather = new Weather();
        weather.setCity("Ekaterinburg");
        weather.setLat(12);
        weather.setLon(34);
        weather.setEx(false);
        weather.setTimeZone("en");
        Realm.getDefaultInstance().executeTransaction(realm -> {
            realm.insert(weather);
        });
        DatabaseUtils.addCityInDatabase("Ekaterinburg", 12, 34, "");

        synchronized (mActivityRule) {
            mActivityRule.wait(3000);
        }

        onView(withId(R.id.listView))
                .perform(scrollToPosition(15))
                .perform(scrollToPosition(8))
                .perform(scrollToPosition(1))
                .perform(scrollToPosition(2))
                .perform(scrollToPosition(10))
                .perform(scrollToPosition(19));

        onView(withId(R.id.place)).perform(click());

        onView(withId(R.id.progress_bar_bar)).check(matches(isDisplayed()));

        synchronized (mActivityRule) {
            mActivityRule.wait(5000);
        }

        onView(withId(R.id.progress_bar_bar)).check(matches(not(isDisplayed())));

        onView(withId(R.id.city)).check(matches(withText("London")));
    }

    /**
     * The fourth test
     * @throws InterruptedException
     */
    @Test
    public void getClickAddCity() throws InterruptedException {
        Weather weather = new Weather();
        weather.setCity("Ekaterinburg");
        weather.setLat(12);
        weather.setLon(34);
        weather.setEx(false);
        weather.setTimeZone("en");
        Realm.getDefaultInstance().executeTransaction(realm -> {
            realm.insert(weather);
        });
        DatabaseUtils.addCityInDatabase("Ekaterinburg", 12, 34, "");
        DatabaseUtils.cites.put("524901", "Moscow");
        DatabaseUtils.cites.put("703448", "Ekaterinburg");
        DatabaseUtils.cites.put("2643743", "London");
        DatabaseUtils.timeZoneMap.put("Moscow", "moscow/europe");
        DatabaseUtils.timeZoneMap.put("Ekaterinburg", "ekaterinburg/asia");
        DatabaseUtils.timeZoneMap.put("London", "london/europe");
        DatabaseUtils.citesCoordinates.put("Moscow", "524901&703448");
        DatabaseUtils.citesCoordinates.put("Ekaterinburg", "5241&7048");
        DatabaseUtils.citesCoordinates.put("London", "5241&448");

        synchronized (mActivityRule) {
            mActivityRule.wait(3000);
        }

        DatabaseUtils.setAmountOfElementsInDatabase(3);
        DatabaseUtils.setCitiesList("ekb");

        onView(withId(R.id.listView))
                .perform(scrollToPosition(15))
                .perform(scrollToPosition(8))
                .perform(scrollToPosition(1))
                .perform(scrollToPosition(2))
                .perform(scrollToPosition(10))
                .perform(scrollToPosition(19));

        onView(withId(R.id.place)).perform(click());

        onView(withId(R.id.progress_bar_bar)).check(matches(isDisplayed()));

        synchronized (mActivityRule) {
            mActivityRule.wait(5000);
        }

        onView(withId(R.id.progress_bar_bar)).check(matches(not(isDisplayed())));

        onView(withId(R.id.add_city)).perform(click());

        synchronized (mActivityRule) {
            mActivityRule.wait(2000);
        }

        Realm.getDefaultInstance().executeTransaction(realm2 -> realm2.deleteAll());
        DatabaseUtils.clean();
    }


    /**
     * The first test
     * @throws InterruptedException
     */
    @Test
    public void fullStart() throws InterruptedException {
        synchronized (mActivityRule) {
            mActivityRule.wait(3000);
        }
        Weather weather = new Weather();
        weather.setCity("Ekaterinburg");
        weather.setLat(12);
        weather.setLon(34);
        weather.setEx(false);
        weather.setTimeZone("en");
        Realm.getDefaultInstance().executeTransaction(realm -> {
            realm.insert(weather);
        });
        DatabaseUtils.addCityInDatabase("Ekaterinburg", 12, 34, "");


        onView(withId(R.id.autocomplete_places)).perform(typeText("eka"));
        closeSoftKeyboard();

        onView(withText("Ekaterinburg"))
                .inRoot(withDecorView(not(is(mActivity.getWindow().getDecorView()))))
                .check(matches(isDisplayed()));

        onView(withText("Ekaterinburg"))
                .inRoot(withDecorView(not(is(mActivity.getWindow().getDecorView()))))
                .perform(click());

        synchronized (mActivityRule) {
            try {
                mActivityRule.wait(4000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        onView(withId(R.id.city)).check(matches(withText("Екатеринбург")));
    }



    @After
    public void tearDown() throws Exception {

        synchronized (mActivityRule) {
            Intents.release();
        }
       //Realm.getDefaultInstance().executeTransaction(realm2 -> realm2.deleteAll());
       //DatabaseUtils.clean();
        DatabaseUtils.setAmountOfElementsInDatabase(null);
        Provider.setWeatherRepository(null);
    }
}
