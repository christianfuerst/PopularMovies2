package com.cf.popularmovies.provider.video;

import android.net.Uri;
import android.provider.BaseColumns;

import com.cf.popularmovies.provider.MovieProvider;
import com.cf.popularmovies.provider.movie.MovieColumns;
import com.cf.popularmovies.provider.review.ReviewColumns;
import com.cf.popularmovies.provider.video.VideoColumns;

/**
 * A video from a movie.
 */
public class VideoColumns implements BaseColumns {
    public static final String TABLE_NAME = "video";
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
     * The unique key in order to access the video on a 3rd party site.
     */
    public static final String KEY = "key";

    /**
     * The name of the video.
     */
    public static final String NAME = "name";

    /**
     * The name of the site, where the video can be accessed.
     */
    public static final String SITE = "site";


    public static final String DEFAULT_ORDER = TABLE_NAME + "." +_ID;

    // @formatter:off
    public static final String[] ALL_COLUMNS = new String[] {
            _ID,
            MOVIE_ID,
            KEY,
            NAME,
            SITE
    };
    // @formatter:on

    public static boolean hasColumns(String[] projection) {
        if (projection == null) return true;
        for (String c : projection) {
            if (c.equals(MOVIE_ID) || c.contains("." + MOVIE_ID)) return true;
            if (c.equals(KEY) || c.contains("." + KEY)) return true;
            if (c.equals(NAME) || c.contains("." + NAME)) return true;
            if (c.equals(SITE) || c.contains("." + SITE)) return true;
        }
        return false;
    }

    public static final String PREFIX_MOVIE = TABLE_NAME + "__" + MovieColumns.TABLE_NAME;
}
