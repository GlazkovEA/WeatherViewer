package homounikumus1.com.data2.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

import io.realm.RealmObject;

public class Coord implements Serializable {
    @SerializedName("lat")
    double lat;
    @SerializedName("lon")
    double lon;

    public double getLat() {
        return lat;
    }

    public double getLon() {
        return lon;
    }
}
