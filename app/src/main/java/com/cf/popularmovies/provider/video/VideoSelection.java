package com.cf.popularmovies.provider.video;

import java.util.Date;

import android.content.Context;
import android.content.ContentResolver;
import android.database.Cursor;
import android.net.Uri;

import com.cf.popularmovies.provider.base.AbstractSelection;
import com.cf.popularmovies.provider.movie.*;

/**
 * Selection for the {@code video} table.
 */
public class VideoSelection extends AbstractSelection<VideoSelection> {
    @Override
    protected Uri baseUri() {
        return VideoColumns.CONTENT_URI;
    }

    /**
     * Query the given content resolver using this selection.
     *
     * @param contentResolver The content resolver to query.
     * @param projection A list of which columns to return. Passing null will return all columns, which is inefficient.
     * @return A {@code VideoCursor} object, which is positioned before the first entry, or null.
     */
    public VideoCursor query(ContentResolver contentResolver, String[] projection) {
        Cursor cursor = contentResolver.query(uri(), projection, sel(), args(), order());
        if (cursor == null) return null;
        return new VideoCursor(cursor);
    }

    /**
     * Equivalent of calling {@code query(contentResolver, null)}.
     */
    public VideoCursor query(ContentResolver contentResolver) {
        return query(contentResolver, null);
    }

    /**
     * Query the given content resolver using this selection.
     *
     * @param context The context to use for the query.
     * @param projection A list of which columns to return. Passing null will return all columns, which is inefficient.
     * @return A {@code VideoCursor} object, which is positioned before the first entry, or null.
     */
    public VideoCursor query(Context context, String[] projection) {
        Cursor cursor = context.getContentResolver().query(uri(), projection, sel(), args(), order());
        if (cursor == null) return null;
        return new VideoCursor(cursor);
    }

    /**
     * Equivalent of calling {@code query(context, null)}.
     */
    public VideoCursor query(Context context) {
        return query(context, null);
    }


    public VideoSelection id(long... value) {
        addEquals("video." + VideoColumns._ID, toObjectArray(value));
        return this;
    }

    public VideoSelection idNot(long... value) {
        addNotEquals("video." + VideoColumns._ID, toObjectArray(value));
        return this;
    }

    public VideoSelection orderById(boolean desc) {
        orderBy("video." + VideoColumns._ID, desc);
        return this;
    }

    public VideoSelection orderById() {
        return orderById(false);
    }

    public VideoSelection movieId(long... value) {
        addEquals(VideoColumns.MOVIE_ID, toObjectArray(value));
        return this;
    }

    public VideoSelection movieIdNot(long... value) {
        addNotEquals(VideoColumns.MOVIE_ID, toObjectArray(value));
        return this;
    }

    public VideoSelection movieIdGt(long value) {
        addGreaterThan(VideoColumns.MOVIE_ID, value);
        return this;
    }

    public VideoSelection movieIdGtEq(long value) {
        addGreaterThanOrEquals(VideoColumns.MOVIE_ID, value);
        return this;
    }

    public VideoSelection movieIdLt(long value) {
        addLessThan(VideoColumns.MOVIE_ID, value);
        return this;
    }

    public VideoSelection movieIdLtEq(long value) {
        addLessThanOrEquals(VideoColumns.MOVIE_ID, value);
        return this;
    }

    public VideoSelection orderByMovieId(boolean desc) {
        orderBy(VideoColumns.MOVIE_ID, desc);
        return this;
    }

    public VideoSelection orderByMovieId() {
        orderBy(VideoColumns.MOVIE_ID, false);
        return this;
    }

    public VideoSelection movieTmdbId(int... value) {
        addEquals(MovieColumns.TMDB_ID, toObjectArray(value));
        return this;
    }

    public VideoSelection movieTmdbIdNot(int... value) {
        addNotEquals(MovieColumns.TMDB_ID, toObjectArray(value));
        return this;
    }

    public VideoSelection movieTmdbIdGt(int value) {
        addGreaterThan(MovieColumns.TMDB_ID, value);
        return this;
    }

    public VideoSelection movieTmdbIdGtEq(int value) {
        addGreaterThanOrEquals(MovieColumns.TMDB_ID, value);
        return this;
    }

    public VideoSelection movieTmdbIdLt(int value) {
        addLessThan(MovieColumns.TMDB_ID, value);
        return this;
    }

    public VideoSelection movieTmdbIdLtEq(int value) {
        addLessThanOrEquals(MovieColumns.TMDB_ID, value);
        return this;
    }

    public VideoSelection orderByMovieTmdbId(boolean desc) {
        orderBy(MovieColumns.TMDB_ID, desc);
        return this;
    }

    public VideoSelection orderByMovieTmdbId() {
        orderBy(MovieColumns.TMDB_ID, false);
        return this;
    }

    public VideoSelection movieTitle(String... value) {
        addEquals(MovieColumns.TITLE, value);
        return this;
    }

