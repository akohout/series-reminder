package de.alexanderkohout.seriesreminder.data;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

/**
 * A helper class to interact with the database.
 */
public class PersistenceLayer {

    /**
     * The reference to the real database.
     */
    private final SQLiteDatabase database;

    /**
     * Create a new persistence layer for the given database.
     *
     * @param database The database to work on.
     */
    public PersistenceLayer(final SQLiteDatabase database) {
        this.database = database;
    }

    /**
     * Find all the series ordered by watching state and title ascending.
     *
     * @return Return the cursor that contains the series data.
     */
    public Cursor findAllSeries() {
        final String[] projection = {
                SeriesTrackerContract.SeriesEntry._ID,
                SeriesTrackerContract.SeriesEntry.COLUMN_NAME_EPISODE,
                SeriesTrackerContract.SeriesEntry.COLUMN_NAME_SEASON,
                SeriesTrackerContract.SeriesEntry.COLUMN_NAME_TITLE,
                SeriesTrackerContract.SeriesEntry.COLUMN_NAME_WATCHING
        };

        final String sortOrder =
                SeriesTrackerContract.SeriesEntry.COLUMN_NAME_WATCHING + " DESC," +
                        SeriesTrackerContract.SeriesEntry.COLUMN_NAME_TITLE + " ASC";

        return database.query(
                SeriesTrackerContract.SeriesEntry.TABLE_NAME,
                projection, null, null, null, null, sortOrder
        );
    }

    /**
     * Find a specific series.
     *
     * Will automatically convert the Cursor to the Series representation.
     *
     * @param id The series identifier.
     *
     * @return Return the searched {@link de.alexanderkohout.seriesreminder
     * .data.Series}.
     */
    public Series findSeries(final long id) {
        final String[] projection = {
                SeriesTrackerContract.SeriesEntry._ID,
                SeriesTrackerContract.SeriesEntry.COLUMN_NAME_EPISODE,
                SeriesTrackerContract.SeriesEntry.COLUMN_NAME_SEASON,
                SeriesTrackerContract.SeriesEntry.COLUMN_NAME_TITLE,
                SeriesTrackerContract.SeriesEntry.COLUMN_NAME_WATCHING
        };

        String selection = SeriesTrackerContract.SeriesEntry._ID + " = ?";
        String[] selectionArgs = {String.valueOf(id)};

        final Cursor cursor = database.query(
                SeriesTrackerContract.SeriesEntry.TABLE_NAME,
                projection, selection, selectionArgs, null, null, null
        );

        final Series series = new Series();
        while (cursor.moveToNext()) {
            series.id = cursor.getLong(
                    cursor.getColumnIndexOrThrow(SeriesTrackerContract.SeriesEntry._ID)
            );
            series.name = cursor.getString(
                    cursor.getColumnIndexOrThrow(SeriesTrackerContract.SeriesEntry.COLUMN_NAME_TITLE)
            );
            series.season = cursor.getInt(
                    cursor.getColumnIndexOrThrow(SeriesTrackerContract.SeriesEntry.COLUMN_NAME_SEASON)
            );
            series.episode = cursor.getInt(
                    cursor.getColumnIndexOrThrow(SeriesTrackerContract.SeriesEntry.COLUMN_NAME_EPISODE)
            );
            series.watching = cursor.getInt(
                    cursor.getColumnIndexOrThrow(SeriesTrackerContract.SeriesEntry.COLUMN_NAME_WATCHING)
            ) == 1;
        }

        return series;
    }

    /**
     * Add a new Series to the database.
     *
     * @param name The title of the new series. Everything else will be
     *             default values.
     *
     * @return The newly created Series object. Will also contain the
     * database identifier.
     */
    public Series addSeries(final String name) {
        final Series series = new Series();
        series.name = name;
        series.season = 1;
        series.episode = 1;
        series.watching = true;

        ContentValues values = new ContentValues();
        values.put(SeriesTrackerContract.SeriesEntry.COLUMN_NAME_TITLE, series.name);
        values.put(SeriesTrackerContract.SeriesEntry.COLUMN_NAME_SEASON, series.season);
        values.put(SeriesTrackerContract.SeriesEntry.COLUMN_NAME_EPISODE, series.episode);
        values.put(SeriesTrackerContract.SeriesEntry.COLUMN_NAME_WATCHING, series.watching ? 1 : 0);

        series.id = database.insert(
                SeriesTrackerContract.SeriesEntry.TABLE_NAME, null, values);

        return series;
    }

    /**
     * Updates a given series entry.
     *
     * @param series The series to be updated. Contains the identifier and
     *               all the data.
     */
    public void updateSeries(final Series series) {
        ContentValues values = new ContentValues();
        values.put(SeriesTrackerContract.SeriesEntry.COLUMN_NAME_TITLE, series.name);
        values.put(SeriesTrackerContract.SeriesEntry.COLUMN_NAME_SEASON, series.season);
        values.put(SeriesTrackerContract.SeriesEntry.COLUMN_NAME_EPISODE, series.episode);
        values.put(SeriesTrackerContract.SeriesEntry.COLUMN_NAME_WATCHING, series.watching ? 1 : 0);

        String selection = SeriesTrackerContract.SeriesEntry._ID + " = ?";
        String[] selectionArgs = {String.valueOf(series.id)};

        database.update(
                SeriesTrackerContract.SeriesEntry.TABLE_NAME,
                values, selection, selectionArgs);
    }

    /**
     * Deletes a given series from the database.
     * @param series The series to be deleted.
     */
    public void deleteSeries(final Series series) {
        String selection = SeriesTrackerContract.SeriesEntry._ID + " = ?";
        String[] selectionArgs = {String.valueOf(series.id)};

        database.delete(
                SeriesTrackerContract.SeriesEntry.TABLE_NAME,
                selection, selectionArgs);
    }
}
