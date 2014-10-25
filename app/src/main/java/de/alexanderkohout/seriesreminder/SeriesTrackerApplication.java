package de.alexanderkohout.seriesreminder;

import android.app.Application;

import uk.co.chrisjenx.calligraphy.CalligraphyConfig;

/**
 * The main application class.
 */
public class SeriesTrackerApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        // Initialize the Postface font as default typeface for the fontPath
        // attribute
        CalligraphyConfig.initDefault(
                "fonts/Postface.otf",
                R.attr.fontPath);
    }
}
