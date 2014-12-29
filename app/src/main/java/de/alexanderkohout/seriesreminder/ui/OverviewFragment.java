package de.alexanderkohout.seriesreminder.ui;

import android.app.Fragment;
import android.app.FragmentManager;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;

import de.alexanderkohout.seriesreminder.R;
import de.alexanderkohout.seriesreminder.SeriesControls;
import de.alexanderkohout.seriesreminder.data.DatabaseHelper;
import de.alexanderkohout.seriesreminder.data.PersistenceLayer;
import de.alexanderkohout.seriesreminder.data.Series;

public class OverviewFragment extends Fragment implements
        SeriesAdapter.OnItemClickListener,
        SeriesControls {

    private PersistenceLayer persistenceLayer;
    private SeriesAdapter adapter;

    public OverviewFragment() {
        setHasOptionsMenu(true);
    }

    public static OverviewFragment newInstance() {
        return new OverviewFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return getActivity().getLayoutInflater().inflate(
                R.layout.overview, container, false
        );
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        SQLiteDatabase database = new DatabaseHelper(
                getActivity().getApplicationContext()).getWritableDatabase();
        persistenceLayer = new PersistenceLayer(database);

        final RecyclerView seriesRecyclerView = (RecyclerView) view
                .findViewById(R.id.seriesRecyclerView);
        seriesRecyclerView.setLayoutManager(
                new LinearLayoutManager(getActivity().getApplicationContext())
        );

        adapter = new SeriesAdapter(
                getActivity(),
                persistenceLayer.findActiveSeries(),
                persistenceLayer.findInactiveSeries(),
                this
        );
        seriesRecyclerView.setAdapter(adapter);

        view.findViewById(R.id.addSeriesButton)
                .setOnClickListener(
                        new AddSeriesClickListener(
                                getFragmentManager(), this
                        )
                );
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.main, menu);
    }

    @Override
    public void onItemClicked(Series series) {
        EditDialogFragment
                .newInstance(series.id, this)
                .show(getFragmentManager(), EditDialogFragment.TAG);
    }

    @Override
    public void add(String title) {
        final Series series = persistenceLayer.addSeries(title);
        adapter.addActiveSeries(series);
    }

    @Override
    public void update(Series series) {
        persistenceLayer.updateSeries(series);
        adapter.updateSeries(series);
    }

    @Override
    public void delete(Series series) {
        persistenceLayer.deleteSeries(series);
        adapter.deleteSeries(series);
    }

    private static class AddSeriesClickListener implements View
            .OnClickListener {
        private final FragmentManager fragmentManager;
        private final SeriesControls seriesControls;

        private AddSeriesClickListener(FragmentManager fragmentManager, SeriesControls seriesControls) {
            this.fragmentManager = fragmentManager;
            this.seriesControls = seriesControls;
        }

        @Override
        public void onClick(View v) {
            AddDialogFragment
                    .newInstance(seriesControls)
                    .show(fragmentManager, AddDialogFragment.TAG);
        }
    }
}
