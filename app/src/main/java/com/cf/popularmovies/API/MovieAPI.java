package com.cf.popularmovies.API;

import com.cf.popularmovies.model.MoviePage;

import retrofit.http.GET;
import retrofit.http.Query;

public interface MovieAPI {

    @GET("/discover/movie")
    MoviePage getPage (
            @Query("api_key") String api_key,
            @Query("sort_by") String sort_by
    );

}
