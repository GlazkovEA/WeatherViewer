package homounikumus1.com.myweatherviewer.screen.cities_list_screen;

import java.util.List;

import homounikumus1.com.data2.model.weather.CitiesArrayWeather;


public interface CLView {
    /**
     * show progress bar
     */
    void showLoading();

    /**
     * hide progress bar
     */
    void hideLoading();

    /**
     * show error view
     */
    void showError();

    /**
     * update recycle view
     * @param weathers - data
     */
    void showCitiesWeather(List<CitiesArrayWeather> weathers);
}
