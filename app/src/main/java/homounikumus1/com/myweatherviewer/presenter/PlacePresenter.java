package homounikumus1.com.myweatherviewer.presenter;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.content.Context;
import android.support.design.widget.FloatingActionButton;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;

import com.google.android.gms.location.places.AutocompleteFilter;
import com.google.android.gms.location.places.AutocompletePrediction;
import com.google.android.gms.location.places.GeoDataClient;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.PlaceBufferResponse;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.RuntimeRemoteException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import homounikumus1.com.myweatherviewer.PlaceAutocompleteAdapter;
import homounikumus1.com.myweatherviewer.R;

public class PlacePresenter {
    private static final String TAG = "PlacePresenter";
    private Context context;
    private GeoDataClient mGeoDataClient;
    private PlaceAutocompleteAdapter mAdapter;
    private Presenter presenter;
    private CityListener listener;

    public PlacePresenter(Context context, GeoDataClient mGeoDataClient, AutoCompleteTextView mAutocompleteView, CityListener listener) {
        this.context = context;
        this.listener = listener;
        this.mGeoDataClient = mGeoDataClient;
        this.presenter = new Presenter(context);

         // clearing data from unnecessary items,
         // in our case we need only cites

        AutocompleteFilter typeFilter = new AutocompleteFilter.Builder()
                .setTypeFilter(AutocompleteFilter.TYPE_FILTER_CITIES)
                .build();
        this.mAdapter = new PlaceAutocompleteAdapter(context, mGeoDataClient, null, typeFilter);
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
                String lat = String.valueOf(latLng.latitude);
                String lon = String.valueOf(latLng.longitude);

                // send data into listeners:
                // if the database already contains 10 items - notify user about it and send null
                // if not - send cityName and string with lat, lon data
                if (listener != null) {
                    if (presenter.amountOfElementsInDatabase() >= 10) {
                        Log.d(TAG, "database already contains 10 items");
                        listener.onCityReady(null, null);
                    } else {
                        Log.d(TAG, "add in database " + place.getName().toString() + " coordinates = " + latLng);
                        // check is this city already in the database, or not
                        boolean isAdd = presenter.addCityInDatabase(place.getName().toString(), "lat=" + lat + "&lon=" + lon);
                        Log.d(TAG, "is already exist in database = " + isAdd);
                        listener.onCityReady(place.getName().toString(), "lat=" + lat + "&lon=" + lon);
                    }
                }

            } catch (RuntimeRemoteException ignored) {
            }
        }
    };

    private AdapterView.OnItemClickListener mAutocompleteClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

            final AutocompletePrediction item = mAdapter.getItem(position);
            assert item != null;
            final String placeId = item.getPlaceId();
            Task<PlaceBufferResponse> placeResult = mGeoDataClient.getPlaceById(placeId);
            placeResult.addOnCompleteListener(mUpdatePlaceDetailsCallback);
        }
    };

    public AdapterView.OnItemClickListener getAutocompleteClickListener() {
        return mAutocompleteClickListener;
    }

    /**
     * Gone layout with search window
     * @param searchWindow view for search
     * @param fab fab
     */
    public void animationClose(View searchWindow, FloatingActionButton fab) {
        Log.d(TAG, "open search window");
        fab.setImageDrawable(context.getDrawable(R.drawable.ic_search_24dp));

        ObjectAnimator animX = ObjectAnimator.ofFloat(searchWindow, "x", 0);
        ObjectAnimator animY = ObjectAnimator.ofFloat(searchWindow, "y", searchWindow.getHeight());
        AnimatorSet animSetXY = new AnimatorSet();
        animSetXY.playTogether(animX, animY);
        animSetXY.start();
        searchWindow.setVisibility(View.GONE);
    }

    /**
     * Show layout with search window
     * @param searchWindow view for search
     * @param fab fab
     */
    public void animationOpen(View searchWindow, FloatingActionButton fab) {
        Log.d(TAG, "close search window");
        fab.setImageDrawable(context.getDrawable(R.drawable.ic_vertical_align_bottom_24dp));
        searchWindow.setVisibility(View.VISIBLE);
        PropertyValuesHolder pvhX = PropertyValuesHolder.ofFloat("x", 0);
        PropertyValuesHolder pvhY = PropertyValuesHolder.ofFloat("y", 0);
        ObjectAnimator.ofPropertyValuesHolder(searchWindow, pvhX, pvhY).start();
    }


}
