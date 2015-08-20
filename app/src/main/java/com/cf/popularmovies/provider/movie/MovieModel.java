package com.cf.popularmovies.provider.movie;

import com.cf.popularmovies.provider.base.BaseModel;

import java.util.Date;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

/**
 * A movie which can be displayed in the app.
 */
public interface MovieModel extends BaseModel {

    /**
     * Unique id for the movie on TMDB.
     */
    int getTmdbId();

    /**
     * Title of the movie.
     * Cannot be {@code null}.
     */
    @NonNull
    String getTitle();

    /**
     * Overview of the movie.
     * Can be {@code null}.
     */
    @Nullable
    String getOverview();

    /**
     * Relase date of the movie.
     * Can be {@code null}.
     */
    @Nullable
    String getReleaseDate();

    /**
     * Average voting score from IMDB user.
     * Can be {@code null}.
     */
    @Nullable
    Double getVoteAverage();

    /**
     * Local path where the backdrop image is stored.
     * Can be {@code null}.
     */
    @Nullable
    String getBackdroppath();

    /**
     * Local path where the poster image is stored.
     * Can be {@code null}.
     */
    @Nullable
    String getPosterpath();
}
