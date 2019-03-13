package homounikumus1.com.data2.model;

import android.support.annotation.NonNull;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

import io.realm.RealmModel;
import io.realm.RealmObject;

/**
 * @author Artur Vasilov
 */
public class WeatherLockal implements Serializable {

    @SerializedName("main")
    private String mMain;

    @SerializedName("icon")
    private String mIcon;

    @SerializedName("description")
    private String description;

    public void setDescription(@NonNull String description) {
        this.description = description;
    }

    @NonNull
    public String getDescription() {
        return description;
    }

    @NonNull
    public String getMain() {
        return mMain;
    }

    public void setMain(@NonNull String main) {
        mMain = main;
    }

    @NonNull
    public String getIcon() {
        return mIcon;
    }

    public void setIcon(@NonNull String icon) {
        mIcon = icon;
    }
}
