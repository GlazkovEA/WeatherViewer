package homounikumus1.com.myweatherviewer.view;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
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
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import java.util.ArrayList;

import homounikumus1.com.myweatherviewer.MVP;
import homounikumus1.com.myweatherviewer.presenter.CityListener;
import homounikumus1.com.myweatherviewer.presenter.Presenter;
import homounikumus1.com.myweatherviewer.R;
import homounikumus1.com.myweatherviewer.WeatherArrayAdapter;
import homounikumus1.com.myweatherviewer.model.Weather;


public class MainActivity extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener, MVP.view, CityListener {
    private static final String TAG = "Main_Action";
    public static boolean WAS_RESTART = false;
    private long mLastClickTime = 0;
    private ArrayList<Weather> weekWeatherList = new ArrayList<>();
    private ArrayList<Weather> savedToadyWeather = new ArrayList<>();
    private ArrayList<Weather> savedWeekWeather = new ArrayList<>();
    private SwipeRefreshLayout swipeRefreshLayout;
    private View mainWeatherView;
    private ActionBar title;
    private RecyclerView weatherListView;
    private WeatherArrayAdapter arrayAdapter;
    private View bar;
    private Presenter presenter;
    private SharedPreferences sPref;

    /**
     * Saved list of cities weather for recreate
     * @param outState saved data
     */
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelableArrayList("today", (ArrayList<Weather>) presenter.getTodayWeatherCash());
        outState.putParcelableArrayList("week", (ArrayList<Weather>) presenter.getWeekWeatherCash());
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // if activity was recreate load data
        if (savedInstanceState != null) {
            savedToadyWeather = savedInstanceState.getParcelableArrayList("today");
            savedWeekWeather = savedInstanceState.getParcelableArrayList("week");
        }
        setContentView(R.layout.activity_main);

        bar = findViewById(R.id.progress_bar);
        ProgressBar progressBar = findViewById(R.id.progress_bar_bar);
        progressBar.getIndeterminateDrawable().setColorFilter(getColor(R.color.colorProgressBar), android.graphics.PorterDuff.Mode.MULTIPLY);

