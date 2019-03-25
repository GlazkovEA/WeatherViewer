package homounikumus1.com.myweatherviewer.screen;

import android.support.annotation.NonNull;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mockito.Mockito;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import homounikumus1.com.data2.model.weather.CitiesArrayWeather;
import homounikumus1.com.data2.repository.Provider;
import homounikumus1.com.data2.repository.WeatherRepository;
import homounikumus1.com.myweatherviewer.MockLifeCycleHandler;
import homounikumus1.com.myweatherviewer.screen.cities_list_screen.CitiesPresenter;
import homounikumus1.com.myweatherviewer.screen.cities_list_screen.CLView;
import homounikumus1.com.myweatherviewer.utils.DatabaseUtils;
import io.reactivex.Observable;

import static junit.framework.Assert.assertNotNull;
import static org.mockito.Mockito.times;

@RunWith(JUnit4.class)
public class CitiesScreen {
    private CLView clView;
    private CitiesPresenter presenter;

    @Before
    public void setUp() throws Exception {
        clView = Mockito.mock(CLView.class);
        MockLifeCycleHandler handler = new MockLifeCycleHandler();
        presenter = new CitiesPresenter(clView, "ru");
    }

    @Test
    public void testCreated() throws Exception {
        assertNotNull(presenter);

    }

    @Test
    public void testNoActionsWithView() throws Exception {
        Mockito.verifyNoMoreInteractions(clView);
    }

    @Test
    public void testInitEmptyDatabase () throws Exception {
        WeatherRepository repository = new TestWeatherRepository(new ArrayList<>());
        Provider.setWeatherRepository(repository);
        DatabaseUtils.setCitiesList("");
       // presenter.init();
        presenter.loadCitesWeather();
        Mockito.verify(clView, times(0)).showCitiesWeather(null);
        Mockito.verify(clView, times(1)).showLoading();
        Mockito.verify(clView, times(1)).hideLoading();
        Mockito.verify(clView, times(0)).showError();
    }

    @Test
    public void testInitFullDatabase () throws Exception {
        WeatherRepository repository = new TestWeatherRepository(new ArrayList<>());
        Provider.setWeatherRepository(repository);
        DatabaseUtils.setCitiesList("EKB");
       // presenter.init();
        presenter.loadCitesWeather();
        Mockito.verify(clView, times(1)).showCitiesWeather(new ArrayList<>());
        Mockito.verify(clView, times(1)).showLoading();
        Mockito.verify(clView, times(1)).hideLoading();
        Mockito.verify(clView, times(0)).showError();

        Mockito.verifyNoMoreInteractions(clView);
    }

    @Test
    public void fullTestWithException () throws Exception {
        WeatherRepository repository = new TestWeatherRepository(null);
        Provider.setWeatherRepository(repository);
        DatabaseUtils.setCitiesList("EKB");
        //presenter.init();
        presenter.loadCitesWeather();
        Mockito.verify(clView, times(0)).showCitiesWeather(new ArrayList<>());
        Mockito.verify(clView, times(1)).showLoading();
        Mockito.verify(clView, times(1)).hideLoading();
        Mockito.verify(clView, times(1)).showError();

        Mockito.verifyNoMoreInteractions(clView);
    }

    @After
    public void tearDown() throws Exception {
        Provider.setWeatherRepository(null);
        Provider.setTimeZoneRepository(null);
        DatabaseUtils.setAmountOfElementsInDatabase(0);
        DatabaseUtils.setCitiesList(null);
    }


    class TestWeatherRepository extends WeatherRepository {
        private List<CitiesArrayWeather> list;

        public TestWeatherRepository(List<CitiesArrayWeather> citiesArrayWeathers) {
            this.list = citiesArrayWeathers;
        }


        @NonNull
        @Override
        public Observable<List<CitiesArrayWeather>> getCityArray(String data, String lang) {
            if (list!=null)
                return Observable.just(list);
            else
                return Observable.error(new IOException());
        }
    }
}
