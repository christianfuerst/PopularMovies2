package com.cf.popularmovies.provider.movie;

import android.net.Uri;
import android.provider.BaseColumns;

import com.cf.popularmovies.provider.MovieProvider;
import com.cf.popularmovies.provider.movie.MovieColumns;
import com.cf.popularmovies.provider.review.ReviewColumns;
import com.cf.popularmovies.provider.video.VideoColumns;

/**
 * A movie which can be displayed in the app.
 */
public class MovieColumns implements BaseColumns {
    public static final String TABLE_NAME = "movie";
    public static final Uri CONTENT_URI = Uri.parse(MovieProvider.CONTENT_URI_BASE + "/" + TABLE_NAME);

    /**
     * Primary key.
     */
    public static final String _ID = BaseColumns._ID;

    /**
     * Unique id for the movie on TMDB.
     */
    public static final String TMDB_ID = "tmdb_id";

    /**
     * Title of the movie.
     */
    public static final String TITLE = "title";

    /**
     * Overview of the movie.
     */
    public static final String OVERVIEW = "overview";

    /**
     * Relase date of the movie.
     */
    public static final String RELEASE_DATE = "release_date";

    /**
     * Average voting score from IMDB user.
     */
    public static final String VOTE_AVERAGE = "vote_average";

    /**
     * Local path where the backdrop image is stored.
     */
    public static final String BACKDROPPATH = "backdropPath";

    /**
     * Local path where the poster image is stored.
     */
    public static final String POSTERPATH = "posterPath";


    public static final String DEFAULT_ORDER = TABLE_NAME + "." +_ID;

    // @formatter:off
    public static final String[] ALL_COLUMNS = new String[] {
            _ID,
            TMDB_ID,
            TITLE,
            OVERVIEW,
            RELEASE_DATE,
            VOTE_AVERAGE,
            BACKDROPPATH,
            POSTERPATH
    };
    // @formatter:on

    public static boolean hasColumns(String[] projection) {
        if (projection == null) return true;
        for (String c : projection) {
            if (c.equals(TMDB_ID) || c.contains("." + TMDB_ID)) return true;
            if (c.equals(TITLE) || c.contains("." + TITLE)) return true;
            if (c.equals(OVERVIEW) || c.contains("." + OVERVIEW)) return true;
            if (c.equals(RELEASE_DATE) || c.contains("." + RELEASE_DATE)) return true;
            if (c.equals(VOTE_AVERAGE) || c.contains("." + VOTE_AVERAGE)) return true;
            if (c.equals(BACKDROPPATH) || c.contains("." + BACKDROPPATH)) return true;
            if (c.equals(POSTERPATH) || c.contains("." + POSTERPATH)) return true;
        }
        return false;
    }

}
