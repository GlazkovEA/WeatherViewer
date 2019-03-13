package homounikumus1.com.model;

import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

public class Model {
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

    /**
     * Constructor for getting one day weather
     *
     * @param time        time in long
     * @param city        city name
     * @param description weather description
     * @param iconName    icon name for URL
     * @param temp        temperature
     * @param pressure    pressure
     * @param humidity    humidity
     * @param wind_speed  wind speed
     * @param sunrise     sunrise in long
     * @param sunset      sunset in long
     * @param cityID      cityID by weatherAPI
     * @param lat         latitude
     * @param lon         longitude
     * @param timeZoneID  time zone
     */
    public Model(String id, long time, String city, String description, String iconName, double temp, double pressure, double humidity,
                 double wind_speed, long sunrise, long sunset, long cityID, double lat, double lon, String timeZoneID) {

        TimeZone timeZone = null;
        if (timeZoneID != null && !timeZoneID.equals("") && !timeZoneID.equals("null")) {
            // get the time zone for the date in the requested place
            // and the right time of sunrise and sunset
            timeZone = TimeZone.getTimeZone(timeZoneID);
        }

        // formatted the date for the requested place
        SimpleDateFormat date = new SimpleDateFormat("d MMMM, EEEE");
        if (timeZone!=null)
            date.setTimeZone(timeZone);
        // formatted time for the sunset and the sunrise for the requested place
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm");
        if (timeZone!=null)
            simpleDateFormat.setTimeZone(timeZone);
        // get NumberFormat object for formatted data fro humidity and temperature
        NumberFormat numberFormat = NumberFormat.getInstance();
        numberFormat.setMaximumFractionDigits(0);

        this.id = id;
        this.lat = lat;
        this.lon = lon;
        this.city = city;
        this.description = description;
        this.iconURL = iconName+".png";
        this.temp = numberFormat.format(temp) + "\u00B0C";
        this.pressure = String.valueOf(pressure) + "mm Hg";
        this.humidity = NumberFormat.getPercentInstance().format(humidity / 100.0);
        this.wind_speed = String.valueOf(wind_speed) + "m/s";
        this.sunrise = simpleDateFormat.format(convertTimeToCalendar(sunrise).getTime());
        this.sunset = simpleDateFormat.format(convertTimeToCalendar(sunset).getTime());
        this.dayOfWeek = date.format(convertTimeToCalendar(time).getTime());
        this.date = convertTimeToCalendar(time).getTime().getDate();
        this.timeZone = timeZoneID;
    }

    /**
     * Constructor for getting week weather
     *
     * @param timeStamp   time in long
     * @param temp        temperature
     * @param humidity    humidity
     * @param description weather description
     * @param iconName    icon name for URL
     * @param timeZoneID  time zone
     */
    public Model (long timeStamp, double temp, double humidity, String description, String iconName, String timeZoneID) {
        TimeZone timeZone = null;
        if (timeZoneID != null) {
            // get the time zone for the date in the requested place
            // and the right time of sunrise and sunset
            timeZone = TimeZone.getTimeZone(timeZoneID);
        }
        // get NumberFormat object for formatted data fro humidity and temperature
        NumberFormat numberFormat = NumberFormat.getInstance();
        numberFormat.setMaximumFractionDigits(0);
        // formatted time for requested place
        Integer date = convertTimeToCalendar(timeStamp).getTime().getDate();
        SimpleDateFormat simpleDateFormat;
        Date today = new Date();
        Integer todayInt = today.getDate();
        if (date.equals(todayInt)) {
            simpleDateFormat = new SimpleDateFormat("HH:mm");
        } else {
            simpleDateFormat = new SimpleDateFormat("d MMMM, EEE '\n' HH:mm");
        }

        if (timeZone != null)
            simpleDateFormat.setTimeZone(timeZone);

        this.description = description;
        this.iconURL = iconName+".png";
        this.temp = numberFormat.format(temp) + "\u00B0C";
        this.dayOfWeek = simpleDateFormat.format(convertTimeToCalendar(timeStamp).getTime());
        this.humidity = NumberFormat.getPercentInstance().format(humidity / 100.0);
    }

    public Model(String id, String city, String description, double temp, String iconName, double lat, double lon, String timeZone) {
        NumberFormat numberFormat = NumberFormat.getInstance();
        numberFormat.setMaximumFractionDigits(0);
        this.id = id;
        this.city = city;
        this.timeZone = timeZone;
        this.description = description;
        this.temp = numberFormat.format(temp) + "\u00B0C";
        this.lat = lat;
        this.lon = lon;
        this.iconURL = iconName+".png";
    }

    public String getId() {
        return id;
    }

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

    /**
     * Convert timestamp to calendar
     *
     * @param timeStamp time in long
     * @return calendar object
     */
    private Calendar convertTimeToCalendar(long timeStamp) {
        Calendar calendar;
        calendar = Calendar.getInstance();
        calendar.setTimeInMillis(timeStamp * 1000);
        calendar.add(Calendar.MILLISECOND, 0);
        return calendar;
    }
}
