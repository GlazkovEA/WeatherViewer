package homounikumus1.com.myweatherviewer.model;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.widget.ImageView;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

public interface LoadImage {
    /**
     * Stores already downloaded Bitmaps for reuse
     */
    Map<String, Bitmap> bitmaps = new HashMap<String, Bitmap>();

    /**
     * AsyncTask to load weather condition icons in a separate thread
     */
    @SuppressLint("StaticFieldLeak")
    public class LoadImageTask extends AsyncTask<String, Void, Bitmap> {
        private ImageView imageView;

        // store ImageView on which to set the downloaded Bitmap
        public LoadImageTask(ImageView imageview) {
            this.imageView = imageview;
        }

        // load image; strings[0] is the String URL representing the image
        @Override
        protected Bitmap doInBackground(String... strings) {
            Bitmap bitmap = null;
            HttpURLConnection connection = null;

            try {
                URL url = new URL(strings[0]); // create URL for image

                // open an HttpURLConnection, get its InputStream
                // and download the image
                connection = (HttpURLConnection) url.openConnection();

                InputStream inputStream = connection.getInputStream();
                bitmap = BitmapFactory.decodeStream(inputStream);
                bitmaps.put(strings[0], bitmap);

            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                assert connection != null;
                connection.disconnect(); // close the HttpURLConnection
            }

            return bitmap;
        }

        // set weather condition image in list item
        @Override
        protected void onPostExecute(Bitmap bitmap) {
            if (bitmap != null) {
                imageView.setImageBitmap(bitmap);
            }
        }
    }
}