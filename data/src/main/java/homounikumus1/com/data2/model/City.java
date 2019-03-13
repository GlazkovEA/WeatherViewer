package homounikumus1.com.data2.model;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

/**
 * @author Artur Vasilov
 */
public class City  implements Serializable {
    @SerializedName("coord")
    private Coord coord;

    @SerializedName("timeZoneId")
    private String timeZone;

    @SerializedName("name")
    private String mName;

    @SerializedName("id")
    private String id;

    @SerializedName("weather")
    private List<WeatherLockal> weathers;

    @SerializedName("list")
    private List<City> cities;

    @SerializedName("sys")
    private Sys sys;

    @SerializedName("main")
    private Main mMain;

    @SerializedName("wind")
    private Wind mWind;

    @SerializedName("dt")
    private long dt;

    @NonNull
    public String getName() {
        return mName;
    }

    public void setName(@NonNull String name) {
        mName = name;
    }

    @Nullable
    public WeatherLockal getWeather() {
        if (weathers == null || weathers.isEmpty()) {
            return null;
        }
        return weathers.get(0);
    }

    @Nullable
    public Main getMain() {
        return mMain;
    }

    @Nullable
    public Wind getWind() {
        return mWind;
    }

    @Nullable
    public String getTimeZone() {
        return timeZone;
    }

    public void setTimeZone(String timeZone) {
        this.timeZone = timeZone;
    }

    public long getDt() {
        return dt;
    }

    public String getId() {
        return id;
    }

    public Coord getCoord() {
        return coord;
    }

    @Nullable
    public Sys getSys() {
        return sys;
    }

    public List<City> getCities() {
        if (cities == null || cities.isEmpty()) {
            return null;
        }
        return cities;
    }
}


