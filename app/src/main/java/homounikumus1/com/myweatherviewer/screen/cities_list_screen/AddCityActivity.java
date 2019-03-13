package homounikumus1.com.myweatherviewer.screen.cities_list_screen;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AutoCompleteTextView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.places.GeoDataClient;
import com.google.android.gms.location.places.Places;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import homounikumus1.com.data2.model.weather.CitiesArrayWeather;
import homounikumus1.com.myweatherviewer.loader.LifecycleHandler;
import homounikumus1.com.myweatherviewer.loader.LoaderLifecycleHandler;

import homounikumus1.com.myweatherviewer.R;
import homounikumus1.com.myweatherviewer.screen.cities_list_screen.search.AView;
import homounikumus1.com.myweatherviewer.screen.cities_list_screen.search.PlacePresenter;
import homounikumus1.com.myweatherviewer.utils.DatabaseUtils;


public class AddCityActivity extends AppCompatActivity implements View.OnClickListener, CLView, AView {
    private static final String TAG = "Add_Action";
    @BindView(R.id.cities_error)
    public TextView citiesError;
    @BindView(R.id.progress_bar_bar)
    public ProgressBar progressBar;
    @BindView(R.id.city_array)
    public RecyclerView cityArray;
    @BindView(R.id.fab)
    public FloatingActionButton fab;
    @BindView(R.id.progress_bar)
    public View bar;
    /**
     * The list for recyclerView
     */
    private ArrayList<CitiesArrayWeather> weatherList = new ArrayList<>();
    /**
     * Double click prevention variable
     */
    private long mLastClickTime = 0;
    /**
     * Variable for check is search window was open when the activity was destroyed
     */
    private boolean IS_OPEN = false;
    /**
     * Search window view variable
     */
    @BindView(R.id.search_window)
    public View searchWindow;
    /**
     * Text field with auto-completion and the ability to edit the entered text
     */
    @BindView(R.id.autocomplete_places)
    public AutoCompleteTextView mAutocompleteView;
    /**
     * Adapter for recyclerView
     */
    private CityArrayAdapter cityArrayAdapter;
    /**
     * The delegate
     */
    private PlacePresenter placePresenter;

    /**
     * Saved list of cities weather for recreate
     *
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
            weatherList.addAll(Objects.requireNonNull(savedInstanceState.getParcelableArrayList("array_list")));
            IS_OPEN = savedInstanceState.getBoolean("isOpen");
        }

        setContentView(R.layout.activity_add_city);
        ButterKnife.bind(this);

        progressBar.getIndeterminateDrawable().setColorFilter(getColor(R.color.colorProgressBar), android.graphics.PorterDuff.Mode.MULTIPLY);
        // we don't needed to showed the bar because
        // we have all data and no need to update it's
        if (!weatherList.isEmpty()) {
            bar.setVisibility(View.INVISIBLE);
        }
        // if cities list wasn't loaded load
        // if it was update presenterCash
        cityArray.setLayoutManager(new LinearLayoutManager(this));
        cityArrayAdapter = new CityArrayAdapter(this, weatherList);
        cityArray.setAdapter(cityArrayAdapter);

        // setting toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        Objects.requireNonNull(getSupportActionBar()).setHomeButtonEnabled(true);
        toolbar.setNavigationOnClickListener((view) -> {
            onBackPressed();
        });

        LifecycleHandler handler = LoaderLifecycleHandler.create(getSupportLoaderManager());
        // The delegate for search cities view
        placePresenter = new PlacePresenter(this, handler, mGeoDataClient, mAutocompleteView);
        placePresenter.init();
        // The delegate for this screen
        CitiesPresenter citiesPresenter = new CitiesPresenter(this, this.getString(R.string.lang));
        citiesPresenter.init();

        mAutocompleteView.setOnItemClickListener(placePresenter.getAutocompleteClickListener());
        fab.setOnClickListener(this);

        if (!weatherList.isEmpty()) {
            if (IS_OPEN) openSearch();
        } else {
            if (DatabaseUtils.amountOfElementsInDatabase() == 0) openSearch();
            else {
                if (DatabaseUtils.checkData()) citiesPresenter.loadCitesWeather();
                else finish();
            }
        }
    }

    @Override
    public void onBackPressed() {
        // if search window is open - close it
        if (searchWindow.getVisibility() == View.INVISIBLE || searchWindow.getVisibility() == View.GONE) {
            // if main activity was'n loaded any city notify the user that the user must to choose a city
            if (DatabaseUtils.checkData()) {
                super.onBackPressed();
                setResult(RESULT_CANCELED);
                finish();
            } else
                Toast.makeText(this, R.string.need_city, Toast.LENGTH_SHORT).show();
        } else
            closeSearch();
    }

    /**
     * If the window is open - close, if it is closed - open
     *
     * @param view fab
     */
    @Override
    public void onClick(View view) {
        //double click prevention
        if (SystemClock.elapsedRealtime() - mLastClickTime < 200) {
            return;
        }
        mLastClickTime = SystemClock.elapsedRealtime();
        switch (view.getId()) {
            case R.id.fab:
                if (searchWindow.getVisibility() == View.INVISIBLE || searchWindow.getVisibility() == View.GONE)
                    openSearch();
                else
                    closeSearch();
                break;
        }
    }

    public void openSearch() {
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
    }

    public void closeSearch() {
        // close keyboard and search window
        Log.d(TAG, "close search");
        IS_OPEN = false;
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        assert imm != null;
        imm.hideSoftInputFromWindow(searchWindow.getWindowToken(), 0);

        placePresenter.animationClose(searchWindow, fab);
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
    public void showError(Throwable throwable) {
        closeSearch();
        bar.setVisibility(View.INVISIBLE);
        Toast.makeText(this, getString(R.string.time_zone_exception), Toast.LENGTH_LONG).show();
    }

    @SuppressLint("RestrictedApi")
    @Override
    public void showError() {
        bar.setVisibility(View.INVISIBLE);
        cityArray.setVisibility(View.INVISIBLE);
        fab.setVisibility(View.INVISIBLE);
        citiesError.setVisibility(View.VISIBLE);
    }

    @Override
    public void showCitiesWeather(List<CitiesArrayWeather> weathers) {

        if (weathers.size() > 0) {
            if (weathers.get(0).isEx())
                Snackbar.make(cityArray, this.getString(R.string.cities_list_absent), Snackbar.LENGTH_LONG).show();
        }

        bar.setVisibility(View.INVISIBLE);
        weatherList.addAll(weathers);
        cityArrayAdapter.update(weathers);
    }

    @Override
    public void cityReady(String placeName, double lat, double lon, String timeZone) {
        ConnectivityManager cm = (ConnectivityManager) this.getSystemService(Context.CONNECTIVITY_SERVICE);
        assert cm != null;
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        // check internet connection
        if (netInfo != null && netInfo.isConnectedOrConnecting()) {
            // check that the data base already consist 10 cities or not
            if (placeName != null) {
                // setResult
                Intent intent = new Intent();
                intent.putExtra("city", placeName);
                intent.putExtra("lat", lat);
                intent.putExtra("lon", lon);
                intent.putExtra("time", timeZone);
                setResult(RESULT_OK, intent);
                finish();
            } else {
                closeSearch();
                Toast.makeText(this, getString(R.string.database_explanation), Toast.LENGTH_LONG).show();
            }
        } else
            Toast.makeText(this, getString(R.string.no_connection), Toast.LENGTH_LONG).show();
    }
}
