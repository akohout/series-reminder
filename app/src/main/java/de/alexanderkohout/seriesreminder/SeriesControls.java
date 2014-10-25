package de.alexanderkohout.seriesreminder;

import de.alexanderkohout.seriesreminder.data.Series;

/**
 * The interface to manipulate series.
 */
public interface SeriesControls {

    /**
     * Add a new series with a default set of attributes.
     *
     * @param title The title of the new series.
     */
    void add(final String title);

    /**
     * Update an existing series' attributes.
     *
     * @param series The series object contains on one hand the identifier of
     *               the series to be updated, and on the other hand all data
     *               attributes to be updated.
     */
    void update(final Series series);

    /**
     * Deletes an existing series.
     *
     * @param series The series object to be deleted.
     */
    void delete(final Series series);
}
