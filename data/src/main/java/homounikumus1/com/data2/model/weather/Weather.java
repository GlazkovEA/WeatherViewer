package homounikumus1.com.data2.model.weather;

import android.support.annotation.NonNull;

import homounikumus1.com.model.Model;
import io.realm.RealmObject;

public class Weather extends RealmObject {
    private String id;
    private String city;
    private double lat;
    private double lon;
    private String pressure;
    private String wind_speed;
    private String sunrise;
    private String sunset;
    private String iconURL;
    private Integer date;
    private String dayOfWeek;
    private String temp;
    private String humidity;
    private String description;
    private String timeZone;
    private boolean isEx = false;

    public void setEx(boolean ex) {
        isEx = ex;
    }

    public boolean isEx() {
        return isEx;
    }

    public Weather() {}

    public Weather(String id, long time, String city, String description, String iconName, double temp, double pressure, double humidity,
                   double wind_speed, long sunrise, long sunset, long cityID, double lat, double lon, String timeZoneID) {

        Model model = new Model(id, time, city, description, iconName, temp, pressure,humidity, wind_speed, sunrise, sunset, cityID, lat, lon, timeZoneID);
        this.id = model.getId();
        this.lat = model.getLat();
        this.lon = model.getLon();
        this.city = model.getCity();
        this.description = model.getDescription();
        this.iconURL = model.getIconURL();
        this.temp = model.getTemp();
        this.pressure = model.getPressure();
        this.humidity = model.getHumidity();
        this.wind_speed = model.getWind_speed();
        this.sunrise = model.getSunrise();
        this.sunset = model.getSunset();
        this.dayOfWeek = model.getDayOfWeek();
        this.date = model.getDate();
        this.timeZone = model.getTimeZone();
    }

    public String getId() {
        return id;
    }

    @NonNull
    public String getCity() {
        return city;
    }

    public String getDayOfWeek() {
        return dayOfWeek;
    }

    public String getTemp() {
        return temp;
    }

    public String getHumidity() {
        return humidity;
    }

    public String getDescription() {
        return description;
    }

    public String getIconURL() {
        return iconURL;
    }

    public Integer getDate() {
        return date;
    }

    public String getPressure() {
        return pressure;
    }

    public String getWind_speed() {
        return wind_speed;
    }

    public String getSunrise() {
        return sunrise;
    }

    public String getSunset() {
        return sunset;
    }

    public String getTimeZone() { return timeZone; }

    public double getLat() {
        return lat;
    }

    public double getLon() {
        return lon;
    }

    public void setTimeZone(String timeZone) {
        this.timeZone = timeZone;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public void setLon(double lon) {
        this.lon = lon;
    }

}
