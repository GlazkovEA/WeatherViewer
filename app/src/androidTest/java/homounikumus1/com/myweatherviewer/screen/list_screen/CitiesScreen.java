package homounikumus1.com.myweatherviewer.screen.list_screen;

import android.content.Context;
import android.content.Intent;
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
import homounikumus1.com.myweatherviewer.screen.cities_list_screen.AddCityActivity;
import homounikumus1.com.myweatherviewer.screen.main_screen.MainActivity;
import homounikumus1.com.myweatherviewer.utils.DatabaseUtils;
import io.realm.Realm;

import static android.support.test.espresso.Espresso.closeSoftKeyboard;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.longClick;
import static android.support.test.espresso.action.ViewActions.typeText;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.contrib.RecyclerViewActions.scrollToPosition;
import static android.support.test.espresso.matcher.RootMatchers.withDecorView;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.junit.Assert.assertEquals;

@RunWith(AndroidJUnit4.class)
public class CitiesScreen {

    @Rule
    public final ActivityTestRule<MainActivity> mActivityRule = new ActivityTestRule<>(MainActivity.class);
    private MainActivity activity;

    @Before
    public void setUp() throws Exception {
        Intents.init();
        activity = mActivityRule.getActivity();
        RequestsHandler.setContext(InstrumentationRegistry.getTargetContext());
    }

    /**
     * The first test
     * @throws Exception
     */
    @Test
    public void start() throws Exception {
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

        onView(withId(R.id.autocomplete_places)).perform(typeText("eka"));
        closeSoftKeyboard();

        synchronized (mActivityRule) {
            mActivityRule.wait(1000);
        }

        onView(withText("Ekaterinburg"))
                .inRoot(withDecorView(not(is(activity.getWindow().getDecorView()))))
                .check(matches(isDisplayed()));

        onView(withText("Ekaterinburg"))
                .inRoot(withDecorView(not(is(activity.getWindow().getDecorView()))))
                .perform(click());

        DatabaseUtils.setAmountOfElementsInDatabase(3);
        DatabaseUtils.setCitiesList("ekb");

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
    }

    /**
     * The second test
     * @throws Exception
     */
    @Test
    public void scroll() throws Exception {
        DatabaseUtils.cites.put("524901", "Moscow");
        DatabaseUtils.cites.put("703448", "Ekaterinburg");
        DatabaseUtils.cites.put("2643743", "London");
        DatabaseUtils.timeZoneMap.put("Moscow", "moscow/europe");
        DatabaseUtils.timeZoneMap.put("Ekaterinburg", "ekaterinburg/asia");
        DatabaseUtils.timeZoneMap.put("London", "london/europe");
        DatabaseUtils.citesCoordinates.put("Moscow", "524901&703448");
        DatabaseUtils.citesCoordinates.put("Ekaterinburg", "5241&7048");
        DatabaseUtils.citesCoordinates.put("London", "5241&448");

        DatabaseUtils.setAmountOfElementsInDatabase(3);
        DatabaseUtils.setCitiesList("ekb");

        synchronized (mActivityRule) {
            mActivityRule.wait(3000);
        }

        onView(withId(R.id.add_city)).perform(click());

        synchronized (mActivityRule) {
            mActivityRule.wait(3000);
        }

        onView(withId(R.id.city_array)).perform(scrollToPosition(0));
        onView(withId(R.id.city_array)).perform(scrollToPosition(1));
        onView(withId(R.id.city_array)).perform(scrollToPosition(2));
    }

