package de.alexanderkohout.seriesreminder.ui;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;

import de.alexanderkohout.seriesreminder.R;
import de.alexanderkohout.seriesreminder.SeriesControls;
import de.alexanderkohout.seriesreminder.data.DatabaseHelper;
import de.alexanderkohout.seriesreminder.data.PersistenceLayer;
import de.alexanderkohout.seriesreminder.data.Series;

/**
 * A fragment to edit series data.
 */
public class EditDialogFragment extends DialogFragment {

    /**
     * The identifier of this fragment.
     */
    public static final String TAG = "SeriesEditDialog";

    /**
     * The key for the arguments set where the series identifier is stored.
     */
    private static final String ARGS_SERIES_ID = "SeriesId";

    /**
     * A handler for manipulating series.
     */
    private SeriesControls seriesControls;

    /**
     * Empty constructor for Android.
     */
    public EditDialogFragment() {
        setStyle(STYLE_NO_TITLE, 0);
    }

    /**
     * Create a new dialog to edit a series.
     *
     * @param id             The identifier of the series to be edited.
     * @param seriesControls A handler that will manipulate the data for this
     *                       fragment.
     * @return A new dialog to edit a series.
     */
    public static EditDialogFragment newInstance(final long id,
                                                 final SeriesControls seriesControls) {
        final EditDialogFragment f = new EditDialogFragment();

        f.seriesControls = seriesControls;

        final Bundle bundle = new Bundle();
        bundle.putLong(ARGS_SERIES_ID, id);
        f.setArguments(bundle);

        return f;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Bundle args = getArguments();

        // Without args we don't have a series identifier, so we give it up
        if (args == null) {
            dismiss();
            return null;
        }

        // Get the database reference to find the series
        final SQLiteDatabase database = new DatabaseHelper(getActivity()
                .getApplicationContext())
                .getWritableDatabase();
        final PersistenceLayer persistenceLayer = new PersistenceLayer(database);

        final Series series = persistenceLayer.findSeries(
                args.getLong(ARGS_SERIES_ID));

        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        // Inflate custom layout for builder
        final LayoutInflater inflater = getActivity().getLayoutInflater();
        final View layoutView = inflater.inflate(R.layout.edit, null);

        final TextView seriesTitleTextView = (TextView) layoutView
                .findViewById(R.id.seriesTitleTextView);
        seriesTitleTextView.setText(series.name);

        final TextView seasonCountTextView = (TextView) layoutView.findViewById(R.id.seasonCountTextView);
        final TextView episodeCountTextView = (TextView) layoutView.findViewById(R.id.episodeCountTextView);
        final String seasonText = getString(R.string.season_count);
        final String episodeText = getString(R.string.episode_count);

        layoutView.findViewById(R.id.decreaseSeasonImageButton).setOnClickListener(
                new ModifySeriesClickListener(
                        series, false, true, seasonText, episodeText, seasonCountTextView, episodeCountTextView
                )
        );
        layoutView.findViewById(R.id.increaseSeasonImageButton).setOnClickListener(
                new ModifySeriesClickListener(
                        series, true, true, seasonText, episodeText, seasonCountTextView, episodeCountTextView
                )
        );

        layoutView.findViewById(R.id.decreaseEpisodeImageButton).setOnClickListener(
                new ModifySeriesClickListener(
                        series, false, false, seasonText, episodeText, seasonCountTextView, episodeCountTextView
                )
        );
        layoutView.findViewById(R.id.increaseEpisodeImageButton).setOnClickListener(
                new ModifySeriesClickListener(
                        series, true, false, seasonText, episodeText, seasonCountTextView, episodeCountTextView
                )
        );

        final Switch watchStateSwitch = (Switch) layoutView.findViewById(R.id
                .watchSwitch);
        watchStateSwitch.setChecked(series.watching);
        watchStateSwitch.setOnCheckedChangeListener(
                new WatchingStateListener(series)
        );

        builder.setView(layoutView)
                .setPositiveButton(R.string.okay, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        if (seriesControls != null) {
                            seriesControls.update(series);
                        }

                        dismiss();
                    }
                })
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dismiss();
                    }
                })
                .setNeutralButton(R.string.delete, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        if (seriesControls != null) {
                            seriesControls.delete(series);
                        }

                        dismiss();
                    }
                });
        return builder.create();
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    /**
     * Some complicated inner class to handle increasing / decreasing of
     * episodes or season counter.
     */
    private static class ModifySeriesClickListener implements View.OnClickListener {

        private static final int MIN_EPISODE = 1;
        private static final int MIN_SEASON = 1;

        private final Series series;
        private final boolean doIncrease;
        private final boolean setSeason;
        private final String seasonText;
        private final String episodeText;
        private final TextView seasonTextView;
        private final TextView episodeTextView;

        private ModifySeriesClickListener(Series series, boolean doIncrease, boolean setSeason,
                                          final String seasonText, final String episodeText,
                                          TextView seasonTextView, TextView episodeTextView) {
            this.series = series;
            this.doIncrease = doIncrease;
            this.setSeason = setSeason;
            this.seasonText = seasonText;
            this.episodeText = episodeText;
            this.seasonTextView = seasonTextView;
            this.episodeTextView = episodeTextView;

            if (setSeason) {
                seasonTextView.setText(
                        String.format(seasonText, series.season)
                );
            } else {
                episodeTextView.setText(
                        String.format(episodeText, series.episode)
                );
            }
        }

        @Override
        public void onClick(View v) {
            if (setSeason) {
                int seasonCount = doIncrease ? series.season + 1 : series.season - 1;
                if (seasonCount < MIN_SEASON) {
                    seasonCount = MIN_SEASON;
                } else {
                    series.episode = MIN_EPISODE;

                    episodeTextView.setText(
                            String.format(episodeText, series.episode)
                    );
                }

                series.season = seasonCount;

                seasonTextView.setText(
                        String.format(seasonText, series.season)
                );
            } else {
                int episodeCount = doIncrease ? series.episode + 1 : series.episode - 1;
                if (episodeCount < MIN_EPISODE) {
                    episodeCount = MIN_EPISODE;
                }
                series.episode = episodeCount;

                episodeTextView.setText(
                        String.format(episodeText, series.episode)
                );
            }
        }
    }

    /**
     * An inner class to handle the watch state of a series.
     */
    private static class WatchingStateListener implements CompoundButton
            .OnCheckedChangeListener {
        private final Series series;

        private WatchingStateListener(Series series) {
            this.series = series;
        }

        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            series.watching = isChecked;
        }
    }
}
