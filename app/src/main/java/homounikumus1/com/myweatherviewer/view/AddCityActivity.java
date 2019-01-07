package homounikumus1.com.myweatherviewer.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AutoCompleteTextView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.location.places.AutocompleteFilter;
import com.google.android.gms.location.places.GeoDataClient;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.location.places.PlacesOptions;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Objects;

import homounikumus1.com.myweatherviewer.MVP;
import homounikumus1.com.myweatherviewer.presenter.CitesListListener;
import homounikumus1.com.myweatherviewer.presenter.CityListener;
import homounikumus1.com.myweatherviewer.presenter.Presenter;
import homounikumus1.com.myweatherviewer.R;
import homounikumus1.com.myweatherviewer.model.Weather;
import homounikumus1.com.myweatherviewer.CityArrayAdapter;
import homounikumus1.com.myweatherviewer.presenter.PlacePresenter;

import static homounikumus1.com.myweatherviewer.view.MainActivity.WAS_RESTART;


public class AddCityActivity extends AppCompatActivity implements View.OnClickListener, MVP.view, CityListener, CitesListListener {
    private static final String TAG = "Add_Action";
    private ArrayList<Weather> weatherList = new ArrayList<>();
    private long mLastClickTime = 0;
    private boolean NO_CITY = false;
    private boolean IS_OPEN = false;
    private RecyclerView cityArray;
    private View searchWindow;
    private AutoCompleteTextView mAutocompleteView;
    private FloatingActionButton fab;
    private PlacePresenter placePresenter;
    private View bar;

    /**
     * Saved list of cities weather for recreate
     * @param outState saved data
     */
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelableArrayList("array_list", weatherList);
        outState.putBoolean("isOpen", IS_OPEN);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        GeoDataClient mGeoDataClient = Places.getGeoDataClient(this, null);
        super.onCreate(savedInstanceState);

        // if activity was recreate load data
        if (savedInstanceState != null) {
            weatherList.addAll(savedInstanceState.getParcelableArrayList("array_list"));
            IS_OPEN = savedInstanceState.getBoolean("isOpen");
        }

        setContentView(R.layout.activity_add_city);

        bar = findViewById(R.id.progress_bar);
        ProgressBar progressBar = findViewById(R.id.progress_bar_bar);
        progressBar.getIndeterminateDrawable().setColorFilter(getColor(R.color.colorProgressBar), android.graphics.PorterDuff.Mode.MULTIPLY);
        // we don't needed to showed the bar because
        // we have all data and no need to update it's
        if (!weatherList.isEmpty()) {
            bar.setVisibility(View.INVISIBLE);
        }

        Presenter presenter = new Presenter(this);
        // init cityArrayAdapter when cities list where ready
        presenter.setCitesListListener(this);
        // if cities list wasn't loaded load
        // if it was update presenterCash
        if (weatherList.isEmpty()) {
            weatherList = presenter.loadCitesWeather(bar);
        } else {
            presenter.getCitesArrayCash().clear();
            presenter.getCitesArrayCash().addAll(weatherList);
        }

        cityArray = findViewById(R.id.city_array);
        cityArray.setLayoutManager(new LinearLayoutManager(this));
        // setting toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        Objects.requireNonNull(getSupportActionBar()).setHomeButtonEnabled(true);
        toolbar.setNavigationOnClickListener((view) -> {
            onBackPressed();
        });

        searchWindow = findViewById(R.id.search_window);
        searchWindow.setVisibility(View.INVISIBLE);

        mAutocompleteView = searchWindow.findViewById(R.id.autocomplete_places);

        placePresenter = new PlacePresenter(this, mGeoDataClient, mAutocompleteView, this);
        mAutocompleteView.setOnItemClickListener(placePresenter.getAutocompleteClickListener());

        fab = findViewById(R.id.fab);
        fab.setOnClickListener(this);