    /**
     * The last test
     * @throws Exception
     */
    @Test
    public void clickToItem() throws Exception {
        DatabaseUtils.cites.put("524901", "Moscow");
        DatabaseUtils.cites.put("703448", "Ekaterinburg");
        DatabaseUtils.cites.put("2643743", "London");
        DatabaseUtils.timeZoneMap.put("Moscow", "moscow/europe");
        DatabaseUtils.timeZoneMap.put("Ekaterinburg", "ekaterinburg/asia");
        DatabaseUtils.timeZoneMap.put("London", "london/europe");
        DatabaseUtils.citesCoordinates.put("Moscow", "524901&703448");
        DatabaseUtils.citesCoordinates.put("Ekaterinburg", "5241&7048");
        DatabaseUtils.citesCoordinates.put("London", "5241&448");

        DatabaseUtils.setAmountOfElementsInDatabase(3);
        DatabaseUtils.setCitiesList("ekb");

        synchronized (mActivityRule) {
            mActivityRule.wait(3000);
        }

        onView(withId(R.id.add_city)).perform(click());

        synchronized (mActivityRule) {
            mActivityRule.wait(3000);
        }

        onView(withId(R.id.city_array)).perform(scrollToPosition(0));
        onView(withId(R.id.city_array)).perform(scrollToPosition(1));
        onView(withId(R.id.city_array)).perform(scrollToPosition(2));
        onView(withText("Moscow")).perform(click());
        synchronized (mActivityRule) {
            mActivityRule.wait(4000);
        }
        onView(withId(R.id.city)).check(matches(withText("Moscow")));

        Realm.getDefaultInstance().executeTransaction(realm2 -> realm2.deleteAll());
        DatabaseUtils.clean();
    }

    /**
     * The fifth test
     * @throws Exception
     */
    @Test
    public void fab() throws Exception {
        DatabaseUtils.cites.put("524901", "Moscow");
        DatabaseUtils.cites.put("703448", "Ekaterinburg");
        DatabaseUtils.cites.put("2643743", "London");
        DatabaseUtils.timeZoneMap.put("Moscow", "moscow/europe");
        DatabaseUtils.timeZoneMap.put("Ekaterinburg", "ekaterinburg/asia");
        DatabaseUtils.timeZoneMap.put("London", "london/europe");
        DatabaseUtils.citesCoordinates.put("Moscow", "524901&703448");
        DatabaseUtils.citesCoordinates.put("Ekaterinburg", "5241&7048");
        DatabaseUtils.citesCoordinates.put("London", "5241&448");

        DatabaseUtils.setAmountOfElementsInDatabase(3);
        DatabaseUtils.setCitiesList("ekb");

        synchronized (mActivityRule) {
            mActivityRule.wait(3000);
        }

        onView(withId(R.id.add_city)).perform(click());

        synchronized (mActivityRule) {
            mActivityRule.wait(3000);
        }

        onView(withId(R.id.city_array)).perform(scrollToPosition(0));
        onView(withId(R.id.city_array)).perform(scrollToPosition(1));
        onView(withId(R.id.city_array)).perform(scrollToPosition(2));

        synchronized (mActivityRule) {
            mActivityRule.wait(1000);
        }

        onView(withId(R.id.fab)).perform(click());

        synchronized (mActivityRule) {
            mActivityRule.wait(1000);
        }

        onView(withId(R.id.autocomplete_places)).perform(typeText("eka"));
        synchronized (mActivityRule) {
            mActivityRule.wait(1000);
        }
        onView(withText("Ekaterinburg"))
                .inRoot(withDecorView(not(is(activity.getWindow().getDecorView()))))
                .check(matches(isDisplayed()));

        onView(withId(R.id.fab)).perform(click());
        closeSoftKeyboard();
    }

    /**
     * The four test
     * @throws Exception
     */
    @Test
    public void clean() throws Exception {
        Context appContext = InstrumentationRegistry.getTargetContext();
        assertEquals("homounikumus1.com.myweatherviewer.mock", appContext.getPackageName());
        Realm.getDefaultInstance().executeTransaction(realm2 -> realm2.deleteAll());
        DatabaseUtils.clean();
    }

    @After
    public void tearDown() throws Exception {
        synchronized (mActivityRule) {
            Intents.release();
        }
        DatabaseUtils.setAmountOfElementsInDatabase(null);
        Provider.setWeatherRepository(null);
    }
}
