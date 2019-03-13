package homounikumus1.com.data2.repository;

import android.support.annotation.NonNull;

import homounikumus1.com.data2.model.TimeZone;
import io.reactivex.Observable;

public interface TimeZoneInterface {
    @NonNull
    Observable<TimeZone> getTimeZone(String data);
}
