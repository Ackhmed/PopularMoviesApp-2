package com.example.popularmovieapp1;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface MovieInterface {

    @GET("movie/popular")
    Call<MovieResults> getPopularMovies( @Query("api_key") String API_KEY,
                                         @Query("language") String language,
                                         @Query("page") int page_number);

    //creating a service for trailers
    @GET("movie/{id}/videos")
    Call<TrailerResults> getTrailers (@Path("id") int id,
                                      @Query("api_key") String API_KEY,
                                      @Query("language") String language);

    //creating a service for Reviews
    @GET("movie/{id}/reviews")
    Call<ReviewResults> getReviews (@Path("id") int id,
                                      @Query("api_key") String API_KEY,
                                      @Query("language") String language,
                                            @Query("page") int page_number);
}
