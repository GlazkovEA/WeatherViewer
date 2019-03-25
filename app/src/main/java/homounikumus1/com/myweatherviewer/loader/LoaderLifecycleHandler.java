package homounikumus1.com.myweatherviewer.loader;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.util.Log;

import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.ObservableTransformer;

public class LoaderLifecycleHandler implements LifecycleHandler {
    private final LoaderManager mLoaderManager;

    private LoaderLifecycleHandler(@NonNull LoaderManager loaderManager) {
        mLoaderManager = loaderManager;
    }

    /**
     * New instance of life cycle handler
     * @param loaderManager - loader manager
     */
    @NonNull
    public static LifecycleHandler create(@NonNull LoaderManager loaderManager) {
        return new LoaderLifecycleHandler(loaderManager);
    }

    @NonNull
    @Override
    public <T> ObservableTransformer<T, T> choice(boolean update, int id) {
        if (update) {
            return reload(id);
        } else {
            return load(id);
        }
    }

    @NonNull
    @Override
    public <T> ObservableTransformer<T, T> load(int id) {
        return new ObservableTransformer<T, T>() {
            @Override
            public ObservableSource<T> apply(Observable<T> upstream) {
                if (mLoaderManager.getLoader(id) == null) {
                    mLoaderManager.initLoader(id, Bundle.EMPTY, new RxLoaderCallbacks<T>(upstream));
                }
                RxLoader<T> loader = (RxLoader<T>) mLoaderManager.getLoader(id);
                return loader != null ? loader.createObservable() : null;
            }
        };
    }

    @NonNull
    @Override
    public <T> ObservableTransformer<T, T> reload(int id) {
        return new ObservableTransformer<T, T>() {
            @Override
            public ObservableSource<T> apply(Observable<T> upstream) {
                if (mLoaderManager.getLoader(id) != null) {
                    mLoaderManager.destroyLoader(id);
                }
                mLoaderManager.initLoader(id, Bundle.EMPTY, new RxLoaderCallbacks<>(upstream));
                RxLoader<T> loader = (RxLoader<T>) mLoaderManager.getLoader(id);
                return loader != null ? loader.createObservable() : null;
            }
        };
    }

    @Override
    public void clear(int id) {
        mLoaderManager.destroyLoader(id);
    }

    private class RxLoaderCallbacks<D> implements LoaderManager.LoaderCallbacks<D> {

        private final Observable<D> mObservable;

        public RxLoaderCallbacks(@NonNull Observable<D> observable) {
            mObservable = observable;
        }

        @NonNull
        @Override
        public Loader<D> onCreateLoader(int id, Bundle args) {
            return new RxLoader<>(mObservable);
        }

        @Override
        public void onLoadFinished(Loader<D> loader, D data) {
            // Do nothing
        }

        @Override
        public void onLoaderReset(Loader<D> loader) {
            // Do nothing
        }
    }
}
