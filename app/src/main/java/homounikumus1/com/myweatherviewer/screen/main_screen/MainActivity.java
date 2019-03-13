package homounikumus1.com.myweatherviewer.screen.main_screen;

import android.Manifest;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import homounikumus1.com.data2.model.weather.Weather;
import homounikumus1.com.data2.model.weather.WeekWeather;
import homounikumus1.com.myweatherviewer.loader.LifecycleHandler;
import homounikumus1.com.myweatherviewer.loader.LoaderLifecycleHandler;
import homounikumus1.com.myweatherviewer.utils.DatabaseUtils;
import homounikumus1.com.myweatherviewer.R;
import homounikumus1.com.myweatherviewer.screen.cities_list_screen.AddCityActivity;
import homounikumus1.com.myweatherviewer.utils.LoadImageUtils;


public class MainActivity extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener, MView {
    private static final String TAG = "Main_Action";
    /**
     * Double click prevention variables
     */
    private long mLastClickTime = 0;
    private long mLastGeoClickTime = 0;
    /**
     * Snackbars for alert user when the app start from database
     */
    private Snackbar snackbar;
    private Snackbar snackbarWeek;
    /**
     * The list for recyclerView
     */
    private ArrayList<WeekWeather> weekWeatherList = new ArrayList<>();
    @BindView(R.id.main_description_fragment)
    public View main_description_fragment;
    @BindView(R.id.add_to_main_weather)
    public View add_to_main_weather;
    @BindView(R.id.week_error)
    public TextView weekError;
    @BindView(R.id.one_day_error)
    public TextView oneDayError;
    @BindView(R.id.city)
    public TextView city;
    @BindView(R.id.main_icon)
    public ImageView icon;
    @BindView(R.id.main_description)
    public TextView mainDesc;
    @BindView(R.id.main_temp)
    public TextView mainTmp;
    @BindView(R.id.main_pressure)
    public TextView press;
    @BindView(R.id.main_humidity)
    public TextView humidity;
    @BindView(R.id.main_wind)
    public TextView mainWind;
    @BindView(R.id.main_sunrise)
    public TextView sunrise;
    @BindView(R.id.main_sunset)
    public TextView sunset;
    @BindView(R.id.swipeRefreshLayout)
    public SwipeRefreshLayout swipeRefreshLayout;
    @BindView(R.id.progress_bar_bar)
    public ProgressBar progressBar;
    @BindView(R.id.main_weather)
    public View mainWeatherView;
    @BindView(R.id.listView)
    public RecyclerView weatherListView;
    private WeatherArrayAdapter arrayAdapter;
    @BindView(R.id.progress_bar)
    public View bar;
    /**
     * Delegate
     */
    private MainPresenter presenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        progressBar.getIndeterminateDrawable().setColorFilter(getColor(R.color.colorProgressBar), android.graphics.PorterDuff.Mode.MULTIPLY);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setTitle("");
        setSupportActionBar(toolbar);

