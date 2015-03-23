package com.example.cbasu.whatnameareyou;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Environment;

public class BluBotDbHelper extends SQLiteOpenHelper {

    private static final String TEXT_TYPE = " TEXT";
    private static final String COMMA_SEP = ",";
    private static final String SQL_CREATE_ENTRIES =
            "CREATE TABLE BluBots (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT," + "beaconname TEXT, "
                    + "rssi INTEGER, " + "unixtime1000 INTEGER )";

    private static final String SQL_DELETE_ENTRIES =
            "DROP TABLE IF EXISTS beacons";
    // If you change the database schema, you must increment the database version.

    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = Environment.getExternalStorageDirectory().getPath() + "/BluBot.db";

    public BluBotDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_ENTRIES);
    }

    //SQLiteDatabase db = SQLiteDatabase.openOrCreateDatabase(dbfile, null);

    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // This database is only a cache for online data, so its upgrade policy is
        // to simply to discard the data and start over
        db.execSQL(SQL_DELETE_ENTRIES);
        onCreate(db);
    }

    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onUpgrade(db, oldVersion, newVersion);
    }

    // Books table name
    private static final String TABLE_BEACONS = "BluBots";

    // Books Table Columns names
    private static final String KEY_ID = "id";
    private static final String KEY_NAME = "beaconname";
    private static final String KEY_VAL = "rssi";
    private static final String KEY_TIME = "unixtime1000";

    //class add value
    //public void addValue(Devices device) {
    public void addValue(String name,short rssi, long time) {
        // 1. get reference to writable DB
        SQLiteDatabase db = this.getWritableDatabase();

        // 2. create ContentValues to add key "column"/value
        ContentValues values = new ContentValues();
        values.put(KEY_NAME, name); // get device name
        values.put(KEY_VAL, rssi); // get rssi
        values.put(KEY_TIME, time);
        // 3. insert
        db.insert(TABLE_BEACONS, // table
                null, //nullColumnHack
                values); // key/value -> keys = column names/ values = column values

        // 4. close
        db.close();

    }

    // Updating single book
    //public int updatevalues(Devices device) {
    public int updatevalues() {

        // 1. get reference to writable DB
        SQLiteDatabase db = this.getWritableDatabase();

        // 2. create ContentValues to add key "column"/value
        ContentValues values = new ContentValues();
        values.put(KEY_NAME, "BluBot1"); // get device bluetooth name
        values.put(KEY_VAL, 85); // get rssi
        values.put(KEY_TIME, 1400000); // get unixtime


        // 3. this updates a row indicated by an integer id
        int i = db.update(TABLE_BEACONS, //table
                values, // column/value
                KEY_ID + " = ?", // selections
                //new String[]{String.valueOf(device.getId())}); //selection args
                new String[]{String.valueOf(100)});

        // 4. close
        db.close();

        return i;

    }
}

