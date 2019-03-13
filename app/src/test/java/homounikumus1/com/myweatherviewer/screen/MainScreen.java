package homounikumus1.com.myweatherviewer.screen;


import android.support.annotation.NonNull;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mockito.Mockito;


import java.util.ArrayList;
import java.util.List;

import homounikumus1.com.data2.model.weather.Weather;
import homounikumus1.com.data2.model.weather.WeekWeather;
import homounikumus1.com.data2.repository.Provider;
import homounikumus1.com.data2.repository.WeatherRepository;
import homounikumus1.com.myweatherviewer.MockLifeCycleHandler;
import homounikumus1.com.myweatherviewer.screen.main_screen.MView;
import homounikumus1.com.myweatherviewer.screen.main_screen.MainPresenter;
import homounikumus1.com.myweatherviewer.utils.DatabaseUtils;
import homounikumus1.com.myweatherviewer.utils.LocationUtils;
import io.reactivex.Observable;

import static junit.framework.Assert.assertNotNull;
import static org.mockito.Mockito.times;

@RunWith(JUnit4.class)
public class MainScreen {
    private MView mView;
    private MainPresenter presenter;

    @Before
    public void setUp() throws Exception {
        mView = Mockito.mock(MView.class);
        MockLifeCycleHandler handler = new MockLifeCycleHandler();
        presenter = new MainPresenter(mView, handler);
    }

    @Test
    public void testCreated() throws Exception {
        assertNotNull(presenter);

    }

    @Test
    public void testNoActionsWithView() throws Exception {
        Mockito.verifyNoMoreInteractions(mView);
    }

    @Test
    public void testInitEmptyDatabase () throws Exception {
        DatabaseUtils.setAmountOfElementsInDatabase(0);
        presenter.init("ru");
        Mockito.verify(mView).startSearchActivity();
    }

    @Test
    public void testInitFullDatabase () throws Exception {
        Weather weather = new Weather();
        List<WeekWeather> weathers = new ArrayList<>();
        DatabaseUtils.setAmountOfElementsInDatabase(10);
        WeatherRepository weatherRepository = new TestWeatherRepository(weather, weathers);
        Provider.setWeatherRepository(weatherRepository);
        presenter.init("ru");

        Mockito.verify(mView, times(1)).showTodayWeather(weather);
        Mockito.verify(mView, times(1)).showWeekWeather(weathers);
        Mockito.verify(mView, times(1)).showLoading();
        Mockito.verify(mView, times(1)).hideLoading();
        Mockito.verify(mView, times(0)).showError(0);

        Mockito.verifyNoMoreInteractions(mView);
    }

    @Test
    public void testStartCommandWithNoSavedObject () throws Exception {
        Weather weather = new Weather();
        List<WeekWeather> weathers = new ArrayList<>();

        DatabaseUtils.setAmountOfElementsInDatabase(0);
        presenter.start(false, "ru");
        Mockito.verifyNoMoreInteractions(mView);

        Mockito.verify(mView, times(0)).showTodayWeather(weather);
        Mockito.verify(mView, times(0)).showWeekWeather(weathers);
        Mockito.verify(mView, times(0)).showLoading();
        Mockito.verify(mView, times(0)).hideLoading();
        Mockito.verify(mView, times(0)).showError(0);

        Mockito.verifyNoMoreInteractions(mView);
    }

    @Test
    public void testStartCommandWithSavedObject () throws Exception {
        Weather weather = new Weather();
        List<WeekWeather> weathers = new ArrayList<>();
        DatabaseUtils.setAmountOfElementsInDatabase(10);
        WeatherRepository weatherRepository = new TestWeatherRepository(weather, weathers);
        Provider.setWeatherRepository(weatherRepository);
        presenter.start(false, "ru");

        Mockito.verify(mView, times(1)).showTodayWeather(weather);
        Mockito.verify(mView, times(1)).showWeekWeather(weathers);
        Mockito.verify(mView, times(1)).showLoading();
        Mockito.verify(mView, times(1)).hideLoading();
        Mockito.verify(mView, times(0)).showError(0);

        Mockito.verifyNoMoreInteractions(mView);
    }

    @Test
    public void fullTest () throws Exception {
        Weather weather = new Weather();
        List<WeekWeather> weathers = new ArrayList<>();
        DatabaseUtils.setAmountOfElementsInDatabase(10);
        WeatherRepository weatherRepository = new TestWeatherRepository(weather, weathers);
        Provider.setWeatherRepository(weatherRepository);
        presenter.init("ru");
        presenter.update(0,0,"EKB","", "ru");

        Mockito.verify(mView, times(2)).showTodayWeather(weather);
        Mockito.verify(mView, times(2)).showWeekWeather(weathers);
        Mockito.verify(mView, times(2)).showLoading();
        Mockito.verify(mView, times(2)).hideLoading();
        Mockito.verify(mView, times(0)).showError(0);

        Mockito.verifyNoMoreInteractions(mView);
    }