    public VideoSelection movieTitleNot(String... value) {
        addNotEquals(MovieColumns.TITLE, value);
        return this;
    }

    public VideoSelection movieTitleLike(String... value) {
        addLike(MovieColumns.TITLE, value);
        return this;
    }

    public VideoSelection movieTitleContains(String... value) {
        addContains(MovieColumns.TITLE, value);
        return this;
    }

    public VideoSelection movieTitleStartsWith(String... value) {
        addStartsWith(MovieColumns.TITLE, value);
        return this;
    }

    public VideoSelection movieTitleEndsWith(String... value) {
        addEndsWith(MovieColumns.TITLE, value);
        return this;
    }

    public VideoSelection orderByMovieTitle(boolean desc) {
        orderBy(MovieColumns.TITLE, desc);
        return this;
    }

    public VideoSelection orderByMovieTitle() {
        orderBy(MovieColumns.TITLE, false);
        return this;
    }

    public VideoSelection movieOverview(String... value) {
        addEquals(MovieColumns.OVERVIEW, value);
        return this;
    }

    public VideoSelection movieOverviewNot(String... value) {
        addNotEquals(MovieColumns.OVERVIEW, value);
        return this;
    }

    public VideoSelection movieOverviewLike(String... value) {
        addLike(MovieColumns.OVERVIEW, value);
        return this;
    }

    public VideoSelection movieOverviewContains(String... value) {
        addContains(MovieColumns.OVERVIEW, value);
        return this;
    }

    public VideoSelection movieOverviewStartsWith(String... value) {
        addStartsWith(MovieColumns.OVERVIEW, value);
        return this;
    }

    public VideoSelection movieOverviewEndsWith(String... value) {
        addEndsWith(MovieColumns.OVERVIEW, value);
        return this;
    }

    public VideoSelection orderByMovieOverview(boolean desc) {
        orderBy(MovieColumns.OVERVIEW, desc);
        return this;
    }

    public VideoSelection orderByMovieOverview() {
        orderBy(MovieColumns.OVERVIEW, false);
        return this;
    }

    public VideoSelection movieReleaseDate(String... value) {
        addEquals(MovieColumns.RELEASE_DATE, value);
        return this;
    }

    public VideoSelection movieReleaseDateNot(String... value) {
        addNotEquals(MovieColumns.RELEASE_DATE, value);
        return this;
    }

    public VideoSelection movieReleaseDateLike(String... value) {
        addLike(MovieColumns.RELEASE_DATE, value);
        return this;
    }

    public VideoSelection movieReleaseDateContains(String... value) {
        addContains(MovieColumns.RELEASE_DATE, value);
        return this;
    }

    public VideoSelection movieReleaseDateStartsWith(String... value) {
        addStartsWith(MovieColumns.RELEASE_DATE, value);
        return this;
    }

    public VideoSelection movieReleaseDateEndsWith(String... value) {
        addEndsWith(MovieColumns.RELEASE_DATE, value);
        return this;
    }

    public VideoSelection orderByMovieReleaseDate(boolean desc) {
        orderBy(MovieColumns.RELEASE_DATE, desc);
        return this;
    }

    public VideoSelection orderByMovieReleaseDate() {
        orderBy(MovieColumns.RELEASE_DATE, false);
        return this;
    }

    public VideoSelection movieVoteAverage(Double... value) {
        addEquals(MovieColumns.VOTE_AVERAGE, value);
        return this;
    }

    public VideoSelection movieVoteAverageNot(Double... value) {
        addNotEquals(MovieColumns.VOTE_AVERAGE, value);
        return this;
    }

    public VideoSelection movieVoteAverageGt(double value) {
        addGreaterThan(MovieColumns.VOTE_AVERAGE, value);
        return this;
    }

    public VideoSelection movieVoteAverageGtEq(double value) {
        addGreaterThanOrEquals(MovieColumns.VOTE_AVERAGE, value);
        return this;
    }

    public VideoSelection movieVoteAverageLt(double value) {
        addLessThan(MovieColumns.VOTE_AVERAGE, value);
        return this;
    }

    public VideoSelection movieVoteAverageLtEq(double value) {
        addLessThanOrEquals(MovieColumns.VOTE_AVERAGE, value);
        return this;
    }

    public VideoSelection orderByMovieVoteAverage(boolean desc) {
        orderBy(MovieColumns.VOTE_AVERAGE, desc);
        return this;
    }

    public VideoSelection orderByMovieVoteAverage() {
        orderBy(MovieColumns.VOTE_AVERAGE, false);
        return this;
    }

    public VideoSelection movieBackdroppath(String... value) {
        addEquals(MovieColumns.BACKDROPPATH, value);
        return this;
    }

    public VideoSelection movieBackdroppathNot(String... value) {
        addNotEquals(MovieColumns.BACKDROPPATH, value);
        return this;
    }

