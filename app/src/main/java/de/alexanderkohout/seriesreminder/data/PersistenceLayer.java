package de.alexanderkohout.seriesreminder.data;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;

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
     * Find all series with watching state "active".
     *
     * @return An ordered list of series with active watching state. Ordering
     * is done by title.
     */
    public ArrayList<Series> findActiveSeries() {
        return findSeriesByWatchingState("1");
    }

    /**
     * Find all series with watching state "inactive".
     *
     * @return An ordered list of series with inactive watching state. Ordering
     * is done by title.
     */
    public ArrayList<Series> findInactiveSeries() {
        return findSeriesByWatchingState("0");
    }

    /**
     * Find all series with desired watching state.
     *
     * @param watchingState The watching state to search for. Should be "1"
     *                      or "0".
     * @return An ordered list of series. Ordering is done by title.
     */
    private ArrayList<Series> findSeriesByWatchingState(final String watchingState) {
        final String[] projection = {
                SeriesReminderContract.SeriesEntry._ID,
                SeriesReminderContract.SeriesEntry.COLUMN_NAME_EPISODE,
                SeriesReminderContract.SeriesEntry.COLUMN_NAME_SEASON,
                SeriesReminderContract.SeriesEntry.COLUMN_NAME_TITLE,
                SeriesReminderContract.SeriesEntry.COLUMN_NAME_WATCHING
        };

        final String sortOrder =
                SeriesReminderContract.SeriesEntry.COLUMN_NAME_WATCHING + " DESC," +
                        SeriesReminderContract.SeriesEntry.COLUMN_NAME_TITLE + " ASC";

        String selection = SeriesReminderContract.SeriesEntry.COLUMN_NAME_WATCHING + " = ?";
        String[] selectionArgs = {watchingState};

        final Cursor cursor = database.query(
                SeriesReminderContract.SeriesEntry.TABLE_NAME,
                projection, selection, selectionArgs, null, null, null
        );

        final ArrayList<Series> activeSeries = new ArrayList<Series>();
        while (cursor.moveToNext()) {
            final Series series = new Series();

            series.id = cursor.getLong(
                    cursor.getColumnIndexOrThrow(SeriesReminderContract.SeriesEntry._ID)
            );
            series.name = cursor.getString(
                    cursor.getColumnIndexOrThrow(SeriesReminderContract.SeriesEntry.COLUMN_NAME_TITLE)
            );
            series.season = cursor.getInt(
                    cursor.getColumnIndexOrThrow(SeriesReminderContract.SeriesEntry.COLUMN_NAME_SEASON)
            );
            series.episode = cursor.getInt(
                    cursor.getColumnIndexOrThrow(SeriesReminderContract.SeriesEntry.COLUMN_NAME_EPISODE)
            );
            series.watching = cursor.getInt(
                    cursor.getColumnIndexOrThrow(SeriesReminderContract.SeriesEntry.COLUMN_NAME_WATCHING)
            ) == 1;

            activeSeries.add(series);
        }

        return activeSeries;
    }

    /**
     * Find a specific series.
     * <p/>
     * Will automatically convert the Cursor to the Series representation.
     *
     * @param id The series identifier.
     * @return Return the searched {@link de.alexanderkohout.seriesreminder
     * .data.Series}.
     */
    public Series findSeries(final long id) {
        final String[] projection = {
                SeriesReminderContract.SeriesEntry._ID,
                SeriesReminderContract.SeriesEntry.COLUMN_NAME_EPISODE,
                SeriesReminderContract.SeriesEntry.COLUMN_NAME_SEASON,
                SeriesReminderContract.SeriesEntry.COLUMN_NAME_TITLE,
                SeriesReminderContract.SeriesEntry.COLUMN_NAME_WATCHING
        };

        String selection = SeriesReminderContract.SeriesEntry._ID + " = ?";
        String[] selectionArgs = {String.valueOf(id)};

        final Cursor cursor = database.query(
                SeriesReminderContract.SeriesEntry.TABLE_NAME,
                projection, selection, selectionArgs, null, null, null
        );

        final Series series = new Series();
        while (cursor.moveToNext()) {
            series.id = cursor.getLong(
                    cursor.getColumnIndexOrThrow(SeriesReminderContract.SeriesEntry._ID)
            );
            series.name = cursor.getString(
                    cursor.getColumnIndexOrThrow(SeriesReminderContract.SeriesEntry.COLUMN_NAME_TITLE)
            );
            series.season = cursor.getInt(
                    cursor.getColumnIndexOrThrow(SeriesReminderContract.SeriesEntry.COLUMN_NAME_SEASON)
            );
            series.episode = cursor.getInt(
                    cursor.getColumnIndexOrThrow(SeriesReminderContract.SeriesEntry.COLUMN_NAME_EPISODE)
            );
            series.watching = cursor.getInt(
                    cursor.getColumnIndexOrThrow(SeriesReminderContract.SeriesEntry.COLUMN_NAME_WATCHING)
            ) == 1;
        }

        return series;
    }

    /**
     * Add a new Series to the database.
     *
     * @param name The title of the new series. Everything else will be
     *             default values.
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
        values.put(SeriesReminderContract.SeriesEntry.COLUMN_NAME_TITLE, series.name);
        values.put(SeriesReminderContract.SeriesEntry.COLUMN_NAME_SEASON, series.season);
        values.put(SeriesReminderContract.SeriesEntry.COLUMN_NAME_EPISODE, series.episode);
        values.put(SeriesReminderContract.SeriesEntry.COLUMN_NAME_WATCHING, series.watching ? 1 : 0);

        series.id = database.insert(
                SeriesReminderContract.SeriesEntry.TABLE_NAME, null, values);

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
        values.put(SeriesReminderContract.SeriesEntry.COLUMN_NAME_TITLE, series.name);
        values.put(SeriesReminderContract.SeriesEntry.COLUMN_NAME_SEASON, series.season);
        values.put(SeriesReminderContract.SeriesEntry.COLUMN_NAME_EPISODE, series.episode);
        values.put(SeriesReminderContract.SeriesEntry.COLUMN_NAME_WATCHING, series.watching ? 1 : 0);

        String selection = SeriesReminderContract.SeriesEntry._ID + " = ?";
        String[] selectionArgs = {String.valueOf(series.id)};

        database.update(
                SeriesReminderContract.SeriesEntry.TABLE_NAME,
                values, selection, selectionArgs);
    }

    /**
     * Deletes a given series from the database.
     *
     * @param series The series to be deleted.
     */
    public void deleteSeries(final Series series) {
        String selection = SeriesReminderContract.SeriesEntry._ID + " = ?";
        String[] selectionArgs = {String.valueOf(series.id)};

        database.delete(
                SeriesReminderContract.SeriesEntry.TABLE_NAME,
                selection, selectionArgs);
    }
}
