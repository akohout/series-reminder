package de.alexanderkohout.seriesreminder.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Database manager class.
 *
 * This class will create, upgrade or downgrade a database based on the app
 * version.
 */
public class DatabaseHelper extends SQLiteOpenHelper {

    /**
     * The version of the Database.
     *
     * If you change the database schema, you must increment the database version.
     */
    public static final int DATABASE_VERSION = 1;

    /**
     * The name of the database file.
     */
    public static final String DATABASE_NAME = "SeriesReminder.db";

    /**
     * Create a new database based on the application context.
     *
     * @param context The application context.
     */
    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    /**
     * Create the database.
     *
     * Is automatically called when the database does not exist.
     *
     * @param db The database reference that is used to execute the SQL
     *           create statement on.
     */
    public void onCreate(SQLiteDatabase db) {
        final String create = "CREATE TABLE " + SeriesTrackerContract.SeriesEntry.TABLE_NAME + " (" +
                SeriesTrackerContract.SeriesEntry._ID + " INTEGER PRIMARY KEY," +
                SeriesTrackerContract.SeriesEntry.COLUMN_NAME_TITLE + " TEXT," +
                SeriesTrackerContract.SeriesEntry.COLUMN_NAME_SEASON + " INTEGER," +
                SeriesTrackerContract.SeriesEntry.COLUMN_NAME_EPISODE + " INTEGER," +
                SeriesTrackerContract.SeriesEntry.COLUMN_NAME_WATCHING + " INTEGER" +
                " )";
        db.execSQL(create);
    }

    /**
     * Upgrades the database.
     *
     * Is automatically called when the database exists,
     * but in a previous version.
     *
     * @param db The database reference.
     * @param oldVersion The currently installed version.
     * @param newVersion The new version that is about to be updated to.
     */
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Nothing to do atm
    }

    /**
     * Downgrades the database.
     *
     * Is automatically called when the database exists,
     * but in a newer version than required.
     *
     * @param db The database reference.
     * @param oldVersion The currently installed version.
     * @param newVersion The new version that is about to be updated to.
     */
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onUpgrade(db, oldVersion, newVersion);
    }
}
