package homounikumus1.com.myweatherviewer.presenter;


public interface CityListener {
    /**
     * @param i city
     * @param c coordinates
     */
    void onCityReady(String i, String c);
}
