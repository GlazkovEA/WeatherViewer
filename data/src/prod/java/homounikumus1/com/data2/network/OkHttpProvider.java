package homounikumus1.com.data2.network;

import android.support.annotation.NonNull;

import java.io.IOException;

import homounikumus1.com.data2.BuildConfig;
import okhttp3.HttpUrl;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public final class OkHttpProvider {

    private OkHttpProvider() {
    }


    private static volatile OkHttpClient sClient;

    /**
     * OkHttpClient builder
     * return OkHttpClient object, initialize OkHttpClient static variable
     */
    @NonNull
    public static OkHttpClient provideClient() {
        OkHttpClient client = sClient;
        if (client == null) {
            synchronized (ApiFactory.class) {
                client = sClient;
                if (client == null) {
                    client = sClient = buildClient();
                }
            }
        }
        return client;
    }

    public static void recreate() {
        sClient = null;
        sClient = buildClient();
    }

    @NonNull
    private static OkHttpClient buildClient() {
        return new OkHttpClient.Builder()
                // add interceptors
                .addInterceptor(new ApiKeyInterceptor()) //
                .addInterceptor(new ApiKeyTimeInterceptor())
                .build();
    }

    /**
     * Interceptor for WeatherApi
     */
    static class ApiKeyInterceptor implements Interceptor {

        @Override
        public Response intercept(@NonNull Chain chain) throws IOException {
            Request request = chain.request();
            HttpUrl url = request.url().newBuilder()
                    .addQueryParameter("appid", BuildConfig.WEATHER_API_KEY)
                    .build();
            request = request.newBuilder().url(url).build();
            return chain.proceed(request);
        }
    }

    /**
     * Interceptor for TimeApi
     */
    static class ApiKeyTimeInterceptor implements Interceptor {

        @Override
        public Response intercept(@NonNull Chain chain) throws IOException {
            Request request = chain.request();
            HttpUrl url = request.url().newBuilder()
                    .addQueryParameter("key", BuildConfig.TIMEZONE_API_KEY)
                    .build();
            request = request.newBuilder().url(url).build();

            return chain.proceed(request);
        }
    }
}
