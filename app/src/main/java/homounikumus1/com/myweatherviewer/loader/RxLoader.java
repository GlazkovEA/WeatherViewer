package homounikumus1.com.myweatherviewer.loader;

import android.support.annotation.Nullable;
import android.support.v4.content.Loader;
import android.util.Log;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.disposables.Disposable;
import io.reactivex.observers.DisposableObserver;

import static homounikumus1.com.myweatherviewer.WeatherApp.getAppContext;

public class RxLoader<T> extends Loader<T> {
    private static String TAG = "my_loader";
    private Observable<T> observable;
    private Disposable disposable;
    private boolean mIsErrorReported = false;
    private ObservableEmitter<T> mEmitter;
    private boolean mIsCompleted = false;
    @Nullable
    private T mData;
    @Nullable
    private Throwable mError;

    RxLoader(Observable<T> observable) {
        super(getAppContext());
        this.observable = observable;
    }

    @Override
    protected void onStartLoading() {
        Log.d(TAG, "start");
        super.onStartLoading();
        if (mEmitter != null && !mIsCompleted && mError == null)
            disposable = observable.subscribeWith(new Loader());
    }

    @Override
    protected void onReset() {
        Log.d(TAG, "reset");
        if (disposable != null) {
            disposable.dispose();
            disposable = null;
        }

        observable = null;
        mData = null;
        mError = null;
        mIsCompleted = false;
        mIsErrorReported = false;
        mEmitter = null;
        super.onReset();
    }

    Observable<T> createObservable() {
        Log.d(TAG, "create");
        return Observable.create((ObservableEmitter<T> emitter) -> {
            mEmitter = emitter;
           /* mEmitter.setDisposable(disposable);

            mEmitter.setCancellable(() -> {
                if (disposable != null) {
                    disposable.dispose();
                    disposable = null;
                }
            });*/

            if (mData != null) mEmitter.onNext(mData);
            if (mIsCompleted) mEmitter.onComplete();
            else if (mError != null && !mIsErrorReported) {
                mEmitter.onError(mError);
                mIsErrorReported = true;
            }

            if (disposable == null && !mIsCompleted && mError == null)
                disposable = observable.subscribeWith(new Loader());
        });
    }

    private class Loader extends DisposableObserver<T> {
        @Override
        public void onNext(T t) {
            Log.d(TAG, "onNext");
            mData = t;
            if (mEmitter != null) mEmitter.onNext(t);
        }

        @Override
        public void onError(Throwable throwable) {
            Log.d(TAG, "onError");
            mError = throwable;
            if (mEmitter != null) {
                mEmitter.onError(throwable);
                mIsErrorReported = true;
            }
        }

        @Override
        public void onComplete() {
            Log.d(TAG, "onComplete");
            mIsCompleted = true;
            if (mEmitter != null) mEmitter.onComplete();
        }
    }
}