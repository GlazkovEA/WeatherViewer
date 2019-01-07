package homounikumus1.com.myweatherviewer.model;

import android.annotation.SuppressLint;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;

import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

public class Weather implements Parcelable, Comparable<Weather> {
    private String city;
    private String dayOfWeek;
    private String temp;
    private String humidity;
    private String description;
    private String iconURL;
    private Integer date;
    private String pressure;
    private String wind_speed;
    private String sunrise;
    private String sunset;
    private String coords;
    private boolean isSelected = false;

    /**
     * Constructor for getting weather for list of cities
     * @param city city name
     * @param description weather description
     * @param temp temperature
     * @param iconName icon name for URL
     * @param coords city coordinates
     */
    public Weather(String city, String description, double temp, String iconName, String coords) {
        // get NumberFormat object for formatted data fro humidity and temperature
        NumberFormat numberFormat = NumberFormat.getInstance();
        numberFormat.setMaximumFractionDigits(0);
        this.city = city;
        this.description = description;
        this.temp = numberFormat.format(temp) + "\u00B0C";
        this.coords = coords;
        this.iconURL = "http://openweathermap.org/img/w/" + iconName + ".png";
        date = 0;
    }

    /**
     * Constructor for getting week weather
     * @param timeStamp time in long
     * @param temp temperature
     * @param humidity humidity
     * @param description weather description
     * @param iconName icon name for URL
     * @param timeZoneID time zone
     */
    @SuppressLint("SimpleDateFormat")
    public Weather(long timeStamp, double temp, double humidity, String description, String iconName, String timeZoneID) {
        // get the time zone for the date in the requested place
        // and the right time of sunrise and sunset
        TimeZone timeZone = TimeZone.getTimeZone(timeZoneID);
        // get NumberFormat object for formatted data fro humidity and temperature
        NumberFormat numberFormat = NumberFormat.getInstance();
        numberFormat.setMaximumFractionDigits(0);
        // formatted time for requested place
        this.date = convertTimeToCalendar(timeStamp).getTime().getDate();
        @SuppressLint("SimpleDateFormat") SimpleDateFormat simpleDateFormat;
        Date today = new Date();
        Integer todayInt = today.getDate();
        if (date.equals(todayInt)) {
            simpleDateFormat = new SimpleDateFormat("HH:mm");
        } else {
            simpleDateFormat = new SimpleDateFormat("d MMMM, EEE '\n' HH:mm");
        }
        simpleDateFormat.setTimeZone(timeZone);

        this.description = description;
        this.iconURL = "http://openweathermap.org/img/w/" + iconName + ".png";
        this.temp = numberFormat.format(temp) + "\u00B0C";
        this.dayOfWeek = simpleDateFormat.format(convertTimeToCalendar(timeStamp).getTime());
        this.humidity = NumberFormat.getPercentInstance().format(humidity / 100.0);

    }

    /**
     * Constructor for getting one day weather
     * @param time time in long
     * @param city city name
     * @param description weather description
     * @param iconName icon name for URL
     * @param temp temperature
     * @param pressure pressure
     * @param humidity humidity
     * @param wind_speed wind speed
     * @param sunrise sunrise in long
     * @param sunset sunset in long
     * @param cityID cityID by weatherAPI
     * @param lat latitude
     * @param lon longitude
     * @param timeZoneID time zone
     */
    public Weather(long time, String city, String description, String iconName, double temp, double pressure, double humidity,
                   double wind_speed, long sunrise, long sunset, long cityID, double lat, double lon, String timeZoneID) {
        // get the time zone for the date in the requested place
        // and the right time of sunrise and sunset
        TimeZone timeZone = TimeZone.getTimeZone(timeZoneID);
        // formatted the date for the requested place
        @SuppressLint("SimpleDateFormat") SimpleDateFormat date = new SimpleDateFormat("d MMMM, EEEE");
        date.setTimeZone(timeZone);
        // formatted time for the sunset and the sunrise for the requested place
        @SuppressLint("SimpleDateFormat") SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm");
        simpleDateFormat.setTimeZone(timeZone);
        // get NumberFormat object for formatted data fro humidity and temperature
        NumberFormat numberFormat = NumberFormat.getInstance();
        numberFormat.setMaximumFractionDigits(0);

        this.coords = "lat=" + lat + "&lon=" + lon;
        this.city = city;
        this.description = description;
        this.iconURL = "http://openweathermap.org/img/w/" + iconName + ".png";
        this.temp = numberFormat.format(temp) + "\u00B0C";
        this.pressure = String.valueOf(pressure)+"mm Hg";
        this.humidity = NumberFormat.getPercentInstance().format(humidity / 100.0);
        this.wind_speed = String.valueOf(wind_speed)+"m/s";
        this.sunrise = simpleDateFormat.format(convertTimeToCalendar(sunrise).getTime());
        this.sunset = simpleDateFormat.format(convertTimeToCalendar(sunset).getTime());
        this.dayOfWeek = date.format(convertTimeToCalendar(time).getTime());
        this.date = convertTimeToCalendar(time).getTime().getDate();
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

    public String getCoords() {
        return coords;
    }

    public final void setSelected(boolean selected) {
        isSelected = selected;
    }

    public boolean isSelected() {
        return isSelected;
    }

    /**
     * Convert timestamp to calendar
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

    /**
     * Implementing the Parcelable interface to save data or,
     * if necessary, transfer to other activity
     * and the Comparable interface to work with Collections
     */
    public static final Parcelable.Creator<Weather> CREATOR = new Parcelable.Creator<Weather>() {
        public Weather createFromParcel(Parcel in) {
            return new Weather(in);
        }

        public Weather[] newArray(int size) {
            return new Weather[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(city);
        parcel.writeString(description);
        parcel.writeString(temp);
        parcel.writeString(iconURL);
        parcel.writeString(coords);
        parcel.writeString(pressure);
        parcel.writeString(humidity);
        parcel.writeString(wind_speed);
        parcel.writeString(sunrise);
        parcel.writeString(sunrise);
        parcel.writeString(dayOfWeek);
    }

    private Weather(Parcel parcel) {
        city = parcel.readString();
        description = parcel.readString();
        temp = parcel.readString();
        iconURL = parcel.readString();
        coords = parcel.readString();
        pressure = parcel.readString();
        humidity = parcel.readString();
        wind_speed = parcel.readString();
        sunrise = parcel.readString();
        sunset = parcel.readString();
        dayOfWeek = parcel.readString();
        NumberFormat numberFormat = NumberFormat.getInstance();
        numberFormat.setMaximumFractionDigits(0);
    }

    @Override
    public int compareTo(@NonNull Weather weather) {
        return this.getCity().compareTo(weather.getCity());
    }
}
