package homounikumus1.com.myweatherviewer.screen.cities_list_screen.search;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.os.Build;
import android.support.design.widget.FloatingActionButton;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.TextView;

import com.google.android.gms.location.places.AutocompleteFilter;
import com.google.android.gms.location.places.AutocompletePrediction;
import com.google.android.gms.location.places.GeoDataClient;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.PlaceBufferResponse;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.RuntimeRemoteException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import homounikumus1.com.data2.repository.Provider;
import homounikumus1.com.myweatherviewer.R;
import homounikumus1.com.myweatherviewer.loader.LifecycleHandler;
import homounikumus1.com.myweatherviewer.utils.DatabaseUtils;
import io.reactivex.disposables.Disposable;

import static homounikumus1.com.myweatherviewer.WeatherApp.getAppContext;

public class PlacePresenter {
    private static final String TAG = "PlacePresenter";
    private AView aView;
    private LifecycleHandler handler;

    /**
     * Main entry point for the Google Places Geo Data API
     * The Geo Data API provides access to Google's database of local place and business information
     */
    private GeoDataClient mGeoDataClient;

    /**
     * Adepter for AutoCompleteTextView
     */
    private PlaceAutocompleteAdapter mAdapter;
    /**
     * Text field with auto-completion and the ability to edit the entered text
     */
    private AutoCompleteTextView mAutocompleteView;

    public PlacePresenter(AView aView, LifecycleHandler handler, GeoDataClient mGeoDataClient, AutoCompleteTextView mAutocompleteView) {
        this.aView = aView;
        this.handler = handler;
        this.mGeoDataClient = mGeoDataClient;
        this.mAutocompleteView = mAutocompleteView;
    }

    public void init() {
        // Filter for customizing the autocomplete predictions from the Geo Data API
        AutocompleteFilter typeFilter = new AutocompleteFilter.Builder()
                .setTypeFilter(AutocompleteFilter.TYPE_FILTER_CITIES)
                .build();
        mAdapter = new PlaceAutocompleteAdapter(mGeoDataClient, null, typeFilter);
        mAutocompleteView.setAdapter(mAdapter);
    }

    private OnCompleteListener<PlaceBufferResponse> mUpdatePlaceDetailsCallback = new OnCompleteListener<PlaceBufferResponse>() {
        @Override
        public void onComplete(Task<PlaceBufferResponse> task) {
            try {
                PlaceBufferResponse places = task.getResult();
                assert places != null;
                // get place
                final Place place = places.get(0);
                // get coordinates from place
                LatLng latLng = place.getLatLng();
                // divide coordinates on latitude and longitude
                double lat = latLng.latitude;
                double lon = latLng.longitude;
                // send data into listeners:
                // if the database already contains 10 items - notify user about it and send null
                // if not - send cityName and string with lat, lon data
                if (DatabaseUtils.amountOfElementsInDatabase() >= 10) {
                    Log.d(TAG, "database already contains 10 items");
                    aView.cityReady(null, 0, 0, null);
                } else {
                    getTimeZone(place, lat, lon);
                }
            } catch (RuntimeRemoteException ignored) {
            }
        }
    };

    /**
     * Request for getting time zone
     *
     * @param place - googleAPI object
     * @param lat   - latitude
     * @param lon   - longitude
     */
    private void getTimeZone(Place place, double lat, double lon) {
        Disposable disposable = Provider.getTimeZoneRepository().getTimeZone(lat + "," + lon)
                .doOnSubscribe(disposable1 -> {
                    aView.showLoading();
                })
                .doOnTerminate(aView::hideLoading)
                .compose(handler.load(R.id.time_zone))
                .subscribe(time -> {
                    // if city already exist in database didn;t add it again
                    boolean isAdd = DatabaseUtils.addCityInDatabase(place.getName().toString(), lat, lon, time.getTimeZone());
                    Log.d(TAG, "is already exist in database = " + isAdd);
                    aView.cityReady(place.getName().toString(), lat, lon, time.getTimeZone());
                }, aView::showError);
    }

    /**
     * Click listener for AutoCompleteTextView
     */
    private AdapterView.OnItemClickListener mAutocompleteClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            //if (position > 0) {
                final AutocompletePrediction item = mAdapter.getItem(position);
                if (item != null) {
                    final String placeId = item.getPlaceId();
                    Task<PlaceBufferResponse> placeResult = mGeoDataClient.getPlaceById(placeId);
                    placeResult.addOnCompleteListener(mUpdatePlaceDetailsCallback);
                }
            //}
        }
    };

    public AdapterView.OnItemClickListener getAutocompleteClickListener() {
        return mAutocompleteClickListener;
    }

    /**
     * Gone layout with search window
     *
     * @param searchWindow view for search
     * @param fab          fab
     */
    public void animationClose(View searchWindow, FloatingActionButton fab) {
        Log.d(TAG, "open search window");
        fab.setImageDrawable(getAppContext().getDrawable(R.drawable.ic_search_24dp));

        ObjectAnimator animX = ObjectAnimator.ofFloat(searchWindow, "x", 0);
        ObjectAnimator animY = ObjectAnimator.ofFloat(searchWindow, "y", searchWindow.getHeight());
        AnimatorSet animSetXY = new AnimatorSet();
        animSetXY.playTogether(animX, animY);
        animSetXY.start();
        searchWindow.setVisibility(View.GONE);
    }

    /**
     * Show layout with search window
     *
     * @param searchWindow view for search
     * @param fab          fab
     */
    public void animationOpen(View searchWindow, FloatingActionButton fab) {
        Log.d(TAG, "close search window");
        fab.setImageDrawable(getAppContext().getDrawable(R.drawable.ic_vertical_align_bottom_24dp));
        searchWindow.setVisibility(View.VISIBLE);
        PropertyValuesHolder pvhX = PropertyValuesHolder.ofFloat("x", 0);
        PropertyValuesHolder pvhY = PropertyValuesHolder.ofFloat("y", 0);
        ObjectAnimator.ofPropertyValuesHolder(searchWindow, pvhX, pvhY).start();
    }


}
