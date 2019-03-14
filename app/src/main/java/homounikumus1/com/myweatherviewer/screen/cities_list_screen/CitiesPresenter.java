package homounikumus1.com.myweatherviewer.screen.cities_list_screen;

import java.util.Objects;

import homounikumus1.com.data2.model.weather.CitiesArrayWeather;
import homounikumus1.com.data2.repository.Provider;
import homounikumus1.com.data2.repository.StartRepository;
import homounikumus1.com.myweatherviewer.utils.DatabaseUtils;
import io.reactivex.disposables.Disposable;

public class CitiesPresenter {
    private CLView clView;
    public String data;
    private String lang;

    public CitiesPresenter(CLView clView, String lang) {
        this.clView = clView;
        this.lang = lang;
    }

    public void init() {
        // get the string with all saved cities id's
        data = DatabaseUtils.loadCitesWeather();
        final Disposable subscribe = Provider.getStartRepository().getCityArray()
                .subscribe(w -> {
                    for (CitiesArrayWeather city : w) {
                        String id = city.getId();
                        // take coordinates assigned to this city and previously stored in the database
                        // that's why the weather's API data not correlated with googleAPI data
                        String[] latLon = Objects.requireNonNull(DatabaseUtils.citesCoordinates.get(DatabaseUtils.cites.get(id))).split("&");

                        city.setCity(DatabaseUtils.cites.get(id));
                        city.setLat(Double.parseDouble(latLon[0]));
                        city.setLon(Double.parseDouble(latLon[1]));
                        city.setTimeZone(DatabaseUtils.timeZoneMap.get(id));
                    }
                    clView.showCitiesWeather(w);
                }, throwable -> {
                });
    }

    public void loadCitesWeather() {
        Disposable disposable = Provider.getWaetherRepository().getCityArray(data, lang)
                .doOnSubscribe(disposable1 -> {
                    clView.showLoading();
                })
                .doOnTerminate(clView::hideLoading)
                //.compose(handler.choice(false, R.id.cities))
                .subscribe(citiesArrayWeathers -> {
                    for (CitiesArrayWeather city : citiesArrayWeathers) {
                        String id = city.getId();
                        // take coordinates assigned to this city and previously stored in the database
                        // that's why the weather's API data not correlated with googleAPI data
                        String[] latLon = Objects.requireNonNull(DatabaseUtils.citesCoordinates.get(DatabaseUtils.cites.get(id))).split("&");

                        city.setCity(DatabaseUtils.cites.get(id));
                        city.setLat(Double.parseDouble(latLon[0]));
                        city.setLon(Double.parseDouble(latLon[1]));
                        city.setTimeZone(DatabaseUtils.timeZoneMap.get(id));
                    }
                    clView.showCitiesWeather(citiesArrayWeathers);
                }, throwable -> {
                    clView.showError();
                });
    }
}
