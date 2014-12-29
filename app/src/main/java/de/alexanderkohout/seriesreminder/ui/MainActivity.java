package de.alexanderkohout.seriesreminder.ui;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;

import de.alexanderkohout.seriesreminder.R;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

/**
 * The main activity of this application.
 * <p/>
 * This activity is responsible for managing the set of series.
 */
public class MainActivity extends ActionBarActivity {

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(new CalligraphyContextWrapper(newBase));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        final Toolbar toolbar = (Toolbar) findViewById(R.id.appToolbar);
        setSupportActionBar(toolbar);

        if (savedInstanceState == null) {
            getFragmentManager().beginTransaction()
                    .add(R.id.container, OverviewFragment.newInstance())
                    .commit();
        }
    }
}
