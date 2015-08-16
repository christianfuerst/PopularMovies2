package com.cf.popularmovies.API;

import com.cf.popularmovies.model.ReviewPage;

import retrofit.http.GET;
import retrofit.http.Path;
import retrofit.http.Query;

public interface ReviewAPI {

    @GET("/movie/{id}/reviews")
    ReviewPage getPage(
            @Query("api_key") String api_key,
            @Path("id") String id
    );

}
