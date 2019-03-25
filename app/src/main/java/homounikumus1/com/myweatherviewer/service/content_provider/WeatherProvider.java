package homounikumus1.com.myweatherviewer.service.content_provider;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

/**
 * We don't need to connect the content provider with any
 * specific database here, because we have access to it through the
 * "Provider" class implemented in the "data" module
 */
public class WeatherProvider extends ContentProvider {

    @Override
    public boolean onCreate() {
        return false;
    }


    @Nullable
    @Override
    public Cursor query( @NonNull Uri uri, @Nullable String[] projection, @Nullable String selection, @Nullable String[] selectionArgs, @Nullable String sortOrder) {
        return null;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        return null;
    }

    @Nullable
    @Override
    public Uri insert( @NonNull Uri uri, @Nullable ContentValues values) {
        return null;
    }

    @Override
    public int delete( @NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {
        return 0;
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues values, @Nullable String selection, @Nullable String[] selectionArgs) {
        return 0;
    }
}
