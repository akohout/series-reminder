package de.alexanderkohout.seriesreminder.ui;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;

import de.alexanderkohout.seriesreminder.R;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

/**
 * The main activity of this application.
 *
 * This activity is responsible for managing the set of series.
 */
public class SeriesActivity extends Activity {

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(new CalligraphyContextWrapper(newBase));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.series);
        if (savedInstanceState == null) {
            getFragmentManager().beginTransaction()
                    .add(R.id.container, SeriesOverviewFragment.newInstance())
                    .commit();
        }
    }
}
