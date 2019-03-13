package homounikumus1.com.myweatherviewer.loader;

import android.support.annotation.NonNull;

import io.reactivex.ObservableTransformer;

public interface LifecycleHandler {

    /**
     * To differentiate load and reload methods calls
     * @param update - load or reload
     * @param id - unique identifier for request on Activity / Fragment
     */
    @NonNull
    <T> ObservableTransformer<T, T> choice(boolean update, int id);

    /**
     * Create loader
     * @param id - unique identifier for request on Activity / Fragment
     */
    @NonNull
    <T> ObservableTransformer<T, T> load(int id);


    /**
     * Reload loader
     * @param id - unique identifier for request on Activity / Fragment
     */
    @NonNull
    <T> ObservableTransformer<T, T> reload(int id);

    /**
     * Clears subscriptions and destroys observable for the request with specified id
     * @param id - unique identifier for request on Activity / Fragment
     */
    void clear(int id);
}
