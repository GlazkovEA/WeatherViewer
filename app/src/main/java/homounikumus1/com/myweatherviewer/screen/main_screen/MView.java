package homounikumus1.com.myweatherviewer.screen.main_screen;

import java.util.List;

import homounikumus1.com.data2.model.weather.Weather;
import homounikumus1.com.data2.model.weather.WeekWeather;


public interface MView {

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
     * @param cause - week, or day data didn't loaded
     */
    void showError(int cause);

    /**
     * notify the user about the impossibility of obtaining data
     */
    void showGeoExplanation();

    /**
     * update recycle view
     * @param weathers - data
     */
    void showWeekWeather(List<WeekWeather> weathers);

    /**
     * update mainView
     * @param weather - data
     */
    void showTodayWeather(Weather weather);

    /**
     * start another activity
     */
    void startSearchActivity();
}
