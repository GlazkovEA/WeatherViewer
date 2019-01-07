package homounikumus1.com.myweatherviewer.model;

import org.json.JSONObject;

public class DataTransfer {
    /**
     * One day weather request listener interface
     */
    public interface TodayChangeListener {
        void onChange(JSONObject jsonObject);
    }

    /**
     * Week weather request listener interface
     */
    public interface WeekChangeListener {
        void onChange(JSONObject jsonObject);
    }

    /**
     * Listener interface for request for getting weather for list of cites
     */
    public interface CityArrayChangeListener {
        void onChange(JSONObject jsonObject);
    }

    /**
     * Time zone listener interface
     */
    public interface TimeZoneChangeListener {
        void onChange(JSONObject jsonObject);
    }
}
