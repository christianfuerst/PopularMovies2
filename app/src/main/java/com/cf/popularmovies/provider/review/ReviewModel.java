package com.cf.popularmovies.provider.review;

import com.cf.popularmovies.provider.base.BaseModel;

import java.util.Date;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

/**
 * A review for a movie.
 */
public interface ReviewModel extends BaseModel {

    /**
     * Reference to movie table.
     */
    long getMovieId();

    /**
     * Author of the review. Who wrote it?
     * Cannot be {@code null}.
     */
    @NonNull
    String getAuthor();

    /**
     * The content of the review.
     * Cannot be {@code null}.
     */
    @NonNull
    String getContent();
}
