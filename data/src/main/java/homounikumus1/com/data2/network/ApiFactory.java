package homounikumus1.com.data2.network;

import android.support.annotation.NonNull;

import homounikumus1.com.data2.BuildConfig;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;

public final class ApiFactory {
    /**
     * Service for getting weather
     */
    private static volatile Service sService;

    /**
     * Service for getting time zone
     */
    private static volatile Service tService;

    private ApiFactory() {}

    /**
     * Weather service builder
     * @return service object, initialize sService variable
     */
    @NonNull
    public static Service getWeatherService() {
        Service service = sService;
        if (service == null) {
            synchronized (ApiFactory.class) {
                service = sService;
                if (service == null) {
                    service = sService = buildRetrofit().create(Service.class);

                }
            }
        }
        return service;
    }

    /**
     * TimeZone service builder
     * @return service object, initialize tService variable
     */
    @NonNull
    public static Service getTimeService() {
        Service service = tService;
        if (service == null) {
            synchronized (ApiFactory.class) {
                service = tService;
                if (service == null) {
                    service = tService = buildTRetrofit().create(Service.class);

                }
            }
        }
        return service;
    }

    /**
     * Build retrofit object
     * @return retrofit object
     */
    @NonNull
    private static Retrofit buildTRetrofit() {
        return new Retrofit.Builder()
                .baseUrl(BuildConfig.TIMEZONE_API_ENDPOINT) //base URL from gradle file
                .client(OkHttpProvider.provideClient()) // set OkHttp client
                .addConverterFactory(GsonConverterFactory.create()) // add converter
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create()) // add adapter for work with rxjava2
                .build();
    }

    @NonNull
    private static Retrofit buildRetrofit() {
        return new Retrofit.Builder()
                .baseUrl(BuildConfig.WEATHER_API_ENDPOINT)
                .client(OkHttpProvider.provideClient())
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .build();
    }

    public static void recreate() {
        OkHttpProvider.recreate();
        sService = buildRetrofit().create(Service.class);
        tService = buildTRetrofit().create(Service.class);
    }
}