    public VideoSelection movieBackdroppathLike(String... value) {
        addLike(MovieColumns.BACKDROPPATH, value);
        return this;
    }

    public VideoSelection movieBackdroppathContains(String... value) {
        addContains(MovieColumns.BACKDROPPATH, value);
        return this;
    }

    public VideoSelection movieBackdroppathStartsWith(String... value) {
        addStartsWith(MovieColumns.BACKDROPPATH, value);
        return this;
    }

    public VideoSelection movieBackdroppathEndsWith(String... value) {
        addEndsWith(MovieColumns.BACKDROPPATH, value);
        return this;
    }

    public VideoSelection orderByMovieBackdroppath(boolean desc) {
        orderBy(MovieColumns.BACKDROPPATH, desc);
        return this;
    }

    public VideoSelection orderByMovieBackdroppath() {
        orderBy(MovieColumns.BACKDROPPATH, false);
        return this;
    }

    public VideoSelection moviePosterpath(String... value) {
        addEquals(MovieColumns.POSTERPATH, value);
        return this;
    }

    public VideoSelection moviePosterpathNot(String... value) {
        addNotEquals(MovieColumns.POSTERPATH, value);
        return this;
    }

    public VideoSelection moviePosterpathLike(String... value) {
        addLike(MovieColumns.POSTERPATH, value);
        return this;
    }

    public VideoSelection moviePosterpathContains(String... value) {
        addContains(MovieColumns.POSTERPATH, value);
        return this;
    }

    public VideoSelection moviePosterpathStartsWith(String... value) {
        addStartsWith(MovieColumns.POSTERPATH, value);
        return this;
    }

    public VideoSelection moviePosterpathEndsWith(String... value) {
        addEndsWith(MovieColumns.POSTERPATH, value);
        return this;
    }

    public VideoSelection orderByMoviePosterpath(boolean desc) {
        orderBy(MovieColumns.POSTERPATH, desc);
        return this;
    }

    public VideoSelection orderByMoviePosterpath() {
        orderBy(MovieColumns.POSTERPATH, false);
        return this;
    }

    public VideoSelection key(String... value) {
        addEquals(VideoColumns.KEY, value);
        return this;
    }

    public VideoSelection keyNot(String... value) {
        addNotEquals(VideoColumns.KEY, value);
        return this;
    }

    public VideoSelection keyLike(String... value) {
        addLike(VideoColumns.KEY, value);
        return this;
    }

    public VideoSelection keyContains(String... value) {
        addContains(VideoColumns.KEY, value);
        return this;
    }

    public VideoSelection keyStartsWith(String... value) {
        addStartsWith(VideoColumns.KEY, value);
        return this;
    }

    public VideoSelection keyEndsWith(String... value) {
        addEndsWith(VideoColumns.KEY, value);
        return this;
    }

    public VideoSelection orderByKey(boolean desc) {
        orderBy(VideoColumns.KEY, desc);
        return this;
    }

    public VideoSelection orderByKey() {
        orderBy(VideoColumns.KEY, false);
        return this;
    }

    public VideoSelection name(String... value) {
        addEquals(VideoColumns.NAME, value);
        return this;
    }

    public VideoSelection nameNot(String... value) {
        addNotEquals(VideoColumns.NAME, value);
        return this;
    }

    public VideoSelection nameLike(String... value) {
        addLike(VideoColumns.NAME, value);
        return this;
    }

    public VideoSelection nameContains(String... value) {
        addContains(VideoColumns.NAME, value);
        return this;
    }

    public VideoSelection nameStartsWith(String... value) {
        addStartsWith(VideoColumns.NAME, value);
        return this;
    }

    public VideoSelection nameEndsWith(String... value) {
        addEndsWith(VideoColumns.NAME, value);
        return this;
    }

    public VideoSelection orderByName(boolean desc) {
        orderBy(VideoColumns.NAME, desc);
        return this;
    }

    public VideoSelection orderByName() {
        orderBy(VideoColumns.NAME, false);
        return this;
    }

    public VideoSelection site(String... value) {
        addEquals(VideoColumns.SITE, value);
        return this;
    }

    public VideoSelection siteNot(String... value) {
        addNotEquals(VideoColumns.SITE, value);
        return this;
    }

    public VideoSelection siteLike(String... value) {
        addLike(VideoColumns.SITE, value);
        return this;
    }

    public VideoSelection siteContains(String... value) {
        addContains(VideoColumns.SITE, value);
        return this;
    }

    public VideoSelection siteStartsWith(String... value) {
        addStartsWith(VideoColumns.SITE, value);
        return this;
    }

    public VideoSelection siteEndsWith(String... value) {
        addEndsWith(VideoColumns.SITE, value);
        return this;
    }

    public VideoSelection orderBySite(boolean desc) {
        orderBy(VideoColumns.SITE, desc);
        return this;
    }

    public VideoSelection orderBySite() {
        orderBy(VideoColumns.SITE, false);
        return this;
    }
}