        // check internet connection
        ConnectivityManager cm = (ConnectivityManager) this.getSystemService(Context.CONNECTIVITY_SERVICE);
        assert cm != null;
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        if (netInfo != null && netInfo.isConnectedOrConnecting() || !savedToadyWeather.isEmpty())
            init();
        else
            noConnection(1);
    }

    /**
     * If there is no internet connection when initializing variables,
     * close the application in all other cases, just notify the user
     * @param scenario constant
     */
    private void noConnection(int scenario) {
        switch (scenario) {
            case 1:
                findViewById(R.id.main_weather).setVisibility(View.INVISIBLE);
                Toast.makeText(this, getString(R.string.no_connection), Toast.LENGTH_LONG).show();
                new Handler().postDelayed(this::finish, 3000);
                break;
            case 2:
                Toast.makeText(this, getString(R.string.no_connection), Toast.LENGTH_LONG).show();
                break;
        }
    }


    private void init() {
        // toolbar settings
        Toolbar toolbar = findViewById(R.id.toolbar);
        setTitle("");
        setSupportActionBar(toolbar);

        presenter = new Presenter(this);

        // check the orientation and pass the required
        // position to the LinearLayoutManager to correctly display the recycler view
        LinearLayoutManager layoutManager;
        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
            layoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        } else {
            layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        }

        weatherListView = findViewById(R.id.listView);
        mainWeatherView = findViewById(R.id.main_weather);
        mainWeatherView.setVisibility(View.INVISIBLE);
        arrayAdapter = new WeatherArrayAdapter(this, weekWeatherList);
        weatherListView.setLayoutManager(layoutManager);
        weatherListView.setAdapter(arrayAdapter);
        title = getSupportActionBar();

        // we don't needed to showed the bar because
        // we have all data and no need to update it's
        if (!savedToadyWeather.isEmpty()) {
            bar.setVisibility(View.INVISIBLE);
        }

        swipeRefreshLayout = findViewById(R.id.swipeRefreshLayout);
        swipeRefreshLayout.setOnRefreshListener(this);

        if (savedToadyWeather.isEmpty())
            start();
        else
            new Handler().postDelayed(() -> {
                // if WAS_RESTARTED if false do nothing because the app received data from activityResult
                if (WAS_RESTART) action(Action.RESTART, null, null);
            }, 1);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        // if the user denied permission show explanation
        switch (requestCode) {
            case 1:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED)
                    action(Action.GET_PLACE_FROM_GDATA, null, null);
                else
                    permissionNeeded();
                break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    /**
     * Put the flag in the position "true" if the activity was destroyed
     */
    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "destroy");
        WAS_RESTART = true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        if (isExplanationShowed()) {
            menu.getItem(0).setVisible(false);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // check internet connection
        ConnectivityManager cm = (ConnectivityManager) this.getSystemService(Context.CONNECTIVITY_SERVICE);
        assert cm != null;
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        if (netInfo != null && netInfo.isConnectedOrConnecting()) {
            int id = item.getItemId();
            if (bar.getVisibility()!=View.VISIBLE) {
                switch (id) {
                    case R.id.add_city:
                        //double click prevention
                        if (SystemClock.elapsedRealtime() - mLastClickTime < 1000){
                            break;
                        }
                        mLastClickTime = SystemClock.elapsedRealtime();
                        // saved last city which displayed activity
                        if (!presenter.getTodayWeatherCash().isEmpty())
                            saveCity(presenter.getTodayWeatherCash().get(0).getCity(), presenter.getTodayWeatherCash().get(0).getCoords());
                        Intent intent = new Intent(this, AddCityActivity.class);
                        startActivityForResult(intent, 1);
                        break;
                    case R.id.place:
                        // request permission - if permission wasn't received early show that the device can't get geodata
                        // and do the button invisible
                        if (!isExplanationShowed())
                            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION}, 1);
                        else {
                            Snackbar.make(mainWeatherView, getString(R.string.geodata_apsent), Snackbar.LENGTH_LONG).setAction("Action", null).show();
                            item.setVisible(false);
                        }
                        break;
                }
            }
            return super.onOptionsItemSelected(item);
        } else {
            noConnection(2);
            return false;
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (bar.getVisibility()==View.VISIBLE)
            bar.setVisibility(View.GONE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (data == null) {
            // if result was null and activity was destroyed update view's
            if (getCity() != null&& WAS_RESTART) {
                action(Action.UPDATE, getCity()[1], getCity()[0]);
            }
            return;
        }
        // update view's
        action(Action.UPDATE, data.getStringExtra("result"), data.getStringExtra("city"));
    }

    @Override
    public void onRefresh() {
        // check internet connection
        ConnectivityManager cm = (ConnectivityManager) this.getSystemService(Context.CONNECTIVITY_SERVICE);
        assert cm != null;
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        if (netInfo != null && netInfo.isConnectedOrConnecting()) {
            // update view's
            action(Action.UPDATE, presenter.getTodayWeatherCash().get(0).getCoords(), presenter.getTodayWeatherCash().get(0).getCity());
            new Handler().postDelayed(() -> {
                swipeRefreshLayout.setRefreshing(false);
            }, 1000);
        } else {
            noConnection(2);
            new Handler().postDelayed(() -> {
                swipeRefreshLayout.setRefreshing(false);
            }, 1000);
        }
    }

    /**
     * Check actions constant's
     */
    @Override
    public void start() {
        if (getCity()!=null) {
            action(Action.START_FROM_FULL_DATABASE, null, null);
        } else {
            if (presenter.amountOfElementsInDatabase()==0) {
                action(Action.START_FROM_EMPTY_DATABASE, null, null);
            } else {
                String[] data = presenter.getFirstElementFromDatabase();
                action(Action.UPDATE, data[1], data[0]);
            }
        }
    }

    /**
     * Explain user why the app need it permission
     */
    private void permissionNeeded() {
        new AlertDialog.Builder(this)
                .setTitle(R.string.attention)
                .setMessage(getString(R.string.explanation))
                .setNegativeButtonIcon(getDrawable(R.drawable.ic_cancel_24dp))
                .setNegativeButton("", (dialog, which) -> {
                    explanationShowed(true);
                    start();
                })
                .setPositiveButtonIcon(getDrawable(R.drawable.ic_check_24dp))
                .setPositiveButton("", (dialog, which) -> {
                    explanationShowed(false);
                    ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION}, 1);
                })
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }

    /**
     * Action's
     * @param action some actions constant
     * @param coordinates data which needed update
     * @param city data which needed update
     */
    @Override
    public void action(Action action, String coordinates, String city) {
        switch (action) {
            case GET_PLACE_FROM_GDATA:
                bar.setVisibility(View.VISIBLE);
                Log.d(TAG, "get geo");
                presenter.setCityListener(this);
                presenter.setSetUpLocationListener(this);
                double[] latLon = presenter.getCoordinates();
                new Handler().postDelayed(() -> {
                    if (latLon != null) {
                        String coord = "lat=" + latLon[0] + "&lon=" + latLon[1];
                        presenter.oneDayWeather(getString(R.string.one_day_weather, coord), "", mainWeatherView, title);
                        presenter.thisWeekWeather(getString(R.string.week_weather, coord), weatherListView, arrayAdapter, weekWeatherList, bar);
                        arrayAdapter.notifyDataSetChanged();
                        weatherListView.smoothScrollToPosition(0);
                    } else {
                        Snackbar.make(mainWeatherView, getString(R.string.geodata_apsent), Snackbar.LENGTH_LONG).setAction("Action", null).show();
                    }
                }, 300);

                break;
            case START_FROM_EMPTY_DATABASE:
                Log.d(TAG, "empty start");
                Intent intent = new Intent(this, AddCityActivity.class);
                startActivityForResult(intent, 1);
                break;
            case START_FROM_FULL_DATABASE:
                Log.d(TAG, String.valueOf("start from database " + getCity()!=null));
                if (getCity() != null) {
                    presenter.oneDayWeather(getString(R.string.one_day_weather, getCity()[1]), getCity()[0], mainWeatherView, title);
                    presenter.thisWeekWeather(getString(R.string.week_weather, getCity()[1]), weatherListView, arrayAdapter, weekWeatherList, bar);
                    arrayAdapter.notifyDataSetChanged();
                    weatherListView.smoothScrollToPosition(0);
                }
                break;
            case RESTART:
                Log.d(TAG, "restart");
                presenter.setWeekWeatherCash(savedWeekWeather);
                presenter.setInView(savedToadyWeather.get(0), mainWeatherView, title);
                weekWeatherList.addAll(savedWeekWeather);
                break;
            case UPDATE:
                bar.setVisibility(View.VISIBLE);
                Log.d(TAG, "update = " + city + " " + coordinates);
                WAS_RESTART = false;
                saveCity(city, coordinates);
                presenter.oneDayWeather(getString(R.string.one_day_weather, coordinates), city, mainWeatherView, title);
                presenter.thisWeekWeather(getString(R.string.week_weather, coordinates), weatherListView, arrayAdapter, weekWeatherList, bar);
                break;
        }
    }

    /**
     * Save the last city that displayed activity
     * @param city city name
     * @param coordinates city coordinates
     */
    private void saveCity(String city, String coordinates) {
        if (city!=null) {
            if (!city.equals("") && !city.equals("null")) {
                sPref = getSharedPreferences("citesPref", MODE_PRIVATE);
                String sb = city +
                        "*" +
                        coordinates;
                SharedPreferences.Editor ed = sPref.edit();
                ed.putString("city", sb);
                ed.apply();
            } else
                Log.d(TAG, "getCity = EMPTY");
        } else
            Log.d(TAG, "getCity = EMPTY");
    }

    /**
     * Get the last city that displayed activity
     * @return city
     */
    private String[] getCity() {
        sPref = getSharedPreferences("citesPref", MODE_PRIVATE);
        String data = sPref.getString("city", "");
        @SuppressLint("CommitPrefEdits") SharedPreferences.Editor ed = sPref.edit();
        ed.remove("city");

        if (data.isEmpty() || data.equals(""))
            return null;

        Log.d(TAG, "getCity = " + data);
        String[] cityAndCoordinates = new String[2];
        cityAndCoordinates[0] = data.substring(0, data.indexOf("*"));
        cityAndCoordinates[1] = data.substring(data.indexOf("*") + 1);

        return cityAndCoordinates;

    }

    /**
     * Save that the explanation of permission's is showed
     * @param exp boolean
     */
    private void explanationShowed(boolean exp) {
        sPref = getPreferences(MODE_PRIVATE);
        SharedPreferences.Editor ed = sPref.edit();
        ed.putBoolean("explanation", exp);
        ed.apply();

    }

    /**
     * Check was the explanation of permission's is showed
     * @return boolean
     */
    private boolean isExplanationShowed() {
        sPref = getPreferences(MODE_PRIVATE);
        return sPref.getBoolean("explanation", false);

    }

    /**
     * For loading from data which app get from geolocation
     * @param i city
     * @param c coordinates
     */
    @Override
    public void onCityReady(String i, String c) {
        saveCity(i, c);
    }
}