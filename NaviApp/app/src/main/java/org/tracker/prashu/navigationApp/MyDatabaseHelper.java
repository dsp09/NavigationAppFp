package org.tracker.prashu.navigationApp;

import android.content.Context;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

// database helper class that connects to the sqlite database and stores the data in database.
public class MyDatabaseHelper extends SQLiteOpenHelper {

    // setting the db name, version and the table name.
    public static final String TAG = "prashu database";
    private static final String DATABASE_NAME = "navigation.db";
    public static final int DATABASE_VERSION = 4;
    public static final String TABLE_NAME = "testTable";

    // column names in the table.
    public static final String ID = "_id";
    public static final String EMAIL = "email";
    public static final String NAME = "name";
    public static final String DATE = "date";
    public static final String TIME = "time";
    public static final String START_LAT = "start_latitiude";
    public static final String START_LONG = "start_longitude";
    public static final String CURRENT_LAT = "current_latitude";
    public static final String CURRENT_LONG = "current_longitude";
    public static final String POLYLINE_ARRAY = "polyline_array";
    public static final String DURATION = "duration";
    public static final String DISTANCE = "distance";
    public static final String COLOR = "color";


    // strings that querries to create the table.
    public static final String CREATE_TABLE = " create table " + TABLE_NAME +
            " (" + ID + " integer primary key autoincrement unique, "
            + EMAIL + " varchar(255), "
            + NAME + " varchar(255), "
            + DATE + " varchar(255), "
            + TIME + " varchar(255), "
            + START_LAT + " varchar(255), "
            + START_LONG + " varchar(255), "
            + CURRENT_LAT + " varchar(255), "
            + CURRENT_LONG + " varchar(255), "
            + POLYLINE_ARRAY + " varchar, "
            + DURATION + " varchar(255), "
            + DISTANCE + " varchar(255),"
            + COLOR + " varchar(255)); ";

    // string that querries to drop the table.
    public static final String DROP_TABLE = "DROP TABLE IF EXISTS " + TABLE_NAME + ";";
    public static Context dbContext;

    //constructor to set the database name, database version, factory value, and context.
    public MyDatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.dbContext = context;
    }

    // overridden method of the SQLiteOpenHelper class that is called on create of the database.
    @Override
    public void onCreate(SQLiteDatabase db) {
        try {
            db.execSQL(CREATE_TABLE);
        } catch (SQLException e) {
        }
    }

    // method that is called on upgrade of the database by first deleting the old table and then creating a new one.
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        try {
            db.execSQL(DROP_TABLE);
            onCreate(db);
        } catch (SQLException e) {
        }
    }
}
