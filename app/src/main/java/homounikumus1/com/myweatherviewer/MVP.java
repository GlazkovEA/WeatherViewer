package homounikumus1.com.myweatherviewer;

import android.support.v7.app.ActionBar;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ProgressBar;

import java.util.ArrayList;

import homounikumus1.com.myweatherviewer.model.Weather;
import homounikumus1.com.myweatherviewer.view.Action;

public interface MVP {
    interface view {
        /**
         * start activity
         */
        void start();

        /**
         * @param action      some actions constant
         * @param coordinates data which needed update
         * @param city        data which needed update
         */
        void action(Action action, String coordinates, String city);
    }

    interface presenter {
        /**
         * @param data  Url which we send in Async task
         * @param city  City Name - because weatherAPI and placesAPI cities Name are not correlated
         *              (the names of cities in Latin)
         * @param view  The main view which will be filled with data about today's weather
         * @param title Here we put today's date
         */
        void oneDayWeather(String data, String city, View view, ActionBar title);

        /**
         * @param data            Url which we send in Async task
         * @param weatherListView RecyclerView for smooth scroll to zero position
         * @param arrayAdapter    Array adapter for notify data set changed
         * @param weatherList     ArrayList for save received data
         * @param bar             Off bar when data will be loaded
         */
        void thisWeekWeather(String data, RecyclerView weatherListView, WeatherArrayAdapter arrayAdapter, ArrayList<Weather> weatherList, View bar);

        /**
         * @param data Url which we send in Async task
         */
        void cityArray(String data, View bar);
    }


}
