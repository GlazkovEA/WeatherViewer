package homounikumus1.com.data2.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DataBase extends SQLiteOpenHelper {
    static final String TABLE_NAME = "CITES";
    private static final String BASE_NAME = "DB";
    private static final int VERSION = 1;

    DataBase(Context context) {
        super(context, BASE_NAME, null, VERSION);
    }

    /**
     * Create table CITES with 4 fields
     * @param sqLiteDatabase db
     */
    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL("CREATE TABLE CITES (_id INTEGER PRIMARY KEY AUTOINCREMENT, "
                + "CITY TEXT, "
                + "CITY_ID TEXT,"
                + "TIME_ZONE TEXT,"
                + "LAT REAL,"
                + "LON TEXT)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }
}
