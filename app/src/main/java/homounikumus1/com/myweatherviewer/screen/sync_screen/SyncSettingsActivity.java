package homounikumus1.com.myweatherviewer.screen.sync_screen;

import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import java.util.Objects;

import homounikumus1.com.myweatherviewer.R;
import homounikumus1.com.myweatherviewer.utils.DatabaseUtils;


public class SyncSettingsActivity extends AppCompatActivity {
    private static GeoListener geo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings);
        // add action bar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case 1:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                    // in case that the geolocation explanation was showed and the geolocation button was off
                    DatabaseUtils.explanationShowed(false);
                    if (geo!=null)
                        geo.onClick(true);
                } else {
                    if (geo != null)
                        geo.onClick(false);
                }
                break;
            default: {
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
            }
        }
    }

    public static void setGeoListener(GeoListener geoListener) {
        geo = geoListener;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    interface GeoListener {
        void onClick(boolean result);

    }
}