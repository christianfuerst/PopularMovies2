package com.cf.popularmovies.provider.video;

import com.cf.popularmovies.provider.base.BaseModel;

import java.util.Date;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

/**
 * A video from a movie.
 */
public interface VideoModel extends BaseModel {

    /**
     * Reference to movie table.
     */
    long getMovieId();

    /**
     * The unique key in order to access the video on a 3rd party site.
     * Cannot be {@code null}.
     */
    @NonNull
    String getKey();

    /**
     * The name of the video.
     * Cannot be {@code null}.
     */
    @NonNull
    String getName();

    /**
     * The name of the site, where the video can be accessed.
     * Cannot be {@code null}.
     */
    @NonNull
    String getSite();
}
