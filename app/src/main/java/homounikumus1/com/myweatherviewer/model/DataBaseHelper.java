package homounikumus1.com.myweatherviewer.model;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import android.widget.Toast;

import homounikumus1.com.myweatherviewer.R;

import static homounikumus1.com.myweatherviewer.model.DataBase.TABLE_NAME;

public class DataBaseHelper {
    private DataBase dataBase;
    private SQLiteDatabase mDB;
    private final Context mCtx;

    public DataBaseHelper(Context ctx) {
        mCtx = ctx;
    }

    /**
     * Get first element that we have in TABLE
     *
     * @return array which consist city name and coordinates of first element in database
     */
    public String[] getFirstElementFromDatabase() {
        @SuppressLint("Recycle") Cursor c = mDB.query(TABLE_NAME, null, null, null, null, null, null);
        String city = null;
        String coordinates = null;
        if (c.moveToFirst()) {
            city = c.getString(c.getColumnIndex("CITY"));
            coordinates = c.getString(c.getColumnIndex("COORDINATES"));
        }
        return new String[]{city, coordinates};
    }

    /**
     * Сheck have we this city in database or not
     *
     * @param city requested city
     * @return boolean
     */
    public boolean checkIsCityAlreadyExist(String city) {
        @SuppressLint("Recycle") Cursor cursor = mDB.rawQuery("SELECT * FROM " + TABLE_NAME + " WHERE    CITY=?", new String[]{city});
        boolean exists = (cursor.getCount() > 0);
        cursor.close();
        return exists;
    }


    public boolean checkIsIDIsAlreadyExist(String city, String id) {
        @SuppressLint("Recycle") Cursor cursor = mDB.rawQuery("SELECT * FROM " + TABLE_NAME + " WHERE CITY_ID=?", new String[]{id});
        boolean exists = (cursor.getCount() > 0);
        String oldCity = "";
        if (cursor.moveToFirst()) {
            oldCity = cursor.getString(cursor.getColumnIndex("CITY"));
        }

        if (exists) {
            @SuppressLint("Recycle") Cursor c = mDB.rawQuery("SELECT * FROM " + TABLE_NAME + " WHERE    CITY=?", new String[]{city});
            if (c.moveToFirst()) {
                String cityID = c.getString(c.getColumnIndex("CITY_ID"));
                if (cityID == null) {
                    Toast.makeText(mCtx, mCtx.getString(R.string.not_add_in_database_explanation, city, oldCity), Toast.LENGTH_LONG).show();
                    delRec(city);
                }
            }
        }
        cursor.close();
        return exists;
    }

    /**
     * Return amount of elements in database
     *
     * @return int
     */
    public long amountOfElementsInDatabase() {
        return DatabaseUtils.queryNumEntries(mDB, TABLE_NAME);
    }

    /**
     * Open database connection
     */
    public void open() {
        dataBase = new DataBase(mCtx);
        mDB = dataBase.getWritableDatabase();
    }

    /**
     * Close database connection
     */
    public void close() {
        if (dataBase != null) dataBase.close();
    }

    /**
     * Get all data from TABLE
     *
     * @return cursor with all data
     */
    public Cursor getAllData() {
        return mDB.query(TABLE_NAME, null, null, null, null, null, null);
    }

    /**
     * Add record in TABLE
     *
     * @param city        added city
     * @param coordinates added city coordinates
     */
    public void addRec(String city, String coordinates) {
        ContentValues cv = new ContentValues();
        cv.put("CITY", city);
        cv.put("COORDINATES", coordinates);
        mDB.insert(TABLE_NAME, null, cv);
        dataBase.close();
        mDB.close();
    }

    /**
     * Delete record from TABLE
     *
     * @param city deleted city
     */
    public void delRec(String city) {
        mDB.delete(TABLE_NAME, "CITY = ?", new String[]{city});
    }

    /**
     * Update record from TABLE
     *
     * @param city updated record
     * @param id   added ID
     */
    public void updateRec(String city, String id) {
        ContentValues cv = new ContentValues();
        cv.put("CITY_ID", id);
        mDB.update(TABLE_NAME, cv, "CITY = ?", new String[]{city});
    }
}
