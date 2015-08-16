package com.cf.popularmovies.API;

import com.cf.popularmovies.model.Video;

import retrofit.http.GET;
import retrofit.http.Path;
import retrofit.http.Query;

public interface VideoAPI {

    @GET("/movie/{id}/videos")
    Video getVideo (
            @Query("api_key") String api_key,
            @Path("id") String id
    );

}
