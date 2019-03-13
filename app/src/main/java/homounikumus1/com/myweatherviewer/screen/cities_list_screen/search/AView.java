package homounikumus1.com.myweatherviewer.screen.cities_list_screen.search;

public interface AView {
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
     * @param throwable - if needed the cause
     */
    void showError(Throwable throwable);

    /**
     *
     * @param placeName - city name
     * @param lat - latitude
     * @param lon - longitude
     * @param timeZone - time zone
     */
    void cityReady(String placeName, double lat, double lon, String timeZone);
}
