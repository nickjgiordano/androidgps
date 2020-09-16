package com.csc20038.gps;

import android.content.Context;
import android.content.ContentValues;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteDatabase;
import android.database.Cursor;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DB_NAME = "locations";
    private static final int DB_VERSION = 1;

    public DatabaseHelper(Context context) {super(context, DB_NAME, null, DB_VERSION);}

    // called upon creation
    @Override
    public void onCreate(SQLiteDatabase db) {
        createDb(db);
    }

    // creates database structure, along with test data
    public void createDb(SQLiteDatabase db) {
        db.execSQL("DROP TABLE Record;");
        db.execSQL("CREATE TABLE Record ("
                + "_id INTEGER PRIMARY KEY AUTOINCREMENT, "
                + "Name TEXT, "
                + "Notes TEXT, "
                + "Latitude REAL, "
                + "Longitude REAL, "
                + "Altitude REAL);"
        );
        insertRecord(db, "London Eye", "on top of the London Eye last April",
                51.5033, -0.1195, 492.126);
        insertRecord(db, "Sherwood Forest", "in the woods, like Robin Hood",
                53.2027, -1.0703, 150.919);
        insertRecord(db, "Kilimanjaro", "I did it!!",
                -3.0674, 37.3520, 19340.6);
        insertRecord(db, "Death Valley", "need waterr",
                36.5323, -116.93, -282.15);
        insertRecord(db, "Parthenon", "Praise Athena! Simply breathtaking",
                37.9703, 23.7225, 45.9318);
        insertRecord(db, "Loch Ness", "didnt find the monster. nice trip tho",
                57.3000, -4.4500, 52.4934);
    }

    // placeholder method used to set instruction upon database upgrades
    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {}

    // placeholder method used to set instruction upon database downgrades
    @Override
    public void onDowngrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {}

    // method used to insert new data into database
    public static long insertRecord(SQLiteDatabase db, String name, String notes, double latitude, double longitude, double altitude) {
        ContentValues recordValues = new ContentValues();
        recordValues.put("Name", name);
        recordValues.put("Notes", notes);
        recordValues.put("Latitude", latitude);
        recordValues.put("Longitude", longitude);
        recordValues.put("Altitude", altitude);
        long newRecordID = db.insert("Record", null, recordValues);
        return newRecordID;
    }

    // method used to update database data
    public static void updateRecord(SQLiteDatabase db, Long id, String name, String notes) {
        ContentValues recordValues = new ContentValues();
        recordValues.put("Name", name);
        recordValues.put("Notes", notes);
        db.update("Record", recordValues, "_id=?", new String[] {Long.toString(id)});
    }

    // method used to delete data from database
    public static void deleteRecord(SQLiteDatabase db, Long id) {db.delete("Record", "_id=?", new String[] {Long.toString(id)});}

    // method used to delete all records from database
    public static void deleteAll(SQLiteDatabase db) {
        db.delete("Record", null, null);
    }

    // method used to concatenate all database records into single string
    // currently used just for email feature
    public static String getDatabaseContentsAsString(SQLiteDatabase db) {
        Cursor cursor = db.query("Record", new String[]{"Name", "Notes", "Latitude", "Longitude", "Altitude"}, null, null, null, null, null);
        String databaseAsString = System.getProperty("line.separator");
        if(cursor.getCount() != 0) {
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                for (int i=0; i < cursor.getColumnCount(); i++) {
                    databaseAsString += cursor.getString(i) + System.getProperty("line.separator");
                }
                databaseAsString += System.getProperty("line.separator") + System.getProperty("line.separator");
                cursor.moveToNext();
            }
            if(cursor != null) cursor.close();
        }
        return databaseAsString.trim();
    }
}