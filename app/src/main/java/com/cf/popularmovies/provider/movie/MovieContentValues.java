package com.cf.popularmovies.provider.movie;

import java.util.Date;

import android.content.Context;
import android.content.ContentResolver;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.cf.popularmovies.provider.base.AbstractContentValues;

/**
 * Content values wrapper for the {@code movie} table.
 */
public class MovieContentValues extends AbstractContentValues {
    @Override
    public Uri uri() {
        return MovieColumns.CONTENT_URI;
    }

    /**
     * Update row(s) using the values stored by this object and the given selection.
     *
     * @param contentResolver The content resolver to use.
     * @param where The selection to use (can be {@code null}).
     */
    public int update(ContentResolver contentResolver, @Nullable MovieSelection where) {
        return contentResolver.update(uri(), values(), where == null ? null : where.sel(), where == null ? null : where.args());
    }

    /**
     * Update row(s) using the values stored by this object and the given selection.
     *
     * @param contentResolver The content resolver to use.
     * @param where The selection to use (can be {@code null}).
     */
    public int update(Context context, @Nullable MovieSelection where) {
        return context.getContentResolver().update(uri(), values(), where == null ? null : where.sel(), where == null ? null : where.args());
    }

    /**
     * Unique id for the movie on TMDB.
     */
    public MovieContentValues putTmdbId(int value) {
        mContentValues.put(MovieColumns.TMDB_ID, value);
        return this;
    }


    /**
     * Title of the movie.
     */
    public MovieContentValues putTitle(@NonNull String value) {
        if (value == null) throw new IllegalArgumentException("title must not be null");
        mContentValues.put(MovieColumns.TITLE, value);
        return this;
    }


    /**
     * Overview of the movie.
     */
    public MovieContentValues putOverview(@Nullable String value) {
        mContentValues.put(MovieColumns.OVERVIEW, value);
        return this;
    }

    public MovieContentValues putOverviewNull() {
        mContentValues.putNull(MovieColumns.OVERVIEW);
        return this;
    }

    /**
     * Relase date of the movie.
     */
    public MovieContentValues putReleaseDate(@Nullable String value) {
        mContentValues.put(MovieColumns.RELEASE_DATE, value);
        return this;
    }

    public MovieContentValues putReleaseDateNull() {
        mContentValues.putNull(MovieColumns.RELEASE_DATE);
        return this;
    }

    /**
     * Average voting score from IMDB user.
     */
    public MovieContentValues putVoteAverage(@Nullable Double value) {
        mContentValues.put(MovieColumns.VOTE_AVERAGE, value);
        return this;
    }

    public MovieContentValues putVoteAverageNull() {
        mContentValues.putNull(MovieColumns.VOTE_AVERAGE);
        return this;
    }

    /**
     * Local path where the backdrop image is stored.
     */
    public MovieContentValues putBackdroppath(@Nullable String value) {
        mContentValues.put(MovieColumns.BACKDROPPATH, value);
        return this;
    }

    public MovieContentValues putBackdroppathNull() {
        mContentValues.putNull(MovieColumns.BACKDROPPATH);
        return this;
    }

    /**
     * Local path where the poster image is stored.
     */
    public MovieContentValues putPosterpath(@Nullable String value) {
        mContentValues.put(MovieColumns.POSTERPATH, value);
        return this;
    }

    public MovieContentValues putPosterpathNull() {
        mContentValues.putNull(MovieColumns.POSTERPATH);
        return this;
    }
}
