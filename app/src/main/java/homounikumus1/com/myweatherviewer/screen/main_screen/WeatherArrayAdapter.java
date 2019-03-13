package homounikumus1.com.myweatherviewer.screen.main_screen;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import homounikumus1.com.data2.model.weather.WeekWeather;
import homounikumus1.com.myweatherviewer.R;
import homounikumus1.com.myweatherviewer.utils.LoadImageUtils;

import static homounikumus1.com.myweatherviewer.WeatherApp.getAppContext;

public class WeatherArrayAdapter extends RecyclerView.Adapter<WeatherArrayAdapter.ViewHolder> {
    /**
     * Incoming data.
     */
    private List<WeekWeather> mData;

    /**
     * Constructor
     * @param data - init data
     */
    public WeatherArrayAdapter(List<WeekWeather> data) {
        this.mData = data;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(getAppContext()).inflate(R.layout.list_item, viewGroup, false);
        return new ViewHolder(view);
    }

    /**
     * Bind holder object with data
     * @param holder view
     * @param i data element position
     */
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int i) {
        WeekWeather day = mData.get(i);
        // if weather condition icon already downloaded, use it;
        // otherwise, download icon in a separate thread
        if (LoadImageUtils.getBitmaps().containsKey(day.getIconURL())) {
            holder.imageview.setImageBitmap(LoadImageUtils.getBitmaps().get(day.getIconURL()));
        } else {
            LoadImageUtils.LoadImage(holder.imageview, day.getIconURL());
        }

        holder.dayTextView.setText(getAppContext().getString(R.string.day_description, day.getDayOfWeek()));
        holder.heightTextView.setText(getAppContext().getString(R.string.temp, day.getTemp()));
        holder.lowTextView.setText(getAppContext().getString(R.string.desc, day.getDescription()));
        holder.humidityTextView.setText(getAppContext().getString(R.string.humidity, day.getHumidity()));
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
    public static class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.image)
        ImageView imageview;
        @BindView(R.id.dayTextView)
        TextView dayTextView;
        @BindView(R.id.heightTextView)
        TextView lowTextView;
        @BindView(R.id.description)
        TextView heightTextView;
        @BindView(R.id.humidityTextView)
        TextView humidityTextView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            lowTextView.setTextColor(getAppContext().getColor(R.color.colorWhite));
            heightTextView.setTextColor(getAppContext().getColor(R.color.colorWhite));
            humidityTextView.setTextColor(getAppContext().getColor(R.color.colorWhite));
            dayTextView.setTextColor(getAppContext().getColor(R.color.colorWhite));
        }
    }
}
