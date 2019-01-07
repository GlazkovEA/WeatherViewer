package homounikumus1.com.myweatherviewer;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
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

import homounikumus1.com.myweatherviewer.model.LoadImage;
import homounikumus1.com.myweatherviewer.model.Weather;
import homounikumus1.com.myweatherviewer.presenter.CityListener;
import homounikumus1.com.myweatherviewer.presenter.Presenter;

public class CityArrayAdapter extends RecyclerView.Adapter<CityArrayAdapter.ViewHolder> implements LoadImage {
    private static final String TAG = "cityArrayAdapter";
    private ArrayList<Weather> forDelete = new ArrayList<>();
    private boolean longClick = false;
    private ActionMode mActionMode;
    private List<Weather> mData;
    private LayoutInflater mInflater;
    private Activity activity;
    private Presenter presenter;
    private CityListener cityListener;

    public CityArrayAdapter(AppCompatActivity activity, CityListener cityListener, List<Weather> data) {
        this.activity = activity;
        this.cityListener = cityListener;
        this.mInflater = LayoutInflater.from(activity.getBaseContext());
        this.mData = data;
        this.presenter = new Presenter(activity.getBaseContext());
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = mInflater.inflate(R.layout.city_list_item, viewGroup, false);
        return new ViewHolder(view);
    }

    /**
     * Bind holder object with data
     * @param holder view
     * @param i data element position
     */
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int i) {
        Weather day = mData.get(i);
        if (bitmaps.containsKey(day.getIconURL()))
            holder.icon.setImageBitmap(bitmaps.get(day.getIconURL()));
        else
            new CityArrayAdapter.LoadImageTask(holder.icon).execute(day.getIconURL());

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
        holder.layout.setBackgroundColor(!day.isSelected() ? activity.getColor(R.color.colorAccentTranperent):activity.getColor(R.color.colorAccentSecond));

        holder.cityDescription.setText(day.getDescription());
        holder.city.setText(day.getCity());
        holder.cityTemp.setText(day.getTemp());
    }

    /**
     * Remove city from database and from view
     * @param position item in data
     */
    private void removeAt(int position) {
        Weather e = mData.get(position);
        presenter.deleteCityFromDatabase(e.getCity());

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

    /**
     * Object that reference's list item's views
     */
    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        ImageView icon;
        TextView city;
        TextView cityTemp;
        TextView cityDescription;
        LinearLayout layout;

        ViewHolder(@NonNull View itemView) {
            super(itemView);

            icon = itemView.findViewById(R.id.city_icon);
            city = itemView.findViewById(R.id.city);
            cityTemp = itemView.findViewById(R.id.city_temp);
            cityDescription = itemView.findViewById(R.id.city_description);
            layout = itemView.findViewById(R.id.city_item);
            layout.setOnClickListener(this);

            layout.setOnLongClickListener((view) -> {
                longClick = true;
                Weather day = mData.get(getAdapterPosition());
                day.setSelected(!day.isSelected());
                view.setBackgroundColor(!day.isSelected() ? activity.getColor(R.color.colorAccentTranperent) : activity.getColor(R.color.colorAccentSecond));
                if (mActionMode != null) {
                    return false;
                }
                mActionMode = activity.startActionMode(mActionModeCallback);
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
            Weather day = mData.get(getAdapterPosition());
            if (!longClick) {
                if (cityListener != null)
                    cityListener.onCityReady(day.getCity(), day.getCoords());
            } else {
                day.setSelected(!day.isSelected());
                view.setBackgroundColor(!day.isSelected() ? activity.getColor(R.color.colorAccentTranperent) : activity.getColor(R.color.colorAccentSecond));
            }
        }


    }

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
                    for (Weather w : mData) {
                        if (w.isSelected()) {
                            forDelete.add(w);
                        }
                    }

                    for (int i = 0; i < mData.size(); i++) {
                        for (Weather w : forDelete) {
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
