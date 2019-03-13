package homounikumus1.com.myweatherviewer;

import android.support.annotation.NonNull;

import homounikumus1.com.myweatherviewer.loader.LifecycleHandler;
import io.reactivex.Observable;
import io.reactivex.ObservableTransformer;

public class MockLifeCycleHandler implements LifecycleHandler {
    @NonNull
    @Override
    public <T> ObservableTransformer<T, T> choice(boolean update, int id) {
        return observable -> observable;
    }

    @NonNull
    @Override
    public <T> ObservableTransformer<T, T> load(int id) {
        return observable -> observable;
    }

    @NonNull
    @Override
    public <T> ObservableTransformer<T, T> reload(int id) {
        return observable -> observable;
    }

    @Override
    public void clear(int id) {

    }
}
