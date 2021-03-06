package homounikumus1.com.data2.network;


import android.support.annotation.NonNull;

import okhttp3.OkHttpClient;

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
                // add interceptor
                .addInterceptor(MockingInterceptor.create())
                .build();
    }

}
