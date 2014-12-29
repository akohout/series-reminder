package de.alexanderkohout.seriesreminder.ui;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.SpannableString;
import android.text.style.UnderlineSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

import de.alexanderkohout.seriesreminder.R;
import de.alexanderkohout.seriesreminder.data.Series;

/**
 * The SeriesAdapter is responsible for managing the series list and its state.
 */
public class SeriesAdapter extends RecyclerView.Adapter<SeriesAdapter.SeriesViewHolder> {

    /**
     * The application context.
     */
    private final Context context;

    /**
     * A list of series with active watching state.
     */
    private final ArrayList<Series> activeSeries;

    /**
     * A list of series with inactive watching state.
     */
    private final ArrayList<Series> inactiveSeries;

    /**
     * An item click listener for list items, i.e. for series entries.
     */
    private final OnItemClickListener onItemClickListener;

    /**
     * New series list adapter.
     *
     * @param context             The application context.
     * @param activeSeries        A list of series with active watching state.
     * @param inactiveSeries      A list of series with inactive watching state.
     * @param onItemClickListener An item click listener for the series
     *                            entries, typically the calling Fragment.
     */
    public SeriesAdapter(final Context context,
                         final ArrayList<Series> activeSeries,
                         final ArrayList<Series> inactiveSeries,
                         final OnItemClickListener onItemClickListener) {
        this.context = context;
        this.activeSeries = activeSeries;
        this.inactiveSeries = inactiveSeries;
        this.onItemClickListener = onItemClickListener;
    }

    @Override
    public SeriesViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        final View view = LayoutInflater.from(context).inflate(
                R.layout.overview_entry, viewGroup, false
        );
        return new SeriesViewHolder(view, onItemClickListener);
    }

    @Override
    public void onBindViewHolder(SeriesViewHolder viewHolder, int position) {
        Series series = null;
        if (position < activeSeries.size()) {
            series = activeSeries.get(position);
        } else if ((position - activeSeries.size()) < inactiveSeries.size()) {
            final int actualPosition = position - activeSeries.size();
            series = inactiveSeries.get(actualPosition);
        }

        viewHolder.bind(context, series);
    }

    @Override
    public int getItemCount() {
        return activeSeries.size() + inactiveSeries.size();
    }

    /**
     * Add an active series to the adapter.
     *
     * @param series The series to be added.
     */
    public void addActiveSeries(final Series series) {
        this.activeSeries.add(series);
        notifyItemInserted(this.activeSeries.size() - 1);
    }

    /**
     * Updates the list entry for the given series object.
     *
     * @param series The series to be updated.
     */
    public void updateSeries(final Series series) {
        // Updating a series is a bit more complex:
        // 1.) If the series is active and was in active list,
        // everything is ok - just give it the new data
        // 2.) If the series is active and was in inactive list,
        // add series to active list and remove from inactive list
        // 3.) If the series is inactive and was in inactive list,
        // everything is ok - just give it the new data
        // 4.) If the series is inactive and was in active list,
        // add series to inactive and remove from active

        boolean wasInActive = false;
        for (int i = 0; i < activeSeries.size(); ++i) {
            if (activeSeries.get(i).id == series.id) {
                // Case 1
                if (activeSeries.get(i).watching == series.watching) {
                    activeSeries.get(i).season = series.season;
                    activeSeries.get(i).episode = series.episode;

                    notifyItemChanged(i);
                }
                // Case 4
                else {
                    activeSeries.remove(i);
                    notifyItemRemoved(i);

                    inactiveSeries.add(series);
                    notifyItemInserted(
                            activeSeries.size() + inactiveSeries.size()
                    );
                }

                wasInActive = true;
                break;
            }
        }

        if (wasInActive) {
            return;
        }

        for (int i = 0; i < inactiveSeries.size(); ++i) {
            if (inactiveSeries.get(i).id == series.id) {
                // Case 3
                if (inactiveSeries.get(i).watching == series.watching) {
                    inactiveSeries.get(i).season = series.season;
                    inactiveSeries.get(i).episode = series.episode;

                    notifyItemChanged(i + activeSeries.size());
                }
                // Case 2
                else {
                    inactiveSeries.remove(i);
                    notifyItemRemoved(activeSeries.size() + i);

                    activeSeries.add(series);
                    notifyItemInserted(
                            activeSeries.size() - 1
                    );
                }

                break;
            }
        }
    }

    /**
     * Delete the given series from the list.
     *
     * @param series The series to be deleted.
     */
    public void deleteSeries(final Series series) {
        int listPositionToBeDeleted = -1;
        boolean isInActiveList = false;

        for (int i = 0; i < activeSeries.size(); ++i) {
            if (activeSeries.get(i).id == series.id) {
                listPositionToBeDeleted = i;
                isInActiveList = true;
                break;
            }
        }

        for (int i = 0; i < inactiveSeries.size(); ++i) {
            if (inactiveSeries.get(i).id == series.id) {
                listPositionToBeDeleted = i;
                break;
            }
        }

        if (listPositionToBeDeleted != -1) {
            if (isInActiveList) {
                activeSeries.remove(listPositionToBeDeleted);
                notifyItemRemoved(listPositionToBeDeleted);
            } else {
                inactiveSeries.remove(listPositionToBeDeleted);
                notifyItemRemoved(listPositionToBeDeleted + activeSeries.size());
            }
        }
    }

    /**
     * The adapter's ViewHolder.
     */
    public static class SeriesViewHolder extends RecyclerView.ViewHolder {

        public TextView seriesTitleTextView;
        public TextView seasonCountTextView;
        public TextView watchStateTextView;

        private Series series;
        private final OnItemClickListener onItemClickListener;

        public SeriesViewHolder(final View itemView,
                                final OnItemClickListener onItemClickListener) {
            super(itemView);
            this.onItemClickListener = onItemClickListener;

            seriesTitleTextView = (TextView) itemView.findViewById(
                    R.id.seriesTitleTextView
            );
            seasonCountTextView = (TextView) itemView.findViewById(
                    R.id.seasonCountTextView
            );
            watchStateTextView = (TextView) itemView.findViewById(
                    R.id.watchStateTextView
            );
        }

        public void bind(final Context context, final Series series) {
            this.series = series;

            this.itemView.setOnClickListener(
                    new SeriesClickListener(series, onItemClickListener)
            );

            final SpannableString spannableString = new SpannableString(series.name);
            spannableString.setSpan(
                    new UnderlineSpan(), 0, series.name.length(), 0
            );
            seriesTitleTextView.setText(spannableString);

            seasonCountTextView.setText(
                    String.format(
                            context.getString(R.string.season_count),
                            series.season) +
                            " - " +
                            String.format(
                                    context.getString(R.string.episode_count),
                                    series.episode
                            )
            );

            watchStateTextView.setTextColor(
                    context.getResources().getColor(
                            series.watching ? R.color.Orange : R.color.Grey
                    )
            );
        }
    }

    private static class SeriesClickListener implements View.OnClickListener {

        private final Series series;
        private final OnItemClickListener onItemClickListener;

        private SeriesClickListener(Series series, OnItemClickListener onItemClickListener) {
            this.series = series;
            this.onItemClickListener = onItemClickListener;
        }

        @Override
        public void onClick(View v) {
            if (onItemClickListener != null) {
                onItemClickListener.onItemClicked(series);
            }
        }
    }

    public interface OnItemClickListener {

        /**
         * Invoked when one list entry is clicked.
         *
         * @param series The clicked series.
         */
        void onItemClicked(final Series series);
    }
}
