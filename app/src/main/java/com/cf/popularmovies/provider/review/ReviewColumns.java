package com.cf.popularmovies.provider.review;

import android.net.Uri;
import android.provider.BaseColumns;

import com.cf.popularmovies.provider.MovieProvider;
import com.cf.popularmovies.provider.movie.MovieColumns;
import com.cf.popularmovies.provider.review.ReviewColumns;
import com.cf.popularmovies.provider.video.VideoColumns;

/**
 * A review for a movie.
 */
public class ReviewColumns implements BaseColumns {
    public static final String TABLE_NAME = "review";
    public static final Uri CONTENT_URI = Uri.parse(MovieProvider.CONTENT_URI_BASE + "/" + TABLE_NAME);

    /**
     * Primary key.
     */
    public static final String _ID = BaseColumns._ID;

    /**
     * Reference to movie table.
     */
    public static final String MOVIE_ID = "movie_id";

    /**
     * Author of the review. Who wrote it?
     */
    public static final String AUTHOR = "author";

    /**
     * The content of the review.
     */
    public static final String CONTENT = "content";


    public static final String DEFAULT_ORDER = TABLE_NAME + "." +_ID;

    // @formatter:off
    public static final String[] ALL_COLUMNS = new String[] {
            _ID,
            MOVIE_ID,
            AUTHOR,
            CONTENT
    };
    // @formatter:on

    public static boolean hasColumns(String[] projection) {
        if (projection == null) return true;
        for (String c : projection) {
            if (c.equals(MOVIE_ID) || c.contains("." + MOVIE_ID)) return true;
            if (c.equals(AUTHOR) || c.contains("." + AUTHOR)) return true;
            if (c.equals(CONTENT) || c.contains("." + CONTENT)) return true;
        }
        return false;
    }

    public static final String PREFIX_MOVIE = TABLE_NAME + "__" + MovieColumns.TABLE_NAME;
}
