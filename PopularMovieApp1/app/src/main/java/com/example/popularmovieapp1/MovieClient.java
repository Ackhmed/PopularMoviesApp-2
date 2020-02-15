package com.example.popularmovieapp1;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


public class MovieClient {

    private static final String TAG = MainActivity.class.getSimpleName();
    private static final String BASE_URL = "https://api.themoviedb.org/3/";
    public static int PAGE = 1;
    private static final String MIDDLE_URL = "api_key=";
    private static Retrofit retrofit = null;


    public static Retrofit getClient() {

        if (retrofit == null) {
            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();

        }
        return retrofit;
    }
}
