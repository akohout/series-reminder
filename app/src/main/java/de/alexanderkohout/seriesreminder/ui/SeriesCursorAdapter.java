package de.alexanderkohout.seriesreminder.ui;

import android.content.Context;
import android.database.Cursor;
import android.text.SpannableString;
import android.text.style.UnderlineSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

import de.alexanderkohout.seriesreminder.R;
import de.alexanderkohout.seriesreminder.data.SeriesTrackerContract;

/**
 * Displays the set of series identified by the given Cursor.
 */
public class SeriesCursorAdapter extends CursorAdapter {

    /**
     * Create a new adapter for the series data set.
     *
     * @param context The application context.
     * @param c The Cursor that contains the data.
     * @param flags Some flags that don't seem to work.
     */
    public SeriesCursorAdapter(Context context, Cursor c, int flags) {
        super(context, c, flags);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        final LayoutInflater inflater = LayoutInflater.from(
                parent.getContext());

        View view = inflater.inflate(R.layout.series_overview_entry, parent, false);

        ViewHolder viewHolder = new ViewHolder();
        viewHolder.seriesTitleTextView = (TextView) view.findViewById(
                R.id.seriesTitleTextView);
        viewHolder.seasonCountTextView = (TextView) view.findViewById(
                R.id.seasonCountTextView);
        viewHolder.watchStateTextView = (TextView) view.findViewById(R.id
                .watchStateTextView);
        view.setTag(viewHolder);

        return view;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        ViewHolder viewHolder = (ViewHolder) view.getTag();

        final String title = cursor.getString(
                cursor.getColumnIndexOrThrow(
                        SeriesTrackerContract.SeriesEntry.COLUMN_NAME_TITLE)
        );
        final SpannableString spannableString = new SpannableString(title);
        spannableString.setSpan(new UnderlineSpan(), 0, title.length(), 0);

        viewHolder.seriesTitleTextView.setText(spannableString);

        viewHolder.seasonCountTextView.setText(
                String.format(
                        context.getString(R.string.season_count),
                        cursor.getInt(
                                cursor.getColumnIndexOrThrow(
                                        SeriesTrackerContract.SeriesEntry.COLUMN_NAME_SEASON)
                        )
                ) + " - " +
                        String.format(
                                context.getString(R.string.episode_count),
                                cursor.getInt(
                                        cursor.getColumnIndexOrThrow(
                                                SeriesTrackerContract.SeriesEntry.COLUMN_NAME_EPISODE)
                                )
                        )
        );

        viewHolder.watchStateTextView.setTextColor(
                context.getResources().getColor(
                        cursor.getInt(
                                cursor.getColumnIndexOrThrow(
                                        SeriesTrackerContract.SeriesEntry.COLUMN_NAME_WATCHING)
                        ) == 1 ? R.color.Orange : R.color.Grey
                )
        );
    }

    private static class ViewHolder {
        public TextView seriesTitleTextView;
        public TextView seasonCountTextView;
        public TextView watchStateTextView;
    }
}
