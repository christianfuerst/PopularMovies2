package com.cf.popularmovies.provider.video;

import java.util.Date;

import android.database.Cursor;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.cf.popularmovies.provider.base.AbstractCursor;
import com.cf.popularmovies.provider.movie.*;

/**
 * Cursor wrapper for the {@code video} table.
 */
public class VideoCursor extends AbstractCursor implements VideoModel {
    public VideoCursor(Cursor cursor) {
        super(cursor);
    }

    /**
     * Primary key.
     */
    public long getId() {
        Long res = getLongOrNull(VideoColumns._ID);
        if (res == null)
            throw new NullPointerException("The value of '_id' in the database was null, which is not allowed according to the model definition");
        return res;
    }

    /**
     * Reference to movie table.
     */
    public long getMovieId() {
        Long res = getLongOrNull(VideoColumns.MOVIE_ID);
        if (res == null)
            throw new NullPointerException("The value of 'movie_id' in the database was null, which is not allowed according to the model definition");
        return res;
    }

    /**
     * Unique id for the movie on TMDB.
     */
    public int getMovieTmdbId() {
        Integer res = getIntegerOrNull(MovieColumns.TMDB_ID);
        if (res == null)
            throw new NullPointerException("The value of 'tmdb_id' in the database was null, which is not allowed according to the model definition");
        return res;
    }

    /**
     * Title of the movie.
     * Cannot be {@code null}.
     */
    @NonNull
    public String getMovieTitle() {
        String res = getStringOrNull(MovieColumns.TITLE);
        if (res == null)
            throw new NullPointerException("The value of 'title' in the database was null, which is not allowed according to the model definition");
        return res;
    }

    /**
     * Overview of the movie.
     * Can be {@code null}.
     */
    @Nullable
    public String getMovieOverview() {
        String res = getStringOrNull(MovieColumns.OVERVIEW);
        return res;
    }

    /**
     * Relase date of the movie.
     * Can be {@code null}.
     */
    @Nullable
    public String getMovieReleaseDate() {
        String res = getStringOrNull(MovieColumns.RELEASE_DATE);
        return res;
    }

    /**
     * Average voting score from IMDB user.
     * Can be {@code null}.
     */
    @Nullable
    public Double getMovieVoteAverage() {
        Double res = getDoubleOrNull(MovieColumns.VOTE_AVERAGE);
        return res;
    }

    /**
     * Local path where the backdrop image is stored.
     * Can be {@code null}.
     */
    @Nullable
    public String getMovieBackdroppath() {
        String res = getStringOrNull(MovieColumns.BACKDROPPATH);
        return res;
    }

    /**
     * Local path where the poster image is stored.
     * Can be {@code null}.
     */
    @Nullable
    public String getMoviePosterpath() {
        String res = getStringOrNull(MovieColumns.POSTERPATH);
        return res;
    }

    /**
     * The unique key in order to access the video on a 3rd party site.
     * Cannot be {@code null}.
     */
    @NonNull
    public String getKey() {
        String res = getStringOrNull(VideoColumns.KEY);
        if (res == null)
            throw new NullPointerException("The value of 'key' in the database was null, which is not allowed according to the model definition");
        return res;
    }

    /**
     * The name of the video.
     * Cannot be {@code null}.
     */
    @NonNull
    public String getName() {
        String res = getStringOrNull(VideoColumns.NAME);
        if (res == null)
            throw new NullPointerException("The value of 'name' in the database was null, which is not allowed according to the model definition");
        return res;
    }

    /**
     * The name of the site, where the video can be accessed.
     * Cannot be {@code null}.
     */
    @NonNull
    public String getSite() {
        String res = getStringOrNull(VideoColumns.SITE);
        if (res == null)
            throw new NullPointerException("The value of 'site' in the database was null, which is not allowed according to the model definition");
        return res;
    }
}
