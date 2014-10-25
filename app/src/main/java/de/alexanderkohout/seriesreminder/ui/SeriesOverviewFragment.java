package de.alexanderkohout.seriesreminder.ui;

import android.app.Fragment;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.CursorAdapter;
import android.widget.ListView;

import de.alexanderkohout.seriesreminder.R;
import de.alexanderkohout.seriesreminder.SeriesControls;
import de.alexanderkohout.seriesreminder.data.DatabaseHelper;
import de.alexanderkohout.seriesreminder.data.PersistenceLayer;
import de.alexanderkohout.seriesreminder.data.Series;

public class SeriesOverviewFragment extends Fragment implements
        AdapterView.OnItemClickListener,
        SeriesControls {

    private PersistenceLayer persistenceLayer;
    private SeriesCursorAdapter adapter;
    private SQLiteDatabase database;

    public SeriesOverviewFragment() {
        setHasOptionsMenu(true);
    }

    public static SeriesOverviewFragment newInstance() {
        return new SeriesOverviewFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.series_overview, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        final ListView seriesListView = (ListView) view.findViewById(R.id.seriesListView);
        seriesListView.setOnItemClickListener(this);

        database = new DatabaseHelper(getActivity().getApplicationContext())
                .getWritableDatabase();
        persistenceLayer = new PersistenceLayer(database);

        adapter = new SeriesCursorAdapter(
                getActivity().getApplicationContext(),
                persistenceLayer.findAllSeries(),
                CursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER
        );

        seriesListView.setAdapter(adapter);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);

        inflater.inflate(R.menu.main, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_add) {
            SeriesAddDialogFragment
                    .newInstance(this)
                    .show(getFragmentManager(), SeriesAddDialogFragment.TAG);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        SeriesEditDialogFragment
                .newInstance(id, this)
                .show(getFragmentManager(), SeriesEditDialogFragment.TAG);
    }

    @Override
    public void add(String title) {
        persistenceLayer.addSeries(title);
        adapter.changeCursor(
                persistenceLayer.findAllSeries()
        );
    }

    @Override
    public void update(Series series) {
        persistenceLayer.updateSeries(series);
        adapter.changeCursor(
                persistenceLayer.findAllSeries()
        );
    }

    @Override
    public void delete(Series series) {
        persistenceLayer.deleteSeries(series);
        adapter.changeCursor(
                persistenceLayer.findAllSeries()
        );
    }
}