        start();
    }

    /**
     * Check actions constant's
     */
    @Override
    public void start() {
        // if cities list was loaded - RESTART
        if (!weatherList.isEmpty()) {
            action(Action.RESTART, null, null);
        } else {
            // if cities list wasn't loaded and database is empty - EMPTY_LIST
            Presenter presenter = new Presenter(this);
            if (presenter.amountOfElementsInDatabase() == 0) {
                action(Action.EMPTY_LIST, null, null);
            } else {
                // if cities list wasn't loaded and database isn't empty - FULL_LIST
                action(Action.FULL_LIST, null, null);
            }
        }
    }

    @Override
    public void onBackPressed() {
        // if search window is open - close it
        if (searchWindow.getVisibility() == View.INVISIBLE || searchWindow.getVisibility() == View.GONE) {
            // if main activity was'n loaded any city notify the user that the user must to choose a city
            if (!NO_CITY) {
                // check internet connection, if all ok - setResult and finish
                ConnectivityManager cm = (ConnectivityManager) this.getSystemService(Context.CONNECTIVITY_SERVICE);
                assert cm != null;
                NetworkInfo netInfo = cm.getActiveNetworkInfo();
                if ((netInfo != null && netInfo.isConnectedOrConnecting()) || !WAS_RESTART) {
                    super.onBackPressed();
                    setResult(RESULT_CANCELED);
                    finish();
                } else
                    Toast.makeText(this, getString(R.string.no_connection), Toast.LENGTH_LONG).show();

            } else {
                Toast.makeText(this, R.string.need_city, Toast.LENGTH_SHORT).show();
            }
        } else {
            action(Action.CLOSE_SEARCH, null, null);
        }
    }

    /**
     * If the window is open - close, if it is closed - open
     * @param view fab
     */
    @Override
    public void onClick(View view) {
        //double click prevention
        if (SystemClock.elapsedRealtime() - mLastClickTime < 200){
            return;
        }
        mLastClickTime = SystemClock.elapsedRealtime();
        switch (view.getId()) {
            case R.id.fab:
                if (searchWindow.getVisibility() == View.INVISIBLE || searchWindow.getVisibility() == View.GONE) {
                    action(Action.OPEN_SEARCH, null, null);
                } else {
                    action(Action.CLOSE_SEARCH, null, null);
                }
                break;
        }
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
            case EMPTY_LIST:
                bar.setVisibility(View.INVISIBLE);
                Log.d(TAG, "start with empty list ");
                // if the shared preferences contains no data
                if (getCity() == null)
                    NO_CITY = true;
                // open keyboard and search window
                mAutocompleteView.requestFocus();
                mAutocompleteView.postDelayed(() -> {
                    InputMethodManager keyboard = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    assert keyboard != null;
                    keyboard.showSoftInput(mAutocompleteView, 0);
                }, 200);
                placePresenter.animationOpen(searchWindow, fab);

                break;
            case FULL_LIST:
                // if the shared preferences contains no data
                if (getCity() == null)
                    NO_CITY = true;
                Log.d(TAG, "start with full list");
                break;
            case OPEN_SEARCH:
                bar.setVisibility(View.INVISIBLE);
                // open keyboard and search window
                Log.d(TAG, "open search");
                IS_OPEN = true;
                mAutocompleteView.postDelayed(() -> {
                    mAutocompleteView.requestFocus();
                    InputMethodManager keyboard = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    assert keyboard != null;
                    keyboard.showSoftInput(mAutocompleteView, 0);
                }, 200);
                placePresenter.animationOpen(searchWindow, fab);
                break;
            case CLOSE_SEARCH:
                // close keyboard and search window
                Log.d(TAG, "close search");
                IS_OPEN = false;
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                assert imm != null;
                imm.hideSoftInputFromWindow(searchWindow.getWindowToken(), 0);

                placePresenter.animationClose(searchWindow, fab);
                break;
            case RESTART:
                // if activity was recreate - update view's
                Log.d(TAG, "restart");
                CityArrayAdapter cityArrayAdapter = new CityArrayAdapter(this, this, weatherList);
                cityArray.setAdapter(cityArrayAdapter);
                if (IS_OPEN) {
                    placePresenter.animationOpen(searchWindow, fab);
                }
                break;
        }
    }

    @Override
    public void onCityReady(String i, String c) {
        ConnectivityManager cm = (ConnectivityManager) this.getSystemService(Context.CONNECTIVITY_SERVICE);
        assert cm != null;
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        // check internet connection
        if (netInfo != null && netInfo.isConnectedOrConnecting()) {
            // check that the data base already consist 10 cities or not
            if (i != null) {
                // setResult
                Intent intent = new Intent();
                intent.putExtra("city", i);
                intent.putExtra("result", c);
                setResult(RESULT_OK, intent);
                finish();
            } else {
                action(Action.CLOSE_SEARCH, null, null);
                Toast.makeText(this, getString(R.string.database_explanation), Toast.LENGTH_LONG).show();
            }
        } else
            Toast.makeText(this, getString(R.string.no_connection), Toast.LENGTH_LONG).show();

    }

    /**
     * Create cityArrayAdapter when cities list where ready
     */
    @Override
    public void onCityListReady() {
        CityArrayAdapter cityArrayAdapter = new CityArrayAdapter(this, this, weatherList);
        cityArray.setAdapter(cityArrayAdapter);
    }

    /**
     * Check that the shared preferences consist data or not
     * @return city
     */
    private String getCity() {
        SharedPreferences sPref = getSharedPreferences("citesPref", MODE_PRIVATE);
        String data = sPref.getString("city", "");
        if (data.equals(""))
            return null;

        return data;
    }
}
