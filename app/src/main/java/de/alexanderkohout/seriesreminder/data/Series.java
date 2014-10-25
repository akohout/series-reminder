package de.alexanderkohout.seriesreminder.data;

/**
 * A series data model.
 */
public class Series {

    /**
     * The database identifier.
     */
    public long id;

    /**
     * The name of the series a.k.a. the visual representation of the series.
     */
    public String name;

    /**
     * The season count of the series.
     */
    public int season;

    /**
     * The episode count of the series.
     */
    public int episode;

    /**
     * A boolean flag indicating whether the user is actively watching the
     * series or not.
     */
    public boolean watching;
}