    @Test
    public void fullTestWithException () throws Exception {
        DatabaseUtils.setAmountOfElementsInDatabase(10);
        WeatherRepository weatherRepository = new TestWeatherRepository(null, null);
        Provider.setWeatherRepository(weatherRepository);
        presenter.init("ru");
        presenter.update(0,0,"EKB","", "ru");

        Mockito.verify(mView, times(0)).showTodayWeather(null);
        Mockito.verify(mView, times(0)).showWeekWeather(null);
        Mockito.verify(mView, times(2)).showLoading();
        Mockito.verify(mView, times(2)).hideLoading();
        Mockito.verify(mView, times(2)).showError(0);
        Mockito.verify(mView, times(2)).showError(1);

        Mockito.verifyNoMoreInteractions(mView);
    }

    @Test
    public void fullTestWithNullTodaysDataOneTime () throws Exception {
        DatabaseUtils.setAmountOfElementsInDatabase(10);
        WeatherRepository weatherRepository = new TestWeatherRepository(null, new ArrayList<>());
        Provider.setWeatherRepository(weatherRepository);
        presenter.init("ru");

        WeatherRepository weatherRepository2 = new TestWeatherRepository(new Weather(), new ArrayList<>());
        Provider.setWeatherRepository(weatherRepository2);
        presenter.update(0,0,"EKB","", "ru");

        if (((TestWeatherRepository) weatherRepository).weather!=null)
            Mockito.verify(mView, times(1)).showTodayWeather(new Weather());
        Mockito.verify(mView, times(2)).showWeekWeather(new ArrayList<>());
        Mockito.verify(mView, times(2)).showLoading();
        Mockito.verify(mView, times(2)).hideLoading();
        Mockito.verify(mView, times(1)).showError(1);

    }

    @Test
    public void getPlaceFromFullGeodata () {
        Weather weather = new Weather();
        List<WeekWeather> weathers = new ArrayList<>();
        DatabaseUtils.setAmountOfElementsInDatabase(10);
        LocationUtils.setIsMock(true);
        WeatherRepository weatherRepository = new TestWeatherRepository(weather, weathers);
        Provider.setWeatherRepository(weatherRepository);
        presenter.init("ru");
        LocationUtils.setCoordinates(new double[]{1,2});
        presenter.getPlace("ru");

        Mockito.verify(mView, times(2)).showTodayWeather(weather);
        Mockito.verify(mView, times(2)).showWeekWeather(weathers);
        Mockito.verify(mView, times(2)).showLoading();
        Mockito.verify(mView, times(2)).hideLoading();
        Mockito.verify(mView, times(0)).showError(0);
        Mockito.verify(mView, times(0)).showGeoExplanation();

        Mockito.verifyNoMoreInteractions(mView);
    }

    @Test
    public void getPlaceFromEmptyGeodata () {
        Weather weather = new Weather();
        List<WeekWeather> weathers = new ArrayList<>();
        LocationUtils.setIsMock(true);
        DatabaseUtils.setAmountOfElementsInDatabase(10);
        WeatherRepository weatherRepository = new TestWeatherRepository(weather, weathers);
        Provider.setWeatherRepository(weatherRepository);
        presenter.init("ru");
        presenter.getPlace("ru");
        LocationUtils.setCoordinates(null);

        Mockito.verify(mView, times(1)).showLoading();
        Mockito.verify(mView, times(1)).hideLoading();
        Mockito.verify(mView, times(0)).showError(0);
        Mockito.verify(mView, times(1)).showTodayWeather(weather);
        Mockito.verify(mView, times(1)).showWeekWeather(weathers);
        Mockito.verify(mView, times(1)).showGeoExplanation();

        Mockito.verifyNoMoreInteractions(mView);
    }

    @After
    public void tearDown() throws Exception {
        Provider.setWeatherRepository(null);
        Provider.setTimeZoneRepository(null);
        DatabaseUtils.setAmountOfElementsInDatabase(null);
        LocationUtils.setCoordinates(null);
        LocationUtils.setIsMock(false);
        DatabaseUtils.setCitiesList(null);
    }


    class TestWeatherRepository extends WeatherRepository {
        private Weather weather;
        private List<WeekWeather> weekWeathers;

        public TestWeatherRepository(Weather weather, List<WeekWeather> weekWeathers) {
            this.weather = weather;
            this.weekWeathers = weekWeathers;
        }


        @NonNull
        @Override
        public Observable<List<WeekWeather>> getWeekWeather(double lat, double lon, String timeZone, String lang) {
            if (weekWeathers!=null)
                return Observable.just(weekWeathers);
            else
                return Observable.error(new Exception());
        }

        @NonNull
        @Override
        public Observable<Weather> getOneDayWeather(double lat, double lon, String cityName, String timeZone, String lang) {
            if (weather!=null)
                return Observable.just(weather);
            else
                return Observable.error(new Exception());
        }
    }
}
