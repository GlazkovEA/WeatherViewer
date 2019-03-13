package homounikumus1.com.myweatherviewer.screen.cities_list_screen;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.ActionMode;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import homounikumus1.com.data2.model.weather.CitiesArrayWeather;
import homounikumus1.com.myweatherviewer.R;
import homounikumus1.com.myweatherviewer.screen.cities_list_screen.search.AView;
import homounikumus1.com.myweatherviewer.utils.DatabaseUtils;
import homounikumus1.com.myweatherviewer.utils.LoadImageUtils;

import static homounikumus1.com.myweatherviewer.WeatherApp.getAppContext;

public class CityArrayAdapter extends RecyclerView.Adapter<CityArrayAdapter.ViewHolder>  {
    private static final String TAG = "cityArrayAdapter";
    /**
     * List for delete checked items.
     */
    private ArrayList<CitiesArrayWeather> forDelete = new ArrayList<>();
    /**
     * If longClick is true put items in list - forDelete.
     */
    private boolean longClick = false;
    /**
     * For managed the recyclerView.
     */
    private ActionMode mActionMode;
    /**
     * Incoming data.
     */
    private List<CitiesArrayWeather> mData;
    private AView aView;

    public CityArrayAdapter(AView aView, List<CitiesArrayWeather> data) {
        this.aView = aView;
        this.mData = data;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(getAppContext()).inflate(R.layout.city_list_item, viewGroup, false);
        return new ViewHolder(view);
    }

    /**
     * Bind holder object with data
     * @param holder view
     * @param i data element position
     */
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int i) {
        CitiesArrayWeather day = mData.get(i);
        if (LoadImageUtils.getBitmaps().containsKey(day.getIconURL()))
            holder.icon.setImageBitmap(LoadImageUtils.getBitmaps().get(day.getIconURL()));
        else
            LoadImageUtils.LoadImage(holder.icon, day.getIconURL());

        Log.d(TAG, "bind view holder");

        // after each binding, reset all selected items
        // if long click flag is false
        if (!longClick) {
            for (int c = 0; c < mData.size(); c++) {
                mData.get(c).setSelected(false);
            }
        }

        // after each binding, colored the items according
        // to whether they were highlighted if we are in the delete menu
        holder.layout.setBackgroundColor(!day.isSelected() ? getAppContext().getColor(R.color.colorAccentTranperent):getAppContext().getColor(R.color.colorAccentSecond));

        holder.cityDescription.setText(day.getDescription());
        holder.city.setText(day.getCity());
        holder.cityTemp.setText(day.getTemp());
    }

    /**
     * Remove city from database and from view
     * @param position item in data
     */
    private void removeAt(int position) {
        CitiesArrayWeather e = mData.get(position);
        DatabaseUtils.deleteCityFromDatabase(e.getCity());

        mData.remove(position);
        notifyItemRemoved(position);
        notifyItemRangeChanged(position, mData.size());
    }

    /**
     * Data's array size
     * @return int
     */
    @Override
    public int getItemCount() {
        return mData.size();
    }

    public void update(List<CitiesArrayWeather> weatherList) {
        mData.clear();
        mData.addAll(weatherList);
        notifyDataSetChanged();
    }

    /**
     * Object that reference's list item's views
     */
    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        @BindView(R.id.city_icon)
        ImageView icon;
        @BindView(R.id.city)
        TextView city;
        @BindView(R.id.city_temp)
        TextView cityTemp;
        @BindView(R.id.city_description)
        TextView cityDescription;
        @BindView(R.id.city_item)
        LinearLayout layout;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);

            city.setTextColor(getAppContext().getColor(R.color.colorWhite));
            cityTemp.setTextColor(getAppContext().getColor(R.color.colorWhite));
            cityDescription.setTextColor(getAppContext().getColor(R.color.colorWhite));

            layout.setOnClickListener(this);

            layout.setOnLongClickListener((view) -> {
                longClick = true;
                CitiesArrayWeather day = mData.get(getAdapterPosition());
                day.setSelected(!day.isSelected());
                view.setBackgroundColor(!day.isSelected() ? getAppContext().getColor(R.color.colorAccentTranperent) : getAppContext().getColor(R.color.colorAccentSecond));
                if (mActionMode != null) {
                    return false;
                }
                mActionMode = layout.startActionMode(mActionModeCallback);
                view.setSelected(true);
                return true;
            });
        }

        /**
         * Select some item from recycler view
         * @param view recycler view item
         */
        @Override
        public void onClick(View view) {
            // if long click flag is - "true"^ marked it "selected" and paint in other color
            // in other way simply send data which consist city name and coordinates at listener
            CitiesArrayWeather day = mData.get(getAdapterPosition());
            if (!longClick) {
                if (aView != null)
                    aView.cityReady(day.getCity(), day.getLat(), day.getLon(), day.getTimeZone());
            } else {
                day.setSelected(!day.isSelected());
                view.setBackgroundColor(!day.isSelected() ? getAppContext().getColor(R.color.colorAccentTranperent) : getAppContext().getColor(R.color.colorAccentSecond));
            }
        }


    }

    /**
     * Action mode settings
     */
    private ActionMode.Callback mActionModeCallback = new ActionMode.Callback() {

        @Override
        public boolean onCreateActionMode(ActionMode mode, Menu menu) {
            MenuInflater inflater = mode.getMenuInflater();
            inflater.inflate(R.menu.menu_contect, menu);
            return true;
        }


        @Override
        public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
            return false;
        }

        @Override
        public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
            switch (item.getItemId()) {
                case R.id.delete:
                    for (CitiesArrayWeather w : mData) {
                        if (w.isSelected()) {
                            forDelete.add(w);
                        }
                    }

                    for (int i = 0; i < mData.size(); i++) {
                        for (CitiesArrayWeather w : forDelete) {
                            if (w.equals(mData.get(i))) {
                                removeAt(i);
                            }
                        }
                    }

                    longClick = false;
                    forDelete.clear();
                    mode.finish();
                    return true;
                default:
                    return false;
            }
        }

        @Override
        public void onDestroyActionMode(ActionMode mode) {
            longClick = false;
            for (int i = 0; i < mData.size(); i++) {
                mData.get(i).setSelected(false);
            }
            notifyDataSetChanged();
            mActionMode = null;
        }
    };

}