        // check the orientation and pass the required
        // position to the LinearLayoutManager to correctly display the recycler view
        LinearLayoutManager layoutManager;
        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT)
            layoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        else
            layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);

        arrayAdapter = new WeatherArrayAdapter(weekWeatherList);
        weatherListView.setLayoutManager(layoutManager);
        weatherListView.setAdapter(arrayAdapter);

        swipeRefreshLayout.setOnRefreshListener(this);

        //Handler for hanle android life cycle
        LifecycleHandler handler = LoaderLifecycleHandler.create(getSupportLoaderManager());
        //Initialize delegate
        presenter = new MainPresenter(this, handler);
        presenter.init(this.getString(R.string.lang));
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        // if the user denied permission show explanation
        switch (requestCode) {
            case 1:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                    Log.d(TAG, "get place");
                    presenter.getPlace(this.getString(R.string.lang));
                } else
                    permissionNeeded();
                break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        //if explanation was showed and user denied it - hide menu item
        if (DatabaseUtils.isExplanationShowed(this)) {
            menu.getItem(0).setVisible(false);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.add_city:
                //double click prevention
                if (SystemClock.elapsedRealtime() - mLastClickTime < 1000) {
                    break;
                }
                mLastClickTime = SystemClock.elapsedRealtime();
                Intent intent = new Intent(this, AddCityActivity.class);
                startActivityForResult(intent, 1);
                break;
            case R.id.place:
                //double click prevention
                if (SystemClock.elapsedRealtime() - mLastGeoClickTime < 1000) {
                    break;
                }
                mLastGeoClickTime = SystemClock.elapsedRealtime();
                // request permission - if permission wasn't received early show that the device can't get geodata
                // and do the button invisible
                ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
                assert cm != null;
                NetworkInfo netInfo = cm.getActiveNetworkInfo();
                if (netInfo != null && netInfo.isConnectedOrConnecting()) {
                    if (!DatabaseUtils.isExplanationShowed(this))
                        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION}, 1);
                    else {
                        Snackbar.make(mainWeatherView, getString(R.string.geodata_apsent), Snackbar.LENGTH_LONG).setAction("Action", null).show();
                        item.setVisible(false);
                    }
                } else {
                    Toast.makeText(this, getString(R.string.no_connection), Toast.LENGTH_LONG).show();
                }
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (data == null) {
            return;
        }
        presenter.update(data.getDoubleExtra("lat", 0), data.getDoubleExtra("lon", 0), data.getStringExtra("city"), data.getStringExtra("time"), this.getString(R.string.lang));
    }

    @Override
    public void onRefresh() {
        // check internet connection
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        assert cm != null;
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        if (netInfo != null && netInfo.isConnectedOrConnecting()) {
            //update data if internet connection is present
            presenter.start(true, this.getString(R.string.lang));
            new Handler().postDelayed(() -> {
                swipeRefreshLayout.setRefreshing(false);
            }, 1000);
        } else {
            Toast.makeText(this, getString(R.string.no_connection), Toast.LENGTH_LONG).show();
            new Handler().postDelayed(() -> {
                swipeRefreshLayout.setRefreshing(false);
            }, 1000);
        }
    }

    /**
     * Explain user why the app need it permission
     */
    private void permissionNeeded() {
        new AlertDialog.Builder(this)
                .setTitle(R.string.attention)
                .setMessage(getString(R.string.explanation))
                .setNegativeButtonIcon(getDrawable(R.drawable.ic_check_24dp))
                .setNegativeButton("", (dialog, which) -> {
                    DatabaseUtils.explanationShowed(this, true);
                    presenter.init(this.getString(R.string.lang));
                })
                .setPositiveButtonIcon(getDrawable(R.drawable.ic_cancel_24dp))
                .setPositiveButton("", (dialog, which) -> {
                    DatabaseUtils.explanationShowed(this, false);
                    ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION}, 1);
                })
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }

    @Override
    public void showLoading() {
        bar.setVisibility(View.VISIBLE);
    }

    @Override
    public void hideLoading() {
        bar.setVisibility(View.INVISIBLE);
    }

    @Override
    public void showError(int cause) {
        bar.setVisibility(View.INVISIBLE);
        if (cause == 0) {
            weekError.setVisibility(View.VISIBLE);
            weatherListView.setVisibility(View.INVISIBLE);
        } else {
            mainWeatherView.setVisibility(View.VISIBLE);
            oneDayError.setVisibility(View.VISIBLE);
            add_to_main_weather.setVisibility(View.INVISIBLE);
            main_description_fragment.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    public void showGeoExplanation() {
        Snackbar.make(mainWeatherView, getString(R.string.geodata_apsent), Snackbar.LENGTH_LONG).setAction("Action", null).show();
        bar.setVisibility(View.INVISIBLE);
    }

    @Override
    public void showWeekWeather(List<WeekWeather> weathers) {
        weekError.setVisibility(View.INVISIBLE);
        weatherListView.setVisibility(View.VISIBLE);
        mainWeatherView.setVisibility(View.VISIBLE);
        if (weathers.size() > 0) {
            if (weathers.get(0).isEx() && snackbar == null) {
                snackbarWeek = Snackbar.make(weatherListView, this.getString(R.string.week_data_absent), Snackbar.LENGTH_INDEFINITE);
                snackbarWeek.setAction("OK", snackbarOnClickListener).show();
            } else {
                if (snackbarWeek != null) {
                    snackbarWeek.dismiss();
                    snackbarWeek = null;
                }
            }
        }
        weekWeatherList.clear();
        weekWeatherList.addAll(weathers);

        if (arrayAdapter != null && weatherListView != null) {
            arrayAdapter.notifyDataSetChanged();
            weatherListView.smoothScrollToPosition(0);
            bar.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    public void showTodayWeather(Weather weather) {
        oneDayError.setVisibility(View.INVISIBLE);
        if (weather.isEx() && snackbarWeek == null) {
            snackbar = Snackbar.make(weatherListView, this.getString(R.string.day_data_absent), Snackbar.LENGTH_INDEFINITE);
            snackbar.setAction("OK", snackbarOnClickListener)
                    .show();
        } else {
            if (snackbar != null) {
                snackbar.dismiss();
                snackbar = null;
            }
        }
        mainWeatherView.setVisibility(View.VISIBLE);
        add_to_main_weather.setVisibility(View.VISIBLE);
        main_description_fragment.setVisibility(View.VISIBLE);
        Objects.requireNonNull(getSupportActionBar()).setTitle(weather.getDayOfWeek());
        city.setText(weather.getCity());
        LoadImageUtils.LoadImage(icon, weather.getIconURL());
        mainDesc.setText(weather.getDescription());
        mainTmp.setText(weather.getTemp());
        press.setText(weather.getPressure());
        humidity.setText(weather.getHumidity());
        mainWind.setText(weather.getWind_speed());
        sunrise.setText(weather.getSunrise());
        sunset.setText(weather.getSunset());
    }

    @Override
    public void startSearchActivity() {
        Intent intent = new Intent(this, AddCityActivity.class);
        startActivityForResult(intent, 1);
    }

    /**
     * explanation for snackbar button
     */
    View.OnClickListener snackbarOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            Dialog dialog = new Dialog(MainActivity.this);
            dialog.setContentView(R.layout.dialog);
            dialog.show();
        }
    };

}