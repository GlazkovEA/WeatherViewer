package homounikumus1.com.data2.repository;

public class Provider {
    /**
     * Link for access to weatherRepository
     */
    private static WeatherRepository weatherRepository;

    /**
     * Link for access to timeZone Repository
     */
    private static TimeZoneRepository timeZoneRepository;

    /**
     * If necessary (for example, when testing) we can replace the implementation of repositories
     * use methods below for set our realisation on get repositories instance
     */
    public static TimeZoneRepository getTimeZoneRepository () {
        if (timeZoneRepository==null)
            timeZoneRepository = new TimeZoneRepository();
        return timeZoneRepository;
    }


    public static WeatherRepository getWaetherRepository () {
        if (weatherRepository==null)
            weatherRepository = new WeatherRepository();
        return weatherRepository;
    }

    public static void setWeatherRepository (WeatherRepository repository) {
        weatherRepository = repository;
    }

    public static void setTimeZoneRepository (TimeZoneRepository repository) {
        timeZoneRepository = repository;
    }
}
