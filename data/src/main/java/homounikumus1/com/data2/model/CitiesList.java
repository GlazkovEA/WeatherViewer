package homounikumus1.com.data2.model;

import android.support.annotation.NonNull;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class CitiesList {
    @SerializedName("list")
    private List<City> cityList;

    @NonNull
    public List<City> getCities() {
        if (cityList == null) {
            return new ArrayList<>();
        }
        return cityList;
    }
}
