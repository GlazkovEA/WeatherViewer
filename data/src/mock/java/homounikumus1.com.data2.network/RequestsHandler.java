package homounikumus1.com.data2.network;


import android.content.Context;
import android.support.annotation.NonNull;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import homounikumus1.com.data2.repository.Provider;
import okhttp3.Request;
import okhttp3.Response;

public class RequestsHandler {
    /**
     * Store request and path to file - if request equals some of keys load file from asset use value
     */
    private final Map<String, String> mResponsesMap = new HashMap<>();

    public RequestsHandler() {
        mResponsesMap.put("/weather", "one_day.json");
        mResponsesMap.put("/group", "cities_array.json");
        mResponsesMap.put("/forecast", "week.json");
    }

    public boolean shouldIntercept(@NonNull String path) {
        Set<String> keys = mResponsesMap.keySet();
        for (String interceptUrl : keys) {
            if (path.contains(interceptUrl)) {
                return true;
            }
        }
        return false;
    }

    @NonNull
    public Response proceed(@NonNull Request request, @NonNull String path) {
        // for check error cases
        String data = Provider.getWaetherRepository().getData();
        if ("error".equals(data)) {
            return OkHttpResponse.error(request, 400, "Error for path " + path);
        }

        Set<String> keys = mResponsesMap.keySet();

        // if request is correct load mocked request from asset
        for (String interceptUrl : keys) {
            if (path.contains(interceptUrl)) {
                String mockResponsePath = mResponsesMap.get(interceptUrl);
                return createResponseFromAssets(request, mockResponsePath);
            }
        }

        return OkHttpResponse.error(request, 500, "Incorrectly intercepted request");
    }

    /**
     * load answer in JSON format of request is correct
     * @param request - request
     * @param assetPath - path to JSON file in asset
     * @return
     */
    @NonNull
    private Response createResponseFromAssets(@NonNull Request request, @NonNull String assetPath) {
        try {
            try (InputStream stream = context.getAssets().open(assetPath)) {
                return OkHttpResponse.success(request, stream);
            }
        } catch (IOException e) {
            return OkHttpResponse.error(request, 500, e.getMessage());
        }
    }

    /**
     * Don't forget set context for load data from asset
     */
    private static Context context;
    public static void setContext(Context context) {
        RequestsHandler.context = context;
    }

}
