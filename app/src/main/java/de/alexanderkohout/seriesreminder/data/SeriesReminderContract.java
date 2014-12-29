package de.alexanderkohout.seriesreminder.data;

import android.provider.BaseColumns;

/**
 * The contract defines some conventions for the series database table.
 */
public class SeriesReminderContract {

    public SeriesReminderContract() {
    }

    public static abstract class SeriesEntry implements BaseColumns {
        public static final String TABLE_NAME = "series";

        public static final String COLUMN_NAME_TITLE = "title";
        public static final String COLUMN_NAME_SEASON = "season";
        public static final String COLUMN_NAME_EPISODE = "episode";
        public static final String COLUMN_NAME_WATCHING = "watching";
    }
}
