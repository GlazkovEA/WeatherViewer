package homounikumus1.com.data2.model.weather;

import homounikumus1.com.model.Model;
import io.realm.RealmObject;

public class WeekWeather extends RealmObject {
    private String iconURL;
    private String dayOfWeek;
    private String temp;
    private String humidity;
    private String description;
    private boolean isEx = false;

    public WeekWeather() {}

    public WeekWeather (long timeStamp, double temp, double humidity, String description, String iconName, String timeZoneID) {
        Model model = new Model(timeStamp, temp, humidity, description, iconName,timeZoneID);
        this.description = model.getDescription();
        this.iconURL = model.getIconURL();
        this.temp = model.getTemp();
        this.dayOfWeek = model.getDayOfWeek();
        this.humidity = model.getHumidity();
    }

    public String getIconURL() {
        return iconURL;
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

    public void setEx(boolean ex) {
        isEx = ex;
    }

    public boolean isEx() {
        return isEx;
    }
}
