package homounikumus1.com.myweatherviewer;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import java.util.List;

import homounikumus1.com.myweatherviewer.model.LoadImage;
import homounikumus1.com.myweatherviewer.model.Weather;

public class WeatherArrayAdapter extends RecyclerView.Adapter<WeatherArrayAdapter.ViewHolder> implements LoadImage {
    private List<Weather> mData;
    private LayoutInflater mInflater;
    private Context context;

    public WeatherArrayAdapter(Context context, List<Weather> data) {
        this.context = context;
        this.mInflater = LayoutInflater.from(context);
        this.mData = data;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = mInflater.inflate(R.layout.list_item, viewGroup, false);
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
        // if weather condition icon already downloaded, use it;
        // otherwise, download icon in a separate thread
        if (bitmaps.containsKey(day.getIconURL())) {
            holder.imageview.setImageBitmap(bitmaps.get(day.getIconURL()));
        } else {
            new LoadImageTask(holder.imageview).execute(day.getIconURL());
        }

        holder.dayTextView.setText(context.getString(R.string.day_description, day.getDayOfWeek()));
        holder.heightTextView.setText(context.getString(R.string.temp, day.getTemp()));
        holder.lowTextView.setText(context.getString(R.string.desc, day.getDescription()));
        holder.humidityTextView.setText(context.getString(R.string.humidity, day.getHumidity()));
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
        ImageView imageview;
        TextView dayTextView;
        TextView lowTextView;
        TextView heightTextView;
        TextView humidityTextView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            imageview = itemView.findViewById(R.id.image);
            dayTextView = itemView.findViewById(R.id.dayTextView);
            heightTextView = itemView.findViewById(R.id.heightTextView);
            lowTextView = itemView.findViewById(R.id.description);
            humidityTextView = itemView.findViewById(R.id.humidityTextView);
        }
    }
}
