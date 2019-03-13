package homounikumus1.com.data2.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

import io.realm.RealmObject;

public class TimeZone extends RealmObject implements Serializable {

    @SerializedName("timeZoneId")
    private String timeZone;

    public String getTimeZone() {
        return timeZone;
    }


    public TimeZone() {
    }
}
