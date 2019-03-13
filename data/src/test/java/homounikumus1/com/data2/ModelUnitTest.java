package homounikumus1.com.data2;

import org.junit.Test;

import homounikumus1.com.data2.model.weather.CitiesArrayWeather;
import homounikumus1.com.data2.model.weather.Weather;
import homounikumus1.com.data2.model.weather.WeekWeather;

import static org.junit.Assert.*;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ModelUnitTest {
    @Test
    public void addition_isCorrect() {
        assertEquals(4, 2 + 2);
    }

    @Test
    public void oneDayModelTest() {
        Weather weather = new Weather("1", 1000000L, "London",
                "good","1.icon", 10d, 1000d,
                100d,1d,1060000L, 12000000L,
                1,1d,1d,"Europe/London"
        );

        assertEquals("12 January, Monday", weather.getDayOfWeek());
        assertEquals(12, weather.getDate().intValue());
        assertEquals("London", weather.getCity());
        assertEquals("good", weather.getDescription());
        assertEquals("1.icon.png", weather.getIconURL());
        assertEquals("100%", weather.getHumidity());
        assertEquals("07:26", weather.getSunrise());
        assertEquals("22:20", weather.getSunset());
        assertEquals("Europe/London", weather.getTimeZone());
        assertEquals("10°C", weather.getTemp());
    }

    @Test
    public void weekModelTest() {
        WeekWeather weather = new WeekWeather(1000000L,10d,
                100d,"good", "1.icon", "Europe/London");

        assertEquals("12 January, Mon \n" + " 14:46", weather.getDayOfWeek());
        assertEquals("good", weather.getDescription());
        assertEquals("1.icon.png", weather.getIconURL());
        assertEquals("100%", weather.getHumidity());
        assertEquals("10°C", weather.getTemp());
    }

    @Test
    public void cityListModelTest() {
        CitiesArrayWeather weather = new CitiesArrayWeather("1","London","good",10, "1.icon", 1d,1d, "Europe/London");

        assertEquals("London", weather.getCity());
        assertEquals("good", weather.getDescription());
        assertEquals("1.icon.png", weather.getIconURL());
        assertEquals("Europe/London", weather.getTimeZone());
        assertEquals("10°C", weather.getTemp());
    }
}