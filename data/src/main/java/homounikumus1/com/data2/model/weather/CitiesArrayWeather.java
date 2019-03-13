package homounikumus1.com.data2.model.weather;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;

import homounikumus1.com.model.Model;
import io.realm.RealmObject;

public class CitiesArrayWeather extends RealmObject implements Parcelable, Comparable<CitiesArrayWeather>{
    private String id;
    private String city;
    private String timeZone;
    private String description;
    private String temp;
    private double lat;
    private double lon;
    private String iconURL;
    private boolean isSelected = false;
    private boolean isEx = false;

    public void setEx(boolean ex) {
        isEx = ex;
    }

    public boolean isEx() {
        return isEx;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public void setTimeZone(String timeZone) {
        this.timeZone = timeZone;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setTemp(String temp) {
        this.temp = temp;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public void setLon(double lon) {
        this.lon = lon;
    }

    public void setIconURL(String iconURL) {
        this.iconURL = iconURL;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public String getCity() {
        return city;
    }

    public String getTimeZone() {
        return timeZone;
    }

    public String getDescription() {
        return description;
    }

    public String getTemp() {
        return temp;
    }

    public double getLat() {
        return lat;
    }

    public double getLon() {
        return lon;
    }

    public String getIconURL() {
        return iconURL;
    }

    public CitiesArrayWeather() {}

    public CitiesArrayWeather(String id, String city, String description, double temp, String iconName, double lat, double lon, String timeZone) {
        Model model = new Model(id, city, description, temp, iconName, lat, lon, timeZone);

        this.id = model.getId();
        this.city = model.getCity();
        this.timeZone = model.getTimeZone();
        this.description = model.getDescription();
        this.temp = model.getTemp();
        this.lat = model.getLat();
        this.lon = model.getLon();
        this.iconURL = model.getIconURL();
    }

    /**
     * Implementing the Parcelable interface to save data or,
     * if necessary, transfer to other activity
     * and the Comparable interface to work with Collections
     */
    public static final Creator<CitiesArrayWeather> CREATOR = new Creator<CitiesArrayWeather>() {
        public CitiesArrayWeather createFromParcel(Parcel in) {
            return new CitiesArrayWeather(in);
        }

        public CitiesArrayWeather[] newArray(int size) {
            return new CitiesArrayWeather[size];
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
    }

    private CitiesArrayWeather(Parcel parcel) {
        city = parcel.readString();
        description = parcel.readString();
        temp = parcel.readString();
        iconURL = parcel.readString();
    }

    @Override
    public int compareTo(@NonNull CitiesArrayWeather weather) {
        return this.getCity().compareTo(weather.getCity());
    }

}
